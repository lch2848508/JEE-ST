package com.estudio.workflow.web;

import java.sql.Connection;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.JSONUtils;

public final class WorkFlowUIDefineService extends BaseQueryUIDefineService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    private WorkFlowUIDefineService() {
        super();
    }

    private String getKey(final long id) {
        return "WorkFlowUIDefine-" + id;
    }

    /**
     * 获取工作流界面定义
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public WorkFlowUIDefine getUIDefine(final long id) throws Exception {
        final String cacheKey = getKey(id);
        WorkFlowUIDefine result = (WorkFlowUIDefine) SystemCacheManager.getInstance().getDesignObject(cacheKey);
        if (result == null) {
            result = getUIDefineFormDB(id);
            SystemCacheManager.getInstance().putDesignObject(cacheKey, result);
        }
        return result;
    }

    /**
     * 从数据库中获取工作流定义
     * 
     * @param id
     * @return
     * @throws Exception
     */
    private WorkFlowUIDefine getUIDefineFormDB(final long id) throws Exception {
        final Connection con = null;
        final WorkFlowUIDefine result = new WorkFlowUIDefine();
        try {
            JSONObject json = RuntimeContext.getObjectWorkFlowService().getWorkFlowDesignInfo(id, con);
            final String content = json.getString("content");
            if (!StringUtils.isEmpty(content)) {
                json = JSONUtils.parserJSONObject(content);
                // parser json 到对象
                result.setSqlDefine(parserSQLDefine(json.getJSONObject("dataset"), json, con));
                parsetUIComboboxFilterExtendParams(result.getComboboxFilterControl2SQL(), json);
                result.setUiDefine(parserUIDefine(json));
                result.setPagination(json.getJSONObject("options").getBoolean("isGridPageAble"));
            }
        } finally {
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 通知变化
     * 
     * @param id
     */
    public void notifyDesignInfoChange(final long id) {
        SystemCacheManager.getInstance().removeDesignObject(getKey(id));
    }

    private static final WorkFlowUIDefineService INSTANCE = new WorkFlowUIDefineService();

    public static WorkFlowUIDefineService getInstance() {
        return INSTANCE;
    }
}
