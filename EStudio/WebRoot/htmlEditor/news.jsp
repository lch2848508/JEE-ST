<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.estudio.DaemonService.WordPressDaemonService"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String categoryHTML = "";
	List<String>wordpressCategorys = WordPressDaemonService.getInstance().getWordPressCatetorys();
	if(wordpressCategorys!=null)
	{
		for(String str:wordpressCategorys)
			categoryHTML += "<option value=\"" + str + "\">" + str + "</option>";
	}
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>门户内容后台维护系统</title>
<style type="text/css">
*
{
	zoom:1
}

html {
	font-family: "微软雅黑", "新宋体";
	font-size: 12px;
	font-weight: normal;
	height: 100%;
	width: 100%;
	margin: 0px;
	padding: 0px;
}

body {
	font-family: "微软雅黑", "新宋体";
	font-size: 12px;
	font-weight: normal;
	height: 100%;
	width: 100%;
	overflow: hidden;
	margin: 0px;
	padding: 0px;
	zoom: 100%;
}

form {
	height: 100%;
	width: 100%;
}

#richEditor {
	width: 100%;
}

.caption {
	font-weight: bold;
	background: #EBF4FF;
}

#mainTable {
	border-top-style: none;
	border-right-style: none;
	border-bottom-style: none;
	border-left-style: none;
	margin: -1px;
}

#mainTable td {
	padding: 4px;
}

.w100 {
	width: 170px
}

.btn {
	font-size: 12px;
	font-weight: bold;
	width: 90px;
	height: 25px;
	border: 1px solid #036;
	background-color: #00F;
	color: #FFF;
}
</style>

<script type="text/javascript" src="../js/release/jquery.min.js"></script>
<script type="text/javascript" src="ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="ckeditor/adapters/jquery.js"></script>

<script language="javascript">
	var editor = null;
	var isNew = true;
	$(function() {
		CKEDITOR.disableAutoInline = true;
		var height = $("body").height() - 272;
		var editor = $("#richEditor").ckeditor({
			height : height
		});
		$(window).bind("resize", function() {
			resizeInputControls();
		});
		resizeInputControls();
		$('#inputForms').ajaxForm(ajaxOptions);

		$.ajaxSetup({
			async : false
		});
	});

	function resizeInputControls() {
		var controls = $(".autoWidth");
		for ( var i = 0; i < controls.length; i++) {
			var control = controls[i];
			$(control).width($(control).parent().width() - 8);
		}
	}

	var ajaxOptions = {
		beforeSubmit : ajaxValidate,
		success : ajaxSuccess,
		error : ajaxError
	};

	// 提交表单之前的检测函数
	function ajaxValidate(formData, jqForm, options) {
		var caption = $("#caption").val();
		if (caption == "") {
			alert("新闻标题不能为空");
			return false;
		}
		return true;
	}

	// 提交表单成功返回结果函数
	function ajaxSuccess(responseText, statusText) {
		var json = $.evalJSON(responseText);
		if (json.r) {
			$("#id").val(json.id);
			alert("成功发布门户内容。");
			isNew = false;
		} else {
			alert("发布门户内容失败。")
		}
	}

	// 提交表单失败 用于网络超时或服务器被关闭的情况
	function ajaxError() {
		alert("登录服务器失败，同服务器的链接丢失！");
	}

	function newNews() {
		if (isNew) {
			if (confirm("编辑的内容尚未保存，是否保存？")) {
				$("#btnSave").click();
				isNew = false;
			}
		}
		$("#caption").val("");
		$("#type").val("");
		$("#summary").val("");
		$("#source").val("");
		$("#author").val("");
		$("#regdate").val("");
		$("#richEditor").val("");
		$("#id").val("");
		isNew = true;
	}

	function editNews(params) {
		var id = params.id;
		$.get("news_service.jsp?o=get&id=" + id, function(text) {
			var json = $.evalJSON(text);
			$("#caption").val(json.caption);
			$("#type").val(json.type);
			$("#summary").val(json.summary);
			$("#source").val(json.source);
			$("#author").val(json.author);
			$("#regdate").val(json.regdate);
			$("#richEditor").val(json.content);
			$("#id").val(json.id);
		});
		isNew = false;
	}

	$(function() {
		var id = top.EditNewsID;
		if (id) {
			editNews({
				id : id
			});
		}
		top.EditNewsID = null;
	});

	function managerNews() {
		if (isNew) {
			if (confirm("编辑的内容尚未保存，是否保存？")) {
				$("#btnSave").click();
				isNew = false;
			}
		}
		top.GLOBAL.executePortalFunction("门户内容管理", "refreshNews", {});
	}
</script>
</head>

<body>
	<form id="inputForms" name="inputForms" action="news_service.jsp?o=save" method="post">
		<table width="100%" height="100%" id="mainTable" style="table-layout:fixed; height:100%; height:100%;; border-collapse:collapse" border="1" bordercolor="#7B889C" cellspacing="0" cellpadding="0">
			<col width="80" />
			<col width="190" />
			<col width="80" />
			<col />
			<tr height="30" style="background-color:#EBF4FF">
				<td colspan="4" align="right"><input id="btnSave" name="button" type="submit" class="btn" id="button" value="保存门户内容" /> <input name="button" id="btnNew" type="button" onclick="newNews()"
					class="btn" id="button" value="新建门户内容" /> <input name="button" type="button" class="btn" onclick="managerNews()" id="button" value="管理门户内容" /></td>
			</tr>
			<tr height="30">
				<td height="30" align="center" class="caption">标题：</td>
				<td colspan="3"><input name="caption" type="text" id="caption" class="autoWidth" /></td>
			</tr>
			<tr height="30">
				<td height="30" align="center" class="caption">类型：</td>
				<td><select name="type" id="type" style="width:176px">
					<% if(StringUtils.isEmpty(categoryHTML)) {%>
						<option value="新闻">新闻</option>
						<option value="通知">通知</option>
						<option value="公告">公告</option>
						<option value="其他">其他</option>
					<% } else { %>
						<%= categoryHTML %>
					<% } %>
				</select></td>
				<td align="center" valign="middle" class="caption">关键词：</td>
				<td><input name="summary" type="text" id="summary" class="autoWidth" /></td>
			</tr>
			<tr height="30">
				<td height="30" align="center" class="caption">时间：</td>
				<td><input name="regdate" type="text" id="regdate" style="width:170px" /></td>
				<td align="center" valign="middle" class="caption">来源：</td>
				<td><label for="source"></label> <input name="source" type="text" id="source" class="autoWidth" /></td>
			</tr>
			<tr height="30">
				<td height="30" align="center" class="caption">作者：</td>
				<td><input name="author" type="text" id="author" style="width:170px" /></td>
				<td align="center" valign="middle">&nbsp;</td>
				<td><input type="hidden" name="id" id="id" />
				</td>
			</tr>
			<tr>
				<td colspan="4" id="tdRichEditor" valign="top"><textarea name="richEditor" rows="30" id="richEditor" style="height:100%"></textarea>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
