
var ODMapDraw = {};

var geoCoord = {
		'中山市': [113.4229, 22.478],
		'东莞市': [113.8953, 22.901],
		'佛山市': [112.8955, 23.1097],
		'广州市': [113.5107, 23.2196],
		'惠州市': [114.6204, 23.1647],
		'汕头市': [116.5792, 23.3605],
		'江门市': [112.6318, 22.1484],
		'河源市': [114.917, 23.9722],
		'深圳市': [114.1235, 22.6539],
		'清远市': [112.9175,24.3292],
		'珠海市': [113.7305, 22.1155],
		'肇庆市': [112.1265, 23.5822],
		'韶关市': [113.7964,24.7028],
		'云浮市': [111.7859,22.8516],
		'阳江市': [111.8298,22.0715],
		'茂名市': [111.0059,22.0221],
		'湛江市': [110.3577,20.9894],
		'汕尾市': [115.5762,23.0438],
		'梅州市': [116.1255,24.1534],
		'潮州市': [116.7847,23.8293],
		'揭阳市': [116.1255,23.313],
};


/*
 * OD专题的绘制
 * 
 * @HeaderData
 * @RecordsData
 */
ODMapDraw.drawing = (function(HeaderData,RecordsData) {
	var legendData =  ODMapDraw.legendDataAndSelected(HeaderData,RecordsData).data;
	var legendSelected = ODMapDraw.legendDataAndSelected(HeaderData,RecordsData).selected;
	var cityLength = RecordsData.length;
	var oneCityName = RecordsData[0][''+HeaderData[0].columnName+''];
	var geoJsonName = chartsPublicFunction.getMapGeoJson(cityLength,oneCityName);
	var markLineData = ODMapDraw.seriesMarkLineData(HeaderData,RecordsData);
	
	var option = ODMapDraw.setODMapOption(legendData,legendSelected,markLineData);
	
	ODMapDraw.ODMapDrawing(geoJsonName,option,HeaderData,RecordsData);

	
});

/*
 * OD专题的图例数据的构建
 * 
 * @HeaderData
 * @RecordsData
 */
ODMapDraw.legendDataAndSelected = (function(HeaderData,RecordsData) {
	var result = {};
	var hLength = RecordsData.length;
	//获取第一列的数组对象关键字
	var firstColID = HeaderData[0].columnName;
	var legendData = [];
	var legendSelected = {};
	for(var i=0;i<hLength;i++)
	{
		legendData[i] = RecordsData[i][''+firstColID+''];
		legendSelected[''+legendData[i]+''] = i==0?true:false;
	}
	result['data'] = legendData;
	result['selected'] = legendSelected;
	return result;
});

/*
 * OD专题的标线数据的获取
 * 
 * @
 */
ODMapDraw.seriesMarkLineData = (function(HeaderData,RecordsData) {
	var result = [];
	var firstColID = HeaderData[0].columnName;
	var startNum = RecordsData.length;
	var endNum = HeaderData.length-1;
	for(var i=0;i<startNum;i++)
	{
		var oneResult = [];
		for(var j=0;j<endNum;j++)
		{
			var oneRecordData = [];
			oneRecordData.push({name:RecordsData[i][''+firstColID+'']});
			oneRecordData.push({name:HeaderData[j+1].columnLabel, value:RecordsData[i][''+HeaderData[j+1].columnName+'']});
			oneResult.push(oneRecordData);
		}
		result.push(oneResult);
	}
	return result;
});

/*
 * OD专题option数据的构建
 * 
 * @
 */
ODMapDraw.setODMapOption = (function(legendData,legendSelected,markLineData) {
	
	var seriesStrusture = [];
	for(var i=0,l=markLineData.length;i<l;i++)
	{
		seriesStrusture.push({
			name:markLineData[i][0][0].name, 
			type:'map',
			//mapLocation:{x:'25%'},
			showLegendSymbol:false,
			roam:true,
			hoverable:false,
			mapType:'GD',
            itemStyle:{
                normal:{
                    borderColor:'rgba(100,149,237,1)',
                    borderWidth:1,
                    areaStyle:{
                        //color: '#1b1b1b'
                    	color: '#F0F0F0'
                    }
                }
            },
			//数据视图
			data:[],
			markLine:{
				smooth:true,
				symbol:['circle', 'arrow'],  
				symbolSize:4,
				effect:{
					show:true,
					scaleSize:1.5,
					period:30,             // 运动周期，无单位，值越大越慢
					//color:'#fff',
					color:'#007dd4',
					shadowColor:'rgba(220,220,220,0.7)',
					//shadowColor:'rgba(51, 255, 255,0.7)',
					shadowBlur:4 			//阴影线宽度
				},
				itemStyle:{
					normal:{
						borderWidth:1,
						//borderColor:['rgba(255,25,0,0.75)','rgba(0,100,255,0.75)','rgba(0,255,0,0.75)','rgba(255,255,0,0.75)'][idx],
						lineStyle:{
							shadowBlur:0.5,
							type:'solid',
							shadowColor:['rgba(255,25,0,0.4)','rgba(0,100,255,0.4)','rgba(0,255,0,0.4)','rgba(255,255,0,0.4)'][0] //背景默认透明
						},
						label:{show:true,textStyle:{fontSize:15, fontWeight:'bold'}}
					}
				},
				data:markLineData[i]
			},
			geoCoord:geoCoord,
		});
	}
	
	var optionsStrusture = {
			//标题
			title:{x:'center',text:'',},
			//背景颜色
			backgroundColor:'#fff',
			//backgroundColor:'#1b1b1b',
			//悬浮提示框
			tooltip : {'trigger':'item',formatter: '{b}'},
			toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
			//图例
			legend:{
				show:true,
				orient:'vertical',
				x:'left',		
				y:'top',
				z:5,
				padding:10,
				itemGap:5,
				itemWidth:25,
				itemHeight:18, 
				borderWidth:0,
				selectedMode:'single',
				selected:legendSelected,
				data:legendData,
				textStyle:{color:'#000',fontSize:13}
			},
			//阈值范围
			dataRange:{			
				min:0,
				max:10000,
				x:'right',
				padding:30,
				itemGap:15,
				splitNumber:5,
				calculable:true,
				orient:'vertical',
				color: ['#ff3333', 'orange', 'yellow','lime','aqua'],
				//color:['#FF0000','#EE9A00', '#EEEE00', '#00CD00'],
				textStyle:{color:'#000'}
			},
			series:seriesStrusture,
	};
	return optionsStrusture;
});

/*
 * OD专题的绘制
 * 
 * @
 */
ODMapDraw.ODMapDrawing = (function(GeoJsonData,option,HeaderData,RecordsData) {
	
	var myChart;
	require.config({
		paths: {
			echarts: './Echartsbase/Echart2.2.7/echarts'
		}
	});
	require(
        [
            'echarts',
            'echarts/chart/map'
        ],
		function (ec) { 
			myChart = ec.init(document.getElementById('EchartBox'));
			require('echarts/util/mapData/params').params.GD = {
				getGeoJson: function (callback) {
					$.getJSON('Echartsbase/data/json/GD/'+GeoJsonData,callback);
				}
			};
            myChart.setOption(option);  
        }  
    );  
});










