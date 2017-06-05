package com.estudio.impl.design.user;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.define.design.user.UserInfoRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.user.IUserInfoService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;

public abstract class DBUserInfoService implements IUserInfoService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getRegisterUser2RoleSQL();

    protected abstract String getMovetoSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getListSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getInsertSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getSelectSQL();

    protected abstract String getUnregisterUser2RoleSQL();

    protected abstract String getUserRoleListSQL();

    protected abstract String getListByRoleSQL();

    IDBCommand selectCMD;
    IDBCommand updateCMD;
    IDBCommand insertCMD;
    IDBCommand deleteCMD;
    IDBCommand listCMD;
    IDBCommand exchangeCMD;
    IDBCommand movetoCMD;
    IDBCommand registerUser2RoleCMD;
    IDBCommand unregisterUser2RoleCMD;
    IDBCommand userRoleListCMD;
    IDBCommand listByRoleCMD;

    /**
     * 初始化
     */
    private void initDBCommand() {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMovetoSQL());
            registerUser2RoleCMD = DBHELPER.getCommand(null, getRegisterUser2RoleSQL());
            unregisterUser2RoleCMD = DBHELPER.getCommand(null, getUnregisterUser2RoleSQL());
            userRoleListCMD = DBHELPER.getCommand(null, getUserRoleListSQL());
            listByRoleCMD = DBHELPER.getCommand(null, getListByRoleSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 构造函数
     */
    protected DBUserInfoService() {
        super();
        initDBCommand();
    }

    @Override
    public ArrayList<Long> getCommonRoles(final Connection con, final String ids) throws Exception {
        final ArrayList<Long> result = new ArrayList<Long>();
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, "select r_id from (select count(*) c, r_id from sys_user2role where u_id in (" + ids.replace(";", ",") + ") group by r_id ) t where c=" + ids.split(";").length, true);
            stmt.executeQuery();
            while (stmt.next())
                result.add(stmt.getLong(1));

        } finally {
            DBHELPER.closeCommand(stmt);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public ArrayList<Long> getUserRoleIDS(final Connection con, final long id) throws Exception {
        final ArrayList<Long> result = new ArrayList<Long>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = userRoleListCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery())
                while (cmd.next())
                    result.add(cmd.getLong(1));
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public boolean unregisterUsers2Roles(final Connection con, final String[] uids, final String[] rids) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = unregisterUser2RoleCMD.clone(tempCon);
            for (final String uid2 : uids) {
                final long uid = Convert.try2Long(uid2, Long.MIN_VALUE);
                cmd.setParam("u_id", uid);
                for (final String rid2 : rids) {
                    final long rid = Convert.try2Long(rid2, Long.MIN_VALUE);
                    cmd.setParam("r_id", rid);
                    cmd.execute();
                }
            }
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public boolean registerUsers2Roles(final Connection con, final String[] uids, final String[] rids) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = registerUser2RoleCMD.clone(tempCon);
            for (final String uid2 : uids) {
                final long uid = Convert.try2Long(uid2, Long.MIN_VALUE);
                cmd.setParam("u_id", uid);
                for (final String rid2 : rids) {
                    final long rid = Convert.try2Long(rid2, Long.MIN_VALUE);
                    cmd.setParam("r_id", rid);
                    cmd.execute();
                }
            }
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
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

    @Override
    public boolean saveRecord(final Connection con, final UserInfoRecord record) throws Exception {
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
            cmd.setParam("realname", record.getRealname());
            cmd.setParam("loginname", record.getLoginname());
            cmd.setParam("sex", record.getSex());
            cmd.setParam("password", record.getPassword());
            cmd.setParam("mobile", record.getMobile());
            cmd.setParam("phone", record.getPhone());
            cmd.setParam("address", record.getAddress());
            cmd.setParam("postcode", record.getPostcode());
            cmd.setParam("email", record.getEmail());
            cmd.setParam("duty", record.getDuty());
            cmd.setParam("photo", record.getPhoto());
            cmd.setParam("p_id", record.getPId());
            cmd.setParam("ext1", record.getExt1());
            cmd.setParam("ext2", record.getExt2());
            cmd.setParam("ext3", record.getExt3());
            record.setOld();
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;

    }

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
            cmd.execute();
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public boolean deleteRecords(final Connection con, final String[] ids) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = deleteCMD.clone(tempCon);
            List<Long> filterIds = new ArrayList<Long>();
            for (final String id : ids) {
                long userId = Convert.str2Long(id);
                if (userId < 0) {
                    filterIds.add(userId);
                    continue;
                }
                cmd.setParam(1, userId);
                cmd.execute();
            }
            if (!filterIds.isEmpty())
                throw new Exception("部分用户不允许被删除,请刷新列表。");
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public void fillRecord(final IDBCommand cmd, final UserInfoRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setRealname(cmd.getString("REALNAME"));
        record.setLoginname(cmd.getString("LOGINNAME"));
        record.setSex(cmd.getInt("SEX"));
        record.setPassword(cmd.getString("PASSWORD"));
        record.setMobile(cmd.getString("MOBILE"));
        record.setPhone(cmd.getString("PHONE"));
        record.setAddress(cmd.getString("ADDRESS"));
        record.setPostcode(cmd.getString("POSTCODE"));
        record.setEmail(cmd.getString("EMAIL"));
        record.setDuty(cmd.getString("DUTY"));
        record.setPhoto(cmd.getBytes("PHOTO"));
        record.setPId(cmd.getLong("P_ID"));
        record.setExt1(cmd.getString("EXT1"));
        record.setExt2(cmd.getString("EXT2"));
        record.setExt3(cmd.getString("EXT3"));
        record.setOld();
    }

    @Override
    public UserInfoRecord getRecord(final Connection con, final long id) throws Exception {
        UserInfoRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new UserInfoRecord();
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
    public ArrayList<UserInfoRecord> getRecordsByRole(final Connection con, final long rid, final long uid) throws Exception {
        final ArrayList<UserInfoRecord> records = new ArrayList<UserInfoRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listByRoleCMD.clone(tempCon);
            cmd.setParam("u_id", uid);
            cmd.setParam("r_id", rid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final UserInfoRecord record = new UserInfoRecord();
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
    public ArrayList<UserInfoRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<UserInfoRecord> records = new ArrayList<UserInfoRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final UserInfoRecord record = new UserInfoRecord();
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
    public UserInfoRecord newRecord() {
        return new UserInfoRecord();
    }

    @Override
    public boolean isLoginNameExists(final String id, final String loginName) throws Exception {
        boolean result = false;
        final String SQL = getLoginNameExistSQL();
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("loginname", loginName);
        result = Convert.obj2Int(DBHELPER.executeScalar(SQL, params, null), 0) != 0;
        return result;
    }

    protected abstract String getLoginNameExistSQL();

}
