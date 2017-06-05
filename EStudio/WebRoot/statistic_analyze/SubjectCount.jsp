<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	response.getWriter().print(StatisticAnalyzeDataServices.getInstance().SubjectCount().toJSONString());
%>