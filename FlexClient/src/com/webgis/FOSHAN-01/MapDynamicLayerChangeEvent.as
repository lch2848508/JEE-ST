package com.webgis
{
	import flash.events.Event;

	/**
	 * 图层树变化事件 该事件
	 */
	public class MapDynamicLayerChangeEvent extends Event
	{
		public static var MAP_DYNAMIC_LAYER_CHANGE:String="MAP_DYNAMIC_LAYER_CHANGE";

		public function MapDynamicLayerChangeEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}

		private var serverInfos:Array=null;
	}
}
