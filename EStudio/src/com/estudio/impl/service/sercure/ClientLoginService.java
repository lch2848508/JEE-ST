package com.estudio.impl.service.sercure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.define.ResultInfo;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.web.sercure.IClientLoginService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;

public abstract class ClientLoginService implements IClientLoginService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected ClientLoginService() {
        super();
    }

    protected abstract String getLoginByOtherWaySQL();

    protected abstract String getUpdateLoginByOthersSQL();

    protected abstract String getLoginInfoSQL();

    protected abstract String getUserIdSQL();

    protected abstract String getUserInfoRoleSQL();

    protected abstract String getChangePasswordSQL();

    @Override
    public void logoff(final HttpSession session) {
        session.setAttribute("__LOGININFO__", null);
        session.invalidate();

    }

    @Override
    public boolean isLogined(final HttpSession session) {
        return getLoginInfo(session) != null;
    }

    @Override
    public void registerLoginInfo(final HttpSession session, final ClientLoginInfo info) {
    	session.setAttribute("__LOGININFO__", info);
        session.setAttribute("SYSTEM_LOGIN_NAME", info.getRealName());
    }

    @Override
    public ClientLoginInfo getLoginInfo(final HttpSession session) {
        ClientLoginInfo loginInfo = null;
        loginInfo = (ClientLoginInfo) session.getAttribute("__LOGININFO__");
        return loginInfo;
    }

    @Override
    public ResultInfo changePassword(final ClientLoginInfo info, final String oldpwd, final String newpwd) {
        ResultInfo result = null;
        if (!StringUtils.equals(oldpwd, info.getPassword()))
            result = new ResultInfo(false, "旧密码不正确!");
        else {
            Connection con = null;
            IDBCommand stmt = null;
            try {
                final String changePwdSQL = getChangePasswordSQL();
                con = DBHELPER.getConnection();
                stmt = DBHELPER.getCommand(con, changePwdSQL);
                stmt.setParam(1, newpwd);
                stmt.setParam(2, info.getId());
                stmt.execute();
                result = new ResultInfo();
                info.setPassword(newpwd);
            } catch (final Exception e) {
                ExceptionUtils.loggerException(e, con);

                result = new ResultInfo(true, e.getMessage());
            } finally {
                DBHELPER.closeCommand(stmt);
                DBHELPER.closeConnection(con);
            }
        }

        return result;
    }

    @Override
    public ClientLoginInfo login(final String user, final String password, final String userType, final String userArea, final String orgId, String sessionId) {
        ClientLoginInfo result = null;
        Connection con = null;
        IDBCommand stmt = null;
        IDBCommand idStmt = null;
        try {
            final String loginSQL = getLoginInfoSQL();
            con = DBHELPER.getConnection();
            idStmt = DBHELPER.getCommand(con, getUserIdSQL(), true);
            idStmt.setParam(1, user.toLowerCase());
            idStmt.setParam(2, password);
            idStmt.setParam(3, userType);
            idStmt.setParam(4, userArea);
            idStmt.setParam(5, orgId);
            idStmt.executeQuery();
            idStmt.next();
            long loginUserId = idStmt.getLong(1);

            stmt = DBHELPER.getCommand(con, loginSQL, true);
            stmt.setParam(1, loginUserId);
            stmt.executeQuery();
            if (stmt.next()) {
                result = new ClientLoginInfo(stmt.getLong(1), stmt.getString(2), stmt.getString(3), stmt.getString(4), stmt.getLong(5), stmt.getString(6), stmt.getString(7), stmt.getString(8), sessionId, stmt.getString("duty"), stmt.getInt("is_mis_role") == 1, stmt.getInt("is_gis_role") == 1);

            }
            if (result != null) {
                DBHELPER.closeCommand(idStmt);
                idStmt = null;
                DBHELPER.closeCommand(stmt);
                stmt = null;
                final String roleSQL = getUserInfoRoleSQL();
                stmt = DBHELPER.getCommand(con, roleSQL);
                stmt.setParam(1, result.getId());
                stmt.executeQuery();
                while (stmt.next())
                    result.getRoles().add(stmt.getLong(1));
                loggerLoginInfo(result, con); // 记录到日志
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeCommand(idStmt);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    @Override
    public ClientLoginInfo loginByOther(final String uuid, final String code, String sessionId) {
        ClientLoginInfo result = null;
        Connection con = null;
        IDBCommand stmt = null;
        try {
            final String loginSQL = getLoginByOtherWaySQL();
            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, loginSQL, true);
            stmt.setParam(1, code);
            stmt.setParam(2, uuid);
            stmt.executeQuery();
            if (stmt.next())
                result = new ClientLoginInfo(stmt.getLong(1), stmt.getString(2), stmt.getString(3), stmt.getString(4), stmt.getLong(5), stmt.getString(6), stmt.getString(7), stmt.getString(7), sessionId, stmt.getString("duty"), true, true);
            if (result != null) {
                DBHELPER.closeCommand(stmt);
                final String roleSQL = getUserInfoRoleSQL();
                stmt = DBHELPER.getCommand(con, roleSQL, true);
                stmt.setParam(1, result.getId());
                stmt.executeQuery();
                while (stmt.next())
                    result.getRoles().add(stmt.getLong(1));
                loggerLoginInfo(result, con); // 记录日志
            }
            DBHELPER.closeCommand(stmt);
            stmt = DBHELPER.getCommand(con, getUpdateLoginByOthersSQL(), true);
            stmt.setParam(1, uuid);
            stmt.execute();
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 记录用户日志信息
     * 
     * @param result
     * @param con
     * @throws Exception
     */
    private void loggerLoginInfo(ClientLoginInfo clientLoginInfo, Connection con) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", clientLoginInfo.getId());
        params.put("session_id", clientLoginInfo.getSessionId());
        params.put("ip", GlobalContext.getClientInfo().getIpAddress());
        params.put("user_name", clientLoginInfo.getRealName());
        params.put("id", DBHELPER.getUniqueID(con, "SEQ_FOR_J2EE_LOGIN_LOGGER"));
        DBHELPER.execute("insert into sys_userlogin_logger (id, user_id, session_id, ip, user_name) values (:id, :user_id, :session_id, :ip, :user_name)", params, con);
    }

    @Override
    public JSONObject registerTempUserInfo(String sessionId, String userName, int sex, String cardId, String mobile, String address, String email) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        Connection con = null;
        PreparedStatement stmt = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select count(*) from sys_userinfo_register where mobile=?");
            stmt.setString(1, mobile);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) != 0) {
                json.put("msg", "该手机号已经注册过，请使用新的手机号注册。");
            } else {
                String sql = "insert into sys_userinfo_register\n" + //
                        "  (id, realname, loginname, sex, mobile, address, email, idcard, registerdate, sessionid)\n" + //
                        "values\n" + //
                        "  (:id, :realname, :loginname, :sex, :mobile, :address, :email, :idcard, :registerdate, :sessionid)";
                cmd = DBHELPER.getCommand(con, sql);
                cmd.setParam("id", DBHELPER.getUniqueID(con));
                cmd.setParam("realname", userName);
                cmd.setParam("loginname", mobile);
                cmd.setParam("sex", sex);
                cmd.setParam("idcard", cardId);
                cmd.setParam("mobile", mobile);
                cmd.setParam("address", address);
                cmd.setParam("email", email);
                cmd.setParam("sessionid", sessionId);
                cmd.setParam("registerdate", Convert.date2SQLDateTime(Calendar.getInstance().getTime()));
                cmd.execute();
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }
}
