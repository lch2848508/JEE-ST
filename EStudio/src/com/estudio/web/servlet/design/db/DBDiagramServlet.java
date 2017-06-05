package com.estudio.web.servlet.design.db;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.db.DBDiagramRecord;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.db.IDBDiagramService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class DBDiagramServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IDBDiagramService service = RuntimeContext.getDbDiagramService();

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
            response.getWriter().println(deleteRecords(request.getParameter("ids").split(";")));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("exchange", operation))
            response.getWriter().println(exchangeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.equals("gettree", operation))
            response.getWriter().println(getEntryAndDiagramTree(getParamLong("ev", -1), getParamLong("dv", -1)));
        else if (StringUtils.equals("moveto", operation))
            response.getWriter().println(moveTo(request.getParameter("from"), getParamLong("to", -1)));
        else if (StringUtils.equals("getentrys", operation))
            response.getWriter().println(service.getDiagramDBEntrys(getParamLong("id"), getParamLong("dv", -1), getParamLong("lv", -1)));
        else if (StringUtils.equals("savediagram", operation))
            response.getWriter().println(service.saveDiagramDBEntrys(getParamLong("id"), request.getParameter("entrys")));
        else if (StringUtils.equals("gettreeversion", operation))
            response.getWriter().println(service.getTreeVersion());
    }

    /**
     * 删除记录
     * 
     * @param parameter
     * @return
     */
    private JSONObject deleteRecords(final String[] ids) {
        final long[] is = new long[ids.length];
        for (int i = 0; i < ids.length; i++)
            is[i] = Convert.try2Long(ids[i], Long.MIN_VALUE);
        final JSONObject json = new JSONObject();
        try {
            service.deleteRecords(null, is);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);
        }
        return json;
    }

    /**
     * 得到实体和Diagram列表
     * 
     * @param string
     * 
     * @return
     */
    private JSONObject getEntryAndDiagramTree(final long ev, final long dv) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final long s_ev = RuntimeContext.getDbEntryService().getDDLVersion(con);
            final long s_dv = RuntimeContext.getVersionService().getVersion(con, IDBDiagramService.OBJECT_TYPE);

            if (ev != s_ev) {
                json.put("es", RuntimeContext.getDbEntryService().getEntrysJSON(con));
                json.put("ev", s_ev);
                json.put("ls", RuntimeContext.getDbEntryService().getDBEntryLinksList(con));
                json.put("lv", s_ev);
            }
            if (dv != s_dv) {
                json.put("ds", service.getDiagramsJSON(con));
                json.put("dv", s_dv);
            }
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
     * 列表数据
     * 
     * @param try2Int
     * @return
     */
    private JSONObject listRecords(final long id) {
        final JSONObject json = new JSONObject();
        try {
            final ArrayList<DBDiagramRecord> records = service.getRecords(null);
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
        DBDiagramRecord record = null;
        try {
            con = DBHELPER.getConnection();

            final long id = getParamLong("id", Long.MIN_VALUE);
            record = service.getRecord(con, id);
            if (record == null)
                record = service.newRecord();
            record.setName(request.getParameter("n"));
            record.setDescript(Convert.str2Bytes(request.getParameter("dsc")));

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
     * 得到记录的JSON对象
     * 
     * @param id
     * @return
     */
    private JSONObject getRecordInfo(final long id) {
        JSONObject json = null;
        try {
            final DBDiagramRecord record = service.getRecord(null, id);
            if (record != null) {
                json = record.getJSON();
                json.put("r", true);
            } else {
                json = new JSONObject();
                JSONUtils.except2JSON(json, new Exception("无记录或记录已经被删除！"));
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);

            json = new JSONObject();
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
            DBHELPER.rollback(con, true);
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
