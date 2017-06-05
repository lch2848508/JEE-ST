package com.estudio.web.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.db.DBException;
import com.estudio.define.db.SQLParam;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.define.webclient.SQLParamUtils;
import com.estudio.define.webclient.form.DSField;
import com.estudio.define.webclient.form.DesignDataSource;
import com.estudio.define.webclient.form.DesignDataSourceCommand;
import com.estudio.define.webclient.portal.AbstractPortalGridDefine;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.define.webclient.portal.SQLDefineBase;
import com.estudio.define.webclient.portal.SQLField;
import com.estudio.impl.webclient.form.DataSetCacheService4WebClient;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

public final class DataService4Lookup {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    // private HashMap<String, JSONObject> datasetName2JSON = new
    // HashMap<String, JSONObject>();

    private static final DataService4Lookup INSTANCE = new DataService4Lookup();

    public static DataService4Lookup getInstance() {
        return INSTANCE;
    }

    private DataService4Lookup() {

    }

    /**
     * 根据DataSet构建一个Grid
     * 
     * @param datasetName
     * @return
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public JSONObject dataset2GridJSON(final Connection con, final long portal_id, final String datasetName, final boolean isMultiSelected, Map<String, String> params) throws DocumentException, Exception {
        return portal_id == -1 ? getFormDataSetJSON(con, portal_id, datasetName, isMultiSelected, params) : getPortalDataSetJSON(con, portal_id, datasetName, isMultiSelected, params);
    }

    /**
     * 清除已经定义好的缓存(portal)
     * 
     * @param portalID
     */
    public void deleteDataSetJSON(final long portalID) {
        SystemCacheManager.getInstance().removeDesignObjectByPrefix(getKeyPrefix(portalID));
    }

    /**
     * 清除已经定义好的缓存
     * 
     * @param datasetName
     */
    public void deleteDataSetJSON(final long portalId, final String datasetName) {
        SystemCacheManager.getInstance().removeDesignObject(getKey(portalId, datasetName));
    }

    /**
     * 生成目录树
     * 
     * @param cmd
     * @param labelField
     * @param sqlDefine
     * @param string
     * @param nodesArray
     * @param json
     * @param httpParams
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private void generalDatasetTreeJSON(final IDBCommand cmd, List<SQLParam> sqlParamList, final JSONObject parentJson, final String keyFieldName, final String labelField, final String iconURL, final Map<String, String> httpParams) throws Exception {
        JSONArray array = null;
        for (SQLParam param : sqlParamList) {
            String paramValue = httpParams.get(param.getName());
            SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
        }
        cmd.executeQuery();
        final HashMap<String, JSONObject> id2JSON = new HashMap<String, JSONObject>();
        String iconField = httpParams.get("ICON_FIELD_NAME");
        String checkEnabledField = httpParams.get("CHECK_ENABLED_FIELD_NAME");
        boolean isCheckEnabledFieldEmpty = StringUtils.isEmpty(checkEnabledField);
        boolean isIconFieldEmpty = StringUtils.isEmpty(iconField);
        while (cmd.next()) {
            if (array == null) {
                array = new JSONArray();
                parentJson.put("children", array);
            }
            final JSONObject itemJSON = new JSONObject();
            itemJSON.put("key", cmd.getString(keyFieldName));
            itemJSON.put("label", cmd.getString(labelField));
            itemJSON.put("checkEnabled", isCheckEnabledFieldEmpty ? 1 : cmd.getInt(checkEnabledField));
            for (int j = 1; j <= cmd.getMetaData().getColumnCount(); j++) {
                itemJSON.put(cmd.getMetaData().getColumnLabel(j).toUpperCase(), cmd.getString(j));
            }
            itemJSON.put("iconURL", isIconFieldEmpty ? iconURL : cmd.getString(iconField));
            id2JSON.put(cmd.getString(keyFieldName), itemJSON);
            array.add(itemJSON);
        }
        final Iterator<Map.Entry<String, JSONObject>> iterator = id2JSON.entrySet().iterator();

        String primaryParamName = httpParams.get("PRIMARY_PARAM_NAME");
        if (StringUtils.isEmpty(primaryParamName) && sqlParamList.size() != 0)
            primaryParamName = sqlParamList.get(0).getName();

        while (iterator.hasNext()) {
            final Map.Entry<String, JSONObject> entry = iterator.next();
            httpParams.put(primaryParamName, entry.getValue().getString("key"));
            generalDatasetTreeJSON(cmd, sqlParamList, entry.getValue(), keyFieldName, labelField, iconURL, httpParams);
        }
    }

    /**
     * 得到Form中的DataSet选择Grid定义
     * 
     * @param con
     * @param datasetName
     * @param isMultiSelected
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws DBException
     */
    private JSONObject getFormDataSetJSON(final Connection con, final long portalId, final String datasetName, final boolean isMultiSelected, Map<String, String> params) throws Exception {
        final String cacheKey = getKey(portalId, datasetName);
        JSONObject json = (JSONObject) SystemCacheManager.getInstance().getDesignObject(cacheKey);
        if (json == null) {
            json = new JSONObject();
            final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
            if (ds != null) {
                ds.getSelect().setKeyFieldName(ds.getPrimaryField().getName());
                ds.getSelect().initPageAndCountCmd();
                if (ds != null) {
                    for (int i = 0; i < ds.getFieldCount(); i++) {
                        final DSField field = ds.getField(i);
                        if (!field.isPrimary()) {
                            final JSONObject columnJson = new JSONObject();
                            columnJson.put("Field", field.getName());
                            columnJson.put("Caption", field.getLabel());
                            columnJson.put("Width", field.getColumnWidth());
                            columnJson.put("IsGroup", field.isGroup());
                            columnJson.put("IsSplit", field.isSplit());
                            JSONUtils.append(json, "Columns", columnJson);
                            // json.append("Columns", columnJson);
                        }
                    }

                    List<String> invalidParamList = new ArrayList<String>();
                    List<SQLParam> validParamList = new ArrayList<SQLParam>();
                    for (int i = 0; i < ds.getSelect().getParamCount(); i++) {
                        final SQLParam4Form param = ds.getSelect().getParam(i);
                        if (!params.containsKey(param.getName()))
                            invalidParamList.add(param.getName());
                        else
                            validParamList.add(param);
                    }

                    for (int i = 0; i < ds.getSelect().getParamCount(); i++) {
                        final SQLParam4Form param = ds.getSelect().getParam(i);
                        final JSONObject paramJSON = new JSONObject();
                        String controlType = param.getControlType();
                        if (StringUtils.isEmpty(controlType))
                            continue;
                        paramJSON.put("Control", controlType);
                        paramJSON.put("Name", param.getName());
                        paramJSON.put("Label", param.getLabel());
                        paramJSON.put("Datatype", 0);
                        paramJSON.put("controlWidth", param.getControlWidth());
                        paramJSON.put("controlPos", param.getControlPos());
                        JSONUtils.append(json, "Params", paramJSON);

                        // 从数据库中读值
                        if (StringUtils.equals("ComboBox", controlType)) {
                            String listSQL = param.getControlAddition();
                            boolean isRefParent = StringUtils.contains(listSQL, "PARENT_COMBOBOX");
                            if (isRefParent)
                                paramJSON.put("isRefParent", true);
                            else
                                paramJSON.put("items", getComboboxItems(con, listSQL, validParamList, invalidParamList, params));
                        }
                    }
                }
            }
            SystemCacheManager.getInstance().putDesignObject(cacheKey, json);
        }
        return json;
    }

    /**
     * 
     * @param portalId
     * @param datasetName
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject getComboboxItems(long portalId, String datasetName, String filterParamName, Map<String, String> params) throws Exception {
        return portalId == -1 ? getFormDatasetFilterComboboxItems(datasetName, filterParamName, params) : getPortalDatasetFilterComboboxItems(portalId, datasetName, filterParamName, params);
    }

    /**
     * 
     * @param datasetName
     * @param params
     * @return
     * @throws Exception
     */
    private JSONObject getPortalDatasetFilterComboboxItems(long portalId, String datasetName, String filterParamName, Map<String, String> params) throws Exception {
        final String key = getKey(portalId, datasetName);
        JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final SQLDefineBase sqlDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con).getSQLDefineByName(datasetName);
            String listSQL = sqlDefine.getParam(filterParamName).getAddition();
            if (!StringUtils.isEmpty(listSQL)) {
                json.put("items", getComboboxItems(con, listSQL, params));
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 
     * @param datasetName
     * @param params
     * @return
     * @throws Exception
     */
    private JSONObject getFormDatasetFilterComboboxItems(String datasetName, String filterParamName, Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        Connection con = null;
        final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
        try {
            con = DBHELPER.getConnection();
            String listSQL = "";
            for (int i = 0; i < ds.getSelect().getParamCount(); i++) {
                SQLParam4Form param = ds.getSelect().getParam(i);
                if (StringUtils.equals(param.getName(), filterParamName)) {
                    listSQL = param.getControlAddition();
                    break;
                }
            }
            if (!StringUtils.isEmpty(listSQL)) {
                json.put("items", getComboboxItems(con, listSQL, params));
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取ComboBox值
     * 
     * @param listSQL
     * @param validParamList
     * @param invalidParamList
     * @param httpParams
     * 
     * @return
     * @throws Exception
     */
    private JSONArray getComboboxItems(Connection con, String listSQL, List<SQLParam> validParamList, List<String> invalidParamList, Map<String, String> httpParams) throws Exception {
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("LABEL", "");
        item.put("ID", "");
        items.add(item);
        IDBCommand cmd = null;
        try {
            String sql = DBHELPER.getSQLTrans().removeSQLParams(listSQL, invalidParamList);
            cmd = DBHELPER.getCommand(con, sql);
            for (SQLParam param : validParamList)
                SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), httpParams.get(param.getName()));
            cmd.executeQuery();
            while (cmd.next()) {
                item = new JSONObject();
                item.put("LABEL", cmd.getString("label"));
                item.put("ID", cmd.getString("id"));
                items.add(item);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return items;
    }

    private JSONArray getComboboxItems(Connection con, String sql, Map<String, String> params) throws Exception {
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("LABEL", "");
        item.put("ID", "");
        items.add(item);
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, sql);
            for (String paramName : cmd.getParams())
                cmd.setParam(paramName, params.get(paramName.toUpperCase()));
            cmd.executeQuery();
            while (cmd.next()) {
                item = new JSONObject();
                item.put("LABEL", cmd.getString("label"));
                item.put("ID", cmd.getString("id"));
                items.add(item);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return items;
    }

    /**
     * 得到表单中的DataSet数据
     * 
     * @param con
     * @param datasetName
     * @param httpRequestParams
     * @param isMultiSelect
     * @param isPageSupport
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONObject getFormLoopupDatasJSON(final Connection con, final String datasetName, final Map<String, String> httpRequestParams, final boolean isPageSupport, final boolean isMultiSelect) throws Exception {

        final JSONObject json = new JSONObject();
        json.put("r", false);

        Connection tempCon = con;
        IDBCommand rcmd = null;
        IDBCommand pcmd = null;
        IDBCommand idCmd = null;
        IDBCommand clearCmd = null;

        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
            if (ds == null || ds.getSelect() == null)
                throw new Exception("获取数据:" + datasetName + "异常.");

            List<String> invalidParamList = new ArrayList<String>();
            List<SQLParam4Form> validParamList = new ArrayList<SQLParam4Form>();
            for (int i = 0; i < ds.getSelect().getParamCount(); i++) {
                final SQLParam4Form param = ds.getSelect().getParam(i);
                final String paramValue = httpRequestParams.get(param.getName());
                if (StringUtils.isEmpty(paramValue))
                    invalidParamList.add(param.getName());
                else
                    validParamList.add(param);
            }

            String[] pageSQLArray = ds.getSelectSQLWithRemoveParams(invalidParamList);// DBHELPER.getSQLTrans().removeSQLParams(ds.getSelect().getSql(),
                                                                                      // invalidParamList);
            pcmd = DBHELPER.getCommand(tempCon, pageSQLArray[0]);
            rcmd = DBHELPER.getCommand(tempCon, pageSQLArray[1]);
            for (int i = 0; i < validParamList.size(); i++) {
                final SQLParam4Form param = validParamList.get(i);
                final String paramValue = getParamValueFormHttpRequest(httpRequestParams, param);
                SQLParamUtils.setParam(pcmd, param.getDataType(), param.getName(), paramValue);
                SQLParamUtils.setParam(rcmd, param.getDataType(), param.getName(), paramValue);
            }
            rcmd.executeQuery();
            rcmd.next();
            int recordPerPage = 200;
            int recordTotal = rcmd.getInt(1);
            int page = Convert.try2Int(httpRequestParams.get("p"), 1);
            int totalPage = (recordTotal % recordPerPage) == 0 ? recordTotal / recordPerPage : (recordTotal / recordPerPage) + 1;
            page = Math.max(1, Math.min(page, totalPage));
            long threadId = Thread.currentThread().getId();
            String sessionId = httpRequestParams.get("SESSIONID");

            boolean isSupportPageOptimize = ds.isSupportPageOptimize();
            if (isSupportPageOptimize) {
                idCmd = DBHELPER.getCommand(tempCon, pageSQLArray[2]);
                for (int i = 0; i < validParamList.size(); i++) {
                    final SQLParam4Form param = validParamList.get(i);
                    final String paramValue = getParamValueFormHttpRequest(httpRequestParams, param);
                    SQLParamUtils.setParam(idCmd, param.getDataType(), param.getName(), paramValue);
                }
                idCmd.setParam("thread_id", threadId);
                idCmd.setParam("session_id", sessionId);
                idCmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                idCmd.setParam("E", new Long(recordPerPage * page));
                idCmd.setParam("R", recordPerPage);
                idCmd.execute();

                pcmd.setParam("thread_id", threadId);
                pcmd.setParam("session_id", sessionId);

                clearCmd = DBHELPER.getCommand(tempCon, "delete from sys_utils_page_optimize where thread_id=? and session_id=?");
                clearCmd.setParam(1, threadId);
                clearCmd.setParam(2, sessionId);
            } else {
                pcmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                pcmd.setParam("E", new Long(recordPerPage * page));
                pcmd.setParam("R", recordPerPage);
            }

            final ArrayList<JSONObject> rowsList = new ArrayList<JSONObject>();

            pcmd.executeQuery();
            while (pcmd.next())
                rowsList.add(DBHELPER.cmdRecord2Json(pcmd));

            // 清理ID數據
            if (clearCmd != null)
                clearCmd.execute();

            json.put("rows", rowsList);
            json.put("tp", totalPage);
            json.put("cp", page);
            json.put("tr", recordTotal);
            json.put("r", true);

        } finally {
            DBHELPER.closeCommand(rcmd);
            DBHELPER.closeCommand(pcmd);
            DBHELPER.closeCommand(idCmd);
            DBHELPER.closeCommand(clearCmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 获取参数值
     * 
     * @param httpRequestParams
     * @param param
     * @return
     */
    private String getParamValueFormHttpRequest(final Map<String, String> httpRequestParams, final SQLParam4Form param) {
        String paramValue = httpRequestParams.get(param.getName());
        // todo: 应该添加是否模糊匹配功能
        if (param.getDataType() == DBParamDataType.String)
            if (param.isPartMatch() && !StringUtils.equals(param.getControlType(), "ComboBox"))
                paramValue = StringUtils.isEmpty(paramValue) ? "%" : "%" + paramValue + "%";
        return paramValue;
    }

    /**
     * 取得Form中定义的TreeDataSet数据
     * 
     * @param con
     * @param datasetName
     * @param httpRequestParams
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONObject getFormLTreeDatasJSON(final Connection con, final String datasetName, final Map<String, String> httpRequestParams) throws Exception {
        final JSONObject json = new JSONObject();
        IDBCommand cmd = null;
        try {
            final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
            final DesignDataSourceCommand dscmd = ds.getSelect();
            cmd = dscmd.getCmd(con);
            String paramValue = "";
            String paramName = "";
            if (dscmd.getParamCount() != 0) {
                paramName = dscmd.getParam(0).getName();
                paramValue = httpRequestParams.get(paramName);
            }
            String keyField = "";
            String captionField = httpRequestParams.get("LABEL_FIELD_NAME");
            for (int i = 0; i < ds.getFieldCount(); i++) {
                DSField field = ds.getField(i);
                if (field.isPrimary())
                    keyField = field.getName();
                else if (StringUtils.isEmpty(captionField))
                    captionField = field.getName();
            }
            String rootIconURL = httpRequestParams.get("ROOT_ICON_URL");
            if (StringUtils.isEmpty(rootIconURL))
                rootIconURL = "../images/18x18/computer.png";

            String iconURL = httpRequestParams.get("ICON_URL");
            if (StringUtils.isEmpty(iconURL))
                iconURL = "../images/18x18/folderClose.png";

            List<SQLParam> sqlParamList = new ArrayList<SQLParam>();
            for (int i = 0; i < dscmd.getParamCount(); i++)
                sqlParamList.add(dscmd.getParam(i));
            generalDatasetTreeJSON(cmd, sqlParamList, json, keyField, captionField, iconURL, httpRequestParams);
            json.put("key", paramValue);
            json.put("label", ds.getLabel());
            json.put("iconURL", rootIconURL);
            json.put("root", true);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    /**
     * 得到数据列表JSON
     * 
     * @param con
     * @param datasetName
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject getGridJSON4Flex(final Connection con, final long portal_id, final String datasetName, final Map<String, String> httpRequestParams) throws Exception, DocumentException {
        final boolean isPageSupport = Convert.str2Boolean(httpRequestParams.get("pageable"), true);
        final boolean isMultiSelect = Convert.str2Boolean(httpRequestParams.get("multiselect"), false);
        return portal_id == -1 ? getFormLoopupDatasJSON(con, datasetName, httpRequestParams, isPageSupport, isMultiSelect) : getPortalLookupDatasJSON(con, portal_id, datasetName, httpRequestParams, isPageSupport, isMultiSelect);
    }

    private String getKey(final long portalId, final String datasetName) {
        return "datasetLookJson-" + portalId + datasetName;
    }

    private String getKeyPrefix(final long portalId) {
        return "datasetLookJson-" + portalId;
    }

    /**
     * 得到Portal中的DataSe选择Grid定义
     * 
     * @param con
     * @param portalId
     * @param datasetName
     * @param isMultiSelected
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws DBException
     */
    private JSONObject getPortalDataSetJSON(final Connection con, final long portalId, final String datasetName, final boolean isMultiSelected, Map<String, String> params) throws Exception {
        final String key = getKey(portalId, datasetName);
        JSONObject json = (JSONObject) SystemCacheManager.getInstance().getDesignObject(key);
        if (json == null) {
            json = new JSONObject();
            final SQLDefineBase sqlDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con).getSQLDefineByName(datasetName);
            // 字段
            if (isMultiSelected) {
                final JSONObject columnJson = new JSONObject();
                columnJson.put("Field", "selected");
                columnJson.put("Caption", "#master_checkbox");
                columnJson.put("Width", 30);
                columnJson.put("IsGroup", false);
                columnJson.put("IsSplit", false);
                JSONUtils.append(json, "Columns", columnJson);
                // json.append("Columns", columnJson);
            }

            for (int i = 0; i < sqlDefine.getFieldCount(); i++) {
                final SQLField field = sqlDefine.getField(i);
                if (!field.isKey() && field.isVisible()) {
                    final JSONObject columnJson = new JSONObject();
                    columnJson.put("Field", field.getFieldName());
                    columnJson.put("Caption", field.getFieldLabel());
                    columnJson.put("Width", field.getExtProp());
                    columnJson.put("IsGroup", false);
                    columnJson.put("IsSplit", false);
                    JSONUtils.append(json, "Columns", columnJson);
                    // json.append("Columns", columnJson);
                }
            }

            List<SQLParam> validParamList = new ArrayList<SQLParam>();
            List<String> invalidParamList = new ArrayList<String>();
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal sqlParam = sqlDefine.getParam(i);
                String paramValue = params.get(sqlParam.getName());
                if (StringUtils.isEmpty(paramValue))
                    invalidParamList.add(sqlParam.getName());
                else
                    validParamList.add(sqlParam);
            }

            // 参数
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal sqlParam = sqlDefine.getParam(i);
                if (sqlParam.isFilter()) {
                    final JSONObject paramJSON = new JSONObject();
                    String controlType = sqlParam.getFilterControl();
                    paramJSON.put("Control", controlType);
                    paramJSON.put("Name", sqlParam.getName());
                    paramJSON.put("Label", sqlParam.getLabel());
                    paramJSON.put("Datatype", sqlParam.getDataType());
                    paramJSON.put("controlWidth", sqlParam.getControlWidth());
                    paramJSON.put("controlPos", sqlParam.getPos());
                    if (StringUtils.equals("ComboBox", controlType)) {
                        String listSQL = sqlParam.getAddition();
                        boolean isRefParent = StringUtils.contains(listSQL, "PARENT_COMBOBOX");
                        if (isRefParent)
                            paramJSON.put("isRefParent", true);
                        else
                            paramJSON.put("items", getComboboxItems(con, listSQL, validParamList, invalidParamList, params));
                    }
                    JSONUtils.append(json, "Params", paramJSON);
                }
            }
            SystemCacheManager.getInstance().putDesignObject(key, json);
        }
        return json;
    }

    /**
     * 得到Portal中定义的DataSet数据
     * 
     * @param con
     * @param datasetName
     * @param httpRequestParams
     * @param isMultiSelect
     * @param isPageSupport
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    private JSONObject getPortalLookupDatasJSON(final Connection con, final long portal_id, final String datasetName, final Map<String, String> httpRequestParams, final boolean isPageSupport, final boolean isMultiSelect) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        IDBCommand rcmd = null;
        IDBCommand pcmd = null;
        IDBCommand idCmd = null;
        IDBCommand clearCmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final SQLDefineBase sqlDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, tempCon).getSQLDefineByName(datasetName);

            if (sqlDefine == null)
                throw new Exception("获取数据异常，数据源不存在。");

            rcmd = sqlDefine.getRCCmd(tempCon, httpRequestParams, httpRequestParams);
            pcmd = sqlDefine.getPCmd(tempCon, httpRequestParams, httpRequestParams);

            setSQLDefineParams(httpRequestParams, rcmd, pcmd, idCmd, sqlDefine);
            rcmd.executeQuery();
            rcmd.next();
            int recordTotal = rcmd.getInt(1);
            int recordPerPage = 200;
            int page = Convert.try2Int(httpRequestParams.get("p"), 1);
            int totalPage = (recordTotal % recordPerPage) == 0 ? recordTotal / recordPerPage : (recordTotal / recordPerPage) + 1;
            page = Math.max(1, Math.min(page, totalPage));

            long threadId = Thread.currentThread().getId();
            String sessionId = httpRequestParams.get("SESSIONID");
            boolean isSupportPageOptimize = sqlDefine.isSupportPageOptimize();

            if (isSupportPageOptimize) {
                idCmd = sqlDefine.getPageIdsCmd(tempCon, httpRequestParams);
                clearCmd = DBHELPER.getCommand(tempCon, "delete from sys_utils_page_optimize where thread_id=? and session_id=?");
                clearCmd.setParam(1, threadId);
                clearCmd.setParam(2, sessionId);

                setSQLDefineParams(httpRequestParams, null, null, idCmd, sqlDefine);
                idCmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                idCmd.setParam("E", new Long(page * recordPerPage));
                idCmd.setParam("R", recordPerPage);
                idCmd.setParam("thread_id", threadId);
                idCmd.setParam("session_id", sessionId);
                idCmd.execute();

                pcmd.setParam("thread_id", threadId);
                pcmd.setParam("session_id", sessionId);
            } else {
                pcmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                pcmd.setParam("E", new Long(page * recordPerPage));
                pcmd.setParam("R", recordPerPage);
            }

            final ArrayList<JSONObject> rowsList = new ArrayList<JSONObject>();
            final String keyFieldName = sqlDefine.getKeyFieldName();
            pcmd.executeQuery();
            while (pcmd.next()) {
                final JSONObject rowJSON = new JSONObject();
                final String rowid = pcmd.getString(keyFieldName);
                rowJSON.put("__key__", rowid);
                for (int i = 0; i < sqlDefine.getFieldCount(); i++) {
                    final SQLField field = sqlDefine.getField(i);
                    String value = ""; // String, Integer, Long, Float,
                    // Double, DateTime, Byte
                    switch (RuntimeContext.getDbEntryService().getDBFieldDataTypeByTypeString(field.getDataType()).getJavaType()) {
                    case String:
                    case Integer:
                    case Long:
                    case Float:
                    case Double:
                        value = pcmd.getString(field.getFieldName());
                        break;
                    case DateTime:
                        value = Convert.datetime2Str(pcmd.getDate(field.getFieldName()));
                        break;
                    case Bytes:
                        value = Convert.bytes2Str(pcmd.getBytes(field.getFieldName()));
                        break;
                    }
                    rowJSON.put(field.getFieldName(), value);
                }
                rowsList.add(rowJSON);
            }
            json.put("rows", rowsList);
            json.put("tp", totalPage);
            json.put("cp", page);
            json.put("tr", recordTotal);
            json.put("r", true);

            if (clearCmd != null)
                clearCmd.execute();

        } finally {
            DBHELPER.closeCommand(rcmd);
            DBHELPER.closeCommand(pcmd);
            DBHELPER.closeCommand(idCmd);
            DBHELPER.closeCommand(clearCmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 取得Portal中定义的TreeDataSet数据
     * 
     * @param con
     * @param portal_id
     * @param datasetName
     * @param httpRequestParams
     * @return
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    private JSONObject getPortalTreeDatasJSON(final Connection con, final long portal_id, final String datasetName, final Map<String, String> httpRequestParams) throws Exception, DocumentException {
        final JSONObject rootJson = new JSONObject();
        IDBCommand cmd = null;
        try {
            final AbstractPortalGridDefine portalGridDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
            final SQLDefine4Portal sqlDefine = (SQLDefine4Portal) portalGridDefine.getSQLDefineByName(datasetName);
            String paramName = "";
            String rootId = "";
            if (sqlDefine.getParamCount() != 0) {
                paramName = sqlDefine.getParam(0).getName();
                rootId = httpRequestParams.get(paramName);
            }
            cmd = sqlDefine.getCmd(con, httpRequestParams);
            String labelField = httpRequestParams.get("LABEL_FIELD_NAME");
            String keyFieldName = "";
            for (int i = 0; i < sqlDefine.getFieldCount(); i++) {
                final SQLField field = sqlDefine.getField(i);
                if (field.isKey())
                    keyFieldName = field.getFieldName();
                else if (field.isCaption() || StringUtils.isEmpty(labelField))
                    labelField = field.getFieldName();
            }
            String rootIconURL = httpRequestParams.get("ROOT_ICON_URL");
            if (StringUtils.isEmpty(rootIconURL))
                rootIconURL = "../images/18x18/root.png";
            String folderIconURL = httpRequestParams.get("ICON_URL");
            if (StringUtils.isEmpty(folderIconURL))
                folderIconURL = "../images/18x18/folder.png";

            if (httpRequestParams.containsKey("TREE_ROOT_ID"))
                rootId = httpRequestParams.get("TREE_ROOT_ID");

            rootJson.put("key", rootId);
            rootJson.put("label", "请选择");

            rootJson.put("iconURL", rootIconURL);
            rootJson.put("root", true);
            List<SQLParam> sqlParamList = new ArrayList<SQLParam>();
            for (int i = 0; i < sqlDefine.getParamCount(); i++)
                sqlParamList.add(sqlDefine.getParam(i));

            generalDatasetTreeJSON(cmd, sqlParamList, rootJson, keyFieldName, labelField, folderIconURL, httpRequestParams);

        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return rootJson;
    }

    /**
     * 得到数据列表JSON
     * 
     * @param con
     * @param datasetName
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject getTreeJSON(final Connection con, final long portal_id, final String datasetName, final Map<String, String> httpRequestParams) throws Exception, DocumentException {
        return portal_id == -1 ? getFormLTreeDatasJSON(con, datasetName, httpRequestParams) : getPortalTreeDatasJSON(con, portal_id, datasetName, httpRequestParams);
    }

    /**
     * @param httpRequestParams
     * @param rcmd
     * @param pcmd
     * @param sqlDefine
     * @throws SQLException
     *             , DBException
     */
    protected void setSQLDefineParams(final Map<String, String> httpRequestParams, final IDBCommand rcmd, final IDBCommand pcmd, final IDBCommand idCmd, final SQLDefineBase sqlDefine) throws Exception {
        for (int i = 0; i < sqlDefine.getParamCount(); i++) {
            final SQLParam4Portal param = sqlDefine.getParam(i);
            String paramValue = httpRequestParams.get(param.getName());
            if (param.getDataType() == DBParamDataType.String)
                if (param.isPartMatch() && !StringUtils.equals(param.getFilterControl(), "ComboBox"))
                    paramValue = StringUtils.isEmpty(paramValue) ? "%" : "%" + paramValue + "%";
            if (rcmd != null)
                SQLParamUtils.setParam(rcmd, param.getDataType(), param.getName(), paramValue);
            if (pcmd != null)
                SQLParamUtils.setParam(pcmd, param.getDataType(), param.getName(), paramValue);
            if (idCmd != null)
                SQLParamUtils.setParam(idCmd, param.getDataType(), param.getName(), paramValue);

        }
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
    public JSONObject saveTemplate(long portalId, String datasetName, long userId, String caption, String values, int isShare) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            long id = DBHELPER.getUniqueID(con);
            stmt = con.prepareStatement("insert into sys_predefine_template4user (id, category, key1, key2, template_values, user_id,is_share,template_caption) values (?, 'listortreedataset', ?, ?, ?, ?,?,?)");
            stmt.setLong(1, id);
            stmt.setString(2, Long.toString(portalId));
            stmt.setString(3, datasetName);
            stmt.setBytes(4, Convert.str2Bytes(values));
            stmt.setLong(5, userId);
            stmt.setInt(6, isShare);
            stmt.setString(7, caption);
            stmt.execute();
            json.put("id", id);
            json.put("r", true);
            // , new Object[] { id, Long.toString(portalId), datasetName,
            // , userId, isShare, caption
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 删除模版
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public JSONObject delTemplate(long id) throws Exception {
        Connection con = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            DBHELPER.execute("delete from sys_predefine_template4user where id=" + id, con);
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取模版
     * 
     * @param portalId
     * @param datasetName
     * @param userId
     * @return
     * @throws Exception
     */
    public JSONObject getTemplate(long portalId, String datasetName, long userId) throws Exception {
        Connection con = null;
        JSONObject json = new JSONObject();
        PreparedStatement stmt = null;
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select id,user_id,TEMPLATE_CAPTION,template_values from sys_predefine_template4user where category='listortreedataset' and (user_id=? or is_share=1) and key1=? and key2=?");
            stmt.setLong(1, userId);
            stmt.setString(2, Long.toString(portalId));
            stmt.setString(3, datasetName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("id", rs.getLong(1));
                record.put("userId", rs.getLong(2));
                record.put("caption", rs.getString(3));
                record.put("values", JSONUtils.parserJSONArray(Convert.bytes2Str(rs.getBytes(4))));
                JSONUtils.append(json, "records", record);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

}
