package com.estudio.web.servlet.design.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.WorkFlowDesignInfo;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectWorkFlowService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class ObjectWorkFlowServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private static final IObjectWorkFlowService SERVICE = RuntimeContext.getObjectWorkFlowService();

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("getcontent", operation)) {
            final OutputStream stream = response.getOutputStream();
            stream.flush();
            downContent(getParamLong("id"), stream);
            stream.close();
        } else if (StringUtils.equals("getVersion", operation))
            response.getWriter().println(getVersion(getParamLong("id")));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecord(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("exchange", operation))
            response.getWriter().println(exchangeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.equals("moveto", operation))
            response.getWriter().println(moveTo(getParamStr("from"), getParamLong("to", -1l)));
        else if (StringUtils.equals("saveWorkFlowItemDesignInfo", operation))
            response.getWriter().println(saveWorkFlowDesignInfo(getParamLong("id"), getParamStr("name"), getParamStr("content")));
        else if (StringUtils.equals("copyWorkFlowItemDesignInfo", operation))
            response.getWriter().println(copyWorkFlowDesignInfo(getParamLong("id"), getParamStr("name")));
        else if (StringUtils.equals("listWorkFlowItemDesignInfos", operation))
            response.getWriter().println(SERVICE.listWorkFlowItemDesignInfos());
        else if (StringUtils.equals("deleteWorkFlowItemDesignInfo", operation))
            response.getWriter().println(SERVICE.deleteWorkFlowDesignInfos(getParamLong("id")));
        else if (StringUtils.equals("exchangeWorkFlowItemDesignInfo", operation))
            response.getWriter().println(SERVICE.exchangeWorkFlowDesignInfo(getParamLong("id1"), getParamLong("id2")));
        else if (StringUtils.equals("getWorkFlowItemDesignInfo", operation))
            response.getWriter().println(SERVICE.getWorkFlowDesignInfo(getParamLong("id"), null));

    }

    /**
     * 复制对象
     * 
     * @param fromId
     * @param newName
     * @return
     * @throws Exception
     */
    private JSONObject copyWorkFlowDesignInfo(final long fromId, final String newName) throws Exception {
        return SERVICE.copyWorkFlowDesignInfo(fromId, newName);
    }

    /**
     * 保存对象
     * 
     * @param id
     * @param name
     * @param content
     * @return
     * @throws Exception
     */
    private JSONObject saveWorkFlowDesignInfo(final long id, final String name, final String content) throws Exception {
        return SERVICE.saveWorkFlowItemDesignInfo(id, name, content);
    }

    /**
     * 下载内容
     * 
     * @param paramInt
     * @param stream
     * @throws SQLException
     *             , DBException
     * @throws IOException
     */
    private void downContent(final long id, final OutputStream stream) throws Exception, IOException {
        final WorkFlowDesignInfo record = SERVICE.getRecord(null, id);
        stream.write(Convert.intToByte(record.getDfm().length));
        stream.write(record.getDfm());
        stream.write(Convert.intToByte(record.getProperty().length));
        stream.write(record.getProperty());
    }

    /**
     * 取得版本号
     * 
     * @param id
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private JSONObject getVersion(final long id) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("version", SERVICE.getVersion(null, id));
        json.put("r", true);
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
        final ArrayList<WorkFlowDesignInfo> records = SERVICE.getRecords(null, id);
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
     * @throws Exception
     */
    private JSONObject saveRecord(final HttpServletRequest request) throws Exception {
        JSONObject json = null;
        Connection con = null;
        WorkFlowDesignInfo record = null;
        boolean isCommited = false;
        try {
            con = DBHELPER.getConnection();
            con.setAutoCommit(false);
            final long id = getParamLong("id", -1l);
            record = SERVICE.getRecord(con, id);
            if (record == null) {
                record = SERVICE.newRecord();
                record.setId(id);
            }
            record.setVersion(getParamLong("version", 1l));
            record.setStatus(getParamStr("status"));
            record.setDescript(getParamStr("descript"));
            record.setDfm(getParamBytes("dfm"));
            record.setProperty(getParamBytes("property"));
            SERVICE.saveRecord(con, record);
            json = record.getJSON();
            json.put("r", true);
            json.put("version", SERVICE.getVersion(con, id));
            con.commit();
            isCommited = true;
            con.setAutoCommit(true);
        } finally {
            if (!isCommited)
                DBHELPER.rollback(con);
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
        final WorkFlowDesignInfo record = SERVICE.getRecord(null, id);
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
     * @throws Exception
     */
    private JSONObject deleteRecord(final long id) throws Exception {
        final JSONObject json = new JSONObject();
        SERVICE.deleteRecord(null, id);
        json.put("r", true);
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
                SERVICE.moveTo(con, Convert.try2Long(id, -1l), toID);
            con.commit();
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            DBHELPER.rollback(con);
            json.put("r", false);
            json.put("msg", e.getMessage());
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
            SERVICE.exchange(null, id1, id2);
            json.put("r", true);
        } catch (final Exception e) {
            json.put("r", false);
            json.put("msg", ExceptionUtils.loggerException(e));
        }
        return json;
    }
}
