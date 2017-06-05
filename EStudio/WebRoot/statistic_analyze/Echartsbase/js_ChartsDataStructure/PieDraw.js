
var pieDataAndDOM = {};

/*
 * 获取X参数
*/
pieDataAndDOM.getMultilPieIndexData=(function(NR,IndexControl){
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
			//result.item=1;
			result.y.push(1);
			//result.first.push(firstIndex.split(',')[0]);
			result.y.push(firstIndex.split(',')[0]);
		}
	}
	return result;
});






/*
 * 饼状图数据的获取和构建
 * 
 * @DataRegion  	图表绘制的地区参数
 * @HeaderData		数据表头的数据信息
 * @RecordsData 	数据体的详细数据
 * @IndexControl	指标控件
 */
pieDataAndDOM.pieData = (function(DataRegion,HeaderData,RecordsData,IndexControl,xPieSerise) {
	var seriseData = [];
	var legend = [];
	var F;
	var obj={result:[],unit:[],yindexResult:[],xindexResult:[]};
	var UnitColumnName=HeaderData[1].columnName;
	var ColumnName=HeaderData[0].columnName;
	//获取指标所在的列编号，如F2
	obj.yindexResult.push(IndexControl.y[0]);
	F = chartsPublicFunction.getIndexColumnName(HeaderData,IndexControl.y,0);
	if(DataRegion == "指标"||DataRegion=="区域"||DataRegion=="时间"||DataRegion=="地市")
	{
			for(var i=0;i<RecordsData.length;i++)
			{
				switch(xPieSerise.item)
				{
				case 1:
						for(var j=0;j<xPieSerise.first.length;j++)
						{
							if(RecordsData[i][ColumnName]==xPieSerise.first[j])
							{
								seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
								obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
							    legend.push(RecordsData[i][ColumnName]);
								obj.unit.push(RecordsData[i][UnitColumnName]);
							}
						}
					
					break;
				case 2:
						if(xPieSerise.first.length>1)
						{
							for(var j=0;j<xPieSerise.first.length;j++)
							{
								if(RecordsData[i][ColumnName]==xPieSerise.first[j])
								{
									if(RecordsData[i].children)
									{
										for(var l=0;l<RecordsData[i].children.length;l++)
										{
											for(var m=0;m<xPieSerise.second.length;m++)
											{
												if(RecordsData[i].children[l][ColumnName]==xPieSerise.second[m])
												{													
													seriseData.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
												    legend.push(RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]);
													obj.unit.push(RecordsData[i].children[l][UnitColumnName]);
													obj.xindexResult.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
												}
											}
										}
									}
									else
									{
									    var info="选择的"+RecordsData[i][ColumnName]+" 指标没有子集目录！结果可能有误！";
									    alert (info);  
									    legend.push(RecordsData[i][ColumnName]);
									    seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
									    obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
									}
								}
							}
						}
						else
						{
							if(RecordsData[i][ColumnName]==xPieSerise.first[0])
							{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xPieSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xPieSerise.second[m])
											{
												obj.unit.push(RecordsData[i].children[l][UnitColumnName]);
											    legend.push(RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]);
												seriseData.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
												obj.xindexResult.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
											}
										}	
									}
							}
						}						
				break;	
				case 3:
					if(xPieSerise.first.length>1)
					{
						for(var j=0;j<xPieSerise.first.length;j++)
						{
							if(RecordsData[i][ColumnName]==xPieSerise.first[j])
							{
								if(RecordsData[i].children)
								{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xPieSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xPieSerise.second[m])
											{
												if(RecordsData[i].children[l].children)
												{
													for(var o=0;o<RecordsData[i].children[l].children.length;o++)
													{
														for(var p=0;p<xPieSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xPieSerise.third[p])
															{
																obj.unit.push(RecordsData[i].children[l].children[o][UnitColumnName]);
																legend.push(RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName]);
																seriseData.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																obj.xindexResult.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
															}
														}
															
													}
												}
												else
												{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													    legend.push(RecordsData[i][ColumnName]);
													    seriseData.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
													    obj.xindexResult.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
												}
											}
										}
									}
								}
								else
								{
									 var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
									    alert (info);  
									    legend.push(RecordsData[i][ColumnName]);
									    seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
									    obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
								}
							}
						}
					}
					else	
					{
							if(RecordsData[i][ColumnName]==xPieSerise.first[0])
							{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xPieSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xPieSerise.second[m])
											{
										
												if(RecordsData[i].children[l].children){
													for(var o=0;o<RecordsData[i].children[l].children.length;o++)
													{
														for(var p=0;p<xPieSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xPieSerise.third[p])
															{
																obj.unit.push(RecordsData[i].children[l].children[o][UnitColumnName]);
																if(xPieSerise.second.length>1)
																	{
																	 legend.push(RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName]);
																	seriseData.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																	obj.xindexResult.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																	}
																else
																	{
																	seriseData.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																	obj.xindexResult.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
																	 legend.push(RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]+"\r\n"+RecordsData[i].children[l].children[o][ColumnName]);
																	}
																	
															}
														}
													}
												}
												else
													{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													    legend.push(RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName]);
													    seriseData.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
													    obj.xindexResult.push({name:RecordsData[i][ColumnName]+"\r\n"+RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l][''+F+'']});
													}							
											}
										}
									}
							}
					}
					break;
				case 4:
				//如果没有第四层就不要用，一般不会到第四层，万一有第四层，取消注释和删除alert即可
				 
				 	if(xPieSerise.first.length>1)
					{
						for(var j=0;j<xPieSerise.first.length;j++)
						{
							if(RecordsData[i][ColumnName]==xPieSerise.first[j])
							{
								if(RecordsData[i].children)
								{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xPieSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xPieSerise.second[m])
											{
												if(RecordsData[i].children[l].children)
												{
													for(var o=0;o<RecordsData[i].children[l].length;o++)
													{
														for(var p=0;p<xPieSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xPieSerise.third[p])
															{
																if(RecordsData[i].children[l].children[o].children)
																{
																	for(var y=0;y<RecordsData[i].children[l].children[o].children.length;y++)
																	{
																		for(var t=0;t<xPieSerise.fourth.length;t++)
																		{
																			if(RecordsData[i].children[l].children[o].children[y][ColumnName]==xPieSerise.fourth[t])
																			{
																				obj.unit.push(RecordsData[i].children[l].children[o].children[y][UnitColumnName]);
																				legend.push(RecordsData[i][ColumnName]);
																				seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});
																				obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});
																			}
																		}
																	}			
																}
																else
																{
																	 var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
																	    alert (info);  
																	    legend.push(RecordsData[i][ColumnName]);
																	    seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																	    obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																}
															}
														}
													}
												}
												else
												{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													    seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l][''+F+'']});
													    obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l][''+F+'']});
												}
											}										    
										}
									}
								}
								else
								{
									   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
									    alert (info);  
									    seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
									    obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
								}
							}
						}
					}
					else
					{
							if(RecordsData[i][ColumnName]==xPieSerise.first[0])
							{
									for(var l=0;l<RecordsData[i].children.length;l++)
									{
										for(var m=0;m<xPieSerise.second.length;m++)
										{
											if(RecordsData[i].children[l][ColumnName]==xPieSerise.second[m])
											{
												if(RecordsData[i].children[l].children)
													{
													for(var o=0;o<RecordsData[i].children[l].children.length;o++)
													{
														for(var p=0;p<xPieSerise.third.length;p++)
														{
															if(RecordsData[i].children[l].children[o][ColumnName]==xPieSerise.third[p])
															{
																if(RecordsData[i].children[l].children[o].children)
																	{
																	for(var y=0;y<RecordsData[i].children[l].children[o].children.length;y++)
																	{
																		for(var t=0;t<xPieSerise.fourth.length;t++)
																		{
																			if(RecordsData[i].children[l].children[o].children[y][ColumnName]==xPieSerise.fourth[t])
																			{
																				obj.unit.push(RecordsData[i].children[l].children[o].children[y][UnitColumnName]);
																				if(xPieSerise.second.length>1)
																				{
																					legend.push(RecordsData[i].children[l][ColumnName]);
																					seriseData.push({name:RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});
																					obj.xindexResult.push({name:RecordsData[i].children[l][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});}
																				if(xPieSerise.third.length>1)
																				{
																					legend.push(RecordsData[i].children[l].children[o][ColumnName]);
																					seriseData.push({name:RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});
																					obj.xindexResult.push({name:RecordsData[i].children[l].children[o][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});}
																				if(xPieSerise.fourth.length>1)
																				{
																					legend.push(RecordsData[i].children[l].children[o].children[y][ColumnName]);
																					seriseData.push({name:RecordsData[i].children[l].children[o].children[y][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});
																					obj.xindexResult.push({name:RecordsData[i].children[l].children[o].children[y][ColumnName],value:RecordsData[i].children[l].children[o].children[y][''+F+'']});}
																				
																			}
																		}
																	}
																	}
																else
																	{
																	   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
																	    alert (info);  
																	    legend.push(RecordsData[i].children[l][ColumnName]);
																	    seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																	    obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l].children[o][''+F+'']});
																	}
															}
														}
													}
													}
												else
													{
													   var info="选择的"+RecordsData[i][ColumnName]+" 指标没有相同子集目录！结果可能有误！";
													    alert (info);  
													    legend.push(RecordsData[i].children[l][ColumnName]);
													    obj.xindexResult.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l][''+F+'']});
													seriseData.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[l][''+F+'']});
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
					location.reload();
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
					seriseData.push({name:RecordsData[i].children[j].F1,value:RecordsData[i].children[j][''+F+'']});
					legend.push(RecordsData[i].children[j].F1);
					obj.xindexResult.push({name:RecordsData[i].children[j].F1,value:RecordsData[i].children[j][''+F+'']});
				}
			}
		}
	}
	obj.result.push(seriseData);
	obj.result.push(legend);
	return obj; 
});

/*
 * 获取并构建饼状图的Option
 * 
 * @ChartParameter	Pie图表serise下data参数数据
 * @ChartType		图表类型
 * @SubjectTitle	Pie图表的title参数数据
 */
pieDataAndDOM.setPieOption = (function(ChartParameter,ChartType,SubjectTitle) {
	var radiusDegrees;
	if(ChartType == "二维饼状图")
	{
		radiusDegrees = ['0%', '45%'];
	}
	if(ChartType == "圆环饼状图")
	{
		radiusDegrees = ['20%', '45%'];
	}
	var optionsStrusture = {
			title:{
				x:'center',
				text:SubjectTitle,
				//subtext:'单位：万吨'
			},
			//气泡提示框
			tooltip:{
				trigger: 'item',
				formatter: "{a} <br/>{b} : {c} ({d}%)"
			},
			toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
			//图例
			legend:{
				orient:'vertical',
				x:'left',
				y:'15%',
				z:5,
				padding:10,
				itemGap:10,
				itemWidth:20,
				itemHeight:20,
				selectedMode:'multiple',
				textStyle:{color:'#000',fontSize:13},
				data:ChartParameter[1],
			},
			//拖拽重计算特性
			//calculable : true,					
			//数据系列
			series : [	
				{
					name:'',
					type:'pie',
					center: ['50%', '55%'],
					radius: radiusDegrees,
					//itemStyle:itemStyle0(),
					selectedMode : 'single',
					data:ChartParameter[0]
				}
			]
		
		};
	return optionsStrusture;
});

/*
 * 饼状图的绘制
 * 
 * @optionsStrusture	Pie图表Option参数数据
 * @ChartParameter		Pie图表serise下data参数数据
 * @ChartType			图表类型
 * @HeaderData			数据表头的数据信息
 * @RecordsData 		数据体的详细数据
 * @indexParam			指标参数
 * @SubjectTitle		专题名称
 */
pieDataAndDOM.pieDrawing = (function(optionsStrusture,ChartParameter,ChartType,HeaderData,RecordsData,indexParam,SubjectTitle) {
	//获取DOM容器，初始化echarts实例
	var myChart = echarts.init(document.getElementById('EchartBox'));
	
	//图表的装载
	myChart.setOption(optionsStrusture);
	
	/*//图表单击时间
	myChart.on("click",function(param){
		var selected = param.name;
		var select_name = "";
		var DataRegion = "全省";
		var name=ChartParameter[1];
		for(var i=0;i<name.length;i++){
			if(selected == name[i]){
				select_name = name[i];
			}
		}
		if(select_name=='珠江三角洲地区'||select_name=='粤东地区'||select_name=='粤西地区'||select_name=='粤北地区')
		{
			DataRegion = select_name;
		}
		else
		{
			DataRegion = "全省";
		}
		var Parameter = [];
		var Unit=pieDataAndDOM.pieData(DataRegion,HeaderData,RecordsData,indexParam).unit;
		var xPieSerise=pieDataAndDOM.getMultilPieIndexData(xNumberofROW, xIndexControl);
		Parameter = pieDataAndDOM.pieData(DataRegion,HeaderData,RecordsData,indexParam,xPieSerise).result;
		var optionsStrusture = pieDataAndDOM.setPieOption(Parameter,ChartType,SubjectTitle,Unit);
		pieDataAndDOM.pieDrawing(optionsStrusture,Parameter,ChartType,HeaderData,RecordsData,indexParam,SubjectTitle);
	});*/
});






