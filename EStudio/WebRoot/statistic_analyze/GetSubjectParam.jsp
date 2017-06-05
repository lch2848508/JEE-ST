<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%
	String selectID = request.getParameter("id");
	response.getWriter().print(StatisticAnalyzeDataServices.getInstance().getSubjectParam(selectID).toJSONString());%>
 
 