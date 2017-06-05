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

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//得到删除提示信息
private function GetDeleteConfirmStr(isGrid:Boolean, datas:Array):String
{
	var text:String=getPortalResourceDefine(isGrid ? "GRID_OPERATION_DELETE" : "TREE_OPERATION_DELETE", "Text", "确定要删除 {0} ?");
	if (isGrid)
	{
		if (datas.length != 1)
			return "确定要删除选择的记录？";
		else
		{
			var data:Object=datas[i];
			for (var i:int=0; i < portalGridControls.grid.columnCount; i++)
			{
				var column:DataGridColumn=portalGridControls.grid.columns[i] as DataGridColumn;
				var cellValue:Object=data[column.dataField];
				if (cellValue is String && cellValue.indexOf("<as>") != -1)
					cellValue=StringUtils.between(StringUtils.between(String(cellValue), "<as>", "</as>"), ">", "<");
				else
					cellValue=StringUtils.str2HTML(String(cellValue));
				cellValue="<span fontWeight=\"bold\" color=\"#CC0000\">" + cellValue + "</span>";
				text=text.replace("{" + (portalGridOptions.isGridIncludeCheckBox ? i - 1 : i) + "}", cellValue);
			}
		}
	}
	else
	{
		text=text.replace("{0}", cellValue="<span fontWeight=\"bold\" color=\"#CC0000\">" + StringUtils.str2HTML(datas[0].label) + "</span>");
	}
	return text;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////
//获取目录节点深度
private function getNodeLevel(tree:Tree, item:Object):int
{
	var result:int=0;
	item=tree.getParentItem(item);
	while (item != null)
	{
		result++;
		item=tree.getParentItem(item);
	}
	return result;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//递归打开目录节点
private function fullExpandTree(item:Object):void
{
	if (!portalGridOptions.isTreeViewAsGrid)
	{
		Tree(portalGridControls.tree).expandItem(item, true);
		if (item.children)
		{
			for (var i:int=0; i < item.children.length; i++)
				fullExpandTree(item.children[i]);
		}
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
private function columnStyle2Array(str:String):Object
{
	var result:Object={};
	if (!StringUtils.isEmpty(str))
	{
		var list:Array=str.split(",");
		for (var i:int=0; i < list.length; i++)
		{
			var styleStr:String=list[i];
			if (!StringUtils.isEmpty(styleStr))
				result[StringUtils.before(styleStr, ":")]=StringUtils.replace(StringUtils.after(styleStr, ":").replace("px", ""), "'", "");
		}
	}
	return result;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//获取表单窗体的标题
public function getFormCaption(isTree:Boolean, isCaption:Boolean):String
{
	return getPortalResourceDefine(isTree ? "TREE_OPERATION_DIALOG" : "GRID_OPERATION_DIALOG", "Text");
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
