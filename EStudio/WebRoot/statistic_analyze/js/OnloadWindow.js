/*
 * 全局变量
 * 
 */
var colNum=0;
var ColumnIndex="";
var allHandleEvent;
var DataColumnsInfo = "";
var x_array = "";
var DataRecords = "";
var ChartType = "";
var DataID = {
	"ID" : 0
};
var myLayout;
var myTopToolbar;
var VisualAreaTabbar;
var ChartsBox;
var DataBox;
var dataToolbar;
var chartsToolbar;
var SubjectArea;
var EChartsArea;
var myGrid;
var gridSearch;
var mySubject;
var mySubjectMenu;
var chartsSelect;
var toolBarisLoading;
var ChartsLayout;
var DataLayout;
var SearchArea;
var DataArea;
var myTree;
var CatalogTree;
var CatalogArea;
var obj = "";
var Num = 0;
/*
 * 窗体初始化加载控件(程序入口)
 * 
 */
function doOnLoad() {
	$.get("DirectoryTreeDataService.jsp", function(data) {
		// document.getElementById("controlAll").style.visibility="hidden";
		var CatalogTreeData = $.evalJSON(data);
		// layout界面所有控件事件函数
		allHandleEvent = new allEvent(DataColumnsInfo, DataRecords, ChartType,
				DataID, obj, Num,myGrid);

		// 整体框架布局
		myLayout = new dhtmlXLayoutObject(document.body, '2U');
		myLayout.cells('a').setWidth('350');

		// 页眉和页脚（页面最上面和最下面的控件面板）
		myLayout.attachHeader("my_logo");
		myLayout.attachFooter("my_copy");

		// 顶端工具条
		/*
		 * myTopToolbar = myLayout.attachToolbar({ icons_path:
		 * "dhtmlxbase/ico/", xml: "layoutData/Top_toolbar.xml", onload:
		 * function() {
		 * allHandleEvent._topToolBar("trafficStatistics",CatalogTreeData); }
		 * }); myTopToolbar.attachEvent("onClick", function(id){
		 * allHandleEvent._topToolBar(id,CatalogTreeData);
		 * $("#EchartBox").html(""); });
		 */

		// 将布局b单元的标题隐藏，并加入两个tabbar页，一个为详细数据，一个为图表展示
		var VisualArea = myLayout.cells('b');
		VisualArea.hideHeader();
		VisualAreaTabbar = VisualArea.attachTabbar();
		// VisualAreaTabbar.setAlign('left');
		// VisualAreaTabbar.enableTabCloseButton(true);
		// 设置tab页两端的方向按钮不可见
		VisualAreaTabbar.setArrowsMode("auto");
		// 详细数据Tab面板
		VisualAreaTabbar.addTab('Data', '详细数据');
		DataBox = VisualAreaTabbar.cells('Data');
		// 详细数据面板的工具条
		dataToolbar = DataBox.attachToolbar({
			icons_path : "dhtmlxbase/ico/toolbarico/",
			xml : "layoutData/Data_toolbar.xml",
		});
		dataToolbar.attachEvent("onClick", function(id) {
			allHandleEvent = new allEvent(DataColumnsInfo, DataRecords,
					ChartType, DataID, obj, Num,myGrid);
			allHandleEvent._dataToolBar(id);
		});

		DataBox.setActive();

		// 图表展示Tab面板
		VisualAreaTabbar.addTab('Charts', '图表展示');
		ChartsBox = VisualAreaTabbar.cells('Charts');
		// 图表展示面板的工具条
		chartsToolbar = ChartsBox.attachToolbar({
			icons_path : "dhtmlxbase/ico/toolbarico/",
			xml : "layoutData/Charts_toolbar.xml",
			onload : function() {
				// itemEvent.echartsToolBar.CS(DataID);
				_chartSelect(DataID);
			}
		});
		chartsToolbar.attachEvent("onClick", function(id) {
			allHandleEvent = new allEvent(DataColumnsInfo, DataRecords,
					ChartType, DataID, obj, Num);
			allHandleEvent._echartsToolBar(id);
		});

		// 图表展示面板区域划分，a区域为Echarts图表区，b区域为专题列表区
		ChartsLayout = ChartsBox.attachLayout('2U');
		EChartsArea = ChartsLayout.cells('a');
		EChartsArea.hideHeader();
		// Echart的DOM的装载
		EChartsArea.appendObject("Echart");

		SubjectArea = ChartsLayout.cells('b');
		SubjectArea.setText("<span>专题列表</span>");
		SubjectArea.setWidth('200');

		// 目录树的加载
		_Cataloginit(CatalogTreeData);
	});
}

/*
 * 目录树的加载
 * 
 */
function _Cataloginit(catalogData) {
	CatalogArea = myLayout.cells('a');
	CatalogTree = CatalogArea.attachTree();
	CatalogArea
			.setText("<span>交通统计</span> <td rowspan=\"2\" style=\"padding-left:25px\" valign=\"top\"><input type=\"text\" id=\"stext\" width=\"100px\"><a href=\"javascript:void(0)\" onClick=\"CatalogTree.findItem(document.getElementById('stext').value,0,1)\"> 查找 </a> | <a href=\"javascript:void(0)\" onClick=\"CatalogTree.findItem(document.getElementById('stext').value)\"> 下一个</a> | <a href=\"javascript:void(0)\" onClick=\"CatalogTree.findItem(document.getElementById('stext').value,1)\"> 上一个 </a></td>");

	CatalogTree.setImagePath("dhtmlxbase/imgs/dhxtree_skyblue/");
	CatalogTree.loadJSONObject(catalogData);
	// CatalogTree = new dhtmlXTreeObject("treeboxbox_tree","100%","100%",0);
	CatalogTree.enableSmartXMLParsing(true);
	// 目录树单击事件
	CatalogTree.attachEvent("onClick", function(id) {

		// 获取节点的所有子节点的ID
		var IDofAllChildren = CatalogTree.getSubItems(id);

		// 若子节点下没有子节点，即IDofAllChildren="",则为数据文件
		if (!IDofAllChildren) {
			DataID = {
				"ID" : id
			};
			var ChartType = "";

			// 根据节点ID获取节点的Text名称
			var SelectName = CatalogTree.getItemText(id);

			// 判断节点是不是含有OD字段名
			var isODData = SelectName.indexOf("OD");
			var isTQData = SelectName.indexOf("天气");

			// OD专题展示的ToolBar
			if (isODData >= 0) {
				chartsToolbar.clearAll();
				chartsToolbar.addButton("userDefined", null, "自定义配置",
						"param-config.ico");

				$("#EchartBox").html("");
				ChartsLayout.cells('b').collapse();

				chartsToolbar.attachEvent("onClick", function(id) {
					// if(id=="userDefined")
					// WeatherDraw.drawing();
				});
			}
			// 天气情况展示的ToolBar
			else if (isTQData >= 0) {
				chartsToolbar.clearAll();
				chartsToolbar.addText("text_from", null, "起始时间");
				chartsToolbar.addInput("date_from", null, "", 75);
				chartsToolbar.addText("text_till", null, "终止时间");
				chartsToolbar.addInput("date_till", null, "", 75);
				chartsToolbar.addButton("sureApply", null, "确定", "excute.png");

				// get inputs
				var input_from = chartsToolbar.getInput("date_from");
				input_from.setAttribute("readOnly", "true");
				input_from.onclick = function() {
					myCalendar.setSensitiveRange(null, input_till.value);
				};

				var input_till = chartsToolbar.getInput("date_till");
				input_till.setAttribute("readOnly", "true");
				input_till.onclick = function() {
					myCalendar.setSensitiveRange(input_from.value, null);
				};

				// init calendar
				var myCalendar = new dhtmlXCalendarObject([ input_from,
						input_till ]);
				myCalendar.setDateFormat("%Y.%m.%d");

				$("#EchartBox").html("");
				ChartsLayout.cells('b').collapse();

			}
			// 其他图表展示的ToolBar
			else if (isODData < 0 && isTQData < 0) {
				if (toolBarisLoading)
					return;
				toolBarisLoading = true;
				chartsToolbar.clearAll();
				chartsToolbar.loadStruct("layoutData/Charts_toolbar.xml",
						function() {
							toolBarisLoading = false;
						});
				ChartsLayout.cells('b').expand();

				// 图表区域和按钮的初始化
				$("#EchartBox").html("");
				chartsToolbar.setItemText('chartSelect', "图表选择");
				$("#subjectName").textbox('setValue', "");
			}

			// 目录树单击时详细数据的获取及响应处理事件。
			_catalogTreeEvent(DataID, ChartType, isODData);
		}
	});
}

/*
 * 目录树单击时详细数据的获取及处理函数
 * 
 * @DataID 数据源的ID @ChartType 图表类型
 */
function _catalogTreeEvent(DataID, ChartType, isODData) {

	$("#subjectName").textbox('setValue', '');
	$("#EchartBox").html("");

	$.get("StatisticDataService.jsp", DataID, function(data) {

		var GridData = $.evalJSON(data);
		// 获取数据表中的表头数据。
		DataColumnsInfo = $.evalJSON(GridData.columnsInfo).datagridColumns;
		ColumnIndex=$.evalJSON(GridData.columnsInfo).columnIndex;
		// 获取数据表中的详细数据。
		DataRecords = $.evalJSON(GridData.records);
		// 得到x指标的第一级分级数组
		var xarray = indexPadding.getxFirstIndex(DataRecords,
				DataColumnsInfo[0].columnName);
		x_array = xarray[0];
		publicFunction.indexDivPadding();
		indexPadding.firstIndexSelect(DataColumnsInfo, ChartType);
		indexPadding.xfirstIndexSelect(DataRecords, x_array, ChartType,
				DataColumnsInfo[0].columnName, DataColumnsInfo);

		// 全局处理事件函数的初始化
		allHandleEvent = new allEvent(DataColumnsInfo, DataRecords, ChartType,
				DataID, obj, Num);
		allHandleEvent._catalogTreeClick();

		// 专题列表构建
		_subjectListLoad(DataID);

		if (isODData >= 0) {
			ODMapDraw.drawing(DataColumnsInfo, DataRecords);
		}

		// ToolBar按钮单击事件
		chartsToolbar.attachEvent("onClick", function(id) {
			if (id == "sureApply") {
				var fromTime = "";
				var toTime = "";
				fromTime = chartsToolbar.getInput("date_from").value;
				toTime = chartsToolbar.getInput("date_till").value;
				if (fromTime == "") {
					dhtmlx.alert({
						type : "alert-warning",
						ok : "确定",
						text : "请选择起始时间！ ",
						callback : function() {
						}
					});
				} else if (toTime == "") {
					dhtmlx.alert({
						type : "alert-warning",
						ok : "确定",
						text : "请选择终止时间！ ",
						callback : function() {
						}
					});
				} else if (fromTime != "" && toTime != "") {
					WeatherDraw.drawing(DataColumnsInfo, DataRecords, fromTime,
							toTime);
				}
			}
		});

	});
}

/*
 * DHTMLX表格数据的初始化加载
 * 
 * @HeaderDataStyle 表头数据 @RecordDataStyle 表数据 @columnCellWidth 表头单元的宽度
 * @columnCellType 表头单元的数据格式 @columnTextAlign 表头字体样式 @columnHeaderMenu 表头右键菜单状态
 */
function _myGridinit(HeaderDataStyle, RecordDataStyle, columnCellWidth,
		columnCellType, columnTextAlign, columnHeaderMenu) {

	myGrid = DataBox.attachGrid();
	myGrid.setSkin("dhx_skyblue");
	myGrid.setImagePath("dhtmlxbase/imgs/");
	// myGrid.enableColumnMove(true,"false,true,true,true,true");
	// 加载数据表头
	for ( var i = 0; i < HeaderDataStyle.length; i++) {
		if (i == 0) {
			myGrid.setHeader(HeaderDataStyle[0], null, columnTextAlign);
		} else {
			myGrid.attachHeader(HeaderDataStyle[i], columnTextAlign);
		}
	}

	
	/*有BUG
	 * var appendhtml = document.createElement("filter");
	appendhtml.innerHTML = "Column<select id='a10'>" +
			"<option value='0'>0</option><option value='1'>1</option><option value='2'>2</option></select>" +
			"Mask<input type='text' name='a12' value='' id='a12'>" +
			"<input type='button' name='a11' value='Filter' id='a11' onclick='myGrid.filterBy(document.getElementById('a10').value,document.getElementById('a12').value);'>" +
			"<br/><br/>" +
			"<input type='button' name='a11' value='Filter column[0] < 500' id='a11' onclick='myGrid.filterBy(0,function(a){ return (a<500);});'>";
	document.body.appendChild(appendhtml);
	var nodep =document.getElementsByTagName("filter"); 
	myGrid.attachHeader(nodep);*/
/*
 * 
 * 文本框匹配查询
 * 
 * var appendhtml = document.createElement("filter"); appendhtml.innerHTML = "<label>请输入查询指标名称：</label><input
 * id='tags' type='text'><input type=\"button\" value=\"Get Value\" />";
 * document.body.appendChild(appendhtml); var nodep =
 * document.getElementsByTagName("filter"); myGrid.attachHeader(nodep); var
 * availableTags = indexPadding.getxFirstIndex(DataRecords,
 * DataColumnsInfo[0].columnName)[0]; var v = document.getElementById("tags");
 * var $v = $(v); $(v).autocomplete(availableTags, { width : 320, max : 4,
 * showNoSuggestionNotice : true, minChars : 0, noSuggestionNotice : 'Sorry, no
 * matching results' });
 */
	
	// 设置表头单元的宽度
	myGrid.setInitWidths(columnCellWidth);

	// 设置表头单元的类型
	myGrid.setColTypes(columnCellType);

	// 设置数据表能够通过Shift或Ctrl进行多行的选择
	myGrid.enableMultiselect(true);

	// 设置数据表能够通过Shift或Ctrl进行多个单元格的选择
	// myGrid.enableMarkedCells();

	// 设置数据表内容右键菜单功能
	// myGrid.enableContextMenu(myMenu);
	// 设置大数据量时，对数据进行分步加载，一次加载30条数据记录
	myGrid.enableSmartRendering(true, 30);
	// 表格数据的加载
	myGrid.init();
	//colNum=myGrid.getColumnsNum();
	//var anchor0="列号<select id='a10'>";
//	for(var i=1;i<colNum;i++){
//		anchor0+="<option value='"+i+"'>"+i+"</option>";
//	}
//	anchor0+="</select>";
	var anchor1=$('<input/>',{
		on:{
			change:function(){
				//document.getElementById('a22').value="";
				//var b=document.getElementById('a22').value;
				//b=parseFloat(b);
				//myGrid.filterBy(document.getElementById('a10').value,function(a){ return (a>b);},false);
				myGrid.filterBy(0,document.getElementById('a12').value);
			}
		},
		type:"text",
		name:"a12",
		value:"",
		id:"a12",
		style:"width:98px;"
	});
	br = $('<br />');
//	var anchor2=$('<input/>',{
//		on:{
//			change:function(){
//				var b=document.getElementById('a22').value;
//				b=parseFloat(b);
//				myGrid.filterBy(0,document.getElementById('a12').value,false);
//				document.getElementById('a12').value="";
//				myGrid.filterBy(document.getElementById('a10').value,function(a){ return (a>b);},true);
//
//			}
//		},
//		type:"text",
//		name:"a22",
//		value:0,
//		id:"a22",
//		style:"width:40px;"
//	});
	var appendhtml=document.createElement("filter");
	appendhtml.innerHTML="";
	document.body.appendChild(appendhtml);
	var w=document.getElementsByTagName("filter");
	var $w=$(w);
	//$(w).append("名称：",anchor1,"<br/>","阈值：",anchor0,anchor2);
	$(w).append("名称：",anchor1);
	var nodep =document.getElementsByTagName("filter"); 
	
	myGrid.parse(RecordDataStyle[0], "json");
	myGrid.attachHeader(nodep);
	// 设置表头右键菜单功能 @columnHeaderMenu为包含的状态，即哪些些列在右键菜单的选择内
	// myGrid.enableHeaderMenu();
	myGrid.enableHeaderMenu(columnHeaderMenu);
	// 设置向右拉动滚动条时，第一列锁定不动 (使用时存在一定问题)
	myGrid.splitAt(1);

	myGrid.expandAll();

}

/*
 * 图表选择事件
 * 
 * @DataID 数据源ID
 */
$(document).ready(function() {
	$("#first-Index").change(function() {
		alert("Option changed!");
	});
});
function _chartSelect(DataID) {
	chartsSelect = new dhtmlXPopup({
		toolbar : chartsToolbar,
		id : "chartSelect"
	});
	chartsSelect.attachObject("Chart-form");
	// paramset = new dhtmlXPopup({ toolbar: chartsToolbar, id: "paramSetting"
	// });
	// paramset.attachObject("Param-form");

	$("img").click(
			function() {

				/*
				 * publicFunction.indexDivPadding();
				 * indexPadding.firstIndexSelect(DataColumnsInfo,ChartType);
				 */
				if (ChartType != this.alt) {
					// 获取图表类型
					ChartType = this.alt;

					// 参数设置面板的专题名称控件重置清空
					$("#subjectName").textbox('setValue', '');
					$("#EchartBox").html("");

					publicFunction.indexDivPadding();
					indexPadding.firstIndexSelect(DataColumnsInfo, ChartType);
					indexPadding.xfirstIndexSelect(DataRecords, x_array,
							ChartType, DataColumnsInfo[0].columnName,
							DataColumnsInfo);
				

					// var element = document.getElementById("firstIndex");
					// if("\v"=="v") {
					// element.onpropertychange = indexPadding.webChange(this);
					// }else{
					// element.addEventListener("input",indexPadding.webChange(this),false);
					// }

					// $(document).ready(function(){
					// $("#first-Index").change(function() {
					// alert("Option changed!");
					// });
					// });
					allHandleEvent = new allEvent(DataColumnsInfo, DataRecords,
							ChartType, DataID, obj, Num);
					allHandleEvent._echartsToolBar("paramSetting");
					chartsSelect.hide();
					chartsToolbar.setItemText('chartSelect', ChartType);
				} else {
					/*
					 * publicFunction.indexDivPadding();
					 * indexPadding.firstIndexSelect(DataColumnsInfo,ChartType);
					 */

					allHandleEvent = new allEvent(DataColumnsInfo, DataRecords,
							ChartType, DataID, obj, Num);
					allHandleEvent._echartsToolBar("paramSetting");
					chartsSelect.hide();
				}
			});
}

/*
 * 专题列表的数据构建及加载
 * 
 * @DataID 数据的ID
 */
function _subjectListLoad(DataID) {
	$.get("SubjectListDataService.jsp", DataID, function(data) {
		var subjectList = [];
		subjectList.push({
			"rows" : $.evalJSON(data)
		});

		mySubjectMenu = new dhtmlXMenuObject();
		mySubjectMenu.setIconsPath("dhtmlxbase/ico/");
		mySubjectMenu.renderAsContextMenu();
		mySubjectMenu.attachEvent("onClick", _subjectListClickMenu);
		mySubjectMenu.loadStruct("layoutData/subjectmenu.xml");
		mySubject = SubjectArea.attachGrid();
		mySubject.setImagePath("dhtmlxbase/imgs/");
		mySubject.setSkin("dhx_skyblue");
		mySubject.setHeader("专题名称");
		mySubject.setInitWidths("*");
		mySubject.setColAlign("left");
		mySubject.setColTypes("ro");
		mySubject.setColSorting("str");
		mySubject.enableDragAndDrop(true);
		// mySubject.enableAutoWidth(false);
		mySubject.enableContextMenu(mySubjectMenu);
		mySubject.attachEvent("onRowSelect", doOnRowSelected);
		mySubject.init();
		mySubject.parse(subjectList[0], "json");

	});
}

function doOnRowSelected(id) {
	_subjectView(DataColumnsInfo, DataRecords, ChartType, DataID, id);
}

// 专题列表的右键功能菜单及响应函数。
function _subjectListClickMenu(menuitemId, type) {
	var data = mySubject.contextID.split("_"); // rowId_colInd
	var option = menuitemId.split("_")[0];
	if (option == "View") {
		_subjectView(DataColumnsInfo, DataRecords, ChartType, DataID, data[0]);
	} else if (option == "Delete") {
		_RemoveSubject(data[0]);
	}
	// alert(data);
	// myGrid.setRowTextStyle(data[0],"color:"+menuitemId.split("_")[1]);
	// return true;
}

// 专题列表右键菜单的 专题删除 功能按钮函数
function _RemoveSubject(rowID) {
	// var row = mySubject.getSelected();
	if (rowID) {
		$.messager.confirm('删除专题', '确定删除该专题吗?', function(r) {
			if (r) {
				$.post('RemoveSubjectEvent.jsp?id=' + rowID, function(result) {
					$.messager.show({
						title : '提示',
						msg : '删除成功！'
					});
					mySubject.deleteSelectedRows();
				});
				$("#EchartBox").html("");
				ChartType = "";
				chartsToolbar.setItemText('chartSelect', '图表选择');
				$("#subjectName").textbox('setValue', null);
			}
		});
	} else {
		// alert('请选择要删除的专题！');
		dhtmlx.alert({
			// title:"提示框",
			type : "alert-warning",
			ok : "确定",
			text : "请选择要删除的专题！ ",
			callback : function() {

			}
		});
	}
}

// 专题列表右键菜单的 专题查看 功能按钮函数
function _subjectView(HeaderData, RecordsData, ChartType, DataID, rowID) {
	// var row = mySubject.getSelected();
	// alert(row);
	$.get("GetSubjectParam.jsp?id=" + rowID, function(result) {
		var SelectResult = $.evalJSON(result);
		// 基本参数——专题名称的填充
		$("#subjectName").textbox('setValue', SelectResult.SubjectName);

		publicFunction.indexDivPadding();
		indexPadding.firstIndexSelect(HeaderData, SelectResult.ChartType);
		var ColumnName = HeaderData[0].columnName;
		var xarray = indexPadding.getxFirstIndex(RecordsData, ColumnName);
		indexPadding.xfirstIndexSelect(HeaderData, xarray[0],
				SelectResult.ChartType, ColumnName, HeaderData);
		// 指标参数控件的填充（根据数据库中专题定制存储的参数进行自动选择）
		_fillControlofSelect(HeaderData, RecordsData, SelectResult);
		ChartType = SelectResult.ChartType;

		// 图表面板的重置
		$("#chart").html("");
		chartsToolbar.setItemText('chartSelect', ChartType);
		// chartsToolbar.disableItem('paramSetting');
		// chartsToolbar.disableItem('refresh');
		// chartsToolbar.disableItem('collectSubject');
		// 控件的显示
		// document.getElementById("baseParam").style.display = "block";
		// document.getElementById("subject-Name").style.display = "block";
		// 指标控件的获取

		// 表头行数
		var NumberofROW = tableProperty.rowsCalculate(HeaderData) + 1;
		var xNumberofROW = publicFunction.getXlevel(xarray);
		// 数据指标地构建
		var IndexControl = indexStructure.indexInit(NumberofROW);
		var xIndexControl = indexStructure.xindexInit(xNumberofROW);
		// 获取指标参数值
		var indexParam = indexStructure.indexSelectResult(HeaderData,
				IndexControl, xIndexControl, RecordsData);
		// var xindexParam=indexParam;
		// 绘制图表
		drawChart.chartsInit(ChartType, HeaderData, RecordsData, indexParam,
				IndexControl, SelectResult.SubjectName);
		obj = drawChart.getObj();
		Num = drawChart.getNum();
		// var ControlofIndex = _ControlSelect();
		// 选中的指标参数
		// var indexParam = _resultofIndexSelect(HeaderData,ControlofIndex);
		// _DrawChart(ChartType,HeaderData,RecordsData,indexParam,ControlofIndex,SelectResult.SubjectName);
	});
}

/*
 * 专题定制的基本参数和指标参数的选项填充
 */
function _fillControlofSelect(headData, recordData, SubjectSelect) {
	// 表头行数
	var NumberofROW = tableProperty.rowsCalculate(headData) + 1;
	var ColumnName = headData[0].columnName;
	var xarray = indexPadding.getxFirstIndex(recordData, ColumnName);
	var xNumberofROW = publicFunction.getXlevel(xarray);
	var controlofFirstIndex = document.getElementsByName('firstIndex');
	var controlofSecondIndex = document.getElementsByName('secondIndex');
	var controlofThirdIndex = document.getElementsByName('thirdIndex');
	var controlofFourthIndex = document.getElementsByName('fourthIndex');
	var controlofFifthIndex = document.getElementsByName('fifthIndex');
	var xcontrolofFirstIndex = document.getElementsByName('xfirstIndex');
	var xcontrolofSecondIndex = document.getElementsByName('xsecondIndex');
	var xcontrolofThirdIndex = document.getElementsByName('xthirdIndex');
	var xcontrolofFourthIndex = document.getElementsByName('xfourthIndex');
	var xcontrolofFifthIndex = document.getElementsByName('xfifthIndex');
	switch (xNumberofROW) {
	case 1:
		var xfirstIndex = SubjectSelect.xfirstIndex.split(',');
		break;
	case 2:
		var xfirstIndex = SubjectSelect.xfirstIndex.split(',');
		var xsecondIndex = SubjectSelect.xsecondIndex.split(',');
		break;
	case 3:
		var xfirstIndex = SubjectSelect.xfirstIndex.split(',');
		var xsecondIndex = SubjectSelect.xsecondIndex.split(',');
		var xthirdIndex = SubjectSelect.xthirdIndex.split(',');
		break;
	case 4:
		var xfirstIndex = SubjectSelect.xfirstIndex.split(',');
		var xsecondIndex = SubjectSelect.xsecondIndex.split(',');
		var xthirdIndex = SubjectSelect.xthirdIndex.split(',');
		var xfourthIndex = SubjectSelect.xfourthIndex.split(',');
		break;
	case 5:
		var xfirstIndex = SubjectSelect.xfirstIndex.split(',');
		var xsecondIndex = SubjectSelect.xsecondIndex.split(',');
		var xthirdIndex = SubjectSelect.xthirdIndex.split(',');
		var xfourthIndex = SubjectSelect.xfourthIndex.split(',');
		var xfifthIndex = SubjectSelect.xfifthIndex.split(',');
		break;
	}
	for ( var i = 0, l = xcontrolofFirstIndex.length; i < l; i++) {
		for ( var j = 0, indexLength = xfirstIndex.length; j < indexLength; j++) {
			if (xcontrolofFirstIndex[i].defaultValue == xfirstIndex[j]) {
				document.getElementsByName('xfirstIndex')[i].checked = true;
			}
		}
	}
	for ( var i = 0, l = document.getElementsByName('xfirstIndex').length; i < l; i++) {
		if (document.getElementsByName('xfirstIndex')[i].checked) {
			indexPadding.xsecondIndexSelect(document
					.getElementsByName('xfirstIndex')[i]);
		}
	}

	if (xsecondIndex != undefined) {
		for ( var i = 0, l = xcontrolofSecondIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = xsecondIndex.length; j < indexLength; j++) {
				if (xcontrolofSecondIndex[i].defaultValue == xsecondIndex[j]) {
					document.getElementsByName('xsecondIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('xsecondIndex').length; i < l; i++) {
			if (document.getElementsByName('xsecondIndex')[i].checked) {
				indexPadding.xthirdIndexSelect(document
						.getElementsByName('xsecondIndex')[i]);
			}
		}
	}

	if (xthirdIndex != undefined) {
		for ( var i = 0, l = xcontrolofThirdIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = xthirdIndex.length; j < indexLength; j++) {
				if (xcontrolofThirdIndex[i].defaultValue == xthirdIndex[j]) {
					document.getElementsByName('xthirdIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('xthirdIndex').length; i < l; i++) {
			if (document.getElementsByName('xthirdIndex')[i].checked) {
				indexPadding.xfourthIndexSelect(document
						.getElementsByName('xthirdIndex')[i]);
			}
		}
	}

	if (xfourthIndex != undefined) {
		for ( var i = 0, l = xcontrolofFourthIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = xfourthIndex.length; j < indexLength; j++) {
				if (xcontrolofFourthIndex[i].defaultValue == xfourthIndex[j]) {
					document.getElementsByName('xfourthIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('xfourthIndex').length; i < l; i++) {
			if (document.getElementsByName('xfourthIndex')[i].checked) {
				indexPadding.xfifthIndexSelect(document
						.getElementsByName('xfourthIndex')[i]);
			}
		}
	}

	if (xfifthIndex != undefined) {
		for ( var i = 0, l = xcontrolofFifthIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = xfifIndex.length; j < indexLength; j++) {
				if (xcontrolofFifthIndex[i].defaultValue == xfifIndex[j]) {
					document.getElementsByName('xfifthIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('xfifthIndex').length; i < l; i++) {
			if (document.getElementsByName('xfifthIndex')[i].checked) {
				indexPadding.xsixthIndexSelect(document
						.getElementsByName('xfifthIndex')[i]);
			}
		}
	}
	switch (NumberofROW) {
	case 1:
		var firstIndex = SubjectSelect.firstIndex.split(',');
		break;
	case 2:
		var firstIndex = SubjectSelect.firstIndex.split(',');
		var secondIndex = SubjectSelect.secondIndex.split(',');
		break;
	case 3:
		var firstIndex = SubjectSelect.firstIndex.split(',');
		var secondIndex = SubjectSelect.secondIndex.split(',');
		var thirdIndex = SubjectSelect.thirdIndex.split(',');
		break;
	case 4:
		var firstIndex = SubjectSelect.firstIndex.split(',');
		var secondIndex = SubjectSelect.secondIndex.split(',');
		var thirdIndex = SubjectSelect.thirdIndex.split(',');
		var fourthIndex = SubjectSelect.fourthIndex.split(',');
		break;
	case 5:
		var firstIndex = SubjectSelect.firstIndex.split(',');
		var secondIndex = SubjectSelect.secondIndex.split(',');
		var thirdIndex = SubjectSelect.thirdIndex.split(',');
		var fourthIndex = SubjectSelect.fourthIndex.split(',');
		var fifthIndex = SubjectSelect.fifthIndex.split(',');
		break;
	}

	for ( var i = 0, l = controlofFirstIndex.length; i < l; i++) {
		for ( var j = 0, indexLength = firstIndex.length; j < indexLength; j++) {
			if (controlofFirstIndex[i].defaultValue == firstIndex[j]) {
				document.getElementsByName('firstIndex')[i].checked = true;
			}
		}
	}
	for ( var i = 0, l = document.getElementsByName('firstIndex').length; i < l; i++) {
		if (document.getElementsByName('firstIndex')[i].checked) {
			indexPadding.secondIndexSelect(document
					.getElementsByName('firstIndex')[i]);
		}
	}

	if (secondIndex != undefined) {
		for ( var i = 0, l = controlofSecondIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = secondIndex.length; j < indexLength; j++) {
				if (controlofSecondIndex[i].defaultValue == secondIndex[j]) {
					document.getElementsByName('secondIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('secondIndex').length; i < l; i++) {
			if (document.getElementsByName('secondIndex')[i].checked) {
				indexPadding.thirdIndexSelect(document
						.getElementsByName('secondIndex')[i]);
			}
		}
	}

	if (thirdIndex != undefined) {
		for ( var i = 0, l = controlofThirdIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = thirdIndex.length; j < indexLength; j++) {
				if (controlofThirdIndex[i].defaultValue == thirdIndex[j]) {
					document.getElementsByName('thirdIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('thirdIndex').length; i < l; i++) {
			if (document.getElementsByName('thirdIndex')[i].checked) {
				indexPadding.fourthIndexSelect(document
						.getElementsByName('thirdIndex')[i]);
			}
		}
	}

	if (fourthIndex != undefined) {
		for ( var i = 0, l = controlofFourthIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = fourthIndex.length; j < indexLength; j++) {
				if (controlofFourthIndex[i].defaultValue == fourthIndex[j]) {
					document.getElementsByName('fourthIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('fourthIndex').length; i < l; i++) {
			if (document.getElementsByName('fourthIndex')[i].checked) {
				indexPadding.fifthIndexSelect(document
						.getElementsByName('fourthIndex')[i]);
			}
		}
	}

	if (fifthIndex != undefined) {
		for ( var i = 0, l = controlofFifthIndex.length; i < l; i++) {
			for ( var j = 0, indexLength = fifIndex.length; j < indexLength; j++) {
				if (controlofFifthIndex[i].defaultValue == fifIndex[j]) {
					document.getElementsByName('fifthIndex')[i].checked = true;
				}
			}
		}
		for ( var i = 0, l = document.getElementsByName('fifthIndex').length; i < l; i++) {
			if (document.getElementsByName('fifthIndex')[i].checked) {
				indexPadding.sixthIndexSelect(document
						.getElementsByName('fifthIndex')[i]);
			}
		}
	}
}

/*
 * function _printData(){ myGrid.printView(); }
 */

