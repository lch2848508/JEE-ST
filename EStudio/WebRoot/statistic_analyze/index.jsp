<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="com.estudio.context.RuntimeConfig"%>
<%@page import="com.estudio.web.servlet.config.WebParamService"%>
<%@page import="com.estudio.intf.webclient.form.IPortal4ClientService"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="com.estudio.define.sercure.ClientLoginInfo"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%
    ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(session);

    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" />
<title>广东省交通运输规划</title>

<!-- JQuery的js文件的导入 -->
<script src="JQuery/jquery.min.js"></script>
<script src="JQuery/browser.js"></script> 
<script src="JQuery/jquery.autocomplete.js"></script>
<link rel="stylesheet" type="text/css" href="JQuery/jquery.autocomplete.css" />
<!-- dhtmlx的js和css等文件 -->
<link rel="stylesheet" type="text/css" href="dhtmlxbase/dhtmlx.css" />
<script src="dhtmlxbase/dhtmlx.js"></script>
<!-- easyUI的js和css等文件 -->
<link rel="stylesheet" type="text/css"
	href="easyUIbase/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyUIbase/themes/icon.css">
<script src="easyUIbase/jquery.easyui.min.js"></script>
<script src="easyUIbase/easyloader.js"></script>
<script src="easyUIbase/easyui-lang-zh_CN.js"></script>

<!-- 数据处理及窗体处理事件 -->
<script src="js/OnloadWindow.js"></script>
<script src="js/PublicFunctions.js"></script>
<script src="js/LayoutEvent.js"></script>
<script src="js/ItemEvent.js"></script>
<script src="js/TableProperty.js"></script>
<script src="js/HeaderDataStructure.js"></script>
<script src="js/RecordsDataStructure.js"></script>
<script src="js/IndexPadding.js"></script>
<script src="js/IndexStructure.js"></script>

<!-- Echarts3.0+图表绘制 -->
<script src="Echartsbase/js/echarts.js"></script>
<script src="Echartsbase/DrawCharts.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/ChartsPublicFunction.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/PieDraw.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/BarDraw.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/LinesDraw.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/StaticMapDraw.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/DynamicMapDraw.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/ODMapDraw.js"></script>
<script src="Echartsbase/js_ChartsDataStructure/WeatherDraw.js"></script>

<!-- Echarts2.0的js文件 (OD专题使用)-->
<script src="Echartsbase/Echart2.2.7/echarts/echarts.js"></script>
<script src="Echartsbase/Echart2.2.7/zrender/tool/color.js"></script>

<style>
html,body {
	width: 100%;
	height: 100%;
	margin: 0px;
	padding: 0px;
	overflow: hidden;
	font-family: "微软雅黑";
}

.my_hdr {
	background-color: #A9FACD;
	height: 50px;
	/* background:url(images/logo_bg.gif) repeat-x; */
	background: url(images/logo_bg_common.png) repeat-x;
	margin-top: -2px;
	border-bottom: solid 1px #A4BED4;
}

.my_hdr #logo {
	height: 45px;
	width: 60%;
	float: left;
}

#logo img {
	height: 50px;
}
/* .my_hdr #Time {
			margin-top: -20px;
			font-size: 12px;
			color: RGBA(180, 21, 55, 0.95);
			font-weight:bold;
			float: left;
		} */
#topToolbar {
	float: right;
	/* padding-top:10px; */
	/* padding-top: 10px; */
}

#topToolbar table {
	border-collapse: collapse;
	width: 100%;
	height: 50px;
	font-size: 12px;
}

#topToolbar td {
	border: solid 1px #A4BED4;
	border-bottom: solid 0px #A4BED4;
	/* padding: 0.3em 1em; */
	padding: 0px 5px;
	background-color: #E6EDF1;
	/* border-right: solid 1px #A4BED4; */
	cursor: pointer;
}

#topToolbar td:hover {
	background-color: rgba(255, 255, 255, 0.5);
	background-repeat: no-repeat;
	background-position: 100px 15px;
}

#topToolbar a {
	text-decoration: none;
	display: block;
	color: #000;
}

.my_ftr {
	background-color: #A9FACD;
	height: 21px;
	padding: 5px;
	text-align: center;
	font-size: 12px;
}

#fm {
	margin: 0;
	padding: 5px 10px;
}

.ftitle {
	font-size: 14px;
	font-weight: bold;
	padding: 5px 0;
	margin-bottom: 10px;
	border-bottom: 1px solid #ccc;
}

.fitem {
	margin-bottom: 5px;
}

.fitem label {
	display: inline-block;
	width: 80px;
}

.fitem input {
	width: 156px;
}

.dhx_header_cmenu { /* background-color:#E7F1FF; */
	background-color: #E7F1FF;
	/* border:2px outset silver; */
	border: 1px outset silver;
	border-color: #0000ff;
	z-index: 2;
}

.dhx_header_cmenu_item {
	white-space: nowrap;
	font-size: 12px;
}
</style>
</head>

<body onload="doOnLoad();">
	<!-- 专题列表面板 -->
	<!-- <div id="SubjectListBox" style="position:absolute;top:60%;width:100%;height:40%"></div> -->
	<!-- 左侧目录树面板 -->
	<!-- <div id="treeBox" style="width:100%;height:100%"></div> -->
	<!-- <script type="text/javascript">
			//右侧信息的隐藏与显示
			$('#pagination_rightPanel').BootSideMenu({side:"right", autoClose:false});
		</script> -->
	<!-- 页面页眉和页脚面板 -->
	<div id="my_logo" class="my_hdr">
		<div id="logo">
			<img src="images/logo_gzdcj.png" />
		</div>
		<div id="topToolbar">
			<table>
				<tr style="height:20px">
					<td colspan="2"
						style="border: solid 0px #A4BED4;background-color: rgba(255,255,255,0);color: rgba(180, 21, 55, 0.95);font-weight: bold;font-size: 12px;">
						<span id="Time"></span></td>
						<!--  
					<td
						style="border: solid 0px #A4BED4;background-color: rgba(255,255,255,0);">
						<a class="changePassword" href="javascript:changePassword()"
						style="color: rgb(236, 248, 46);font-weight: bold;font-size: 13px;">更改密码</a>
					</td>
					<td
						style="border: solid 0px #A4BED4;background-color: rgba(255,255,255,0);">
						<a class="exitSystem" href="javascript:exitSystem()"
						style="color: rgb(236, 248, 46);font-weight: bold;font-size: 13px;">退出系统</a>
					</td>-->
				</tr>
				<tr>
	   				<!-- <td>
	   					<a href="#">&nbsp首页&nbsp</a>
	   				</td> -->
	   				<td>
	   					<a href="../flexclient/index.jsp">&nbsp台帐管理&nbsp</a>
	   				</td>
	   				<td>
	   					<a href="../WebGIS/index.jsp">&nbsp交通一张图&nbsp</a>
	   				</td>
	   				<td style="font-weight:bold">
	   					<a href="index.jsp">&nbsp数据分析&nbsp</a>
	   				</td>
	   				<!-- <td>
	   					<a href="#">&nbsp实地核查&nbsp</a>
	   				</td> -->
	   				<!--<td>
	   					<a href="#">&nbsp综合评价&nbsp</a>
	   				</td>-->
	   			</tr>
			</table>
		</div>
	</div>
	<div id="my_copy" class="my_ftr">
		<span> &copy; 2016-2017 技术支持：</span> <span> <a
			style="text-decoration:none;" href="http://www.chinadci.com"
			target="_blank">广州城市信息研究所有限公司</a> </span>
	</div>
	<!-- 图表类型选择Dialog面板 -->
	<div id="Chart-form" style="display: none;">
		<div
			style="background-color:#EBEBEB;height:20px;font-size:13px;padding:2px;">&nbsp;柱状图&折线图</div>
		<img id="AreatusBar" title="簇状柱状图" alt="簇状柱状图"
			src="images/AreatusBar.png" style="padding:5px;cursor:pointer">
		<!-- <img id="StackBar" title="堆积柱状图" alt="堆积柱状图" src="images/StackBar.png" style="padding:5px;cursor:pointer"> -->
		<img id="Lines" title="折线图" alt="折线图" src="images/lines.png"
			style="padding:5px;cursor:pointer">
		<!-- <img id="CrossBar" title="二维条状图" alt="二维条状图" src="images/CrossBar.png" style="padding:5px;cursor:pointer">
		<img id="3DBar" title="三维柱状图" alt="三维柱状图" src="images/3DBar.png" style="padding:5px;cursor:pointer"> -->

		<div
			style="background-color:#EBEBEB;height:20px;font-size:13px;padding:2px;">&nbsp;饼状图</div>
		<img id="2DPie" title="二维饼状图" alt="二维饼状图" src="images/2DPie.png"
			style="padding:5px;cursor:pointer"> <img id="RingPie"
			title="圆环饼状图" alt="圆环饼状图" src="images/RingPie.png"
			style="padding:5px;cursor:pointer">

		<div
			style="background-color:#EBEBEB;height:20px;font-size:13px;padding:2px;">&nbsp;地
			图</div>
		<img id="StaticMap" title="静态地图" alt="静态地图" src="images/StaticMap.png"
			style="padding:5px;cursor:pointer"> <img id="DynamicMap"
			title="动态地图" alt="动态地图" src="images/DynamicMap.gif"
			style="padding:5px;cursor:pointer">
	</div>
	<!-- 指标参数选择Dialog面板 -->
	<div id="Param-form" style="display: none;">
		<form id="fm">
			<div id="baseParam" class="ftitle">基本参数</div>
			<div id="subject-Name" class="fitem">
				<label>专题名称</label> <input id="subjectName" class="easyui-textbox"
					style="width:250px">
			</div>
			<div id="index-Param" class="ftitle">指标参数</div>
			<p>列参数设置
			<p />
			<div id="indexParam"></div>
			<!-- lc -->
			<br>
			<hr size=1 color=red width=90%>
			<br>
			<div id="xindexParam"></div>
			<div id="tips">
				<p>提示:</p>
				<ul>
					<li>年份指标请务必选择</li>
					<li>请减少相邻指标多选多（n*n）</li>
					<li>地图展示请注意使用合适的表格</li>
				</ul>
			</div>
		</form>
	</div>

	<!-- 图表展现面板 -->
	<div id="Echart"
		style="width:100%;height:100%;overflow:hidden;display:none;">
		<div id="EchartBox" style="width:100%;height:100%;overflow:hidden;"></div>
	</div>

	<script type="text/javascript">
		//window.setInterval('showTime()',1000);
		window.setInterval('showTime()', 1);
		function showTime() {
			var Today = new Date();
			var year = Today.getFullYear();
			var month = Today.getMonth();
			var date = Today.getDate();
			/* var h=Today.getHours(); 
			var m=Today.getMinutes(); 
			var s=Today.getSeconds();
			m=checkTime(m); 
			s=checkTime(s); */
			var day;
			if (Today.getDay() == 0)
				day = "星期日";
			if (Today.getDay() == 1)
				day = "星期一";
			if (Today.getDay() == 2)
				day = "星期二";
			if (Today.getDay() == 3)
				day = "星期三";
			if (Today.getDay() == 4)
				day = "星期四";
			if (Today.getDay() == 5)
				day = "星期五";
			if (Today.getDay() == 6)
				day = "星期六";
			document.getElementById("Time").innerHTML = "今天是：" + year + "-"
					+ (month + 1) + "-" + date + "  " + day;
			//document.getElementById("Time").innerHTML="今天是："+year+"-"+(month+1)+"-"+date+"  "+day+"  "+h+":"+m+":"+s;
		}
		/* function checkTime(i){ 
			if (i<10)
			{i="0" + i;} 
			return i; 
		}  */

		/* $("#topToolbar div").click(function(){
		    var Div_ID = $(this).attr("id");
			alert(Div_ID);
			//alert(DateUtils.getTodayStr());
		});  */

		//退出系统
		function exitSystem() {
			alert("按钮功能待开发");
		}

		//更改密码
		function changePassword() {
			alert("按钮功能待开发");
		}
		
	</script>
</body>
</html>