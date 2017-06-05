
var dynamicMapDataAndDOM = {};
var myChart;
var echartsOption;

dynamicMapDataAndDOM.getxIndexData=(function(NR,IndexControl){
	var firstIndex = "";
	var secondIndex = "";
	var thirdIndex = "";
	var fourthIndex = "";
	var fifthIndex = "";
	var result={first:[],second:[],third:[],fourth:[],fifth:[],y:[],item:0};
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
		result.item=2;
		result.second.push(secondIndex.split(',')[0]);
		result.y.push(2);
		result.y.push(secondIndex.split(',')[0]);
	}
	
	if(thirdIndex!=undefined&&thirdIndex!=""){
		result.item=3;
		result.third.push(thirdIndex.split(',')[0]);
		result.y.push(3);
		result.y.push(thirdIndex.split(',')[0]);
	}	
	if(fourthIndex!=undefined&&fourthIndex!="")
	{
		result.item=4;
		result.fourth.push(fourthIndex.split(',')[0]);
		result.y.push(4);
		result.y.push(fourthIndex.split(',')[0]);
	}
	if(fifthIndex!=undefined&&fifthIndex!=""){
		result.item=5;
		result.y.push(5);
		result.fifth.push(fifthIndex.split(',')[0]);
		result.y.push(fifthIndex.split(',')[0]);
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
 * 时间轴控件数据构建
 * 
 * @IndexControl	指标控件
 */
dynamicMapDataAndDOM.setTimeData = (function(IndexControl) {
	var xfirstIndex = "";
	var xsecondIndex="";
	var result={firstSelected:[],secondSelected:[]};
	if(IndexControl[0]!=undefined&&IndexControl[0]!="")
	{	
		xfirstIndex = indexStructure.getIndexValue(IndexControl[0]);}
	if(IndexControl[1]!=undefined&&IndexControl[1]!="")
	{	xsecondIndex = indexStructure.getIndexValue(IndexControl[1]);}
	if(xfirstIndex!=undefined&&xfirstIndex!=""){
		for(var i=0,l=xfirstIndex.split(',').length;i<l;i++)
		{
			result.firstSelected.push(xfirstIndex.split(',')[i]);
		}
	}
	if(xsecondIndex!=undefined&&xsecondIndex!=""){
		result.secondSelected.push(xsecondIndex.split(',')[0]);
}
	return result;
});

/*
 * 静态地图数据的获取和构建
 * 
 * @GeoJson  		地图的GeoJson文件
 * @HeaderData		数据表头的数据信息
 * @RecordsData 	数据体的详细数据
 * @TimeData
 * @IndexControl	指标控件
 */
dynamicMapDataAndDOM.dynamicMapData = (function(GeoJson,HeaderData,RecordsData,TimeData,DynamicMapSerise,xDynamicMapSerise,IndexControl) {
	var obj={result:[],unit:[],yindexResult:[]};
	var ColumnName=HeaderData[0].columnName;
		var F ;
		for(var k=0;k<TimeData.firstSelected.length;k++)
		{
			obj.result.push([]);
			obj.yindexResult.push(TimeData.firstSelected[k]);
		}
		for(var k=0;k<TimeData.firstSelected.length;k++)
		{
			F= chartsPublicFunction.getIndexColumnName(HeaderData,IndexControl.y,0);	
			if(GeoJson == "guang_dong_geo_QY.json")
			{ 
				for(var l=0;l<RecordsData[k].children.length;l++)
				{
					
					obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));
					obj.result[k].push({name:RecordsData[k].children[l][ColumnName],value:RecordsData[k].children[l][''+F+'']});

				}
			}
			else if(GeoJson=="guang_dong_geo_ZSJDQ.json"||GeoJson=="guang_dong_geo_YDDQ.json"||GeoJson=="guang_dong_geo_YXDQ.json"||GeoJson=="guang_dong_geo_YBDQ.json")
			{
				for(var i=0;i<RecordsData.length;i++)
					{
							if(RecordsData[i][ColumnName]==TimeData.firstSelected[k])
							{					
								for(var j=0,childrenLength=RecordsData[i].children.length;j<childrenLength;j++)
								{
									obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));
									obj.result[k].push({name:RecordsData[i].children[j][ColumnName],value:RecordsData[i].children[j][''+F+'']});
								}
							}							
					}
			}
		}
//				if(RecordsData[0].children)
//				{
//					if(RecordsData[0].children[1].children)
//					{
//						var flag=0;
//						for(var i=0;i<RecordsData[0].children[1].children.length;i++)
//						{					
//							if(RecordsData[0].children[1].children[i].children)
//							{
//								flag=i;
//							}
//						}
//						if(flag!=0)
//						{
//							for(var i=0;i<RecordsData.length;i++)
//							{
//									if(RecordsData[i][ColumnName]==TimeData.firstSelected[k])
//									{					
//										for(var j=0,childrenLength=RecordsData[i].children.length;j<childrenLength;j++)
//										{
//											for(var l=0;l<flag;l++)
//											{
//												if(RecordsData[i].children[j].children[l][ColumnName]==TimeData.secondSelected[0])
//												{
//													obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));
//													obj.result[k].push({name:RecordsData[i].children[j][ColumnName],value:RecordsData[i].children[j].children[l][''+F+'']});
//												}
//											}
//										}
//									}
//							}
//						}	
//						else
//						{
//							if(RecordsData[0].children[0][ColumnName]=="珠江三角洲地区")
//							{
//								for(var i=0;i<RecordsData.length;i++)
//								{
//										if(RecordsData[i][ColumnName]==TimeData.firstSelected[k])
//										{					
//											for(var j=0,childrenLength=RecordsData[i].children.length;j<childrenLength;j++)
//											{
//												obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));
//												obj.result[k].push({name:RecordsData[i].children[j][ColumnName],value:RecordsData[i].children[j][''+F+'']});
//											}
//										}							
//								}
//							}						
//							else
//							{
//								for(var b=0;b<RecordsData[k].children.length;b++)
//								{
//									for(var a=0;a<RecordsData[k].children[b].children.length;a++)
//									{
//										if(RecordsData[k].children[b].children[a][ColumnName]==TimeData.secondSelected[0])
//										{
//											obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));
//											obj.result[k].push({name:RecordsData[k].children[b][ColumnName],value:RecordsData[k].children[b].children[a][''+F+'']});
//										}
//									}								
//								}
//							}
//						}
//					}
//					else 
//					{
//						for(var l=0;l<RecordsData[k].children.length;l++)
//						{
//							
//							obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));
//							obj.result[k].push({name:RecordsData[k].children[l][ColumnName],value:RecordsData[k].children[l][''+F+'']});
//
//						}
//						if(obj.result[0]==null)
//						{alert("请选择包含地市信息的数据!");
//						location.reload();}
//					}
//				}					
//			}
//			else
//			{alert("请选择包含地市信息的数据!");
//			location.reload();}
		
		return obj; 
});

/*
 * 静态地图数据的获取和构建
 * 
 * @TimeData  		
 * @MapData		
 * @SubjectTitle 	
 */
dynamicMapDataAndDOM.setDynamicMapOption = (function(TimeData,MapData,SubjectTitle,Unit) {
	var dataRange_maxmin = dynamicMapDataAndDOM.setDataRange(MapData,TimeData.length);;	
	var baseOptionSerises = [];
	for(var i=0,l=TimeData.length;i<l;i++)
	{
		baseOptionSerises.push({
			name:'',
			type:'map',	
			//mapLocation: {x:280,y:30,width:'78%',height:'88%'},		
			mapType:'GD',
			label: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
			selectedMode : 'single',
			left: '15%',
			right:'15%',
			top: '12%',
			bottom: '12%',
		});
	}
	var baseOption = {
			timeline: {
	            // y: 0,
	            axisType: 'category',
	            // realtime: false,
	            // loop: false,
	            autoPlay: false,
	            // currentIndex: 2,
	            playInterval: 3000,
	            data: TimeData,
	            label:{
	            	show:true,
	            	interval:'auto',
	            	rotate:0,
	            	formatter:function(s) { return s.slice(0, 12); },
	            	textStyle:{fontSize:13,color:'#000'}
	            	},
	            controlStyle:{
	            	position: 'left',
	            	itemSize: 20,
	            	itemGap:5,
	            	normal:{color:'#000'},
	            	emphasis:{color:'#1e90ff'}
	            	},
	            /*checkpointStyle:{
	            	symbol:'auto',
	            	symbolSize:10,
	            	color:'#90C8F6',
	            	label:{show:false,textStyle:{color:'auto'}}
	            	},*/	
	        },
			title:{
				x:'center',
				text:SubjectTitle,
				subtext:Unit
			},
			backgroundColor:'#fff',
			tooltip : {'trigger':'item',formatter: '{b}：{c}'},
			dataRange:{			
				x:'left',
				y:'bottom',
				z:5,	
				min:dataRange_maxmin[0],
				max:dataRange_maxmin[1],
//				min: 100,
//				max: 500000,
				padding:10,
				itemGap:15,
				splitNumber:5,
				calculable:true,
				orient:'vertical',
				text:['高','低'], 
				color:['#FF0000','#EE9A00', '#EEEE00', '#00AA00'],
				textStyle:{color:'#000'},
			},
			toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
			series:baseOptionSerises,
	};
	var optionsStrusture = [];
	for(var i=0,l=TimeData.length;i<l;i++)
	{
		optionsStrusture.push({
				series:[{
					name:'',
					data:MapData[i],
				}],
		});
	}
	
	optionsStrusture = {
			baseOption: baseOption,
			options:optionsStrusture,
	};
	return optionsStrusture;
});

/*
 * 动态地图的Option的dataRange参数获取
 * 
 * @Parameter	DynamicMap图表serise下data参数数据
 * @DataLength	数据长度，即时间长度
 */
dynamicMapDataAndDOM.setDataRange = (function(Parameter,DataLength) {
	var result =[];
	var temp = [];
	for(var i=0;i<DataLength;i++)
	{
		for(var j=0,l1=Parameter[i].length;j<l1;j++)
		{
			temp.push({value:Parameter[i][j].value});
		}
	}
	var arr = chartsPublicFunction.setDataRange(temp);
	result.push(Math.min.apply(null, arr));
	result.push(Math.max.apply(null, arr));
	return result;
});

/*
 * 动态地图的绘制
 * 
 * @GeoJsonData			geojson数据名称
 * @HeaderData			数据表头的数据信息
 * @RecordsData 		数据体的详细数据
 * @TimeData
 * @optionsStrusture	StaticMap的Option参数数据
 * @indexParam			指标参数
 * @IndexControl
 * @SubjectTitle		专题名称
 */
dynamicMapDataAndDOM.dynamicMapDrawing = (function(GeoJsonData,HeaderData,RecordsData,TimeData,optionsStrusture,DynamicMapSerise,xDynamicMapSerise,indexParam,SubjectTitle,Unit) {
	//获取geojson数据。
	$.getJSON('Echartsbase/data/json/GD/'+GeoJsonData, function (chinaJson) {
		//地图的注册
		echarts.registerMap('GD', chinaJson);
		
		//获取DOM容器，初始化echarts实例
		myChart = echarts.init(document.getElementById('EchartBox'));
		
		//图表的装载
		//echartsOption = optionsStrusture;
		//myChart.setOption(echartsOption);
		myChart.setOption(optionsStrusture);
		
		//图表单击时间
		myChart.on("click",function(param){
			var selected = param.name;
			var select_name;
			var GeoJson = "guang_dong_geo_QY.json";
			var name=['珠江三角洲地区','粤东地区','粤西地区','粤北地区','地市'];
			var DataRecord=[];
			for(var i=0;i<name.length;i++){
				if(selected == name[i]){
					select_name = name[i];
				}
			}
			switch(select_name)
			{
				case '珠江三角洲地区' : GeoJson ="guang_dong_geo_ZSJDQ.json";count=1;break;
				case '粤东地区'     : GeoJson ="guang_dong_geo_YDDQ.json";count=2;break;
				case '粤西地区'	 : GeoJson ="guang_dong_geo_YXDQ.json";count=3;break;
				case '粤北地区'	 : GeoJson ="guang_dong_geo_YBDQ.json";count=4;break;
				case '地市'		:;break;
				default :count=0;break;
			}
			if(count!=0)
			{
				for(var l=0;l<TimeData.firstSelected.length;l++)
				{
					DataRecord.push([]);
				}	
				for(var l=0;l<TimeData.firstSelected.length;l++)
				{
					for(var n=0;n<RecordsData.length;n++)
					{
						if(RecordsData[n][HeaderData[0].columnName]==TimeData.firstSelected[l])
						{
							DataRecord[l]=$.extend(true,{},RecordsData[n]);
							for(var p=0;p<RecordsData[n].children[count-1].children.length;p++)
							{
								DataRecord[l].children[p]=$.extend(true,{},RecordsData[n].children[count-1].children[p]);	
							}
							
						}
					}												
				}			
			}
//				var flag=0;
//				if(RecordsData[0].children)
//				{
//					if(RecordsData[0].children[1].children)
//					{
//						for(var i=0;i<RecordsData[0].children[1].children.length;i++)
//						{					
//							if(RecordsData[0].children[1].children[i].children)
//							{
//								flag=flag+1;
//							}
//						}
//						var m=RecordsData[0].children[1].children.length-flag;
//						if(flag!=0)
//						{
//							for(var l=0;l<TimeData.firstSelected.length;l++)
//							{
//								DataRecord.push([]);
//							}							
//							for(var l=0;l<TimeData.firstSelected.length;l++)
//							{
//								for(var n=0;n<RecordsData.length;n++)
//								{
//									if(RecordsData[n][HeaderData[0].columnName]==TimeData.firstSelected[l])
//									{
//										DataRecord[l]=$.extend(true,{},RecordsData[n]);
//										for(var k=0;k<flag;k++)
//										{
//											DataRecord[l].children[k]=$.extend(true,{},RecordsData[n].children[count-1].children[m+k]);
//										}				
//									}
//								}												
//							}				
//						}
//						else
//						{
//							for(var l=0;l<TimeData.firstSelected.length;l++)
//							{
//								DataRecord.push([]);
//							}	
//							for(var l=0;l<TimeData.firstSelected.length;l++)
//							{
//								for(var n=0;n<RecordsData.length;n++)
//								{
//									if(RecordsData[n][HeaderData[0].columnName]==TimeData.firstSelected[l])
//									{
//										DataRecord[l]=$.extend(true,{},RecordsData[n]);
//										for(var p=0;p<RecordsData[n].children[count-1].children.length;p++)
//										{
//											DataRecord[l].children[p]=$.extend(true,{},RecordsData[n].children[count-1].children[p]);	
//										}
//										
//									}
//								}												
//							}			
//						}
//					}					
//				}					

			if(DataRecord[0]!=undefined)
			{
				var Parameter = [];
				Parameter = dynamicMapDataAndDOM.dynamicMapData(GeoJson,HeaderData,DataRecord,TimeData,DynamicMapSerise,xDynamicMapSerise,indexParam).result;
				var optionsStrusture = dynamicMapDataAndDOM.setDynamicMapOption(TimeData.firstSelected,Parameter,SubjectTitle,Unit);
				dynamicMapDataAndDOM.dynamicMapDrawing(GeoJson,HeaderData,DataRecord,TimeData,optionsStrusture,DynamicMapSerise,xDynamicMapSerise,indexParam,SubjectTitle,Unit);
			}
			
			
		});
		
	});
});


function setDataRange(data) {
	//echartsOption.baseOption.dataRange.min = 0;
	//myChart.setOption(echartsOption);
	//alert(myChart);
}










