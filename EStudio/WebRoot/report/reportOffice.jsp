<%@page import="org.apache.commons.io.FilenameUtils"%>
<%@page import="java.net.URL"%>
<%@page import="org.apache.http.client.utils.URLEncodedUtils"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="com.estudio.impl.webclient.report.DBReportJSEnginer"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="java.io.StringWriter"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page	import="com.estudio.define.webclient.report.ReportTemplateDefine"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.estudio.utils.HttpRequestUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.estudio.utils.Convert"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    request.setCharacterEncoding("utf-8");
    response.setCharacterEncoding("utf-8");
    response.setContentType("text/html; charset=UTF-8");
    String urlFileName = "";
    String excelFileName = request.getParameter("reportFileName");
    if (StringUtils.isEmpty(excelFileName))
        excelFileName = "" + System.currentTimeMillis() + ".xls";
    else
        excelFileName = "" + System.currentTimeMillis() + "/" + excelFileName;
    if (!StringUtils.endsWithIgnoreCase(excelFileName, ".xls"))
        excelFileName = excelFileName + ".xls";

    String[] strReportTemplateIds = request.getParameter("templateid").split(",");
    long[] templateIds = new long[strReportTemplateIds.length];
    for (int i = 0; i < strReportTemplateIds.length; i++)
        templateIds[i] = Convert.str2Long(strReportTemplateIds[i]);
    DBReportJSEnginer jsEnginer = DBReportJSEnginer.create(templateIds, HttpRequestUtils.getRequestParams(request));
    try {
        String saveFileName = RuntimeContext.getAppTempDir() + "report/" + excelFileName;
        FileUtils.forceMkdir(new File(saveFileName).getParentFile());
        jsEnginer.execute(saveFileName);
    } finally {
        jsEnginer.disponse();
    }
    String filename = FilenameUtils.getName(excelFileName);

    String url = StringUtils.replace("../temp/report/" + excelFileName, filename, java.net.URLEncoder.encode(filename, "UTF-8"));
    response.sendRedirect(url);
%>
