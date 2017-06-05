package com.webgis.intf
{
	import com.esri.ags.geometry.Extent;
	import com.esri.ags.geometry.MapPoint;
	import com.webgis.MapLayerManager;

	public interface MapControlMultiMapIntf
	{
		function initParams(value:MapLayerManager):void;
		function centerAt(p:MapPoint, level:int):void;
	}
}
