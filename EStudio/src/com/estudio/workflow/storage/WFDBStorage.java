package com.estudio.workflow.storage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.blazeds.services.MessageHelper;
import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.intf.db.CallableStmtParamDefine;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.ICallableStmtAction;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.service.script.ScriptService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSCompress;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.SecurityUtils;
import com.estudio.web.service.DataService4Portal;
import com.estudio.workflow.base.WFActivity;
import com.estudio.workflow.base.WFActivityType;
import com.estudio.workflow.base.WFFormSetting;
import com.estudio.workflow.base.WFFormSettingItem;
import com.estudio.workflow.base.WFLink;
import com.estudio.workflow.base.WFProcess;
import com.estudio.workflow.base.WFTimeUnit;
import com.estudio.workflow.base.WFVariable;
import com.estudio.workflow.base.WFVariableDataType;
import com.estudio.workflow.utils.WFCalendarService;
import com.estudio.workflow.utils.WFUtils;
import com.estudio.workflow.web.WorkFlowUIDefine;
import com.estudio.workflow.web.WorkFlowUIDefineService;

public abstract class WFDBStorage implements IWFStorage {

    /**
     * 流程步骤信息
     * 
     * @author ShengHongL
     */
    private class RuningProcessInfo {

        long processTypeId;
        String activityName;
        long processId;
        long reciverUserId;
        String reciverUserName;
        long stepId;
    }

    static final int TYPE_OPERATION = 2; // 业务对象
    static final int TYPE_FOLDER = 1; // 目录
    static final int TYPE_WORKFLOW = 11; // 工作流

    protected final int TYPE_IS_USER = 1;
    protected final int TYPE_IS_ROLE = 1;

    protected static final int WORKFLOW_ENGINEER_USERID = -65535;
    protected static final String WORKFLOW_ENGINEER_USERNAME = "归档";

    private final Map<Long, WFProcess> id2WFProcess = new HashMap<Long, WFProcess>();// 缓存对象
    private final List<WFProcess> processList = new ArrayList<WFProcess>(); // 流程列表
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper(); // 数据库对象

    // 获取工作流对象SQL
    protected abstract String getSQL4Process();

    // 获取工作流活动体对象SQL
    protected abstract String getSQL4Action();

    // 获取连接体定义SQL
    protected abstract String getSQL4Links();

    // 获取角色定义
    protected abstract String getSQL4Roles();

    // 获取变量定义
    protected abstract String getSQL4Variables();

    // 获取表单定义
    protected abstract String getSQL4Forms();

    // 获取列表
    protected abstract String getSQL4List();

    // 获取业务树
    protected abstract String getSQL4ObjectTree();

    // 新建案件SQL语句
    protected abstract String getSQL4CreateCase();

    // 业务废弃
    protected abstract String getSQL4AbabdonProcessByStep();

    // 恢复业务
    protected abstract String getSQL4RestoreProcessByStep();

    // 根据步骤获取业务类型 环节名称 业务ID
    protected abstract String getSQL4GetStepFormsInfo();

    // 保存业务变量
    protected abstract String getSQL4SaveProcessVariableValue();

    // 发送到下一节点
    protected abstract String getSQL4SendToNextActitity();

    // 退件给项目创建人
    protected abstract String getSQL4BackProcess2Creator();

    // 更新意见
    protected abstract String getSQL4UpdateIdea();

    // 生成业务编号
    protected abstract long generalProcessId(final Connection con) throws Exception;

    // 新增意见
    private String getSQL4InsertIdea() {
        return "insert into sys_workflow_r_ideas (id,idea_content,idea_date) values (:step_id,:idea_content,:idea_date)";
    }

    // 获取人员树列表
    protected abstract JSONObject getActivityUserOrRoleTree(Connection con, WFActivity activity, HashMap<String, Object> scriptContextParams) throws Exception;

    // 退件
    protected abstract String getSQL4BackProcess();

    // 获取变量值列表
    protected String getSQL4ListProcessVariables() {
        return "select var_name,var_value from sys_workflow_r_variables where process_id = :process_id";
    }

    // 检查流程环节表单是否保存
    private String getSQL4IsActivityFormSaved() {
        return "select count(*) as c from sys_workflow_r_form_status where activity_name=:activityname and user_id=:user_id and form_id=:formid and process_id=:processid";
    }

    private String getLoggerSQL() {
        return "insert into sys_workflow_r_logger\n" + //
                "  (id,\n" + //
                "   ip,\n" + //
                "   mac_name,\n" + //
                "   type,\n" + //
                "   user_id,\n" + //
                "   step_id,\n" + //
                "   process_id,\n" + //
                "   process_type_id,\n" + //
                "   action_caption,user_name,sessionid)\n" + //
                "values\n" + //
                "  (:id,\n" + //
                "   :ip,\n" + //
                "   :mac_name,\n" + //
                "   :type,\n" + //
                "   :user_id,\n" + //
                "   :step_id,\n" + //
                "   :process_id,\n" + //
                "   :process_type_id,\n" + //
                "   :action_caption,:user_name,:sessionid)";
    }

    // 获取意见
    protected abstract String getSQL4ProcessIdeas();

    protected abstract String getSQL4GeneralProcessId();

    // 标记表单保存状态
    protected abstract String getSQL4FlagActivityFormSaved();

    // 获取步骤
    protected abstract String getSQL4ProcessSteps();

    // 获取签收案件的SQL语句
    protected abstract String getSignCaseProcedureName();

    // 工作流 发送
    @Override
    public JSONObject sendProcessEx(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand varCmd = null;
        IDBCommand formSavedCmd = null;
        IDBCommand sendCmd = null;
        IDBCommand loggerCmd = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);

            varCmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
            formSavedCmd = DBHELPER.getCommand(con, getSQL4IsActivityFormSaved());
            sendCmd = DBHELPER.getCommand(con, getSQL4SendToNextActitity());
            loggerCmd = DBHELPER.getCommand(con, getLoggerSQL());

            final List<RuningProcessInfo> processInfoList = getRuningProcessInfoList(con, processStepInfos, false);
            final StringBuilder successSB = new StringBuilder();
            final StringBuilder errorSB = new StringBuilder();
            final List<String> successStepIds = new ArrayList<String>();
            final Map<String, List<UserHelperClass>> activityName2UserList = new HashMap<String, List<UserHelperClass>>();
            final Map<Long, Map<String, List<UserHelperClass>>> process2ActivityName2UserList = new HashMap<Long, Map<String, List<UserHelperClass>>>();
            final Map<String, WFActivity> name2Activity = new HashMap<String, WFActivity>();
            final Map<Long, WFProcess> processId2WFProcess = new HashMap<Long, WFProcess>();
            final Map<Long, WFActivity> processId2FromActivity = new HashMap<Long, WFActivity>();
            final Map<Long, JSONObject> stepId2ProcessInfo = new HashMap<Long, JSONObject>();
            Map<Long, List<WFLink>> stepId2Links = new HashMap<Long, List<WFLink>>();
            for (final RuningProcessInfo rpf : processInfoList) {
                try {
                    final WFProcess wfp = getProcess(rpf.processTypeId);
                    final WFActivity fromActivity = wfp.getActivity(rpf.activityName);

                    final Map<String, Object> scriptContextParams = getScriptContextParams(varCmd, rpf);

                    checkActivityBindFormSaved(formSavedCmd, rpf); // 检查表单数据

                    onActivityValidDataEvent(con, wfp, fromActivity, rpf, scriptContextParams); // 执行数据校验
                    final List<WFLink> links = getMatchActivityLinks(con, wfp, rpf, httpContextParams, scriptContextParams); // 后续节点及收件人
                    if (links.size() == 0) {
                        errorSB.append(rpf.processId).append(":无后续节点.\n");
                        DBHELPER.rollback(con, false);
                        continue;
                    }
                    stepId2Links.put(rpf.stepId, links);

                    final Map<String, List<UserHelperClass>> processActivityUserList = new HashMap<String, List<UserHelperClass>>();
                    for (final WFLink link : links) { // 后续节点收件人
                        final String toActivityName = link.getEndActivityName();
                        name2Activity.put(toActivityName, wfp.getActivity(toActivityName));
                        List<UserHelperClass> userList = activityName2UserList.get(toActivityName);
                        if (userList == null) {
                            userList = getActivityReciverUserList(con, wfp.getActivity(toActivityName));
                            activityName2UserList.put(toActivityName, userList);
                        }
                        final List<UserHelperClass> okList = new ArrayList<UserHelperClass>();
                        for (final UserHelperClass user : userList)
                            if (onFilterCandidateEvent(con, wfp.getActivity(toActivityName), user, scriptContextParams))
                                okList.add(user);
                        if (!okList.isEmpty())
                            processActivityUserList.put(toActivityName, okList);
                    }
                    if (processActivityUserList.isEmpty()) {
                        errorSB.append(rpf.processId).append(":无合适收件人.\n");
                        DBHELPER.rollback(con, false);
                        continue;
                    }

                    // 判断是否能智能发送
                    boolean isSended = false;
                    if (processActivityUserList.size() == 1) {
                        final Map.Entry<String, List<UserHelperClass>> entry = processActivityUserList.entrySet().iterator().next();
                        final WFActivity toActivity = wfp.getActivity(entry.getKey());
                        final List<UserHelperClass> toUserList = entry.getValue();
                        if (fromActivity.isSmartSend() && !toActivity.isMultiReciver() && (toUserList.size() == 1)) {
                            sendProcessToNextActivity(sendCmd, wfp, rpf, fromActivity, toActivity, toUserList, httpContextParams, scriptContextParams);
                            // 日志
                            logger(loggerCmd, rpf, LOGGER_SEND_CASE);

                            if (toActivity.getType() == WFActivityType.END) {
                                successSB.append(rpf.processId).append(":已办结\n.");
                            } else {
                                successSB.append(rpf.processId).append(":成功发送给").append(toUserList.get(0).name).append("\n");
                                long[] reciverIds = new long[toUserList.size()];
                                int index = 0;
                                for (UserHelperClass user : toUserList)
                                    reciverIds[index++] = user.id;
                                if (toActivity.isRoleAccept()) {
                                    String messageContent = "有一份业务编号为:" + rpf.processId + "的案件发送到共办箱,请查收.";
                                    MessageHelper.getInstance().publishMessage2Role(reciverIds, messageContent);
                                } else {
                                    String messageContent = "有一份业务编号为:" + rpf.processId + "的案件发送给你,请查收.";
                                    MessageHelper.getInstance().publishMessage2User(reciverIds, messageContent);
                                }
                            }
                            successStepIds.add(Long.toString(rpf.stepId));
                            isSended = true;
                        }
                    }

                    // JSON
                    // processId2Info.put(rpf.processTypeId, wfp);

                    if (!isSended) {
                        processId2WFProcess.put(rpf.stepId, wfp);
                        processId2FromActivity.put(rpf.stepId, fromActivity);
                        process2ActivityName2UserList.put(rpf.stepId, processActivityUserList);
                        final JSONObject processJson = new JSONObject();
                        processJson.put("process_id", rpf.processId);
                        processJson.put("activity_name", rpf.activityName);
                        processJson.put("process_type", rpf.processTypeId);
                        processJson.put("step_id", rpf.stepId);
                        stepId2ProcessInfo.put(rpf.stepId, processJson);
                    }
                    DBHELPER.commit(con);
                } catch (final Exception e) {
                    DBHELPER.rollback(con, false);
                    errorSB.append(rpf.processId).append(":").append(ExceptionUtils.loggerException(e, con)).append("\n");
                }
            } // end for

            if (!process2ActivityName2UserList.isEmpty())
                json.put("multiStepInfo", generalMultiStepInfo(stepId2ProcessInfo, processId2WFProcess, processId2FromActivity, name2Activity, process2ActivityName2UserList, stepId2Links));

            json.put("successMsg", replaceTwiceEnterOne(successSB.toString()));
            json.put("errorMsg", replaceTwiceEnterOne(errorSB.toString()));
            json.put("successStepIds", successStepIds.toArray());
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(sendCmd);
            DBHELPER.closeCommand(formSavedCmd);
            DBHELPER.closeCommand(varCmd);
            DBHELPER.closeCommand(loggerCmd);
            if (con != null) {
                DBHELPER.endTransaction(con);
                DBHELPER.closeConnection(con);
            }
        }
        return json;
    }

    @Override
    public JSONObject sendProcessSpecial(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand varCmd = null;
        IDBCommand formSavedCmd = null;
        IDBCommand sendCmd = null;
        IDBCommand loggerCmd = null;
        try {
            con = DBHELPER.getConnection();

            varCmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
            formSavedCmd = DBHELPER.getCommand(con, getSQL4IsActivityFormSaved());
            sendCmd = DBHELPER.getCommand(con, getSQL4SendToNextActitity());
            loggerCmd = DBHELPER.getCommand(con, getLoggerSQL());

            final List<RuningProcessInfo> processInfoList = getRuningProcessInfoList(con, processStepInfos, false);
            final StringBuilder successSB = new StringBuilder();
            final StringBuilder errorSB = new StringBuilder();
            final List<String> successStepIds = new ArrayList<String>();
            final Map<String, List<UserHelperClass>> activityName2UserList = new HashMap<String, List<UserHelperClass>>();
            final Map<Long, Map<String, List<UserHelperClass>>> process2ActivityName2UserList = new HashMap<Long, Map<String, List<UserHelperClass>>>();
            final Map<String, WFActivity> name2Activity = new HashMap<String, WFActivity>();
            final Map<Long, WFProcess> processId2WFProcess = new HashMap<Long, WFProcess>();
            final Map<Long, WFActivity> processId2FromActivity = new HashMap<Long, WFActivity>();
            final Map<Long, JSONObject> stepId2ProcessInfo = new HashMap<Long, JSONObject>();
            Map<Long, List<WFLink>> stepId2Links = new HashMap<Long, List<WFLink>>();
            for (final RuningProcessInfo rpf : processInfoList) {
                try {
                    final WFProcess wfp = getProcess(rpf.processTypeId);
                    final WFActivity fromActivity = wfp.getActivity(rpf.activityName);

                    final Map<String, Object> scriptContextParams = getScriptContextParams(varCmd, rpf);

                    // checkActivityBindFormSaved(formSavedCmd, rpf); // 检查表单数据

                    // onActivityValidDataEvent(con, wfp, fromActivity, rpf,
                    // scriptContextParams); // 执行数据校验

                    final List<WFLink> links = getActivityLinks4Special(con, wfp, rpf);
                    if (links.size() == 0) {
                        errorSB.append(rpf.processId).append(":无后续节点.\n");
                        DBHELPER.rollback(con, false);
                        continue;
                    }
                    stepId2Links.put(rpf.stepId, links);

                    final Map<String, List<UserHelperClass>> processActivityUserList = new HashMap<String, List<UserHelperClass>>();
                    for (final WFLink link : links) { // 后续节点收件人
                        final String toActivityName = link.getEndActivityName();
                        name2Activity.put(toActivityName, wfp.getActivity(toActivityName));
                        List<UserHelperClass> userList = activityName2UserList.get(toActivityName);
                        if (userList == null) {
                            userList = getActivityReciverUserList(con, wfp.getActivity(toActivityName));
                            activityName2UserList.put(toActivityName, userList);
                        }
                        final List<UserHelperClass> okList = new ArrayList<UserHelperClass>();
                        // if
                        // (onFilterCandidateEvent(wfp.getActivity(toActivityName),
                        // user, scriptContextParams))

                        for (final UserHelperClass user : userList)
                            okList.add(user);
                        if (!okList.isEmpty())
                            processActivityUserList.put(toActivityName, okList);
                    }
                    if (processActivityUserList.isEmpty()) {
                        errorSB.append(rpf.processId).append(":无合适收件人.\n");
                        DBHELPER.rollback(con, false);
                        continue;
                    }
                    // JSON
                    // processId2Info.put(rpf.processTypeId, wfp);

                    processId2WFProcess.put(rpf.stepId, wfp);
                    processId2FromActivity.put(rpf.stepId, fromActivity);
                    process2ActivityName2UserList.put(rpf.stepId, processActivityUserList);
                    final JSONObject processJson = new JSONObject();
                    processJson.put("process_id", rpf.processId);
                    processJson.put("activity_name", rpf.activityName);
                    processJson.put("process_type", rpf.processTypeId);
                    processJson.put("step_id", rpf.stepId);
                    stepId2ProcessInfo.put(rpf.stepId, processJson);

                } catch (final Exception e) {
                    errorSB.append(rpf.processId).append(":").append(ExceptionUtils.loggerException(e, con)).append("\n");
                }
            } // end for

            if (!process2ActivityName2UserList.isEmpty())
                json.put("multiStepInfo", generalMultiStepInfo(stepId2ProcessInfo, processId2WFProcess, processId2FromActivity, name2Activity, process2ActivityName2UserList, stepId2Links));

            json.put("successMsg", replaceTwiceEnterOne(successSB.toString()));
            json.put("errorMsg", replaceTwiceEnterOne(errorSB.toString()));
            json.put("successStepIds", successStepIds.toArray());
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(sendCmd);
            DBHELPER.closeCommand(formSavedCmd);
            DBHELPER.closeCommand(varCmd);
            DBHELPER.closeCommand(loggerCmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject sendMultiProcessToNextActivity(final JSONArray array, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand varCmd = null;
        IDBCommand sendCmd = null;
        IDBCommand loggerCmd = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con); // 加入事务控住

            varCmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
            sendCmd = DBHELPER.getCommand(con, getSQL4SendToNextActitity());
            loggerCmd = DBHELPER.getCommand(con, getLoggerSQL());

            final StringBuilder successSB = new StringBuilder();
            final StringBuilder errorSB = new StringBuilder();
            final List<String> successStepIds = new ArrayList<String>();

            for (int i = 0; i < array.size(); i++) { // 业务组
                final JSONObject processActivity = array.getJSONObject(i);
                final JSONArray processList = processActivity.getJSONArray("process");
                final JSONArray toActivitys = processActivity.getJSONArray("toActivitys");
                for (int j = 0; j < processList.size(); j++) { // 业务
                    final JSONObject processJson = processList.getJSONObject(j);
                    final RuningProcessInfo rpf = new RuningProcessInfo();
                    rpf.stepId = processJson.getLong("step_id");
                    rpf.processId = processJson.getLong("process_id");
                    rpf.activityName = processJson.getString("activity_name");
                    rpf.processTypeId = processJson.getLong("process_type");
                    final WFProcess wfp = getProcess(rpf.processTypeId);
                    final WFActivity fromActivity = wfp.getActivity(rpf.activityName);
                    boolean isFinished = false;
                    final List<String> userNames = new ArrayList<String>();
                    try {
                        final Map<String, Object> scriptContextParams = getScriptContextParams(varCmd, rpf);
                        for (int m = 0; m < toActivitys.size(); m++) { // 节点
                            final JSONObject activityJson = toActivitys.getJSONObject(m);
                            final String toActivityName = activityJson.getString("name");
                            final WFActivity toActivity = wfp.getActivity(toActivityName);
                            if (toActivity.getType() == WFActivityType.END)
                                isFinished = true;
                            final List<UserHelperClass> toUserList = new ArrayList<UserHelperClass>();
                            final JSONArray usersArray = activityJson.getJSONArray("toUsers");
                            for (int n = 0; n < usersArray.size(); n++) { // 人员
                                final JSONObject userJson = usersArray.getJSONObject(n);
                                final UserHelperClass userInfo = new UserHelperClass();
                                userInfo.id = userJson.getLong("id");
                                userInfo.name = userJson.getString("name");
                                userNames.add(userInfo.name);
                                toUserList.add(userInfo);
                            }
                            sendProcessToNextActivity(sendCmd, wfp, rpf, fromActivity, toActivity, toUserList, params, scriptContextParams);
                            successStepIds.add(Long.toString(rpf.stepId));
                            // 日志
                            logger(loggerCmd, rpf, LOGGER_SEND_CASE);

                            if (!isFinished) {
                                long[] reciverIds = new long[toUserList.size()];
                                int index = 0;
                                for (UserHelperClass user : toUserList)
                                    reciverIds[index++] = user.id;
                                if (toActivity.isRoleAccept()) {
                                    String messageContent = "有一份业务编号为:" + rpf.processId + "的案件发送到共办箱,请查收.";
                                    MessageHelper.getInstance().publishMessage2Role(reciverIds, messageContent);
                                } else {
                                    String messageContent = "有一份业务编号为:" + rpf.processId + "的案件发送给你,请查收.";
                                    MessageHelper.getInstance().publishMessage2User(reciverIds, messageContent);
                                }
                            }
                        }
                        if (isFinished)
                            successSB.append(rpf.processId).append(":").append("成功办结.\n");
                        else
                            successSB.append(rpf.processId).append(":").append("已发送给").append(Arrays.toString(userNames.toArray())).append(".\n");

                        con.commit();
                    } catch (final Exception e) {
                        DBHELPER.rollback(con, false);
                        errorSB.append(rpf.processId).append(":").append(ExceptionUtils.loggerException(e, con)).append("\n");
                    }// end try
                } // end for
            }
            json.put("successMsg", replaceTwiceEnterOne(successSB.toString()));
            json.put("errorMsg", replaceTwiceEnterOne(errorSB.toString()));
            json.put("successStepIds", successStepIds.toArray());
            json.put("r", true);

        } finally {
            DBHELPER.closeCommand(varCmd);
            DBHELPER.closeCommand(sendCmd);
            DBHELPER.closeCommand(loggerCmd);
            DBHELPER.endTransaction(con);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 将两个回车替换为一个
     * 
     * @param str
     * @return
     */
    private String replaceTwiceEnterOne(final String value) {
        String str = value;
        while (str.indexOf("\n\n") != -1)
            str = StringUtils.replace(str, "\n\n", "\n");
        return str;
    }

    /**
     * 生成分组
     * 
     * @param stepId2ProcessInfo
     * 
     * @param processId2WFProcess
     * @param processId2FromActivity
     * 
     * @param name2Activity
     * 
     * @param process2ActivityName2UserList
     * @param links
     * @return
     */
    private JSONArray generalMultiStepInfo(final Map<Long, JSONObject> stepId2ProcessInfo, final Map<Long, WFProcess> processId2WFProcess, final Map<Long, WFActivity> processId2FromActivity, final Map<String, WFActivity> name2Activity, final Map<Long, Map<String, List<UserHelperClass>>> process2ActivityName2UserList, Map<Long, List<WFLink>> processId2Links) {
        final Map<String, List<Long>> key2List = new HashMap<String, List<Long>>();
        final Map<String, JSONArray> key2JsonObject = new HashMap<String, JSONArray>();

        for (final Map.Entry<Long, Map<String, List<UserHelperClass>>> entry : process2ActivityName2UserList.entrySet()) {
            final long processId = entry.getKey();
            final Map<String, List<UserHelperClass>> activityAndUserList = entry.getValue();
            final String key = generalMultiStepInfoKey(activityAndUserList);
            if (!key2JsonObject.containsKey(key))
                key2JsonObject.put(key, generalActivityAndUserJson(name2Activity, activityAndUserList, processId2WFProcess.get(processId), processId2FromActivity.get(processId), processId2Links.get(entry.getKey())));
            if (!key2List.containsKey(key))
                key2List.put(key, new ArrayList<Long>());
            key2List.get(key).add(processId);
        }
        final JSONArray result = new JSONArray();
        for (final Map.Entry<String, List<Long>> entry : key2List.entrySet()) {
            final JSONObject json = new JSONObject();
            WFProcess wfp = null;
            WFActivity fromActivity = null;
            for (final Long step_id : entry.getValue()) {
                JSONUtils.append(json, "processIds", stepId2ProcessInfo.get(step_id));
                if (wfp == null) {
                    wfp = processId2WFProcess.get(step_id);
                    fromActivity = processId2FromActivity.get(step_id);
                }
            }
            json.put("processName", wfp.getName());
            json.put("status", fromActivity.getCaption());
            json.put("activitys", key2JsonObject.get(entry.getKey()));
            result.add(json);
        }
        return result;
    }

    /**
     * 根据节点及名称生成JSON对象
     * 
     * @param name2Activity
     * 
     * @param activityAndUserList
     * @param formActicity
     * @param wfProcess
     * @return
     */
    private JSONArray generalActivityAndUserJson(final Map<String, WFActivity> name2Activity, final Map<String, List<UserHelperClass>> activityAndUserList, final WFProcess wfProcess, final WFActivity formActicity, final List<WFLink> links) {
        final JSONArray array = new JSONArray();
        Collections.sort(links);
        for (final WFLink link : links) {
            final String activityName = link.getEndActivityName();
            if (activityAndUserList.containsKey(activityName)) {
                final WFActivity activity = name2Activity.get(activityName);
                final JSONObject json = new JSONObject();
                json.put("name", activity.getName());
                json.put("caption", activity.getCaption());
                json.put("multiReciver", activity.isMultiReciver());
                for (final UserHelperClass user : activityAndUserList.get(activityName)) {
                    final JSONObject userJson = new JSONObject();
                    userJson.put("id", user.id);
                    userJson.put("name", user.name);
                    JSONUtils.append(json, "users", userJson);
                }
                array.add(json);
            }
        }

        return array;
    }

    /**
     * 根据节点及名称生成一个唯一标识号
     * 
     * @param activityAndUserList
     * @return
     */
    private String generalMultiStepInfoKey(final Map<String, List<UserHelperClass>> activityAndUserList) {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, List<UserHelperClass>> entry : activityAndUserList.entrySet()) {
            final String activityName = entry.getKey();
            sb.append(activityName);
            sb.append("-");
            for (final UserHelperClass user : entry.getValue())
                sb.append(user.id).append(",");
            sb.append("\n");
        }
        return SecurityUtils.md5(sb.toString());
    }

    /**
     * 获取脚本运行环境变量
     * 
     * @param varCmd
     * @param processInfo
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    private Map<String, Object> getScriptContextParams(final IDBCommand varCmd, final RuningProcessInfo processInfo) throws Exception {
        final HashMap<String, Object> scriptContextParams = new HashMap<String, Object>();
        scriptContextParams.put("PROCESS_ID", processInfo.processId); // 业务ID
        scriptContextParams.put("PROCESS_TYPE_ID", processInfo.processTypeId); // 业务类型ID
        scriptContextParams.put("PROCESS_STEP_ID", processInfo.stepId); // 业务类型ID
        scriptContextParams.put("WFDBService", WFDBService.getInstance());
        scriptContextParams.put("USER_ID", GlobalContext.getLoginInfo().getId());
        scriptContextParams.put("USER_NAME", GlobalContext.getLoginInfo().getRealName());
        scriptContextParams.put("DEPARTMENT_ID", GlobalContext.getLoginInfo().getDepartmentId());
        scriptContextParams.put("DEPARTMENT_NAME", GlobalContext.getLoginInfo().getDepartmentId());
        addSenderToScriptParams(scriptContextParams);

        getProcessVariables(varCmd, getProcess(processInfo.processTypeId), processInfo.processId, scriptContextParams);

        return scriptContextParams;
    }

    /**
     * 初始化工作流数据服务
     * 
     * @param con
     * @param processInfo
     * @param httpContextParams
     * @throws Exception
     */
    private void initWFDBService(final Connection con, final RuningProcessInfo processInfo, final Map<String, String> httpContextParams) throws Exception {
        WFDBService.getInstance().setProcessId(processInfo != null ? processInfo.processId : 0L);
        WFDBService.getInstance().setConnection(con);
        WFDBService.getInstance().setHttpParams(httpContextParams);
        if (httpContextParams != null && processInfo != null) {
            WFProcess wfProcess = getProcess(processInfo.processTypeId);
            for (Map.Entry<String, Map<String, String>> entry : wfProcess.getFormParams().entrySet()) {
                for (Map.Entry<String, String> entry1 : entry.getValue().entrySet()) {
                    final String paramName = entry1.getKey();
                    final String paramValue = entry1.getValue();
                    if (StringUtils.equals(paramValue, "REQ.WORKFLOW_PROCESS_ID"))
                        httpContextParams.put(paramName, Long.toString(processInfo.processId));
                    else if (StringUtils.equals(paramValue, "REQ.WORKFLOW_STEP_ID"))
                        httpContextParams.put(paramName, Long.toString(processInfo.stepId));
                }
            }
        }
    }

    /**
     * 获取流程节点对应的用户及角色列表
     * 
     * @param con
     * @param activity
     * @return
     * @throws Exception
     */
    protected abstract List<UserHelperClass> getActivityReciverUserList(Connection con, WFActivity activity) throws Exception;

    // 工作流 退件
    @Override
    public JSONObject backProcessEx(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        IDBCommand varCmd = null;
        IDBCommand loggerCmd = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);
            cmd = DBHELPER.getCommand(con, getSQL4BackProcess());
            varCmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
            loggerCmd = DBHELPER.getCommand(con, getLoggerSQL());
            final List<RuningProcessInfo> processInfoList = getRuningProcessInfoList(con, processStepInfos, true);
            final StringBuilder successSB = new StringBuilder();
            final StringBuilder errorSB = new StringBuilder();
            final List<String> successStepIds = new ArrayList<String>();
            for (final RuningProcessInfo rpf : processInfoList) { // 循环退件
                executeBackProcessEx(con, cmd, varCmd, rpf, successSB, errorSB, successStepIds);
                // 日志
                logger(loggerCmd, rpf, LOGGER_BACK_CASE);
            }// 循环结束
            json.put("successMsg", replaceTwiceEnterOne(successSB.toString()));
            json.put("errorMsg", replaceTwiceEnterOne(errorSB.toString()));
            json.put("successStepIds", successStepIds.toArray());
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(varCmd);
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeCommand(loggerCmd);
            if (con != null) {
                DBHELPER.endTransaction(con);
                DBHELPER.closeConnection(con);
            }
        }
        return json;
    }

    @Override
    public JSONObject backProcessToCreator(JSONArray processStepInfos, Map<String, String> httpContextParams) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        IDBCommand varCmd = null;
        IDBCommand loggerCmd = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);
            cmd = DBHELPER.getCommand(con, getSQL4BackProcess2Creator());
            varCmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
            loggerCmd = DBHELPER.getCommand(con, getLoggerSQL());
            final List<RuningProcessInfo> processInfoList = getRuningProcessInfoList(con, processStepInfos, true);
            final StringBuilder successSB = new StringBuilder();
            final StringBuilder errorSB = new StringBuilder();
            final List<String> successStepIds = new ArrayList<String>();
            for (final RuningProcessInfo rpf : processInfoList) { // 循环退件
                if (rpf.processId == rpf.stepId)
                    continue;
                executeBackProcess2Creator(con, cmd, varCmd, rpf, successSB, errorSB, successStepIds);
                // 日志
                logger(loggerCmd, rpf, LOGGER_BACK_CASE_TO_CREATOR);
            }// 循环结束
            json.put("successMsg", replaceTwiceEnterOne(successSB.toString()));
            json.put("errorMsg", replaceTwiceEnterOne(errorSB.toString()));
            json.put("successStepIds", successStepIds.toArray());
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(varCmd);
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeCommand(loggerCmd);
            if (con != null) {
                DBHELPER.endTransaction(con);
                DBHELPER.closeConnection(con);
            }
        }
        return json;
    }

    // 工作流 废除
    @Override
    public JSONObject abandoProcessEx(final JSONArray processStepInfos, final Map<String, String> httpContextParams) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        IDBCommand varCmd = null;
        IDBCommand loggerCmd = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);
            cmd = DBHELPER.getCommand(con, getSQL4AbabdonProcessByStep());
            varCmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
            loggerCmd = DBHELPER.getCommand(con, getLoggerSQL());
            final List<RuningProcessInfo> processInfoList = getRuningProcessInfoList(con, processStepInfos, false);
            final StringBuilder successSB = new StringBuilder();
            final StringBuilder errorSB = new StringBuilder();
            final List<String> successStepIds = new ArrayList<String>();
            for (final RuningProcessInfo rpf : processInfoList) { // 循环退件
                executeAbandonProcessEx(con, cmd, varCmd, rpf, successSB, errorSB, successStepIds);
                // 日志
                logger(loggerCmd, rpf, LOGGER_ABANDON_CASE);
            }// 循环结束
            json.put("successMsg", replaceTwiceEnterOne(successSB.toString()));
            json.put("errorMsg", replaceTwiceEnterOne(errorSB.toString()));
            json.put("successStepIds", successStepIds.toArray());
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(varCmd);
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeCommand(loggerCmd);
            if (con != null) {
                DBHELPER.endTransaction(con);
                DBHELPER.closeConnection(con);
            }
        }
        return json;
    }

    @Override
    public JSONObject signCaseList(final long[] step_ids) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();

            final List<List<Object>> paramValuesList = new ArrayList<List<Object>>();
            final List<Long> okList = new ArrayList<Long>();
            final List<Long> errorList = new ArrayList<Long>();
            final List<String> successStepIds = new ArrayList<String>();
            final List<String> listStepIds = new ArrayList<String>();
            final long userId = getCurrentUserHelperClass().id;
            for (final long stepId : step_ids) {
                final List<Object> paramList = new ArrayList<Object>();
                paramList.add(userId);
                paramList.add(stepId);
                paramList.add(0L);
                paramList.add(0L);
                paramValuesList.add(paramList);
                listStepIds.add(Long.toString(stepId));
            }
            final CallableStmtParamDefine[] paramDefine = new CallableStmtParamDefine[] { new CallableStmtParamDefine(DBParamDataType.Long, false), new CallableStmtParamDefine(DBParamDataType.Long, true), new CallableStmtParamDefine(DBParamDataType.Long, true), new CallableStmtParamDefine(DBParamDataType.Long, true) };
            DBHELPER.executeProcedure(con, getSignCaseProcedureName(), paramDefine, paramValuesList, new ICallableStmtAction() {

                @Override
                public void processStatement(final CallableStatement stmt) throws SQLException {
                    if (stmt.getInt(3) == 1) {
                        okList.add(stmt.getLong(4));
                        successStepIds.add(listStepIds.get(0));
                    } else
                        errorList.add(stmt.getLong(4));
                    listStepIds.remove(0);
                }
            });
            json.put("r", true);

            if (!okList.isEmpty())
                json.put("successMsg", replaceTwiceEnterOne("成功签收业务编号为:" + StringUtils.join(okList.iterator(), ",") + "的案件."));
            if (!errorList.isEmpty())
                json.put("errorMsg", replaceTwiceEnterOne("签收业务编号为:" + StringUtils.join(errorList.iterator(), ",") + "的案件失败,原因可能是已被其他用户签收！"));
            json.put("successStepIds", successStepIds.toArray());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.endTransaction(con);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取业务信息步骤列表
     * 
     * @param con
     * @param processStepInfos
     * @return
     * @throws Exception
     * @throws SQLException
     */
    private List<RuningProcessInfo> getRuningProcessInfoList(final Connection con, final JSONArray processStepInfos, final boolean isForceFromDB) throws SQLException, Exception {
        final List<RuningProcessInfo> result = new ArrayList<WFDBStorage.RuningProcessInfo>();
        IDBCommand cmd = null;
        try {
            final JSONObject firstJson = processStepInfos.getJSONObject(0);
            final boolean isForceReload = !firstJson.containsKey("action_name") || !firstJson.containsKey("process_id") || isForceFromDB;
            if (isForceReload) { // 需要从数据库中加载
                cmd = DBHELPER.getCommand(con, getSQL4GetStepFormsInfo());
                for (int i = 0; i < processStepInfos.size(); i++) {
                    final long step_id = processStepInfos.getJSONObject(i).getLong("step_id");
                    cmd.setParam("step_id", step_id);
                    if (cmd.executeQuery() && cmd.next()) {
                        final RuningProcessInfo info = new RuningProcessInfo();
                        info.processTypeId = cmd.getLong("process_type_id");
                        info.activityName = cmd.getString("activity_name");
                        info.processId = cmd.getLong("process_id");
                        info.reciverUserId = cmd.getLong("reciver_userid");
                        info.reciverUserName = cmd.getString("reciver_username");
                        info.stepId = step_id;
                        result.add(info);
                    }
                }
            } else
                for (int i = 0; i < processStepInfos.size(); i++) {
                    final JSONObject item = processStepInfos.getJSONObject(i);
                    final RuningProcessInfo info = new RuningProcessInfo();
                    info.processTypeId = item.getLong("process_type");
                    info.activityName = item.getString("action_name");
                    info.processId = item.getLong("process_id");
                    info.stepId = item.getLong("step_id");
                    result.add(info);
                }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    /**
     * 执行具体的案件退回函数
     * 
     * @param con
     * @param backCmd
     * @param successSb
     * @param errorSb
     * @param rpf
     */
    private void executeBackProcessEx(final Connection con, final IDBCommand backCmd, final IDBCommand varCmd, final RuningProcessInfo rpf, final StringBuilder successSb, final StringBuilder errorSb, final List<String> successStepIds) {
        try {
            final WFProcess wfp = getProcess(rpf.processTypeId);
            final WFActivity activity = wfp.getActivity(rpf.activityName);
            if ((activity != null) && (activity != wfp.getFirstActivity()) && activity.isBackAble()) {

                // 退件
                backCmd.setParam("step_id", rpf.stepId);
                backCmd.execute();

                // 退件事件
                final List<UserHelperClass> receiveUsers = new ArrayList<UserHelperClass>();
                final UserHelperClass reciver = new UserHelperClass();
                reciver.id = rpf.reciverUserId;
                reciver.name = rpf.reciverUserName;
                receiveUsers.add(reciver);
                onActivityBackEvent(varCmd, wfp, rpf, activity, receiveUsers);
                successSb.append(rpf.processId).append(":成功退件.\n");
                successStepIds.add(Long.toString(rpf.stepId));

                String messageContent = "您处理的编号为:" + rpf.processId + "的案件被退回,请在收件箱中查收.";
                MessageHelper.getInstance().publishMessage2User(reciver.id, messageContent);

            } else if (activity != null)
                errorSb.append(rpf.processId).append(":不允许退件.\n");
            else
                errorSb.append(rpf.processId).append(":业务环节异常.\n");
            DBHELPER.commit(con);
        } catch (final Exception e) {
            DBHELPER.rollback(con, false);
            errorSb.append(rpf.processId).append(":").append(ExceptionUtils.loggerException(e, con)).append("\n");
        }
    }

    /**
     * 执行具体的案件退回函数
     * 
     * @param con
     * @param backCmd
     * @param successSb
     * @param errorSb
     * @param rpf
     */
    private void executeBackProcess2Creator(final Connection con, final IDBCommand backCmd, final IDBCommand varCmd, final RuningProcessInfo rpf, final StringBuilder successSb, final StringBuilder errorSb, final List<String> successStepIds) {
        try {
            final WFProcess wfp = getProcess(rpf.processTypeId);
            final WFActivity activity = wfp.getActivity(rpf.activityName);
            if (activity != null) {

                // 退件
                backCmd.setParam("step_id", rpf.stepId);
                backCmd.execute();

                // 退件事件
                final List<UserHelperClass> receiveUsers = new ArrayList<UserHelperClass>();
                final UserHelperClass reciver = getCreatorUserInfo(con, rpf.processId);
                // reciver.id = rpf.reciverUserId;
                // reciver.name = rpf.reciverUserName;
                receiveUsers.add(reciver);
                onActivityBackEvent(varCmd, wfp, rpf, activity, receiveUsers);
                successSb.append(rpf.processId).append(":成功退件.\n");
                successStepIds.add(Long.toString(rpf.stepId));

                String messageContent = "您处理的编号为:" + rpf.processId + "的案件被退回,请在收件箱中查收.";
                MessageHelper.getInstance().publishMessage2User(reciver.id, messageContent);

            } else {
                errorSb.append(rpf.processId).append(":业务环节异常.\n");
            }
            DBHELPER.commit(con);
        } catch (final Exception e) {
            DBHELPER.rollback(con, false);
            errorSb.append(rpf.processId).append(":").append(ExceptionUtils.loggerException(e, con)).append("\n");
        }
    }

    /**
     * 获取创建人信息
     * 
     * @param con
     * @param processId
     * @return
     * @throws SQLException
     */
    private UserHelperClass getCreatorUserInfo(Connection con, long processId) throws SQLException {
        UserHelperClass result = new UserHelperClass();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select reciver_userid,b.realname from sys_workflow_r_step a ,sys_userinfo b where b.id=a.reciver_userid and a.id = ?");
            stmt.setLong(1, processId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            result.id = rs.getLong(1);
            result.name = rs.getString(2);
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return result;
    }

    /**
     * 执行废除函数
     * 
     * @param con
     * @param cmd
     * @param varCmd
     * @param successStepIds
     * @param runingProcessInfo
     * @param successSB
     * @param errorSB
     */
    private void executeAbandonProcessEx(final Connection con, final IDBCommand abandonCmd, final IDBCommand varCmd, final RuningProcessInfo rpf, final StringBuilder successSb, final StringBuilder errorSb, List<String> successStepIds) throws Exception {
        final long user_id = GlobalContext.getLoginInfo().getId();
        abandonCmd.setParam("user_id", user_id);
        try {
            final WFProcess wfp = getProcess(rpf.processTypeId);
            onProcessAbabdonEvent(varCmd, wfp, rpf);
            abandonCmd.setParam("step_id", rpf.stepId);
            abandonCmd.execute();
            DBHELPER.commit(con);
            successStepIds.add(Long.toString(rpf.stepId));
            successSb.append(rpf.processId).append(":成功删除.\n");// 成功废除
        } catch (final Exception e) {
            DBHELPER.rollback(con, false);
            errorSb.append(rpf.processId).append(":").append(ExceptionUtils.loggerException(e, con)).append("\n");
        }
    }

    @Override
    public JSONObject getIdeas(final long step_id) throws Exception {
        final JSONObject result = new JSONObject();
        result.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            final RuningProcessInfo wfi = getRuningProcessInfo(con, step_id);
            result.put("ideaAble", getProcess(wfi.processTypeId).getActivity(wfi.activityName).isIdeaAble());
            cmd = DBHELPER.getCommand(con, getSQL4ProcessIdeas());
            cmd.setParam("process_id", wfi.processId);
            cmd.executeQuery();
            while (cmd.next()) {
                final JSONObject idea = new JSONObject();
                idea.put("user", cmd.getString("REALNAME"));
                idea.put("idea", Convert.bytes2Str(cmd.getBytes("IDEA_CONTENT")));
                idea.put("step_id", cmd.getString("ID"));
                idea.put("date", cmd.getString("IDEA_DATE"));
                idea.put("files", cmd.getString("FILELIST"));
                idea.put("activity", cmd.getString("ACTIVITY_CAPTION"));
                if (cmd.getLong("ID") != step_id)
                    JSONUtils.append(result, "ideas", idea);
                else
                    result.put("myidea", idea);
            }
            result.put("step_id", step_id);
            if (!result.getBoolean("ideaAble") && !result.containsKey("ideas"))
                result.put("msg", "环节" + getProcess(wfi.processTypeId).getActivity(wfi.activityName).getCaption() + "不允许签署审批意见！");
            else
                result.put("r", true);

        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    // 是否签署意见
    private boolean isStepIdeaed(final Connection con, final long step_id) throws Exception {
        boolean result = false;
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, "select count(*) from sys_workflow_r_ideas where id=:step_id");
            cmd.setParam("step_id", step_id);
            result = cmd.executeQuery() && cmd.next() && (cmd.getLong(1) != 0);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    @Override
    public JSONObject saveIdea(final long step_id, final String idea_content) throws Exception {
        final JSONObject result = new JSONObject();
        result.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            final RuningProcessInfo wfi = getRuningProcessInfo(con, step_id);
            final WFActivity activity = getProcess(wfi.processTypeId).getActivity(wfi.activityName);
            if (activity.isIdeaAble()) {
                cmd = DBHELPER.getCommand(con, (isStepIdeaed(con, step_id) ? getSQL4UpdateIdea() : getSQL4InsertIdea()));
                cmd.setParam("step_id", step_id);
                cmd.setParam("idea_content", Convert.str2Bytes(idea_content));
                cmd.setParam("idea_date", new Date());
                cmd.execute();
                result.put("r", true);
            } else
                result.put("msg", "当前环节不允许签署意见！");
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    @Override
    public JSONObject getDiagram(final long step_id) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final RuningProcessInfo wfi = getRuningProcessInfo(con, step_id);
            final WFProcess wfp = getProcess(wfi.processTypeId);
            if (wfp != null) {
                json.put("diagram", wfp.getDiagramJson());
                json.put("steps", getProcessSteps(con, wfi.processId));
                json.put("processTypeId", wfi.processTypeId);
                json.put("r", true);
            } else
                json.put("msg", "无法获取业务流程信息,流程可能被删除！");
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 产生步骤信息
     * 
     * @param con
     * @param processId
     * @return
     * @throws Exception
     * @throws DBException
     */
    private JSONArray getProcessSteps(final Connection con, final long processId) throws Exception, DBException {
        final JSONArray result = new JSONArray();
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, getSQL4ProcessSteps());
            cmd.setParam("process_id", processId);
            cmd.executeQuery();
            int index = 0;
            while (cmd.next()) {
                final JSONObject json = new JSONObject();
                json.put("step_no", ++index);
                json.put("activityCaption", cmd.getString("ACTIVITY_CAPTION"));
                json.put("activityName", cmd.getString("ACTIVITY_NAME"));
                json.put("reciver", cmd.getString("REALNAME"));
                json.put("send_date", cmd.getString("SEND_DATE"));
                json.put("step_id", cmd.getString("ID"));
                json.put("type", cmd.getString("TYPE"));
                json.put("finish_date", cmd.getString("finish_date"));
                result.add(json);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    @Override
    public void flagActivityFormSaved(final Connection con, final Long processId, final String activityName, final List<Long> savedFormIDS, final long userID) throws Exception {
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, getSQL4FlagActivityFormSaved());
            for (int i = 0; i < savedFormIDS.size(); i++) {
                cmd.setParam("processid", processId);
                cmd.setParam("activityname", activityName);
                cmd.setParam("formid", savedFormIDS.get(i));
                cmd.setParam("user_id", userID);
                cmd.execute();
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
    }

    @Override
    public WFProcess getProcess(final long id) throws Exception {
        WFProcess result = null;
        synchronized (id2WFProcess) {
            if (id2WFProcess.containsKey(id))
                result = id2WFProcess.get(id);
        }
        return result;
    }

    /**
     * 获取正在运行的业务信息
     * 
     * @param con
     * @param step_id
     * @return
     * @throws Exception
     */
    private RuningProcessInfo getRuningProcessInfo(final Connection con, final long step_id) throws Exception {
        RuningProcessInfo result = null;
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, getSQL4GetStepFormsInfo());
            cmd.setParam("step_id", step_id);
            if (cmd.executeQuery() && cmd.next()) {
                result = new RuningProcessInfo();
                result.processTypeId = cmd.getLong("process_type_id");
                result.activityName = cmd.getString("activity_name");
                result.processId = cmd.getLong("process_id");
                result.reciverUserId = cmd.getLong("reciver_userid");
                result.reciverUserName = cmd.getString("reciver_username");
                result.stepId = step_id;
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    /**
     * 流程节点绑定的表单是否保存
     * 
     * @param rpf
     * @param params
     * @return
     * @throws Exception
     * @throws
     */
    private void checkActivityBindFormSaved(final IDBCommand formSavedCmd, final RuningProcessInfo rpf) throws Exception {
        final WFActivity activity = getProcess(rpf.processTypeId).getActivity(rpf.activityName);
        for (int i = 0; i < activity.getForms().size(); i++) {
            final WFFormSetting wfs = activity.getForms().get(i);
            if (wfs.isRequire()) {
                formSavedCmd.setParam("processid", rpf.processId);
                formSavedCmd.setParam("activityname", rpf.activityName);
                formSavedCmd.setParam("user_id", GlobalContext.getLoginInfo().getId());
                formSavedCmd.setParam("formid", wfs.getId());
                formSavedCmd.executeQuery();
                formSavedCmd.next();
                if (formSavedCmd.getLong(1) == 0)
                    throw new Exception("尚未填写表单.");
            }
        }
    }

    /**
     * 发送案件到下一节点
     * 
     * @param cmd
     * @param wfProcess
     * @param fromActivity
     * @param toActivity
     * @param stepId
     * @param activityName
     * @param toUserList
     * @param json
     * @throws SQLException
     * @throws Exception
     */
    private void sendProcessToNextActivity(final IDBCommand cmd, final WFProcess wfProcess, final RuningProcessInfo rpf, final WFActivity fromActivity, final WFActivity toActivity, final List<UserHelperClass> toUserList, final Map<String, String> httpContextParams, final Map<String, Object> scriptContextParams) throws Exception {
        final Date sendDate = new Date();
        cmd.setParam("stepid", rpf.stepId);
        cmd.setParam("activityname", toActivity.getName());
        cmd.setParam("activitycaption", toActivity.getCaption());
        cmd.setParam("limit_num", toActivity.getTimeLimit().getTime());
        cmd.setParam("limit_unit", WFTimeUnit.toChineseStr(toActivity.getTimeLimit().getUnit()));
        cmd.setParam("senddate", sendDate);
        cmd.setParam("is_sendto_role", toActivity.isRoleAccept() ? 1 : 0);
        cmd.setParam("activity_duration", WFCalendarService.getInstance().getDurationDate(sendDate, toActivity.getTimeLimit()));

        for (final UserHelperClass user : toUserList) {
            cmd.setParam("userid", user.id);
            cmd.execute();
        }

        onActivitySendAndReceiveEvent(cmd.getConnection(), wfProcess, rpf, fromActivity, toActivity, toUserList, httpContextParams, scriptContextParams);
    }

    /**
     * 执行废除脚本
     * 
     * @param varCmd
     * @param wfp
     * @param rpf
     * @return
     * @throws Exception
     */
    private boolean onActivityValidDataEvent(final Connection con, final WFProcess wfp, final WFActivity activity, final RuningProcessInfo rpf, final Map<String, Object> scriptContextParams) throws Exception {
        final String dataValidScript = activity.getValidDataEvent();
        if (StringUtils.isEmpty(dataValidScript))
            return true;

        addSenderToScriptParams(scriptContextParams); // 发件人
        scriptContextParams.put("PROCESS_ID", rpf.processId); // 业务ID
        scriptContextParams.put("PROCESS_TYPE_ID", rpf.processTypeId); // 业务类型ID
        scriptContextParams.put("PROCESS_STEP_ID", rpf.stepId); // 业务类型ID
        WFDBService.getInstance().setProcessId(rpf.processId);
        WFDBService.getInstance().setConnection(con);
        HashMap<String, String> formParams = new HashMap<String, String>();
        WFDBService.getInstance().setHttpParams(formParams);
        scriptContextParams.put("WFDBService", WFDBService.getInstance()); // 数据库服务
        ScriptService.getInstance().eval(dataValidScript, scriptContextParams);

        return true;
    }

    /**
     * 执行废除脚本
     * 
     * @param cmd
     * @param wfp
     * @param rpf
     * @return
     * @throws Exception
     */
    private boolean onProcessAbabdonEvent(final IDBCommand cmd, final WFProcess wfp, final RuningProcessInfo rpf) throws Exception {
        final String cancelScriot = wfp.getCancelEvent();
        if (StringUtils.isEmpty(cancelScriot))
            return true;
        try {
            final Map<String, Object> scriptContextParams = getScriptContextParams(cmd, rpf);
            HashMap<String, String> formParams = new HashMap<String, String>();
            initWFDBService(cmd.getConnection(), rpf, formParams);
            ScriptService.getInstance().eval(cancelScriot, scriptContextParams);
        } finally {
            initWFDBService(null, null, null);
        }
        return true;
    }

    /**
     * 执行退件操作脚本
     * 
     * @param con
     * @param wfp
     * @param rpf
     * @param activity
     * @param toUserList
     * @return
     * @throws Exception
     */
    private boolean onActivityBackEvent(final IDBCommand cmd, final WFProcess wfp, final RuningProcessInfo rpf, final WFActivity activity, final List<UserHelperClass> toUserList) throws Exception {
        final String backScript = activity.getBackEvent();
        if (StringUtils.isEmpty(backScript))
            return true;

        try {
            HashMap<String, String> formParams = new HashMap<String, String>();
            initWFDBService(cmd.getConnection(), rpf, formParams);
            final Map<String, Object> scriptContextParams = getScriptContextParams(cmd, rpf);
            scriptContextParams.put("RECIVERS", toUserList.toArray());
            scriptContextParams.put("RECIVER", toUserList.get(0));

            // 发送事件
            ScriptService.getInstance().eval(backScript, scriptContextParams);
        } finally {
            // 清除设置
            initWFDBService(null, null, null);
        }

        return true;
    }

    /**
     * 发送接收事件处理
     * 
     * @param con
     * @param wfp
     * @param rpf
     * @param fromActivity
     * @param toActivity
     * @param toUserList
     * @return
     * @throws Exception
     */
    private boolean onActivitySendAndReceiveEvent(final Connection con, final WFProcess wfp, final RuningProcessInfo rpf, final WFActivity fromActivity, final WFActivity toActivity, final List<UserHelperClass> toUserList, final Map<String, String> httpContextParams, final Map<String, Object> scriptContextParams) throws Exception {
        final String fromScript = fromActivity != null ? fromActivity.getSendEvent() : null;
        final String toScript = toActivity != null ? toActivity.getReceiveEvent() : null;
        final String finishScript = wfp.getFinishEvent();
        if (StringUtils.isEmpty(fromScript) && StringUtils.isEmpty(toScript))
            return true;
        initWFDBService(con, rpf, httpContextParams);

        scriptContextParams.put("RECIVERS", toUserList.toArray());
        scriptContextParams.put("RECIVER", toUserList.get(0));

        try {
            // 发送事件
            if (!StringUtils.isEmpty(fromScript))
                ScriptService.getInstance().eval(fromScript, scriptContextParams);

            // 接收事件
            if (!StringUtils.isEmpty(toScript))
                ScriptService.getInstance().eval(toScript, scriptContextParams);

            // 办结业务事件
            if ((toActivity != null) && (toActivity.getType() == WFActivityType.END) && !StringUtils.isEmpty(finishScript))
                ScriptService.getInstance().eval(finishScript, scriptContextParams);
        } finally {
            // 清除设置
            initWFDBService(null, null, null);
        }

        return true;
    }

    /**
     * 用户过滤事件函数
     * 
     * @param con
     * 
     * @param candidate
     * @param scriptContextParams
     * @return
     * @throws Exception
     */
    protected boolean onFilterCandidateEvent(Connection con, final WFActivity activity, final UserHelperClass candidate, final Map<String, Object> scriptContextParams) throws Exception {
        String userFilterScript = activity.getUserFilter();
        if (StringUtils.isEmpty(userFilterScript))
            return true;

        if (StringUtils.contains(userFilterScript, "SENDUSER") && !scriptContextParams.containsKey("SENDUSER"))
            addSenderToScriptParams(scriptContextParams); // 发件人

        if (StringUtils.contains(userFilterScript, "CREATOR") && !scriptContextParams.containsKey("CREATOR"))
            addCreatorToScriptParams(con, scriptContextParams); // 创件人

        // 过滤变量
        scriptContextParams.put("CANDIDATE", candidate);

        return Convert.obj2Boolean(ScriptService.getInstance().eval(userFilterScript, scriptContextParams));
    }

    /**
     * 将发送人信息添加到脚本执行上下文中
     * 
     * @param scriptContextParams
     */
    private void addSenderToScriptParams(final Map<String, Object> scriptContextParams) {
        final UserHelperClass sender = getCurrentUserHelperClass();
        scriptContextParams.put("SENDUSER", sender);
    }

    /**
     * 创建者添加到脚本执行上下文中
     * 
     * @param con
     * @param scriptContextParams
     * @throws SQLException
     */
    private void addCreatorToScriptParams(Connection con, Map<String, Object> scriptContextParams) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select b.id, b.loginname,b.realname,b.p_id,b.duty,b.ext1,b.ext2,b.ext3 from sys_workflow_r_process a,sys_userinfo b where a.create_userid = b.id and a.id=?");
            stmt.setLong(1, Convert.str2Long(scriptContextParams.get("PROCESS_ID").toString()));
            ResultSet rs = stmt.executeQuery();
            UserHelperClass creator = new UserHelperClass();
            if (rs.next()) {
                creator.id = rs.getLong(1);
                creator.name = rs.getString(2);
                creator.departmentId = rs.getLong(4);
                creator.ext1 = rs.getString("ext1");
                creator.ext2 = rs.getString("ext2");
                creator.ext3 = rs.getString("ext3");
            }
            scriptContextParams.put("CREATOR", creator);
        } finally {
            DBHELPER.closeStatement(stmt);
        }

    }

    /**
     * @return
     */
    private UserHelperClass getCurrentUserHelperClass() {
        final ClientLoginInfo loginInfo = GlobalContext.getLoginInfo();
        final UserHelperClass sender = new UserHelperClass();
        sender.id = loginInfo.getId();
        sender.departmentId = loginInfo.getDepartmentId();
        sender.name = loginInfo.getRealName();
        sender.ext1 = loginInfo.getExt1();
        sender.ext2 = loginInfo.getExt2();
        sender.ext3 = loginInfo.getExt3();
        return sender;
    }

    /**
     * 获取匹配的连接体
     * 
     * @param varCmd
     * @param wf
     * @param processInfo
     * @param httpContextParams
     * @return
     * @throws Exception
     */
    private List<WFLink> getMatchActivityLinks(final Connection con, final WFProcess wf, final RuningProcessInfo processInfo, final Map<String, String> httpContextParams, final Map<String, Object> scriptContextParams) throws Exception {
        initWFDBService(con, processInfo, httpContextParams);
        List<WFLink> result = null;
        try {
            final List<WFLink> links = wf.getLinks(processInfo.activityName);
            final List<WFLink> matchLinks = new ArrayList<WFLink>();
            final List<WFLink> blankLinks = new ArrayList<WFLink>();
            final List<WFLink> scriptLinks = new ArrayList<WFLink>();
            for (int i = 0; i < links.size(); i++) {
                final WFLink link = links.get(i);
                if (StringUtils.isEmpty(link.getScript()))
                    blankLinks.add(link);
                else {
                    Object evalObj = ScriptService.getInstance().eval(link.getScript(), scriptContextParams);
                    boolean isOK = Convert.obj2Boolean(evalObj);
                    if (isOK)
                        matchLinks.add(link);
                    else
                        scriptLinks.add(link);
                }
            }

            if (matchLinks.size() == 0)
                matchLinks.addAll(blankLinks);

            if (matchLinks.size() == 0)
                matchLinks.addAll(scriptLinks);

            // 根据优先级排序
            Collections.sort(matchLinks);
            result = matchLinks;
        } finally {
            initWFDBService(null, null, null);
        }

        return result;
    }

    /**
     * 获取所有的Activity
     * 
     * @param con
     * @param wfp
     * @param rpf
     * @param httpContextParams
     * @param scriptContextParams
     * @return
     * @throws Exception
     */
    private List<WFLink> getActivityLinks4Special(final Connection con, final WFProcess wf, final RuningProcessInfo processInfo) throws Exception {
        List<WFLink> result = new ArrayList<WFLink>();
        try {
            final List<WFLink> links = wf.getLinks();
            for (WFLink link : links) {
                if (StringUtils.equals(processInfo.activityName, link.getEndActivityName()))
                    continue;
                result.add(link);
            }
        } finally {
            initWFDBService(null, null, null);
        }

        return result;
    }

    /**
     * 获取业务员变量值列表
     * 
     * @param con
     * @param processId
     * @param scriptContextParams
     * @throws DBException
     * @throws SQLException
     */
    private void getProcessVariables(final Connection con, final WFProcess wf, final long processId, final Map<String, Object> scriptContextParams) throws Exception {
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
            getProcessVariables(cmd, wf, processId, scriptContextParams);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
    }

    /**
     * 获取业务变量值列表
     * 
     * @param cmd
     * @param wf
     * @param processId
     * @param scriptContextParams
     * @throws SQLException
     * @throws DBException
     */
    private void getProcessVariables(final IDBCommand cmd, final WFProcess wf, final long processId, final Map<String, Object> scriptContextParams) throws Exception {
        cmd.setParam("process_id", processId);
        cmd.executeQuery();
        for (final WFVariable var : wf.getVariables()) {
            final WFVariableDataType type = var.getDataType();
            if (type == WFVariableDataType.STRING)
                scriptContextParams.put(var.getName(), "");
            else if (type == WFVariableDataType.BOOLEAN)
                scriptContextParams.put(var.getName(), false);
            else if (type == WFVariableDataType.NUMBER)
                scriptContextParams.put(var.getName(), null);
            else if (type == WFVariableDataType.DATETIME)
                scriptContextParams.put(var.getName(), null);
        }

        while (cmd.next()) {
            final String varName = cmd.getString(1);
            final WFVariable var = wf.getVariable(varName);
            if (var == null)
                continue;
            String v = cmd.getString(2);
            if (v == null)
                v = "";
            final WFVariableDataType type = var.getDataType();
            if (type == WFVariableDataType.STRING)
                scriptContextParams.put(varName, v);
            else if (type == WFVariableDataType.BOOLEAN)
                scriptContextParams.put(varName, Convert.obj2Boolean(v));
            else if (type == WFVariableDataType.NUMBER)
                scriptContextParams.put(varName, Convert.try2Double(v, 0));
            else if (type == WFVariableDataType.DATETIME)
                scriptContextParams.put(varName, Convert.try2Date(v, null));
        }
    }

    @Override
    public void calcActivityVariables(final String processTypeId, final String activityName, final String processId, final String step_id, final Map<String, String> httpParams, final Connection con) throws Exception {
        final WFProcess wfp = getProcess(Convert.try2Long(processTypeId, 0));
        if (wfp == null)
            return;
        final WFActivity activity = wfp.getActivity(activityName);
        if (activity == null)
            return;

        WFDBService.getInstance().setProcessId(Convert.str2Long(processId));
        WFDBService.getInstance().setConnection(con);
        WFDBService.getInstance().setHttpParams(httpParams);

        final HashMap<String, Object> scriptContextParams = new HashMap<String, Object>();
        scriptContextParams.put("WFDBService", WFDBService.getInstance());
        scriptContextParams.put("USER_ID", httpParams.get("USER_ID"));
        scriptContextParams.put("USER_NAME", httpParams.get("USER_NAME"));
        scriptContextParams.put("DEPARTMENT_ID", httpParams.get("DEPARTMENT_ID"));
        scriptContextParams.put("DEPARTMENT_NAME", httpParams.get("DEPARTMENT_NAME"));
        scriptContextParams.put("PROCESS_ID", processId); // 业务ID
        scriptContextParams.put("PROCESS_TYPE_ID", processTypeId); // 业务类型ID
        getProcessVariables(con, wfp, Convert.str2Long(processId), scriptContextParams);

        IDBCommand cmd = null;
        final Iterator<Map.Entry<String, String>> interator = activity.getVariableValueScript().entrySet().iterator();
        try {
            while (interator.hasNext()) {
                final Map.Entry<String, String> entry = interator.next();
                final String varName = entry.getKey();
                final WFVariable var = wfp.getVariable(varName);
                if (var == null)
                    continue;
                final WFVariableDataType varDataType = var.getDataType();
                final String varScript = entry.getValue();
                if (!StringUtils.isEmpty(varScript)) {
                    String scriptVal = "";
                    final Object obj = ScriptService.getInstance().eval(varScript, scriptContextParams);
                    if (varDataType == WFVariableDataType.BOOLEAN)
                        scriptVal = String.valueOf(Convert.obj2Boolean(obj));
                    else if (varDataType == WFVariableDataType.NUMBER)
                        scriptVal = String.valueOf(Convert.obj2Number(obj));
                    else if (varDataType == WFVariableDataType.DATETIME)
                        scriptVal = Convert.datetime2Str(Convert.obj2Date(obj));
                    else
                        scriptVal = obj == null ? "" : String.valueOf(obj);

                    if (cmd == null)
                        cmd = DBHELPER.getCommand(con, getSQL4SaveProcessVariableValue());
                    cmd.setParam("process_id", Convert.str2Long(processId));
                    cmd.setParam("var_name", varName);
                    cmd.setParam("var_value", scriptVal);
                    cmd.execute();
                }
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
    }

    /**
     * 保存业务变量值
     * 
     * @param con
     * @param varName
     * @param scriptVal
     * @throws Exception
     */
    public void saveProcessVariableValue(final Connection con, final long process_id, final String varName, final String scriptVal) throws Exception {
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, getSQL4SaveProcessVariableValue());
            cmd.setParam("process_id", process_id);
            cmd.setParam("var_name", varName);
            cmd.setParam("var_value", scriptVal);
            cmd.executeQuery();
        } finally {
            DBHELPER.closeCommand(cmd);
        }
    }

    @Override
    public JSONObject getCaseStepFormInfo(final long stepId, final boolean isSkipForms) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, getSQL4GetStepFormsInfo());
            cmd.setParam("step_id", stepId);
            if (cmd.executeQuery() && cmd.next()) {
                final long processTypeId = cmd.getLong("process_type_id");
                final String activityName = cmd.getString("activity_name");
                final long processId = cmd.getLong("process_id");

                final WFProcess wf = getProcess(processTypeId);
                if (wf != null) {
                    final WFActivity activity = wf.getActivity(activityName);
                    if (activity != null) {
                        json.put("activityCaption", activity.getCaption());

                        final JSONObject paramsMap = new JSONObject();

                        // 处理表单
                        for (int i = 0; i < activity.getForms().size(); i++) {
                            final WFFormSetting wfs = activity.getForms().get(i);
                            if (!isSkipForms) {
                                final JSONObject formJson = new JSONObject();
                                formJson.put("id", wfs.getId());
                                formJson.put("caption", wfs.getCaption());

                                // 表单控件权限
                                for (int j = 0; j < wfs.getControls().size(); j++) {
                                    final WFFormSettingItem item = wfs.getControls().get(j);
                                    final JSONObject itemJson = new JSONObject();
                                    itemJson.put("readonly", item.isReadonly());
                                    itemJson.put("visible", !item.isHidden());
                                    itemJson.put("name", item.getName());
                                    JSONUtils.append(formJson, "controls", itemJson);
                                    // formJson.append("controls", itemJson);
                                }
                                JSONUtils.append(json, "forms", formJson);
                            }

                            // 获取参数
                            final Map<String, String> formParamValues = wf.getFormParams(String.valueOf(wfs.getId()));
                            final Iterator<Map.Entry<String, String>> iterator = formParamValues.entrySet().iterator();
                            while (iterator.hasNext()) {
                                final Map.Entry<String, String> entry = iterator.next();
                                final String paramName = entry.getKey();
                                final String paramValue = entry.getValue();
                                if (StringUtils.equals(paramValue, "REQ.WORKFLOW_PROCESS_ID"))
                                    json.put(paramName, processId);
                                else if (StringUtils.equals(paramValue, "REQ.WORKFLOW_STEP_ID"))
                                    json.put(paramName, stepId);
                                paramsMap.put(paramName, paramValue);
                            }
                        }
                        json.put("paramMap", paramsMap);
                    }
                }

                json.put("activity_name", activityName);
                json.put("process_type_id", processTypeId);
                json.put("process_id", processId);

                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 废除业务
     * 
     * @throws Exception
     */
    @Override
    public JSONObject restoreProcessByStep(final long[] step_ids) throws Exception {
        final long user_id = GlobalContext.getLoginInfo().getId();
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        boolean isOK = false;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);
            cmd = DBHELPER.getCommand(con, getSQL4RestoreProcessByStep());

            cmd.setParam("user_id", user_id);
            for (final long step_id : step_ids) {
                cmd.setParam("step_id", step_id);
                cmd.execute();
            }
            DBHELPER.commit(con);
            isOK = true;
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            DBHELPER.rollback(con, false);
            JSONUtils.except2JSON(json, e);
        } finally {
            if (!isOK && (con != null))
                con.rollback();
            DBHELPER.closeCommand(cmd);
            DBHELPER.endTransaction(con);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 新建一个工作流CASE
     */
    @Override
    public JSONObject createCase(final long process_type_id, final long ui_id) throws Exception {
        final long user_id = GlobalContext.getLoginInfo().getId();
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        IDBCommand varCmd = null;
        IDBCommand loggerCmd = null;
        try {
            final WFProcess wfp = id2WFProcess.get(process_type_id);
            if ((wfp != null) && StringUtils.equals(wfp.getStatus(), WFProcess.STATUS_IS_RUNING)) { // 业务处于可执行状态
                final Date currentDate = new Date();

                con = DBHELPER.getConnection();
                DBHELPER.beginTransaction(con);
                varCmd = DBHELPER.getCommand(con, getSQL4ListProcessVariables());
                loggerCmd = DBHELPER.getCommand(con, getLoggerSQL());

                final long process_id = generalProcessId(con);
                cmd = DBHELPER.getCommand(con, getSQL4CreateCase());
                cmd.setParam("process_id", process_id);
                cmd.setParam("user_id", user_id);
                cmd.setParam("process_type_id", process_type_id);
                cmd.setParam("activity_name", wfp.getFirstActivity().getName());
                cmd.setParam("createdate", currentDate);
                cmd.setParam("process_durtion", WFCalendarService.getInstance().getDurationDate(currentDate, wfp.getTimeLimit()));
                cmd.setParam("activity_duration", WFCalendarService.getInstance().getDurationDate(currentDate, wfp.getFirstActivity().getTimeLimit()));
                cmd.execute();

                final RuningProcessInfo rpi = getRuningProcessInfo(con, process_id);
                final List<UserHelperClass> users = new ArrayList<UserHelperClass>();
                users.add(getCurrentUserHelperClass());

                final WFActivity activity = wfp.getFirstActivity();
                onActivitySendAndReceiveEvent(con, wfp, rpi, null, activity, users, new HashMap<String, String>(), getScriptContextParams(varCmd, rpi));

                // 日志
                logger(loggerCmd, rpi, LOGGER_NEW_CASE);

                json.put("process_id", process_id);
                json.put("r", true);
            } else
                json.put("msg", "业务已经被删除或不再运行状态,请刷新页面后重试！");
            // begin
            // estudio_workflow.create_case(:user_id,:process_type_id,:activity_name);end;
            DBHELPER.commit(con);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            DBHELPER.rollback(con, false);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(varCmd);
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeCommand(loggerCmd);
            DBHELPER.endTransaction(con);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject getProcessList(final long user_id, final long ui_id, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final WorkFlowUIDefine workflowUiDefine = WorkFlowUIDefineService.getInstance().getUIDefine(ui_id);
            final SQLDefine4Portal sqlDefine = workflowUiDefine.getSqlDefine();
            final int page = Convert.try2Int(params.get("p"), 1);
            final int recordPerPage = Convert.try2Int(params.get("r"), 2500);
            DataService4Portal.getInstance().getGridData4Flex(con, sqlDefine, params, json, page, recordPerPage, true, "-1", workflowUiDefine.isPagination(),false);

            final JSONObject filterControlItems = new JSONObject();
            json.put("filterComboboxItems", filterControlItems);
            final JSONObject blankJson = new JSONObject();
            blankJson.put("LABEL", " ");
            blankJson.put("ID", null);
            final Map<String, Object> cmdParams = DataService4Portal.getInstance().getSqlDefineParamValues(sqlDefine, params);
            for (final Map.Entry<String, String> entry : workflowUiDefine.getComboboxFilterControl2SQL().entrySet()) {
                final String name = entry.getKey();
                final String sql = entry.getValue();
                final JSONArray items = DBHELPER.executeQuery(sql, cmdParams, con);
                items.add(0, blankJson);
                filterControlItems.put(name, items);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 从数据库加载工作流定义
     * 
     * @param id
     * @return
     * @throws DBException
     * @throws SQLException
     */
    private WFProcess loadProcessFromDB(final long id, final Connection con) throws Exception {
        Connection tempCon = null;
        WFProcess result = null;

        IDBCommand cmdProcess = null; // 工作流对象
        IDBCommand cmdAction = null;
        IDBCommand cmdLinks = null;
        IDBCommand cmdRoles = null;
        IDBCommand cmdVariables = null;
        IDBCommand cmdForms = null;

        try {
            tempCon = con;
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();

            // 流程总定义
            cmdProcess = DBHELPER.getCommand(tempCon, getSQL4Process());
            cmdVariables = DBHELPER.getCommand(tempCon, getSQL4Variables());
            cmdForms = DBHELPER.getCommand(tempCon, getSQL4Forms());
            cmdAction = DBHELPER.getCommand(tempCon, getSQL4Action());
            cmdRoles = DBHELPER.getCommand(tempCon, getSQL4Roles());
            cmdLinks = DBHELPER.getCommand(tempCon, getSQL4Links());

            // 加载数据
            result = loadProcessFromDB(id, cmdProcess, cmdAction, cmdLinks, cmdRoles, cmdVariables, cmdForms);

        } finally {
            DBHELPER.closeCommand(cmdProcess);
            DBHELPER.closeCommand(cmdAction);
            DBHELPER.closeCommand(cmdLinks);
            DBHELPER.closeCommand(cmdRoles);
            DBHELPER.closeCommand(cmdVariables);
            DBHELPER.closeCommand(cmdForms);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 加载数据
     * 
     * @param id
     * @param result
     * @param cmdProcess
     * @param cmdAction
     * @param cmdLinks
     * @param cmdRoles
     * @param cmdVariables
     * @param cmdForms
     * @return
     * @throws SQLException
     * @throws DBException
     * @throws JSONException
     */
    private WFProcess loadProcessFromDB(final long id, final IDBCommand cmdProcess, final IDBCommand cmdAction, final IDBCommand cmdLinks, final IDBCommand cmdRoles, final IDBCommand cmdVariables, final IDBCommand cmdForms) throws Exception {
        WFProcess result = null;
        cmdProcess.setParam("id", id);
        cmdProcess.executeQuery();
        if (cmdProcess.next()) {
            result = new WFProcess();
            result.setId(id);
            result.setName(cmdProcess.getString("NAME"));
            result.setDescript(cmdProcess.getString("DESCRIPT"));
            result.setVersion(cmdProcess.getInt("VERSION"));
            result.setStatus(cmdProcess.getString("STATUS"));
            result.setCreateDate(cmdProcess.getDateTime("CREATEDATE"));
            result.setModifyDate(cmdProcess.getDateTime("LASTMODIFYDATE"));
            result.getTimeLimit().setTime(cmdProcess.getInt("limit_num"));
            result.getTimeLimit().setUnit(WFTimeUnit.fromChineseStr(cmdProcess.getString("limit_unit")));
            result.setDesignProperty(Convert.bytes2Str(cmdProcess.getBytes("property")));

            // 变量
            cmdVariables.setParam("pid", id);
            cmdVariables.executeQuery();
            while (cmdVariables.next()) {
                final WFVariable var = new WFVariable();
                var.setName(cmdVariables.getString("name"));
                var.setDescript(cmdVariables.getString("paramcomment"));
                var.setDataType(WFUtils.designType2WFDataType(cmdVariables.getString("datatype")));

                result.getVariables().add(var);
            }

            // 表单
            loadRolesFromDB(cmdRoles, id, result.getRoles());

            // 表单
            loadFormsFromDB(cmdForms, id, result.getForms());

            // 活动体定义
            cmdAction.setParam("PID", id);
            cmdAction.executeQuery();
            while (cmdAction.next()) {
                final WFActivity action = new WFActivity();
                action.setId(cmdAction.getLong("id"));
                action.setName(cmdAction.getString("name"));
                action.setCaption(cmdAction.getString("caption"));
                action.setDescript(cmdAction.getString("descript"));
                action.setSplit(cmdAction.getInt("issplit") == 1);
                action.setJoin(cmdAction.getInt("isjoin") == 1);
                action.setScript(Convert.bytes2Str(cmdAction.getBytes("taskscript")));
                action.setType(WFUtils.designType2WFActivityType(cmdAction.getString("type")));
                action.getTimeLimit().setTime(cmdAction.getInt("limit_num"));
                action.getTimeLimit().setUnit(WFTimeUnit.fromChineseStr(cmdAction.getString("limit_unit")));
                action.setUserFilter(JSCompress.getInstance().compress(Convert.bytes2Str(cmdAction.getBytes("user_filter"))));
                action.setReceiveEvent(JSCompress.getInstance().compress(Convert.bytes2Str(cmdAction.getBytes("receive_event"))));
                action.setSendEvent(JSCompress.getInstance().compress(Convert.bytes2Str(cmdAction.getBytes("send_event"))));
                // 附加属性
                final JSONObject actionDesignProperty = result.getActivityDesignProperty(action.getName()).getJSONObject("Property");

                if (actionDesignProperty.containsKey("multiReciver"))
                    action.setMultiReciver(actionDesignProperty.getBoolean("multiReciver"));
                if (actionDesignProperty.containsKey("isBackAble"))
                    action.setBackAble(actionDesignProperty.getBoolean("isBackAble")); // 是否允许退件
                if (actionDesignProperty.containsKey("isIdeaAble"))
                    action.setIdeaAble(actionDesignProperty.getBoolean("isIdeaAble")); // 是否允许签署意见
                if (actionDesignProperty.containsKey("smartSend"))
                    action.setSmartSend(actionDesignProperty.getBoolean("smartSend"));
                if (actionDesignProperty.containsKey("roleAccept"))
                    action.setRoleAccept(actionDesignProperty.getBoolean("roleAccept"));
                if (actionDesignProperty.containsKey("smartKip"))
                    action.setSmartSkip(actionDesignProperty.getBoolean("smartKip"));
                if (actionDesignProperty.containsKey("validDataEvent"))
                    action.setValidDataEvent(JSCompress.getInstance().compress(actionDesignProperty.getString("validDataEvent")));
                if (actionDesignProperty.containsKey("backEvent"))
                    action.setBackEvent(JSCompress.getInstance().compress(actionDesignProperty.getString("backEvent")));

                // 变量
                final String FVars = Convert.bytes2Str(cmdAction.getBytes("variables"));
                if (!StringUtils.isEmpty(FVars)) {
                    final JSONArray FArray = JSONUtils.parserJSONArray(FVars);
                    for (int i = 0; i < FArray.size(); i++) {
                        final JSONObject json = FArray.getJSONObject(i);
                        final String value = json.getString("Value");
                        if (!StringUtils.isEmpty(value))
                            action.getVariableValueScript().put(json.getString("Name"), value);
                    }
                }

                // 角色
                if ((action.getType() == WFActivityType.END) || (action.getType() == WFActivityType.BEGIN))
                    action.getRoles().add(-65535L);
                else
                    loadRolesFromDB(cmdRoles, action.getId(), action.getRoles());

                // 表单
                loadFormsFromDB(cmdForms, action.getId(), action.getForms());

                result.getActivities().add(action);
            }

            // 连接体定义
            cmdLinks.setParam("pid", id);
            cmdLinks.executeQuery();
            while (cmdLinks.next()) {
                final WFLink link = new WFLink();
                link.setId(cmdLinks.getLong("id"));
                link.setName(cmdLinks.getString("name"));
                link.setCaption(cmdLinks.getString("caption"));
                link.setDescript(cmdLinks.getString("descript"));
                link.setStartActivityName(cmdLinks.getString("SOURCE_ID"));
                link.setEndActivityName(cmdLinks.getString("TARGET_ID"));
                link.setScript(Convert.bytes2Str(cmdLinks.getBytes("link_condition")));
                link.setPriority(getLinkPriority(result.getLinkDesignProperty(link.getName())));
                result.getLinks().add(link);
            }
        }
        return result;
    }

    // 获取连接体的优先级
    private int getLinkPriority(final JSONObject json) {
        int result = 0;
        if (json.containsKey("Property")) {
            final JSONObject propertyJson = json.getJSONObject("Property");
            if (propertyJson.containsKey("priority"))
                result = Convert.try2Int(propertyJson.getString("priority"), 0);
        }
        return result;
    }

    /**
     * 获取表单定义
     * 
     * @param cmdForms
     * @param id
     * @param forms
     * @throws DBException
     * @throws SQLException
     * @throws JSONException
     */
    private void loadFormsFromDB(final IDBCommand cmdForms, final long id, final List<WFFormSetting> forms) throws Exception {
        cmdForms.setParam("pid", id);
        cmdForms.executeQuery();
        while (cmdForms.next()) {
            final WFFormSetting form = new WFFormSetting();
            form.setId(cmdForms.getLong("id"));
            form.setCaption(cmdForms.getString("caption"));
            final JSONObject json = JSONUtils.parserJSONObject(Convert.bytes2Str(cmdForms.getBytes("controls")));
            final String formName = "Form_" + form.getId();
            @SuppressWarnings("rawtypes")
            final Iterator iterator = json.keySet().iterator();
            while (iterator.hasNext()) {
                final String controlName = (String) iterator.next();
                final String right = json.getString(controlName);
                final boolean readonly = right.charAt(0) == '1';
                final boolean hidden = right.charAt(1) == '1';
                final boolean require = ((right.length() > 2) && (right.charAt(2) == '1')) ? true : false;
                if (controlName.equals(formName)) {
                    form.setReadonly(readonly);
                    form.setHidden(hidden);
                    form.setRequire(require);
                } else {
                    final WFFormSettingItem item = new WFFormSettingItem(controlName, hidden, readonly, require);
                    form.getControls().add(item);
                }
            }
            forms.add(form);
        }
    }

    /**
     * 获取表单是否只读
     */
    @Override
    public boolean isFormReadOnly(final long processTypeID, final String activityName, final long formId) throws Exception {
        final WFFormSetting wf = getProcess(processTypeID).getActivity(activityName).getFormSetting(formId);
        return (wf == null) || wf.isReadonly();
    }

    /**
     * 读取角色信息
     * 
     * @param cmdRoles
     * @param id
     * @param roles
     * @throws DBException
     * @throws SQLException
     */
    private void loadRolesFromDB(final IDBCommand cmdRoles, final long id, final List<Long> roles) throws Exception {
        cmdRoles.setParam("pid", id);
        cmdRoles.executeQuery();
        while (cmdRoles.next())
            roles.add(cmdRoles.getLong("roleid"));
    }

    @Override
    public void notifyWFProcessChange(final long id, final boolean isDeleted, final Connection con) throws Exception {
        synchronized (id2WFProcess) {
            if (id2WFProcess.containsKey(id)) {
                processList.remove(id2WFProcess.get(id));
                id2WFProcess.remove(id);
            }
            if (!isDeleted) {
                final WFProcess process = loadProcessFromDB(id, con);
                if (process != null) {
                    id2WFProcess.put(id, process);
                    processList.add(process);
                }
            }
        }
    }

    @Override
    public void loadAllRunableProcess() throws Exception {
        Connection con = null;
        IDBCommand cmd = null;

        IDBCommand cmdProcess = null; // 工作流对象
        IDBCommand cmdAction = null;
        IDBCommand cmdLinks = null;
        IDBCommand cmdRoles = null;
        IDBCommand cmdVariables = null;
        IDBCommand cmdForms = null;

        try {
            synchronized (id2WFProcess) {
                id2WFProcess.clear();
                processList.clear();

                con = DBHELPER.getConnection();

                cmd = DBHELPER.getCommand(con, getSQL4List());
                cmdProcess = DBHELPER.getCommand(con, getSQL4Process());
                cmdVariables = DBHELPER.getCommand(con, getSQL4Variables());
                cmdForms = DBHELPER.getCommand(con, getSQL4Forms());
                cmdAction = DBHELPER.getCommand(con, getSQL4Action());
                cmdRoles = DBHELPER.getCommand(con, getSQL4Roles());
                cmdLinks = DBHELPER.getCommand(con, getSQL4Links());

                cmd.executeQuery();
                while (cmd.next()) {
                    final long id = cmd.getLong(1);
                    final WFProcess process = loadProcessFromDB(id, cmdProcess, cmdAction, cmdLinks, cmdRoles, cmdVariables, cmdForms);
                    if (process != null) {
                        id2WFProcess.put(id, process);
                        processList.add(process);
                    }
                }
            }
        } finally {
            DBHELPER.closeCommand(cmdProcess);
            DBHELPER.closeCommand(cmdAction);
            DBHELPER.closeCommand(cmdLinks);
            DBHELPER.closeCommand(cmdRoles);
            DBHELPER.closeCommand(cmdVariables);
            DBHELPER.closeCommand(cmdForms);
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
    }

    /**
     * 根据角色列表获取能新建的工作流对象列表
     * 
     * @param roles
     * @return
     */
    public List<Long> getWFProcessWhoCanCreateIt(final List<Long> roles) {
        final boolean isAdmin = roles.indexOf(-1L) != -1;
        final List<Long> result = new ArrayList<Long>();
        final List<WFProcess> list = getAllProcess();
        for (int i = 0; i < list.size(); i++) {
            final WFProcess process = list.get(i);
            if (StringUtils.equals(process.getStatus(), WFProcess.STATUS_IS_RUNING)) {
                final WFActivity firstActivity = process.getFirstActivity();
                if ((firstActivity != null) && (isAdmin || WFUtils.hasIntersection(firstActivity.getRoles(), roles)))
                    result.add(process.getId());
            }
        }
        return result;
    }

    @Override
    public List<WFProcess> getAllProcess() {
        return processList;
    }

    @Override
    public JSONObject getWFProcessTreeJSON() throws Exception {
        final List<Long> filterRoles = GlobalContext.getLoginInfo().getRoles();
        final JSONObject json = new JSONObject();
        final List<Long> list = getWFProcessWhoCanCreateIt(filterRoles);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, getSQL4ObjectTree());
            getWFProcessTreeJSONFromDB(cmd, -1l, list, json);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取业务树
     * 
     * @param cmd
     * @param pid
     * @param list
     * @param json
     * @throws DBException
     * @throws SQLException
     * @throws JSONException
     */
    private void getWFProcessTreeJSONFromDB(final IDBCommand cmd, final long pid, final List<Long> filterList, final JSONObject json) throws Exception {
        cmd.setParam("pid", pid);
        cmd.executeQuery();
        final List<JSONObject> folders = new ArrayList<JSONObject>();
        while (cmd.next()) {
            final long id = cmd.getLong("id");
            final int type = cmd.getInt("type");
            if (type == TYPE_WORKFLOW) {
                if (filterList.indexOf(id) == -1)
                    continue;
                final JSONObject itemJSON = new JSONObject();
                itemJSON.put("id", id);
                itemJSON.put("caption", cmd.getString("caption"));
                itemJSON.put("type", TYPE_WORKFLOW);
                JSONUtils.append(json, "process", itemJSON);
                // json.append("process", itemJSON);
            } else {
                final JSONObject itemJSON = new JSONObject();
                itemJSON.put("id", id);
                itemJSON.put("caption", cmd.getString("caption"));
                itemJSON.put("type", type);
                folders.add(itemJSON);
            }
        }
        if (!folders.isEmpty())
            for (int i = 0; i < folders.size(); i++) {
                final JSONObject parentJson = folders.get(i);
                getWFProcessTreeJSONFromDB(cmd, parentJson.getLong("id"), filterList, parentJson);
                if ((parentJson.getInt("type") == TYPE_OPERATION) && parentJson.containsKey("process"))
                    JSONUtils.append(json, "operation", parentJson);// json.append("operation",
                // parentJson);
                if ((parentJson.getInt("type") == TYPE_FOLDER) && (parentJson.containsKey("folder") || parentJson.containsKey("operation")))
                    JSONUtils.append(json, "folder", parentJson);
                // json.append("folder", parentJson);
            }
    }

    @Override
    public JSONObject getProcessMessage(long processOrStepId) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            String sql = "select a.id,\n" + //
                    "       a.msg_type,\n" + //
                    "       a.content,\n" + //
                    "       to_char(a.REGDATE, 'YYYY-MM-DD HH24:MI') regdate,\n" + //
                    "       b.realname\n" + //
                    "  from sys_workflow_r_process_msg a, sys_userinfo b\n" + //
                    " where a.user_id = b.id\n" + //
                    "   and a.process_id in\n" + //
                    "       (select distinct process_id from sys_workflow_r_step where id = " + processOrStepId + ")";//

            con = DBHELPER.getConnection();
            json.put("rows", DBHELPER.executeQuery(sql, con));
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject saveProcessMessage(long processOrStepId, int type, String content) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        IDBCommand ucmd = null;
        try {
            String sql = "insert into sys_workflow_r_process_msg\n" + //
                    "  (id, process_id, user_id, msg_type, content, regdate,step_id)\n" + //
                    "values\n" + //
                    "  (:uid,\n" + //
                    "   (select distinct process_id\n" + //
                    "      from sys_workflow_r_step\n" + //
                    "     where id = :process_id),\n" + //
                    "   :user_id,\n" + //
                    "   :msg_type,\n" + //
                    "   :content,\n" + //
                    "   sysdate,:step_id)";

            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, sql);
            cmd.setParam("uid", DBHELPER.getUniqueID(con));
            cmd.setParam("process_id", processOrStepId);
            cmd.setParam("user_id", GlobalContext.getLoginInfo().getId());
            cmd.setParam("msg_type", type);
            cmd.setParam("content", content);
            cmd.setParam("step_id", processOrStepId);
            cmd.execute();

            ucmd = DBHELPER.getCommand(con, "update SYS_WORKFLOW_R_STEP set is_exists_msg =1 where id=:id");
            ucmd.setParam("id", processOrStepId);
            ucmd.execute();

            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(ucmd);
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 发送通知消息
     * 
     * @param reciverId
     * @param content
     */
    // private void postNotifyMessageToOther(long reciverId, String content) {
    // List<Long> recivers = new ArrayList<Long>();
    // recivers.add(reciverId);
    // MessageService.getInstance().sendMessage(WORKFLOW_ENGINEER_USERID,
    // WORKFLOW_ENGINEER_USERNAME, recivers, content);
    // }

    /**
     * 发送通知消息
     * 
     * @param reciverIds
     * @param content
     */
    // private void postNotifyMessageToOtherUser(long[] reciverIds, String
    // content) {
    // List<Long> recivers = new ArrayList<Long>();
    // for (int i = 0; i < reciverIds.length; i++)
    // recivers.add(reciverIds[i]);
    // MessageService.getInstance().sendMessage(WORKFLOW_ENGINEER_USERID,
    // WORKFLOW_ENGINEER_USERNAME, recivers, content);
    // }

    /**
     * 日志记录
     * 
     * @param con
     * @param ip
     * @param userId
     * @param stepId
     * @param type
     * @throws Exception
     */
    private void logger(final IDBCommand cmd, final RuningProcessInfo rpf, final int type) throws Exception {
        cmd.setParam("id", DBHELPER.getUniqueID(cmd.getConnection(), "seq_for_j2ee_workflow_logger"));
        cmd.setParam("ip", GlobalContext.getClientInfo().getIpAddress());
        cmd.setParam("mac_name", GlobalContext.getClientInfo().getMacName());
        cmd.setParam("type", type);
        cmd.setParam("user_id", GlobalContext.getLoginInfo().getId());
        cmd.setParam("user_name", GlobalContext.getLoginInfo().getRealName());
        cmd.setParam("step_id", rpf.stepId);
        cmd.setParam("process_id", rpf.processId);
        cmd.setParam("process_type_id", rpf.processTypeId);
        cmd.setParam("action_caption", rpf.activityName);
        cmd.setParam("sessionid", GlobalContext.getLoginInfo().getSessionId());
        cmd.execute();
    }

    private static final int LOGGER_NEW_CASE = 0;
    private static final int LOGGER_SEND_CASE = 1;
    private static final int LOGGER_BACK_CASE = 2;
    private static final int LOGGER_ABANDON_CASE = 3;
    private static final int LOGGER_BACK_CASE_TO_CREATOR = 4;
}
