package com.webgis.intf
{
	import com.esri.ags.Map;
	import com.webgis.service.MapServiceClient;

	public interface MapWidgetIntf
	{
		function get map():Object; //获取地图
		function set map(value:Object):void; //设置地图
		function setProperty(obj:Object):void; //设置属性
		function set mapApp(value:Object):void;
		function get mapApp():Object;
		function set widgetContain(value:MapWidgetContainIntf):void;
		function get widgetContain():MapWidgetContainIntf;
		function get mapServiceClient():MapServiceClient;
		function set mapServiceClient(value:MapServiceClient):void;
		function setParams(params:Object):void;
	}
}
