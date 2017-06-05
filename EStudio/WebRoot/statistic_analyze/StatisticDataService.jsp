<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    
<%	
	long id = Long.parseLong(request.getParameter("ID"));
	response.getWriter().print(StatisticAnalyzeDataServices.getInstance().getStatisticDataJSON(id)); 
 %>
