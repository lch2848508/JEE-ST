package com.webgis.layer
{
	import com.esri.ags.SpatialReference;
	import com.esri.ags.geometry.Extent;
	import com.esri.ags.geometry.MapPoint;
	import com.esri.ags.layers.ArcGISDynamicMapServiceLayer;
	import com.esri.ags.layers.TiledMapServiceLayer;
	import com.esri.ags.layers.supportClasses.LOD;
	import com.esri.ags.layers.supportClasses.TileInfo;
	import com.estudio.flex.utils.StringUtils;
	
	import flash.net.URLRequest;
	import flash.sampler.Sample;
	
	public class TianDiTu4FSImage extends TiledMapServiceLayer
	{
		private var _tileInfo:TileInfo;
		private var _baseURL:String;
		private var _baseURLs:Array;
		private var _initExtent:String;
		private var _layerId:String;
		private var _mapStyle:String="";
		public var proxyURL:String="";
		
		public function TianDiTu4FSImage()
		{
			super();
			this._tileInfo=new TileInfo();
			this._initExtent=null;
			this.buildTileInfo();
			setLoaded(true);
		}
		
		override public function get fullExtent():Extent
		{
			return initialExtent;
		}
		
		public function set initExtent(initextent:String):void
		{
			this._initExtent=initextent;
		}
		
		override public function get initialExtent():Extent
		{
			return new Extent(435385.313112412, 2504688.54284861, 540562.664032815, 2609865.89376901, new SpatialReference(3857));
		}
		
		override public function get spatialReference():SpatialReference
		{
			return new SpatialReference(102113);
		}
		
		override public function get tileInfo():TileInfo
		{
			return this._tileInfo;
		}
		
		//根据不同地图类型加载不同WMTS服务
		override protected function getTileURL(level:Number, row:Number, col:Number):URLRequest
		{
			var urlRequest:String="http://19.128.104.244:8001/ZWDOM2014/wmts";
			urlRequest+="?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0" + //
				"&LAYER=ZWDOM2014&STYLE=ZWDOM2014&TILEMATRIXSET=Matrix_0" + //
				"&TILEMATRIX=" + level + //
				"&TILEROW=" + row + //
				"&TILECOL=" + col + //
				"&FORMAT=image/tile"; //
			if (!StringUtils.isEmpty(proxyURL))
				urlRequest=proxyURL + urlRequest;
			return new URLRequest(urlRequest);
		}
		
		//切片信息
		private function buildTileInfo():void
		{
			this._tileInfo.height=256;
			this._tileInfo.width=256;
			this._tileInfo.origin=new MapPoint(-20037508.3427892, 20037508.3427892, new SpatialReference(3857));
			this._tileInfo.spatialReference=new SpatialReference(3857);
			this._tileInfo.lods=new Array();
			for (var i:int=9; i < 20; i++)
			{
				var lod:LOD=new LOD();
				lod.level=i;
				lod.scale=591657527.591555 / Math.pow(2, i);
				lod.resolution=156543.033928 / Math.pow(2, i);
				this._tileInfo.lods.push(lod);
			}
		}
	}
}
