
function allEvent(Data_ColumnsInfo,Data_Records,Chart_Type,Data_ID,Data_obj,Data_Num,Data_myGrid){
	this.HeaderData = Data_ColumnsInfo;
	this.RecordsData = Data_Records;
	this.ChartType = Chart_Type;
	this.DataID = Data_ID;
	this.obj=Data_obj;
	this.Num=Data_Num;
	this.myGrid=Data_myGrid;
}

allEvent.prototype = {
	
	/*
	 * 顶部工具条按钮事件
	 * 
	 * @toolbarName	顶部工具栏功能按钮名称
	 * @data		oracle数据库中的原始数据
	 */
	_topToolBar: function(toolbarName,data){
		switch(toolbarName)
		{
			case "trafficStatistics" 	: itemEvent.topToolBar("交通统计汇编资料",data.item[0],this.HeaderData,this.RecordsData);break;
			case "trafficCensus" 		: itemEvent.topToolBar("交通情况调查资料",data.item[1],this.HeaderData,this.RecordsData);break;
			case "economicStatistics" 	: itemEvent.topToolBar("社会经济统计资料",data.item[2],this.HeaderData,this.RecordsData);break;
			case "searchResult" 		: itemEvent.topToolBar("查询统计结果数据",data.item[6],this.HeaderData,this.RecordsData);break;
			case "odData" 				: itemEvent.topToolBar("OD期望数据",data.item[3],this.HeaderData,this.RecordsData);break;
			case "trafficFlow" 			: itemEvent.topToolBar("交通流量数据",data.item[4],this.HeaderData,this.RecordsData);break;
			case "WeatherConditions"	: itemEvent.topToolBar("天气情况数据",data.item[5],this.HeaderData,this.RecordsData);break;
			default						: break;
		}
	},
	
	/*
	 * 图表面板的工具条按钮事件
	 * 
	 * @toolbarName		图表展示面板工具栏功能按钮名称
	 */
	_echartsToolBar:function(toolbarName){
		switch(toolbarName)
		{
			case "paramSetting" 	: itemEvent.echartsToolBar.PS(this.HeaderData,this.RecordsData,this.ChartType);break;
			case "refresh" 			: itemEvent.echartsToolBar.RF(this.HeaderData,this.RecordsData,this.ChartType);break;
			case "collectSubject"	: itemEvent.echartsToolBar.SC(this.HeaderData,this.RecordsData,this.ChartType,this.DataID);break;
			//case "dataToCSV"  		: itemEvent.echartsToolBar.CSV(this.obj,this.Num);break;
			//case "button_apply"		: WeatherDraw.drawing();break;
			default					: break;
		}
	},
	/*
	 *详细数据面板的工具条按钮事件
	 * 
	 * @toolbarName		图表展示面板工具栏功能按钮名称
	 */
	_dataToolBar:function(toolbarName){
		switch(toolbarName)
		{
			case "dataToExcel" 	: itemEvent.dataToolBar.EXL(this.myGrid);break;
			//case "toPDF" 			: itemEvent.echartsToolBar.PDF(this.myGrid);break;
			default					: break;
		}
	},
	/*
	 * 目录树单击数据事件
	 * 
	 */
	_catalogTreeClick: function(){
		
		//表格数据的数据表头格式的构建。
		var HeaderDataStyle = itemEvent.tableGrid.HeaderStyleStructure(this.HeaderData);
		
		//表格数据的数据格式的构建。
		var RecordDataStyle = itemEvent.tableGrid.RecordDataStyleStructure(this.HeaderData,this.RecordsData);
		
		//表格列数计算
		var columnNumber = tableProperty.columnsCalculate(this.HeaderData);
		
		//DHTMLX的TreeGrid表头单元格属性参数构建
		var columnCellWidth = headerDataStructure.headerCellWidth(columnNumber);
		var columnCellType = headerDataStructure.headerCellType(columnNumber);
		var columnTextAlign = headerDataStructure.headerTextAlign(columnNumber);
		var columnHeaderMenu = headerDataStructure.headerMenu(columnNumber);
		
		//myGrid数据的初始化和加载
		_myGridinit(HeaderDataStyle,RecordDataStyle,columnCellWidth,columnCellType,columnTextAlign,columnHeaderMenu);
	},
	
	/*
	 * 
	 * 
	 */
	_test: function(){
		
	},
};

