package com.estudio.impl.design.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ObjectTreeRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectTreeService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public abstract class DBObjectTreeService implements IObjectTreeService {
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getUpdateSQL();

    protected abstract String getUnLockObjectSQL();

    protected abstract String getSelectSQL();

    protected abstract String getReportTreeSQL(Connection con);

    protected abstract String getQueryTreeSQL(Connection con);

    protected abstract String getObjectVersionAndLockStatusByUserIdSQL();

    protected abstract String getObjectTreeByUserIdSQL();

    protected abstract String getMoveSQL();

    protected abstract String getLockObjectSQL();

    protected abstract String getListSQL();

    protected abstract String getInsertSQL();

    protected abstract String getFormTreeSQL(Connection con);

    protected abstract String getExchangeSQL();

    protected abstract String getDeleteSQL();

    protected IDBCommand deleteCMD;
    protected IDBCommand exchangeCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand listCMD;
    protected IDBCommand movetoCMD;
    protected IDBCommand selectCMD;
    protected IDBCommand updateCMD;
    private long versionAllForm = -1;

    {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMoveSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    public DBObjectTreeService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectTreeService#deleteRecord(java.
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
            cmd.setParam("id", id);
            cmd.execute();
            RuntimeContext.getVersionService().incVersion(tempCon, IObjectTreeService.OBJECT_FORM);
            RuntimeContext.getWfStorage().notifyWFProcessChange(id, true, con);
            NotifyService4Cluster.getInstance().notifyClusterMessage(1, id, 1, con);
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
     * com.estudio.impl.design.objects.IObjectTreeService#exchange(java.sql.
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
     * com.estudio.impl.design.objects.IObjectTreeService#fillRecord(com.estudio
     * .intf.db.IDBCommand, com.estudio.define.design.objects.ObjectTreeRecord)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final ObjectTreeRecord record) throws Exception {
        record.setMemo(Convert.bytes2Str(cmd.getBytes("MEMO")));
        record.setId(cmd.getLong("ID"));
        record.setCaption(cmd.getString("CAPTION"));
        record.setType(cmd.getInt("TYPE"));
        record.setVersion(cmd.getLong("VERSION"));
        record.setSortorder(cmd.getLong("SORTORDER"));
        record.setPid(cmd.getLong("PID"));
        record.setLockby(cmd.getLong("LOCKBY"));
        record.setPropId(cmd.getLong("PROP_ID"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectTreeService#getFormObjects(long)
     */
    @Override
    public JSONObject getFormObjects(final long version) throws Exception, DBException {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            final long serverVersion = RuntimeContext.getVersionService().getVersion(con, OBJECT_FORM);
            if ((serverVersion != version) || (versionAllForm != serverVersion)) {
                final HashMap<Long, JSONObject> ID2JSON = new HashMap<Long, JSONObject>();
                ID2JSON.put(-1L, json);
                stmt = DBHELPER.getCommand(con, getFormTreeSQL(con));
                stmt.executeQuery();
                while (stmt.next()) {
                    final JSONObject itemJSON = new JSONObject();
                    itemJSON.put("id", stmt.getLong(1));
                    itemJSON.put("caption", stmt.getString(3));
                    itemJSON.put("type", stmt.getInt(4));
                    itemJSON.put("params", stmt.getString(5));
                    itemJSON.put("version", stmt.getLong(6));
                    ID2JSON.put(stmt.getLong(1), itemJSON);
                    final long PID = stmt.getLong(2);
                    if (ID2JSON.containsKey(PID))
                        JSONUtils.append(ID2JSON.get(PID), "items", itemJSON);
                    // ID2JSON.get(PID).append("items", itemJSON);
                }
            }
            json.put("r", true);
            json.put("version", serverVersion);
            versionAllForm = serverVersion;
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectTreeService#getObjectsTree(long,
     * long)
     */
    @Override
    public JSONObject getObjectsTree(final long pid, final long userID) throws Exception, DBException {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            // 版本控制 0:normal 1:checkin 2:locked by other
            stmt = DBHELPER.getCommand(con, getObjectTreeByUserIdSQL());
            stmt.setParam(1, userID);
            this.getObjectsTree(json, stmt, pid);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectTreeService#getRecord(java.sql
     * .Connection, long)
     */
    @Override
    public ObjectTreeRecord getRecord(final Connection con, final long id) throws Exception {
        ObjectTreeRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new ObjectTreeRecord();
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
     * com.estudio.impl.design.objects.IObjectTreeService#getRecords(java.sql
     * .Connection, long)
     */
    @Override
    public ArrayList<ObjectTreeRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<ObjectTreeRecord> records = new ArrayList<ObjectTreeRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final ObjectTreeRecord record = new ObjectTreeRecord();
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
     * com.estudio.impl.design.objects.IObjectTreeService#getReportObjects()
     */
    @Override
    public JSONObject getReportObjects() throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            final HashMap<Long, JSONObject> ID2JSON = new HashMap<Long, JSONObject>();
            ID2JSON.put(-1L, json);
            stmt = DBHELPER.getCommand(con, getReportTreeSQL(con));
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject itemJSON = new JSONObject();
                itemJSON.put("id", stmt.getLong(1));
                itemJSON.put("caption", stmt.getString(3));
                itemJSON.put("type", stmt.getInt(4));
                itemJSON.put("params", stmt.getString(5));
                ID2JSON.put(stmt.getLong(1), itemJSON);
                final long PID = stmt.getLong(2);
                if (ID2JSON.containsKey(PID))
                    JSONUtils.append(ID2JSON.get(PID), "items", itemJSON);
                // ID2JSON.get(PID).append("items", itemJSON);
            }
            json.put("r", true);

        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject getQueryObjects() throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            final HashMap<Long, JSONObject> ID2JSON = new HashMap<Long, JSONObject>();
            ID2JSON.put(-1L, json);
            stmt = DBHELPER.getCommand(con, getQueryTreeSQL(con));
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject itemJSON = new JSONObject();
                itemJSON.put("id", stmt.getLong(1));
                itemJSON.put("caption", stmt.getString(3));
                itemJSON.put("type", stmt.getInt(4));
                ID2JSON.put(stmt.getLong(1), itemJSON);
                final long PID = stmt.getLong(2);
                if (ID2JSON.containsKey(PID))
                    JSONUtils.append(ID2JSON.get(PID), "items", itemJSON);
                // ID2JSON.get(PID).append("items", itemJSON);
            }
            json.put("r", true);

        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectTreeService#moveTo(java.sql.Connection
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
     * @see com.estudio.impl.design.objects.IObjectTreeService#newRecord()
     */
    @Override
    public ObjectTreeRecord newRecord() {
        return new ObjectTreeRecord();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectTreeService#saveRecord(java.sql
     * .Connection, com.estudio.define.design.objects.ObjectTreeRecord)
     */
    @Override
    public boolean saveRecord(final Connection con, final ObjectTreeRecord record) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            if (record.isNew())
                cmd = insertCMD.clone(tempCon);
            else cmd = updateCMD.clone(tempCon);
            cmd.setParam("memo", Convert.str2Bytes(record.getMemo()));
            cmd.setParam("caption", record.getCaption());
            cmd.setParam("type", record.getType());
            cmd.setParam("version", record.getVersion());
            cmd.setParam("sortorder", record.getSortorder());
            cmd.setParam("pid", record.getPid());
            cmd.setParam("lockby", record.getLockby());
            cmd.setParam("prop_id", record.getPropId());
            cmd.setParam("id", record.getId());
            record.setOld();
            cmd.execute();
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
     * com.estudio.impl.design.objects.IObjectTreeService#VersionControlObject
     * (long, boolean, long)
     */
    @Override
    public synchronized JSONObject versionControlObject(final long id, final boolean isCheckIn, final long userID) throws Exception, DBException {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            if (isCheckIn) {
                stmt = DBHELPER.getCommand(con, getLockObjectSQL());
                stmt.setParam(1, userID);
                stmt.setParam(2, id);
                stmt.execute();
            } else {
                stmt = DBHELPER.getCommand(con, getUnLockObjectSQL());
                stmt.setParam(1, id);
                stmt.execute();
            }
            DBHELPER.closeCommand(stmt);
            stmt = DBHELPER.getCommand(con, getObjectVersionAndLockStatusByUserIdSQL());
            stmt.setParam(1, userID);
            stmt.setParam(2, id);
            stmt.executeQuery();
            if (stmt.next()) {
                json.put("r", true);
                json.put("version", stmt.getLong(1));
                json.put("vss", stmt.getLong(2));
            }
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 读取具体的目录树
     * 
     * @param json
     * @param stmt
     * @param pid
     * @param userID
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    private void getObjectsTree(final JSONObject json, final IDBCommand stmt, final long pid) throws Exception, DBException {
        stmt.setParam(2, pid);
        stmt.executeQuery();
        final HashMap<Long, JSONObject> id2json = new HashMap<Long, JSONObject>();
        while (stmt.next()) {
            final JSONObject itemJSON = new JSONObject();
            final long ID = stmt.getLong("id");
            itemJSON.put("id", ID);
            itemJSON.put("caption", stmt.getString("caption"));
            itemJSON.put("type", stmt.getInt("type"));
            itemJSON.put("version", stmt.getLong("version"));
            itemJSON.put("prop_id", stmt.getLong("prop_id"));
            itemJSON.put("vss", stmt.getLong("vss"));
            itemJSON.put("lockby", stmt.getString("realname"));
            JSONUtils.append(json, "items", itemJSON);
            // json.append("items", itemJSON);
            id2json.put(ID, itemJSON);
        }
        final Iterator<Entry<Long, JSONObject>> iterator = id2json.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<Long, JSONObject> entry = iterator.next();
            getObjectsTree(entry.getValue(), stmt, entry.getKey());
        }
    }

    @Override
    public void sortObjects(List<String> objectIds) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select id from sys_object_tree where id in (" + StringUtils.join(objectIds, ",") + ") order by sortorder");
            ResultSet rs = stmt.executeQuery();
            objectIds.clear();
            while (rs.next())
                objectIds.add(rs.getString(1));
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
    }
}
