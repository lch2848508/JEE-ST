var itemEvent = {};
var obj = "";
/*
 * 顶部工具条功能函数
 * 
 * @toolName 工具按钮名称 @catalogData 目录树数据源 @HeaderData 表头数据源 @RecordsData 表数据源
 */
itemEvent.topToolBar = (function(toolName, catalogData, HeaderData, RecordsData) {
	if (toolName != "查询统计结果数据") {
		catalogData.id = 0;
		var CatalogArea = myLayout.cells('a');
		CatalogArea.setText(toolName);
		var CatalogTree = CatalogArea.attachTree();
		CatalogTree.setImagePath("dhtmlxbase/imgs/dhxtree_skyblue/");
		CatalogTree.loadJSONObject(catalogData);
		// 目录树单击事件
		CatalogTree.attachEvent("onClick", function(id) {

			// 获取节点的所有子节点的ID
			var IDofAllChildren = CatalogTree.getSubItems(id);

			// 若子节点下没有子节点，即IDofAllChildren="",则为数据文件
			if (!IDofAllChildren) {
				// 根据节点ID获取节点的Text名称
				// var SelectName = myTree.getItemText(id);
				var DataID = {
					"ID" : id
				};
				var ChartType = "";

				// 图表区域和按钮的初始化
				$("#EchartBox").html("");
				chartsToolbar.setItemText('chartSelect', "图表选择");
				$("#subjectName").textbox('setValue', "");

				// 目录树单击时详细数据的获取及响应处理事件。
				_catalogTreeEvent(DataID, ChartType, -1);
			}
		});
	} else {
		dhtmlx.alert({
			// title:"提示框",
			type : "alert-warning",
			ok : "确定",
			text : "待开发！ ",
			callback : function() {
			}
		});
	}
});
/*
 * 详细数据面板工具条功能函数
 * 
 */
itemEvent.dataToolBar={};
/*
 * 表格数据转EXCEL
 */
itemEvent.dataToolBar.EXL=(function(myGrid){
	myGrid.toPDF('http://14.23.162.226:9090/grid-excel/generate', 'color');
});
/*
 * 图表面板工具条功能函数
 * 
 */
itemEvent.echartsToolBar = {};
// 图表选择按钮事件
/*
 * itemEvent.echartsToolBar.CS = (function(DataID) { var chartsSelect = new
 * dhtmlXPopup({ toolbar: chartsToolbar, id: "chartSelect" });
 * chartsSelect.attachObject("Chart-form"); //paramset = new dhtmlXPopup({
 * toolbar: chartsToolbar, id: "paramSetting" });
 * //paramset.attachObject("Param-form");
 * 
 * //图表选择事件 _chartSelect();
 * 
 * });
 */

/*
 * 参数设置按钮事件
 * 
 * @HeaderData @RecordsData @ChartType
 */
itemEvent.echartsToolBar.PS = (function(HeaderData, RecordsData, ChartType) {
	if (RecordsData) {
		if (ChartType) {
			document.getElementById("Param-form").style.display = "";
			$("#Param-form")
					.dialog(
							{
								title : "参数设置",
								iconCls : 'icon-paramConfigDialog',
								width : 400,
								height : 500,
								resizable : true,
								closed : false,
								cache : false,
								modal : true,
								buttons : [
										{
											text : "确定",
											iconCls : "icon-ok",
											handler : function() {
												var SubjectTitle = $(
														"#subjectName")
														.textbox('getValue');
												if (SubjectTitle == "") {
													dhtmlx.alert({
														// title:"提示框",
														type : "alert-warning",
														ok : "确定",
														text : "专题名称不能为空！ ",
														callback : function() {
														}
													});
												} else {
													// 表头行数
													// var NumberofROW =
													// tableProperty.rowsCalculate(HeaderData);
													var NumberofROW = ycount;
													// 数据指标地构建
													var IndexControl = indexStructure
															.indexInit(NumberofROW);
													var ColumnName = HeaderData[0].columnName;
													var xarray = new indexPadding.getxFirstIndex(
															RecordsData,
															HeaderData[0].columnName);
													// var
													// xNumberofROW=publicFunction.getXlevel(xarray);
													var xNumberofROW = xcount;
													var xIndexControl = indexStructure
															.xindexInit(xNumberofROW);
													/*
													 * 之前判断指标选择的条件，因为可以选取根节点，所以舍弃这种方法，直接判断选中了没有
													 * var result = 0; var
													 * xresult=0; for(var j=0;j<NumberofROW;j++) {
													 * result =
													 * indexStructure.indexJudge(IndexControl,j);
													 * if(result&&j!=NumberofROW-1)
													 * result=0; else break; }
													 * if(ChartType=="静态地图"||ChartType=="动态地图"){
													 * xresult =
													 * indexStructure.indexJudge(xIndexControl,0); }
													 * else{ for(var i=0;i<xNumberofROW;i++) {
													 * 
													 * xresult =
													 * indexStructure.indexJudge(xIndexControl,i);
													 * if(xresult&&i!=xNumberofROW-1){
													 * xresult=0;} else break; } }
													 * if(!result||!xresult)
													 */
													if (xcount == 0
															|| ycount == 0) {
														dhtmlx
																.alert({
																	// title:"提示框",
																	type : "alert-warning",
																	ok : "确定",
																	text : "指标选择不能为空！ ",
																	callback : function() {
																	}
																});
													}

													else {
														// 获取指标参数值
														var indexParam = indexStructure
																.indexSelectResult(
																		HeaderData,
																		IndexControl,
																		xIndexControl,
																		RecordsData);

														// 绘制图表
														drawChart.chartsInit(
																ChartType,
																HeaderData,
																RecordsData,
																indexParam,
																IndexControl,
																SubjectTitle);
														obj = drawChart
																.getObj();
														Num = drawChart
																.getNum();
														// 参数设置面板的关闭
														$("#Param-form")
																.dialog("close");
													}
												}
											}
										},
										{
											text : "取消",
											iconCls : "icon-cancel",
											handler : function() {
												$("#Param-form")
														.dialog("close");
											}
										} ]
							});
		} else {
		}
	} else {
	}
});

/*
 * 刷新按钮事件
 * 
 * @HeaderData @RecordsData @ChartType
 */
itemEvent.echartsToolBar.RF = (function(HeaderData, RecordsData, ChartType) {
	// 获取专题标题
	var SubjectTitle = $("#subjectName").textbox('getValue');
	var ColumnName = HeaderData[0].columnName;
	var xarray = indexPadding.getxFirstIndex(RecordsData, ColumnName);
	var xNumberofROW = publicFunction.getXlevel(xarray);
	var xIndexControl = indexStructure.xindexInit(xNumberofROW);
	// 表头行数
	var NumberofROW = tableProperty.rowsCalculate(HeaderData) + 1;
	// 数据指标地构建
	var IndexControl = indexStructure.indexInit(NumberofROW);
	// 获取指标参数值
	var indexParam = indexStructure.indexSelectResult(HeaderData, IndexControl,
			xIndexControl, RecordsData);
	// 绘制图表
	drawChart.chartsInit(ChartType, HeaderData, RecordsData, indexParam,
			IndexControl, SubjectTitle);
	obj = drawChart.getObj();
	Num = drawChart.getNum();
	/*
	 * var result = 0; for(var j=0;j<NumberofROW;j++) { result =
	 * _judgeIndex(IndexControl,j); if(result&&j!=NumberofROW-1) result=0; else
	 * break; } if(SubjectTitle!=""&&ChartType!=""&&result) { //获取指标参数值 var
	 * indexParam = indexStructure.indexSelectResult(HeaderData,IndexControl);
	 * 
	 * //绘制图表
	 * drawChart.chartsInit(ChartType,HeaderData,RecordsData,indexParam,IndexControl,SubjectTitle); }
	 * else{}
	 */
});
/*
 * 导出数据按钮事件
 * 
 * 
 */
//itemEvent.echartsToolBar.CSV = (function(obj, Num) {
//	if (obj != undefined && obj != "" && Num != 0) {
//		var row = "";
//		var CSV = "";
//		row += "指标" + ',';
//		for ( var i = 0; i < obj.yindexResult.length; i++) {
//			row += obj.yindexResult[i] + ',';
//		}
//		row = row.slice(0, -1);
//		CSV += row + '\r\n';
//		if (Num == 2) {// 转置
//			var arr = [];
//			var arr1 = [];
//			for ( var i = 0; i < obj.result[0].length; i++) {
//				arr[i] = [];
//			}
//			for ( var i = 0; i < obj.result.length; i++) {
//				for ( var j = 0; j < obj.result[i].length; j++) {
//					arr[j][i] = obj.result[i][j].value;
//				}
//			}
//			for ( var i = 0; i < obj.result[0].length; i++) {
//				arr1[i] = [];
//			}
//			for ( var i = 0; i < obj.result.length; i++) {
//				for ( var j = 0; j < obj.result[i].length; j++) {
//					arr1[j][i] = obj.result[i][j].name;
//				}
//			}
//			for ( var i = 0; i < arr.length; i++) {
//				var row = "";
//				var arr2 = arr1[i][0].split("\r\n");
//				for ( var k = 0; k < arr2.length; k++) {
//					row += '"' + arr2[k] + '"';
//				}
//				row += ',';
//				for ( var j = 0; j < arr[i].length; j++) {
//					row += '"' + arr[i][j] + '",';
//				}
//				row.slice(0, -1);
//				CSV += row + '\r\n';
//			}
//		} else if (Num == 1) {
//			var row = "";
//			for ( var i = 0; i < obj.result.length; i++) {
//				row += obj.result[i].name + ',';
//				row += obj.result[i].value + '\r\n';
//				;
//			}
//			// row.slice(0, -1);
//			CSV += row + '\r\n';
//		} else if (Num == 3) {
//			var row = "";
//			for ( var i = 0; i < obj.xindexResult.length; i++) {
//				row += obj.xindexResult[i].name + ',';
//				row += obj.xindexResult[i].value + '\r\n';
//			}
//			CSV += row + '\r\n';
//		}
//		if (CSV == '') {
//			alert("Invalid data");
//			return;
//		}
//		// 添加临时标签
//		var link = document.createElement("a");
//		link.id = "lnkDwnldLnk";
//		// 这部分将添加锚标签，并自动单击后删除它
//		document.body.appendChild(link);
//		var csv = CSV;
//		if (window.FileReader) {
//			blob = new Blob([ csv ], {
//				type : 'text/csv'
//			});
//			var csvUrl = window.URL.createObjectURL(blob);
//			var filename = 'UserExport.csv';
//			$("#lnkDwnldLnk").attr({
//				'download' : filename,
//				'href' : csvUrl
//			});
//
//			$('#lnkDwnldLnk')[0].click();
//			document.body.removeChild(link);
//		} else {
//			alert("该功能用于IE10或以上浏览器！");
//		}			
//		
//	}
//});
/*
 * 专题收藏按钮事件
 * 
 * @HeaderData @RecordsData @ChartType
 */
itemEvent.echartsToolBar.SC = (function(HeaderData, RecordsData, ChartType,
		DataID) {
	var Subjecttitle = $("#subjectName").textbox('getValue');
	if (Subjecttitle) {
		$.messager
				.confirm(
						'专题收藏',
						'确定收藏   ' + Subjecttitle + '  专题吗?',
						function(r) {
							if (r) {
								var nameOfSubject = [ {
									"subjectName" : Subjecttitle
								} ];
								$
										.post(
												'SubjectJudge.jsp',
												nameOfSubject[0],
												function(result) {
													var judgeResult = $
															.evalJSON(result);
													if (judgeResult.SubjectName != undefined) {
														dhtmlx
																.alert({
																	// title:"提示框",
																	type : "alert-warning",
																	ok : "确定",
																	text : "该专题已经存在，请修改专题名称！",
																	callback : function() {
																	}
																});
													} else {
														$
																.get(
																		'SubjectCount.jsp',
																		function(
																				result) {
																			SubjectCount = $
																					.evalJSON(result).length;
																			// var
																			// NumberofRow
																			// =
																			// _RowCount(HeaderData)+1;
																			var NumberofROW = tableProperty
																					.rowsCalculate(HeaderData) + 1;
																			var ColumnName = HeaderData[0].columnName;
																			var xarray = indexPadding
																					.getxFirstIndex(
																							RecordsData,
																							ColumnName);
																			var xNumberofROW = publicFunction
																					.getXlevel(xarray);
																			var firstIndex = "";
																			var secondIndex = "";
																			var thirdIndex = "";
																			var fourthIndex = "";
																			var fifthIndex = "";
																			var xfirstIndex = "";
																			var xsecondIndex = "";
																			var xthirdIndex = "";
																			var xfourthIndex = "";
																			var xfifthIndex = "";
																			// var
																			// ControlofIndex
																			// =
																			// _ControlSelect();
																			// var
																			// indexParam
																			// =
																			// _resultofIndexSelect(HeaderData,ControlofIndex);
																			// 数据指标地构建
																			var IndexControl = indexStructure
																					.indexInit(NumberofROW);
																			var xIndexControl = indexStructure
																					.xindexInit(xNumberofROW);
																			// 获取指标参数值
																			var indexParam = indexStructure
																					.indexSelectResult(
																							HeaderData,
																							IndexControl,
																							xIndexControl,
																							RecordsData);

																			switch (NumberofROW) {
																			case 1:
																				var controlofFirstIndex = document
																						.getElementsByName('firstIndex');
																				firstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				break;
																			case 2:
																				var controlofFirstIndex = document
																						.getElementsByName('firstIndex');
																				firstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('secondIndex');
																				secondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				break;
																			case 3:
																				var controlofFirstIndex = document
																						.getElementsByName('firstIndex');
																				firstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('secondIndex');
																				secondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				var controlofThirdIndex = document
																						.getElementsByName('thirdIndex');
																				thirdIndex = indexStructure
																						.getIndexValue(controlofThirdIndex);
																				break;
																			case 4:
																				var controlofFirstIndex = document
																						.getElementsByName('firstIndex');
																				firstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('secondIndex');
																				secondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				var controlofThirdIndex = document
																						.getElementsByName('thirdIndex');
																				thirdIndex = indexStructure
																						.getIndexValue(controlofThirdIndex);
																				var controlofFourthIndex = document
																						.getElementsByName('fourthIndex');
																				fourthIndex = indexStructure
																						.getIndexValue(controlofFourthIndex);
																				break;
																			case 5:
																				var controlofFirstIndex = document
																						.getElementsByName('firstIndex');
																				firstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('secondIndex');
																				secondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				var controlofThirdIndex = document
																						.getElementsByName('thirdIndex');
																				thirdIndex = indexStructure
																						.getIndexValue(controlofThirdIndex);
																				var controlofFourthIndex = document
																						.getElementsByName('fourthIndex');
																				fourthIndex = indexStructure
																						.getIndexValue(controlofFourthIndex);
																				var controlofFifthIndex = document
																						.getElementsByName('fifthIndex');
																				fifthIndex = indexStructure
																						.getIndexValue(controlofFifthIndex);
																				break;
																			}
																			switch (xNumberofROW) {
																			case 1:
																				var controlofFirstIndex = document
																						.getElementsByName('xfirstIndex');
																				xfirstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				break;
																			case 2:
																				var controlofFirstIndex = document
																						.getElementsByName('xfirstIndex');
																				xfirstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('xsecondIndex');
																				xsecondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				break;
																			case 3:
																				var controlofFirstIndex = document
																						.getElementsByName('xfirstIndex');
																				xfirstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('xsecondIndex');
																				xsecondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				var controlofThirdIndex = document
																						.getElementsByName('xthirdIndex');
																				xthirdIndex = indexStructure
																						.getIndexValue(controlofThirdIndex);
																				break;
																			case 4:
																				var controlofFirstIndex = document
																						.getElementsByName('xfirstIndex');
																				xfirstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('xsecondIndex');
																				xsecondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				var controlofThirdIndex = document
																						.getElementsByName('xthirdIndex');
																				xthirdIndex = indexStructure
																						.getIndexValue(controlofThirdIndex);
																				var controlofFourthIndex = document
																						.getElementsByName('xfourthIndex');
																				xfourthIndex = indexStructure
																						.getIndexValue(controlofFourthIndex);
																				break;
																			case 5:
																				var controlofFirstIndex = document
																						.getElementsByName('xfirstIndex');
																				xfirstIndex = indexStructure
																						.getIndexValue(controlofFirstIndex);
																				var controlofSecondIndex = document
																						.getElementsByName('xsecondIndex');
																				xsecondIndex = indexStructure
																						.getIndexValue(controlofSecondIndex);
																				var controlofThirdIndex = document
																						.getElementsByName('xthirdIndex');
																				xthirdIndex = indexStructure
																						.getIndexValue(controlofThirdIndex);
																				var controlofFourthIndex = document
																						.getElementsByName('xfourthIndex');
																				xfourthIndex = indexStructure
																						.getIndexValue(controlofFourthIndex);
																				var controlofFifthIndex = document
																						.getElementsByName('xfifthIndex');
																				xfifthIndex = indexStructure
																						.getIndexValue(controlofFifthIndex);
																				break;
																			}
																			var subjectInformation = [ {
																				"SubjectID" : SubjectCount + 1,
																				"subjectName" : Subjecttitle,
																				"chartType" : ChartType,
																				"data_id" : DataID.ID,
																				"index_first" : firstIndex,
																				"index_second" : secondIndex,
																				"index_third" : thirdIndex,
																				"index_fourth" : fourthIndex,
																				"index_fifth" : fifthIndex,
																				"xindex_first" : xfirstIndex,
																				"xindex_second" : xsecondIndex,
																				"xindex_third" : xthirdIndex,
																				"xindex_fourth" : xfourthIndex,
																				"xindex_fifth" : xfifthIndex
																			} ];
																			$
																					.post(
																							'SubjectSave2OracleTable.jsp',
																							subjectInformation[0],
																							function(
																									data) {
																								$.messager
																										.show({
																											title : '提示',
																											msg : '专题收藏成功！'
																										});
																								var refreshDataID = {
																									"ID" : subjectInformation[0].data_id
																								};
																								_subjectListLoad(refreshDataID);
																							});
																		});
													}
												});
							}
						});
	} else {
		/*
		 * dhtmlx.alert({ //title:"提示框", type:"alert-warning", ok:"确定",
		 * text:"请先绘制专题图！ ", callback:function(){} });
		 */
	}
});

/*
 * 数据表的构建
 */
itemEvent.tableGrid = {};
// 表数据表头格式的构建
itemEvent.tableGrid.HeaderStyleStructure = (function(HeaderData) {
	// return _GridHeaderData(HeaderData);
	return headerDataStructure.headerData(HeaderData);
});

// 表数据体数据格式的构建
itemEvent.tableGrid.RecordDataStyleStructure = (function(HeaderData,
		RecordsData) {
	return recordsDataStructure.recordsData(HeaderData, RecordsData);
});

function _judgeIndex(index, N) {
	var result = 0;
	for ( var i = 0; i < index[N].length; i++) {
		if (index[N][i].checked) {
			result = 1;
			break;
		}
	}
	return result;
}
