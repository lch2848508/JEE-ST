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
<%@page	import="com.estudio.define.webclient.report.ReportTemplateDefine"%>
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

<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8">
<link rel="icon" href="print1.ico" mce_href="print1.ico" type="image/x-icon">
<link rel="shortcut icon" href="print1.ico" mce_href="print1.ico" type="image/x-icon">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title><%=StringEscapeUtils.escapeHtml3(reportDefine.getReportName())%></title>
<style type="text/css">
html {
	width: 100%;
	height: 100%
}

body {
	width: 100%;
	height: 100%
}

* {
	padding: 0px;
	margin: 0px;
	font-family: "微软雅黑", "宋体";
}

.btn {
	height: 40px;
	width: 100px;
	font-weight: bold;
	font-size: 12px;
}

.printContent {
	position: absolute;
	left: 0px;
	top: 0px;
	right: 0px;
	bottom: 50px;
}

.toolbar {
	position: absolute;
	left: 0px;
	height: 40px;
	right: 0px;
	bottom: 0px;
	background-color: #CCC;
	border-top-width: 1px;
	border-right-width: 1px;
	border-bottom-width: 1px;
	border-left-width: 1px;
	border-top-style: solid;
	border-top-color: #333;
	border-right-color: #333;
	border-bottom-color: #333;
	border-left-color: #333;
	padding: 5px;
}
</style>
<%
    if(RuntimeConfig.getInstance().isRelease()) {
%>
<script type="text/javascript"
	src="../js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<%
    } else {
%>
<script type="text/javascript" src="../js/jslib/jquery/jquery.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<script type="text/javascript" src="../js/jslib/jquery/jquery.json.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
<%
    }
%>

<script type="text/javascript" src="../js/jslib/jquery/zebra_dialog/zebra_dialog.js"></script>
<link rel="stylesheet" href="../js/jslib/jquery/zebra_dialog/css/default/zebra_dialog.css" type="text/css">

<style type="text/css">
.content {
	font-family: "微软雅黑", "宋体";
	font-size: 12px;
}
</style>

<script language="javascript">
    //打印报表
    function printReport() {
        if (!checkReportOCX())
            return;
        factory.printing.Print(true,"iframeReport");
    }

    //打印预览报表
    function previewReport() {
        if (!checkReportOCX())
            return;
        factory.printing.Preview("iframeReport");
    }

    //页面设置
    function setupReport() {
        if (!checkReportOCX())
            return;
        factory.printing.PageSetup();
    }

    //输出PDF文件
    function printToPDF() {
        if (!checkReportOCX())
            return;
        //factory.printing.PrintPDF({ url: "example.pdf" });
    }

    //检查打印控件
    function checkReportOCX() {
        if (factory && factory.object && secmgr && secmgr.object && secmgr.validLicense)
        	return true;
        else
           showHelp();
        return false;
    }
    
    //显示帮助系统
    var help = null;
    function showHelp(){
 				   winRegister = new $.Zebra_Dialog('', {
 				      source: {'iframe': {
 				         'src':  "../document/print_help.html",
 				         'height': 480
 				     	}},
    			        width: 640,
    			        type:false,
    			        buttons:false,
    			        title:  '打印帮助'
    			    });
    }
 
    $(function() {
        var ifrdoc = $("#iframeReport")[0].contentWindow.document;
        var htmlContent = "<%=StringEscapeUtils.escapeEcmaScript(reportContent)%>";
		ifrdoc.designMode = "on"; // 文档进入可编辑模式
		ifrdoc.open(); // 打开流
		ifrdoc.write(htmlContent);
		ifrdoc.close(); // 关闭流
		ifrdoc.designMode = "off"; // 文档进入非可编辑模式

		if (!checkReportOCX())
			return;

		var pageInfo = <%=reportDefine.getPageInfo()%>;
		factory.printing.SetMarginMeasure(1); //页边距设置为毫米
		if (pageInfo) {
			factory.printing.paperSize = pageInfo.PageName;
			factory.printing.portrait = !pageInfo.Dir;
			factory.printing.leftMargin = pageInfo.ML;
			factory.printing.topMargin = pageInfo.MT;
			factory.printing.rightMargin = pageInfo.MR;
			factory.printing.bottomMargin = pageInfo.MB;
			factory.printing.header = pageInfo.Header;
			factory.printing.footer = pageInfo.Footer;
		}
	});
</script>
</head>
<body style="margin: 0px;" width="100%" height="100%" scroll="no" class="content">
	<div class="printContent">
		<iframe width="100%" height="100%" frameborder="0" id="iframeReport"></iframe>
	</div>
	<div class="toolbar">
		<div style="float:left">
			<input name="" type="button" value="打印帮助" onclick="showHelp()"
				class="btn" />
		</div>
		<div style="float:right">
			<input name="" type="button" value="页面设置" onclick="setupReport()"
				class="btn" /> <input name="" type="button" value="直接打印"
				class="btn" onclick="printReport()" class="btn" /> <input name=""
				type="button" value="打印预览" onclick="previewReport()" class="btn" />
		</div>
	</div>

	<!-- 打印控件 -->
	<div style="display:none">
		<!-- MeadCo 安全管理控件 -->
		<object id="secmgr"	codebase="../download/smsx6.cab#Version=6,5,439,72"	classid="clsid:5445BE81-B796-11D2-B931-002018654E2E" viewastext>
			<param name="GUID" value="{ED36A290-070E-4496-8C45-E4B8696531EC}">
			<param name="PATH" value="../download/sxlic.jpg">
			<param name="REVISION" value="0">
			<param name="PerUser" value="true">
		</object>
		<object id="factory" classid="clsid:1663ED61-23EB-11D2-B92F-008048FDD814" viewastext></object>
	</div>
</body>
</html>

<%
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        DataService4Report.getInstance().unregisterReportTemplateDefine(null);
        RuntimeContext.getDbHelper().closeConnection(con);
    }
%>