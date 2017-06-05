<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>广东省综合运输体系规划运输平台</title>
	
	<link rel="stylesheet" type="text/css" href="dhtmlxbase/dhtmlx.css"/>
	<script src="dhtmlxbase/dhtmlx.js"></script>
	
	<script src = "js/jquery/jquery-1.11.3.js"></script>	
	<link rel="stylesheet" type="text/css" href="js/jquery/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="js/jquery/themes/icon.css">
	<script type="text/javascript" src="js/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="js/jquery/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="js/jquery/easyloader.js"></script>
	<script type="text/javascript" src="js/jquery/easyui-lang-zh_CN.js"></script>
	
	
	<script src="js/OnloadWindow.js"></script>
	<script src="js/HeaderDataBuilding.js"></script>
	<script src="js/RecordsDataBuilding.js"></script>
	<script src="js/IndexControlsBuilding.js"></script>
	<script src="js/EchartsToolBar.js"></script>
	<script src="js/PublicFunctions.js"></script>
	<!-- <script src="js/CollectFilePanel.js"></script> -->
	
	<script src="Echarts/javascript/echarts/echarts.js"></script>
	<script type="text/javascript" src="js/Echarts/MappingChart.js"></script>
	<script type="text/javascript" src="js/Echarts/PublicFunction.js"></script>
	<script type="text/javascript" src="js/Echarts/ChartDOM.js"></script>
	<script type="text/javascript" src="js/Echarts/MapData.js"></script>
	<script type="text/javascript" src="js/Echarts/PieData.js"></script>
	<script type="text/javascript" src="js/Echarts/BarData.js"></script>
	<script type="text/javascript" src="js/Echarts/LineData.js"></script>
	<script type="text/javascript" src="js/Echarts/MapWithTimeOptionData.js"></script>

	<link rel="stylesheet" type="text/css" href="js/bootstrap/css/BootSideMenu_silver.css">
	<link rel="stylesheet" type="text/css" href="js/bootstrap/css/bootstrap_silver.css">
	<script type="text/javascript" src="js/bootstrap/js/BootSideMenu.js"></script>
	
	<style>
		html, body {
			width: 100%;
			height: 100%;
			margin: 0px;
			padding: 0px;
			overflow: hidden;
		}
		.my_hdr {
			background-color: #A9FACD;
			height:50px;
			background:url(images/logo_bg.png) repeat-x;
		}
		.my_ftr {
			background-color: #A9FACD;
			height:21px;
			padding:5px;
			text-align:right;
			font-size:12px;
		}
		 #fm{
            margin:0;
            padding:5px 10px;
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;
        }
        .fitem label{
            display:inline-block;
            width:80px;
        }
        .fitem input{
            width:156px;
        }
        
        .dhx_header_cmenu{
			/* background-color:#E7F1FF; */
			background-color:#E7F1FF;
			/* border:2px outset silver; */
			border:1px outset silver;
			border-color: #0000ff;
			z-index:2;
		}
		.dhx_header_cmenu_item{
			white-space:nowrap;
			font-size:12px;
		}
	</style>
	
</head>

<body onload="doOnLoad();">
	<!-- 专题列表面板 -->
	<!-- <div id="SubjectListBox" style="position:absolute;top:60%;width:100%;height:40%"></div> -->
	<!-- 左侧目录树面板 -->
	<!-- <div id="treeBox" style="width:100%;height:100%"></div> -->
	<!-- 图表展现面板 -->
	<div id="Echart" style="width:100%;height:100%;overflow:hidden;">
		<div id="EchartBox" style="width:95%;height:100%;overflow:hidden;"></div>
		<!--右边栏-->
		<div id="pagination_rightPanel" class="pagination_rightPanel" style="width:200px">
			<div id="SubjectListBox" style="width:200px;height:100%"></div>
		</div>
	</div>
	<script type="text/javascript">
			//右侧信息的隐藏与显示
			$('#pagination_rightPanel').BootSideMenu({side:"right", autoClose:false});
		</script>
	<!-- 专题列表面板 -->
	<!-- <div id="SubjectListBox1" style="width:20%;height:100%">111</div> -->
	<!-- 页面页眉和页脚面板 -->
	<div id="my_logo" class="my_hdr"></div>
	<div id="my_copy" class="my_ftr">技术支持: 广州城市信息研究所有限公司交通事业部 </div><!-- 版权所有&copy;广州省交通运输厅 -->
	<!-- 图表类型选择Dialog面板 -->
	<div id="Chart-form" style="display: none;">
		<div style="background-color:#EBEBEB;height:20px;font-size:13px;padding:2px;">&nbsp柱状图</div>
		<img id="AreatusBar" title="簇状柱状图" alt="簇状柱状图" src="images/AreatusBar.png" style="padding:5px;cursor:pointer">
		<img id="StackBar" title="堆积柱状图" alt="堆积柱状图" src="images/StackBar.png" style="padding:5px;cursor:pointer">
		<!-- <img id="CrossBar" title="二维条状图" alt="二维条状图" src="images/CrossBar.png" style="padding:5px;cursor:pointer">
		<img id="3DBar" title="三维柱状图" alt="三维柱状图" src="images/3DBar.png" style="padding:5px;cursor:pointer"> -->
		
		<div style="background-color:#EBEBEB;height:20px;font-size:13px;padding:2px;">&nbsp饼状图</div>
		<img id="2DPie" title="二维饼状图" alt="二维饼状图" src="images/2DPie.png" style="padding:5px;cursor:pointer">
		<img id="RingPie" title="圆环饼状图" alt="圆环饼状图" src="images/RingPie.png" style="padding:5px;cursor:pointer">
		
		<div style="background-color:#EBEBEB;height:20px;font-size:13px;padding:2px;">&nbsp地     图</div>
		<img id="StaticMap" title="静态地图" alt="静态地图" src="images/StaticMap.png" style="padding:5px;cursor:pointer">
		<img id="DynamicMap" title="动态地图" alt="动态地图" src="images/DynamicMap.gif" style="padding:5px;cursor:pointer">
	</div>
	<!-- 指标参数选择Dialog面板 -->
	<div id="Param-form" style="display: none;">
		<form id="fm">
			<div id="baseParam" class="ftitle">基本参数</div>
		    <div id="subject-Name" class="fitem">
		    	<label>专题名称</label>
		    	<input id="subjectName" class="easyui-textbox" style="width:250px">
		    </div>
		    <div id="index-Param" class="ftitle">指标参数</div>
		    <div id="indexParam"></div>
       	</form>
	</div>
	<!-- 收藏夹Dialog面板 -->
	<!-- <div id="SubjectFile-form" style="display: none;">
		<div id="SubjectListBox" style="width:244px;height:224px"></div>
	</div> -->
</body>
</html>