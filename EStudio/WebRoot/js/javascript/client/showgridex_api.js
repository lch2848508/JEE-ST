// ///////////////////////////////////////////////////////////////////////////////////////////
// 定义一个对象，所有同PORTAL相关的函数都包含在此对象中
var PORTAL = {};

PORTAL.getParams = function() {
    return top.INTF_PORTALGRIDEX.getParams(__portalid__);
}
// ///////////////////////////////////////////////////////////////////////////////////////////
// 布局相关
PORTAL.LAYOUT = {};
PORTAL.LAYOUT.A = function() {
    return top.INTF_PORTALGRIDEX.LAYOUT.get(__portalid__, "a");
};
PORTAL.LAYOUT.B = function() {
    return top.INTF_PORTALGRIDEX.LAYOUT.get(__portalid__, "b");
};
PORTAL.LAYOUT.C = function() {
    return top.INTF_PORTALGRIDEX.LAYOUT.get(__portalid__, "c");
};
PORTAL.LAYOUT.get = function(name) {
    return top.INTF_PORTALGRIDEX.LAYOUT.get(__portalid__, name);
}
// ///////////////////////////////////////////////////////////////////////////////////////////
// 列表相关
PORTAL.CONTROL = {};

PORTAL.CONTROL.getRootId = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.getRootId(__portalid__, controlName);
}
// ------------------------------------------------------------------------------
PORTAL.CONTROL.getSelectedItem = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.getSelectedItem(__portalid__, controlName);
}
// ------------------------------------------------------------------------------
PORTAL.CONTROL.getSelectedItems = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.getSelectedItems(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.append = function(controlName, isChild) {
    return top.INTF_PORTALGRIDEX.CONTROL.append(__portalid__, controlName, isChild);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.edit = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.edit(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.viewform = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.viewform(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.del = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.del(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.up = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.up(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.down = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.down(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.refresh = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.refresh(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.getRecords = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.getRecords(__portalid__, controlName);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.update = function(controlName, record) {
    return top.INTF_PORTALGRIDEX.CONTROL.updateRecord(__portalid__, controlName, record);
}
// -------------------------------------------------------------------------------
PORTAL.CONTROL.selectItem = function(controlName, key) {
    return top.INTF_PORTALGRIDEX.CONTROL.selectItem(__portalid__, controlName, key);
}
// --------------------------------------------------------------------------------
PORTAL.CONTROL.refreshSelectedItem = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.refreshSelectedItem(__portalid__, controlName);
}

PORTAL.CONTROL.firstPage = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.firstPage(__portalid__, controlName);
}

PORTAL.CONTROL.lastPage = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.lastPage(__portalid__, controlName);
}

PORTAL.CONTROL.callLater = function(controlName, funName) {
    if (typeof(funName) == "function") {
        window["__" + controlName + "callLaterFunName__"] = funName;
        funName = "__" + controlName + "callLaterFunName__";
    }
    return top.INTF_PORTALGRIDEX.CONTROL.callLater(__portalid__, controlName, funName);
}

PORTAL.CONTROL.callFunction = function(controlName, funName, params) {
    return top.INTF_PORTALGRIDEX.CONTROL.executeSWFControlFunction(__portalid__, controlName, funName, params);
}

PORTAL.CONTROL.saveToServer = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.saveToServer(__portalid__, controlName);
}

PORTAL.CONTROL.setDiagramActionBackground = function(controlName, actionName, color) {
    return top.INTF_PORTALGRIDEX.CONTROL.setDiagramActionBackground(__portalid__, controlName, actionName, color);
}
PORTAL.CONTROL.focusDiagramActions = function(controlName, actions) {
    return top.INTF_PORTALGRIDEX.CONTROL.focusDiagramActions(__portalid__, controlName, actions);
}
PORTAL.CONTROL.loadDiagram = function(controlName, diagramName) {
    return top.INTF_PORTALGRIDEX.CONTROL.loadDiagram(__portalid__, controlName, diagramName);
}

//
PORTAL.CONTROL.setDiagramActionStep = function(controlName, actionName, step) {
    return top.INTF_PORTALGRIDEX.CONTROL.setDiagramActionStep(__portalid__, controlName, step);
}
PORTAL.CONTROL.setDiagramActionSetting = function(controlName, actionName, bg, step) {
    return top.INTF_PORTALGRIDEX.CONTROL.setDiagramActionSetting(__portalid__, controlName, bg, step);
}
PORTAL.CONTROL.batchSetDiagramActionSettings = function(controlName, params) {
    return top.INTF_PORTALGRIDEX.CONTROL.batchSetDiagramActionSettings(__portalid__, controlName, params);
}
PORTAL.CONTROL.getDiagramActionSettings = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.getDiagramActionSettings(__portalid__, controlName);
}

PORTAL.CONTROL.setActivePage = function(controlName, activeControlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.setActivePage(__portalid__, controlName, activeControlName);
}
PORTAL.CONTROL.setContent = function(controlName, content) {
    return top.INTF_PORTALGRIDEX.CONTROL.setContent(__portalid__, controlName, content);
}
PORTAL.CONTROL.getContent = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.getContent(__portalid__, controlName);
}
PORTAL.CONTROL.setRichViewText = function(controlName, text, isHTML) {
    return top.INTF_PORTALGRIDEX.CONTROL.setRichViewText(__portalid__, controlName, text, isHTML);
}
PORTAL.CONTROL.getRichViewText = function(controlName, isHTML) {
    return top.INTF_PORTALGRIDEX.CONTROL.getRichViewText(__portalid__, controlName, isHTML);
}
PORTAL.CONTROL.addGeometry = function(controlName, geometry) {
    return top.INTF_PORTALGRIDEX.CONTROL.addGeometry(__portalid__, controlName, geometry);
}

PORTAL.CONTROL.setFormParams = function(controlName, params) {
    return top.INTF_PORTALGRIDEX.CONTROL.setFormParams(__portalid__, controlName, params);
}

PORTAL.CONTROL.setRecordId = function(controlName, recordId) {
    return top.INTF_PORTALGRIDEX.CONTROL.setRecordId(__portalid__, controlName, recordId);
}

PORTAL.CONTROL.getParams = function(controlName) {
    return top.INTF_PORTALGRIDEX.CONTROL.getParams(__portalid__, controlName);
}

PORTAL.CONTROL.setFilterParams = function(controlName, params) {
    return top.INTF_PORTALGRIDEX.CONTROL.setFilterParams(__portalid__, controlName, params);
}

PORTAL.CONTROL.setReadonly = function(controlName, isReadonly) {
    return top.INTF_PORTALGRIDEX.CONTROL.setReadonly(__portalid__, controlName, isReadonly);
}
// /////////////////////////////////////////////////////////////////////////////////////////
PORTAL.DB = {};
PORTAL.DB.getServerUniqueId = function() {
    return top.GLOBAL.DB.getServerUniqueid();
};
// 获取序列号
PORTAL.DB.getSerialCode = function(template) {
    return top.GLOBAL.DB.getSerialCode(template);
}
PORTAL.DB.getDataSetJson = function(datasetName, params) {
    return top.GLOBAL.DB.getLookupDatasetJson(__portalid__, datasetName, params);
}
PORTAL.DB.lookupDataSet = function(datasetName, params, callbackFun, w, h, pageAble, multiSelect) {
    if (typeof(callbackFun) == "function") {
        window["__gridDataSetCallbackFunction__"] = callbackFun;
        callbackFun = "__gridDataSetCallbackFunction__";
    }
    top.GLOBAL.DB.gridDataSet(__portalid__, datasetName, params, callbackFun, w, h, pageAble, multiSelect, "PORTALGRID_" + __portalid__);
}

PORTAL.DB.executeDataSet = function(sqlName, params) {
    return top.GLOBAL.DB.executeSQL(__portalid__, sqlName, params);
}

PORTAL.DB.batchExecuteDataSet = function(sqlName, params) {
    return top.GLOBAL.DB.batchExecuteSQL(__portalid__, sqlName, params);
}

PORTAL.DB.treeDataSet = function(datasetName, params, callbackFun, width, height, multiSelect, values, isClientCache) {
    if (typeof(callbackFun) == "function") {
        window["__treeDataSetCallbackFunction__"] = callbackFun;
        callbackFun = "__treeDataSetCallbackFunction__";
    }
    return top.GLOBAL.DB.treeDataSet(__portalid__, datasetName, params, callbackFun, width, height, multiSelect, "PORTALGRID_" + __portalid__, '', '', {
                datasetName : datasetName,
                selectedValues : values,
                portalId : __portalid__
            }, isClientCache);
}
// //////////////////////////////////////////////////////////////////////////////////////////////
PORTAL.UTILS = {};
PORTAL.UTILS.sendMessage = function(mobiles, content) {
    return top.GLOBAL.UTILS.sendMobileMessage(mobiles, content);
}

PORTAL.executePortalFunction = top.GLOBAL.executePortalFunction;
// //////////////////////////////////////////////////////////////////////////////////////////////
PORTAL.EXCEL = {};

PORTAL.EXCEL.downTemplate = function(sheetName, colHeaders, startRow, startCol) {
    if (isNaN(startRow))
        startRow = 0;
    if (isNaN(startCol))
        startCol = 0;
    var template = {
        sheetName : sheetName,
        header : [{
                    row : startRow,
                    startCol : startCol,
                    items : colHeaders
                }]
    };
    top.downloadExcelTemplate(ExcelTemplateDefine);
}
// ---------------------------------------------------------------------------------------------------------------------------------------
// 导入Excel
PORTAL.EXCEL.importExcel = function(datasetName, params, sheetName, colHeaders, colParams, callbackFun, startRow, startCol) {
    if (isNaN(startRow))
        startRow = 0;
    if (isNaN(startCol))
        startCol = 0;
    var ExcelImportDefine = {
        portalID : __portalid__,
        execute : [{
                    sheetName : sheetName,
                    datasetName : datasetName,
                    firstRow : startRow + 1,
                    firstCol : startCol,
                    params2Col : colParams
                }]
    };
    var ExcelTemplateDefine = {
        sheetName : sheetName,
        header : [{
                    row : startRow,
                    startCol : startCol,
                    items : colHeaders
                }]
    };
    if (typeof(callbackFun) == "function") {
        window["__flexUploadExcelCallbackFunction__"] = callbackFun;
        callbackFun = "__flexUploadExcelCallbackFunction__";
    }
    top.uploadExcelTemplate($.toJSON(ExcelImportDefine), $.toJSON(ExcelTemplateDefine), params, callbackFun, "PORTALGRID_" + __portalid__);
}
// ----------------------------------------------------------------------------------------------------------------------------------------
// 导出Excel
PORTAL.EXCEL.exportExcel = function(datasetName, params, filename, sheetName, fields, columns, startRow, startCol) {
    if (isNaN(startRow))
        startRow = 0;
    if (isNaN(startCol))
        startCol = 0;
    var execute = [];
    execute.push({
                sheetName : sheetName,
                datasetName : datasetName,
                firstRow : startRow,
                firstCol : startCol,
                fields : fields,
                headers : columns
            });
    var ExcelExportDefine = {
        filename : filename,
        portalID : __portalid__,
        execute : execute
    };
    top.exportToExcel(ExcelExportDefine, params);
}

PORTAL.EXCEL.jsonTemplate2Excel = function(define) {
    var data = {
        define : $.toJSON(define),
        o : "jsonTemplate2Excel"
    };
    var URL = "../client/excelservlet";
    var result = null;
    $.post(URL, data, function(text) {
                var result = $.evalJSON(text);
                var url = result.url;
                GLOBAL.OpenURL(url);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
/*
 * var params = {}; var datasetName = "导出Excel"; var sheetName = "数据"; var fields = ["CODE", "AUTHOR", "UNIT", "TITLE", "JOUR", "PY", "VL", "QI", "PP"]; var columns = ["论文代码", "作者", "单位", "论文标题", "期刊名称", "年", "卷", "期", "页码"]; var filename = "数据下载模版" PORTAL.EXCEL.exportExcel(datasetName, params,
 * filename, sheetName, fields, columns);
 */
// //////////////////////////////////////////////////////////////////////////////////////////////
PORTAL.WINDOW = {};
PORTAL.WINDOW.listDataSet = PORTAL.DB.listDataSet;
PORTAL.WINDOW.treeDataSet = PORTAL.DB.treeDataSet;
PORTAL.WINDOW.selectDate = function(callbackFun) {
    if (typeof(callbackFun) == "function") {
        window["__flexSelectedDateCallbackFunction__"] = callbackFun;
        callbackFun = "__flexSelectedDateCallbackFunction__";
    }
    top.GLOBAL.UTILS.selectDate("PORTALGRID_" + __portalid__, callbackFun);
}
// ///////////////////////////////////////////////////////////////////////////////////////////////
PORTAL.WINDOW.modalDialog = function(formids, caption, readonly, params, callbackFun, closeCallbackFun, isSilent) {
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
    top.GLOBAL.WIN.modalDialog(formids, formSize.w, formSize.h, caption, readonly, params, callbackFun, closeCallbackFun, "PORTALGRID_" + __portalid__, isSilent);
}
// ///////////////////////////////////////////////////////////////////////////////////////////////
PORTAL.WINDOW.closeDialog = function(formids) {
    top.GLOBAL.WIN.closeDialog(formids);
}

PORTAL.WINDOW.saveFormDialog = function(formids) {
    top.GLOBAL.WIN.saveFormDialog(formids);
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
    top.GLOBAL.UTILS.flexConfirm(msg, callbackFun, cancelFun, "PORTALGRID_" + __portalid__);
}

GLOBAL.moduleSWFDialog = function(caption, width, height, url, params, callbackFun) {
    if (typeof(callbackFun) == "function") {
        window["__flexModuleSWFDialogCallbackFunction__"] = callbackFun;
        callbackFun = "__flexModuleSWFDialogCallbackFunction__";
    }
    top.GLOBAL.WIN.moduleSWFDialog(caption, width, height, url, params, "PORTALGRID_" + __portalid__, callbackFun);
}

GLOBAL.OpenURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.goURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.USERINFO = {
    id : top.INTF_ENVIRONMENT.USERID,
    name : top.INTF_ENVIRONMENT.REALNAME
};
GLOBAL.INTF_ENVIRONMENT = top.INTF_ENVIRONMENT;
PORTAL.WINDOW.goURL = top.GLOBAL.UTILS.OpenURL;
GLOBAL.WINDOW = PORTAL.WINDOW;
GLOBAL.PRINT = top.GLOBAL.PRINT;

GLOBAL.gotoPortal = top.GLOBAL.gotoPortal;
GLOBAL.closePortal = top.GLOBAL.closePortal;
GLOBAL.executePortalFunction = top.GLOBAL.executePortalFunction;
GLOBAL.STORAGE = top.GLOBAL.STORAGE;
GLOBAL.UTILS = top.GLOBAL.UTILS;
GLOBAL.popupMessage = top.GLOBAL.popupMessage;

GLOBAL.commonWords = function(items, callbackFun, initStr) {
    if (typeof(callbackFun) == "function") {
        window["__flexModuleCOMMONWordsFunction__"] = callbackFun;
        callbackFun = "__flexModuleCOMMONWordsFunction__";
    }
    return top.GLOBAL.commonWords(items, "PORTALGRID_" + __portalid__, callbackFun, initStr);
};
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
function executeOtherCallmeFunction() {
    // alert(top.getCallPortalFunctionParams);
    var params = top.getCallPortalFunctionParams(getFrameID());
    if (params) {
        var funNames = params[0].split(".");
        var funParams = params[1];
        var fun = window;
        for (var i = 0; i < funNames.length; i++) {
            var funName = funNames[i];
            var fun = fun[funName];
        }
        if (typeof(fun) == "function") {
            return fun(funParams);
        } else
            return fun;
    }

}

var EXCEL = {};
// ///////////////////////////////////////////////////////////////////////////////////////////
var dynamicCreateWorkFlowProcess = top.dynamicCreateWorkFlowProcess;
// //////////////////////////////////////////////////////////////////////////////////////////
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
var DHTML4FLEX = top.DHTML4FLEX;
// ///////////////////////////////////////////////////////////////////////////////////////////
function documentOnLoad() {
    if (window['ON_PORTAL_INITIALIZE'])
        ON_PORTAL_INITIALIZE();
    executeOtherCallmeFunction();
}
// ////////////////////////////////////////////////////////////////////////////////////////////
