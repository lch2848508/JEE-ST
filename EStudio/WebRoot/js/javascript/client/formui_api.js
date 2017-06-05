// ///////////////////////////////////////////////////////////////////////////////////////////
// 定义一个对象，所有同Form相关的函数都包含在此对象中
var FORM = {};
FORM.isReadonly = function() {
    return top.MainFormFlash.Callback_Form_IsReadonly(__formids__);
}
// //////////////////////////////////////////////////////////////////////////////////////////
// 表单参数操作区域
FORM.PARAMS = {};
// 取得表单所有参数
FORM.PARAMS.getAll = function() {
    return top.INTF_FORM.PARAMS.getParams(__formids__);
};
// 得到单项表单参数
FORM.PARAMS.get = function(paramName) {
    var params = FORM.PARAMS.getAll();
    return params ? params[paramName] : null;
};

FORM.initParams = function(params) {
    return top.INTF_FORM.getInstance(__formids__).initParams(params);
};

FORM.isCallByPortal = function() {
    return Convert.try2Int(FORM.PARAMS.get("IS_PORTAL_FORM"), 0) == 1;
};
FORM.isPortal = FORM.isCallByPortal;
FORM.close = function() {
    return top.INTF_FORM.close(__formids__);
};

// //////////////////////////////////////////////////////////////////////////////////////////
FORM.DB = {};
// 是否为空表单
FORM.DB.isNew = function() {
    return top.INTF_FORM.DATASET.isNew(__formids__);
};
// 检查是否存在数据
FORM.DB.existsRecord = function(datasetName) {
    return top.INTF_FORM.DATASET.existsRecord(__formids__, datasetName);
};

FORM.DB.existsDataSet = function(datasetName) {
    return top.INTF_FORM.DATASET.existsDataSet(__formids__, datasetName);
};
// 取得服务端唯一标识号
FORM.DB.getServerUniqueId = function() {
    return top.GLOBAL.DB.getServerUniqueid();
};
// 获取序列号
FORM.DB.getSerialCode = function(template) {
    return top.GLOBAL.DB.getSerialCode(template);
}
// 取得服务端时间
FORM.DB.getServerDate = function(format) {
    return top.GLOBAL.DB.getServerDate(format);
};
// 获取数据源的当前值
FORM.DB.getDataSetValue = function(datasourceName, fieldName) {
    return top.INTF_FORM.DATASET.getValue(__formids__, datasourceName, fieldName);
};

// 设置数据源的当前值
FORM.DB.setDataSetValue = function(datasourceName, fieldName, value) {
    return top.INTF_FORM.DATASET.setValue(__formids__, datasourceName, fieldName, value);
};

// 设置数据源的当前值
FORM.DB.setDataSetValues = function(datasourceName, values) {
    return top.INTF_FORM.DATASET.setValues(__formids__, datasourceName, values);
};

// 设置数据源值
FORM.DB.getDataSetRecords = function(datasourceName) {
    var records = top.INTF_FORM.DATASET.getDatas(__formids__, datasourceName);
    var result = [];
    for (var k in records)
        result[k * 1] = records[k];
    return result;
};

// 追加数据源记录
FORM.DB.appendRecords = function(datasourceName, records) {
    return top.INTF_FORM.DATASET.appendRecords(__formids__, datasourceName, records);
};
// 执行DataSet中的SQL
FORM.DB.executeDataSet = function(datasetName, params, type) {
    return top.INTF_FORM.DATASET.execute(__formids__, datasetName, params, type);
};

FORM.DB.batchExecuteDataSet = function(datasetName, params, type) {
    return top.INTF_FORM.DATASET.batchExecute(__formids__, datasetName, params, type);
};

// 执行DataSet中的Select语句 并将执行的结构列表显示供用户选择
FORM.DB.lookupDataSet = function(datasetName, params, callbackFun, w, h, pageAble, multiSelect) {
    if (typeof(callbackFun) == "function") {
        window["__gridDataSetCallbackFunction__"] = callbackFun;
        callbackFun = "__gridDataSetCallbackFunction__";
    }
    top.GLOBAL.DB.gridDataSet(-1, datasetName, params, callbackFun, w, h, pageAble, multiSelect, __formids__);
};

FORM.DB.treeDataSet = function(datasetName, params, callbackFun, width, height, multiSelect, labelField, groupField, values, isClientCache) {
    if (typeof(callbackFun) == "function") {
        window["__treeDataSetCallbackFunction__"] = callbackFun;
        callbackFun = "__treeDataSetCallbackFunction__";
    }
    return top.GLOBAL.DB.treeDataSet(-1, datasetName, params, callbackFun, width, height, multiSelect, __formids__, labelField, groupField, {
                datasetName : datasetName,
                selectedValues : values,
                portalId : -1
            }, isClientCache);
};

FORM.DB.listDataSet = FORM.DB.lookupDataSet;
// 获取DataSet中的值并返回JSON格式
FORM.DB.getDataSetJson = function(datasetName, params) {
    return top.GLOBAL.DB.getLookupDatasetJson(-1, datasetName, params);
};
// 刷新数据
FORM.DB.refreshDataSets = function(params) {
    return top.INTF_FORM.DB.refresh(__formids__, params);
};

FORM.DB.batchSetDataSetValues = function(datasetName, keys, values) {
    return top.INTF_FORM.DB.batchSetValues(__formids__, datasetName, keys, values);
};

FORM.DB.save = function() {
    return top.INTF_FORM.DATASET.save(__formids__);
};

FORM.DB.copyFrom = function(params, datasetNames) {
    return top.INTF_FORM.DATASET.copyFrom(__formids__, params, datasetNames);
};

FORM.DB.updateDataSetValues = function(datasetName, records) {
    return top.INTF_FORM.DATASET.updateDataSetValues(__formids__, datasetName, records)
};

FORM.DB.clearRecords = function(datasetName) {
    return top.INTF_FORM.DATASET.clearRecords(__formids__, datasetName);
};

FORM.DB.deleteRecord = function(datasetName) {
    return top.INTF_FORM.DATASET.deleteRecord(__formids__, datasetName);
};

FORM.DB.deleteRecords = function(datasetName, keys) {
    return top.INTF_FORM.DATASET.deleteRecords(__formids__, datasetName, keys);
};

// //////////////////////////////////////////////////////////////////////////////////////////
FORM.CONTROL = {};
// 得到控件值
FORM.CONTROL.getControlValue = function(controlName) {
    return top.INTF_FORM.CONTROL.getValue(__formids__, controlName);
};

FORM.CONTROL.getControlValueEx = function(controlName) {
    return top.INTF_FORM.CONTROL.getValueEx(__formids__, controlName);
};

// 设置控件值
FORM.CONTROL.setControlValue = function(controlName, value, extValue) {
    return top.INTF_FORM.CONTROL.setValue(__formids__, controlName, value, extValue);
};

FORM.CONTROL.get = function(controlName) {
    return top.INTF_FORM.CONTROL.get(__formids__, controlName);
};

FORM.CONTROL.getDBGridSelectedItem = function(controlName) {
    return top.INTF_FORM.CONTROL.getGridSelectedItem(__formids__, controlName);
};

FORM.CONTROL.selectGridItem = function(gridName, keyFieldName, value) {
    return top.INTF_FORM.CONTROL.selectDBGridByKeyField(__formids__, gridName, keyFieldName, value);
};

FORM.CONTROL.setControlsEnabled = function(controlName, enable) {
    return top.INTF_FORM.CONTROL.setControlsEnabled(__formids__, controlName, enable);
};

FORM.CONTROL.setControlsVisible = function(controlName, visible) {
    return top.INTF_FORM.CONTROL.setControlsVisible(__formids__, controlName, visible);
};

FORM.CONTROL.setGridBehaviour = function(controlName, addEnabled, deleteEnabled) {
    return top.INTF_FORM.CONTROL.setGridBehaviour(__formids__, controlName, addEnabled, deleteEnabled);
};

FORM.CONTROL.clearComboboxItems = function(controlName) {
    return top.INTF_FORM.CONTROL.clearComboboxItems(__formids__, controlName);
}

FORM.CONTROL.setPageControlActivePage = function(pagecontrol, tabsheet) {
    return top.INTF_FORM.CONTROL.setTabSheetActivePage(__formids__, pagecontrol, tabsheet);
}

// 注册窗体初始化事件
FORM.EVENT = {};

// 注册事件
FORM.EVENT.registerEvent = registerEventFunction;
// //////////////////////////////////////////////////////////////////////////////////////////
FORM.WINDOW = {};
// 更改窗体URL
FORM.WINDOW.changeWindowURL = function(url) {
    // alert(url);
};

// 在Portal的公共Tabsheet中显示URL
FORM.WINDOW.showCommonURL = function(url) {
    // alert(url);
};

FORM.WINDOW.goURL = function(url) {
    top.GLOBAL.UTILS.OpenURL(url);
};
// 得到对话框窗体的初始数据
FORM.WINDOW.getDialogInitDatas = function() {
    return null;// top.WindowUtils.getCallbackFun()(FORM.CONST.ZERO);
};
FORM.WINDOW.listDataSet = FORM.DB.listDataSet;
// 执行DataSet中的Select语句 并将执行的结构列表显示供用户选择
FORM.WINDOW.lookupDataSet = FORM.DB.lookupDataSet;
// 获取DataSet中的值并返回JSON格式
FORM.WINDOW.getDataSetJson = FORM.DB.getDataSetJson;

FORM.WINDOW.modalDialog = function(formids, caption, readonly, params, callbackFun, closeCallbackFun, isSilent) {
    var formSize = top.getFomrsSize(formids);
    if (typeof(callbackFun) == "function") {
        window["__modalDialogCallbackFunction__"] = callbackFun;
        callbackFun = "__modalDialogCallbackFunction__";
    }
    if (typeof(closeCallbackFun) == "function") {
        window["__modalDialogCloseCallbackFunction__"] = closeCallbackFun;
        closeCallbackFun = "__modalDialogCloseCallbackFunction__";
    }
    if ((typeof closeCallbackFun) == "boolean") {
        isSilent = closeCallbackFun;
        closeCallbackFun = ""
    };
    top.GLOBAL.WIN.modalDialog(formids, formSize.w, formSize.h, caption, readonly, params, callbackFun, closeCallbackFun, __formids__, isSilent);
};
// //////////////////////////////////////////////////////////////////////////////////////////
FORM.CONST = {};
// 常量定义区
FORM.CONST.ZERO = 0;
FORM.CONST.ONE = 1;
FORM.CONST.TWO = 2;
FORM.CONST.THREE = 3;
FORM.CONST.SQL_SELECT = 0;
FORM.CONST.SQL_INSERT = 1;
FORM.CONST.SQL_UPDATE = 2;
FORM.CONST.SQL_DELETE = 3;
// //////////////////////////////////////////////////////////////////////////////////////////
function registerEventFunction(controlType, controlId, eventType, eventName) {
    if (!registerEventFunction.events)
        registerEventFunction.events = [];
    registerEventFunction.events.push({
                controlType : controlType,
                controlId : controlId,
                eventType : eventType,
                eventName : eventName
            });
}
// ///////////////////////////////////////////////////////////////////////////////////////////
// 触发窗体事件
function triggerEventFunction(controlType, controlId, eventType, params) {
    var result = true;
    if (!registerEventFunction.events)
        return true;
    for (var i = 0; i < registerEventFunction.events.length; i++) {
        var item = registerEventFunction.events[i];
        if (eventType == item.eventType && (!controlId || controlId == item.controlId) && (!controlType || item.controlType == controlType)) {
            var fun = window[item.eventName];
            if (fun && typeof(fun) == "function") {
                var tempResult = !params ? fun() : fun(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9]);
                if (tempResult == undefined)
                    tempResult = true;
                if (!tempResult)
                    result = false;
            }
        }

    }
    return result;
}
// controlType: "form" , controlId: null , eventType: "OnBeforeSave" , params:
// []
// ///////////////////////////////////////////////////////////////////////////////////////////
// 触发窗体事件
function triggerEventFunctionFromAs(params) {
    return triggerEventFunction(params.controlType, params.controlId, params.eventType, params.params);
}
// ////////////////////////////////////////////////////////////////////////////////////////////
// Hook性质的函数 用于执行相应的控件事件
function __TRIGGER_CONTROL_EVENT__(params) {
    var funName = params.funName;
    var controlId = params.controlId;
    if (window[funName]) {
        // var control = top.INTF_FORM.CONTROL.get(__formids__, controlId);
        window[funName](controlId);
    }
}
// /////////////////////////////////////////////////////////////////////////////////////////////
var IDCARD = top.IDCARD;
var GLOBAL = {};
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
    top.GLOBAL.UTILS.flexConfirm(msg, callbackFun, cancelFun, __formids__);
};

GLOBAL.moduleSWFDialog = function(caption, width, height, url, params, callbackFun) {
    if (typeof(callbackFun) == "function") {
        window["__flexModuleSWFDialogCallbackFunction__"] = callbackFun;
        callbackFun = "__flexModuleSWFDialogCallbackFunction__";
    }
    top.GLOBAL.WIN.moduleSWFDialog(caption, width, height, url, params, __formids__, callbackFun);
}

GLOBAL.commonWords = function(items, callbackFun,initStr) {
    if (typeof(callbackFun) == "function") {
        window["__flexModuleCOMMONWordsFunction__"] = callbackFun;
        callbackFun = "__flexModuleCOMMONWordsFunction__";
    }
    return top.GLOBAL.commonWords(items, __formids__, callbackFun,initStr);
};
// ////////////////////////////////////////////////////////////////////////////////////////////
var OPUTILS = {};
OPUTILS.sendMsm = function(mobiles, content) {
    return top.GLOBAL.UTILS.sendMobileMessage(mobiles, content);
};
OPUTILS.sendEmail = function(address, subject, content) {
    return top.GLOBAL.UTILS.sendEmail(address, subject, content);
};
// ///////////////////////////////////////////////////////////////////////////////////////////
function executeOtherCallmeFunction() {
    var params = top.getCallPortalFunctionParams(__formids__);
    if (params) {
        var funNames = params[0].split(".");
        var funParams = params[1];
        var fun = window;
        for (var i = 0; i < funNames.length; i++) {
            var funName = funNames[i];
            var fun = fun[funName];
        }
        if (typeof(fun) == "function")
            return fun(funParams);
        else
            return fun;
    }
}
// ///////////////////////////////////////////////////////////////////////////////////////////
// 下载附件
function downAttachment(paramsStr) {
    eval("url = " + paramsStr);
    GLOBAL.goURL("/attachment/" + url);
}
// /////////////////////////////////////////////////////////////////////////////////////////////
// ////////////////////////////////////////////////////////////////////////////////////////////

// ////////////////////////////////////////////////////////////////////////////////////////////
var WEBGIS = {};
WEBGIS.winMap = function(caption, mapName, initFunction, initParams, callFunction) {
    var mapURL = "Map4MIS.swf";
    GLOBAL.moduleSWFDialog(caption, 1000, 600, mapURL, {
                mapName : mapName,
                initFunction : initFunction,
                initParams : initParams,
                userId : GLOBAL.USERINFO.id
            }, callFunction);
}
// //////////////////////////////////////////////////////////////////////////////////////////
WEBGIS.selectMapFeature = function(mapName, feature, callFunction) {
    WEBGIS.winMap("选取地图实体", mapName, "showFeature", feature, callFunction);
}
// ///////////////////////////////////////////////////////////////////////////////////////////

GLOBAL.flexAlert = top.GLOBAL.UTILS.flexAlert;
GLOBAL.OpenURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.goURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.USERINFO = {
    id : top.INTF_ENVIRONMENT.USERID,
    name : top.INTF_ENVIRONMENT.REALNAME
};
GLOBAL.INTF_ENVIRONMENT = top.INTF_ENVIRONMENT;
GLOBAL.WINDOW = FORM.WINDOW;
GLOBAL.PRINT = top.GLOBAL.PRINT;
GLOBAL.gotoPortal = top.GLOBAL.gotoPortal;
GLOBAL.executePortalFunction = top.GLOBAL.executePortalFunction;
GLOBAL.STORAGE = top.GLOBAL.STORAGE;
GLOBAL.UTILS = top.GLOBAL.UTILS;
GLOBAL.popupMessage = top.GLOBAL.popupMessage;
dynamicCreateWorkFlowProcess = top.dynamicCreateWorkFlowProcess;
DHTML4FLEX = top.DHTML4FLEX;
EXTINTF = {
// readIdCard : top.IDCARD.read
};

function documentOnLoad() {
    triggerEventFunction("form", null, "OnInitialize", []);
    executeOtherCallmeFunction();
}