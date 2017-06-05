
var staticMapDataAndDOM = {};

staticMapDataAndDOM.getMapIndexData = (function(IndexControl) {
	var firstIndex = "";
	var secondIndex="";
	var result={firstSelected:[],secondSelected:[]};
	if(IndexControl[0]!=undefined&&IndexControl[0]!="")
	{	
	firstIndex = indexStructure.getIndexValue(IndexControl[0]);}
	if(IndexControl[1]!=undefined&&IndexControl[1]!="")
	{	secondIndex = indexStructure.getIndexValue(IndexControl[1]);}
	if(firstIndex!=undefined&&firstIndex!=""){
			result.firstSelected.push(firstIndex.split(',')[0]);
	}
	if(secondIndex!=undefined&&secondIndex!=""){
		result.secondSelected.push(secondIndex.split(',')[0]);
}
	return result;
});

/*
 * 静态地图数据的获取和构建
 * 
 * @GeoJson 地图的GeoJson文件 @HeaderData 数据表头的数据信息 @RecordsData 数据体的详细数据
 * @IndexControl 指标控件
 */
staticMapDataAndDOM.staticMapData = (function(GeoJson,HeaderData,RecordsData,xMapSerise,IndexControl) {
	var F;
	var obj={result:[],unit:[],yindexResult:[]};
	var ColumnName=HeaderData[0].columnName;
	obj.yindexResult.push(IndexControl.y[0]);
	// 获取指标所在的列编号，如F2
	F = chartsPublicFunction.getIndexColumnName(HeaderData,IndexControl.y,0);
	if(GeoJson == "guang_dong_geo_DS.json")
	{
		if(RecordsData[0]!=undefined)
		{
			if(RecordsData[0].children)
			{
				for(var i=0;i<RecordsData.length;i++)
				{
					for(var j=0;j<RecordsData[i].children.length;j++)
					{
						if(RecordsData[i].children[j][ColumnName]==xMapSerise.firstSelected[0])
						{
							obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));						
							obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[j][''+F+'']});		
						}
						if(RecordsData[i].children[j][ColumnName]==xMapSerise.secondSelected[0])
						{
							obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));						
							obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[j][''+F+'']});		
						}
					}
				}		
				return obj; 
			}
			else
			{
				for(var i=0,Length=RecordsData.length;i<Length;i++)
				{
					obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));
					obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});
				}
				return obj; 
			}
		}
	}
	else if(GeoJson=="guang_dong_geo_ZSJDQ.json"||GeoJson=="guang_dong_geo_YDDQ.json"||GeoJson=="guang_dong_geo_YXDQ.json"||GeoJson=="guang_dong_geo_YBDQ.json")
	{
		if(RecordsData[0].children)
		{
			for(var i=0;i<RecordsData.length;i++)
			{
				for(var j=0;j<RecordsData[i].children.length;j++)
				{
					if(RecordsData[i].children[j][ColumnName]==xMapSerise.firstSelected[0])
					{
						obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));						
						obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[j][''+F+'']});		
					}
				}
			}
			
		}
		else
		{
			for(var i=0;i<RecordsData.length;i++)
			{
				obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));						
				obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});		
			}
		}
	}
	else if(GeoJson=="guang_dong_geo_QY.json")
	{
					var flag=0;
					for(var i=0;i<RecordsData[0].children.length;i++)
					{					
						if(RecordsData[0].children[i].children)
						{
							flag=flag+1;
						}
					}
					if(flag!=0)
					{
						for(var i=0;i<RecordsData.length;i++)
						{
							for(var j=0;j<RecordsData[i].children.length;j++)
							{
								if(RecordsData[i].children[j][ColumnName]==xMapSerise.firstSelected[0])
								{
									obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));						
									obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[j][''+F+'']});		
								}
							}
						}
					}
					else
					{
						for(var i=0;i<RecordsData.length;i++)
						{
							obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData, IndexControl.y, 0));						
							obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i][''+F+'']});		
						}
					}
	}
// if(RecordsData[i][ColumnName]==xMapSerise.firstSelected[0])
// {
// for(var j=0;j<RecordsData[i].children.length;j++)
// {
// for(var l=0;l<RecordsData[i].children[j].children.length;l++)
// {
// if(RecordsData[i].children[j].children[l][ColumnName]==xMapSerise.secondSelected[0])
// {
// obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData,
// IndexControl.y, 0));
// obj.result.push({name:RecordsData[i].children[j][ColumnName],value:RecordsData[i].children[j].children[l][''+F+'']});
// }
// }
// }
// }
// }
// return obj;
// }
// else
// {
// for(var i=0;i<RecordsData.length;i++)
// {
// if(RecordsData[i][ColumnName]==xMapSerise.firstSelected[0])
// {
// for(var j=0;j<RecordsData[i].children.length;j++)
// {
// obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData,
// IndexControl.y, 0));
// obj.result.push({name:RecordsData[i].children[j][ColumnName],value:RecordsData[i].children[j][''+F+'']});
// }
// }
// }
// return obj;
// }
// }
// else
// {
// for(var i=0;i<RecordsData.length;i++)
// {
// for(var j=0;j<RecordsData[i].children.length;j++)
// {
// if(RecordsData[i].children[j][ColumnName]==xMapSerise.firstSelected[0])
// {
// obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData,
// IndexControl.y, 0));
// obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[j][''+F+'']});
// }
// if(RecordsData[i].children[j][ColumnName]==xMapSerise.secondSelected[0])
// {
// obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData,
// IndexControl.y, 0));
// obj.result.push({name:RecordsData[i][ColumnName],value:RecordsData[i].children[j][''+F+'']});
// }
// }
// }
// return obj;
// }
// }
// else
// {
// for(var j=0;j<RecordsData.length;j++)
// {
// obj.unit.push(chartsPublicFunction.getIndexColumnLabel(HeaderData,
// IndexControl.y, 0));
// obj.result.push({name:RecordsData[j][ColumnName],value:RecordsData[j][''+F+'']});
// }
// }
			return obj; 
		
});

/*
 * 获取并构建静态地图的Option
 * 
 * @MapData StaticMap图表serise下data参数数据 @SubjectTitle StaticMap图表的title参数数据
 */
staticMapDataAndDOM.setStaticMapOption = (function(MapData,SubjectTitle,Unit) {
	var dataRange_maxmin = chartsPublicFunction.setDataRange(MapData);
	var optionsStrusture = {
		title:{
			x:'center',
			text:SubjectTitle,
			subtext:Unit
		},
		backgroundColor:'#fff',
		tooltip : {'trigger':'item',formatter: '{b}:<br>{c}'},
		toolbox: {
	        feature: {
	            saveAsImage: {}
	        }
	    },
		dataRange: {
	        min: Math.min.apply(null, dataRange_maxmin),
	        max: Math.max.apply(null, dataRange_maxmin),
	        x: 'left',
	        y: 'bottom', 
	        calculable : true,
	        color:['#FF0000','#EE9A00', '#EEEE00', '#00AA00']
	    },
		series : [
		{
			name: '',
			type: 'map',
			mapType: 'GD',
			itemStyle:{
				normal:{label:{show:true,textStyle:{fontSize:8,align:'left',baseline:'middle'}}},
				emphasis:{label:{show:true}}
			},
			selectedMode : 'single',
			left: '15%',
			right:'15%',
			top: '12%',
			bottom: '12%',
			data:MapData,
		},
		]
	};
	return optionsStrusture;
});

/*
 * 静态地图的绘制
 * 
 * @GeoJsonData geojson数据名称 @optionsStrusture StaticMap的Option参数数据 @HeaderData
 * 数据表头的数据信息 @RecordsData 数据体的详细数据 @indexParam 指标参数 @SubjectTitle 专题名称
 */
staticMapDataAndDOM.staticMapDrawing = (function(GeoJsonData,optionsStrusture,HeaderData,RecordsData,xMapSerise,indexParam,SubjectTitle,Unit) {
	// 获取geojson数据。
	$.getJSON('Echartsbase/data/json/GD/'+GeoJsonData, function (chinaJson) {
		// 地图的注册
		echarts.registerMap('GD', chinaJson);
		
		// 获取DOM容器，初始化echarts实例
		var myChart = echarts.init(document.getElementById('EchartBox'));
		
		// 图表的装载
		myChart.setOption(optionsStrusture);
		
		// 图表单击时间
		myChart.on("click",function(param){
			var selected = param.name;
			var count=0;
			var select_name;
			var DataRecord=[];
			var GeoJson = "guang_dong_geo_QY.json";
			var name=['珠江三角洲地区','粤东地区','粤西地区','粤北地区'];
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
				default :alert("该区域暂无下级数据！");count=0;break;
			}	
			if(count!=0)
			{
				var num=0;
				var flag=0;
				for(var i=0;i<RecordsData[0].children.length;i++)
				{					
					if(RecordsData[0].children[i].children)
					{
						flag=flag+1;
					}
				}
				if(flag!=0)
				{
					for(var i=0;i<RecordsData.length;i++)
					{
						if(RecordsData[i][HeaderData[0].columnName]==select_name)
						{
							num=i;
						}
					}
					var flg=0;
					for(var d=0;d<RecordsData[num].children.length;d++)
					{					
						if(RecordsData[num].children[d].children)
						{
							flg=flg+1;
						}
					}
					for(var l=0;l<flg;l++)
					{
						DataRecord.push([]);
					}		
					var count=RecordsData[num].children.length-flg;
					for(var k=count,b=0;k<RecordsData[num].children.length;k++,b++)
					{
						DataRecord[b]=RecordsData[num].children[k];
					}

				}
				else
				{
					DataRecord=RecordsData[count-1].children;
				}
							

				if(DataRecord[0]!=undefined)
				{
					var Parameter = [];
					Parameter = staticMapDataAndDOM.staticMapData(GeoJson,HeaderData,DataRecord,xMapSerise,indexParam).result;
					var optionsStrusture = staticMapDataAndDOM.setStaticMapOption(Parameter,SubjectTitle,Unit);
					staticMapDataAndDOM.staticMapDrawing(GeoJson,optionsStrusture,HeaderData,DataRecord,xMapSerise,indexParam,SubjectTitle,Unit);
				}
			}
		});
		
	});
});



