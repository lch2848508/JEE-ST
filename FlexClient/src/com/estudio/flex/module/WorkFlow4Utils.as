import com.estudio.flex.RUNTIME_COMMAND;
import com.estudio.flex.component.Pagination;
import com.estudio.flex.module.component.AdvDataGridColumnHrefRender;
import com.estudio.flex.module.component.AdvancedDataGridGroupItemRendererEx;
import com.estudio.flex.module.component.WinDiagram;
import com.estudio.flex.module.component.WinIdea;
import com.estudio.flex.module.component.WinProcessMessage;
import com.estudio.flex.module.component.WinSelectActivityAndUser;
import com.estudio.flex.module.component.WinSelectActivityAndUsers_bak;
import com.estudio.flex.module.component.WinSelectProcessType;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayCollectionUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.ui.Keyboard;

import mx.collections.ArrayCollection;
import mx.collections.GroupingCollection2;
import mx.collections.HierarchicalCollectionView;
import mx.containers.Grid;
import mx.controls.DateField;
import mx.controls.LinkBar;
import mx.controls.LinkButton;
import mx.controls.Menu;
import mx.controls.advancedDataGridClasses.FTEAdvancedDataGridItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
import mx.events.MenuEvent;
import mx.formatters.DateFormatter;
import mx.managers.SystemManager;

import spark.components.Button;
import spark.components.ComboBox;
import spark.components.Label;
import spark.components.TextInput;
import spark.events.IndexChangeEvent;

private var _tag:String="";

private var _ui_id:String="";

private var _workflowId:String="";

private var _iframeId:String="";

private var _uiDefine:Object=null;

private var _toolbarItems:Array=[];

private var _selected_step_id:String="";

//-------------------------------------------------------------------------------------------
[Embed(source="/assets/common/new.png")] //新建
[Bindable]
public var imgNew:Class;

[Embed(source="/assets/common/delete.png")] //废除
[Bindable]
public var imgDelete:Class;

[Embed(source="/assets/common/back.png")] //退回
[Bindable]
public var imgBack:Class;

[Embed(source="/assets/common/go.png")] //发送
[Bindable]
public var imgGo:Class;

[Embed(source="/assets/common/go_red.png")] //发送
[Bindable]
public var imgGoRed:Class;


[Embed(source="/assets/common/sitemap.png")] //流程图
[Bindable]
public var imgWorkFlow:Class;

[Embed(source="/assets/common/search.png")] //过滤
[Bindable]
public var imgFilter:Class;

[Embed(source="/assets/common/order.png")] //刷新
[Bindable]
public var imgRefresh:Class;

[Embed(source="/assets/common/idea.png")] //审批意见
[Bindable]
public var imgIdea:Class;

[Embed(source="/assets/common/viewform.png")] //查看表单
[Bindable]
public var imgViewForm:Class;

[Embed(source="/assets/common/editform.png")] //编辑表单
[Bindable]
public var imgEditForm:Class;

[Embed(source="/assets/common/search.png")] //编辑表单
[Bindable]
public var imgSearch:Class;

[Embed(source="/assets/common/ico_clear.png")] //编辑表单
[Bindable]
public var imgClearFilter:Class;

[Embed(source="/assets/common/br_down.png")] //编辑表单
[Bindable]
public var imgArrowDown:Class;

[Embed(source="/assets/common/br_up.png")] //编辑表单
[Bindable]
public var imgArrowUp:Class;

[Embed(source="/assets/common/sign.png")]
[Bindable]
public var imgSign:Class;

[Embed(source="/assets/common/refresh.png")]
[Bindable]
public var imgWorkFlowRefresh:Class;

[Embed(source="/assets/common/email_edit.png")]
[Bindable]
public var imageWorkFlowEditMessage:Class;

[Embed(source="/assets/common/email.png")]
[Bindable]
public var imageWorkFlowViewMessage:Class;

[Embed(source="/assets/common/back_red.png")]
[Bindable]
public var imgBackRed:Class;



private static var SEND_RESULT_SELECTED_USERS:int=1;

private static var SEND_RESULT_OK:int=2;

private var paramName2FilterControl:Object={};

private var filterComboboxValues:Object={};

private var lockFilterControlEvent:Boolean=false;

private var contextPopupMenu:Menu=null;

private var contextPopupMenuItems:Array=[];

private var actionNameFieldName:String="";

private var actionCaptionFieldName:String="";

private var processTypeFieldName:String="";

private var processIdFieldName:String="";

private var isSettingAllWFParams:Boolean=false;

private var allowMaxSelectNumber:int=50;

//------------------------------------------------------------------------------------------
private function getFormUniqueKey(stepId:String):String
{
	return this.ui_id + "-" + stepId;
}

//------------------------------------------------------------------------------------------
private function createToolbarItems(options:Object, hasFilterControl:Boolean):void
{
	if (options.isCreateAble)
	{
		_toolbarItems.push({label: "新建", hint: "新建一个业务", icon: imgNew, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_NEW_CASE}); //新建一个业务
		contextPopupMenuItems.push({label: "新建", hint: "新建一个业务", icon: imgNew, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_NEW_CASE}); //新建一个业务
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isEditFormAble)
	{
		_toolbarItems.push({label: "编辑", hint: "编辑业务表单数据", icon: imgEditForm, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_FORM}); //编辑表单数据
		contextPopupMenuItems.push({label: "编辑", hint: "编辑业务表单数据", icon: imgEditForm, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_FORM}); //编辑表单数据
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isViewFormAble)
	{
		_toolbarItems.push({label: "查看", hint: "查看业务表单数据", icon: imgViewForm, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_FORM}); //查看业务表单数据
		contextPopupMenuItems.push({label: "查看", hint: "查看业务表单数据", icon: imgViewForm, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_FORM}); //查看业务表单数据
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isDeleteAble)
	{
		_toolbarItems.push({label: "删除", hint: "删除一个业务", icon: imgDelete, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_DELETE_CASE}); //废除选定的业务员
		contextPopupMenuItems.push({label: "删除", hint: "删除一个业务", icon: imgDelete, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_DELETE_CASE}); //废除选定的业务员
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isBackAble)
	{
		_toolbarItems.push({label: "退回", hint: "将业务退回给上一环节发件人", icon: imgBack, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_BACK_CASE}); //退回业务
		contextPopupMenuItems.push({label: "退回", hint: "将业务退回给上一环节发件人", icon: imgBack, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_BACK_CASE}); //退回业务
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isSendAble)
	{
		_toolbarItems.push({label: "发送", hint: "将业务发送到下一环节", icon: imgGo, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE});
		contextPopupMenuItems.push({label: "发送", hint: "将业务发送到下一环节", icon: imgGo, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE});
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isIdeaAble)
	{
		_toolbarItems.push({label: "批注", hint: "填写批注", icon: imgIdea, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_IDEA});
		contextPopupMenuItems.push({label: "批注", hint: "填写批注", icon: imgIdea, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_IDEA});
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isSignAble)
	{
		_toolbarItems.push({label: "签收", hint: "签收业务", icon: imgSign, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_SIGN_ITEM});
		contextPopupMenuItems.push({label: "签收业务", hint: "签收业务", icon: imgSign, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_SIGN_ITEM});
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isViewStepAble)
	{
		_toolbarItems.push({label: "流程", hint: "查看业务流程图", icon: imgWorkFlow, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_DIAGRAM});
		contextPopupMenuItems.push({label: "流程", hint: "查看业务流程图", icon: imgWorkFlow, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_DIAGRAM});
		contextPopupMenuItems.push({type: "separator"});
	}


	if (options.isEditMessage)
	{
		_toolbarItems.push({label: "监察督办", hint: "增加督办(问责)信息", icon: imageWorkFlowEditMessage, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_MESSAGE});
		contextPopupMenuItems.push({label: "监察督办", hint: "查看督办(问责)信息", icon: imageWorkFlowEditMessage, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_MESSAGE});
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isViewMessage)
	{
		_toolbarItems.push({label: "监察督办", hint: "查看督办(问责)信息", icon: imageWorkFlowViewMessage, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_MESSAGE});
		contextPopupMenuItems.push({label: "监察督办", hint: "查看督办(问责)信息", icon: imageWorkFlowViewMessage, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_MESSAGE});
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isBackToFirstStep)
	{
		_toolbarItems.push({label: "打回", hint: "直接退件给项目创建人，项目重新流转，但不会删除以前的审批信息。", icon: imgBackRed, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_BACK_TO_CREATOR});
		contextPopupMenuItems.push({label: "打回", hint: "直接退件给项目创建人，项目重新流转，但不会删除以前的审批信息。", icon: imgBackRed, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_BACK_TO_CREATOR});
		contextPopupMenuItems.push({type: "separator"});
	}

	if (options.isSpecialSend)
	{
		_toolbarItems.push({label: "特送", hint: "特送，允许项目无条件发送给后续的环节。", icon: imgGoRed, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE_SPECIAL});
		contextPopupMenuItems.push({label: "特送", hint: "特送", icon: imgGoRed, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE_SPECIAL});
		contextPopupMenuItems.push({type: "separator"});
	}


	//刷新
	if (!options.isGridPageAble)
	{
		_toolbarItems.push({label: "刷新", hint: "刷新业务案件列表", icon: imgWorkFlowRefresh, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_REFRESH});
		contextPopupMenuItems.push({label: "刷新", hint: "刷新业务案件列表", icon: imgWorkFlowRefresh, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_REFRESH});
		contextPopupMenuItems.push({type: "separator"});
	}

	if (hasFilterControl)
		_toolbarItems.push({label: "查询面板", hint: "显示(隐藏)查询面板", icon: imgArrowUp, tag: RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_FILTER_PANEL});

	if (contextPopupMenuItems.length != 0)
		contextPopupMenuItems.length=contextPopupMenuItems.length - 1;
}

//-------------------------------------------------------------------------------------------
//创建过滤控件
private function createFilterControls(params:Array):void
{
	for (var i:int=0; i < params.length; i++)
	{
		var param:Object=params[i];
		var labelStr:String=param.comment;
		var filterControl:String=param.filterControl;
		var paramName:String=param.name;
		var control:UIComponent=null;
		if (StringUtils.equal("Date", filterControl))
		{
			if (!StringUtils.isEmpty(labelStr))
				createFilterLabel(labelStr);
			var datefield:DateField=new DateField();
			datefield.editable=false;
			datefield.formatString="YYYY-MM-DD";
			datefield.yearNavigationEnabled=true;
			//datefield.addEventListener(CalendarLayoutChangeEvent.CHANGE, eventFilterDateChange);
			datefield.height=23;
			this.groupFilter.addElement(datefield);
			control=datefield;

		}
		else if (StringUtils.equal("TextBox", filterControl))
		{
			if (!StringUtils.isEmpty(labelStr))
				createFilterLabel(labelStr);
			var textInput:TextInput=new TextInput();
			textInput.width=param["controlWidth"] ? param["controlWidth"] * 1 : 120;
			textInput.height=23;
			textInput.addEventListener(KeyboardEvent.KEY_DOWN, eventFilterInputKeyPress);
			this.groupFilter.addElement(textInput);
			control=textInput;
		}
		else if (StringUtils.equal("ComboBox", filterControl))
		{
			if (!StringUtils.isEmpty(labelStr))
				createFilterLabel(labelStr);
			var combobox:ComboBox=new ComboBox();
			combobox.labelField="LABEL";
			combobox.height=23;
			combobox.width=param["controlWidth"] ? param["controlWidth"] * 1 : 120;
			this.groupFilter.addElement(combobox);
			if (filterComboboxValues[paramName])
			{
				combobox.dataProvider=new ArrayCollection(filterComboboxValues[paramName]);
				combobox.selectedIndex=0;
				applyComboboxBestWidth(combobox);
			}
			//combobox.addEventListener(IndexChangeEvent.CHANGE, eventFilterDateChange);
			combobox.addEventListener(FlexEvent.CREATION_COMPLETE, function(event:FlexEvent):void
			{
				event.currentTarget.textInput.editable=false;
			});

			control=combobox;
		}
		if (control != null)
		{
			paramName2FilterControl[paramName]=control;
		}
	}

	//创建查询按钮
	var btn:Button=new Button();
	btn.setStyle("icon", imgFilter);
	btn.toolTip="点击查询过滤数据"
	btn.height=23;
	btn.width=23;
	btn.addEventListener(MouseEvent.CLICK, eventFilterDateChange);
	this.groupFilter.addElement(btn);

	btn=new Button();
	btn.setStyle("icon", imgClearFilter);
	btn.toolTip="点击清除查询过滤条件"
	btn.height=23;
	btn.width=23;
	btn.addEventListener(MouseEvent.CLICK, eventClearFilter);
	this.groupFilter.addElement(btn);

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
private function applyComboboxBestWidth(combobox:ComboBox):void
{
	if (combobox && combobox.dataProvider)
	{
		var minWidth:int=0;
		for (var i:int=0; i < combobox.dataProvider.length; i++)
		{
			var item:Object=combobox.dataProvider.getItemAt(i);
			if (item && item.hasOwnProperty("LABEL"))
				minWidth=Math.max(measureText(item.LABEL).width, minWidth);
		}
		combobox.width=minWidth + 40;
		combobox.invalidateSize();
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//过滤器文本输入框回车事件 过滤数据
private function eventFilterInputKeyPress(event:KeyboardEvent):void
{
	if (lockFilterControlEvent)
		return;
	if (event == null || event.keyCode == Keyboard.ENTER)
	{
		this.refresh();
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//DataChange事件
private function eventFilterDateChange(event:Event):void
{
	eventFilterInputKeyPress(null);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//清除查询条件
private function eventClearFilter(event:Event):void
{
	for (var k:String in this.paramName2FilterControl)
	{
		var control:UIComponent=this.paramName2FilterControl[k];
		if (control is TextInput)
			TextInput(control).text="";
		else if (control is DateField)
			DateField(control).text="";
	}
	eventFilterInputKeyPress(null);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建过滤Label
private function createFilterLabel(labelStr:String):void
{
	var label:Label=new Label();
	label.text=labelStr;
	label.verticalCenter=0;
	label.setStyle("fontWeight", "bold");
	this.groupFilter.addElement(label);
}

//-------------------------------------------------------------------------------------------
public function get ui_id():String
{
	return _ui_id;
}

public function set ui_id(v:String):void
{
	_ui_id=v;
	_iframeId="WORKFLOW_UI_" + _ui_id;
}

public function get tag():String
{
	return _tag;
}

public function set tag(v:String):void
{
	_tag=v;
}

public function getIFrameID():String
{
	return _iframeId;
}


//创建界面
public function createUI(uiDefine:Object):Boolean
{
	_uiDefine=uiDefine;
	var hasFilterControl:Boolean=uiDefine.params;
	createToolbarItems(uiDefine.options, hasFilterControl);

	this.actionNameFieldName=_uiDefine.actionNameFieldName
	this.actionCaptionFieldName=_uiDefine.actionCaptionFieldName
	this.processTypeFieldName=_uiDefine.processTypeFieldName
	this.processIdFieldName=_uiDefine.processIdFieldName

	isSettingAllWFParams=!StringUtils.isEmpty(this.actionNameFieldName) && !StringUtils.isEmpty(this.actionCaptionFieldName) && !StringUtils.isEmpty(this.processTypeFieldName) && !StringUtils.isEmpty(this.processIdFieldName);

	return true;
}

public function getUIOptions():Object
{
	return _uiDefine.options;
}

//刷新数据
public function refresh():void
{
	this.Pagination.refresh();
}

//-------------------------------------------------------------------------------------------
//创建完成 DataGridWrap
protected function eventDataGridWrapCreateCompleted(event:FlexEvent):void
{
	DataGridWrap.isIncludeCheckBoxColumn=_uiDefine.options.isGridIncludeCheckBox;
	DataGridWrap.isGroupAble=_uiDefine.options.isGridGroupAble;
	DataGridWrap.columnDefine=_uiDefine.columns;
	DataGridWrap.createColumns(this);
	DataGridWrap.isGridMultiSelectedAble=!_uiDefine.options.isGridIncludeCheckBox && _uiDefine.options.isGridMultiSelectedAble;
}

//-------------------------------------------------------------------------------------------
//创建完成 模块
protected function eventModuleCreateCompleted(event:FlexEvent):void
{
	this.Pagination.portalGrid=this;
	//是否显示分页区域
	if (!_uiDefine.options.isGridPageAble)
	{
		PaginationUI.visible=false;
		PaginationUI.height=0;
		Pagination.numberOfPerPage=2500;
		goPage(2500, 1); //最多一次加载2500个case
	}
	else
	{
		goPage(25, 1); //一页25个case
	}
	var hasFilterControl:Boolean=this._uiDefine.params;
	if (!hasFilterControl)
		this.topGroup.removeElementAt(1);
	else
		createFilterControls(this._uiDefine.params as Array);

	if (initHookParams)
	{
		this[initHookParams.fun](initHookParams.params);
	}

	contextPopupMenu=Menu.createMenu(FlexGlobals.topLevelApplication as UIComponent, contextPopupMenuItems, false);
	contextPopupMenu.variableRowHeight=true;
	contextPopupMenu.labelField="hint";
	FlexGlobals.topLevelApplication.registerControlContextPopupMenu(DataGridWrap.uid, contextPopupMenu);
	//DataGridWrap.addEventListener(RightClickManager.RIGHT_CLICK, eventDataGridWrapContext);
	contextPopupMenu.addEventListener(MenuEvent.ITEM_CLICK, eventContextPopupMenuItemClick);
	DataGridWrap.doubleClickFunction=event4DataGridDoubleClick;

	fix4OtherPortalCall();
}

//-------------------------------------------------------------------------------------------
//双击DataGrid事件
private function event4DataGridDoubleClick():void
{
	if (_uiDefine.options.isEditFormAble)
		workFunction4Href(RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_FORM);
	else if (_uiDefine.options.isViewFormAble)
		workFunction4Href(RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_FORM);
	else
		workFunction4Href(RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_DIAGRAM);
}

//-------------------------------------------------------------------------------------------
private function eventContextPopupMenuItemClick(event:MenuEvent):void
{
	var tag:String=event.item.tag;
	workFunction4Href(tag);
}

//-------------------------------------------------------------------------------------------
private function getCurrentRender(c:Object):Object
{
	var result:Object=null;
	while (c)
	{
		if (c is IListItemRenderer && !(c is LinkButton) && !(c is LinkBar))
		{
			result=c;
			break;
		}
		c=c.owner;
	}
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////
//右键菜单功能
private function eventDataGridWrapContext(event:FlexEvent):void
{
	FlexGlobals.topLevelApplication.eventApplicationRightClick(null);
	if (FlexGlobals.topLevelApplication.existsModalForm())
	{
		FlexGlobals.topLevelApplication.broadcastRightClickEvent();
		return;
	}

	var c:Object=event.target;
	var render:Object=getCurrentRender(c);
	if (render && render.data && DataGridWrap.DataGrid.selectedItem != render.data) //多个LinkButton
		DataGridWrap.DataGrid.selectedItem=render.data;

	var topFlex:UIComponent=FlexGlobals.topLevelApplication as UIComponent;
	var p:Point=new Point(topFlex.mouseX, topFlex.mouseY);
	p.x=Math.min(topFlex.width - contextPopupMenu.width, p.x);
	p.y=Math.min(topFlex.height - contextPopupMenu.height, p.y);
	contextPopupMenu.hide();
	contextPopupMenu.show(p.x, p.y);

	event.stopPropagation();
	event.stopImmediatePropagation();
}

//-------------------------------------------------------------------------------------------
//工具栏按钮点击事件
protected function eventToolbarItemClick(event:ItemClickEvent):void
{
	workFunction4Href(event.item.tag, false);
}

//---------------------------------------------------------------------------------------------
public function workFunction4Href(tag:String, autoCheckItem:Boolean=true):void
{
	if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_NEW_CASE) && !_uiDefine.options.isDataReadOnly)
		WinSelectProcessType.showDialog(createNewCase);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_FORM) && DataGridWrap.selectedStepID != "-1")
		editForm(DataGridWrap.selectedStepID, false);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_FORM) && DataGridWrap.selectedStepID != "-1")
		editForm(DataGridWrap.selectedStepID, true);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_DELETE_CASE) && !_uiDefine.options.isDataReadOnly)
		abandonSelectedCase(autoCheckItem);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE) && !_uiDefine.options.isDataReadOnly)
		sendCaseToNext(null, autoCheckItem);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE_SPECIAL))
		sendCaseToSpecial(null, autoCheckItem);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_BACK_CASE) && !_uiDefine.options.isDataReadOnly)
		backSelectedCase(null, autoCheckItem);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_DIAGRAM) && DataGridWrap.selectedStepID != "-1")
		viewDiagram(DataGridWrap.selectedStepID);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_IDEA) && DataGridWrap.selectedStepID != "-1")
		editIdea(DataGridWrap.selectedStepID);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_BACK_TO_CREATOR) && !_uiDefine.options.isDataReadOnlys)
		backCaseToCreator(DataGridWrap.selectedStepID, autoCheckItem);
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_FILTER_PANEL))
		showOrHideFilterPanel();
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_SIGN_ITEM))
		signSelectedCase();
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_REFRESH))
		Pagination.refresh();
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_MESSAGE) && DataGridWrap.selectedStepID != "-1")
		editAdditionMessage();
	else if (StringUtils.equal(tag, RUNTIME_COMMAND.COMMAND_WORKFLOW_VIEW_MESSAGE) && DataGridWrap.selectedStepID != "-1")
		viewAdditionMessage();
}

//-------------------------------------------------------------------------------------------
//编辑附加信息
private function editAdditionMessage():void
{
	var stepId:String=DataGridWrap.selectedStepID;
	WinProcessMessage.execute(stepId, false);
}

//-------------------------------------------------------------------------------------------
//显示附加信息
private function viewAdditionMessage():void
{
	var stepId:String=DataGridWrap.selectedStepID;
	WinProcessMessage.execute(stepId, true);
}

//-------------------------------------------------------------------------------------------
//签收业务项
private function signSelectedCase():void
{
	var ids:Array=DataGridWrap.getSelectedIDS();
	if (ids.length == 0)
	{
		showHandleMessage("请选择需要签收的业务！", true, true, false);
		return;
	}

	AlertUtils.confirm("案件被签出后会自动转到您的待办箱中，不允许被回签，是否签出？", function():void
	{
		var json:Object=JSFunUtils.JSFun("signProcess", {step_ids: ids.join(",")});
		if (json)
		{
			processHttpMessage(json);
			Pagination.refresh();
		}
	});
}

//-------------------------------------------------------------------------------------------
//签署意见
private function editIdea(step_id:String):void
{
	var json:Object=JSFunUtils.JSFun("getProcessIdeas", {step_id: step_id});
	if (json)
	{
		if (json.r)
		{
			json.instance=this;
			WinIdea.show(json, _uiDefine.options);
		}
		else if (json.msg)
		{
			showHandleMessage(json.msg, true, true, false);
		}
	}
	else
	{
		showHandleMessage("该案件不能添加批注!", false, true, false);
	}
}

//-------------------------------------------------------------------------------------------
private function viewDiagram(step_id:String):void
{
	_selected_step_id=step_id;
	var json:Object=JSFunUtils.JSFun("getProcssDiagram", {step_id: step_id});
	if (json && json.r)
	{
		json.instance=this;
		WinDiagram.show(json, _uiDefine.options);
	}
	else if (json && json.msg)
	{
		AlertUtils.alert(json.msg);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////
private function backCaseToCreator(step_id:String, autoCheckItem:Boolean):void
{
	var selectedItems:Array=DataGridWrap.getSelectedItems(autoCheckItem);
	var selectedProcessInfos:Array=getSeletectProcessStepInfos(selectedItems);
	if (selectedProcessInfos.length == 0 && DataGridWrap.selectedStepID != "-1")
	{
		selectedItems=DataGridWrap.getSelectedItems(true);
		selectedProcessInfos=getSeletectProcessStepInfos(selectedItems);
	}

	if (selectedProcessInfos.length == 0 || selectedProcessInfos.length > allowMaxSelectNumber)
	{ //没有选择案件或案件过多
		showHandleMessage(selectedProcessInfos.length == 0 ? "请首先选择需要退回的案件!" : "一次最多只能选择" + allowMaxSelectNumber + "个案件!", true, true, false); //显示错误消息
		return;
	}
	else
	{ //退件
		AlertUtils.confirm("确定要退回选定的业务?", function():void
		{
			var json:Object=JSFunUtils.JSFun("WORKFLOW_BACK_CASE_TO_CREATOR", JSON.stringify(selectedProcessInfos));
			if (json)
			{
				processHttpMessage(json);
				if (!StringUtils.isEmpty(json.successMsg)) //必须有成功信息才刷新数据
				{
					Pagination.refresh();
					DataGridWrap.restoreSelectedItems(selectedItems);
				}
			}
		});
	}
}

//--------------------------------------------------------------------------------------------
//退件给收件人
private function backSelectedCase(step_id:String, autoCheckItem:Boolean):void
{
	var selectedItems:Array=DataGridWrap.getSelectedItems(autoCheckItem);
	var selectedProcessInfos:Array=getSeletectProcessStepInfos(selectedItems);
	if (selectedProcessInfos.length == 0 && DataGridWrap.selectedStepID != "-1")
	{
		selectedItems=DataGridWrap.getSelectedItems(true);
		selectedProcessInfos=getSeletectProcessStepInfos(selectedItems);
	}

	if (selectedProcessInfos.length == 0 || selectedProcessInfos.length > allowMaxSelectNumber)
	{ //没有选择案件或案件过多
		showHandleMessage(selectedProcessInfos.length == 0 ? "请首先选择需要退回的案件!" : "一次最多只能选择" + allowMaxSelectNumber + "个案件!", true, true, false); //显示错误消息
		return;
	}
	else
	{ //退件
		AlertUtils.confirm("确定要退回选定的业务?", function():void
		{
			var json:Object=JSFunUtils.JSFun("WORKFLOW_BACK_CASE", JSON.stringify(selectedProcessInfos));
			if (json)
			{
				processHttpMessage(json);
				if (!StringUtils.isEmpty(json.successMsg)) //必须有成功信息才刷新数据
				{
					Pagination.refresh();
					DataGridWrap.restoreSelectedItems(selectedItems);
				}
			}
		});
	}
}


//--------------------------------------------------------------------------------------------
//发送选择的案件
private function sendCaseToNext(step_id:String, autoCheckItem:Boolean):void
{
	var selectedItems:Array=DataGridWrap.getSelectedItems(autoCheckItem);
	var selectedProcessInfos:Array=getSeletectProcessStepInfos(selectedItems);
	if (selectedProcessInfos.length == 0 && DataGridWrap.selectedStepID != "-1")
	{
		selectedItems=DataGridWrap.getSelectedItems(true);
		selectedProcessInfos=getSeletectProcessStepInfos(selectedItems);
	}

	if (selectedProcessInfos.length == 0 || selectedProcessInfos.length > allowMaxSelectNumber)
	{
		showHandleMessage(selectedProcessInfos.length == 0 ? "请首先选择需要发送的案件!" : "一次最多只能选择" + allowMaxSelectNumber + "个案件!", true, true, false); //显示错误消息
		return;
	}
	var firstJson:Object=JSFunUtils.JSFun("WORKFLOW_SEND_CASE", JSON.stringify(selectedProcessInfos));
	if (firstJson)
	{
		processHttpMessage(firstJson);
		var multiUsers:Array=firstJson.multiStepInfo;
		if (multiUsers) //弹出了选择用户对话框
		{
			WinSelectActivityAndUser.execute(multiUsers, firstJson.processInfos, function(sendResult:Object):void
			{
				if (sendResult != null)
					processHttpMessage(sendResult);
				if (!StringUtils.isEmpty(sendResult.successMsg) || !StringUtils.isEmpty(firstJson.json))
				{
					Pagination.refresh();
					DataGridWrap.restoreSelectedItems(selectedItems);
				}
			});
		}
		else
		{
			//说明直接发送成功
			if (!StringUtils.isEmpty(firstJson.successMsg))
			{
				Pagination.refresh();
				DataGridWrap.restoreSelectedItems(selectedItems);
			}
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////
private function sendCaseToSpecial(step_id:String, autoCheckItem:Boolean):void
{
	var selectedItems:Array=DataGridWrap.getSelectedItems(autoCheckItem);
	var selectedProcessInfos:Array=getSeletectProcessStepInfos(selectedItems);
	if (selectedProcessInfos.length == 0 && DataGridWrap.selectedStepID != "-1")
	{
		selectedItems=DataGridWrap.getSelectedItems(true);
		selectedProcessInfos=getSeletectProcessStepInfos(selectedItems);
	}

	if (selectedProcessInfos.length == 0 || selectedProcessInfos.length > allowMaxSelectNumber)
	{
		showHandleMessage(selectedProcessInfos.length == 0 ? "请首先选择需要发送的案件!" : "一次最多只能选择" + allowMaxSelectNumber + "个案件!", true, true, false); //显示错误消息
		return;
	}
	var firstJson:Object=JSFunUtils.JSFun("WORKFLOW_SPECIAL_SEND_CASE", JSON.stringify(selectedProcessInfos));
	if (firstJson)
	{
		processHttpMessage(firstJson);
		var multiUsers:Array=firstJson.multiStepInfo;
		if (multiUsers) //弹出了选择用户对话框
		{
			WinSelectActivityAndUser.execute(multiUsers, firstJson.processInfos, function(sendResult:Object):void
			{
				if (sendResult != null)
					processHttpMessage(sendResult);
				if (!StringUtils.isEmpty(sendResult.successMsg) || !StringUtils.isEmpty(firstJson.json))
				{
					Pagination.refresh();
					DataGridWrap.restoreSelectedItems(selectedItems);
				}
			});
		}
		else
		{
			//说明直接发送成功
			if (!StringUtils.isEmpty(firstJson.successMsg))
			{
				Pagination.refresh();
				DataGridWrap.restoreSelectedItems(selectedItems);
			}
		}
	}
}

//---------------------------------------------------------------------------------------------
private function getSeletectProcessStepInfo(selectedItem:Object):Object
{
	if (!selectedItem)
		selectedItem=this.DataGridWrap.DataGrid.selectedItem;

	var result:Object={};
	result["step_id"]=selectedItem.__key__;
	if (isSettingAllWFParams)
	{
		result["action_name"]=getItemValueByName(selectedItem, this.actionNameFieldName);
		result["process_type"]=getItemValueByName(selectedItem, this.processTypeFieldName);
		result["process_id"]=getItemValueByName(selectedItem, this.processIdFieldName);
	}
	return result;
} //---------------------------------------------------------------------------------------------

//获取选定的业务步骤信息
private function getSeletectProcessStepInfos(selectedItems:Array):Array
{
	var result:Array=[];
	for (var i:int=0; i < selectedItems.length; i++)
	{
		var item:Object=selectedItems[i];
		var step_id:String=item.__key__;
		if (isSettingAllWFParams)
		{
			var actionName:String=getItemValueByName(item, this.actionNameFieldName);
			var processType:String=getItemValueByName(item, this.processTypeFieldName);
			var processId:String=getItemValueByName(item, this.processIdFieldName);
			var processInfo:Object={step_id: step_id, action_name: actionName, process_type: processType, process_id: processId};
			result.push(processInfo);
		}
		else
		{
			var processInfo:Object={step_id: step_id};
			result.push(processInfo);
		}
	}
	return result;
}

//---------------------------------------------------------------------------------------------
//查看工作流表单信息
private function editForm(stepId:String, isReadOnly:Boolean):void
{
	var item:Object=DataGridWrap.DataGrid.selectedItem;
	var json:Object=JSFunUtils.JSFun("getCaseStepFormInfo", {step_id: stepId, actionName: isSettingAllWFParams ? getItemValueByName(item, this.actionNameFieldName) : ""});
	if (!(json && json.forms))
	{
		if (!json.r && json.msg)
			showHandleMessage(json.msg, true, true, false);
		else
			showHandleMessage("无法获取表单信息!", true, true, false);
		return;
	}

	if (isSettingAllWFParams)
	{
		json.process_id=getItemValueByName(item, this.processIdFieldName);
		json.process_type_id=getItemValueByName(item, this.processTypeFieldName);
		json.activityCaption=getItemValueByName(item, this.actionCaptionFieldName);
		json.formCaption=json.activityCaption + "[" + json.process_id + "]";
		if (json.paramMap)
		{
			for (var k:String in json.paramMap)
			{
				if (json.paramMap[k] == "REQ.WORKFLOW_PROCESS_ID")
					json[k]=json.process_id;
				else if (json.paramMap[k] == "REQ.WORKFLOW_STEP_ID")
					json[k]=item.__key__;
				else
					json[k]=json.paramMap[k];
			}
			json.paramMap=null;
		}
	}
	FlexGlobals.topLevelApplication.editWorkFlowActivityForms(this, this.ui_id, stepId, json, saveCallbackFunction, isReadOnly || _uiDefine.options.isDataReadOnly, _uiDefine.options.isPageControlFormMode, getFormUniqueKey(stepId));
}

//---------------------------------------------------------------------------------------------
private function getItemValueByName(item:Object, name:String):String
{
	var result:String="";
	if (!StringUtils.isEmpty(name))
	{
		var tempV:String=item[name];
		if (tempV.indexOf(">") != -1)
		{
			tempV=StringUtils.between(tempV, ">", "<");
			result=tempV;
		}
		else
		{
			result=tempV;
		}
	}
	return result;
}

//---------------------------------------------------------------------------------------------
//保存后回调函数
private function saveCallbackFunction(result:Object, isAutoSend:Boolean):void
{
	if (result)
	{
		if (isAutoSend)
		{
			//workFunction4Href(RUNTIME_COMMAND.COMMAND_WORKFLOW_SEND_CASE);
		}
		else
		{
			var record:Object=result.records;
			if (record)
			{
				var datagridRecord:Object=DataGridWrap.DataGrid.selectedItem;
				for (var k:String in record)
				{
					if (k != "__key__" && k != "__chk__")
						datagridRecord[k]=record[k];
				}
				DataGridWrap.DataGrid.invalidateDisplayList();
				DataGridWrap.DataGrid.invalidateList();
				DataGridWrap.DataGrid.validateNow();
			}
		}
	}
}

//---------------------------------------------------------------------------------------------
//超连接点击事件
public function event4HrefLinkColumn(item:Object):void
{
	var href:String=item.href;
	if (StringUtils.startWith(href, "FUNGrid_"))
	{
		href=StringUtils.after(href, "FUNGrid_");
		workFunction4Href(href);
	}
}

//---------------------------------------------------------------------------------------------
//新建案件
private function createNewCase(processInfo:Object):void
{
	var v_processTypeId:String=processInfo.id;
	var json:Object=JSFunUtils.JSFun("createWorkFlowProcess", {typeId: v_processTypeId, ui_id: _ui_id});
	if (json && json.r)
	{
		processInfo.process_id=json.process_id;
		selectNewProcessByProcessID(processInfo);
	}
	if (json && json.extMessage)
		showHandleMessage(json.extMessage, false, true, false);
}

//--------------------------------------------------------------------------------------------
private var initHookParams:Object=null;

public function selectNewProcessByProcessID(params:Object):void
{
	if (this.Pagination)
	{
		if (_uiDefine.options.isSortByTimeDESC) //事件倒序显示
			this.Pagination.firstPage();
		else
			this.Pagination.lastPage();

		this.DataGridWrap.DataGrid.validateDisplayList();
		this.DataGridWrap.DataGrid.callLater(function():void
		{
			DataGridWrap.selectRecordByKeyValue(params.process_id);
			workFunction4Href(RUNTIME_COMMAND.COMMAND_WORKFLOW_EDIT_FORM);
		});
		showHandleMessage("新建业务 \"" + params.label + "\" 成功!", false, true, false);
	}
	else
	{
		initHookParams={fun: "selectNewProcessByProcessID", params: params};
	}

}

//--------------------------------------------------------------------------------------------
//废除选定的案件
private function abandonSelectedCase(autoCheckItem:Boolean):void
{
	var selectedItems:Array=DataGridWrap.getSelectedItems(autoCheckItem);
	var selectedProcessInfos:Array=getSeletectProcessStepInfos(selectedItems);
	if (selectedProcessInfos.length == 0 && DataGridWrap.selectedStepID != "-1")
	{
		selectedItems=DataGridWrap.getSelectedItems(true);
		selectedProcessInfos=getSeletectProcessStepInfos(selectedItems);
	}

	if (selectedProcessInfos.length == 0 || selectedProcessInfos.length > allowMaxSelectNumber)
	{
		showHandleMessage(selectedProcessInfos.length == 0 ? "请首先选择需要删除的案件!" : "一次最多只能选择" + allowMaxSelectNumber + "个案件!", true, true, false); //显示错误消息
		return;
	}

	AlertUtils.confirm("您确定要删除选择的业务吗？", function():void
	{
		var json:Object=JSFunUtils.JSFun("WORKFLOW_ABANDON_CASE", JSON.stringify(selectedProcessInfos));
		if (json)
		{
			processHttpMessage(json);
			Pagination.refresh();
			DataGridWrap.restoreSelectedItems(selectedItems);
		}
	});

}

//--------------------------------------------------------------------------------------------
//处理Http服务信息
private function processHttpMessage(json:Object):void
{
	var successStepIds:Array=json.successStepIds;
	if (successStepIds && successStepIds.length != 0)
	{
		for (var i:int=0; i < successStepIds.length; i++)
		{
			var uniqueFormId:String=getFormUniqueKey(successStepIds[i]);
			FlexGlobals.topLevelApplication.closePageControlWorkFlowForms(uniqueFormId);
		}
	}

	if (!StringUtils.isEmpty(json.successMsg))
		showHandleMessage(json.successMsg, false, true, false);

	if (!StringUtils.isEmpty(json.errorMsg))
		showHandleMessage(json.errorMsg, true, true, false);

	if (!StringUtils.isEmpty(json.popupMsg))
		showHandleMessage(json.popupMsg, false, true, false);

	if (!StringUtils.isEmpty(json.alertMsg))
		showHandleMessage(json.alertMsg, false, false, true);
}

//--------------------------------------------------------------------------------------------
//转到页
public function goPage(recordPrePage:int, page:int):void
{
	var filterParams:Object=getFilterParams();
	var datas:Object=JSFunUtils.JSFun("loadWorkFlowProcessList", {ui_id: _ui_id, r: recordPrePage, p: page, filterParams: filterParams});
	if (datas && datas.r)
	{

		this.DataGridWrap.initData(datas.rows);
		//totalPage:int, totalRecord:int, currentPage:int
		this.Pagination.updatePages(datas.t, datas.tr, datas.p);


		lockFilterControlEvent=true;
		var comboboxFilterValues:Object=datas.filterComboboxItems;
		for (var k:String in comboboxFilterValues)
		{
			var combobox:ComboBox=this.paramName2FilterControl[k] as ComboBox;
			if (combobox && combobox.selectedItem)
			{
				var id:String=combobox.selectedItem.ID;
				combobox.dataProvider=new ArrayCollection(comboboxFilterValues[k]);
				var index:int=ArrayCollectionUtils.indexOf(combobox.dataProvider as ArrayCollection, "ID", id);
				combobox.selectedIndex=index == -1 ? 0 : index;
			}
			else
			{
				filterComboboxValues[k]=comboboxFilterValues[k];
			}
			applyComboboxBestWidth(combobox);
		}
		lockFilterControlEvent=false;


	}
}

//--------------------------------------------------------------------------------------------
//获取过滤参数
private function getFilterParams():Object
{
	var result:Object={};
	for (var k:String in this.paramName2FilterControl)
	{
		var control:UIComponent=this.paramName2FilterControl[k];
		var value:String="";
		if (control is TextInput)
			value=StringUtils.trim(TextInput(control).text);
		else if (control is DateField)
			value=DateField(control).text;
		else if (control is ComboBox)
			value=ComboBox(control).selectedItem ? ComboBox(control).selectedItem.ID : null;
		if (!StringUtils.isEmpty(value))
			result[k]=value;
	}
	return result;
}

//--------------------------------------------------------------------------------------------
private function showHandleMessage(content:String, isErrorMsg:Boolean, isShowInMsn:Boolean, isShowInAlert:Boolean):void
{
	if (StringUtils.isEmpty(content))
		return;
	if (isShowInMsn) //显示在提示窗体中
		FlexGlobals.topLevelApplication.popupMessage({time: getCurrentTimeStr(), type: 0, sendName: "工作流系统", content: content, isError: isErrorMsg});
	if (isShowInAlert) //Alert方式显示
		AlertUtils.alert(content);
}

//---------------------------------------------------------------------------------------------
private function getCurrentTimeStr():String
{
	dateFormatter.formatString="MM-DD JJ:NN:SS";
	return dateFormatter.format(new Date());
}

//---------------------------------------------------------------------------------------------
private var dateFormatter:DateFormatter=new DateFormatter();

//---------------------------------------------------------------------------------------------
private var _filterPanelVisible:Boolean=true;

private function showOrHideFilterPanel():void
{
	_filterPanelVisible=!_filterPanelVisible;
	if (_filterPanelVisible && this.ToolbarFilter.owner == null)
		topGroup.addElementAt(this.ToolbarFilter, 1);
	else if (!_filterPanelVisible && this.ToolbarFilter.owner != null)
		topGroup.removeElement(this.ToolbarFilter);
	_toolbarItems[_toolbarItems.length - 1].icon=_filterPanelVisible ? imgArrowUp : imgArrowDown;
	Toolbar.dataProvider=new ArrayCollection(_toolbarItems);
	Toolbar.invalidateDisplayList();
}

////////////////////////////////////////////////////////////////////////////////////////////////
private var fix4OtherPortalCallFunction:Array=[];
private var focusRecordByIDErrorFunction:Function=null;

/////////////////////////////////////////////////////////////////////////////////////////////////
public function focusRecordByID(params:Object):void
{
	focusRecordByIDErrorFunction=params.callFunction;
	if (Pagination && DataGridWrap && DataGridWrap.DataGrid && DataGridWrap.DataGrid.dataProvider)
	{
		if (!Pagination.isFirstPage)
			Pagination.firstPage();
		findRecordByID(params.id);
	}
	else
		callLater(function():void
		{
			focusRecordByID(params);
		});
}

/////////////////////////////////////////////////////////////////////////////////////////////////
public function findRecordByID(id:String):void
{
	var hasFocused:Boolean=DataGridWrap.findRecordByID(id);
	while (!hasFocused)
	{
		if (Pagination.isLastPage)
			break;
		Pagination.nextPage();
		hasFocused=DataGridWrap.findRecordByID(id);
	}
	if (!hasFocused && focusRecordByIDErrorFunction)
	{
		focusRecordByIDErrorFunction();
		focusRecordByIDErrorFunction=null;
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////
private function fix4OtherPortalCall():void
{
}
////////////////////////////////////////////////////////////////////////////////////////////////

