<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page import="java.util.List"%>
<%@page import="net.minidev.json.JSONArray"%>
<%@page import="com.estudio.utils.Convert"%>
<%@page import="com.estudio.web.servlet.config.WebParamService"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String p_id = request.getParameter("p_id");
    String attachmentType = request.getParameter("type");
    long selectedId = Convert.try2Long(request.getParameter("id"), -1);
    List<JSONObject> records = RuntimeContext.getAttachmentService().listFiles(null, attachmentType, p_id);
    int selectedIndex = -1;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%>-附件查看器</title>
<%
    if(RuntimeConfig.getInstance().isRelease()) {
%>
<script type="text/javascript" src="../js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<%
    } else {
%>
<script type="text/javascript" src="../js/jslib/jquery/jquery.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<script type="text/javascript" src="../js/jslib/jquery/jquery.json.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<%
    }
%>
<script type="text/javascript" src="../js/javascript/weboffice.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<script type="text/javascript" src="../js/jslib/jquery/jquery.media.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<script type="text/javascript" src="../js/jslib/pdfobject_source.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>



<style type="text/css">
html {
	font-family: "微软雅黑", "新宋体";
	font-size: 12px;
	font-style: normal;
	width: 100%;
	height: 100%;
	padding: 0;
	margin: 0;
}

body {
	width: 100%;
	height: 100%;
	padding: 0;
	margin: 0;
}

#divNavigator {
	background-color: #EBF4FF;
	overflow: auto;
	position: absolute;
	height: 100%;
	width: 350px;
	left: 0px;
	top: 0px;
	right: 350px;
	bottom: 0px;
	border-right-width: 1px;
	border-right-style: solid;
	border-right-color: #000;
}

#divContain {
	position: absolute;
	left: 350px;
	top: 31px;
	right: 0px;
	bottom: 0px;
	overflow: auto;
	padding-left:1px;
}

#divNavigator li {
	padding: 5px;
	color: #0000EE;
}

#divNavigator li a {
	text-decoration: none;
	padding-left: 2px;
	color: #00F;
}

#divNavigator li img {
	
}

#divHeader {
	padding: 0px;
	position: absolute;
	left: 351px;
	top: 0px;
	right: 0px;
	height: 30px;
	background-color: #EBF4FF;
	border-bottom-width: 1px;
	border-bottom-style: solid;
	border-bottom-color: #000;
}

#divTitle {
	float: left;
	padding-left: 4px;
	font-size: 14px;
	font-weight: bold;
	padding-top: 4px;
}

#divToolbar {
	float: right;
	padding: 2px;
}

.btn {
	background-color: #CCC;
	border: 1px solid #000;
	height: 25px;
	width: 80px;
	font-weight: bold;
	visible:hidden;
}

.btn1 {
	background-color: #CCC;
	border: 1px solid #000;
	height: 25px;
	width: 80px;
	font-weight: bold;
	visible:hidden;
}

.r0 {
	
}

.r270 {
	-moz-transform: rotate(-90deg);
	-webkit-transform: rotate(-90deg);
	filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=3 );
}

.r90 {
	-moz-transform: rotate(90deg);
	-webkit-transform: rotate(90deg);
	filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=1 );
}

.r180 {
	-moz-transform: rotate(180deg);
	-webkit-transform: rotate(180deg);
	filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=2 );
}
</style>

<script>
	var currentFileURL = "";
	function goURL(url, caption) {
		var w = $("#divContain").width();
		var h = $("#divContain").height();
		
		currentFileURL = url;
		$("#divTitle").html("当前文件:" + caption);
		$(".btn").hide();
		$("#divContain").html("");
		if (isOfficeFile(url)) {
			viewOffice(url);
		} else if (isPictureFile(url)) {
			$(".viewPic").show();
			viewPicture(url);
		} else if (isMedia(url)) {
			viewMedia(url);
			$(".media").show();
		} else if (isPDF(url)) {
			$(".pdf").show();
			viewPDF(url);
		} else {
			downFile();
		}
		
	}
	
	function viewPDF(url)
	{
		var w = $("#divContain").width();
		var h = $("#divContain").height();
		var html = "<object	data='" + url + "' type='application/pdf' width='" + w + "' height='" + h + "'></object>";
		$("#divContain").html(html);
	}
	
	function viewMedia(url)
	{
		var w = $("#divContain").width();
		var h = $("#divContain").height();
		var html = "<a class='media {width:"+w+", height:"+h+"}' href=\"" + url + "\"></a>";
		$("#divContain").html(html);
		$("a.media").media();
	}
	
	function viewOffice(url)
	{
		downFile();
		//var openDocObj = new ActiveXObject("SharePoint.OpenDocuments.2");
		//alert(openDocObj);
		//alert(url);
		//openDocObj.ViewDocument(url);
		//var html = createOfficeControl();
		//$("#divContain").html(html);
		//viewOfficeURL(url);
	}
	
	function viewOfficeURL(url) {
	var office = $("#WebOffice1")[0];
	if (office) {
			office.HideMenuItem(0x01+0x02+0x04+0x10+0x20+0x1000+0x4000+0x2000);
			var fileExt = getFileExt(url).toLowerCase();
			if(fileExt=="xls" || fileExt=="xlsx")
				office.LoadOriginalFile("../" + url,"xls");
			else if(fileExt=="doc" || fileExt=="docx")
				office.LoadOriginalFile("../" + url,"doc");
			else if(fileExt=="ppt" || fileExt=="pptx")
				office.LoadOriginalFile("../" + url,"ppt");
		}
	}
	
	function viewPicture(url)
	{
		var w = $("#divContain").width();
		var h = $("#divContain").height();
		imageRDegree = 0;
		var html = "<img id='imgView' src='" + url + "' width='" + w + "' height='" + h + "'/>"
		$("#divContain").html(html);
		$('#imgView').load(function() {  
             adjustImgSize($(this), $(this).parent().width(), $(this).parent().height(),false);  
         });
	}
	
	var imageRDegree = 0;
	function rImage(r)
	{
		imageRDegree += r;
		imageRDegree = imageRDegree%360;
		var html = "<img id='imgView' class='r" + imageRDegree + "' src='" + currentFileURL + "' width='100%' height='100%'/>"
		$("#divContain").html(html);
		$('#imgView').load(function() {  
			if(imageRDegree==0 || imageRDegree==180)
             	adjustImgSize($(this), $(this).parent().width(), $(this).parent().height(),false);  
             else 
             	adjustImgSize($(this), $(this).parent().height(), $(this).parent().width(),true);  
         });
	}

	function isOfficeFile(url) {
		var ext = getFileExt(url);
		if (ext != "") {
			ext = ext.toLowerCase();
			return jQuery.inArray(ext,[ "doc", "xls", "ppt", "docx", "pptx", "xlsx"])!=-1;
		}
		return false;
	}

	function isPictureFile(url) {
		var ext = getFileExt(url);
		if (ext != "") {
			ext = ext.toLowerCase();
			return jQuery.inArray(ext,[ "bmp", "jpg", "jpeg", "gif", "png"])!=-1;
		}
		return false;
	}
	
	function isMedia(url) {
		var ext = getFileExt(url);
		if (ext != "") {
			ext = ext.toLowerCase();
			return jQuery.inArray(ext,[ "wmv", "flv", "avi", "mp3", "mpg", "mpeg", "mp4","3g2","3gp","wma"])!=-1;
		}
		return false;
	}	

	function isPDF(url) {
		var ext = getFileExt(url);
		if (ext != "") {
			ext = ext.toLowerCase();
			return ext=="pdf";
		}
		return false;
	}


	function getFileExt(pathfilename) {
		var reg = /(\\+)/g;
		var pfn = pathfilename.replace(reg, "#");
		var arrpfn = pfn.split("#");
		var fn = arrpfn[arrpfn.length - 1];
		var arrfn = fn.split(".");
		return arrfn[arrfn.length - 1];
	}
	
	function adjustImgSize(img, boxWidth, boxHeight,isRotate) {  
        var tempImg = new Image();          
        tempImg.src = img.attr('src');  
        var imgWidth=tempImg.width;  
        var imgHeight=tempImg.height;  
        if((boxWidth/boxHeight)>=(imgWidth/imgHeight))  
        {  
            img.width((boxHeight*imgWidth)/imgHeight);  
            img.height(boxHeight);  
            var margin=(boxWidth-img.width())/2;  
            img.css(isRotate?"margin-top":"margin-left",margin);  
        }  
        else  
        {  
            img.width(boxWidth);  
            img.height((boxWidth*imgHeight)/imgWidth);  
            var margin=(boxHeight-img.height())/2;  
            img.css(isRotate?"margin-left":"margin-top",margin);  
        }  
    }; 
	
	function downFile()
	{
		window.open(currentFileURL, "downloadWindow");
	}
	
	function createIFrame(frameid,htmlContent) {
    $("#" + frameid).remove();
    var html = "<iframe id='" + frameid + "' name='" + frameid + "' style='width:100%;height:100%'></iframe>";

    var ifrdoc = $(html).appendTo(document.body)[0].contentWindow.document;
    ifrdoc.designMode = "on"; // 文档进入可编辑模式
    ifrdoc.open(); // 打开流
    ifrdoc.write(htmlContent);
    ifrdoc.close(); // 关闭流
    ifrdoc.designMode = "off"; // 文档进入非可编辑模式

    return true;
};

function downloadQuickTime()
{
	window.open("../download/QuickTimeInstaller.exe", "downloadWindow");
}

function downloadPDFReader()
{
	window.open("../download/FoxitReaderchs7.1.4.330Setup.exe", "downloadWindow");	
}
	
</script>
</head>

<body>

	<!-- 导航 -->
	<div id="divNavigator" name="divNavigator">
		<%
		    int index = 0;
			for (JSONObject record : records) {
		%>
		<li><img src="../images/filetype_images/16x16/<%=record.getString("fileext")%>.gif"></img><a
			href='javascript:goURL("<%=record.getString("url")%>","<%=(index+1)%>. <%=record.getString("caption")%>")'><%=(index+1)%>. <%=record.getString("caption")%></a></li>
		<%
		    	if (selectedId == record.getLong("id"))selectedIndex = index;
		    	index++;
			}
		%>
	</div>

	<div id="divHeader" name="divHeader">
		<div id="divTitle"></div>
		<div id="divToolbar">
			<input onClick="rImage(270)" class="btn viewPic" name="btnDownload" type="button" value="左转" /> 
			<input onClick="rImage(180)" class="btn viewPic" name="btnDownload" type="button" value="翻转" /> 
			<input onClick="rImage(90)" class="btn viewPic" name="btnDownload" type="button" value="右转" /> 
			<input onClick="downloadQuickTime()" class="btn media" name="btnDownload" type="button" value="下载播放器" /> 
			<input onClick="downloadPDFReader()" class="btn pdf" name="btnDownload" type="button" value="下载阅读器" /> 
			<input onClick="downFile()" class="btn1" name="btnDownload"	type="button" value="下载" />
		</div>
	</div>

	<div id="divContain" name="divContain"></div>
</body>
<script>
$(function(){
	<%if(selectedIndex==-1 && !records.isEmpty()) selectedIndex = 0;%>
	<%if(selectedIndex!=-1) { JSONObject record = records.get(selectedIndex);%>
		goURL("<%=record.getString("url")%>", "<%=(selectedIndex+1)%>. <%=record.getString("caption")%>");
<%}%>
	})
</script>
</html>
