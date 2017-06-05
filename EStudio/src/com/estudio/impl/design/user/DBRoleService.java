package com.estudio.impl.design.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.user.RoleRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.user.IRoleService;
import com.estudio.utils.ExceptionUtils;

public abstract class DBRoleService implements IRoleService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getMovetoSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getListSQL();

    protected abstract String getDeleteRoleSQL();

    protected abstract String getInsertSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getSelectSQL();

    protected abstract String getSelectTypeSQL();

    protected abstract String getInsertTypeSQL();

    protected abstract String getDeleteRoleTypeSQL();

    protected abstract String getUpdateTypeSQL();

    protected abstract String getMoveRoleToTypeSQL();

    protected abstract String getExchangeRoleTypeSQL();

    private IDBCommand selectCMD;
    private IDBCommand updateCMD;
    private IDBCommand insertCMD;
    private IDBCommand deleteCMD;
    private IDBCommand listCMD;
    private IDBCommand exchangeCMD;
    private IDBCommand movetoCMD;

    private IDBCommand selectTypeCMD;
    private IDBCommand insertTypeCMD;
    private IDBCommand updateTypeCMD;
    private IDBCommand deleteTypeCMD;
    private IDBCommand moveRoleTypeToCMD;
    private IDBCommand exchangeRoleTypeCMD;

    /**
     * 构造函数
     */
    protected DBRoleService() {
        super();
        initDBCommand();
    }

    /**
     * 初始化参数
     */
    private void initDBCommand() {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteRoleSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMovetoSQL());

            selectTypeCMD = DBHELPER.getCommand(null, getSelectTypeSQL());
            insertTypeCMD = DBHELPER.getCommand(null, getInsertTypeSQL());
            updateTypeCMD = DBHELPER.getCommand(null, getUpdateTypeSQL());
            deleteTypeCMD = DBHELPER.getCommand(null, getDeleteRoleTypeSQL());
            moveRoleTypeToCMD = DBHELPER.getCommand(null, getMoveRoleToTypeSQL());
            exchangeRoleTypeCMD = DBHELPER.getCommand(null, getExchangeRoleTypeSQL());

        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

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
            if (cmd != null)
                cmd.close();
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public boolean moveRoleTypeTo(final Connection con, final long id, final long p_id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = moveRoleTypeToCMD.clone(tempCon);
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

    @Override
    public boolean exchange(final Connection con, final long id_1, final long id_2) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = exchangeCMD.clone(tempCon);
            cmd.setParam("id1", Long.toString(id_1));
            cmd.setParam("id2", Long.toString(id_2));
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 交换顺序
     * 
     * @param object
     * @param id1
     * @param id2
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public boolean exchangeRoleType(final Connection con, final long id_1, final long id_2) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = exchangeRoleTypeCMD.clone(tempCon);
            cmd.setParam("id1", Long.toString(id_1));
            cmd.setParam("id2", Long.toString(id_2));
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public boolean saveRecord(final Connection con, final RoleRecord record) throws Exception {
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
            cmd.setParam("descript", record.getDescript());
            cmd.setParam("p_id", record.getPid());
            result = cmd.execute();
            record.setOld();
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;

    }

    @Override
    public boolean deleteRole(final Connection con, final long id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = deleteCMD.clone(tempCon);
            cmd.setParam("ID", id);
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public void fillRecord(final IDBCommand cmd, final RoleRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setName(cmd.getString("name"));
        record.setDescript(cmd.getBytes("descript"));
        record.setOld();
    }

    @Override
    public RoleRecord getRecord(final Connection con, final long id) throws Exception {
        RoleRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new RoleRecord();
                fillRecord(cmd, record);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return record;
    }

    @Override
    public ArrayList<RoleRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<RoleRecord> records = new ArrayList<RoleRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final RoleRecord record = new RoleRecord();
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

    @Override
    public RoleRecord newRecord() {
        return new RoleRecord();
    }

    @Override
    public JSONObject saveRoleType(final Connection con, final long roleId, final String roleTypeName) throws Exception {
        final JSONObject json = new JSONObject();
        IDBCommand cmd = null;
        long id = roleId;
        try {
            if (id == -1) {
                id = DBHELPER.getUniqueID();
                cmd = insertTypeCMD.clone(con);
                cmd.setParam("name", roleTypeName);
                cmd.setParam("id", id);
                cmd.execute();
                json.put("id", id);
                json.put("n", roleTypeName);
            } else {
                cmd = updateTypeCMD.clone(con);
                cmd.setParam("name", roleTypeName);
                cmd.setParam("id", id);
                cmd.execute();
                json.put("n", roleTypeName);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    @Override
    public boolean deleteRoleType(final Connection con, final long id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = deleteTypeCMD.clone(tempCon);
            cmd.setParam("ID", id);
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public JSONObject getRoleTypeInfp(final Connection con, final long id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        final JSONObject json = new JSONObject();
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectTypeCMD.clone(tempCon);
            cmd.setParam(1, id);
            cmd.executeQuery();
            if (cmd.next()) {
                json.put("n", cmd.getString(2));
                json.put("id", cmd.getString(1));
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }
}
