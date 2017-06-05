/*******************************************************************************
 * 定义同PortalGrid相关的一些JS函数
 ******************************************************************************/
// 全局函数接口
var INTF_PORTALGRID = {
    _portalID2Instance : {},
    GRID : {},
    TREE : {},
    LAYOUT : {},
    TOOLBAR : {}
};


// 获取PortalGrid栏目的定义 此函数为系统级函数 仅供Flash调用 不应该被任何外部函数调用
INTF_PORTALGRID.getDefine = function(id) {
    var result = null;
    $.post("../client/griddefine?id=" + id, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

// 删除PortalGrid中数据列表中的一条记录 此函数将真实删除数据库中的记录
// 此函数仅供Flash调用
INTF_PORTALGRID.GRID.deleteServer = function(data) {
    data = data[0];
    var url = "../client/dataservice?o=deletegrid4flex";
    var result = null;
    $.post(url, data, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

INTF_PORTALGRID.GRID.exchangeServer = function(data) {
    var result = false;
    $.post("../client/dataservice?o=exchangegridorder", data[0], function(text) {
                result = getBooleanAjaxValue(text);
            }).fail(function() {
                result = false;
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
            });
    return result;
};

INTF_PORTALGRID.GRID.refreshGridSelectedItem = function(params) {
    var result = null;
    params = params[0];
    params["o"] = "refreshgridselecteditem";
    var url = "../client/dataservice?" + $.param(params);
    $.get(url, function(text) {
                result = getObjectAjaxValue(text);

            });
    return result;
};

INTF_PORTALGRID.GRID.getJson = function(data) {
    data = data[0];
    var URL = "../client/dataservice?o=getgridjson4flex";
    var result = null;
    $.post(URL, data, function(text) {
                result = getObjectAjaxValue(text, true);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

INTF_PORTALGRID.GRID.get = function(id) {
    return INTF_PORTALGRID.getInstance(id).getGrid();
    // return MainFormFlash.Callback_Portal_getGrid(id);
};

INTF_PORTALGRID.GRID.add = function(id) {
    return MainFormFlash.Callback_Portal_funGridNew(id);
};

INTF_PORTALGRID.GRID.edit = function(id) {
    return MainFormFlash.Callback_Portal_funGridEdit(id);
};

INTF_PORTALGRID.GRID.del = function(id) {
    return MainFormFlash.Callback_Portal_funGridDelete(id);
};

INTF_PORTALGRID.GRID.view = function(id) {
    return MainFormFlash.Callback_Portal_eventDetailInfoGrid(id);
};

INTF_PORTALGRID.GRID.up = function(id) {
    return MainFormFlash.Callback_Portal_funGridExchange(id, true);
};

INTF_PORTALGRID.GRID.down = function(id) {
    return MainFormFlash.Callback_Portal_funGridExchange(id, false);
};

INTF_PORTALGRID.GRID.refresh = function(id) {
    return MainFormFlash.Callback_Portal_funGridRefresh(id);
};

INTF_PORTALGRID.GRID.remove = function(id) {
    return MainFormFlash.Callback_Portal_funGridDelete(id);
};

INTF_PORTALGRID.GRID.getSelectedItems = function(id) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Portal_getGridSelectedItems(id));
};

INTF_PORTALGRID.GRID.getDatas = function(id) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Portal_getGridDatas(id));
};

INTF_PORTALGRID.GRID.getSelectedItem = function(id) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Portal_getGridSelectedItem(id));
};

INTF_PORTALGRID.GRID.gotoPage = function(id, page) {
    return MainFormFlash.Callback_Portal_gotoPage(id, page);
};

INTF_PORTALGRID.GRID.refreshSelectedItem = function(id) {
    return MainFormFlash.Callback_Portal_refreshGridSelectedItem(id);
};

INTF_PORTALGRID.GRID.setCellValue = function(keyValue, fieldname, value, id, refresh) {
    return MainFormFlash.Callback_Portal_setGridCellValue(id, __JS_OBJECT_2_FLEX_OBJECT__(keyValue), fieldname, __JS_OBJECT_2_FLEX_OBJECT__(value), refresh);
};

INTF_PORTALGRID.GRID.setCellsValue = function(keyValues, fieldname, value, id, refresh) {
    return MainFormFlash.Callback_Portal_setGridCellsValue(id, WrapObjectToArray(keyValues), WrapObjectToArray(fieldname), __JS_OBJECT_2_FLEX_OBJECT__(WrapObjectToArray(value)), refresh);
};

INTF_PORTALGRID.GRID.batchSetGridCellsValue = function(keys, fieldnames, values, id) {
    return MainFormFlash.Callback_Portal_batchSetGridCellsValue(id, WrapObjectToArray(keys), WrapObjectToArray(fieldnames), __JS_OBJECT_2_FLEX_OBJECT__(WrapObjectToArray(values)), true);
};

INTF_PORTALGRID.GRID.selectItem = function(keyValue, id) {
    return MainFormFlash.Callback_Portal_selectGridItem(id, keyValue);
};

// ///////////////////////////////////////////////////////////////////////////////////////
INTF_PORTALGRID.TREE.deleteServer = function(data) {
    data = data[0];
    var url = "../client/dataservice?o=deletetreenode";
    var result = null;
    $.post(url, data, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

INTF_PORTALGRID.TREE.exchangeServer = function(data) {
    var result = false;
    $.post("../client/dataservice?o=exchangetreeorder", data[0], function(text) {
                result = getBooleanAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = false;
            });
    return result;
};

INTF_PORTALGRID.TREE.refreshDatas = function(params) {
    var result = null;
    var id = params[0]["id"];
    $.post("../client/dataservice?o=getportaltreedatas&portal_id=" + id, function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

// 动态加载树状列表数据
INTF_PORTALGRID.TREE.dynamicLoadData = function(params) {
    var result = null;
    var id = params[0]["portal_id"];
    var p_id = params[0]["p_id"];
    $.post("../client/dataservice?o=dynamicLoadTreeData&portal_id=" + id + "&p_id=" + p_id, function(text) {
                eval("json=" + text + ";");
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

INTF_PORTALGRID.TREE.get = function(id) {
    return INTF_PORTALGRID.getInstance(id).getTree();
    // return MainFormFlash.Callback_Portal_getTree(id);
};

INTF_PORTALGRID.TREE.addSibling = function(id) {
    return MainFormFlash.Callback_Portal_funTreeNew(id, true);
};

INTF_PORTALGRID.TREE.addChild = function(id) {
    return MainFormFlash.Callback_Portal_funTreeNew(id, false);
};

INTF_PORTALGRID.TREE.edit = function(id) {
    return MainFormFlash.Callback_Portal_funTreeEdit(id);
};

INTF_PORTALGRID.TREE.del = function(id) {
    return MainFormFlash.Callback_Portal_funTreeDelete(id);
};

INTF_PORTALGRID.TREE.up = function(id) {
    return MainFormFlash.Callback_Portal_funTreeExchange(id, true);
};

INTF_PORTALGRID.TREE.down = function(id) {
    return MainFormFlash.Callback_Portal_funTreeExchange(id, false);
};

INTF_PORTALGRID.TREE.refresh = function(id) {
    return MainFormFlash.Callback_Portal_funTreeRefresh(id);
};

INTF_PORTALGRID.TREE.getSelectedItem = function(id) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Portal_getTreeSelectedItem(id));
};

INTF_PORTALGRID.TREE.getSelectedItems = function(id) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Portal_getTreeSelectedItems(id));
};

INTF_PORTALGRID.TREE.getDatas = function(id) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Portal_getTreeDatas(id));
};
// /////////////////////////////////////////////////////////////////////////////////////////////////////////
INTF_PORTALGRID.LAYOUT.get = function(id, name) {
    return INTF_PORTALGRID.getInstance(id).getLayout(name);
};
// ////////////////////////////////////////////////////////////////////////////////////////////////////////
INTF_PORTALGRID.TOOLBAR.getItem = function(name, id) {
    return INTF_PORTALGRID.getInstance(id).getToolBarItem(name);
};

INTF_PORTALGRID.TOOLBAR.setToolbarItemEnabled = function(name, enabled, id) {
    return MainFormFlash.Callback_Portal_setToolBarItemEnabled(id, name, enabled);
};

// //////////////////////////////////////////////////////////////////////////////////////////////////////////
// ----------------------------------------------------------------------------------------------------------------------
// 导出Excel
function exportToExcel(exportDefine, params) {
    exportDefine = WrapObjectToArray(exportDefine);
    params["exportdefine"] = $.toJSON(exportDefine);
    $.post("../client/excelservlet?o=exportexcel", params, function(text) {
                var json = getObjectAjaxValue(text, true);
                if (json) {
                    var path = json.path;
                    MainFormFlash.Callback_goURL(path);
                }
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
}

// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var dynamicLoadTreeData = INTF_PORTALGRID.TREE.dynamicLoadData;
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ---------------------------------------------------------------------------------------------------------------------
function uploadExcelTemplate(ExcelImportDefine, ExcelTemplateDefine, params, funName, frameID) {
    MainFormFlash.Callback_uploadExcel(ExcelImportDefine, ExcelTemplateDefine, params, frameID, funName);
}
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var getGridProtalDefine = INTF_PORTALGRID.getDefine;
var serverDeletePortalGrid = INTF_PORTALGRID.GRID.deleteServer;
var serverDeletePortalTree = INTF_PORTALGRID.TREE.deleteServer;
var exchangePortalTree = INTF_PORTALGRID.TREE.exchangeServer;
var exchangePortalGrid = INTF_PORTALGRID.GRID.exchangeServer;
var refreshGridSelectedItem = INTF_PORTALGRID.GRID.refreshGridSelectedItem;
var getPortalGridJson = INTF_PORTALGRID.GRID.getJson;
var getPortalGridTreeDatas = INTF_PORTALGRID.TREE.refreshDatas;