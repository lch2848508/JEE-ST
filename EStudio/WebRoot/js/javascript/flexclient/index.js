// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
// 初始化Flex同浏览器的桥接
var MainFormFlash = null;
$(function() {
	MainFormFlash = isIE() ? window["MainForm"] : document["MainFormEx"];
});
// ////////////////////////////////////////////////////////////////////////////////////////////////////////
// 注销
function logoff() {
	$.ajax({
		url : "../servlet/login?o=logoff",
		data : "logoff",
		dataType : "text",
		async : false,
		type : "POST",
		error : function() {
		},
		success : function(responseText) {
		}
	});
	var str = window.location.href;
	str = str.substr(0, str.lastIndexOf("/"));
	str = str.substr(0, str.lastIndexOf("/"));
	str = str + "/index.jsp";
	window.location.href = str;
}
// /////////////////////////////////////////////////////////////////////////////////////////////
function changePassword(oldPwd, newPwd) {
	var result = false;
	var url = "../servlet/login?o=changepwd";
	$.post(url, {
		edit_oldpassword : oldPwd,
		edit_newpassword : newPwd
	}, function(text) {
		eval("json=" + text + ";");
		result = json.r;
	}).fail(function() {
		result = false;
	});
	return result;
}
// //////////////////////////////////////////////////////////////////////////////////////////////