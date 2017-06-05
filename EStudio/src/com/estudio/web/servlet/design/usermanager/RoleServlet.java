package com.estudio.web.servlet.design.usermanager;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.user.RoleRecord;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.user.IRoleService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class RoleServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IRoleService service = RuntimeContext.getRoleService();

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String operation = request.getParameter("o");
        if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecord(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.endsWith("exchange", operation))
            response.getWriter().println(exchangeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.endsWith("moveto", operation))
            response.getWriter().println(moveTo(request.getParameter("from"), getParamLong("to", -1)));
        else if (StringUtils.equals("saveRoleType", operation))
            response.getWriter().println(saveRoleType(request));
        else if (StringUtils.equals("deleteRoleType", operation))
            response.getWriter().println(deleteRoleType(getParamLong("id")));
        else if (StringUtils.equals("moveRoleTypeTo", operation))
            response.getWriter().println(moveRoleTypeTo(request.getParameter("from"), getParamLong("to", -1)));
        else if (StringUtils.equals("exchangeRoleType", operation))
            response.getWriter().println(exchangeRoleTypeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.equals("getRoleTypeInfo", operation))
            response.getWriter().println(getTypeInfo(getParamLong("id", Long.MIN_VALUE)));
        else response.getWriter().println("{r:false,msg:'无效请求'}");
    }

    /**
     * 删除角色类型列表
     * 
     * @param paramLong
     * @return
     */
    private JSONObject deleteRoleType(final long id) {
        final JSONObject json = new JSONObject();
        try {
            service.deleteRoleType(null, id);
            json.put("r", true);
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
            final ArrayList<RoleRecord> records = service.getRecords(null, id);
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
        RoleRecord record = null;
        try {
            con = DBHELPER.getConnection();
            final long id = getParamLong("id", -1);
            record = service.getRecord(con, id);
            if (record == null)
                record = service.newRecord();
            record.setName(request.getParameter("n"));
            record.setDescript(Convert.str2Bytes(request.getParameter("dsc")));
            record.setPid(getParamLong("p_id", -1));
            service.saveRecord(con, record);
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
     * 保存角色类型
     * 
     * @param request
     * @return
     */
    private JSONObject saveRoleType(final HttpServletRequest request) {
        JSONObject json = null;
        Connection con = null;
        try {
            con = DBHELPER.getConnection();

            final String roleTypeName = getParamStr("name");
            final long id = getParamLong("id");

            json = service.saveRoleType(con, id, roleTypeName);

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
        JSONObject json = null;
        try {
            final RoleRecord record = service.getRecord(null, id);
            if (record != null) {
                json = record.getJSON();
                json.put("r", true);
            } else {
                json = new JSONObject();
                JSONUtils.except2JSON(json, new Exception("记录不存在或已经被删除！"));
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            json = new JSONObject();
            JSONUtils.except2JSON(json, e);

        }
        return json;
    }

    private JSONObject getTypeInfo(final long id) {
        JSONObject json = null;
        try {
            json = service.getRoleTypeInfp(null, id);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            json = new JSONObject();
            JSONUtils.except2JSON(json, e);

        }
        return json;
    }

    /**
     * 删除记录
     * 
     * @param try2Int
     * @return
     */
    private JSONObject deleteRecord(final long id) {
        final JSONObject json = new JSONObject();
        try {
            service.deleteRole(null, id);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);

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
     * 移动节点
     * 
     * @param parameter
     * @param try2Int
     * @return
     */
    private JSONObject moveRoleTypeTo(final String formIDS, final long toID) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            con.setAutoCommit(false);
            final String[] ids = formIDS.split(";");
            for (final String id : ids)
                service.moveRoleTypeTo(con, Convert.try2Long(id, -1l), toID);
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

    /**
     * 交换节点顺序
     * 
     * @param id1
     * @param id2
     * @return
     */
    private JSONObject exchangeRoleTypeSortorder(final long id1, final long id2) {
        final JSONObject json = new JSONObject();
        try {
            service.exchangeRoleType(null, id1, id2);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);

        }
        return json;
    }

}
