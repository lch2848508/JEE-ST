import avmplus.getQualifiedClassName;

import com.esri.ags.components.Navigation;
import com.estudio.flex.client.MainForm;
import com.estudio.flex.client.WinFormChangePassword;
import com.estudio.flex.common.FormConst;
import com.estudio.flex.common.InterfaceApplication;
import com.estudio.flex.common.InterfaceFormUI;
import com.estudio.flex.common.InterfacePortalGrid;
import com.estudio.flex.common.InterfacePortalGridEx;
import com.estudio.flex.common.InterfaceQueryGroup;
import com.estudio.flex.common.InterfaceWorkFlowUI;
import com.estudio.flex.component.ErrorLoggerWindow;
import com.estudio.flex.component.FormNavigatorContent;
import com.estudio.flex.component.InputPicture;
import com.estudio.flex.component.MSNPopupWindow;
import com.estudio.flex.component.NavigatorListItemRender;
import com.estudio.flex.ext.WinQuestion;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.DateUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;
import com.estudio.flex.utils.UIUtils;
import com.google.code.flexiframe.IFrame;
import com.utilities.IconUtility;

import ext.intf.IExtensionSWF;

import flash.display.DisplayObject;
import flash.display.StageDisplayState;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.UncaughtErrorEvent;
import flash.text.Font;

import flexlib.controls.tabBarClasses.SuperTab;
import flexlib.events.SuperTabEvent;

import mx.charts.chartClasses.InstanceCache;
import mx.collections.ArrayCollection;
import mx.controls.AdvancedDataGrid;
import mx.controls.Image;
import mx.controls.Menu;
import mx.controls.ProgressBar;
import mx.controls.SWFLoader;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumnGroup;
import mx.core.ClassFactory;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.events.IndexChangedEvent;
import mx.events.MenuEvent;
import mx.events.ModuleEvent;
import mx.managers.PopUpManager;
import mx.managers.SystemManager;
import mx.managers.ToolTipManager;
import mx.messaging.config.ConfigMap;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.CommandMessageExt;
import mx.utils.StringUtil;

import spark.components.Group;
import spark.components.List;
import spark.components.NavigatorContent;
import spark.components.Spinner;
import spark.modules.ModuleLoader;
import spark.skins.spark.SpinnerSkin;

private var _compilerRef:Array=[mx.messaging.config.ConfigMap, mx.messaging.messages.CommandMessage, mx.messaging.messages.CommandMessageExt, mx.messaging.messages.MessagePerformanceInfo, mx.messaging.messages.RemotingMessage, Group, spark.modules.Module, InputPicture, ProgressBar, CheckBox, DataGrid, DataGridColumn, Tree, TextInput, DateField, Button, BorderWrapContain, GroupBox, Pagination, HDividedBox, BorderContainer, TextInputEx, ButtonBar, ComboBox, TextArea, RichTextEditor, PopUpMenuButton, AdvancedDataGrid, AdvancedDataGridColumn, AdvancedDataGridColumnGroup, Spinner, SpinnerSkin];
private var titleHeaderHeight:int=28; //栏目及PageControl头高
private var _indexParams:Object=null; //该变量保存JSP页面中传递过来的所有定制参数
private var _portalsDefine:Object=null;
private var _isShowMyDesktop:Boolean=false;
private var _applicationMap:Object={id2PortalInstance: {}, id2SWFLoad: {}, tabSheet2IconURL: {}, id2TabSheet: {}, tabSheet2Id: {}, tabSheet2SelectedIndex: {}, id2ModuleLoader: {}, tabSheet2Loader: {}, id2Frame: {}};
private var _activePortalGrid:InterfacePortalGrid=null; //当前活动的PortalGrid定义
private var _activePortalGridEx:InterfacePortalGridEx=null; //当前活动的PortalGrid定义
private var _commonTabSheetID:String="Tabsheet_Portal_COMMON";
private var _portalCaption2ItemIndex:Object={};

private var _maxNavigatorItemTitleLength:int=0;
private var _executePortalFunctionParamsHook:Object={};
private var _autorunPortalItems:Array=[]; //自动开始运行
private var _disableClosePortalItems:Array=["Tabsheet_Portal_0"]; //不允许删除

private var _contextPopupMenu:Array=[]; //右键菜单列表
private var _controlUID2ContextPopupMeun:Object={}; //控件同右键菜单的对应关系

public var isRelease:Boolean=false;
public var appVersion:int=0;
public var loginUserId:String=null;
public var loginRealname:String=null;
public var loginDuty:String="";

private var _workflowFormContain:FormNavigatorContent=null; //工作流编辑窗体
private var _workflowFormUniueKey:String=null; //工作流正在编辑窗体的唯一标识号
private var _currentWorkflowUiTabSheet:NavigatorContent=null;
public var cache:Object={};
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public var gobalSetting:Object={ //
		readOnlyColor: 0xF0F0F0 //自读空间的颜色
	}; //

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getWorkAreaHeight():int
{
	return pagecontrolMain.height;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function registerControlContextPopupMenu(controlID:String, contextMenu:Menu):void
{
	this._contextPopupMenu.push(contextMenu);
	this._controlUID2ContextPopupMeun[controlID]=contextMenu;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 公共TabSheet区域
 * @return
 *
 */
private function get CommonTabSheet():NavigatorContent
{

	if (_applicationMap.id2TabSheet[_commonTabSheetID] == null)
	{
		var tabsheet:NavigatorContent=new NavigatorContent();
		tabsheet.uid=_commonTabSheetID;
		pagecontrolMain.addElement(tabsheet);
		pagecontrolMain.selectedIndex=pagecontrolMain.numChildren - 1;
		_applicationMap.id2TabSheet[_commonTabSheetID]=tabsheet;
		_applicationMap.tabSheet2Id[tabsheet.uid]=_commonTabSheetID;
		_applicationMap.tabSheet2SelectedIndex[tabsheet.uid]=-1;
	}
	return _applicationMap.id2TabSheet[_commonTabSheetID] as NavigatorContent;
}


//--------------------------------------------------------------------------------------
private var _applicationCreateCompleted:Boolean=false;

protected function eventApplicationCreateComplete(event:FlexEvent):void
{
	registerCallbackInterface();
	initApplication();
	//WinQuestion.execute();
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * application初始化处理事件 在此事件中系统完成一些动态创建的功能及初始化全局变量
 * @param event事件
 */
[Embed(source="/assets/common/computer.png")] //新建
[Bindable]
public var iconComputer:Class;

private function initApplication():void
{
	var i:int=0;
	_indexParams=JSFunUtils.JSFun("getIndexParams");
	_portalsDefine=_indexParams.PORTAL_ITEMS.item[0].item;
	imgLogoBg.source=_indexParams.LOGOBGURL;
	imgLogoBg.validateNow();
	imgLogo.source=_indexParams.LOGOURL;
	imgLogo.left=_indexParams.LOGOLEFT;
	imgLogo.top=_indexParams.LOGOTOP;
	imgLogo.validateNow();

	_isShowMyDesktop=_indexParams.ISSHOWMYDESKTOP;

	//Logon图标
	isRelease=_indexParams.ISRELEASE;
	appVersion=_indexParams.APPVERSION;
	loginUserId=_indexParams.LOGIN_USERID;
	loginRealname=_indexParams.LOGIN_REALNAME;
	loginDuty=_indexParams.LOGIN_DUTY;


	//创建手拉风琴导航栏
	panelNavigator.title="欢迎您:" + _indexParams["LOGIN_NAME"];
	//panelNavigator.title = DateUtils.getTodayStr();
	if (label_TodayEx)
		label_TodayEx.text="欢迎您：" + _indexParams["LOGIN_NAME"] + " 今天是：" + DateUtils.getTodayStr();


	if (StringUtils.equal("Tree", _indexParams.NAVIGATOR_TYPE))
	{
		this.currentState=treeNavigatorState.name;
		createTreeNavigatorItems(_portalsDefine);
	}
	else if (StringUtils.equal("Accordion", _indexParams.NAVIGATOR_TYPE))
	{
		this.currentState=accordionNavigatorState.name;
		createAccordionItems(_portalsDefine);
	}
	else if (StringUtils.equal("ToolBar", _indexParams.NAVIGATOR_TYPE))
	{
		this.currentState=toolbarNavigatorState.name;
		createToolbarNavigation(_portalsDefine);
	}

	panelNavigator.width=_indexParams["NAVIGATOR_WIDTH"];
	labelCopyright.text=_indexParams.COPYRIGHT;

	//自动运行的栏目
	for (i=0; i < _autorunPortalItems.length; i++)
	{
		var idx:Array=_autorunPortalItems[i];
		selectAccordionItem(idx[0], idx[1]);
	}

	for (i=0; i < pagecontrolMain.numElements; i++)
	{
		var child:Object=pagecontrolMain.getElementAt(i)
		if (_disableClosePortalItems.indexOf(child.id) != -1)
		{
			pagecontrolMain.setClosePolicyForTab(i, SuperTab.CLOSE_NEVER);
		}
	}

	ToolTipManager.showDelay=0;

	//RightClickManager.regist();
	//this.addEventListener(RightClickManager.RIGHT_CLICK, eventApplicationRightClick);
	createEditorContextMenu();

	_applicationCreateCompleted=true;


	//加载扩展插件
	loadExtensionsSWFModule();

	_msnWin.addEventListener(CloseEvent.CLOSE, function(e:Event):void
	{
		_MSN_WINDOW_IS_VISIBLE=false;
		//imgMsnWindowStatus.source=_MSN_WINDOW_IS_VISIBLE ? "../images/flex/show_msn.png" : "../images/flex/hide_msn.png";
		closePopupWindow(_msnWin);
	});

	pagecontrolMain.invalidateDisplayList();
	pagecontrolMain.validateDisplayList();
	pagecontrolMain.validateNow();


	//是否显示我的桌面页
	if (_isShowMyDesktop)
	{
		var tabSheet_Portal_MyDesktop:NavigatorContent=new NavigatorContent();
		tabSheet_Portal_MyDesktop.label="首页";
		tabSheet_Portal_MyDesktop.icon=iconComputer;
		pagecontrolMain.addElementAt(tabSheet_Portal_MyDesktop, 0);
		pagecontrolMain.setClosePolicyForTab(0, SuperTab.CLOSE_NEVER);
		var swfLoader:SWFLoader=new SWFLoader();
		swfLoader.percentHeight=100;
		swfLoader.percentWidth=100;
		swfLoader.load("/gis/index.jpg");
		tabSheet_Portal_MyDesktop.addElement(swfLoader);
	}

	var fontList:Array=Font.enumerateFonts(true);
	for (var i:int=0; i < fontList.length; i++)
		fontList[i]=fontList[i].fontName;
	var isExistsFont:Boolean=ArrayUtils.contain(fontList, "YaHei Consolas Hybrid");
	var isChrome:Boolean=JSFunUtils.JSFun("isChrome", {});
	if (!(isChrome && !isExistsFont))
		ArrayUtils.remove(arrTopMenuItems, ArrayUtils.find(arrTopMenuItems, "type", "0"));

	//JMS服务
	if (!_indexParams.ICQ_ENABLED)
		ArrayUtils.remove(arrTopMenuItems, ArrayUtils.find(arrTopMenuItems, "type", "0"));

	//WEBGIS
	if (!_indexParams.WEBGIS_ENABLED || !_indexParams.IS_GIS_ROLE)
		ArrayUtils.remove(arrTopMenuItems, ArrayUtils.find(arrTopMenuItems, "type", "4"));
	linkbarTopMenuItems.dataProvider=arrTopMenuItems;
}

//--------------------------------------------------------------------------------------
public function eventApplicationRightClick(event:FlexEvent):void
{
	for (var i:int=0; i < _contextPopupMenu.length; i++)
	{
		var menu:Menu=Menu(_contextPopupMenu[i]);
		menu.hide();
	}
	if (event)
	{
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
}

//--------------------------------------------------------------------------------------
private function createTreeNavigatorItems(items:Object):void
{
	var treeData:Array=[];

	var i:int;
	var j:int;
	for (i=0; i < items.length; i++)
	{
		var item:Object=items[i];

		var categoryData:Object={index: i, iconURL: "../images/18x18/" + item["im0"], label: item["text"], children: []};

		_maxNavigatorItemTitleLength=Math.max(item["text"].length, _maxNavigatorItemTitleLength);

		for (j=0; j < item["item"].length; j++)
		{
			var childItem:Object=item["item"][j];
			var listItem:Object={index: j, label: childItem["text"], iconURL: "../images/18x18/" + childItem["im0"], i: i, j: j};
			if (!childItem.ishidden)
			{
				categoryData.children.push(listItem);
				_maxNavigatorItemTitleLength=Math.max(childItem["text"].length, _maxNavigatorItemTitleLength);
			}
			_portalCaption2ItemIndex[childItem["text"]]=[i, j];


			if (childItem.autorun)
				_autorunPortalItems.push([i, j]);
			if (childItem.disableclose)
				_disableClosePortalItems.push("Tabsheet_Portal_" + childItem.id);
		}
		treeData.push(categoryData);
	}

	treeNavigator.dataProvider=new ArrayCollection(treeData);
	treeNavigator.invalidateDisplayList();
	treeNavigator.invalidateList();
	treeNavigator.validateNow();
	for (var k:int=0; k < treeData.length; k++)
	{
		treeNavigator.expandItem(treeData[k], true);
	}

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
[Embed(source="/assets/common/blank.png")]
[Bindable]
private var blankImage:Class;

//创建工具条导航菜单
private function createToolbarNavigation(items:Object):void
{
	toolbarNavigation.dataProvider=null;
	var menuItems:Array=[];
	var i:int;
	var j:int;
	for (i=0; i < items.length; i++)
	{
		var item:Object=items[i];
		var menuItem:Object={};
		menuItem.label=item["text"];
		menuItem.iconUrl="../images/18x18/" + item["im0"];
		menuItem.children=[];
		menuItems.push(menuItem);

		for (j=0; j < item["item"].length; j++)
		{
			var childItem:Object=item["item"][j];
			var listItem:Object={label: childItem["text"], iconUrl: "../images/18x18/" + childItem["im0"], i: i, j: j};
			if (!childItem.ishidden)
			{
				menuItem.children.push(listItem);
			}
			_portalCaption2ItemIndex[childItem["text"]]=[i, j];

			if (childItem.autorun)
				_autorunPortalItems.push([i, j]);
			if (childItem.disableclose)
				_disableClosePortalItems.push("Tabsheet_Portal_" + childItem.id);
		}

	}

	var menuItem:Object={};
	menuItem.label="系统功能";
	menuItem.iconUrl="../images/system.png";
	menuItem.children=[];
	menuItem.children.push({label: "错误日志", iconUrl: "../images/bug.png", i: -1, j: 0});
	menuItem.children.push({label: "更改密码", iconUrl: "../images/changepassword.png", i: -1, j: 1});
	menuItem.children.push({label: "退出系统", iconUrl: "../images/logout.png", i: -1, j: 2});
	menuItems.push(menuItem);

	toolbarNavigation.dataProvider=menuItems;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 创建手拉风琴(Tree)导航栏 本系统只支持2级栏目导航
 * @param items栏目定义
 */
private function createAccordionItems(items:Object):void
{
	var i:int;
	var j:int;
	for (i=0; i < items.length; i++)
	{
		var item:Object=items[i];
		var content:NavigatorContent=new NavigatorContent();
		content.label=item["text"];
		content.percentWidth=100;
		content.percentHeight=100;
		content.icon=IconUtility.getClass(content, "../images/18x18/" + item["im0"], 18, 18);
		accordionNavigator.addElement(content);

		_maxNavigatorItemTitleLength=Math.max(item["text"].length, _maxNavigatorItemTitleLength)

		var listItems:Array=[];
		for (j=0; j < item["item"].length; j++)
		{
			var childItem:Object=item["item"][j];
			var listItem:Object={label: childItem["text"], icon: "../images/18x18/" + childItem["im0"], i: i, j: j};
			if (!childItem.ishidden)
			{
				listItems.push(listItem);
				_maxNavigatorItemTitleLength=Math.max(childItem["text"].length, _maxNavigatorItemTitleLength);
			}
			_portalCaption2ItemIndex[childItem["text"]]=[i, j];
			if (childItem.autorun)
				_autorunPortalItems.push([i, j]);
			if (childItem.disableclose)
				_disableClosePortalItems.push("Tabsheet_Portal_" + childItem.id);
		}

		var listBox:List=new List();
		UIUtils.Position(listBox, 1, 1, 1, 2);
		//listBox.setStyle("useRollOver",false);
		listBox.dataProvider=new ArrayCollection(listItems);
		listBox.setStyle("borderVisible", "false");
		listBox.visible=true;
		//listBox.useHandCursor = true;
		//listBox.mouseChildren = false;
		//listBox.buttonMode = true;
		listBox.itemRenderer=new ClassFactory(NavigatorListItemRender);
		content.addElement(listBox);
		listBox.addEventListener(MouseEvent.CLICK, navigatorItemClick);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 手拉风琴栏目项被选择事件
 * @param event
 */
private function navigatorItemClick(event:MouseEvent):void
{
	var listBox:List=(List)(event.currentTarget);
	var item:Object=listBox.selectedItem;
	if (!item)
		return;
	selectAccordionItem(item.i, item.j);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
protected function toolbarNavigationitemClickHandler(event:MenuEvent):void
{
	var item:Object=event.item;
	if (item.i == -1)
	{
		switch (item.j)
		{
			case 0:
				ErrorLoggerWindow.show();
				break;
			case 1:
				WinFormChangePassword.execute();
				break;
			case 2:
				AlertUtils.confirm("确定要退出系统？", function():void
				{
					JSFunUtils.JSFun("logoff");
				});
				break;
			case 3:
				FlexGlobals.topLevelApplication.stage.displayState=StageDisplayState.FULL_SCREEN
				break;
		}
	}
	else
	{
		selectAccordionItem(item.i, item.j);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 选择导航树中的某一项
 * @param accordionIndex 分组栏目索引
 * @param itemIndex 项索引
 */
private function selectAccordionItem(accordionIndex:int, itemIndex:int, params:Object=null):NavigatorContent
{
	var result:NavigatorContent=null;
	if (accordionIndex == -1 || itemIndex == -1)
		return result;

	var item:Object=_portalsDefine[accordionIndex]["item"][itemIndex];

	var readAble:Boolean=item.read != 0;
	var writeAble:Boolean=item.write != 0;

	var portalWinType:String=item["property"][2];
	var portalType:String=item["property"][0];
	var portalProperty:String=item["property"][1];
	var portalId:String=item["id"];

	if (portalWinType == "2") //在浏览器中新开一个窗口
	{
		openPortalItem(portalType, portalProperty, null, !writeAble, item["text"], portalId, params);
	}
	else if (portalWinType == "3")
	{
		openPortalItem(portalType, portalProperty, null, !writeAble, item["text"], portalId, params);
	}
	else
	{
		//portalWinType 0:单独使用一个TabSheet 1一个组共享一个Tabsheet
		var tabsheetID:String=portalWinType == "0" ? item["id"] : portalWinType == "1" ? _portalsDefine[accordionIndex]["id"] : _commonTabSheetID;
		var tabsheet:NavigatorContent=null;
		var gifURL:String="../images/18x18/" + item["im1"];
		if (_applicationMap.id2TabSheet[tabsheetID] == null)
		{
			tabsheet=new NavigatorContent();
			tabsheet.label=item["text"];
			tabsheet.uid=tabsheetID;
			tabsheet.id="Tabsheet_Portal_" + tabsheetID;
			tabsheet.icon=IconUtility.getClass(tabsheet, gifURL, 18, 18);
			pagecontrolMain.addElement(tabsheet);

			pagecontrolMain.selectedChild=tabsheet;
			_applicationMap.id2TabSheet[tabsheetID]=tabsheet;
			_applicationMap.tabSheet2Id[tabsheet.uid]=tabsheetID;
			_applicationMap.tabSheet2SelectedIndex[tabsheet.uid]=itemIndex;
			_applicationMap.tabSheet2IconURL[tabsheet.uid]=gifURL;
			//打开连接
			openPortalItem(portalType, portalProperty, tabsheet, !writeAble, "", portalId, params);

			if (_disableClosePortalItems.indexOf(tabsheet.id) != -1)
			{
				var index:int=pagecontrolMain.getElementIndex(tabsheet);
				pagecontrolMain.setClosePolicyForTab(index, SuperTab.CLOSE_NEVER);
			}
		}
		else
		{
			tabsheet=(NavigatorContent)(_applicationMap.id2TabSheet[tabsheetID]);
			pagecontrolMain.selectedChild=tabsheet;
			if (_applicationMap.tabSheet2SelectedIndex[tabsheet.uid] != itemIndex)
			{
				//iframe bug fixed
				//bug fixed 4 iframe
				var iframe:IFrame=this._applicationMap.id2Frame[pagecontrolMain.selectedChild["uid"]];
				if (iframe)
				{
					iframe.visible=false;
					iframe.updateFrameVisibility(false);
				}

				tabsheet.icon=null;
				tabsheet.icon=IconUtility.getClass(tabsheet, gifURL, 18, 18);
				tabsheet.label=item["text"];
				_applicationMap.tabSheet2SelectedIndex[tabsheet.uid]=itemIndex
				//打开连接	
				openPortalItem(portalType, portalProperty, tabsheet, !writeAble, "", portalId, params);
			}
			else //刷新
			{
				var m:Object=_applicationMap.tabSheet2Loader[tabsheet.uid];
				if (m is ModuleLoader)
				{
					if (m.child is InterfacePortalGrid)
						InterfacePortalGrid(m.child).refresh();
					else if (m.child is InterfacePortalGridEx)
					{
						InterfacePortalGridEx(m.child).setParams(params);
						InterfacePortalGridEx(m.child).refresh();
					}
				}
			}
		}
		result=tabsheet;
	}
	return result;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function removeMainPageControlPage(tabsheet:NavigatorContent):void
{
	var m:Object=_applicationMap.tabSheet2Loader[tabsheet.uid];
	if (m is ModuleLoader)
	{
		if (m is ModuleLoader)
		{
			if (m.child is InterfacePortalGrid)
				closePortalItemEditForm(m.child as InterfacePortalGrid);
			else if (m.child is InterfacePortalGridEx)
			{
				closePortalItemEditForm(m.child as InterfacePortalGridEx);
				InterfacePortalGridEx(m.child).updateControl(false);
			}
		}
	}

	var iframe:IFrame=this._applicationMap.id2Frame[tabsheet.uid];
	if (iframe)
	{
		iframe.visible=false;
		iframe.updateFrameVisibility(false);
	}

	if (tabsheet != _workflowFormContain)
	{
		tabsheet.removeAllElements();
		if (tabsheet == _currentWorkflowUiTabSheet)
		{
			_currentWorkflowUiTabSheet=null;
			_workflowFormUniueKey=null;
			if (_workflowFormContain.owner != null)
				this.pagecontrolMain.removeElement(_workflowFormContain);
		}
	}
	else
	{
		if (_currentWorkflowUiTabSheet != null)
		{
			callLater(function():void
			{
				pagecontrolMain.selectedChild=_currentWorkflowUiTabSheet;
				_workflowFormUniueKey=null;
				_currentWorkflowUiTabSheet=null;
			});
		}
	}
	var id:String=_applicationMap.tabSheet2Id[tabsheet.uid];
	var portalInstance:Object=_applicationMap.id2PortalInstance[id];
	delete _applicationMap.id2TabSheet[id];
	delete _applicationMap.tabSheet2Id[tabsheet.uid];
	delete _applicationMap.tabSheet2SelectedIndex[tabsheet.uid];
	//private var _applicationMap:Object = {id2PortalInstance: {} , id2SWFLoad: {} , tabSheet2IconURL: {} , id2TabSheet: {} , tabSheet2Id: {} , tabSheet2SelectedIndex: {} , id2ModuleLoader: {} , tabSheet2Loader: {} , id2Frame: {}};

	//触发更改事件\
	pagecontrolMain.callLater(function():void
	{
		pagecontrolMain_changeHandler(null);
	});
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @param event
 */
protected function eventMainPageControlClose(event:SuperTabEvent):void
{
	var index:int=event.tabIndex;
	var control:UIComponent=pagecontrolMain.getChildAt(index) as UIComponent;
	var tabsheet:NavigatorContent=NavigatorContent(control);
	removeMainPageControlPage(tabsheet);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 加载具体的栏目内容
 * @param
 * @return
 */
private function openPortalItem(protalType:String, property:String, tabsheet:NavigatorContent, readOnly:Boolean, portalCaption:String="", portalId:String="", params:Object=null):void
{
	if (tabsheet != null)
	{
		var m:Object=_applicationMap.tabSheet2Loader[tabsheet.uid];
		if (m is ModuleLoader)
		{
			if (m.child is InterfacePortalGrid)
				closePortalItemEditForm(m.child as InterfacePortalGrid);
			else if (m.child is InterfacePortalGridEx)
			{
				closePortalItemEditForm(m.child as InterfacePortalGridEx);
				InterfacePortalGridEx(m.child).updateControl(false);
			}
		}
		tabsheet.removeAllElements();
	}

	if (protalType == "0") //打开数据列表
	{
		openPortalItem_PortalGridDefine(property, readOnly, tabsheet, portalId);
	}
	else if (protalType == "1") //绑定的表单或外部表单
	{
		if (!StringUtils.isEmpty(property))
		{
			openPortalItem_BindForms(property, readOnly, tabsheet, portalCaption, portalId, params);
		}
	}
	else if (protalType == "2") //打开外部URL
	{
		openPortalItem_URL(property, tabsheet, portalId);
	}
	else if (protalType == "3") //工作流对象视图对象
	{
		openPortalItem_WorkFlowUI(property, tabsheet, portalId);
	}
	else if (protalType == "4") //查询组定义
	{
		openPortalItem_QueryGroupUI(property, tabsheet, portalId);
	}
	else if (protalType == "5")
	{
		openPortalItem_PortalGridDefineEx(property, readOnly, tabsheet, portalId, params);
	}

	pagecontrolMain.callLater(function():void
	{
		pagecontrolMain_changeHandler(null);
	});

}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var tabsheet2IFrame:Object={};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function openPortalItem_URL(property:String, tabsheet:NavigatorContent, portalId:String):void
{
	if (StringUtils.isEmpty(property))
		return;

	var url:String=property;
	if (url == "")
		url="about:Tabs";
	if (url.toLowerCase().indexOf("http://") == -1 && url.indexOf("../") == -1)
		url="../" + url;
	url=StringUtils.replace(url, "{USER_ID}", String(JSFunUtils.JSFun("getUserId")));
	if (tabsheet == null)
	{
		flash.net.navigateToURL(new flash.net.URLRequest(url), "_blank");
	}
	else
	{
		if (StringUtils.endWith(url.toLowerCase(), ".swf") || StringUtils.contain(url.toLowerCase(), ".swf?"))
		{
			var swfKey:String=tabsheet.uid + url;
			var swfLoad:SWFLoader=this._applicationMap.id2SWFLoad[swfKey];
			if (!swfLoad)
			{
				swfLoad=new SWFLoader();
				this._applicationMap.id2SWFLoad[swfKey]=swfLoad;
				//事件 解决跨栏目调用第一次的问题
				swfLoad.addEventListener(Event.COMPLETE, function(event:Event):void
				{
					var loaderSWF:SWFLoader=event.target as SWFLoader;
					var obj:SystemManager=loaderSWF.content as SystemManager;
					obj.addEventListener(FlexEvent.APPLICATION_COMPLETE, function(event:Event):void
					{
						var app:Object=event.target.application;
						var callFun:Array=_executePortalFunctionParamsHook[portalId];
						if (callFun && app.hasOwnProperty(callFun[0]))
							app[callFun[0]](callFun[1]);
						_executePortalFunctionParamsHook[portalId]=null;
					});
				});
				swfLoad.load(url);
			}
			this._applicationMap.id2PortalInstance[portalId]=swfLoad;
			swfLoad.visible=true;
			tabsheet.addElement(swfLoad);
			UIUtils.fullAlign(swfLoad);
		}
		else
		{
			var iframe:IFrame=this._applicationMap.id2Frame[tabsheet.uid];
			if (!iframe)
			{
				iframe=new IFrame("flexframe_");
				_applicationMap.id2Frame[tabsheet.uid]=iframe;
				initPredefinedPortalFunction(portalId, iframe.getIFrameID());
			}
			_applicationMap.id2PortalInstance[portalId]=iframe;
			if (iframe.tag != portalId)
			{
				iframe.source=url;
				iframe.tag=portalId;
			}
			UIUtils.fullAlign(iframe);
			tabsheet.addElement(iframe);
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//新建查询组定义
private function openPortalItem_QueryGroupUI(property:String, tabsheet:NavigatorContent, portalId:String):void
{
	var items:Array=[];
	var propertyList:Array=property.split(",");
	var isSameQueryCondition:Boolean=false;
	for (var i:int=0; i < propertyList.length; i++)
	{
		if (propertyList[i] == "-1")
		{
			isSameQueryCondition=true;
			continue;
		}
		var id:String=StringUtils.between(propertyList[i], "[", "]");
		var caption:String=StringUtils.before(propertyList[i], "[");
		items.push({id: id, label: caption});
	}

	var loader:ModuleLoader=_applicationMap.id2ModuleLoader[portalId];
	var intfQueryGroup:InterfaceQueryGroup=null;
	if (!loader)
	{
		loader=new ModuleLoader();
		loader.addEventListener(ModuleEvent.READY, function(event:ModuleEvent):void
		{
			intfQueryGroup=(event.currentTarget as ModuleLoader).child as InterfaceQueryGroup;
			intfQueryGroup.initQueryList(items, isSameQueryCondition);
			//interfaceGrid.TabSheet=tabsheet;
			intfQueryGroup.tag=portalId;
			_applicationMap.id2PortalInstance[portalId]=InterfaceQueryGroup;

		});
		_applicationMap.id2ModuleLoader[portalId]=loader;
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		UIUtils.fullAlign(loader);
		loader.loadModule("./flash/com/estudio/flex/module/QueryGroupUI.swf?version=" + appVersion);
		tabsheet.addElement(loader);
	}
	else
	{
		intfQueryGroup=loader.child as InterfaceQueryGroup;
		//interfaceGrid.TabSheet=tabsheet;
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		tabsheet.addElement(loader);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

private function openPortalItem_WorkFlowUI(property:String, tabsheet:NavigatorContent, portalId:String):void
{
	var workflowID:String=StringUtils.between(property, "<id>", "</id>");

	var loader:ModuleLoader=_applicationMap.id2ModuleLoader[workflowID];
	var intfWorkFlowUI:InterfaceWorkFlowUI=null;
	if (!loader)
	{
		var gridDefine:Object=JSFunUtils.JSFun("getWorkFlowUIDefine", workflowID);
		if (!gridDefine) //无法获取栏目数据
		{
			AlertUtils.alert("无法获取工作流栏目界面配置信息!", AlertUtils.ALERT_STOP);
			return;
		}

		loader=new ModuleLoader();
		loader.addEventListener(ModuleEvent.READY, function(event:ModuleEvent):void
		{
			intfWorkFlowUI=(event.currentTarget as ModuleLoader).child as InterfaceWorkFlowUI;
			intfWorkFlowUI.createUI(gridDefine);
			//interfaceGrid.TabSheet=tabsheet;
			intfWorkFlowUI.tag=portalId;
			intfWorkFlowUI.ui_id=workflowID;

			_applicationMap.id2PortalInstance[portalId]=intfWorkFlowUI;

			if (_executePortalFunctionParamsHook[portalId])
			{
				var funName:String=_executePortalFunctionParamsHook[portalId][0];
				var params:Object=_executePortalFunctionParamsHook[portalId][1];
				var instanceObject:Object=intfWorkFlowUI;
				if (instanceObject.hasOwnProperty(funName))
					instanceObject[funName](params);
				_executePortalFunctionParamsHook[portalId]=null;
			}
		});
		_applicationMap.id2ModuleLoader[workflowID]=loader;
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		UIUtils.fullAlign(loader);
		loader.loadModule("./flash/com/estudio/flex/module/WorkFlowUI.swf?version=" + appVersion);
		tabsheet.addElement(loader);
	}
	else
	{
		intfWorkFlowUI=loader.child as InterfaceWorkFlowUI;
		//interfaceGrid.TabSheet=tabsheet;
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		tabsheet.addElement(loader);
		intfWorkFlowUI.refresh();
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function openPortalItem_PortalGridDefine(property:String, readOnly:Boolean, tabsheet:NavigatorContent, portalId:String):void
{
	var loader:ModuleLoader=_applicationMap.id2ModuleLoader[property];
	var interfaceGrid:InterfacePortalGrid=null;
	if (!loader)
	{
		var gridDefine:Object=JSFunUtils.JSFun("getGridProtalDefine", property);
		if (!gridDefine) //无法获取栏目数据
		{
			AlertUtils.alert("无法获取栏目数据,请与管理人员联系!", AlertUtils.ALERT_STOP);
			return;
		}

		loader=new ModuleLoader();
		loader.addEventListener(ModuleEvent.READY, function(event:ModuleEvent):void
		{
			interfaceGrid=(event.currentTarget as ModuleLoader).child as InterfacePortalGrid;
			interfaceGrid.createUI(gridDefine);
			interfaceGrid.readonly=readOnly;
			interfaceGrid.TabSheet=tabsheet;
			interfaceGrid.tag=portalId;
			_applicationMap.id2PortalInstance[portalId]=interfaceGrid;
			initPredefinedPortalFunction(portalId, interfaceGrid.getIFrameID());
		});
		_applicationMap.id2ModuleLoader[property]=loader;
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		UIUtils.fullAlign(loader);
		loader.loadModule("./flash/com/estudio/flex/module/PortalGrid.swf?version=" + appVersion);
		tabsheet.addElement(loader);
	}
	else
	{
		interfaceGrid=loader.child as InterfacePortalGrid;
		interfaceGrid.TabSheet=tabsheet;
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		tabsheet.addElement(loader);
		if (!interfaceGrid.isCommonSearch)
			interfaceGrid.refresh();
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function openPortalItem_PortalGridDefineEx(property:String, readOnly:Boolean, tabsheet:NavigatorContent, portalId:String, params:Object=null):void
{
	var loader:ModuleLoader=_applicationMap.id2ModuleLoader[property];
	var interfaceGrid:InterfacePortalGridEx=null;
	if (!loader)
	{
		var gridDefine:Object=JSFunUtils.JSFun("getGridProtalDefineEx", property, params ? params : {});
		if (!gridDefine) //无法获取栏目数据
		{
			AlertUtils.alert("无法获取栏目数据,请与管理人员联系!", AlertUtils.ALERT_STOP);
			return;
		}

		loader=new ModuleLoader();
		loader.addEventListener(ModuleEvent.READY, function(event:ModuleEvent):void
		{
			interfaceGrid=(event.currentTarget as ModuleLoader).child as InterfacePortalGridEx;
			interfaceGrid.readonly=readOnly;
			interfaceGrid.setParams(params);
			interfaceGrid.createUI(gridDefine);
			interfaceGrid.tabsheet=tabsheet;
			interfaceGrid.tag=portalId;
			_applicationMap.id2PortalInstance[portalId]=interfaceGrid;
			initPredefinedPortalFunction(portalId, interfaceGrid.getIFrameID());

		});
		_applicationMap.id2ModuleLoader[property]=loader;
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		UIUtils.fullAlign(loader);
		loader.loadModule("./flash/com/estudio/flex/module/PortalGridEx.swf?version=" + appVersion);
		tabsheet.addElement(loader);
	}
	else
	{
		interfaceGrid=loader.child as InterfacePortalGridEx;
		interfaceGrid.tabsheet=tabsheet;
		interfaceGrid.setParams(params);
		_applicationMap.tabSheet2Loader[tabsheet.uid]=loader;
		tabsheet.addElement(loader);
		interfaceGrid.refresh();
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function openPortalItem_BindForms(property:String, readonly:Boolean, tabsheet:NavigatorContent, portalCaption:String, portalId:String, extParams:Object):void
{
	var formids:Array=[];
	var reqParams:Object={formParams: {}, uiParams: {purposeType: FormConst.PURPOSETYPE_PORTALITEM}}; //用途

	var formProperty:Object=Convert.xmlStr2Object(property);
	if (!formProperty.BindForms)
		return;

	var forms:Array=formProperty.BindForms[0].Form;
	for (var i:int=0; i < forms.length; i++)
	{
		var form:Object=forms[i];
		formids.push(form.ID);
		var params:Array=form.Params[0].Param;
		for (var j:int=0; j < params.length; j++)
		{
			var param:Object=params[j];
			var paramName:String=param.Name;
			var paramValue:String=StringUtils.between(param.Value, "[", "]");
			if (extParams && extParams.hasOwnProperty(paramName))
				reqParams.formParams[paramName]=extParams[paramName];
			else
				reqParams.formParams[paramName]=JSFunUtils.JSFun("getEnvironmentValue", paramValue, paramValue);
		}
	}

	reqParams.uiParams.showToolbar=Convert.object2Boolean(Convert.str2int(formProperty.BindForms[0].ShowToolbar, 0) == 1, true);
	reqParams.uiParams.formids=formids;
	reqParams.uiParams.prefix="portal_form_";
	reqParams.uiParams.showType=(tabsheet == null) ? FormConst.SHOWTYPE_MODALDIALOG : FormConst.SHOWTYPE_NAVIGATION;

	getAndProcessFormUI(reqParams, readonly, function(module:UIComponent, form:InterfaceFormUI, isFirst:Boolean):void
	{
		if (tabsheet != null)
		{
			if (isFirst)
			{
				form.tag=portalId;
				_applicationMap.id2PortalInstance[portalId]=form;
				initPredefinedPortalFunction(portalId, form.getIFrameID());
			}
			UIUtils.fullAlign(module);
			tabsheet.addElement(module);
		}
		else
		{
			var formSize:Object=getFormSize(formids);
			initPredefinedPortalFunction(portalId, form.getIFrameID());
			modalDialog(module, formSize.w, formSize.h, portalCaption, null, null, null);
		}
	});
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 主界面中工作区PageControl中当前TabSheet发生变化时事件处理函数
 * @param event
 */
private function pagecontrolMain_changeHandler(event:IndexChangedEvent):void
{
	if (event != null && event.oldIndex != event.newIndex && event.oldIndex != -1)
	{
		var oldChild:NavigatorContent=pagecontrolMain.getElementAt(event.oldIndex) as NavigatorContent;
		var oldIFrame:IFrame=this._applicationMap.id2Frame[oldChild.uid];
		if (oldIFrame && oldIFrame.owner == oldChild)
		{
			oldIFrame.visible=false;
			oldIFrame.updateFrameVisibility(false);
		}

//		if (_applicationMap.tabSheet2Loader[oldChild.uid] is ModuleLoader)
//		{
//			var oldIntf:Object=_applicationMap.tabSheet2Loader[oldChild.uid].child;
//			if (oldIntf is InterfacePortalGrid && )
//				closePortalItemEditForm(oldIntf as InterfacePortalGrid);
//			else if (oldIntf is InterfacePortalGridEx)
//			{
//				closePortalItemEditForm(oldIntf as InterfacePortalGridEx);
//				InterfacePortalGridEx(oldIntf).updateControl(false);
//			}
//		}
	}

	_activePortalGrid=null;
	_activePortalGridEx=null;
	var child:NavigatorContent=pagecontrolMain.selectedChild as NavigatorContent;
	if (child)
	{
		if (_applicationMap.tabSheet2Loader[child.uid] is ModuleLoader)
		{
			var intf:Object=_applicationMap.tabSheet2Loader[child.uid].child;
			if (intf is InterfacePortalGrid)
				_activePortalGrid=intf as InterfacePortalGrid;
			else if (intf is InterfacePortalGridEx)
			{
				_activePortalGridEx=intf as InterfacePortalGridEx;
				_activePortalGridEx.updateControl(true);
			}
		}
		else
		{
			var iframe:IFrame=this._applicationMap.id2Frame[child.uid];
			if (iframe && iframe.owner == child /*child.child.getElementIndex(iframe) != -1*/)
			{
				iframe.visible=true;
				iframe.updateFrameVisibility(true);
			}
		}
	}




}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//实现 InterfaceApplication 接口函数
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var _waitingTimes:int=0;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * @see com.estudio.flex.common.InterfaceApplication.waiting
 */
public function waiting():void
{
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * @see com.estudio.flex.common.InterfaceApplication.endwaiting
 */
public function endwaiting():void
{
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function gotoPortal(portalCaption:String, newCaption:String="", params:Object=null):void
{
	portalCaption=ObjectUtils.unescape4flex(portalCaption) as String;
	newCaption=ObjectUtils.unescape4flex(newCaption) as String;
	var item:Array=_portalCaption2ItemIndex[portalCaption];
	if (item)
	{
		var tabsheet:NavigatorContent=selectAccordionItem(item[0], item[1], params) as NavigatorContent;
		if (tabsheet && !StringUtils.isEmpty(newCaption))
		{
			tabsheet.label=newCaption;
			pagecontrolMain.invalidateDisplayList();
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//关闭栏目
public function closePortalFunction(portalCaption:String):void
{
	portalCaption=ObjectUtils.unescape4flex(portalCaption) as String;
	var item:Array=_portalCaption2ItemIndex[portalCaption];
	if (!item)
		return;
	var portalParams:Object=_portalsDefine[item[0]]["item"][item[1]];
	var portalId:String=portalParams.id;
	var portalInstance:Object=_applicationMap.id2PortalInstance[portalId];
	if (!portalInstance)
		return;

	for (var i:int=pagecontrolMain.numElements - 1; i >= 0; i--)
	{
		var tabsheet:NavigatorContent=pagecontrolMain.getElementAt(i) as NavigatorContent;
		if (portalId == _applicationMap.tabSheet2Id[tabsheet.uid])
		{
			removeMainPageControlPage(tabsheet);
			pagecontrolMain.removeElement(tabsheet);
			break;
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function executePortalFunction(portalCaption:String, funName:String, params:Object, newCaption:String=null):void
{
	newCaption=ObjectUtils.unescape4flex(newCaption) as String;
	portalCaption=ObjectUtils.unescape4flex(portalCaption) as String;
	params=ObjectUtils.unescape4flex(params);
	var item:Array=_portalCaption2ItemIndex[portalCaption];
	if (!item)
		return;
	var portalParams:Object=_portalsDefine[item[0]]["item"][item[1]];
	var portalId:String=portalParams.id;

	var tabsheet:NavigatorContent=null;
	var portalInstance:Object=_applicationMap.id2PortalInstance[portalId];
	if (portalInstance != null && portalInstance instanceof SWFLoader)
	{
		var swf:SWFLoader=portalInstance as SWFLoader;
		portalInstance=(swf.content as SystemManager).application;
	}

	var isPortalFunctionExists:Boolean=portalInstance && portalInstance.hasOwnProperty(funName);
	var isIFrameExists:Boolean=false;
	if (portalInstance && portalInstance.hasOwnProperty("getIFrameID"))
		isIFrameExists=JSFunUtils.JSFun("isIFrameExists", portalInstance.getIFrameID());

	if (portalInstance) //栏目已经创建
	{
		if (isPortalFunctionExists) //处理类似WorkFlow类的栏目
		{
			tabsheet=selectAccordionItem(item[0], item[1]);
			portalInstance[funName](params);
		}
		else if (isIFrameExists && portalId == portalInstance.tag) //处理IFrame类的栏目
		{
			tabsheet=selectAccordionItem(item[0], item[1]);
			JSFunUtils.JSFun("executeFrameFunction", portalInstance.getIFrameID(), funName, params);
		}
		else if (portalInstance.hasOwnProperty("getIFrameID"))
		{
			_executePortalFunctionParamsHook[portalId]=[funName, params];
			initPredefinedPortalFunction(portalId, portalInstance.getIFrameID());
			tabsheet=selectAccordionItem(item[0], item[1]);
		}
	}
	else
	{
		_executePortalFunctionParamsHook[portalId]=[funName, params];
		tabsheet=selectAccordionItem(item[0], item[1]);
	}

	if (tabsheet && !StringUtils.isEmpty(newCaption))
	{
		tabsheet.label=newCaption;
		pagecontrolMain.invalidateDisplayList();
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function initPredefinedPortalFunction(portalId:String, iframeId:String):void
{
	if (_executePortalFunctionParamsHook[portalId])
		JSFunUtils.JSFun("registerCallPortalFunctionParams", iframeId, _executePortalFunctionParamsHook[portalId][0], _executePortalFunctionParamsHook[portalId][1]);
	_executePortalFunctionParamsHook[portalId]=null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//根据Portal名字获取对应的工作流UI信息ID
public function getWorkFlowUiIdByPortalName(portalName:String):String
{
	portalName=ObjectUtils.unescape4flex(portalName) as String;
	var item:Array=_portalCaption2ItemIndex[portalName];
	var params:String=_portalsDefine[item[0]]["item"][item[1]].property[1];
	return StringUtils.between(params, "<id>", "</id>");
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
