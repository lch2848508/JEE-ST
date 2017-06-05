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


public function funGridDelete(isDeleteAllCheck:Boolean=true):void
{
	var portalInstance:InterfacePortalGrid=this;
	var selectDatas:Array=getSelectedGridDatas(isDeleteAllCheck);
	var ids:Array=selectDatas[0] as Array;
	var datas:Array=selectDatas[1] as Array;
	if (ids.length == 0)
	{
		AlertUtils.alert(getPortalResourceDefine("GRID_NO_SELECT", "Text") + ", 不能执行删除命令!");
	}
	else
	{
		AlertUtils.confirm(GetDeleteConfirmStr(true, datas), function():void
		{
			var data:Object=serverDeleteGrid(ids);
			if (data && data["r"])
			{
				if (portalGridOptions.gridFormShowType == 2)
					for (var i:int=0; i < ids.length; i++)
						FlexGlobals.topLevelApplication.closeCurrentPortalEditFormInfo(portalInstance, ids[i]);

				if (portalGridControls.pagination == null)
				{
					for (var i:int=0; i < datas.length; i++)
						portalGridDatas.gridData.removeItemAt(portalGridDatas.gridData.getItemIndex(datas[i]));
				}
				else
				{
					fillGridByData(data["data"]);
				}
			}
			else if (data && !StringUtils.isEmpty(data.msg))
			{
				AlertUtils.alert("删除数据失败:" + data.msg);
			}
		});
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//删除树节点
public function funTreeDelete(isDeleteAllCheck:Boolean=true):void
{
	var portalInstance:Object=this;
	if (!portalGridControls.tree.selectedItem)
	{
		AlertUtils.alert("无数据,不能执行删除操作!");
		return;
	}

	if (!portalGridOptions.isTreeViewAsGrid)
	{
		if (treeDataIsRoot(portalGridControls.tree.selectedItem))
		{
			AlertUtils.alert("不能删除当前选择的内容!");
			return;
		}

		if (Tree(portalGridControls.tree).dataDescriptor.hasChildren(portalGridControls.tree.selectedItem))
		{
			AlertUtils.alert("当前选择的内容下面包含子内容，该内容不允许被删除！");
			return;
		}
	}

	var selectTreeDatas:Array=getSelectedTreeDatas(isDeleteAllCheck);
	var ids:Array=selectTreeDatas[0];
	var datas:Array=selectTreeDatas[1];

	AlertUtils.confirm(GetDeleteConfirmStr(false, datas), function():void
	{
		var json:Object=serverDeleteTree(ids);
		if (json && json.r)
		{
			if (portalGridOptions.treeFormShowType == 2)
				for (var i:int=0; i < ids.length; i++)
					FlexGlobals.topLevelApplication.closeCurrentPortalEditFormInfo(portalInstance, ids[i]);

			var data:Object=datas[0];
			var parentNode:Object=portalGridOptions.isTreeViewAsGrid ? portalGridDatas.TreeViewRootNode : Tree(portalGridControls.tree).getParentItem(data);
			var children:Array=parentNode["children"];
			var index:int=children.indexOf(portalGridControls.tree.selectedItem);
			children.splice(children.indexOf(data), 1);
			if (children.length == 0 && !portalGridOptions.isTreeViewAsGrid)
			{
				delete parentNode["children"];
				children=null;
			}
			portalGridControls.tree.selectedItem=(children && children.length != 0) ? children[index < children.length ? index : index - 1] : portalGridOptions.isTreeViewAsGrid ? null : parentNode;
			portalGridControls.tree.invalidateList();
			eventTreeOnSelectChange(null);
		}
		else if (json && json.msg)
		{
			AlertUtils.alert(json.msg);
		}
	});
	//查询数据清除
	portalGridDatas._treeDataList4Search=[];
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
//新增列表项目
public function funGridNew():void
{
	if (portalGridOptions.isCommonSearch)
		return;
	FlexGlobals.topLevelApplication.editPortalItem(this, false, true, readonly, portalGridOptions.gridFormShowType);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//编辑列表项目
public function funGridEdit():void
{
	if (portalGridControls.grid.selectedItem != null)
		FlexGlobals.topLevelApplication.editPortalItem(this, false, false, readonly || portalGridOptions.isCommonSearch, portalGridOptions.gridFormShowType);
}



///////////////////////////////////////////////////////////////////////////////////////////////////////////
//编辑目录树节点
public function funTreeEdit():void
{
	var canEdit:Boolean=!portalGridOptions.isCommonSearch && (portalGridOptions.isTreeViewAsGrid && portalGridControls.tree.selectedItem != null) || (!portalGridOptions.isTreeViewAsGrid && getNodeLevel(Tree(portalGridControls.tree), portalGridControls.tree.selectedItem) != 0);
	if (canEdit)
	{
		if (portalGridOptions.isTreeViewAsGrid)
			_parentItem=portalGridDatas.TreeViewRootNode;
		FlexGlobals.topLevelApplication.editPortalItem(this, true, false, readonly || portalGridOptions.isCommonSearch, portalGridOptions.treeFormShowType);
	}
	else
	{
		AlertUtils.alert(portalGridOptions.isTreeViewAsGrid ? "无数据,不能编辑！" : "不能编辑当前选择的内容!");
	}
	//清除查询缓存
	portalGridDatas._treeDataList4Search=[];
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//交换Grid顺序
public function funGridExchange(isGridUp:Boolean):void
{
	var data:Object=portalGridControls.grid.selectedItem;
	if (data != null)
	{
		var index1:int=portalGridDatas.gridData.getItemIndex(data);
		var index2:int=-1;
		if (isGridUp && index1 != 0)
			index2=index1 - 1;
		if (!isGridUp && index1 != portalGridDatas.gridData.length - 1)
			index2=index1 + 1;


		if (index2 != -1 && severExchangeGrid(data["__key__"], portalGridDatas.gridData.getItemAt(index2)["__key__"]))
		{
			if (isGridUp)
			{
				portalGridDatas.gridData.addItemAt(portalGridDatas.gridData.removeItemAt(index2), index1);
			}
			else
			{
				portalGridDatas.gridData.addItemAt(portalGridDatas.gridData.removeItemAt(index1), index2);
				portalGridControls.grid.selectedIndex=index1 + 1;
			}
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//交换Tree顺序
public function funTreeExchange(isUp:Boolean):void
{
	var data:Object=portalGridControls.tree.selectedItem;
	if (!data || (!portalGridOptions.isTreeViewAsGrid && treeDataIsRoot(data)))
		return;

	var parent:Object=portalGridOptions.isTreeViewAsGrid ? portalGridDatas.TreeViewRootNode : Tree(portalGridControls.tree).getParentItem(data);
	var children:Array=parent["children"];
	var index1:int=children.indexOf(data);
	var index2:int=isUp ? index1 - 1 : index1 + 1;
	if (index1 >= 0 && index1 < children.length && index2 >= 0 && index2 < children.length)
	{
		var key1:String=children[index1].id;
		var key2:String=children[index2].id;
		if (severExchangeTree(key1, key2))
		{
			if (!portalGridOptions.isTreeViewAsGrid)
			{
				var temp:Object=children[index1];
				children[index1]=children[index2];
				children[index2]=temp;
				portalGridControls.tree.invalidateList();
			}
			else
			{
				if (isUp)
					portalGridDatas.treeData.addItemAt(portalGridDatas.treeData.removeItemAt(index2), index1);
				else
				{
					portalGridDatas.treeData.addItemAt(portalGridDatas.treeData.removeItemAt(index1), index2);
					portalGridControls.tree.selectedIndex=index1 + 1;
				}
			}
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//刷新数据列表
public function funGridRefresh():void
{
	if (portalGridControls.grid != null)
	{
		var pos:Number=portalGridControls.grid.verticalScrollPosition;
		var selectedItem:Object=portalGridControls.grid.selectedItem;
		goPage(portalGridControls.pagination == null ? 2500 : portalGridControls.pagination.recordPrePage, 1);
		if (selectedItem)
		{
			for (var i:int=0; i < portalGridDatas.gridData.length; i++)
			{
				if (StringUtils.equal(selectedItem.__key__, portalGridDatas.gridData.getItemAt(i).__key__))
				{
					portalGridControls.grid.selectedItem=portalGridDatas.gridData.getItemAt(i);
					break;
				}
			}
		}
		hookGridSelectedChange(null);
		portalGridControls.grid.verticalScrollPosition=pos;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function setGridCellsValue(keyValues:Array, fieldname:String, value:*, refresh:Boolean=true):void
{
	for (var i:int=0; i < keyValues.length; i++)
	{
		setGridCellValue(keyValues[i], fieldname, value, false);
	}
	if (refresh)
		portalGridControls.grid.invalidateList();

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function selectGridItem(keyValue:String, refresh:Boolean=true):void
{
	var arr:Array=portalGridDatas.gridData.source;
	for (var i:int=0; i < arr.length; i++)
	{
		if (StringUtils.equal(keyValue, arr[i].__key__))
		{
			portalGridControls.grid.selectedIndex=i;
			if (refresh)
				portalGridControls.grid.invalidateList();
			break;
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//批量设置多个单元格的值
public function batchSetGridCellsValue(keyValues:Array, fieldnames:*, records:Array, refresh:Boolean=true):void
{
	var fields:Array=(fieldnames is Array) ? fieldnames : [fieldnames];
	var arr:Array=portalGridDatas.gridData.source;
	var gridIDS:Array=[];
	var i:int=0;
	for (i=0; i < arr.length; i++)
	{
		gridIDS.push(arr[i].__key__);
	}
	for (i=0; i < keyValues.length; i++)
	{
		var index:int=gridIDS.indexOf(keyValues[i]);
		if (index != -1)
		{
			for (var m:int=0; m < fields.length; m++)
			{
				var fieldName:String=fields[m];
				arr[index][fieldName]=records[i][fieldName];
			}
		}
	}

	if (refresh)
		portalGridControls.grid.invalidateList();

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function refreshGridSelectedItem():void
{
	if (portalGridControls.grid && portalGridControls.grid.selectedItem)
	{
		var params:Object={key: portalGridControls.grid.selectedItem.__key__, portal_id: portalGridOptions.portalID};
		if (portalGridControls.tree)
		{
			params.p_id=portalGridDatas.SELECTED_TREE_ID;
			if (portalGridOptions.isTreeSupportCheckBox)
			{
				var ids:Array=[];
				treeGetSelectedIDS(portalGridDatas.treeData.source, ids);
				params["p_id_s"]=ids.join(",");
			}
		}
		var record:Object=JSFunUtils.JSFun("refreshGridSelectedItem", params);
		var k:String="";
		for (k in portalGridControls.grid.selectedItem)
		{
			if (k.indexOf("__") != 0 && k != "mx_internal_uid")
			{
				if (k in record)
					portalGridControls.grid.selectedItem[k]=record[k];
				else
					portalGridControls.grid.selectedItem[k]=null;
			}
		}

		for (k in record)
		{
			if (!(k in portalGridControls.grid.selectedItem))
			{
				portalGridControls.grid.selectedItem[k]=record[k]
			}
		}

		portalGridControls.grid.invalidateList();
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//刷新内容
public function refresh():void
{
	if (portalGridControls.tree && portalGridControls.tree.visible)
		funTreeRefresh();
	else if (portalGridControls.grid && portalGridControls.grid.visible)
		funGridRefresh();
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//更新界面
public function update():void
{
	if (portalGridControls.grid)
		portalGridControls.grid.invalidateList();
	if (portalGridControls.tree)
		portalGridControls.tree.invalidateList();
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//设置Grid单元格值
public function setGridCellValue(keyValue:String, fieldname:String, value:*, refresh:Boolean=true):void
{
	var arr:Array=portalGridDatas.gridData.source;
	for (var i:int=0; i < arr.length; i++)
	{
		if (StringUtils.equal(keyValue, arr[i].__key__))
		{
			arr[i][fieldname]=value;
			if (refresh)
				portalGridControls.grid.invalidateList();
			break;
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//查询数据
public function funTreeSearch():void
{
	var x:int=FlexGlobals.topLevelApplication.mouseX + 20;
	var y:int=FlexGlobals.topLevelApplication.mouseY + 20;
	FlexGlobals.topLevelApplication.showSearchDialog(callbackFunction4TreeSearch, x, y);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function callbackFunction4TreeSearch(searchStr:String, isDownSearch:Boolean):void
{
	if (portalGridDatas._treeDataList4Search.length == 0)
		treeDataToList(portalGridDatas.treeData.source, portalGridDatas._treeDataList4Search);

	var index:int=portalGridDatas._treeDataList4Search.indexOf(portalGridControls.tree.selectedItem);
	if (index == -1)
		index=isDownSearch ? 0 : portalGridDatas._treeDataList4Search.length - 1;
	if (isDownSearch)
	{
		for (var i:int=index + 1; i < portalGridDatas._treeDataList4Search.length; i++)
		{
			var item:Object=portalGridDatas._treeDataList4Search[i];
			if (isTreeNodeMatch(item, searchStr))
			{
				portalGridControls.tree.selectedItem=item;
				eventTreeOnSelectChange(null);
				hookTreeSelectedChange(null);
				break;
			}
		}
	}
	else
	{
		for (var i:int=index - 1; i >= 0; i--)
		{
			var item:Object=portalGridDatas._treeDataList4Search[i];
			if (isTreeNodeMatch(item, searchStr))
			{
				portalGridControls.tree.selectedItem=item;
				eventTreeOnSelectChange(null);
				hookTreeSelectedChange(null);
				break;
			}
		}
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

private function isTreeNodeMatch(item:Object, str:String):Boolean
{
	return item.label.indexOf(str) != -1;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//刷新树列表
public function funTreeRefresh():void
{
	if (portalGridControls.tree && portalGridControls.tree.visible)
	{
		var selectedRecord:Object=null;
		var ids:Array=[];
		var selectedID:String=portalGridControls.tree.selectedItem ? portalGridControls.tree.selectedItem.id : null;
		if (portalGridOptions.isTreeSupportCheckBox)
			treeGetSelectedIDS(portalGridDatas.treeData.source, ids);
		else if (portalGridControls.tree.selectedItem)
			ids.push(portalGridControls.tree.selectedItem.id);

		var newDatas:Object=JSFunUtils.JSFun("getPortalGridTreeDatas", {id: portalGridOptions.portalID});
		if (newDatas && newDatas["r"])
		{
			if (portalGridOptions.isTreeViewAsGrid)
			{
				var children:Array=newDatas.records[0].children;
				if (!children)
				{
					children=[];
					newDatas.records[0].children=children;
				}
				portalGridDatas.TreeViewRootNode=newDatas.records[0];
				portalGridDatas.treeData=new ArrayCollection(children);
			}
			else
			{
				portalGridDatas.treeData=new ArrayCollection(newDatas.records);
			}
			portalGridDatas.SELECTED_TREE_ID=null;
			portalGridControls.tree.dataProvider=portalGridDatas.treeData;
			portalGridControls.tree.validateNow();
			var newDataList:Array=[];
			treeDataToList(portalGridDatas.treeData.source, newDataList);
			for (var i:int=0; i < newDataList.length; i++)
			{
				var record:Object=newDataList[i];
				if (ids.indexOf(record.id) != -1)
				{
					if (portalGridOptions.isTreeSupportCheckBox)
					{
						record.selected=true;
						if (record.id == selectedID)
						{
							selectedRecord=record;
						}
					}
					else
					{
						selectedRecord=record;
						break;
					}
				}
			}

			if (selectedRecord == null && newDataList.length != 0)
			{
				selectedRecord=newDataList[0];
			}
			if (selectedRecord)
				portalGridDatas.SELECTED_TREE_ID=selectedRecord.id;

			portalGridControls.tree.callLater(function():void
			{
				if (!portalGridOptions.isTreeViewAsGrid)
					Tree(portalGridControls.tree).expandChildrenOf(portalGridDatas.treeData.getItemAt(0), true);
				portalGridControls.tree.selectedItem=selectedRecord;
				if (portalGridControls.grid != null)
					funGridRefresh();
			});
		}
	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//新增目录树节点
private var _parentItem:Object=null;

public function funTreeNew(isSameLevel:Boolean):void
{
	if (portalGridOptions.isCommonSearch)
		return;
	if (portalGridOptions.isTreeViewAsGrid)
	{
		_parentItem=this.portalGridDatas.TreeViewRootNode;
	}
	else
	{
		_parentItem=null;
		if (_PORTAL_DEFINE.Layout.TreeSingleLevel)
			_parentItem=portalGridDatas.treeData.getItemAt(0);
		else if (isSameLevel)
			_parentItem=Tree(portalGridControls.tree).getParentItem(portalGridControls.tree.selectedItem);
		else
			_parentItem=Tree(portalGridControls.tree).selectedItem;
		if (_parentItem == null)
			_parentItem=portalGridControls.tree.selectedItem;
		Tree(portalGridControls.tree).expandItem(_parentItem, true);
	}
	FlexGlobals.topLevelApplication.editPortalItem(this, true, true, portalGridOptions.readonly, portalGridOptions.treeFormShowType);
	//清除查找缓存
	portalGridDatas._treeDataList4Search=[];
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//布局相关
public function getLayout(name:String):Group
{
	return this.portalGridControls.layoutClass.getBox(name);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getGrid():DataGrid
{
	return portalGridControls.grid;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function getTree():ListBase
{
	return portalGridControls.tree;
}

