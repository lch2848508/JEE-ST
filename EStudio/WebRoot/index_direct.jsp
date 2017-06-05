<%@page import="com.estudio.context.RuntimeConfig"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%
    String u = request.getParameter("u");
	String p = request.getParameter("p");
	String map = request.getParameter("map");
	if (StringUtils.isEmpty(u))u = "";
	if (StringUtils.isEmpty(p))p = "";
%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
			<meta http-equiv="X-UA-Compatible" content="IE=8" />
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	 <%
	    if (RuntimeConfig.getInstance().isRelease()) {
	 %>
		<script type="text/javascript" src="js/release/jquery.min.js?version=<%=RuntimeConfig.getInstance().getVersion()%>"></script>
		<script type="text/javascript" src="js/jslib/utils.js"></script>
	 <%
	    } else {
	 %>
		<script type="text/javascript" src="js/jslib/jquery/jquery.js"></script>
		<script type="text/javascript" src="js/jslib/jquery/jquery.cookie.js"></script>
		<script type="text/javascript" src="js/jslib/jquery/jquery.json.js"></script>
		<script type="text/javascript" src="js/jslib/utils.js"></script>
	 <%
	    }
	 %>
	
		<script type="text/javascript" src="js/jslib/jquery/zebra_dialog/zebra_dialog.js"></script>   
		<script type="text/javascript">
			$(function(){
				$.post("servlet/login?o=login",{o:"login",u:"<%=u%>",p:"<%=p%>",skip_code:1}, ajaxSuccess);
			});
			
			// 提交表单成功返回结果函数
			function ajaxSuccess(responseText, statusText) {
				eval("var status=" + responseText + ";");
				if(status["r"]){
					if("1"==<%=map%>){
						if(status.is_gis_role)
			        		window.location.href = "WebGIS/index.jsp";
			        	else if(status.is_mis_role && confirm("你不能访问地图服务，是否进入业务系统?"))
			        		window.location.href = "flexclient/index.jsp";
			        	else
			        		alert("你暂时不能使用本系统，请与系统管理人员联系。");
		        	}else{
		        		if(status.is_mis_role) 
		        			window.location.href = "flexclient/index.jsp";
			        	else if(status.is_gis_role) 
			        		window.location.href = "WebGIS/index.jsp";
			        	else
			        		alert("你暂时不能使用本系统，请与系统管理人员联系。");
		        	}
				}else//登录系统失败
				{
					if(status.extMsg){
						alert(status.extMsg);
		          		window.close();
					}else{
						alert("登录系统失败!\n请输入正确的用户名、密码(区分大小写)及校验码");
						$("#imgcode")[0].src = "servlet/verifyimage?t=login&r=" + (new Date()).getTime();
		        		$('#c').val('');
					}
				}
			}
		</script>
	</head>
</html>