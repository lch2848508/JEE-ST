<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	String subjectname = request.getParameter("subjectName");
	response.getWriter().print(StatisticAnalyzeDataServices.getInstance().SubjectJudge(subjectname).toJSONString());
%>