package com.estudio.flex.module
{
	import com.estudio.flex.common.InterfaceFormUI;

//通用可编辑控件
	public interface InterfaceEditableControl
	{
		// 数据绑定
		function get databaseName():String;
		function set databaseName(value:String):void;

		function get fieldName():String;
		function set fieldName(value:String):void;

		function get extFieldName():String;
		function set extFieldName(value:String):void;

		function get controlValue():String;
		function get controlExtValue():String;

		function set dataservice(value:FormDataService):void;
		function get dataservice():FormDataService;

		function setDataBindParams(formInstance:InterfaceFormUI, formDataService:FormDataService, databaseName:String, fieldName:String, extFieldName:String):void;

		function get isBindDatasource():Boolean;

		function setControlValue(value:String, extValue:String, isSettingDataservice:Boolean):void;


		//是否只读
		function get readonly():Boolean;
		function set readonly(value:Boolean):void;

		function get defaultReadonly():Boolean;
		function set defaultReadonly(value:Boolean):void;

		function get controlType():int;

		function get formInstance():InterfaceFormUI;
		function bindEvent():void;
		function unBindEvent():void;

		function registerEvent(eventName:String, eventFun:Function):void;

		function reset():void;
	}
}
