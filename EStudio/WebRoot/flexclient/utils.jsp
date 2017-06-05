<%@page import="com.estudio.web.servlet.config.WebParamService"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="com.estudio.define.sercure.ClientLoginInfo"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>

<%
    ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(session);
    try {
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<title><%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%></title>
		<script type="text/javascript" src="../jslib/jquery/jquery.js"></script>
		<script language="javascript" type="text/javascript">
		var VAR_DOCUMENT_TITLE = "<%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%>";
		
		$(function(){
			favorite_home();
			//CloseWin();
		})
		
		function CloseWin(){ 
			window.opener=null; 
			window.open('','_self'); 
			window.close(); 
			} 

		
		function favorite_home() {
			var str = window.location.href;
			str = str.substr(0, str.lastIndexOf("/"));
			str = str.substr(0, str.lastIndexOf("/"));
			str = str + "/index.jsp";
			SetHome(str, VAR_DOCUMENT_TITLE);
		}
		
		function AddFavorite(sURL, sTitle) {
			try {
				window.external.addFavorite(sURL, sTitle);
			} catch (e) {
				try {
					window.sidebar.addPanel(sTitle, sURL, "");
				} catch (e) {
					alert("加入收藏失败，请使用Ctrl+D进行添加");
				}
			}
		}
		function SetHome(obj, vrl) {
			try {
				obj.style.behavior = 'url(#default#homepage)';
				obj.setHomePage(vrl);
			} catch (e) {
				if (window.netscape) {
					try {
						netscape.security.PrivilegeManager
								.enablePrivilege("UniversalXPConnect");
					} catch (e) {
						alert("此操作被浏览器拒绝！\n请在浏览器地址栏输入“about:config”并回车\n然后将 [signed.applets.codebase_principal_support]的值设置为'true',双击即可。");
					}
					var prefs = Components.classes['@mozilla.org/preferences-service;1']
							.getService(Components.interfaces.nsIPrefBranch);
					prefs.setCharPref('browser.startup.homepage', vrl);
				}
			}
		}
		
        </script>

	</head>
	<body scroll="no">
		
	</body>
</html>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }
%>