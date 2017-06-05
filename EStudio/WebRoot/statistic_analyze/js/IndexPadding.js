/*
 * 全局变量
 * @ColumnsInfo		data数据源
 * @Controltype		控件类型（由图表类型决定），柱状图或动态地图→控件类型为checkbox；其他图表→控件类型为radio
 * @firstData		一级指标下的数据源
 * @secondData		二级指标下的数据源
 * @thirdData		三级指标下的数据源
 * @fourthData		四级指标下的数据源
 * @fifthData		五级指标下的数据源
 */
var judge="";
var ColumnsInfo;
var ChartType = "";
var Controltype;
var firstData;
var secondData;
var thirdData;
var fourthData;
var fifthData;
//x各级指标下的数据源
var xfirstData;
var xsecondData;
var xsecondObj;
var xthirdData;
var xthirdObj;
var xfourthData;
var xfourthObj;
var xfifthData;
var xfifthObj;
var indexPadding = {};
//x指标的数据源
var xColumnsInfo;
var DataRecord;
var ColumnName;
var columnLabel;
//记录指标的实时级数
var xcount=0;
var ycount=0;
/*
 * 一级指标
 * 
 * @data  表头数据
 * @Type    图表类型
 */


indexPadding.firstIndexSelect = (function(data,Type) {
	//指标重置
	publicFunction.indexDivReset(1);
	var firstIndex = "";
	ColumnsInfo = data;
	ChartType = Type;
	firstData = data;
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\" id=\"firstValue\" name=\"firstValue\"size=\"31\" value=\"\" readOnly=\"true\"></div>" +
			"<div class=\"select_checkBox\" id=\"first_select\"> " +
			"<div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择指标</span><b></b></a></p></div>" +
			"<br><div class=\"chartOptionsFlowTrend\"\" id=\"first_checkbox\"><ul> <li class=\"\">" +
			"<nobr><input onclick=\"javascript:yselectall(this)\"type=\"checkbox\"  name=\"controlAll\"  id=\"controlAll\"/><label for=\"controlAll\">全选</label></nobr>";
	if(ChartType=="二维饼状图"||ChartType=="圆环饼状图"||ChartType=="静态地图"||ChartType=="动态地图")
	{
		Controltype = "radio";
		firstIndex="<tr>";
		document.getElementById("first-lable").style.display = "block";$("#first-Index").html("");
		for(var i=1,l=ColumnsInfo.length;i<l;i++)
		{
			firstIndex += "<td><input name='firstIndex' type='"+Controltype+"' value='"+ColumnsInfo[i].columnLabel+"' onclick='javascript:indexPadding.secondIndexSelect(this)'/>"+ColumnsInfo[i].columnLabel+"</td>";
		}
		firstIndex += "</tr>";
		ycount=0;
		$("#first-Index").html(firstIndex);
	}
	else
	{
		Controltype = "checkbox";
		document.getElementById("first-lable").style.display = "block";$("#first-Index").html("");
		for(var i=1,l=ColumnsInfo.length;i<l;i++){
			firstIndex+="<li class=\"\"><nobr><input name=\"firstIndex\" type=\""+Controltype+"\" value=\""+ColumnsInfo[i].columnLabel+"\" id=\""+ColumnsInfo[i].columnLabel+"\" onclick=\"javascript:indexPadding.secondIndexSelect(this)\"/><label for=\""+ColumnsInfo[i].columnLabel+"\">"+ColumnsInfo[i].columnLabel+"</label></nobr></li>";
			//firstIndex += "<td><input name='firstIndex' type='"+Controltype+"' value='"+ColumnsInfo[i].columnLabel+"' onclick='javascript:indexPadding.secondIndexSelect(this)'/>"+ColumnsInfo[i].columnLabel+"</td>";
		}
		e+=""+firstIndex+"</ul></div></div> ";
		ycount=0;
		$("#first-Index").html(e);
		$(function(){
			$("#first_select").hover(function(){ 
				$("#first_checkbox").css("display","inline-block");},function(){
					$("#first_checkbox").css("display","none");
					});
			});
	}
});

/*
 * x级指标的构建
 * 
 */
indexPadding.xfirstIndexSelect = (function(data,xarray,Type,columnname,HeaderData) {
	//指标重置
	judge=HeaderData[0].columnLabel;
	columnLabel=HeaderData[0].columnName;
	publicFunction.xindexDivReset(1);
	var xfirstIndex = "";
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\" id=\"xfirstValue\" name=\"xfirstValue\" value=\"\" readOnly=\"true\"size=\"30\"></div>" +
			"<div class=\"select_checkBox\" id=\"xfirst_select\"> " +
			"<div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择时间</span><b></b></a></p></div><br>" +
			"<div class=\"chartOptionsFlowTrend\" id=\"xfirst_checkbox\"><ul> <li class=\"\"><nobr><input onclick=\"javascript:xselectall(this)\"type=\"checkbox\"  name=\"xcontrolAll\"  id=\"xcontrolAll\"/><label for=\"xcontrolAll\">全选</label></nobr></li>";
	DataRecord=data;
	xColumnsInfo = xarray;
	ChartType = Type;
	ColumnName=columnname;
	document.getElementById("xfirst-lable").style.display = "block";$("#xfirst-Index").html("");
	if(ChartType=="静态地图")
	{
		if(judge=="地市"||judge=="区域"){
		Controltype="radio";
		firstIndex="<tr>";
		if(DataRecord[0].children)
		{
			if(DataRecord[0].children[0].children)
			{
					for(var j=0;j<xColumnsInfo.length;j++)
					{
						xfirstIndex += "<td><input name='xfirstIndex' type='"+Controltype+"' value='"+xColumnsInfo[j]+"' onclick='javascript:indexPadding.xsecondIndexSelect(this)'/>"+xColumnsInfo[j]+"</td>";
					}
					xfirstIndex += "</tr>";
					xcount=0;
					$("#xfirst-Index").html(xfirstIndex);
			}
			else
			{
				var flag=0;
				for(var i=0;i<DataRecord[0].children.length;i++)
				{					
					if(DataRecord[0].children[i].children)
					{
						flag=flag+1;
					}
				}
				var num=DataRecord[0].children.length-flag;
				if(flag!=0)
				{
					for(var j=0;j<num;j++)
					{
						xfirstIndex += "<td><input name='xfirstIndex' type='"+Controltype+"' value='"+DataRecord[0].children[j][columnLabel]+"' onclick='javascript:indexPadding.xsecondIndexSelect(this)'/>"+DataRecord[0].children[j][columnLabel]+"</td>";
					}
					xfirstIndex += "</tr>";
					xcount=0;
					$("#xfirst-Index").html(xfirstIndex);	
				}
				else
				{
					xfirstData = undefined;
					document.getElementById("xfirst-lable").style.display = "none";xcount=1;	$("#xfirst-Index").html("");	

				}
			}
				
		}
		else
		{
			xfirstData = undefined;
			document.getElementById("xfirst-lable").style.display = "none";	$("#xfirst-Index").html("");
			xcount=1;
		}
		}
		else{alert("该数据不能生成静态地图！指标选择默认为空！");xcount=0;}
	}
	else{	
		Controltype = "checkbox";
		for(var i=0,l=xColumnsInfo.length;i<l;i++){
			xfirstIndex+="<li class=\"\"><input name=\"xfirstIndex\" type=\""+Controltype+"\" value=\""+xColumnsInfo[i]+"\" id=\""+xColumnsInfo[i]+"\" onclick=\"javascript:indexPadding.xsecondIndexSelect(this)\"/><label for=\""+xColumnsInfo[i]+"\">"+xColumnsInfo[i]+"</label></li>";
		}
		e+=""+xfirstIndex+"</ul></div></div> ";
		xcount=0;
		$("#xfirst-Index").html(e);
		$(function(){
			$("#xfirst_select").hover(function(){ 
				$("#xfirst_checkbox").css("display","inline-block");},function(){
					$("#xfirst_checkbox").css("display","none");
					});
			});
		}
/*
 * 添加的，供固定格式的表格使用（仅包含不超两级列）将两级提前显示出来
 */
/*	if(xsecondData!=undefined)
	{
		var xcheckboxofFirstindex = document.getElementsByName('xfirstIndex');
		xsecondData=indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[0].defaultValue,ColumnName).tempdata;
		xsecondObj = indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[0].defaultValue,ColumnName).tempobj;
		var xsecondIndex="";
		e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> <div class=\"select_checkBox\" id=\"xsecond_select\"> <div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>请选择行参数</span><b></b></a></p></div><div class=\"input-style\"><input type=\"text\" id=\"xsecondValue\" size=\"30\" name=\"xsecondValue\" value=\"\" readOnly=\"true\"></div><br><div class=\"chartOptionsFlowTrend\" id=\"xsecond_checkbox\"><ul>";
		document.getElementById("xsecond-lable").style.display = "block";$("#xsecond-Index").html("");
		for(var i=0,l=xsecondData.length;i<l;i++)
		{
			xsecondIndex+="<li class=\"\"><input name=\"xsecondIndex\" type=\""+Controltype+"\" value=\""+xsecondData[i]+"\" onclick=\"javascript:indexPadding.xthirdIndexSelect(this)\"/><span>"+xsecondData[i]+"</span></li>";
		}
		e+=""+xsecondIndex+"</ul></div></div> ";
		$("#xsecond-Index").html(e);
		$(function(){
			$("#xsecond_select").hover(function(){ 
				$("#xsecond_checkbox").css("display","inline-block");},function(){
					$("#xsecond_checkbox").css("display","none");
					});
			});
	}*/
});



/*
 * x二级指标的构建
 * 
 */
indexPadding.xsecondIndexSelect = (function(obj) {

	var xsecondIndex = "";
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\" id=\"xsecondValue\" size=\"30\" name=\"xsecondValue\" value=\"\" readOnly=\"true\"></div>" +
			"<div class=\"select_checkBox\" id=\"xsecond_select\"> " +
			"<div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>请选择行参数</span><b></b></a></p></div><br>" +
			"<div class=\"chartOptionsFlowTrend\" id=\"xsecond_checkbox\"><ul>";
	var xcheckboxofFirstindex = document.getElementsByName('xfirstIndex');
	if(ChartType=="簇状柱状图"||ChartType=="二维饼状图"||ChartType == "圆环饼状图"){
		var flg = true;  
		  var items = document.getElementsByName("xfirstIndex");  
		  for(var i=0;i<items.length;i++){  
		      if(items[i].type=="checkbox"&& items[i].name=="xfirstIndex"&&items.length!=1){  
		          if(!items[i].checked){  
		              flg = false;  
		              break;  
		          }  
		      }  
		  }  
		  document.getElementById("xcontrolAll").checked= flg; 
			if(obj.checked)
			{	
				xcount=1;
				var flag=0;
				var strs= new Array(); 
				strs=document.getElementById('xfirstValue').value.split(","); 
				for(var i=0;i<strs.length;i=i+1){
					if(strs[i]==obj.value)
					{
						flag=1;
					}
				}
				if(flag!=1){document.getElementById('xfirstValue').value+=obj.value+",";}		
				var count = indexPadding.checkedIndexNumber(xcheckboxofFirstindex);
				Controltype = "checkbox";
				xsecondData=indexPadding.xmidData(DataRecord,obj.defaultValue,ColumnName).tempdata;
				xsecondObj = indexPadding.xmidData(DataRecord,obj.defaultValue,ColumnName).tempobj;
			}
			else
			{
				document.getElementById('xfirstValue').value=document.getElementById('xfirstValue').value.replace( obj.value+",","");
				var count = indexPadding.checkedIndexNumber(xcheckboxofFirstindex);
				if(count==0)
				{
					xsecondData = undefined;
					document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");
					xcount=0;
				}
				else 
				{	xcount=1;
					Controltype = "checkbox";
					for(var j=0,l=xcheckboxofFirstindex.length;j<l;j++)
					{
						if(xcheckboxofFirstindex[j].checked)
						{
							xsecondData=indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[j].defaultValue,ColumnName).tempdata;
							xsecondObj = indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[j].defaultValue,ColumnName).tempobj;
						}
					}
				}
			}
	}
	else if(ChartType=="折线图"){
		if(obj.checked)
		{	xcount=1;
			document.getElementById('xfirstValue').value+=obj.value+",";		
			var count = indexPadding.checkedIndexNumber(xcheckboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				xsecondData=indexPadding.xmidData(DataRecord,obj.defaultValue,ColumnName).tempdata;
				xsecondObj = indexPadding.xmidData(DataRecord,obj.defaultValue,ColumnName).tempobj;
			}
			else
			{
				xsecondData = indexPadding.xmidData(DataRecord,obj.defaultValue,ColumnName).tempdata;
				xsecondObj = indexPadding.xmidData(DataRecord,obj.defaultValue,ColumnName).tempobj;
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('xfirstValue').value=document.getElementById('xfirstValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(xcheckboxofFirstindex);
			if(count==0)
			{
				xsecondData = undefined;
				document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");
				xcount=0;
			}
			else if(count==1)
			{
				xcount=1;
				Controltype = "checkbox";
				for(var j=0,l=xcheckboxofFirstindex.length;j<l;j++)
				{
					if(xcheckboxofFirstindex[j].checked)
					{
						xsecondData=indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xsecondObj = indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
			else
			{
				xcount=1;
				Controltype = "radio";
				for(var j=0,l=xcheckboxofFirstindex.length;j<l;j++)
				{
					if(xcheckboxofFirstindex[j].checked)
					{
						xsecondData=indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xsecondObj= indexPadding.xmidData(DataRecord,xcheckboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
		}
	}
	
	//指标重置
	publicFunction.xindexDivReset(2);
	if(ChartType=="静态地图")
	{
		xcount=1;
		xsecondData = undefined;
		document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");			
	}
	if(ChartType=="动态地图")
	{
		if(judge=="时间"){
		var flg = true;  
		  var items = document.getElementsByName("xfirstIndex");  
		  for(var i=0;i<items.length;i++){  
		      if(items[i].type=="checkbox"&& items[i].name=="xfirstIndex"&&items.length!=1){  
		          if(!items[i].checked){  
		              flg = false;  
		              break;  
		          }  
		      }  
		  }  
		  document.getElementById("xcontrolAll").checked= flg; 
			if(obj.checked)
			{	
				xcount=1;
				var flag=0;
				var strs= new Array(); 
				strs=document.getElementById('xfirstValue').value.split(","); 
				for(var i=0;i<strs.length;i=i+1){
					if(strs[i]==obj.value)
					{
						flag=1;
					}
				}
				if(flag!=1){document.getElementById('xfirstValue').value+=obj.value+",";}		
							xcount=1;
							xsecondData = undefined;
							document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");
			}
			else
			{
				document.getElementById('xfirstValue').value=document.getElementById('xfirstValue').value.replace( obj.value+",","");
				var count = indexPadding.checkedIndexNumber(xcheckboxofFirstindex);
				if(count==0)
				{
					xcount=0;
				}
				else 
				{	xcount=1;
				}
				xsecondData = undefined;
				document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");
			}
		}
		else{alert("该数据不能生成动态地图！指标选择默认为空！");xcount=0;}
	}
//	if(ChartType=="静态地图")
//	{	
//		Controltype = "radio";
//		if(DataRecord[0].children)
//		{
//			if(DataRecord[0].children[1].children)
//			{
//				var flag=0;
//				for(var i=0;i<DataRecord[0].children[1].children.length;i++)
//				{					
//					if(DataRecord[0].children[1].children[i].children)
//					{
//						flag=i;
//					}
//				}
//				if(flag!=0)
//				{
//					xsecondData=DataRecord[0].children[1].children[flag].children;
//				}
//				else
//				{
//					xcount=1;
//					xsecondData = undefined;
//					document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");	
//				}
//			}
//			else
//			{
//				xcount=1;
//				xsecondData = undefined;
//				document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");	
//			}
//				
//		}
//		else
//		{
//			xcount=1;
//			xsecondData = undefined;
//			document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");			
//		}	
//	}
//	if(ChartType=="动态地图")
//	{
//		var flg = true;  
//		  var items = document.getElementsByName("xfirstIndex");  
//		  for(var i=0;i<items.length;i++){  
//		      if(items[i].type=="checkbox"&& items[i].name=="xfirstIndex"&&items.length!=1){  
//		          if(!items[i].checked){  
//		              flg = false;  
//		              break;  
//		          }  
//		      }  
//		  }  
//		  document.getElementById("xcontrolAll").checked= flg; 
//			if(obj.checked)
//			{	
//				xcount=1;
//				var flag=0;
//				var strs= new Array(); 
//				strs=document.getElementById('xfirstValue').value.split(","); 
//				for(var i=0;i<strs.length;i=i+1){
//					if(strs[i]==obj.value)
//					{
//						flag=1;
//					}
//				}
//				if(flag!=1){document.getElementById('xfirstValue').value+=obj.value+",";}		
//				if(DataRecord[0].children)
//				{
//					if(DataRecord[0].children[1].children)
//					{
//						var flag=0;
//						for(var i=0;i<DataRecord[0].children[1].children.length;i++)
//						{					
//							if(DataRecord[0].children[1].children[i].children)
//							{
//								flag=i;
//							}
//						}
//						if(flag!=0)
//						{
//							Controltype = "radio";
//							xsecondData=DataRecord[0].children[1].children[flag].children;
//						}
//						else
//						{
//							xcount=1;
//							xsecondData = undefined;
//							document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");
//						}
//					}
//					else{alert("该数据不能制作动态专题地图!");
//					location.reload();}
//				}
//				else{alert("该数据不能制作动态专题地图!");
//				location.reload();}
//			}
//			else
//			{
//				document.getElementById('xfirstValue').value=document.getElementById('xfirstValue').value.replace( obj.value+",","");
//				var count = indexPadding.checkedIndexNumber(xcheckboxofFirstindex);
//				if(count==0)
//				{
//					xsecondData = undefined;
//					document.getElementById("xsecond-lable").style.display = "none";	$("#xsecond-Index").html("");
//					xcount=0;
//				}
//				else 
//				{	xcount=1;
//					Controltype = "radio";
//					xsecondData=DataRecord[0].children[1].children[0].children;
//				}
//			}
//	}
	if(xsecondData!=undefined&&Controltype=="checkbox")
	{

		document.getElementById("xsecond-lable").style.display = "block";$("#xsecond-Index").html("");
			for(var i=0,l=xsecondData.length;i<l;i++)
			{
				xsecondIndex+="<li class=\"\"><input name=\"xsecondIndex\" type=\""+Controltype+"\" value=\""+xsecondData[i]+"\" id=\""+xsecondData[i]+"\" onclick=\"javascript:indexPadding.xthirdIndexSelect(this)\"/><label for=\""+xsecondData[i]+"\">"+xsecondData[i]+"</label></li>";
			}
			e+=""+xsecondIndex+"</ul></div></div> ";
			//xcount=2;
			$("#xsecond-Index").html(e);
			$(function(){
				$("#xsecond_select").hover(function(){ 
					$("#xsecond_checkbox").css("display","inline-block");},function(){
						$("#xsecond_checkbox").css("display","none");
						});
				});
	}
	else if(xsecondData!=undefined&&Controltype=="radio"&&ChartType!="静态地图"&&ChartType!="动态地图")
	{
		xsecondIndex="<tr>";
		document.getElementById("xsecond-lable").style.display = "block";$("#xsecond-Index").html("");
		for(var i=0,l=xsecondData.length;i<l;i++)
		{
			xsecondIndex += "<td><input name='xsecondIndex' type='"+Controltype+"' value='"+xsecondData[i]+"' onclick='javascript:indexPadding.xthirdIndexSelect(this)'/>"+xsecondData[i]+"</td>";
		}
		xsecondIndex += "</tr>";
		//xcount=2;
		$("#xsecond-Index").html(xsecondIndex);
	}
	else if(xsecondData!=undefined&&Controltype=="radio"&&(ChartType=="动态地图"||ChartType=="静态地图"))
	{
			secondIndex="<tr>";
			document.getElementById("xsecond-lable").style.display = "block";$("#xsecond-Index").html("");
			for(var i=0,l=xsecondData.length;i<l;i++)
			{
				xsecondIndex += "<td><input name='xsecondIndex' type='"+Controltype+"' value='"+xsecondData[i][columnLabel]+"' onclick='javascript:indexPadding.xthirdIndexSelect(this)'/>"+xsecondData[i][columnLabel]+"</td>";
			}
			xsecondIndex += "</tr>";
			//xcount=2;
			$("#xsecond-Index").html(xsecondIndex);
	}
});

/*
 * x三级指标的构建
 * 
 */
indexPadding.xthirdIndexSelect = (function(obj) {
	var xthirdIndex = "";
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\" id=\"xthirdValue\" name=\"xthirdValue\"size=\"30\" value=\"\" readOnly=\"true\"></div>"+
			"<div class=\"select_checkBox\" id=\"xthird_select\"> <div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择指标</span><b></b></a></p></div>" +
			"<br>" +
			"<div class=\"chartOptionsFlowTrend\" id=\"xthird_checkbox\"><ul>";
	var checkboxofFirstindex = document.getElementsByName('xsecondIndex');
	if(obj.type=="checkbox")
	{
		if(obj.checked)
		{
			xcount=2;
			document.getElementById('xsecondValue').value+=obj.value+",";
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				xthirdData = indexPadding.xmidData(xsecondObj,obj.defaultValue,ColumnName).tempdata;
				xthirdObj = indexPadding.xmidData(xsecondObj,obj.defaultValue,ColumnName).tempobj;
			}
			else
			{
				xthirdData = indexPadding.xmidData(xsecondObj,obj.defaultValue,ColumnName).tempdata;
				xthirdObj = indexPadding.xmidData(xsecondObj,obj.defaultValue,ColumnName).tempobj;
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('xsecondValue').value=document.getElementById('xsecondValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==0)
			{
				xthirdData = undefined;
				document.getElementById("xthird-lable").style.display = "none";	$("#xthird-Index").html("");
				xcount=1;
			}
			else if(count==1)
			{
				xcount=2;
				Controltype = "checkbox";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						xthirdData = indexPadding.xmidData(xsecondObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xthirdObj = indexPadding.xmidData(xsecondObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
			else
			{
				xcount=2;
				Controltype = "radio";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						xthirdData = indexPadding.xmidData(xsecondObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xthirdObj = indexPadding.xmidData(xsecondObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
		}
	}
	else if(obj.type=="radio")
	{
		if(ChartType=="动态地图"||ChartType=="静态地图")
		{
			xthirdData = undefined;
			document.getElementById("xthird-lable").style.display = "none";	$("#xthird-Index").html("");
			xcount=2;
		}
		else
		{
			if(obj.checked){xcount=2;}
			Controltype = "radio";
			xthirdData = indexPadding.xmidData(xsecondObj,obj.defaultValue,ColumnName).tempdata;
			xthirdObj = indexPadding.xmidData(xsecondObj,obj.defaultValue,ColumnName).tempobj;
		}
		
	}
	//指标重置
	publicFunction.xindexDivReset(3);
	if(xthirdData!=undefined&&Controltype=="checkbox")
	{
		document.getElementById("xthird-lable").style.display = "block";$("#xthird-Index").html("");
		for(var i=0,l=xthirdData.length;i<l;i++)
		{
			xthirdIndex+="<li class=\"\"><input name=\"xthirdIndex\" type=\""+Controltype+"\" value=\""+xthirdData[i]+"\"id=\""+xthirdData[i]+"\" onclick=\"javascript:indexPadding.xfourthIndexSelect(this)\"/><label for=\""+xthirdData[i]+"\">"+xthirdData[i]+"</label></li>";
		}
		e+=""+xthirdIndex+"</ul></div></div> ";
		//xcount=3;
		$("#xthird-Index").html(e);
		$(function(){
			$("#xthird_select").hover(function(){ 
				$("#xthird_checkbox").css("display","inline-block");},function(){
					$("#xthird_checkbox").css("display","none");
					});
			});
	}
	else if(xthirdData!=undefined&&Controltype=="radio")
	{
		xthirdIndex="<tr>";
		document.getElementById("xthird-lable").style.display = "block";$("#xthird-Index").html("");
		for(var i=0,l=xthirdData.length;i<l;i++)
		{
			xthirdIndex += "<td><input name='xthirdIndex' type='"+Controltype+"' value='"+xthirdData[i]+"' onclick='javascript:indexPadding.xfourthIndexSelect(this)'/>"+xthirdData[i]+"</td>";
		}
		xthirdIndex += "</tr>";
		//xcount=3;
		$("#xthird-Index").html(xthirdIndex);
	}
});

/*
 * x四级指标的构建
 * 
 */
indexPadding.xfourthIndexSelect = (function(obj) {
	var xfourthIndex = "";
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\" id=\"xfourthValue\" name=\"xfourthValue\" value=\"\" readOnly=\"true\"size=\"30\"></div>" +
			"<div class=\"select_checkBox\" id=\"xfourth_select\"> <div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择指标</span><b></b></a></p></div>" +
			"<br><div class=\"chartOptionsFlowTrend\" id=\"xfourth_checkbox\"><ul>";
	var checkboxofFirstindex = document.getElementsByName('xthirdIndex');
	
	if(obj.type=="checkbox")
	{
		if(obj.checked)
		{
			document.getElementById('xthirdValue').value+=obj.value+",";		
			xcount=3;
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				xfourthData = indexPadding.xmidData(xthirdObj,obj.defaultValue,ColumnName).tempdata;
				xfourthObj = indexPadding.xmidData(xthirdObj,obj.defaultValue,ColumnName).tempobj;
			}
			else
			{
				xfourthData = indexPadding.xmidData(xthirdObj,checkboxofFirstindex[1].defaultValue,ColumnName).tempdata;
				xfourthObj = indexPadding.xmidData(xthirdObj,checkboxofFirstindex[1].defaultValue,ColumnName).tempobj;
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('xthirdValue').value=document.getElementById('xthirdValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==0)
			{
				xfourthData = undefined;
				document.getElementById("xfourth-lable").style.display = "none";	$("#xfourth-Index").html("");
				xcount=2;
			}
			else if(count==1)
			{
				xcount=3;
				Controltype = "checkbox";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						xfourthData = indexPadding.xmidData(xthirdObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xfourthObj = indexPadding.xmidData(xthirdObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
			else
			{
				xcount=3;
				Controltype = "radio";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						xfourthData = indexPadding.xmidData(xthirdObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xfourthObj = indexPadding.xmidData(xthirdObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
		}
	}
	else if(obj.type=="radio")
	{
		if(obj.checked){xcount=3;}
		Controltype = "radio";
		xfourthData = indexPadding.xmidData(xthirdObj,obj.defaultValue,ColumnName).tempdata;
		xfourthObj = indexPadding.xmidData(xthirdObj,obj.defaultValue,ColumnName).tempobj;
	}
	//指标重置
	publicFunction.xindexDivReset(4);
	
	if(ChartType=="动态地图")
	{
		Controltype = "radio";
	}
	if(xfourthData!=undefined&&Controltype=="checkbox")
	{
		document.getElementById("xfourth-lable").style.display = "block";$("#xfourth-Index").html("");
		for(var i=0,l=xfourthData.length;i<l;i++)
		{
			xfourthIndex+="<li class=\"\"><input name=\"xfourthIndex\" type=\""+Controltype+"\" value=\""+xfourthData[i]+"\"id=\""+xfourthData[i]+"\" onclick=\"javascript:indexPadding.xfifthIndexSelect(this)\"/><label for=\""+xfourthData[i]+"\">"+xfourthData[i]+"</label></li>";
		}
		e+=""+xfourthIndex+"</ul></div></div> ";
		//xcount=4;
		$("#xfourth-Index").html(e);
		$(function(){
			$("#xfourth_select").hover(function(){ 
				$("#xfourth_checkbox").css("display","inline-block");},function(){
					$("#xfourth_checkbox").css("display","none");
					});
			});
	}
	else if(xfourthData!=undefined&&Controltype=="radio")
	{
		xfourthIndex="<tr>";
		document.getElementById("xfourth-lable").style.display = "block";$("#xfourth-Index").html("");
		for(var i=0,l=xfourthData.length;i<l;i++)
		{
			xfourthIndex += "<td><input name='xfourthIndex' type='"+Controltype+"' value='"+xfourthData[i]+"' onclick='javascript:indexPadding.xfifthIndexSelect(this)'/>"+xfourthData[i]+"</td>";
		}
		xfourthIndex += "</tr>";
		//xcount=4;
		$("#xfourth-Index").html(xfourthIndex);
	}
});

/*
 * x五级指标的构建
 * 
 */
indexPadding.xfifthIndexSelect = (function(obj) {
	var xfifthIndex = "";
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\" size=\"30\"id=\"xfifthValue\" name=\"xfifthValue\" value=\"\" readOnly=\"true\"></div>" +
			"<div class=\"select_checkBox\" id=\"xfifth_select\"> <div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择指标</span><b></b></a></p></div>" +
			"<br><div class=\"chartOptionsFlowTrend\" id=\"xfifth_checkbox\"><ul>";
	var checkboxofFirstindex = document.getElementsByName('xfourthIndex');
	if(obj.type=="checkbox")
	{
		if(obj.checked)
		{
			xcount=4;
			document.getElementById('xfourthValue').value+=obj.value+",";		
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				xfifthData = indexPadding.xmidData(xfourthObj,obj.defaultValue,ColumnName).tempdata;
				xfifthObj = indexPadding.xmidData(xfourthObj,obj.defaultValue,ColumnName).tempobj;
			}
			else
			{
				xfifthData = indexPadding.xmidData(xfourthObj,checkboxofFirstindex[1].defaultValue,ColumnName).tempdata;
				xfifthObj = indexPadding.xmidData(xfourthObj,checkboxofFirstindex[1].defaultValue,ColumnName).tempobj;
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('xfourthValue').value=document.getElementById('xfourthValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==0)
			{
				xfifthData = undefined;
				document.getElementById("xfifth-lable").style.display = "none";	$("#xfifth-Index").html("");
				xcount=3;
			}
			else if(count==1)
			{
				xcount=4;
				Controltype = "checkbox";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						xfifthData = indexPadding.xmidData(xfourthObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xfifthObj = indexPadding.xmidData(xfourthObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
			else
			{
				xcount=4;
				Controltype = "radio";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						xfifthData = indexPadding.xmidData(xfourthObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempdata;
						xfifthObj = indexPadding.xmidData(xfourthObj,checkboxofFirstindex[j].defaultValue,ColumnName).tempobj;
					}
				}
			}
		}
	}
	else if(obj.type=="radio")
	{
		if(obj.checked){xcount=4;}
		Controltype = "radio";
		xfifthData = indexPadding.xmidData(xfourthObj,obj.defaultValue,ColumnName).tempdata;
		xfifthObj = indexPadding.xmidData(xfourthObj,obj.defaultValue,ColumnName).tempobj;
	}
	//指标重置
	publicFunction.xindexDivReset(5);
	
	if(ChartType=="动态地图")
	{
		Controltype = "radio";
	}
	if(xfifthData!=undefined&&Controltype=="checkbox")
	{
		document.getElementById("xfifth-lable").style.display = "block";$("#xfifth-Index").html("");
		for(var i=0,l=xfifthData.length;i<l;i++)
		{
			xfifthIndex+="<li class=\"\"><input name=\"xfifthIndex\" type=\""+Controltype+"\" value=\""+xfifthData[i]+"\"id=\""+xfifthData[i]+"\" onclick=\"javascript:indexPadding.xsixthIndexSelect(this)\"/><label for=\""+xfifthData[i]+"\">"+xfifthData[i]+"</label></li>";
		}
		e+=""+xfifthIndex+"</ul></div></div> ";
		//xcount=5;
		$("#xfifth-Index").html(e);
		$(function(){
			$("#xfifth_select").hover(function(){ 
				$("#xfifth_checkbox").css("display","inline-block");},function(){
					$("#xfifth_checkbox").css("display","none");
					});
			});
	}
	else if(xfifthData!=undefined&&Controltype=="radio")
	{
		xfifthIndex="<tr>";
		document.getElementById("xfifth-lable").style.display = "block";$("#xfifth-Index").html("");
		for(var i=0,l=xfifthData.length;i<l;i++)
		{
			xfifthIndex += "<td><input name='xfifthIndex' type='"+Controltype+"' value='"+xfifthData[i]+"' onclick='javascript:indexPadding.xsixthIndexSelect(this)'/>"+xfifthData[i]+"</td>";
		}
		xfifthIndex += "</tr>";
		//xcount=5;
		$("#xfifthIndex").html(xfifthIndex);
	}
});
indexPadding.xsixthIndexSelect = (function(obj) {
	if(obj.checked){xcount=5;}
});
/*
 * 二级指标的构建
 * 
 */
indexPadding.secondIndexSelect = (function(obj) {
	//设置普通复选框与全选框的关联

	var secondIndex = "";
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\"size=\"15\" id=\"secondValue\" name=\"secondValue\" value=\"\" readOnly=\"true\"></div>" +
			"<div class=\"select_checkBox\" id=\"second_select\"> <div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择指标</span><b></b></a></p></div>" +
			"<br><div class=\"chartOptionsFlowTrend\" id=\"second_checkbox\"><ul>";
	var checkboxofFirstindex = document.getElementsByName('firstIndex');
	if(obj.type=="checkbox")
	{
		var flg = true;  
		  var items = document.getElementsByName("firstIndex");  
		  for(var i=0;i<items.length;i++){  
		      if(items[i].type=="checkbox"&& items[i].name=="firstIndex"&&items.length!=1){  
		          if(!items[i].checked){  
		              flg = false;  
		              break;  
		          }  
		      }  
		  }  
		  document.getElementById("controlAll").checked= flg;  
		if(obj.checked)
		{
			ycount=1;
			var flag=0;
			var strs= new Array(); 
			strs=document.getElementById('firstValue').value.split(","); 
			for(var i=0;i<strs.length;i=i+1){
				if(strs[i]==obj.value)
				{
					flag=1;
				}
			}
			if(flag!=1){document.getElementById('firstValue').value+=obj.value+",";}
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				secondData = indexPadding.midData(firstData,obj.defaultValue);
			}
			else
			{
				secondData = indexPadding.midData(firstData,obj.defaultValue);		
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('firstValue').value=document.getElementById('firstValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==0)
			{
				secondData = undefined;
				document.getElementById("second-lable").style.display = "none";	$("#second-Index").html("");
				ycount=0;
			}
			else if(count==1)
			{
				ycount=1;
				Controltype = "checkbox";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						secondData = indexPadding.midData(firstData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
			else
			{	
				ycount=1;
				Controltype = "radio";
				e="";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						secondData = indexPadding.midData(firstData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
		}
	}
	else
	{
		if(obj.checked){ycount=1;}
		Controltype = "radio";
		secondData = indexPadding.midData(firstData,obj.defaultValue);
	}
	//指标重置
	publicFunction.indexDivReset(2);
	
	if(ChartType=="动态地图")
	{
		Controltype = "radio";
	}
	if(secondData!=undefined&&Controltype != "radio")
	{
		document.getElementById("second-lable").style.display = "block";$("#second-Index").html("");
		for(var i=0,l=secondData.length;i<l;i++)
		{
			secondIndex+="<li class=\"\"><input name=\"secondIndex\" type=\""+Controltype+"\" value=\""+secondData[i].columnLabel+"\" id=\""+secondData[i].columnLabel+"\" onclick=\"javascript:indexPadding.thirdIndexSelect(this)\"/><label for=\""+secondData[i].columnLabel+"\">"+secondData[i].columnLabel+"</label></li>";
		}
		e+=""+secondIndex+"</ul></div></div> ";
		//ycount=2;
		$("#second-Index").html(e);
		$(function(){
			$("#second_select").hover(function(){ 
				$("#second_checkbox").css("display","inline-block");},function(){
					$("#second_checkbox").css("display","none");
					});
			});
	}
	if(secondData!=undefined&&Controltype== "radio")
	{
		secondIndex="<tr>";
		document.getElementById("second-lable").style.display = "block";$("#second-Index").html("");
		for(var i=0,l=secondData.length;i<l;i++)
		{
			secondIndex += "<td><input name='secondIndex' type='"+Controltype+"' value='"+secondData[i].columnLabel+"' onclick='javascript:indexPadding.thirdIndexSelect(this)'/>"+secondData[i].columnLabel+"</td>";
		}
		secondIndex += "</tr>";
		//ycount=2;
		$("#second-Index").html(secondIndex);
	}
});

/*
 * 三级指标的构建
 * 
 */
indexPadding.thirdIndexSelect = (function(obj) {
	var thirdIndex = "";
	var checkboxofFirstindex = document.getElementsByName('secondIndex');
		
	if(obj.type=="checkbox")
	{
		var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
				"<div class=\"input-style\"><input type=\"text\" id=\"thirdValue\" size=\"15\"name=\"thirdValue\" value=\"\" readOnly=\"true\"></div>" +
				"<div class=\"select_checkBox\" id=\"third_select\"> <div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择指标</span><b></b></a></p></div>" +
				"<br><div class=\"chartOptionsFlowTrend\" id=\"third_checkbox\"><ul>";
		if(obj.checked)
		{
			ycount=2;
			document.getElementById('secondValue').value+=obj.value+",";		
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				thirdData = indexPadding.midData(secondData,obj.defaultValue);
			}
			else
			{
				thirdData = indexPadding.midData(secondData,checkboxofFirstindex[1].defaultValue);
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('secondValue').value=document.getElementById('secondValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==0)
			{
				thirdData = undefined;
				document.getElementById("third-lable").style.display = "none";	$("#third-Index").html("");
				ycount=1;
			}
			else if(count==1)
			{
				ycount=2;
				Controltype = "checkbox";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						thirdData = indexPadding.midData(secondData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
			else
			{
				ycount=2;
				Controltype = "radio";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						thirdData = indexPadding.midData(secondData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
		}
	}
	else if(obj.type=="radio")
	{if(obj.checked){ycount=2;}
		Controltype = "radio";
		thirdData = indexPadding.midData(secondData,obj.defaultValue);
	}
	//指标重置
	publicFunction.indexDivReset(3);
	
	if(ChartType=="动态地图")
	{
		Controltype = "radio";
	}
	if(thirdData!=undefined&&Controltype=="checkbox")
	{
		document.getElementById("third-lable").style.display = "block";$("#third-Index").html("");
		for(var i=0,l=thirdData.length;i<l;i++)
		{
			thirdIndex+="<li class=\"\"><input name=\"thirdIndex\" type=\""+Controltype+"\" value=\""+thirdData[i].columnLabel+"\" id=\""+thirdData[i].columnLabel+"\" onclick=\"javascript:indexPadding.fourthIndexSelect(this)\"/><label for=\""+thirdData[i].columnLabel+"\">"+thirdData[i].columnLabel+"</label></li>";
		}
		e+=""+thirdIndex+"</ul></div></div> ";
		//ycount=3;
		$("#third-Index").html(e);
		$(function(){
			$("#third_select").hover(function(){ 
				$("#third_checkbox").css("display","inline-block");},function(){
					$("#third_checkbox").css("display","none");
					});
			});
	}
	if(thirdData!=undefined&&Controltype=="radio")
	{
		thirdIndex="<tr>";
		document.getElementById("third-lable").style.display = "block";$("#third-Index").html("");
		for(var i=0,l=thirdData.length;i<l;i++)
		{
			thirdIndex += "<td><input name='thirdIndex' type='"+Controltype+"' value='"+thirdData[i].columnLabel+"' onclick='javascript:indexPadding.fourthIndexSelect(this)'/>"+thirdData[i].columnLabel+"</td>";
		}
		thirdIndex += "</tr>";
		//ycount=3;
		$("#third-Index").html(thirdIndex);
	}
});

/*
 * 四级指标的构建
 * 
 */
indexPadding.fourthIndexSelect = (function(obj) {
	var fourthIndex = "";
	var flag=0;
	var e="<link rel=\"stylesheet\" href=\"js/selectIndex.css\" type=\"text/css\"/> " +
			"<div class=\"input-style\"><input type=\"text\" id=\"fourthValue\" size=\"15\"name=\"fourthValue\" value=\"\" readOnly=\"true\"></div>" +
			"<div class=\"select_checkBox\" id=\"fourth_select\"> " +
			"<div class=\"chartQuota\"><p><a href=\"javascript:;\" hidefocus=\"true\" title=\"请选择指标\"><span>选择指标</span><b></b></a></p></div>" +
			"<br><div class=\"chartOptionsFlowTrend\" id=\"fourth_checkbox\"><ul>";
	var checkboxofFirstindex = document.getElementsByName('thirdIndex');
	if(obj.type=="checkbox")
	{
		if(obj.checked)
		{
			ycount=3;
			document.getElementById('thirdValue').value+=obj.value+",";		
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				fourthData = indexPadding.midData(thirdData,obj.defaultValue);
			}
			else
			{
				fourthData = indexPadding.midData(thirdData,checkboxofFirstindex[1].defaultValue);
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('thirdValue').value=document.getElementById('thirdValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==0)
			{
				fourthData = undefined;
				document.getElementById("fourth-lable").style.display = "none";	$("#fourth-Index").html("");
				ycount=2;
			}
			else if(count==1)
			{
				ycount=3;
				Controltype = "checkbox";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						fourthData = indexPadding.midData(thirdData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
			else
			{
				ycount=3;
				Controltype = "radio";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						fourthData = indexPadding.midData(thirdData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
		}
	}
	else if(obj.type=="radio")
	{
		if(obj.checked){ycount=3;}
		Controltype = "radio";
		fourthData = indexPadding.midData(thirdData,obj.defaultValue);
	}
	//指标重置
	publicFunction.indexDivReset(4);
	
	if(ChartType=="动态地图")
	{
		Controltype = "radio";
	}
	if(fourthData!=undefined&&Controltype=="checkbox")
	{
		document.getElementById("fourth-lable").style.display = "block";$("#fourth-Index").html("");
		for(var i=0,l=fourthData.length;i<l;i++)
		{
			fourthIndex+="<li class=\"\"><input name=\"fourthIndex\" type=\""+Controltype+"\" value=\""+fourthData[i].columnLabel+"\"id=\""+fourthData[i].columnLabel+"\" onclick=\"javascript:indexPadding.fifthIndexSelect(this)\"/><label for=\""+fourthData[i].columnLabel+"\">"+fourthData[i].columnLabel+"</label></li>";
		}
		e+=""+fourthIndex+"</ul></div></div> ";
		//ycount=4;
		$("#fourth-Index").html(e);
		$(function(){
			$("#fourth_select").hover(function(){ 
				$("#fourth_checkbox").css("display","inline-block");},function(){
					$("#fourth_checkbox").css("display","none");
					});
			});
	}
	if(fourthData!=undefined&&Controltype=="radio")
	{
		fourthIndex="<tr>";
		document.getElementById("fourth-lable").style.display = "block";$("#fourth-Index").html("");
		for(var i=0,l=fourthData.length;i<l;i++)
		{
			fourthIndex += "<td><input name='fourthIndex' type='"+Controltype+"' value='"+fourthData[i].columnLabel+"' onclick='javascript:indexPadding.fifthIndexSelect(this)'/>"+fourthData[i].columnLabel+"</td>";
		}
		fourthIndex += "</tr>";
		//ycount=4;
		$("#fourth-Index").html(fourthIndex);
	}
});

/*
 * 五级指标的构建
 * 
 */
indexPadding.fifthIndexSelect = (function(obj) {
	var fifthIndex = "<tr>";
	var checkboxofFirstindex = document.getElementsByName('fourthIndex');
	if(obj.checked)
	if(obj.type=="checkbox")
	{
		if(obj.checked)
		{
			ycount=4;
			document.getElementById('fourthValue').value+=obj.value+",";		
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==1)
			{
				Controltype = "checkbox";
				fifthData = indexPadding.midData(fourthData,obj.defaultValue);
			}
			else
			{
				fifthData = indexPadding.midData(fourthData,checkboxofFirstindex[1].defaultValue);
				Controltype = "radio";
			}
		}
		else
		{
			document.getElementById('fourthValue').value=document.getElementById('fourthValue').value.replace( obj.value+",","");
			var count = indexPadding.checkedIndexNumber(checkboxofFirstindex);
			if(count==0)
			{
				fifthData = undefined;
				document.getElementById("fifth-lable").style.display = "none";	$("#fifth-Index").html("");
				ycount=3;
			}
			else if(count==1)
			{
				ycount=4;
				Controltype = "checkbox";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						fifthData = indexPadding.midData(fourthData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
			else
			{
				ycount=4;
				Controltype = "radio";
				for(var j=0,l=checkboxofFirstindex.length;j<l;j++)
				{
					if(checkboxofFirstindex[j].checked)
					{
						fifthData = indexPadding.midData(fourthData,checkboxofFirstindex[j].defaultValue);
					}
				}
			}
		}
	}
	else if(obj.type=="radio")
	{
		if(obj.checked){ycount=4;}
		Controltype = "radio";
		fifthData = indexPadding.midData(fourthData,obj.defaultValue);
	}
	//指标重置
	publicFunction.indexDivReset(5);
	
	if(ChartType=="动态地图")
	{
		Controltype = "radio";
	}
	if(fifthData!=undefined)
	{
		document.getElementById("fifth-lable").style.display = "block";$("#fifth-Index").html("");
		for(var i=0,l=fifthData.length;i<l;i++)
		{
			fifthIndex += "<td><input name='fifthIndex' type='"+Controltype+"' value='"+fifthData[i].columnLabel+"' onclick='javascript:indexPadding.sixthIndexSelect(this)'/>"+fifthData[i].columnLabel+"</td>";
		}
		fifthIndex += "</tr>";
		//ycount=5;
		$("#fifth-Index").html(fifthIndex);
	}
});

/*
 * 六级指标的构建
 * 
 */
indexPadding.sixthIndexSelect = (function(obj) {
	if(obj,checked){ycount=5;}
});

/*
 * 指标数据的构建（根据上一级指标选择，获取下一级指标的数据源）
 * 
 * @data
 * @name
 */
indexPadding.midData = (function(data,name) {
	var tempData;
	for(var i=0,l=data.length;i<l;i++)
	{
		if(data[i].columnLabel==name)
		{
			for(var a=0;a<data[i].children.length;a++){
				if(data[i].children[a].children&&data[i].children[a].children[0]!=undefined){
					tempData = data[i].children;
				}
				else{
					tempData=undefined;
				}
			}
		}
	}
	return tempData;

});
/*
 * x指标的选择数据
 */
indexPadding.xmidData=(function (data,name,columnName)
{
	var xtempData=
	{
		tempobj:new Array([]),
		tempdata:[]
	};
	var datasource=data;

	for(var i=0;i<datasource.length;i++)
	{
		if(datasource[i][columnName]==name)
		{		
			if(datasource[i].children)
			{			
						xtempData.tempobj=datasource[i].children;
						for(var j=0;j<xtempData.tempobj.length;j++)
						{
							xtempData.tempdata.push(xtempData.tempobj[j][columnName]);
						}
				
			}
			else
			{
				xtempData.tempdata=undefined;
			}
			
		}
	}
	return xtempData;
});
	/*
	 * $.each(data,function(item, content)
	{	
		var _array=new Array([]);
		console.log("所有一级指标"+item[columnName]);
				//_array.push(item.children[columnName]);
				item.children.forEach(function(item1)
				{
					while(item[columnName]==_name)
						{
						return item1[columnName];
						//console.log("所有一级指标"+item[columnName]);
						}
					
				});
				//console.log("选中的儿子指标"+item.children.forEach(function(item1)
			
	});
	return _array;
});*/


/*
 * 计算checkbox控件选中个数
 * 
 * @arrayIndex
 */
indexPadding.checkedIndexNumber = (function(arrayIndex) {
	var checkedNumber=0;
	for(var i=0,l=arrayIndex.length;i<l;i++)
	{
		if(arrayIndex[i].checked)
		{
			checkedNumber += 1;
		}
	}
	return checkedNumber;
});

//这个函数得到firstindexselect的数组
indexPadding.getxFirstIndex=(function(dataRecords,columnName)
{
	ColumnName=columnName;;
	var _array = new Array([],[],[],[],[]);
	if (!Array.prototype.forEach) {  
	    Array.prototype.forEach = function(callback, thisArg) {  
	        var T, k;  
	        if (this == null) {  
	            throw new TypeError(" this is null or not defined");  
	        }  
	        var O = Object(this);  
	        var len = O.length >>> 0; // Hack to convert O.length to a UInt32  
	        if ({}.toString.call(callback) != "[object Function]") {  
	            throw new TypeError(callback + " is not a function");  
	        }  
	        if (thisArg) {  
	            T = thisArg;  
	        }  
	        k = 0;  
	        while (k < len) {  
	            var kValue;  
	            if (k in O) {  
	                kValue = O[k];  
	                callback.call(T, kValue, k, O);  
	            }  
	            k++;  
	        }  
	    };  
	}  
	else{
		dataRecords.forEach(function (item) 
			{
			  _array[0].push(item[ColumnName]);
			  if (item.children) 
			  {
			    item.children.forEach(function (item1) 
			    {
			    	_array[1].push(item[ColumnName]);
			      if (item1.children) 
			      {
			        item1.children.forEach(function (item2) 
			        {
			        	_array[2].push(item[ColumnName]);
			        	if(item2.children)
			        	{
			        		item2.children.forEach(function(item3)
			        		{
			        			_array[3].push(item[ColumnName]);
			        			if(item3.children)
			        			{
			        				item3.children.forEach(function(item4)
			        				{
			        					_array[4].push(item[ColumnName]);
			        					if(item4.children)
					        			{
					        				alert("指标级数超过5层，请联系开发人员修改");
					        				location.reload();
					        			}
			        				});
			        				
			        			}
			        			
			        		});
			        	}
			        });
			      }
			    });
			  }
			});
	}
	return _array;	
});

//全选checkbox事件
function yselectall(obj){  
    var items = document.getElementsByName("firstIndex");
    for(var i=0;i<items.length;i++){  
        if(items[i].type=="checkbox"&& items[i].name=="firstIndex")  
            items[i].checked = obj.checked;  
    	}    
	for(var j=0;j<items.length;j++){
		indexPadding.secondIndexSelect(items[j]);
	}
} 
function xselectall(obj){  
	    var items = document.getElementsByName("xfirstIndex");
	    for(var i=0;i<items.length;i++){  
	        if(items[i].type=="checkbox"&& items[i].name=="xfirstIndex")  
	            items[i].checked = obj.checked;  
	    	}    
		for(var j=0;j<items.length;j++){
			indexPadding.xsecondIndexSelect(items[j]);
		}
	} 
