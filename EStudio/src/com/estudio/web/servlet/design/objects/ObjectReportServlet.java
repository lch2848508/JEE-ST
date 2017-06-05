package com.estudio.web.servlet.design.objects;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.objects.ObjectReportRecord;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectReportService;
import com.estudio.web.servlet.BaseServlet;

public class ObjectReportServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IObjectReportService service = RuntimeContext.getObjectReportService();

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("getTemplate", operation))
            service.getTemplate(null, getParamLong("id", Long.MIN_VALUE), response.getOutputStream());
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecord(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("saveTemplate", operation))
            response.getWriter().println(saveTemplate(getParamLong("id"), getParamStr("filename"), getParamStr("template")));
        else if (StringUtils.equals("downloadTemplate", operation))
            response.getWriter().println(downloadTemplate(getParamLong("id")));
    }

    /**
     * 下载模板文件
     * 
     * @param paramLong
     * @return
     * @throws Exception
     */
    private JSONObject downloadTemplate(long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            service.downloadTemplate(con, id, json);
            json.put("r",true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 上传模板文件
     * 
     * @param paramLong
     * @param paramStr
     * @param paramStr2
     * @return
     * @throws Exception
     */
    private JSONObject saveTemplate(long id, String filename, String templateContent) throws Exception {
        JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            service.saveTemplate(con, id, filename, templateContent);
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
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
        ObjectReportRecord record = null;
        try {
            con = DBHELPER.getConnection();
            final long id = getParamLong("id", -1L);
            record = service.getRecord(con, id);
            if (record == null) {
                record = service.newRecord();
                record.setId(getParamLong("id"));
            }
            record.setParams(getParamStr("params"));
            record.setContent(getParamStr("content"));
            record.setTemplate(getParamBase64Bytes("template"));
            service.saveRecord(con, record);
            json = record.getJSON();
            json.put("r", true);
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
        final ObjectReportRecord record = service.getRecord(null, id);
        if (record != null) {
            json = record.getJSON();
            json.put("r", true);
        } else {
            json = new JSONObject();
            json.put("r", true);
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
        return json;
    }

}
