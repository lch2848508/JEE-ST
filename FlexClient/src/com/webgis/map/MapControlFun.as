import com.esri.ags.Graphic;
import com.esri.ags.Map;
import com.esri.ags.SpatialReference;
import com.esri.ags.events.DrawEvent;
import com.esri.ags.events.GeometryServiceEvent;
import com.esri.ags.events.MapMouseEvent;
import com.esri.ags.geometry.Extent;
import com.esri.ags.geometry.Geometry;
import com.esri.ags.geometry.MapPoint;
import com.esri.ags.geometry.Polygon;
import com.esri.ags.geometry.Polyline;
import com.esri.ags.layers.ArcGISDynamicMapServiceLayer;
import com.esri.ags.layers.ArcGISTiledMapServiceLayer;
import com.esri.ags.layers.GraphicsLayer;
import com.esri.ags.layers.Layer;
import com.esri.ags.layers.TiledMapServiceLayer;
import com.esri.ags.layers.WMSLayer;
import com.esri.ags.symbols.Symbol;
import com.esri.ags.symbols.TextSymbol;
import com.esri.ags.tasks.GeometryService;
import com.esri.ags.tasks.PrintTask;
import com.esri.ags.tasks.supportClasses.AreasAndLengthsParameters;
import com.esri.ags.tasks.supportClasses.AreasAndLengthsResult;
import com.esri.ags.tasks.supportClasses.CalculationType;
import com.esri.ags.tasks.supportClasses.LengthsParameters;
import com.esri.ags.tasks.supportClasses.ProjectParameters;
import com.esri.ags.tools.DrawTool;
import com.esri.ags.tools.NavigationTool;
import com.esri.ags.utils.GeometryUtil;
import com.estudio.flex.utils.ArrayUtils;
import com.estudio.flex.utils.Convert;
import com.estudio.flex.utils.StringUtils;
import com.webgis.event.MapModeEvent;
import com.webgis.layer.TianDiTu4FSImage;
import com.webgis.layer.TianDiTu4FSVector;
import com.webgis.layer.TianDiTuLayer;
import com.webgis.layer.TianDiTuLayerMercator;
import com.webgis.layer.TileDynamicMapServiceLayer;
import com.webgis.map.MapControl;
import com.webgis.service.MapServiceClient;

import flash.display.Graphics;
import flash.external.ExternalInterface;
import flash.text.TextFormat;
import flash.utils.setTimeout;

import mx.collections.ArrayCollection;
import mx.containers.TitleWindow;
import mx.controls.Alert;
import mx.core.FlexGlobals;
import mx.core.UIComponent;
import mx.events.FlexEvent;
import mx.rpc.AsyncResponder;
import mx.rpc.Fault;

import spark.components.CheckBox;

import flashx.textLayout.events.ModelChange;

import uk.co.teethgrinder.charts.Area;

private var _maxLevel:int=12;
private var isIdentifyMode:Boolean=false;
private var isTrafficFlowMode:Boolean=false;
private var isMeasureMode:Boolean=false;
private var drawCallFunction:Function=null;
private var lastCommonGraphicsLayerFeature:Graphic=null;
private var areaNavigatorLayer:GraphicsLayer=null;
private var areaNavigatorFeatures:Array=null;
////////////////////////////////////////////////////////////////////////
public var identifyFuntion:Function=null;
public var trafficFuntion:Function=null;
public function get maxLevel():int
{
	return _maxLevel;
}

public function set maxLevel(value:int):void
{
	_maxLevel=value;
}

// ActionScript file
//完善函数并加上函数的
////////////////////////////////////////////////////////////////////////////////////
override public function zoomIn():void
{
	super.zoomIn();
}

////////////////////////////////////////////////////////////////////////////////////
override public function zoomOut():void
{
	super.zoomOut();
}

////////////////////////////////////////////////////////////////////////////////////
//前一视图
public function zoomToPrevExtent():void
{
	navigationTool.zoomToPrevExtent();
}

////////////////////////////////////////////////////////////////////////////////////
//后一视图 
public function zoomToNextExtent():void
{
	navigationTool.zoomToNextExtent();
}

////////////////////////////////////////////////////////////////////////////////////
//拉框放大
public function zoom_In():void
{
	navigationTool.activate(NavigationTool.ZOOM_IN);
}

////////////////////////////////////////////////////////////////////////////////////
//拉框缩小
public function zoom_Out():void
{
	navigationTool.activate(NavigationTool.ZOOM_OUT);
}

////////////////////////////////////////////////////////////////////////////////////
//平移
public function pan():void
{
	//navigationTool.activate(NavigationTool.PAN);
}

////////////////////////////////////////////////////////////////////////////////////
//使操作无效的
public function deactivate():void
{
	navigationTool.deactivate();
}

///////////////////////////////////////////////////////////////////////////////////
//定义创建地图方法供鹰眼图使用图
public function createLayer(type:String, url:String, serverInfo:Object=null):Layer
{
	var layer:Layer=null;
	url=StringUtils.nvl(url, "");
	if (StringUtils.equal("ArcGISDynamicMapService", type))
	{
		if (serverInfo.proxyCache)
		{
			layer=new TileDynamicMapServiceLayer(url, this);
			TileDynamicMapServiceLayer(layer).proxyURL=flash.external.ExternalInterface.call("getWebGisProxyCache") + "/" + serverInfo.id + "/"; // proxyUrl;
			TileDynamicMapServiceLayer(layer).serverId=serverInfo.id;
		}
		else
		{
			layer=new ArcGISDynamicMapServiceLayer(url);
		}
	}
	else if (StringUtils.equal("ArcGISTiledMapService", type))
	{
		layer=new ArcGISTiledMapServiceLayer(url);
		if (serverInfo.proxyCache)
			ArcGISTiledMapServiceLayer(layer).proxyURL=flash.external.ExternalInterface.call("getWebGisProxyCache") + "/" + serverInfo.id + "/";
	}
	else if (StringUtils.equal("WMSService", type))
	{
		layer=new WMSLayer(url);
			//if (layers != null)
			//	WMSLayer(layer).visibleLayers=new ArrayCollection(layers)
	}
	else if (StringUtils.equal("天地图经纬度", type))
	{
		if (serverInfo.proxyCache)
			url=flash.external.ExternalInterface.call("getWebGisProxyCache") + "/" + serverInfo.id + "/?" + url;
		var extParams:Object=serverInfo.extParams;
		layer=new TianDiTuLayer(url, extParams.layerId, extParams.tileMatrixSetId, extParams.imageFormat, extParams.mapStyle, Convert.str2int(extParams.minLevel, 0), Convert.str2int(extParams.maxLevel, 20), serverInfo.initExtent);
	}
	else if (StringUtils.equal("天地图墨卡托", type))
	{
		if (serverInfo.proxyCache)
			url=flash.external.ExternalInterface.call("getWebGisProxyCache") + "/" + serverInfo.id + "/?" + url;
		var extParams:Object=serverInfo.extParams;
		layer=new TianDiTuLayerMercator(url, extParams.layerId, extParams.tileMatrixSetId, extParams.imageFormat, extParams.mapStyle, Convert.str2int(extParams.minLevel, 0), Convert.str2int(extParams.maxLevel, 20), serverInfo.initExtent);
	}
	else if (StringUtils.equal("佛山天地图影像", type))
	{

		if (serverInfo.proxyCache)
			url=flash.external.ExternalInterface.call("getWebGisProxyCache") + "/" + serverInfo.id + "/?" + url;
		var extParams:Object=serverInfo.extParams;
		layer=new TianDiTu4FSImage();
		TianDiTu4FSImage(layer).proxyURL=url;
	}
	else if (StringUtils.equal("佛山天地图矢量", type))
	{
		if (serverInfo.proxyCache)
			url=flash.external.ExternalInterface.call("getWebGisProxyCache") + "/" + serverInfo.id + "/?" + url;
		var extParams:Object=serverInfo.extParams;
		layer=new TianDiTu4FSVector();
		TianDiTu4FSVector(layer).proxyURL=url;
	}
	return layer;
}

//////////////////////////////////////////////////////////////////////////////////
private var checkbox2LabelLayerInfo:Object={};

public function initLabelLayers(layerInfo:Object):void
{
	labelLayerContain.removeAllElements();
	for (var k:String in checkbox2LabelLayerInfo)
	{
		var childLayerInfo:Object=checkbox2LabelLayerInfo[k];
		if (childLayerInfo.labelLayer)
		{
			this.removeLayer(childLayerInfo.labelLayer)
			childLayerInfo.labelLayer=null;
		}
	}

	labelLayerContain.visible=layerInfo.children != null;
	if (!labelLayerContain.visible)
		return;
	if (labelLayerContain.parent == null)
		(this.parent as Object).addElement(labelLayerContain);

	var controls:Array=layerInfo.subLayerControls;
	if (controls == null)
	{
		controls=[];
		for (var i:int=0; i < layerInfo.children.length; i++)
		{
			var childLayerInfo:Object=layerInfo.children[i];
			var checkBox:CheckBox=new CheckBox();
			checkBox.label=childLayerInfo.label;
			checkBox.addEventListener(Event.CHANGE, event4LabelLayerChange);
			controls.push(checkBox);
			checkbox2LabelLayerInfo[checkBox.uid]=childLayerInfo;
		}
		layerInfo.subLayerControls=controls;
	}
	for (var i:int=0; i < controls.length; i++)
	{
		labelLayerContain.addElement(controls[i]);
		switchLabelLayerVisibleByCheckBox(controls[i] as CheckBox, controls[i].selected);
	}
}

//////////////////////////////////////////////////////////////////////////////////
public function resetMapParent():void
{
	var p:Object=this.parent;
	if (labelLayerContain.visible)
	{
		(labelLayerContain.parent as Object).removeElement(labelLayerContain);
		p.addElement(labelLayerContain);
	}
}
//////////////////////////////////////////////////////////////////////////////////
private function event4LabelLayerChange(event:Event):void
{
	switchLabelLayerVisibleByCheckBox(event.currentTarget as CheckBox, event.currentTarget.selected);
}
private function switchLabelLayerVisibleByCheckBox(checkbox:CheckBox, isSelected:Boolean):void
{
	var layerInfo:Object=checkbox2LabelLayerInfo[checkbox.uid];
	if (isSelected)
	{
		var index:int=1;
		for (var i:int=0; i < labelLayerContain.numElements; i++)
		{
			if (checkbox == labelLayerContain.getElementAt(i))
				continue;
			index+=(labelLayerContain.getElementAt(i) as CheckBox).selected ? 1 : 0;
		}
		var layer:Layer=createLayer(layerInfo.type, layerInfo.url, layerInfo);
		layer.id="Layer" + (labelLayerContain.getElementIndex(checkbox) + 1);
		this.addLayer(layer, index);
		layerInfo.labelLayer=layer;
	}
	else if (layerInfo.labelLayer)
	{
		removeLayer(layerInfo.labelLayer as Layer);
		layerInfo.labelLayer=null;
	}
}

//////////////////////////////////////////////////////////////////////////////////
public function supportLayerType(type:String):Boolean
{
	var types:Array=["ArcGISDynamicMapService", "ArcGISTiledMapService", "WMSService"];
	return ArrayUtils.indexOf(types, type) != -1;
}

///////////////////////////////////////////////////////////////////////////////////
//定义地图放大缩小级数
private function layerShowHandler(event:FlexEvent):Array
{
	// update the LODs/zoomslider to use/show the levels for the selected base map
	if (event.target as TiledMapServiceLayer)
	{
		var tiledLayer:TiledMapServiceLayer=event.target as TiledMapServiceLayer;
		return tiledLayer.tileInfo.lods;
	}
	else
	{
		return null;
	}
}

////////////////////////////////////////////////////////////////////////////////////
//拖动模式

public function activePanMode():void
{
	isIdentifyMode=false;
	isMeasureMode=false;
	isTrafficFlowMode=false;
	drawTool.deactivate();
	FlexGlobals.topLevelApplication.dispatchEvent(new MapModeEvent(MapModeEvent.PAN, true, false));
}

////////////////////////////////////////////////////////////////////////////////////
//放大模式
public function activeZoominMode():void
{
	isIdentifyMode=false;
	isTrafficFlowMode=false;
	drawTool.deactivate();
	navigationTool.activate(NavigationTool.ZOOM_IN);
}

////////////////////////////////////////////////////////////////////////////////////
//缩小模式
public function activeZoomoutMode():void
{
	isIdentifyMode=false;
	isTrafficFlowMode=false;
	drawTool.deactivate();
	navigationTool.activate(NavigationTool.ZOOM_OUT);
}

////////////////////////////////////////////////////////////////////////////////////

public function activeMeasureMode(isArea:Boolean):void
{
	clearFeature();
	isMeasureMode=true;
	isIdentifyMode=false;
	isTrafficFlowMode=false;
	navigationTool.deactivate();
	drawTool.activate(isArea ? DrawTool.POLYGON : DrawTool.POLYLINE);
	if (ArrayUtils.contain(this.layerIds, "measureGraphicsLayers"))
		this.removeLayer(measureGraphicsLayer);
	this.addLayer(measureGraphicsLayer);
	FlexGlobals.topLevelApplication.dispatchEvent(new MapModeEvent(MapModeEvent.MEASURE));
}

////////////////////////////////////////////////////////////////////////////////////////
public function activeIdentifyMode():void
{
	isTrafficFlowMode=false;
	isIdentifyMode=true;
	navigationTool.deactivate();
	drawTool.deactivate();
	clearFeature();
	FlexGlobals.topLevelApplication.dispatchEvent(new MapModeEvent(MapModeEvent.IDENTIFY));
}
public function activeTrafficFlowMode():void
{
	isIdentifyMode=false;
	isTrafficFlowMode=true;
	navigationTool.deactivate();
	drawTool.deactivate();
	clearFeature();
	FlexGlobals.topLevelApplication.dispatchEvent(new MapModeEvent(MapModeEvent.TRAFFICFLOW));
}
////////////////////////////////////////////////////////////////////////////////////
public function activeSpatialSearchMode():void
{
	isMeasureMode=false;
	isIdentifyMode=false;
	isTrafficFlowMode=false;
	navigationTool.deactivate();
	drawTool.deactivate();
	if (!ArrayUtils.contain(this.layerIds, "measureGraphicsLayers"))
		this.addLayer(measureGraphicsLayer);
	clearFeature();
}

////////////////////////////////////////////////////////////////////////////////////
public function activeDrawMode(tag:String, callFunction:Function, layer:GraphicsLayer=null):void
{
	drawCallFunction=callFunction;
	if (StringUtils.isEmpty(tag))
	{
		//navigationTool.activate(NavigationTool.PAN);
		drawTool.deactivate();
		if (lastCommonGraphicsLayerFeature != null)
		{
			measureGraphicsLayer.clear();
			measureGraphicsLayer.add(lastCommonGraphicsLayerFeature);
			drawCallFunction(measureGraphicsLayer, lastCommonGraphicsLayerFeature);
		}
	}
	else
	{
		//navigationTool.deactivate();
		drawTool.graphicsLayer=layer == null ? measureGraphicsLayer : layer;
		drawTool.activate(tag);
	}

}

////////////////////////////////////////////////////////////////////////////////////
protected function event4MapClickHandler(event:MapMouseEvent):void
{
	if (isIdentifyMode && identifyFuntion != null)
		identifyFuntion(event.mapPoint, this);
	if(isTrafficFlowMode&&trafficFuntion!=null)
		trafficFuntion(event.mapPoint,this);
}

////////////////////////////////////////////////////////////////////////////////////
protected function event4DrawEnd(event:DrawEvent):void
{
	var geometry:Geometry=event.graphic.geometry;
	if (isMeasureMode)
	{
		if (StringUtils.isEmpty(geometryService.url))
			return;
		if (geometry is Polyline) //测距离
			mapServiceClient.length(geometry, measureGraphicsLayer);
		else if (geometry is Polygon) //测面积
			mapServiceClient.areaAndLength(geometry, measureGraphicsLayer);
	}
	else if (drawCallFunction != null)
		drawCallFunction(measureGraphicsLayer, event.graphic);
}

protected function drawTool_drawStartHandler(event:DrawEvent):void
{
	if (!isMeasureMode)
		measureGraphicsLayer.clear();
}

/////////////////////////////////////////////////////////////////////////////////////////////
//点选数据
public function splashFeature(feature:Graphic, isZoom:Boolean, isClear:Boolean, isShowAttributes:Boolean=false, featureCaption:String="", offsetHeight:int=0):void
{
	removeLayer(commonGraphicsLayer);
	commonGraphicsLayer.clear();
	lastCommonGraphicsLayerFeature=null;
	if (feature && feature.geometry)
	{
		if (!isClear)
			lastCommonGraphicsLayerFeature=feature;

		if (isZoom)
		{
			var p:MapPoint=feature.geometry is MapPoint ? (Geometry.fromJSON(MapPoint(feature.geometry).toJSON()) as MapPoint) : feature.geometry.extent.center;
			if (feature.geometry is MapPoint)
			{
				var resolution:Number=this.lods[this.level].resolution;
				p.y-=offsetHeight * resolution;
				zoomTo(p);
				
			}
			else
			{
				var bestResolution:Number=Math.max(feature.geometry.extent.width / this.width, feature.geometry.extent.height / (this.height - 300));
				var tempExtent:Extent=new Extent();
				tempExtent.xmin=p.x - bestResolution * width / 2;
				tempExtent.xmax=p.x + bestResolution * width / 2;
				tempExtent.ymin=p.y - bestResolution * height / 2 - offsetHeight * bestResolution;
				tempExtent.ymax=p.y + bestResolution * height / 2 + offsetHeight * bestResolution;
				zoomTo(tempExtent);
			}

		}

		feature.symbol=getSymbolFunction(feature);
		commonGraphicsLayer.add(feature);
		addLayer(commonGraphicsLayer);
		glow.play();
		if (isClear)
		{
			setTimeout(function():void
			{
				clearFeature();
			}, (glow.repeatCount + 1) * glow.duration);
		}

		if (isShowAttributes)
		{
			var labelPoint:MapPoint=null;
			if (feature.geometry is MapPoint)
			{
				labelPoint=feature.geometry as MapPoint;
				showAttributeWindow(labelPoint, feature, featureCaption);
			}
			else if (feature.geometry is Polyline)
			{
				var path:Object=(feature.geometry as Polyline).paths[0];
				var index:int=path.length / 2;
				labelPoint=path[index];
				showAttributeWindow(labelPoint, feature, featureCaption);
			}
			else if (feature.geometry is Polygon)
			{
				var ring:Object=(feature.geometry as Polygon).rings[0];
				var index:int=ring.length / 2;
				labelPoint=ring[index];
				showAttributeWindow(labelPoint, feature, featureCaption);
			}
		}
	}
}

public function splashFeatures(features:Array, isZoom:Boolean, isClear:Boolean):void
{
	removeLayer(commonGraphicsLayer);
	commonGraphicsLayer.clear();
	if (features == null || features.length == 0)
		return;

	var extent:Extent=null;
	var resolution:Number=this.lods[this.lods.length - 1].resolution;
	for each (var feature:Graphic in features)
	{
		feature.symbol=getSymbolFunction(feature);
		commonGraphicsLayer.add(feature);
		var geometryExtent:Extent=feature.geometry.extent;
		if (geometryExtent == null)
		{
			var tempPoint:MapPoint=MapPoint(feature.geometry);
			geometryExtent=new Extent(tempPoint.x - resolution, tempPoint.y - resolution, tempPoint.x + resolution, tempPoint.y + resolution);
		}
		if (extent == null)
			extent=geometryExtent;
		else
			extent=geometryExtent.union(extent);
	}

	var isZoom:Boolean=false;
	var bestResolution:Number=Math.max(extent.width / this.width, extent.height / (this.height - 300));
	var p:MapPoint=extent.center;
	var tempExtent:Extent=new Extent();
	tempExtent.xmin=p.x - bestResolution * width / 2;
	tempExtent.xmax=p.x + bestResolution * width / 2;
	tempExtent.ymin=p.y - bestResolution * height / 2 - 300 * bestResolution;
	tempExtent.ymax=p.y + bestResolution * height / 2 + 300 * bestResolution;
	zoomTo(tempExtent);
	addLayer(commonGraphicsLayer);
	glow.play();
	if (isClear)
	{
		setTimeout(function():void
		{
			clearFeature();
		}, (glow.repeatCount + 1) * glow.duration);
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////
//点选数据
public function splashFeatureMileage(feature:Graphic, isZoom:Boolean, isClear:Boolean, isShowAttributes:Boolean=false, featureCaption:String="", offsetHeight:int=0):void
{
	removeLayer(mileageGraphicsLayer);
	mileageGraphicsLayer.clear();
	lastCommonGraphicsLayerFeature=null;
	if (feature && feature.geometry)
	{
		if (!isClear)
			lastCommonGraphicsLayerFeature=feature;
		
		if (isZoom)
		{
			var p:MapPoint=feature.geometry is MapPoint ? (Geometry.fromJSON(MapPoint(feature.geometry).toJSON()) as MapPoint) : feature.geometry.extent.center;
			if (feature.geometry is MapPoint)
			{
				var resolution:Number=this.lods[this.level].resolution;
				p.y-=offsetHeight * resolution;
				zoomTo(p);
				
			}
			else
			{
				var bestResolution:Number=Math.max(feature.geometry.extent.width / this.width, feature.geometry.extent.height / (this.height - 300));
				var tempExtent:Extent=new Extent();
				tempExtent.xmin=p.x - bestResolution * width / 2;
				tempExtent.xmax=p.x + bestResolution * width / 2;
				tempExtent.ymin=p.y - bestResolution * height / 2 - offsetHeight * bestResolution;
				tempExtent.ymax=p.y + bestResolution * height / 2 + offsetHeight * bestResolution;
				zoomTo(tempExtent);
			}
			
		}
		
		feature.symbol=getSymbolFunction(feature);
		mileageGraphicsLayer.add(feature);
		addLayer(mileageGraphicsLayer);
		glow.play();
		if (isClear)
		{
			setTimeout(function():void
			{
				clearFeature();
			}, (glow.repeatCount + 1) * glow.duration);
		}
		
		if (isShowAttributes)
		{
			var labelPoint:MapPoint=null;
			if (feature.geometry is MapPoint)
			{
				labelPoint=feature.geometry as MapPoint;
				showAttributeWindow(labelPoint, feature, featureCaption);
			}
			else if (feature.geometry is Polyline)
			{
				var path:Object=(feature.geometry as Polyline).paths[0];
				var index:int=path.length / 2;
				labelPoint=path[index];
				showAttributeWindow(labelPoint, feature, featureCaption);
			}
			else if (feature.geometry is Polygon)
			{
				var ring:Object=(feature.geometry as Polygon).rings[0];
				var index:int=ring.length / 2;
				labelPoint=ring[index];
				showAttributeWindow(labelPoint, feature, featureCaption);
			}
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////
//测试
private function showAttributeWindow(point:MapPoint, feature:Object, featureCaption:String):void
{
	//contentNavigator.dataProvider=new ArrayCollection([feature]);
	datagrid4featureAttributes.dataProvider=new ArrayCollection(feature.attributes);
	this.infoWindowContent=datagrid4featureAttributes; //contentNavigator;
	this.infoWindow.label=featureCaption;
	this.infoWindow.show(point);
}

////////////////////////////////////////////////////////////////////////////////////
public function clearFeature():void
{
	commonGraphicsLayer.clear();
	measureGraphicsLayer.clear();
	infoWindow.hide();
}

///////////////////////////////////////////////////////////////////////////////////
//符号化
private function getSymbolFunction(graphic:Graphic):Symbol
{
	var result:Symbol;
	//根据元素的类型进行显示样式的设定
	switch (graphic.geometry.type)
	{
		case Geometry.MAPPOINT:
			result=sms;
			break;
		case Geometry.POLYLINE:
			result=sls;
			break;
		case Geometry.POLYGON:
			result=sfs;
			break;
	}
	return result;
}

////////////////////////////////////////////////////////////////////////////////////
public function setGeometryServiceUrl(url:String, proxyURL:String):void
{
	geometryService.url=url;
	geometryService.proxyURL=proxyURL;
}

////////////////////////////////////////////////////////////////////////////////////
public function clear():void
{
	measureGraphicsLayer.clear();
	mileageGraphicsLayer.clear();
}

////////////////////////////////////////////////////////////////////////////////////
public function showStatistic(records:Object):void
{
	if (areaNavigatorLayer == null)
	{
		areaNavigatorLayer=new GraphicsLayer;
		areaNavigatorLayer.id="areaNavigatorLayer";
	}
	if (areaNavigatorFeatures)
		generateStatisticFeatures(records);
	else
		mapServiceClient.getMapAreaNavigatorFeatures(function(json:Object):void
		{
			areaNavigatorFeatures=[];
			var layerRecords:Array=json.records;
			for (var i:int=0; i < layerRecords.length; i++)
			{
				var featureRecords:Array=layerRecords[i];
				for (var j:int=0; j < featureRecords.length; j++)
				{
					var record:Object=featureRecords[j];
					record.geometry=Geometry.fromJSON(record.wkt);
					//record.center=Geometry.fromJSON(record.center);
					//record.center=null;
					record.wkt=null;
					record.level=i;
					areaNavigatorFeatures.push(record);
				}
			}
			generateStatisticFeatures(records);
		});
	if (this.layerIds.indexOf(areaNavigatorLayer.id) == -1)
	{
		this.addLayer(areaNavigatorLayer);
	}
}

/////////////////////////////////////////////////////////////////////////////////////
private function generateStatisticFeatures(records:Object):void
{
	areaNavigatorLayer.clear();
	for (var i:int=0; i < areaNavigatorFeatures.length; i++)
	{
		var feature:Object=areaNavigatorFeatures[i];
		var graphic:Graphic=new Graphic(feature.geometry);
		graphic.symbol=sfs;
		areaNavigatorLayer.add(graphic);
	}
}
/////////////////////////////////////////////////////////////////////////////////////
