package com.estudio.workflow.engine;

import java.sql.Connection;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.utils.ThreadUtils;
import com.estudio.workflow.storage.IWFStorage;
import com.estudio.workflow.utils.WFCalendarService;

public final class WFEngineer {

    private final IWFStorage storage = RuntimeContext.getWfStorage();
    private boolean isInited = false;

    public JSONObject getDiagram(final long step_id) throws Exception {
        return storage.getDiagram(step_id);
    }

    /**
     * ��ȡ����б�
     * 
     * @param step_id
     * @return
     */
    public JSONObject getIdeas(final long step_id) throws Exception {
        return storage.getIdeas(step_id);
    }

    /**
     * �������
     * 
     * @param paramLong
     * @return
     * @throws Exception
     */
    public JSONObject saveIdea(final long step_id, final String idea_content) throws Exception {
        return storage.saveIdea(step_id, idea_content);
    }

    /**
     * ���������
     * 
     * @param processTypeId
     * @param activityName
     * @param processId
     * @param step_id
     * @param con
     */
    public void calcActivityVariables(final String processTypeId, final String activityName, final String processId, final String step_id, final Map<String, String> httpParams, final Connection con) throws Exception {
        storage.calcActivityVariables(processTypeId, activityName, processId, step_id, httpParams, con);
    }

    /**
     * �½�һ��case
     * 
     * @param clientLoginInfo
     * @param process_type_id
     * @param ui_id
     * @return
     * @throws Exception
     */
    public JSONObject createProcess(final long process_type_id, final long ui_id) throws Exception {
        return storage.createCase(process_type_id, ui_id);
    }

    /**
     * ��ȡ�����б�
     * 
     * @param clientLoginInfo
     * @param paramLong
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject getProcessList(final long ui_id, final Map<String, String> params) throws Exception {
        return storage.getProcessList(GlobalContext.getLoginInfo().getId(), ui_id, params);
    }

    /**
     * ҵ��ָ�
     * 
     * @param clientLoginInfo
     * @param step_ids
     * @return
     * @throws Exception
     */
    public JSONObject restoreProcessByStep(final long[] step_ids) throws Exception {
        return storage.restoreProcessByStep(step_ids);
    }

    /**
     * ��ȡ�������������Ϣ
     * 
     * @param stepId
     * @return
     * @throws Exception
     */
    public JSONObject getCaseStepFormInfo(final long stepId, final boolean isSkipForms) throws Exception {
        return storage.getCaseStepFormInfo(stepId, isSkipForms);
    }

    /**
     * �������Ͱ���
     * 
     * @param parseObject
     * @param params
     * @return
     */
    public JSONObject batchSendCase(final JSONArray array, final Map<String, String> params) throws Exception {
        return storage.sendMultiProcessToNextActivity(array, params);
    }

    /**
     * ǩ�հ����б�
     * 
     * @param step_ids
     * @return
     * @throws Exception
     */
    public JSONObject signCaseList(final long[] step_ids) throws Exception {
        return storage.signCaseList(step_ids);
    }

    /**
     * �жϹ��������Ƿ�ֻ��
     * 
     * @param processTypeID
     * @param activityName
     * @param formId
     * @return
     * @throws Exception
     */
    public boolean isFormReadOnly(final long processTypeID, final String activityName, final long formId) throws Exception {
        return storage.isFormReadOnly(processTypeID, activityName, formId);
    }

    /**
     * ���캯��
     * 
     * @throws Exception
     */
    private WFEngineer() {

    }

    /**
     * ��ʼ��
     */
    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int times = 0;
                while (!isInited) {
                    try {
                        storage.loadAllRunableProcess(); // �洢����
                        WFCalendarService.getInstance().loadHolidaySetting(); // ���ڷ���
                        isInited = true;
                        times++;
                    } catch (final Exception e) {
                        if (++times > 5)
                            e.printStackTrace();
                        ThreadUtils.sleepMinute(1);
                    }
                }
            }
        }).start();
    }

    /**
     * ������ ����
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     */
    public JSONObject sendProcessEx(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        return storage.sendProcessEx(processStepInfos, httpContextParams);
    }

    public JSONObject sendProcessSpecial(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        return storage.sendProcessSpecial(processStepInfos, httpContextParams);
    }

    /**
     * ������ �˼�
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     */
    public JSONObject backProcessEx(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        return storage.backProcessEx(processStepInfos, httpContextParams);
    }

    public JSONObject backProcessToCreator(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        return storage.backProcessToCreator(processStepInfos, httpContextParams);
    }

    public JSONObject getProcessMessage(long processOrStepId) throws Exception {
        return storage.getProcessMessage(processOrStepId);
    }

    public JSONObject saveProcessMessage(long processOrStepId, int type, String content) throws Exception {
        return storage.saveProcessMessage(processOrStepId, type, content);
    }

    /**
     * ������ �ϳ�
     * 
     * @param processStepInfos
     * @param httpContextParams
     * @return
     */
    public JSONObject abandoProcessEx(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        return storage.abandoProcessEx(processStepInfos, httpContextParams);
    }

    public static final WFEngineer INSTANCE = new WFEngineer();

    public static WFEngineer getInstance() {
        return INSTANCE;
    }

}
