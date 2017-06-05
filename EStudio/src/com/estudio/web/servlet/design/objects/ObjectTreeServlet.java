package com.estudio.web.servlet.design.objects;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.objects.ObjectTreeRecord;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectTreeService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class ObjectTreeServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IObjectTreeService service = RuntimeContext.getObjectTreeService();;

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = request.getParameter("o");
        if (StringUtils.equals("gettree", operation))
            response.getWriter().println(service.getObjectsTree(this.getParamLong("pid", -1l), getLoginInfo().getId()));
        else if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(this.getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecord(this.getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(this.getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.endsWith("exchange", operation))
            response.getWriter().println(exchangeSortorder(this.getParamLong("id1", Long.MIN_VALUE), this.getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.endsWith("moveto", operation))
            response.getWriter().println(moveTo(getParamStr("form"), this.getParamLong("to", -1l)));
        else if (StringUtils.equals("vss", operation))
            response.getWriter().println(service.versionControlObject(this.getParamLong("id"), StringUtils.equals("y", getParamStr("ischeckin")), getLoginInfo().getId()));
        else if (StringUtils.equals("getforms", operation))
            response.getWriter().println(service.getFormObjects(this.getParamLong("version", -1l)));
        else if (StringUtils.equals("getreports", operation))
            response.getWriter().println(service.getReportObjects());
        else if (StringUtils.equals("getquerys", operation))
            response.getWriter().println(service.getQueryObjects());

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
            final ArrayList<ObjectTreeRecord> records = service.getRecords(null, id);
            json.put("result", true);
            for (int i = 0; i < records.size(); i++)
                json.put("record", records.get(i).getJSON());
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
        ObjectTreeRecord record = null;
        try {
            con = DBHELPER.getConnection();
            final long id = getParamLong("id", -1);
            record = service.getRecord(con, id);
            if (record == null) {
                record = service.newRecord();
                record.setId(DBHELPER.getUniqueID(con));
                record.setVersion(0);
                record.setLockby(getLoginInfo().getId());
                record.setPid(getParamLong("pid"));
                record.setPropId(record.getId());
                record.setType(getParamLong("type"));
                record.setSortorder(System.currentTimeMillis());
            }
            record.setMemo(request.getParameter("memo"));
            record.setCaption(request.getParameter("caption"));
            service.saveRecord(con, record);
            json = record.getJSON();
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

            json = JSONUtils.except2JSON(e);
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
            final ObjectTreeRecord record = service.getRecord(null, id);
            if (record != null) {
                json = record.getJSON();
                json.put("r", true);
            } else {
                json = new JSONObject();
                json.put("r", false);
                json.put("msg", "无记录或记录已经被删除！");
            }
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
            service.deleteRecord(null, id);
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
            ExceptionUtils.loggerException(e, con);
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
