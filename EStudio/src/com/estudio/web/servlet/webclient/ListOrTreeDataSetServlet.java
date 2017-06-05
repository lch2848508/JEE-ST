package com.estudio.web.servlet.webclient;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.service.DataService4Lookup;
import com.estudio.web.servlet.BaseServlet;

public class ListOrTreeDataSetServlet extends BaseServlet {

    private static final long serialVersionUID = 7796203848531808268L;
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("list", operation))
            response.getWriter().println(getListDataSetDefine(getParams()));
        else if (StringUtils.equals("tree", operation))
            response.getWriter().println(getTreeDataSetDefine());
        else if (StringUtils.equals("getDataSetGridDatas", operation))
            response.getWriter().println(getDataSetGridDatas(request));
        else if (StringUtils.equals("getTemplate", operation))
            response.getWriter().println(getTemplate(getParamLong("portalId"), getParamStr("datasetName"), loginInfo.get().getId()));
        else if (StringUtils.equals("getComboboxItems", operation))
            response.getWriter().println(getComboboxItems(getParamLong("portalId"), getParamStr("dataset"), getParamStr("paramName"), getParams()));
        else if (StringUtils.equals("deleteTemplate", operation))
            response.getWriter().println(delTemplate(getParamLong("id")));
        else if (StringUtils.equals("saveTemplate", operation))
            response.getWriter().println(saveTemplate(getParamLong("portalId"), getParamStr("datasetName"), loginInfo.get().getId(), getParamStr("caption"), getParamStr("values"), getParamInt("isShare")));
    }

    /**
     * 動態獲取下拉列表過濾控件的值
     * 
     * @param portalId
     * @param datasetName
     * @param filterParamName
     * @param params
     * @return
     * @throws Exception
     */
    private JSONObject getComboboxItems(long portalId, String datasetName, String filterParamName, Map<String, String> params) throws Exception {
        return DataService4Lookup.getInstance().getComboboxItems(portalId, datasetName, filterParamName, params);
    }

    /**
     * 保存模版
     * 
     * @param portalId
     * @param datasetName
     * @param userId
     * @param values
     * @param isShare
     * @return
     * @throws Exception
     */
    private JSONObject saveTemplate(long portalId, String datasetName, long userId, String caption, String values, int isShare) throws Exception {
        return DataService4Lookup.getInstance().saveTemplate(portalId, datasetName, userId, caption, values, isShare);
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    private JSONObject delTemplate(long id) throws Exception {
        return DataService4Lookup.getInstance().delTemplate(id);
    }

    /**
     * 
     * @param portalId
     * @param datasetName
     * @param userId
     * @return
     * @throws Exception
     */
    private JSONObject getTemplate(long portalId, String datasetName, long userId) throws Exception {
        return DataService4Lookup.getInstance().getTemplate(portalId, datasetName, userId);
    }

    /**
     * @param request
     * @return
     * @throws Exception
     * @throws JSONException
     * @throws DocumentException
     */
    private JSONObject getDataSetGridDatas(final HttpServletRequest request) throws Exception, DocumentException {
        Connection con = null;
        final JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            final String datasetName = request.getParameter("dataset");
            final int portal_id = Convert.try2Int(request.getParameter("portal_id"), -1);
            json.put("records", DataService4Lookup.getInstance().getGridJSON4Flex(con, portal_id, datasetName, getParams()));
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 
     * @return
     */
    private JSONObject getTreeDataSetDefine() {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            final String labelField = getParamStr("labelField");
            final String groupField = getParamStr("groupField");
            con = DBHELPER.getConnection();
            final String datasetName = getParamStr("dataset");
            final int portal_id = getParamInt("portal_id", -1);
            // boolean isMultiSelected =
            // Convert.str2Boolean(getParamStr("multiselect"));
            if (StringUtils.isEmpty(labelField) && StringUtils.isEmpty(groupField))
                JSONUtils.append(json, "rows", DataService4Lookup.getInstance().getTreeJSON(con, portal_id, datasetName, getParams()));
            else {
                final Map<String, String> params = getParams();
                params.put("pageable", "False");
                final JSONArray records = DataService4Lookup.getInstance().getGridJSON4Flex(con, portal_id, datasetName, getParams()).getJSONArray("rows");
                json.put("rows", flatRecordToTreeRecord(records, labelField, groupField));
            }
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            ExceptionUtils.printExceptionTrace(e);
            json.put("msg", e.getMessage());
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 平面数据转为树状数据
     * 
     * @param records
     * @param labelField
     * @param groupField
     * @return
     */
    private JSONArray flatRecordToTreeRecord(final JSONArray records, final String labelField, final String groupField) {
        final JSONArray result = new JSONArray();
        if (records != null) {
            final Map<String, JSONObject> maps = new HashMap<String, JSONObject>();
            for (int i = 0; i < records.size(); i++) {
                final JSONObject record = records.getJSONObject(i);
                record.put("label", record.getString(labelField));
                String groupKey = record.getString(groupField);
                if (StringUtils.isEmpty(groupKey))
                    groupKey = "无分组";
                JSONObject groupJson = maps.get(groupKey);
                if (groupJson == null) {
                    groupJson = new JSONObject();
                    groupJson.put("label", groupKey);
                    maps.put(groupKey, groupJson);
                    result.add(groupJson);
                }
                JSONUtils.append(groupJson, "children", record);
            }
        }
        return result;
    }

    /**
     * @param params
     * @param request
     * @return
     */
    private JSONObject getListDataSetDefine(Map<String, String> params) {
        Connection con = null;
        final JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            final String datasetName = getParamStr("dataset");
            final int portal_id = getParamInt("portal_id", -1);// Convert.try2Int(request.getParameter("portal_id"),
            json.put("define", DataService4Lookup.getInstance().dataset2GridJSON(con, portal_id, datasetName, false, params));
            json.put("records", DataService4Lookup.getInstance().getGridJSON4Flex(con, portal_id, datasetName, params));
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            ExceptionUtils.printExceptionTrace(e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

}
