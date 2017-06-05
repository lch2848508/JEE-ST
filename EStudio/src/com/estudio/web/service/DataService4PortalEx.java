package com.estudio.web.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.db.DBFieldDataType;
import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.define.webclient.SQLParamUtils;
import com.estudio.define.webclient.portal.PortalGridDefineEx;
import com.estudio.define.webclient.portal.PortalGridExColumn;
import com.estudio.define.webclient.portal.PortalGridExControl;
import com.estudio.define.webclient.portal.PortalGridExGrid;
import com.estudio.define.webclient.portal.PortalGridExSQLBase;
import com.estudio.define.webclient.portal.PortalGridExTree;
import com.estudio.define.webclient.portal.SQLColumnURL;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.define.webclient.portal.SQLDefineBase;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public final class DataService4PortalEx {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    /**
     * @throws DBException
     *             获取初始化数据
     * @param con
     * @param portalID
     * @param params
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws
     */
    public JSONObject getInitData4Flex(final Connection con, final PortalGridDefineEx portalGridDefine, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        for (PortalGridExControl control : portalGridDefine.getControls()) {
            if (control.getControlType() == PortalGridExControl.CONTROL_GRID) {
                PortalGridExGrid gridControl = (PortalGridExGrid) control;
                JSONObject gridData = getGridData(con, gridControl, params);
                JSONArray gridRows = gridData.getJSONArray("rows");
                if (gridRows != null && !gridRows.isEmpty())
                    addRelationFieldValueToParams(gridControl.getControlName(), gridRows.getJSONObject(0), params, gridControl.getRelationFields());
                json.put(control.getControlName(), gridData);
            } else if (control.getControlType() == PortalGridExControl.CONTROL_TREE) {
                JSONObject treeJson = getTreeData(con, (PortalGridExTree) control, params);
                json.put(control.getControlName(), treeJson);
                PortalGridExTree treeControl = (PortalGridExTree) control;
                addRelationFieldValueToParams(treeControl.getControlName(), treeJson, params, treeControl.getRelationFields());
            }
        }
        return json;
    }

    /**
     * 将第一条数据添加到参数列表中
     * 
     * @param controlName
     * 
     * @param recordJson
     * @param params
     * @param relationFields
     */
    private void addRelationFieldValueToParams(String controlName, JSONObject recordJson, Map<String, String> params, List<String> relationFields) {
        for (String fieldName : relationFields)
            params.put(controlName + "$" + fieldName, recordJson.getString(fieldName));
        params.put(controlName + "$__key__", recordJson.getString("__key__"));
        params.put(controlName + "$__keys__", recordJson.getString("__key__"));
    }

    /**
     * 获取Tree数据
     * 
     * @param con
     * @param control
     * @param params
     * @return
     * @throws Exception
     */
    private JSONObject getTreeData(Connection con, PortalGridExTree control, Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        IDBCommand cmd = null;
        try {
            SQLDefineBase sqlDefine = control.getSelectSQLDefine();
            SQLDefineBase rootSqlDefine = control.getRootSQLDefine();

            String treeRootID = getTreeRootID(con, sqlDefine, rootSqlDefine, params, control);
            params.put(control.getControlName() + "$__key__", treeRootID);
            params.put(control.getControlName() + "$" + sqlDefine.getKeyFieldName(), treeRootID);

            json.put(sqlDefine.getKeyFieldName(), treeRootID);
            json.put(sqlDefine.getLabelFieldName(), control.getControlComment());
            json.put("__key__", treeRootID);
            json.put("root", true);
            json.put("isAsync", control.isAsyncLoad());

            JSONArray records = new JSONArray();
            cmd = sqlDefine.getCmd(con, null);
            int treeDataLevel = 0;
            getTreeData(cmd, sqlDefine, control, params, records, treeDataLevel);
            if (!records.isEmpty())
                json.put("children", records);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    /**
     * 获取根节点
     * 
     * @param sqlDefine
     * @param rootSqlDefine
     * @param params
     * @param control
     * @return
     * @throws Exception
     */
    private String getTreeRootID(Connection con, SQLDefineBase sqlDefine, SQLDefineBase rootSqlDefine, Map<String, String> params, PortalGridExTree control) throws Exception {
        String result = "";

        // 如果有根目录SQL定义
        if (rootSqlDefine != null) {
            IDBCommand cmd = null;
            try {
                cmd = rootSqlDefine.getCmd(con, null);
                for (int i = 0; i < rootSqlDefine.getParamCount(); i++) {
                    SQLParam4Portal param = rootSqlDefine.getParam(i);
                    String paramValue = params.get(param.getInitSQLName() + "$" + param.getInitField());
                    SQLParamUtils.setParam(cmd, param, paramValue);
                }
                if (cmd.executeQuery() && cmd.next())
                    result = cmd.getString(1);
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        } else {
            String keyName = control.getControlName() + "$" + "__key__";
            if (params.containsKey(keyName)) {
                result = params.get(keyName);
            } else {
                keyName = control.getControlName() + "$" + control.getSelectSQLDefine().getKeyFieldName();
                if (params.containsKey(keyName))
                    result = params.get(keyName);
            }

            if (StringUtils.isEmpty(result)) {
                for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                    SQLParam4Portal param = sqlDefine.getParam(i);
                    if (StringUtils.equalsIgnoreCase(param.getName(), sqlDefine.getKeyFieldName())) {
                        result = params.get(param.getInitValue());
                    }
                }
            }
        }
        if (StringUtils.isEmpty(result))
            result = "-1";
        return result;
    }

    /**
     * 获取Tree数据
     * 
     * @param cmd
     * @param sqlDefine
     * @param control
     * @param params
     * @param records
     * @throws Exception
     */
    private void getTreeData(IDBCommand cmd, SQLDefineBase sqlDefine, PortalGridExTree control, Map<String, String> params, JSONArray records, int treeDataLevel) throws Exception {
        setCmdParams4Selected(control, sqlDefine, cmd, params);
        cmd.executeQuery();
        while (cmd.next()) {
            JSONObject json = generalTreeRecord(cmd, sqlDefine, control);
            records.add(json);
        }

        int level = control.isAsyncLoad() ? treeDataLevel + 1 : 0;
        if (level < 1) {
            for (int i = 0; i < records.size(); i++) {
                JSONObject json = records.getJSONObject(i);
                if (control.isAsyncLoad())
                    json.put("isLoaded", true);
                params.put(control.getControlName() + "$__key__", json.getString("__key__"));
                params.put(control.getControlName() + "$" + sqlDefine.getKeyFieldName(), json.getString(sqlDefine.getKeyFieldName()));
                JSONArray childRecords = new JSONArray();
                getTreeData(cmd, sqlDefine, control, params, childRecords, level);
                if (!childRecords.isEmpty())
                    json.put("children", childRecords);
            }
        }
    }

    /**
     * @param cmd
     * @param sqlDefine
     * @param control
     * @return
     * @throws Exception
     */
    private JSONObject generalTreeRecord(IDBCommand cmd, SQLDefineBase sqlDefine, PortalGridExTree control) throws Exception {
        JSONObject json = new JSONObject();
        for (int i = 0; i < sqlDefine.getFieldCount(); i++)
            json.put(sqlDefine.getField(i).getFieldName(), cmd.getString(sqlDefine.getField(i).getFieldName()));
        json.put("__key__", cmd.getString(sqlDefine.getKeyFieldName()));
        return json;
    }

    /**
     * 获取Grid数据
     * 
     * @param con
     * @param control
     * @param params
     * @return
     * @throws Exception
     */
    private JSONObject getGridData(Connection con, PortalGridExGrid control, Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        SQLDefineBase sqlDefine = control.getSelectSQLDefine();
        IDBCommand rcmd = null; // record count record
        IDBCommand pcmd = null; // page record
        IDBCommand clearCmd = null;
        IDBCommand idCmd = null;
        Map<String, String> controlFilterParams = new HashMap<String, String>();
        String paramPrefix = control.getControlName() + "$";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.startsWith(key, paramPrefix)) {
                String newKey = StringUtils.substringAfter(key, paramPrefix);
                controlFilterParams.put(newKey, entry.getValue());
            }
        }

        try {
            if (control.isPagination()) { // 分页
                int page = Convert.try2Int(params.get("p"), 1);
                int recordPerPage = Convert.try2Int(params.get("r"), 25);
                pcmd = sqlDefine.getPCmd(con, null, controlFilterParams);

                // 計算总页数
                rcmd = sqlDefine.getRCCmd(con, null, controlFilterParams);
                setCmdParams4Selected(control, sqlDefine, rcmd, params);
                rcmd.executeQuery();
                rcmd.next();
                int recordTotal = rcmd.getInt(1);
                int totalPage = (recordTotal % recordPerPage) == 0 ? recordTotal / recordPerPage : (recordTotal / recordPerPage) + 1;
                page = Math.max(1, Math.min(totalPage, page));

                boolean isSupportPageOptimize = sqlDefine.isSupportPageOptimize();
                if (isSupportPageOptimize) { // 是否优化分页
                    long threadId = Thread.currentThread().getId();
                    String sessionId = params.get("SESSIONID");
                    idCmd = sqlDefine.getPageIdsCmd(con, controlFilterParams);
                    clearCmd = DBHELPER.getCommand(con, "delete from sys_utils_page_optimize where thread_id=? and session_id=?");
                    clearCmd.setParam(1, threadId);
                    clearCmd.setParam(2, sessionId);
                    pcmd.setParam("thread_id", threadId);
                    pcmd.setParam("session_id", sessionId);
                    setCmdParams4Selected(control, sqlDefine, idCmd, params);
                    idCmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                    idCmd.setParam("E", new Long(page * recordPerPage));
                    idCmd.setParam("R", recordPerPage);
                    idCmd.setParam("thread_id", threadId);
                    idCmd.setParam("session_id", sessionId);
                    idCmd.execute();
                } else {
                    pcmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                    pcmd.setParam("E", new Long(page * recordPerPage));
                    pcmd.setParam("R", recordPerPage);
                }
                json.put("t", totalPage);
                json.put("tr", recordTotal);
                json.put("p", page);

            } else { // 不分页
                pcmd = sqlDefine.getCmd(con, controlFilterParams);
                json.put("t", 65535);
                json.put("tr", Integer.MAX_VALUE);
                json.put("p", 1);
            }

            setCmdParams4Selected(control, sqlDefine, pcmd, params);

            pcmd.executeQuery();
            int totalRecord = 0;
            while (pcmd.next() && totalRecord < 2500) {
                JSONUtils.append(json, "rows", generalGridRecord(pcmd, control));
                totalRecord++;
            }

            if (clearCmd != null)
                clearCmd.execute();
        } finally {
            DBHELPER.closeCommand(rcmd);
            DBHELPER.closeCommand(pcmd);
            DBHELPER.closeCommand(idCmd);
            DBHELPER.closeCommand(clearCmd);
        }
        return json;
    }

    /**
     * 生成单行记录
     * 
     * @param pcmd
     * @param control
     * @return
     * @throws Exception
     */
    private JSONObject generalGridRecord(IDBCommand cmd, PortalGridExGrid control) throws Exception {
        JSONObject json = new JSONObject();
        for (String fieldName : control.getNoColumnFields()) {
            json.put(fieldName, cmd.getString(fieldName));
        }
        String keyFieldName = control.getSelectSQLDefine().getKeyFieldName();
        if (!StringUtils.isEmpty(keyFieldName))
            json.put("__key__", cmd.getString(keyFieldName));
        for (PortalGridExColumn column : control.getColumns()) {
            String fieldName = column.getFieldName();
            if (column.getURLCount() != 0) { // 超级连接列
                for (int i = 0; i < column.getURLCount(); i++) {
                    final SQLColumnURL url = column.getURL(i);
                    String hrefCaption = url.getLabel();
                    if (StringUtils.isEmpty(hrefCaption) && !StringUtils.isEmpty(fieldName))
                        hrefCaption = cmd.getString(column.getFieldName());
                    if (!StringUtils.isEmpty(hrefCaption)) {
                        String jsFun = "";
                        if (url.isFunction()) {
                            if ((url.getJson() != null) && url.getJson().containsKey("URL"))
                                jsFun = url.getJson().getString("URL");
                        } else
                            jsFun = url.getType();
                        JSONObject urlJson = new JSONObject();
                        urlJson.put("control", control.getControlName());
                        urlJson.put("fun", jsFun);
                        urlJson.put("label", hrefCaption);
                        JSONUtils.append(json, column.getUrlFieldName(), urlJson);
                    }
                }
            }

            if (!StringUtils.isEmpty(fieldName)) {
                String fieldValue = "";
                final String dataType = cmd.getMetaData().getColumnTypeName(cmd.getFieldNames().indexOf(fieldName) + 1);
                if (RuntimeContext.getDbEntryService().getDBFieldDataTypeByTypeString(dataType).getDataTypeCategory() == DBFieldDataType.categoryBinary)
                    fieldValue = Convert.bytes2Str(cmd.getBytes(fieldName));
                else
                    fieldValue = cmd.getString(fieldName);
                json.put(fieldName, fieldValue);

            }
        }
        return json;
    }

    /**
     * 设置查找参数
     * 
     * @param control
     * 
     * @param sqlDefine
     * @param rcmd
     * @param params
     * @throws Exception
     */
    private void setCmdParams4Selected(PortalGridExControl control, SQLDefineBase sqlDefine, IDBCommand rcmd, Map<String, String> params) throws Exception {
        String paramValue = "";
        for (int i = 0; i < sqlDefine.getParamCount(); i++) {
            SQLParam4Portal param = sqlDefine.getParam(i);
            if (param.isFilter()) // 控件
            {
                paramValue = params.get(control.getControlName() + "$" + param.getName());
                if (StringUtils.isEmpty(paramValue))
                    paramValue = params.get(param.getInitValue());
                else if (param.getDataType() == DBParamDataType.String && param.isPartMatch())
                    paramValue = "%" + paramValue + "%";
            } else if (params.containsKey(param.getInitSQLName() + "$" + param.getInitField())) // 传递过来有值
                paramValue = params.get(param.getInitSQLName() + "$" + param.getInitField());
            else if (StringUtils.startsWith(param.getInitValue(), "REQ.")) // REQ环境类
                paramValue = params.get(StringUtils.substringAfter(param.getInitValue(), "REQ."));
            else
                paramValue = params.get(param.getInitValue());
            SQLParamUtils.setParam(rcmd, param, paramValue);
        }
    }

    /**
     * 获取控件记录
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject getControlRecords(Map<String, String> params) throws Exception {
        long portalId = Convert.str2Long(params.get("portalId"));
        String[] controls = params.get("controls").split(",");
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            PortalGridDefineEx portalGridDefine = (PortalGridDefineEx) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con);
            if (portalGridDefine == null)
                throw new Exception("栏目已经被删除,请关闭系统并重新登录!");
            for (PortalGridExControl control : portalGridDefine.getControls()) {
                if (ArrayUtils.contains(controls, control.getControlName())) {
                    if (control.getControlType() == PortalGridExControl.CONTROL_GRID) {
                        PortalGridExGrid gridControl = (PortalGridExGrid) control;
                        JSONObject gridData = getGridData(con, gridControl, params);
                        JSONArray gridRows = gridData.getJSONArray("rows");
                        if (gridRows != null && !gridRows.isEmpty())
                            addRelationFieldValueToParams(gridControl.getControlName(), gridRows.getJSONObject(0), params, gridControl.getRelationFields());
                        json.put(control.getControlName(), gridData);
                    } else if (control.getControlType() == PortalGridExControl.CONTROL_TREE) {
                        JSONObject treeJson = getTreeData(con, (PortalGridExTree) control, params);
                        json.put(control.getControlName(), treeJson);
                        PortalGridExTree treeControl = (PortalGridExTree) control;
                        addRelationFieldValueToParams(treeControl.getControlName(), treeJson, params, treeControl.getRelationFields());
                    }
                }
            }

            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JSONObject deletePortalExControlRecord(Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            long portalId = Convert.str2Long(params.get("portalId"));
            PortalGridDefineEx portal = (PortalGridDefineEx) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con);
            if (portal == null)
                throw new Exception("栏目已经被删除,请关闭系统并重新登录或刷新页面。");
            String controlName = params.get("controlName");
            PortalGridExControl control = portal.getPortalGridExControl(controlName);
            if (control == null || !(control instanceof PortalGridExSQLBase))
                throw new Exception("栏目已经发生更改,请关闭系统并重新登录或刷新页面。");
            SQLDefineBase sqlDefine = ((PortalGridExSQLBase) control).getDeleteSQLDefine();
            if (sqlDefine == null)
                throw new Exception("控件尚未定义删除操作的SQL,请与系统管理员联系。");
            String[] ids = StringUtils.split(params.get("ids"), ",");
            DBHELPER.beginTransaction(con);
            cmd = sqlDefine.getCmd(con, null);
            for (String id : ids) {
                for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                    SQLParam4Portal param = sqlDefine.getParam(i);
                    String paramValue = "";
                    if (StringUtils.equalsIgnoreCase(controlName, param.getInitSQLName()))
                        paramValue = id;
                    else
                        paramValue = params.get(param.getName());
                    SQLParamUtils.setParam(cmd, param, paramValue);
                }
                cmd.execute();
            }
            con.commit();
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            if (con != null) {
                con.rollback();
                DBHELPER.endTransaction(con);
            }
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JSONObject exchangePortalExControlRecord(Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            long portalId = Convert.str2Long(params.get("portalId"));
            PortalGridDefineEx portal = (PortalGridDefineEx) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con);
            if (portal == null)
                throw new Exception("栏目已经被删除,请关闭系统并重新登录或刷新页面。");
            String controlName = params.get("controlName");
            PortalGridExControl control = portal.getPortalGridExControl(controlName);
            if (control == null || !(control instanceof PortalGridExSQLBase))
                throw new Exception("栏目已经发生更改,请关闭系统并重新登录或刷新页面。");
            SQLDefineBase sqlDefine = ((PortalGridExSQLBase) control).getExchangeSQLDefine();
            if (sqlDefine == null)
                throw new Exception("控件尚未定义交换记录顺序操作的SQL,请与系统管理员联系。");
            String[] ids = StringUtils.split(params.get("ids"), ",");
            cmd = sqlDefine.getCmd(con, null);
            cmd.setParam(sqlDefine.getParam(0).getName(), ids[0]);
            cmd.setParam(sqlDefine.getParam(1).getName(), ids[1]);
            cmd.execute();
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JSONObject getControlItemRecord(Connection con, long portalId, Map<String, String> params, String firstFormDatasetKeyValue) throws Exception {
        JSONObject json = null;
        IDBCommand cmd = null;
        try {
            PortalGridDefineEx portal = (PortalGridDefineEx) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con);
            if (portal == null)
                throw new Exception("栏目已经被删除,请关闭系统并重新登录或刷新页面。");
            String controlName = params.get("controlName");
            PortalGridExControl control = portal.getPortalGridExControl(controlName);
            if (control == null || !(control instanceof PortalGridExSQLBase))
                throw new Exception("栏目已经发生更改,请关闭系统并重新登录或刷新页面。");
            SQLDefineBase sqlDefine = ((PortalGridExSQLBase) control).getSelectSQLDefine();
            cmd = sqlDefine.getSingleCmd(con, params);
            params.put("K", firstFormDatasetKeyValue);
            setCmdParams4Selected(control, sqlDefine, cmd, params);
            cmd.setParam("K", firstFormDatasetKeyValue);
            if (cmd.executeQuery() && cmd.next()) {
                if (control.getControlType() == PortalGridExControl.CONTROL_GRID)
                    json = generalGridRecord(cmd, (PortalGridExGrid) control);
                else
                    json = generalTreeRecord(cmd, sqlDefine, (PortalGridExTree) control);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JSONObject refreshPortalGridExSelectedItem(Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            long portalId = Convert.str2Long(params.get("portalId"));
            String[] controlNames = params.get("controls").split(",");
            for (String controlName : controlNames) {
                params.put("controlName", controlName);
                JSONObject controlParams = JSONUtils.parserJSONObject(params.get(controlName + "$params"));
                for (String k : controlParams.keySet())
                    params.put(k, controlParams.getString(k));
                json.put(controlName, getControlItemRecord(con, portalId, params, params.get(controlName + "$__recordkey__")));
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 保存Grid中的编辑数据
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject savePortalGridEx(Map<String, String> params) throws Exception {
        String controlName = params.get("controlName");
        long portalId = Convert.str2Long(params.get("portalId"));
        JSONArray news = JSONUtils.parserJSONArray(params.get("n"));
        JSONArray updates = JSONUtils.parserJSONArray(params.get("u"));
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand ucmd = null;
        IDBCommand icmd = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);
            PortalGridDefineEx portal = (PortalGridDefineEx) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con);
            PortalGridExSQLBase control = (PortalGridExSQLBase) portal.getPortalGridExControl(controlName);
            if (!news.isEmpty() && control.getInsertSQLDefine() != null) {
                icmd = control.getInsertSQLDefine().getCmd(con, null);
                saveRecordToPortalGridEx(control.getInsertSQLDefine(), icmd, news, controlName);
            }
            if (!updates.isEmpty() && control.getUpdateSQLDefine() != null) {
                ucmd = control.getUpdateSQLDefine().getCmd(con, null);
                saveRecordToPortalGridEx(control.getUpdateSQLDefine(), ucmd, updates, controlName);
            }
            json.put("r", true);
        } finally {
            if (con != null)
                DBHELPER.endTransaction(con);
            DBHELPER.closeCommand(icmd);
            DBHELPER.closeCommand(ucmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 保存数据到数据库
    private void saveRecordToPortalGridEx(SQLDefineBase sqlDefine, IDBCommand cmd, JSONArray records, String controlName) throws Exception {
        for (int i = 0; i < records.size(); i++) {
            try {
                JSONObject record = records.getJSONObject(i);
                for (int j = 0; j < sqlDefine.getParamCount(); j++) {
                    SQLParam4Portal param = sqlDefine.getParam(j);
                    String paramValue = StringUtils.equals(controlName, param.getInitSQLName()) ? record.getString(param.getInitField()) : record.getString(param.getInitSQLName() + "$" + param.getInitField());
                    SQLParamUtils.setParam(cmd, param, paramValue);
                }
                cmd.execute();
                cmd.getConnection().commit();
            } catch (final Exception e) {
                ExceptionUtils.loggerException(e);

                cmd.getConnection().rollback();
                ExceptionUtils.printExceptionTrace(e);
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JSONObject getFilterComboboxItems(Connection con, PortalGridDefineEx portal, Map<String, String> httpParams) throws Exception {
        JSONObject json = new JSONObject();
        for (PortalGridExControl control : portal.getControls()) {
            if (control.getControlType() != PortalGridExControl.CONTROL_GRID)
                continue;
            PortalGridExGrid grid = (PortalGridExGrid) control;
            for (int i = 0; i < grid.getSelectSQLDefine().getParamCount(); i++) {
                SQLParam4Portal param = grid.getSelectSQLDefine().getParam(i);
                if (StringUtils.equals(param.getFilterControl(), "ComboBox") && !StringUtils.isEmpty(param.getParamValueSQL()))
                    json.put(StringUtils.upperCase(control.getControlName() + "_" + param.getName()), getFilterComboboxItems(con, grid.getSelectSQLDefine(), param.getParamValueSQL(), httpParams));
            }
        }
        return json;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private JSONArray getFilterComboboxItems(Connection con, SQLDefineBase sqlDefineBase, String paramValueSQL, Map<String, String> httpParams) throws Exception {
        IDBCommand cmd = null;
        JSONArray result = new JSONArray();
        try {
            cmd = DBHELPER.getCommand(con, paramValueSQL);
            for (int i = 0; i < sqlDefineBase.getParamCount(); i++) {
                SQLParam4Portal param = sqlDefineBase.getParam(i);
                SQLParamUtils.setParam(cmd, param, httpParams.get(param.getName()));
            }
            SQLParamUtils.setParam(cmd, DBParamDataType.String, "PARENT_COMBOBOX", httpParams.get("PARENT_COMBOBOX"));
            cmd.executeQuery();
            JSONObject record = new JSONObject();
            record.put("ID", "");
            record.put("LABEL", "");
            result.add(record);
            while (cmd.next()) {
                result.add(DBHELPER.cmdRecord2Json(cmd));
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    /**
     * 
     * @param con
     * @param sqlDefine
     * @param paramName
     * @param httpParams
     * @param json
     * @return
     * @throws Exception
     */
    public void getSQLDefine4PortalComboboxFilterItems(Connection con, final SQLDefine4Portal sqlDefine, String paramName, Map<String, String> httpParams, final JSONObject json) throws Exception {
        SQLParam4Portal param = sqlDefine.getParam(paramName);
        String sql = param.getParamValueSQL();
        json.put("records", getFilterComboboxItems(con, sqlDefine, sql, httpParams));
    }

    /**
     * 获取下拉空间的值
     * 
     * @param object
     * @param portalId
     * @param controlName
     * @param httpParams
     * @return
     * @throws Exception
     */
    public JSONObject getAFilterComboboxItems(Connection con, long portalId, String controlName, String paramName, Map<String, String> httpParams) throws Exception {
        Connection tempCon = con;
        JSONObject json = new JSONObject();
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            PortalGridDefineEx portal = (PortalGridDefineEx) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, tempCon);
            PortalGridExSQLBase control = (PortalGridExSQLBase) portal.getPortalGridExControl(controlName);
            getSQLDefine4PortalComboboxFilterItems(tempCon, ((SQLDefine4Portal) ((PortalGridExGrid) control).getSelectSQLDefine()), paramName, httpParams, json);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 
     * @param portalId
     * @param controlName
     * @param pid
     * @return
     * @throws Exception
     */
    public JSONObject loadPortalExTreeAsyncRecords(long portalId, String controlName, String pid, Map<String, String> params) throws Exception {
        Connection con = null;
        JSONObject json = new JSONObject();
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            PortalGridDefineEx portal = (PortalGridDefineEx) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalId, con);
            PortalGridExTree control = (PortalGridExTree) portal.getPortalGridExControl(controlName);
            SQLDefineBase sqlDefine = control.getSelectSQLDefine();
            cmd = sqlDefine.getCmd(con, null);
            params.put(control.getControlName() + "$__key__", pid);
            params.put(control.getControlName() + "$" + sqlDefine.getKeyFieldName(), pid);
            JSONArray records = new JSONArray();
            getTreeData(cmd, sqlDefine, control, params, records, 0);
            json.put("r", true);
            if (!records.isEmpty())
                json.put("records", records);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    // -------------------------------------------------------------------------------------------------------------------------------
    private DataService4PortalEx() {
    }

    private static final DataService4PortalEx INSTANCE = new DataService4PortalEx();

    public static DataService4PortalEx getInstance() {
        return INSTANCE;
    }

}
