package com.estudio.web.servlet.webclient;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.BaseQueryUIDefine;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.define.webclient.query.QueryUIDefine;
import com.estudio.impl.webclient.query.QueryUIDefineService;
import com.estudio.intf.db.IDBHelper;
import com.estudio.officeservice.ExcelUtils;
import com.estudio.utils.Convert;
import com.estudio.web.service.DataService4AbstractPortal;
import com.estudio.web.service.DataService4Portal;
import com.estudio.web.service.DataService4PortalEx;
import com.estudio.web.servlet.BaseServlet;

public class QueryServlet extends BaseServlet {
    private static final String ISEXPORTEXCEL = "ISEXPORTEXCEL";

    private static final long serialVersionUID = 2329758129615866031L;

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getUIDefine", operation))
            response.getWriter().println(getQueryUIDefines(getParamLong("id"), getParams()));
        else if (StringUtils.equals("query", operation))
            response.getWriter().println(getQueryRecord(getParamLong("ui_id"), getParams()));
        else if (StringUtils.equals("exportExcel", operation))
            response.getWriter().println(exportQueryRecord2Excel());
        else if (StringUtils.equalsIgnoreCase(operation, "getQueryControlFilterComboboxItems"))
            response.getWriter().println(getAFilterComboboxItems(getParamLong("id"), getParamStr("paramName"), getParams()));

    }

    /**
     * 
     * @param id
     * @param paramName
     * @param httpParams
     * @return
     * @throws Exception
     */
    private JSONObject getAFilterComboboxItems(long id, String paramName, Map<String, String> httpParams) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final QueryUIDefine queryUIDefine = QueryUIDefineService.getInstance().getUIDefine(con, id);
            // 获取参数
            final SQLDefine4Portal sqlDefine = queryUIDefine.getSqlDefine();
            DataService4PortalEx.getInstance().getSQLDefine4PortalComboboxFilterItems(con, sqlDefine, paramName, httpParams, json);
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 导出查询结果到Excel
     * 
     * @return
     * @throws Exception
     */
    private JSONObject exportQueryRecord2Excel() throws Exception {
        final JSONObject result = new JSONObject();
        result.put("r", false);
        Map<String, String> httpParams = getParams();
        httpParams.put(ISEXPORTEXCEL, "1");
        final JSONObject recordJson = getQueryRecord(getParamLong("ui_id"), httpParams);
        if (recordJson.getBoolean("r")) {
            final BaseQueryUIDefine queryUIDefine = QueryUIDefineService.getInstance().getUIDefine(null, getParamLong("ui_id"));
            final SQLDefine4Portal sqlDefine = queryUIDefine.getSqlDefine();
            result.put("url", ExcelUtils.getInstance().createExcelBySQLDefine(sqlDefine, recordJson.getJSONArray("rows"), getParamStr("filterFields").split(";")));
            result.put("r", true);
        }
        return result;
    }

    /**
     * 查询数据
     * 
     * @param paramLong
     * @param params
     * @return
     * @throws Exception
     */
    private JSONObject getQueryRecord(final long id, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        boolean isSkipProcessURL = params.containsKey(ISEXPORTEXCEL) && StringUtils.equalsIgnoreCase(params.get(ISEXPORTEXCEL), "1");
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final QueryUIDefine queryUIDefine = QueryUIDefineService.getInstance().getUIDefine(con, id);

            // 获取参数
            final SQLDefine4Portal sqlDefine = queryUIDefine.getSqlDefine();
            final Map<String, Object> cmdParams = DataService4Portal.getInstance().getSqlDefineParamValues(sqlDefine, params);
            final Map<String, String> cmdStrParams = new HashMap<String, String>();
            for (final Map.Entry<String, Object> entry : cmdParams.entrySet())
                cmdStrParams.put(entry.getKey(), entry.getValue() != null ? String.valueOf(entry.getValue()) : null);

            // 初始化
            if (queryUIDefine.getInitSQLDefine() != null)
                DataService4AbstractPortal.getInstance().executeSQLDefine(con, queryUIDefine.getInitSQLDefine(), cmdStrParams);

            // 查询
            final int page = Convert.try2Int(params.get("p"), 1);
            final int recordPerPage = Convert.try2Int(params.get("r"), 2500);
            DataService4Portal.getInstance().getGridData4Flex(con, sqlDefine, params, json, page, recordPerPage, true, "-1", queryUIDefine.isPagination(), isSkipProcessURL);

            // 下拉框列表项
            // JSONObject filterControlItems = getFilterComboboxItems(con,
            // queryUIDefine,
            // cmdStrParams);
            // json.put("filterComboboxItems", filterControlItems);

            // 清理
            if (queryUIDefine.getCleanSQLDefine() != null)
                DataService4AbstractPortal.getInstance().executeSQLDefine(con, queryUIDefine.getCleanSQLDefine(), cmdStrParams);

            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 
     * @param con
     * @param queryUIDefine
     * @param cmdParams
     * @return
     * @throws Exception
     */
    private JSONObject getFilterComboboxItems(final Connection con, final QueryUIDefine queryUIDefine, final Map<String, String> cmdParams) throws Exception {
        final JSONObject filterControlItems = new JSONObject();
        final JSONObject blankJson = new JSONObject();
        for (final Map.Entry<String, String> entry : queryUIDefine.getComboboxFilterControl2SQL().entrySet()) {
            final String name = entry.getKey();
            final String sql = entry.getValue();
            final JSONArray items = DBHELPER.executeQuery(sql, mapStr2Object(cmdParams), con);
            blankJson.put("LABEL", " ");
            blankJson.put("ID", null);
            items.add(0, blankJson);
            filterControlItems.put(name, items);
        }
        return filterControlItems;
    }

    /**
     * 
     * @param strMap
     * @return
     */
    private Map<String, Object> mapStr2Object(final Map<String, String> strMap) {
        final Map<String, Object> result = new HashMap<String, Object>();
        for (final Map.Entry<String, String> entry : strMap.entrySet())
            result.put(entry.getKey(), entry.getValue());
        return result;
    }

    /**
     * 获取查询定义
     * 
     * @param paramLongs
     * @return
     * @throws Exception
     */
    private JSONObject getQueryUIDefines(final long id, final Map<String, String> cmdParams) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final QueryUIDefine ui = QueryUIDefineService.getInstance().getUIDefine(con, id);
            json.put("define", ui.getUiDefine());

            // 下拉框列表项
            cmdParams.put("PARENT_COMBOBOX", "-1");
            final JSONObject filterControlItems = getFilterComboboxItems(con, ui, cmdParams);
            json.put("filterComboboxItems", filterControlItems);

            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }
}
