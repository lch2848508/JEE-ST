package com.estudio.impl.design.objects;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minidev.json.JSONObject;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ObjectFormsRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectFormService;
import com.estudio.intf.design.objects.IObjectTreeService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public abstract class DBObjectFormService implements IObjectFormService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    protected IDBCommand selectCMD;

    private final Lock cacheKeyLock = new ReentrantLock();

    protected abstract String getUpdateWFSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getSelectSQL();

    protected abstract String getMoveSQL();

    protected abstract String getListSQL();

    protected abstract String getInsertSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getControlSQL();

    private String getListDataSourceCacheKeyListSQL() {
        return "select id,keyname from SYS_OBJECT_FORMS_DS_KEYS";
    }

    private String getDeleteDataSourceKeyNameSQL() {
        return "delete from SYS_OBJECT_FORMS_DS_KEYS where keyname=:keyname";
    }

    private String getDataSourceCacheKeySaveSQL() {
        return "insert into SYS_OBJECT_FORMS_DS_KEYS(id,keyname) values (:id,:keyname)";
    }

    private String getDataSourceCacheKeyExistsSQL() {
        return "select count(*) from SYS_OBJECT_FORMS_DS_KEYS where keyname=:keyname";
    }

    protected IDBCommand updateCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand deleteCMD;
    protected IDBCommand listCMD;
    protected IDBCommand exchangeCMD;
    protected IDBCommand movetoCMD;
    protected IDBCommand updateWFCMD;
    protected IDBCommand controlsCMD;

    {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMoveSQL());
            updateWFCMD = DBHELPER.getCommand(null, getUpdateWFSQL());
            controlsCMD = DBHELPER.getCommand(null, getControlSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    public DBObjectFormService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#deleteRecord(java.
     * sql.Connection, long)
     */
    @Override
    public boolean deleteRecord(final Connection con, final long id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = deleteCMD.clone(tempCon);
            cmd.setParam(1, id);
            result = cmd.execute();
            RuntimeContext.getFormDefineService().notifyFormDefineIsChanged(id);
            RuntimeContext.getVersionService().incVersion(tempCon, IObjectTreeService.OBJECT_FORM);
            NotifyService4Cluster.getInstance().notifyClusterMessage(2, id, 0, con);
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#exchange(java.sql.
     * Connection, long, long)
     */
    @Override
    public boolean exchange(final Connection con, final long id_1, final long id_2) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = exchangeCMD.clone(tempCon);
            cmd.setParam("id1", id_1);
            cmd.setParam("id2", id_2);
            result = cmd.execute();
            RuntimeContext.getVersionService().incVersion(tempCon, IObjectTreeService.OBJECT_FORM);
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#fillRecord(com.estudio
     * .intf.db.IDBCommand, com.estudio.define.design.objects.ObjectFormsRecord)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final ObjectFormsRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setDfmstream(cmd.getBytes("DFMSTREAM"));
        record.setXmlstream(cmd.getBytes("XMLSTREAM"));
        record.setDatasource(cmd.getBytes("DATASOURCE"));
        record.setJsscript(cmd.getBytes("JSSCRIPT"));
        record.setVersion(cmd.getLong("VERSION"));
        record.setType(cmd.getInt("TYPE"));
        record.setFormParams(cmd.getString("FORM_PARAMS"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#getFormControls(java
     * .sql.Connection, long, long)
     */
    @Override
    public JSONObject getFormControls(final Connection con, final long id, final long version) throws Exception {
        IDBCommand cmd = null;
        final JSONObject result = new JSONObject();
        result.put("r", true);
        try {
            final long serverVersion = getFormVersion(con, id);
            if (version != serverVersion) {
                cmd = controlsCMD.clone(con);
                cmd.setParam(1, id);
                cmd.executeQuery();
                if (cmd.next()) {
                    result.put("dss", JSONUtils.parserJSONArray(Convert.bytes2Str(cmd.getBytes(1))));
                    result.put("controls", JSONUtils.parserJSONObject(Convert.bytes2Str(cmd.getBytes(2))));
                }
            }
            result.put("version", serverVersion);
            result.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#getFormVersion(java
     * .sql.Connection, long)
     */
    @Override
    public long getFormVersion(final Connection con, final long id) throws Exception {
        Connection tempCon = con;
        IDBCommand stmt = null;
        long result = -1;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, "select version from sys_object_forms where id=?", true);
            stmt.setParam(1, id);
            stmt.executeQuery();
            if (stmt.next())
                result = stmt.getLong(1);
        } finally {
            DBHELPER.closeCommand(stmt);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#getRecord(java.sql
     * .Connection, long)
     */
    @Override
    public ObjectFormsRecord getRecord(final Connection con, final long id) throws Exception {
        ObjectFormsRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new ObjectFormsRecord();
                fillRecord(cmd, record);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#getRecordInfo(long,
     * long)
     */
    @Override
    public JSONObject getRecordInfo(final long id, final long version) throws Exception, DBException {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            if (getFormVersion(con, id) != version) {
                final ObjectFormsRecord record = getRecord(con, id);
                if (record != null) {
                    json.put("dfm", Convert.bytes2Str(record.getDfmstream()));
                    json.put("version", record.getVersion());
                    json.put("r", true);
                }
            } else {
                json.put("version", version);
                json.put("r", true);
            }

        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#getRecords(java.sql
     * .Connection, long)
     */
    @Override
    public ArrayList<ObjectFormsRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<ObjectFormsRecord> records = new ArrayList<ObjectFormsRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final ObjectFormsRecord record = new ObjectFormsRecord();
                    fillRecord(cmd, record);
                    records.add(record);
                }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return records;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#moveTo(java.sql.Connection
     * , long, long)
     */
    @Override
    public boolean moveTo(final Connection con, final long id, final long p_id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = movetoCMD.clone(tempCon);
            cmd.setParam("id", id);
            cmd.setParam("p_id", p_id);
            result = cmd.execute();
            RuntimeContext.getVersionService().incVersion(tempCon, IObjectTreeService.OBJECT_FORM);
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.objects.IObjectFormService#newRecord()
     */
    @Override
    public ObjectFormsRecord newRecord() {
        return new ObjectFormsRecord();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#saveRecord(java.sql
     * .Connection, com.estudio.define.design.objects.ObjectFormsRecord)
     */
    @Override
    public boolean saveRecord(final Connection con, final ObjectFormsRecord record) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = record.isNew() ? insertCMD.clone(tempCon) : updateCMD.clone(tempCon);
            cmd.setParam("id", record.getId());
            cmd.setParam("dfmstream", record.getDfmstream());
            cmd.setParam("xmlstream", record.getXmlstream());
            cmd.setParam("datasource", record.getDatasource());
            cmd.setParam("jsscript", record.getJsscript());
            cmd.setParam("version", record.getVersion());
            cmd.setParam("type", record.getType());
            cmd.setParam("form_params", record.getFormParams());
            record.setOld();
            cmd.execute();
            RuntimeContext.getFormDefineService().notifyFormDefineIsChanged(record.getId());
            RuntimeContext.getVersionService().incVersion(tempCon, IObjectTreeService.OBJECT_FORM);
            NotifyService4Cluster.getInstance().notifyClusterMessage(2, record.getId(), 0, con);
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectFormService#saveRecord4WorkFlow
     * (java.sql.Connection, long, byte[], byte[])
     */
    @Override
    public JSONObject saveRecord4WorkFlow(final Connection con, final long id, final byte[] ds4wf, final byte[] cs4wf) throws Exception, DBException {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        IDBCommand cmd = null;
        try {
            cmd = updateWFCMD.clone(con);
            cmd.setParam("id", id);
            cmd.setParam("dss", ds4wf);
            cmd.setParam("controls", cs4wf);
            cmd.execute();
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    /**
	 * 
	 */
    @Override
    public JSONObject getDataSourceCacheKeyList(final Connection con) throws Exception {
        final JSONObject json = new JSONObject();
        final List<JSONObject> items = new ArrayList<JSONObject>();
        json.put("r", false);
        Connection tempCon = con;
        IDBCommand cmd = null;
        cacheKeyLock.lock();
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(tempCon, getListDataSourceCacheKeyListSQL());
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final JSONObject item = new JSONObject();
                    item.put("id", cmd.getLong(1));
                    item.put("key", cmd.getString(2));
                    items.add(item);
                }
            json.put("items", items);
            json.put("r", true);
        } finally {
            cacheKeyLock.unlock();
            DBHELPER.closeCommand(cmd);
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
	 * 
	 */
    @Override
    public JSONObject deleteDataSourceCacheKey(final Connection con, final String key) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        cacheKeyLock.lock();
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("keyname", key);
            DBHELPER.execute(getDeleteDataSourceKeyNameSQL(), params, tempCon);
            json.put("r", true);
        } finally {
            cacheKeyLock.unlock();
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
	 * 
	 */
    @Override
    public JSONObject saveDataSourceCacheKey(final Connection con, final String key) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        cacheKeyLock.lock();
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("keyname", key);
            if (Convert.obj2Int(DBHELPER.executeScalar(getDataSourceCacheKeyExistsSQL(), params, tempCon), 0) == 0) {
                params.put("id", DBHELPER.getUniqueID(tempCon));
                DBHELPER.execute(getDataSourceCacheKeySaveSQL(), params, tempCon);
            }
            json.put("r", true);
        } finally {
            cacheKeyLock.unlock();
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
	 * 
	 */
    @Override
    public JSONObject getDataSourceTemplateList(final Connection con) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(tempCon, "select id,caption,category from SYS_OBJECT_FORMS_DS_TEMPLATES t");
            final List<JSONObject> items = new ArrayList<JSONObject>();
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final JSONObject item = new JSONObject();
                    item.put("id", cmd.getLong(1));
                    item.put("caption", cmd.getString(2));
                    item.put("category", cmd.getString(3));
                    items.add(item);
                }
            json.put("items", items);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    @Override
    public JSONObject deleteDataSourceTemplate(final Connection con, final String category, final String caption) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("category", category);
            params.put("caption", caption);
            DBHELPER.execute("delete from SYS_OBJECT_FORMS_DS_TEMPLATES where category=:category and caption=:caption", params, tempCon);
            json.put("r", true);
        } finally {
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
	 * 
	 */
    @Override
    public JSONObject saveDataSourceTemplate(final Connection con, final String category, final String caption, final String content) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("category", category);
            params.put("caption", caption);
            if (Convert.obj2Int(DBHELPER.executeScalar("select count(*) from SYS_OBJECT_FORMS_DS_TEMPLATES where caption=:caption and category=:category", params, tempCon), 0) == 0) {
                params.put("id", DBHELPER.getUniqueID(tempCon));
                params.put("content", Convert.str2Bytes(content));
                DBHELPER.execute("insert into SYS_OBJECT_FORMS_DS_TEMPLATES (id,caption,category,content) values (:id,:caption,:category,:content)", params, tempCon);
            } else {
                params.put("content", Convert.str2Bytes(content));
                DBHELPER.execute("update SYS_OBJECT_FORMS_DS_TEMPLATES set content=:content where category=:category and caption=:caption", params, tempCon);
            }
            json.put("r", true);
        } finally {
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
	 * 
	 */
    @Override
    public JSONObject getDataSourceTemplate(final Connection con, final String category, final String caption) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(tempCon, "select content from SYS_OBJECT_FORMS_DS_TEMPLATES where category=? and caption=?");
            cmd.setParam(1, category);
            cmd.setParam(2, caption);
            if (cmd.executeQuery() && cmd.next()) {
                json.put("content", Convert.bytes2Str(cmd.getBytes(1)));
                json.put("r", true);
            }

        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

}
