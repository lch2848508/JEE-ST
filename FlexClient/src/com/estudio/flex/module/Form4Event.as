import com.estudio.flex.RUNTIME_COMMAND;
import com.estudio.flex.common.FormConst;
import com.estudio.flex.component.CheckBoxEx;
import com.estudio.flex.component.ComboBoxEx;
import com.estudio.flex.component.DateFieldEx;
import com.estudio.flex.component.FormGrid;
import com.estudio.flex.component.TextInputEx;
import com.estudio.flex.component.TitleWindowEx;
import com.estudio.flex.component.mx.RichEditorEx;
import com.estudio.flex.module.EditableControlParams;
import com.estudio.flex.module.FormDataService;
import com.estudio.flex.module.InterfaceEditableControl;
import com.estudio.flex.module.component.LookupCombobox;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayCollectionUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.StringUtils;

import flash.display.InteractiveObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.net.SharedObject;
import flash.ui.Keyboard;

import mx.charts.chartClasses.InstanceCache;
import mx.collections.ArrayCollection;
import mx.containers.TitleWindow;
import mx.controls.Alert;
import mx.controls.DateField;
import mx.controls.RichTextEditor;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.FlexEvent;
import mx.managers.FocusManager;
import mx.managers.IFocusManager;
import mx.managers.PopUpManager;
import mx.utils.StringUtil;

import spark.components.CheckBox;
import spark.components.NavigatorContent;
import spark.components.RichEditableText;
import spark.components.TextArea;
import spark.components.TextInput;
import spark.components.supportClasses.SkinnableTextBase;
import spark.events.DropDownEvent;
import spark.events.IndexChangeEvent;

private function eventToolbarItemClick(event:MouseEvent):void
{
	if (event.currentTarget.enabled)
		executeByBarItemID(event.currentTarget["id"]);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//工具条事件处理函数
private function executeByBarItemID(id:String):void
{
	switch (id)
	{
		case "____save____":
			if (this.readonly)
				AlertUtils.msnMessage("保存表单", "该表单不允许被保存。", false);
			else
				this.eventSaveButtonClick(null);
			break;
		default:
			if (_isHasJSScript)
				IFrameUtils.execute(getIFrameID(), id, null);
			break;
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//刷新Grid
private function refreshFormGrid(datasourceName:String, exceptionControls:Array=null):void
{
	for (var i:int=0; i < _formDBGrids.length; i++)
	{
		var grid:FormGrid=_formDBGrids[i] as FormGrid;
		if (StringUtils.equal(grid.databaseName, datasourceName) && (exceptionControls == null || ArrayUtils.indexOf(exceptionControls, grid) == -1))
		{
			grid.lockEvent=true;
			grid.refresh();
			grid.lockEvent=false;
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var controlValueChangedBetweenFocusInAndFocusOut:Object={};

private var lastestFocusedControl:Object=null;

private function eventOnControlFocusIn(event:Event):void
{
	controlValueChangedBetweenFocusInAndFocusOut[event.currentTarget.uid]=false;
}

/**
 *控件值发生变化事件
 * @param event
 *
 */
private function eventOnControlDataChange(event:Event):void
{
	
	var intfControl:InterfaceEditableControl=InterfaceEditableControl(event.currentTarget);
	var controlType:int=intfControl.controlType;
	if (intfControl.isBindDatasource) //判断是否数据绑定
	{
		if (controlType == EditableControlParams.CONST_COMBOBOX) //下拉列表框
		{
			var indexEvent:IndexChangeEvent=IndexChangeEvent(event);
			var newIndex:int=Math.max(indexEvent.newIndex, -1);
			if (Object(intfControl).isFixedList)
			{
				if (newIndex == -1)
				{
					var extValue:String=intfControl.controlExtValue;
					var value:String=intfControl.controlValue;
					intfControl.setControlValue(value, extValue, false);
				}
				else
				{
					if (!StringUtils.isEmpty(intfControl.extFieldName))
						setDataSetValue(intfControl.databaseName, intfControl.extFieldName, intfControl.controlExtValue, [intfControl]);
					setDataSetValue(intfControl.databaseName, intfControl.fieldName, intfControl.controlValue, [intfControl]);
					clearAllChildrenComboboxs(event.currentTarget as ComboBoxEx);
					refreshFormGrid(intfControl.databaseName);
				}
			} //固定值
			else //非固定值
			{
				if (!StringUtils.isEmpty(intfControl.extFieldName))
					setDataSetValue(intfControl.databaseName, intfControl.extFieldName, intfControl.controlExtValue, [intfControl]);
				setDataSetValue(intfControl.databaseName, intfControl.fieldName, intfControl.controlValue, [intfControl]);
				clearAllChildrenComboboxs(event.currentTarget as ComboBoxEx);
				refreshFormGrid(intfControl.databaseName);
				if (newIndex == -1)
					ComboBoxEx(intfControl).addAdditionItem(intfControl.controlValue, intfControl.controlValue);
			}
		}
		else if (controlType == EditableControlParams.CONST_CHECKBOX) //CheckBox
		{
			setDataSetValue(intfControl.databaseName, intfControl.fieldName, CheckBoxEx(intfControl).selected ? "1" : "0", [intfControl]);
		}
		else // controlType == EditableControlParams.CONST_INPUTTEXT CONST_MEMO CONST_RICHEDIT
		{
			setDataSetValue(intfControl.databaseName, intfControl.fieldName, intfControl.controlValue, [intfControl]);
		}
	}
	if (controlType != EditableControlParams.CONST_INPUTTEXT && controlType != EditableControlParams.CONST_MEMO && controlType != EditableControlParams.CONST_RICHEDIT)
		eventOnChange4JS(event);
	else
	{
		controlValueChangedBetweenFocusInAndFocusOut[event.currentTarget.uid]=true;
		lastestFocusedControl=event.currentTarget;
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

private function eventOnControlFocusOut(event:Event):void
{
	if (controlValueChangedBetweenFocusInAndFocusOut[event.currentTarget.uid])
	{
		controlValueChangedBetweenFocusInAndFocusOut[event.currentTarget.uid]=false;
		if (lastestFocusedControl == event.currentTarget)
			lastestFocusedControl=event.currentTarget;
		eventOnChange4JS(event);
	}
}

private function triggerLastestFocusChange():void
{
	if (lastestFocusedControl && controlValueChangedBetweenFocusInAndFocusOut[lastestFocusedControl.uid])
	{
		controlValueChangedBetweenFocusInAndFocusOut[lastestFocusedControl.uid]=false;
		eventOnControlChange4JS(UIComponent(lastestFocusedControl))
		lastestFocusedControl=null;
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *表单添加到舞台事件
 * @param event
 *
 */
private var _modalIsCreated:Boolean=false;


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建IFrame
private var htmlContent:String=null;

private function createIFrameAndInitOwners():void
{
	//获取窗体实例
	_formContain=null;
	if (_params.uiParams.showType == FormConst.SHOWTYPE_MODALDIALOG)
	{
		var c:UIComponent=this.owner as UIComponent;
		while (c && !(c is TitleWindowEx))
		{
			c=c.owner as UIComponent;
		}
		_formContain=c;
		_formCaption=_formContain.title;
	}
	else
	{
		var c:UIComponent=this.owner as UIComponent;
		while (c && !(c is NavigatorContent))
		{
			c=c.owner as UIComponent;
		}
		_formContain=c;
		if (_formContain)
			_formCaption=_formContain.label;

	}
//	//工具条
//	if (groupFormToolbar != null)
//	{
//		groupFormToolbarContain.visible=!_isDialog && _showToolbar;
//		groupFormToolbarContain.height=groupFormToolbarContain.visible ? 28 : 0;
//		groupFormToolbar.invalidateDisplayList();
//	}

	//创建IFrame
	IFrameUtils.removeIFrame(getIFrameID());
	IFrameUtils.createIFrameBySrc(getIFrameID(), "../client/formui.jsp?iframeId=" + getIFrameID() + "&formIds=" + _params.uiParams.formids.join(","));
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *模块创建完成事件
 * @param event
 *
 */
private function eventModalCreationCompleteHandler(event:FlexEvent):void
{
	createForm();
	createExtButtons();
	//addEventListener(RightClickManager.RIGHT_CLICK, eventFormMouseRightClick);
	//界面刷新时创建IFrame
//	groupFormToolbarContain.visible=!_isDialog && _showToolbar;
//	groupFormToolbarContain.height=groupFormToolbarContain.visible ? 28 : 0;
//	groupFormToolbar.invalidateDisplayList();
}

public function prepareFormShow():void
{
	callLater(createIFrameAndInitOwners);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getIFrameID():String
{
	return _iframeID;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *保存 关闭按钮创建完成后事件响应函数
 * @param event
 *
 */
private function eventBottomButtonsInitialize(event:FlexEvent):void
{
	var so:Object=SharedObject.getLocal("forms");
	var isSelected:Boolean=so.data.closeAfterSave == null || so.data.closeAfterSave as Boolean;
	checkbox_closeAfterSave.selected=isSelected;
}


//---------------------------------------------------------------------------------------
private function eventFormMouseRightClick(event:FlexEvent):void
{
	//eventFormMouseRightClick");
	event.stopPropagation();
	event.stopImmediatePropagation();
}

//////////////////////////////////////////////////////////////////////////////////////
/**
 *保存后是否关闭CheckBox点击事件
 * @param event
 *
 */
private function eventCheckBoxCloseAfterSaveHandle(event:MouseEvent):void
{
	var so:Object=SharedObject.getLocal("forms");
	so.data.closeAfterSave=checkbox_closeAfterSave.selected;
	so.flush();
}

//////////////////////////////////////////////////////////////////////////////////////
//关闭
private function eventCloseButtonClick(event:MouseEvent):void
{
	FlexGlobals.topLevelApplication.closeWindow(this.parent);
}

/////////////////////////////////////////////////////////////////////////////////////
//保存
private function eventSaveButtonClick(event:MouseEvent):void
{
	//eventSaveButtonClick");
	var isSaveAndSend:Boolean=event != null && event.currentTarget == this.btnSaveAndSend;
	var result:int=checkFormInputValidAndSave(isSaveAndSend);
	switch (result)
	{
		case ERROR_ON_BEFORE_SAVE:
			AlertUtils.msnMessage("表单保存", "保存表单失败。", true);
			break;
		case ERROR_ON_FORM_VALID:
			AlertUtils.msnMessage("表单保存", "表单中部分数据填写格式不正确，保存表单失败。", true);
			break;
		case ERROR_ON_FORM_SAVE_AJAX:
			AlertUtils.msnMessage("表单保存", _saveFormErrorMsg, true);
			break;
		case ERROR_ON_DATASET_NOCHANGE:
			AlertUtils.msnMessage("表单保存", "保存表单成功.", false);
			FlexGlobals.topLevelApplication.executeModalDialogCallFunction(this.parent);
			if (_isDialog && (this.checkbox_closeAfterSave.selected || isSaveAndSend))
				eventCloseButtonClick(event);
			break;
		case ON_SAVE_SUCCESSED:
		{
			var winParent:Object=this.parent;
			if (!_isDialog || (_isDialog && this.checkbox_closeAfterSave.selected))
				FlexGlobals.topLevelApplication.executeModalDialogCallFunction(winParent);
			AlertUtils.msnMessage("表单保存", "成功保存表单数据.", false);
			if (isSaveAndSend || (_isDialog && this.checkbox_closeAfterSave.selected))
				eventCloseButtonClick(event);
		}
			break;
	}

	//发送工作流
	if (isSaveAndSend && (result == ON_SAVE_SUCCESSED || result == ERROR_ON_DATASET_NOCHANGE))
	{
		_params.uiParams.workflowUIInstance.workFunction4Href(RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function clearAllChildrenComboboxs(combobox:UIComponent):void
{
	var children:Array=_combobox2ChildrenCombobox[combobox.uid];
	if (!children)
		return;

	var tempValue:Boolean=_formDataService.allowAutoAppendRecord;
	_formDataService.allowAutoAppendRecord=false;
	for (var i:int=0; i < children.length; i++)
	{
		var control:UIComponent=children[i] as UIComponent;
		if (control is ComboBoxEx)
		{
			var childComboBox:ComboBoxEx=ComboBoxEx(control);

			childComboBox.unBindEvent();

			childComboBox.dataProvider=FormDataService.BLANK_ARRAY_COLLECTION;
			childComboBox.selectedIndex=-1;
			childComboBox.selectedItem=null;
			childComboBox.textInputValue="";
			childComboBox.setControlValue(null, null, true);
			//clearAllChildrenComboboxs(childComboBox);

			childComboBox.bindEvent();
		}
		else if (control is LookupCombobox)
		{
			LookupCombobox(control).setControlValue("", "", true);
				//clearAllChildrenComboboxs(control);
		}
	}
	_formDataService.allowAutoAppendRecord=tempValue;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//动态获取Combobox数据
private function dynamicLoadCombobox(combobox:ComboBoxEx, datasetName:String, valueFieldName:String, displayFieldName:String, parentComboboxValue:String):void
{
	combobox.dataProvider=_formDataService.dynamicLoadDataSetRecords4Combobox(datasetName, parentComboboxValue, valueFieldName, displayFieldName, getFormParams());
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下拉列表框打开事件 检查下拉列表中是否有数据
private function eventOnComboboxOpen(event:Event):void
{
	var combobox:ComboBoxEx=event.currentTarget as ComboBoxEx;
	var id:String=combobox.uid;
	var params:Object=_controlUID2ControlParams[id];

	//是否需要动态加载数据
	if (combobox.dataProvider.length == 0 && params.ItemsFromDB && !StringUtils.isEmpty(params.ItemDataSource))
	{
		var parentComboboxKey:String="";
		if (!StringUtils.isEmpty(params.ParentCombobox))
			parentComboboxKey=getControlValue(params.ParentCombobox);
		dynamicLoadCombobox(combobox, params.ItemDataSource, params.ItemValueField, params.ItemDisplayField, parentComboboxKey);
	}

	//需要动态定位位置
	if (combobox.selectedIndex < 0 && !StringUtils.isEmpty(params.DataSource) && !StringUtils.isEmpty(params.FieldName))
	{
		var index:int=ArrayCollectionUtils.indexOf(combobox.dataProvider as ArrayCollection, "data", _formDataService.getDataSetValue(params.DataSource, params.FieldName));
		if (index != -1)
		{
			combobox.selectedIndex=index;
			combobox.selectedItem=combobox.dataProvider.getItemAt(index);
			if (combobox.dropDown)
				(combobox.dropDown as UIComponent).validateNow();
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//执行JS脚本事件
private function triggerJSEvent(c:UIComponent, type:String):Object
{
	if (_isHasJSScript)
	{
		var funName:String=_controlUIDAndEventName2JSFunName[c.uid + "__" + type];
		return IFrameUtils.execute(getIFrameID(), "__TRIGGER_CONTROL_EVENT__", {funName: funName, controlId: c.uid});
	}
	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @param funName
 * @param params
 */
public function executeJSFunction(funName:String, params:Object):Object
{
	if (_isHasJSScript)
		return IFrameUtils.execute(getIFrameID(), funName, params);
	return null;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//数据源选中记录发生变化触发事件
public function eventOnDataSourceSelectChangedEvent(datasourceName:String, grid:FormGrid, type:int, prevKeyValue:String):void
{
	var controls:Array=null;
	var i:int=0;
	var value:String=null;
	var extValue:String=null;

	//第一步 缓存MasterDetail数据
	if (!StringUtils.isEmpty(prevKeyValue))
	{
		if (type != FormDataService.DATASET_SELECTED_CHANGE_TYPE_DELETE)
			this._formDataService.cacheMasterDetailRecords(datasourceName, prevKeyValue);
		else //需要把清除的缓存清除掉
			this._formDataService.clearMasterDetailCache(datasourceName, prevKeyValue);
	}

	var controlShouldReadonly:Boolean=grid.readonly || grid.selectedItem == null;
	var intf:InterfaceEditableControl=null;

	//第二步 计算所有同此相关的下拉列表控件
	if (type == FormDataService.DATASET_SELECTED_CHANGE_TYPE_NORMAL || type == FormDataService.DATASET_SELECTED_CHANGE_TYPE_DELETE)
	{ //普通选择及删除
		controls=this.getDataBindControlByDataSourceName(datasourceName, null, [grid]);
		var allControls:Array=[];
		for (i=0; i < controls.length; i++)
		{
			var control:UIComponent=controls[i].control;
			if (allControls.indexOf(control) != -1 || StringUtils.isEmpty(controls[i].fieldName))
				continue;
			allControls.push(control);

			intf=InterfaceEditableControl(control);
			value=this._formDataService.getDataSetValue(intf.databaseName, intf.fieldName);
			var oldValue:String=intf.controlValue;
			extValue=StringUtils.isEmpty(intf.extFieldName) ? "" : this._formDataService.getDataSetValue(intf.databaseName, intf.extFieldName);

			intf.unBindEvent();

			if (intf.controlType == EditableControlParams.CONST_COMBOBOX && !StringUtils.isEmpty(this._combobox2ParentCombobox[control.uid]))
			{
				//ComboBoxEx(intf).dataProvider=FormDataService.BLANK_ARRAY_COLLECTION;
			}
			intf.setControlValue(value, extValue, false);

			intf.bindEvent();

			intf.readonly=controlShouldReadonly || intf.defaultReadonly;
			if (intf.controlType == EditableControlParams.CONST_GRID)
				setControlsEnabledByGrid(intf as FormGrid);
		}
	}
	else if (type == FormDataService.DATASET_SELECTED_CHANGE_TYPE_INSERT)
	{ //插入记录
		grid.lockEvent=true;
		controls=getDataBindControlByDataSourceName(datasourceName, null, [grid]);
		for (i=0; i < controls.length; i++)
		{
			intf=InterfaceEditableControl(controls[i].control);
			value=_formDataService.getDataSetValue(childDatasetName, intf.fieldName);
			extValue=((intf.controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX || intf.controlType == EditableControlParams.CONST_COMBOBOX) && !StringUtils.isEmpty(intf.extFieldName)) ? _formDataService.getDataSetValue(childDatasetName, intf.extFieldName) : "";
			intf.setControlValue(value, extValue, false);
			intf.readonly=intf.defaultReadonly || controlShouldReadonly;
		}
		grid.refresh();
		grid.lockEvent=false;
	}

	//第三部加载MasterDetail数据
	var datasets:Array=[];
	_formDataService.getMasterDetailRecords(datasourceName, getDataSetValue(datasourceName, _formDataService.getDataSetKeyField(datasourceName)), datasets, type == FormDataService.DATASET_SELECTED_CHANGE_TYPE_INSERT);
	if (datasets.length != 0)
	{
		for (i=0; i < datasets.length; i++)
		{
			var childDatasetName:String=datasets[i];
			controls=getDataBindControlByDataSourceName(childDatasetName, "", _formDBGrids);
			for (var j:int=0; j < controls.length; j++)
			{
				intf=InterfaceEditableControl(controls[j].control);
				value=_formDataService.getDataSetValue(childDatasetName, intf.fieldName);
				extValue=((intf.controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX || intf.controlType == EditableControlParams.CONST_COMBOBOX) && !StringUtils.isEmpty(intf.extFieldName)) ? _formDataService.getDataSetValue(childDatasetName, intf.extFieldName) : "";
				intf.setControlValue(value, extValue, false);
				intf.readonly=controlShouldReadonly;
			}
		}

		for (i=0; i < _formDBGrids.length; i++)
		{
			var tempGrid:FormGrid=FormGrid(_formDBGrids[i]);
			if (ArrayUtils.indexOf(datasets, tempGrid.databaseName) != -1)
			{
				tempGrid.initData(_formDataService.getRecordIndex(tempGrid.databaseName));
				setControlsEnabledByGrid(tempGrid);
			}
		}
	}

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function event4comboBoxFocusIn(event:Event):void
{
	var c:ComboBoxEx=ComboBoxEx(event.currentTarget);
	//c.textInput.editable=false;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var _jsEventTriggerTraceLogger:Object={}; //临时记录日志调用情况 避免事件被重复调用

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function registerJsEventTriggerTrace(c:UIComponent, type:String):Boolean
{
	var result:Boolean=false;
	var key:String=c.uid + type;
	if (!_jsEventTriggerTraceLogger.hasOwnProperty(key) || !_jsEventTriggerTraceLogger[key])
	{
		_jsEventTriggerTraceLogger[key]=true;
		result=true;
	}
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function unregisterJsEventTriggerTrace(c:UIComponent, type:String):Boolean
{
	var result:Boolean=false;
	var key:String=c.uid + type;
	if (_jsEventTriggerTraceLogger.hasOwnProperty(key))
		_jsEventTriggerTraceLogger[key]=false;
	result=true;
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventOnClick4JS(event:Event):void
{
	if (registerJsEventTriggerTrace(UIComponent(event.currentTarget), "OnClick"))
	{
		triggerJSEvent(UIComponent(event.currentTarget), "OnClick");
		unregisterJsEventTriggerTrace(UIComponent(event.currentTarget), "OnClick");
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventOnChange4JS(event:Event):void
{
	eventOnControlChange4JS(UIComponent(event.currentTarget));
}

private function eventOnControlChange4JS(c:UIComponent):void
{
	if (registerJsEventTriggerTrace(c, "OnChange"))
	{
		triggerJSEvent(c, "OnChange");
		unregisterJsEventTriggerTrace(c, "OnChange");
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventOnDBClick4JS(event:Event):void
{
	if (registerJsEventTriggerTrace(UIComponent(event.currentTarget), "OnDBLClick"))
	{
		triggerJSEvent(UIComponent(event.currentTarget), "OnDBLClick");
		unregisterJsEventTriggerTrace(UIComponent(event.currentTarget), "OnDBLClick");
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventOnTextButtonClick4JS(c:UIComponent):void
{
	if (registerJsEventTriggerTrace(c, "OnButtonClick"))
	{
		triggerJSEvent(c, "OnButtonClick");
		unregisterJsEventTriggerTrace(c, "OnButtonClick");
	}
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var rightClickTarget:InteractiveObject=null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventDateFieldChange(event:CalendarLayoutChangeEvent):void
{
	setControlValue(event.currentTarget.uid, DateField.dateToString(event.newDate, 'YYYY-MM-DD'), "");
	eventOnChange4JS(event);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventControlEnterNextFocus(event:KeyboardEvent):void
{
	if (event.keyCode == 13)
	{
		var fm:IFocusManager=event.target.focusManager;
		fm.getNextFocusManagerComponent().setFocus();
	}
	//fm.setFocus();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

