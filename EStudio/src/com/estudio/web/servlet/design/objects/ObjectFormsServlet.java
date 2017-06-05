package com.estudio.web.servlet.design.objects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.objects.ObjectFormsRecord;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectFormService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class ObjectFormsServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IObjectFormService service = RuntimeContext.getObjectFormService();

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(getParamLong("id", Long.MIN_VALUE), getParamLong("version", -1L)));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecord(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.endsWith("exchange", operation))
            response.getWriter().println(exchangeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.endsWith("moveto", operation))
            response.getWriter().println(moveTo(getParamStr("from"), getParamLong("to", -1L)));
        else if (StringUtils.equals("getControls", operation))
            response.getWriter().println(getFormControls(getParamLong("id"), getParamLong("version")));
        else if (StringUtils.equals("getDatasourceCacheKeys", operation))
            response.getWriter().println(service.getDataSourceCacheKeyList(null));
        else if (StringUtils.equals("saveDatasourceCacheKey", operation))
            response.getWriter().println(service.saveDataSourceCacheKey(null, getParamStr("keyname")));
        else if (StringUtils.equals("deleteDatasourceCacheKey", operation))
            response.getWriter().println(service.deleteDataSourceCacheKey(null, getParamStr("keyname")));
        else if (StringUtils.equals("getDatasourceTemplateList", operation))
            response.getWriter().println(service.getDataSourceTemplateList(null));
        else if (StringUtils.equals("saveDatasourceTemplate", operation))
            response.getWriter().println(service.saveDataSourceTemplate(null, getParamStr("category"), getParamStr("caption"), getParamStr("content")));
        else if (StringUtils.equals("deleteDatasourceTemplate", operation))
            response.getWriter().println(service.deleteDataSourceTemplate(null, getParamStr("category"), getParamStr("caption")));
        else if (StringUtils.equals("getDatasourceTemplateContent", operation))
            response.getWriter().println(service.getDataSourceTemplate(null, getParamStr("category"), getParamStr("caption")));

    }

    /**
     * 获取表单或控件定义 WorkFlow
     * 
     * @param id
     * @param version
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONObject getFormControls(final long id, final long version) throws Exception {
        Connection con = null;
        JSONObject result = null;
        try {
            con = DBHELPER.getConnection();
            result = service.getFormControls(con, id, version);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 列表数据
     * 
     * @param try2Int
     * @return
     */
    private JSONObject listRecords(final long id) throws Exception {
        final JSONObject json = new JSONObject();
        final ArrayList<ObjectFormsRecord> records = service.getRecords(null, id);
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
     * @throws JSONException
     */
    private JSONObject saveRecord(final HttpServletRequest request) throws Exception {
        JSONObject json = null;
        Connection con = null;
        ObjectFormsRecord record = null;
        try {
            con = DBHELPER.getConnection();
            final long id = getParamLong("id", -1);
            record = service.getRecord(con, id);
            if (record == null) {
                record = service.newRecord();
                record.setId(id);
            }
            record.setDfmstream(getParamBase64Bytes("dfmstream"));
            record.setXmlstream(getParamBase64Bytes("xmlstream"));
            record.setDatasource(getParamBase64Bytes("datasource"));
            record.setJsscript(getParamBase64Bytes("jsscript"));
            record.setType(getParamLong("type", 0L));
            record.setFormParams(getParamStr("form_params"));
            service.saveRecord(con, record);
            service.saveRecord4WorkFlow(con, record.getId(), getParamBase64Bytes("ds4wf"), getParamBase64Bytes("cs4wf"));

            json = new JSONObject();
            json.put("version", service.getFormVersion(con, id));
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
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONObject getRecordInfo(final long id, final long version) throws Exception {
        return service.getRecordInfo(id, version);
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
        } catch (final Exception e) {
            json.put("r", false);
            json.put("msg", ExceptionUtils.loggerException(e));
        }
        return json;
    }
}
