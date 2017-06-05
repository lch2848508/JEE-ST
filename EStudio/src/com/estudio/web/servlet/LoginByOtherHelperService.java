package com.estudio.web.servlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.ExceptionUtils;

public final class LoginByOtherHelperService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    static final String SELECT_SQL = "select id,login_userid,uuid,rndcode,regdate,isvalid from sys_userlogin_by_others where id=:id";
    static final String UPDATE_SQL = "update sys_userlogin_by_others set id=:id,login_userid=:login_userid,uuid=:uuid,rndcode=:rndcode,regdate=:regdate,isvalid=:isvalid where id=:id";
    static final String INSERT_SQL = "insert into sys_userlogin_by_others(id,login_userid,uuid,rndcode) values (:id,:login_userid,:uuid,:rndcode)";
    static final String DELETE_SQL = "delete from sys_userlogin_by_others where id=:id";
    static final String LIST_SQL = "select id,login_userid,uuid,rndcode,regdate,isvalid from sys_userlogin_by_others";
    static final String EXCHANGE_SQL = "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from SYS_USERLOGIN_BY_OTHERS where id = :id1; select sortorder into idx_2 from SYS_USERLOGIN_BY_OTHERS where id = :id2; update SYS_USERLOGIN_BY_OTHERS set sortorder = idx_2 where id = :id1;  update SYS_USERLOGIN_BY_OTHERS set sortorder = idx_1 where id = :id2; end;";
    static final String MOVE_SQL = "update SYS_USERLOGIN_BY_OTHERS set p_id=:p_id where id = :id";

    private IDBCommand selectCMD;
    private IDBCommand updateCMD;
    private IDBCommand insertCMD;
    private IDBCommand deleteCMD;
    private IDBCommand listCMD;
    private IDBCommand exchangeCMD;
    private IDBCommand movetoCMD;

    {
        try {
            selectCMD = DBHELPER.getCommand(null, SELECT_SQL);
            updateCMD = DBHELPER.getCommand(null, UPDATE_SQL);
            insertCMD = DBHELPER.getCommand(null, INSERT_SQL);
            deleteCMD = DBHELPER.getCommand(null, DELETE_SQL);
            listCMD = DBHELPER.getCommand(null, LIST_SQL);
            exchangeCMD = DBHELPER.getCommand(null, EXCHANGE_SQL);
            movetoCMD = DBHELPER.getCommand(null, MOVE_SQL);
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 移动节点
     * 
     * @param con
     * @param id
     * @param p_id
     * @return
     * @throws SQLException
     *             , DBException
     */
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
     * 
     * @param con
     * @param id_1
     * @param id_2
     * @return
     * @throws SQLException
     *             , DBException
     */
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
     * 
     * @param con
     * @param record
     * @return
     * @throws SQLException
     *             , DBException
     */
    public boolean saveRecord(final Connection con, final LoginByOtherHelperRecord record) throws Exception {
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
            cmd.setParam("login_userid", record.getLoginUserid());
            cmd.setParam("uuid", record.getUuid());
            cmd.setParam("rndcode", record.getRndcode());
            cmd.setParam("regdate", record.getRegdate());
            cmd.setParam("isvalid", record.getIsvalid());
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

    /**
     * 删除记录
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     */
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
     * /** 填充记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public void fillRecord(final IDBCommand cmd, final LoginByOtherHelperRecord record) throws Exception {
        record.setId(cmd.getInt("ID"));
        record.setLoginUserid(cmd.getInt("LOGIN_USERID"));
        record.setUuid(cmd.getString("UUID"));
        record.setRndcode(cmd.getInt("RNDCODE"));
        record.setRegdate(cmd.getDate("REGDATE"));
        record.setIsvalid(cmd.getInt("ISVALID"));
        record.setOld();
    }

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public LoginByOtherHelperRecord getRecord(final Connection con, final long id) throws Exception {
        LoginByOtherHelperRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new LoginByOtherHelperRecord();
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
     * 得到数据集
     * 
     * @param con
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     */
    public ArrayList<LoginByOtherHelperRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<LoginByOtherHelperRecord> records = new ArrayList<LoginByOtherHelperRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final LoginByOtherHelperRecord record = new LoginByOtherHelperRecord();
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
     * 增加一条记录
     * 
     * @return
     */
    public LoginByOtherHelperRecord newRecord() {
        return new LoginByOtherHelperRecord();
    }

    private static LoginByOtherHelperService instance = new LoginByOtherHelperService();

    public static LoginByOtherHelperService getInstance() {
        return instance;
    }

    private LoginByOtherHelperService() {
    }

}
