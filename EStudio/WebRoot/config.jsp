<%@page import="com.estudio.web.servlet.config.WebParamService"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="org.dom4j.Document"%>
<%@page import="org.dom4j.Element"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.estudio.utils.Convert"%>
<%
    Document dom = WebParamService.getInstance().getDOM();
    List<?> groupList = dom.getRootElement().elements("group");
    String queryString = request.getQueryString();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="zh">
	<head>
		<title>系统参数配置</title>
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<script type="text/javascript" src="jslib/jquery/jquery.single.min.js"></script>
		<script type="text/javascript">
        function save(){
			var data = {o:"save"};
			$("input:text,select").each(function(){data[this.id]=this.value;});
			$.post("config/webparams",data,function(text){alert("系统参数已经保存!");});
		}
		document.oncontextmenu = function(){return false;}

        </script>
		<link href="css/base.css" rel="stylesheet" type="text/css" />
		<style type="text/css">
#table_contain {
	border-collapse: collapse;
}

#table_contain td {
	padding: 4px;
}

#table_contain .category {
	background-color: #FFC;
	font-weight: bold;
}

#table_contain input,select {
	width: 100%;
	height: 22px;
	line-height: 20px;
	border: 1px dotted #CCCCCC;
}

.btn_common {
	border: 1px solid #7D93AD;
	width: 75px;
	height: 30px;
}
</style>
	</head>
	<body style="padding: 8px; border: none">
		<table border="0" cellspacing="0" width="100%" cellpadding="0">
			<tr>
				<td valign="top">
					<table width="100%" border="1" bordercolor="#7D93AD" cellspacing="0" cellpadding="0" id="table_contain">
						<%
						    for (int i = 0; i < groupList.size(); i++) {
						        Element ge = (Element) groupList.get(i);
						        List<?> il = ge.elements("item");
						%>
						<tr>
							<td colspan="3" class="category" height=28>
								<%=ge.attributeValue("name")%>
							</td>
						</tr>
						<%
						    for (int j = 0; j < il.size(); j++) {
						            Element ie = (Element) il.get(j);
						            String control = "";
						            String items = ie.attributeValue("items");
						            if (StringUtils.isEmpty(items)) {
						                control = "<input type=\"text\" id=\"" + ie.attributeValue("name") + "\" value=\"" + Convert.nvl(ie.attributeValue("value"), "") + "\" />";
						            } else {
						                control = "<select id= \"" + ie.attributeValue("name") + "\">";
						                String[] vs = items.split(";");
						                for (int m = 0; m < vs.length; m++) {
						                    control += "<option value=\"" + vs[m] + "\"";
						                    if (StringUtils.equals(vs[m], ie.attributeValue("value")))
						                        control += " selected=\"selected\"";
						                    control += (">" + vs[m] + "</option>");
						                }
						                control += "</select>";
						            }
						%>
						<tr height=28>
							<td style="padding: 8px;" width=120 nowrap="nowrap">
								<%=Convert.nvl(ie.attributeValue("name"), "")%>
							</td>
							<td width=180 nowrap="nowrap">
								<%=Convert.nvl(ie.attributeValue("title"), "")%>
							</td>
							<td>
								<%=control%>
							</td>
						</tr>
						<%
						    }
						    }
						%>
					</table>
				</td>
			</tr>
			<%
			    if (StringUtils.equals(queryString, "1C483F28FE1048B8BAA9BE9F41DD7BF2")) {
			%>
			<tr>
				<td height="40" align="right">
					<input name="" type="button" value="保存" onClick="save()" class="btn_common" />
				</td>
			</tr>
			<%
			    }
			%>
		</table>

	</body>
</html>
