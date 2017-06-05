import com.acm.ComboCheck;
import com.acm.ComboCheckBox;
import com.acm.ComboCheckEvent;
import com.estudio.flex.component.IconButton;
import com.estudio.flex.component.Pagination;
import com.estudio.flex.component.SparkComboboxEx;
import com.estudio.flex.component.ToolbarVertline;
import com.estudio.flex.module.component.AdvDataGridColumnHrefRender;
import com.estudio.flex.module.component.AdvancedDataGridGroupItemRendererEx;
import com.estudio.flex.module.component.AdvancedDataGridWrap;
import com.estudio.flex.module.component.ChartPanel;
import com.estudio.flex.module.component.WinDiagram;
import com.estudio.flex.module.component.WinIdea;
import com.estudio.flex.module.component.WinSelectActivityAndUsers_bak;
import com.estudio.flex.module.component.WinSelectProcessType;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayCollectionUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;
import com.estudio.flex.utils.UIUtils;

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.net.navigateToURL;
import flash.ui.Keyboard;

import mx.collections.ArrayCollection;
import mx.collections.GroupingCollection2;
import mx.containers.Grid;
import mx.containers.VDividedBox;
import mx.controls.AdvancedDataGrid;
import mx.controls.DateField;
import mx.controls.LinkBar;
import mx.controls.LinkButton;
import mx.controls.Menu;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.controls.advancedDataGridClasses.FTEAdvancedDataGridItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.effects.easing.Back;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.DividerEvent;
import mx.events.DropdownEvent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
import mx.events.MenuEvent;
import mx.formatters.DateFormatter;
import mx.managers.SystemManager;

import spark.components.Button;
import spark.components.ComboBox;
import spark.components.Group;
import spark.components.Label;
import spark.components.TextInput;
import spark.events.IndexChangeEvent;

private var _iframeID:String="";
private var _tag:String="";
private var _queryList:ArrayCollection=null;
private var _filterComponent:Array=[];
private var _query2Define:Object={};
private var _lockFilterControlEvent:Boolean=false;
private var _isSameCondition:Boolean=false;
private var _isQueryButtonClicked:Boolean=false;

//------------------------------------------------------------------------------------------------------
//创建界面
public function initQueryList(items:Array, isSameCondition:Boolean):Boolean
{
	_isSameCondition=isSameCondition;
	_queryList=new ArrayCollection(items);
	this.currentState=_isSameCondition && items.length > 1 ? "groupStyle" : "splitStyle";
	return true;
}

//------------------------------------------------------------------------------------------------------
//刷新数据
public function refresh():void
{

}

//------------------------------------------------------------------------------------------------------
public function event4HrefLinkColumn(item:Object):void
{
	var href:String=item.href;
	if (StringUtils.startWith(href, "FUNGrid_"))
	{
		href=StringUtils.after(href, "FUNGrid_");
		IFrameUtils.execute(getIFrameID(), href, _query2Define[comboboxQuerys.selectedItem.id].grid.DataGrid.selectedItem);
	}
}

//------------------------------------------------------------------------------------------------------
//IFrame ID
public function getIFrameID():String
{
	return "iframe_" + _tag;
}

//------------------------------------------------------------------------------------------------------
//附加信息
public function get tag():String
{
	return _tag;
}

//------------------------------------------------------------------------------------------------------
//附加信息
public function set tag(value:String):void
{
	_tag=value;
}

//--------------------------------------------------------------------------------------------------------
protected function eventQueryButtonClick(event:Event):void
{
	_isQueryButtonClicked=true;
	var item:Object=_query2Define[comboboxQuerys.selectedItem.id];
	item.pagination.refresh();
}

//--------------------------------------------------------------------------------------------------------
protected function eventComboboxChange(event:IndexChangeEvent):void
{
	eventFilterInputKeyPress(null);
}

//--------------------------------------------------------------------------------------------------------
protected function eventComboboxCreateComplete(event:Event):void
{
	var cb:ComboBox=event.currentTarget as ComboBox;
	cb.textInput.editable=false;
}

//--------------------------------------------------------------------------------------------------------
private function eventFilterInputKeyPress(event:KeyboardEvent):void
{
	if (_lockFilterControlEvent)
		return;
	if (event == null || event.keyCode == Keyboard.ENTER)
	{
		eventQueryButtonClick(null);
	}
}

//--------------------------------------------------------------------------------------------------------
private function eventClearFilter(event:Event):void
{
	var item:Object=_query2Define[comboboxQuerys.selectedItem.id];
	for (var k:String in item.paramName2FilterControl)
	{
		var control:UIComponent=item.paramName2FilterControl[k];
		if (control is TextInput)
			TextInput(control).text="";
		else if (control is DateField)
			DateField(control).text="";
		else if (control is ComboBox)
		{
			var comb:ComboBox=control as ComboBox;
			comb.selectedIndex=-1;
			comb.selectedItem=null;
			comb.textInput.text="";
		}
	}
	callLater(function():void
	{
		eventQueryButtonClick(null);
	});
}

private function eventCustomozColumnComboboxOpen(event:Event):void
{
	var c:ComboBox=event.currentTarget as ComboBox;
	var items:ArrayCollection=c.dataProvider as ArrayCollection;
	for (var i:int=0; i < items.length; i++)
	{
		c.dropDown.width=Math.max(c.dropDown.width, this.measureText(items.getItemAt(i).label).width + 44);
	}
}

private function eventCustomcolumn(event:Event):void
{
	var queryDefine:Object=_query2Define[comboboxQuerys.selectedItem.id];
	queryDefine.btnCustomizColumn.component.openDropDown();
}

//--------------------------------------------------------------------------------------------------------
//模块创建完成事件
private function eventModuleCreateCompleted(event:Event):void
{
	_filterComponent=[{panel: this.filterPanel1, contain: this.groupFilter1}, {panel: this.filterPanel2, contain: this.groupFilter2}, {panel: this.filterPanel3, contain: this.groupFilter3}, {panel: this.filterPanel4, contain: this.groupFilter4}, {panel: this.filterPanel5, contain: this.groupFilter5}];
	if (_queryList.length != 0)
	{
		comboboxQuerys.selectedIndex=0;
		comboboxQuerys.selectedItem=_queryList.getItemAt(0);
		eventQueryTypeComboboxChange(null);
		var willWidth:int=20;
		for (var i:int=0; i < _queryList.length; i++)
		{
			var w:int=this.measureText(_queryList.getItemAt(i).label).width + 25;
			willWidth=Math.max(willWidth, w);
		}
		this.comboboxQuerys.width=willWidth + 20;
	}
	splitContain.addEventListener(DividerEvent.DIVIDER_RELEASE, eventDividerDrag);
}

//--------------------------------------------------------------------------------------------------------
private function eventDividerDrag(event:DividerEvent):void
{
	var box:VDividedBox=event.currentTarget as VDividedBox;
	var item:Object=_query2Define[comboboxQuerys.selectedItem.id];
	if (item.chart)
	{
		item.chart.eventChartResize(null);
	}
}

//--------------------------------------------------------------------------------------------------------
protected function eventQueryTypeComboboxChange(event:IndexChangeEvent):void
{
	var index:int=event == null ? 0 : event.newIndex;
	var item:Object=_queryList.getItemAt(index);
	var filterComponent:Object=null;
	var i:int=0;
	for (i=0; i < _filterComponent.length; i++)
	{
		filterComponent=_filterComponent[i];
		filterComponent.panel.height=0;
		filterComponent.contain.removeAllElements();
	}
	this.gridContain.removeAllElements();
	this.chartContain.removeAllElements();

	var id:String=item.id;
	var queryDefine:Object=_query2Define[id];
	var isFirstCreate:Boolean=false;
	if (!queryDefine)
	{
		isFirstCreate=true;

		queryDefine={};

		var json:Object=JSFunUtils.JSFun("getQueryGroupDefine", id);
		var define:Object=json.define;
		//选择项定义
		queryDefine.options=define.options;
		queryDefine.charts=define.charts;
		queryDefine.options.isChartAble=queryDefine.options.isChartAble && define.charts && define.charts.length != 0;
		if (queryDefine.options.isChartAble)
		{
			for (i=0; i < define.charts.length; i++)
			{
				define.charts[i].icon=imgChartIcons[define.charts[i].type];
				define.charts[i].label=imgChartIcons[define.charts[i].caption];
			}
		}


		//生成DataGrid
		var dataGrid:AdvancedDataGridWrap=new AdvancedDataGridWrap();
		dataGrid.isIncludeCheckBoxColumn=define.options.isGridIncludeCheckBox;
		dataGrid.isGroupAble=define.options.isGridGroupAble;
		dataGrid.columnDefine=define.columns;
		dataGrid.createColumns(this);
		dataGrid.doubleClickFunction=event4DataGridDoubleClick;
		dataGrid.isGridMultiSelectedAble=!define.options.isGridIncludeCheckBox && define.options.isGridMultiSelectedAble;
		queryDefine.grid=dataGrid;


		//生成分页控件
		var page:Pagination=new Pagination();
		queryDefine.pagination=page;
		page.portalGrid=this;
		if (!queryDefine.options.isGridPageAble)
			page.numberOfPerPage=2500;

		//生成查询控件
		var filterControlNumner:int=createFilterControls(define.params, queryDefine, define.toolbar);

		//生成统计图表控件
		if (queryDefine.options.isChartAble)
		{
			var chart:ChartPanel=new ChartPanel();
			chart.percentWidth=100;
			chart.percentHeight=100;
			queryDefine.chart=chart;
		}
		queryDefine.pagination.bottomBorder=queryDefine.options.isChartAble;

		var comboboxFilterValues:Object=json.filterComboboxItems;
		initComboboxFilterItems(comboboxFilterValues, queryDefine);

		//缓存
		_query2Define[id]=queryDefine;

		//判断是否有查询条件

		if (filterControlNumner == 0)
			eventQueryButtonClick(null);
	}

	this.gridContain.addElement(queryDefine.grid);
	if (queryDefine.chart)
	{
		this.chartContain.addElement(queryDefine.chart);
		this.chartContain.percentHeight=100;
		this.chartContain.visible=true;
		splitContain.setStyle("verticalGap", 5);
		lineGridBottom.visible=true;
		lineGridBottomColor.weight=1;
	}
	else
	{
		this.chartContain.percentHeight=0;
		this.chartContain.visible=false;
		splitContain.setStyle("verticalGap", 0);
		lineGridBottom.visible=false;
		lineGridBottomColor.weight=0;
	}

	//this.filterContain.height=queryDefine.searchPanelHeight;
	for (i=0; i < queryDefine.filterPanelVisibles.length; i++)
	{
		if (queryDefine.filterPanelVisibles[i])
		{
			filterComponent=_filterComponent[i];
			filterComponent.panel.height=30;
		}
	}

	if (!isFirstCreate)
	{
		for (i=0; i < queryDefine.filterControls.length; i++)
		{
			var groupFilter:Object=_filterComponent[queryDefine.filterControlPos[i]].contain;
			groupFilter.addElement(queryDefine.filterControls[i]);
		}
	}

	if (queryDefine.options.isGridPageAble)
		gridContain.addElement(queryDefine.pagination);
	else
		gridContain.addElement(lineGridBottom);
	//PaginationUI.height=queryDefine.options.isGridPageAble ? 30 : 0;
	//PaginationUI.visible=queryDefine.options.isGridPageAble ? true : false;
	//queryDefine.pagination.height=queryDefine.options.isGridPageAble ? 30 : 0;
	//queryDefine.pagination.visible=queryDefine.options.isGridPageAble;
	//IFrameUtils.createIFrameByHTML(getIFrameID(), queryDefine.html);
	IFrameUtils.createIFrameBySrc(getIFrameID(), "../client/querygrid.jsp?portalId=" + _tag + "&iframeId=" + getIFrameID() + "&uiid=" + id);
}

//-------------------------------------------------------------------------------------------
//绘制统计图
private function eventComboboxChartChange(event:IndexChangeEvent):void
{
	generalChart();
}

//-------------------------------------------------------------------------------------------
//创建过滤控件
private function createFilterControls(params:Array, queryDefine:Object, toolbar:Object):int
{
	var controlNumber:int=0;
	var paramName2FilterControl:Object={};
	queryDefine.paramName2FilterControl=paramName2FilterControl;
	queryDefine.filterPanelVisibles=[true, false, false, false, false];
	queryDefine.filterControls=[];
	queryDefine.filterControlPos=[];
	queryDefine.filterComboboxItems=[];
	var groupFilter:Object=_filterComponent[0].contain;
	var lastControlPos:int=0;

	if (params == null)
		params=[];
	var i:int=0;
	for (i=0; i < params.length; i++)
	{
		var param:Object=params[i];
		if (param.pos == 0)
			param.pos=1;
		lastControlPos=param.pos - 1;
		queryDefine.filterPanelVisibles[param.pos - 1]=true;
		groupFilter=_filterComponent[param.pos - 1].contain;
		var labelStr:String=param.comment;
		var filterControl:String=param.filterControl;
		var paramName:String=param.name;
		var control:UIComponent=null;
		if (StringUtils.equal("Date", filterControl))
		{
			if (!StringUtils.isEmpty(labelStr))
			{
				queryDefine.filterControls.push(createFilterLabel(groupFilter, labelStr));
				queryDefine.filterControlPos.push(param.pos - 1);
			}

			var datefield:DateField=new DateField();
			datefield.editable=false;
			datefield.formatString="YYYY-MM-DD";
			datefield.yearNavigationEnabled=true;
			//datefield.addEventListener(CalendarLayoutChangeEvent.CHANGE, eventQueryButtonClick);
			datefield.height=23;
			groupFilter.addElement(datefield);
			control=datefield;

		}
		else if (StringUtils.equal("TextBox", filterControl))
		{
			if (!StringUtils.isEmpty(labelStr))
			{
				queryDefine.filterControls.push(createFilterLabel(groupFilter, labelStr));
				queryDefine.filterControlPos.push(param.pos - 1);
			}
			var textInput:TextInput=new TextInput();
			textInput.width=param["controlWidth"] ? param["controlWidth"] * 1 : 120;
			textInput.height=23;
			textInput.addEventListener(KeyboardEvent.KEY_DOWN, eventFilterInputKeyPress);
			groupFilter.addElement(textInput);
			control=textInput;
		}
		else if (StringUtils.equal("ComboBox", filterControl))
		{
			if (!StringUtils.isEmpty(labelStr))
			{
				queryDefine.filterControls.push(createFilterLabel(groupFilter, labelStr));
				queryDefine.filterControlPos.push(param.pos - 1);
			}
			var combobox:ComboBox=new ComboBox();
			combobox.labelField="LABEL";
			combobox.height=23;
			combobox.width=param["controlWidth"] ? param["controlWidth"] * 1 : 120;
			groupFilter.addElement(combobox);
			//combobox.addEventListener(IndexChangeEvent.CHANGE, eventComboboxChange);
			combobox.addEventListener(FlexEvent.CREATION_COMPLETE, eventComboboxCreateComplete);
			control=combobox;
			queryDefine.filterComboboxItems.push({combobox: combobox, paramName: paramName, isExistsParent: params[i].isExistsParent});
			combobox.addEventListener(IndexChangeEvent.CHANGE, event4FilterComboboxChange);
		}
		if (control != null)
		{
			queryDefine.filterControls.push(control);
			queryDefine.filterControlPos.push(param.pos - 1);
			paramName2FilterControl[paramName]=control;
			controlNumber++;
		}
	}

	var btnWidth:int=queryDefine.options.isGridPageAble ? 24 : 23;
	var isSplitCreated:Boolean=false;
	if (params.length != 0)
	{
		//创建查询按钮
		var btn:IconButton=new IconButton();
		btn.iconURL="../images/18x18/search.png";
		btn.toolTip="点击查询过滤数据";
		btn.addEventListener(MouseEvent.CLICK, eventQueryButtonClick);
		groupFilter.addElement(btn);
		queryDefine.filterControls.push(btn);
		queryDefine.filterControlPos.push(param.pos - 1);

		var btn:IconButton=new IconButton();
		btn.iconURL="../images/18x18/filter_delete0.png";
		btn.toolTip="清除过滤条件";
		btn.addEventListener(MouseEvent.CLICK, eventClearFilter);
		groupFilter.addElement(btn);
		queryDefine.filterControls.push(btn);
		queryDefine.filterControlPos.push(param.pos - 1);
	}

	//下面代码生成附加工具条
	//var toolbars:Array = 

	var searchPanelHeight:int=0;
	for (i=0; i < queryDefine.filterPanelVisibles.length; i++)
	{
		searchPanelHeight+=28 * (queryDefine.filterPanelVisibles[i] ? 1 : 0);
	}
	queryDefine.searchPanelHeight=searchPanelHeight;


	//创建工具条按钮
	if (queryDefine.options.isGridExportExcelAble) //导出Excel
	{
		var btn:IconButton=new IconButton();
		btn.iconURL="../images/flex/page_excel.png";
		btn.toolTip="导出Excel";
		btn.addEventListener(MouseEvent.CLICK, eventExportToExcel);
		groupFilter.addElement(btn);
		queryDefine.filterControls.push(btn);
		queryDefine.filterControlPos.push(lastControlPos);

		if (queryDefine.options.isGridPageAble)
		{
			queryDefine.pagination.contain.addElementAt(new ToolbarVertline(), queryDefine.pagination.contain.numElements - 2);
			queryDefine.pagination.contain.addElementAt(btn, queryDefine.pagination.contain.numElements - 2);
		}
		else
		{
			var split:UIComponent=new ToolbarVertline();
			queryDefine.filterControls.push(split);
			queryDefine.filterControlPos.push(lastControlPos);
			groupFilter.addElementAt(split, groupFilter.numElements - 1);

			queryDefine.filterControls.push(btn);
			queryDefine.filterControlPos.push(lastControlPos);
		}
		isSplitCreated=true;
	}

	//自定义工具条
	var toolbarItems:Array=toolbar && toolbar.hasOwnProperty("Items") ? toolbar.Items : [];
	for (var i:int=0; i < toolbarItems.length; i++)
	{
		var item:Object=toolbarItems[i];
		var btn:IconButton=new IconButton();
		btn.toolTip=item["Caption"];
		btn.iconURL="../images/18x18/" + StringUtils.replace(item["Icon"], ".bmp", ".png");
		btn.params={fun: StringUtils.replace(StringUtils.replace(item["Function"], "[", ""), "]", "")};
		btn.addEventListener(MouseEvent.CLICK, event4AdditionToolbarItemClick);
		groupFilter.addElement(btn);
		queryDefine.filterControls.push(btn);
		queryDefine.filterControlPos.push(lastControlPos);
	}

	//自定义数据列
	queryDefine.customColumns=queryDefine.grid.custonizColumns;
	if (queryDefine.options.isGridSupportCustomizColumns && queryDefine.customColumns.length)
	{
		var group:Group=new Group();
		group.width=btnWidth;
		group.height=btnWidth;
		groupFilter.addElement(group);


		if (queryDefine.options.isGridPageAble)
		{
			if (!isSplitCreated)
				queryDefine.pagination.contain.addElementAt(new ToolbarVertline(), queryDefine.pagination.contain.numElements - 2);
			queryDefine.pagination.contain.addElementAt(group, queryDefine.pagination.contain.numElements - 2);
		}
		else
		{
			if (!isSplitCreated)
			{
				var split:UIComponent=new ToolbarVertline();
				queryDefine.filterControls.push(split);
				queryDefine.filterControlPos.push(lastControlPos);
				groupFilter.addElementAt(split, groupFilter.numElements - 1);
			}

			queryDefine.filterControls.push(group);
			queryDefine.filterControlPos.push(lastControlPos);
		}

		var cbColumns:ComboCheck=new ComboCheck();
		cbColumns.type="combobox";
		cbColumns.width=btnWidth - 2;
		cbColumns.height=btnWidth - 2;
		cbColumns.minWidth=btnWidth - 2;
		cbColumns.labelField="label";
		cbColumns.dataProvider=queryDefine.customColumns;
		cbColumns.visible=false;
		(cbColumns.component as ComboBox).addEventListener(DropdownEvent.OPEN, eventCustomozColumnComboboxOpen);
		cbColumns.addEventListener(ItemClickEvent.ITEM_CLICK, eventColumnItemClick);

		group.addElement(cbColumns);
		queryDefine.btnCustomizColumn=cbColumns;

		btn=new IconButton();
		btn.iconURL="../images/flex/application_view_columns.png";
		btn.toolTip="自定义数据列";
		btn.addEventListener(MouseEvent.CLICK, eventCustomcolumn);
		group.addElement(btn);
	}



	//自定义图表
	if (queryDefine.options.isChartAble)
	{
		var cbChart:SparkComboboxEx=new SparkComboboxEx();
		cbChart.labelField="caption";
		cbChart.dataProvider=new ArrayCollection(queryDefine.charts);
		applyBestComboBoxWidth(cbChart);
		cbChart.addEventListener(IndexChangeEvent.CHANGE, eventComboboxChartChange);
		cbChart.addEventListener(FlexEvent.CREATION_COMPLETE, eventComboboxCreateComplete);
		cbChart.selectedIndex=0;
		queryDefine.chartCombobox=cbChart;

		if (queryDefine.options.isGridPageAble)
		{
			queryDefine.pagination.contain.addElementAt(new ToolbarVertline(), queryDefine.pagination.contain.numElements - 2);

			var label:Label=new Label();
			label.text="统计图";
			queryDefine.pagination.contain.addElementAt(label, queryDefine.pagination.contain.numElements - 2);
			queryDefine.pagination.contain.addElementAt(cbChart, queryDefine.pagination.contain.numElements - 2);
		}
		else
		{

			var split:UIComponent=new ToolbarVertline();
			queryDefine.filterControls.push(split);
			queryDefine.filterControlPos.push(lastControlPos);
			groupFilter.addElementAt(split, groupFilter.numElements);

			var label:Label=new Label();
			label.text="统计图";
			label.setStyle("fontWeight", "bold");
			queryDefine.filterControls.push(label);
			queryDefine.filterControlPos.push(lastControlPos);
			groupFilter.addElement(label);

			queryDefine.filterControls.push(cbChart);
			queryDefine.filterControlPos.push(lastControlPos);
			groupFilter.addElement(cbChart);
		}

	}
	return controlNumber;
}

////////////////////////////////////////////////////////////////////////////////////////////////
//更新事件
private function event4FilterComboboxChange(event:IndexChangeEvent):void
{
	var queryDefine:Object=_query2Define[comboboxQuerys.selectedItem.id];
	var filterComboboxList:Array=queryDefine.filterComboboxItems;
	var pCombobox:ComboBox=event.currentTarget as ComboBox;
	var reloadCombobox:ComboBox=null;
	var firstIndex:int=1;
	for (var i:int=0; i < filterComboboxList.length; i++)
	{
		if (filterComboboxList[i].combobox == pCombobox)
		{
			firstIndex=i + 1;
			break;
		}
	}
	for (var i:int=firstIndex; i < filterComboboxList.length; i++)
	{
		if (filterComboboxList[i - 1].combobox == pCombobox && filterComboboxList[i].isExistsParent)
		{
			pCombobox=filterComboboxList[i].combobox as ComboBox;
			pCombobox.selectedItem=null;
			pCombobox.textInput.text="";
			pCombobox.selectedIndex=-1;
			pCombobox.dataProvider.removeAll();
			if (reloadCombobox == null)
				reloadCombobox=pCombobox;
		}
		else
		{
			break;
		}
	}
	if (reloadCombobox)
		dyanmicLoadFilterComboboxItems(reloadCombobox);
}

////////////////////////////////////////////////////////////////////////////////////////////////
private function dyanmicLoadFilterComboboxItems(combobox:ComboBox):void
{
	var queryDefine:Object=_query2Define[comboboxQuerys.selectedItem.id];
	var filterComboboxList:Array=queryDefine.filterComboboxItems;
	if (combobox.dataProvider.length == 0)
	{
		for (var i:int=1; i < filterComboboxList.length; i++)
		{
			if (filterComboboxList[i].combobox == combobox && filterComboboxList[i].isExistsParent)
			{
				var pItem:Object=filterComboboxList[i - 1].combobox.selectedItem;
				if (pItem)
				{
					var params:Object={PARENT_COMBOBOX: pItem.ID, paramName: filterComboboxList[i].paramName};
					params.id=comboboxQuerys.selectedItem.id;
					var json:Object=JSFunUtils.JSFun("getQueryControlFilterComboboxItems", params);
					if (json)
					{
						if (!json.records)
							json.records=[{ID: "", LABEL: ""}];
						combobox.dataProvider=new ArrayCollection(json.records);
						var maxWidth:int=40;
						for (var j:int=0; j < json.records.length; j++)
						{
							var r:Object=json.records[j];
							maxWidth=Math.max(measureText(r.LABEL).width + 40, maxWidth);
						}
						combobox.width=maxWidth;
						combobox.invalidateDisplayList();
						combobox.validateNow();
					}
				}
				break;
			}
		}
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////
private function event4AdditionToolbarItemClick(event:MouseEvent):void
{
	var btn:IconButton=event.currentTarget as IconButton;
	var params:Object=btn.params;
	IFrameUtils.execute(getIFrameID(), params.fun, ObjectUtils.mergeParams(_query2Define[comboboxQuerys.selectedItem.id].grid.DataGrid.selectedItem, getFilterParams()));
}

//---------------------------------------------------------------------------------------------
//导出Excel
private function eventExportToExcel(event:Event):void
{
	var filterParams:Object=getFilterParams();
	var filterFields:Array=[];

	var queryDefine:Object=_query2Define[comboboxQuerys.selectedItem.id];
	var grid:AdvancedDataGridWrap=queryDefine.grid;
	for (var i:int=0; i < grid.allFieldColumns.length; i++)
	{
		var column:AdvancedDataGridColumn=AdvancedDataGridColumn(grid.allFieldColumns[i]);
		if (column.visible || !queryDefine.options.isGridExportExcelOnlyVisiualColumn)
		{
			var fieldName:String=column.dataField;
			if (!StringUtils.isEmpty(fieldName))
				filterFields.push(fieldName);
		}
	}

	var datas:Object=JSFunUtils.JSFun("exportExcel", {id: comboboxQuerys.selectedItem.id, r: 32767, p: 1, filterParams: filterParams, filterFields: filterFields.join(";")});
	if (datas && datas.r)
		FlexGlobals.topLevelApplication.goURL(datas.url);
}
//---------------------------------------------------------------------------------------------


[Embed(source="assets/common/search.png")] //过滤
[Bindable]
private var imgFilter:Class;

[Embed(source="assets/common/ico_clear.png")] //编辑表单
[Bindable]
private var imgClearFilter:Class;

[Embed(source="assets/common/columns.png")] //编辑表单
[Bindable]
private var imgCustomizColumn:Class;

[Embed(source="assets/common/importexcel.png")] //编辑表单
[Bindable]
private var imgExportExcel:Class;

[Embed(source="assets/common/chart_1.png")] //编辑表单
[Bindable]
private var imgChart_0:Class;

[Embed(source="assets/common/chart_2.png")] //编辑表单
[Bindable]
private var imgChart_1:Class;

[Embed(source="assets/common/chart_3.png")] //编辑表单
[Bindable]
private var imgChart_2:Class;

private var imgChartIcons:Array=[imgChart_0, imgChart_1, imgChart_2];

//-------------------------------------------------------------------------------------------------
private function createFilterLabel(groupFilter:Object, labelStr:String):Label
{
	var label:Label=new Label();
	label.text=labelStr;
	label.verticalCenter=0;
	label.setStyle("fontWeight", "bold");
	groupFilter.addElement(label);
	return label;
}

//--------------------------------------------------------------------------------------------------
//查询
public function goPage(recordPrePage:int, page:int):void
{
	var filterParams:Object=getFilterParams();
	var datas:Object=JSFunUtils.JSFun("queryRecords", {id: comboboxQuerys.selectedItem.id, r: recordPrePage, p: page, filterParams: filterParams});
	if (datas && datas.r)
	{
		var item:Object=_query2Define[comboboxQuerys.selectedItem.id];

		item.grid.initData(datas.rows);
		//totalPage:int, totalRecord:int, currentPage:int
		item.pagination.updatePages(datas.t, datas.tr, datas.p);

		//var comboboxFilterValues:Object=datas.filterComboboxItems;
		//initComboboxFilterItems(comboboxFilterValues,item);

		/*
		_lockFilterControlEvent=true;
		for (var k:String in comboboxFilterValues)
		{
			var combobox:ComboBox=item.paramName2FilterControl[k] as ComboBox;
			if (combobox)
			{
				var id:String=combobox.selectedItem ? combobox.selectedItem.ID : "";
				combobox.dataProvider=new ArrayCollection(comboboxFilterValues[k]);
				var index:int=ArrayCollectionUtils.indexOf(combobox.dataProvider as ArrayCollection, "ID", id);
				combobox.selectedIndex=index == -1 ? 0 : index;
			}
			else
			{
				item.filterComboboxValues[k]=comboboxFilterValues[k];
			}
		}
		_lockFilterControlEvent=false;
		*/
		generalChart(); //生成图表

	}
}

//--------------------------------------------------------------------------------------------
private function initComboboxFilterItems(comboboxFilterValues:Object, queryDefine:Object):void
{
	_lockFilterControlEvent=true;
	for (var k:String in comboboxFilterValues)
	{
		var combobox:ComboBox=queryDefine.paramName2FilterControl[k] as ComboBox;
		if (combobox)
		{
			var id:String=combobox.selectedItem ? combobox.selectedItem.ID : "";
			combobox.dataProvider=new ArrayCollection(comboboxFilterValues[k]);
			var index:int=ArrayCollectionUtils.indexOf(combobox.dataProvider as ArrayCollection, "ID", id);
			combobox.selectedIndex=index == -1 ? 0 : index;
				//applyBestComboBoxWidth(combobox);
		}
		else
		{
			queryDefine.filterComboboxValues[k]=comboboxFilterValues[k];
		}
	}
	_lockFilterControlEvent=false;
}

//--------------------------------------------------------------------------------------------
private function applyBestComboBoxWidth(combobox:ComboBox):void
{
	if (combobox && combobox.dataProvider)
	{
		var minWidth:int=0;
		for (var i:int=0; i < combobox.dataProvider.length; i++)
		{
			var item:Object=combobox.dataProvider.getItemAt(i);
			if (item && item.hasOwnProperty(combobox.labelField))
				minWidth=Math.max(measureText(item[combobox.labelField]).width, minWidth);
		}
		combobox.width=minWidth + 40;
		combobox.invalidateSize();
	}
}

//--------------------------------------------------------------------------------------------
//获取过滤参数
private function getFilterParams():Object
{
	var item:Object=_query2Define[comboboxQuerys.selectedItem.id];
	var result:Object={};
	for (var k:String in item.paramName2FilterControl)
	{
		var control:UIComponent=item.paramName2FilterControl[k];
		var value:String="";
		if (control is TextInput)
			value=StringUtils.trim(TextInput(control).text);
		else if (control is DateField)
			value=DateField(control).text;
		else if (control is ComboBox)
			value=ComboBox(control).selectedItem ? ComboBox(control).selectedItem.ID : null;
		result[k]=value;
	}
	return result;
}

////////////////////////////////////////////////////////////////////////////////////////////////
public function setFilterValues(record:Object):void
{
	eventClearFilter(null);
	var item:Object=_query2Define[comboboxQuerys.selectedItem.id];


	for (var k:String in record)
	{
		var control:UIComponent=item.paramName2FilterControl[k];
		if (!control)
			continue;
		var paramValue:String=record[k];

		if (control is TextInput)
		{
			(TextInput(control)).text=paramValue;
			TextInput(control).invalidateDisplayList();
			TextInput(control).validateDisplayList();
			TextInput(control).validateNow();
		}
		else if (control is DateField)
		{
			DateField(control).selectedDate=Convert.str2DateTime(paramValue, null);
			DateField(control).invalidateDisplayList();
			DateField(control).validateDisplayList();
			DateField(control).validateNow();
		}
		else if (control is ComboBox)
		{
			var combo:ComboBox=control as ComboBox;
			var items:Array=ArrayCollection(combo.dataProvider).source;
			var index:int=ArrayUtils.find(items, "LABEL", paramValue);
			if (index == -1)
				index=ArrayUtils.find(items, "ID", paramValue);
			if (index != -1)
			{
				combo.selectedIndex=index;
				combo.selectedItem=combo.dataProvider.getItemAt(index);
				combo.invalidateDisplayList();
				combo.validateDisplayList();
				combo.validateNow();
			}
		}
	}
	this.callLater(function():void
	{
		eventQueryButtonClick(null);
	});
}

//--------------------------------------------------------------------------------------------
private function eventColumnItemClick(event:ItemClickEvent):void
{
	var data:Object=event.item;
	data.column.visible=data.selected;
}

//--------------------------------------------------------------------------------------------
//生成图表数据
private function generalChart():void
{
	var item:Object=_query2Define[comboboxQuerys.selectedItem.id];
	if (!item.chartCombobox)
		return;
	var cb:ComboBox=item.chartCombobox;
	var chartParams:Object=cb.selectedItem;
	var datas:ArrayCollection=item.grid.DataGrid.dataProvider;
	item.chart.buildChartData(chartParams, datas);
}

//--------------------------------------------------------------------------------------------
private function event4DataGridDoubleClick():void
{
	var item:Object=_query2Define[comboboxQuerys.selectedItem.id].grid.DataGrid.selectedItem;
	if (item && !item.children)
	{
		var newObject:Object={};
		IFrameUtils.execute(getIFrameID(), "QUERY_ON_DOUBLECLICK", item);
	}
}
//////////////////////////////////////////////////////////////////////////////////////////////
