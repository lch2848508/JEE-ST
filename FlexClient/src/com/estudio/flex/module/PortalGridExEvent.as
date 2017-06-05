import com.estudio.flex.module.PortalGridEx;
import com.estudio.flex.module.component.PortalGridExControl;
import com.estudio.flex.module.component.PortalGridExFileManager;
import com.estudio.flex.module.component.PortalGridExForm;
import com.estudio.flex.module.component.PortalGridExGISMap;
import com.estudio.flex.module.component.PortalGridExGrid;
import com.estudio.flex.module.component.PortalGridExPictureList;
import com.estudio.flex.module.component.PortalGridExProperty;
import com.estudio.flex.module.component.PortalGridExTree;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.IFrameUtils;
import com.estudio.flex.utils.JSFunUtils;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;

import mx.core.FlexGlobals;

private var controlRelationOtherInfo:Object={};

////////////////////////////////////////////////////////////////////////////////////
private function event4ControlSelectedChange(controlName:String):void
{
	var controlRelation:Object=controlRelationOtherInfo[controlName];
	if (!controlRelation)
	{
		var childControls:Array=[];
		getAllChildControl(controlName, childControls);

		var otherRelationControls:Array=[];
		getOtherRelationControl(childControls, otherRelationControls);

		controlRelation={childControls: childControls, otherRelationControls: otherRelationControls};
		controlRelationOtherInfo[controlName]=controlRelation;
	}
//	trace(JSON.stringify(controlRelation.childControls))
	if (controlRelation.childControls.length)
	{
		for (var i:int=0; i < controlRelation.childControls.length; i++)
		{
			var childControl:PortalGridExControl=portalControlName2ControlInstance[controlRelation.childControls[i]];
			if (childControl is PortalGridExGrid)
				PortalGridExGrid(childControl).saveToServer();
		}

		var params:Object={};
		for (var i:int=0; i < controlRelation.childControls.length; i++)
		{
			if (portalControlName2ControlInstance[controlRelation.childControls[i]] is PortalGridExGrid)
				params=ObjectUtils.mergeParams(params, portalControlName2ControlInstance[controlRelation.childControls[i]].getFilterParams());
		}
		for (var i:int=0; i < controlRelation.otherRelationControls.length; i++)
			params=ObjectUtils.mergeParams(params, portalControlName2ControlInstance[controlRelation.otherRelationControls[i]].getRelationFieldValues());
		params.o="getPortalExControlRecords";
		params.controls=controlRelation.childControls.join(",");
		params.portalId=getPortalID();
		var data:Object=JSFunUtils.JSFun("getPortalExControlRecords", JSON.stringify(params));
		if (data && data.r)
		{
			for (var i:int=0; i < controlRelation.childControls.length; i++)
			{
				var childControl:PortalGridExControl=portalControlName2ControlInstance[controlRelation.childControls[i]];
				if (childControl is PortalGridExGrid)
					PortalGridExGrid(childControl).initData(data[controlRelation.childControls[i]]);
				else if (childControl is PortalGridExTree)
					PortalGridExTree(childControl).initData(data[controlRelation.childControls[i]]);
			}
		}
	}
	initFormControlReqParams(controlName);
	initJSONContentRecordId(controlName);
}

///////////////////////////////////////////////////////////////////////////////////
private function getOtherRelationControl(childControls:Array, otherControls:Array):void
{
	for (var i:int=0; i < childControls.length; i++)
	{
		var childControlName:String=childControls[i];
		var childControl:PortalGridExControl=portalControlName2ControlInstance[childControlName];
		for (var j:int=0; j < childControl.parentControlParams.length; j++)
		{
			var parentControlName:String=childControl.parentControlParams[j].control;
			if (ArrayUtils.indexOf(childControls, parentControlName) == -1 && ArrayUtils.indexOf(otherControls, parentControlName) == -1)
				otherControls.push(parentControlName);
		}
	}
}

private function getPortalExControlParentParams(control:PortalGridExControl, isChild:Boolean=true):Object
{
	var result:Object={};
	if (control is PortalGridExTree && !isChild)
	{
		var pRecord:Object=PortalGridExTree(control).getParentItem();
		for (var j:int=0; j < control.parentControlParams.length; j++)
		{
			var parentControlName:String=control.parentControlParams[j].control;
			var parentControlFieldName:String=control.parentControlParams[j].field;
			if (StringUtils.equal(parentControlName, control.controlName))
				result[parentControlName + "$" + parentControlFieldName]=pRecord[parentControlFieldName];
			else
				result[parentControlName + "$" + parentControlFieldName]=portalControlName2ControlInstance[parentControlName].getRecordFieldValue(parentControlFieldName, false, false);
		}
	}
	else
	{
		var isGridControl:Boolean=control is PortalGridExGrid;
		for (var j:int=0; j < control.parentControlParams.length; j++)
		{
			var parentControlName:String=control.parentControlParams[j].control;
			if (isGridControl && StringUtils.equal(parentControlName, control.controlName))
				continue;
			var parentControlFieldName:String=control.parentControlParams[j].field;
			result[parentControlName + "$" + parentControlFieldName]=portalControlName2ControlInstance[parentControlName].getRecordFieldValue(parentControlFieldName, false, false);
		}
	}
	return result;
}

///////////////////////////////////////////////////////////////////////////////////
//获取所有的子控件
private function getAllChildControl(controlName:String, childControls:Array):void
{
	var control:PortalGridExControl=portalControlName2ControlInstance[controlName];
	if (control != null)
	{
		for (var i:int=0; i < control.childControls.length; i++)
		{
			var childControlName:String=control.childControls[i];
			if (StringUtils.equal(childControlName, controlName))
				continue;
			if (ArrayUtils.indexOf(childControls, childControlName) == -1)
			{
				childControls.push(childControlName);
				getAllChildControl(childControlName, childControls);
			}
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////
public function executeToolbarItemClickFunction(params:Object):void
{
	var controlName:String=params.controlName;
	var funName:String=params.id;
	var index:int=params.index;
	if (StringUtils.isEmpty(controlName))
	{
		IFrameUtils.execute(getIFrameID(), funName, {index: index});
	}
	else
	{
		var portalControl:PortalGridExControl=this.portalControlName2ControlInstance[controlName];
		switch (funName.toLowerCase())
		{
			case "new":
				appendRecord(portalControl);
				break;
			case "newchild":
				appendRecord(portalControl, true);
				break;
			case "edit":
				if (portalControl.IsSelectedItemSupportOperation("edit"))
				{
					if (readonly)
						viewRecord(portalControl);
					else
						editRecord(portalControl);
				}
				break;
			case "viewform":
				if (portalControl.IsSelectedItemSupportOperation("edit"))
					viewRecord(portalControl);
				break;
			case "delete":
				if (portalControl.IsSelectedItemSupportOperation("delete"))
					deleteRecord(portalControl);
				break;
			case "moveup":
				if (portalControl.IsSelectedItemSupportOperation("moveup"))
					moveupRecord(portalControl);
				break;
			case "movedown":
				if (portalControl.IsSelectedItemSupportOperation("movedown"))
					movedownRecord(portalControl);
				break;
			case "refresh":
				refreshRecord(portalControl);
				break;
			case "columnishref":
				executeHerfFunction(params.href);
				break;
			default:
				IFrameUtils.execute(getIFrameID(), funName, {index: index});
				break;
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////
private function getBindFormParams(control:PortalGridExControl, isNew:Boolean, isChild:Boolean):Object
{
	var result:Object={};
	var bindForms:Object=control.bindForms;
	result.formIds=bindForms.Forms;
	for (var k:String in bindForms.Params)
	{
		var v:String=bindForms.Params[k];
		var controlName:String=StringUtils.before(v, ".");
		var fieldName:String=StringUtils.after(v, ".");
		if (!StringUtils.isEmpty(controlName) && !StringUtils.isEmpty(fieldName))
		{
			if (controlName == "REQ")
			{
				paramValue=portalParams[fieldName];
			}
			else
			{
				var tempIsNew:Boolean=isNew;
				if (!StringUtils.equal(control.controlName, controlName))
					tempIsNew=false;
				var paramValue:String=portalControlName2ControlInstance[controlName].getRecordFieldValue(fieldName, tempIsNew, isChild);
			}
			if (!StringUtils.isEmpty(paramValue))
			{
				result[k]=paramValue;
				result.isExistValidParams=true;
			}
		}
	}
	return result;
}

////////////////////////////////////////////////////////////////////////////////////
//增加记录
private function appendRecord(portalControl:PortalGridExControl, isChild:Boolean=false):void
{
	if (portalControl.isInnerEditor())
	{

	}
	else
	{
		if (portalControl is PortalGridExTree && PortalGridExTree(portalControl).isRootNodeSelected())
			isChild=true;
		var formParams:Object=getBindFormParams(portalControl, true, isChild);
		var extParams:Object={callfrom: "PortalGridEx", controlName: portalControl.controlName};
		extParams=ObjectUtils.mergeParams(extParams, getPortalExControlParentParams(portalControl, isChild), portalParams);
		formParams=ObjectUtils.mergeParams(formParams, extParams);
		formParams.isNew=true;
		formParams.readonly=false;
		formParams.portalId=getPortalID();
		formParams.isChild=isChild;
		formParams.formCaption=portalControl["getWindowCaption"](true);
		FlexGlobals.topLevelApplication.editPortalItemEx(this, formParams, portalControl.getShowFormType(), false);
	}
}

////////////////////////////////////////////////////////////////////////////////////
//编辑记录
public function editRecord(portalControl:PortalGridExControl):void
{
	if (portalControl.isInnerEditor())
	{

	}
	else
	{
		openPortalControlBindForm(portalControl, false);
	}
}

private function openPortalControlBindForm(portalControl:PortalGridExControl, isReadonly:Boolean):void
{
	var formParams:Object=getBindFormParams(portalControl, false, false);
	var extParams:Object={callfrom: "PortalGridEx", controlName: portalControl.controlName};
	extParams=ObjectUtils.mergeParams(extParams, getPortalExControlParentParams(portalControl, false), portalParams);
	formParams=ObjectUtils.mergeParams(formParams, extParams);
	formParams.isNew=false;
	formParams.readonly=readonly;
	formParams.portalId=getPortalID();
	formParams.isChild=false;
	formParams.formCaption=portalControl["getWindowCaption"](false);
	FlexGlobals.topLevelApplication.editPortalItemEx(this, formParams, portalControl.getShowFormType(), isReadonly);
}

////////////////////////////////////////////////////////////////////////////////////
//编辑记录
public function viewRecord(portalControl:PortalGridExControl):void
{
	if (!portalControl.isInnerEditor())
		openPortalControlBindForm(portalControl, true);
}

///////////////////////////////////////////////////////////////////////////////////
//删除记录
private function deleteRecord(portalControl:PortalGridExControl):void
{
	var ids:String=portalControl.getSelectedKeys();
	AlertUtils.confirm(portalControl.getConformMsg(), function():void
	{
		var params:Object={controlName: portalControl.controlName, ids: ids, portalId: getPortalID(), o: "deletePortalExControlRecord"};
		if (JSFunUtils.JSFun("deletePortalExControlRecord", params))
		{
			portalControl.deleteSelectedItems();
			event4ControlSelectedChange(portalControl.controlName);
			var delIds:Array=ids.split(",");
			for (var i:int=0; i < delIds.length; i++)
				FlexGlobals.topLevelApplication.closeCurrentPortalEditFormInfo(this, delIds[i]);
		}
	});
}

//////////////////////////////////////////////////////////////////////////////////
//上移记录
private function moveupRecord(portalControl:PortalGridExControl):void
{
	var exchangeRecords:Array=portalControl.getExchangeRecordKeys(true);
	var params:Object={controlName: portalControl.controlName, ids: exchangeRecords.join(","), portalId: getPortalID(), o: "exchangePortalExControlRecord"};
	if (JSFunUtils.JSFun("exchangePortalExControlRecord", params))
	{
		portalControl.exchangeRecordIndex(true);
	}
}

//////////////////////////////////////////////////////////////////////////////////
//下移记录
private function movedownRecord(portalControl:PortalGridExControl):void
{
	var exchangeRecords:Array=portalControl.getExchangeRecordKeys(false);
	var params:Object={controlName: portalControl.controlName, ids: exchangeRecords.join(","), portalId: getPortalID(), o: "exchangePortalExControlRecord"};
	if (JSFunUtils.JSFun("exchangePortalExControlRecord", params))
	{
		portalControl.exchangeRecordIndex(false);
	}
}

//////////////////////////////////////////////////////////////////////////////////
//刷新记录
private function refreshRecord(portalControl:PortalGridExControl):void
{
	FlexGlobals.topLevelApplication.closeCurrentPortalEditFormInfo(this, null);
	portalControl.refreshData();
}

///////////////////////////////////////////////////////////////////////////////////
public function callback4GetBindFormParams(controlName:String, isNew:Boolean, record:Object):Object
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	var result:Object={};
	var bindForms:Object=control.bindForms;
	result.formIds=bindForms.Forms;
	for (var k:String in bindForms.Params)
	{
		var v:String=bindForms.Params[k];
		var paramControlName:String=StringUtils.before(v, ".");
		var fieldName:String=StringUtils.after(v, ".");
		if (!StringUtils.isEmpty(controlName) && !StringUtils.isEmpty(fieldName) && StringUtils.equal(controlName, paramControlName))
			result[k]=record[k];
	}
	return result;

}

////////////////////////////////////////////////////////////////////////////////////////
public function callback4FormSave(controlName:String, isNew:Boolean, isChild:Boolean, record:Object):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	control.mergeRecord(isNew, isChild, record);
	initFormControlReqParams(controlName);
}

/////////////////////////////////////////////////////////////////////////////////////////
public function loadPortalGridRecords(controlName:String, numPerPage:int, page:int, filterParams:Object):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	var controlParentParams:Object=getPortalExControlParentParams(control as PortalGridExControl);
	var params:Object=ObjectUtils.mergeParams(filterParams, controlParentParams, portalParams);
	params.o="getPortalExControlRecords";
	params.controls=controlName;
	params.portalId=getPortalID();
	params.p=page;
	params.r=numPerPage;
	var data:Object=JSFunUtils.JSFun("getPortalExControlRecords", JSON.stringify(params));
	if (data && data.r)
	{
		PortalGridExGrid(control).initData(data[controlName]);
		PortalGridExGrid(control).advGrid.callLater(function():void
		{
			event4ControlSelectedChange(controlName);
		});
	}
}

/////////////////////////////////////////////////////////////////////////////////////////
public function loadPortalTreeRecords(controlName:String):void
{
	var control:Object=this.portalControlName2ControlInstance[controlName];
	var controlParentParams:Object=getPortalExControlParentParams(control as PortalGridExControl);
	var params:Object=controlParentParams;
	params.o="getPortalExControlRecords";
	params.controls=controlName;
	params.portalId=getPortalID();
	params[controlName + "$__key__"]=control.getSelectedItem().__key__;
	params=ObjectUtils.mergeParams(params, portalParams);
	var data:Object=JSFunUtils.JSFun("getPortalExControlRecords", JSON.stringify(params));
	if (data.r)
	{
		control.initData(data[controlName]);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////
public function initFormControlReqParams(controlName:String=null):void
{
	for (var k:String in portalControlName2ControlInstance)
	{
		var control:Object=portalControlName2ControlInstance[k];
		if (control is PortalGridExForm && (StringUtils.isEmpty(controlName) || isBindFormParent(control as PortalGridExControl, controlName)))
		{
			var formParams:Object=getBindFormParams(control as PortalGridExControl, false, false);
			if (controlName == null && !formParams.isExistValidParams)
				continue;
			PortalGridExForm(control).initReqParams(formParams);
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////
public function initJSONContentRecordId(controlName:String=null):void
{
	if (controlName == null)
	{
		for (var k:String in portalControlName2ControlInstance)
		{
			var control:Object=portalControlName2ControlInstance[k];
			if (control is PortalGridExProperty || control is PortalGridExPictureList || control is PortalGridExFileManager)
			{
				if (!StringUtils.isEmpty(control.parentControlName))
				{
					var item:Object=getControlSelectedItem(control.parentControlName);
					control.recordId=control.recordPrefix + (item != null ? item.__key__ : "");
					control.enabled=item != null;
				}
			}
			else if (control is PortalGridExGISMap)
			{
				PortalGridExGISMap(control).loadGeometry();
			}
		}
	}
	else
	{
		var item:Object=getControlSelectedItem(controlName);
		for (var k:String in portalControlName2ControlInstance)
		{
			var control:Object=portalControlName2ControlInstance[k];
			if (control is PortalGridExProperty || control is PortalGridExPictureList || control is PortalGridExFileManager || control is PortalGridExGISMap)
			{
				if (StringUtils.equal(controlName, control.parentControlName))
				{
					if (control is PortalGridExGISMap)
					{
						PortalGridExGISMap(control).loadGeometry();
					}
					else
					{
						control.recordId=control.recordPrefix + (item != null ? item.__key__ : "");
						control.enabled=item != null;
					}
				}
			}
		}
	}
}


/////////////////////////////////////////////////////////////////////////////////////////
private function isBindFormParent(control:PortalGridExControl, controlName:String):Boolean
{
	var bindForms:Object=control.bindForms;
	for (var k:String in bindForms.Params)
	{
		var v:String=bindForms.Params[k];
		var pName:String=StringUtils.before(v, ".");
		if (StringUtils.equal(controlName, pName))
			return true;
	}
	return false;
}

///////////////////////////////////////////////////////////////////////////////////////////
private function executeHerfFunction(href:String):void
{
	if (StringUtils.startWith(href, "event:"))
		href=StringUtils.after(href, "event:");
	var funName:String=StringUtils.trim(StringUtils.before(href, "("));
	var params:Array=StringUtils.between(href, "(", ")").split("|");
	if (funName == "downloadAttachment") //下载附件
	{
		var contentId:int=Convert.str2int(params[3], 0);
		var url:String="00000000" + int(contentId / 10000);
		url=url.substr(url.length - 8);
		url=JSFunUtils.JSFun("getAttachmentBasePath", {}) + url + "/" + params[4];
		FlexGlobals.topLevelApplication.viewAttachmentWindow(params[0], params[1], params[2], url);
	}
	else
	{
		IFrameUtils.execute(getIFrameID(), funName, params);
	}
}
///////////////////////////////////////////////////////////////////////////////////////////
