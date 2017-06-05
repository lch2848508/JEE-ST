// 定义同工作流相关的JAVAScript函数
// -----------------------------------------------------------------------------------------------------------
function getWorkFlowUIDefine(id) {
    id = id[0];
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "getUIDefine",
        id : id
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -----------------------------------------------------------------------------------------------------------
// 获取能创建的流程列表
function getCanCreateProcessList() {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "getCanCreateProcessList"
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -----------------------------------------------------------------------------------------------------------
// 新建一个case
function createWorkFlowProcess(params) {
    // {typeId:v_processTypeId,ui_id:_ui_id}
    var processTypeId = params[0].typeId;
    var ui_id = params[0].ui_id;
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "newCase",
        process_type_id : processTypeId,
        ui_id : ui_id
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ------------------------------------------------------------------------------------------------------------
// 读取case列表
function loadWorkFlowProcessList(params) {
    // {typeId:v_processTypeId,ui_id:_ui_id}
    var ui_id = params[0].ui_id;
    var r = params[0].r;
    var p = params[0].p;
    var filterParmas = params[0].filterParams;
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "loadWorkFlowProcessList",
        r : r,
        p : p,
        ui_id : ui_id
    };
    for (var k in filterParmas)
        params[k] = filterParmas[k];
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ---------------------------------------------------------------------------------------------------------------
// 废弃业务
function abandonCaseList(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "abandoProcessList",
        step_ids : params[0].step_ids
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ---------------------------------------------------------------------------------------------------------------
// 恢复业务
function restoreCaseList(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "restoreProcessList",
        step_ids : params[0].step_ids
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ---------------------------------------------------------------------------------------------------------------
var CASESTEPFORMCACHE = {};
// ---------------------------------------------------------------------------------------------------------------
// 获取环节绑定的表单信息
function getCaseStepFormInfo(params) {
    params = params[0];
    var step_id = params.step_id;
    var actionName = params.actionName;

    var result = null;
    var URL = "../client/workflow";
    var formDefine = null;
    if (!StringUtils.isEmpty(actionName))
        formDefine = CASESTEPFORMCACHE[actionName];
    if (formDefine)
        return formDefine;
    var params = {
        o : "getCaseStepFormInfo",
        step_id : step_id
    };
    $.post(URL, params, function(text) {
                var json = getObjectAjaxValue(text);
                if (json) {
                    result = json;
                    if (!StringUtils.isEmpty(actionName))
                        CASESTEPFORMCACHE[actionName] = result;
                }
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ----------------------------------------------------------------------------------------------------------------
// 业务发送 批发
function sendCaseToOthers(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "batchSend",
        step_ids : params[0].step_ids
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ----------------------------------------------------------------------------------------------------------------
// 业务发送 单发
function sendSingleCase(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "send",
        step_id : params[0].step_id
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ----------------------------------------------------------------------------------------------------------------
// 业务多发
function batchSendCase(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "batchsend",
        infos : params[0]
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ----------------------------------------------------------------------------------------------------------------
// 业务发送 单发
function backCaseS(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "back",
        step_ids : params[0].step_ids
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ----------------------------------------------------------------------------------------------------------------
function sendSingleCaseToOther(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "sendSingleCaseToOther",
        step_id : params[0].step_id,
        activityName : params[0].activityName,
        activityCaption : params[0].activityCaption,
        user_ids : params[0].user_ids.join(","),
        user_names : params[0].user_names.join(",")
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -----------------------------------------------------------------------------------------------------------------
// 获取流程图
function getProcssDiagram(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "getDiagram",
        step_id : params[0].step_id
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -------------------------------------------------------------------------------------------------------------------
function ideaProcess(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "saveIde",
        step_id : params[0].step_id,
        idea_content : params[0].idea_content
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ----------------------------------------------------------------------------------------------------------------------
function getProcessIdeas(params) {
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "getIdea",
        step_id : params[0].step_id
    };
    $.post(URL, params, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ----------------------------------------------------------------------------------------------------------------------
function getWorkFlowCommonWords() {
    return ["同意", "不同意", "请", "协助", "认真", "办理", "务必", "这个"];
}
// ----------------------------------------------------------------------------------------------------------------------
// 动态创建一个工作流业务
function dynamicCreateWorkFlowProcess(processTypeId, processTypeName, portalName, callFunction) {
    var ui_id = getWorkFlowUiIdByPortalName(portalName);
    var result = null;
    var URL = "../client/workflow";
    var params = {
        o : "newCase",
        process_type_id : processTypeId,
        ui_id : ui_id
    };
    $.post(URL, params, function(text) {
                var json = getObjectAjaxValue(text, true);
                if (json) {
                    var process_id = json.process_id;
                    if (callFunction)
                        callFunction(process_id);
                    GLOBAL.executePortalFunction(portalName, "selectNewProcessByProcessID", {
                                process_id : process_id,
                                label : processTypeName
                            });
                }
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ------------------------------------------------------------------------------------------------------------------------
function signProcess(params) {
    var result = null;
    var params = params[0];
    var url = "../client/workflow";
    params.o = "signCases";
    $.post(url, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -------------------------------------------------------------------------------------------------------------------------
// 发送案件
function WORKFLOW_SEND_CASE(params) {
    var processStepInfos = params[0];
    var url = "../client/workflow";
    $.post(url, {
                o : "WORKFLOW_SEND_CASE",
                processStepInfos : processStepInfos
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// --------------------------------------------------------------------------------------------------------------------------
// 自由发送流程
function WORKFLOW_SPECIAL_SEND_CASE(params) {
    var processStepInfos = params[0];
    var url = "../client/workflow";
    $.post(url, {
                o : "WORKFLOW_SPECIAL_SEND_CASE",
                processStepInfos : processStepInfos
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -------------------------------------------------------------------------------------------------------------------------
// 废弃案件
function WORKFLOW_ABANDON_CASE(params) {
    var processStepInfos = params[0];
    var url = "../client/workflow";
    $.post(url, {
                o : "WORKFLOW_ABANDON_CASE",
                processStepInfos : processStepInfos
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -------------------------------------------------------------------------------------------------------------------------
// 废弃案件
function WORKFLOW_BACK_CASE(params) {
    var processStepInfos = params[0];
    var url = "../client/workflow";
    $.post(url, {
                o : "WORKFLOW_BACK_CASE",
                processStepInfos : processStepInfos
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// -------------------------------------------------------------------------------------------------------------------------
//退回按键到创建人
function WORKFLOW_BACK_CASE_TO_CREATOR(params) {
    var processStepInfos = params[0];
    var url = "../client/workflow";
    $.post(url, {
                o : "WORKFLOW_BACK_CASE_TO_CREATOR",
                processStepInfos : processStepInfos
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// --------------------------------------------------------------------------------------------------------------------------
function WORKFLOW_BATCH_SEND(params) {
    var url = "../client/workflow";
    $.post(url, {
                o : "WORKFLOW_BATCH_SEND",
                datas : params[0]
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ---------------------------------------------------------------------------------------------------------------------------
function WORKFLOW_GET_PROCESS_MESSAGE(params) {
    var url = "../client/workflow";
    $.post(url, {
                o : "getProcessMessage",
                processOrStepId : params[0]
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ---------------------------------------------------------------------------------------------------------------------------
function WORKFLOW_SAVE_PROCESS_MESSAGE(params) {
    params = params[0];
    var url = "../client/workflow";
    $.post(url, {
                o : "saveProcessMessage",
                processOrStepId : params[0],
                type : params[1],
                content : params[2]
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// --------------------------------------------------------------------------------------------------------------------------

