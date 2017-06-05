package com.estudio.workflow.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.workflow.base.WFProcess;

/**
 * 工作流持久性存储对象
 * 
 * @author ShengHongL
 */
public interface IWFStorage {

    /**
     * 获取所有工作流对象
     * 
     * @throws Exception
     */
    public void loadAllRunableProcess() throws Exception;

    /**
     * 获取所有工作流对象
     */
    public List<WFProcess> getAllProcess();

    /**
     * 获取工作流流程对象
     * 
     * @param id
     * @return
     * @throws DBException
     * @throws SQLException
     */
    public abstract WFProcess getProcess(long id) throws Exception;

    /**
     * 通知数据已经发生变化
     * 
     * @param id
     * @param isDeleted
     * @param con
     * @throws Exception
     */
    public abstract void notifyWFProcessChange(long id, boolean isDeleted, Connection con) throws Exception;

    /**
     * 获取工作流对象树
     * 
     * @return
     * @throws Exception
     */
    public abstract JSONObject getWFProcessTreeJSON() throws Exception;

    /**
     * 新建一个case
     * 
     * @param user_id
     * @param process_type_id
     * @param ui_id
     * @return
     * @throws Exception
     */
    public abstract JSONObject createCase(long process_type_id, long ui_id) throws Exception;

    /**
     * 获取case列表
     * 
     * @param departmentId
     * @param ui_id
     * @param params
     * @return
     * @throws Exception
     */
    public abstract JSONObject getProcessList(long user_id, long ui_id, Map<String, String> params) throws Exception;

    /**
     * 恢复业务
     * 
     * @param user_id
     * @param step_ids
     * @return
     * @throws Exception
     */
    public abstract JSONObject restoreProcessByStep(long[] step_ids) throws Exception;

    /**
     * 获取工作流表单信息
     * 
     * @param stepId
     * @return
     * @throws Exception
     */
    public abstract JSONObject getCaseStepFormInfo(long stepId, boolean isSkipForms) throws Exception;

    /**
     * 计算活动体变量值
     * 
     * @param processTypeId
     * @param activityName
     * @param processId
     * @param step_id
     * @param con
     */
    public abstract void calcActivityVariables(String processTypeId, String activityName, String processId, String step_id, Map<String, String> httpParams, Connection con) throws Exception;

    /**
     * 业务流程环节表单是否有保存操作
     * 
     * @param str2Long
     * @param activityName
     * @param savedFormIDS
     */
    public void flagActivityFormSaved(Connection con, Long str2Long, String activityName, List<Long> savedFormIDS, long user_id) throws Exception;

    /**
     * 获取流程信息
     * 
     * @param step_id
     * @return
     * @throws Exception
     */
    public JSONObject getDiagram(long step_id) throws Exception;

    /**
     * 签署意见
     * 
     * @param step_id
     * @param idea_content
     * @return
     * @throws Exception
     */
    JSONObject saveIdea(long step_id, String idea_content) throws Exception;

    /**
     * 获取意见列表
     * 
     * @param step_id
     * @return
     */
    public JSONObject getIdeas(long step_id) throws Exception;

    /**
     * 签收案件列表
     * 
     * @param step_ids
     * @return
     * @throws Exception
     */
    public JSONObject signCaseList(long[] step_ids) throws Exception;

    public boolean isFormReadOnly(long processTypeID, String activityName, long formId) throws Exception;

    /**
     * 批量发送案件
     * 
     * @param array
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject sendMultiProcessToNextActivity(JSONArray array, Map<String, String> params) throws Exception;

    /**
     * 发送案件
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject sendProcessEx(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * 特送案件
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject sendProcessSpecial(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * 退回案件
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject backProcessEx(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * 退件到收件人
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject backProcessToCreator(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * 废除案件
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject abandoProcessEx(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * 获取案件督办信息
     * 
     * @param processOrStepId
     * @return
     * @throws Exception
     */
    public JSONObject getProcessMessage(long processOrStepId) throws Exception;

    /**
     * 保存附件信息
     * 
     * @param processOrStepId
     * @param type
     * @param content
     * @return
     * @throws Exception
     */
    public JSONObject saveProcessMessage(long processOrStepId, int type, String content) throws Exception;

}
