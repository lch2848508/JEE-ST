// ///////////////////////////////////////////////////////////////////////////////////////////
// 定义一个对象，所有同PORTAL相关的函数都包含在此对象中
var PORTAL = {};
// ///////////////////////////////////////////////////////////////////////////////////////////
// 布局相关
PORTAL.LAYOUT = {};
PORTAL.LAYOUT.A = function() {
    return top.INTF_PORTALGRID.LAYOUT.get(__portalid__, "a");
};
PORTAL.LAYOUT.B = function() {
    return top.INTF_PORTALGRID.LAYOUT.get(__portalid__, "b");
};
PORTAL.LAYOUT.C = function() {
    return top.INTF_PORTALGRID.LAYOUT.get(__portalid__, "c");
};
PORTAL.LAYOUT.Detail = function() {
    return top.INTF_PORTALGRID.LAYOUT.get(__portalid__, "BoxDetail");
}
PORTAL.LAYOUT.get = function(name) {
    return top.INTF_PORTALGRID.LAYOUT.get(__portalid__, name);
}
// ///////////////////////////////////////////////////////////////////////////////////////////
// 列表相关
PORTAL.GRID = {};
PORTAL.GRID.get = function() {
    return top.INTF_PORTALGRID.GRID.get(__portalid__);
};

PORTAL.GRID.add = function() {
    return top.INTF_PORTALGRID.GRID.add(__portalid__);
};
PORTAL.GRID.edit = function() {
    return top.INTF_PORTALGRID.GRID.edit(__portalid__);
};
PORTAL.GRID.del = function() {
    return top.INTF_PORTALGRID.GRID.del(__portalid__);
};
PORTAL.GRID.view = function() {
    return top.INTF_PORTALGRID.GRID.view(__portalid__);
};
PORTAL.GRID.up = function() {
    return top.INTF_PORTALGRID.GRID.up(__portalid__);
};
PORTAL.GRID.down = function() {
    return top.INTF_PORTALGRID.GRID.down(__portalid__);
};
PORTAL.GRID.refresh = function() {
    return top.INTF_PORTALGRID.GRID.refresh(__portalid__);
};
PORTAL.GRID.remove = function() {
    return top.INTF_PORTALGRID.GRID.remove(__portalid__);
};
PORTAL.GRID.getSelectedItem = function() {
    return top.INTF_PORTALGRID.GRID.getSelectedItem(__portalid__);
}
PORTAL.GRID.getSelectedItems = function() {
    return top.INTF_PORTALGRID.GRID.getSelectedItems(__portalid__);
}
PORTAL.GRID.getDatas = function() {
    return top.INTF_PORTALGRID.GRID.getDatas(__portalid__);
}
PORTAL.GRID.gotoPage = function(page) {
    return top.INTF_PORTALGRID.GRID.gotoPage(__portalid__, page);
}
PORTAL.GRID.setCellValue = function(key, fieldname, value, refresh) {
    if (typeof(refresh) == 'undefined')
        refresh = true;
    top.INTF_PORTALGRID.GRID.setCellValue(key, fieldname, value, __portalid__, refresh);
}
PORTAL.GRID.setCellsValue = function(key, fieldname, value, refresh) {
    if (typeof(refresh) == 'undefined')
        refresh = true;
    top.INTF_PORTALGRID.GRID.setCellsValue(key, fieldname, value, __portalid__, refresh);
}
PORTAL.GRID.selectItem = function(key) {
    top.INTF_PORTALGRID.GRID.selectItem(key, __portalid__);
}

PORTAL.GRID.batchSetCellsValue = function(keys, fieldnames, values) {
    top.INTF_PORTALGRID.GRID.batchSetGridCellsValue(keys, fieldnames, values, __portalid__);
}

PORTAL.GRID.refreshSelectItem = function() {
    top.INTF_PORTALGRID.GRID.refreshSelectedItem(__portalid__);
}
// ///////////////////////////////////////////////////////////////////////////////////////////
PORTAL.TREE = {};
PORTAL.TREE.get = function() {
    return top.INTF_PORTALGRID.TREE.get(__portalid__);
};
PORTAL.TREE.getSelectedItems = function() {
    return top.INTF_PORTALGRID.TREE.getSelectedItems(__portalid__);
};
PORTAL.TREE.getSelectItemId = function() {
    return PORTAL.TREE.getSelectedItems()[0].id;
}
PORTAL.TREE.getSelectedItem = function() {
    return PORTAL.TREE.getSelectedItems()[0];
}
PORTAL.TREE.addSibling = function() {
    return top.INTF_PORTALGRID.TREE.addSibling(__portalid__);
};
PORTAL.TREE.addChild = function() {
    return top.INTF_PORTALGRID.TREE.addChild(__portalid__);
};
PORTAL.TREE.edit = function() {
    return top.INTF_PORTALGRID.TREE.edit(__portalid__);
};
PORTAL.TREE.del = function() {
    return top.INTF_PORTALGRID.TREE.del(__portalid__);
};
PORTAL.TREE.up = function() {
    return top.INTF_PORTALGRID.TREE.up(__portalid__);
};
PORTAL.TREE.down = function() {
    return top.INTF_PORTALGRID.TREE.down(__portalid__);
};
PORTAL.TREE.refresh = function() {
    return top.INTF_PORTALGRID.TREE.refresh(__portalid__);
};
PORTAL.TREE.getDatas = function() {
    return top.INTF_PORTALGRID.TREE.getDatas(__portalid__);
}

PORTAL.TREE.getRootId = function() {
    return PORTAL.TREE.getDatas()[0].id;
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
/*
 * var params = {}; var datasetName = "导出Excel"; var sheetName = "数据"; var
 * fields = ["CODE", "AUTHOR", "UNIT", "TITLE", "JOUR", "PY", "VL", "QI", "PP"];
 * var columns = ["论文代码", "作者", "单位", "论文标题", "期刊名称", "年", "卷", "期", "页码"]; var
 * filename = "数据下载模版" PORTAL.EXCEL.exportExcel(datasetName, params, filename,
 * sheetName, fields, columns);
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
    top.GLOBAL.WIN.modalDialog(formids, formSize.w, formSize.h, caption, readonly, params, callbackFun, closeCallbackFun, "PORTALGRID_" + __portalid__, isSilent);
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
};

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

// ////////////////////////////////////////////////////////////////////////////////////////////
PORTAL.TOOLBAR = {};
PORTAL.TOOLBAR.CONST = {
    TREE_NEW_SIBLING : "folder_new_sibling",
    TREE_NEW_CHILD : "folder_new_child",
    TREE_DELETE : "folder_delete",
    TREE_EDIT : "folder_edit",
    TREE_MOVEUP : "folder_moveup",
    TREE_MOVEDOWN : "folder_movedown",
    GRID_NEW : "grid_new",
    GRID_EDIT : "grid_edit",
    GRID_DELETE : "grid_delete",
    GRID_MOVEUP : "grid_moveup",
    GRID_MOVEDOWN : "grid_movedown"
};

PORTAL.TOOLBAR.getToolbarItem = function(name) {
    return top.INTF_PORTALGRID.TOOLBAR.getItem(name, __portalid__);
}

PORTAL.TOOLBAR.setToolbarItemEnabled = function(name, enabled) {
    return top.INTF_PORTALGRID.TOOLBAR.setToolbarItemEnabled(name, enabled, __portalid__);
}

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
    var params = top.getCallPortalFunctionParams(getFrameID());
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
