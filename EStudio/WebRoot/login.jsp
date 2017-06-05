<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%
	String u = request.getParameter("u");
	String p = request.getParameter("p");
%>
<script type="text/javascript" src="js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<script>
$(function(){
    var url = "servlet/login?o=login&u=<%=u%>&p=<%=p%>&skip_code=1";
    $.get(url,function(text){
        window.location.href = "WebGIS/index.jsp";
    });
    
});
</script>
