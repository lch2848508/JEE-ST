import com.estudio.flex.common.InterfacePortalGrid;
import com.estudio.flex.component.IconButton;
import com.estudio.flex.component.Pagination;
import com.estudio.flex.component.ToolbarVertline;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxHeaderRender;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxItemRender;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxRender;
import com.estudio.flex.component.mx.datagrid.render.CommonHeaderRender;
import com.estudio.flex.component.mx.datagrid.render.CommonItemRender;
import com.estudio.flex.component.mx.datagrid.render.HrefRender4PortalGrid;
import com.estudio.flex.component.mx.treeview.render.IconItemRender;
import com.estudio.flex.module.PortalGrid;
import com.estudio.flex.utils.AjaxUtils;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayCollectionUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;
import com.estudio.flex.utils.UIUtils;
import com.google.code.flexiframe.IFrame;
import com.utilities.IconUtility;

import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.geom.Point;
import flash.ui.Keyboard;
import flash.ui.Mouse;
import flash.ui.MouseCursor;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.collections.ArrayList;
import mx.collections.IList;
import mx.controls.Alert;
import mx.controls.DataGrid;
import mx.controls.DateField;
import mx.controls.LinkBar;
import mx.controls.LinkButton;
import mx.controls.Menu;
import mx.controls.PopUpMenuButton;
import mx.controls.Text;
import mx.controls.Tree;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.controls.dataGridClasses.FTEDataGridItemRenderer;
import mx.controls.dataGridClasses.MXDataGridItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListBase;
import mx.core.Application;
import mx.core.ClassFactory;
import mx.core.FlexGlobals;
import mx.core.ScrollPolicy;
import mx.core.UIComponent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
import mx.events.ListEvent;
import mx.events.MenuEvent;
import mx.events.ScrollEvent;
import mx.events.StateChangeEvent;

import spark.components.Button;
import spark.components.CheckBox;
import spark.components.ComboBox;
import spark.components.Group;
import spark.components.HGroup;
import spark.components.Label;
import spark.components.TextInput;
import spark.events.IndexChangeEvent;
import spark.skins.spark.ImageSkin;

private function executeByBarItemID(id:String, extParams:Object=null):void
{
	switch (id)
	{
		case "folder_new_sibling":
		case "folder_new_child":
			funTreeNew(id == "folder_new_sibling");
			break;
		case "folder_delete":
			funTreeDelete();
			break;
		case "folder_edit":
			funTreeEdit();
			break;
		case "folder_moveup":
		case "folder_movedown":
			funTreeExchange(id == "folder_moveup");
			break;
		case "folder_refresh":
			funTreeRefresh();
			break;
		case "folder_filter":
			funTreeSearch();
			break;
		case "grid_new":
			funGridNew();
			break;
		case "grid_edit":
			funGridEdit();
			break;
		case "grid_delete":
			funGridDelete(true);
			break;
		case "grid_moveup":
		case "grid_movedown":
			funGridExchange(id == "grid_moveup");
			break;
		default:
			if (_isHasJSScript)
				IFrameUtils.execute("PORTALGRID_" + portalGridOptions.portalID, id, extParams);
			break;
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//选择的树节点发生变化时执行此事件
private function eventTreeOnSelectChange(event:Object):void
{
	var skipTreeOnSelectChange:Boolean=false;
	if (portalGridOptions.isTreeSupportCheckBox && portalGridControls.tree.selectedItem)
	{
		if (portalGridOptions.isTreeViewAsGrid)
		{
			var onlySelected:Boolean=!(event && (event.itemRenderer is CheckBoxItemRender) && CheckBoxItemRender(event.itemRenderer).isMouseOnCheckBox(event.itemRenderer.mouseX, event.itemRenderer.mouseY));
			if (onlySelected)
			{
				treeOnlyCheckSelectedItem(portalGridDatas.treeData.source, portalGridControls.tree.selectedItem);
				portalGridControls.tree.selectedItem.selected=true;
				portalGridDatas.SELECTED_TREE_ID=null;
			}
			else
			{
				portalGridControls.tree.selectedItem.selected=portalGridControls.tree.selectedItem.selected == 1 ? 0 : 1;
				skipTreeOnSelectChange=true;
			}
		}
		else
		{
			treeOnlyCheckSelectedItem(portalGridDatas.treeData.source, portalGridControls.tree.selectedItem);
			portalGridControls.tree.selectedItem.selected=true;
		}
		portalGridControls.tree.invalidateList();
	}

	//需要动态加载数据
	if (portalGridOptions.isAsyncTreeData && portalGridControls.tree.selectedItem)
	{
		if (!portalGridControls.tree.selectedItem.children && !portalGridControls.tree.selectedItem.__loaded__)
		{
			var params:Object={p_id: portalGridControls.tree.selectedItem.id, portal_id: getPortalID()};
			var dynamicTreeData:Object=JSFunUtils.JSFun("dynamicLoadTreeData", params);
			if (dynamicTreeData && dynamicTreeData.records && dynamicTreeData.records.length != 0 && dynamicTreeData.records[0].children && dynamicTreeData.records[0].children.length != 0)
			{
				portalGridControls.tree.selectedItem.children=dynamicTreeData.records[0].children;
				portalGridControls.tree.invalidateList();
			}
			portalGridControls.tree.selectedItem.__loaded__=true;
		}
	}

	var data:Object=portalGridControls.tree.selectedItem;
	if (!data)
	{
		portalGridDatas.SELECTED_TREE_ID=null;
		if (portalGridControls.grid)
			fillGridByData({t: 0, tr: 0, p: 0, rows: []});
	}
	else if (data["id"] != portalGridDatas.SELECTED_TREE_ID)
	{
		portalGridDatas.SELECTED_TREE_ID=data["id"];
		if (portalGridControls.grid && !skipTreeOnSelectChange)
			goPage(portalGridControls.pagination == null ? 2500 : portalGridControls.pagination.recordPrePage, 1);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//显示列表项详细信息
public function eventDetailInfoGrid():void
{
	if (portalGridControls.grid.selectedItem != null)
		FlexGlobals.topLevelApplication.editPortalItem(this, false, false, readonly);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//操链接响应事件
public function event4HrefLinkColumn(hrefItem:Object):void
{
	eventLinkHandle(hrefItem.href, hrefItem.params);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Grid MosueDown 事件
private function event4GridMouseDown(event:MouseEvent):void
{
	//event4GridMouseDown
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Grid Mouse UP 事件
private function event4GridMouseUp(event:MouseEvent):void
{
	//event4GridMouseDown
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//工具条按钮鼠标点击事件
private function eventToolbarItemClick(event:MouseEvent):void
{
	if (event.currentTarget.enabled)
		executeByBarItemID(event.currentTarget["id"]);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Grid选择项发生变化
private function hookGridSelectedChange(event:ListEvent):void
{
	if (_isHasJSScript)
		IFrameUtils.execute("PORTALGRID_" + portalGridOptions.portalID, "EVENT_AFTER_GRID_SELECTED", portalGridControls.grid.selectedItem);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Tree选择项发生变化
private function hookTreeSelectedChange(event:ListEvent):void
{
	if (_isHasJSScript)
		IFrameUtils.execute("PORTALGRID_" + portalGridOptions.portalID, "EVENT_AFTER_TREE_SELECTED", getTreeSelectedItems()[0]);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Grid对应的超链接
public function eventLinkHandle(funName:String, params:String=""):void
{
	if (StringUtils.startWith(funName, "FUNGrid_"))
	{
		switch (StringUtils.after(funName, "FUNGrid_"))
		{
			case "Edit":
				funGridEdit();
				break;
			case "DetailInfo":
				eventDetailInfoGrid();
				break;
			case "Delete":
				funGridDelete(false);
				break;
			case "MoveUp":
				funGridExchange(true);
				break;
			case "MoveDown":
				funGridExchange(false);
				break;
		}
	}
	else
	{
		if (_isHasJSScript)
			IFrameUtils.execute("PORTALGRID_" + portalGridOptions.portalID, funName, params);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//选择的树节点发生变化时执行此事件
private function eventTreeCheckBoxOnClick(selectedItem:Object):void
{
	var data:Object=portalGridControls.tree.selectedItem;
	if (data["id"] != portalGridDatas.SELECTED_TREE_ID)
		portalGridDatas.SELECTED_TREE_ID=data["id"];
	if (portalGridControls.grid)
		goPage(portalGridControls.pagination == null ? 2500 : portalGridControls.pagination.recordPrePage, 1);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//过滤器文本输入框回车事件 过滤数据
private function eventFilterInputKeyPress(event:KeyboardEvent):void
{
	if (event == null || event.keyCode == Keyboard.ENTER)
	{
		if (portalGridControls.pagination != null)
			goPage(portalGridControls.pagination.recordPrePage, portalGridControls.pagination.currentPage);
		else
			goPage(2500, 1);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
private function isControlOwnerInclude(c:Object, controls:Array):Boolean
{
	var result:Boolean=false;
	while (c)
	{
		if (controls.indexOf(c) != -1)
		{
			result=true;
			break;
		}
		c=UIComponent(c.owner);
	}
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventTreeOrGridRightClick(event:FlexEvent):void
{
	FlexGlobals.topLevelApplication.eventApplicationRightClick(null);

	if (FlexGlobals.topLevelApplication.existsModalForm())
	{
		FlexGlobals.topLevelApplication.broadcastRightClickEvent();
		return;
	}
	//if (!isControlOwnerInclude(event.target, [portalGridControls.tree, portalGridControls.grid]))
	//	return;
	var render:Object=getCurrentRender(event.target);
	if (event.currentTarget == portalGridControls.tree)
	{
		if (render && render.data && render.data != portalGridControls.tree.selectedItem)
		{
			portalGridControls.tree.selectedItem=render.data;
			eventTreeOnSelectChange(null);
		}
		showPopupMenu(portalGridControls.treeMenu);
	}
	else if (event.currentTarget == portalGridControls.grid)
	{
		if (render && render.data && render.data != portalGridControls.grid.selectedItem)
		{
			portalGridControls.grid.selectedItem=render.data;
			hookGridSelectedChange(null);
		}
		showPopupMenu(portalGridControls.gridMenu);
	}
	event.stopPropagation();
	event.stopImmediatePropagation();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventMenuItemClick(event:MenuEvent):void
{
	var tag:String=event.item.tag;
	if (!StringUtils.isEmpty(tag))
	{
		if (ArrayUtils.indexOf(portalGridControls.supportReadonlyTags, tag) != -1 && this.readonly)
		{
			AlertUtils.alert("当前模块为只读，不能执行此项操作!");
		}
		else
		{
			executeByBarItemID(tag);
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventContextMenuItemClick(event:FlexEvent):void
{
	/*
	portalGridControls
	contextMenuItem2Tag:[], allContextMenuItem: []
	*/
	for (var i:int=0; i < portalGridControls.allContextMenuItem.length; i++)
	{
		if (portalGridControls.allContextMenuItem[i] == event.currentTarget)
		{
			var tag:String=portalGridControls.contextMenuItem2Tag[i];
			if (ArrayUtils.indexOf(portalGridControls.supportReadonlyTags, tag) != -1 && this.readonly)
			{
				AlertUtils.alert("当前模块为只读，不能执行此项操作!");
			}
			else
			{
				executeByBarItemID(tag);
			}
			break;
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
private function showPopupMenu(menu:Menu):void
{
	var topFlex:UIComponent=FlexGlobals.topLevelApplication as UIComponent;
	var p:Point=new Point(topFlex.mouseX, topFlex.mouseY);
	p.x=Math.min(topFlex.width - menu.width, p.x);
	p.y=Math.min(topFlex.height - menu.height, p.y);
	menu.hide();
	menu.show(p.x, p.y);
}
