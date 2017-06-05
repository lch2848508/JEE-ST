<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
//String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";	
%>
<%@ page import="com.fsglj.SummaryTableServlet" %>
<% 
SummaryTableServlet ser=new SummaryTableServlet();
String nfCombox="";//ser.getYearCombox(); 
%>

<!DOCTYPE html>
<html lang="utf-8">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>养护投资计划资金总表</title>
	<script type="text/javascript" src="../fsglj_page/dhtmlxbase/dhtmlx.js"></script>
	<script type="text/javascript" src="../js/release/jquery.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../fsglj_page/dhtmlxbase/dhtmlx.css">
  	<style type="text/css">
  		html,body{
  			width:100%;
  			height:100%;
  			margin:0px;
  			overflow:hidden;
  			padding:0px;		
  		}
  	</style>
  </head>  
  <body scroll="no">
  	<div id="toolBar" style="width:100%"></div>
	<div id="mainbox" style="width:100%;height:100%"></div>
	<script type="text/javascript">
	dhtmlxEvent(window,"load",function(){
		var lmdate;
		//工具栏
		var myToolBar=new dhtmlXToolbarObject("toolBar");	
		myToolBar.addInput(1, 1, "", 100);
		//获取输入框，设置为下拉框
		$("input").replaceWith("年份&nbsp;<select style='height:23px'>"+"<%=nfCombox %>"+"</select>");
		//$("input").replaceWith("年份&nbsp;<select style='height:23px'><option value='2014'>2014</option><option value='2015'>2015</option><option value='2016' selected>2016</option><option value='2017'>2017</option><option value='2018'>2018</option><option value='2019'>2019</option><option value='2020'>2020</option><option value='2021'>2021</option><option value='2022'>2022</option></select>");
		myToolBar.addButton(2,2,"","../images/18x18/search.png","");
		myToolBar.showItem(2);
		myToolBar.setItemToolTip(2,"查询");			
		loadGridData();
		myToolBar.attachEvent("onClick",function(id){			
			if(id==2){
				loadGridData();
			}
		});	
	});	
	
	
	//加载Grid
	function loadGridData(){
		//Servlet获取数据
		var v_nf=$("select").val();
		$.ajax({
			url:"../servlet/SummaryTableServlet",
			data:{selectValue:v_nf},
			type:"GET",
			success:function(r_data){
				//Grid数据
				var mygrid=new dhtmlXGridObject("mainbox");
				mygrid.setImagePath("dhtmlxbase/imgs/");
				mygrid.setSkin("dhx_skyblue");
				var headAlign=[];
				var colType="ro";
				for(var i=0;i<12;i++){
					headAlign.push("text-align:center;");//全部居中
					if(i>0){
						colType=colType+",ro";
					}
				}
				mygrid.setHeader("项目,全局,各区公路局,#cspan,#cspan,#cspan,#cspan,各所,#cspan,#cspan,市局及待批,备注",null,headAlign);
				mygrid.setColAlign("left,center,center,center,center,center,center,center,center,center,center,center");
				mygrid.attachHeader("#rspan,#rspan,禅城,南海,顺德,高明,三水,九江,高明,路桥,#rspan,#rspan",headAlign);
				mygrid.setColTypes(colType);
				mygrid.setInitWidths("153");
				mygrid.enablePreRendering(58);//设置大数据量时，对数据进行分步加载，一次加载58条数据记录
				// mygrid.setColumnColor("#d5f1ff");
				mygrid.init();
				var json_data=JSON.parse(r_data);
				var sort_data=json_data.sort(function(a,b){return a.id>b.id?1:-1});
				data={
					rows: sort_data	
				};
				
				mygrid.parse(data,"json");
				var boldLine=[6,7,8,12,18,19,29,32,37,38,39,40,41,42,53,58];
				for(var i=0;i<boldLine.length;i++){
					mygrid.setRowTextBold(boldLine[i]);//加粗
				}
									
			}
		});
		
	}
	
	
	</script>
  </body>
</html>
