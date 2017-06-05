package com.estudio.web.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.db.DBException;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.define.webclient.SQLParamUtils;
import com.estudio.define.webclient.form.DesignDataSource;
import com.estudio.define.webclient.form.DesignDataSourceCommand;
import com.estudio.define.webclient.form.FormDefine;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.impl.webclient.form.DataSetCacheService4WebClient;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.SecurityUtils;
import com.estudio.workflow.engine.WFEngineer;
import com.estudio.workflow.web.WorkFlowUIDefineService;

public final class DataService4Form {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    /**
     * 判断表单中是否存在值
     * 
     * @param con
     * @param id
     * @param params
     * @return
     */
    public boolean hasFormData(final Connection con, final long formId, final Map<String, String> params) {
        return false;
    }

    /**
     * 执行数据集合
     * 
     * @param datasetName
     * @param sqlType
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONObject executeDataSet(final String datasetName, final long sqlType, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
            if (ds != null) {
                final DesignDataSourceCommand dsCMD = sqlType == 0 ? ds.getSelect() : sqlType == 1 ? ds.getInsert() : sqlType == 2 ? ds.getUpdate() : ds.getDelete();
                cmd = dsCMD.getCmd(con);
                for (int i = 0; i < dsCMD.getParamCount(); i++) {
                    final SQLParam4Form param = dsCMD.getParam(i);
                    String paramValue = params.get(dsCMD.getParam(i).getName());
                    if (StringUtils.isEmpty(paramValue) || StringUtils.equals(paramValue, "null"))
                        paramValue = "";
                    SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
                }
                cmd.execute();
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 执行数据集合
     * 
     * @param datasetName
     * @param sqlType
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONObject batchExecuteDataSet(final String datasetName, final long sqlType, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            con.setAutoCommit(false);
            final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
            if (ds != null) {
                final DesignDataSourceCommand dsCMD = sqlType == 0 ? ds.getSelect() : sqlType == 1 ? ds.getInsert() : sqlType == 2 ? ds.getUpdate() : ds.getDelete();
                cmd = dsCMD.getCmd(con);
                JSONArray paramArray = JSONUtils.parserJSONArray(params.get("params"));
                for (int i = 0; i < paramArray.size(); i++) {
                    JSONObject paramJson = paramArray.getJSONObject(i);
                    for (int j = 0; j < dsCMD.getParamCount(); j++) {
                        final SQLParam4Form param = dsCMD.getParam(j);
                        String paramValue = paramJson.getString(dsCMD.getParam(j).getName());
                        if (StringUtils.isEmpty(paramValue) || StringUtils.equals(paramValue, "null"))
                            paramValue = "";
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
                    }
                    cmd.execute();
                }
                con.commit();
                json.put("r", true);
            }
        } finally {
            if (con != null) {
                con.rollback();
                con.setAutoCommit(false);
            }
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 动态获取表单数据
     * 
     * @param con
     * @param datasetList
     * @param clientParamsJsonStr
     * @param httpParams
     * @return
     * @throws Exception
     */
    public JSONObject getDataSetRecords(final Connection con, final String[] datasetList, final String clientParamsJsonStr, final Map<String, String> httpParams, final String sessionId) throws Exception {
        final JSONObject result = new JSONObject();
        result.put("r", false);
        Connection tempCon = con;
        final JSONObject clientParamsJson = JSONUtils.parserJSONObject(clientParamsJsonStr);
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            for (final String element : datasetList) {
                final String datasetName = element;
                final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
                if (ds == null) {
                    result.put("msg", "表单项已经发生更改,请注销当前用户并重新登录系统！");
                    break;
                }
                final ArrayList<JSONObject> records = getDataSetRecord(tempCon, ds, clientParamsJson, httpParams, sessionId);
                result.put(datasetName, records);
                if (!clientParamsJson.containsKey(datasetName) && (records.size() != 0))
                    clientParamsJson.put(datasetName, records.get(0));
            }
            result.put("r", true);
        } finally {
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 读取数据
     * 
     * @param con
     * @param ds
     * @param datasetParam
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private ArrayList<JSONObject> getDataSetRecord(final Connection con, final DesignDataSource ds, final JSONObject datasetParam, final Map<String, String> httpRequest, final String sessionId) throws Exception {
        ArrayList<JSONObject> result = null;
        String cacheKey = "";
        Map<String, String> paramName2Value = null;

        // 从缓存中加载数据
        if (ds.isReadOnly() && (ds.getCacheLevel() != 0)) {
            paramName2Value = new HashMap<String, String>();
            cacheKey = calcDataSourceCacheKey(ds, datasetParam, httpRequest, sessionId, paramName2Value);
            result = (ArrayList<JSONObject>) SystemCacheManager.getInstance().getDataSourceResultSet(cacheKey);
        }

        if (result == null) {
            result = new ArrayList<JSONObject>();
            final DesignDataSourceCommand select = ds.getSelect();
            IDBCommand cmd = null;
            try {
                cmd = select.getCmd(con);

                // 参数赋值
                if (paramName2Value != null)
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramName2Value.get(param.getName()));
                    }
                else
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        String paramValue = "";
                        if (!StringUtils.isEmpty(param.getInitDS()))
                            paramValue = getParamValueByDataSetJSON(datasetParam, param.getInitDS(), param.getInitField());
                        else {
                            String initValue = param.getInitValue();
                            if (!StringUtils.isEmpty(initValue))
                                if (StringUtils.startsWith(initValue, "REQ.")) {
                                    initValue = StringUtils.substringAfter(initValue, "REQ.");
                                    paramValue = httpRequest.get(initValue);
                                }
                        }
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
                    }

                cmd.executeQuery();

                if (ds.isReadOnly() && ds.isAppendNullRecord())
                    result.add(DBHELPER.appendNullRecord2Json(cmd));

                while (cmd.next())
                    result.add(DBHELPER.cmdRecord2Json(cmd));

                // 缓存记录
                if (ds.isReadOnly() && (ds.getCacheLevel() != 0))
                    SystemCacheManager.getInstance().putDataSourceResultSet(cacheKey, result);
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        }
        return result;
    }

    /**
     * 获取参数值
     * 
     * @param datasetParam
     * @param initDS
     * @param initField
     * @return
     * @throws Exception
     */
    private String getParamValueByDataSetJSON(final JSONObject datasetParam, final String initDS, final String initField) throws Exception {
        String result = null;
        if (datasetParam.containsKey(initDS)) {
            final JSONObject temp = datasetParam.getJSONObject(initDS);
            if (temp.containsKey(initField))
                result = temp.getString(initField);
        }
        return result;
    }

    /**
     * 获取下拉列表项
     * 
     * @param con
     * @param firstKey
     * @param datasetNames
     * @param firstDataSetName
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONObject getDataSetRecord4ComboBox(final Connection con, final String firstKey, final String[] datasetNames, final String firstDataSetName, final String sessionId) throws Exception {
        final JSONObject result = new JSONObject();
        result.put("r", false);
        Connection tempCon = con;
        final Map<String, String> dataset2KeyValues = new HashMap<String, String>();
        dataset2KeyValues.put(firstDataSetName, firstKey);
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            for (final String datasetName : datasetNames) {
                final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
                if (ds == null) {
                    result.put("msg", "表单项已经发生更改,请注销当前用户并重新登录系统！");
                    break;
                }
                result.put(datasetName, getDataSetRecord4ComboBox(tempCon, ds, dataset2KeyValues, sessionId));
            }
            result.put("r", true);
        } finally {
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 获取记录集
     * 
     * @param con
     * @param ds
     * @param dataset2KeyValues
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private ArrayList<JSONObject> getDataSetRecord4ComboBox(final Connection con, final DesignDataSource ds, final Map<String, String> dataset2KeyValues, final String sessionId) throws Exception {
        ArrayList<JSONObject> result = null;
        Map<String, String> paramName2Value = null;

        // 从缓存中取数据
        String cacheKey = "";
        if (ds.isReadOnly() && (ds.getCacheLevel() != 0)) {
            paramName2Value = new HashMap<String, String>();
            cacheKey = calcDataSourceCacheKey(ds, dataset2KeyValues, sessionId, paramName2Value);
            result = (ArrayList<JSONObject>) SystemCacheManager.getInstance().getDataSourceResultSet(cacheKey);
        }

        if (result == null) {
            result = new ArrayList<JSONObject>();
            final DesignDataSourceCommand select = ds.getSelect();
            IDBCommand cmd = null;
            try {
                cmd = select.getCmd(con);

                // 参数赋值
                if (paramName2Value != null)
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramName2Value.get(param.getName()));
                    }
                else
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        final String paramValue = dataset2KeyValues.get(param.getInitDS());
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
                    }
                cmd.executeQuery();
                boolean isFirstRow = true;

                if (ds.isReadOnly() && ds.isAppendNullRecord())
                    result.add(DBHELPER.appendNullRecord2Json(cmd));

                while (cmd.next()) {
                    result.add(DBHELPER.cmdRecord2Json(cmd));
                    if (isFirstRow) {
                        dataset2KeyValues.put(ds.getName(), result.get(0).getString(ds.getPrimaryField().getName()));
                        isFirstRow = false;
                    }
                }

                // 缓存数据
                if (ds.isReadOnly() && (ds.getCacheLevel() != 0))
                    SystemCacheManager.getInstance().putDataSourceResultSet(cacheKey, result);
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        }

        return result;
    }

    /**
     * 保存数据
     * 
     * @param paramStr
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject saveData4Flex(final Map<String, String> params, final HttpSession session) throws Exception {
        final JSONObject json = new JSONObject();
        final String[] formids = params.get("formids").split(",");
        final List<Long> savedFormIDS = new ArrayList<Long>();
        final String jsonStr = params.get("datasetValues");
        final JSONObject datasetValues = strTry2Json(jsonStr);
        if (datasetValues == null) {
            json.put("r", false);
            json.put("msg", "解析JSON数据出现错误,JSONStr=" + jsonStr);
            return json;
        }

        Connection con = null;
        final IDBCommand cmd = null;

        try {
            // 工作流只读表单过滤
            final boolean isWorkFlowForm = StringUtils.equals(params.get("callfrom"), "workflow");

            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);

            String firstFormDatasetKeyValue = params.get("mainDatasetKeyValue");
            for (final String formid : formids) {
                final FormDefine form = RuntimeContext.getFormDefineService().getFormDefine(Convert.str2Long(formid), con);

                // 如果是工作流表单需要判断表单是否已经设置为只读
                if (isWorkFlowForm && WFEngineer.getInstance().isFormReadOnly(Convert.str2Long(params.get("processTypeId")), params.get("activityName"), form.getFormId()))
                    continue;

                for (int j = 0; j < form.getDataSetCount(); j++) {
                    final DesignDataSource dataset = form.getDataSet(j);
                    if (datasetValues.containsKey(dataset.getName())) {
                        saveDataset4Flex(con, dataset, datasetValues.getJSONObject(dataset.getName()), params);
                        if (savedFormIDS.indexOf(form.getFormId()) == -1)
                            savedFormIDS.add(form.getFormId());
                    }
                }
            }

            if (StringUtils.equals(params.get("callfrom"), "Grid")) {
                final long portalID = Convert.str2Long(params.get("portalid"));
                final long gridID = Convert.str2Long(params.get("uid"));
                final long treeNodeID = Convert.str2Long(params.get("treenodeid"));
                json.put("portalData", DataService4Portal.getInstance().getGridItemJSON4Flex(con, portalID, treeNodeID, params, gridID));
            } else if (StringUtils.equals(params.get("callfrom"), "Tree")) {
                final long portalID = Convert.str2Long(params.get("portalid"));
                final long treeID = Convert.str2Long(params.get("uid"));
                final long parentNodeID = Convert.str2Long(params.get("treenodeid"));
                json.put("portalData", DataService4Portal.getInstance().getTreeNodeJSON4Flex(con, portalID, parentNodeID, params, treeID));
            } else if (StringUtils.equals(params.get("callfrom"), "PortalGridEx")) {
                long portalId = Convert.str2Long(params.get("portalId"));
                json.put("portalData", DataService4PortalEx.getInstance().getControlItemRecord(con, portalId, params, firstFormDatasetKeyValue));
            } else if (StringUtils.equals(params.get("callfrom"), "FormGrid")) {
                final String gridDataSetName = params.get("griddatasetname");
                final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(gridDataSetName);
                json.put("datasetRecord", getFormDataSetRecord4FormGrid(con, ds, params, session));
            } else if (StringUtils.equals(params.get("callfrom"), "workflow")) { // 从工作流调用
                final String activityName = params.get("activityName");
                final String processTypeId = params.get("processTypeId");
                final String processId = params.get("WORKFLOW_PROCESS_ID");
                final String step_id = params.get("process_step_id");
                final String workflow_ui_id = params.get("workflow_ui_id");
                WFEngineer.getInstance().calcActivityVariables(processTypeId, activityName, processId, step_id, params, con); // 计算变量
                RuntimeContext.getWfStorage().flagActivityFormSaved(con, Convert.str2Long(processId), activityName, savedFormIDS, Convert.str2Long(params.get("USER_ID")));// 设置表单保存标记
                final SQLDefine4Portal sqlDefine = WorkFlowUIDefineService.getInstance().getUIDefine(Convert.str2Long(workflow_ui_id)).getSqlDefine();
                json.put("records", DataService4Portal.getInstance().getGridItemJSON4Flex(con, sqlDefine, params, Convert.str2Long(step_id)));
            }
            DBHELPER.commit(con);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            DBHELPER.rollback(con, false);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.endTransaction(con);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 解析数据
     * 
     * @param jsonStr
     * @return
     */
    private JSONObject strTry2Json(final String jsonStr) {
        JSONObject result = null;
        if (!StringUtils.isEmpty(jsonStr))
            try {
                result = JSONUtils.parserJSONObject(StringUtils.replaceEach(jsonStr, new String[] { ":NaN", ":\"NaN\"" }, new String[] { "\"\"", "\"\"" }));
            } catch (final Exception e) {
                ExceptionUtils.loggerException(e);
                ExceptionUtils.printExceptionTrace(e);
            }
        return result;
    }

    /**
     * 生成数据
     * 
     * @param con
     * @param ds
     * @param params
     * @param session
     * @return
     * @throws Exception
     */
    private JSONObject getFormDataSetRecord4FormGrid(final Connection con, final DesignDataSource ds, final Map<String, String> params, final HttpSession session) throws Exception {
        JSONObject json = null;
        IDBCommand cmd = null;
        try {
            cmd = ds.getSingleCmd(con);
            final DesignDataSourceCommand select = ds.getSelect();
            for (int i = 0; i < select.getParamCount(); i++) {
                final SQLParam4Form param = select.getParam(i);
                final String paramName = param.getName();
                final String paramValue = params.get(paramName);
                SQLParamUtils.setParam(cmd, param.getDataType(), paramName, paramValue);
            }
            final FormDefine formdefine = RuntimeContext.getFormDefineService().getFormDefine(Convert.str2Long(params.get("formids").split(",")[0]), con);
            String primaryKey = "";
            final JSONObject datasetValue = JSONUtils.parserJSONObject(params.get("datasetValues"));
            if (datasetValue.containsKey(formdefine.getPrimaryDataSet().getName())) {
                final DesignDataSource pds = formdefine.getPrimaryDataSet();
                final JSONArray is = datasetValue.getJSONObject(pds.getName()).getJSONArray("i");
                final JSONArray us = datasetValue.getJSONObject(pds.getName()).getJSONArray("u");
                primaryKey = is.size() != 0 ? is.getJSONObject(0).getString(pds.getPrimaryField().getName()) : us.size() != 0 ? us.getJSONObject(0).getString(pds.getPrimaryField().getName()) : "";
            } else
                primaryKey = params.get("id");
            cmd.setParam("__singlekey__", primaryKey);
            cmd.executeQuery();
            if (cmd.next())
                json = DBHELPER.cmdRecord2Json(cmd);

        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    /**
     * 保存Dataset
     * 
     * @param con
     * @param dataset
     * @param jsonObject
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private void saveDataset4Flex(final Connection con, final DesignDataSource dataset, final JSONObject datasetValues, final Map<String, String> httpParams) throws Exception {
        IDBCommand dcmd = null;
        IDBCommand ucmd = null;
        IDBCommand icmd = null;
        DesignDataSourceCommand cmd = null;

        if (dataset.isReadOnly())
            return;

        try {
            // 删除数据
            JSONArray datas = datasetValues.getJSONArray("d");
            if (datas.size() != 0) {
                cmd = dataset.getDelete();
                if (!StringUtils.isEmpty(cmd.getSql().trim())) {
                    dcmd = cmd.getCmd(con);
                    for (int i = 0; i < datas.size(); i++) {
                        initDSParams4Execute4Flex(dataset, cmd, dcmd, datas.getJSONObject(i), httpParams);
                        dcmd.execute();
                    }
                }
            }
            // 增加数据
            datas = datasetValues.getJSONArray("i");
            if (datas.size() != 0) {
                // System.out.println(dataset.getName());
                cmd = dataset.getInsert();
                if (!StringUtils.isEmpty(cmd.getSql().trim())) {
                    icmd = cmd.getCmd(con);
                    for (int i = 0; i < datas.size(); i++) {
                        initDSParams4Execute4Flex(dataset, cmd, icmd, datas.getJSONObject(i), httpParams);
                        icmd.execute();
                    }
                }
            }
            // 更新数据
            datas = datasetValues.getJSONArray("u");
            if (datas.size() != 0) {
                cmd = dataset.getUpdate();
                if (!StringUtils.isEmpty(cmd.getSql().trim())) {
                    ucmd = cmd.getCmd(con);
                    for (int i = 0; i < datas.size(); i++) {
                        initDSParams4Execute4Flex(dataset, cmd, ucmd, datas.getJSONObject(i), httpParams);
                        ucmd.execute();
                    }
                }
            }
        } finally {
            DBHELPER.closeCommand(dcmd);
            DBHELPER.closeCommand(ucmd);
            DBHELPER.closeCommand(icmd);
        }

    }

    /**
     * 初始化参数
     * 
     * @param cmd
     * @param dcmd
     * @param jsonObject
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private void initDSParams4Execute4Flex(final DesignDataSource dataset, final DesignDataSourceCommand cmd, final IDBCommand icmd, final JSONObject record, final Map<String, String> httpRequest) throws Exception {
        for (int i = 0; i < cmd.getParamCount(); i++) {
            final SQLParam4Form param = cmd.getParam(i);
            String paramValue = "";

            if (!StringUtils.isEmpty(param.getInitDS())) { // 从数据集中读取数据
                final String fieldKey = StringUtils.equals(dataset.getName(), param.getInitDS()) ? param.getInitField() : param.getInitDS() + "_" + param.getInitField();
                paramValue = record.containsKey(fieldKey) ? record.getString(fieldKey) : "";
            } else { // 从参数列表及其他途径获得数据
                String initValue = param.getInitValue();
                if (!StringUtils.isEmpty(initValue))
                    if (StringUtils.startsWith(initValue, "REQ.")) {
                        initValue = StringUtils.substringAfter(initValue, "REQ.");
                        paramValue = httpRequest.get(initValue);
                    }
            }
            SQLParamUtils.setParam(icmd, param.getDataType(), param.getName(), paramValue);
        }

    }

    /**
     * 得到表单集的数据集合
     * 
     * @param con
     * @param formids
     * @param params
     * @param includeReadonlyDataset
     * @return
     * @throws Exception
     */
    private JSONObject getFormsData(final Connection con, final String[] formids, final Map<String, String> params, final boolean includeReadonlyDataset, final HttpSession session) throws Exception {
        final JSONObject json = new JSONObject();
        for (final String formid : formids) {
            final FormDefine form = RuntimeContext.getFormDefineService().getFormDefine(Long.parseLong(formid), con);
            for (int j = 0; j < form.getDataSetCount(); j++) {
                final DesignDataSource ds = form.getDataSet(j);
                if ((ds.getFieldCount() != 0) && ds.isFormDataSet() && (includeReadonlyDataset || !ds.isReadOnly()))
                    json.put(ds.getName(), getDataSetData(con, json, ds, params, session));
            }
        }
        return json;
    }

    /**
     * 得到Form的空白数据集
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject getFormBlankData(final Map<String, String> params, final HttpSession session) throws Exception {
        Connection con = null;
        JSONObject json = null;
        try {
            con = DBHELPER.getConnection();
            json = getFormsData(con, params.get("formids").split(","), params, false, session);
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 得到数据源
     * 
     * @param params
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public JSONObject getDataSetsData(final Map<String, String> params, final HttpSession session) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final JSONObject datasetJSON = JSONUtils.parserJSONObject(params.get("data"));
            final String[] datasets = params.get("datasets").split(",");
            for (final String dataset : datasets) {
                final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(dataset);
                if (ds.getFieldCount() != 0) {
                    final JSONArray array = getDataSetData(con, datasetJSON, ds, params, session);
                    json.put(ds.getName(), array);
                    datasetJSON.put(ds.getName(), array);
                }
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 根据查询条件生成数据
     * 
     * @param con
     * @param datasetJSONs
     * @param ds
     * @param param2Value
     * @param session
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONArray getDataSetData(final Connection con, final JSONObject datasetName2JSONValues, final DesignDataSource ds, final Map<String, String> param2Value, final HttpSession session) throws Exception {
        if ((session == null) && (ds.isReadOnly() || ds.isAsyncLoad()))
            return null;

        JSONArray jsonArray = null;
        String cacheKey = "";
        Map<String, String> paramName2Value = null;
        // 第一步从缓存中读取数据
        if (ds.isReadOnly() && (ds.getCacheLevel() != 0)) {
            paramName2Value = new HashMap<String, String>();
            cacheKey = calcDataSourceCacheKey(ds, param2Value, datasetName2JSONValues, session.getId(), paramName2Value);
            jsonArray = (JSONArray) SystemCacheManager.getInstance().getDataSourceResultSet(cacheKey);
        }

        if (jsonArray == null) {
            jsonArray = new JSONArray();
            final DesignDataSourceCommand select = ds.getSelect();
            IDBCommand cmd = null;
            try {
                cmd = select.getCmd(con);

                // 赋值参数
                if (paramName2Value != null)
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramName2Value.get(param.getName()));
                    }
                else
                    initDSParamForSelect(con, select, cmd, param2Value, datasetName2JSONValues);

                cmd.executeQuery();

                if (ds.isReadOnly() && ds.isAppendNullRecord())
                    jsonArray.add(DBHELPER.appendNullRecord2Json(cmd));

                while (cmd.next())
                    jsonArray.add(DBHELPER.cmdRecord2Json(cmd));

                if (ds.isReadOnly() && (ds.getCacheLevel() != 0))
                    SystemCacheManager.getInstance().putDataSourceResultSet(cacheKey, jsonArray);
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        }
        return jsonArray;
    }

    /**
     * 初始化DSCommand的参数
     * 
     * @param con
     * @param dsCmd
     * @param cmd
     * @param param2Value
     * @param datasetJSONs
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private boolean initDSParamForSelect(final Connection con, final DesignDataSourceCommand dsCmd, final IDBCommand cmd, final Map<String, String> param2Value, final JSONObject datasetName2Values) throws Exception {
        final boolean result = true;
        for (int i = 0; i < dsCmd.getParamCount(); i++) {
            final SQLParam4Form param = dsCmd.getParam(i);
            final String initDataSetName = param.getInitDS();
            final String initFieldName = param.getInitField();
            final String firstInitDataSetName = param.getFirstInitDS();
            final String firstInitFieldName = param.getFirstInitField();
            String paramValue = null;
            boolean isInitializedParamValue = false;

            // 用于处理诸如下拉列表框初始值的问题
            if (!StringUtils.isEmpty(firstInitDataSetName) && !StringUtils.isEmpty(firstInitFieldName)) {
                final JSONArray records = datasetName2Values.getJSONArray(firstInitDataSetName);
                if ((records != null) && (records.size() != 0) && records.getJSONObject(0).containsKey(firstInitFieldName)) {
                    paramValue = records.getJSONObject(0).getString(firstInitFieldName);
                    isInitializedParamValue = true;
                }
            }

            if (!isInitializedParamValue)
                if (!StringUtils.isEmpty(initDataSetName)) { // 从数据集中读取数据
                    final JSONArray records = datasetName2Values.getJSONArray(initDataSetName);
                    if ((records != null) && (records.size() != 0) && records.getJSONObject(0).containsKey(initFieldName))
                        paramValue = records.getJSONObject(0).getString(initFieldName);
                } else { // 从参数列表及其他途径获得数据
                    String initValue = param.getInitValue();
                    if (!StringUtils.isEmpty(initValue))
                        if (StringUtils.startsWith(initValue, "REQ.")) {
                            initValue = StringUtils.substringAfter(initValue, "REQ.");
                            paramValue = param2Value.get(initValue);
                        }
                }
            // 证书 特殊的用途
            if (StringUtils.equalsIgnoreCase(paramValue, "null"))
                paramValue = Long.toString(Long.MIN_VALUE);
            SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
            // cmd.setParam(paramName, paramValue);
        }
        return result;
    }

    /**
     * 批量导入Excel数据
     * 
     * @param dataJSON
     * @param requestParams
     * @return
     * @throws JSONException
     */
    public JSONObject importExcel(final JSONObject dataJSON, final Map<String, String> requestParams) {
        final JSONObject json = new JSONObject();
        if (dataJSON.getJSONArray("items").getJSONObject(0).containsKey("columns")) {
            final JSONArray columns = dataJSON.getJSONArray("items").getJSONObject(0).getJSONArray("columns");
            final JSONArray datas = dataJSON.getJSONArray("items").getJSONObject(0).getJSONArray("datas");

            for (int i = 0; i < datas.size(); i++) {
                final JSONObject record = new JSONObject();
                final JSONArray data = datas.getJSONArray(i);
                for (int j = 0; j < columns.size(); j++)
                    record.put(columns.getString(j), data.getString(j));
                JSONUtils.append(json, "records", record);
                // json.append("records", record);
            }
            json.put("r", true);
        } else {
            json.put("r", false);
            json.put("msg", "Excel中无数据");
        }

        return json;
    }

    /**
     * @param con
     * @param sessionId
     * @param paramStr
     * @param paramStr2
     * @param paramStr3
     * @return
     * @throws DBException
     * @throws SQLException
     */
    public JSONObject getDataSetRecord4ComboBox(final Connection con, final String datasetName, final String keyValue, final Map<String, String> params, final String sessionId) throws Exception {
        final JSONObject result = new JSONObject();
        result.put("r", false);
        final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
        if (ds == null) {
            result.put("msg", "表单定义已发生变化,无法获取数据定义,请从新打开页面后重试!");
            return result;
        }

        boolean recordInCache = false;
        String cacheKey = "";
        Map<String, String> paramName2Value = null;

        if (ds.isReadOnly() && (ds.getCacheLevel() != 0)) {
            paramName2Value = new HashMap<String, String>();
            cacheKey = calcDataSourceCacheKey4Combobox(ds, keyValue, params, sessionId, paramName2Value);
            final JSONArray cacheRecords = (JSONArray) SystemCacheManager.getInstance().getDataSourceResultSet(cacheKey);
            if (cacheRecords != null) {
                result.put("r", true);
                result.put("records", cacheRecords);
                recordInCache = true;
            }
        }

        // 缓存中无数据
        if (!recordInCache) {
            Connection tempCon = con;
            IDBCommand cmd = null;
            try {
                if (tempCon == null)
                    tempCon = DBHELPER.getConnection();
                final DesignDataSourceCommand select = ds.getSelect();
                cmd = select.getCmd(tempCon);

                // 参数赋值
                if (paramName2Value != null)
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramName2Value.get(param.getName()));
                    }
                else
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        String paramValue = "";
                        if (StringUtils.startsWith(param.getInitField(), "REQ."))
                            paramValue = params.get(StringUtils.substringAfter(param.getInitValue(), "REQ."));
                        else
                            paramValue = params.containsKey(param.getName()) ? params.get(param.getName()) : keyValue;
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
                    }

                cmd.executeQuery();
                int index = 0;
                final JSONArray records = new JSONArray();
                while (cmd.next() && ((index++) < MAX_RECORD_NUMBER))
                    records.add(DBHELPER.cmdRecord2Json(cmd));
                result.put("r", true);
                result.put("records", records);
                if (ds.isReadOnly() && (ds.getCacheLevel() != 0))
                    SystemCacheManager.getInstance().putDataSourceResultSet(cacheKey, result);
            } finally {
                DBHELPER.closeCommand(cmd);
                if (tempCon != con)
                    DBHELPER.closeConnection(tempCon);
            }
        }
        return result;
    }

    /**
     * 计算缓存值
     * 
     * @param ds
     * @param param2Value
     * @param datasetName2Values
     * @param sessionId
     * @return
     */
    private String calcDataSourceCacheKey(final DesignDataSource ds, final Map<String, String> param2Value, final JSONObject datasetName2Values, final String sessionId, final Map<String, String> paramName2Value) {
        String result = ds.getCacheKey();
        if (ds.getCacheLevel() == 1)
            result += "-" + sessionId;
        String tempStr = "";
        final DesignDataSourceCommand select = ds.getSelect();
        for (int i = 0; i < select.getParamCount(); i++) {
            final SQLParam4Form param = select.getParam(i);
            final String initDataSetName = param.getInitDS();
            final String initFieldName = param.getInitField();
            final String firstInitDataSetName = param.getFirstInitDS();
            final String firstInitFieldName = param.getFirstInitField();
            String paramValue = null;
            boolean isInitializedParamValue = false;

            // 用于处理诸如下拉列表框初始值的问题
            if (!StringUtils.isEmpty(firstInitDataSetName) && !StringUtils.isEmpty(firstInitFieldName)) {
                final JSONArray records = datasetName2Values.getJSONArray(firstInitDataSetName);
                if ((records != null) && (records.size() != 0) && records.getJSONObject(0).containsKey(firstInitFieldName)) {
                    paramValue = records.getJSONObject(0).getString(firstInitFieldName);
                    isInitializedParamValue = true;
                }
            }

            if (!isInitializedParamValue)
                if (!StringUtils.isEmpty(initDataSetName)) { // 从数据集中读取数据
                    final JSONArray records = datasetName2Values.getJSONArray(initDataSetName);
                    if ((records != null) && (records.size() != 0) && records.getJSONObject(0).containsKey(initFieldName))
                        paramValue = records.getJSONObject(0).getString(initFieldName);
                } else { // 从参数列表及其他途径获得数据
                    String initValue = param.getInitValue();
                    if (!StringUtils.isEmpty(initValue))
                        if (StringUtils.startsWith(initValue, "REQ.")) {
                            initValue = StringUtils.substringAfter(initValue, "REQ.");
                            paramValue = param2Value.get(initValue);
                        }
                }
            // 证书 特殊的用途
            if (StringUtils.equalsIgnoreCase(paramValue, "null"))
                paramValue = Long.toString(Long.MIN_VALUE);

            paramName2Value.put(param.getName(), paramValue);

            tempStr += param.getName() + "=" + paramValue;
        }

        if (StringUtils.isEmpty(tempStr))
            result += SecurityUtils.md5(tempStr);
        return result;
    }

    /**
     * 计算查询键值
     * 
     * @param ds
     * @param keyValue
     * @param params
     * @param sessionId
     * @return
     */
    private String calcDataSourceCacheKey4Combobox(final DesignDataSource ds, final String keyValue, final Map<String, String> params, final String sessionId, final Map<String, String> paramName2Value) {
        String result = ds.getCacheKey();
        if (ds.getCacheLevel() == 1)
            result += "-" + sessionId;
        String tempStr = "";
        final DesignDataSourceCommand select = ds.getSelect();
        for (int i = 0; i < select.getParamCount(); i++) {
            final SQLParam4Form param = select.getParam(i);
            String paramValue = "";
            if (StringUtils.startsWith(param.getInitField(), "REQ."))
                paramValue = params.get(StringUtils.substringAfter(param.getInitValue(), "REQ."));
            else
                paramValue = params.containsKey(param.getName()) ? params.get(param.getName()) : keyValue;
            paramName2Value.put(param.getName(), paramValue);
            tempStr += param.getName() + "=" + paramValue;
        }
        if (StringUtils.isEmpty(tempStr))
            result += SecurityUtils.md5(tempStr);
        return result;
    }

    /**
     * 计算缓存键值
     * 
     * @param ds
     * @param dataset2KeyValues
     * @param sessionId
     * @return
     */
    private String calcDataSourceCacheKey(final DesignDataSource ds, final Map<String, String> dataset2KeyValues, final String sessionId, final Map<String, String> paramName2Value) {
        String result = ds.getCacheKey();
        if (ds.getCacheLevel() == 1)
            result += "-" + sessionId;
        String tempStr = "";
        final DesignDataSourceCommand select = ds.getSelect();
        for (int i = 0; i < select.getParamCount(); i++) {
            final SQLParam4Form param = select.getParam(i);
            final String paramValue = dataset2KeyValues.get(param.getInitDS());
            paramName2Value.put(param.getName(), paramValue);
            tempStr += param.getName() + "=" + paramValue;
        }
        if (StringUtils.isEmpty(tempStr))
            result += SecurityUtils.md5(tempStr);
        return result;
    }

    /**
     * 计算查询键
     * 
     * @param ds
     * @param datasetParam
     * @param httpRequest
     * @param sessionId
     * @return
     * @throws Exception
     */
    private String calcDataSourceCacheKey(final DesignDataSource ds, final JSONObject datasetParam, final Map<String, String> httpRequest, final String sessionId, final Map<String, String> paramName2Value) throws Exception {
        String result = ds.getCacheKey();
        if (ds.getCacheLevel() == 1)
            result += "-" + sessionId;
        String tempStr = "";
        final DesignDataSourceCommand select = ds.getSelect();
        for (int i = 0; i < select.getParamCount(); i++) {
            final SQLParam4Form param = select.getParam(i);
            String paramValue = "";
            if (!StringUtils.isEmpty(param.getInitDS()))
                paramValue = getParamValueByDataSetJSON(datasetParam, param.getInitDS(), param.getInitField());
            else {
                String initValue = param.getInitValue();
                if (!StringUtils.isEmpty(initValue))
                    if (StringUtils.startsWith(initValue, "REQ.")) {
                        initValue = StringUtils.substringAfter(initValue, "REQ.");
                        paramValue = httpRequest.get(initValue);
                    }
            }
            paramName2Value.put(param.getName(), paramValue);
            tempStr += param.getName() + "=" + paramValue;
        }
        if (StringUtils.isEmpty(tempStr))
            result += SecurityUtils.md5(tempStr);
        return result;
    }

    /**
     * 获取DataSet值
     * 
     * @param con
     * @param datasetName
     * @param params
     * @return
     * @throws DBException
     * @throws SQLException
     */
    public JSONObject getASyncDataSetRecord(final Connection con, final String datasetName, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        final DesignDataSource ds = DataSetCacheService4WebClient.getInstance().getDataSet(datasetName);
        if (ds == null) {
            json.put("msg", "表单定义已发生变化,无法获取数据定义,请从新打开页面后重试!");
            return json;
        }

        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final DesignDataSourceCommand select = ds.getSelect();
            cmd = select.getCmd(tempCon);
            for (int i = 0; i < select.getParamCount(); i++) {
                String paramValue = "";
                final SQLParam4Form param = select.getParam(i);
                if ((param.getDataType() == DBParamDataType.String) && param.isPartMatch())
                    paramValue = "%" + params.get("matchStr") + "%";
                else if (StringUtils.startsWith(param.getInitValue(), "REQ."))
                    paramValue = params.get(StringUtils.substringAfter(param.getInitValue(), "REQ."));
                else
                    paramValue = params.get("parentKey");
                /*
                 * if (params.containsKey(param.getName())) paramValue =
                 * params.get(param.getName()); else paramValue =
                 * params.get(param.getInitDS());
                 */
                SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
            }
            cmd.executeQuery();
            int index = 0;
            final JSONArray records = new JSONArray();
            while (cmd.next() && ((index++) < MAX_RECORD_NUMBER))
                records.add(DBHELPER.cmdRecord2Json(cmd));
            json.put("r", true);
            json.put("records", records);
        } finally {
            DBHELPER.closeCommand(cmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    // dynamic async dataset max record numbers
    private static final int MAX_RECORD_NUMBER = 2000;

    /**
     * @return
     */
    public static DataService4Form getInstance() {
        return INSTANCE;
    }

    private static final DataService4Form INSTANCE = new DataService4Form();

    /**
     * 
     */
    private DataService4Form() {

    }

    /**
     * @param exportDefineJSON
     * @param params
     * @return
     */
    public JSONArray exportData4ExportExcel(final JSONObject exportDefineJSON, final Map<String, String> params) {
        //
        return null;
    }

}
