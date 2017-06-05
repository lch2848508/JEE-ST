function getQueryGroupDefine(id) {
	id = id[0];
	var result = null;
	var URL = "../client/queryservlet";
	var params = {
		o : "getUIDefine",
		id : id
	};
	$.post(URL, params, function(text) {
		result = getObjectAjaxValue(text);
	}).fail(function() {
		GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
		result = AJAX_FAIL_ERROR;
	});
	return result;
}
// ----------------------------------------------------------------------------
// 查询数据
function queryRecords(params) {
	// {typeId:v_processTypeId,ui_id:_ui_id}
	var ui_id = params[0].id;
	var r = params[0].r;
	var p = params[0].p;
	var filterParmas = params[0].filterParams;
	var result = null;
	var URL = "../client/queryservlet";
	var params = {
		o : "query",
		r : r,
		p : p,
		ui_id : ui_id
	};
	for ( var k in filterParmas)
		params[k] = filterParmas[k];
	$.post(URL, params, function(text) {
		result = getObjectAjaxValue(text);
	}).fail(function() {
		GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
		result = AJAX_FAIL_ERROR;
	});
	return result;
}
// ------------------------------------------------------------------------------
function exportExcel(params) {
	// {typeId:v_processTypeId,ui_id:_ui_id}
	var ui_id = params[0].id;
	var r = params[0].r;
	var p = params[0].p;
	var filterParmas = params[0].filterParams;
	var filterFields = params[0].filterFields;
	var result = null;
	var URL = "../client/queryservlet";
	var params = {
		o : "exportExcel",
		r : r,
		p : p,
		ui_id : ui_id,
		filterFields : filterFields
	};
	for ( var k in filterParmas)
		params[k] = filterParmas[k];
	$.post(URL, params, function(text) {
		result = getObjectAjaxValue(text);
	}).fail(function() {
		GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
		result = AJAX_FAIL_ERROR;
	});
	return result;
}
// ----------------------------------------------------------------------------------

var __queryControlFilterComboboxItems__ = {};
function getQueryControlFilterComboboxItems(params) {
	params = params[0];
	var key = params.id + "-" + params.paramName + "-" + params.PARENT_COMBOBOX;
	if (__queryControlFilterComboboxItems__[key])
		return __queryControlFilterComboboxItems__[key];
	params.o = "getQueryControlFilterComboboxItems";
	var url = "../client/queryservlet";
	var result = null;
	$.post(url, params, function(text) {
		result = getObjectAjaxValue(text);
		__queryControlFilterComboboxItems__[key] = result;
	}).fail(function() {
		GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
		result = AJAX_ERROR_MSG;
	});
	return result;
}
// -----------------------------------------------------------------------------------
