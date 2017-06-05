package com.webgis
{
	import com.esri.ags.Map;
	import com.esri.ags.events.IdentifyEvent;
	import com.esri.ags.geometry.Extent;
	import com.esri.ags.geometry.Geometry;
	import com.esri.ags.layers.ArcGISDynamicMapServiceLayer;
	import com.esri.ags.layers.GraphicsLayer;
	import com.esri.ags.layers.Layer;
	import com.esri.ags.layers.supportClasses.DynamicLayerInfo;
	import com.esri.ags.layers.supportClasses.LOD;
	import com.esri.ags.layers.supportClasses.LayerDefinition;
	import com.esri.ags.layers.supportClasses.LayerDrawingOptions;
	import com.esri.ags.layers.supportClasses.LayerMapSource;
	import com.esri.ags.renderers.SimpleRenderer;
	import com.esri.ags.symbols.SimpleFillSymbol;
	import com.esri.ags.symbols.SimpleLineSymbol;
	import com.esri.ags.symbols.SimpleMarkerSymbol;
	import com.esri.ags.symbols.Symbol;
	import com.esri.ags.tasks.FindTask;
	import com.esri.ags.tasks.IdentifyTask;
	import com.esri.ags.tasks.supportClasses.FindParameters;
	import com.esri.ags.tasks.supportClasses.IdentifyParameters;
	import com.estudio.flex.utils.AlertUtils;
	import com.estudio.flex.utils.ArrayCollectionUtils;
	import com.estudio.flex.utils.ArrayUtils;
	import com.estudio.flex.utils.Convert;
	import com.estudio.flex.utils.StringUtils;
	import com.webgis.event.MapModeEvent;
	import com.webgis.layer.TileDynamicMapServiceLayer;
	import com.webgis.map.MapControl;
	import com.webgis.service.MapServiceClient;

	import flash.external.ExternalInterface;

	import mx.collections.ArrayCollection;
	import mx.containers.Tile;
	import mx.core.FlexGlobals;
	import mx.effects.Glow;

	import spark.effects.Fade;

	import uk.co.teethgrinder.elements.labels.Title;

	/**
	 * 地图图层管理类
	 */
	public class MapLayerManager
	{
		private var serverId2MapLayer:Object={}; //服务名对应的图层对象
		private var config:Object=null;
		private var overmapLayer:Layer=null;
		private var measureLayer:Layer=null;
		private var baseLayers:Array=[];
		private var layerId2LayerInfo:Object={};
		private var id2ServerInfo:Object={};
		private var dynamicLayerTree:Array=null;
		private var layerIds:Array=[];
		private var serverIdAndLayerName2LayerInfo:Object={};
		private var serverId2LegendInfo:Object={};
		private var searchResultLayer:Layer=null;
		private var isSearchResultInLayer:Boolean=false;
		private var fadeFilter:Fade=new Fade();
		private var _printServerUrl:String="";
		private var dynamicLayerServerList:Array=[];
		public var enabledLayerAlpha:Boolean=true;


		public function getPrintServerUrl():String
		{
			return _printServerUrl;
		}

		public function MapLayerManager()
		{
		}

		////////////////////////////////////////////////////////////////////////////////
		//动态切换图层
		public function switchDynamicLayerVisible(map:MapControl, data:Object, selectedFieldName):void
		{
			if (data)
				calcDynamicLayerVisible(map, id2ServerInfo[data.serverId], selectedFieldName);
			else
				for (var i:int=0; i < dynamicLayerServerList.length; i++)
					calcDynamicLayerVisible(map, dynamicLayerServerList[i], selectedFieldName);
			if (enabledLayerAlpha)
				sortDynamicLayers(map, selectedFieldName);
			map.callLater(function():void
			{
				var event:MapModeEvent=new MapModeEvent(MapModeEvent.SWITCHLAYER);
				event.map=map;
				FlexGlobals.topLevelApplication.dispatchEvent(event);
			});
		}

		/////////////////////////////////////////////////////////////////////////////////
		public function sortDynamicLayers(map:MapControl, selectedFieldName:String):void
		{
			for (var i:int=0; i < dynamicLayerServerList.length; i++)
			{
				var layer:Layer=dynamicLayerServerList[i].layers[selectedFieldName];
				if (layer != null)
				{
					map.removeLayer(layer);
					map.addLayer(layer, 1);
						//layer.alpha = 0.5
				}
			}
		}

		public function getDynamicLayers():ArrayCollection
		{
			return new ArrayCollection(dynamicLayerServerList);
		}

		/////////////////////////////////////////////////////////////////////////////////
		//显示专题图
		public function setSpecialLayer(serverId:String, layerDefines:Array, drawLayerOptions:Array, layerFieldName:String, allLayers:Array):void
		{
			var serverInfo:Object=id2ServerInfo[serverId];
			id2ServerInfo[serverId].layerDefines=layerDefines;
			id2ServerInfo[serverId].drawLayerOptions=drawLayerOptions;
			var layer:Object=serverInfo.layers[layerFieldName];
			if (layer)
			{
				if (layer is TileDynamicMapServiceLayer)
					(layer as TileDynamicMapServiceLayer).extLayers=allLayers;
				if (layer.hasOwnProperty("layerDefinitions"))
					layer.layerDefinitions=layerDefines;
				if (layer.hasOwnProperty("layerDrawingOptions"))
					layer.layerDrawingOptions=drawLayerOptions;
			}
			else
			{
				AlertUtils.alert("请首先勾选相应的服务(图层)。");
			}
		}

		/////////////////////////////////////////////////////////////////////////////////
		public function setSearchResultLayerVisible(v:Boolean):void
		{
			if (!searchResultLayer)
				return;
			searchResultLayer.visible=v;
			if (v)
				fadeFilter.play([searchResultLayer]);
			else
				fadeFilter.stop();
		}

		/////////////////////////////////////////////////////////////////////////////////
		public function setLayerFilter(map:Map, serverId:String, layerNames:String, layerType:int, filterExpress:String, mbr:Array=null, layerFieldName:String="selected"):void
		{
			var serverInfo:Object=id2ServerInfo[serverId];
			if (searchResultLayer)
				map.removeLayer(searchResultLayer);
			searchResultLayer=null;
			fadeFilter.stop();

			if (serverInfo.type != "ArcGISDynamicMapService")
				return;


			if (!StringUtils.isEmpty(filterExpress))
			{
				if (serverInfo.proxyCache)
				{
					searchResultLayer=new TileDynamicMapServiceLayer(serverInfo.url, map);
					TileDynamicMapServiceLayer(searchResultLayer).proxyURL=flash.external.ExternalInterface.call("getWebGisProxyCache") + "/-65535/"; // proxyUrl;
					TileDynamicMapServiceLayer(searchResultLayer).serverId=serverInfo.id;
				}
				else
				{
					searchResultLayer=new ArcGISDynamicMapServiceLayer(serverInfo.url);
				}

				var names:Array=layerNames.split(",");
				var layerDefines:Array=[];
				var drawLayerOptions:Array=[];
				var symbol:Symbol=layerType == 0 ? new SimpleMarkerSymbol("circle", 20, 0xEE0000) : layerType == 1 ? new SimpleLineSymbol("solid", 0xFFF0000, 1, 5) : new SimpleFillSymbol("solid", 0xFFFF00, 0.8, new SimpleLineSymbol("solid", 0xEE0000, 1, 1));
				var render:SimpleRenderer=new SimpleRenderer(symbol);
				var dynamicLayerInfos:Array=[];
				for (var i:int=0; i < names.length; i++)
				{
					var layerDefine:LayerDefinition=new LayerDefinition();
					layerDefine.layerId=names[i];
					layerDefine.definition=filterExpress;
					layerDefines.push(layerDefine);

					var drawLayerOption:LayerDrawingOptions=new LayerDrawingOptions();
					drawLayerOption.layerId=names[i];
					drawLayerOption.renderer=render;
					render.label="查询结果";
					drawLayerOptions.push(drawLayerOption);

					var layerInfo:DynamicLayerInfo=new DynamicLayerInfo();
					layerInfo.layerId=names[i];
					var layerSource:LayerMapSource=new LayerMapSource();
					layerSource.mapLayerId=layerInfo.layerId;
					layerInfo.source=layerSource;
					layerInfo.defaultVisibility=true;
					dynamicLayerInfos.push(layerInfo);
				}

				if (searchResultLayer is TileDynamicMapServiceLayer)
					Object(searchResultLayer).visibleLayers=new ArrayCollection(names);
				Object(searchResultLayer).layerDefinitions=layerDefines;
				Object(searchResultLayer).layerDrawingOptions=drawLayerOptions;
				Object(searchResultLayer).dynamicLayerInfos=dynamicLayerInfos;

				map.addLayer(searchResultLayer);

				fadeFilter.alphaFrom=1;
				fadeFilter.alphaTo=0;
				fadeFilter.repeatCount=65535;
				fadeFilter.repeatDelay=500;
				fadeFilter.play([searchResultLayer]);
				//fadeFilter.play([searchResultLayer],true);
				isSearchResultInLayer=true;
				if (mbr)
				{
					var m:Extent=new Extent(mbr[0], mbr[1], mbr[2], mbr[3]);
					if (!map.extent.intersection(m))
						map.extent=m;
				}
			}

		}

		////////////////////////////////////////////////////////////////////////////////
		//获取需要显示图例的图层
		public function getLegendLayers(selectedFieldName:String):Array
		{
			var legendServers:Array=[];
			for (var k:String in id2ServerInfo)
			{
				var serverInfo:Object=id2ServerInfo[k];
				var layer:Layer=serverInfo.layers[selectedFieldName] as Layer;
				if (layer && layer.visible && (serverInfo.type == "ArcGISDynamicMapService" || serverInfo.type == "ArcGISTiledMapService"))
				{
					var serverItem:Object={proxyCache: serverInfo.proxyCache, layers: {}, id: serverInfo.id, url: serverInfo.url, label: serverInfo.name, onlyServer: serverInfo.onlyServer};
					if (!serverInfo.onlyServer)
					{
						for (var i:int=0; i < serverInfo.layerNames.length; i++)
						{
							var layerInfo:Object=serverInfo.layerNames[i];
							if (layerInfo[selectedFieldName])
								serverItem.layers[layerInfo.layerName]=layerInfo.label;
						}
					}
					legendServers.push(serverItem);
				}
			}
			return legendServers;
		}

		///////////////////////////////////////////////////////////////////////////////
		//计算服务需要显示的图层数组
		private function calcDynamicLayerVisible(map:MapControl, serverInfo:Object, selectedFieldName:String):void
		{
			if (!serverInfo)
				return;
			var visibleLayers:Array=[];
			for (var i:int=0; i < serverInfo.layerNames.length; i++)
			{
				var layerInfo:Object=serverInfo.layerNames[i];
				if (layerInfo[selectedFieldName])
					visibleLayers.push(layerInfo.layerName);
			}

			if (visibleLayers.length == 0)
			{
				map.removeLayer(serverInfo.layers[selectedFieldName]);
				serverInfo.layers[selectedFieldName]=null;
			}
			else
			{
				var layer:Object=null;
				if (!serverInfo.layers[selectedFieldName])
				{
					layer=map.createLayer(serverInfo.type, serverInfo.url, serverInfo);
					serverInfo.layers[selectedFieldName]=layer;
					map.addLayer(serverInfo.layers[selectedFieldName]);
//					if (map.loaded && serverInfo.initExtent && !map.extent.intersects(serverInfo.initExtent))
//					{
//						var mapBaseLayer:Object=map.layers.getItemAt(0) as Object;
//						if (mapBaseLayer.hasOwnProperty("fullExtent") && !mapBaseLayer["fullExtent"].intersects(serverInfo.initExtent))
//							map.extent=serverInfo.initExtent;
//					}
				}
				if (serverInfo.layers[selectedFieldName].hasOwnProperty("visibleLayers") && visibleLayers.join("") != "*")
				{
					layer=serverInfo.layers[selectedFieldName] as Layer;
					if (layer is ArcGISDynamicMapServiceLayer)
						Object(layer).dynamicLayerInfos=createDynamicLayerInfos(visibleLayers);
					else
						Object(layer).visibleLayers=new ArrayCollection(visibleLayers);
				}

				if (layer)
				{
					layer.id="layer_" + serverInfo.id;
					if (layer && serverInfo.dynamicLayerInfos && layer.hasOwnProperty("dynamicLayerInfos"))
						layer.dynamicLayerInfos=serverInfo.dynamicLayerInfos;
					if (layer && serverInfo.layerDefines && layer.hasOwnProperty("layerDefinitions"))
						layer.layerDefinitions=serverInfo.layerDefines;
					if (layer && serverInfo.drawLayerOptions && layer.hasOwnProperty("layerDrawingOptions"))
						layer.layerDrawingOptions=serverInfo.drawLayerOptions;
					if (enabledLayerAlpha)
						layer.alpha=serverInfo.hasOwnProperty("layerAlpha") && serverInfo.layerAlpha ? serverInfo.layerAlpha / 100 : 1;
				}
			}
		}

		///////////////////////////////////////////////////////////////////////////////////
		public static function createDynamicLayerInfos(layers:Array):Array
		{
			var result:Array=[];
			for (var i:int=0; i < layers.length; i++)
			{
				var layerInfo:DynamicLayerInfo=new DynamicLayerInfo();
				layerInfo.layerId=layers[i];
				var layerSource:LayerMapSource=new LayerMapSource();
				layerSource.mapLayerId=layerInfo.layerId;
				layerInfo.source=layerSource;
				layerInfo.defaultVisibility=true;
				result.push(layerInfo);
			}
			return result;
		}

		/////////////////////////////////////////////////////////////////////////////////
		/**
		 * 初始化
		 */
		public function init(map:MapControl, config:Object):void
		{
			this.config=config;

			//第一步创建地图图层
			createMapLayers(map);

			//处理图层树
			createDynamicLayerTree(map);

			//排序
			sortDynamicLayerServerList();
		}

		/**
		 * 创建地图图层
		 */
		private function createMapLayers(map:MapControl):void
		{
			var hasBaseMapLayer:Boolean=false;

			//处理底图
			var baseLayers:Array=config.appConfig.baseLayers;
			if (!baseLayers)
				baseLayers=[];
			for (var i:int=0; i < baseLayers.length; i++)
			{
				var layerInfo:Object=baseLayers[i];
				if (layerInfo.type == "GeometryService")
				{
					var proxyURL:String=layerInfo.proxyCache ? flash.external.ExternalInterface.call("getWebGisProxyCache") + "/-65535/" : null;
					map.setGeometryServiceUrl(layerInfo.url, proxyURL);
				}
				else
				{
					//if (!map.supportLayerType(layerInfo.type))
					//	continue;

					if (layerInfo.purpose == 1)
						overmapLayer=map.createLayer(layerInfo.type, layerInfo.url, layerInfo);
					else if (layerInfo.purpose == 2)
						measureLayer=map.createLayer(layerInfo.type, layerInfo.url, layerInfo);
					else if (layerInfo.purpose == 4)
						_printServerUrl=layerInfo.url;
					else
					{
						this.baseLayers.push(layerInfo);
						if (!hasBaseMapLayer)
						{
							map.addLayer(map.createLayer(layerInfo.type, layerInfo.url, layerInfo));
							map.initLabelLayers(layerInfo);
							hasBaseMapLayer=true;
						}
					}
				}
			}
		}

		////////////////////////////////////////////////////////////////////////////
		//字符串转化为Extent
		private function generateInitExtent(str:String):Extent
		{
			if (StringUtils.isEmpty(str))
				return null;
			var s:Array=str.split(",");
			return new Extent(s[0] * 1, s[1] * 1, s[2] * 1, s[3] * 1);
		}

		/**
		 * 生成动态图层树
		 */
		private function createDynamicLayerTree(map:MapControl):void
		{
			//处理可以叠加的图层 动态图层
			var serverInfos:Array=config.layerConfig.serverInfo;
			if (!serverInfos)
				serverInfos=[];
			for (var i:int=0; i < serverInfos.length; i++)
			{
				var serverInfo:Object=serverInfos[i];
				if (!map.supportLayerType(serverInfo.type))
					continue;
				var dynamicServerInfo:Object={id: serverInfo.id, layerNames: []};
				dynamicServerInfo.ident=serverInfo.ident;
				dynamicServerInfo.query=serverInfo.query;
				dynamicServerInfo.type=serverInfo.type;
				dynamicServerInfo.url=serverInfo.url;
				dynamicServerInfo.maxLayerType=serverInfo.maxLayerType;
				dynamicServerInfo.proxyCache=serverInfo.proxyCache;
				dynamicServerInfo.proxyUrl=serverInfo.proxyUrl;
				dynamicServerInfo.name=serverInfo.name;
				dynamicServerInfo.initExtent=generateInitExtent(serverInfo.initExtent);
				dynamicServerInfo.layers={};
				id2ServerInfo[serverInfo.id]=dynamicServerInfo;
				dynamicLayerServerList.push(dynamicServerInfo);
			}

			//处理图层信息
			var layerInfos:Array=config.layerConfig.layerInfos;
			if (!layerInfos)
				layerInfos=[];
			for (var i:int=0; i < layerInfos.length; i++)
			{
				var layerInfo:Object=layerInfos[i];
				layerId2LayerInfo[layerInfo.id]=layerInfo;
			}

			//图集项
			var layerItems:Object=config.layerConfig.layerTree;
			var list:Array=[];
			ArrayCollectionUtils.TreeData2List(layerItems && layerItems.children ? layerItems.children : [], list);
			for (var i:int=0; i < list.length; i++)
			{
				var item:Object=list[i];
				if (item.type == 0 || (item.layerType != 4 && !layerId2LayerInfo[item.layerId]))
					continue;
				var serverId:Number=item.layerType == 4 ? item.layerId : layerId2LayerInfo[item.layerId].serverId;
				item.layerName=item.layerType == 4 ? "*" : layerId2LayerInfo[item.layerId].name;
				item.ident=item.layerType == 4 ? id2ServerInfo[serverId].ident : layerId2LayerInfo[item.layerId].ident;
				item.query=item.layerType == 4 ? id2ServerInfo[serverId].query : layerId2LayerInfo[item.layerId].query;
				item.sortorder=item.layerType == 4 ? -1 : layerId2LayerInfo[item.layerId].sortorder;
				item.serverId=serverId;
				if (id2ServerInfo[serverId])
					id2ServerInfo[serverId].layerNames.push(item);
				if (item.layerType == 4)
					id2ServerInfo[serverId].onlyServer=true;
				layerIds.push(item.layerId);
				serverIdAndLayerName2LayerInfo[serverId + "-" + item.layerName]=item;
			}
			for (var k:String in id2ServerInfo)
			{
				var serverInfo:Object=id2ServerInfo[k];
				serverInfo.layerAlpha=config.serverId2Alpha ? config.serverId2Alpha[k] : 1;
				if (serverInfo.layerNames && serverInfo.layerNames && serverInfo.layerNames.length >= 2) //根据mxd中的顺序进行排序
				{
					serverInfo.layerNames.sort(function(item1:Object, item2:Object):int
					{
						return item1.layerName * 1 - item2.layerName * 1;
					});
				}
			}
			dynamicLayerTree=[config.layerConfig.layerTree];
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		//排序
		private function sortDynamicLayerServerList():void
		{
			var sortConfig:Array=config.layerSortConfig;
			if (!sortConfig)
				sortConfig=[];
			var getSortConfigIndex:Function=function(serverId:String):int
			{
				var index:int=-1;
				for (var i:int=0; i < sortConfig.length; i++)
				{
					if (Convert.str2Number("" + sortConfig[i]) == Convert.str2Number("" + serverId))
					{
						index=i;
						break;
					}
				}
				return index;
			};

			var compareFunction:Function=function(serverInfo1:Object, serverInfo2:Object):int
			{
				var index1:int=getSortConfigIndex(serverInfo1.id);
				var index2:int=getSortConfigIndex(serverInfo2.id);
				if (index1 == -1 || index2 == -1)
					return serverInfo1.maxLayerType - serverInfo2.maxLayerType;
				else
					return index1 - index2;
			};

			dynamicLayerServerList.sort(compareFunction);
		}


		//////////////////////////////////////////////////////////////////////////////////////////////////

		/**
		 * 鹰眼图层
		 */
		public function getOvermapLayer():Layer
		{
			return overmapLayer;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////

		/**
		 * 测量长度面积图层
		 */
		public function getMeasureLayer():Layer
		{
			return measureLayer;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * 获取动态图层树
		 */
		public function getDynamicLayerTree():Array
		{
			return dynamicLayerTree;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		public function getQueryLayerIds(map:MapControl, selectedFieldName:String):Array
		{
			return getDynamicVisibleLayerTree(map, "query", selectedFieldName);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		public function getIdentifyLayerIds(map:MapControl, selectedFieldName:String):Array
		{
			return getDynamicVisibleLayerTree(map, "ident", selectedFieldName);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		//获取动态图层树
		private function getDynamicVisibleLayerTree(map:MapControl, purpose:String, selectedFieldName:String):Array
		{
			var layerIds:Array=[];
			var serverIds:Array=[];
			for (var k:String in id2ServerInfo)
			{
				var serverInfo:Object=id2ServerInfo[k];
				if (!serverInfo.onlyServer)
				{
					if (serverInfo.layers[selectedFieldName] && serverInfo.layers[selectedFieldName].visible)
					{
						for (var i:int=0; i < serverInfo.layerNames.length; i++)
						{
							var layerInfo:Object=serverInfo.layerNames[i];
							if (layerInfo[selectedFieldName] && layerInfo[purpose])
								layerIds.push(layerInfo.layerId);
						}
					}
				}
				else if (serverInfo.layers[selectedFieldName] && serverInfo.layers[selectedFieldName].visible && ArrayUtils.indexOf(map.layerIds, serverInfo.layers[selectedFieldName].id) != -1)
					serverIds.push(k);
			}
			return [layerIds, serverIds];
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		public function getLayerIds():Array
		{
			return layerIds;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//根据服务URL及图层名称获取图层信息
		public function getLayerInfoByServerIdAndLayerName(serverId:String, layerName:String):Object
		{
			return serverIdAndLayerName2LayerInfo[serverId + "-" + layerName];
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		public function getBaseLayers():Array
		{
			return this.baseLayers;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		public function getServerLegendInfo(serverId:String):Object
		{
			return serverId2LegendInfo[serverId];
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		public function registerServerLegendInfo(serverId:String, info:Object):void
		{
			serverId2LegendInfo[serverId]=info;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////

	}
}
