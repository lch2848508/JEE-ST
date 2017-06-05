package com.estudio.flex.component
{
	import com.estudio.flex.common.InterfaceFormUI;
	import com.estudio.flex.module.EditableControlParams;
	import com.estudio.flex.module.FormDataService;
	import com.estudio.flex.module.InterfaceEditableControl;
	import com.estudio.flex.utils.StringUtils;

	import mx.core.FlexGlobals;
	import mx.events.FlexEvent;

	import spark.components.TextInput;

	public class TextInputEx extends TextInput implements InterfaceEditableControl
	{
		/////////////////////////////////////////////////////////////////////////////////////////
		//原来的属性
		public function TextInputEx()
		{
			super();
			this.addEventListener(FlexEvent.CREATION_COMPLETE, function(event:FlexEvent):void
			{
				setStyle("contentBackgroundColor", readonly ? FlexGlobals.topLevelApplication.gobalSetting.readOnlyColor : _bgColor);
			});
		}

		[Bindable]
		public var includeButton:Boolean=false;

		[Bindable]
		public var buttonToolHint:String="";

		[Bindable]
		public var btnFunction:Function=null;

		[Bindable]
		public var disabledBtnFunction:Boolean=false;


		private var _bgColor:uint=0xFFFFFF;

		public function set bgColor(v:uint):void
		{
			_bgColor=v;
			setStyle("contentBackgroundColor", readonly ? FlexGlobals.topLevelApplication.gobalSetting.readOnlyColor : _bgColor);
		}

		/////////////////////////////////////////////////////////////////////////////////////////////
		//实现接口 InterfceEditableControl
		private var controlParams:EditableControlParams=new EditableControlParams();

		public function get databaseName():String
		{
			return controlParams.databaseName;
		}

		public function set databaseName(value:String):void
		{
			controlParams.databaseName=value;
		}

		public function get fieldName():String
		{
			return controlParams.fieldName;
		}

		public function set fieldName(value:String):void
		{
			controlParams.fieldName=value;
		}

		public function get extFieldName():String
		{
			return controlParams.extFieldName;
		}

		public function set extFieldName(value:String):void
		{
			controlParams.extFieldName=value;
		}

		public function get controlValue():String
		{
			return this.text;
		}

		public function get controlExtValue():String
		{
			return controlValue;
		}

		public function setControlValue(value:String, extValue:String, isSettingDataservice:Boolean):void
		{
			this.text=value;
			if (isSettingDataservice && isBindDatasource)
				controlParams.dataservice.setDataSetValue(controlParams.databaseName, controlParams.fieldName, value);
		}


		//是否只读
		public function get readonly():Boolean
		{
			return controlParams.readonly;
		}

		public function set readonly(value:Boolean):void
		{
			if (controlParams.readonly != value)
			{
				controlParams.readonly=value;
				this.editable=!value;
			}
			this.setStyle("contentBackgroundColor", value ? FlexGlobals.topLevelApplication.gobalSetting.readOnlyColor : _bgColor);
		}

		public function get defaultReadonly():Boolean
		{
			return controlParams.defaultReadOnly;
		}

		public function set defaultReadonly(value:Boolean):void
		{
			controlParams.defaultReadOnly=value;
		}

		public function get controlType():int
		{
			return EditableControlParams.CONST_INPUTTEXT;
		}

		public function reset():void
		{
			if (this.formInstance)
				this.readonly=this.defaultReadonly || this.formInstance.readonly;
		}

		public function set dataservice(value:FormDataService):void
		{
			controlParams.dataservice=value;
		}

		public function get dataservice():FormDataService
		{
			return controlParams.dataservice;
		}

		public function setDataBindParams(formInstance:InterfaceFormUI, formDataService:FormDataService, databaseName:String, fieldName:String, extFieldName:String):void
		{
			controlParams.formInstance=formInstance;
			controlParams.dataservice=formDataService;
			controlParams.databaseName=databaseName;
			controlParams.fieldName=fieldName;
			controlParams.extFieldName=extFieldName;
			controlParams.isBindDatasource=!(StringUtils.isEmpty(databaseName) && !StringUtils.isEmpty(fieldName));
		}

		public function get isBindDatasource():Boolean
		{
			return controlParams.isBindDatasource;
		}

		public function get formInstance():InterfaceFormUI
		{
			return controlParams.formInstance;
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////
		private var isBindEvented:Boolean=false;

		public function bindEvent():void
		{
			if (!isBindEvented)
			{
				isBindEvented=true;
				for (var eventName:String in controlParams.eventMap)
					this.addEventListener(eventName, controlParams.eventMap[eventName]);
			}
		}

		public function unBindEvent():void
		{
			if (isBindEvented)
			{
				for (var eventName:String in controlParams.eventMap)
					this.removeEventListener(eventName, controlParams.eventMap[eventName]);
				isBindEvented=false;
			}
		}

		public function registerEvent(eventName:String, eventFun:Function):void
		{
			controlParams.eventMap[eventName]=eventFun;
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////

		public var isValueChanged:Boolean=false;
	}
}
