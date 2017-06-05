/***********************************************************************************************************************************************************************************************************************************************************************************************************
 * 定义同PortalGrid相关的一些JS函数
 **********************************************************************************************************************************************************************************************************************************************************************************************************/
// 全局函数接口
var INTF_PORTALGRIDEX = {
    CONTROL : {},
    LAYOUT : {},
    _portalID2Instance : {}
};
// ------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.getSelectedItem = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getSelectedItem(portalId, controlName);
}
// ------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.getSelectedItems = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getSelectedItems(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.append = function(portalId, controlName, isChild) {
    return MainFormFlash.Callback_PortalEx_append(portalId, controlName, isChild);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.edit = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_edit(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.viewform = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_viewform(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.del = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_del(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.up = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_up(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.down = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_down(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.refresh = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_refresh(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.getRecords = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getRecords(portalId, controlName);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.updateRecord = function(portalId, controlName, record) {
    return MainFormFlash.Callback_PortalEx_updateRecord(portalId, controlName, record);
}
// -------------------------------------------------------------------------------
INTF_PORTALGRIDEX.CONTROL.selectItem = function(portalId, controlName, key) {
    return MainFormFlash.Callback_PortalEx_selectItem(portalId, controlName, key);
}
INTF_PORTALGRIDEX.CONTROL.getRootId = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getRootId(portalId, controlName);
}
INTF_PORTALGRIDEX.CONTROL.refreshSelectedItem = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_refreshSelectedItem(portalId, controlName);
}

INTF_PORTALGRIDEX.CONTROL.firstPage = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_firstPage(portalId, controlName);
}

INTF_PORTALGRIDEX.CONTROL.lastPage = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_lastPage(portalId, controlName);
}

INTF_PORTALGRIDEX.CONTROL.callLater = function(portalId, controlName, funName) {
    return MainFormFlash.Callback_PortalEx_callLater(portalId, controlName, funName);
}

INTF_PORTALGRIDEX.CONTROL.executeSWFControlFunction = function(portalId, controlName, funName, params) {
    return MainFormFlash.Callback_PortalEx_executeSWFControlFunction(portalId, controlName, funName, params);
}
INTF_PORTALGRIDEX.CONTROL.saveToServer = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_saveToServer(portalId, controlName);
}

INTF_PORTALGRIDEX.CONTROL.setDiagramActionBackground = function(portalId, controlName, actionName, color) {
    return MainFormFlash.Callback_PortalEx_setDiagramActionBackground(portalId, controlName, actionName, color);
}

INTF_PORTALGRIDEX.CONTROL.setDiagramActionStep = function(portalId, controlName, actionName, step) {
    return MainFormFlash.Callback_PortalEx_setDiagramActionStep(portalId, controlName, actionName, step);
}

INTF_PORTALGRIDEX.CONTROL.setDiagramActionSetting = function(portalId, controlName, actionName, color, step) {
    return MainFormFlash.Callback_PortalEx_setDiagramActionSetting(portalId, controlName, actionName, color, step);
}

INTF_PORTALGRIDEX.CONTROL.batchSetDiagramActionSettings = function(portalId, controlName, params) {
    return MainFormFlash.Callback_PortalEx_batchSetDiagramActionSettings(portalId, controlName, params);
}

INTF_PORTALGRIDEX.CONTROL.getDiagramActionSettings = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getDiagramActionSettings(portalId, controlName);
}

INTF_PORTALGRIDEX.CONTROL.focusDiagramActions = function(portalId, controlName, actions) {
    return MainFormFlash.Callback_PortalEx_focusDiagramActions(portalId, controlName, actions);
}
INTF_PORTALGRIDEX.CONTROL.loadDiagram = function(portalId, controlName, diagramName) {
    return MainFormFlash.Callback_PortalEx_loadDiagram(portalId, controlName, diagramName);
}
INTF_PORTALGRIDEX.CONTROL.setActivePage = function(portalId, controlName, activeControlName) {
    return MainFormFlash.Callback_PortalEx_setActivePage(portalId, controlName, activeControlName);
}
INTF_PORTALGRIDEX.CONTROL.setContent = function(portalId, controlName, content) {
    return MainFormFlash.Callback_PortalEx_setContent(portalId, controlName, content);
}
INTF_PORTALGRIDEX.CONTROL.getContent = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getContent(portalId, controlName);
}
INTF_PORTALGRIDEX.CONTROL.setRichViewText = function(portalId, controlName, text) {
    return MainFormFlash.Callback_PortalEx_setRichViewText(portalId, controlName, text);
}
INTF_PORTALGRIDEX.CONTROL.getRichViewText = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getRichViewText(portalId, controlName);
}
INTF_PORTALGRIDEX.CONTROL.addGeometry = function(portalId, controlName, geometry) {
    return MainFormFlash.Callback_PortalEx_addGeometrys(portalId, controlName, geometry);
}

INTF_PORTALGRIDEX.CONTROL.setFormParams = function(portalId, controlName, params) {
    return MainFormFlash.Callback_PortalEx_setFormParams(portalId, controlName, params);
}

INTF_PORTALGRIDEX.CONTROL.setRecordId = function(portalId, controlName, recordId) {
    return MainFormFlash.Callback_PortalEx_setRecordId(portalId, controlName, recordId);
}

INTF_PORTALGRIDEX.CONTROL.getParams = function(portalId, controlName) {
    return MainFormFlash.Callback_PortalEx_getControlParams(portalId, controlName);
}

INTF_PORTALGRIDEX.CONTROL.setFilterParams = function(portalId, controlName, params) {
    return MainFormFlash.Callback_PortalEx_setControlFilterParams(portalId, controlName, params);
}

INTF_PORTALGRIDEX.CONTROL.setReadonly = function(portalId, controlName, isReadonly) {
    return MainFormFlash.Callback_PortalEx_setControlReadonly(portalId, controlName, isReadonly);
}

INTF_PORTALGRIDEX.getParams = function(portalId) {
    return MainFormFlash.Callback_PortalEx_getParams(portalId);
}
// --------------------------------------------------------------------------------
INTF_PORTALGRIDEX.LAYOUT.get = function(portalId, name) {
    return INTF_PORTALGRIDEX.getInstance(portalId).getLayout(name);
}
// --------------------------------------------------------------------------------

// 获取PortalGrid栏目的定义 此函数为系统级函数 仅供Flash调用 不应该被任何外部函数调用
INTF_PORTALGRIDEX.getDefine = function(params) {
    var result = null;
    $.post("../client/griddefine?id=" + params[0], params[1], function(text) {
                result = getObjectAjaxValue(text);

            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var getGridProtalDefineEx = INTF_PORTALGRIDEX.getDefine;
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function getPortalExControlRecords(params) {
    params = $.evalJSON(params);
    var url = "../client/dataservice";
    var result = null;
    $.post(url, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var __portalControlFilterComboboxItemCache__ = {};
function getPortalControlFilterComboboxItems(params) {
    params = params[0];
    var key = params.portalId + "-" + params.controlName + "-" + params.paramName + "-" + params.PARENT_COMBOBOX;
    if (__portalControlFilterComboboxItemCache__[key])
        return __portalControlFilterComboboxItemCache__[key];
    params.o = "getPortalControlFilterComboboxItems";
    var url = "../client/dataservice";
    var result = null;
    $.post(url, params, function(text) {
                result = getObjectAjaxValue(text);
                __portalControlFilterComboboxItemCache__[key] = result;
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function refreshPortalGridExSelectedItem(params) {
    params = params[0];
    var url = "../client/dataservice";
    var result = null;
    $.post(url, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function deletePortalExControlRecord(params) {
    return postToDataServiceAndReturnBoolean(params[0]);
}
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function exchangePortalExControlRecord(params) {
    return postToDataServiceAndReturnBoolean(params[0]);
}
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function postToDataServiceAndReturnBoolean(params) {
    var url = "../client/dataservice";
    var result = null;
    $.post(url, params, function(text) {
                result = getBooleanAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 保存栏目控件内容
function savePortalGridExControl(params) {
    params = params[0];
    var url = "../client/dataservice";
    var result = null;
    $.post(url, params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

