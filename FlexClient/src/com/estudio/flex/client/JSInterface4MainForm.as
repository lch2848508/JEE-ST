import com.estudio.component.Win4CommonWord;
import com.estudio.flex.client.DynamicUI;
import com.estudio.flex.client.WinGridDataForm;
import com.estudio.flex.client.WinSWFDialog;
import com.estudio.flex.client.WinTreeDataForm;
import com.estudio.flex.common.InterfaceFormUI;
import com.estudio.flex.common.InterfacePortalGrid;
import com.estudio.flex.common.InterfacePortalGridEx;
import com.estudio.flex.common.InterfaceQueryGroup;
import com.estudio.flex.component.ErrorLoggerWindow;
import com.estudio.flex.component.SessionMissError;
import com.estudio.flex.component.UploadExcelWindow;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;

import ext.swf.WinSelectedDate;

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.TimerEvent;
import flash.external.ExternalInterface;
import flash.net.URLRequest;

import mx.controls.listClasses.ListBase;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.managers.PopUpManager;

import spark.components.BorderContainer;
import spark.components.Group;
import spark.components.Label;

import uk.co.teethgrinder.elements.labels.Title;

private var _id2FormUI:Object={};
private var _id2FormParams:Object={};
private var _dynamicUI:DynamicUI=new DynamicUI;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 获取具体一个PortalGridDefine实例的DataGrid组件
 * @param portalID PortalGridDefine中的唯一标识号
 * @return 返回PortalGridDefine实例
 */
private function getPortalGrid(portalID:String=null):InterfacePortalGrid
{
	var result:InterfacePortalGrid=this._activePortalGrid;

	if (!StringUtils.isEmpty(portalID) && this._applicationMap.id2ModuleLoader[portalID])
		result=this._applicationMap.id2ModuleLoader[portalID].child as InterfacePortalGrid;

	return result;
}

private function getPortalGridEx(portalID:String=null):InterfacePortalGridEx
{
	var result:InterfacePortalGridEx=this._activePortalGridEx;
	if (!StringUtils.isEmpty(portalID) && this._applicationMap.id2ModuleLoader[portalID])
		result=this._applicationMap.id2ModuleLoader[portalID].child as InterfacePortalGridEx;
	return result;
}

private function getQueryGroup(portalId:String):InterfaceQueryGroup
{
	var result:InterfaceQueryGroup=this._applicationMap.id2ModuleLoader[portalId].child as InterfaceQueryGroup;
	return result;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 获取一个具体的表单或表单组实例
 * @param id 表单或表单组的唯一标识号
 * @return 返回表单定义 如果表单不存在返回null
 */
private function getForm(id:String):InterfaceFormUI
{
	var result:InterfaceFormUI=this._id2FormUI[id];
	return result;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *设置控件组是否可用
 * @param formId
 * @param controlNames
 * @param enabled
 *
 */
public function setFormControlEnabled(formId:String, controlNames:Array, enabled:Boolean):void
{
	var form:InterfaceFormUI=this.getForm(formId);
	form.setControlsEnabled(controlNames, enabled);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *设置控件组是否可见
 * @param formId
 * @param controlNames
 * @param enabled
 *
 */

public function setFormControlVisible(formId:String, controlNames:Array, visible:Boolean):void
{
	var form:InterfaceFormUI=this.getForm(formId);
	form.setControlsVisible(controlNames, visible);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 注册一个具体的表单或表单组
 * @param id 表单ID
 * @param form 表单实例
 */
private function registerFormUIAndParams(id:String, form:InterfaceFormUI, params:Object):void
{
	this._id2FormUI[id]=form;
	this._id2FormParams[id]=params;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 弹出一个Flex风格的消息框 类是于JavaScript的Alert
 * @param content 消息框的内容
 * @param type 消息框的内容 0-普通消息 1-警告消息 2-错误消息 如果type不是0,1,2则当作0处理
 */
public function alert(content:String, type:int=0):void
{
	AlertUtils.alert(ObjectUtils.unescape4flex(content) as String, type);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//确认信息
public function confirm(content:String, okFunName:String, cancelFunName:String, iframeid:String):void
{
	AlertUtils.confirm(ObjectUtils.unescape4flex(content) as String, function(params:Object):void
	{
		IFrameUtils.execute(iframeid, okFunName, {});
	}, StringUtils.isEmpty(cancelFunName) ? null : function(params:Object):void
	{
		IFrameUtils.execute(iframeid, cancelFunName, {});
	});
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 取得动态表单支持的类实例
 * @return 动态类支持实例
 */
public function get dynamicUI():DynamicUI
{
	return this._dynamicUI;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 将DataSet中的数据以目录树的形式显示出来 当用户选中目录树中的某一项时调用相应的JS回调函数
 * @param datas 需要显示的数据
 * @param multiSelected 目录树是否支持多选
 * @param callFunName 选中目录树后调用的JS回调函数的名称
 * @param iframeID 对应的PortalGridDefine或FormUI的ID 通过此ID FlexApp可以找到相应的IFrame定义
 */
public function treeDataSet(datas:Array, multiSelected:Boolean, callFunName:String, iframeID:String, width:int, height:int, extParams:Object):void
{
	WinTreeDataForm.execute(ObjectUtils.unescape4flex(datas) as Array, multiSelected, callFunName, iframeID, width, height, extParams);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *将DataSet中的数据以列表的形式显示出来 当用户选中数据并点击确认时调用相应的JS回调函数
 * @param params
 *
 */
public function gridDataSet(params:Object):void
{
	WinGridDataForm.execute(ObjectUtils.unescape4flex(params));
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 打开外部URL
 * @param url 网站URL
 */
public function goURL(url:String, target:String="_blank"):void
{
	url=ObjectUtils.unescape4flex(url) as String;
	flash.net.navigateToURL(new flash.net.URLRequest(url), target);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public function popupMSNMessage(category:String, message:String, isError:Boolean):void
{
	return AlertUtils.msnMessage(ObjectUtils.unescape4flex(category) as String, ObjectUtils.unescape4flex(message) as String, isError);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//保存表单数据
public function saveFormDataSet(id:String):Boolean
{
	return this.getForm(id).save();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
public function uploadExcel(importDefine:String, templateDefine:String, extParams:Object, frameID:String, funName:String):void
{
	UploadExcelWindow.execute(ObjectUtils.unescape4flex(importDefine) as String, ObjectUtils.unescape4flex(templateDefine) as String, ObjectUtils.unescape4flex(extParams), frameID, funName);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//记录错误信息
private var errorTimes:int=0;

public function loggerErrorMessage(msg:String):void
{
	msg=ObjectUtils.unescape4flex(msg) as String;
	if (StringUtils.contain(msg, "EXCEPTION4CLIENT"))
	{
		AlertUtils.alert(StringUtils.after(msg, "</CODE>"));
	}
	else
	{
		ErrorLoggerWindow.logger(msg);
		this.errorTimes++;
		var items:Array=this.linkbarTopMenuItems.dataProvider.source;
		for (var i:int=0; i < items.length; i++)
		{
			if (items[i].type == "1")
			{
				items[i].label="错误日志(" + this.errorTimes + ")";
				this.linkbarTopMenuItems.dataProvider=items;
				this.linkbarTopMenuItems.invalidateDisplayList();
				break;
			}
		}
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////
//显示错误信息
public function showLoggerErrorMessage():void
{

}



///////////////////////////////////////////////////////////////////////////////////////////////////////
//表单相关
private function Callback_Form_getControlValue(formId:String, controlName:String):String
{
	return ObjectUtils.escape4js(this.getForm(formId).getControlValue(controlName)) as String;
}

private function Callback_Form_getControlValueEx(formId:String, controlName:String):String
{
	return ObjectUtils.escape4js(this.getForm(formId).getControlValueEx(controlName)) as String;
}


private function Callback_Form_setControlValue(formId:String, controlName:String, value:String, extValue:String=null, isSettingFormDataService:Boolean=true):void
{
	this.getForm(formId).setControlValue(controlName, ObjectUtils.unescape4flex(value) as String, ObjectUtils.unescape4flex(extValue) as String, isSettingFormDataService);
}

private function Callback_Form_getControl(formId:String, controlName:String):UIComponent
{
	return this.getForm(formId).getControl(controlName);
}

private function Callback_Form_getDBGridSelectedItem(formId:String, controlName:String):Object
{
	return ObjectUtils.escape4js(this.getForm(formId).getDBGridSelectedItem(controlName));
}

private function Callback_Form_selectDBGridByKeyField(formId:String, controlName:String, keyFieldName:String, value:String):void
{
	this.getForm(formId).selectDBGridByKeyField(controlName, keyFieldName, ObjectUtils.unescape4flex(value) as String);
}

private function Callback_Form_setControlsEnabled(formId:String, controlNames:Array, isEnabled:Boolean):void
{
	this.getForm(formId).setControlsEnabled(controlNames, isEnabled);
}

private function Callback_Form_setControlsVisible(formId:String, ControlNames:Array, isVisible:Boolean):void
{
	this.getForm(formId).setControlsVisible(ControlNames, isVisible);
}

private function Callback_Form_setTabSheetActivePage(formId:String, pageControlName:String, tabsheetName:String):void
{
	this.getForm(formId).setTabSheetActivePage(pageControlName, tabsheetName);
}

private function Callback_Form_setGridBehaviour(formId:String, controlName:String, addEnabled:Boolean, deleteEnabled:Boolean):void
{
	this.getForm(formId).setGridBehaviour(controlName, addEnabled, deleteEnabled);
}

private function Callback_Form_clearComboboxItems(formId:String, controlName:String):void
{
	this.getForm(formId).clearComboboxItems(controlName);
}

private function Callback_Form_getFormParams(formId:String):Object
{
	return ObjectUtils.escape4js(this.getForm(formId).getFormParams());
}

private function Callback_Form_isNew(formId:String):Boolean
{
	return this.getForm(formId).isNew();
}

private function Callback_Form_existsRecord(formId:String, datasetName:String):Boolean
{
	return this.getForm(formId).existsRecord(datasetName);
}

private function Callback_Form_existsDataSet(formId:String, datasetName:String):Boolean
{
	return this.getForm(formId).existsDataSet(datasetName);
}

private function Callback_Form_getDataSetValue(formId:String, datasetName:String, fieldName:String):String
{
	return ObjectUtils.escape4js(this.getForm(formId).getDataSetValue(datasetName, fieldName)) as String;
}

private function Callback_Form_setDataSetValue(formId:String, datasetName:String, fieldName:String, value:String, exceptControls:Array=null):void
{
	this.getForm(formId).setDataSetValue(datasetName, fieldName, ObjectUtils.unescape4flex(value) as String, exceptControls);
}

private function Callback_Form_setDataSetValues(formId:String, datasetName:String, values:Object):void
{
	this.getForm(formId).setDataSetValues(datasetName, ObjectUtils.unescape4flex(values));
}

private function Callback_Form_getDataSetRecords(formId:String, datasetName:String):Array
{
	var result:Array=this.getForm(formId).getDataSetRecords(datasetName);
	result=ObjectUtils.escape4js(result) as Array;
	return result;
}

private function Callback_Form_clearDataSetRecords(formId:String, datasetName:String):void
{
	this.getForm(formId).clearDataSetRecords(datasetName);
}

private function Callback_Form_deleteDataSetRecord(formId:String, datasetName:String):void
{
	this.getForm(formId).deleteDataSetRecord(datasetName);
}

private function Callback_deleteDataSetRecordByKeys(formId:String, datasetName:String, keys:Array):void
{
	this.getForm(formId).deleteDataSetRecordByKeys(datasetName, keys);
}


private function Callback_Form_copyForm(formId:String, params:Object, datasetNames:Array):Boolean
{
	return this.getForm(formId).copyForm(params, datasetNames);
}

private function Callback_Form_batchAppendRecords(formId:String, datasetName:String, records:Array):int
{
	return this.getForm(formId).batchAppendRecords(datasetName, ObjectUtils.unescape4flex(records) as Array);
}

private function Callback_Form_batchSetDatasetRecordsByKeys(formId:String, datasetName:String, keys:Array, records:Array):void
{
	this.getForm(formId).batchSetDatasetRecordsByKeys(datasetName, ObjectUtils.unescape4flex(keys) as Array, ObjectUtils.unescape4flex(records) as Array);
}

private function Callback_Form_updateDataSetValues(formId:String, datasetName:String, records:Array):void
{
	this.getForm(formId).updateDatasetValues(datasetName, ObjectUtils.unescape4flex(records) as Array);
}


private function Callback_Form_save(formId:String, forceRefreshGrid:Boolean=false):Boolean
{
	return this.getForm(formId).save(forceRefreshGrid);
}

private function Callback_Form_IsReadonly(formid:String):Boolean
{
	var formUI:InterfaceFormUI=this.getForm(formid);
	if (formUI)
		return formUI.readonly;
	return true;
}

private function Callback_Form_Close(formid:String):void
{
	var formUI:InterfaceFormUI=this.getForm(formid);
	if (formUI)
		formUI.close();
}


private function Callback_Form_refreshFormDatas(formid:String, params:Object):void
{
	params=ObjectUtils.unescape4flex(params);
	var formUI:InterfaceFormUI=this.getForm(formid);
	if (formUI)
	{
		var formParams:Object=this._id2FormParams[formid];
		for (var k:String in params)
			formParams[k]=params[k];
		var formIds:Array=formid.split("_");
		var ids:Array=[];
		for (var j=0; j < formIds.length; j++)
		{
			if (Convert.str2int(formIds[j], 0) != 0)
				ids.push(formIds[j]);
		}
		formUI.initFormData(JSFunUtils.JSFun("getFormsDefine", JSON.stringify(ObjectUtils.mergeParams({formids: ids.join(","), onlydata: 1}, formParams))).data, true, false, false);
		formUI.params.formParams=formParams;
		formUI.triggerAfterSaveEvent();
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////
private function Callback_Portal_getGrid(portalId:String):void
{
	this.getPortalGrid(portalId).getGrid();
}

private function Callback_Portal_funGridNew(portalId:String):void
{
	this.getPortalGrid(portalId).funGridNew();
}

private function Callback_Portal_funGridEdit(portalId:String):void
{
	this.getPortalGrid(portalId).funGridEdit();
}

private function Callback_Portal_funGridDelete(portalId:String, isDeleteAll:Boolean=true):void
{
	this.getPortalGrid(portalId).funGridDelete(isDeleteAll);
}

private function Callback_Portal_eventDetailInfoGrid(portalId:String):void
{
	this.getPortalGrid(portalId);
}

private function Callback_Portal_funGridExchange(portalId:String, isGridUp:Boolean):void
{
	this.getPortalGrid(portalId).funGridExchange(isGridUp);
}


private function Callback_Portal_funGridRefresh(portalId:String):void
{
	this.getPortalGrid(portalId).funGridRefresh();
}


private function Callback_Portal_getGridSelectedItems(portalId:String):Array
{
	return ObjectUtils.escape4js(this.getPortalGrid(portalId).getGridSelectedItems()) as Array;
}

private function Callback_Portal_getGridDatas(portalId:String):Array
{
	return ObjectUtils.escape4js(this.getPortalGrid(portalId).getGridDatas()) as Array;
}

private function Callback_Portal_getGridSelectedItem(portalId:String):Object
{
	return ObjectUtils.escape4js(this.getPortalGrid(portalId).getGridSelectedItem());
}

private function Callback_Portal_gotoPage(portalId:String, page:int):void
{
	this.getPortalGrid(portalId).gotoPage(page);
}

private function Callback_Portal_refreshGridSelectedItem(portalId:String):void
{
	this.getPortalGrid(portalId).refreshGridSelectedItem();
}

private function Callback_Portal_setGridCellValue(portalId:String, keyValue:String, fieldname:String, value:*, refresh:Boolean=true):void
{
	this.getPortalGrid(portalId).setGridCellValue(keyValue, fieldname, ObjectUtils.unescape4flex(value), refresh);
}

private function Callback_Portal_setGridCellsValue(portalId:String, keyValues:Array, fieldname:String, value:*, refresh:Boolean=true):void
{
	this.getPortalGrid(portalId).setGridCellsValue(keyValues, fieldname, ObjectUtils.unescape4flex(value), refresh);
}

private function Callback_Portal_batchSetGridCellsValue(portalId:String, keyValues:Array, fieldnames:*, records:Array, refresh:Boolean=true):void
{
	this.getPortalGrid(portalId).batchSetGridCellsValue(keyValues, fieldnames, ObjectUtils.unescape4flex(records) as Array, refresh);
}

private function Callback_Portal_selectGridItem(portalId:String, keyValue:String, refresh:Boolean=true):void
{
	this.getPortalGrid(portalId).selectGridItem(keyValue, refresh);
}

private function Callback_Portal_getTree(portalId:String):ListBase
{
	return this.getPortalGrid(portalId).getTree();
}

private function Callback_Portal_funTreeNew(portalId:String, isSameLevel:Boolean):void
{
	this.getPortalGrid(portalId).funTreeNew(isSameLevel);
}

private function Callback_Portal_funTreeEdit(portalId:String):void
{
	this.getPortalGrid(portalId).funTreeEdit();
}

private function Callback_Portal_funTreeDelete(portalId:String, isDeleteAllCheck:Boolean=true):void
{
	this.getPortalGrid(portalId).funTreeDelete(isDeleteAllCheck);
}

private function Callback_Portal_funTreeExchange(portalId:String, isUp:Boolean):void
{
	this.getPortalGrid(portalId).funTreeExchange(isUp);
}

private function Callback_Portal_funTreeRefresh(portalId:String):void
{
	this.getPortalGrid(portalId).funTreeRefresh();
}

private function Callback_Portal_getTreeSelectedItem(portalId:String):Object
{
	return ObjectUtils.escape4js(this.getPortalGrid(portalId).geTreeSelectedItem());
}

private function Callback_Portal_getTreeSelectedItems(portalId:String):Array
{
	return ObjectUtils.escape4js(this.getPortalGrid(portalId).getTreeSelectedItems()) as Array;
}

private function Callback_Portal_getTreeDatas(portalId:String):Array
{
	return ObjectUtils.escape4js(this.getPortalGrid(portalId).getTreeDatas()) as Array;
}

private function Callback_Portal_getLayout(portalId:String, name:String):Group
{
	return this.getPortalGrid(portalId).getLayout(name);
}

private function Callback_Portal_getToolBarItem(portalId:String, name:String):UIComponent
{
	return this.getPortalGrid(portalId).getToolBarItem(name);
}

private function Callback_Portal_setToolBarItemEnabled(portalId:String, name:String, enabled:Boolean):void
{
	this.getPortalGrid(portalId).setToolBarItemEnabled(name, enabled);
}

//---------------------------------------------------------------------------------------------------------
public function raiseSessionMissError():void
{
	SessionMissError.execute();
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function registerCallbackInterface():void
{
	//注册Portal相关回调函数
	ExternalInterface.addCallback("Callback_Portal_getGrid", this.Callback_Portal_getGrid);
	ExternalInterface.addCallback("Callback_Portal_funGridNew", this.Callback_Portal_funGridNew);
	ExternalInterface.addCallback("Callback_Portal_funGridEdit", this.Callback_Portal_funGridEdit);
	ExternalInterface.addCallback("Callback_Portal_funGridDelete", this.Callback_Portal_funGridDelete);
	ExternalInterface.addCallback("Callback_Portal_eventDetailInfoGrid", this.Callback_Portal_eventDetailInfoGrid);
	ExternalInterface.addCallback("Callback_Portal_funGridExchange", this.Callback_Portal_funGridExchange);
	ExternalInterface.addCallback("Callback_Portal_funGridRefresh", this.Callback_Portal_funGridRefresh);
	ExternalInterface.addCallback("Callback_Portal_getGridSelectedItems", this.Callback_Portal_getGridSelectedItems);
	ExternalInterface.addCallback("Callback_Portal_getGridDatas", this.Callback_Portal_getGridDatas);
	ExternalInterface.addCallback("Callback_Portal_getGridSelectedItem", this.Callback_Portal_getGridSelectedItem);
	ExternalInterface.addCallback("Callback_Portal_gotoPage", this.Callback_Portal_gotoPage);
	ExternalInterface.addCallback("Callback_Portal_refreshGridSelectedItem", this.Callback_Portal_refreshGridSelectedItem);
	ExternalInterface.addCallback("Callback_Portal_setGridCellValue", this.Callback_Portal_setGridCellValue);
	ExternalInterface.addCallback("Callback_Portal_setGridCellsValue", this.Callback_Portal_setGridCellsValue);
	ExternalInterface.addCallback("Callback_Portal_batchSetGridCellsValue", this.Callback_Portal_batchSetGridCellsValue);
	ExternalInterface.addCallback("Callback_Portal_selectGridItem", this.Callback_Portal_selectGridItem);
	ExternalInterface.addCallback("Callback_Portal_getTree", this.Callback_Portal_getTree);
	ExternalInterface.addCallback("Callback_Portal_funTreeNew", this.Callback_Portal_funTreeNew);
	ExternalInterface.addCallback("Callback_Portal_funTreeEdit", this.Callback_Portal_funTreeEdit);
	ExternalInterface.addCallback("Callback_Portal_funTreeDelete", this.Callback_Portal_funTreeDelete);
	ExternalInterface.addCallback("Callback_Portal_funTreeExchange", this.Callback_Portal_funTreeExchange);
	ExternalInterface.addCallback("Callback_Portal_funTreeRefresh", this.Callback_Portal_funTreeRefresh);
	ExternalInterface.addCallback("Callback_Portal_getTreeSelectedItem", this.Callback_Portal_getTreeSelectedItem);
	ExternalInterface.addCallback("Callback_Portal_getTreeSelectedItems", this.Callback_Portal_getTreeSelectedItems);
	ExternalInterface.addCallback("Callback_Portal_getTreeDatas", this.Callback_Portal_getTreeDatas);
	ExternalInterface.addCallback("Callback_Portal_getLayout", this.Callback_Portal_getLayout);
	ExternalInterface.addCallback("Callback_Portal_getToolBarItem", this.Callback_Portal_getToolBarItem);
	ExternalInterface.addCallback("Callback_Portal_setToolBarItemEnabled", this.Callback_Portal_setToolBarItemEnabled);

	//注册同栏目Ex相关的函数
	ExternalInterface.addCallback("Callback_PortalEx_getSelectedItem", this.PortalEx_getControlSelectedItem);
	ExternalInterface.addCallback("Callback_PortalEx_getSelectedItems", this.PortalEx_getControlSelectedItems);
	ExternalInterface.addCallback("Callback_PortalEx_append", this.PortalEx_appendControl);
	ExternalInterface.addCallback("Callback_PortalEx_edit", this.PortalEx_editControl);
	ExternalInterface.addCallback("Callback_PortalEx_viewform", this.PortalEx_viewControl);
	ExternalInterface.addCallback("Callback_PortalEx_del", this.PortalEx_delControl);
	ExternalInterface.addCallback("Callback_PortalEx_up", this.PortalEx_upControl);
	ExternalInterface.addCallback("Callback_PortalEx_down", this.PortalEx_downControl);
	ExternalInterface.addCallback("Callback_PortalEx_refresh", this.PortalEx_refreshControl);
	ExternalInterface.addCallback("Callback_PortalEx_getRecords", this.PortalEx_getControlRecords);
	ExternalInterface.addCallback("Callback_PortalEx_updateRecord", this.PortalEx_updateControlRecord);
	ExternalInterface.addCallback("Callback_PortalEx_selectItem", this.PortalEx_selectControlItem);
	ExternalInterface.addCallback("Callback_PortalEx_getRootId", this.PortalEx_getControlRootId);
	ExternalInterface.addCallback("Callback_PortalEx_refreshSelectedItem", this.PortalEx_refreshSelectedItem);
	ExternalInterface.addCallback("Callback_PortalEx_executeSWFControlFunction", this.PortalEx_executeSWFControlFunction);
	ExternalInterface.addCallback("Callback_PortalEx_saveToServer", this.PortalEx_saveToServer);
	ExternalInterface.addCallback("Callback_PortalEx_firstPage", this.PortalEx_ControlFirstPage);
	ExternalInterface.addCallback("Callback_PortalEx_lastPage", this.PortalEx_ControlLastPage);
	ExternalInterface.addCallback("Callback_PortalEx_callLater", this.PortalEx_ControlCallLater);
	ExternalInterface.addCallback("Callback_PortalEx_setDiagramActionBackground", this.PortalEx_setDiagramActionBackground);
	ExternalInterface.addCallback("Callback_PortalEx_setDiagramActionStep", this.PortalEx_setDiagramActionStep);
	ExternalInterface.addCallback("Callback_PortalEx_setDiagramActionSetting", this.PortalEx_setDiagramActionSetting);
	ExternalInterface.addCallback("Callback_PortalEx_batchSetDiagramActionSettings", this.PortalEx_batchSetDiagramActionSettings);
	ExternalInterface.addCallback("Callback_PortalEx_getDiagramActionSettings", this.PortalEx_getDiagramActionSettings);
	ExternalInterface.addCallback("Callback_PortalEx_focusDiagramActions", this.PortalEx_focusDiagramActions);
	ExternalInterface.addCallback("Callback_PortalEx_loadDiagram", this.PortalEx_loadDiagram);
	ExternalInterface.addCallback("Callback_PortalEx_setActivePage", this.PortalEx_setActivePage);
	ExternalInterface.addCallback("Callback_PortalEx_setContent", this.PortalEx_setContent);
	ExternalInterface.addCallback("Callback_PortalEx_getContent", this.PortalEx_getContent);
	ExternalInterface.addCallback("Callback_PortalEx_setRichViewText", this.PortalEx_setRichViewText);
	ExternalInterface.addCallback("Callback_PortalEx_getRichViewText", this.PortalEx_getRichViewText);
	ExternalInterface.addCallback("Callback_PortalEx_addGeometrys", this.PortalEx_addGeometrys);
	ExternalInterface.addCallback("Callback_PortalEx_setRecordId", this.PortalEx_setJsonControlRecordId);
	ExternalInterface.addCallback("Callback_PortalEx_setFormParams", this.PortalEx_setFormControlParams);
	ExternalInterface.addCallback("Callback_PortalEx_getParams", this.PortalEx_getParams);
	ExternalInterface.addCallback("Callback_PortalEx_getControlParams", this.PortalEx_getControlParams);
	ExternalInterface.addCallback("Callback_PortalEx_setControlFilterParams", this.PortalEx_setControlFilterParams);
	ExternalInterface.addCallback("Callback_PortalEx_setControlReadonly", this.PortalEx_setControlReadonly);



	//表单相关回调函数
	ExternalInterface.addCallback("Callback_Form_getControlValue", this.Callback_Form_getControlValue);
	ExternalInterface.addCallback("Callback_Form_setControlValue", this.Callback_Form_setControlValue);
	ExternalInterface.addCallback("Callback_Form_getControl", this.Callback_Form_getControl);
	ExternalInterface.addCallback("Callback_Form_getDBGridSelectedItem", this.Callback_Form_getDBGridSelectedItem);
	ExternalInterface.addCallback("Callback_Form_selectDBGridByKeyField", this.Callback_Form_selectDBGridByKeyField);
	ExternalInterface.addCallback("Callback_Form_setControlsEnabled", this.Callback_Form_setControlsEnabled);
	ExternalInterface.addCallback("Callback_Form_setControlsVisible", this.Callback_Form_setControlsVisible);
	ExternalInterface.addCallback("Callback_Form_setTabSheetActivePage", this.Callback_Form_setTabSheetActivePage);
	ExternalInterface.addCallback("Callback_Form_setGridBehaviour", this.Callback_Form_setGridBehaviour);
	ExternalInterface.addCallback("Callback_Form_clearComboboxItems", this.Callback_Form_clearComboboxItems);
	ExternalInterface.addCallback("Callback_Form_getFormParams", this.Callback_Form_getFormParams);
	ExternalInterface.addCallback("Callback_Form_isNew", this.Callback_Form_isNew);
	ExternalInterface.addCallback("Callback_Form_existsRecord", this.Callback_Form_existsRecord);
	ExternalInterface.addCallback("Callback_Form_existsDataSet", this.Callback_Form_existsDataSet);
	ExternalInterface.addCallback("Callback_Form_getDataSetValue", this.Callback_Form_getDataSetValue);
	ExternalInterface.addCallback("Callback_Form_setDataSetValue", this.Callback_Form_setDataSetValue);
	ExternalInterface.addCallback("Callback_Form_setDataSetValues", this.Callback_Form_setDataSetValues);
	ExternalInterface.addCallback("Callback_Form_getDataSetRecords", this.Callback_Form_getDataSetRecords);
	ExternalInterface.addCallback("Callback_Form_copyForm", this.Callback_Form_copyForm);
	ExternalInterface.addCallback("Callback_Form_batchAppendRecords", this.Callback_Form_batchAppendRecords);
	ExternalInterface.addCallback("Callback_Form_batchSetDatasetRecordsByKeys", this.Callback_Form_batchSetDatasetRecordsByKeys);
	ExternalInterface.addCallback("Callback_Form_updateDataSetValues", this.Callback_Form_updateDataSetValues);
	ExternalInterface.addCallback("Callback_Form_save", this.Callback_Form_save);
	ExternalInterface.addCallback("Callback_Form_refreshFormDatas", this.Callback_Form_refreshFormDatas);
	ExternalInterface.addCallback("Callback_Form_IsReadonly", this.Callback_Form_IsReadonly);
	ExternalInterface.addCallback("Callback_Form_Close", this.Callback_Form_Close);

	ExternalInterface.addCallback("Callback_Form_getControlValueEx", this.Callback_Form_getControlValueEx);
	ExternalInterface.addCallback("Callback_Form_clearDataSetRecords", this.Callback_Form_clearDataSetRecords);
	ExternalInterface.addCallback("Callback_Form_deleteDataSetRecord", this.Callback_Form_deleteDataSetRecord);
	ExternalInterface.addCallback("Callback_Form_deleteDataSetRecordByKeys", this.Callback_deleteDataSetRecordByKeys);


	ExternalInterface.addCallback("Callback_Query_SetFilterValues", this.Query_SetFilterValues);

	//杂项函数
	ExternalInterface.addCallback("Callback_popupMessage", this.popupMessage);
	ExternalInterface.addCallback("Callback_popupMSNMessage", this.popupMSNMessage);
	ExternalInterface.addCallback("Callback_modalForms", this.modalForms);
	ExternalInterface.addCallback("Callback_closeModalForms", this.closeModalForms);

	ExternalInterface.addCallback("Callback_alert", this.alert);
	ExternalInterface.addCallback("Callback_confirm", this.confirm);
	ExternalInterface.addCallback("Callback_gotoPortal", this.gotoPortal);
	ExternalInterface.addCallback("Callback_executePortalFunction", this.executePortalFunction);
	ExternalInterface.addCallback("Callback_closePortal", this.closePortalFunction);
	ExternalInterface.addCallback("Callback_getWorkFlowUiIdByPortalName", this.getWorkFlowUiIdByPortalName);
	ExternalInterface.addCallback("Callback_loggerErrorMessage", this.loggerErrorMessage);
	ExternalInterface.addCallback("Callback_goURL", this.goURL);
	ExternalInterface.addCallback("Callback_closeModalDialog", this.closeModalDialog);
	ExternalInterface.addCallback("Callback_saveModalForms", this.saveModalForms);
	ExternalInterface.addCallback("Callback_commonWords", this.showCommonWords);

	ExternalInterface.addCallback("Callback_gridDataSet", this.gridDataSet);
	ExternalInterface.addCallback("Callback_treeDataSet", this.treeDataSet);
	ExternalInterface.addCallback("Callback_uploadExcel", this.uploadExcel);

	//对话框中调用swf
	ExternalInterface.addCallback("Callback_moduleSWFDialog", this.moduleSWFDialog);

	ExternalInterface.addCallback("Callback_raiseSessionMissError", this.raiseSessionMissError);

}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_getSelectedItem
private function PortalEx_getControlSelectedItem(id:String, controlName:String):Object
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf ? intf.getControlSelectedItem(controlName) : null;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_getSelectedItems
private function PortalEx_getControlSelectedItems(id:String, controlName:String):Array
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf ? intf.getControlSelectedItems(controlName) : null;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_append
private function PortalEx_appendControl(id:String, controlName:String, isChild:Boolean):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.appendControl(controlName, isChild);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_edit
private function PortalEx_editControl(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.editControl(controlName, false);
}

private function PortalEx_viewControl(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.editControl(controlName, true);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_del
private function PortalEx_delControl(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.delControl(controlName);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_up
private function PortalEx_upControl(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.upControl(controlName);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_down
private function PortalEx_downControl(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.downControl(controlName);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_refresh
private function PortalEx_refreshControl(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.refreshControl(controlName);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_getRecords
private function PortalEx_getControlRecords(id:String, controlName:String):Array
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf ? intf.getControlRecords(controlName) : null;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_updateRecord
private function PortalEx_updateControlRecord(id:String, controlName:String, record:Object):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.updateControlRecord(controlName, record);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_selectItem
private function PortalEx_selectControlItem(id:String, controlName:String, key:String):Object
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf ? intf.selectControlItem(controlName, key) : null;

}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_getRootId
private function PortalEx_getControlRootId(id:String, controlName:String):String
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf ? intf.getControlRootId(controlName) : null;
}

///////////////////////////////////////////////////////////////////////////////////////////////////
private function PortalEx_refreshSelectedItem(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.refreshSelectedItem(controlName);
}

private function PortalEx_ControlFirstPage(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.firstPage(controlName);
}

private function PortalEx_ControlCallLater(id:String, controlName:String, JSFunName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.callLaterFunction(controlName, JSFunName);
}

private function PortalEx_ControlLastPage(id:String, controlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.lastPage(controlName);
}

//////////////////////////////////////////////////////////////////////////////////////////////////
private function PortalEx_executeSWFControlFunction(id:String, controlName:String, funName:String, params:Object):Object
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf ? intf.executeSWFControlFunction(controlName, funName, params) : null;
}

/////////////////////////////////////////////////////////////////////////////////////////////////

private function PortalEx_setDiagramActionBackground(id:String, controlName:String, action:String, color:uint):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setDiagramActionBackground(controlName, action, color);
}

private function PortalEx_setDiagramActionStep(id:String, controlName:String, action:String, step:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setDiagramActionStep(controlName, action, step);
}

private function PortalEx_setDiagramActionSetting(id:String, controlName:String, action:String, color:uint, step:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setDiagramActionSetting(controlName, action, color, step);
}

private function PortalEx_batchSetDiagramActionSettings(id:String, controlName:String, params:Object):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.batchSetDiagramActionSettings(controlName, params);
}

private function PortalEx_getDiagramActionSettings(id:String, controlName:String):Object
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		return intf.getDiagramActionSettings(controlName);
	return {};
}


private function PortalEx_focusDiagramActions(id:String, controlName:String, actions:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.focusDiagramActions(controlName, actions.split(","));
}

private function PortalEx_loadDiagram(id:String, controlName:String, diagramName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.loadDiagram(controlName, diagramName);
}

private function PortalEx_setActivePage(id:String, controlName:String, activeControlName:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setActivePage(controlName, activeControlName);
}

private function PortalEx_setContent(id:String, controlName:String, content:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setContent(controlName, content);
}

private function PortalEx_getContent(id:String, controlName:String, content:String):String
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		return intf.getContent(controlName);
	return "";
}

private function PortalEx_setRichViewText(id:String, controlName:String, text:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setRichViewText(controlName, text);
}

private function PortalEx_getRichViewText(id:String, controlName:String):String
{
	var result:String="";
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		result=intf.getRichViewText(controlName);
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
private function PortalEx_addGeometrys(id:String, controlName:String, geometry:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.addGeometrys(controlName, geometry);
}

//////////////////////////////////////////////////////////////////////////////////////////////////
private function PortalEx_setJsonControlRecordId(id:String, controlName:String, recordId:String):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setJsonControlRecordId(controlName, recordId);
}

//////////////////////////////////////////////////////////////////////////////////////////////////
private function PortalEx_setFormControlParams(id:String, controlName:String, params:Object):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	if (intf)
		intf.setFormControlParams(controlName, params);
}

private function PortalEx_getParams(id:String):Object
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf.getParams();
}

private function PortalEx_getControlParams(id:String, controlName:String):Object
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf.getControlParams(controlName);
}

private function PortalEx_setControlFilterParams(id:String, controlName:String, params:Object):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf.setControlFilterParams(controlName, params);
}


private function PortalEx_setControlReadonly(id:String, controlName:String, isReadonly:Boolean):void
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf.setControlReadonly(controlName, isReadonly);
}


//////////////////////////////////////////////////////////////////////////////////////////////////
private function PortalEx_saveToServer(id:String, controlName:String):Object
{
	var intf:InterfacePortalGridEx=getPortalGridEx(id);
	return intf ? intf.saveToServer(controlName) : null;
}
///////////////////////////////////////////////////////////////////////////////////////////////////
private var URL2SWFDialogInstance:Object={};

private function moduleSWFDialog(caption:String, width:int, height:int, url:String, params:Object, iframeId:String, callFunctionName:String):void
{
	var win:UIComponent=URL2SWFDialogInstance[url];
	if (!win)
	{
		win=new WinSWFDialog();
		win.width=width;
		win.height=height;
		WinSWFDialog(win).swfURL=url;
		URL2SWFDialogInstance[url]=win;
	}
	WinSWFDialog(win).title=caption;
	WinSWFDialog(win).swfParams=params;
	WinSWFDialog(win).iframeId=iframeId;
	WinSWFDialog(win).callFunctionName=callFunctionName;
	PopUpManager.addPopUp(win, FlexGlobals.topLevelApplication as DisplayObject, true);
	PopUpManager.centerPopUp(win);
}

///////////////////////////////////////////////////////////////////////////////////////////////////
private function Query_SetFilterValues(portalId:String, params:Object):void
{
	var queryGroup:InterfaceQueryGroup=getQueryGroup(portalId);
	queryGroup.setFilterValues(params);
}

////////////////////////////////////////////////////////////////////////////////////////////////////
private function showCommonWords(items:Array, iFrameId:String, jsFunname:String,initStr:String):void
{
	Win4CommonWord.execute(items, iFrameId, jsFunname,initStr);
}
////////////////////////////////////////////////////////////////////////////////////////////////////
