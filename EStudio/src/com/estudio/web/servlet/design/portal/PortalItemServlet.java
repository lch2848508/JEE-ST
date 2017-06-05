package com.estudio.web.servlet.design.portal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.portal.PortalItemRecord;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.portal.IPortalItemService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class PortalItemServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IPortalItemService service = RuntimeContext.getPortalItemService();

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("updateprop", operation))
            response.getWriter().println(updateProperty(getParamLong("id", Long.MIN_VALUE), getParamStr("property")));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecord(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("exchange", operation))
            response.getWriter().println(exchangeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.equals("moveto", operation))
            response.getWriter().println(moveTo(getParamStr("from"), getParamLong("to", -1l)));
        else if (StringUtils.equals("published", operation))
            response.getWriter().println(service.published(getParamLong("id", -1l), Convert.str2Boolean(getParamStr("ispublished"))));
    }

    /**
     * 更新参数
     * 
     * @param paramInt
     * @param paramStr
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONObject updateProperty(final long id, final String property) throws Exception {
        JSONObject json = null;
        Connection con = null;
        PortalItemRecord record = null;
        try {
            con = DBHELPER.getConnection();
            record = service.getRecord(con, id);
            if (record != null) {
                record.setProperty(property);
                service.saveRecord(con, record);
                json = record.getJSON();
                json.put("r", true);
                // 通知
                RuntimeContext.getPortal4ClientGridDefineService().notifyGridDefineIsChanged(id);
                RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            } else {
                json = new JSONObject();
                json.put("r", false);
                json.put("msg", "栏目不存在或已经被删除！");
            }

        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 列表数据
     * 
     * @param try2Int
     * @return
     */
    private JSONObject listRecords(final long id) throws Exception {
        final JSONObject json = new JSONObject();
        final ArrayList<PortalItemRecord> records = service.getRecords(null, id);
        json.put("r", true);
        for (int i = 0; i < records.size(); i++)
            JSONUtils.append(json, "record", records.get(i).getJSON());
        return json;
    }

    /**
     * 保存记录
     * 
     * @param request
     * @return
     */
    private JSONObject saveRecord(final HttpServletRequest request) throws Exception {
        JSONObject json = null;
        Connection con = null;
        PortalItemRecord record = null;
        try {
            con = DBHELPER.getConnection();
            final long id = getParamLong("id", -1l);
            record = service.getRecord(con, id);
            if (record == null)
                record = service.newRecord();
            record.setPId(getParamLong("p_id", -1l));
            record.setName(getParamStr("name"));
            record.setSortorder(getParamLong("sortorder", -1l));
            record.setType(getParamInt("type", 2));
            record.setProperty(getParamStr("property"));
            record.setPublished(getParamInt("published", 0));
            record.setCreatedate(getParamDate("createdate"));
            record.setVersion(getParamLong("version", 0l));
            record.setIcon(getParamStr("icon"));
            record.setWin(getParamInt("win"));
            record.setAutorun(getParamInt("autorun"));
            record.setDisableClose(getParamInt("disableclose"));
            record.setHidden(getParamInt("ishidden"));
            service.saveRecord(con, record);
            json = record.getJSON();
            json.put("r", true);

            // 通知后台服务内容发生变化
            RuntimeContext.getPortal4ClientGridDefineService().notifyGridDefineIsChanged(id);
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();

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
    private JSONObject getRecordInfo(final long id) throws Exception {
        JSONObject json = null;
        final PortalItemRecord record = service.getRecord(null, id);
        if (record != null) {
            json = record.getJSON();
            json.put("r", true);
        } else {
            json = new JSONObject();
            json.put("r", false);
            json.put("msg", "无记录或记录已经被删除！");
        }
        return json;
    }

    /**
     * 删除记录
     * 
     * @param try2Int
     * @return
     */
    private JSONObject deleteRecord(final long id) throws Exception {
        final JSONObject json = new JSONObject();
        service.deleteRecord(null, id);
        json.put("r", true);
        // 通知机制
        RuntimeContext.getPortal4ClientGridDefineService().notifyGridDefineIsChanged(id);
        RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
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
            // 通知缓存 已经发生变化
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
        } catch (final Exception e) {
            DBHELPER.rollback(con);
            json.put("r", false);
            json.put("msg", ExceptionUtils.loggerException(e,con));
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
            // 通知
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
        } catch (final Exception e) {
            json.put("r", false);
            json.put("msg", ExceptionUtils.loggerException(e));
        }
        return json;
    }
}
