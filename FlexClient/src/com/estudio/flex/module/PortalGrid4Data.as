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

import mx.binding.utils.BindingUtils;
import mx.collections.ArrayCollection;
import mx.collections.ArrayList;
import mx.collections.IList;
import mx.controls.Alert;
import mx.controls.DataGrid;
import mx.controls.DateField;
import mx.controls.PopUpMenuButton;
import mx.controls.Text;
import mx.controls.Tree;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.controls.dataGridClasses.MXDataGridItemRenderer;
import mx.controls.listClasses.ListBase;
import mx.core.Application;
import mx.core.ClassFactory;
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

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//执行服务器端删除函数
private function serverDeleteGrid(ids:Array):Object
{
	var data:Object={id: _GLOBAL_DEFINE["PORTAL_ID"], ids: ids.join(",")};
	if (portalGridControls.pagination != null)
	{
		data["p"]=portalGridControls.pagination.currentPage;
		data["r"]=portalGridControls.pagination.recordPrePage;
		data["p_id"]=portalGridDatas.SELECTED_TREE_ID;
		data["reloaddata"]=true;
		generalFilterCondition(data); //过滤条件
	}
	return JSFunUtils.JSFun("serverDeletePortalGrid", data);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//删除目录树节点
private function serverDeleteTree(ids:Array):Object
{
	var data:Object={id: _GLOBAL_DEFINE["PORTAL_ID"], ids: ids.join(",")};
	return JSFunUtils.JSFun("serverDeletePortalTree", data);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//交换Grid项排列顺序
private function severExchangeGrid(key1:String, key2:String):Boolean
{
	var data:Object={id: _GLOBAL_DEFINE["PORTAL_ID"], id1: key1, id2: key2};
	return JSFunUtils.JSFun("exchangePortalGrid", data) as Boolean;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//交换Tree项排列顺序
private function severExchangeTree(key1:String, key2:String):Boolean
{
	var data:Object={id: _GLOBAL_DEFINE["PORTAL_ID"], id1: key1, id2: key2};
	return JSFunUtils.JSFun("exchangePortalTree", data) as Boolean;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//初始化数据
private function initData():void
{
	//目录树
	if (_PORTAL_DEFINE["Layout"]["TreeView"])
	{
		if (portalGridOptions.isTreeViewAsGrid)
		{
			var children:Array=_GLOBAL_DEFINE["INIT_DATA"]["TREE"][0].children;
			if (!children)
			{
				children=[];
				_GLOBAL_DEFINE["INIT_DATA"]["TREE"][0].children=children;
			}
			portalGridDatas.treeData=new ArrayCollection(children);
			portalGridControls.tree.dataProvider=portalGridDatas.treeData;
			portalGridDatas.TreeViewRootNode=_GLOBAL_DEFINE["INIT_DATA"]["TREE"][0];
			portalGridDatas.SELECTED_TREE_ID=null;
			if (children.length != 0)
				portalGridControls.tree.selectedItem=children[0];
			eventTreeOnSelectChange(null);
			hookTreeSelectedChange(null);
		}
		else
		{
			portalGridDatas.treeData=new ArrayCollection(_GLOBAL_DEFINE["INIT_DATA"]["TREE"]);
			portalGridControls.tree.dataProvider=portalGridDatas.treeData;
			portalGridDatas.SELECTED_TREE_ID=_GLOBAL_DEFINE["INIT_DATA"]["TREEROOTID"];
		}
		hookTreeSelectedChange(null);
	}

	//列表视图
	if (_PORTAL_DEFINE["Layout"]["GridView"] && !portalGridOptions.isTreeViewAsGrid)
		fillGridByData(_GLOBAL_DEFINE["INIT_DATA"]["GRID"]);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getTreeDatas():Array
{
	return portalGridDatas.treeData.toArray();
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getGridDatas():Array
{
	return portalGridDatas.gridData.toArray();
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getTreeSelectedItems():Array
{
	var result:Array=[];
	for (var i:int=0; i < portalGridControls.tree.selectedItems.length; i++)
	{
		var temp:Object=portalGridControls.tree.selectedItems[i];
		result.push({id: temp.id, label: temp.label});
	}
	return result;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getGridSelectedItems():Array
{
	return portalGridOptions.isGridIncludeCheckBox ? getSelectedGridDatas(true)[1] : portalGridControls.grid.selectedItems;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getGridSelectedItem():Object
{
	return portalGridControls.grid.selectedItem;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function geTreeSelectedItem():Object
{
	return portalGridControls.tree.selectedItem
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getSelectedKey(isTree:Boolean, isNew:Boolean):String
{
	var result:String="";
	if (isTree && portalGridControls.tree.selectedItem)
		result=portalGridControls.tree.selectedItem.id;
	else if (!isTree && portalGridControls.grid.selectedItem)
		result=portalGridControls.grid.selectedItem.__key__;
	return result;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//取得选定的节点
public function getSelectedID(isTree:Boolean, isNew:Boolean, defaultValue:String):Object
{
	var result:Object={callfrom: isTree ? "Tree" : "Grid"};
	result.uid=isNew ? defaultValue : isTree ? portalGridControls.tree.selectedItem.id : portalGridControls.grid.selectedItem.__key__;
	if (isTree)
		result.treenodeid=isNew ? _parentItem.id : (portalGridOptions.isTreeViewAsGrid ? _parentItem.id : Tree(portalGridControls.tree).getParentItem(portalGridControls.tree.selectedItem).id);
	else if (portalGridControls.tree)
		result.treenodeid=portalGridControls.tree.selectedItem.id;
	else
		result.treenodeid=-1;

	if (portalGridOptions.isTreeSupportCheckBox && !isTree && portalGridControls.tree != null)
	{
		var ids:Array=[];
		treeGetSelectedIDS(portalGridDatas.treeData.source, ids);
		result["p_id_s"]=ids.join(",");
	}
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//得到选定的Grid数据列表
public function getSelectedGridDatas(isAllChecked:Boolean=true):Array
{
	var ids:Array=[];
	var datas:Array=[];
	var i:int=0;
	if (portalGridControls.grid != null)
	{
		if (portalGridOptions.isGridIncludeCheckBox && isAllChecked)
		{

			for (i=0; i < portalGridDatas.gridData.length; i++)
			{
				var itemData:Object=portalGridDatas.gridData.getItemAt(i);
				if (Convert.object2Boolean(itemData["__chk__"]))
				{
					ids.push(itemData["__key__"]);
					datas.push(itemData)
				}
			}
		}
		else
		{
			if (portalGridControls.grid.selectedItem != null)
			{
				ids.push(portalGridControls.grid.selectedItem["__key__"]);
				datas.push(portalGridControls.grid.selectedItem);
			}
		}
	}
	return [ids, datas];
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//得到选定的Grid数据列表
public function getSelectedTreeDatas(isAllChecked:Boolean=true):Array
{
	var data:Object=portalGridControls.tree.selectedItem;
	return [[data["id"]], [data]];
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//从新填充Grid
private function fillGridByData(data:Object):void
{
	portalGridDatas.gridData=new ArrayCollection(data["rows"]);
	portalGridControls.grid.dataProvider=portalGridDatas.gridData;
	if (portalGridControls.pagination)
		portalGridControls.pagination.updatePages(data["t"], data["tr"], data["p"]);
	if (portalGridControls.headerCheckBox != null)
		portalGridControls.headerCheckBox.selected=false;
	hookGridSelectedChange(null);
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
