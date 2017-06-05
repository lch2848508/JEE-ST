<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="net.minidev.json.JSONArray"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="com.estudio.gis.oracle.WebGISResourceService4Oracle"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="com.estudio.web.servlet.config.WebParamService"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%
    String requestMapAppId = request.getParameter("appId");
	String url = request.getRequestURI();
	url = url.replace("/WebGIS/index.jsp", "");
	JSONObject mapAppsJson = WebGISResourceService4Oracle.getInstance().getMapAppList(RuntimeContext.getClientLoginService().getLoginInfo(request.getSession()));
	JSONArray mapAppArray = mapAppsJson.getJSONArray("mapApps");
	if(StringUtils.isEmpty(requestMapAppId))requestMapAppId = mapAppArray!=null? mapAppArray.getJSONObject(0).getString("id"):"-1";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8" />  
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<link rel="icon" href="../images/logo.ico" mce_href="images/logo.ico" type="image/x-icon">
<link rel="shortcut icon" href="../images/logo.ico"	mce_href="images/logo.ico" type="image/x-icon">
<title><%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%></title>
<script type="text/javascript" src="../js/jslib/jquery/jquery.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
<script type="text/javascript" src="../js/jslib/jquery/jquery.json.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
<script type="text/javascript" src="../js/jslib/utils.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
<script type="text/javascript" src="../js/javascript/flexclient/intf_iframe4flex.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
<script type="text/javascript" src="webgis_config.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
<script type="text/javascript" src="../js/jslib/jquery/zebra_dialog/zebra_dialog.js"></script>
<link rel="stylesheet" href="../js/jslib/jquery/zebra_dialog/css/default/zebra_dialog.css" type="text/css">

<script language="javascript" type="text/javascript">
		var baseURL = StringUtils.before(window.location.href,"WebGIS/index.jsp");
		function getWebgisModuleDir() {
			return baseURL + "flexclient/flash/";
		}

		function getWebGisProxyCache() {
			return baseURL + "webgis/proxy";
		}
		
		function getMapAppList() {
			return <%= mapAppsJson %>;
		}
		
		function getDefaultMapAppId()
		{
			return <%= requestMapAppId %>;
		}
		
		function getUserId()
		{
			return <%= RuntimeContext.getClientLoginService().getLoginInfo(request.getSession()).getId() %>;
		}
		
		function getUserName()
		{
			return "<%= RuntimeContext.getClientLoginService().getLoginInfo(request.getSession()).getRealName() %>";
		}
		
		function isLogin()
		{
			return true;
		}
		
		function isMisRole()
		{
			return <%= RuntimeContext.getClientLoginService().getLoginInfo(request.getSession()).isMisRole() %>;
		}
		
		function getAppCaption()
		{
			return "<%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%>";
		}
		
		function getMapAppURL()
		{
			return "<%= request.getRequestURI() %>";
		}
		
		function getMapVersion()
		{
			return <%= RuntimeConfig.getInstance().getVersion() %>;
		}
		
		function getLogoUrl()
		{
			return {
				LOGOURL:"../images/<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_URL,"logo.png")%>",
				LOGOLEFT:<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_Left,"0")%>,
				LOGOTOP:<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_Top,"0")%>,
				LOGOBGURL:"../images/<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_BG_URL,"logo_bg.png")%>"				
			};
		}
		
		
		
		function changePassword(oldPwd,newPwd) {
			$("#areaLogger").val($(document.body).html());
		    return false;
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
	
	 $.ajaxSetup({
  			async: false
  		});
  		
  	function OpenMISLayerDetailInfo(params)
  	{
  		var url = params.url;
  		if(url&&url!="") url += "?id=" + params.id
  		window.open(url);
  	}
  	
  	function filterSpatialAnalyResult(params){
  		var isIE = /msie/.test(navigator.userAgent.toLowerCase());
  		var app = isIE ? window["MainForm"]	: document["MainFormEx"];
  		app.filterSpatialAnalyResult(params);
  	}
  	
  	$(function(){
     	if(isBrowseScale()){
     		$("#divZoomStatus").show();
     		var totalTimes = 15000;
			setTimeout(function(){$("#divZoomStatus").hide();},totalTimes);
		}
     });
     
  function executePredefineHookfunction(frameId) {}
 
  function getWebGISConfig() {
  	     return WebGISConfig;
  }
  
  function openDownloadFontDialog()
  {
			   winRegister = new $.Zebra_Dialog('', {
			      source: {'iframe': {
			         'src':  '../download/filelist.html#chrome',
			         'height': 500
			     	}},
		        width: 1024,
		        type:false,
		        title:  'Chrome字体乱码解决方案'
		    });
  }
  
 function openFlatWindow(caption,url,width,height)
 {
 			var w = $(document.body).width()-30;
 			var h = $(document.body).height()-120;
			   winRegister = new $.Zebra_Dialog('', {
			      source: {'iframe': {
			         'src':  url,
			         'height': h,
			         'scrolling':'yes'
			     	}},
		        width: w,
		        buttons:false,
		        type:false,
		        title:  caption
		    });
}
  
  
</script>



<style>
* {
	font-size: 12px;
	zoom: 1;
}

html {
	width: 100%;
	height: 100%;
}

body {
	margin: 0px;
	overflow: hidden;
	background-color: #D9E9FE;
	width: 100%;
	height: 100%;
	zoom: 1;
	font-family: "微软雅黑", "新宋体";
}

#divZoomStatus {
	position: absolute;
	left: 0px;
	right: 0px;
	bottom: 0px;
	z-index: 1;
	background-color: #00F;
	text-align: center;
	padding: 4px;
	font-size: 12px;
	font-weight: bold;
	color: #FF3;
	display:none;
}
</style>
</head>
<body scroll="no">
	<object id="MainForm"
		style="POSITION: absolute;left:0;top:0;width:100%;height:100%"
		classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">
		<param name="movie"
			value="../flexclient/flash/MapApp.swf?version=<%= RuntimeConfig.getInstance().getVersion() %>" />
		<param name="quality" value="high" />
		<param name="bgcolor" value="#FFFFFF" />
		<param name="allowScriptAccess" value="always" />
		<param name="allowFullScreen" value="true" />
		<param name="wmode" value="opaque" />
		<param name="scale" value="1"/>
		<embed wmode="opaque"
			style="POSITION: absolute;left:0;top:0;width:100%;height:100%"
			src="../flexclient/flash/MapApp.swf?version=<%= RuntimeConfig.getInstance().getVersion() %>"
			allowFullScreen="true" quality="high" bgcolor="#FFFFFF" width="100%"
			height="100%" name="MainFormEx" align="middle" play="true" scale="1"
			loop="false" allowScriptAccess="*"
			type="application/x-shockwave-flash" />
	</object>
	<div id="divZoomStatus">您的浏览器网页显示比例不是100%,您可以通过组合键 <font color="#FF0000">Ctrl+0</font> 使其恢复正常，这样可以提高本系统的显示效果!</div>
</body>
</html>