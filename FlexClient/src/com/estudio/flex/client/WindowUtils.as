import com.estudio.flex.client.WinICQ;
import com.estudio.flex.client.WinSearchForm;
import com.estudio.flex.common.FormConst;
import com.estudio.flex.common.InterfaceFormUI;
import com.estudio.flex.common.InterfacePortalGrid;
import com.estudio.flex.common.InterfacePortalGridEx;
import com.estudio.flex.common.InterfaceWorkFlowUI;
import com.estudio.flex.component.FormNavigatorContent;
import com.estudio.flex.component.TitleWindowEx;
import com.estudio.flex.utils.AjaxUtils;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;
import com.estudio.flex.utils.UIUtils;
import com.google.code.flexiframe.IFrame;
import com.utilities.IconUtility;

import flash.desktop.Clipboard;
import flash.desktop.ClipboardFormats;
import flash.display.DisplayObject;
import flash.display.Loader;
import flash.events.MouseEvent;
import flash.system.System;
import flash.ui.ContextMenu;

import mx.controls.Alert;
import mx.controls.ButtonBar;
import mx.controls.Menu;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.events.MenuEvent;
import mx.events.ModuleEvent;
import mx.graphics.SolidColor;
import mx.graphics.SolidColorStroke;
import mx.managers.FocusManager;
import mx.managers.PopUpManager;
import mx.utils.UIDUtil;

import spark.components.BorderContainer;
import spark.components.Button;
import spark.components.Group;
import spark.components.HGroup;
import spark.components.NavigatorContent;
import spark.components.RichEditableText;
import spark.components.TextInput;
import spark.components.TitleWindow;
import spark.components.VGroup;
import spark.events.TitleWindowBoundsEvent;
import spark.layouts.supportClasses.LayoutBase;
import spark.modules.Module;
import spark.modules.ModuleLoader;
import spark.primitives.Line;

[Embed(source="/assets/common/forms.png")]
[Bindable]
public var windowTitleIcon:Class;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//私有变量定义区域
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var _window2Object:Object={};

private var _window2IFrame:Object={};

private var _btn2params:Object={};

private var _formIDS2Module:Object={}; //表单ID对应的模块实例

private var _window2CallbackFun:Object={};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 将UI对象以模式对话框的方式显示出来
 * @param component 需要显示的组件
 * @param width 模式窗口宽
 * @param height 模式窗口高
 * @param caption 窗口标题
 * @param callBackup 窗口被选中关闭时需要执行的回调函数及其他一些相关的对象
 * @return 窗口实例
 */
public function modalDialog(component:UIComponent, width:int, height:int, caption:String, callBackup:Object=null, closeFuncton:Object=null, iframeid:String=null):TitleWindow
{
	component.left=0;
	component.right=0;
	component.top=0;
	component.bottom=0;

	var dialog:TitleWindow=new TitleWindowEx();

	dialog.title=caption;
	dialog.width=width;
	dialog.height=height;

	dialog.addElement(component);

	_window2Object[component.uid]=dialog;
	_window2Object[dialog.uid]=component;
	_window2CallbackFun[dialog.uid]={iframeid: iframeid, callbackFun: callBackup, closeCallbackFun: closeFuncton};

	dialog.addEventListener(CloseEvent.CLOSE, eventDialogOnClose);

	FlexGlobals.topLevelApplication.showPopupWindow(dialog, true);
	//PopUpManager.centerPopUp(dialog);

	dialog.setFocus();

	return dialog;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 在模式窗口中显示URL制定的HTML页面
 * @param url 页面地址
 * @param width 窗口宽
 * @param height 窗口高
 * @param caption 窗口标题
 * @param btns 需要在窗口底部添加的按钮标题列表 例如["保存","关闭"]
 * @param funNames 在窗口底部添加的按钮点击时调用的脚本函数名称列表 例如["funJsSave","funJsClose"]
 */
public function dialogURL(url:String, width:int, height:int, caption:String, btns:Array, funNames:Array):void
{
//    var group : VGroup = new VGroup();
//    group.gap = 0;
//    var dialog : TitleWindow = modalDialog(group , width , height , caption , null);
//    dialog.addEventListener(TitleWindowBoundsEvent.WINDOW_MOVE , eventIFrameMove);
//
//    var iframe : IFrame = new IFrame("dialog_");
//    iframe.source = url;
//    UIUtils.fullAlign(iframe);
//    group.addElement(iframe);
//
//    _window2IFrame[dialog.uid] = iframe;
//
//    var line : Line = new Line();
//    line.stroke = new SolidColorStroke(0x000000 , 1 , 0.5);
//    line.left = 0;
//    line.height = 0;
//    line.percentWidth = 100;
//
//    group.addElement(line);
//
//    var bargroup : BorderContainer = new BorderContainer();
//    UIUtils.bottomAlign(bargroup , 40);
//    bargroup.setStyle("borderVisible" , false);
//
//    group.addElement(bargroup);
//
//    for (var i : int = 0 ; i < btns.length ; i++)
//    {
//        var btn : Button = new Button();
//        btn.label = btns[i];
//        btn.width = 75;
//        btn.height = 30;
//        btn.top = 5;
//        btn.right = (btns.length - i - 1) * 80 + 5;
//        var funName : String = funNames[i];
//
//        _btn2params[btn.uid] = {fid: iframe.iframeId , did: dialog.uid , fun: funName};
//
//        btn.addEventListener(MouseEvent.CLICK , function(event : MouseEvent) : void
//        {
//            var p : Object = _btn2params[event.currentTarget["uid"]];
//            IFrameUtils.execute(p.fid , "WindowUtils.initWINID" , p.did);
//            if (!StringUtils.isEmpty(p.fun))
//                IFrameUtils.execute(p.fid , p.fun , null);
//        });
//        bargroup.addElement(btn);
//    }
//    dialog.invalidateDisplayList();
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 关闭窗口
 * @param form 在模式窗口中被显示的对象
 */
public function closeWindow(form:UIComponent):void
{
	var dialog:TitleWindow=_window2Object[form.uid];

	if (_window2IFrame[dialog.uid] && _window2IFrame[dialog.uid] is IFrame)
	{
		var iframe:IFrame=IFrame(_window2IFrame[dialog.uid]);
		iframe.removeIFrame();
		dialog.removeEventListener(TitleWindowBoundsEvent.WINDOW_MOVE, eventIFrameMove);
	}
	executeModalDialogCloseCallFunction(dialog);

	delete _window2Object[form.uid];
	delete _window2Object[dialog.uid];
	delete _window2IFrame[dialog.uid];
	delete _window2CallbackFun[dialog.uid];

	closePopupWindow(dialog);

	dialog.removeEventListener(CloseEvent.CLOSE, eventDialogOnClose);
	dialog.removeAllElements();
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//编辑工作流表单
public function editWorkFlowActivityForms(workflowUIInstance:InterfaceWorkFlowUI, ui_id:String, stepId:String, params:Object, callFun:Function, isReadOnly:Boolean, isPageControlFormMode:Boolean, formUniqueKey:String):void
{
	var formids:Array=[];
	var i:int=0;
	var controlStatus:Array=[];
	var formDefines:Array=params.forms;
	for (i=0; i < formDefines.length; i++)
	{
		var formDefine:Object=formDefines[i];
		formids.push(formDefine.id);
		if (formDefine.controls)
		{
			for (var j:int=0; j < formDefine.controls.length; j++)
				controlStatus.push(formDefine.controls[j]);
		}
	}

	var processId:String=params.process_id;
	var processTypeId:String=params.process_type_id;
	var activityName:String=params.activity_name;
	var activityCaption:String=params.activityCaption;
	var formCaption:String=params.formCaption;
	var formParams:Object={workflow_ui_id: ui_id, callfrom: "workflow", WORKFLOW_PROCESS_ID: processId, processTypeId: processTypeId, activityName: activityName, process_step_id: stepId};
	for (var k:String in params)
	{
		if (!StringUtils.equal("forms", k) && !formParams[k])
			formParams[k]=params[k];
	}
	var uiParams:Object={callfunction: callFun};
	var reqParams:Object={formParams: formParams, uiParams: uiParams};
	reqParams.uiParams.purposeType=FormConst.PURPOSETYPE_WORKFLOW_EDIT;
	reqParams.uiParams.formids=formids;
	reqParams.uiParams.showToolbar=false;
	reqParams.uiParams.hideFormTab=isPageControlFormMode;
	reqParams.uiParams.prefix="pagecontrol_workflow_form_";
	reqParams.uiParams.showType=isPageControlFormMode ? FormConst.SHOWTYPE_NAVIGATION : FormConst.SHOWTYPE_MODALDIALOG;
	reqParams.uiParams.workflowUIInstance=workflowUIInstance;
	reqParams.uiParams.controlStatus={uid: activityName, controls: controlStatus};

	getAndProcessFormUI(reqParams, isReadOnly, function(module:UIComponent, form:InterfaceFormUI, isFirst:Boolean):void
	{
		if (isPageControlFormMode)
		{
			_currentWorkflowUiTabSheet=pagecontrolMain.selectedChild as NavigatorContent;
			if (_workflowFormContain == null)
				_workflowFormContain=new FormNavigatorContent();
			_workflowFormContain.label=formCaption;
			if (_workflowFormContain.owner == null)
				pagecontrolMain.addElement(_workflowFormContain);
			_workflowFormUniueKey=formUniqueKey;
			pagecontrolMain.selectedChild=_workflowFormContain;
			UIUtils.fullAlign(module);
			_workflowFormContain.addForm(workflowUIInstance, module, form);
		}
		else
		{
			var formSize:Object=getFormSize(formids);
			modalDialog(module, formSize.w, formSize.h, formCaption, null, null, null);
		}
	});
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function createFormInstance4PortalItemEx(control:Object, formParams:Object):void
{
	var formIds:Array=formParams.formIds;
	if (formIds.length == 0)
	{
		AlertUtils.msnMessage("系统", "尚未定义同控件相关的表单，请于系统管理员联系.", true);
		return;
	}
	var reqParams:Object={formParams: {}, uiParams: {formids: formIds, purposeType: FormConst.PURPOSETYPE_PORTALITEMEX_CONTROL}};
	reqParams.uiParams.showType=FormConst.SHOWTYPE_NAVIGATION;
	reqParams.uiParams.prefix=control.controlName + control.portalInstance.getPortalID() + "_";
	reqParams.uiParams.showToolbar=formParams.showToolbar;
	reqParams.uiParams.control=control;
	getAndProcessFormUI(reqParams, formParams.readonly, function(module:UIComponent, form:InterfaceFormUI, isFirst:Boolean):void
	{
		module.percentWidth=100;
		module.percentHeight=100;
		control.formContain.addElement(module);
		control.formInstance=form;
		control.portalInstance.initFormControlReqParams();
	});
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function loadFormData4PortalItemEx(form:InterfaceFormUI, formParams:Object):void
{
	form.initFormParams(formParams);
	var httpParams:Object=ObjectUtils.mergeParams({formids: formParams.formIds.join(","), onlydata: 1}, formParams);
	httpParams.o="getdefine"
	AjaxUtils.postData("../client/formsdefine", httpParams, function(text:String, token:Object):void
	{
		var obj:Object=JSON.parse(text);
		if (obj.r)
			form.initFormData(obj.data, true, false, true);
	});
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 编辑扩展表单
 */
public function editPortalItemEx(portalDefineEx:InterfacePortalGridEx, formParams:Object, formShowType:int=0, isReadonly:Boolean=false):void
{
	//{"controlName":"UserInfoGrid","callfrom":"PortalGridEx","id":"","formIds":["225765"],"DepartmentTree.__key__":"-1","p_id":"-1"}
	var formIds:Array=formParams.formIds;
	if (formIds.length == 0)
	{
		AlertUtils.msnMessage("系统", "尚未定义同控件相关的表单，请于系统管理员联系.", true);
		return;
	}
	var reqParams:Object={formParams: formParams, uiParams: {formids: formIds, purposeType: FormConst.PURPOSETYPE_PORTALITEMEX_EDIT, portalInstance: portalDefineEx}};
	reqParams.uiParams.showType=formShowType == 0 ? FormConst.SHOWTYPE_MODALDIALOG : FormConst.SHOWTYPE_NAVIGATION;
	reqParams.uiParams.prefix="portalitemex_edit_";
	getAndProcessFormUI(reqParams, formParams.readonly, function(module:UIComponent, form:InterfaceFormUI, isFirst:Boolean):void
	{
		form.readonly=isReadonly;
		if (formShowType == 0) //对话框中显示
		{
			var formSize:Object=getFormSize(formIds);
			modalDialog(module, formSize.w + 25, formSize.h, formParams.formCaption);
		}
		else
		{
			module.percentWidth=100;
			module.percentHeight=100;
			module.horizontalCenter=0;
			var tabsheet:NavigatorContent=formShowType == 1 ? portalDefineEx.tabsheet : CommonTabSheet;
			tabsheet.icon=null;
			tabsheet.icon=IconUtility.getClass(tabsheet, "../images/18x18/application_form.png", 18, 18);
			tabsheet.label=formParams.formCaption;
			tabsheet.removeAllElements();
			tabsheet.addElement(module);
			_applicationMap.tabSheet2SelectedIndex[tabsheet.uid]=-1;
			if (tabsheet == CommonTabSheet)
			{
				_editPortalItemFormInstance={portalInstance: portalDefineEx, isNew: formParams.isNew, key: formParams.isNew ? "" : portalDefineEx.getSelectedKey(formParams.controlName)};
			}
			if (pagecontrolMain.selectedChild != tabsheet)
				pagecontrolMain.selectedChild=tabsheet;
		}
	});
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 编辑PortalGridDefine中被选择的Tree或Grid数据
 * @param portalDefine 实现InterfacePortalGrid接口的栏目实例
 * @param isTree true-新增或编辑Tree数据 false-新增或编辑Grid数据
 * @param isNew true-新增数据 false-编辑现有数据
 * @param readonly 数据是否为只读数据 如果数据为只读数据则弹出的表单被禁止编辑保存 缺省值:false
 */
public function editPortalItem(portalDefine:InterfacePortalGrid, isTree:Boolean, isNew:Boolean, readonly:Boolean=false, formShowType:int=0):void
{
	var formDefine:Object=portalDefine.getForms(isTree);
	var formIds:Array=formDefine.ids;
	if (formIds.length == 0)
	{
		AlertUtils.msnMessage("系统", "尚未定义同控件相关的表单，请于系统管理员联系.", true);
		return;
	}

	var reqParams:Object={formParams: portalDefine.getParams(isTree, isNew), uiParams: {formids: formIds, purposeType: FormConst.PURPOSETYPE_PORTALITEM_EDIT, portalInstance: portalDefine, isTree: isTree, isNew: isNew}};
	reqParams.uiParams.showType=formShowType == 0 ? FormConst.SHOWTYPE_MODALDIALOG : FormConst.SHOWTYPE_NAVIGATION;
	reqParams.uiParams.prefix="portal_item_edit_";
	getAndProcessFormUI(reqParams, readonly, function(module:UIComponent, form:InterfaceFormUI, isFirst:Boolean):void
	{
		if (formShowType == 0) //对话框中显示
		{
			var formSize:Object=getFormSize(formIds);
			modalDialog(module, formSize.w, formSize.h, portalDefine.getFormCaption(isTree, isNew));
		}
		else
		{
			module.percentWidth=100;
			module.percentHeight=100;
			var tabsheet:NavigatorContent=formShowType == 1 ? portalDefine.TabSheet : CommonTabSheet;
			tabsheet.icon=null;
			tabsheet.icon=IconUtility.getClass(tabsheet, "../images/18x18/application_form.png", 18, 18);
			tabsheet.label=portalDefine.getFormCaption(isTree, isNew);
			tabsheet.removeAllElements();
			tabsheet.addElement(module);
			_applicationMap.tabSheet2SelectedIndex[tabsheet.uid]=-1;
			if (tabsheet == CommonTabSheet)
				_editPortalItemFormInstance={portalInstance: portalDefine, isNew: isNew, isTree: isTree, key: portalDefine.getSelectedKey(isTree, isNew)};
			pagecontrolMain.selectedChild=tabsheet;
		}
	});
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var _editPortalItemFormInstance:Object={};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function closePortalItemEditForm(portalInstance:Object):void
{
	if (_editPortalItemFormInstance.portalInstance == portalInstance && _applicationMap.id2TabSheet[_commonTabSheetID])
	{
		callLater(function():void
		{
			var commonTabSheet:NavigatorContent=_applicationMap.id2TabSheet[_commonTabSheetID] as NavigatorContent;
			if (commonTabSheet)
			{
				removeMainPageControlPage(commonTabSheet);
				pagecontrolMain.removeElement(commonTabSheet);
			}
			_editPortalItemFormInstance={};
		});
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function resetCurrentPortalEditFormInfo(key:String):void
{
	if (_editPortalItemFormInstance.portalInstance)
	{
		_editPortalItemFormInstance.isNew=false;
		_editPortalItemFormInstance.key=key;
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function closeCurrentPortalEditFormInfo(portalInstance:Object, key:String):void
{
	if (_editPortalItemFormInstance.portalInstance == portalInstance && (_editPortalItemFormInstance.key == key || StringUtils.isEmpty(key)))
		closePortalItemEditForm(portalInstance);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *历史回退
 * @param params
 *
 */
public function goHistoryBack(params:Object):void
{
	//{tabsheet: tabsheet , icon: tabsheet.icon , label: tabsheet.label , child: tabsheet.getElementAt(0) , itemIndex: _applicationMap.tabSheet2SelectedIndex[tabsheet.uid]}
	var tabsheet:NavigatorContent=params.tabsheet as NavigatorContent;
	var label:String=params.label as String;
	var child:UIComponent=params.child as UIComponent;
	var itemIndex:String=params.itemIndex as String;

	tabsheet.icon=null;
	tabsheet.removeAllElements();
	tabsheet.addElement(child);
	tabsheet.label=label;
	tabsheet.icon=IconUtility.getClass(tabsheet, _applicationMap.tabSheet2IconURL[tabsheet.uid], 18, 18);
	_applicationMap.tabSheet2SelectedIndex[tabsheet.uid]=itemIndex;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 在模式对话框中显示表单列表
 * @param formids 表单列表
 * @param w 模式窗体宽
 * @param h 模式窗体高
 * @param caption 模式窗体标题
 * @param readonly 窗体内容是否为只读
 * @param params 传递给窗口的参数
 * @param callbackFun 回调函数定义 缺省值:null
 */
public function modalForms(formids:Array, caption:String, readonly:Boolean, params:Object, callbackFun:Object=null, closeCallFunction:Object=null, iframeid:String=null, isSilent:Boolean=false, rightSetting:Object=null):void
{
	var formParams:Object=ObjectUtils.unescape4flex(params);
	var uiParams:Object={formids: formids, prefix: "modal_dialog_", showType: FormConst.SHOWTYPE_MODALDIALOG, purposeType: FormConst.PURPOSETYPE_COMMON_DIALOG};
	uiParams.isHiddenButtons=formParams.IS_HIDDEN_BUTTONS;
	uiParams.extButtons=formParams.EXT_BUTTONS;
	uiParams.callbackIFrameID=iframeid;
	var reqParams:Object={formParams: formParams, uiParams: uiParams};
	getAndProcessFormUI(reqParams, readonly, function(module:UIComponent, form:InterfaceFormUI, isFirst:Boolean):void
	{
		var formSize:Object=getFormSize(formids);
		modalDialog(module, formSize.w, formSize.h, ObjectUtils.unescape4flex(caption) as String, callbackFun, closeCallFunction, iframeid);
	});
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function closeModalForms(formIds:Array):void
{
	var key:String="";
	for (var k:String in _window2Object)
	{
		var obj:Object=_window2Object[k];
		if (obj is ModuleLoader && obj.child is InterfaceFormUI)
		{
			var formInstance:InterfaceFormUI=obj.child as InterfaceFormUI;
			var formParams:Array=formInstance.params.uiParams.formids;
			if (formParams.join(",") == formIds.join(","))
			{
				key=k;
				break;
			}
		}
	}
	if (key != "")
		closeModalDialog(key);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function closePageControlWorkFlowForms(formUniqueKey:String):void
{
	if (StringUtils.equal(_workflowFormUniueKey, formUniqueKey) && _workflowFormContain.owner != null)
	{
		callLater(function():void
		{
			var isEditPageControlSelected:Boolean=pagecontrolMain.selectedChild == _workflowFormContain;
			if (isEditPageControlSelected)
				pagecontrolMain.selectedChild=_currentWorkflowUiTabSheet;
			_currentWorkflowUiTabSheet=null;
		});

		this.pagecontrolMain.removeElement(_workflowFormContain);
		_currentWorkflowUiTabSheet=null;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//private var _workflowFormContain:FormNavigatorContent = null; //工作流编辑窗体
//private var _workflowFormUniueKey:String = null; //工作流正在编辑窗体的唯一标识号

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function executeModalDialogCallFunction(ui:UIComponent):void
{
	var p:Object=_window2CallbackFun[_window2Object[ui.uid]];
	if (p && !StringUtils.isEmpty(p.iframeid) && p.callbackFun)
	{
		IFrameUtils.execute(p.iframeid, p.callbackFun, {});
	}
}

private function executeModalDialogCloseCallFunction(dialog:TitleWindow):void
{
	var p:Object=_window2CallbackFun[dialog.uid];
	if (p && !StringUtils.isEmpty(p.iframeid) && p.closeCallbackFun)
	{
		IFrameUtils.execute(p.iframeid, p.closeCallbackFun, {});
	}
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 根据窗口ID关闭相应的窗口 此函数主要用于通过JavaScript来关闭窗口函数
 * @param winId 表单ID
 */
public function closeModalDialog(winId:String):void
{
	closeWindow(_window2Object[winId]);
}

public function saveModalForms(formIds:Array):void
{
	var key:String="";
	for (var k:String in _window2Object)
	{
		var obj:Object=_window2Object[k];
		if (obj is ModuleLoader && obj.child is InterfaceFormUI)
		{
			var formInstance:InterfaceFormUI=obj.child as InterfaceFormUI;
			var formParams:Array=formInstance.params.uiParams.formids;
			if (formParams.join(",") == formIds.join(","))
			{
				formInstance.save();
				break;
			}
		}
	}
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 窗口关闭事件处理函数
 * @param event
 */
private function eventDialogOnClose(event:CloseEvent):void
{
	var dialog:TitleWindow=event.currentTarget as TitleWindow;
	closeWindow(_window2Object[dialog.uid]);
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * bug修正类事件处理函数
 * @param
 * @return
 */
private function eventIFrameMove(event:TitleWindowBoundsEvent):void
{
	var iframe:IFrame=((event.currentTarget as TitleWindow).getElementAt(0) as VGroup).getElementAt(0) as IFrame;
	iframe.invalidateDisplayList();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//获取表单模块
private function getAndProcessFormUI(reqParams:Object, readonly:Boolean, processFun:Function):void
{
	var ids:String=reqParams.uiParams.formids.join(",");
	var key:String=reqParams.uiParams.prefix + ids;
	var module:ModuleLoader=_formIDS2Module[key];
	var form:InterfaceFormUI=null;
	if (!module)
	{
		var formDefineInfo:Object=JSFunUtils.JSFun("getFormsDefine", JSON.stringify(ObjectUtils.mergeParams({formids: ids}, reqParams.formParams)));
		if (!formDefineInfo)
		{
			AlertUtils.alert("无法获取绑定的表单信息，请与系统管理人员联系!", AlertUtils.ALERT_STOP);
			return;
		}
		module=new ModuleLoader();
		module.addEventListener(ModuleEvent.READY, function(event:ModuleEvent):void
		{
			form=(event.currentTarget as ModuleLoader).child as InterfaceFormUI;
			form.initParams(reqParams);
			form.createUI(formDefineInfo);
			form.readonly=readonly;
			registerFormUIAndParams(form.getIFrameID(), form, reqParams.formParams);
			processFun(module, form, true);
		});
		module.loadModule("./flash/com/estudio/flex/module/Form.swf?version=" + appVersion);
		_formIDS2Module[key]=module;
	}
	else
	{
		form=module.child as InterfaceFormUI
		form.initParams(reqParams);
		form.readonly=readonly;
		form.initFormData(JSFunUtils.JSFun("getFormsDefine", JSON.stringify(ObjectUtils.mergeParams({formids: ids, onlydata: 1}, reqParams.formParams))).data, true, false, true);
		form.setControlStatus();
		form.resumePageControlSelectedIndex();
		registerFormUIAndParams(form.getIFrameID(), form, reqParams.formParams);
		processFun(module, form, false);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//获取表单尺寸
private function getFormSize(formIds:Array):Object
{
	var formSize:Object=JSFunUtils.JSFun("getPortalFormSizes", formIds);
	var app:DisplayObject=FlexGlobals.topLevelApplication as DisplayObject;
	formSize.w+=(formIds.length == 1 ? 20 : 30);
	formSize.h+=(formIds.length == 1 ? 90 : 120);
	if (formSize.w > app.width)
		formSize.w=app.width;
	if (formSize.h > app.height)
		formSize.h=app.height;
	formSize.w=Math.max(380, formSize.w);
	formSize.h=Math.max(170, formSize.h);
	return formSize;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function existsModalForm():Boolean
{
	return modalComponents.length != 0;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var modalComponents:Array=[];

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function showPopupWindow(win:UIComponent, isModal:Boolean=false, isCenter:Boolean=false, x:int=-1, y:int=-1):void
{
	PopUpManager.addPopUp(win, FlexGlobals.topLevelApplication as DisplayObject, isModal);
	if (isModal)
	{
		PopUpManager.centerPopUp(win);
		modalComponents.push(win);
		executeModuleFunctuion("startMouseOverEvent");
	}
	else if (isCenter)
	{
		PopUpManager.centerPopUp(win);
	}

	if (x != -1 && y != -1)
	{
		win.x=x;
		win.left=x;
		win.y=y;
		win.top=y;
	}

}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function closePopupWindow(win:UIComponent):void
{
	PopUpManager.removePopUp(win);
	var index:int=modalComponents.indexOf(win);
	if (index != -1)
	{
		executeModuleFunctuion("stopMouseOverEvent");
		ArrayUtils.remove(modalComponents, index);
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function broadcastRightClickEvent():void
{
	executeModuleFunctuion("eventRightClickEvent");
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function executeModuleFunctuion(funName:String):void
{
	var win:TitleWindow=null;
	var topUI:Object=modalComponents[modalComponents.length - 1];
	if (topUI is TitleWindow)
	{
		var child:Object=TitleWindow(topUI).getElementAt(0);
		if (child is ModuleLoader)
			child=ModuleLoader(child).child;
		if (child && child.hasOwnProperty(funName))
			child[funName]();
	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//private var editorContextMenu:Menu=null;
private var editorContextMenuItems:Array=[];
private var currentEditor:RichEditableText=null;
private var editorContextMenu:ContextMenu=new ContextMenu();

public function popupEditorContextMenu(editor:RichEditableText):void
{
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createEditorContextMenu():void
{
//	editorContextMenuItems.push({label: "复制 Ctrl+C", tag: "Copy"});
//	editorContextMenuItems.push({label: "剪切 Ctrl+X", tag: "Cut"});
//	editorContextMenuItems.push({label: "粘贴 Ctrl+V", tag: "Paste"});
//	editorContextMenuItems.push({label: "清除 Del", tag: "Delete"});
//	editorContextMenuItems.push({type: "separator"});
//	editorContextMenuItems.push({label: "全选 Ctrl+A", tag: "SelectAll"});
//
//	editorContextMenu=Menu.createMenu(FlexGlobals.topLevelApplication as UIComponent, editorContextMenuItems);
//	editorContextMenu.variableRowHeight=true;
//	editorContextMenu.addEventListener(MenuEvent.ITEM_CLICK, eventEditorMenuItemClick);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventEditorMenuItemClick(event:MenuEvent):void
{
	//
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function showSearchDialog(callFunction:Function=null, x:int=-1, y:int=-1):void
{
	WinSearchForm.showForm(callFunction, x, y);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function ExecuteICQMessage():void
{
	WinICQ.execute();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function viewAttachmentWindow(pId:String,  type:String,id:String, url:String):void
{
	//var url="../client/attachment_list.jsp?p_id=" + data[keyFieldName] + "&type=" + attachmentType;
	goURL(url, "attachmentWindow");
}
