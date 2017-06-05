// ActionScript file

import com.estudio.flex.RUNTIME_GLOBAL;
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
import com.estudio.flex.module.LayoutClass;
import com.estudio.flex.module.PortalGrid;
import com.estudio.flex.utils.AjaxUtils;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayCollectionUtils;
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
import flash.ui.ContextMenu;
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
import spark.events.IndexChangeEvent;

private const _URL_DATASERVICE:String="../client/pdservice";
private const _O_GET_GRID_JSON4FLEX:String="getgridjson4flex";

private var _PORTAL_DEFINE:Object=null;
private var _GLOBAL_DEFINE:Object=null;
//数据定义
private var portalGridDatas:Object={_treeDataList4Search: [], //
		_treeData: new ArrayCollection(), //
		_gridData: new ArrayCollection(), //
		_TreeViewRootNode: null, //
		_SELECTED_TREE_ID: null //
	};


//控件集
private var portalGridControls:Object={layoutClass: new LayoutClass(), //布局对象
		grid: null, //列表
		tree: null, //树
		pagination: null, //分页
		supportReadonlyBtns: [], //支持权限的按钮
		supportReadonlyColumns: [], //支持权限的列
		toolbarItemName2Instance: {}, //工具条按钮字段
		gridParam2Control: {}, // 按钮过滤控件字典
		headerCheckBox: null, //Grid表头的全选CheckBox
		gridColumnFields: [], //Grid Column字段列表
		iframe: null, //IFrame对象
		treeContextMenu: new ContextMenu, //Tree右键菜单
		gridContextMenu: new ContextMenu, //Grid右键菜单
		contextMenuItem2Tag: [], //
		allContextMenuItem: [], //
		treeMenu: null, //
		gridMenu: null, //
		treeMenuItems: [], //
		gridMenuItems: [], //
		supportRightContextMenuItems: [], //
		supportReadonlyTags: [], //
		tag2ContextMenuItems: {} //
	};

//选择项集合
private var portalGridOptions:Object={portalID: "", //Portal ID
		readonly: false, isGridIncludeCheckBox: false, //Grid是否包含CheckBox
		isTreeSupportCheckBox: false, //Tree是否包含CheckBox
		gridFormShowType: 0, //Grid绑定的表单显示方式
		treeFormShowType: 0, //Tree绑定的表单显示方式
		isTreeViewAsGrid: false, //Tree是否已Grid的方式展现
		isCommonSearch: false, //是否为普通查询
		isAsyncTreeData: false //是否异步加载数据
	};

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//权限只读
public function set readonly(value:Boolean):void
{
	if (portalGridOptions.readonly != value)
	{
		portalGridOptions.readonly=value;
		var i:int=0;
		for (i=0; i < portalGridControls.supportReadonlyBtns.length; i++)
		{
			portalGridControls.supportReadonlyBtns[i].enabled=!portalGridOptions.readonly;
		}

		for (i=0; i < portalGridControls.supportReadonlyColumns.length; i++)
		{
			portalGridControls.supportReadonlyColumns[i].visible=!portalGridOptions.readonly;
		}

		for (i=0; i < portalGridControls.supportRightContextMenuItems.length; i++)
		{
			var o:Object=portalGridControls.supportRightContextMenuItems[i];
			o.enabled=!value;
		}
		createTreeAndGridContextMenu();
	}

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function get readonly():Boolean
{
	return portalGridOptions.readonly;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
public function set headerCheckBox(value:CheckBox):void
{
	portalGridControls.headerCheckBox=value;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
public function get headerCheckBox():CheckBox
{
	return portalGridControls.headerCheckBox;
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////
//只选择被选中的数据
private function treeOnlyCheckSelectedItem(array:Array, selectedItem:Object):void
{
	for (var i:int=0; i < array.length; i++)
	{
		array[i].selected=false;
		if (array[i].children && array[i].children is Array)
		{
			treeOnlyCheckSelectedItem(array[i].children as Array, selectedItem);
		}
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//获取所有CheckBox被选中的Tree数据ID列表
private function treeGetSelectedIDS(array:Array, ids:Array):void
{
	for (var i:int=0; i < array.length; i++)
	{
		if (array[i].selected)
			ids.push(array[i].id);
		if (array[i].children && array[i].children is Array)
		{
			treeGetSelectedIDS(array[i].children as Array, ids);
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//得到变量定义
private function getPortalResourceDefine(resourceName:String, resourceType:String, defaultValue:String=""):String
{
	var result:String=defaultValue;
	var resource:Object=_PORTAL_DEFINE["Resources"];
	if (resource && resource[resourceName] && resource[resourceName][resourceType])
		result=resource[resourceName][resourceType];
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//转到页码 同时有强制刷新功能
public function goPage(recordPerpage:int, page:int):void
{
	if (portalGridControls.grid != null)
	{
		var postData:Object={id: portalGridOptions.portalID, p: page, r: recordPerpage, p_id: portalGridDatas.SELECTED_TREE_ID, o: _O_GET_GRID_JSON4FLEX};
		if (portalGridOptions.isTreeSupportCheckBox)
		{
			var ids:Array=[];
			treeGetSelectedIDS(portalGridDatas.treeData.source, ids);
			postData["p_id_s"]=ids.join(",");
		}
		generalFilterCondition(postData);
		//利用ajax 从服务器端读取数据


		var tempGridData:Object=JSFunUtils.JSFun("getPortalGridJson", postData);

		fillGridByData(tempGridData);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//产生过滤条件
private function generalFilterCondition(postData:Object):void
{
	var params:Array=_PORTAL_DEFINE["Grid"]["Param"];
	if (params.length != 0)
	{
		for (var i:int=0; i < params.length; i++)
		{
			var param:Object=params[i];
			var paramName:String=param["name"];
			var control:UIComponent=portalGridControls.gridParam2Control[paramName];
			if (control is TextInput)
			{
				postData[paramName]=StringUtils.trim((TextInput(control)).text);
			}
			else if (control is DateField)
			{
				postData[paramName]=Convert.dateTime2Str(DateField(control).selectedDate, "YYYY-MM-DD");
			}
			else if (control is ComboBox)
			{
				var combo:ComboBox=control as ComboBox;
				if (combo.selectedItem != null)
				{
					postData[paramName]=combo.selectedItem.data;
				}
			}
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function treeDataIsRoot(data:Object):Boolean
{
	return data == (portalGridOptions.isTreeViewAsGrid ? portalGridDatas.TreeViewRootNode : portalGridDatas.treeData.getItemAt(0));
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//得到Tree对应的表单列表
public function getForms(isTree:Boolean):Object
{
	return isTree ? {ids: _GLOBAL_DEFINE["PORTAL_DEFINE"]["Tree"]["BindForms"]["Forms"], size: {w: _GLOBAL_DEFINE["TREE_FORMS_SIZE"][0], h: _GLOBAL_DEFINE["TREE_FORMS_SIZE"][1]}} : {ids: _GLOBAL_DEFINE["PORTAL_DEFINE"]["Grid"]["BindForms"]["Forms"], size: {w: _GLOBAL_DEFINE["GRID_FORMS_SIZE"][0], h: _GLOBAL_DEFINE["GRID_FORMS_SIZE"][1]}};
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getCallfun(isTree:Boolean):Object
{
	return isTree ? getTreeCallfunction : getGridCallfunction;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//取得参数
public function getParams(isTree:Boolean, isNew:Boolean):Object
{
	var result:Object={};
	var params:Object=_PORTAL_DEFINE[isTree ? "Tree" : "Grid"]["BindForms"]["Params"];
	var paramValue:String="";
	var paramName:String="";
	var k:String="";
	if (isTree)
	{
		for (k in params)
		{
			paramName=params[k];
			if (StringUtils.equal("TREE.ID", paramName))
				paramValue=isNew ? RUNTIME_GLOBAL.getServerUniqueID() : portalGridDatas.SELECTED_TREE_ID;
			else if (StringUtils.equal("TREE.PID", paramName))
				paramValue=(isNew || portalGridOptions.isTreeViewAsGrid) ? _parentItem.id : Tree(portalGridControls.tree).getParentItem(portalGridControls.tree.selectedItem).id;
			else
				paramValue=paramName;
			result[k]=paramValue;
		}
	}
	else
	{
		for (k in params)
		{
			if (StringUtils.equal("TREE.ID", params[k]))
				paramValue=portalGridDatas.SELECTED_TREE_ID;
			else if (StringUtils.equal("GRID.ID", params[k]))
				paramValue=(isNew || portalGridControls.grid.selectedItem == null) ? RUNTIME_GLOBAL.getServerUniqueID() : portalGridControls.grid.selectedItem["__key__"];
			else
				paramValue=paramName;
			result[k]=paramValue;
		}
	}
	return result;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getTreeCallfunction(data:Object):void
{
	var isNew:Boolean=data.isNew;
	var rowRecord:Object=data.data;

	if (portalGridOptions.isTreeSupportCheckBox)
		rowRecord.selected=1;

	if (!isNew) //修改记录
	{

		var selectRecord:Object=portalGridControls.tree.selectedItem;
		if (portalGridOptions.treeFormShowType != 0)
		{
			if (portalGridDatas._treeDataList4Search.length == 0)
				treeDataToList(portalGridDatas.treeData.source, portalGridDatas._treeDataList4Search);

			for (var i:int=0; i < portalGridDatas._treeDataList4Search.length; i++)
			{
				if (rowRecord.id == portalGridDatas._treeDataList4Search[i].id)
				{
					selectRecord=portalGridDatas._treeDataList4Search[i];
					break;
				}
			}
		}
		if (selectRecord)
		{
			selectRecord.label=rowRecord.label;
			portalGridDatas.treeData.refresh();
			portalGridControls.tree.invalidateList();
		}

	}
	else
	{
		if (!_parentItem.children)
			_parentItem.children=[];
		_parentItem.children.push(rowRecord);
		if (!portalGridOptions.isTreeViewAsGrid)
			Tree(portalGridControls.tree).expandItem(_parentItem, true);
		portalGridControls.tree.selectedItem=rowRecord;
		if (!portalGridOptions.isTreeViewAsGrid)
			Tree(portalGridControls.tree).firstVisibleItem=rowRecord;
		eventTreeOnSelectChange(null);
		if (portalGridOptions.treeFormShowType == 2)
			FlexGlobals.topLevelApplication.resetCurrentPortalEditFormInfo(rowRecord.id);
		portalGridDatas._treeDataList4Search=[];
	}

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getGridCallfunction(data:Object):void
{
	/*{ isTree: _params.isTree, isNew: _params.isNew, data: saveResult["portalData"]}*/
	var isNew:Boolean=data.isNew;
	var rowRecord:Object=data.data;
	if (!isNew) //修改记录
	{
		var focusedRecord:Object=portalGridControls.grid.selectedItem;
		if (portalGridOptions.gridFormShowType != 0)
		{
			for (var i:int=0; i < portalGridDatas._gridData.length; i++)
			{
				var item:Object=portalGridDatas._gridData._gridData.getItemAt(i);
				if (item.__key__ == rowRecord.__key__)
				{
					focusedRecord=item;
					break;
				}
			}
		}
		if (focusedRecord != null)
		{
			for (var k:String in portalGridControls.gridColumnFields)
			{
				if (portalGridControls.gridColumnFields[k] in rowRecord)
					focusedRecord[portalGridControls.gridColumnFields[k]]=rowRecord[portalGridControls.gridColumnFields[k]];
				else
					focusedRecord[portalGridControls.gridColumnFields[k]]="";
			}
			portalGridControls.grid.selectedItem=focusedRecord;
		}
		portalGridDatas.gridData.refresh();
		portalGridControls.grid.validateNow();
		hookGridSelectedChange(null);
	}
	else
	{
		if (portalGridControls.pagination != null)
			goPage(portalGridControls.pagination.recordPrePage, 65535);
		else
			portalGridDatas.gridData.addItem(rowRecord);
		for (var i:int=portalGridDatas.gridData.length - 1; i >= 0; i--)
		{
			if (StringUtils.equal(portalGridDatas.gridData.getItemAt(i)["__key__"], rowRecord["__key__"]))
			{
				portalGridControls.grid.selectedIndex=i;
				if (portalGridOptions.gridFormShowType == 2)
					FlexGlobals.topLevelApplication.resetCurrentPortalEditFormInfo(rowRecord["__key__"]);
				hookGridSelectedChange(null);
				break;
			}
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getPortalID():String
{
	return portalGridOptions.portalID;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//刷新Tree
private function treeDataToList(source:Array, target:Array):void
{
	for (var i:int=0; i < source.length; i++)
	{
		target.push(source[i]);
		if (source[i].children)
			treeDataToList(source[i].children, target);
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private var _tag:String="";

public function get tag():String
{
	return this._tag;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function set tag(value:String):void
{
	this._tag=value;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public function get isCommonSearch():Boolean
{
	return portalGridOptions.isCommonSearch;
}
//---------------------------------------------------------------------------------
