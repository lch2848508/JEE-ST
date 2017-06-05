<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	String Dataid = request.getParameter("ID");
	response.getWriter().print(StatisticAnalyzeDataServices.getInstance().SubjectListDataService(Dataid).toJSONString());
%>