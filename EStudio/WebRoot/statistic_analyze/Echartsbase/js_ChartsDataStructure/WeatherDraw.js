
var WeatherDraw = {};

/*
 * 天气情况专题的绘制
 * 
 * @HeaderData
 * @RecordsData
 * @timeFrom eg 2016.05.04
 * @timeTill
 */
WeatherDraw.drawing = (function(HeaderData,RecordsData,timeFrom,timeTill) {
	var time_from = timeFrom.split('.');
	var time_till = timeTill.split('.');
	
	//构建专题标题
	var subjectTitle = timeFrom+' 至 '+timeTill+'天气情况';
	
	//获取X轴数据
	var xAxisData = WeatherDraw.xAxisData(time_from,time_till);
	
	//series数据的构建
	var seriesData = WeatherDraw.seriesData(HeaderData,RecordsData,time_from,time_till);
	//alert(seriesData.heighT);
	
	
	//option的构建
	//var option = WeatherDraw.setOption(subjectTitle,xAxisData,seriesData);
	
	//WeatherDraw.WeatherDrawing(option);

	require(['Echartsbase/Echart2.2.7/zrender/tool/color'], function(){
		var zrColor = require('Echartsbase/Echart2.2.7/zrender/tool/color');
        var areaColor = zrColor.getLinearGradient(
            0, 200, 0, 400,
            [[0, 'rgba(255, 51, 51,0.8)'],[0.8, 'rgba(0,255,255,0.1)']]
        );
      //option的构建
    	var option = WeatherDraw.setOption(subjectTitle,xAxisData,seriesData,areaColor);
    	
    	WeatherDraw.WeatherDrawing(option);
	});
	
	
});

/*
 * xAxis的data数据的构建
 * 
 * @time_from
 * @time_till
 */
WeatherDraw.xAxisData = (function(time_from,time_till) {
	var result = [];
	var fromDate=new Date(time_from[0],time_from[1],time_from[2]);
	var tillDate=new Date(time_till[0],time_till[1],time_till[2]);
	var totalDays = (tillDate.getTime()-fromDate.getTime())/(1000*3600*24)+2;
	
	var year_F = fromDate.getFullYear();
	var mouth_F = fromDate.getMonth();
	var date_F = fromDate.getDate();
	var everyMouthDays = WeatherDraw.DaysOfeachMouth(year_F);
	var temp = mouth_F;
	
	for(var i=0;i<totalDays;i++)
	{
		if(i==0||temp != mouth_F)
		{
			result[i] = mouth_F + "月" + date_F + "日";
			temp = mouth_F;
		}
		else
		{
			result[i] = date_F + "日";
		}
		date_F++;
		if(date_F > everyMouthDays[mouth_F])
		{
			date_F = 1;
			mouth_F++;
			if(mouth_F>12)
			{
				mouth_F = 1;
				year_F++;
				everyMouthDays = WeatherDraw.DaysOfeachMouth(year_F);
			}
		}
		
	}
	return result;
	
});

/*
 * series的data数据的构建
 * 
 * @HeaderData
 * @RecordsData
 * @time_from
 * @time_till
 */
WeatherDraw.seriesData = (function(HeaderData,RecordsData,time_from,time_till) {
	var result = {};
	var lowTData = [];
	var heighTData = [];
	
	var fromDate=new Date(time_from[0],time_from[1],time_from[2]);
	var tillDate=new Date(time_till[0],time_till[1],time_till[2]);
	var totalDays = (tillDate.getTime()-fromDate.getTime())/(1000*3600*24)+1;
	
	var year_F = fromDate.getFullYear();
	var mouth_F = fromDate.getMonth();
	var date_F = fromDate.getDate();
	var everyMouthDays = WeatherDraw.DaysOfeachMouth(year_F);
	
	var temp = mouth_F;
	
	for(var i=0;i<totalDays;i++)
	{
		for(var j=date_F;j<everyMouthDays[mouth_F];j++)
		{
			var F = [];
			for(var k=0;k<HeaderData.length;k++)
			{
				if(parseInt(HeaderData[k].columnLabel)==j||HeaderData[k].columnLabel==j+"日")
				{
					F[0] = HeaderData[k].children[0].columnName;
					F[1] = HeaderData[k].children[1].columnName;
					F[2] = HeaderData[k].children[2].columnName;
				}
			}
			heighTData.push({value:RecordsData[mouth_F-1][''+F[0]+''], symbol:WeatherDraw.getSymbolImage(RecordsData[mouth_F-1][''+F[2]+'']), symbolSize:15});
			//heighTData[i] = RecordsData[mouth_F-1][''+F[0]+''];
			//heighTData[i] = RecordsData[mouth_F-1][''+F[0]+'']-RecordsData[mouth_F-1][''+F[1]+''];
			lowTData[i] = RecordsData[mouth_F-1][''+F[1]+''];
			i++;
			if(i>=totalDays)
			{
				break;
			}
		}
		date_F = 1;
		mouth_F++;
		if(mouth_F>12)
		{
			mouth_F = 1;
			year_F++;
			everyMouthDays = WeatherDraw.DaysOfeachMouth(year_F);
		}
	}
	
	result['heighT'] = heighTData;
	result['lowT'] = lowTData;
	
	return result;
});

/*
 * 专题option的构建
 * 
 * @title
 * @xAxis_data
 * @seriesData
 * @areaColor
 */
WeatherDraw.setOption = (function(title,xAxis_data,seriesData,areaColor) {
	var optionsStrusture = {
		    title: {
		    	x:'center',
		        text: title
		    },
		    tooltip : {
		        trigger: 'axis'
		    },
		    toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    xAxis : [
		        {
		        	name: '日期',
		            type : 'category',
		            boundaryGap : false,
		            data : xAxis_data
		        }
		    ],
		    yAxis : [
		        {
		        	name: '气温(°C)',
		            type : 'value',
		            axisLabel: {
		                show:false
		            }
		        }
		    ],
		    series : [
		        {
		            name:'最低气温',
		            type:'line',
		            stack: '总量',
		            label: {
		                normal: {
		                    show: true,
		                    position: 'top',
		                    textStyle: {
			                	color: '#1b1b1b'
		                    }
		                }
		            },
		            lineStyle: {
		            	normal: {
			            	color: 'aqua',
		                }
		            },
		            //areaStyle: {normal: {}},
		            data:seriesData.lowT
		        },
		        {
		            name:'最高气温',
		            type:'line',
		            stack: '总量',
		            label: {
		                normal: {
		                    show: true,
		                    position: 'top',
		                    textStyle: {
			                	color: '#1b1b1b'
		                    }
		                }
		            },
		            lineStyle: {
		            	normal: {
			            	color: '#ff3333',
		                }
		            },
		            areaStyle: {
		            	normal: {
		            		color : areaColor,
		            	}
		            },
		            data:seriesData.heighT
		        }
		    ]
		};
	return optionsStrusture;
});


/*
 * 专题的绘制
 * 
 * @
 * @
 */
WeatherDraw.WeatherDrawing = (function(optionsStrusture) {
	//获取DOM容器，初始化echarts实例
	var myChart = echarts.init(document.getElementById('EchartBox'));
	
	//图表的装载
	myChart.setOption(optionsStrusture);	
	
});




/*
 * 计算指定年的每月天数
 * 
 * @
 * @
 */
WeatherDraw.DaysOfeachMouth = (function(thisYear) {
	var result = [];
	result[0] = thisYear;
	for(var i=1;i<13;i++)
	{
		if(i == 2){
			if(thisYear % 100 == 0)
			{
				result[i] = thisYear % 400 == 0 ? 29 : 28;
			}
			else if(thisYear % 100 != 0)
			{
				result[i] = thisYear % 4 == 0 ? 29 : 28;
			}
	    }
		else if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12)
		{
			result[i] = 31;
		}
		else
		{
			result[i] = 30;
		}
	}
	return result;
});

/*
 * 根据天气情况获取获取图标
 * 
 * @TQ	天气情况
 */
WeatherDraw.getSymbolImage = (function(TQ) {
	var result = "";
	switch(TQ)
	{
		case '晴天' :  result = "image://images/weather/sunny.png"; break;
		case '阴天' :  result = "image://images/weather/overcast.png"; break;
		case '多云' :  result = "image://images/weather/cloudy.png"; break;
		case '小雨' :  result = "image://images/weather/lightRain.png"; break;
		case '大雨' :  result = "image://images/weather/heavyRain.png"; break;
		case '暴雨' :  result = "image://images/weather/rainstorm.png"; break;
		case '雾天' :  result = "image://images/weather/greasy.png"; break;
		case '冰冻' :  result = "image://images/weather/frozen.png"; break;
	}
	return result;
});




