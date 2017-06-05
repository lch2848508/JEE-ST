
var drawChart = {};
var obj="";
var Num=0;
/*
 * 图表绘制初始化
 * 
 * @ChartType		图表类型
 * @HeaderData		表头数据
 * @RecordsData		数据表
 * @indexParam		指标参数
 * @IndexControl	指标控件
 * @SubjectTitle	专题名称
 */
drawChart.chartsInit = (function(ChartType,HeaderData,RecordsData,indexParam,IndexControl,SubjectTitle) {
	//表头行数
	//var NumberofROW = tableProperty.rowsCalculate(HeaderData);
	var NumberofROW=ycount;
	var ColumnName=HeaderData[0].columnName;
	var xarray=indexPadding.getxFirstIndex(RecordsData,ColumnName);
	//var xNumberofROW=publicFunction.getXlevel(xarray);
	var xNumberofROW=xcount;
	var xIndexControl=indexStructure.xindexInit(xNumberofROW);
	var Parameter = [];
	var RegionResult = "";
	var DataRegion="";
	var GeoJson="";
	DataRegion=HeaderData[0].columnLabel;
	switch(DataRegion)
	{
	case "时间":GeoJson="guang_dong_geo_QY.json";break;
		case "区域":GeoJson="guang_dong_geo_QY.json";break;
		case "地市":GeoJson="guang_dong_geo_DS.json";break;
		case "珠江三角洲地区": GeoJson = "guang_dong_geo_ZSJDQ.json";break;
		case "粤东地区": GeoJson = "guang_dong_geo_YDDQ.json";break;
		case "粤西地区": GeoJson = "guang_dong_geo_YXDQ.json";break;
		case "粤北地区": GeoJson = "guang_dong_geo_YBDQ.json";break;
	}
	if(ChartType == "二维饼状图"||ChartType == "圆环饼状图")
	{
		//Pie图表serise下data参数数据的获取和构建
		var xPieSerise=pieDataAndDOM.getMultilPieIndexData(xNumberofROW, xIndexControl);
		Parameter = pieDataAndDOM.pieData(DataRegion,HeaderData,RecordsData,indexParam,xPieSerise).result;
		var Unit=pieDataAndDOM.pieData(DataRegion,HeaderData,RecordsData,indexParam,xPieSerise).unit[0];
		obj=pieDataAndDOM.pieData(DataRegion,HeaderData,RecordsData,indexParam,xPieSerise);
		//PieDOM的option参数数据地构建
		var optionsStrusture = pieDataAndDOM.setPieOption(Parameter,ChartType,SubjectTitle,Unit);
		//Pie图表的绘制
		pieDataAndDOM.pieDrawing(optionsStrusture,Parameter,ChartType,HeaderData,RecordsData,indexParam,SubjectTitle);
		Num=3;
	}
	else if(ChartType == "簇状柱状图"||ChartType == "堆积柱状图")
	{
		//获取Bar的每个指标下的数据
		var BarSerise = barDataAndDOM.getMultilBarIndexData(NumberofROW,IndexControl);
		var xBarSerise=barDataAndDOM.getMultilBarIndexData(xNumberofROW, xIndexControl);
		obj=barDataAndDOM.barData(DataRegion,HeaderData,RecordsData,BarSerise,xBarSerise,indexParam);
		//Bar图表serise下data参数数据的获取和构建
		Parameter = barDataAndDOM.barData(DataRegion,HeaderData,RecordsData,BarSerise,xBarSerise,indexParam).result;
		var Unit=barDataAndDOM.barData(DataRegion,HeaderData,RecordsData,BarSerise,xBarSerise,indexParam).unit[0];
		//Bar的xAxis下data参数数据地构建
		var LegendData  = barDataAndDOM.setLegend(Parameter,(BarSerise.y.length-1));
		//BarDOM的option参数数据地构建
		var optionsStrusture = barDataAndDOM.setBarOption(BarSerise.y,Parameter,ChartType,LegendData,SubjectTitle,Unit);
		
		//Bar图表的绘制
		barDataAndDOM.barDrawing(HeaderData,RecordsData,ChartType,optionsStrusture,LegendData,indexParam,IndexControl,SubjectTitle);
		Num=2;
	}
	else if(ChartType == "静态地图")
	{
		var xMapSerise=staticMapDataAndDOM.getMapIndexData(xIndexControl);
		//StaticMap图表serise下data参数数据的获取和构建
		Parameter = staticMapDataAndDOM.staticMapData(GeoJson,HeaderData,RecordsData,xMapSerise,indexParam).result;
		//StaticMapDOM的option参数数据地构建
		var Unit=staticMapDataAndDOM.staticMapData(GeoJson,HeaderData,RecordsData,xMapSerise,indexParam).unit[0];
		var optionsStrusture = staticMapDataAndDOM.setStaticMapOption(Parameter,SubjectTitle,Unit);
		obj=staticMapDataAndDOM.staticMapData(GeoJson,HeaderData,RecordsData,xMapSerise,indexParam);
		//StaticMap图表的绘制
		staticMapDataAndDOM.staticMapDrawing(GeoJson,optionsStrusture,HeaderData,RecordsData,xMapSerise,indexParam,SubjectTitle,Unit);
		Num=1;
	}
	else if(ChartType == "动态地图")
	{
		//时间数据的构建，以数据表头第一行（第一列除外）为时间刻度
		var TimeData = dynamicMapDataAndDOM.setTimeData(xIndexControl);
		var xDynamicMapSerise=dynamicMapDataAndDOM.getxIndexData(xNumberofROW, xIndexControl);
		var DynamicMapSerise=dynamicMapDataAndDOM.getxIndexData(NumberofROW, IndexControl);
		var Unit=dynamicMapDataAndDOM.dynamicMapData(GeoJson,HeaderData,RecordsData,TimeData,DynamicMapSerise,xDynamicMapSerise,indexParam).unit[0];
		//DynamicMap图表serise下data参数数据的获取和构建
		Parameter = dynamicMapDataAndDOM.dynamicMapData(GeoJson,HeaderData,RecordsData,TimeData,DynamicMapSerise,xDynamicMapSerise,indexParam).result;
		obj=dynamicMapDataAndDOM.dynamicMapData(GeoJson,HeaderData,RecordsData,TimeData,DynamicMapSerise,xDynamicMapSerise,indexParam);
		//DynamicMapDOM的option参数数据地构建
		var optionsStrusture = dynamicMapDataAndDOM.setDynamicMapOption(TimeData.firstSelected,Parameter,SubjectTitle,Unit);
		Num=2;
		//DynamicMap图表的绘制
		dynamicMapDataAndDOM.dynamicMapDrawing(GeoJson,HeaderData,RecordsData,TimeData,optionsStrusture,DynamicMapSerise,xDynamicMapSerise,indexParam,SubjectTitle,Unit);
		
	}
	else if(ChartType == "折线图")
	{
		
		//获取Lines的每个指标下的数据
		var LinesSerise = linesDataAndDOM.getMultilLinesIndexData(NumberofROW,IndexControl);
		var xLineSerise=linesDataAndDOM.getMultilLinesIndexData(xNumberofROW, xIndexControl);
		//Lines图表serise下data参数数据的获取和构建
		Parameter = linesDataAndDOM.linesData(DataRegion,HeaderData,RecordsData,LinesSerise,xLineSerise,indexParam).result;
		var Unit=linesDataAndDOM.linesData(DataRegion,HeaderData,RecordsData,LinesSerise,xLineSerise,indexParam).unit[0];
		//Lines的xAxis下data参数数据地构建
		var LegendData  = linesDataAndDOM.setLegend(Parameter,(LinesSerise.y.length-1));
		obj=linesDataAndDOM.linesData(DataRegion,HeaderData,RecordsData,LinesSerise,xLineSerise,indexParam);
		var optionsStrusture = linesDataAndDOM.setLinesOption(LinesSerise.y,Parameter,ChartType,LegendData,SubjectTitle,Unit);
		Num=2;
		//Lines图表的绘制
		linesDataAndDOM.linesDrawing(HeaderData,RecordsData,ChartType,optionsStrusture,LegendData,indexParam,IndexControl,SubjectTitle);
		
	}
});

drawChart.getObj=(function(){return obj;});
drawChart.getNum=(function(){return Num;});

