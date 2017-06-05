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
 * �������־��Դ洢����
 * 
 * @author ShengHongL
 */
public interface IWFStorage {

    /**
     * ��ȡ���й���������
     * 
     * @throws Exception
     */
    public void loadAllRunableProcess() throws Exception;

    /**
     * ��ȡ���й���������
     */
    public List<WFProcess> getAllProcess();

    /**
     * ��ȡ���������̶���
     * 
     * @param id
     * @return
     * @throws DBException
     * @throws SQLException
     */
    public abstract WFProcess getProcess(long id) throws Exception;

    /**
     * ֪ͨ�����Ѿ������仯
     * 
     * @param id
     * @param isDeleted
     * @param con
     * @throws Exception
     */
    public abstract void notifyWFProcessChange(long id, boolean isDeleted, Connection con) throws Exception;

    /**
     * ��ȡ������������
     * 
     * @return
     * @throws Exception
     */
    public abstract JSONObject getWFProcessTreeJSON() throws Exception;

    /**
     * �½�һ��case
     * 
     * @param user_id
     * @param process_type_id
     * @param ui_id
     * @return
     * @throws Exception
     */
    public abstract JSONObject createCase(long process_type_id, long ui_id) throws Exception;

    /**
     * ��ȡcase�б�
     * 
     * @param departmentId
     * @param ui_id
     * @param params
     * @return
     * @throws Exception
     */
    public abstract JSONObject getProcessList(long user_id, long ui_id, Map<String, String> params) throws Exception;

    /**
     * �ָ�ҵ��
     * 
     * @param user_id
     * @param step_ids
     * @return
     * @throws Exception
     */
    public abstract JSONObject restoreProcessByStep(long[] step_ids) throws Exception;

    /**
     * ��ȡ����������Ϣ
     * 
     * @param stepId
     * @return
     * @throws Exception
     */
    public abstract JSONObject getCaseStepFormInfo(long stepId, boolean isSkipForms) throws Exception;

    /**
     * ���������ֵ
     * 
     * @param processTypeId
     * @param activityName
     * @param processId
     * @param step_id
     * @param con
     */
    public abstract void calcActivityVariables(String processTypeId, String activityName, String processId, String step_id, Map<String, String> httpParams, Connection con) throws Exception;

    /**
     * ҵ�����̻��ڱ��Ƿ��б������
     * 
     * @param str2Long
     * @param activityName
     * @param savedFormIDS
     */
    public void flagActivityFormSaved(Connection con, Long str2Long, String activityName, List<Long> savedFormIDS, long user_id) throws Exception;

    /**
     * ��ȡ������Ϣ
     * 
     * @param step_id
     * @return
     * @throws Exception
     */
    public JSONObject getDiagram(long step_id) throws Exception;

    /**
     * ǩ�����
     * 
     * @param step_id
     * @param idea_content
     * @return
     * @throws Exception
     */
    JSONObject saveIdea(long step_id, String idea_content) throws Exception;

    /**
     * ��ȡ����б�
     * 
     * @param step_id
     * @return
     */
    public JSONObject getIdeas(long step_id) throws Exception;

    /**
     * ǩ�հ����б�
     * 
     * @param step_ids
     * @return
     * @throws Exception
     */
    public JSONObject signCaseList(long[] step_ids) throws Exception;

    public boolean isFormReadOnly(long processTypeID, String activityName, long formId) throws Exception;

    /**
     * �������Ͱ���
     * 
     * @param array
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject sendMultiProcessToNextActivity(JSONArray array, Map<String, String> params) throws Exception;

    /**
     * ���Ͱ���
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject sendProcessEx(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * ���Ͱ���
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject sendProcessSpecial(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * �˻ذ���
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject backProcessEx(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * �˼����ռ���
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject backProcessToCreator(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * �ϳ�����
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    public JSONObject abandoProcessEx(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception;

    /**
     * ��ȡ����������Ϣ
     * 
     * @param processOrStepId
     * @return
     * @throws Exception
     */
    public JSONObject getProcessMessage(long processOrStepId) throws Exception;

    /**
     * ���渽����Ϣ
     * 
     * @param processOrStepId
     * @param type
     * @param content
     * @return
     * @throws Exception
     */
    public JSONObject saveProcessMessage(long processOrStepId, int type, String content) throws Exception;

}
