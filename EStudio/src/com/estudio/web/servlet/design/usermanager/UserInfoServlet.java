package com.estudio.web.servlet.design.usermanager;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.user.UserInfoRecord;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.user.IUserInfoService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class UserInfoServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IUserInfoService service = RuntimeContext.getUserInfoService();

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(request.getSession());
        final String operation = request.getParameter("o");
        if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecords(request.getParameter("ids").split(";")));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("listbyrole", operation))
            response.getWriter().println(listRecordsByRole(getParamLong("id", Long.MIN_VALUE), loginInfo.getId()));
        else if (StringUtils.equals("exchange", operation))
            response.getWriter().println(exchangeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.equals("moveto", operation))
            response.getWriter().println(moveTo(request.getParameter("from"), getParamLong("to", -1l)));
        else if (StringUtils.equals("getcommonroles", operation))
            response.getWriter().println(getCommonRoles(request.getParameter("ids")));
        else if (StringUtils.equals("setusersroles", operation))
            response.getWriter().println(setUserRoles(request.getParameter("uids"), request.getParameter("roles"), request.getParameter("unroles")));
        else if (StringUtils.equals("deletefromrole", operation))
            response.getWriter().println(removeUsersFromRole(request.getParameter("ids"), request.getParameter("rid")));
        else if (StringUtils.equals("isloginnameexists", operation))
            response.getWriter().println(isLoginUserExists(request.getParameter("id"), request.getParameter("loginname")));
        else if (StringUtils.endsWith("getphoto", operation)) {
            final ServletOutputStream stream = response.getOutputStream();
            try {
                final UserInfoRecord record = service.getRecord(null, getParamLong("id", Long.MIN_VALUE));
                if ((record.getPhoto() != null) && (record.getPhoto().length > 3)) {
                    response.setContentType("image/jpg");
                    response.setHeader("Pragma", "No-cache");
                    response.setHeader("Cache-Control", "no-cache");
                    response.setDateHeader("Expires", 0);
                    stream.write(record.getPhoto());
                }
            } catch (final Exception e) {
                ExceptionUtils.loggerException(e);
            }
            stream.flush();
            stream.close();
        }
    }

    /**
     * 判断用户登录名是否存在
     * 
     * @param parameter
     * @param parameter2
     * @return
     */
    private JSONObject isLoginUserExists(final String id, final String loginName) {
        final JSONObject json = new JSONObject();
        try {
            json.put("r", service.isLoginNameExists(id, loginName));
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }

    /**
     * 将用户列表从单个角色中删除
     * 
     * @param uids
     * @param rid
     * @return
     */
    private JSONObject removeUsersFromRole(final String uids, final String rid) {
        final JSONObject json = new JSONObject();
        try {
            json.put("r", service.unregisterUsers2Roles(null, uids.split(";"), rid.split(";")));
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }

    /**
     * 设置用户角色
     * 
     * @param parameter
     * @param parameter2
     * @param parameter3
     * @return
     */
    private JSONObject setUserRoles(final String uids, final String roles, final String unroles) {
        final JSONObject json = new JSONObject();
        final Connection con = null;
        try {
            final String[] _uids = uids.split(";");
            if (!StringUtils.isBlank(roles))
                service.registerUsers2Roles(con, _uids, roles.split(";"));
            if (!StringUtils.isBlank(unroles))
                service.unregisterUsers2Roles(con, _uids, unroles.split(";"));
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 得到共同的工作组
     * 
     * @param split
     * @return
     */
    private JSONObject getCommonRoles(final String ids) {
        final JSONObject json = new JSONObject();
        try {
            json.put("ids", service.getCommonRoles(null, ids));
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }

    /**
     * 删除多用户
     * 
     * @param split
     * @return
     */
    private JSONObject deleteRecords(final String[] ids) {
        final JSONObject json = new JSONObject();
        try {
            service.deleteRecords(null, ids);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }

    /**
     * 根据角色ID获取用户列表
     * 
     * @param id
     * @return
     */
    private JSONObject listRecordsByRole(final long id, final long uid) {
        final JSONObject json = new JSONObject();
        try {
            final ArrayList<UserInfoRecord> records = service.getRecordsByRole(null, id, uid);
            json.put("r", true);
            for (int i = 0; i < records.size(); i++)
                JSONUtils.append(json, "record", records.get(i).getJSON());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }

    /**
     * 列表数据
     * 
     * @param try2Int
     * @return
     */
    private JSONObject listRecords(final long id) {
        final JSONObject json = new JSONObject();
        try {
            final ArrayList<UserInfoRecord> records = service.getRecords(null, id);
            json.put("r", true);
            for (int i = 0; i < records.size(); i++)
                JSONUtils.append(json, "record", records.get(i).getJSON());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }

    /**
     * 保存记录
     * 
     * @param request
     * @return
     */
    private JSONObject saveRecord(final HttpServletRequest request) {
        JSONObject json = null;
        Connection con = null;
        UserInfoRecord record = null;
        final String[] uids = new String[1];
        String regisgerRoles;
        String unregisgerRoles;
        try {
            con = DBHELPER.getConnection();
            final long id = this.getParamLong("id", -1l);
            record = service.getRecord(con, id);
            if (record == null)
                record = service.newRecord();
            record.setRealname(getParamStr("realname"));
            record.setLoginname(getParamStr("loginname"));
            record.setSex(this.getParamInt("sex", -1));
            record.setPassword(getParamStr("password"));
            record.setMobile(getParamStr("mobile"));
            record.setPhone(getParamStr("phone"));
            record.setAddress(getParamStr("address"));
            record.setPostcode(getParamStr("postcode"));
            record.setEmail(getParamStr("email"));
            record.setDuty(getParamStr("duty"));
            record.setPhoto(getParamBytes("photo"));
            record.setPId(getParamLong("p_id", -1l));
            record.setExt1(getParamStr("ext1"));
            record.setExt2(getParamStr("ext2"));
            record.setExt3(getParamStr("ext3"));
            regisgerRoles = getParamStr("roles");
            unregisgerRoles = getParamStr("unroles");

            service.saveRecord(con, record);
            uids[0] = Long.toString(record.getId());

            if (!StringUtils.isBlank(regisgerRoles))
                service.registerUsers2Roles(con, uids, regisgerRoles.split(";"));
            if (!StringUtils.isBlank(unregisgerRoles))
                service.unregisterUsers2Roles(con, uids, unregisgerRoles.split(";"));

            json = record.getJSON();
            json.put("r", true);

        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            json = new JSONObject();
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 得到记录的JSON对象
     * 
     * @param id
     * @return
     */
    private JSONObject getRecordInfo(final long id) {
        Connection con = null;
        JSONObject json = null;
        try {
            con = DBHELPER.getConnection();
            final UserInfoRecord record = service.getRecord(con, id);
            if (record != null) {
                json = record.getJSON();
                json.put("roles", service.getUserRoleIDS(con, id));
                json.put("r", true);
            } else {
                json = new JSONObject();
                JSONUtils.except2JSON(json, new Exception("记录不存在或已经被删除!"));
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            json = new JSONObject();
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 移动节点
     * 
     * @param parameter
     * @param try2Int
     * @return
     */
    private JSONObject moveTo(final String formIDS, final long toID) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            con.setAutoCommit(false);
            final String[] ids = formIDS.split(";");
            for (final String id : ids)
                service.moveTo(con, Convert.try2Long(id, -1l), toID);
            con.commit();
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            DBHELPER.rollback(con);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 交换节点顺序
     * 
     * @param id1
     * @param id2
     * @return
     */
    private JSONObject exchangeSortorder(final long id1, final long id2) {
        final JSONObject json = new JSONObject();
        try {
            service.exchange(null, id1, id2);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }
}
