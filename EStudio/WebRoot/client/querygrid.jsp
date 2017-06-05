<%@page import="com.estudio.impl.webclient.query.QueryUIDefineService"%>
<%@page import="com.estudio.define.webclient.query.QueryUIDefine"%>
<%@page import="com.estudio.intf.db.IDBHelper"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="com.estudio.utils.Convert"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String portalId = request.getParameter("portalId");
	String iframeId = request.getParameter("iframeId");
	String uiid = request.getParameter("uiid");
	Connection con = null;
	IDBHelper DBHELPER = RuntimeContext.getDbHelper();
	QueryUIDefine ui = null;
    try {
        con = DBHELPER.getConnection();
        ui = QueryUIDefineService.getInstance().getUIDefine(null,Long.parseLong( uiid));
    } finally {
        DBHELPER.closeConnection(con);
    }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<% if(RuntimeConfig.getInstance().isRelease()) {%>
<script type='text/javascript' src='../js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>>'></script>
<script type='text/javascript' src='../js/release/modal.query.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<% } else { %>					
<script type='text/javascript' src='../js/jslib/jquery/jquery.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/jslib/jquery/jquery.json.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/jslib/utils.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/javascript/client/query_api.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<% } %>
<script type='text/javascript'>
  var __iframeId="<%=iframeId%>";
  var __portalId__ = <%= portalId %>
  var __queryid__ = <%=uiid%>;
  function getFrameID(){return "<%=iframeId%>";}
  <%= 
  	ui.getJs()
  %>
  
  function documentOnLoad()
  {
  if(!top.query_on_load_event_flags[__queryid__])
  {
  	  top.query_on_load_event_flags[__queryid__] = true;
  	  if(window["QUERY_ON_INITIALIZE"])
  	  	QUERY_ON_INITIALIZE();
  }
  }
</script>
</head>
<body onload="documentOnLoad()">

</body>
</html>