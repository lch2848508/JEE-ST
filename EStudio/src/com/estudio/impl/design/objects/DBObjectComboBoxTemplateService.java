package com.estudio.impl.design.objects;

import java.sql.Connection;
import java.util.ArrayList;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ComboBoxTemplateRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectComboBoxTemplateService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;

public abstract class DBObjectComboBoxTemplateService implements IObjectComboBoxTemplateService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getUpdateSQL();

    protected abstract String getSelectSQL();

    protected abstract String getMoveSQL();

    protected abstract String getListSQL();

    protected abstract String getInsertSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getDeleteSQL();

    protected IDBCommand selectCMD;
    protected IDBCommand updateCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand deleteCMD;
    protected IDBCommand listCMD;
    protected IDBCommand exchangeCMD;
    protected IDBCommand movetoCMD;

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

    public DBObjectComboBoxTemplateService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#deleteRecord
     * (java.sql.Connection, long)
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
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#exchange
     * (java.sql.Connection, long, long)
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
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#fillRecord
     * (com.estudio.intf.db.IDBCommand,
     * com.estudio.define.design.objects.ComboBoxTemplateRecord)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final ComboBoxTemplateRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setName(cmd.getString("NAME"));
        record.setContent(cmd.getBytes("CONTENT"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#getRecord
     * (java.sql.Connection, long)
     */
    @Override
    public ComboBoxTemplateRecord getRecord(final Connection con, final long id) throws Exception {
        ComboBoxTemplateRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new ComboBoxTemplateRecord();
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
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#getRecords
     * (java.sql.Connection, long)
     */
    @Override
    public ArrayList<ComboBoxTemplateRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<ComboBoxTemplateRecord> records = new ArrayList<ComboBoxTemplateRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final ComboBoxTemplateRecord record = new ComboBoxTemplateRecord();
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
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#getTemplate
     * (java.sql.Connection)
     */
    @Override
    public JSONArray getTemplate(final Connection con) throws Exception, DBException {
        final JSONArray result = new JSONArray();
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, "select id,name,content from sys_combobox_template order by id", true);
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject json = new JSONObject();
                json.put("id", stmt.getLong(1));
                json.put("name", stmt.getString(2));
                json.put("content", Convert.bytes2Str(stmt.getBytes(3)));
                result.add(json);
            }
        } finally {
            DBHELPER.closeCommand(stmt);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#moveTo
     * (java.sql.Connection, long, long)
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
     * @see
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#newRecord
     * ()
     */
    @Override
    public ComboBoxTemplateRecord newRecord() {
        return new ComboBoxTemplateRecord();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectComboBoxTemplateService#saveRecord
     * (java.sql.Connection,
     * com.estudio.define.design.objects.ComboBoxTemplateRecord)
     */
    @Override
    public boolean saveRecord(final Connection con, final ComboBoxTemplateRecord record) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            if (record.isNew()) {
                cmd = insertCMD.clone(tempCon);
                record.setId(DBHELPER.getUniqueID(tempCon));
            } else cmd = updateCMD.clone(tempCon);
            cmd.setParam("id", record.getId());
            cmd.setParam("name", record.getName());
            cmd.setParam("content", record.getContent());
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

}
