		
var barDataAndDOM = {};

/*
 * 多维柱状图指标数据的获取与构建
 * 
 * @NR				表头行数
 * @IndexControl	指标控件
 */
barDataAndDOM.getMultilBarIndexData = (function(NR,IndexControl) {
	var firstIndex = "";
	var secondIndex = "";
	var thirdIndex = "";
	var fourthIndex = "";
	var fifthIndex = "";
	var result={first:[],second:[],third:[],fourth:[],fifth:[],item:0,y:[]};
	switch(NR)
	{
		case 1 : firstIndex = indexStructure.getIndexValue(IndexControl[0]);
				 break;
		case 2 : firstIndex = indexStructure.getIndexValue(IndexControl[0]);
				 secondIndex = indexStructure.getIndexValue(IndexControl[1]);
				 break;
		case 3 : firstIndex = indexStructure.getIndexValue(IndexControl[0]);
				 secondIndex = indexStructure.getIndexValue(IndexControl[1]);
				 thirdIndex = indexStructure.getIndexValue(IndexControl[2]);
		 		 break;
		case 4 : firstIndex = indexStructure.getIndexValue(IndexControl[0]);
		 		 secondIndex = indexStructure.getIndexValue(IndexControl[1]);
		 		 thirdIndex = indexStructure.getIndexValue(IndexControl[2]);
		 		 fourthIndex = indexStructure.getIndexValue(IndexControl[3]);
		 		 break;
		case 5 : firstIndex = indexStructure.getIndexValue(IndexControl[0]);
 		 		 secondIndex = indexStructure.getIndexValue(IndexControl[1]);
 		 		 thirdIndex = indexStructure.getIndexValue(IndexControl[2]);
 		 		 fourthIndex = indexStructure.getIndexValue(IndexControl[3]);
				 fifthIndex = indexStructure.getIndexValue(IndexControl[4]);
				 break;
	}
	if(firstIndex!=undefined&&firstIndex!=""){
		if(firstIndex.split(',').length>1)
		{
			result.item=1;
			result.y.push(1);
			for(var i=0,l=firstIndex.split(',').length;i<l;i++)
			{
				result.first.push(firstIndex.split(',')[i]);
				result.y.push(firstIndex.split(',')[i]);
			}
		}
		else 
			{
			result.item=1;
			result.first.push(firstIndex.split(',')[0]);
			}
	}
	
	if(secondIndex!=undefined&&secondIndex!=""){
		if(secondIndex.split(',').length>1)
		{
			result.item=2;
			result.y.push(2);
			for(var i=0,l=secondIndex.split(',').length;i<l;i++)
			{	
				result.second.push(secondIndex.split(',')[i]);
				result.y.push(secondIndex.split(',')[i]);
			}
		}
		else 
		{
		result.item=2;
		result.second.push(secondIndex.split(',')[0]);
		}
	}
	
	if(thirdIndex!=undefined&&thirdIndex!=""){
		if(thirdIndex.split(',').length>1)
		{
			result.item=3;
			result.y.push(3);
			for(var i=0,l=thirdIndex.split(',').length;i<l;i++)
			{
				result.third.push(thirdIndex.split(',')[i]);
				result.y.push(thirdIndex.split(',')[i]);
			}
		}
		else 
		{
		result.item=3;
		result.third.push(thirdIndex.split(',')[0]);
		}
	}
	
	if(fourthIndex!=undefined&&fourthIndex!="")
	{
		if(fourthIndex.split(',').length>1)
		{
			result.item=4;
			result.y.push(4);
			for(var i=0,l=fourthIndex.split(',').length;i<l;i++)
			{
				result.fourth.push(fourthIndex.split(',')[i]);
				result.y.push(fourthIndex.split(',')[i]);
			}
		}
		else 
		{
		result.item=4;
		result.fourth.push(fourthIndex.split(',')[0]);
		}
	}
	if(fifthIndex!=undefined&&fifthIndex!=""){
		if(fifthIndex.split(',').length>1)
		{
			result.item=5;
			result.y.push(5);
			for(var i=0,l=fifthIndex.split(',').length;i<l;i++)
			{
				result.fifth.push(fifthIndex.split(',')[i]);
				result.y.push(fifthIndex.split(',')[i]);
			}
		}
		else 
		{
		result.item=5;
		result.fifth.push(fifthIndex.split(',')[0]);
		}
	}
	if(fifthIndex!=undefined&&fourthIndex!=undefined&&thirdIndex!=undefined&&secondIndex!=undefined&&firstIndex!=undefined){
		if(firstIndex.split(',').length==1&&secondIndex.split(',').length==1&&thirdIndex.split(',').length==1&&fourthIndex.split(',').length==1&&fifthIndex.split(',').length==1)
		{
			result.y.push(1);
			result.y.push(firstIndex.split(',')[0]);
		}
	}
	return result;
});


/*
 * 柱状图数据的获取和构建
 * 
 * @DataRegion  	图表绘制的地区参数
 * @HeaderData		数据表头的数据信息
 * @RecordsData 	数据体的详细数据
 * @indexResult		指标选择结果
 * @BarSerise		所选指标下的数据
 * @indexResult		指标选择结果
 */
barDataAndDOM.barData = (function(DataRegion,HeaderData,RecordsData,BarSerise,xBarSerise,indexResult) {
	var obj={result:[],unit:[],xindexResult:[],yindexResult:[],value:[]};
	var legend = [];
	var F = [];
	var ColumnName=HeaderData[0].columnName;
	for(var i=1,l=BarSerise.y.length;i<l;i++)
	{
		obj.result.push([]);
		obj.yindexResult.push(BarSerise.y[i]);
	}
	for(var k=1;k<BarSerise.y.length;k++)
	{

		indexResult.y[BarSerise.y[0]-1] = BarSerise.y[k];
		F[k-1] = chartsPublicFunction.getIndexColumnName(HeaderData,indexResult.y,0);
		obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, indexResult.y, 0));
		if(DataRegion == "指标"||DataRegion=="区域"||DataRegion=="时间"||DataRegion=="地市")
		{
			for(var i=0;i<RecordsData.length;i++)
			{
				//指标层数
				switch(xBarSerise.item)
				{
				case 1:
					//for(var i=0;i<xBarSerise.first.length;i++){obj.CSVData.push([]);}
						for(var j=0;j<xBarSerise.first.length;j++)
						{
							if(RecordsData[i][ColumnName]==xBarSerise.first[j])
							{
								obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F[k-1]+'']});
							}
						}
					break;
				case 2:
						if(xBarSerise.first.length>1)
						{
							for(var j=0;j<xBarSerise.first.length;j++)
							{
								if(RecordsData[i][ColumnName]==xBarSerise.first[j])
								{
									if(RecordsData[i].children)
									{
										for(var l=0;l<RecordsData[i].children.length;l++)
										{
											for(var m=0;m<xBarSerise.second.length;m++)
											{
												if(RecordsData[i].children[l][ColumnName]==xBarSerise.second[m])
												{													
													obj.result[k-1].push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F[k-1]+'']});
												}
											}
										}
									}
									else
									{
									    var info="选择的"+RecordsData[i][ColumnName]+" 指标没有子集目录！结果可能有误！";
									    alert (info);  
										obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F[k-1]+'']});
									}
								}
							}
						}
						else
						{
							if(RecordsData[i][ColumnName]==xBarSerise.first[0])
							{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xBarSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xBarSerise.second[m])
											{
												obj.result[k-1].push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F[k-1]+'']});
											}
										}	
									}
							}
						}						
				break;	
				case 3:
					if(xBarSerise.first.length>1)
					{
						for(var j=0;j<xBarSerise.first.length;j++)
						{
							if(RecordsData[i][ColumnName]==xBarSerise.first[j])
							{
								if(RecordsData[i].children)
								{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xBarSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xBarSerise.second[m])
											{
												if(RecordsData[i].children[l].children)
												{
													for(var o=0;o<RecordsData[i].children[l].children.length;o++)
													{
														for(var p=0;p<xBarSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xBarSerise.third[p])
															{									
																obj.result[k-1].push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o][''+F[k-1]+'']});
															}
														}
															
													}
												}
												else
												{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													obj.result[k-1].push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F[k-1]+'']});
												}
											}
										}
									}
								}
								else
								{
									 var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
									    alert (info);  
									obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F[k-1]+'']});
								}
							}
						}
					}
					else	
					{
							if(RecordsData[i][ColumnName]==xBarSerise.first[0])
							{
								
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xBarSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xBarSerise.second[m])
											{
										
												if(RecordsData[i].children[l].children){
													for(var o=0;o<RecordsData[i].children[l].children.length;o++)
													{
														for(var p=0;p<xBarSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xBarSerise.third[p])
															{																
																obj.result[k-1].push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o][''+F[k-1]+'']});
															}
														}
													}
												}
												else
													{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													obj.result[k-1].push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F[k-1]+'']});
													}							
											}
										}
									}
							}
					}

					break;
				case 4:
					//如果没有第四层就不要用，一般不会到第四层，万一有第四层，取消注释和删除alert即可。
					if(xBarSerise.first.length>1)
					{
						for(var j=0;j<xBarSerise.first.length;j++)
						{
							if(RecordsData[i][ColumnName]==xBarSerise.first[j])
							{
								if(RecordsData[i].children)
								{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xBarSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xBarSerise.second[m])
											{
												if(RecordsData[i].children[l].children)
												{
													for(var o=0;o<RecordsData[i].children[l].length;o++)
													{
														for(var p=0;p<xBarSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xBarSerise.third[p])
															{
																if(RecordsData[i].children[l].children[o].children)
																{
																	for(var y=0;y<RecordsData[i].children[l].children[o].children.length;y++)
																	{
																		for(var t=0;t<xBarSerise.fourth.length;t++)
																		{
																			if(RecordsData[i].children[l].children[o].children[y][ColumnName]==xBarSerise.fourth[t])
																			{
																				obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F[k-1]+'']});
																			}
																		}
																	}			
																}
																else
																{
																	 var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
																	    alert (info);  
																	obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o][''+F[k-1]+'']});
																}
															}
														}
													}
												}
												else
												{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l][''+F[k-1]+'']});
												}
											}										    
										}
									}
								}
								else
								{
									   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
									    alert (info);  
									obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F[k-1]+'']});
								}
							}
						}
					}
					else
					{
							if(RecordsData[i][ColumnName]==xBarSerise.first[0])
							{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xBarSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xBarSerise.second[m])
											{
												if(RecordsData[i].children[l].children)
													{
													for(var o=0;o<RecordsData[i].children[l].children.length;o++)
													{
														for(var p=0;p<xBarSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xBarSerise.third[p])
															{
																if(RecordsData[i].children[l].children[o].children)
																	{
																	for(var y=0;y<RecordsData[i].children[l].children[o].children.length;y++)
																	{
																		for(var t=0;t<xBarSerise.fourth.length;t++)
																		{
																			if(RecordsData[i].children[l].children[o].children[y][ColumnName]==xBarSerise.fourth[t])
																			{
																				
																				obj.result[k-1].push({name:RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F[k-1]+'']});
																			}
																		}
																	}
																	}
																else
																	{
																	   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
																	    alert (info);  
																	obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o][''+F[k-1]+'']});
																	}
															}
														}
													}
													}
												else
													{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													obj.result[k-1].push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l][''+F[k-1]+'']});
													}
											}
										}
									}
							}
						}	
					
					
					//alert("表格数据超过三级！请联系开发人员！");
					//location.reload();
					break;
				case 5:
					alert("你选择了五层！开发人员还未开发");
					location.reload() ;
				}
			}
		}
	
		
		else
		{
			for(var i=0,parentLength=RecordsData.length;i<parentLength;i++)
			{
				if(RecordsData[i].F1 == DataRegion)
				{
					for(var j=0,childrenLength=RecordsData[i].children.length;j<childrenLength;j++)
					{
						result[k-1].push({name:RecordsData[i].children[j].F1,value:RecordsData[i].children[j][''+F[k-1]+'']});
					}
				}
			}
		}
	}
	return obj;
});

/*
 * 获取并构建图例数据
 * 
 * @Parameter	Bar图表serise下data参数数据
 * @DataLength	xAxis轴长度
 */
barDataAndDOM.setLegend = (function(Parameter,DataLength) {
	var result =[];
	var temp = [];
	for(var i=0;i<DataLength;i++)
	{
		for(var j=0,l1=Parameter[i].length;j<l1;j++)
		{
			temp.push(Parameter[i][j].name);
		}
		result.push(temp);
		temp=[];
	}
	return result;
});

/*
 * 获取并构建柱状图的Option
 * 
 * @BarSerise	所选指标下的数据
 * @BarData		Bar的DOM的data参数数据
 * @ChartType	图表类型
 * @LegendData	Bar的xAxis下data参数数据
 * @title		Bar图表的title参数数据
 */
barDataAndDOM.setBarOption = (function(BarSerise,BarData,ChartType,LegendData,title,unit) {
	var OptionSerise = [];
	var legend_data = [];
	var zrColor;
	var colorList;
	var itemStyle;
	/* 可以做出彩虹样式，不过颜色有限，不推荐使用。
	require(['Echartsbase/Echart2.2.7/zrender/tool/color'], function(){
		 zrColor = require('Echartsbase/Echart2.2.7/zrender/tool/color');
		 colorList = [
		  '#ff7f50','#87cefa','#da70d6','#32cd32','#6495ed',
		  '#ff69b4','#ba55d3','#cd5c5c','#ffa500','#40e0d0'];
		 itemStyle = {
				    normal: {
				        color: function(params) {
				          if (params.dataIndex < 0) {
				            // for legend
				            return zrColor.lift(
				              colorList[colorList.length - 1], params.seriesIndex * 0.1
				            );
				          }
				          else {
				            // for bar
				            return zrColor.lift(
				              colorList[params.dataIndex], params.seriesIndex * 0.1
				            );
				          }
				        }
				    }
				};
	}
	);*/
	for(var j=1,legendLength=BarSerise.length;j<legendLength;j++)
	{
		legend_data.push(BarSerise[j]);
	}
	for(var i=0,l=(BarSerise.length-1);i<l;i++)
	{
		if(ChartType == "簇状柱状图")
		{
			OptionSerise.push({
				name:BarSerise[i+1],
				type:'bar',
				label: barDataAndDOM.setLable(ChartType),
				//stack:1,
				//selectedMode : 'single',
				//itemStyle:itemStyle,
				data:BarData[i]
			});
		}
		else if(ChartType == "堆积柱状图")
		{
			OptionSerise.push({
				name:BarSerise[i+1],
				type:'bar',
				label: barDataAndDOM.setLable(ChartType),
				stack:1,
				selectedMode : 'single',
				data:BarData[i]
			});
		}
	}
	/*获得单位
	var getxAxisName=function(data)
	{
		if(ChartType=="静态地图"||ChartType=="动态地图")
		{
			return "年份";
		}
		else
		{
			return indexPadding.getUnit(data);
		}
			
	};*/
	var optionsStrusture = {
			title:{
				x:'center',
				text:title,
				//subtext:'单位：万吨'
			},
			tooltip : {'trigger':'item'},
			legend : {
				show:true,
				orient:'vertical',
				x:'right',		
				y:'center',
				z:5,
				padding:10,
				itemGap:5,
				itemWidth:25,
				itemHeight:18,
				selectedMode:'multiple',
				textStyle:{color:'#000',fontSize:13},
				data:legend_data
			},
		    toolbox: {
		        show : true,
		        feature : {
		            mark : {show: true},
		            dataView : {show: true, readOnly: true},
		            magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
		            restore : {show: true},
		            saveAsImage : {show: true}
		        }
		    },
			grid: {
		        left: '3%',
		        right: '15%',
		        bottom: '3%',
		        containLabel: true
		    },
		    xAxis : [
		        {
		            name:'指标',
					type : 'category',
					axisLabel:{interval:0},
					data:LegendData[0]
		        }
		    ],
		    yAxis : [
		        {
		        	name:unit,
		            type : 'value',
		           // scale:true
		        }
		    ],
			/*xAxis : {
				name:'地区',
				type : 'category',
				axisLabel:{interval:0},
				data:LegendData[0]
			},*/
			//yAxis:{type : 'value',scale:true},
			//grid:{x2:'15%'},
			
			//数据系列
			series : OptionSerise,
			
	};
	return optionsStrusture;
});

/*
 * 柱状图图上文本标签设置
 * 
 * @ChartType	图表类型
 */
barDataAndDOM.setLable = (function(ChartType){
	if(ChartType == "簇状柱状图")
	{
		return {
			normal:{
				show: true,
				formatter: '{c}',
		        position: 'top',
		        textStyle:{
		        	color: "#000",
		        },
			}
		};
	}
	else if(ChartType == "堆积柱状图")
	{
		return {
			normal:{
				show: true,
				formatter: '{c}',
		        position: 'inside',
		        textStyle:{
		        	color: "#000",
		        },
			}
		};
	}
});

/*
 * 柱状图的绘制
 * 
 * @HeaderData			数据表头的数据信息
 * @RecordsData 		数据体的详细数据
 * @ChartType			图表类型
 * @optionsStrusture
 * @LegendData			
 * @indexParam			指标参数
 * IndexControl			
 * @SubjectTitle		专题名称
 */
barDataAndDOM.barDrawing = (function(HeaderData,RecordsData,ChartType,optionsStrusture,LegendData,indexParam,IndexControl,SubjectTitle) {
	//获取DOM容器，初始化echarts实例
	var myChart = echarts.init(document.getElementById('EchartBox'));
	
	//图表的装载
	myChart.setOption(optionsStrusture);
	
	//图表单击事件
	myChart.on("click",function(param){
	/*	var selected = param.name;
		var select_name = "";
		var DataRegion = "全省";
		var name=['珠江三角洲地区','粤东地区','粤西地区','粤北地区'];
		for(var i=0;i<name.length;i++){
			if(selected == name[i]){
				select_name = name[i];
			}
		}
		if(select_name=='珠江三角洲地区'||select_name=='粤东地区'||select_name=='粤西地区'||select_name=='粤北地区')
		{
			DataRegion = select_name;
			var Parameter = [];
			//表头行数
			var ColumnName=HeaderData[0].columnName;
			var xarray=indexPadding.getxFirstIndex(RecordsData,ColumnName);
			//var xNumberofROW=publicFunction.getXlevel(xarray);
			var xNumberofROW=xcount;
			//var NumberofROW = tableProperty.rowsCalculate(HeaderData)+1;
			var NumberofROW=ycount;
			var BarSerise = barDataAndDOM.getMultilBarIndexData(NumberofROW,IndexControl);
			var xBarSerise=barDataAndDOM.getMultilBarIndexData(NumberofROW,IndexControl);
			Parameter = barDataAndDOM.barData(DataRegion,HeaderData,RecordsData,BarSerise,xBarSerise,indexParam).result;
			var Unit= barDataAndDOM.barData(DataRegion,HeaderData,RecordsData,BarSerise,xBarSerise,indexParam).unit;
			var LegendData  = barDataAndDOM.setLegend(Parameter,(BarSerise.y.length-1));
			var optionsStrusture = barDataAndDOM.setBarOption(BarSerise.y,Parameter,ChartType,LegendData,SubjectTitle,Unit);
			barDataAndDOM.barDrawing(HeaderData,RecordsData,ChartType,optionsStrusture,LegendData,indexParam,IndexControl,SubjectTitle);
		}*/
	});
});




