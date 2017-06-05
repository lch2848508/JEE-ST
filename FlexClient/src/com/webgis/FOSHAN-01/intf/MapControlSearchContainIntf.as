package com.webgis.intf
{
	import com.esri.ags.Graphic;
	import com.esri.ags.geometry.Geometry;

	public interface MapControlSearchContainIntf
	{
		function clear():void;
		function registerSearchContent(obj:Object,whereGeometry:Geometry=null):void;
	}
}
