package com.estudio.flex.component
{
	import com.estudio.flex.common.InterfaceFormUI;
	import com.estudio.flex.module.EditableControlParams;
	import com.estudio.flex.module.FormDataService;
	import com.estudio.flex.module.InterfaceEditableControl;
	import com.estudio.flex.utils.ArrayCollectionUtils;
	import com.estudio.flex.utils.StringUtils;

	import mx.collections.ArrayCollection;
	import mx.core.FlexGlobals;
	import mx.events.FlexEvent;

	import spark.components.ComboBox;

	public class ComboBoxEx extends ComboBox implements InterfaceEditableControl
	{
		private var isFirst:Boolean=false;

		private var parentComboBox:InterfaceEditableControl=null;
		private var childrenComboBox:Array=[];
		private var tempInputTextValue:String="";
		public var isFixedList:Boolean=true;

		public function ComboBoxEx()
		{
			super();
			addEventListener(FlexEvent.CREATION_COMPLETE, function(event:FlexEvent):void
			{
				textInput.setStyle("contentBackgroundColor", readonly ? FlexGlobals.topLevelApplication.gobalSetting.readOnlyColor : _bgColor);
			});
		}

		private var _bgColor:uint=0xFFFFFF;

		public function set bgColor(v:uint):void
		{
			_bgColor=v;
			if (this.textInput)
				this.textInput.setStyle("contentBackgroundColor", readonly ? FlexGlobals.topLevelApplication.gobalSetting.readOnlyColor : _bgColor);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//设置值
		public function set textInputValue(v:String):void
		{
			tempInputTextValue=v;
			callLater(syncSetTextInputText);
		}

		private function syncSetTextInputText():void
		{
			textInput.text=tempInputTextValue;
			textInput.validateNow();
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
			var result:String="";
			if (isFixedList)
			{
				if (selectedItem && selectedIndex > -1)
					result=selectedItem.data;
				else if (isBindDatasource)
				{
					result=controlParams.dataservice.getDataSetValue(controlParams.databaseName, controlParams.fieldName);
					var index:int=ArrayCollectionUtils.indexOf(dataProvider as ArrayCollection, "data", result);
					if (index != -1)
					{
						selectedIndex=index;
						selectedItem=dataProvider.getItemAt(index);
						textInputValue=selectedItem.label;
					}
					else
					{
						textInputValue=result;
					}
				}
			}
			else
			{
				if (selectedItem)
					result=selectedIndex > -1 ? selectedItem.data : selectedItem;
				else if (isBindDatasource)
					result=controlParams.dataservice.getDataSetValue(controlParams.databaseName, controlParams.fieldName);
			}
			return result;
		}

		public function get controlExtValue():String
		{
			var result:String="";
			if (isFixedList)
			{
				if (selectedItem && selectedIndex > -1)
					result=selectedItem.label;
				else if (isBindDatasource && !StringUtils.isEmpty(controlParams.extFieldName))
					result=controlParams.dataservice.getDataSetValue(controlParams.databaseName, controlParams.extFieldName);
			}
			else
			{
				if (selectedItem)
					result=selectedIndex > -1 ? selectedItem.label : selectedItem;
				else if (isBindDatasource && !StringUtils.isEmpty(controlParams.extFieldName))
					result=controlParams.dataservice.getDataSetValue(controlParams.databaseName, controlParams.extFieldName);
			}
			return result;
		}

		public function setControlValue(value:String, extValue:String, isSettingdatabase:Boolean):void
		{
			var oldValue:String=this.controlValue;
			var dataProvider:ArrayCollection=dataProvider as ArrayCollection;
			var index:int=ArrayCollectionUtils.indexOf(dataProvider, "data", value);
			if (index != -1)
			{
				selectedIndex=index;
				selectedItem=dataProvider.getItemAt(index);
				textInputValue=selectedItem.label;
			}
			else
			{
				addAdditionItem(value, extValue);
			}
			if (isSettingdatabase && isBindDatasource)
			{
				if (!StringUtils.isEmpty(controlParams.extFieldName))
					controlParams.dataservice.setDataSetValue(controlParams.databaseName, controlParams.extFieldName, extValue);
				controlParams.dataservice.setDataSetValue(controlParams.databaseName, controlParams.fieldName, value);
			}
		}

		////////////////////////////////////////////////////////////////////////////////////
		public function addAdditionItem(value:String, extValue:String):void
		{
			if (!StringUtils.isEmpty(value))
			{
				extValue=StringUtils.isEmpty(extFieldName) ? value : extValue;
				var item:Object={label: extValue, data: value, isAddition: true};
				if (dataProvider.length != 0 && dataProvider.getItemAt(dataProvider.length - 1).isAddition)
					dataProvider.removeItemAt(dataProvider.length - 1);
				dataProvider.addItem(item);
				selectedIndex=dataProvider.length - 1;
				selectedItem=item;
			}
			else
			{
				selectedIndex=-1;
				selectedItem=null;
			}
			textInputValue=extValue;
		}

		////////////////////////////////////////////////////////////////////////////////////
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
				this.enabled=!value;
			}
			if (textInput)
				textInput.setStyle("contentBackgroundColor", value ? FlexGlobals.topLevelApplication.gobalSetting.readOnlyColor : _bgColor);
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
			return EditableControlParams.CONST_COMBOBOX;
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

	}
}
