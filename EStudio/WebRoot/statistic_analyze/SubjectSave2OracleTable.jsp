<%@page import="com.estudio.gis.statisticAnalyze.StatisticAnalyzeDataServices" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    
<%
	String subjectid = request.getParameter("SubjectID");
	String subjectname = request.getParameter("subjectName");
	String charttype = request.getParameter("chartType");
	String DataID = request.getParameter("data_id");
	String firstIndex = request.getParameter("index_first");
	String secondIndex = request.getParameter("index_second");
	String thirdIndex = request.getParameter("index_third");
	String fourthIndex = request.getParameter("index_fourth");
	String fifthIndex = request.getParameter("index_fifth");
	String xfirstIndex = request.getParameter("xindex_first");
	String xsecondIndex = request.getParameter("xindex_second");
	String xthirdIndex = request.getParameter("xindex_third");
	String xfourthIndex = request.getParameter("xindex_fourth");
	String xfifthIndex = request.getParameter("xindex_fifth");
	StatisticAnalyzeDataServices.getInstance().SubjectSave2OracleTable(subjectid, subjectname, charttype, DataID, firstIndex, secondIndex, thirdIndex, fourthIndex, fifthIndex, xfirstIndex, xsecondIndex, xthirdIndex, xfourthIndex, xfifthIndex);
 %>