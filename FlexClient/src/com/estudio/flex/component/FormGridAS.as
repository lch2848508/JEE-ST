import com.estudio.flex.common.InterfaceFormUI;
import com.estudio.flex.component.FormGrid;
import com.estudio.flex.component.UploadExcelWindow;
import com.estudio.flex.component.UploadExcelWindow4DataGridImport;
import com.estudio.flex.component.mx.datagrid.render.AttachmentHrefRender4FormGrid;
import com.estudio.flex.component.mx.datagrid.render.ButtonItemRender;
import com.estudio.flex.component.mx.datagrid.render.ButtonTextInputEditor;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxEditor;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxHeaderRender;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxRender;
import com.estudio.flex.component.mx.datagrid.render.ComboBoxEditor;
import com.estudio.flex.component.mx.datagrid.render.ComboBoxItemRender;
import com.estudio.flex.component.mx.datagrid.render.CommonHeaderRender;
import com.estudio.flex.component.mx.datagrid.render.CommonItemRender;
import com.estudio.flex.component.mx.datagrid.render.DateFieldEditor;
import com.estudio.flex.component.mx.datagrid.render.HrefRender4FormGrid;
import com.estudio.flex.component.mx.datagrid.render.IndiationItemRender;
import com.estudio.flex.component.mx.datagrid.render.MemoEditor;
import com.estudio.flex.component.mx.datagrid.render.MemoItemRender;
import com.estudio.flex.component.mx.datagrid.render.TextInputEditor;
import com.estudio.flex.module.FormDataService;
import com.estudio.flex.utils.AjaxUtils;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayCollectionUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.StringUtils;

import flash.events.MouseEvent;
import flash.net.FileFilter;

import mx.collections.ArrayCollection;
import mx.controls.ButtonBar;
import mx.controls.ComboBox;
import mx.controls.DateField;
import mx.controls.TextInput;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.core.ClassFactory;
import mx.core.FlexGlobals;
import mx.events.DataGridEvent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
import mx.events.ListEvent;
import mx.managers.FocusManager;
import mx.utils.ObjectUtil;

import spark.components.gridClasses.ComboBoxGridItemEditor;

private var _gridParams:Object=null;

private var _gridData:ArrayCollection=null;

private var _gridKeyField:String="";

private var _bindForms:Object=null;

private var _gridColumnFields:Array=[];


private var _eventOnModifyFunName:String="";
private var _eventOnAddFunName:String="";
private var _eventOnDeleteFunName:String="";
private var _eventOnExtButtonClick:String="";
private var _eventOnSelectFunName:String="";
private var _eventOnDoubleClickFunName:String="";

private var _eventFields:Array=[];

private var _columnFields:Array=[];
private var _columnField2Keys:Object={};

public var lockEvent:Boolean=false;

public var checkBoxColumnHeaders:Array=[];
public var showToolbar:Boolean=false;
private var _gridColumnDefine:Object=null;
private var _gridColumn2ComboboxProperty:Object={};

private var _addEnabled:Boolean=true;
private var _delEnabled:Boolean=true;
public var supportAdd:Boolean=true;
public var supportDelete:Boolean=true;
public var supportExport:Boolean;
public var extButtons:Array=[];

/////////////////////////////////////////////////////////////////////////////////////////////
//数据绑定
public function set bindForm(value:String):void
{
	if (StringUtils.isEmpty(value))
		return;
	_bindForms={formids: [], params: []};
	var paramNames:Array=[];
	var xml:XML=new XML(value);
	var bindForms:XMLList=xml.BindForms["Form"] as XMLList;
	for each (var bindForm:XML in bindForms)
	{
		var formID:String=bindForm.@ID;
		_bindForms.formids.push(formID);
		for each (var param:XML in bindForm.Params.Param)
		{
			var paramName:String=param.@Name;
			if (paramNames.indexOf(paramName) == -1)
			{
				paramNames.push(paramName);
				var paramValue:String=com.estudio.flex.utils.StringUtils.between(param.@Value, "[", "]");
				_bindForms.params.push({name: paramName, value: paramValue});
			}
		}
	}
}




////////////////////////////////////////////////////////////////////////////////////////////
//创建界面
public function createUI(uiParams:Object):void
{
	_gridParams=uiParams;
	controlParams.databaseName=uiParams.DataSource;
	var columns:Array=(uiParams.Column is Array) ? uiParams.Column : [uiParams.Column];
	createGridColumns(columns);
	_gridColumnDefine=columns;
	_eventOnModifyFunName=uiParams.OnModify;
	_eventOnAddFunName=uiParams.OnAdd;
	_eventOnDeleteFunName=uiParams.OnDelete;
	_eventOnSelectFunName=uiParams.OnSelect;
	_eventOnDoubleClickFunName=uiParams.OnDoubleClick;
	_eventOnExtButtonClick=uiParams.OnExtButtonClick;
}

////////////////////////////////////////////////////////////////////////////////////////////
//初始化数据定义
public function initFormDataService(value:FormDataService):void
{
	controlParams.dataservice=value;
}

////////////////////////////////////////////////////////////////////////////////////////////
//获取列字段列表
public function get gridColumnFields():Array
{
	return _gridColumnFields;
}

//////////////////////////////////////////////////////////////////////////////////////////////
//初始化数据定义
private var INIT_DATA_SELECTED_INDEX:int=0;

public function initData(selectedIndex:int=0):void
{
	if (isBindDatasource)
	{
		if (StringUtils.isEmpty(_gridKeyField))
			_gridKeyField=controlParams.dataservice.getDataSetKeyField(controlParams.databaseName);
		_gridData=controlParams.dataservice.getArrayCollection(controlParams.databaseName);
		this.grid.dataProvider=_gridData;
		INIT_DATA_SELECTED_INDEX=selectedIndex;
		this.grid.callLater(gridCallLaterFunction);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////
private function gridCallLaterFunction():void
{
	if (_gridData.length != 0 && INIT_DATA_SELECTED_INDEX < _gridData.length)
	{
		this.grid.selectedIndex=INIT_DATA_SELECTED_INDEX;
		eventOnGridItemClickHandler(null);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////
public function refresh():void
{
	if (isBindDatasource && _gridData)
	{
		var position:Number=this.grid.verticalScrollPosition;
		var positionH:Number=this.grid.horizontalScrollPosition;
		var selectedRecord:Object=this.grid.selectedItem;
		_gridData.refresh();
		this.grid.dataProvider=_gridData;
		this.grid.selectedItem=selectedRecord;
		controlParams.dataservice.setDataSetRecordIndexByItem(controlParams.databaseName, selectedItem);
		//controlParams.dataservice.setDataSetRecordIndexByItem(controlParams.databaseName, selectedItem);
		if (!lockEvent)
			formInstance["eventOnDataSourceSelectChangedEvent"](controlParams.databaseName, this, FormDataService.DATASET_SELECTED_CHANGE_TYPE_NORMAL, null);
		this.grid.verticalScrollPosition=position;
		this.grid.horizontalScrollPosition=positionH;
		this.grid.invalidateList();
		this.grid.validateNow();
	}
}

////////////////////////////////////////////////////////////////////////////////////////////
//创建数据列表
private function createGridColumns(columns:Array):void
{
	var gridColumns:Array=this.grid.columns;

	var indColumn:DataGridColumn=new DataGridColumn();
	indColumn.itemRenderer=new ClassFactory(IndiationItemRender);
	indColumn.minWidth=16;
	indColumn.width=16;
	indColumn.resizable=false;
	indColumn.editable=false;

	gridColumns.push(indColumn);

	var lockGridColumn:int=-1;

	for (var i:int=0; i < columns.length; i++)
	{
		var column:Object=columns[i];

		var gridColumn:DataGridColumn=new DataGridColumn();
		gridColumn.headerText=column.Caption;
		gridColumn.minWidth=0; //
		gridColumn.width=Math.max(gridColumn.minWidth, column.Width);
		gridColumn.setStyle("textAlign", column.Alignment.toLowerCase());

		gridColumn.dataField=StringUtils.between(column.FieldName, "[", "]");

		_columnFields.push(gridColumn.dataField);

		_gridColumnFields.push(gridColumn.dataField);
		if (Convert.object2Boolean(column.TriggerEvent, false))
			_eventFields.push(gridColumn.dataField);

		var editor:ClassFactory=null;
		if (column.ControlType == "TextBox")
		{ //OK
			gridColumn.headerRenderer=new ClassFactory(CommonHeaderRender);
			gridColumn.itemRenderer=new ClassFactory(CommonItemRender);
			editor=new ClassFactory(TextInputEditor);
			editor.properties={readonly: this.readonly, dataField: gridColumn.dataField, datasetName: this.databaseName, formDataService: this.dataservice, formInstance: this.formInstance, grid: this};
			gridColumn.itemEditor=editor;

		}
		else if (column.ControlType == "Date" || column.ControlType == "DateTime") //日期选择框 OK
		{
			gridColumn.headerRenderer=new ClassFactory(CommonHeaderRender);
			var render:ClassFactory=new ClassFactory(CommonItemRender);
			render.properties={fontName: "Courier New", fontSize: 12};
			gridColumn.itemRenderer=render;
			editor=new ClassFactory(DateFieldEditor);
			editor.properties={isIncludeTime: column.ControlType == "DateTime", readonly: this.readonly, dataField: gridColumn.dataField, datasetName: this.databaseName, formDataService: this.dataservice, formInstance: this.formInstance, grid: this};
			gridColumn.itemEditor=editor;
			gridColumn.width=FlexGlobals.topLevelApplication.measureTextWidth(column.ControlType == "DateTime" ? "0000-00-00 00:00:00" : "0000-00-00") + 50;
			gridColumn.resizable=false;
		}
		else if (column.ControlType == "ComboBox")
		{ //OK
			gridColumn.headerRenderer=new ClassFactory(CommonHeaderRender);
			var comboboxProperty:Object=column.ComboBox[0];
			comboboxProperty.displayField=StringUtils.between(comboboxProperty.DisplayField, "[", "]");
			comboboxProperty.parentField=StringUtils.between(comboboxProperty.ParentField, "[", "]");
			comboboxProperty.valueField=StringUtils.between(comboboxProperty.ValueField, "[", "]");
			comboboxProperty.displayFieldEx=StringUtils.between(column.DisplayFieldName, "[", "]");
			comboboxProperty.isDataProviderFixed=!StringUtils.isEmpty(comboboxProperty.displayFieldEx);
			comboboxProperty.datasetName=controlParams.databaseName;
			comboboxProperty.dataField=gridColumn.dataField;
			comboboxProperty.comboboxItems=comboboxProperty.Items[0].Item;

			editor=new ClassFactory(ComboBoxItemRender);
			editor.properties={comboboxProperty: comboboxProperty, formDataService: controlParams.dataservice};
			gridColumn.itemRenderer=editor;

			editor=new ClassFactory(ComboBoxEditor);
			editor.properties={grid: this, readonly: this.readonly, comboboxProperty: comboboxProperty, formDataService: controlParams.dataservice};
			gridColumn.itemEditor=editor;
			_gridColumn2ComboboxProperty[gridColumn.dataField]=comboboxProperty;
		}
		else if (column.ControlType == "CheckBox")
		{ //OK
//			if (!StringUtils.startWith(gridColumn.headerText, "__"))
//			{
//				editor=new ClassFactory(CheckBoxHeaderRender);
//				//todo:增加点击注册更新事件
//				editor.properties={checkBoxClickHookFun: funCheckBoxHeaderClickHook, instance: this};
//				gridColumn.headerRenderer=editor;
//			}
//			else
//			{
//				gridColumn.headerText=""; //StringUtils.between (gridColumn.headerText , "__" , "__");
//				gridColumn.setStyle("textAlign", "center");
//			}
			gridColumn.headerRenderer=new ClassFactory(CommonHeaderRender);

			editor=new ClassFactory(CheckBoxEditor);
			editor.properties={formGrid: this};
			gridColumn.itemRenderer=editor;
			gridColumn.resizable=false;
			gridColumn.editable=false;
				//c.rendererIsEditor = true;
		}
		else if (column.ControlType == "Memo")
		{ //OK

			gridColumn.headerRenderer=new ClassFactory(CommonHeaderRender);
			gridColumn.itemRenderer=new ClassFactory(MemoItemRender);
			editor=new ClassFactory(MemoEditor);
			editor.properties={readonly: this.readonly, dataField: gridColumn.dataField, datasetName: this.databaseName, formDataService: this.dataservice, formInstance: this.formInstance, grid: this};
			gridColumn.itemEditor=editor;
		}
		else if (column.ControlType == "ButtonTextBox")
		{ //OK
			gridColumn.headerRenderer=new ClassFactory(CommonHeaderRender);
			gridColumn.itemRenderer=new ClassFactory(CommonItemRender);

			editor=new ClassFactory(ButtonTextInputEditor);
			editor.properties={readonly: this.readonly, funName: column.FunName, frameID: formInstance.getIFrameID(), dataField: gridColumn.dataField, datasetName: this.databaseName, formDataService: this.dataservice, formInstance: this.formInstance, grid: this};
			gridColumn.itemEditor=editor;
		}
		else if (column.ControlType == "Button")
		{
			editor=new ClassFactory(ButtonItemRender);
			editor.properties={readonly: this.readonly, funName: column.FunName, frameID: formInstance.getIFrameID(), grid: this};
			gridColumn.itemEditor=editor;
			gridColumn.itemRenderer=editor;
		}
		else if (column.ControlType == "HerfLink")
		{
			editor=new ClassFactory(HrefRender4FormGrid);
			editor.properties={frameID: formInstance.getIFrameID()};
			gridColumn.itemRenderer=editor;
			gridColumn.editable=false;

		}
		else if (column.ControlType == "HrefLink4File")
		{
			editor=new ClassFactory(AttachmentHrefRender4FormGrid);
			var _fileFilter:Array=[];
			if (StringUtils.isEmpty(column.fileExts))
				column.fileExts="";
			var l1:Array=column.fileExts.split("||");
			for (var j:int=0; j < l1.length; j++)
			{
				var str:String=l1[j];
				var l2:Array=str.split("|");
				_fileFilter.push(new FileFilter(l2[0], l2[1]));
			}
			var attachmentType:String=StringUtils.nvl(column.attachmentType, "Attachment");
			var attributes:Object={type: attachmentType, fileSize: column.fileSize * (column.fileSizeUnit == "0" ? 1024 * 1024 : 1024), fileExts: _fileFilter}
			editor.properties={columnWidth: gridColumn.width, attachmentType: attachmentType, fileAttribtes: attributes, form: this.formInstance, frameID: formInstance.getIFrameID(), databaseName: this.databaseName, dataservice: this.dataservice};
			gridColumn.itemRenderer=editor;
			gridColumn.editable=false;
		}
		else
		{
			gridColumn.headerRenderer=new ClassFactory(CommonHeaderRender);
			gridColumn.itemRenderer=new ClassFactory(CommonItemRender);
			gridColumn.editable=false;
		}
		if (column.IsSpliter == "True")
			lockGridColumn=i + 1;
		gridColumns.push(gridColumn);
	}

	if (lockGridColumn != -1)
		this.grid.lockedColumnCount=lockGridColumn + 1;

	this.grid.columns=gridColumns;
	this.grid.invalidateDisplayList();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
private function comboboxLabelFunction(item:Object, column:DataGridColumn):String
{
	return "测试";
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//设置下拉列表框对象
private function getComboboxItems(combobox:Object):ArrayCollection
{
	var result:Array=[];
	if (Convert.object2Boolean(combobox.FormDB))
	{
		var ds:String=combobox.DataSource;
		var df:String=StringUtils.between(combobox.DisplayField, "[", "]");
		var vf:String=StringUtils.between(combobox.ValueField, "[", "]");
		if (!StringUtils.isEmpty(ds) && !StringUtils.isEmpty(df) && !StringUtils.isEmpty(vf))
		{
			var records:Array=controlParams.dataservice.getDataSetRecords(ds);
			for (var j:int=0; j < records.length; j++)
			{
				var record:Object=records[j];
				result.push({label: record[df], data: record[vf]});
			}
		}
	}
	else
	{
		if (combobox.Items && combobox.Items.Item)
		{
			var items:Array=combobox.Items.Item;
			for (var i:int=0; i < items.length; i++)
				result.push({label: items[i].Display, data: items[i].Value});
		}
	}
	return new ArrayCollection(result);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//追加数据记录
private function appendGrid():void
{
	if (!this._addEnabled)
		return;
	if (formInstance.readonly)
		return;

	var parentDatasetNames:Array=[];
	var index:int=controlParams.dataservice.appendDataSetRecord(controlParams.databaseName, parentDatasetNames);
	if (parentDatasetNames.length != 0)
	{ //递归添加父Grid
		var parentGrids:Array=formInstance["getDBGridsByDatasetName"](parentDatasetNames);
		for (var i:int=0; i < parentGrids.length; i++)
		{
			var pGrid:FormGrid=parentGrids[i] as FormGrid;
			//("1");
			pGrid.lockEvent=true;
			pGrid.refresh();
			pGrid.lockEvent=false;
			//("2");
			pGrid.grid.selectedIndex=0;
			controlParams.dataservice.setDataSetRecordIndex(pGrid.databaseName, 0);
			pGrid._oldSelectedID=controlParams.dataservice.getDataSetValue(pGrid.databaseName, pGrid._gridKeyField);
			formInstance["setControlsEnabledByGrid"](pGrid);
		}
	}

	var record:Object=controlParams.dataservice.getDataSetRecord(controlParams.databaseName, index);
	this.grid.selectedIndex=index;
	this.grid.selectedItem=record;
	controlParams.dataservice.setDataSetRecordIndex(controlParams.databaseName, index);

	if (!lockEvent)
		formInstance["eventOnDataSourceSelectChangedEvent"](controlParams.databaseName, this, FormDataService.DATASET_SELECTED_CHANGE_TYPE_INSERT, _oldSelectedID);
	//缺省值设置完成

	controlParams.dataservice.setDataSetRecordIndexByItem(controlParams.databaseName, record);
	_gridData.refresh();

	this.grid.dataProvider=_gridData;
	this.grid.selectedItem=record;
	if (!StringUtils.isEmpty(_eventOnAddFunName))
	{
		var resultObject:Object=formInstance["executeJSFunction"](_eventOnAddFunName, {});
		if (resultObject)
		{
			for (var K:String in resultObject)
				record[K]=resultObject[K];
		}
	}

	this.grid.invalidateList();
	this.grid.validateNow();
	if (!isNaN(this.grid.maxVerticalScrollPosition))
	{
		this.grid.verticalScrollPosition=this.grid.maxVerticalScrollPosition;
	}
	_oldSelectedID=record[_gridKeyField];

	//启动编辑功能
	if (this.grid.columns[1].editable)
		this.grid.editedItemPosition={columnIndex: 1, rowIndex: this.grid.selectedIndex};
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//回调数据
private function afterEditGridItemCallFunction(data:Object):void
{
	if (data != null)
	{
		var record:Object=controlParams.dataservice.mergeDataSetRecord(controlParams.databaseName, data);
		controlParams.dataservice.setDataSetRecordIndexByItem(controlParams.databaseName, record);
		if (!lockEvent)
			formInstance["eventOnDataSourceSelectChangedEvent"](controlParams.databaseName, this, FormDataService.DATASET_SELECTED_CHANGE_TYPE_NORMAL, null);
		_gridData.refresh();
		this.grid.dataProvider=_gridData;
		this.grid.selectedItem=record;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//取得参数
private function getFormsParams(isNew:Boolean):Object
{
	var result:Object={};
	for each (var param:Object in _bindForms.params)
	{
		var name:String=param.name;
		var value:String=param.value;
		var dsName:String=StringUtils.before(value, ".");
		var fieldName:String=StringUtils.after(value, ".");
		var formParams:Object=formInstance.getFormParams();

		if (StringUtils.equal("REQ", dsName))
		{
			if (formParams.hasOwnProperty(fieldName))
				result[name]=formInstance.getFormParams()[fieldName];
			else
				result[name]=value;
		}
		else
		{
			if (isNew && StringUtils.equal(dsName, this.controlParams.databaseName))
				result[name]="NULL";
			else
				result[name]=controlParams.dataservice.getDataSetValue(dsName, fieldName);
		}
	}
	return result;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//删除数据
public function deleteGrid(isForce:Boolean=false):void
{
	if (!this._delEnabled && !isForce)
		return;

	if (this.readonly && !isForce)
		return;

	var index:int=this.grid.selectedIndex;
	if (index != -1)
	{
		var oldData:Object=grid.selectedItem;
		if (!StringUtils.isEmpty(_eventOnDeleteFunName))
		{
			var result:Object=formInstance["executeJSFunction"](_eventOnDeleteFunName, oldData);
			if (result === false)
				return;
		}

		controlParams.dataservice.deleteDataSetRecord(controlParams.databaseName);
		_gridData.refresh();
		var position:Number=this.grid.verticalScrollPosition;
		this.grid.dataProvider=_gridData;
		if (_gridData.length <= index)
			index=index - 1;
		if (index != -1 && index < _gridData.length)
		{
			this.grid.selectedItem=_gridData.getItemAt(index);
			controlParams.dataservice.setDataSetRecordIndexByItem(controlParams.databaseName, this.grid.selectedItem);
		}
		else
		{
			this.grid.selectedItem=null;
			controlParams.dataservice.setDataSetRecordIndex(controlParams.databaseName, -1);
		}

		if (!lockEvent)
			formInstance["eventOnDataSourceSelectChangedEvent"](controlParams.databaseName, this, FormDataService.DATASET_SELECTED_CHANGE_TYPE_DELETE, null);

		this.grid.verticalScrollPosition=position;
		this.grid.invalidateList();
		this.grid.validateNow();
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//选取对象发生变化
private var _oldSelectedID:String=null;

protected function eventOnGridItemClickHandler(event:ListEvent):void
{
	//eventOnGridItemClickHandler");
	if (this.grid.selectedItem[_gridKeyField] != _oldSelectedID)
	{
		controlParams.dataservice.setDataSetRecordIndexByItem(controlParams.databaseName, this.grid.selectedItem);
		if (event != null && !lockEvent)
			formInstance["eventOnDataSourceSelectChangedEvent"](controlParams.databaseName, this, FormDataService.DATASET_SELECTED_CHANGE_TYPE_NORMAL, _oldSelectedID);
		_oldSelectedID=this.grid.selectedItem[_gridKeyField];
		if (!StringUtils.isEmpty(_eventOnSelectFunName))
			formInstance["executeJSFunction"](_eventOnSelectFunName, {});
	}

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var isRuningOnModifuFunction:Boolean=false;

//Grid编辑结束事件
public function triggerChangeEvent(fieldName:String, oldValue:String, newValue:String):void
{
	if (isRuningOnModifuFunction)
		return;

	if (!StringUtils.isEmpty(fieldName) && ArrayUtils.contain(_eventFields, fieldName))
	{
		isRuningOnModifuFunction=true;
		formInstance["executeJSFunction"](_eventOnModifyFunName, {fieldName: fieldName, value: newValue, record: grid.selectedItem});
		isRuningOnModifuFunction=false;
	}

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function resetRefComboboxColumns(fieldName:String, index:int):Boolean
{
	var result:Boolean=false;
	for (var i:int=index + 1; i < grid.columnCount; i++)
	{
		var comboboxProperty:Object=_gridColumn2ComboboxProperty[grid.columns[i].dataField];
		if (comboboxProperty && StringUtils.equal(comboboxProperty.parentField, fieldName))
		{
			formInstance.setDataSetValue(controlParams.databaseName, grid.columns[i].dataField, null, [this]);
			if (!StringUtils.isEmpty(comboboxProperty.displayFieldEx))
				formInstance.setDataSetValue(controlParams.databaseName, comboboxProperty.displayFieldEx, null, [this]);
			resetRefComboboxColumns(grid.columns[i].dataField, i);
			result=true;
		}
	}
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function checkRenderEditHookFunction(fieldName:String, newValue:String):void
{
	controlParams.dataservice.triggerDataSetChangeEvent(controlParams.databaseName);
	formInstance.setDataSetValue(controlParams.databaseName, fieldName, newValue, [this]);
	triggerChangeEvent(fieldName, "", newValue);
}

protected function eventButtonItemClick(event:ItemClickEvent):void
{
	if (StringUtils.isEmpty(controlParams.databaseName))
		return;
	var item:Object=event.item;
	switch (item.tag)
	{
		case "add": //增加
			if (!this.readonly)
				appendGrid();
			break;
		case "delete": //编辑
			if (!this.readonly)
				deleteGrid();
			break;
		case "import": //导入
			if (!this.readonly)
				importFromExcel();
			break;
		case "export": //导出
			exportToExcel();
			break;
		default:
			executeExtButtonClick(item);
			break;
	}
}

private function toolbarVisibleFun():Boolean
{
	return showToolbar && !this.readonly;
}

protected function eventGridChange(event:ListEvent):void
{
	eventOnGridItemClickHandler(event);
}

public function get selectedItem():Object
{
	return this.grid.selectedItem;
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//导出到Excel
private function exportToExcel():void
{
	if (StringUtils.isEmpty(controlParams.databaseName))
		return;
	var records:Array=controlParams.dataservice.getDataSetRecords(controlParams.databaseName);
	var i:int=0;
	//列标题
	var columnCaptions:Array=[];
	for (i=1; i < this.grid.columnCount; i++)
	{
		var column:Object=this.grid.columns[i];
		columnCaptions.push(column.headerText);
	}

	//列数据
	var dataRecords:Array=[];
	for (i=0; i < records.length; i++)
	{
		var record:Object=records[i];
		var dataRecord:Object=[];
		for (var j:int=0; j < _columnFields.length; j++)
		{
			var v:String=records[i][_columnFields[j]];
			dataRecord.push(v);
		}
		dataRecords.push(dataRecord);
	}

	//产生数据
	AjaxUtils.postData("../client/excelservlet?o=data2excel", {define: JSON.stringify({columns: columnCaptions, datas: dataRecords})}, function(text:String, token:Object):void
	{
		var json:Object=JSON.parse(text);
		if (json.r)
			FlexGlobals.topLevelApplication.goURL(json.path);
		else
			AlertUtils.alert("导出Excel文件失败:" + json.msg, AlertUtils.ALERT_WARNING);
	});

}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//从Excel导入
private function importFromExcel():void
{
	var colHeaders:Array=[];
	var colFields:Array=[];
	for (var i:int=1; i < this.grid.columnCount; i++)
	{
		colHeaders.push(this.grid.columns[i].headerText);
		colFields.push(this.grid.columns[i].dataField);
	}

	var params:Object={header: colHeaders.join(",")};
	UploadExcelWindow4DataGridImport.execute(params, function(json:Object):void
	{
		var records:Array=json.records;
		var record:Array=records[0];
		var fieldName2ColumnIndex:Object={};
		for (var i:int=1; i < grid.columnCount; i++)
		{
			var index:int=record.indexOf(grid.columns[i].headerText);
			if (index != -1)
				fieldName2ColumnIndex[grid.columns[i].dataField]=index;
		}
		var gridRecords:Array=[];
		for (i=1; i < records.length; i++)
		{
			record=records[i];
			var gridRecord:Object={};
			for (var k:String in fieldName2ColumnIndex)
				gridRecord[k]=record[fieldName2ColumnIndex[k]];
			gridRecords.push(gridRecord);

		}
		if (gridRecords.length != 0)
			formInstance.batchAppendRecords(controlParams.databaseName, gridRecords);
	});
}

//------------------------------------------------------------------------------------------------------
protected function eventButtonCreateComplete(event:FlexEvent):void
{

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
protected function group1_creationCompleteHandler(event:FlexEvent):void
{
	var buttonList:Array=[];
	if (supportAdd)
		buttonList.push({label: "增加", tag: "add"});
	if (supportDelete)
		buttonList.push({label: "删除", tag: "delete"});
	if (supportExport)
	{
		buttonList.push({label: "导入", tag: "import"});
		buttonList.push({label: "导出", tag: "export"});
	}
	for (var i:int=0; i < extButtons.length; i++)
	{
		var btnLabel:String=StringUtils.trim(extButtons[i]);
		if (StringUtils.isEmpty(btnLabel))
			continue;
		buttonList.push({label: btnLabel, tag: btnLabel});
	}
	if (buttonList.length == 0 && buttonBar != null && buttonBar.parent != null)
		this.removeElement(buttonBar);
	else if (buttonBar != null)
		buttonBar.dataProvider=buttonList;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function select(keyFieldName:String, value:String):void
{
	if (!StringUtils.equal(_oldSelectedID, controlParams.dataservice.getDataSetValue(controlParams.databaseName, keyFieldName)))
	{
		for (var i:int=0; i < (this.grid.dataProvider as ArrayCollection).length; i++)
		{
			var record:Object=(this.grid.dataProvider as ArrayCollection).getItemAt(i);
			if (StringUtils.equal(value, record[keyFieldName]))
			{
				this.grid.selectedItem=record;
				eventOnGridItemClickHandler(null);
				if (!lockEvent)
					formInstance["eventOnDataSourceSelectChangedEvent"](controlParams.databaseName, this, FormDataService.DATASET_SELECTED_CHANGE_TYPE_NORMAL, _oldSelectedID);
				_oldSelectedID=controlParams.dataservice.getDataSetValue(controlParams.databaseName, keyFieldName);
				break;
			}
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function funCheckBoxHeaderClickHook(index:int):void
{
	controlParams.dataservice.registerDataSetStatus(controlParams.databaseName, FormDataService.DATASET_OPERATION_TYPE_UPDATE, index);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function initToolbarButtons():void
{

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function executeExtButtonClick(params:Object):void
{
	if (!StringUtils.isEmpty(_eventOnExtButtonClick))
	{
		formInstance["executeJSFunction"](_eventOnExtButtonClick, params);
	}
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
