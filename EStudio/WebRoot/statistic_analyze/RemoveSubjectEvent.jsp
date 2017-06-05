<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    
<%
	String selectID = request.getParameter("id");
	StatisticAnalyzeDataServices.getInstance().RemoveSubjectEvent(selectID);
 %>