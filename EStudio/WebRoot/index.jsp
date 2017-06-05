<%@page import="com.estudio.context.RuntimeConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.estudio.web.servlet.config.WebParamService"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%
    String u = request.getParameter("u");
	String p = request.getParameter("p");
	if (StringUtils.isEmpty(u))u = "";
	if (StringUtils.isEmpty(p))p = "";
	boolean isSkipCode = (!StringUtils.isEmpty(u) && !StringUtils.isEmpty(p));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=8" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%></title>
<style type="text/css">
html {
	width: 100%;
	height: 100%;
	margin: 0px;
	padding: 0px;
	overflow: hidden;
}

body {
	width: 100%;
	height: 100%;
	margin: 0px;
	padding: 0px;
	background-image: url(./images/login_bg.jpg);
	background-repeat: no-repeat;
	background-position: center center;
	overflow: hidden;
	
}

* {
	margin: 0px;
	padding: 0px;
	font-family: "微软雅黑", "新宋体";
	*zoom: 1;
}

.mainTable {
	width: 960px;
	border-collapse: collapse;
	table-layout: fixed;
}

#logoInfo {
	background-image: url(./images/logo.png);
	background-repeat: no-repeat;
	height: 70px;
	width: 70px;
	position: relative;
	left: 20px;
	top: 20px;
	background-position: center center;
	border: thick solid #900;
}

#logoCaption {
	position: relative;
	left: 20px;
	top: 20px;
	background-position: center center;
	text-align: left;
	font-family: "微软雅黑", "新宋体";
	font-size: 22pt;
	font-weight: bold;
	color: #C06;
	width: 800px;
	height: 70px;
	padding-top: 20px;
}

#tableLogin{
    
}

.divUserName {
	padding-top: 37px;
}

.divPassword {
	padding-top: 10px;
}

.divCode {
	padding-top: 6px;
}

.divCodeImage {
	padding-top: 8px;
	padding-left: 5px;
}

input {
	font-size: 16px;
	font-family: "微软雅黑", "新宋体";
}

.STYLE1 {
	color: #FFFF00;
	font-weight: bold;
	font-size: 12px;
}

.divMain {
	width: 1020px;
	height: 500px;
	position: relative;
    background-image: url(./images/login_form.png);
    background-repeat: no-repeat;
    background-position: center center;
    border: 0px none #FFF;
}

.W100 {
	border: 0px none #FFF;
}

#divZoomStatus {
	position: absolute;
	left: 0px;
	right: 0px;
	bottom: 0px;
	z-index: 1000;
	background-color: #00F;
	text-align: center;
	padding: 4px;
	font-family: "微软雅黑", "新宋体";
	font-size: 12px;
	font-weight: bold;
	color: #FF3;
	display: none;
}
</style>
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
<link rel="stylesheet" href="js/jslib/jquery/zebra_dialog/css/default/zebra_dialog.css" type="text/css">

<script type="text/javascript">	
               var flashMinVersion = 18;
				function checkPlayPlayer()
				{
				    var f = flashChecker();
				    if (!f.f || f.v<flashMinVersion) {
				        if (isIE())
				            $("#flashWaringDiv").html("您的Flash插件不能满足系统要求，请<a href='./download/flashplayer_activex_21.exe'>点击下载</a>并安装。");
				        else
				           $("#flashWaringDiv").html("您的Flash插件不能满足系统要求，请<a href='./download/install_flash_player_21_plugin.exe'>点击下载</a>并安装。");
				    }
				}
				
				function checkBrowserScale()
				{
				    if(isBrowseScale()){
						$("#divZoomStatus").show();
						setTimeout(function(){$("#divZoomStatus").hide();},15000);
					}
				}
				
				function initWindow()
				{
					$("#chk_autologin")[0].checked=false;
				   <%if (!isSkipCode) {%>
				    var cookie = $.cookie("loginname");
				    if (cookie)
				    {
				        $("#u")[0].value = cookie;
				    }
				    var storage=window.localStorage;
					if(storage.username)
					{
						 $("#u")[0].value = storage.username;
						 $("#p")[0].value = storage.password;
						 $("#chk_gotomap")[0].checked=true;
						 $("#chk_autologin")[0].checked=true;
						
					}
				    <%}%>
				    if(storage.username)
					{
						 submitFormBtnClick();
					}
					else
					{
						 if($.cookie("chk_gotomap") && $("#chk_gotomap")[0])
				    	$("#chk_gotomap")[0].checked = $.cookie("chk_gotomap")=="true";
				    
					    $("#btn_login").click(submitFormBtnClick);
					    <% if(RuntimeConfig.getInstance().isEnabledUserRegister()){ %>
					    $("#btn_register").click(openRegisterDialog);
				    <% } %>
					}
				   
				    
				    
				}


				// 提交表单成功返回结果函数
				function ajaxSuccess(responseText, statusText) {
				    eval("var status=" + responseText + ";");
				    if (status["r"]) // 成功登陆系统
				    {
				    	 if($("#chk_autologin")[0].checked)
				        {
				        	if(!window.localStorage){
					            alert("浏览器支持不支持自动登录！");
					        }else{
				        	var storage = window.localStorage; 
						    storage["username"]=$("#u")[0].value; 
						    storage["password"]=$("#p")[0].value; 
						    }
				        }
				        $.cookie("loginname", $("#u")[0].value, {
				            expires : 7,
				            path : '/'
				        });
				        
				        <%if (RuntimeConfig.getInstance().isWEBGISEnabled()) {%>
				        	$.cookie("chk_gotomap", $("#chk_gotomap")[0].checked?"true":"false", {
				            	expires : 7,
				            	path : '/'
				        	});
				        
				        if($("#chk_gotomap")[0].checked)
				        {
				        	var val=window.location.href;
				       		var val1=val.split("?");
				        	if(status.is_gis_role){
				        		if(val1.length==1)
				        		{
				        			window.location.href = "WebGIS/index.jsp";
				        		}
				        		else 
				        			window.location.href = "WebGIS/index.jsp?"+val1[1];
				        	}
				        	else if(status.is_mis_role && confirm("你不能访问地图服务，是否进入业务系统?"))
				        		window.location.href = "flexclient/index.jsp";
				        	else
				        		alert("你暂时不能使用本系统，请与系统管理人员联系。");
				        }
				        else <%}%>
				       	{
				        	if(status.is_mis_role) 
				        		window.location.href = "flexclient/index.jsp";
				        	else if(status.is_gis_role) 
				        		window.location.href = "WebGIS/index.jsp";
				        	else
				        		alert("你暂时不能使用本系统，请与系统管理人员联系。");
				        }
				    } else // 登录系统失败
				    {
				        if (status.extMsg) {
				            alert(status.extMsg);
				            window.close();
				        } else {
				        	alert("登录系统失败!\n请输入正确的用户名、密码(区分大小写)及校验码");
				        	$("#imgcode")[0].src = "servlet/verifyimage?t=login&r=" + (new Date()).getTime();
				        	$('#c').val('');
				        }
				    }
				}

				// ----------------------------------------------------------------------------------------
				// 设为首页
				

				function softlib() {
				    window.open("download/filelist.html");
				}
    			var VAR_DOCUMENT_TITLE = "<%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%>";
    		
    			$(function(){
    			    initWindow();
    			    checkPlayPlayer();
    			    checkBrowserScale();
    			});
    			
    			function submitFormBtnClick()
    			{
    				var u = $("#u").val();
    				var p = $("#p").val();
    				var c = $("#c").val();
    				var storage=window.localStorage;
    				var autoLogin=storage.username?"true":"false";
					if(autoLogin=="true")
    				{
    					<% isSkipCode=true;%>
    				} 
    				if(StringUtils.isEmpty(u)||StringUtils.isEmpty(p))
    				{
    					return alert("用户名及密码不能为空，请重新输入后继续。");
    				}
    				
    				<%if (!isSkipCode) {%>
    				if(StringUtils.isEmpty(c))
    				{
    					return alert("验证码不能为空，请重新输入后继续。");
    				}
    				<%}%>
    				$.post("servlet/login?o=login",{o:"login",autoLogin:autoLogin,u:u,p:p,c:c,skip_code:<%=isSkipCode ? 1 : 0%>}, ajaxSuccess);
				}
    			
 <% if(RuntimeConfig.getInstance().isEnabledUserRegister() || RuntimeConfig.getInstance().isEnableForgetPassword()) {%>
    			var winRegister = null;
 				function openRegisterDialog()
    			{
 				   winRegister = new $.Zebra_Dialog('', {
 				      source: {'iframe': {
 				         'src':  'register.jsp',
 				         'height': 350
 				     	}},
    			        'buttons':  [
    			                     {caption: '注册', callback: event4RegisterButton},
    			            		 {caption: '关闭', callback: event4CloseRegisterButton}
    			                    ],
    			        width: 980,
    			        type:false,
    			        title:  '<%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%>-新用户注册'
    			    });
    			}
    			    
    			function event4RegisterButton()
    			{
    			    var iframe = $("iframe")[0];
    			    var win = iframe.contentWindow;
    			    var params = win.getParams();
    			    if(params==null)
    			    {
    			        alert("输入内容不完整，不能注册。");
    			        return false;
    			    }
    			    var result = false;
    			    $.post("servlet/login?o=regisger",params,function(text){
    			        var json = $.evalJSON(text);
    			        if(json.r) {
    			            result = true;
    			            alert("您已经成功注册，我们将以短信的形式将您的密码发送给您。");
    			        } else {
    			            alert(json.msg);
    			        }
    			    });
    			    return result;
    			}
    			
    			function event4CloseRegisterButton()
    			{
    			    winRegister.close();
    			}
    			
<% } %>

			 function softLibList()
    			{
 				   winRegister = new $.Zebra_Dialog('', {
 				      source: {'iframe': {
 				         'src':  'download/filelist.html#flash',
 				         'height': 480
 				     	}},
    			        width: 1024,
    			        type:false,
    			        buttons:false,
    			        title:  '运行环境支持'
    			    });
    			}
    			
</script>
</head>
<body scroll="no">
<table id="tableLogin" width="100%" height="100%" border="0" bordercolor="#990000" cellpadding="0" cellspacing="0">
<tr>
<td valign="middle" align="center">

<div class="divMain">
<img src="images/<%=WebParamService.getInstance().getParamValue(WebParamService.LOGO_URL, "logo.png")%>" style="position:absolute; left:40px; top:45px" />
<!-- 
     	<div style="position:absolute; left:70px;font-size:28px; top:23px;font-weight:bold;color:#CA0001;"><%=WebParamService.getInstance().getParamValue(WebParamService.APP_NAME)%></div>
-->
<input name="u" type="text" class="W100" style="position:absolute; left:680px;top:184px;width: 230px" id="u" value="<%=u%>" > 
<input name="p" type="password" class="W100" style="position:absolute; left:680px;top:242px;width: 230px;" id="p" value="<%=p%>" /> 
<input name="c" type="text" class="W100" id="c" style="position:absolute; left:680px;top:302px; width: 100px;" /> 
<img name="imgcode" id="imgcode" src="servlet/verifyimage?t=login" style="position:absolute; left:800px;top:305px;" /> 
<input id="btn_login" name="btn_login" type="image" src="images/btn_ok.png" alt="登录" width="160" height="45" border="0" style="position:absolute; right:93px;top:355px;" />

<%
    if (RuntimeConfig.getInstance().isWEBGISEnabled()) {
%>
<input style="position:absolute; left:560px;top:344px;" checked="true" name="chk_gotomap" id="chk_gotomap" type="checkbox" value="" /> <span style="position:absolute; left:580px;top:340px;color: #933;font-weight:bold">直接进入地图模式</span>
<input style="position:absolute; left:560px;top:374px;" checked="false" name="chk_autologin" id="chk_autologin" type="checkbox" value="" /> <span style="position:absolute; left:580px;top:370px;color: #933;font-weight:bold">自动登录</span>
<%
    }
%>
<div id="flashWaringDiv" style="position:absolute;top:420px;right:87px; font-size:14px; color:#F00; font-weight:bold"></div>

</div></td>
</tr>
</table>
<div style="position:absolute; z-index:1; right:4px; top:4px;">
<table class="STYLE1">
<tr>
<td><img src="images/favorites.gif">
</td>
<td style="cursor:pointer" onclick="favorite_home()">收藏</td>
<td></td>
<td><img src="images/softlib.png">
</td>
<td style="cursor:pointer" onclick="softLibList()">下载软件</td>
<tr>
</table>
</div>
<div id="divZoomStatus">
您的浏览器网页显示比例不是100%,您可以通过组合键 <font color="#FF0000">Ctrl+0</font> 使其恢复正常，这样可以提高本系统的显示效果！
</div>
</body>
</html>
