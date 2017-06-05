package com.webgis.event
{
	import flash.events.Event;

	import mx.core.FlexGlobals;
	import mx.core.UIComponent;

	public class MapWidgetEvent extends Event
	{
		public function MapWidgetEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		public var controlType:int=0;
		public var isVisible:Boolean=false;
		public var triggerComponent:UIComponent=null;

		public static var MAP_WIDGET_EVENT:String="map_widget_event";


		//////////////////////////////////////////////////////////////////////////////////
		//发布事件
		public static function dispatch(controlType:int, isVisible:Boolean, triggerComponent:UIComponent):void
		{
			var event:MapWidgetEvent=new MapWidgetEvent(MAP_WIDGET_EVENT);
			event.controlType=controlType;
			event.isVisible=isVisible;
			event.triggerComponent=triggerComponent;
			FlexGlobals.topLevelApplication.dispatchEvent(event);
		}

		//////////////////////////////////////////////////////////////////////////////////
		public static function addEventListener(fun:Function):void
		{
			FlexGlobals.topLevelApplication.addEventListener(MAP_WIDGET_EVENT, fun);
		}
		//////////////////////////////////////////////////////////////////////////////////
	}
}
