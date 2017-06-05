package com.estudio.web.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.db.SQLParam;
import com.estudio.define.design.db.DBFieldDataType;
import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.define.webclient.SQLParamUtils;
import com.estudio.define.webclient.portal.AbstractPortalGridDefine;
import com.estudio.define.webclient.portal.PortalGridColumn;
import com.estudio.define.webclient.portal.PortalGridDefine;
import com.estudio.define.webclient.portal.SQLColumnURL;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.define.webclient.portal.SQLDefineBase;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

public final class DataService4Portal {
    protected static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    protected static final DataService4Portal INSTANCE = new DataService4Portal();

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
    public JSONObject getInitData4Flex(final Connection con, final PortalGridDefine portalGridDefine, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        if (portalGridDefine.getLayout().isTreeView())
            json.put("TREE", getTreeData4Flex(con, portalGridDefine, params));

        final String treeRootId = getTreeRootID(portalGridDefine, params, con);
        json.put("TREEROOTID", treeRootId);

        if (portalGridDefine.getLayout().isGridView())
            json.put("GRID", portalGridDefine.isCommonSearch() ? new JSONArray() : getGridData4Flex(con, portalGridDefine, treeRootId, params));
        return json;
    }

    /**
     * 获取Grid列表数据
     * 
     * @param con
     * @param portalGridDefine
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONObject getGridData4Flex(final Connection con, final PortalGridDefine portalGridDefine, final String parentId, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        try {
            if (tempCon != null)
                tempCon = DBHELPER.getConnection();
            if (portalGridDefine.getLayout().isGridView()) {
                final SQLDefine4Portal sqlDefine = portalGridDefine.getGridSQLDefine();
                final int page = Convert.try2Int(params.get("p"), 1);
                final int recordPerPage = portalGridDefine.getLayout().isGridPagination() ? Convert.try2Int(params.get("r"), 25) : 2500;
                final boolean includeCheckBox = portalGridDefine.getLayout().isGridSupportCheckBox();
                getGridData4Flex(tempCon, sqlDefine, params, json, page, recordPerPage, includeCheckBox, (StringUtils.isEmpty(parentId) || StringUtils.equalsIgnoreCase(parentId, "null")) ? "-1" : parentId, portalGridDefine.isPagination(), false);
            } // end if

        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * @param con
     * @param sqlDefine
     * @param params
     * @param json
     * @param page
     * @param recordPerPage
     * @param includeCheckBox
     * @param parent_id
     * @throws SQLException
     * @throws DBException
     * @throws JSONException
     */
    public void getGridData4Flex(final Connection con, final SQLDefine4Portal sqlDefine, final Map<String, String> params, final JSONObject json, final int recordPage, final int recordPerPage, final boolean includeCheckBox, final String parent_id, boolean isPagination, boolean isSkipProcessURL) throws Exception {
        IDBCommand rcmd = null;
        IDBCommand pcmd = null;
        IDBCommand idCmd = null;
        IDBCommand clearCmd = null;
        boolean isOptimizePage = sqlDefine.isSupportPageOptimize();
        try {
            long threadId = Thread.currentThread().getId();
            String sessionId = params.get("SESSIONID");
            if (isPagination) {
                rcmd = sqlDefine.getRCCmd(con, params, params);
                pcmd = sqlDefine.getPCmd(con, params, params);

                if (isOptimizePage) {
                    idCmd = sqlDefine.getPageIdsCmd(con, params);
                    idCmd.setParam(1, threadId);
                    idCmd.setParam(2, sessionId);

                    clearCmd = DBHELPER.getCommand(con, "delete from sys_utils_page_optimize where thread_id=? and session_id=?");
                    clearCmd.setParam(1, threadId);
                    clearCmd.setParam(2, sessionId);
                }

            } else {
                pcmd = sqlDefine.getCmd(con, params);
            }
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                if (StringUtils.equalsIgnoreCase(param.getInitSQLName(), "TREE")) {
                    if (StringUtils.equals(param.getInitField(), "__CHKIDS__")) {
                        if (isPagination) {
                            rcmd.setParam(param.getName(), params.get("p_id_s"));
                            if (isOptimizePage)
                                idCmd.setParam(param.getName(), params.get("p_id_s"));
                        }
                        pcmd.setParam(param.getName(), params.get("p_id_s"));
                    } else {
                        if (isPagination) {
                            rcmd.setParam(param.getName(), parent_id);
                            if (isOptimizePage)
                                idCmd.setParam(param.getName(), parent_id);
                        }
                        pcmd.setParam(param.getName(), parent_id);
                    }
                } else {
                    final String paramValue = getSQLDefineParamValue(param.getInitValue(), params, param);
                    if (isPagination) {
                        SQLParamUtils.setParam(rcmd, param, paramValue);
                        if (isOptimizePage)
                            SQLParamUtils.setParam(idCmd, param, paramValue);
                    }
                    SQLParamUtils.setParam(pcmd, param, paramValue);
                }
            }

            int recordTotal = 2500;
            if (isPagination) {
                rcmd.executeQuery();
                rcmd.next();
                recordTotal = rcmd.getInt(1);
            }

            final int totalPage = (recordTotal % recordPerPage) == 0 ? recordTotal / recordPerPage : (recordTotal / recordPerPage) + 1;
            int page = recordPage;
            if (page > totalPage)
                page = totalPage;
            if (page == 0)
                page = 1;

            if (isPagination) {
                if (isOptimizePage) {
                    idCmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                    idCmd.setParam("E", new Long(page * recordPerPage));
                    idCmd.setParam("R", recordPerPage);
                    idCmd.execute();

                    pcmd.setParam("thread_id", threadId);
                    pcmd.setParam("session_id", sessionId);
                } else {
                    pcmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                    pcmd.setParam("E", new Long(page * recordPerPage));
                    pcmd.setParam("R", recordPerPage);
                }
            }

            final ArrayList<JSONObject> rowsList = new ArrayList<JSONObject>();
            final String keyFieldName = sqlDefine.getKeyFieldName();
            pcmd.executeQuery();
            final List<String> skipFields = new ArrayList<String>();
            skipFields.addAll(pcmd.getFieldNames());
            for (int i = 0; i < sqlDefine.getColumnCount(); i++) {
                final PortalGridColumn column = sqlDefine.getColumn(i);
                if (!StringUtils.isEmpty(column.getFieldName()))
                    skipFields.remove(column.getFieldName());
            }
            int totalRecod = 0;
            while (pcmd.next() && (totalRecod++) < 2500) {
                final JSONObject rowJSON = new JSONObject();
                final String rowid = pcmd.getString(keyFieldName);
                rowJSON.put("__key__", rowid);
                if (includeCheckBox)
                    rowJSON.put("__chk__", false);
                for (int i = 0; i < sqlDefine.getColumnCount(); i++) {
                    final PortalGridColumn column = sqlDefine.getColumn(i);
                    if (StringUtils.isEmpty(column.getFieldName()))
                        rowJSON.put("__F" + i + "__", getGridColumnData(pcmd, sqlDefine.getColumn(i), isSkipProcessURL));
                    else
                        rowJSON.put(column.getFieldName(), getGridColumnData(pcmd, sqlDefine.getColumn(i), isSkipProcessURL));
                }

                for (final String fieldName : skipFields)
                    rowJSON.put(fieldName, pcmd.getString(fieldName));

                rowsList.add(rowJSON);
            }
            json.put("rows", rowsList);
            json.put("t", totalPage);
            json.put("tr", recordTotal);
            json.put("p", page);

            if (isPagination && isOptimizePage)
                clearCmd.execute();
        } finally {
            DBHELPER.closeCommand(rcmd);
            DBHELPER.closeCommand(pcmd);
            DBHELPER.closeCommand(idCmd);
            DBHELPER.closeCommand(clearCmd);
        }
    }

    /**
     * 获取SQLDefine的参数值列表
     * 
     * @param sqlDefine
     * @param params
     * @return
     */
    public Map<String, Object> getSqlDefineParamValues(final SQLDefine4Portal sqlDefine, final Map<String, String> params) {
        final Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < sqlDefine.getParamCount(); i++) {
            final SQLParam4Portal param = sqlDefine.getParam(i);
            final String paramValue = getSQLDefineParamValue(param.getInitValue(), params, param);
            result.put(param.getName(), paramValue);
        }
        return result;
    }

    /**
     * 取得列表值
     * 
     * @param con
     * @param portal_id
     * @param parent_id
     * @param httpRequestParams
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     */
    public JSONObject getGridJSON4Flex(final Connection con, final long portal_id, final long parent_id, final Map<String, String> httpRequestParams) throws Exception, DocumentException {
        JSONObject json = new JSONObject();
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, tempCon);
            if (portalGridDefine.getLayout().isGridView())
                json = getGridData4Flex(tempCon, portalGridDefine, Long.toString(parent_id), httpRequestParams);
            json.put("r", true);
        } finally {

            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 生成树状视图的数据
     * 
     * @param con
     * @param portal_id
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     */
    public JSONObject getTreeData4Flex(final Connection con, final long portal_id, final Map<String, String> params) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        try {
            if (tempCon != null)
                tempCon = DBHELPER.getConnection();
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, tempCon);
            json.put("records", getTreeData4Flex(con, portalGridDefine, params));
            json.put("r", true);
        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 获取Tree列表数据
     * 
     * @param con
     * @param portalID
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONArray getTreeData4Flex(final Connection con, final PortalGridDefine portalGridDefine, final Map<String, String> params) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {

            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final SQLDefine4Portal sqlDefine = portalGridDefine.getTreeSQLDefine();
            final Map<String, String> paramName2Value = new HashMap<String, String>();
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                if (!StringUtils.isEmpty(param.getInitValue())) {
                    final String paramValue = (StringUtils.equalsIgnoreCase(params.get("o"), "dynamicLoadTreeData") && StringUtils.equalsIgnoreCase("TREE", param.getInitSQLName()) && StringUtils.equalsIgnoreCase("ID", param.getInitField())) ? params.get("p_id") : getSQLDefineParamValue(param.getInitValue(), params, param);
                    paramName2Value.put(param.getName(), paramValue);
                    paramName2Value.put(param.getInitField(), paramValue);
                }
            }

            final String keyField = sqlDefine.getKeyFieldName();
            final String captionField = sqlDefine.getCaptionFieldName();

            boolean recircle = false;
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                if (StringUtils.equals(param.getInitSQLName(), "TREE") && StringUtils.equals(param.getInitField(), sqlDefine.getKeyFieldName())) {
                    recircle = true;
                    break;
                }

            }
            cmd = sqlDefine.getCmd(tempCon, params);

            final JSONObject rootJson = new JSONObject();
            rootJson.put("id", getTreeRootID(portalGridDefine, params, tempCon));
            rootJson.put("label", portalGridDefine.getPortalName());
            rootJson.put("iconURL", "../images/18x18/root.png");
            final JSONArray nodesArray = new JSONArray();
            rootJson.put("children", nodesArray);
            jsonArray.add(rootJson);
            final int level = 0;
            getTreeData4Flex(cmd, nodesArray, sqlDefine, paramName2Value, keyField, captionField, params, recircle, portalGridDefine.getFolderGif(), portalGridDefine, level);

        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return jsonArray;
    }

    /**
     * 生成目录树数据
     * 
     * @param cmd
     * @param jsonArray
     * @param sqlDefine
     * @param paramName2Value
     * @param keyField
     * @param captionField
     * @param params
     * @param recircle
     * @param folderGif
     * @param level
     * @param portalGridDefine
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private void getTreeData4Flex(final IDBCommand cmd, final JSONArray jsonArray, final SQLDefineBase sqlDefine, final Map<String, String> paramName2Value, final String keyField, final String captionField, final Map<String, String> params, final boolean recircle, final String folderGif, final PortalGridDefine portalGridDefine, final int nodeLevel) throws Exception {
        for (int i = 0; i < sqlDefine.getParamCount(); i++) {
            final SQLParam4Portal param = sqlDefine.getParam(i);
            final String paramValue = paramName2Value.get(param.getName());
            SQLParamUtils.setParam(cmd, param, paramValue);
        }
        cmd.execute();

        if (cmd.executeQuery()) {
            final ArrayList<JSONObject> elementList = new ArrayList<JSONObject>();
            final ArrayList<Map<String, String>> element2ParamValues = new ArrayList<Map<String, String>>();
            final ArrayList<String> cmdFieldList = cmd.getFieldNames();
            final Map<String, String> field2Value = new HashMap<String, String>();
            while (cmd.next()) {
                field2Value.clear();
                for (int i = 0; i < cmdFieldList.size(); i++)
                    field2Value.put(cmdFieldList.get(i), cmd.getString(i + 1));
                final JSONObject itemJson = new JSONObject();
                itemJson.put("label", field2Value.get(captionField));
                itemJson.put("id", field2Value.get(keyField));
                itemJson.put("iconURL", "../images/18x18/" + folderGif);
                for (String fieldName : portalGridDefine.getTreeSQLDefine().getExtFields()) {
                    itemJson.put(fieldName, field2Value.get(fieldName));
                }
                jsonArray.add(itemJson);
                final Map<String, String> param2value = new HashMap<String, String>();
                for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                    final SQLParam4Portal param = sqlDefine.getParam(i);
                    if (!StringUtils.isEmpty(param.getInitField()))
                        param2value.put(param.getName(), field2Value.get(param.getInitField()));
                    else
                        param2value.put(param.getName(), getSQLDefineParamValue(param.getInitValue(), params, param));
                }
                elementList.add(itemJson);
                element2ParamValues.add(param2value);
            }
            int level = nodeLevel;
            if (recircle && (!portalGridDefine.getLayout().isAsyncTreeData() || (level < 1))) {
                level++;
                for (int i = 0; i < elementList.size(); i++) {
                    final JSONArray childArray = new JSONArray();
                    getTreeData4Flex(cmd, childArray, sqlDefine, element2ParamValues.get(i), keyField, captionField, params, recircle, folderGif, portalGridDefine, level);
                    if (childArray.size() != 0) {
                        elementList.get(i).put("__loaded__", true);
                        elementList.get(i).put("children", childArray);
                    }
                }

            }
        }
    }

    /**
     * 交换数据列表排列顺序
     * 
     * @param paramInt
     * @param paramInt2
     * @param paramInt3
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws DBException
     */
    public JSONObject exchangeGridRowSortorder(final long portal_id, final long id1, final long id2) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand cmd = null;
        try {
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
            final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_EXCHANGE_GRID);
            if (sqlDefine == null) {
                json.put("r", false);
                json.put("msg", "服务器断尚未定义调整数据列表顺序的SQL语句,操作失败！");
            } else {
                con = DBHELPER.getConnection();
                cmd = sqlDefine.getCmd(con, null);
                cmd.setParam(sqlDefine.getParam(0).getName(), id1);
                cmd.setParam(sqlDefine.getParam(1).getName(), id2);
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
     * 交换树节点排列顺序
     * 
     * @param portal_id
     * @param id1
     * @param id2
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws DBException
     */
    public JSONObject exchangeTreeNodeSortorder(final long portal_id, final long id1, final long id2) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand cmd = null;
        try {
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
            final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_EXCHANGE_TREE);
            if (sqlDefine == null) {
                json.put("r", false);
                json.put("msg", "服务器断尚未定义调整数据列表顺序的SQL语句,操作失败！");
            } else {
                con = DBHELPER.getConnection();
                cmd = sqlDefine.getCmd(con, null);
                cmd.setParam(sqlDefine.getParam(0).getName(), id1);
                cmd.setParam(sqlDefine.getParam(1).getName(), id2);
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
     * 删除目录树节点
     * 
     * @param portal_id
     * @param ids
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     */
    public JSONObject deleteTreeNode(final long portal_id, final long[] ids, final Map<String, String> params) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand cmd = null;
        try {
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
            final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_DELETE_TREE);
            if (sqlDefine == null) {
                json.put("r", false);
                json.put("msg", "服务器断尚未定义删除数据列表的SQL语句,操作失败！");
            } else {
                con = DBHELPER.getConnection();
                cmd = sqlDefine.getCmd(con, null);
                for (final long id : ids) {
                    for (int j = 0; j < sqlDefine.getParamCount(); j++) {
                        final SQLParam4Portal param = sqlDefine.getParam(j);
                        if (StringUtils.equals(param.getInitSQLName(), "REQ"))
                            cmd.setParam(param.getName(), params.get(param.getInitField()));
                        else if (StringUtils.equals(param.getInitSQLName(), "TREE"))
                            cmd.setParam(param.getName(), id);
                    }
                    cmd.execute();
                }
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 删除Grid中的数据
     * 
     * @param object
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject deleteGridJSON4Flex(final Connection con, final Map<String, String> params) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(Convert.str2Int(params.get("id")), con);
            final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_DELETE_GRID);
            cmd = sqlDefine.getCmd(tempCon, null);
            final String[] ids = params.get("ids").split(",");
            for (final String id : ids) {
                for (int j = 0; j < sqlDefine.getParamCount(); j++) {
                    final SQLParam4Portal param = sqlDefine.getParam(j);
                    if (StringUtils.equals(param.getInitSQLName(), "REQ"))
                        cmd.setParam(param.getName(), params.get(param.getInitField()));
                    else if (StringUtils.equals(param.getInitSQLName(), "GRID"))
                        cmd.setParam(param.getName(), id);
                }
                cmd.execute();
            }
            if (Convert.str2Boolean(params.get("reloaddata")))
                json.put("data", getGridData4Flex(tempCon, portalGridDefine, params.get("p_id"), params));
            json.put("r", true);

        } finally {
            DBHELPER.closeCommand(cmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 删除列表行
     * 
     * @param portalID
     * @param ids
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject deleteGridRow(final long portal_id, final long[] ids) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand cmd = null;
        try {
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
            final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_DELETE_GRID);
            if (sqlDefine == null) {
                json.put("r", false);
                json.put("msg", "服务器断尚未定义删除数据列表的SQL语句,操作失败！");
            } else {
                con = DBHELPER.getConnection();
                cmd = sqlDefine.getCmd(con, null);
                for (final long id : ids) {
                    cmd.setParam("id", id);
                    cmd.execute();
                }
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 得到ComboBox类型参数对象列表
     * 
     * @param portal_id
     * @return
     * @throws JSONException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     */
    public JSONObject getGridComboBoxParamItems(final Connection con, final long portal_id, final Map<String, String> httpParams) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();

        final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
        if (portalGridDefine.getLayout().isGridView()) {
            final SQLDefineBase sqlDefine = portalGridDefine.getGridSQLDefine();
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                if (StringUtils.indexOf(param.getFilterControl(), "ComboBox") != -1) {
                    JSONArray itemJSON = new JSONArray();
                    itemJSON.add("");
                    itemJSON.add("");
                    JSONUtils.append(json, param.getName(), itemJSON);
                    // json.append(param.getName(), itemJSON);
                    if (param.isFormDB() && !StringUtils.isEmpty(param.getComboBoxDB())) {
                        final SQLDefineBase paramSQLDefien = portalGridDefine.getSQLDefineByName(param.getComboBoxDB());
                        if (paramSQLDefien != null) {
                            IDBCommand cmd = null;
                            try {
                                cmd = paramSQLDefien.getCmd(con, httpParams);
                                for (int j = 0; j < paramSQLDefien.getParamCount(); j++) {
                                    final SQLParam4Portal param4CB = paramSQLDefien.getParam(j);
                                    final String paramValue = httpParams.get(param4CB.getInitValue());
                                    SQLParamUtils.setParam(cmd, param4CB, paramValue);
                                }
                                cmd.executeQuery();
                                while (cmd.next()) {
                                    itemJSON = new JSONArray();
                                    itemJSON.add(cmd.getString(1));
                                    itemJSON.add(cmd.getString(2));
                                    JSONUtils.append(json, param.getName(), itemJSON);
                                    // json.append(param.getName(), itemJSON);
                                }
                            } finally {
                                DBHELPER.closeCommand(cmd);
                            }
                            if (json.getJSONArray(param.getName()).size() == 2)
                                json.getJSONArray(param.getName()).remove(0);
                        }

                    } else if (!param.isFormDB() && !StringUtils.isEmpty(param.getComboBoxItems())) {
                        final String[] items = param.getComboBoxItems().split("\\|");
                        for (final String item : items) {
                            itemJSON = new JSONArray();
                            itemJSON.add(StringUtils.substringBefore(item, "="));
                            itemJSON.add(StringUtils.substringAfter(item, "="));
                            JSONUtils.append(json, param.getName(), itemJSON);
                            // json.append(param.getName(), itemJSON);
                        }
                    }
                }
            }
        }
        return json;
    }

    /**
     * 得到一个树状节点的节点数据
     * 
     * @param con
     * @param portalID
     * @param parentTreeNodeID
     * @param params
     * @param portalTreeNodeID
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject getTreeNodeJSON4Flex(final Connection con, final long portal_id, final long parentTreeNodeID, final Map<String, String> httpRequestParams, final long portalTreeNodeID) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        IDBCommand singleCmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, tempCon);
            final SQLDefine4Portal sqlDefine = portalGridDefine.getTreeSQLDefine();

            singleCmd = sqlDefine.getSingleCmd(con, httpRequestParams);
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                if (StringUtils.equalsIgnoreCase(param.getInitSQLName(), "TREE"))
                    SQLParamUtils.setParam(singleCmd, param, Long.toString(parentTreeNodeID));
                else {
                    final String paramValue = getSQLDefineParamValue(param.getInitValue(), httpRequestParams, param);
                    SQLParamUtils.setParam(singleCmd, param, paramValue);
                }
            }
            singleCmd.setParam("K", portalTreeNodeID);

            if (singleCmd.executeQuery())
                while (singleCmd.next()) {
                    json.put("label", singleCmd.getString(sqlDefine.getCaptionFieldName()));
                    json.put("id", singleCmd.getString(sqlDefine.getKeyFieldName()));
                    json.put("iconURL", "../images/18x18/" + portalGridDefine.getFolderGif());
                }
        } finally {
            DBHELPER.closeCommand(singleCmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 取得一条数据列表数据
     * 
     * @param con
     * @param portid
     * @param parentNodeID
     * @param params
     * @param recordID
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject getGridItemJSON4Flex(final Connection con, final long portal_id, final long parent_id, final Map<String, String> params, final long recordID) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        IDBCommand singleCmd = null;

        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, tempCon);
            final SQLDefine4Portal sqlDefine = portalGridDefine.getGridSQLDefine();

            singleCmd = sqlDefine.getSingleCmd(tempCon, params);
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                if (StringUtils.equalsIgnoreCase(param.getInitSQLName(), "TREE")) {
                    if (StringUtils.equals(param.getInitField(), "__CHKIDS__"))
                        SQLParamUtils.setParam(singleCmd, param, params.get("p_id_s"));
                    else
                        SQLParamUtils.setParam(singleCmd, param, Long.toString(parent_id));
                } else {
                    final String paramValue = getSQLDefineParamValue(param.getInitValue(), params, param);
                    SQLParamUtils.setParam(singleCmd, param, paramValue);
                }
            }
            singleCmd.setParam("K", recordID);

            final String keyFieldName = sqlDefine.getKeyFieldName();
            singleCmd.executeQuery();

            while (singleCmd.next()) {
                final String rowid = singleCmd.getString(keyFieldName);
                json.put("__key__", rowid);
                if (portalGridDefine.getLayout().isGridSupportCheckBox())
                    json.put("__chk__", false);
                for (int i = 0; i < sqlDefine.getColumnCount(); i++) {
                    final PortalGridColumn column = sqlDefine.getColumn(i);
                    if (StringUtils.isEmpty(column.getFieldName()))
                        json.put("__F" + i + "__", getGridColumnData(singleCmd, sqlDefine.getColumn(i), false));
                    else
                        json.put(column.getFieldName(), getGridColumnData(singleCmd, sqlDefine.getColumn(i), false));
                }
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(singleCmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 取得一条数据列表数据
     * 
     * @param con
     * @param portid
     * @param parentNodeID
     * @param params
     * @param recordID
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject getGridItemJSON4Flex(final Connection con, final SQLDefine4Portal sqlDefine, final Map<String, String> params, final long recordID) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        IDBCommand singleCmd = null;

        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();

            singleCmd = sqlDefine.getSingleCmd(tempCon, params);
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                if (StringUtils.equalsIgnoreCase(param.getInitSQLName(), "TREE")) {
                    if (StringUtils.equals(param.getInitField(), "__CHKIDS__"))
                        SQLParamUtils.setParam(singleCmd, param, params.get("p_id_s"));
                    else
                        SQLParamUtils.setParam(singleCmd, param, "-1");
                } else {
                    final String paramValue = getSQLDefineParamValue(param.getInitValue(), params, param);
                    SQLParamUtils.setParam(singleCmd, param, paramValue);
                }
            }
            singleCmd.setParam("K", recordID);

            final String keyFieldName = sqlDefine.getKeyFieldName();
            singleCmd.executeQuery();

            while (singleCmd.next()) {
                final String rowid = singleCmd.getString(keyFieldName);
                json.put("__key__", rowid);
                json.put("__chk__", false);
                for (int i = 0; i < sqlDefine.getColumnCount(); i++) {
                    final PortalGridColumn column = sqlDefine.getColumn(i);
                    if (StringUtils.isEmpty(column.getFieldName()))
                        json.put("__F" + i + "__", getGridColumnData(singleCmd, sqlDefine.getColumn(i), false));
                    else
                        json.put(column.getFieldName(), getGridColumnData(singleCmd, sqlDefine.getColumn(i), false));
                }
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(singleCmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 得到数据列表数据
     * 
     * @param portal_id
     * @param parent_id
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject getGridJSON(final Connection con, final long portal_id, final long parent_id, final Map<String, String> httpRequestParams) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        IDBCommand rcmd = null;
        IDBCommand pcmd = null;

        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, tempCon);
            if (portalGridDefine.getLayout().isGridView()) {
                final SQLDefine4Portal sqlDefine = portalGridDefine.getGridSQLDefine();
                rcmd = sqlDefine.getRCCmd(tempCon, httpRequestParams, httpRequestParams);
                pcmd = sqlDefine.getPCmd(tempCon, httpRequestParams, httpRequestParams);

                for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                    final SQLParam4Portal param = sqlDefine.getParam(i);
                    if (StringUtils.equalsIgnoreCase(param.getInitSQLName(), "TREE")) {
                        rcmd.setParam(param.getName(), parent_id);
                        pcmd.setParam(param.getName(), parent_id);
                    } else {
                        final String paramValue = getSQLDefineParamValue(param.getInitValue(), httpRequestParams, param);
                        SQLParamUtils.setParam(rcmd, param, paramValue);
                        SQLParamUtils.setParam(pcmd, param, paramValue);
                    }
                }

                rcmd.executeQuery();
                rcmd.next();
                final long recordTotal = rcmd.getInt(1);

                long page = Convert.try2Int(httpRequestParams.get("p"), 1);
                final long recordPerPage = portalGridDefine.getLayout().isGridPagination() ? Convert.try2Int(httpRequestParams.get("r"), 25) : 2500;
                final long totalPage = (recordTotal % recordPerPage) == 0 ? recordTotal / recordPerPage : (recordTotal / recordPerPage) + 1;

                if (page > totalPage)
                    page = totalPage;
                if (page == 0)
                    page = 1;

                pcmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                pcmd.setParam("E", new Long(page * recordPerPage));

                final ArrayList<JSONObject> rowsList = new ArrayList<JSONObject>();
                final String keyFieldName = sqlDefine.getKeyFieldName();
                if (pcmd.executeQuery())
                    while (pcmd.next()) {
                        final JSONObject rowJSON = new JSONObject();
                        final String rowid = pcmd.getString(keyFieldName);
                        rowJSON.put("id", rowid);
                        final ArrayList<Object> datas = new ArrayList<Object>();
                        //
                        if (portalGridDefine.getLayout().isGridSupportCheckBox())
                            datas.add("0");
                        for (int i = 0; i < sqlDefine.getColumnCount(); i++)
                            datas.add(getGridColumnData(pcmd, sqlDefine.getColumn(i), false));
                        rowJSON.put("data", datas);
                        rowsList.add(rowJSON);
                    }
                json.put("rows", rowsList);
                json.put("t", totalPage);
                json.put("tr", recordTotal);
                json.put("p", page);
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(pcmd);
            DBHELPER.closeCommand(rcmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 得到
     * 
     * @param cmd
     * @param column
     * @param rowid
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    /**
     * 得到
     * 
     * @param cmd
     * @param column
     * @param rowid
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private String getGridColumnData(final IDBCommand cmd, final PortalGridColumn column, boolean isSkipProcessURL) throws Exception {
        String result = "";
        if (column.getURLCount() == 0) {
            if (!StringUtils.isEmpty(column.getFieldName())) {
                final String dataType = cmd.getMetaData().getColumnTypeName(cmd.getFieldNames().indexOf(column.getFieldName()) + 1);
                if (RuntimeContext.getDbEntryService().getDBFieldDataTypeByTypeString(dataType).getDataTypeCategory() == DBFieldDataType.categoryBinary)
                    result = Convert.bytes2Str(cmd.getBytes(column.getFieldName()));
                else
                    result = cmd.getString(column.getFieldName());
            }
        } else if (isSkipProcessURL) {
            if (column.getURLCount() == 1) {
                final SQLColumnURL url = column.getURL(0);
                String hrefCaption = url.getLabel();
                if (StringUtils.isEmpty(hrefCaption) && !StringUtils.isEmpty(column.getFieldName()))
                    result = cmd.getString(column.getFieldName());
            }
        } else {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < column.getURLCount(); i++) {
                final SQLColumnURL url = column.getURL(i);
                String hrefCaption = url.getLabel();
                if (StringUtils.isEmpty(hrefCaption) && !StringUtils.isEmpty(column.getFieldName()))
                    hrefCaption = cmd.getString(column.getFieldName());
                if (!StringUtils.isEmpty(hrefCaption)) {
                    String jsFun = "";
                    if (url.isFunction()) {
                        if ((url.getJson() != null) && url.getJson().containsKey("URL"))
                            jsFun = url.getJson().getString("URL");
                    } else
                        jsFun = "FUNGrid_" + url.getType();
                    if (StringUtils.endsWith(jsFun, "IS_A_HREF_COLUMN"))
                        sb.append(StringUtils.replace(hrefCaption, "event:", ""));
                    else if (!StringUtils.isEmpty(jsFun))
                        sb.append("<a href='" + jsFun + "'>" + StringEscapeUtils.escapeXml10(hrefCaption) + "</a>");
                }
            }
            result = sb.toString();
        }
        return result;
    }

    /**
     * 得到定义树的跟节点ID
     * 
     * @param portalD
     * @param httpRequestParams
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public String getTreeRootID(final PortalGridDefine portalGridDefine, final Map<String, String> httpRequestParams, final Connection con) throws Exception {
        String result = "-65535";
        final SQLDefine4Portal treeRootSQLDefine = (SQLDefine4Portal) portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_TREE_ROOT);
        if (treeRootSQLDefine == null) {
            if (portalGridDefine.getLayout().isTreeView()) {
                final SQLDefineBase sqlDefine = portalGridDefine.getTreeSQLDefine();
                final Map<String, String> paramName2Value = new HashMap<String, String>();
                for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                    final SQLParam4Portal param = sqlDefine.getParam(i);
                    if (!StringUtils.isEmpty(param.getInitValue())) {
                        paramName2Value.put(param.getName(), getSQLDefineParamValue(param.getInitValue(), httpRequestParams, param));
                        paramName2Value.put(param.getInitField(), getSQLDefineParamValue(param.getInitValue(), httpRequestParams, param));
                    }
                }
                final String keyField = sqlDefine.getKeyFieldName();
                if (paramName2Value.containsKey(keyField))
                    result = paramName2Value.get(keyField).toString();
            }
        } else {
            IDBCommand cmd = null;
            try {
                cmd = treeRootSQLDefine.getCmd(con, httpRequestParams);
                for (int i = 0; i < treeRootSQLDefine.getParamCount(); i++) {
                    final SQLParam4Portal param = treeRootSQLDefine.getParam(i);
                    SQLParamUtils.setParam(cmd, param, getSQLDefineParamValue(param.getInitValue(), httpRequestParams, param));
                }
                if (cmd.executeQuery() && cmd.next())
                    result = cmd.getString(1);
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        }
        return result;
    }

    /**
     * 得到定义树的跟节点ID
     * 
     * @param portalD
     * @param httpRequestParams
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public String getTreeRootID(final long portalID, final Map<String, String> httpRequestParams, final Connection con) throws Exception, DocumentException {
        Connection tempCon = con;
        String result = "-65535";
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final PortalGridDefine portalGridDefine = (PortalGridDefine) RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalID, tempCon);
            if (portalGridDefine.getLayout().isTreeView())
                result = getTreeRootID(portalGridDefine, httpRequestParams, tempCon);
        } finally {
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 得到初始参数值
     * 
     * @param initValue
     * @param httpRequestParams
     * @param isFilter
     * @param name
     * @return
     */
    private String getSQLDefineParamValue(final String initValue, final Map<String, String> httpRequestParams, final SQLParam4Portal param) {
        if (param.isFilter()) {
            final String str = httpRequestParams.get(param.getName());
            if (StringUtils.isEmpty(str))
                return httpRequestParams.get(initValue);
            else if ((param.getDataType() == DBParamDataType.String) && param.isPartMatch())
                return "%" + str + "%";
            else
                return str;
        } else if (httpRequestParams.containsKey(initValue))
            return httpRequestParams.get(initValue);
        else if (StringUtils.startsWith(initValue, "REQ."))
            return httpRequestParams.get(StringUtils.substringAfter(initValue, "REQ."));
        else
            return param.getInitValue();
    }

    /**
     * 批量导入Excel
     * 
     * @param portalID
     * @param dataJSON
     * @param param2Col
     * @param requestParams
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONObject importExcel(final long portal_id, final JSONObject dataJSON, JSONObject param2Col, final Map<String, String> requestParams) throws Exception, DocumentException {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        String extParamsStr = requestParams.get("params");
        JSONObject extParams = StringUtils.isEmpty(extParamsStr) ? new JSONObject() : JSONUtils.parserJSONObject(extParamsStr);
        try {
            final JSONArray executesJSON = dataJSON.getJSONArray("items");
            con = DBHELPER.getConnection();
            final AbstractPortalGridDefine portalGridDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);

            for (int i = 0; i < executesJSON.size(); i++) {
                final JSONObject executeJSON = executesJSON.getJSONObject(i);
                final JSONArray columns = executeJSON.getJSONArray("columns");
                final String dataSet = executeJSON.getString("dataset");
                final ArrayList<String> columnsList = new ArrayList<String>();
                for (int j = 0; j < columns.size(); j++)
                    columnsList.add(columns.getString(j));

                final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByName(dataSet);
                cmd = sqlDefine.getCmd(con, null);

                final JSONArray datas = executeJSON.getJSONArray("datas");
                Map<String, Integer> paramName2ColumnIndex = new HashMap<String, Integer>();
                for (int m = 0; m < sqlDefine.getParamCount(); m++) {
                    final SQLParam param = sqlDefine.getParam(m);
                    final String paramName = param.getName();
                    int index = -1;
                    if (param2Col.containsKey(paramName))
                        index = columnsList.indexOf(param2Col.get(paramName));
                    paramName2ColumnIndex.put(paramName, index);
                }

                for (int j = 0; j < datas.size(); j++) {
                    final JSONArray data = datas.getJSONArray(j);
                    for (int m = 0; m < sqlDefine.getParamCount(); m++) {
                        final SQLParam param = sqlDefine.getParam(m);
                        final String paramName = param.getName();
                        int index = paramName2ColumnIndex.get(paramName);
                        String paramValue = "";
                        if (index == -1)
                            paramValue = extParams.getString(paramName);
                        else
                            paramValue = data.getString(index);
                        // cmd.setParam(paramName, paramValue);
                        SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
                    }
                    cmd.execute();
                }
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 根据模版定义生成JSON文件
     * 
     * @param exportDefineJSON
     * @param int1
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DocumentException
     */
    public JSONArray exportData4ExportExcel(final JSONObject exportDefineJSON, final long int1, final Map<String, String> params) throws Exception, DocumentException {

        final JSONArray datas = new JSONArray();
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            final long portalID = exportDefineJSON.getInt("portalID");
            final AbstractPortalGridDefine portalGridDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portalID, con);
            final JSONArray executes = exportDefineJSON.getJSONArray("execute");
            for (int i = 0; i < executes.size(); i++) {
                final JSONObject execute = executes.getJSONObject(i);
                final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByName(execute.getString("datasetName"));
                if (cmd != null) {
                    cmd.close();
                    cmd = null;
                }
                cmd = sqlDefine.getCmd(con, params);
                for (int j = 0; j < sqlDefine.getParamCount(); j++) {
                    final SQLParam sqlParam = sqlDefine.getParam(j);
                    String paramValue = params.get(sqlParam.getName());
                    if ((sqlParam.getDataType() == DBParamDataType.String) && sqlParam.isPartMatch())
                        paramValue = "%" + paramValue + "%";
                    cmd.setParam(sqlParam.getName(), paramValue);
                }
                cmd.executeQuery();

                final JSONArray columns = execute.getJSONArray("fields");
                while (cmd.next()) {
                    final JSONArray data = new JSONArray();
                    for (int m = 0; m < columns.size(); m++)
                        if (columns.get(m) != null)
                            data.add(cmd.getString(columns.getString(m)));
                    datas.add(data);
                }

            }
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return datas;
    }

    private DataService4Portal() {
    }

    public static DataService4Portal getInstance() {
        return INSTANCE;
    }

}
