package com.estudio.impl.webclient.query;

import java.sql.Connection;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.webclient.query.QueryUIDefine;
import com.estudio.impl.db.DBSqlUtils;
import com.estudio.utils.JSCompress;
import com.estudio.utils.JSONUtils;
import com.estudio.workflow.web.BaseQueryUIDefineService;

public final class QueryUIDefineService extends BaseQueryUIDefineService {
    private QueryUIDefineService() {
        super();
    }

    private String getKey(final long id) {
        return "QueryUIDefine-" + id;
    }

    /**
     * 信息通知
     * 
     * @param id
     */
    public void notifyQueryUIDefineIsChanged(final long id) {
        SystemCacheManager.getInstance().removeDesignObject(getKey(id));
    }

    /**
     * 获取UI定义
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public QueryUIDefine getUIDefine(final Connection con, final long id) throws Exception {
        final String cacheKey = getKey(id);
        QueryUIDefine result = (QueryUIDefine) SystemCacheManager.getInstance().getDesignObject(cacheKey);
        if (result == null) {
            final JSONObject json = RuntimeContext.getObjectQueryService().get(con, id);
            if (json.getBoolean("r")) {
                final String content = json.getString("content");
                result = json2UIDefine(JSONUtils.parserJSONObject(content), con);
                SystemCacheManager.getInstance().putDesignObject(cacheKey, result);
            }
        }
        return result;
    }

    /**
     * JSON对象解析为UI
     * 
     * @param parseObject
     * @return
     * @throws Exception
     */
    private QueryUIDefine json2UIDefine(final JSONObject json, final Connection con) throws Exception {
        final QueryUIDefine result = new QueryUIDefine();
        final JSONObject datasetJson = json.getJSONObject("dataset");
        String SQL = datasetJson.getString("sql");
        final String initSQL = StringUtils.trim(DBSqlUtils.deleteComment(StringUtils.substringBetween(SQL, "/**BEGIN_INITIALIZE_SQL**/", "/**END_INITIALIZE_SQL**/")));
        final String cleanSQL = StringUtils.trim(DBSqlUtils.deleteComment(StringUtils.substringBetween(SQL, "/**BEGIN_CLEAN_SQL**/", "/**END_CLEAN_SQL**/")));
        SQL = StringUtils.replace(StringUtils.replace(SQL, initSQL, ""), cleanSQL, "");
        SQL = StringUtils.trim(DBSqlUtils.deleteComment(SQL));
        result.setSqlDefine(parserSQLDefineEx(datasetJson, json, con, SQL, true));
        if (!StringUtils.isEmpty(initSQL))
            result.setInitSQLDefine(parserSQLDefineEx(datasetJson, json, con, initSQL, false));
        if (!StringUtils.isEmpty(cleanSQL))
            result.setCleanSQLDefine(parserSQLDefineEx(datasetJson, json, con, cleanSQL, false));
        parsetUIComboboxFilterExtendParams(result.getComboboxFilterControl2SQL(), json);
        String compressJs = JSCompress.getInstance().compress(json.getString("js"));
        json.put("js", "");
        result.setJs(compressJs);
        if (json.containsKey("toolbar"))
            json.put("toolbar", JSONUtils.parserJSONObject(json.getString("toolbar")));
        result.setUiDefine(parserUIDefine(json));
        result.setPagination(json.getJSONObject("options").getBoolean("isGridPageAble"));

        return result;
    }

    private static final QueryUIDefineService INSTANCE = new QueryUIDefineService();

    public static QueryUIDefineService getInstance() {
        return INSTANCE;
    }

}
