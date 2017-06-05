<%@page import="com.estudio.utils.ExceptionUtils"%>
<%@page import="com.estudio.define.webclient.form.FormDefine"%>
<%@page import="com.estudio.intf.db.IDBHelper"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="com.estudio.utils.Convert"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
    String formIds = request.getParameter("formIds");
	String iframeId = request.getParameter("iframeId");
	String[] ids = formIds.split(",");
	StringBuilder sb = new StringBuilder();
	Connection con = null;
	IDBHelper DBHELPER = RuntimeContext.getDbHelper();
	try {
        con = DBHELPER.getConnection();
        for (final String id : ids) {
    		final FormDefine form = RuntimeContext.getFormDefineService().getFormDefine(Convert.try2Long(id, -1), con);
    		sb.append(form.getJscript());
        }
    } catch (final Exception e) {
        ExceptionUtils.loggerException(e,con);
    } finally {
        DBHELPER.closeConnection(con);
    }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
    if(RuntimeConfig.getInstance().isRelease()) {
%>
<script type='text/javascript' src='../js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>>'></script>
<script type='text/javascript' src='../js/release/modal.form.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<%
    } else {
%>
<script type='text/javascript' src='../js/jslib/jquery/jquery.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/jslib/jquery/jquery.json.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/jslib/utils.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<script type='text/javascript' src='../js/javascript/client/formui_api.js?version=<%=RuntimeConfig.getInstance().getVersion()%>'></script>
<%
    }
%>
<script type='text/javascript'>
  var __formids__="<%=iframeId%>";
  function getIFrameID() {return __formids__;}
  <%=sb%>   
</script>
</head>
<body onload="documentOnLoad()">

</body>
</html>