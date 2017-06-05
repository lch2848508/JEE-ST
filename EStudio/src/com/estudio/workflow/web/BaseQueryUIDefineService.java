package com.estudio.workflow.web;

import java.sql.Connection;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.define.webclient.portal.PortalGridColumn;
import com.estudio.define.webclient.portal.SQLColumnURL;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.define.webclient.portal.SQLField;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.utils.JSONUtils;

public class BaseQueryUIDefineService {

    protected BaseQueryUIDefineService() {

    }

    /**
     * 解析Combobox附件参数
     * 
     * @param comboboxFilterControl2SQL
     * @param json
     */
    protected void parsetUIComboboxFilterExtendParams(final Map<String, String> comboboxFilterControl2SQL, final JSONObject json) {
        final JSONArray jsonArray = json.getJSONObject("dataset").getJSONArray("params");
        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject paramJson = jsonArray.getJSONObject(i);
            if (StringUtils.equals("ComboBox", paramJson.getString("filterControl"))) {
                final String paramName = paramJson.getString("name");
                final String sql = paramJson.getString("addition");
                if (!StringUtils.isEmpty(paramName) && !StringUtils.isEmpty(sql))
                    comboboxFilterControl2SQL.put(paramName, sql);
                paramJson.put("isExistsParent", StringUtils.containsIgnoreCase(sql, ":PARENT_COMBOBOX"));
            }
            paramJson.put("addition", null);
        }
    }

    /**
     * 解析界面定义
     * 
     * @param json
     * @return
     * @throws Exception
     */
    protected JSONObject parserUIDefine(final JSONObject json) throws Exception {
        final JSONArray paramsJSONArray = json.getJSONObject("dataset").getJSONArray("params");
        json.remove("dataset");
        final JSONArray array = json.getJSONArray("columns");
        for (int i = 0; i < array.size(); i++) {
            final JSONObject columnJson = array.getJSONObject(i);

            final String urlStr = columnJson.getString("url");
            if (!StringUtils.isEmpty(urlStr))
                columnJson.put("url", JSONUtils.parserJSONArray(urlStr));

            final String styleStr = columnJson.getString("style");
            if (!StringUtils.isEmpty(styleStr))
                columnJson.put("style", JSONUtils.parserJSONObject(styleStr));
        }

        for (int i = 0; i < paramsJSONArray.size(); i++) {
            final JSONObject paramJson = paramsJSONArray.getJSONObject(i);
            if (paramJson.getBoolean("isFilter") && !StringUtils.isEmpty(paramJson.getString("filterControl")))
                JSONUtils.append(json, "params", paramJson);
        }

        json.put("r", true);
        return json;
    }

    /**
     * 解析生成SQLDefine
     * 
     * @param con
     * 
     * @param jsonObject
     * @return
     * @throws Exception
     */
    protected SQLDefine4Portal parserSQLDefine(final JSONObject json, final JSONObject rootJson, final Connection con) throws Exception {
        final String SQL = json.getString("sql");
        return parserSQLDefineEx(json, rootJson, con, SQL, true);
    }

    /**
     * 解析JSON生成SQLDefine
     * 
     * @param json
     * @param rootJson
     * @param con
     * @param SQL
     * @param includeField
     * @return
     * @throws Exception
     */
    protected SQLDefine4Portal parserSQLDefineEx(final JSONObject json, final JSONObject rootJson, final Connection con, final String SQL, final boolean includeField) throws Exception {
        final SQLDefine4Portal result = new SQLDefine4Portal("", SQL, 0);

        JSONArray array = null;
        // 参数
        array = json.getJSONArray("params");
        for (int i = 0; i < array.size(); i++) {
            final JSONObject paramJson = array.getJSONObject(i);
            final boolean isWholeWord = paramJson.containsKey("wholeword") && paramJson.getBoolean("wholeword");
            int controlWidth = paramJson.containsKey("controlWidth") ? paramJson.getInt("controlWidth") : 80;
            if (controlWidth == 0)
                controlWidth = 80;
            result.addParam(new SQLParam4Portal(paramJson.getString("name"), paramJson.getString("comment"), DBParamDataType.fromInt(paramJson.getInt("intDataType")), "", paramJson.getString("init"), paramJson.getBoolean("isFilter"), paramJson.getString("filterControl"), "", false, "", "", isWholeWord, 1, paramJson.getBoolean("skipNull"), paramJson.getString("addition"), controlWidth));
        }
        result.initCommand(con);

        if (includeField) {
            // 字段
            array = json.getJSONArray("fields");
            for (int i = 0; i < array.size(); i++) {
                final JSONObject fieldJson = array.getJSONObject(i);
                result.addField(new SQLField(fieldJson.getString("name"), fieldJson.getString("comment"), false, fieldJson.getBoolean("primary"), "", true));
            }

            // 数据列
            array = rootJson.getJSONArray("columns");
            for (int i = 0; i < array.size(); i++) {
                final JSONObject columnJson = array.getJSONObject(i);
                String fieldName = StringUtils.substringBetween(columnJson.getString("name"), "[", "]");
                String fieldCaption = columnJson.getString("caption");
                final PortalGridColumn column = new PortalGridColumn(fieldName, fieldCaption);
                final String urlStr = columnJson.getString("url");
                if (!StringUtils.isEmpty(urlStr)) {
                    final JSONArray urlArray = JSONUtils.parserJSONArray(urlStr);
                    for (int j = 0; j < urlArray.size(); j++) {
                        final JSONObject urlJson = urlArray.getJSONObject(j);
                        final SQLColumnURL url = new SQLColumnURL(urlJson.getString("href"), "", urlJson.getString("display"));
                        column.addURL(url);
                    }
                }
                result.addColumn(column);
            }
            result.initPageAndCountCmd();
            result.initSingleCmd();
        }

        return result;
    }
}
