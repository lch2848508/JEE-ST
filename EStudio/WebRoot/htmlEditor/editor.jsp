<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=8">
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-control" content="no-cache">
<meta http-equiv="Cache" content="no-cache">
<title>富文本编辑器</title>
<style type="text/css">
* {
	zoom: 1
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


#richEditor {
	width: 100%;
	height:100%;
}
</style>

<script type="text/javascript" src="../js/release/jquery.min.js"></script>
<script type="text/javascript" src="ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="ckeditor/adapters/jquery.js"></script>

<script language="javascript">
	var editor = null;
	var needSetValue = false;
	var tempContent = "";
	$(function() {
		CKEDITOR.disableAutoInline = true;
		var height = $("body").height()-80;
		editor = $("#richEditor").ckeditor({
			height : height
		}).editor;
		setInterval(function(){
			if(needSetValue){
				needSetValue = false;
				$("#richEditor").val(tempContent);
				tempContent = "";
			}
		},250);
	});
	
	function setText(params)
	{
		var text = params.text;
		var oldText = getText();
		if(text!=oldText)
		{
			tempContent = text;
			needSetValue = true;
		}
	}
	
	function getText()
	{
		return $("#richEditor").val();
	}


</script>
</head>

<body >
	<textarea name="richEditor" id="richEditor" style="height:100%;width:100%"></textarea>
</body>
</html>
