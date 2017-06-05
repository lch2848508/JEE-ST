
var JMGJ = {};

/*
 * 江门公交刷卡
 * 
 * @HeaderData
 * @RecordsData
 * @timeFrom eg 2016.05.04
 * @timeTill
 */
JMGJ.drawing = (function(headerData,recordData) {
	//var time_from = timeFrom.split('.');
	//var time_till = timeTill.split('.');
	
	//构建专题标题
	var subjectTitle = '天气情况';
	
	//获取X轴数据
	//var xAxisData = ['5:00','6:00','7:00','8:00','9:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00','23:00'];
	
	//series数据的构建
	var GJData = GetGJData (headerData, recordData);
	var GJParam = GetGJParam (headerData, recordData);
	var seriesData = [];
	var index = GJParam[0].cardTypeNum;
	var cardAll = ColumnsInfo[1].children.length;
	var lineName = [];
	for(n=1;n<GJParam.length;n++){
		var k = [];
		for(i=0;i<GJData[0].rows.length;i++){
			if(GJData[0].rows[i].data[0].value == GJParam[n].line){
				for(j=1+index;j<GJData[0].rows[i].data.length;j+=cardAll){
					k.push(GJData[0].rows[i].data[j]);
				}
			}
			
		}
		seriesData[n] = k;
		lineName[n-1] = GJParam[n].line;
	}
	
	var resultData = [];
	for(i=1;i<seriesData.length;i++){
		resultData.push({
			name:lineName[i-1],
			type:'line',
			data:seriesData[i]
		});
	}
	optionGJ = {
			title:{
				text:'公交刷卡数据',
				x:'center'
			},
			legend : {
				show:true,
				orient:'vertical',
				x:'right',		
				y:'center',
				/*x:'center',		
				y:'bottom',*/
				z:5,
				padding:10,
				itemGap:5,
				itemWidth:25,
				itemHeight:18,
				selectedMode:'multiple',
				textStyle:{color:'#000',fontSize:13},
				data:lineName
			},
			tooltip : {'trigger':'item'},
			toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
			xAxis : {
				name:'时间',
				type : 'category',
				splitLine:{show:false},
				data:[6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22]
			},
			yAxis:{
				scale:true,
				name:'数量'
			},
			grid:{
				show:false,
				x:'5%',
				y:'15%',
				x2:'10%',
				y2:'13%'
			},
			series : resultData
		};
	//获取DOM容器，初始化echarts实例
	var myChart = echarts.init(document.getElementById('EchartBox'));
	
	//图表的装载
	myChart.setOption(optionGJ);	
	//alert(seriesData.heighT);
	
	
	//option的构建
	//var option = WeatherDraw.setOption(subjectTitle,xAxisData,seriesData);
	
	//WeatherDraw.WeatherDrawing(option);

	/*require(['Echartsbase/Echart2.2.7/zrender/tool/color'], function(){
		var zrColor = require('Echartsbase/Echart2.2.7/zrender/tool/color');
        var areaColor = zrColor.getLinearGradient(
            0, 200, 0, 400,
            [[0, 'rgba(255, 51, 51,0.8)'],[0.8, 'rgba(0,255,255,0.1)']]
        );
      //option的构建
    	var option = WeatherDraw.setOption(subjectTitle,xAxisData,seriesData,areaColor);
    	
    	WeatherDraw.WeatherDrawing(option);
	});*/
	
	
});









