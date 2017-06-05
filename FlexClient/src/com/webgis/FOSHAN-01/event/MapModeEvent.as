package com.webgis.event
{
	import com.webgis.map.MapControl;
	
	import flash.events.Event;

	public class MapModeEvent extends Event
	{
		public function MapModeEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}

		public var map:MapControl = null;
		
		public static var PAN:String="panMode";
		public static var MEASURE:String="measureMode";
		public static var IDENTIFY:String="identifyMode";
		public static var SEARCH:String="searchMode";
		public static var SWITCHLAYER:String="switchLayer";
		public static var CHANGEBASELAYER:String = "changeBaseMap";
	}
}
