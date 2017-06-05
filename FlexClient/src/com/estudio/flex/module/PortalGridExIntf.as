import com.estudio.flex.module.component.PortalGridExControl;
import com.estudio.flex.module.component.PortalGridExGrid;
import com.estudio.flex.module.component.PortalGridExIFrame;
import com.estudio.flex.module.component.PortalGridExSWF;
import com.estudio.flex.module.component.PortalGridExTree;
import com.estudio.flex.utils.ArrayCollectionUtils;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;

import mx.core.UIComponent;

public function getControlSelectedItem(controlName:String):Object
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	return (control is PortalGridExGrid || control is PortalGridExTree) ? control.getSelectedItem() : null;
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_getSelectedItems
public function getControlSelectedItems(controlName:String):Array
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	return (control is PortalGridExGrid || control is PortalGridExTree) ? control.getSelectedItems() : null;
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_append
public function appendControl(controlName:String, isChild:Boolean):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
		this.executeToolbarItemClickFunction({controlName: controlName, id: isChild ? "newchild" : "new", index: -1});
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_edit
public function editControl(controlName:String, isReadonly:Boolean):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
	{
		if (isReadonly)
			this.executeToolbarItemClickFunction({controlName: controlName, id: "viewform", index: -1});
		else
			this.executeToolbarItemClickFunction({controlName: controlName, id: "edit", index: -1});
	}
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_del
public function delControl(controlName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
		this.executeToolbarItemClickFunction({controlName: controlName, id: "delete", index: -1});
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_up
public function upControl(controlName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
		this.executeToolbarItemClickFunction({controlName: controlName, id: "moveup", index: -1});
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_down
public function downControl(controlName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
		this.executeToolbarItemClickFunction({controlName: controlName, id: "movedown", index: -1});
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_refresh
public function refreshControl(controlName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
		this.executeToolbarItemClickFunction({controlName: controlName, id: "refresh", index: -1});
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_getRecords
public function getControlRecords(controlName:String):Array
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	return (control is PortalGridExGrid || control is PortalGridExTree) ? control.getRecords() : null;
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_updateRecord
public function updateControlRecord(controlName:String, record:Object):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
	{
		control.mergeRecord(false, false, record);
		ArrayCollectionUtils.flagRecordModified(record);
	}
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_selectItem
public function selectControlItem(controlName:String, key:String):Object
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is PortalGridExGrid || control is PortalGridExTree)
	{
		control.selectRecordByKey(key);
		return control.getSelectedItem();
	}
	return null;
}

///////////////////////////////////////////////////////////////////////////////
public function callLaterFunction(controlName:String, funName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control is UIComponent)
		UIComponent(control).callLater(function():void
		{
			IFrameUtils.execute(getIFrameID(), funName, {});
		});
}

///////////////////////////////////////////////////////////////////////////////
//Callback_PortalEx_getRootId
public function getControlRootId(controlName:String):String
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	return (control is PortalGridExTree) ? PortalGridExTree(control).getRootId() : null;

}

///////////////////////////////////////////////////////////////////////////////
public function refreshSelectedItem(controlName:String):void
{
	var params:Object={};
	var controls:Array=[];
	var controlNames:Array=[controlName];
	for (var i:int=0; i < controlNames.length; i++)
	{
		var control:PortalGridExControl=this.portalControlName2ControlInstance[controlNames[i]];
		if (control is PortalGridExGrid || control is PortalGridExTree)
		{
			if (control is PortalGridExTree && PortalGridExTree(control).isRootNodeSelected())
				continue;
			if (control is PortalGridExGrid && PortalGridExGrid(control).getSelectedItem().children)
				continue;

			controls.push(controlNames[i]);
			var controlParams:Object={};
			controlParams[control.controlName + "$params"]=JSON.stringify(getPortalExControlParentParams(control, false));
			params=ObjectUtils.mergeParams(controlParams, params);
			params[control.controlName + "$__recordkey__"]=control.getRecordFieldValue("__key__", false, false);
		}
	}
	if (controls.length == 0)
		return;
	params.o="refreshPortalGridExSelectedItem";
	params.controls=controls.join(",");
	params.portalId=getPortalID();
	var result:Object=JSFunUtils.JSFun("refreshPortalGridExSelectedItem", params);
	if (result && result.r)
	{
		for (var i:int=0; i < controls.length; i++)
		{
			var record:Object=result[controls[i]];
			if (!record)
				continue;
			var oldRecord:Object=portalControlName2ControlInstance[controls[i]].getSelectedItem();
			for (var k:String in record)
				oldRecord[k]=record[k];
			portalControlName2ControlInstance[controls[i]].refreshUI();
		}
	}
}

public function firstPage(controlName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control && control is PortalGridExGrid)
		return PortalGridExGrid(control).firstPage();
}

public function lastPage(controlName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control && control is PortalGridExGrid)
		return PortalGridExGrid(control).lastPage();
}

///////////////////////////////////////////////////////////////////////////////
public function executeSWFControlFunction(controlName:String, funName:String, params:Object):Object
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control && control is PortalGridExSWF)
		return PortalGridExSWF(control).executeFunction(funName, params);
	else if (control && control is PortalGridExIFrame)
		return PortalGridExIFrame(control).executeFunction(funName, params);
	return null;
}

//////////////////////////////////////////////////////////////////////////////
//保存到服务器
public function saveToServer(controlName:String):Object
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	if (control && control is PortalGridExGrid)
		return PortalGridExGrid(control).saveToServer();
	return {r: false};
}
///////////////////////////////////////////////////////////////////////////////

