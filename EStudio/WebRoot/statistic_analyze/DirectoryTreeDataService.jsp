<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    
<%
	String DirectoryTree =StatisticAnalyzeDataServices.getInstance().getStatisticTreeJSON(-1);%>
<%=DirectoryTree%>