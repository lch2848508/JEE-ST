import com.estudio.flex.component.CheckBoxEx;
import com.estudio.flex.component.ComboBoxEx;
import com.estudio.flex.component.DateFieldEx;
import com.estudio.flex.component.FormGrid;
import com.estudio.flex.component.GroupBox;
import com.estudio.flex.component.IconButton;
import com.estudio.flex.component.InputFileUpload;
import com.estudio.flex.component.InputFileUploadSimp;
import com.estudio.flex.component.InputPicture;
import com.estudio.flex.component.TextAreaEx;
import com.estudio.flex.component.TextInputEx;
import com.estudio.flex.component.ToolbarVertline;
import com.estudio.flex.component.mx.RichEditorEx;
import com.estudio.flex.module.EditableControlParams;
import com.estudio.flex.module.FormDataService;
import com.estudio.flex.module.InterfaceEditableControl;
import com.estudio.flex.module.component.FormLine;
import com.estudio.flex.module.component.LookupCombobox;
import com.estudio.flex.utils.AlertUtils;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.ChineseConvert;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.StringUtils;
import com.estudio.flex.utils.UIUtils;
import com.utilities.IconUtility;

import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;

import flexlib.containers.SuperTabNavigator;
import flexlib.scheduling.scheduleClasses.lineRenderer.Line;

import mx.collections.ArrayCollection;
import mx.containers.Canvas;
import mx.containers.TabNavigator;
import mx.controls.DateField;
import mx.controls.PopUpMenuButton;
import mx.controls.RichTextEditor;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.CalendarLayoutChangeEvent;
import mx.events.FlexEvent;
import mx.events.MenuEvent;

import spark.components.BorderContainer;
import spark.components.Button;
import spark.components.CheckBox;
import spark.components.HGroup;
import spark.components.Image;
import spark.components.Label;
import spark.components.NavigatorContent;
import spark.components.TextArea;
import spark.components.TextInput;
import spark.components.supportClasses.SkinnableTextBase;
import spark.events.DropDownEvent;
import spark.events.IndexChangeEvent;
import spark.primitives.Line;

import uk.co.teethgrinder.tr;

private var _combobox2ChildrenCombobox:Object={};

private var _combobox2ParentCombobox:Object={};

[Embed(source="/assets/common/valid.png")] //过滤
[Bindable]
private static var IMG_VALID_PNG:Class;
private var _isFormAutoFill:Boolean=false;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function fillDataValueToControls():void
{
	for (var i:int=0; i < this._DataBindControlsArray.length; i++)
	{
		var c:UIComponent=this._DataBindControlsArray[i];
		this.fillDataValueToControl(c);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//为单个控件填充值
private function fillDataValueToControl(c:UIComponent):void
{
	var intfControl:InterfaceEditableControl=InterfaceEditableControl(c);

	intfControl.unBindEvent(); //暂停事件绑定

	var v:String="";
	var controlType:int=intfControl.controlType;
	if (controlType == EditableControlParams.CONST_INPUTTEXT || //
		controlType == EditableControlParams.CONST_DATE_EX || //
		controlType == EditableControlParams.CONST_CHECKBOX || //
		controlType == EditableControlParams.CONST_MEMO || //
		controlType == EditableControlParams.CONST_RICHEDIT)
	{
		v=intfControl.dataservice.getDataSetValue(intfControl.databaseName, intfControl.fieldName);
		intfControl.setControlValue(v, null, false);
	}
	else if (controlType == EditableControlParams.CONST_COMBOBOX || controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX)
	{
		v=intfControl.dataservice.getDataSetValue(intfControl.databaseName, intfControl.fieldName);
		var extV:String=intfControl.dataservice.getDataSetValue(intfControl.databaseName, intfControl.extFieldName);
		intfControl.setControlValue(v, extV, false);
	}
	else if (controlType == EditableControlParams.CONST_GRID)
	{
		var grid:FormGrid=intfControl as FormGrid;
		grid.initData();
	}
	else if (controlType == EditableControlParams.CONST_FILEUPLOAD)
	{
		v=intfControl.dataservice.getDataSetValue(intfControl.databaseName, intfControl.fieldName);
		InputFileUpload(c).p_id=v;
	}
	else if (controlType == EditableControlParams.CONST_FILEUPLOADSIMP)
	{
		v=intfControl.dataservice.getDataSetValue(intfControl.databaseName, intfControl.fieldName);
		InputFileUploadSimp(c).p_id=v;
	}
	else if (controlType == EditableControlParams.CONST_PICTURE)
	{
		InputPicture(c).loadPicture();
	}

	intfControl.bindEvent(); //恢复事件绑定
}

///////////////////////////////////////////////////////////////////////////////////
//设置控件值
/**
 *
 * @param controlName
 * @param value
 */
public function setControlValue(controlName:String, value:String, extValue:String=null, isSettingFormDataService:Boolean=true):void
{
	//1 判断控件是否存在
	var control:Object=_controlUID2Instance[controlName];
	if (control == null)
	{
		if (!FlexGlobals.topLevelApplication.isRelease)
			AlertUtils.msnMessage("系统", "表单中名称为" + controlName + "的控件不存在!", true);
		return;
	}


	//3 赋值
	var intf:InterfaceEditableControl=InterfaceEditableControl(control);
	intf.unBindEvent();

	var oldValue:String=intf.controlValue;
	intf.setControlValue(value, extValue, isSettingFormDataService);
	if ((intf.controlType == EditableControlParams.CONST_COMBOBOX || intf.controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX) && !StringUtils.equal(oldValue, value))
		clearAllChildrenComboboxs(UIComponent(intf));
	//relationOtherComboBox(ComboBoxEx(intf), value, extValue);


	intf.bindEvent();



}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function setGridBehaviour(controlName:String, addEnabled:Boolean, deleteEnabled:Boolean):void
{
	var grid:FormGrid=getControl(controlName) as FormGrid;
	if (grid)
	{
		grid.supportAdd=addEnabled;
		grid.supportDelete=deleteEnabled;
		grid.initToolbarButtons();
	}
}

public function setTabSheetActivePage(pageControlName:String, tabSheetName:String):void
{
	var pageControl:TabNavigator=getControl(pageControlName) as TabNavigator;
	var tabsheet:NavigatorContent=getControl(tabSheetName) as NavigatorContent;
	if (pageControl && tabsheet)
		pageControl.selectedChild=tabsheet;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//得到控件值
/**
 *
 * @param controlName
 * @return
 */
public function getControlValue(controlName:String):String
{
	var result:String="";

	var control:UIComponent=_controlUID2Instance[controlName];
	if (control != null)
		result=InterfaceEditableControl(control).controlValue;
	else if (!FlexGlobals.topLevelApplication.isRelease)
		AlertUtils.msnMessage("系统", "表单中名称为" + controlName + "的控件不存在!", true);

	return result;
}

public function getControlValueEx(controlName:String):String
{
	var result:String="";

	var control:UIComponent=_controlUID2Instance[controlName];
	if (control != null)
		result=InterfaceEditableControl(control).controlExtValue;
	return result;
}





/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//设置单个控件的状态
public function setControlEnabled(c:UIComponent, enable:Boolean, skipNativeSetting:Boolean=false):void
{
	if (c is InterfaceEditableControl)
	{
		var intf:InterfaceEditableControl=InterfaceEditableControl(c);
		intf.readonly=!enable;
		if (intf.controlType == EditableControlParams.CONST_GRID)
			setControlsEnabledByGrid(FormGrid(c));
	}
	else if (c is Button)
	{
		Button(c).enabled=enable;
	}
}


////////////////////////////////////////////////////////////////////////////////////////////////////
//设置多个控件的Enabled
public function setControlsEnabled(controls:Array, enabled:Boolean):void
{
	if (controls.length == 0)
		return;
	if (controls[0] is Array)
		controls=controls[0];
	for (var i:int=0; i < controls.length; i++)
	{
		if (controls[i] is UIComponent)
			setControlEnabled(UIComponent(controls[i]), enabled);
		else
			setControlEnabled(_controlUID2Instance[String(controls[i])], enabled);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//设置多个控件的Visible
public function setControlsVisible(controls:Array, isVisible:Boolean):void
{
	if (controls.length == 0)
		return;
	if (controls[0] is Array)
		controls=controls[0];
	for (var i:int=0; i < controls.length; i++)
	{
		if (controls[i] is UIComponent)
			setControlVisible(UIComponent(controls[i]), isVisible);
		else
			setControlVisible(_controlUID2Instance[String(controls[i])], isVisible);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//设置控件的Visible
public function setControlVisible(control:UIComponent, isVisible:Boolean):void
{
	if (_tabsheet2PageControl.hasOwnProperty(control.uid))
	{
		var pageControl:TabNavigator=TabNavigator(_tabsheet2PageControl[control.uid]);
		if (_tabsheet2Visible[control.uid] == isVisible)
			return;
		if (isVisible && control.parent == null)
		{
			var controlIndex:int=_controlUID2ControlParams[control.uid].index;
			var index:int=pageControl.numElements;
			for (var i:int=0; i < pageControl.numElements; i++)
			{
				var n:NavigatorContent=NavigatorContent(pageControl.getElementAt(i));
				if (_controlUID2ControlParams[n.uid].index > controlIndex)
				{
					index=i;
					break;
				}
			}
			pageControl.addElementAt(control, index);
		}
		else if (!isVisible && control.parent != null)
		{
			pageControl.removeElement(control);
		}
		_tabsheet2Visible[control.uid]=isVisible;
		pageControl.invalidateDisplayList();
	}
	else
	{
		control.visible=isVisible;
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////
//根据Grid设置其他相关控件的Enabled
public function setControlsEnabledByGrid(grid:FormGrid):void
{
	if (!grid.isBindDatasource)
		return;
	//setControlsEnabledByGrid：" + _formDataService.getArray(grid.databaseName).length);
	var enabled:Boolean=!grid.readonly && this._formDataService.getArray(grid.databaseName).length != 0;
	var datasourceName:String=grid.databaseName;
	var controls:Array=this.getDataBindControlByDataSourceName(datasourceName, null, [grid]);
	for (var i:int=0; i < controls.length; i++)
	{
		if (controls[i].control is InterfaceEditableControl)
		{
			var intf:InterfaceEditableControl=InterfaceEditableControl(controls[i].control);
			intf.readonly=intf.defaultReadonly || !enabled;
			if (intf.controlType == EditableControlParams.CONST_GRID)
				this.setControlsEnabledByGrid(intf as FormGrid);
		}

	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//取得控件
/**
 *
 * @param controlName
 * @return
 */
public function getControl(controlName:String):UIComponent
{
	var control:UIComponent=_controlUID2Instance[controlName];
	if (control == null && !FlexGlobals.topLevelApplication.isRelease)
		AlertUtils.msnMessage("系统", "表单中名称为" + controlName + "的控件不存在!", true);
	return control;
}

//////////////////////////////////////////////////////////////////////////////////
//根据DatasetName 获取Grid列表
public function getDBGridsByDatasetName(datasetNames:Array):Array
{
	var result:Array=[];
	for (var i:int=0; i < datasetNames.length; i++)
	{
		var datasetName:String=datasetNames[i];
		for (var j:int=0; j < _formDBGrids.length; j++)
		{
			var grid:FormGrid=_formDBGrids[j] as FormGrid;
			if (StringUtils.equal(grid.databaseName, datasetName))
			{
				result.push(grid);
				break;
			}
		}
	}
	return result;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @param controlName
 * @return
 */
public function getDBGridSelectedItem(controlName:String):Object
{
	var grid:FormGrid=getControl(controlName) as FormGrid;
	return grid.selectedItem;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function selectDBGridByKeyField(controlName:String, keyFieldName:String, value:String):void
{
	return (getControl(controlName) as FormGrid).select(keyFieldName, value);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *获取DataSet所有数据
 * @param datasetName
 * @return
 */
public function getDataSetRecords(datasetName:String):Array
{
	return _formDataService.getDataSetRecords(datasetName);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *
 * @param datasetName
 * @param records
 * @return
 */
public function batchAppendRecords(datasetName:String, records:Array):int
{
	var result:int=_formDataService.batchAppendRecords(datasetName, records);
	for (var i:int=0; i < _formDBGrids.length; i++)
	{
		var grid:FormGrid=_formDBGrids[i] as FormGrid;
		if (StringUtils.equal(grid.databaseName, datasetName))
		{
			grid.lockEvent=true;
			grid.refresh();
			grid.lockEvent=false;
		}
	}
	return result;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 创建表单UI 该函数因为Flex控件的初始化顺序及创建顺序的原因只处理一些赋值工作 具体的创建有 Module 的CreateComplete事件触发
 * @param formDefine 表单定义
 * @param isDialog 表单是否显示在对话框中
 * @return Module实例自身
 *
 */
public function createUI(formDefine:Object):UIComponent
{
	_formDefine=formDefine;
	this._isHasJSScript=!StringUtils.isEmpty(StringUtils.trim(_formDefine.JS));
	_formDataService.initDefine(_formDefine.datasets); //初始化数据服务
	_formDataService.initFormData(_formDefine.data, true);
	_isMainDatasetHasRecord=_formDataService.getMainDatasetID() != null;
	return this;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 创建表单窗体
 * @param forms 表单窗体定义
 *
 */
[Bindable]
private var _formCaptions:ArrayCollection=new ArrayCollection([]);
private var _formPageControl:TabNavigator=null;

private function createForms(forms:Array):void
{

	var i:int=0;
	var j:int=0;
	var toolbarItems:Array=[];
	var form:Object=null;

	toolbarItems.push({supportRight: true, type: "btn", id: "____save____", icon: "table_save.png", title: "保存", text: ""});

	if (forms.length == 1)
	{
		form=forms[0].Form;

		if (!_isDialog && form.Anchor == "Left,Top,Right,Bottom")
			_isFormAutoFill=true;

		var formContainer:UIComponent=createAForm(form, true);

		//"Toolbars" : "{\"Items\":[{\"Caption\":\"测试\",\"Items\":\"\",\"Position\":\"\",\"Type\":\"按钮\",\"Icon\":\"accept.bmp\",\"Function\":\"\"},{\"Caption\":\"呵呵\",\"Items\":\"\",\"Position\":\"\",\"Type\":\"按钮\",\"Icon\":\"add.bmp\",\"Function\":\"\"}]}",
		if (!StringUtils.isEmpty(form.Toolbars))
		{
			var formToolbarItems:Object=JSON.parse(StringUtils.replace(form.Toolbars, ".bmp", ".png"));
			if (formToolbarItems.Items)
			{
				formToolbarItems=formToolbarItems.Items;
				for (j=0; j < formToolbarItems.length; j++)
				{
					var btnItem:Object=formToolbarItems[j];
					if (btnItem.Type == "按钮" || btnItem.Type == "标题按钮")
					{
						toolbarItems.push({supportRight: Convert.object2Boolean(btnItem.SupportRight, false), type: "btn", id: btnItem.Function, icon: btnItem.Icon, title: btnItem.Caption, text: ((btnItem.Type == "标题按钮") ? btnItem.Caption : "")});
					}
					else if (btnItem.Type == "分隔条")
					{
						toolbarItems.push({type: "sep"});
					}
					else if (btnItem.Type == "标签")
					{
						toolbarItems.push({type: "label", label: btnItem.Caption});
					}
				}
			}
		}
		if (_isFormAutoFill)
		{
			groupFormContainAutoFill.removeAllElements();
			this.groupFormContainAutoFill.addElement(formContainer);
		}
		else
		{
			this.groupFormContainScroll.addElement(formContainer);
		}
	}
	else //多页表单
	{

		var pageControl:TabNavigator=new TabNavigator();
		pageControl.left=0;
		pageControl.top=0;
		pageControl.percentWidth=100;
		pageControl.percentHeight=100;
		pageControl.creationPolicy="all";
		if (_hideFormTab)
			pageControl.setStyle("tabHeight", 0);
		//pageControl.
		_formPageControl=pageControl;

		this.groupFormContainScroll.addElement(pageControl);

		for (i=0; i < forms.length; i++)
		{
			form=forms[i].Form;
			//var formPage:BorderContainer = new BorderContainer();
			var formPage:NavigatorContent=new NavigatorContent();
			formPage.label=form.Caption;
			pageControl.addElement(formPage);
			formPage.setStyle("backgroundColor", "#FCFCFC");
			formPage.addElement(createAForm(form, false));
		}
	}

	if (this.currentState == "normalToolbarStyle")
		createToolbar(this.groupFormToolbar, toolbarItems);

	_showToolbarEx=_showToolbar;
	if (_readonly && !_isDialog && _showToolbar)
	{
		this.currentState="normalStyle";
		_showToolbarEx=false;
	}
//	if (!_readonly && !_isDialog && _showToolbar)
//		this.currentState="normalToolbarStyle";

}

//---------------------------------------------------------------------------------------------------

public function getFormCaptions():ArrayCollection
{
	return _formCaptions;
}

//---------------------------------------------------------------------------------------------------
public function selectForm(index:int):void
{
	if (_formPageControl)
	{
		_formPageControl.selectedIndex=index;
	}
}

//---------------------------------------------------------------------------------------------------
//创建表单
private function createAForm(form:Object, isBorderVisible:Boolean):UIComponent
{
	_formCaptions.addItem({label: form.Caption});

	//var scroller:ScrollerPanel=new ScrollerPanel();

	var formContainer:BorderContainer=new BorderContainer();

	//居中显示表单
	form.Anchor="Left,Top,Right,Bottom";
	initControlSize(formContainer, form, 0, 0, false, false);
	initControlStyle(formContainer, form);

	createControls(form, formContainer, 0, 0);
	formContainer.setStyle("borderVisible", isBorderVisible);
	formContainer.setStyle("backgroundColor", "#FCFCFC");

	return formContainer;
	//scroller.addElementEx(formContainer);
	//return scroller;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 创建具体的表单
private function createForm():void
{
	var forms:Array=_formDefine.forms;
	createForms(forms); //创建UI
	sortFormControlsByDataSource();
	initFormData(_formDefine.data, true, true, true); //初始化数据
	resetFormControlStatusByEnabled(!this.readonly); //设置表单是否可以编辑
	var i:int=0;
	for (i=0; i < _formDBGrids.length; i++)
	{
		var grid:FormGrid=FormGrid(_formDBGrids[i]);
		setControlsEnabledByGrid(grid);
	}

	if (this._controlStatus != null)
	{
		setControlStatus();
		this._controlStatus=null;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建按钮
private function createButtons(buttons:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(buttons is Array))
		buttons=[buttons];
	for (var i:int=0; i < buttons.length; i++)
	{
		var button:Object=buttons[i];
		var c:Button=new Button();

		initControlCommon(c, button);
		initControlSize(c, button, offsetLeft, offsetTop, false, false);
		initControlStyle(c, button);
		initControlEvent(c, button);
		controlParent["addElement"](c);
		c.label=button.Caption;
		if (Convert.object2Boolean(button.IsSupportRight))
			_formSupportReadOnlyBtns.push(c);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createPageControls(pagecontrols:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(pagecontrols is Array))
		pagecontrols=[pagecontrols];
	for (var i:int=0; i < pagecontrols.length; i++)
	{
		var pagecontrol:Object=pagecontrols[i];
		var c:TabNavigator=new TabNavigator();
		c.setStyle("tabHeight", 25);
		c.setStyle("paddingTop", 0);
		initControlSize(c, pagecontrol, offsetLeft, offsetTop, false, false);
		initControlStyle(c, pagecontrol);

		c.uid=pagecontrol["Name"];
		_controlUID2Instance[c.uid]=c;

		controlParent["addElement"](c);

		var pages:Object=pagecontrol.Page;
		if (!(pages is Array))
			pages=[pages];
		for (var j:int=0; j < pages.length; j++)
		{
			var page:Object=pages[j];
			var c_p:NavigatorContent=new NavigatorContent();
			c_p.label=page.Caption;
			UIUtils.fullAlign(c_p);
			initControlStyle(c_p, page);
			c["addElement"](c_p);

			c_p.uid=page["Name"];
			_controlUID2Instance[c_p.uid]=c_p;

			_tabsheet2PageControl[c_p.uid]=c;
			_tabsheet2Visible[c_p.uid]=true;

			createControls(page, c_p, offsetLeft, offsetTop);
			_controlUID2ControlParams[c_p.uid]={Visible: true, index: j};
		}
		c.selectedIndex=pagecontrol.PageIndex;
		_controlUID2Instance[c.id]=c;
		_formPageControl2SelectedIndex[c.id]=c.selectedIndex;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function callFunction4DateFieldEx(control:DateFieldEx, controlValue:String):void
{
	setDataSetValue(control.databaseName, control.fieldName, controlValue, [control]);
	eventOnControlChange4JS(control);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createDatePicks(datepicks:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(datepicks is Array))
		datepicks=[datepicks];
	for (var i:int=0; i < datepicks.length; i++)
	{
		var datepick:Object=datepicks[i];
		var c:DateFieldEx=new DateFieldEx();
		c.includeTime=Convert.object2Boolean(datepick.IncludeTime);
		initControlCommon(c, datepick);
		initControlSize(c, datepick, offsetLeft, offsetTop, false, true);
		initControlStyle(c, datepick);
		initControlUtils(c, datepick);
		initControlDataBind(c, datepick);
		initControlEvent(c, datepick);
		controlParent["addElement"](c);
		c.callbackChange=callFunction4DateFieldEx;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createComboBoxs(comboboxs:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(comboboxs is Array))
		comboboxs=[comboboxs];
	for (var i:int=0; i < comboboxs.length; i++)
	{
		var combobox:Object=comboboxs[i];
		var c:ComboBoxEx=new ComboBoxEx();
		initControlCommon(c, combobox);
		initControlSize(c, combobox, offsetLeft, offsetTop, false, true);
		initControlStyle(c, combobox);
		initControlUtils(c, combobox);
		initControlDataBind(c, combobox, true);
		initControlEvent(c, combobox);
		c.dataProvider=null;
		c.isFixedList=Convert.object2Boolean(combobox.IsFixedList, true);

		c.setStyle("styleName", "FormComboBox");
		controlParent["addElement"](c);
		//c.addEventListener(FocusEvent.FOCUS_IN, event4comboBoxFocusIn);
		//c.itemMatchingFunction=findMatchComboboxWithPinYinSupport;

		//设置联动关系
		if (!StringUtils.isEmpty(combobox.ParentCombobox))
		{
			var children:Array=_combobox2ChildrenCombobox[combobox.ParentCombobox];
			if (!children)
			{
				children=[];
				_combobox2ChildrenCombobox[combobox.ParentCombobox]=children;
			}
			children.push(c);
			_combobox2ParentCombobox[c.uid]=combobox.ParentCombobox;
		}
			//设置联动关系结束
	}
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createLookupComboBoxs(comboboxs:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(comboboxs is Array))
		comboboxs=[comboboxs];
	for (var i:int=0; i < comboboxs.length; i++)
	{
		var combobox:Object=comboboxs[i];
		var c:LookupCombobox=new LookupCombobox();
		initControlCommon(c, combobox);
		initControlSize(c, combobox, offsetLeft, offsetTop, false, true);
		c.datagrid.minWidth=Math.max(combobox.Width, 400);
		initControlStyle(c, combobox);
		initControlUtils(c, combobox);

		c.callbackeChang=eventLookupComboboxChang;
		c.readonly=Convert.object2Boolean(combobox.ReadOnly, false);
		c.formDataService=_formDataService;
		c.columns=combobox.ItemColumns;
		c.databaseName=combobox.DataSource;


		c.itemDatasetName=combobox.ItemDataSource;
		c.itemDisplayFieldName=combobox.ItemDisplayField;
		c.itemValueFieldName=combobox.ItemValueField;
		c.itemSearchFieldName=combobox.ItemSearchField;

		initControlDataBind(c, combobox, true);
		initControlEvent(c, combobox);
		//c.dataProvider=new ArrayCollection();
		//c.setStyle("styleName", "FormComboBox");
		controlParent["addElement"](c);
		//c.addEventListener(FocusEvent.FOCUS_IN, event4comboBoxFocusIn);
		_formLookupComboBoxs.push(c);

		c.parentComboBox=combobox.ParentCombobox;

		//设置联动关系
		if (!StringUtils.isEmpty(combobox.ParentCombobox))
		{
			var children:Array=_combobox2ChildrenCombobox[combobox.ParentCombobox];
			if (!children)
			{
				children=[];
				_combobox2ChildrenCombobox[combobox.ParentCombobox]=children;
			}
			children.push(c);
			_combobox2ParentCombobox[c.uid]=combobox.ParentCombobox;
		}
		//设置联动关系结束


		_DataBindControlsArray.push(c);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createTextBoxs(textboxs:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(textboxs is Array))
		textboxs=[textboxs];
	for (var i:int=0; i < textboxs.length; i++)
	{
		var textbox:Object=textboxs[i];
		var c:TextInputEx=new TextInputEx();
		c.includeButton=Convert.object2Boolean(textbox.HasButton);
		c.buttonToolHint=textbox.ButtonToolHint;
		initControlCommon(c, textbox);
		initControlSize(c, textbox, offsetLeft, offsetTop, false, true);
		initControlStyle(c, textbox);
		initControlUtils(c, textbox);
		initControlDataBind(c, textbox);
		initControlEvent(c, textbox);
		if (!StringUtils.equal("0", textbox.PasswordChar))
			c.displayAsPassword=true;

		if (!StringUtils.isEmpty(textbox.MaskString))
			c.restrict=textbox.MaskString;
		controlParent["addElement"](c);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createMemos(memos:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(memos is Array))
		memos=[memos];
	for (var i:int=0; i < memos.length; i++)
	{
		var memo:Object=memos[i];
		var c:UIComponent=null;

		if (Convert.object2Boolean(memo.RichEdit, false))
		{
			var canvas:Canvas=new Canvas();
			c=new RichEditorEx();
			initControlCommon(c, memo);
			initControlSize(canvas, memo, offsetLeft, offsetTop, false, false);
			initControlStyle(c, memo);
			initControlUtils(c, memo);
			initControlDataBind(c, memo);
			initControlEvent(c, memo);
			c.left=0;
			c.top=0;
			c.right=0;
			c.bottom=0;
			canvas.addElement(c);
			controlParent["addElement"](canvas);
		}
		else
		{
			c=new TextAreaEx();
			initControlCommon(c, memo);
			initControlSize(c, memo, offsetLeft, offsetTop, false, false);
			initControlStyle(c, memo);
			initControlUtils(c, memo);
			initControlDataBind(c, memo);
			initControlEvent(c, memo);
			controlParent["addElement"](c);
		}
			//c.includeButton = Convert.str2Boolean (textbox.HasButton);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//产生标签
private function createLables(lables:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(lables is Array))
		lables=[lables];
	for (var i:int=0; i < lables.length; i++)
	{
		var label:Object=lables[i];
		var c:Label=new Label();
		initControlCommon(c, label);
		c.setStyle("verticalAlign", "middle");
		initControlSize(c, label, offsetLeft, offsetTop, false, false);
		initControlStyle(c, label);

		c.uid=label["Name"];
		_controlUID2Instance[c.uid]=c;

		c.text=label["Caption"];
		controlParent["addElement"](c);
	}

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createCheckBoxs(checkboxs:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(checkboxs is Array))
		checkboxs=[checkboxs];
	for (var i:int=0; i < checkboxs.length; i++)
	{
		var checkbox:Object=checkboxs[i];
		var c:CheckBoxEx=new CheckBoxEx();

		//c.setStyle("baseColor" , "#FFFFFF");
		initControlCommon(c, checkbox);
		initControlSize(c, checkbox, offsetLeft, offsetTop, true, true);
		initControlStyle(c, checkbox);
		initControlDataBind(c, checkbox);
		initControlEvent(c, checkbox);
		c.label=checkbox.Caption;
		c.alpha=1;
		controlParent["addElement"](c);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createGroupBoxs(groupboxs:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(groupboxs is Array))
		groupboxs=[groupboxs];
	for (var i:int=0; i < groupboxs.length; i++)
	{
		var groupbox:Object=groupboxs[i];
		var c:GroupBox=new GroupBox();
		initControlSize(c, groupbox, offsetLeft, offsetTop, false, false);
		initControlStyle(c, groupbox);
		controlParent["addElement"](c);
		c.title=groupbox.Caption;
		createControls(groupbox, c, 0, -20);
		c.uid=groupbox["Name"];
		_controlUID2Instance[c.uid]=c;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createControls(parentControlDefine:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	//标签
	var lables:Object=parentControlDefine.Label;
	if (lables)
		createLables(lables, controlParent, offsetLeft, controlParent is GroupBox ? -17 : 3);

	//文本输入框
	var textboxs:Object=parentControlDefine.TextBox;
	if (textboxs)
		createTextBoxs(textboxs, controlParent, offsetLeft, offsetTop);

	//下拉列表框
	var comboboxs:Object=parentControlDefine.ComboBox;
	if (comboboxs)
		createComboBoxs(comboboxs, controlParent, offsetLeft, offsetTop);

	//日期时间选择框
	var datepicks:Object=parentControlDefine.DatePick;
	if (datepicks)
		createDatePicks(datepicks, controlParent, offsetLeft, offsetTop);

	//pagecontrol
	var pagecontrols:Object=parentControlDefine.PageControl;
	if (pagecontrols)
		createPageControls(pagecontrols, controlParent, offsetLeft, offsetTop);

	//groupbox
	var groupboxs:Object=parentControlDefine.GroupBox;
	if (groupboxs)
		createGroupBoxs(groupboxs, controlParent, offsetLeft, offsetTop);

	//checkbox
	var checkboxs:Object=parentControlDefine.CheckBox;
	if (checkboxs)
		createCheckBoxs(checkboxs, controlParent, offsetLeft, offsetTop);

	//多行文本编辑框
	var memos:Object=parentControlDefine.Memo;
	if (memos)
		createMemos(memos, controlParent, offsetLeft, offsetTop);

	//按钮
	var buttons:Object=parentControlDefine.Button;
	if (buttons)
		createButtons(buttons, controlParent, offsetLeft, offsetTop);

	//数据列表
	var dbgrids:Object=parentControlDefine.DBGrid;
	if (dbgrids)
		createDBGrid(dbgrids, controlParent, offsetLeft, offsetTop);

	var lookupComboBox:Object=parentControlDefine.LookupComboBox;
	if (lookupComboBox)
		createLookupComboBoxs(lookupComboBox, controlParent, offsetLeft, offsetTop);


	//数据校验
	var valids:Object=parentControlDefine.ValidControl;
	if (valids)
		createValidControls(valids, controlParent, offsetLeft, offsetTop);

	//文件上传
	var fileUploads:Object=parentControlDefine.UploadFile;
	if (fileUploads)
		createFileUploadControls(fileUploads, controlParent, offsetLeft, offsetTop);

	var attachments:Object=parentControlDefine.Attachment;
	if (attachments)
		createAttachmentInputControls(attachments, controlParent, offsetLeft, offsetTop);


	//照片框
	var pictures:Object=parentControlDefine.Picture;
	if (pictures)
		createPictureControl(pictures, controlParent, offsetLeft, offsetTop);

	var lines:Object=parentControlDefine.Line;
	if (lines)
		createLineControl(lines, controlParent, offsetLeft, offsetTop);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createLineControl(lines:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(lines is Array))
		lines=[lines];
	for each (var line:Object in lines)
	{
		var l:FormLine=new FormLine();
		initControlSize(l, line, offsetLeft, offsetTop, line.Type == "|", line.Type == "-");
		l.color=parseInt(line.Color.substr(1), 16);
		l.lineWidth=parseInt(line.LineWidth);
		controlParent["addElement"](l);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建图片控件
private function createPictureControl(pictures:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(pictures is Array))
		pictures=[pictures];
	for (var i:int=0; i < pictures.length; i++)
	{
		var picture:Object=pictures[i];
		var c:InputPicture=new InputPicture();

		initControlCommon(c, picture);
		initControlSize(c, picture, offsetLeft, offsetTop);
		initControlDataBind(c, picture);

		//c.saveToDB=Convert.object2Boolean(upload.SaveToDB, false);
		//c.type=upload.Type;
		c.acceptFileExts=picture.AcceptFileExts;
		c.maxFileSize=picture.AllowMaxFileSize;
		c.fileSizeUnit=Convert.str2int(picture.UnitType, 0);
		c.readonly=this._readonly;
		c.type=picture.PictureType;
		c.databaseName=picture.DataSource;
		c.fieldName=picture.FieldName;
		c.isSaveToDB=Convert.object2Boolean(picture.IsSaveToDB, false);
		c.setResizeParams(Convert.object2Boolean(picture.AllowResize, true), picture.AllowMaxWidth, picture.AllowMaxHeight, Convert.object2Boolean(picture.IsCreateThumbnail, true), picture.ThumbnailWidth, picture.ThumbnailHeight, Convert.object2Boolean(picture.ShowAsThumbnail, true));
		controlParent["addElement"](c);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建文件上传组件
private function createFileUploadControls(fileUploads:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(fileUploads is Array))
		fileUploads=[fileUploads];
	for (var i:int=0; i < fileUploads.length; i++)
	{
		var upload:Object=fileUploads[i];
		var c:InputFileUpload=new InputFileUpload();

		initControlCommon(c, upload);
		initControlSize(c, upload, offsetLeft, offsetTop);
		initControlDataBind(c, upload);
		c.saveToDB=Convert.object2Boolean(upload.SaveToDB, false);
		c.type=upload.Type;
		c.acceptFileExts=upload.AcceptFileExts;
		c.allowMaxSize=upload.MaxFileSize;
		c.fileSizeUnit=Convert.str2int(upload.UnitType, 0);
		c.readonly=this._readonly;
		controlParent["addElement"](c);
	}
}

private function createAttachmentInputControls(attachments:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(attachments is Array))
		attachments=[attachments];
	for (var i:int=0; i < attachments.length; i++)
	{
		var upload:Object=attachments[i];
		var c:InputFileUploadSimp=new InputFileUploadSimp();

		initControlCommon(c, upload);
		initControlSize(c, upload, offsetLeft, offsetTop + 3, false, true);
		initControlDataBind(c, upload);
		c.saveToDB=Convert.object2Boolean(upload.SaveToDB, false);
		c.type=upload.Type;
		c.acceptFileExts=upload.AcceptFileExts;
		c.allowMaxSize=upload.MaxFileSize;
		c.fileSizeUnit=Convert.str2int(upload.UnitType, 0);
		c.readonly=this._readonly;
		controlParent["addElement"](c);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createDBGrid(dbgrids:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(dbgrids is Array))
		dbgrids=[dbgrids];
	for (var i:int=0; i < dbgrids.length; i++)
	{
		var dbgrid:Object=dbgrids[i];
		var grid:FormGrid=new FormGrid();
		grid.supportExport=Convert.object2Boolean(dbgrid.SupportExportImport, false);
		grid.supportAdd=Convert.object2Boolean(dbgrid.ShowAddButton, false);
		grid.supportDelete=Convert.object2Boolean(dbgrid.ShowDeleteButton, false);
		grid.extButtons=StringUtils.split(dbgrid.ExtButtons);
		grid.bindForm=dbgrid.BindForm;
		_DataBindControlsArray.push(grid); //数据绑定
		initControlCommon(grid, dbgrid);
		initControlSize(grid, dbgrid, 0, offsetTop); //fixed
		grid.showToolbar=Convert.object2Boolean(dbgrid.ShowDeleteButton) || Convert.object2Boolean(dbgrid.ShowAddButton);
		controlParent["addElement"](grid);
		grid.initFormDataService(this._formDataService);
		grid.setDataBindParams(this, this._formDataService, dbgrid.DataSource, "", "");
		grid.createUI(dbgrid);
		_formDBGrids.push(grid);
		registerDataBindControl(grid.databaseName, "", grid);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private function createValidControls(valids:Object, controlParent:UIComponent, offsetLeft:int, offsetTop:int):void
{
	if (!(valids is Array))
		valids=[valids];
	for (var i:int=0; i < valids.length; i++)
	{
		var valid:Object=valids[i];
		var c:Image=new Image();
		c.source=IMG_VALID_PNG; //"../images/18x18/valid.png";
		initControlSize(c, valid, offsetLeft, offsetTop);
		c.visible=false;
		valid.Name=c.uid;
		_formValidControls.push(valid);
		initControlCommon(c, valid);
		//c.visible = false;
		controlParent["addElement"](c);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////
//创建工具条对象
private function createToolbar(toolbarContain:HGroup, menuItems:Array):UIComponent
{
	for (var i:int=0; i < menuItems.length; i++)
	{
		var menuItem:Object=menuItems[i];
		if (menuItem.type == "btn")
		{
			var btn:IconButton=new IconButton();
			btn.label=menuItem["text"];
			btn.toolTip=menuItem["title"];
			btn.iconURL="../images/18x18/" + menuItem["icon"];
			btn.id=menuItem["id"];
			if (menuItem.supportRight)
				_formSupportReadOnlyBtns.push(btn);
			btn.addEventListener(MouseEvent.CLICK, eventToolbarItemClick);
			toolbarContain.addElement(btn);
		}
		else if (menuItem.type == "sep")
		{
			var sep:ToolbarVertline=new ToolbarVertline();
			toolbarContain.addElement(sep);
		}
		else if (menuItem.type == "label")
		{
			var label:Label=new Label();
			label.text=menuItem.label;
			label.setStyle("paddingTop", 2);
			label.setStyle("paddingLeft", 2);
			label.setStyle("paddingRight", 2);
			label.setStyle("fontWeight", "bold");
			toolbarContain.addElement(label);
		}
		else if (menuItem.type == "list")
		{
			var menuBar:PopUpMenuButton=new PopUpMenuButton();
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
				if (!(event.item.supportRight && _readonly))
					executeByBarItemID(bar.id);
			});

			menuBar.id=menuItem.items[0].id;
			menuBar.setStyle("icon", IconUtility.getClass(menuBar, "../images/18x18/" + menuItem.items[0].icon, 18, 18));
			menuBar.toolTip=menuItem.items[0].title;

			if (menuItem.supportRight)
				_formSupportReadOnlyBtns.push(menuBar);

			menuBar.addEventListener(MouseEvent.CLICK, eventToolbarItemClick);

			toolbarContain.addElement(menuBar);

		}
	}
	return toolbarContain;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//初始化通用参数
private function initControlCommon(control:UIComponent, controlParams:Object):void
{
	if (controlParams.hasOwnProperty("TabIndex") && control.hasOwnProperty())
	{
		control.tabIndex=controlParams.TabIndex * 1;
		control.tabEnabled=true;
	}

	if (control.hasOwnProperty("bgColor") && controlParams.Color && controlParams.Color != "#FFFFFF")
		control["bgColor"]=parseInt(controlParams.Color.substr(1), 16);

	controlParams.ReadOnly=Convert.object2Boolean(controlParams.ReadOnly, false);
	controlParams.Visible=Convert.object2Boolean(controlParams.Visible, true);
	control.uid=controlParams["Name"];
	this._controlUID2Instance[control.uid]=control;
	this._controlUID2ControlParams[control.uid]=controlParams;
	control.visible=Convert.object2Boolean(controlParams.Visible, true);
	if (control is InterfaceEditableControl)
	{
		InterfaceEditableControl(control).defaultReadonly=controlParams.ReadOnly;
		InterfaceEditableControl(control).readonly=controlParams.ReadOnly;
		var controlType:int=InterfaceEditableControl(control).controlType;
		//回车跳转事件
		if (controlType == EditableControlParams.CONST_INPUTTEXT || controlType == EditableControlParams.CONST_COMBOBOX || //
			controlType == EditableControlParams.CONST_CHECKBOX || controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX)
			InterfaceEditableControl(control).registerEvent(KeyboardEvent.KEY_DOWN, this.eventControlEnterNextFocus);
	}
	else if (control is Button)
	{
		Button(control).enabled=!controlParams.ReadOnly;
	}

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//生成数据绑定
private function initControlDataBind(control:UIComponent, controlParams:Object, isCombobox:Boolean=false):void
{
	var datasourceName:String=controlParams.DataSource;
	if (StringUtils.isEmpty(datasourceName))
		return;

	var fieldName:String="";
	var extFieldName:String="";
	var intfControl:InterfaceEditableControl=InterfaceEditableControl(control);
	var controlType:int=intfControl.controlType;
	if (controlType == EditableControlParams.CONST_INPUTTEXT || //
		controlType == EditableControlParams.CONST_MEMO || //
		controlType == EditableControlParams.CONST_RICHEDIT)
	{
		fieldName=StringUtils.between(controlParams.FieldName, "[", "]");
		intfControl.registerEvent(Event.CHANGE, eventOnControlDataChange);
		intfControl.registerEvent(FocusEvent.FOCUS_IN, eventOnControlFocusIn);
		intfControl.registerEvent(FocusEvent.FOCUS_OUT, eventOnControlFocusOut);
	}
	else if (controlType == EditableControlParams.CONST_DATE_EX || //
		controlType == EditableControlParams.CONST_CHECKBOX)
	{
		fieldName=StringUtils.between(controlParams.FieldName, "[", "]");
		intfControl.registerEvent(Event.CHANGE, eventOnControlDataChange);
	}
	else if (controlType == EditableControlParams.CONST_COMBOBOX)
	{ //ComboBox		
		fieldName=StringUtils.between(controlParams.FieldName, "[", "]");
		controlParams.ExtFieldName=StringUtils.between(controlParams.ExtFieldName, "[", "]");
		extFieldName=controlParams.ExtFieldName;
		controlParams.ItemDisplayField=StringUtils.between(controlParams.ItemDisplayField, "[", "]");
		controlParams.ItemValueField=StringUtils.between(controlParams.ItemValueField, "[", "]");
		controlParams.ItemsFromDB=Convert.object2Boolean(controlParams.ItemsFromDB);
		var itemDS:String=controlParams.ItemDataSource;
		if (controlParams.ItemsFromDB && !StringUtils.isEmpty(itemDS))
			registerDataBindControl(itemDS, null, control);
		intfControl.registerEvent(DropDownEvent.OPEN, eventOnComboboxOpen);
		control.addEventListener(IndexChangeEvent.CHANGE, eventOnControlDataChange);
	}
	else if (controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX)
	{ //Lookup Combobox
		extFieldName=controlParams.DisplayField;
		fieldName=controlParams.ValueField;
	}
	else if (controlType == EditableControlParams.CONST_PICTURE || controlType == EditableControlParams.CONST_FILEUPLOAD || controlType == EditableControlParams.CONST_FILEUPLOADSIMP)
	{ //Lookup Combobox
		fieldName=StringUtils.between(controlParams.FieldName, "[", "]");
	}

	controlParams.FieldName=fieldName;
	intfControl.setDataBindParams(this, this._formDataService, datasourceName, fieldName, extFieldName);

	if (!StringUtils.isEmpty(fieldName))
		registerDataBindControl(datasourceName, fieldName, control);

	if (!StringUtils.isEmpty(extFieldName))
		registerDataBindControl(datasourceName, extFieldName, control);

	if (intfControl.isBindDatasource)
		this._DataBindControlsArray.push(control);

	_controlUID2DataBindParams[control.uid]=controlParams;
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//设置控件数据源
private function initControlUtils(control:UIComponent, controlDefine:Object):void
{
	if (controlDefine.hasOwnProperty("TabIndex"))
		control.tabIndex=int(controlDefine["TabIndex"]) + 1;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//属性
private function initControlStyle(control:UIComponent, controlDefine:Object):void
{
	var fontName:String=controlDefine["FontName"];
	var fontSize:String=controlDefine["FontSize"];
	var fontColor:String=controlDefine["FontColor"];
	var fontWeight:Boolean=controlDefine["FontBold"] == "True";
	//var bgColor : String = controlDefine["Color"];
	if (!StringUtils.isEmpty(fontName) && fontName != "宋体" && fontName != "ProjectStudioFont")
		control.setStyle("fontFamily", fontName);
	if (!StringUtils.isEmpty(fontSize))
		control.setStyle("fontSize", Number(fontSize) * 4 / 3);
	if (!StringUtils.isEmpty(fontColor))
		control.setStyle("color", fontColor);
	//if (!StringUtils.isEmpty (bgColor))
	//    control.setStyle ("backgroundColor", bgColor);
	if (fontWeight)
		control.setStyle("fontWeight", "bold");

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//尺寸
private function initControlSize(control:Object, controlDefine:Object, offsetLeft:int, offsetTop:int, skipWidth:Boolean=false, skipHeight:Boolean=false, fixWidth:int=0, fixHeight:int=0):void
{
	var anchor:String=_isDialog ? "Left,Top" : StringUtils.nvl(controlDefine.Anchor, "Left,Top");
	var anchorLeft:Boolean=anchor.indexOf("Left") != -1;
	var anchorTop:Boolean=anchor.indexOf("Top") != -1;
	var anchorRight:Boolean=anchor.indexOf("Right") != -1;
	var anchorBottom:Boolean=anchor.indexOf("Bottom") != -1;
	if (anchorRight)
		control.right=Convert.str2int(controlDefine["Right"], 0);
	if (anchorBottom)
		control.bottom=Convert.str2int(controlDefine["Bottom"], 0);
	if (anchorLeft)
		control.left=Convert.str2int(controlDefine["Left"], 0);
	if (anchorTop)
		control.top=Convert.str2int(controlDefine["Top"], 0) + offsetTop;
	if ((!anchorLeft || !anchorRight) && !skipWidth)
		control.width=int(controlDefine["Width"]) + fixWidth;
	if ((!anchorTop || !anchorBottom) && !skipHeight)
		control.height=int(controlDefine["Height"]) + fixHeight;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//初始化控件事件
private function initControlEvent(control:UIComponent, controlDefine:Object):void
{
	for (var k:String in controlDefine)
	{
		var funName:String=controlDefine[k];
		if (StringUtils.startWith(k, "On") && !StringUtils.isEmpty(funName))
		{
			switch (k)
			{
				case "OnButtonClick": //文本编辑框附件按钮事件
					if (InterfaceEditableControl(control).controlType == EditableControlParams.CONST_INPUTTEXT)
						TextInputEx(control).btnFunction=eventOnTextButtonClick4JS;
					break;
				case "OnDBLClick": //双击事件
					control.addEventListener(MouseEvent.DOUBLE_CLICK, eventOnDBClick4JS);
					break;
				case "OnClick": //点击事件
					control.addEventListener(MouseEvent.CLICK, eventOnClick4JS);
					break;
				case "OnFocus":
					break;
				case "OnBlur":
					break;
				case "OnKeyDown":
					break;
				case "OnKeyUp":
					break;
				case "OnKeyPress":
					break;

			}
			_controlUIDAndEventName2JSFunName[control.uid + "__" + k]=funName;
		}
	}
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//初始化下拉列表框
private function initComboboxItems():void
{
	var i:int=0;
	var j:int=0;
	for (i=0; i < _DataBindControlsArray.length; i++)
	{
		var c:UIComponent=_DataBindControlsArray[i];
		var intf:InterfaceEditableControl=InterfaceEditableControl(c);
		if (intf.controlType != EditableControlParams.CONST_COMBOBOX)
			continue;

		var params:Object=_controlUID2DataBindParams[c.uid];
		if (params.ItemsFromDB)
		{ //从数据库获取
			ComboBoxEx(c).dataProvider=FormDataService.BLANK_ARRAY_COLLECTION;
			var itemDS:String=params.ItemDataSource;
			var fieldD:String=params.ItemDisplayField;
			var fieldV:String=params.ItemValueField;
			var array:Array=_formDataService.getArray(itemDS);
			if (array)
			{
				ComboBoxEx(c).dataProvider=new ArrayCollection([]);
				for (j=0; j < array.length; j++)
					ComboBoxEx(c).dataProvider.addItem({label: array[j][fieldD], data: array[j][fieldV]});
			}
		} //从数据库读取结束
		else
		{
			if (!ComboBoxEx(c).dataProvider)
			{
				ComboBoxEx(c).dataProvider=new ArrayCollection([]);
				if (params.Item is Array)
				{
					for (j=0; j < params.Item.length; j++)
					{
						var item:Object=params.Item[j];
						ComboBoxEx(c).dataProvider.addItem({label: item.Display, data: item.Value});
					}
				}
			}
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//初始化表单各个控件是否可以编辑
private function resetFormControlStatusByEnabled(value:Boolean):void
{
	var i:int=0;
	var c:UIComponent=null;
	for (i=0; i < _DataBindControlsArray.length; i++)
	{
		c=_DataBindControlsArray[i];
		if (c is InterfaceEditableControl)
			InterfaceEditableControl(c).reset();
	}
	if (this.btnOK)
		this.btnOK.enabled=value;

	if (this.btnSaveAndSend)
		this.btnSaveAndSend.enabled=value;

	for (i=0; i < _formSupportReadOnlyBtns.length; i++)
	{
		c=_formSupportReadOnlyBtns[i];
		var controlParams:Object=this._controlUID2ControlParams[c.uid];
		if (controlParams)
		{
			if (value)
			{
				c.enabled=!controlParams.ReadOnly;
				c.visible=controlParams.Visible;
			}
			else
			{
				c.enabled=false;
			}
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//注册数据绑定控件
private function registerDataBindControl(datasourceName:String, fieldName:String, control:UIComponent):void
{
	if (!_datasource2DataBindControlList[datasourceName])
		_datasource2DataBindControlList[datasourceName]=[];
	_datasource2DataBindControlList[datasourceName].push({control: control, fieldName: fieldName});
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//根据数据绑定获取控件列表
private function getDataBindControlByDataSourceName(datasourceName:String, fieldName:String=null, exceptControls:Array=null):Array
{
	var result:Array=[];
	if (_datasource2DataBindControlList[datasourceName])
	{
		var tempArray:Array=_datasource2DataBindControlList[datasourceName];
		for (var i:int=0; i < tempArray.length; i++)
		{
			var p:Object=tempArray[i];
			var control:UIComponent=p.control;
			if (control is FormGrid)
			{
				var grid:FormGrid=control as FormGrid;
				if ((StringUtils.isEmpty(fieldName) || ArrayUtils.indexOf(grid.gridColumnFields, fieldName) != -1) && (exceptControls == null || ArrayUtils.indexOf(exceptControls, p.control) == -1))
					result.push(p);
			}
			else
			{
				if ((StringUtils.isEmpty(fieldName) || StringUtils.equal(fieldName, p.fieldName)) && (exceptControls == null || ArrayUtils.indexOf(exceptControls, p.control) == -1))
					result.push(p);
			}
		}
	}
	return result;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//根据数据源排序控件
private function sortFormControlsByDataSource():void
{
	for (var k:String in _datasource2DataBindControlList)
	{
		var controls:Array=_datasource2DataBindControlList[k];
		controls.sort(function orderLastName(a:Object, b:Object):int
		{
			var result:int=0;
			var p1:Object=_controlUID2DataBindParams[a.control.uid];
			var p2:Object=_controlUID2DataBindParams[b.control.uid];
			if (p1 != null && p2 != null)
			{
				if (!StringUtils.isEmpty(p1.DataSource) && StringUtils.equal(p1.DataSource, p2.DataSource)) //同一个数据源
				{
					var d1:String=p1.ItemDataSource;
					var d2:String=p2.ItemDataSource;
					if (!StringUtils.isEmpty(d1) && !StringUtils.isEmpty(d2))
					{
						result=(_formDataService.getDataSetIndex(d1) > _formDataService.getDataSetIndex(d2)) ? 1 : -1;
					}
				}
				else if (!StringUtils.isEmpty(p1.DataSource) && !StringUtils.isEmpty(p2.DataSource))
				{
					result=(_formDataService.getDataSetIndex(p1.DataSource) > _formDataService.getDataSetIndex(p2.DataSource)) ? 1 : -1;
				}
			}
			return result;
		});
		_datasource2DataBindControlList[k]=controls;
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//设置下拉控件列表项目
private function builderComboBoxItems(datasetName:String, records:Array, isInitValue:Boolean=false):void
{
	var controls:Array=getDataBindControlByDataSourceName(datasetName);
	for (var i:int=0; i < controls.length; i++)
	{
		var control:UIComponent=controls[i].control;
		var intf:InterfaceEditableControl=InterfaceEditableControl(control);
		if (intf.controlType != EditableControlParams.CONST_COMBOBOX || !intf.isBindDatasource)
			continue;


		var combobox:ComboBoxEx=control as ComboBoxEx;
		var params:Object=_controlUID2DataBindParams[control.uid];
		var fieldD:String=params.ItemDisplayField;
		var fieldV:String=params.ItemValueField;
		var items:Array=[];

		//if(Convert.object2Boolean(params.AppendNullValue))
		//	items.push({label: " ", data: null});

		for (var j:int=0; j < records.length; j++)
		{
			items.push({label: records[j][fieldD], data: records[j][fieldV]});
		}
		combobox.dataProvider=new ArrayCollection(items);

		if (isInitValue)
		{
			var selectedIndex:int=-1;
			var text:String="";
			var data:String="";
			if (items.length != 0)
			{
				selectedIndex=0;
				data=items[0].data;
				text=items[0].label;
			}
			_formDataService.setDataSetRecordIndex(datasetName, selectedIndex);
			combobox.selectedIndex=selectedIndex;
			ComboBoxEx(combobox).textInputValue=text;
			var comboboxDataSource:String=params.DataSource;
			var comboboxFieldName:String=params.FieldName;
			if (!StringUtils.isEmpty(comboboxDataSource) && !StringUtils.isEmpty(comboboxFieldName))
			{
				_formDataService.setDataSetValue(comboboxDataSource, comboboxFieldName, data);
				if (!StringUtils.isEmpty(params.ExtFieldName) && !StringUtils.equal(params.ExtFieldName, comboboxFieldName))
				{
					_formDataService.setDataSetValue(comboboxDataSource, params.ExtFieldName, text);
					setDataSetValue(comboboxDataSource, params.ExtFieldName, text, [combobox]);
				}
			}
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public function resumePageControlSelectedIndex():void
{
	for (var k:String in _formPageControl2SelectedIndex)
	{
		var pagecontrol:TabNavigator=_controlUID2Instance[k] as TabNavigator;
		pagecontrol.selectedIndex=_formPageControl2SelectedIndex[k];
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////
private var _callControlStatusUniqueID:String="";

//重置控件状态设置
private function resetControlsEnabled():void
{
	for (var k:String in _controlUID2Instance)
	{
		var c:UIComponent=_controlUID2Instance[k];
		if (c is InterfaceEditableControl)
			InterfaceEditableControl(c).reset();
		else
		{
			var p:Object=_controlUID2ControlParams[k];
			if (c && p && !(c is Image))
			{
				setControlVisible(c, p.Visible);
				setControlEnabled(c, !p.ReadOnly);
			}

		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////
public function setControlStatus():void
{
	var controlStatus:Object=_controlStatus;
	//setControlStatus");
	if (controlStatus != null /* && !StringUtils.equal(controlStatus.uid, _callControlStatusUniqueID)*/)
	{
		var i:int=0;
		resetControlsEnabled();
		for (i=0; i < controlStatus.controls.length; i++)
		{
			var obj:Object=controlStatus.controls[i];
			var c:UIComponent=getControl(obj.name);
			if (c)
			{
				this.setControlVisible(c, obj.visible);
				this.setControlEnabled(c, !obj.readonly);
			}
		}
		_callControlStatusUniqueID=controlStatus.uid;
	}
	for (i=0; i < _formDBGrids.length; i++)
	{
		var grid:FormGrid=FormGrid(_formDBGrids[i]);
		setControlsEnabledByGrid(grid);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
private function eventLookupComboboxChang(control:LookupCombobox):void
{
	clearAllChildrenComboboxs(control);
	if (registerJsEventTriggerTrace(control, "OnChange"))
	{
		triggerJSEvent(control, "OnChange");
		unregisterJsEventTriggerTrace(control, "OnChange");
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
private function findMatchComboboxWithPinYinSupport(cb:ComboBoxEx, input:String):Vector.<int>
{
	var results:Vector.<int>=new Vector.<int>;
	var ac:ArrayCollection=ArrayCollection(cb.dataProvider);
	for (var i:int=0; i < ac.length; i++)
	{
		var item:Object=ac.getItemAt(i);
		if (StringUtils.isEmpty(item.label) || StringUtils.isEmpty(input))
			continue;
		if (!item.hasOwnProperty("__pyheader__"))
			item["__pyheader__"]=ChineseConvert.convertString(item.label);
		if (item.label.indexOf(input) == 0 || item["__pyheader__"].indexOf(input.toUpperCase()) == 0)
			results.push(i);
	}
	return results;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
private function dateLabelFunction(v:Date):String
{
	if (v == null)
		return "";
	return DateField.dateToString(v, "YYYY-MM-DD");
}

/////////////////////////////////////////////////////////////////////////////////////////////////////
public function clearComboboxItems(controlName:String):void
{
	var c:Object=getControl(controlName);
	if (c == null || !(c is InterfaceEditableControl))
		return;
	var intf:InterfaceEditableControl=InterfaceEditableControl(c);
	if (intf.controlType == EditableControlParams.CONST_COMBOBOX || intf.controlType == EditableControlParams.CONST_LOOKUP_COMBOBOX)
	{
		var v:String=intf.controlValue;
		var extV:String=intf.controlExtValue;
		if (intf.controlType == EditableControlParams.CONST_COMBOBOX)
		{
			var cb:ComboBoxEx=ComboBoxEx(c);
			cb.dataProvider=FormDataService.BLANK_ARRAY_COLLECTION;
			cb.selectedIndex=-1;
			cb.selectedItem=null;
			if (_combobox2ParentCombobox.hasOwnProperty(cb.uid))
			{
				var params:Object=_controlUID2ControlParams[controlName];
				if (params.ItemsFromDB && !StringUtils.isEmpty(params.ItemDataSource))
				{
					var key:String=getControlValue(_combobox2ParentCombobox[cb.uid]);
					_formDataService.clearDynamicDataSource4ComboBox(params.ItemDataSource, key);
				}
			}
		}

	}

}

/////////////////////////////////////////////////////////////////////////////////////////////////////
private function bindInputFileAndPictures():void
{
	for (var k:String in _controlUID2Instance)
	{
		var control:Object=_controlUID2Instance[k];
		if (control is InputFileUpload)
		{
			InputFileUpload(control).resetPid();
		}
		else if (control is InputFileUploadSimp)
		{
			InputFileUploadSimp(control).resetPid();
		}
		else if (control is InputPicture)
		{
			InputPicture(control).resetPid();
		}
	}
}
