import com.esri.ags.portal.Portal;
import com.estudio.flex.component.IconButton;
import com.estudio.flex.component.IconListButton;
import com.estudio.flex.component.ToolbarVertline;
import com.estudio.flex.module.LayoutClass;
import com.estudio.flex.module.LayoutClassEx;
import com.estudio.flex.module.PortalGridEx;
import com.estudio.flex.module.component.PortalGridExCalendar;
import com.estudio.flex.module.component.PortalGridExControl;
import com.estudio.flex.module.component.PortalGridExDiagram;
import com.estudio.flex.module.component.PortalGridExFileManager;
import com.estudio.flex.module.component.PortalGridExForm;
import com.estudio.flex.module.component.PortalGridExGISMap;
import com.estudio.flex.module.component.PortalGridExGrid;
import com.estudio.flex.module.component.PortalGridExIFrame;
import com.estudio.flex.module.component.PortalGridExPageControl;
import com.estudio.flex.module.component.PortalGridExPictureList;
import com.estudio.flex.module.component.PortalGridExProperty;
import com.estudio.flex.module.component.PortalGridExRichView;
import com.estudio.flex.module.component.PortalGridExSWF;
import com.estudio.flex.module.component.PortalGridExTree;
import com.estudio.flex.module.component.ToolbarGroup;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.StringUtils;
import com.estudio.flex.utils.UIUtils;
import com.utilities.IconUtility;

import flash.events.ContextMenuEvent;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Utils3D;
import flash.ui.ContextMenu;
import flash.ui.ContextMenuItem;

import mx.collections.ArrayCollection;
import mx.controls.ButtonBar;
import mx.controls.LinkBar;
import mx.controls.PopUpMenuButton;
import mx.core.UIComponent;
import mx.events.MenuEvent;

import spark.components.Group;
import spark.components.HGroup;
import spark.components.Label;
import spark.components.NavigatorContent;

private var portalReadOnly:Boolean=false;
private var portalTag:String="";
private var portalGridDefine:Object=null;
private var portalContain:NavigatorContent=null;
private var portalLayout:LayoutClassEx=new LayoutClassEx();
private var portalControlName2ControlInstance:Object={};
private var portalControlList:Array=[];
private var toolbarItemsA:Array=null;
private var toolbarItemsB:Array=null;
private var toolbarItemsC:Array=null;
private var portalControlNames:Array=[];
private var comboboxFilterValues:Object=null;
private var contextMenuItem2Params:Array=[];
////////////////////////////////////////////////////////////////////////////////////
private var portalParams:Object={};

public function setParams(params:Object):void
{
	var oldParams:Object=portalParams;
	portalParams=params ? params : {};
	if (portalGridDefine == null || JSON.stringify(oldParams) == JSON.stringify(portalParams))
		return;
	this.refresh();
}

public function getParams():Object
{
	return portalParams;
}

////////////////////////////////////////////////////////////////////////////////////
//创建界面
public function createUI(gridDefine:Object):UIComponent
{
	portalGridDefine=gridDefine;
	portalLayout.strogeKey="portalex_" + getPortalID();
	portalLayout.version=portalGridDefine.VERSION;
	var layout:Object=portalGridDefine.PORTAL_DEFINE.layout;
	var layoutToolbars:Array=[layout.aToolbar, layout.bToolbar, layout.cToolbar];
	var layoutSplitSizes:Array=[layout.splitL, layout.splitR, layout.splitT, layout.splitB];
	var layoutComponent:UIComponent=portalLayout.createLayout(layout.layerType, layoutToolbars, layoutSplitSizes);
	comboboxFilterValues=gridDefine.COMBOBOX_ITEMS;
	this.addElement(layoutComponent);

	createControls(gridDefine.PORTAL_DEFINE.controls);

	//创建工具条
	var portalControlA:PortalGridExControl=null;
	portalControlA=portalControlName2ControlInstance[layout.aControl];
	if (layout.aControl != "" && portalLayout.getBox(LayoutClassEx.BOX_A) && portalControlA)
	{
		portalLayout.getBox(LayoutClassEx.BOX_A).addElement(portalControlA);
		toolbarItemsA=portalControlA.toolbars;
	}

	var portalControlB:PortalGridExControl=portalControlName2ControlInstance[layout.bControl];
	if (layout.bControl != "" && portalLayout.getBox(LayoutClassEx.BOX_B) && portalControlB)
	{
		portalLayout.getBox(LayoutClassEx.BOX_B).addElement(portalControlB);
		toolbarItemsB=portalControlB.toolbars;
	}

	var portalControlC:PortalGridExControl=portalControlName2ControlInstance[layout.cControl];
	if (layout.cControl != "" && portalLayout.getBox(LayoutClassEx.BOX_C) && portalControlC)
	{
		portalLayout.getBox(LayoutClassEx.BOX_C).addElement(portalControlC);
		toolbarItemsC=portalControlC.toolbars;
	}

	mergeAdditionToolbars();

	if (portalLayout.getBox(LayoutClassEx.TOOLBAR_A))
		createToolbars(portalLayout.getBox(LayoutClassEx.TOOLBAR_A), toolbarItemsA, portalControlA);
	if (portalLayout.getBox(LayoutClassEx.TOOLBAR_B))
		createToolbars(portalLayout.getBox(LayoutClassEx.TOOLBAR_B), toolbarItemsB, portalControlB);
	if (portalLayout.getBox(LayoutClassEx.TOOLBAR_C))
		createToolbars(portalLayout.getBox(LayoutClassEx.TOOLBAR_C), toolbarItemsC, portalControlC);


	//填充数据
	fullDataToControls(null, gridDefine.INIT_DATA);

	//创建脚本运行环境
	createIFrame4JSScript();

	return null;
}

////////////////////////////////////////////////////////////////////////////////////
public function get isCreateCompleted():Boolean
{
	var result:Boolean=true;
	for (var controlName:String in portalControlName2ControlInstance)
	{
		var control:PortalGridExControl=portalControlName2ControlInstance[controlName];
		if (!control.isCreateCompleted)
		{
			result=false;
			break;
		}
	}
	return result;
}

////////////////////////////////////////////////////////////////////////////////////
private function createIFrame4JSScript():void
{
	if (isCreateCompleted)
		IFrameUtils.createIFrameBySrc(getIFrameID(), "../client/portalgridex.jsp?portalId=" + getPortalID() + "&iframeId=" + getIFrameID());
	else
		this.callLater(createIFrame4JSScript);
}

////////////////////////////////////////////////////////////////////////////////////
public function getPortalID():String
{
	return portalGridDefine.PORTAL_ID;
}

////////////////////////////////////////////////////////////////////////////////////
public function set readonly(value:Boolean):void
{
	portalReadOnly=value;
}

////////////////////////////////////////////////////////////////////////////////////
public function get readonly():Boolean
{
	return portalReadOnly;
}

////////////////////////////////////////////////////////////////////////////////////
public function set tabsheet(value:NavigatorContent):void
{
	portalContain=value;
}

////////////////////////////////////////////////////////////////////////////////////
public function get tabsheet():NavigatorContent
{
	return portalContain;
}

////////////////////////////////////////////////////////////////////////////////////
public function getLayout(name:String):Group
{
	return null;
}

////////////////////////////////////////////////////////////////////////////////////
public function refresh():void
{
	for (var i:int=0; i < portalControlList.length; i++)
	{
		var control:PortalGridExControl=portalControlList[i];
		if (control is PortalGridExGrid || control is PortalGridExTree)
		{
			PortalGridExControl(control).refreshData();
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////
public function getIFrameID():String
{
	return "PORTALGRID_" + portalGridDefine.PORTAL_ID;
}

////////////////////////////////////////////////////////////////////////////////////
public function get tag():String
{
	return portalTag;
}

////////////////////////////////////////////////////////////////////////////////////
public function set tag(value:String):void
{
	portalTag=value;
}

/////////////////////////////////////////////////////////////////////////////////////
private function getJavascript():String
{
	return portalGridDefine.PORTAL_DEFINE.js;
}

/////////////////////////////////////////////////////////////////////////////////////
private function createControls(controls:Array):void
{
	var pageControls:Array=[];
	for (var i:int=0; i < controls.length; i++)
	{
		var control:Object=controls[i];
		var controlType:int=control.type;
		var controlName:String=control.name;
		var controlComment:String=control.comment;
		if (controlType == 0) //grid
			portalControlName2ControlInstance[controlName]=createGrid(controlName, controlComment, control);
		else if (controlType == 1)
			portalControlName2ControlInstance[controlName]=createTree(controlName, controlComment, control);
		else if (controlType == 2)
			portalControlName2ControlInstance[controlName]=createForm(controlName, controlComment, control);
		else if (controlType == 3)
			portalControlName2ControlInstance[controlName]=createSWF(controlName, controlComment, control);
		else if (controlType == 4)
			portalControlName2ControlInstance[controlName]=createIFrame(controlName, controlComment, control);
		else if (controlType == 5) //RichView
			portalControlName2ControlInstance[controlName]=createRichView(controlName, controlComment, control);
		else if (controlType == 6) //Diagram
			portalControlName2ControlInstance[controlName]=createDiagram(controlName, controlComment, control);
		else if (controlType == 7) //Calendar
			portalControlName2ControlInstance[controlName]=createCalendar(controlName, controlComment, control);
		else if (controlType == 8) //GISMap
			portalControlName2ControlInstance[controlName]=createGISMap(controlName, controlComment, control);
		else if (controlType == 9) //Property
			portalControlName2ControlInstance[controlName]=createProperty(controlName, controlComment, control);
		else if (controlType == 10) //PictureList
			portalControlName2ControlInstance[controlName]=createPictureList(controlName, controlComment, control);
		else if (controlType == 11) //FileManager
			portalControlName2ControlInstance[controlName]=createFileManager(controlName, controlComment, control);
		else if (controlType == 12) //PageControl
			pageControls.push(control);
		portalControlNames.push(controlName);
		portalControlList.push(portalControlName2ControlInstance[controlName]);
	}

	for (var i:int=0; i < pageControls.length; i++)
	{
		var control:Object=pageControls[i];
		var controlName:String=control.name;
		var controlComment:String=control.comment;
		//FileManager
		portalControlName2ControlInstance[controlName]=createPageControl(controlName, controlComment, control);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createRichView(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExRichView=new PortalGridExRichView();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.portalInstance=this;
	control.initParams(controlParams);
	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createDiagram(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExDiagram=new PortalGridExDiagram();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.portalInstance=this;
	control.initParams(controlParams);
	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createCalendar(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExCalendar=new PortalGridExCalendar();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.portalInstance=this;
	control.initParams(controlParams);
	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createGISMap(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExGISMap=new PortalGridExGISMap();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.portalInstance=this;
	control.initParams(controlParams);
	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createProperty(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExProperty=new PortalGridExProperty();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.portalInstance=this;
	control.initParams(controlParams);
	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createPictureList(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExPictureList=new PortalGridExPictureList();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.portalInstance=this;
	control.initParams(controlParams);
	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createFileManager(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExFileManager=new PortalGridExFileManager();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.portalInstance=this;
	control.initParams(controlParams);

	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createPageControl(controlName:String, controlComment:String, controlParams:Object):UIComponent
{
	var control:PortalGridExPageControl=new PortalGridExPageControl();
	control.controlName=controlName;
	control.controlComment=controlComment;
	control.controlName2Instance=portalControlName2ControlInstance;
	control.portalInstance=this;
	control.initParams(controlParams);
	return control;
}

/////////////////////////////////////////////////////////////////////////////////////////
private function createGrid(controlName:String, controlComment:String, control:Object):UIComponent
{
	var grid:PortalGridExGrid=new PortalGridExGrid();
	grid.controlName=controlName;
	grid.controlComment=controlComment;
	grid.portalId=getPortalID();
	grid.portalInstance=this;
	grid.initParams(control);
	grid.callfun4SelectedChange=event4ControlSelectedChange;
	return grid;
}

////////////////////////////////////////////////////////////////////////////////////////
private function createTree(controlName:String, controlComment:String, control:Object):UIComponent
{
	var tree:PortalGridExTree=new PortalGridExTree();
	tree.controlName=controlName;
	tree.controlComment=controlComment;
	tree.portalInstance=this;
	tree.initParams(control);
	tree.callfun4SelectedChange=event4ControlSelectedChange;
	return tree;
}

////////////////////////////////////////////////////////////////////////////////////////
private function createForm(controlName:String, controlComment:String, control:Object):UIComponent
{
	var form:PortalGridExForm=new PortalGridExForm();
	form.controlName=controlName;
	form.controlComment=controlComment;
	form.portalInstance=this;
	form.initParams(control);
	return form;
}

////////////////////////////////////////////////////////////////////////////////////////
private function createSWF(controlName:String, controlComment:String, control:Object):UIComponent
{
	var swf:PortalGridExSWF=new PortalGridExSWF();
	swf.controlName=controlName;
	swf.controlComment=controlComment;
	swf.portalInstance=this;
	swf.initParams(control);
	return swf;
}

private function createIFrame(controlName:String, controlComment:String, control:Object):UIComponent
{
	var iframe:PortalGridExIFrame=new PortalGridExIFrame();
	iframe.controlName=controlName;
	iframe.controlComment=controlComment;
	iframe.setIFrameId(getIFrameID() + "_" + controlName);
	iframe.initParams(control);
	return iframe;
}


////////////////////////////////////////////////////////////////////////////////////////
private var btnIndex:int=0;
private var toolbarItem2Params:Object={};

private function generalBtnId():String
{
	return "Btn_" + (btnIndex++);
}

////////////////////////////////////////////////////////////////////////////////////////
private function createToolbars(contain:Group, menuItems:Array, control:PortalGridExControl):void
{
	var contextMenu:ContextMenu=new ContextMenu();
	contextMenu.hideBuiltInItems();
	//contextMenu.addEventListener(ContextMenuEvent.MENU_SELECT,event4ContextMenuItemClick);

	var toolbarContain:HGroup=new HGroup();
	toolbarContain.gap=1;
	toolbarContain.verticalAlign="middle";
	toolbarContain.paddingLeft=1;
	UIUtils.fullAlign(toolbarContain);
	contain.addElement(toolbarContain);
	var menuBar:PopUpMenuButton=null;
	var toolbarItem:UIComponent=null;
	for (var i:int=0; i < menuItems.length; i++)
	{
		toolbarItem=null;
		var menuItem:Object=menuItems[i];
		var posIndex:int=Convert.str2int(menuItem.PosIndex, 0) - 1;
		if (menuItem.type == "btn")
		{
			var btn:IconButton=new IconButton();
			btn.label=menuItem["text"];
			btn.toolTip=menuItem["title"];
			btn.iconURL="../images/18x18/" + menuItem["icon"];
			btn.id=generalBtnId();
			btn.enabled=menuItem.supportRight ? !readonly : true;
			toolbarItem2Params[btn.id]=menuItem;
			btn.addEventListener(MouseEvent.CLICK, eventToolbarItemClick);
			toolbarItem=btn;

			var contentMenuItem:ContextMenuItem=new ContextMenuItem(btn.toolTip, false, btn.enabled);
			contextMenu.customItems.push(contentMenuItem);
			contextMenuItem2Params.push({menuItem: contentMenuItem, params: menuItem, control: control});
			contentMenuItem.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, event4ContextMenuItemClick);
		}
		else if (menuItem.type == "btnCombobox")
		{
			var cbBtn:IconListButton=new IconListButton();
			cbBtn.iconURL="../images/18x18/" + menuItem["icon"];
			cbBtn.id=generalBtnId();
			cbBtn.items=menuItem.items;
			cbBtn.enabled=menuItem.supportRight ? !readonly : true;
			toolbarItem2Params[cbBtn.id]=menuItem;
			toolbarItem=cbBtn;
			cbBtn.clickFunction=callFunction4ListButton;

			for (var j:int=0; j < cbBtn.items.length; j++)
			{
				var item:Object=cbBtn.items[j];
				var contentMenuItem:ContextMenuItem=new ContextMenuItem(item.title, false, cbBtn.enabled);
				contextMenu.customItems.push(contentMenuItem);
				contextMenuItem2Params.push({menuItem: contentMenuItem, params: menuItem, index: j, subItem: item, control: control});
				contentMenuItem.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, event4ContextMenuItemClick);
			}
		}
		else if (menuItem.type == "sep")
		{
			var sep:ToolbarVertline=new ToolbarVertline();
			toolbarItem=sep;
		}
		else if (menuItem.type == "subitem")
		{
			menuBar=new PopUpMenuButton();
			menuBar.label=menuItem["title"];
			menuBar.setStyle("fontWeight", "bold");
			menuBar.height=24;
			menuBar.labelField="label";
			menuBar.enabled=menuItem.supportRight ? !readonly : true;
			menuBar.dataProvider=new ArrayCollection(menuItem.items);
			menuBar.id=generalBtnId();
			toolbarItem2Params[menuBar.id]=menuItem;
			menuBar.addEventListener(MouseEvent.CLICK, eventToolbarItemClick);
			menuBar.addEventListener(mx.events.MenuEvent.ITEM_CLICK, eventToolbarItemClick);

			toolbarItem=menuBar;
		}
		else if (menuItem.type == "list")
		{
//            menuBar = new PopUpMenuButton ();
//            menuBar.label = "";
//            menuBar.width = 43;
//            menuBar.height = 24;
//            menuBar.labelField = "title";
//            menuBar.dataProvider = new ArrayCollection (menuItem.items);
//
//            menuBar.addEventListener (mx.events.MenuEvent.ITEM_CLICK , function(event:MenuEvent):void
//            {
//                var bar:PopUpMenuButton = event.currentTarget as PopUpMenuButton;
//                bar.id = event.item.id;
//                bar.setStyle ("icon" , IconUtility.getClass (menuBar , "../images/18x18/" + event.item.icon , 18 , 18));
//                bar.toolTip = event.item.title;
////				if (!(event.item.supportRight && portalGridOptions.readonly))
////					executeByBarItemID (bar.id);
//            });
//
//            menuBar.id = menuItem.items[0].id;
//            menuBar.setStyle ("icon" , IconUtility.getClass (menuBar , "../images/18x18/" + menuItem.items[0].icon , 18 , 18));
//            menuBar.toolTip = menuItem.items[0].title;
//
////			if (menuItem.supportRight)
////				portalGridControls.supportReadonlyBtns.push (menuBar);
//
//            if (posIndex == -1)
//                toolbarContain.addElement (menuBar);
//            else
//                toolbarContain.addElementAt (menuBar , Math.min (posIndex , toolbarContain.numElements));
//
////			portalGridControls.toolbarItemName2Instance[menuBar.id] = menuBar;
		}
		else if (menuItem.type == "label")
		{
			var label:Label=new Label();
			label.text=menuItem.title;
			label.setStyle("fontWeight", "bold");
			label.setStyle("paddingLeft", "1");
			toolbarItem=label;
		}

		if (toolbarItem != null)
		{
			if (posIndex == -1)
				toolbarContain.addElement(toolbarItem);
			else
				toolbarContain.addElementAt(toolbarItem, Math.min(posIndex, toolbarContain.numElements));
		}
	}

	if (control is PortalGridExGrid)
	{
		control.callLater(function():void
		{
			PortalGridExGrid(control).createFilterToolbar(toolbarContain, comboboxFilterValues)
		});
	}
	if (control)
		control.contextMenu=contextMenu;
//	return toolbarContain;
}

////////////////////////////////////////////////////////////////////////////////////////
private function callFunction4ListButton(btn:IconListButton, item:Object):void
{
	var btnId:String=btn.id;
	var btnParams:Object=toolbarItem2Params[btnId];
	btnParams.id=item.type;
	btnParams.index=ArrayUtils.find(btn.items, "type", item.type);
	executeToolbarItemClickFunction(btnParams);
}

////////////////////////////////////////////////////////////////////////////////////////
private function eventToolbarItemClick(event:Event):void
{
	var btnId:String=event.currentTarget.id;
	var btnParams:Object=toolbarItem2Params[btnId];
	if (event is MenuEvent)
	{
		btnParams.index=MenuEvent(event).index;
		event.currentTarget.label=btnParams.items[btnParams.index].label;
	}
	executeToolbarItemClickFunction(btnParams);
}

////////////////////////////////////////////////////////////////////////////////////////
private function event4ContextMenuItemClick(event:Event):void
{
	var index:int=-1;
	for (var i:int=0; i < contextMenuItem2Params.length; i++)
	{
		if (contextMenuItem2Params[i].menuItem == event.currentTarget)
		{
			index=i;
			break;
		}
	}
	if (index == -1)
		return;
	var item:Object=contextMenuItem2Params[index];
	if (item.subItem)
	{
		item.params.id=item.subItem.type;
		item.params.index=item.index;
	}
	executeToolbarItemClickFunction(item.params);

}

////////////////////////////////////////////////////////////////////////////////////////
//创建附加工具条
private function mergeAdditionToolbars():void
{
	if (!toolbarItemsA)
		toolbarItemsA=[];
	if (!toolbarItemsB)
		toolbarItemsB=[];
	if (!toolbarItemsC)
		toolbarItemsC=[];

	var extToolbarItems:Array=portalGridDefine.PORTAL_DEFINE.toolbar.Items;
	for (var i:int=0; i < extToolbarItems.length; i++)
	{
		var toolbars:Array=null;
		var btnItem:Object=extToolbarItems[i];
		if (btnItem.Position == "A区域")
			toolbars=toolbarItemsA;
		else if (btnItem.Position == "B区域")
			toolbars=toolbarItemsB;
		else if (btnItem.Position == "C区域")
			toolbars=toolbarItemsC;

		if (!toolbars)
			continue;
		btnItem.Icon=StringUtils.replace(btnItem.Icon, ".bmp", ".png");
		var PosIndex:int=Convert.str2int(btnItem.PosIndex, 0);
		if (btnItem.Type == "按钮" || btnItem.Type == "标题按钮")
			toolbars.push({PosIndex: PosIndex, type: "btn", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
		else if (btnItem.Type == "分隔条")
			toolbars.push({PosIndex: PosIndex, type: "sep"});
		else if (btnItem.Type == "下拉列表")
			toolbars.push({PosIndex: PosIndex, items: btnItem.Items, type: "subitem", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
		else if (btnItem.Type == "标签")
			toolbars.push({PosIndex: PosIndex, type: "label", title: btnItem.Caption});
	}
}

////////////////////////////////////////////////////////////////////////////////////////
public function getSelectedKey(controlName:String):String
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	return portalControl.getRecordFieldValue("__key__", false, false);
}

////////////////////////////////////////////////////////////////////////////////////////
public function setDiagramActionBackground(controlName:String, action:String, color:uint):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExDiagram(portalControl).setActionBackground(action, color);
}

public function focusDiagramActions(controlName:String, actions:Array):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExDiagram(portalControl).focusActions(actions);
}

public function loadDiagram(controlName:String, diagramName:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExDiagram(portalControl).loadDiagram(diagramName);
}


public function setDiagramActionStep(controlName:String, action:String, step:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExDiagram(portalControl).setActionStep(action, step);
}

public function setDiagramActionSetting(controlName:String, action:String, bg:uint, step:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExDiagram(portalControl).setActionSetting(action, bg, step);
}

public function batchSetDiagramActionSettings(controlName:String, params:Object):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExDiagram(portalControl).batchSetActionSettings(params);
}

public function getDiagramActionSettings(controlName:String):Object
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		return PortalGridExDiagram(portalControl).getActionSettings();
	return {};
}


//分页面板
public function setActivePage(controlName:String, activeControlName:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExPageControl(portalControl).setActivePage(activeControlName);
}



//属性 文件管理器 图片管理器
public function setContent(controlName:String, content:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
	{
		Object(portalControl).setContent(content);
	}
}

public function getContent(controlName:String):String
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		return Object(portalControl).getContent();
	return "";
}

//富文本编辑器
public function setRichViewText(controlName:String, text:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExRichView(portalControl).setText(text);
}

public function getRichViewText(controlName:String):String
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		return PortalGridExRichView(portalControl).getText();
	return "";
}

public function addGeometrys(controlName:String, geometrys:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
		PortalGridExGISMap(portalControl).addGeometrys(geometrys);
}

/////////////////////////////////////////////////////////////////////////////////////////////
public function setJsonControlRecordId(controlName:String, recordId:String):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (!portalControl)
		return;

	if (portalControl is PortalGridExProperty)
		PortalGridExProperty(portalControl).recordId=recordId;
	else if (portalControl is PortalGridExPictureList)
		PortalGridExPictureList(portalControl).recordId=recordId;
	else if (portalControl is PortalGridExFileManager)
		PortalGridExFileManager(portalControl).recordId=recordId;

}

/////////////////////////////////////////////////////////////////////////////////////////////
public function getControlParams(controlName:String):Object
{
	var result:Object={};
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl is PortalGridExGrid)
	{
		var tempParams:Object=PortalGridExGrid(portalControl).getFilterParams();
		for (var k:String in tempParams)
		{
			var value:String=tempParams[k];
			if (StringUtils.contain(k, "$"))
				k=StringUtils.after(k, "$");
			result[k]=value;
		}
	}
	return result;
}

/////////////////////////////////////////////////////////////////////////////////////////////
public function setFormControlParams(controlName:String, params:Object):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (!portalControl || !(portalControl is PortalGridExForm))
		return;
	PortalGridExForm(portalControl).initReqParams(params);

}

/////////////////////////////////////////////////////////////////////////////////////////////
public function setControlFilterParams(controlName:String, params:Object):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl instanceof PortalGridExGrid)
		PortalGridExGrid(portalControl).setFilterControlValues(params);
}

public function setControlReadonly(controlName:String, isReadonly:Boolean):void
{
	var portalControl:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (portalControl)
	{
		portalControl.readonly=isReadonly;
		portalControl.resetCurrentState();
	}

}
/////////////////////////////////////////////////////////////////////////////////////////////
