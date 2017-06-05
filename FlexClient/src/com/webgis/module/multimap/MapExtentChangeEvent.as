package com.webgis.module.multimap
{
	import com.esri.ags.Map;
	import com.esri.ags.geometry.Extent;

	import flash.events.Event;

	public class MapExtentChangeEvent extends Event
	{
		public final var MAPEXTENTCHANGE:String="MapExtentChangeEvent";

		public function MapExtentChangeEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}

		public var extent:Extent=null;
		public var map:Map=null;
	}
}
