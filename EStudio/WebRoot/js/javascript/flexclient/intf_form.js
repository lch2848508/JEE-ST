/***********************************************************************************************************************************************************************************************************************************************************************************************************
 * 定义同表单操作相关的一些JS函数
 **********************************************************************************************************************************************************************************************************************************************************************************************************/
// /////////////////////////////////////////////////////////////////////////////////////////////////
var INTF_FORM = {
    _id2Instance : {},
    CONTROL : {},
    DATASET : {},
    PARAMS : {},
    UTILS : {}
};
INTF_FORM.DB = INTF_FORM.DATASET;
// ///////////////////////////////////////////////////////////////////////////////////////////////////
INTF_FORM.getDefine = function(data) {
    data = $.evalJSON(data[0]);
    var URL = "../client/formsdefine";
    data.o = "getdefine";
    var result = null;
    $.post(URL, data, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};
INTF_FORM.isReadOnly = function(id) {
    return MainFormFlash.Callback_Form_IsReadonly(id);
}

INTF_FORM.close = function(id) {
    return MainFormFlash.Callback_Form_Close(id);
}
// //////////////////////////////////////////////////////////////////////////////////////////////////
INTF_FORM.CONTROL.getValue = function(id, controlName) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Form_getControlValue(id, controlName));
};

INTF_FORM.CONTROL.getValueEx = function(id, controlName) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Form_getControlValueEx(id, controlName));
};

INTF_FORM.CONTROL.setValue = function(id, controlName, value, extValue) {
    return MainFormFlash.Callback_Form_setControlValue(id, controlName, __JS_OBJECT_2_FLEX_OBJECT__(value), __JS_OBJECT_2_FLEX_OBJECT__(extValue));
};

INTF_FORM.CONTROL.get = function(id, controlName) {
    return INTF_FORM.getInstance(id).getControl(controlName);
};

INTF_FORM.CONTROL.getGridSelectedItem = function(id, controlName) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Form_getDBGridSelectedItem(id, controlName));
};

INTF_FORM.CONTROL.selectDBGridByKeyField = function(id, controlName, keyfield, value) {
    return MainFormFlash.Callback_Form_selectDBGridByKeyField(id, controlName, keyfield, __JS_OBJECT_2_FLEX_OBJECT__(value));
};

INTF_FORM.CONTROL.setControlsEnabled = function(id, controlName, enable) {
    return MainFormFlash.Callback_Form_setControlsEnabled(id, WrapObjectToArray(controlName, true), enable);
};

INTF_FORM.CONTROL.setControlsVisible = function(id, controlName, visible) {
    return MainFormFlash.Callback_Form_setControlsVisible(id, WrapObjectToArray(controlName, true), visible);
};

INTF_FORM.CONTROL.setGridBehaviour = function(id, controlName, addEnabled, deleteEnabled) {
    return MainFormFlash.Callback_Form_setGridBehaviour(id, controlName, addEnabled, deleteEnabled);
};

INTF_FORM.CONTROL.clearComboboxItems = function(id, controlName) {
    return MainFormFlash.Callback_Form_clearComboboxItems(id, controlName);
}

INTF_FORM.CONTROL.setTabSheetActivePage = function(id, pagecontrol, tabsheet) {
    return MainFormFlash.Callback_Form_setTabSheetActivePage(id, pagecontrol, tabsheet);
}

// /////////////////////////////////////////////////////////////////////////////////////////////////
INTF_FORM.PARAMS.getParams = function(id) {
    var result = MainFormFlash.Callback_Form_getFormParams(id);
    result = __FLEX_OBJECT_2_JS_OBJECT__(result);
    return result;
};
// /////////////////////////////////////////////////////////////////////////////////////////////////

INTF_FORM.DATASET.isNew = function(id) {
    return MainFormFlash.Callback_Form_isNew(id);
};

INTF_FORM.DATASET.existsRecord = function(id, datasetName) {
    return MainFormFlash.Callback_Form_existsRecord(id, datasetName);
};

INTF_FORM.DATASET.existsDataSet = function(id, datasetName) {
    return MainFormFlash.Callback_Form_existsDataSet(id, datasetName);
};

INTF_FORM.DATASET.refresh = function(id, params) {
    return MainFormFlash.Callback_Form_refreshFormDatas(id, __JS_OBJECT_2_FLEX_OBJECT__(params));
};

INTF_FORM.DATASET.getValue = function(id, datasetName, fieldName) {
    return __FLEX_OBJECT_2_JS_OBJECT__(MainFormFlash.Callback_Form_getDataSetValue(id, datasetName, fieldName));
};

INTF_FORM.DATASET.setValue = function(id, datasetName, fieldName, value) {
    return MainFormFlash.Callback_Form_setDataSetValue(id, datasetName, fieldName, __JS_OBJECT_2_FLEX_OBJECT__(value));
};

INTF_FORM.DATASET.setValues = function(id, datasetName, values) {
    return MainFormFlash.Callback_Form_setDataSetValues(id, datasetName, __JS_OBJECT_2_FLEX_OBJECT__(values));
};

INTF_FORM.DATASET.getDatas = function(id, datasetName) {
    var records = MainFormFlash.Callback_Form_getDataSetRecords(id, datasetName);
    return __FLEX_OBJECT_2_JS_OBJECT__(records);
};

INTF_FORM.DATASET.copyFrom = function(id, params, datasetNames) {
    return MainFormFlash.Callback_Form_copyForm(id, __JS_OBJECT_2_FLEX_OBJECT__(params), WrapObjectToArray(datasetNames));
};

INTF_FORM.DATASET.appendRecords = function(id, datasetName, records) {
    return MainFormFlash.Callback_Form_batchAppendRecords(id, datasetName, __JS_OBJECT_2_FLEX_OBJECT__(WrapObjectToArray(records)));
};

INTF_FORM.DATASET.batchSetValues = function(id, datasetName, keys, records) {
    return MainFormFlash.Callback_Form_batchSetDatasetRecordsByKeys(id, datasetName, __JS_OBJECT_2_FLEX_OBJECT__(WrapObjectToArray(keys)), __JS_OBJECT_2_FLEX_OBJECT__(WrapObjectToArray(records)));
};

INTF_FORM.DATASET.updateDataSetValues = function(id, datasetName, records) {
    return MainFormFlash.Callback_Form_updateDataSetValues(id, datasetName, __JS_OBJECT_2_FLEX_OBJECT__(WrapObjectToArray(records)));
};

INTF_FORM.DATASET.save = function(id) {
    var result = MainFormFlash.Callback_Form_save(id);
    return result;
};

INTF_FORM.DATASET.execute = function(id, datasetName, params, type) {
    var result = false;
    params["datasetname"] = datasetName;
    params["sqltype"] = type;
    params["o"] = "executedataset";
    $.post("../client/dataservice", filterParams(params), function(text) {
                result = getBooleanAjaxValue(text);
            }).fail(function() {
                result = false;
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
            });
    return result;
};

INTF_FORM.DATASET.batchExecute = function(id, datasetName, params, type) {
    var result = false;
    params = {
        params : $.toJSON(WrapObjectToArray(params))
    };
    params["datasetname"] = datasetName;
    params["sqltype"] = type;
    params["o"] = "batchexecutedataset";
    $.post("../client/dataservice", filterParams(params), function(text) {
                result = getBooleanAjaxValue(text);
            }).fail(function() {
                result = false;
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
            });
    return result;
};

function filterInvalidChar(str) {
    str = StringUtils.replaceAll(str, ":NaN", ":\"\"");
    str = StringUtils.replaceAll(str, ":\"NaN\"", ":\"\"");
    str = StringUtils.replaceAll(str, ":null", ":\"\"");
    str = StringUtils.replaceAll(str, ":\"null\"", ":\"\"");
    return str;
}

INTF_FORM.DATASET.saveToServer = function(data) {
    var params = data[0];
    params.datasetValues = filterInvalidChar(params.datasetValues);
    var result = null;
    $.post("../client/dataservice?o=saveform", params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}

INTF_FORM.DATASET.clearRecords = function(id, datasetName) {
    return MainFormFlash.Callback_Form_clearDataSetRecords(id, datasetName);
};

INTF_FORM.DATASET.deleteRecord = function(id, datasetName) {
    return MainFormFlash.Callback_Form_deleteDataSetRecord(id, datasetName);
};

INTF_FORM.DATASET.deleteRecords = function(id, datasetName, keys) {
    return MainFormFlash.Callback_Form_deleteDataSetRecordByKeys(id, datasetName, keys);
};

// //////////////////////////////////////////////////////////////////////////////////////////////////
var formids2size = {};
INTF_FORM.UTILS.getFormsSize = function(ids) {
    var key = ids.join("_");
    if (!formids2size[key]) {
        $.post("../client/formsdefine", {
                    o : "getformsize",
                    formids : ids.join(",")
                }, function(text) {
                    var json = getObjectAjaxValue(text, true);
                    if (json)
                        formids2size[key] = json;
                }).fail(function() {
                    GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                    result = AJAX_ERROR_MSG;
                });
    }
    return formids2size[key];
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////
var getFormsDefine = INTF_FORM.getDefine;
var saveFormDataset = INTF_FORM.DATASET.saveToServer;
var getFomrsSize = INTF_FORM.UTILS.getFormsSize;
function getPortalFormSizes(ids) {
    return getFomrsSize(ids[0]);
};
// /////////////////////////////////////////////////////////////////////////////////////////////////////
// 增加缓存机制
var comboboxLinkCache = [];
function getFormDataSetRecord4Combobox(params) {
    var values = params[0];
    var ds = params[1];
    var dsNames = [];
    for (var i = 0; i < ds.length; i++)
        dsNames.push(ds[i].ds);
    var dataKey = dsNames.join() + "-2-" + values.data;
    var result = comboboxLinkCache[dataKey];
    if (!result) {
        $.post("../client/dataservice", {
                    o : "getDataSetRecord4ComboBox",
                    firstKey : values.data,
                    ds : dsNames.join(),
                    firstDS : params[2]
                }, function(text) {
                    var json = getObjectAjaxValue(text);
                    if (json) {
                        result = json;
                        comboboxLinkCache[dataKey] = result;
                    }
                }).fail(function() {
                    GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                    result = AJAX_ERROR_MSG;
                });
    }
    return result;
};
// ////////////////////////////////////////////////////////////////////////////////////////////////////////
function dynamicLoadFormDataSetRecord(params) {
    var params = params[0];
    var result = {
        result : false
    };
    $.post("../client/dataservice", {
                o : "getDataSetRecords",
                params : $.toJSON(params)
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ////////////////////////////////////////////////////////////////////////////////////////////////////////
function dynamicLoadFormDatasetRecords(params) {
    var result = {
        result : false
    };
    $.post("../client/dataservice", {
                o : "getDataSetRecords",
                ds : params[0].join(),
                params : $.toJSON(params[1])
            }, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// //////////////////////////////////////////////////////////////////////////////////////////////////////
function getASyncDataSetRecords(params) {
    var result = null;
    params[0].flexSessionID = flexSessionID; // 缓存
    $.post("../client/dataservice", params[0], function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////////
function dynamicLoadDataSetRecords4Combobox(params) {
    var result = null;
    params = params[0];
    params.o = "dynamicLoadDataSetRecords4Combobox";
    $.post("../client/dataservice", params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// ////////////////////////////////////////////////////////////////////////////////////////////////////////
