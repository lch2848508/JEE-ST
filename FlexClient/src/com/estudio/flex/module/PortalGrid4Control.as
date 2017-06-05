import com.estudio.flex.common.InterfacePortalGrid;
import com.estudio.flex.component.CustomMenuItemRenderer;
import com.estudio.flex.component.DataGridEx;
import com.estudio.flex.component.FilterPanel;
import com.estudio.flex.component.IconButton;
import com.estudio.flex.component.IconMenuItemRenderer;
import com.estudio.flex.component.Pagination;
import com.estudio.flex.component.ToolbarVertline;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxHeaderRender;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxItemRender;
import com.estudio.flex.component.mx.datagrid.render.CheckBoxRender;
import com.estudio.flex.component.mx.datagrid.render.CommonHeaderRender;
import com.estudio.flex.component.mx.datagrid.render.CommonItemRender;
import com.estudio.flex.component.mx.datagrid.render.HrefRender4PortalGrid;
import com.estudio.flex.component.mx.datagrid.render.IconColumnRender4DynamicUI;
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

import flash.display.DisplayObject;
import flash.events.ContextMenuEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.ui.ContextMenuItem;

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.collections.ArrayList;
import mx.collections.IList;
import mx.controls.Alert;
import mx.controls.DataGrid;
import mx.controls.DateField;
import mx.controls.Menu;
import mx.controls.PopUpMenuButton;
import mx.controls.Text;
import mx.controls.Tree;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.controls.dataGridClasses.MXDataGridItemRenderer;
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
import spark.components.Group;
import spark.components.HGroup;
import spark.components.Label;
import spark.components.TextInput;
import spark.components.VGroup;
import spark.events.IndexChangeEvent;


public function createUI(define:Object):UIComponent
{
	portalGridControls.treeContextMenu.hideBuiltInItems();
	portalGridControls.gridContextMenu.hideBuiltInItems();

	_GLOBAL_DEFINE=define;
	_PORTAL_DEFINE=define["PORTAL_DEFINE"];
	portalGridOptions.portalID=_GLOBAL_DEFINE["PORTAL_ID"];
	_isHasJSScript=!StringUtils.isEmpty(StringUtils.trim(_GLOBAL_DEFINE.JS));
	portalGridOptions.isTreeViewAsGrid=_PORTAL_DEFINE["Layout"]["TreeView"] && _PORTAL_DEFINE["Layout"]["TreeSingleLevel"] && _PORTAL_DEFINE["Layout"]["TreeViewAsGrid"];
	portalGridOptions.isTreeSupportCheckBox=_PORTAL_DEFINE["Layout"]["TreeSupportCheckBox"];
	portalGridOptions.isCommonSearch=_PORTAL_DEFINE.IsCommonSearch;
	portalGridOptions.isAsyncTreeData=_PORTAL_DEFINE["Layout"].AsyncTreeData;

	var w1:int=Convert.str2int(getPortalResourceDefine("LAYOUT_TREE_SIZE", "Text"), 250);
	var w2:int=Convert.str2int(getPortalResourceDefine("LAYOUT_DETAIL_SIZE", "Text"), 250);

	portalGridControls.layoutClass.treeSize=w1;
	portalGridControls.layoutClass.detailSize=w2;

	createLayout();

	//Grid
	if (_PORTAL_DEFINE["Layout"]["GridView"])
	{
		createGrid();

		if (_PORTAL_DEFINE["Layout"]["GridPagination"])
		{
			portalGridControls.pagination=new Pagination();
			portalGridControls.pagination.left=0;
			portalGridControls.pagination.top=0;
			portalGridControls.pagination.percentWidth=100;
			portalGridControls.layoutClass.getBox(LayoutClass.TOOLBAR_PAGINATION).addElement(portalGridControls.pagination);
			portalGridControls.pagination.portalGrid=this;
		}

	}

	//Tree
	if (_PORTAL_DEFINE["Layout"]["TreeView"])
		createTreeView(_PORTAL_DEFINE.Tree.ExtFields);

	var layout:Object=_PORTAL_DEFINE["Layout"];
	var toolbarGrid:Boolean=layout["GridView"] && layout["ToolbarGrid"]; //工具条
	var toolbarTree:Boolean=layout["TreeView"] && layout["ToolbarTree"];
	var toolbarSplit:Boolean=layout["ToolbarSplit"];
	if (toolbarGrid || toolbarTree)
	{
		createUIToolbar(toolbarGrid, toolbarTree, toolbarSplit);
		createTreeAndGridContextMenu();
	}

	initData();

	return this;

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createTreeAndGridContextMenu():void
{
	if (this.portalGridControls.treeMenu)
		this.portalGridControls.treeMenu.removeEventListener(MenuEvent.ITEM_CLICK, eventMenuItemClick);
	if (this.portalGridControls.gridMenu)
		this.portalGridControls.gridMenu.removeEventListener(MenuEvent.ITEM_CLICK, eventMenuItemClick);

	if (_PORTAL_DEFINE["Layout"]["GridView"])
	{
		if (portalGridControls.gridMenuItems.length != 0)
		{
			if (portalGridControls.gridMenuItems[portalGridControls.gridMenuItems.length - 1].type == "separator")
			{
				portalGridControls.gridMenuItems.length=portalGridControls.gridMenuItems.length - 1;
			}

			if (portalGridControls.gridMenuItems.length != 0)
			{
				this.portalGridControls.gridMenu=Menu.createMenu(FlexGlobals.topLevelApplication as UIComponent, portalGridControls.gridMenuItems, false);
				this.portalGridControls.gridMenu.variableRowHeight=true;
				FlexGlobals.topLevelApplication.registerControlContextPopupMenu(this.portalGridControls.grid.uid, this.portalGridControls.gridMenu);
				this.portalGridControls.gridMenu.itemRenderer=new ClassFactory(IconMenuItemRenderer);
				this.portalGridControls.gridMenu.addEventListener(MenuEvent.ITEM_CLICK, eventMenuItemClick);

				if (this.portalGridControls.grid.contextMenu != portalGridControls.gridContextMenu)
				{
					this.portalGridControls.grid.contextMenu=portalGridControls.gridContextMenu;
						//this.portalGridControls.grid.addEventListener(RightClickManager.RIGHT_CLICK, eventTreeOrGridRightClick);
				}
			}
		}
	}

	if (_PORTAL_DEFINE["Layout"]["TreeView"])
	{
		if (portalGridControls.treeMenuItems.length != 0)
		{
			if (portalGridControls.treeMenuItems[portalGridControls.treeMenuItems.length - 1].type == "separator")
			{
				portalGridControls.treeMenuItems.length=portalGridControls.treeMenuItems.length - 1;

			}
			if (portalGridControls.treeMenuItems.length != 0)
			{
				this.portalGridControls.treeMenu=Menu.createMenu(FlexGlobals.topLevelApplication as UIComponent, portalGridControls.treeMenuItems, false);
				this.portalGridControls.treeMenu.variableRowHeight=true;
				FlexGlobals.topLevelApplication.registerControlContextPopupMenu(this.portalGridControls.tree.uid, this.portalGridControls.treeMenu);
				this.portalGridControls.treeMenu.itemRenderer=new ClassFactory(IconMenuItemRenderer);
				this.portalGridControls.treeMenu.addEventListener(MenuEvent.ITEM_CLICK, eventMenuItemClick);

				if (this.portalGridControls.tree.contextMenu != portalGridControls.treeContextMenu)
				{
					this.portalGridControls.tree.contextMenu=portalGridControls.treeContextMenu;
						//this.portalGridControls.tree.addEventListener(RightClickManager.RIGHT_CLICK, eventTreeOrGridRightClick);
				}
			}
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建布局
private function createLayout():void
{
	portalGridControls.layoutClass.strogeKey="PORTAL_" + this.portalGridOptions.portalID;
	portalGridControls.layoutClass.layoutType=layoutType;
	portalGridControls.layoutClass.version=_PORTAL_DEFINE["version"];

	var layout:Object=_PORTAL_DEFINE["Layout"];
	var layoutType:String=layout["LayoutType"]; //布局类型
	var toolbarGrid:Boolean=layout["ToolbarGrid"]; //工具条
	var toolbarTree:Boolean=layout["ToolbarTree"];
	var toolbarSplit:Boolean=layout["ToolbarSplit"];
	var gridPagination:Boolean=layout["GridPagination"];
	var treeView:Boolean=layout["TreeView"];
	var gridView:Boolean=layout["GridView"];

	var treeCell:String=layout["TreeCell"];
	var gridCell:String=layout["GridCell"];

	this.portalGridOptions.gridFormShowType=layout["GridFormShowType"];
	this.portalGridOptions.treeFormShowType=layout["TreeFormShowType"];

	switch (layoutType)
	{
		case "1C":
			addElement(portalGridControls.layoutClass.createLayout_1C(treeView, toolbarTree, gridView, toolbarGrid, gridPagination));
			break;
		case "2U":
			addElement(portalGridControls.layoutClass.createLayout_2U(treeView, toolbarTree, gridView, toolbarGrid, gridPagination, toolbarSplit, treeCell, gridCell));
			break;
		case "2E":
			addElement(portalGridControls.layoutClass.createLayout_2E(treeView, toolbarTree, gridView, toolbarGrid, gridPagination, toolbarSplit, treeCell, gridCell));
			break;
		case "3J":
			addElement(portalGridControls.layoutClass.createLayout_3J(treeView, toolbarTree, gridView, toolbarGrid, gridPagination, toolbarSplit, treeCell, gridCell));
			break;
		case "3T":
			addElement(portalGridControls.layoutClass.createLayout_3T(treeView, toolbarTree, gridView, toolbarGrid, gridPagination, toolbarSplit, treeCell, gridCell));
			break;
		case "3U":
			addElement(portalGridControls.layoutClass.createLayout_3U(treeView, toolbarTree, gridView, toolbarGrid, gridPagination, toolbarSplit, treeCell, gridCell));
			break;
		case "3L":
			addElement(portalGridControls.layoutClass.createLayout_3L(treeView, toolbarTree, gridView, toolbarGrid, gridPagination, toolbarSplit, treeCell, gridCell));
			break;
	}

	portalGridControls.layoutClass.loadLayoutSizes();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建树状视图
private function createTreeView(extFields:Array):void
{
	if (portalGridOptions.isTreeViewAsGrid)
	{
		var _gridView:DataGrid=new DataGrid();
		//创建数据列表
		_gridView=new DataGrid();
		_gridView.doubleClickEnabled=true;
		_gridView.setStyle("borderStyle", "none");
		_gridView.draggableColumns=false;
		_gridView.addEventListener(MouseEvent.DOUBLE_CLICK, function():void
		{
			funTreeEdit();
		});
		_gridView.addEventListener(ListEvent.CHANGE, hookTreeSelectedChange);
		_gridView.addEventListener(ListEvent.CHANGE, eventTreeOnSelectChange);

		_gridView.headerHeight=28;
		_gridView.rowHeight=25;
		_gridView.setStyle("alternatingItemColors", [0xFEFEFE, 0xFFFFFF]);
		_gridView.sortableColumns=false;
		UIUtils.fullAlign(_gridView);
		_gridView.horizontalScrollPolicy="auto";
		_gridView.verticalScrollPolicy="auto";
		var gridColumns:Array=_gridView.columns;
		var column:DataGridColumn=null;
		if (portalGridOptions.isTreeSupportCheckBox)
		{
			column=new DataGridColumn();
			column.dataField="selected";
			column.width=25;
			column.headerText="";
			var render:ClassFactory=new ClassFactory(CheckBoxItemRender);
			render.properties={clickCallFunction: function()
			{
				funGridRefresh();
			}};
			column.itemRenderer=render;

			column.resizable=false;
			column.setStyle("textAlign", "center");
			gridColumns.push(column);
		}
		column=new DataGridColumn();
		column.dataField="label";
		column.headerText=getPortalResourceDefine("TREE_NAME", "Text", _GLOBAL_DEFINE["INIT_DATA"]["TREE"][0].label);
		gridColumns.push(column);

		if (extFields)
		{
			for (var i:int=0; i < extFields.length; i++)
			{
				var fieldName:String=extFields[i].FieldName;
				var fieldLabel:String=extFields[i].FieldLabel;
				column=new DataGridColumn();
				column.dataField=fieldName;
				column.headerText=fieldLabel;
				gridColumns.push(column);
			}
		}

		_gridView.columns=gridColumns;
		portalGridControls.tree=_gridView;
	}
	else
	{
		var _treeView:Tree=new Tree();
		_treeView.percentWidth=100;
		_treeView.percentHeight=100;

		_treeView.horizontalScrollPolicy=ScrollPolicy.AUTO;
		_treeView.verticalScrollPolicy=ScrollPolicy.AUTO;
		_treeView.addEventListener(ListEvent.CHANGE, hookTreeSelectedChange);
		//执行完后展看所有节点
		_treeView.addEventListener(FlexEvent.CREATION_COMPLETE, function(event:FlexEvent):void
		{
			_treeView.selectedItem=_treeView.dataProvider.getItemAt(0);
			_treeView.expandChildrenOf(_treeView.selectedItem, true);
		});


		//点击事件
		_treeView.addEventListener(ListEvent.CHANGE, eventTreeOnSelectChange);
		_treeView.addEventListener(MouseEvent.DOUBLE_CLICK, function(e:MouseEvent):void
		{
			funTreeEdit()
		});
		_treeView.doubleClickEnabled=true;

		var itemRender:ClassFactory=new ClassFactory(IconItemRender);
		itemRender.properties={ownerDocument: this, includeCheckBox: portalGridOptions.isTreeSupportCheckBox, checkboxClickFun: portalGridOptions.isTreeSupportCheckBox ? eventTreeCheckBoxOnClick : null};
		_treeView.itemRenderer=itemRender;
		_treeView.left=1;
		_treeView.top=1;
		_treeView.right=1;
		_treeView.bottom=1;
		_treeView.labelField="label";
		portalGridControls.tree=_treeView
	}
	portalGridControls.layoutClass.getBox(LayoutClass.BOX_TREE).addElement(portalGridControls.tree);
}

/////////////////////////////////////////////////////////////////////////////////////////////
//创建数据列表
private function createGrid():void
{
	//创建数据列表
	this.portalGridControls.grid=new DataGridEx();
	this.portalGridControls.grid.editable=true;
	this.portalGridControls.grid.setStyle("borderStyle", "none");
	this.portalGridControls.grid.draggableColumns=false;
	this.portalGridControls.grid.doubleClickEnabled=true;
	this.portalGridControls.grid.addEventListener(MouseEvent.DOUBLE_CLICK, function():void
	{
		funGridEdit();
	});
	this.portalGridControls.grid.addEventListener(ListEvent.CHANGE, hookGridSelectedChange);
	this.portalGridControls.grid.addEventListener(MouseEvent.MOUSE_DOWN, event4GridMouseDown);
	this.portalGridControls.grid.addEventListener(MouseEvent.MOUSE_UP, event4GridMouseUp);

	portalGridControls.grid.headerHeight=28;
	portalGridControls.grid.rowHeight=25;
	portalGridControls.grid.setStyle("alternatingItemColors", [0xFFFFFF, 0xFFFFFF]);
	portalGridControls.grid.sortableColumns=false;
	UIUtils.fullAlign(portalGridControls.grid);
	this.portalGridControls.layoutClass.getBox(LayoutClass.BOX_GRID).addElement(portalGridControls.grid);


	//创建数据列表的数据列
	var gridDefine:Object=_PORTAL_DEFINE["Grid"];
	var columnsDefine:Array=gridDefine["Columns"] as Array;
	var gridColumns:Array=portalGridControls.grid.columns;
	var gridAutoWidth:Boolean=false;

	for (var i:int=0; i < columnsDefine.length; i++)
	{
		var columnDefine:Object=columnsDefine[i];
		var isCheckColumn:Boolean=columnDefine["Caption"] == "__chk__";
		var isSpecialColumn:Boolean=columnDefine.Special;
		var column:DataGridColumn=new DataGridColumn();
		//trace("isSpecialColumn:", isSpecialColumn);

		if (isCheckColumn)
		{
			column.dataField="__chk__";
			column.width=25;
			column.headerText="";
			portalGridOptions.isGridIncludeCheckBox=true;
			var headerRenderer:ClassFactory=new ClassFactory(CheckBoxHeaderRender);
			headerRenderer.properties={portalGrid: this};
			column.headerRenderer=headerRenderer;
			column.itemRenderer=new ClassFactory(CheckBoxItemRender);
			column.resizable=false;
			column.setStyle("textAlign", columnDefine["Align"]);
		}
		else
		{
			column.headerRenderer=new ClassFactory(CommonHeaderRender);
			column.setStyle("textAlign", columnDefine["Align"]);
			column.headerText=columnDefine["Caption"];
			column.dataField=columnDefine["Field"] == "" ? ("__F" + (portalGridOptions.isGridIncludeCheckBox ? (i - 1) : i) + "__") : columnDefine["Field"];

			if (column.dataField != "__key__")
				portalGridControls.gridColumnFields.push(column.dataField);

			if (columnDefine["Width"] == "" || columnDefine["Width"] == "*")
				gridAutoWidth=true;
			else if (columnDefine["Width"] == "0")
				column.visible=false;
			else
				column.width=(int)(columnDefine["Width"]);

			if (columnDefine["IsLinkColumn"]) //超级连接列
			{
				var render:ClassFactory=new ClassFactory(HrefRender4PortalGrid);
				render.properties={align: columnDefine["Align"], portalGrid: this, columnStyle: columnStyle2Array(columnDefine.Style)};
				column.itemRenderer=render;
			}
			else if (isSpecialColumn) //专题列
			{
				var render:ClassFactory=new ClassFactory(IconColumnRender4DynamicUI);
				column.itemRenderer=render;
			}
			else
			{
				var render:ClassFactory=new ClassFactory(CommonItemRender);
				render.properties={columnStyle: columnStyle2Array(columnDefine.Style)};
				column.itemRenderer=render;
			}

			column.resizable=!columnDefine["FixedWidth"];
		}

		if (columnDefine.ReadonlyAble)
			portalGridControls.supportReadonlyColumns.push(column);
		column.editable=columnDefine.Editor;
		gridColumns.push(column);
	}

	portalGridControls.grid.horizontalScrollPolicy="auto";
	portalGridControls.grid.verticalScrollPolicy="auto";

	if (_PORTAL_DEFINE["Layout"]["SplitGrid"])
	{
		portalGridControls.grid.lockedColumnCount=portalGridOptions.isGridIncludeCheckBox ? _PORTAL_DEFINE["Layout"]["SplitColumnIndex"] + 1 : _PORTAL_DEFINE["Layout"]["SplitColumnIndex"];
	}

	this.portalGridControls.grid.columns=gridColumns;
	this.portalGridControls.grid.invalidateList();
}

////////////////////////////////////////////////////////////////////////////////////////////////////////
//生成Tree的工具条对象
private function createDetailToolbarItems(menuItems:Array):Boolean
{
	var btnID:String="";
	var resourceID:String="";
	var additionToolbars:Array=_PORTAL_DEFINE["Layout"]["ToolbarAddition"]["Items"];
	for (var i:int=0; i < additionToolbars.length; i++)
	{
		var btnItem:Object=additionToolbars[i];
		if (btnItem.Position == "DetailToolbar")
		{
			var PosIndex:int=Convert.str2int(btnItem.PosIndex, 0);
			if (btnItem.Type == "按钮" || btnItem.Type == "标题按钮")
			{
				menuItems.push({PosIndex: PosIndex, type: "btn", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
			}
			else if (btnItem.Type == "分隔条")
			{
				menuItems.push({PosIndex: PosIndex, type: "sep"});
			}
			else if (btnItem.Type == "下拉列表")
			{
				menuItems.push({PosIndex: PosIndex, items: btnItem.Items, type: "subitem", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
			}
			else if (btnItem.Type == "标签")
			{
				menuItems.push({PosIndex: PosIndex, type: "label", title: btnItem.Caption});
			}

		}
	}
	return menuItems.length != 0;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////
//生成Tree的工具条对象
private function createTreeToolbarItems(menuItems:Array, layout:Object):void
{
	var btnID:String="";
	var resourceID:String="";
	if (!portalGridOptions.isCommonSearch)
	{
		//新增  && 编辑
		if (layout["TreeSupportNew"])
		{
			if (layout["TreeSingleLevel"])
			{
				btnID="folder_new_child";
				resourceID="TOOLBAR_TREE_NEW_CHILD";
				menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
				createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);
			}
			else
			{
				var items:Array=[{id: "folder_new_sibling", supportRight: true, icon: getPortalResourceDefine("TOOLBAR_TREE_NEW_SAME_LEVEL", "Icon"), title: getPortalResourceDefine("TOOLBAR_TREE_NEW_SAME_LEVEL", "Title"), text: getPortalResourceDefine("TOOLBAR_TREE_NEW_SAME_LEVEL", "Text")}, {id: "folder_new_child", supportRight: true, icon: getPortalResourceDefine("TOOLBAR_TREE_NEW_CHILD", "Icon"), title: getPortalResourceDefine("TOOLBAR_TREE_NEW_CHILD", "Title"), text: getPortalResourceDefine("TOOLBAR_TREE_NEW_CHILD", "Text")}];
				btnID="folder_new_sibling";
				resourceID="TOOLBAR_TREE_NEW_SAME_LEVEL";
				menuItems.push({supportRight: true, type: "list", items: items});
				createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);
			}

			menuItems.push({type: "sep"});
			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);

			btnID="folder_edit";
			resourceID="TOOLBAR_TREE_EDIT";
			menuItems.push({supportRight: false, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			menuItems.push({type: "sep"});
			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, false);
			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);
		}
		//删除           
		if (layout["TreeSupportDelete"])
		{
			btnID="folder_delete";
			resourceID="TOOLBAR_TREE_DELETE";
			menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			menuItems.push({type: "sep"});
			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);
			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);
		}
		//交换顺序
		if (layout["TreeSupportExchange"])
		{
			btnID="folder_moveup";
			resourceID="TOOLBAR_TREE_UP";
			menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);

			btnID="folder_movedown";
			resourceID="TOOLBAR_TREE_DOWN";
			menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			menuItems.push({type: "sep"});
			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);

			createContextPopupMenuItem(true, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);
		}
	}

	//过滤数据
	btnID="folder_filter";
	menuItems.push({type: "btn", id: btnID, icon: "filter_16x16.png", title: "查找数据", text: ""});
	createContextPopupMenuItem(true, "查找数据", btnID, "filter_16x16.png", false, false);
	createContextPopupMenuItem(true, "查找数据", btnID, "filter_16x16.png", true, false);

	//刷新
	btnID="folder_refresh";
	resourceID="TOOLBAR_TREE_DOWN";
	menuItems.push({type: "btn", id: btnID, icon: "arrow_refresh.png", title: "刷新数据", text: ""});
	createContextPopupMenuItem(true, "刷新数据", btnID, "arrow_refresh.png", false, false);
	createContextPopupMenuItem(true, "刷新数据", btnID, "arrow_refresh.png", true, false);

	var additionToolbars:Array=_PORTAL_DEFINE["Layout"]["ToolbarAddition"]["Items"];
	for (var i:int=0; i < additionToolbars.length; i++)
	{
		var btnItem:Object=additionToolbars[i];
		if (btnItem.Position == "TreeToolbar")
		{
			var PosIndex:int=Convert.str2int(btnItem.PosIndex, 0);
			if (btnItem.Type == "按钮" || btnItem.Type == "标题按钮")
			{
				menuItems.push({PosIndex: PosIndex, type: "btn", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
				createContextPopupMenuItem(true, btnItem.Caption, btnItem.Function, btnItem.Icon, false, Convert.object2Boolean(btnItem.SupportRight, false));
			}
			else if (btnItem.Type == "分隔条")
			{
				menuItems.push({PosIndex: PosIndex, type: "sep"});
				createContextPopupMenuItem(true, "", "", "", true, false);
			}
			else if (btnItem.Type == "下拉列表")
			{
				menuItems.push({PosIndex: PosIndex, items: btnItem.Items, type: "subitem", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
			}
			else if (btnItem.Type == "标签")
			{
				menuItems.push({PosIndex: PosIndex, type: "label", title: btnItem.Caption});
			}

		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createContextPopupMenuItem(isTree:Boolean, caption:String, tag:String, iconURL:String, isSeparator:Boolean, isSupportRight:Boolean):void
{
	iconURL="../images/18x18/" + iconURL;
	var contextMenuItem:Object={label: caption, tag: tag, iconURL: iconURL};
	var newBuildMenuItem:ContextMenuItem=new ContextMenuItem(caption);
	newBuildMenuItem.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, eventContextMenuItemClick);
	if (isTree)
	{
		//separator
		if (!isSeparator)
		{
			portalGridControls.treeContextMenu.customItems.push(newBuildMenuItem);
			portalGridControls.treeMenuItems.push(contextMenuItem);
			portalGridControls.tag2ContextMenuItems[tag]=[newBuildMenuItem, contextMenuItem];
		}
		else
		{
			portalGridControls.treeMenuItems.push({type: "separator"});
		}
	}
	else
	{
		if (!isSeparator)
		{
			portalGridControls.gridContextMenu.customItems.push(newBuildMenuItem);
			portalGridControls.gridMenuItems.push(contextMenuItem);
			portalGridControls.tag2ContextMenuItems[tag]=[newBuildMenuItem, contextMenuItem];
		}
		else
		{
			portalGridControls.gridMenuItems.push({type: "separator"});
		}
	}
	portalGridControls.contextMenuItem2Tag.push(tag);
	portalGridControls.allContextMenuItem.push(newBuildMenuItem);

	if (!isSeparator && isSupportRight)
	{
		portalGridControls.supportRightContextMenuItems.push(contextMenuItem);
		portalGridControls.supportRightContextMenuItems.push(newBuildMenuItem);
		portalGridControls.supportReadonlyTags.push(tag);
	}
	//portalGridControls.contextMenuItem2Tag[newBuildMenuItem]=tag;
	//treeMenu:null,//
	//	gridMenu:null,//
	//	treeMenuItems:[],//
	//	gridMenuItems:[]//
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//生成Grid的工具条对象
private function createGridToolbarItems(menuItems:Array, layout:Object):void
{
	var btnID:String="";
	var resourceID:String="";

	if (!portalGridOptions.isCommonSearch)
	{
		//新增 && 编辑
		if (layout["GridSupportNew"])
		{
			btnID="grid_new";
			resourceID="TOOLBAR_GRID_NEW";
			menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			menuItems.push({type: "sep"});

			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);
			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);

			btnID="grid_edit";
			resourceID="TOOLBAR_GRID_EDIT";
			menuItems.push({supportRight: false, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			menuItems.push({type: "sep"});

			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, false);
			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);
		}

		//删除
		if (layout["GridSupportDelete"])
		{
			btnID="grid_delete";
			resourceID="TOOLBAR_GRID_DELETE";
			menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			menuItems.push({type: "sep"});

			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);
			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);
		}

		//位置移动
		if (layout["GridSupportExchange"])
		{
			btnID="grid_moveup";
			resourceID="TOOLBAR_GRID_UP";
			menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);

			btnID="grid_movedown";
			resourceID="TOOLBAR_GRID_DOWN";
			menuItems.push({supportRight: true, type: "btn", id: btnID, icon: getPortalResourceDefine(resourceID, "Icon"), title: getPortalResourceDefine(resourceID, "Title"), text: getPortalResourceDefine(resourceID, "Text")});
			menuItems.push({type: "sep"});
			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), false, true);
			createContextPopupMenuItem(false, getPortalResourceDefine(resourceID, "Title"), btnID, getPortalResourceDefine(resourceID, "Icon"), true, false);
		}
	}
	var additionToolbars:Array=_PORTAL_DEFINE["Layout"]["ToolbarAddition"]["Items"];
	for (var i:int=0; i < additionToolbars.length; i++)
	{
		var btnItem:Object=additionToolbars[i];
		if (btnItem.Position == "GridToolbar")
		{
			var PosIndex:int=Convert.str2int(btnItem.PosIndex, 0);
			if (btnItem.Type == "按钮" || btnItem.Type == "标题按钮")
			{
				menuItems.push({PosIndex: PosIndex, type: "btn", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
				createContextPopupMenuItem(false, btnItem.Caption, btnItem.Function, btnItem.Icon, false, Convert.object2Boolean(btnItem.SupportRight, false));
			}
			else if (btnItem.Type == "分隔条")
			{
				menuItems.push({PosIndex: PosIndex, type: "sep"});
				createContextPopupMenuItem(false, "", "", "", true, false);
			}
			else if (btnItem.Type == "下拉列表")
			{
				menuItems.push({PosIndex: PosIndex, items: btnItem.Items, type: "subitem", supportRight: Convert.object2Boolean(btnItem.SupportRight, false), id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
			}
			else if (btnItem.Type == "标签")
			{
				menuItems.push({PosIndex: PosIndex, type: "label", title: btnItem.Caption});
			}
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建工具条
private function createUIToolbar(toolbarGrid:Boolean, toolbarTree:Boolean, toolbarSplit:Boolean):void
{
	var layout:Object=_PORTAL_DEFINE["Layout"];
	var menuItems:Array=null;
	var resourceID:String="";
	var btnID:String="";
	var toolbarGridContain:Group=null;
	var boolbarGridContainParent:Group=null;
	if (toolbarSplit)
	{
		//for Tree
		if (toolbarTree)
		{
			menuItems=[];
			createTreeToolbarItems(menuItems, layout);
			boolbarGridContainParent=this.portalGridControls.layoutClass.getBox(LayoutClass.TOOLBAR_TREE)
			createToolbar(this.portalGridControls.layoutClass.getBox(LayoutClass.TOOLBAR_TREE), menuItems);
		}

		//for Grid
		if (toolbarGrid)
		{
			menuItems=[];
			createGridToolbarItems(menuItems, layout);
			boolbarGridContainParent=this.portalGridControls.layoutClass.getBox(LayoutClass.TOOLBAR_GRID);
			toolbarGridContain=createToolbar(boolbarGridContainParent, menuItems) as Group;
		}
	}
	else
	{
		menuItems=[];
		if (toolbarTree)
			createTreeToolbarItems(menuItems, layout);
		if (toolbarGrid)
			createGridToolbarItems(menuItems, layout);
		boolbarGridContainParent=this.portalGridControls.layoutClass.getBox(LayoutClass.TOOLBAR_TOP);
		toolbarGridContain=createToolbar(boolbarGridContainParent, menuItems) as Group;
	}

	//详细区域显示工具条
	menuItems=[];
	if (createDetailToolbarItems(menuItems) && this.portalGridControls.layoutClass.createDetailLayout(_PORTAL_DEFINE["Layout"]["DetailCell"]))
	{
		createToolbar(this.portalGridControls.layoutClass.getBox(LayoutClass.TOOLBAR_DETAIL), menuItems) as Group;
	}
	//创建过滤器
	if (toolbarGrid)
	{
		var params:Array=_PORTAL_DEFINE["Grid"]["Param"];

		var filterContain:FilterPanel=new FilterPanel();
		filterContain.height=28 * 4;
		var vGroup:VGroup=this.portalGridControls.layoutClass.getBox(LayoutClass.BOX_GRID);
		vGroup.addElementAt(filterContain, 0);
		filterContain.validateNow();

		var filterPanels:Array=[toolbarGridContain, filterContain.getFilterPanel(0), filterContain.getFilterPanel(1), filterContain.getFilterPanel(2), filterContain.getFilterPanel(3)];
		//boolbarGridContainParent.addElement(filterContain);
		//boolbarGridContainParent.parent.height = 400;
		//boolbarGridContainParent.height = 400;
		var filterPanelVS:Array=[false, false, false, false, false];

		for (var i:int=0; i < params.length; i++)
		{
			var param:Object=params[i];
			var pos:int=Convert.str2int(param.pos, 1);
			if (pos == 0)
				pos=1;
			filterPanelVS[pos - 1]=true;
			toolbarGridContain=filterPanels[pos - 1];

			if (param["label"] != "")
			{
				var label:Label=new Label();
				label.text=param["label"];
				UIUtils.padding(label, 0, 3, 0, 0);
				toolbarGridContain.addElement(label);
			}

			if (param["control"] == "TextBox")
			{
				var input:TextInput=new TextInput();
				input.width=param["controlWidth"] ? param["controlWidth"] * 1 : 120;
				input.height=24;
				toolbarGridContain.addElement(input);
				portalGridControls.gridParam2Control[param["name"]]=input;
				input.addEventListener(KeyboardEvent.KEY_DOWN, eventFilterInputKeyPress);
			}
			else if (param["control"] == "Date")
			{
				var datefield:DateField=new DateField();
				datefield.editable=true;
				datefield.formatString="YYYY-MM-DD";
				datefield.yearNavigationEnabled=true;
				toolbarGridContain.addElement(datefield);
				portalGridControls.gridParam2Control[param["name"]]=datefield;
					//datefield.addEventListener(mx.events.FlexEvent.DATA_CHANGE, function(event:FlexEvent):void {
					//	eventFilterInputKeyPress(null);
					//});
			}
			else if (param["control"] == "ComboBox")
			{
				var combobox:ComboBox=new ComboBox();
				combobox.width=param["controlWidth"] ? param["controlWidth"] * 1 : 120;
				combobox.height=24;
				toolbarGridContain.addElement(combobox);
				combobox.addEventListener(FlexEvent.CREATION_COMPLETE, function(event:FlexEvent):void
				{
					event.currentTarget.textInput.editable=false;
				});

				portalGridControls.gridParam2Control[param["name"]]=combobox;
				var comboboxInitItems:Array=_GLOBAL_DEFINE.COMBOBOX_PARAMS[param["name"]];
				var comboboxItems:Array=[];
				var maxItemLength:int=0;
				for (var j:int=0; j < comboboxInitItems.length; j++)
				{
					var itemValue:String=comboboxInitItems[j][0];
					var itemLabel:String=comboboxInitItems[j][1];
					comboboxItems.push({data: itemValue, label: itemLabel});
					var itemWidth:int=FlexGlobals.topLevelApplication.measureTextWidth(itemLabel);
					if (itemWidth > maxItemLength)
						maxItemLength=itemWidth;
				}
				combobox.dataProvider=new ArrayCollection(comboboxItems);
				combobox.width=maxItemLength + 30;
				combobox.selectedIndex=0;
			}

		}

		filterContain.setPanelVisible(filterPanelVS);

		if (params.length != 0)
		{
			var btn:IconButton=new IconButton();
			btn.toolTip="过滤数据";
			btn.addEventListener(MouseEvent.CLICK, function():void
			{
				eventFilterInputKeyPress(null);
			});
			btn.iconURL="../images/18x18/search.png";
			toolbarGridContain.addElement(btn);
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建工具条对象
private function createToolbar(parent:Group, menuItems:Array):UIComponent
{
	var toolbarContain:HGroup=new HGroup();
	toolbarContain.gap=0;
	toolbarContain.verticalAlign="middle";
	toolbarContain.paddingLeft=2;
	UIUtils.fullAlign(toolbarContain);
	parent.addElement(toolbarContain);
	var menuBar:PopUpMenuButton=null;
	for (var i:int=0; i < menuItems.length; i++)
	{
		var menuItem:Object=menuItems[i];
		var posIndex:int=Convert.str2int(menuItem.PosIndex, 0) - 1;
		if (menuItem.type == "btn")
		{
			var btn:IconButton=new IconButton();
			btn.label=menuItem["text"];
			btn.toolTip=menuItem["title"];
			btn.iconURL="../images/18x18/" + menuItem["icon"];
			btn.id=menuItem["id"];
			if (menuItem.supportRight)
				portalGridControls.supportReadonlyBtns.push(btn);
			btn.addEventListener(MouseEvent.CLICK, eventToolbarItemClick);

			if (posIndex == -1)
				toolbarContain.addElement(btn);
			else
				toolbarContain.addElementAt(btn, Math.min(posIndex, toolbarContain.numElements));

			portalGridControls.toolbarItemName2Instance[btn.id]=btn;
		}
		else if (menuItem.type == "sep")
		{
			var sep:ToolbarVertline=new ToolbarVertline();
			if (posIndex == -1)
				toolbarContain.addElement(sep);
			else
				toolbarContain.addElementAt(sep, Math.min(posIndex, toolbarContain.numElements));
		}
		else if (menuItem.type == "subitem")
		{
			menuBar=new PopUpMenuButton();
			menuBar.label=menuItem["title"];
			menuBar.setStyle("fontWeight", "bold");
			menuBar.height=24;
			menuBar.labelField="label";
			menuBar.dataProvider=new ArrayCollection(menuItem.items);

			if (posIndex == -1)
				toolbarContain.addElement(menuBar);
			else
				toolbarContain.addElementAt(menuBar, Math.min(posIndex, toolbarContain.numElements));

			menuBar.id=menuItem.id;
			menuBar.addEventListener(mx.events.MenuEvent.ITEM_CLICK, function(event:MenuEvent):void
			{

				var bar:PopUpMenuButton=event.currentTarget as PopUpMenuButton;
				if (portalGridControls.supportReadonlyBtns.indexOf(bar) == -1 || !portalGridOptions.readonly)
					executeByBarItemID(bar.id, event.item);
			});

			if (menuItem.supportRight)
				portalGridControls.supportReadonlyBtns.push(menuBar);

			portalGridControls.toolbarItemName2Instance[menuBar.id]=menuBar;

		}
		else if (menuItem.type == "list")
		{
			menuBar=new PopUpMenuButton();
			menuBar.label="";
			menuBar.width=43;
			menuBar.height=24;
			menuBar.labelField="title";
			menuBar.dataProvider=new ArrayCollection(menuItem.items);

			menuBar.addEventListener(mx.events.MenuEvent.ITEM_CLICK, function(event:MenuEvent):void
			{
				var bar:PopUpMenuButton=event.currentTarget as PopUpMenuButton;
				bar.id=event.item.id;
				bar.setStyle("icon", IconUtility.getClass(menuBar, "../images/18x18/" + event.item.icon, 18, 18));
				bar.toolTip=event.item.title;
				if (!(event.item.supportRight && portalGridOptions.readonly))
					executeByBarItemID(bar.id);
			});

			menuBar.id=menuItem.items[0].id;
			menuBar.setStyle("icon", IconUtility.getClass(menuBar, "../images/18x18/" + menuItem.items[0].icon, 18, 18));
			menuBar.toolTip=menuItem.items[0].title;

			if (menuItem.supportRight)
				portalGridControls.supportReadonlyBtns.push(menuBar);

			if (posIndex == -1)
				toolbarContain.addElement(menuBar);
			else
				toolbarContain.addElementAt(menuBar, Math.min(posIndex, toolbarContain.numElements));

			portalGridControls.toolbarItemName2Instance[menuBar.id]=menuBar;
		}
		else if (menuItem.type == "label")
		{
			var label:Label=new Label();
			label.text=menuItem.title;
			label.setStyle("fontWeight", "bold");
			if (posIndex == -1)
				toolbarContain.addElement(label);
			else
				toolbarContain.addElementAt(label, Math.min(posIndex, toolbarContain.numElements));
		}
	}
	return toolbarContain;
}

//----------------------------------------------------------------------------------------------------------------
public function gotoPage(page:int):void
{
	goPage(portalGridControls.pagination == null ? 2500 : portalGridControls.pagination.recordPrePage, page);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getToolBarItem(name:String):UIComponent
{
	var result:UIComponent=portalGridControls.toolbarItemName2Instance[name] as UIComponent;
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function setToolBarItemEnabled(name:String, enabled:Boolean):void
{
	if (!portalGridControls.hasOwnProperty(name))
	{
		var array:Array=portalGridControls.tag2ContextMenuItems[name];
		if (array != null)
		{
			for (var i:int=0; i < array.length; i++)
				array[i].enabled=enabled;
		}
		if (portalGridControls.toolbarItemName2Instance[name])
			portalGridControls.toolbarItemName2Instance[name].enabled=enabled;
	}
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

