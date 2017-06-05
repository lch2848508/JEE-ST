import com.estudio.flex.common.FormConst;
import com.estudio.flex.common.InterfacePortalGrid;
import com.estudio.flex.component.FormGrid;
import com.estudio.flex.component.IconButton;
import com.estudio.flex.component.InputFileUpload;
import com.estudio.flex.component.InputPicture;
import com.estudio.flex.component.TextInputEx;
import com.estudio.flex.component.TitleWindowEx;
import com.estudio.flex.component.ToolbarVertline;
import com.estudio.flex.component.mx.RichEditorEx;
import com.estudio.flex.module.FormDataService;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayCollectionUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.FormValid;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;
import com.estudio.flex.utils.UIUtils;
import com.utilities.IconUtility;

import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;

import mx.collections.ArrayCollection;
import mx.containers.TabNavigator;
import mx.controls.DateField;
import mx.controls.PopUpMenuButton;
import mx.controls.RichTextEditor;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.FlexEvent;
import mx.events.MenuEvent;

import spark.components.BorderContainer;
import spark.components.Button;
import spark.components.CheckBox;
import spark.components.HGroup;
import spark.components.Image;
import spark.components.NavigatorContent;
import spark.components.TextArea;
import spark.components.TextInput;
import spark.components.TitleWindow;
import spark.components.supportClasses.SkinnableTextBase;

private static const ERROR_ON_BEFORE_SAVE:int=0;
private static const ERROR_ON_DATASET_NOCHANGE:int=1;
private static const ERROR_ON_FORM_VALID:int=2;
private static const ERROR_ON_FORM_SAVE_AJAX:int=3;
private static const ON_SAVE_SUCCESSED:int=4;

private var _formDefine:Object=null; //表单定义
private var _params:Object=null; //表单参数
private var _isHasJSScript:Boolean=false; //是否有javascript脚本
private var _iframeID:String=null; //表单IFrameID

private var _DataBindControlsArray:Array=[]; //支持数据绑定的控件数组
private var _controlUID2DataBindParams:Object={}; //控件实例到数据绑定定义的字典
private var _controlUID2Instance:Object={}; //控件名称 控件实例字典
private var _controlUID2ControlParams:Object={}; //控件名称对应的创建控件参数字典
private var _controlUIDAndEventName2JSFunName:Object={}; //控件事件对应的JavaScript函数名称
private var _datasource2DataBindControlList:Object={};
private var _tabsheet2PageControl:Object={}; //TabSheet对应的父PageControl
private var _tabsheet2Visible:Object={}; //TabSheet对应的Visible
private var _lastTextEditor:Object=null;

private var _formDBGrids:Array=[]; //DBGrid列表
private var _formSupportReadOnlyBtns:Array=[]; //支持权限的按钮集合
private var _formPageControl2SelectedIndex:Object={}; //PageControl对应的缺省选定页
private var _formValidControls:Array=[]; //数据校验定义列表
private var _formLookupComboBoxs:Array=[]; //Lookup Combobox 集合

private var _formDataService:FormDataService=new FormDataService(this); //数据服务

private var _readonly:Boolean=false; //表单是否只读

private var _saveFormErrorMsg:String=""; //最后的错误信息

private var _datasourceInitComboboxsCache:Object={}; //ComboBox 缓存数据
private var _controlStatus:Object=null;

private var _isMainDatasetHasRecord:Boolean=false; //主数据源是否存在记录


private var _formContain:Object=null; //表单容器
private var _formCaption:String=""; //表单标题

[Bindable]
private var _showToolbar:Boolean=true; //是否显示工具条

[Bindable]
private var _showToolbarEx:Boolean=true; //修正后的是否显示工具条

[Bindable]
private var _hideFormTab:Boolean=false; //是否隐藏PageContrl的Tab

[Bindable]
private var showType:int=0; //显示方式 0 对话框 1 显示在PageControl中

[Bindable]
private var purposeType:int=0; //用途分类 0:Portal 1:Portal Item Editor 2:Common Modal Dialog 3:WorkFlow

[Bindable]
private var _isWorkflowEditModalForm:Boolean=false;

[Bindable]
private var _isDialog:Boolean=false;

private var _isCreateCompleted=false;

private var _extButtons:Array=null;

private var _isHiddenButtons:Boolean=false;

private var _callbackIFrameID:String="";


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//此部分代码很乱，需要优化调整 清除 现在逻辑混乱
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 设置表单是否只读 设置的同时自动设置控件的只读属性
 * @param value
 *
 */
public function set readonly(value:Boolean):void
{
	//if (_readonly != value)
	//{
	_readonly=value;
	if (_readonly && !_isDialog && _showToolbar && groupFormToolbar && groupFormToolbar.numElements == 1)
	{
		this.currentState="normalStyle";
		_showToolbarEx=false;
	}
	if (!_readonly && !_isDialog && _showToolbar)
	{
		this.currentState="normalToolbarStyle";
		_showToolbarEx = true;
	}


	resetFormControlStatusByEnabled(!value);
	//}
}

/**
 * 获取表单是否只读属性
 * @return
 */
public function get readonly():Boolean
{
	return _readonly;
}


public function get isCreateCompleted():Boolean
{
	return _isCreateCompleted;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 获取表单传递过来的参数
 * @return 参数值
 */
public function getFormParams():Object
{
	return _params.formParams;
}

//------------------------------------------------------------------------------------------------------------------------------
//获取表单参数 
public function getFormParam(paramName:String):String
{
	var obj:Object=_params.formParams[paramName];
	return obj ? obj.toString() : "";
}

//------------------------------------------------------------------------------------------------------------------------------
//设置表单参数
public function setFormParam(paramName:String, value:String):void
{
	_params.formParams[paramName]=value;
}

public function initFormParams(params:Object):void
{
	_params.formParams=params;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 初始化参数
 * @param params 参数
 */
public function initParams(params:Object):void
{
	_params=params;
	_isHiddenButtons=_params.uiParams.isHiddenButtons;
	_extButtons=_params.uiParams.extButtons;
	_callbackIFrameID=_params.uiParams.callbackIFrameID;
	_showToolbar=Convert.object2Boolean(params.uiParams.showToolbar, true);
	_showToolbarEx=_showToolbar;
	_hideFormTab=Convert.object2Boolean(params.uiParams.hideFormTab, false);
	_isWorkflowEditModalForm=params.uiParams.purposeType == FormConst.PURPOSETYPE_WORKFLOW_EDIT && params.uiParams.showType == FormConst.SHOWTYPE_MODALDIALOG
	_controlStatus=params.uiParams.controlStatus;
	_isDialog=params.uiParams.showType == FormConst.SHOWTYPE_MODALDIALOG;
	this.currentState=_isDialog ? "dialogStyle" : _showToolbar ? "normalToolbarStyle" : "normalStyle";
	if (_isDialog && _isHiddenButtons && (_extButtons == null || _extButtons.length == 0))
		this.currentState="dialogStyleAndHiddenButton";
	_iframeID="FORMS_" + _params.uiParams.prefix + _params.uiParams.formids.join("_");

	if (_isCreateCompleted && groupButtons)
		createExtButtons();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var extButtonInfos:Array=[];

private function createExtButtons():void
{
	for (var i:int=extButtonInfos.length - 1; i >= 0; i--)
	{
		var btn:UIComponent=extButtonInfos[i].btn;
		btn.removeEventListener(MouseEvent.CLICK, event4ExtButtonClick);
		groupButtons.removeElement(btn);
	}
	extButtonInfos=[];

	if (_isHiddenButtons && btnOK && btnOK.parent == groupButtons)
		groupButtons.removeElement(btnOK);

	if (_isHiddenButtons && btnSaveAndSend && btnSaveAndSend.parent == groupButtons)
		groupButtons.removeElement(btnSaveAndSend);

	if (!_isHiddenButtons && btnOK && btnOK.parent != groupButtons)
		groupButtons.addElementAt(btnOK, groupButtons.numElements - 1);

	if (!_isHiddenButtons && btnSaveAndSend && btnSaveAndSend.parent != groupButtons && btnSaveAndSend.visible)
		groupButtons.addElementAt(btnSaveAndSend, groupButtons.numElements - 1);



	if (_extButtons != null && _extButtons.length != 0)
	{
		var index:int=groupButtons.numElements - 1;
		for (var i:int=0; i < _extButtons.length; i++)
		{
			var eBtn:Button=new Button();
			eBtn.label=_extButtons[i].label;
			eBtn.width=75;
			eBtn.height=30;
			eBtn.setStyle("fontWeight","bold");
			eBtn.addEventListener(MouseEvent.CLICK, event4ExtButtonClick);
			groupButtons.addElementAt(eBtn, index);
			extButtonInfos.push({btn: eBtn, funname: _extButtons[i].funname});
		}
	}
}

private function event4ExtButtonClick(event:MouseEvent):void
{
	for (var i:int=0; i < extButtonInfos.length; i++)
	{
		if (extButtonInfos[i].btn == event.currentTarget)
		{
			IFrameUtils.execute(_callbackIFrameID, extButtonInfos[i].funname, {});
			break;
		}
	}
}

protected function groupButtons_creationCompleteHandler(event:FlexEvent):void
{
	createExtButtons();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function get params():Object
{
	return _params;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function test():String
{
	return "I'm a test";
}

///////////////////////////////////////////////////////////////////////////////////////
//获取表单数据是否为新增记录
public function isNew():Boolean
{
	return !isDatasetExistRecord();
}

///////////////////////////////////////////////////////////////////////////////////////
//获取表单数据是否为新增记录
public function existsRecord(datasetName:String):Boolean
{
	return _formDataService.getRecordIndex(datasetName) != -1;
}

//////////////////////////////////////////////////////////////////////////////////////
//是否存在数据集
public function existsDataSet(datasetName:String):Boolean
{
	return _formDataService.getDataSet(datasetName);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getMainDataSetKey():String
{
	return _formDataService.getMainDatasetID();
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var _tag:String="";

public function get tag():String
{
	return this._tag;
}

public function set tag(value:String):void
{
	this._tag=value;
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var _modified:Boolean=false;

public function get modified():Boolean
{
	return _modified;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function set modified(v:Boolean):void
{
	if (_modified != v)
	{
		_modified=v;
		var caption:String=_formCaption + (_modified ? "(*)" : "");
		if (_formContain != null)
		{
			if (_formContain instanceof NavigatorContent)
				(_formContain as NavigatorContent).label=caption;
			else
				_formContain.title=caption;
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function isDatasetExistRecord():Boolean
{
	return _isMainDatasetHasRecord;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function close():void
{
	eventCloseButtonClick(null);
}
