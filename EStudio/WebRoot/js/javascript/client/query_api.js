// ///////////////////////////////////////////////////////////////////////////////////////////
// 定义一个对象，所有同PORTAL相关的函数都包含在此对象中
var QUERY = {};
// ///////////////////////////////////////////////////////////////////////////////////////////
QUERY.UTILS = {};
// ///////////////////////////////////////////////////////////////////////////////////////////
QUERY.UTILS.sendMessage = function(mobiles, content) {
    return top.GLOBAL.UTILS.sendMobileMessage(mobiles, content);
}
// //////////////////////////////////////////////////////////////////////////////////////////////
QUERY.WINDOW = {};
QUERY.WINDOW.modalDialog = function(formids, caption, readonly, params, callbackFun, closeCallbackFun, isSilent) {
    var formSize = top.getFomrsSize(formids);
    if (typeof(callbackFun) == "function") {
        window["__modalDialogCallbackFunction__"] = callbackFun;
        callbackFun = "__modalDialogCallbackFunction__";
    }
    if (typeof(closeCallbackFun) == "function") {
        window["__modalDialogCloseCallbackFunction__"] = closeCallbackFun;
        closeCallbackFun = "__modalDialogCloseCallbackFunction__";
    }
    top.GLOBAL.WIN.modalDialog(formids, formSize.w, formSize.h, caption, readonly, params, callbackFun, closeCallbackFun, __portalid__, isSilent);
}
// //////////////////////////////////////////////////////////////////////////////////////////////
var GLOBAL = {};
GLOBAL.flexAlert = top.GLOBAL.UTILS.flexAlert;
GLOBAL.flexConfirm = function(msg, callbackFun, cancelFun) {
    if (!cancelFun)
        cancelFun = "";
    if (typeof(callbackFun) == "function") {
        window["__flexConfirmCallbackFunction__"] = callbackFun;
        callbackFun = "__flexConfirmCallbackFunction__";
    }
    if (typeof(cancelFun) == "function") {
        window["__flexConfirmCallbackCancelFunction__"] = cancelFun;
        cancelFun = "__flexConfirmCallbackCancelFunction__";
    }
    top.GLOBAL.UTILS.flexConfirm(msg, callbackFun, cancelFun, __portalid__);
};

GLOBAL.OpenURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.goURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.USERINFO = {
    id : top.INTF_ENVIRONMENT.USERID,
    name : top.INTF_ENVIRONMENT.REALNAME
};
GLOBAL.INTF_ENVIRONMENT = top.INTF_ENVIRONMENT;
QUERY.WINDOW.goURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.WINDOW = QUERY.WINDOW;
GLOBAL.PRINT = top.GLOBAL.PRINT;

GLOBAL.gotoPortal = top.GLOBAL.gotoPortal;
GLOBAL.closePortal = top.GLOBAL.closePortal;
GLOBAL.executePortalFunction = top.GLOBAL.executePortalFunction;
GLOBAL.STORAGE = top.GLOBAL.STORAGE;
// ////////////////////////////////////////////////////////////////////////////////////////////
OPUTILS = {};
OPUTILS.sendMsm = function(mobiles, content) {
    return top.GLOBAL.UTILS.sendMobileMessage(mobiles, content);
}
OPUTILS.sendEmail = function(address, subject, content) {
    return top.GLOBAL.UTILS.sendEmail(address, subject, content);
}

function SendSMS(mobiles, content) {
    return top.GLOBAL.UTILS.sendMobileMessage(mobiles, content);
}
// ///////////////////////////////////////////////////////////////////////////////////////////
function downAttachment(paramsStr) {
    eval("url = " + paramsStr);
    GLOBAL.goURL("/attachment/" + url);
}
// ///////////////////////////////////////////////////////////////////////////////////////////
var dynamicCreateWorkFlowProcess = top.dynamicCreateWorkFlowProcess;
// ////////////////////////////////////////////////////////////////////////////////////////////
QUERY.setFilterValues = function(params) {
    return top.MainFormFlash.Callback_Query_SetFilterValues(__portalId__, params);
}
// ////////////////////////////////////////////////////////////////////////////////////////////
