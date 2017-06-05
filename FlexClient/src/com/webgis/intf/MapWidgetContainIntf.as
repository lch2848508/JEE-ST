package com.webgis.intf
{
	import com.esri.ags.Map;
	
	import mx.core.UIComponent;

	/**
	 * Map控件容器接口
	 */
	public interface MapWidgetContainIntf
	{
		function getMapContain():UIComponent; //地图
		function getNavigatorContain():UIComponent; //导航面板容器
		function getOverviewMapContain():UIComponent; //鹰眼图容器
		function getDynamicLayerTreeContain():UIComponent; //动态图层树状列表
		function getToolbarContain():UIComponent; //工具条容器
		function getNavigatorAreaContain():UIComponent; //区域导航面板
		function getSearchResultContain():UIComponent; //查询结果
		function getFavoriteContain():UIComponent; //收藏夹
		function getShareContain():UIComponent; //分享地图
		function getStatisticContain():UIComponent; //社会经济数据
		function getPlottingContain():UIComponent;
		function getMISLayerContain():UIComponent;
		function getSpatialAnalyContain():UIComponent; //空间分析
		function setParams(v:Object):void;

		//显示隐藏附加信息
		function showExtContain(caption:String, control:UIComponent,closedFunction:Function=null):void;
		function hideExtContain(caption:String, control:UIComponent):void;

		//查询显示框
		function get searchContain():MapControlSearchContainIntf;
		function set searchContain(value:MapControlSearchContainIntf):void;

		//多图比对
		function get multiMap():MapControlMultiMapIntf;
		function set multiMap(value:MapControlMultiMapIntf):void;

		//收藏夹
		function get favorite():MapControlFavorite;
		function set favorite(value:MapControlFavorite):void;
		
		//分享地图
		function get share():MapControlShare;
		function set share(value:MapControlShare):void;
		
		function get statistic():MapControlStatisticIntf;
		function set statistic(value:MapControlStatisticIntf):void;

		function focusLayerTree():void;
		function focusSearchContain():void;
		function focusFavorite():void;
		function focusShare():void;
		function getContainByLabel(label:String):UIComponent;
	}



}
