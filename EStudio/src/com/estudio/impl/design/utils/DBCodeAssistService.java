package com.estudio.impl.design.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.utils.CodeAssistRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.utils.ICodeAssistService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public abstract class DBCodeAssistService implements ICodeAssistService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    protected IDBCommand selectCMD;

    protected abstract String getCodeAssistSQL();

    protected abstract String getSelectSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getMoveSQL();

    protected IDBCommand updateCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand deleteCMD;
    protected IDBCommand exchangeCMD;
    protected IDBCommand movetoCMD;
    final String VERSION_ASSIST = "CodeAssist";
    private JSONObject codeAssistJSON;

    {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMoveSQL());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
    }

    public DBCodeAssistService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.utils.ICodeAssistService#getCodeAssist(long)
     */
    @Override
    public JSONObject getCodeAssist(final long version) throws Exception {
        Connection con = null;
        IDBCommand stmt = null;
        JSONObject result = null;
        try {
            con = DBHELPER.getConnection();
            final long serverVersion = RuntimeContext.getVersionService().getVersion(con, VERSION_ASSIST);
            if (version == serverVersion) {
                result = new JSONObject();
                result.put("r", true);
                result.put("version", serverVersion);
            } else if ((version != serverVersion) || (codeAssistJSON == null)) {
                stmt = DBHELPER.getCommand(con, getCodeAssistSQL(), true);
                final JSONObject json = new JSONObject();
                final long[] rootIDS = { -2, -1, -3, -4 };
                final String[] captions = { "SQL", "JavaScript", "JAVA", "数据校验" };
                for (int i = 0; i < rootIDS.length; i++) {
                    final long id = rootIDS[i];
                    final String caption = captions[i];
                    final JSONObject itemJSON = new JSONObject();
                    itemJSON.put("id", id);
                    itemJSON.put("caption", caption);
                    getCodeAssist(id, stmt, itemJSON);
                    JSONUtils.append(json, "items", itemJSON);
                    // json.append("items", itemJSON);
                }
                json.put("r", true);
                json.put("version", serverVersion);
                codeAssistJSON = json;
                result = codeAssistJSON;
            } else
                result = codeAssistJSON;
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 取得代码助手
     * 
     * @param id
     * @param stmt
     * @param json
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private void getCodeAssist(final long pid, final IDBCommand stmt, final JSONObject json) throws Exception {
        final ArrayList<Long> ids = new ArrayList<Long>();
        final ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
        stmt.setParam(1, pid);
        stmt.executeQuery();
        while (stmt.next()) {
            final long id = stmt.getLong(1);
            final JSONObject itemJSON = new JSONObject();
            itemJSON.put("id", id);
            itemJSON.put("caption", stmt.getString(2));
            itemJSON.put("content", Convert.bytes2Str(stmt.getBytes(3)));
            itemJSON.put("help", Convert.bytes2Str(stmt.getBytes(4)));
            itemJSON.put("type", stmt.getInt(5));
            itemJSON.put("exttype", stmt.getString(6));
            ids.add(id);
            jsons.add(itemJSON);
        }
        for (int i = 0; i < ids.size(); i++)
            getCodeAssist(ids.get(i), stmt, jsons.get(i));
        json.put("items", jsons);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.utils.ICodeAssistService#moveTo(java.sql.Connection
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
            cmd.setParam("pid", p_id);
            result = cmd.execute();
            RuntimeContext.getVersionService().incVersion(tempCon, VERSION_ASSIST);
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
     * com.estudio.impl.design.utils.ICodeAssistService#exchange(java.sql.Connection
     * , long, long)
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
            RuntimeContext.getVersionService().incVersion(tempCon, VERSION_ASSIST);
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
     * com.estudio.impl.design.utils.ICodeAssistService#saveRecord(java.sql.
     * Connection, com.estudio.define.utils.CodeAssistRecord)
     */
    @Override
    public boolean saveRecord(final Connection con, final CodeAssistRecord record) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            if (record.isNew()) {
                cmd = insertCMD.clone(tempCon);
                record.setId(DBHELPER.getUniqueID(tempCon));
            } else
                cmd = updateCMD.clone(tempCon);
            cmd.setParam("id", record.getId());
            cmd.setParam("caption", record.getCaption());
            cmd.setParam("content", Convert.str2Bytes(record.getContent()));
            cmd.setParam("pid", record.getPid());
            cmd.setParam("help", Convert.str2Bytes(record.getHelp()));
            cmd.setParam("sortorder", record.getSortorder());
            cmd.setParam("type", record.getType());
            cmd.setParam("exttype", record.getExtType());
            record.setOld();
            cmd.execute();
            RuntimeContext.getVersionService().incVersion(tempCon, VERSION_ASSIST);
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
     * com.estudio.impl.design.utils.ICodeAssistService#deleteRecord(java.sql
     * .Connection, long)
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
            RuntimeContext.getVersionService().incVersion(tempCon, VERSION_ASSIST);
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
     * com.estudio.impl.design.utils.ICodeAssistService#fillRecord(com.estudio
     * .intf.db.IDBCommand, com.estudio.define.utils.CodeAssistRecord)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final CodeAssistRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setCaption(cmd.getString("CAPTION"));
        record.setContent(Convert.bytes2Str(cmd.getBytes("CONTENT")));
        record.setHelp(Convert.bytes2Str(cmd.getBytes("HELP")));
        record.setType(cmd.getInt("TYPE"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.utils.ICodeAssistService#getRecord(java.sql.
     * Connection, long)
     */
    @Override
    public CodeAssistRecord getRecord(final Connection con, final long id) throws Exception {
        CodeAssistRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new CodeAssistRecord();
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
     * @see com.estudio.impl.design.utils.ICodeAssistService#newRecord()
     */
    @Override
    public CodeAssistRecord newRecord() {
        return new CodeAssistRecord();
    }

}
