/**
 * 该文件用于定义一些同列表栏目相关的接口
 *
 * */
package com.estudio.flex.common
{
	import mx.core.UIComponent;

	import spark.components.Group;
	import spark.components.NavigatorContent;

	/******************************************************************************************************
	 * 接口定义:定义一些同列表栏目相关的函数
	 *****************************************************************************************************/
	public interface InterfacePortalGridEx
	{
		function get isCreateCompleted():Boolean;
		function createUI(gridDefine:Object):UIComponent;
		function getPortalID():String;
		function set readonly(value:Boolean):void;
		function get readonly():Boolean;

		function set tabsheet(value:NavigatorContent):void;
		function get tabsheet():NavigatorContent;

		function getLayout(name:String):Group;

		function refresh():void;

		function getIFrameID():String;

		function get tag():String;

		function set tag(value:String):void;

		function getSelectedKey(controlName:String):String;

		function setParams(param:Object):void;
		function getParams():Object;
		///////////////////////////////////////////////////////////////////////////////////////////////////
		//控件操作部分
		///////////////////////////////////////////////////////////////////////////////////////////////////
		// ------------------------------------------------------------------------------
		//Callback_PortalEx_getSelectedItem
		function getControlSelectedItem(controlName:String):Object;
		//Callback_PortalEx_getSelectedItems
		function getControlSelectedItems(controlName:String):Array;
		//Callback_PortalEx_append
		function appendControl(controlName:String, isChild:Boolean):void;
		//Callback_PortalEx_edit
		function editControl(controlName:String, isReadonly:Boolean):void;
		//Callback_PortalEx_del
		function delControl(controlName:String):void;
		//Callback_PortalEx_up
		function upControl(controlName:String):void;
		//Callback_PortalEx_down
		function downControl(controlName:String):void;
		//Callback_PortalEx_refresh
		function refreshControl(controlName:String):void;
		//Callback_PortalEx_getRecords
		function getControlRecords(controlName:String):Array;
		//Callback_PortalEx_updateRecord
		function updateControlRecord(controlName:String, record:Object):void;
		//Callback_PortalEx_selectItem
		function selectControlItem(controlName:String, key:String):Object;
		//Callback_PortalEx_getRootId
		function getControlRootId(controlName:String):String;

		function getControlParams(controlName:String):Object;

		function setControlFilterParams(controlName:String, params:Object):void;
		function setControlReadonly(controlName:String, isReadonly:Boolean):void;

		function refreshSelectedItem(controlNames:String):void;
		function firstPage(controlNames:String):void;
		function lastPage(controlNames:String):void;
		function callLaterFunction(controlName:String, funName:String):void;
		function executeSWFControlFunction(controlName:String, funName:String, params:Object):Object;

		///////////////////////////////////////////////////////////////////////////////////////////////////
		//流程图
		function setDiagramActionBackground(controlName:String, action:String, color:uint):void;
		function focusDiagramActions(controlName:String, actions:Array):void;
		function loadDiagram(controlName:String, diagramName:String):void;
		function setDiagramActionStep(controlName:String, action:String, step:String):void;
		function setDiagramActionSetting(controlName:String, action:String, bg:uint, step:String):void;
		function batchSetDiagramActionSettings(controlName:String, params:Object):void;
		function getDiagramActionSettings(controlName:String):Object;
		//分页面板
		function setActivePage(controlName:String, activeControlName:String):void;
		//属性 文件管理器 图片管理器
		function setContent(controlName:String, content:String):void;
		function getContent(controlName:String):String;
		function setJsonControlRecordId(controlName:String, recordId:String):void;
		function setFormControlParams(controlName:String, params:Object):void;

		//富文本编辑器
		function setRichViewText(controlName:String, text:String):void;
		function getRichViewText(controlName:String):String;
		//GISMap
		function addGeometrys(controlName:String, geometry:String):void;
		//////////////////////////////////////////////////////////////////////////////////////////////////

		//保存到服务器
		function saveToServer(controlName:String):Object;

		function updateControl(isVisible:Boolean):void;
		///////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
