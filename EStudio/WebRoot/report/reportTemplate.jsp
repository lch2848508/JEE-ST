<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="java.io.StringWriter"%>
<%@page import="org.apache.velocity.Template"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.velocity.VelocityContext"%>
<%@page import="org.apache.velocity.app.Velocity"%>
<%@page import="com.estudio.web.service.DataService4Report"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page
	import="com.estudio.define.webclient.report.ReportTemplateDefine"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.estudio.utils.HttpRequestUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.estudio.utils.Convert"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
    String reportID = request.getParameter("templateid");
    Connection con = null;
    try {
        con = RuntimeContext.getDbHelper().getConnection();
        ReportTemplateDefine reportDefine = RuntimeContext.getReportDefineService().getDefine(con, Convert.try2Int(reportID, 0));
        DataService4Report.getInstance().registerReportTemplateDefine(reportDefine);

        //初始化模板引擎
        Velocity.init();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("ReportService", DataService4Report.getInstance());
        velocityContext.put("StringUtils", new StringUtils());
        velocityContext.put("StringEscape", new StringEscapeUtils());
        Map<String, List> reportRecords = DataService4Report.getInstance().getRecords(con, reportDefine, HttpRequestUtils.getRequestParams(request));
        Iterator<Map.Entry<String, List>> iterator = reportRecords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List> entry = iterator.next();
            String datasetName = entry.getKey();
            List list = entry.getValue();
            velocityContext.put("RECORD_" + datasetName + "_S", list);
            if (list.size() != 0)
                velocityContext.put("RECORD_" + datasetName, list.get(0));
            else
                velocityContext.put("RECORD_" + datasetName, new HashMap<String, String>());
        }
        StringWriter writer = new StringWriter();
        Velocity.evaluate(velocityContext, writer, "velocity", reportDefine.getTemplate());
        String reportContent = writer.toString();
%>

<%=reportContent%>

<%
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        DataService4Report.getInstance().unregisterReportTemplateDefine(null);
        RuntimeContext.getDbHelper().closeConnection(con);
    }
%>