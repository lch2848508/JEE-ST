package com.estudio.workflow.web.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;
import com.estudio.workflow.engine.WFEngineer;
import com.estudio.workflow.storage.IWFStorage;
import com.estudio.workflow.web.WorkFlowUIDefineService;

public class WorkFlowRuntimeServlet extends BaseServlet {

    private static final long serialVersionUID = -1494360434798771714L;
    private static IWFStorage storage = RuntimeContext.getWfStorage();

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        JSONObject resultJson = null;
        if (StringUtils.equals("WORKFLOW_SEND_CASE", operation)) // 发送
            resultJson = WFEngineer.getInstance().sendProcessEx(JSONUtils.parserJSONArray(getParamStr("processStepInfos")), getParams());
        else if (StringUtils.equals("WORKFLOW_SPECIAL_SEND_CASE", operation))
            resultJson = WFEngineer.getInstance().sendProcessSpecial(JSONUtils.parserJSONArray(getParamStr("processStepInfos")), getParams());
        else if (StringUtils.equals("WORKFLOW_BACK_CASE", operation)) // 退回
            resultJson = WFEngineer.getInstance().backProcessEx(JSONUtils.parserJSONArray(getParamStr("processStepInfos")), getParams());
        else if (StringUtils.equals("WORKFLOW_BACK_CASE_TO_CREATOR", operation)) // 退回
            resultJson = WFEngineer.getInstance().backProcessToCreator(JSONUtils.parserJSONArray(getParamStr("processStepInfos")), getParams());
        else if (StringUtils.equals("WORKFLOW_ABANDON_CASE", operation)) // 废除
            resultJson = WFEngineer.getInstance().abandoProcessEx(JSONUtils.parserJSONArray(getParamStr("processStepInfos")), getParams());
        else if (StringUtils.equals("WORKFLOW_BATCH_SEND", operation))
            resultJson = WFEngineer.getInstance().batchSendCase(JSONUtils.parserJSONArray(getParamStr("datas")), getParams());
        else if (StringUtils.equals("signCases", operation))
            resultJson = WFEngineer.getInstance().signCaseList(getParamLongs("step_ids"));
        else if (StringUtils.equals("getUIDefine", operation))
            resultJson = WorkFlowUIDefineService.getInstance().getUIDefine(getParamLong("id")).getUiDefine();
        else if (StringUtils.equals("getCanCreateProcessList", operation))
            resultJson = transJsonToFlexJson(storage.getWFProcessTreeJSON());
        else if (StringUtils.equals("newCase", operation))
            resultJson = WFEngineer.getInstance().createProcess(getParamLong("process_type_id"), getParamLong("ui_id"));
        else if (StringUtils.equals("loadWorkFlowProcessList", operation))
            resultJson = WFEngineer.getInstance().getProcessList(getParamLong("ui_id"), getParams());
        else if (StringUtils.equals("restoreProcessList", operation))
            resultJson = WFEngineer.getInstance().restoreProcessByStep(getParamLongs("step_ids"));
        else if (StringUtils.equals("getCaseStepFormInfo", operation))
            resultJson = WFEngineer.getInstance().getCaseStepFormInfo(getParamLong("step_id"), getParamLong("skipForms") == 1);
        else if (StringUtils.equals("getDiagram", operation))
            resultJson = WFEngineer.getInstance().getDiagram(getParamLong("step_id"));
        else if (StringUtils.equals("saveIde", operation))
            resultJson = WFEngineer.getInstance().saveIdea(getParamLong("step_id"), getParamStr("idea_content"));
        else if (StringUtils.equals("getIdea", operation))
            resultJson = WFEngineer.getInstance().getIdeas(getParamLong("step_id"));
        else if (StringUtils.equals("getProcessMessage", operation))
            resultJson = WFEngineer.getInstance().getProcessMessage(getParamLong("processOrStepId"));
        else if (StringUtils.equals("saveProcessMessage", operation))
            resultJson = WFEngineer.getInstance().saveProcessMessage(getParamLong("processOrStepId"), getParamInt("type"), getParamStr("content"));

        String extMessage = GlobalContext.getClientMessage().toString();
        if (!StringUtils.isEmpty(extMessage))
            resultJson.put("popupMsg", extMessage);
        extMessage = GlobalContext.getAlertMessage().toString();
        if (!StringUtils.isEmpty(extMessage))
            resultJson.put("alertMsg", extMessage);
        response.getWriter().println(resultJson);
    }

    /**
     * 内容转换
     * 
     * @param wfProcessTreeJSON
     * @return
     * @throws Exception
     */
    private JSONObject transJsonToFlexJson(final JSONObject fromJson) throws Exception {
        final JSONObject toJson = new JSONObject();
        toJson.put("id", "-1");
        toJson.put("label", "业务对象列表");
        final List<JSONObject> parentList = new ArrayList<JSONObject>();
        parentList.add(toJson);
        transJsonToFlexJson(fromJson, toJson, parentList);
        toJson.put("r", true);
        parentList.remove(toJson);
        return toJson;
    }

    /**
     * 转换JSON
     * 
     * @param fromJson
     * @param toJson
     * @param parentList
     * @throws Exception
     */
    private void transJsonToFlexJson(final JSONObject fromJson, final JSONObject toJson, final List<JSONObject> parentList) throws Exception {

        // 目录
        if (fromJson.containsKey("folder")) {
            final JSONArray array = fromJson.getJSONArray("folder");
            for (int i = 0; i < array.size(); i++) {
                final JSONObject folderJson = new JSONObject();
                folderJson.put("id", array.getJSONObject(i).getLong("id"));
                folderJson.put("label", array.getJSONObject(i).getString("caption"));
                // toJson.append("children", folderJson);
                JSONUtils.append(toJson, "children", folderJson);
                parentList.add(folderJson);
                transJsonToFlexJson(array.getJSONObject(i), folderJson, parentList);
                parentList.remove(folderJson);
            }
        }

        // 业务
        if (fromJson.containsKey("operation")) {
            final JSONArray array = fromJson.getJSONArray("operation");
            for (int i = 0; i < array.size(); i++) {
                final JSONObject operationJson = new JSONObject();
                operationJson.put("id", array.getJSONObject(i).getLong("id"));
                operationJson.put("label", array.getJSONObject(i).getString("caption"));
                // toJson.append("children", operationJson);
                JSONUtils.append(toJson, "children", operationJson);
                parentList.add(operationJson);
                transJsonToFlexJson(array.getJSONObject(i), operationJson, parentList);
                parentList.remove(operationJson);
            }
        }

        // 业务
        if (fromJson.containsKey("process")) {
            final JSONArray array = fromJson.getJSONArray("process");
            for (int i = 0; i < array.size(); i++) {
                final JSONObject processJson = new JSONObject();
                processJson.put("id", array.getJSONObject(i).getLong("id"));
                processJson.put("label", array.getJSONObject(i).getString("caption"));
                for (int j = 0; j < parentList.size(); j++)
                    // parentList.get(j).append("process", processJson);
                    JSONUtils.append(parentList.get(j), "process", processJson);
            }
        }

    }
}
