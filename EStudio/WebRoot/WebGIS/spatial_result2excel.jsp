<%@page import="java.io.File"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="com.estudio.officeservice.ExcelUtils"%>
<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.estudio.utils.JSONUtils"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page import="net.minidev.json.JSONArray"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.nio.charset.Charset"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
    String jsFile = request.getParameter("js");
    jsFile = RuntimeContext.getAppTempDir() + "/webgis_spatial_temp/" + jsFile;
    String jsContent = FileUtils.readFileToString(new File(jsFile), Charset.forName("utf-8"));
    String spatialAnalyCondition = StringUtils.substringBetween(jsContent, "var spatialAnalyCondition = ", ";");
    String spatialAnalyResult = StringUtils.substringBetween(jsContent, "var spatialAnalyResult = ", ";");
    JSONObject conditionJson = JSONUtils.parserJSONObject(spatialAnalyCondition);
    JSONObject resultJson = JSONUtils.parserJSONObject(spatialAnalyResult);

    List<String> fieldList = new ArrayList<String>();
    Map<String, String> fieldName2Label = new HashMap<String, String>();

    if ("districtAnaly".equals(conditionJson.getString("o"))) {
        fieldList.add("CITY_NAME");
        fieldName2Label.put("CITY_NAME", "行政区域");
    }

    JSONArray groupFields = conditionJson.getJSONArray("groupFields");
    JSONArray groupFieldLabels = conditionJson.getJSONArray("groupFieldLables");
    for (int i = 0; i < groupFields.size(); i++) {
        String fieldName = groupFields.getString(i);
        String fieldLabel = groupFieldLabels.getString(i);
        fieldList.add(fieldName);
        fieldName2Label.put(fieldName, fieldLabel);
    }
    int layerType = conditionJson.getInt("layerType");
    JSONArray functionList = conditionJson.getJSONArray("statisticFunctions");
    for (int i = 0; i < functionList.size(); i++) {
        String funName = functionList.getString(i);
        if ("max".equals(funName) || "min".equals(funName) || "sum".equals(funName)) {
    if (layerType == 0)
        continue;
    if ("max".equals(funName)) {
        fieldList.add("MAX_GEO_LENGTH");
        fieldName2Label.put("MAX_GEO_LENGTH", "最大长度");
        if (layerType == 2) {
            fieldList.add("MAX_GEO_AREA");
            fieldName2Label.put("MAX_GEO_AREA", "最大面积");
        }
    } else if ("min".equals(funName)) {
        fieldList.add("MIN_GEO_LENGTH");
        fieldName2Label.put("MIN_GEO_LENGTH", "最小长度");
        if (layerType == 2) {
            fieldList.add("MIN_GEO_AREA");
            fieldName2Label.put("MIN_GEO_AREA", "最小面积");
        }
    } else if ("sum".equals(funName)) {
        fieldList.add("SUM_GEO_LENGTH");
        fieldName2Label.put("SUM_GEO_LENGTH", "总长度");
        if (layerType == 2) {
            fieldList.add("SUM_GEO_AREA");
            fieldName2Label.put("SUM_GEO_AREA", "总面积");
        }
    }
        } else if ("count".equals(funName)) {
    fieldList.add("COUNT");
    fieldName2Label.put("COUNT", "总数");
        } else {
    funName = StringUtils.substringAfter(funName, ") as ");
    fieldList.add(funName);
    fieldName2Label.put(funName, conditionJson.getJSONObject("fun2Label").getString(funName));
        }
    }

    JSONArray records = resultJson.getJSONArray("records");
    JSONArray cvsJSON = new JSONArray();

    JSONArray recordJson = new JSONArray();
    for (String str : fieldList)
        recordJson.add(fieldName2Label.get(str));
    cvsJSON.add(recordJson);

    for (int i = 0; i < records.size(); i++) {
        JSONObject record = records.getJSONObject(i);
        recordJson = new JSONArray();
        for (String str : fieldList) {
    recordJson.add(record.getString(str));
        }
        cvsJSON.add(recordJson);
    }

    String excelFileName = jsFile.substring(0, jsFile.length() - 3) + ".xls";
    ExcelUtils.getInstance().createExcelByCVSJSONArray(excelFileName, cvsJSON);

    jsFile = request.getParameter("js");
    response.sendRedirect("../temp/webgis_spatial_temp/" + jsFile.substring(0, jsFile.length() - 3) + ".xls");
%>