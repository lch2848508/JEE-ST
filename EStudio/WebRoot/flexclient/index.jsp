<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="com.estudio.web.servlet.config.WebParamService"%>
<%@page import="com.estudio.intf.webclient.form.IPortal4ClientService"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="com.estudio.define.sercure.ClientLoginInfo"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>


<%
    ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(session);
	IPortal4ClientService portal4ClientService = RuntimeContext.getPortal4ClientService();
    try {
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
		<link rel="shortcut icon" href="../images/logo.ico" mce_href="images/logo.ico" type="image/x-icon">
		<title><%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%></title>
		
<% if(RuntimeConfig.getInstance().isRelease()) { %>
		<script type="text/javascript" src="../js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/release/flexclient.index.min.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
<% } else { %>
		<script type="text/javascript" src="../js/jslib/jquery/jquery.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/jslib/jquery/jquery.json.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/jslib/utils.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/index.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_global.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_grid.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_gridex.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_form.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_form_extend.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_workflow.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_query.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_message.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_iframe4flex.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/intf_third_interface.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
		<script type="text/javascript" src="../js/javascript/flexclient/extensionswf.js?version=<%=RuntimeConfig.getInstance().getVersion() %>"></script>
<% } %>

		<script type="text/javascript" src="../js/jslib/jquery/zebra_dialog/zebra_dialog.js"></script>
		<link rel="stylesheet" href="../js/jslib/jquery/zebra_dialog/css/default/zebra_dialog.css" type="text/css">

		<script language="javascript" type="text/javascript">
		var VAR_DOCUMENT_TITLE = "<%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%>";
		var NAVIGATOR_TYPE = "<%=WebParamService.getInstance().getParamValue(WebParamService.NAVIGATOR_CONTROL)%>";
		var NAVIGATOR_WIDTH= <%=WebParamService.getInstance().getParamValue(WebParamService.NAVIGATOR_WIDTH)%>
    	var COPYGIGHTSTR = "<%=WebParamService.getInstance().getParamValue(WebParamService.COPYRIGHT)%>";    	
    	var PORTAL_ITEMS = <%=portal4ClientService.getPortalTreeByUserID(null, loginInfo.getId(), loginInfo.getDepartmentId(), loginInfo)%>;    	   	
    	var LOGIN_NAME = "<%=loginInfo.getRealName()%>";
    	var COPYRIGHT = "<%=WebParamService.getInstance().getParamValue(WebParamService.COPYRIGHT)%>";
    	var ISRELEASE = <%= RuntimeConfig.getInstance().isRelease() %>;
    	var IS_GIS_ROLE = <%=loginInfo.isGisRole()  %>;
    	var ISSHOWMYDESKTOP = false; //是否显示首页

		var VAR_INDEX_PARAMS = {
			LOGIN_NAME : LOGIN_NAME,
			LOGIN_DUTY : "<%=loginInfo.getDuty()%>",
			NAVIGATOR_TYPE : NAVIGATOR_TYPE,
			NAVIGATOR_WIDTH : NAVIGATOR_WIDTH,
			PORTAL_ITEMS : PORTAL_ITEMS,
			COPYRIGHT:COPYRIGHT,
			TITLE:VAR_DOCUMENT_TITLE,
			ISRELEASE:ISRELEASE,
			APPVERSION:<%= RuntimeConfig.getInstance().getVersion() %>,
			ISSHOWMYDESKTOP:ISSHOWMYDESKTOP,
			LOGIN_USERID:<%=loginInfo.getId()%>,
			LOGIN_REALNAME:"<%=loginInfo.getRealName()%>",
			ICQ_ENABLED:<%= RuntimeConfig.getInstance().isICQEnabled() %>,
			JMS_ENABLED:<%= RuntimeConfig.getInstance().isJMSEnabled() %>,
			WEBGIS_ENABLED:<%= RuntimeConfig.getInstance().isWEBGISEnabled() %>,
			LOGOURL:"../images/<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_URL,"logo.png")%>",
			LOGOLEFT:<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_Left,"0")%>,
			LOGOTOP:<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_Top,"0")%>,
			LOGOBGURL:"../images/<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_BG_URL,"logo_bg.png")%>",
			IS_GIS_ROLE:IS_GIS_ROLE
		};
		
		function getIndexParams() {
			return VAR_INDEX_PARAMS;
		}
		
		var INTF_ENVIRONMENT = {
			USERID:<%=loginInfo.getId()%>,
			REALNAME:"<%=loginInfo.getRealName()%>"
		};
		
		function getUserId()
		{
		    return INTF_ENVIRONMENT.USERID;
		}
		
		
		
		
		
		var baseURL = StringUtils.before(window.location.href,"flexclient/index.jsp");
		function getWebgisModuleDir() {
			return baseURL + "flexclient/flash/";
		}

		function getWebGisProxyCache() {
			return baseURL + "webgis/proxy";
		}
		
	function getHTMLContent()
    {
    	//alert("Hello");
    	//$("#DebugText").val( $(document.body).html());
    }
	
	    $(function(){
     		if(isBrowseScale()){
				$("#divZoomStatus").show();
				setTimeout(function(){$("#divZoomStatus").hide();},15000);
			}
     	});	
	    
	    
	    function openDownloadFontDialog()
		{
			   winRegister = new $.Zebra_Dialog('', {
			      source: {'iframe': {
			         'src':  '../download/filelist.html#chrome',
			         'height': 500
			     	}},
		        width: 1024,
		        type:false,
		        buttons:false,
		        title:  'Chrome字体乱码解决方案'
		    });
		}
	    
	    function openFlatWindow(caption,url,width,height)
		{
			   winRegister = new $.Zebra_Dialog('', {
			      source: {'iframe': {
			         'src':  url,
			         'height': height,
			         'scrolling':'yes'
			     	}},
		        width: width,
		        buttons:false,
		        type:false,
		        title:  caption
		    });
		}
	    
	    var query_on_load_event_flags = {};
	    
	    function getAttachmentBasePath()
	    {
	    	return "<%= RuntimeContext.getAttachmentService().getServerURL() %>"
	    }
	    
	    function downloadAttachmentEx(url)
	    {
	    	url = "<%= RuntimeContext.getAttachmentService().getServerURL() %>" + url;
	    	window.open(url,"viewAttachment");
	    }
</script>



<style>
* {
	font-size: 12px;
	zoom: 1;
	padding:0px;
	margin:0px;
	*zoom: 1;
}

html {
	width: 100%;
	height: 100%;
	overflow: hidden;
	zoom: 1;
}

body {
	margin: 0px;
	overflow: hidden;
	background-color: #D9E9FE;
	width: 100%;
	height: 100%;
	zoom: 1;
}


#divZoomStatus {
	position: absolute;
	left: 0px;
	right: 0px;
	bottom: 0px;
	z-index: 1000;
	background-color: #00F;
	text-align: center;
	padding: 4px;
	font-family: "微软雅黑", "新宋体";
	font-size: 12px;
	font-weight: bold;
	color: #FF3;
	display:none;
}

</style>
	</head>
	<body scroll="no">
		<object id="MainForm" type="application/x-shockwave-flash" style="POSITION: absolute;left:0;top:0;width:100%;height:100%" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">
			<param name="movie" value="./flash/MainForm.swf?version=<%= RuntimeConfig.getInstance().getVersion() %>" />
			<param name="quality" value="high" />
			<param name="bgcolor" value="#FFFFFF" />
			<param name="allowScriptAccess" value="always" />
			<param name="allowFullScreen" value="true" />
			<!-- <param name="wmode" value="direct" />		  -->	
			<param name="wmode" value="opaque" />
			
			<embed style="POSITION: absolute;left:0;top:0;width:100%;height:100%" wmode="opaque"  src="./flash/MainForm.swf?version=<%= RuntimeConfig.getInstance().getVersion() %>"  allowFullScreen="true" quality="high" bgcolor="#FFFFFF" width="100%" height="100%" name="MainFormEx" align="middle" play="true" loop="false" quality="high" allowScriptAccess="*" type="application/x-shockwave-flash" >
			</embed>
			
		</object>
		<!-- 
		<textarea rows="" cols="" id="DebugText" style="POSITION: absolute;left:0;bottom:0;width:100%;height:50%"></textarea>
		 -->
<div id="divZoomStatus">
您的浏览器网页显示比例不是100%,您可以通过组合键 <font color="#FF0000">Ctrl+0</font> 使其恢复正常，这样可以提高本系统的显示效果！ </div>
	</body>
</html>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }
%>