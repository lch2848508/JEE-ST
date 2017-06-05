import com.estudio.flex.common.FormConst;
import com.estudio.flex.common.InterfacePortalGrid;
import com.estudio.flex.common.InterfacePortalGridEx;
import com.estudio.flex.component.FormGrid;
import com.estudio.flex.component.InputFileUpload;
import com.estudio.flex.component.TextInputEx;
import com.estudio.flex.component.mx.RichEditorEx;
import com.estudio.flex.module.EditableControlParams;
import com.estudio.flex.module.FormDataService;
import com.estudio.flex.module.InterfaceEditableControl;
import com.estudio.flex.module.component.LookupCombobox;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.FormValid;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;

import mx.containers.TabNavigator;
import mx.controls.DateField;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.utils.ObjectUtil;

import spark.components.Image;
import spark.components.TextArea;
import spark.components.TextInput;

private var saveResult:Object=null;

/**
 * 初始化表单中的数据
 * @param data 数据
 * @param isFullInit 是否全部初始化 true-初始化所有的DataSet false-不初始化readonly=true的DataSet
 *
 */
public function initFormData(data:Object, isFullInit:Boolean=true, isFirstRun:Boolean=false, isCreateIFrame:Boolean=false):void
{

	this._formDataService.allowAutoAppendRecord=false;
	this._lastTextEditor=null;

	this.hideVaildControls();
	if (!isFirstRun)
	{
		this._formDataService.initFormData(data, isFullInit);
		this._isMainDatasetHasRecord=this._formDataService.getMainDatasetID() != null;
	}
	this.initComboboxItems();
	this.fillDataValueToControls();

	this._formDataService.allowAutoAppendRecord=true;
	this.modified=false;

	_isCreateCompleted=true;

	if (isCreateIFrame)
		this.prepareFormShow();


}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//取得DataSet值
/**
 *
 * @param datasetName
 * @param fieldName
 * @return
 */
public function getDataSetValue(datasetName:String, fieldName:String):String
{
	return this._formDataService.getDataSetValue(datasetName, fieldName);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//设置DataSet值
/**
 *
 * @param datasetName
 * @param fieldName
 * @param value
 */
public function setDataSetValue(datasetName:String, fieldName:String, value:String, exceptControls:Array=null):void
{
	var oldValue:String=_formDataService.getDataSetValue(datasetName, fieldName);
	_formDataService.setDataSetValue(datasetName, fieldName, value);

	var controls:Array=getDataBindControlByDataSourceName(datasetName, fieldName, exceptControls);
	var k:String=null;
	var control:UIComponent=null;
	for (var i:int=0; i < controls.length; i++)
	{
		control=controls[i].control;
		var intfControl:InterfaceEditableControl=InterfaceEditableControl(control);

		intfControl.unBindEvent();

		var controlType:int=intfControl.controlType;
		if (controlType == EditableControlParams.CONST_INPUTTEXT || //
			controlType == EditableControlParams.CONST_DATE_EX || //
			controlType == EditableControlParams.CONST_CHECKBOX || //
			controlType == EditableControlParams.CONST_MEMO || //
			controlType == EditableControlParams.CONST_RICHEDIT)
		{
			intfControl.setControlValue(value, null, false);
		}
		else if (controlType == EditableControlParams.CONST_COMBOBOX || //
			controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX)
		{
			if (StringUtils.equal(intfControl.fieldName, fieldName) && !StringUtils.equal(value, oldValue))
			{
				var extValue:String="";
				if (!StringUtils.isEmpty(intfControl.extFieldName))
					extValue=_formDataService.getDataSetValue(intfControl.databaseName, intfControl.extFieldName);
				intfControl.setControlValue(value, extValue, false);
				clearAllChildrenComboboxs(UIComponent(control));
			}
		}
		else if (controlType == EditableControlParams.CONST_GRID)
		{
			var grid:FormGrid=control as FormGrid;
			var o:Object=grid.grid.itemEditorInstance;
			if (o && o.hasOwnProperty("dataField") && StringUtils.equal(o.dataField, fieldName))
			{
				o.cellValue=value;
			}
			grid.lockEvent=true;
			grid.refresh();
			grid.lockEvent=false;
		}

		intfControl.bindEvent();
	}

	if (exceptControls)
		ArrayUtils.addAll(controls, exceptControls);

	refreshFormGrid(datasetName, controls);
}


public function clearDataSetRecords(datasetName:String):void
{
	_formDataService.clearDataSetRecords(datasetName);
	_formDataService.setDataSetRecordIndex(datasetName, -1);
	var isBindGrid:Boolean=false;
	for (var i:int=0; i < _formDBGrids.length; i++)
	{
		var grid:FormGrid=_formDBGrids[i] as FormGrid;
		if (StringUtils.equal(grid.databaseName, datasetName))
		{
			grid.lockEvent=true;
			grid.refresh();
			grid.lockEvent=false;
			isBindGrid=true;
		}
	}
}

public function deleteDataSetRecord(datasetName:String):void
{
	var isBindGrid:Boolean=false;
	for (var i:int=0; i < _formDBGrids.length; i++)
	{
		var grid:FormGrid=_formDBGrids[i] as FormGrid;
		if (StringUtils.equal(grid.databaseName, datasetName))
		{
			grid.deleteGrid(true);
			grid.lockEvent=true;
			grid.refresh();
			grid.lockEvent=false;
			isBindGrid=true;
		}
	}
}

public function deleteDataSetRecordByKeys(datasetName:String, keys:Array):void
{
	_formDataService.deleteDataSetRecords(datasetName, keys);
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @param datasetName
 * @param values
 */
public function setDataSetValues(datasetName:String, values:Object):void
{
	var fields:Array=[];
	var k:String=null;
	var oldValues:Object={};
	for (k in values)
	{
		oldValues[k]=_formDataService.getDataSetValue(datasetName, k);
		_formDataService.setDataSetValue(datasetName, k, values[k]);
		fields.push(k);
	}

	var controls:Array=getDataBindControlByDataSourceName(datasetName);
	for (var i:int=0; i < controls.length; i++)
	{
		var control:UIComponent=controls[i].control;
		var intfControl:InterfaceEditableControl=InterfaceEditableControl(control);
		var controlType:int=intfControl.controlType;
		if (!values.hasOwnProperty(intfControl.fieldName) && controlType != EditableControlParams.CONST_GRID)
			continue;

		intfControl.unBindEvent();

		var value:String=values[intfControl.fieldName];
		var oldValue:String=oldValues[intfControl.fieldName];
		if (controlType == EditableControlParams.CONST_INPUTTEXT || //
			controlType == EditableControlParams.CONST_CHECKBOX || //
			controlType == EditableControlParams.CONST_MEMO || //
			controlType == EditableControlParams.CONST_DATE_EX || //
			controlType == EditableControlParams.CONST_RICHEDIT)
		{
			intfControl.setControlValue(value, null, false);
		}
		else if (controlType == EditableControlParams.CONST_COMBOBOX || controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX)
		{
			if (!StringUtils.equal(value, oldValue))
			{
				var extValue:String=StringUtils.isEmpty(intfControl.extFieldName) ? "" : _formDataService.getDataSetValue(intfControl.databaseName, intfControl.extFieldName);
				intfControl.setControlValue(value, extValue, false);
				clearAllChildrenComboboxs(UIComponent(control));
			}
		}
		else if (controlType == EditableControlParams.CONST_GRID)
		{
			var grid:FormGrid=control as FormGrid;
			var o:Object=grid.grid.itemEditorInstance;
			if (o && o.hasOwnProperty("dataField") && values.hasOwnProperty(o.dataField))
			{
				o.cellValue=values[o.dataField];
			}
			grid.lockEvent=true;
			grid.refresh();
			grid.lockEvent=false;
		}

		intfControl.bindEvent();
	}

}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//批量设置数据源值
/**
 *
 * @param datasetName
 * @param keys
 * @param records
 */
public function batchSetDatasetRecordsByKeys(datasetName:String, keys:Array, records:Array):void
{
	var i:int=0;
	var oldRecords:Array=_formDataService.getArray(datasetName);
	var recordKeys:Array=[];
	for (i=0; i < oldRecords.length; i++)
		recordKeys.push(String(oldRecords[i][_formDataService.getDataSetKeyField(datasetName)]));

	for (i=0; i < keys.length; i++)
	{
		var key:String=keys[i];
		var index:int=recordKeys.indexOf(key);
		if (index != -1)
		{
			var oldRecord:Object=oldRecords[index];
			var newRecord:Object=records[i];
			for (var k:String in newRecord)
				oldRecord[k]=newRecord[k];
		}
		_formDataService.registerDataSetStatus(datasetName, FormDataService.DATASET_OPERATION_TYPE_UPDATE, index);
	}

	//刷新Grid
	for (i=0; i < _formDBGrids.length; i++)
	{
		var grid:FormGrid=_formDBGrids[i] as FormGrid;
		if (grid.isBindDatasource && StringUtils.equal(grid.databaseName, datasetName))
		{
			grid.lockEvent=true;
			grid.refresh();
			grid.lockEvent=false;
		}
	}


}

/////////////////////////////////////////////////////////////////////////////////////
public function updateDatasetValues(datasetName:String, records:Array):void
{
	var keys:Array=[];
	var keyFieldName:String=_formDataService.getDataSetKeyField(datasetName);
	for (var i:int=0; i < records.length; i++)
		keys.push(String(records[i][keyFieldName]));
	batchSetDatasetRecordsByKeys(datasetName, keys, records);
}

/////////////////////////////////////////////////////////////////////////////////////
public function triggerAfterSaveEvent():void
{
	if (_params && _params.uiParams.portalInstance) //被Portal调用
	{

		var grid:InterfacePortalGrid=_params.uiParams.portalInstance as InterfacePortalGrid;
		if (_params.isNew)
		{
			var hasRecordFocused:Boolean=false;
			grid.gotoPage(0);
			var records:Array=ArrayCollection(grid.getGrid().dataProvider).source;
			for (var i:int=0; i < records.length; i++)
			{
				var keyValue:String=records[i].__key__;
				for (var k:String in _params.formParams)
				{
					if (StringUtils.equal(_params.formParams[k], keyValue))
					{
						grid.getGrid().selectedIndex=i;
						i=records.length + 1;
						hasRecordFocused=true;
						break;
					}
				}
			}
			if (!hasRecordFocused)
				_params.uiParams.portalInstance=null;
			_params.isNew=false;
		}
	}

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//保存数据
public function save(forceRefreshGrid:Boolean=false):Boolean
{
	var saveStatus:int=checkFormInputValidAndSave();
	if (saveStatus == ON_SAVE_SUCCESSED || saveStatus == ERROR_ON_DATASET_NOCHANGE)
	{
		FlexGlobals.topLevelApplication.executeModalDialogCallFunction(this.parent);
		return true;
	}
	return false;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function saveEx(isSendAfterSave:Boolean):void
{
	var result:int=checkFormInputValidAndSave(isSendAfterSave);
	switch (result)
	{
		case ERROR_ON_BEFORE_SAVE:
			break;
		case ERROR_ON_DATASET_NOCHANGE:
			AlertUtils.msnMessage("表单保存", "保存表单成功(表单数据无更改).", false);
			FlexGlobals.topLevelApplication.executeModalDialogCallFunction(this.parent);
			break;
		case ERROR_ON_FORM_VALID:
			AlertUtils.msnMessage("表单保存", "表单中部分数据填写格式不正确，保存表单失败。", true);
			break;
		case ERROR_ON_FORM_SAVE_AJAX:
			AlertUtils.msnMessage("表单保存", _saveFormErrorMsg, true);
			break;
		case ON_SAVE_SUCCESSED:
			AlertUtils.msnMessage("表单保存", "成功保存表单数据.", false);
			break;
	}
	//发送工作流
	if (_params.uiParams.purposeType == FormConst.PURPOSETYPE_WORKFLOW_EDIT && (result == ON_SAVE_SUCCESSED || result == ERROR_ON_DATASET_NOCHANGE))
	{
		if (isSendAfterSave)
			_params.uiParams.callfunction({result: true}, isSendAfterSave);
		else
			_params.uiParams.callfunction(saveResult, false);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//保存表单
/**
 *
 * @param datasetsValue
 * @return
 */
protected function saveForm(datasetsValue:Object):Object
{
	var postDataSetValue:String=ObjectUtils.toJSON(datasetsValue);
	var reqParams:Object=null;
	if (_params.uiParams.purposeType == FormConst.PURPOSETYPE_PORTALITEM_EDIT)
		reqParams=ObjectUtils.mergeParams(_params.formParams, _params.uiParams.portalInstance.getSelectedID(_params.uiParams.isTree, _params.uiParams.isNew, _formDataService.getMainDatasetID()), {portalid: _params.uiParams.portalInstance.getPortalID(), formids: _params.uiParams.portalInstance.getForms(_params.uiParams.isTree).ids.join(","), datasetValues: postDataSetValue});
	else if (_params.uiParams.purposeType == FormConst.PURPOSETYPE_PORTALITEMEX_EDIT)
		reqParams=ObjectUtils.mergeParams(_params.formParams, {formids: _params.uiParams.formids.join(","), datasetValues: postDataSetValue});
	else
		reqParams=ObjectUtils.mergeParams(_params.formParams, {formids: _params.uiParams.formids.join(","), datasetValues: postDataSetValue});
	return JSFunUtils.JSFun("saveFormDataset", reqParams);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//校验表单值并保存
private function checkFormInputValidAndSave(isSendButtonClick:Boolean=false):int
{
	_saveFormErrorMsg="";

	//检查脚本的 OnBeforeSave 事件
	if (_isHasJSScript)
	{
		if (!IFrameUtils.execute(getIFrameID(), "triggerEventFunctionFromAs", {controlType: "form", controlId: null, eventType: "OnBeforeSave", params: []}))
			return ERROR_ON_BEFORE_SAVE;
	}

	//检查是否有数据发生更改
	var modifyDatasetValue:Object=_formDataService.getModifiedDatasetsValue();
	if (modifyDatasetValue == null)
		return ERROR_ON_DATASET_NOCHANGE;

	//检查数据校验
	if (!validFormInputs())
		return ERROR_ON_FORM_VALID;

	//ajax 数据保存
	_params.formParams.mainDatasetKeyValue=_formDataService.getMainDatasetID();
	saveResult=saveForm(modifyDatasetValue);
	if (!saveResult || !saveResult["r"])
	{
		_saveFormErrorMsg=saveResult ? saveResult["msg"] : "保存表单失败，请查看系统调试信息或同管理人员联系!";
		return ERROR_ON_FORM_SAVE_AJAX;
	}



	//保存后事件
	if (_params.uiParams.purposeType == FormConst.PURPOSETYPE_PORTALITEM_EDIT) //栏目
	{
		_params.uiParams.portalInstance.getCallfun(_params.uiParams.isTree)({isTree: _params.uiParams.isTree, isNew: _params.uiParams.isNew, data: saveResult["portalData"]});
		_params.uiParams.isNew=false;
		_params.formParams=_params.uiParams.portalInstance.getParams(_params.uiParams.isTree, false);
	}
	else if (_params.uiParams.purposeType == FormConst.PURPOSETYPE_PORTALITEMEX_EDIT) //栏目Ex
	{
		_params.uiParams.portalInstance.callback4FormSave(_params.formParams.controlName, _params.formParams.isNew, _params.formParams.isChild, saveResult["portalData"]); //, .getCallfun (_params.uiParams.isTree) ({isTree: _params.uiParams.isTree , isNew: _params.uiParams.isNew , data: saveResult["portalData"]});
		if (_params.uiParams.isNew)
		{
			_params.uiParams.isNew=false;
			_params.formParams=ObjectUtils.mergeParams(_params.formParams, _params.uiParams.portalInstance.callback4GetBindFormParams(_params.formParams.controlName, false, saveResult["portalData"]));
		}
	}
	else if (_params.uiParams.purposeType == FormConst.PURPOSETYPE_WORKFLOW_EDIT)
	{
		_params.uiParams.callfunction(saveResult, isSendButtonClick);
	}
	else if (_params.uiParams.purposeType == FormConst.PURPOSETYPE_PORTALITEMEX_CONTROL)
	{
		_params.uiParams.control.callback4AfterSave();
	}

	//清除状态
	_formDataService.clearDatasetStatus();
	_isMainDatasetHasRecord=true;
	modified=false;
	bindInputFileAndPictures();

	//调用表单 OnAfterSave 脚本
	if (_isHasJSScript)
		IFrameUtils.execute(getIFrameID(), "triggerEventFunctionFromAs", {controlType: "form", controlId: null, eventType: "OnAfterSave", params: null});

	return ON_SAVE_SUCCESSED;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//数据校验
private function validFormInputs():Boolean
{
	var errorControls:Array=[];
	for (var i:int=0; i < _formValidControls.length; i++)
	{
		var valid:Object=_formValidControls[i];
		if (!valid.Control)
			continue;
		var c:UIComponent=_controlUID2Instance[valid.Control];
		if (!c)
			continue;
		var intf:InterfaceEditableControl=InterfaceEditableControl(c);
		if (intf.readonly)
			continue;
		var img:Image=_controlUID2Instance[valid.Name];
		var value:String=intf.controlValue;
		var validResult:Object=validValue(value, valid, _controlUID2ControlParams[c.uid]);
		img.visible=!validResult["r"];
		if (img.visible)
		{
			img.toolTip=validResult["msg"];
			errorControls.push(img);
		}
	}

	if (errorControls.length != 0)
	{
		var firstControl:UIComponent=errorControls[0];
		while (firstControl.parent != this)
		{
			if (firstControl.parent is TabNavigator)
			{
				var p:TabNavigator=firstControl.parent as TabNavigator;
				var index:int=p.getChildIndex(firstControl);
				if (p.selectedIndex != index)
					p.selectedIndex=index;
			}
			firstControl=firstControl.parent as UIComponent;
		}
	}
	return errorControls.length == 0;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//校验数据值
private function validValue(value:String, valid:Object, controlParams:Object):Object
{
	var datasourceName:String=controlParams.DataSource;
	var fieldName:String=controlParams.FieldName;
	//if (!StringUtils.isEmpty(datasourceName) && !StringUtils.isEmpty(fieldName) && _formDataService.getArray(datasourceName).length != 0)
	return FormValid.checkControlValue([value, valid, controlParams]);
	//return {r: true, msg: ""};
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//隐藏校验控件
private function hideVaildControls():void
{
	for (var i:int=0; i < _formValidControls.length; i++)
	{
		var valid:Object=_formValidControls[i];
		var img:Image=_controlUID2Instance[valid.Name];
		img.visible=false;
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////
public function copyForm(params:Object, datasetNames:Array):Boolean
{
	var datasets:Array=_formDataService.getDatasets();
	if (!datasets || datasets.length == 0)
		return false;

	var data:Object=JSFunUtils.JSFun("getFormsDefine", JSON.stringify(ObjectUtils.mergeParams({formids: _params.uiParams.formIDS.join(","), onlydata: 1}, params)));
	data=data.data;

	var i:int=0;
	var j:int=0;

	for (i=0; i < datasets.length; i++)
	{
		var dataset:Object=datasets[i];
		var datasetName:String=dataset.Name;
		if (dataset.Readonly)
		{
			_formDataService.setArray(datasetName, data[datasetName]);
			builderComboBoxItems(datasetName, data[datasetName], false);
		}
		else if (datasetNames == null || ArrayUtils.indexOf(datasetNames, datasetName) != -1)
		{
			var records:Array=data[datasetName];
			for (j=0; j < records.length; j++)
			{
				delete records[j][dataset.Keyfield];
			}
			_formDataService.batchAppendRecords(datasetName, records);
		}
	}

	//初始化Combobox对象
	fillDataValueToControls();

	//设置同Grid关联的控件
	for (j=0; j < _formDBGrids.length; j++)
	{
		var grid:FormGrid=FormGrid(_formDBGrids[j]);
		setControlsEnabledByGrid(grid);
	}


	return true;
}
