<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="com.estudio.utils.Convert"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String portalId = request.getParameter("portalId");
	String iframeId = request.getParameter("iframeId");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<% if(RuntimeConfig.getInstance().isRelease()) {%>
<script type='text/javascript' src='../js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>>'></script>
<script type='text/javascript' src='../js/release/modal.portalex.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<% } else { %>					
<script type='text/javascript' src='../js/jslib/jquery/jquery.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/jslib/jquery/jquery.json.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/jslib/utils.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/javascript/client/showgridex_api.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<% } %>
<script type='text/javascript'>
  var __portalid__="<%=portalId%>";
  function getFrameID(){return "<%=iframeId%>";}
  <%= 
  	RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(Convert.str2Long(portalId), null).getJavaScript()
  %>
</script>
</head>
<body onload="documentOnLoad()">

</body>
</html>