package com.estudio.impl.design.user;

import java.sql.Connection;
import java.util.ArrayList;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.user.DepartmentRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.user.IDepartmentService;
import com.estudio.utils.ExceptionUtils;

public abstract class DBDepartmentService implements IDepartmentService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getMovetoSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getListSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getInsertSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getSelectSQL();

    private IDBCommand selectCMD;
    private IDBCommand updateCMD;
    private IDBCommand insertCMD;
    private IDBCommand deleteCMD;
    private IDBCommand listCMD;
    private IDBCommand exchangeCMD;
    private IDBCommand movetoCMD;

    /**
     * 构造函数
     */
    protected DBDepartmentService() {
        super();
        initCommand();
    }

    /**
     * 初始化参数
     */
    protected final void initCommand() {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMovetoSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 移动到
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

    /**
     * 交换顺序
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

    /**
     * 保存记录
     */
    @Override
    public boolean saveRecord(final Connection con, final DepartmentRecord record) throws Exception {
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
            cmd.setParam("p_id", record.getPId());
            cmd.setParam("name", record.getName());
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

    /**
     * 删除记录
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

    /**
     * 填充记录
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final DepartmentRecord record) throws Exception {
        record.setPId(cmd.getLong("P_ID"));
        record.setId(cmd.getLong("ID"));
        record.setName(cmd.getString("name"));
        record.setOld();
    }

    /**
     * 得到记录
     */
    @Override
    public DepartmentRecord getRecord(final Connection con, final long id) throws Exception {
        DepartmentRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new DepartmentRecord();
                fillRecord(cmd, record);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return record;
    }

    /**
     * 得到记录集
     */
    @Override
    public ArrayList<DepartmentRecord> getRecords(final Connection con, final long pid, final long uid) throws Exception {
        final ArrayList<DepartmentRecord> records = new ArrayList<DepartmentRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, uid);
            cmd.setParam(2, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final DepartmentRecord record = new DepartmentRecord();
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

    /**
     * 新增记录
     */
    @Override
    public DepartmentRecord newRecord() {
        return new DepartmentRecord();
    }

}
