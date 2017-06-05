/*
 * Copyright 2009 ESRI
 *
 * All rights reserved under the copyright laws of the United States
 * and applicable international laws, treaties, and conventions.
 *
 * You may freely redistribute and use this sample code, with or
 * without modification, provided you include the original copyright
 * notice and use restrictions.
 *
 * See use restrictions at http://resources.esri.com/help/9.3/usagerestrictions.htm.
 */
package com.webgis.module.overViewMap
{
	import com.esri.ags.Graphic;
	import com.esri.ags.Map;
	import com.esri.ags.events.ExtentEvent;
	import com.esri.ags.events.MapEvent;
	import com.esri.ags.geometry.Extent;
	import com.esri.ags.geometry.MapPoint;
	import com.esri.ags.layers.GraphicsLayer;
	import com.esri.ags.symbols.SimpleFillSymbol;
	import com.esri.ags.symbols.FillSymbol;
	import com.esri.ags.symbols.SimpleLineSymbol;

	import flash.events.Event;
	import flash.events.MouseEvent;

//--------------------------------------
//  Other metadata
//--------------------------------------

	[IconFile("overview-map.png")]

	/**
	 * An overview map that extends the base Map component and provides a convenience
	 * for linking the overview map to the main map.
	 */
	public class OverviewMap extends Map
	{
		//--------------------------------------------------------------------------
		//
		//  Constructor
		//
		//--------------------------------------------------------------------------

		/**
		 * Creates a new overview map instance.
		 *
		 * @param map The main map that is linked to this overview map.
		 */
		public function OverviewMap(mainMap:Map=null)
		{
			super();

			this.mainMap=mainMap;

			// Disable map navigation
			mapNavigationEnabled=false;

			// Hide the map controls
			logoVisible=false;
			panArrowsVisible=false;
			scaleBarVisible=false;
			zoomSliderVisible=false;

			addEventListener(MouseEvent.CLICK, onOverviewClick);
		}

		//--------------------------------------------------------------------------
		//
		//  Constants
		//
		//--------------------------------------------------------------------------

		/**
		 * Overview mode in which the overview map's extent remains constant.
		 */
		public static const STATIC_MODE:String="static";

		/**
		 * Overview mode in which the overview map's extent changes to track the extent
		 * of the associated main map.  This is the default overview mode.
		 */
		public static const DYNAMIC_MODE:String="dynamic";

		//--------------------------------------------------------------------------
		//
		//  Variables
		//
		//--------------------------------------------------------------------------

		private var _mainMap:Map;
		private var _extentBox:Graphic;
		private var _graphicsLayer:GraphicsLayer;
		private var _boxFillSymbol:FillSymbol;

		private var _dragging:Boolean=false;
		private var _dragStartPoint:MapPoint;
		private var _dragStartExtent:Extent;

		//--------------------------------------------------------------------------
		//  Property:  mainMap
		//--------------------------------------------------------------------------

		[Bindable("mainMapChanged")]
		/**
		 * The main map that is linked to this overview map.
		 */
		public function get mainMap():Map
		{
			return _mainMap;
		}

		/**
		 * @private
		 */
		public function set mainMap(value:Map):void
		{
			if (value !== _mainMap)
			{
				removeMapListeners();
				_mainMap=value;
				addMapListeners();

				dispatchEvent(new Event("mainMapChanged"));
			}
		}

		//--------------------------------------------------------------------------
		//  Property:  overviewMode
		//--------------------------------------------------------------------------

		[Bindable]
		[Inspectable(category="Mapping", enumeration="dynamic,static", defaultValue="dynamic")]
		/**
		 * The display mode of the overview.
		 * Use STATIC_MODE to lock the overview map at its current extent.
		 * Use DYNAMIC_MODE to allow the overview map extent to change in relation to
		 * the main map's extent.
		 *
		 * @default dynamic
		 */
		public var overviewMode:String=DYNAMIC_MODE;

		//--------------------------------------------------------------------------
		//  Property:  overviewClickRecenterEnabled
		//--------------------------------------------------------------------------

		[Bindable]
		[Inspectable(category="Mapping", defaultValue="true")]
		/**
		 * Enables clicking on the overview map to recenter the main map to the clicked location.
		 *
		 * @default true
		 */
		public var overviewClickRecenterEnabled:Boolean=true;

		//--------------------------------------------------------------------------
		//  Property:  overviewDragExtentBoxEnabled
		//--------------------------------------------------------------------------

		private var _overviewDragExtentBoxEnabled:Boolean=true;

		[Bindable]
		[Inspectable(category="Mapping", defaultValue="true")]
		/**
		 * Enables dragging the overview indicator box to recenter the main map.
		 *
		 * @default true
		 */
		public function get overviewDragExtentBoxEnabled():Boolean
		{
			return _overviewDragExtentBoxEnabled;
		}

		/**
		 * @private
		 */
		public function set overviewDragExtentBoxEnabled(value:Boolean):void
		{
			_overviewDragExtentBoxEnabled=value;

			if (_extentBox)
			{
				_extentBox.buttonMode=_overviewDragExtentBoxEnabled;
				_extentBox.useHandCursor=_overviewDragExtentBoxEnabled;
			}
		}

		//--------------------------------------------------------------------------
		//  Property:  boxFillSymbol
		//--------------------------------------------------------------------------

		[Bindable("boxFillSymbolChanged")]
		/**
		 * The fill symbol that is used to draw the indicator box that signifies the main map extent.
		 */
		public function get boxFillSymbol():FillSymbol
		{
			return _boxFillSymbol;
		}

		/**
		 * @private
		 */
		public function set boxFillSymbol(value:FillSymbol):void
		{
			_boxFillSymbol=value;

			// Apply the new symbol if the graphics layer is already created
			if (_graphicsLayer)
			{
				_graphicsLayer.symbol=effectiveBoxFillSymbol();
			}

			dispatchEvent(new Event("boxFillSymbolChanged"));
		}

		//--------------------------------------------------------------------------
		//
		//  Methods
		//
		//--------------------------------------------------------------------------

		/**
		 * @private
		 */
		override protected function createChildren():void
		{
			super.createChildren();

			// Create a graphics layer to display the extent box of the main map
			_graphicsLayer=new GraphicsLayer();
			_graphicsLayer.symbol=effectiveBoxFillSymbol();
			addLayer(_graphicsLayer);

			// Create the extent indicator box
			_extentBox=new Graphic();
			_extentBox.buttonMode=overviewDragExtentBoxEnabled;
			_extentBox.useHandCursor=overviewDragExtentBoxEnabled;
			_extentBox.addEventListener(MouseEvent.MOUSE_DOWN, onBoxDragStart);
			_extentBox.addEventListener(MouseEvent.CLICK, onBoxClick);
			_graphicsLayer.add(_extentBox);

			if (mainMap && mainMap.loaded)
			{
				updateOverviewExtent();
			}
		}

		private function addMapListeners():void
		{
			if (mainMap)
			{
				if (mainMap.loaded)
				{
					updateOverviewExtent();
				}
				else
				{
					mainMap.addEventListener(MapEvent.LOAD, onMainMapLoad, false, 0, true);
				}
				mainMap.addEventListener(ExtentEvent.EXTENT_CHANGE, onMainMapExtentChange, false, 0, true);
			}
		}

		private function removeMapListeners():void
		{
			if (mainMap)
			{
				mainMap.removeEventListener(MapEvent.LOAD, onMainMapLoad);
				mainMap.removeEventListener(ExtentEvent.EXTENT_CHANGE, onMainMapExtentChange);
			}
		}

		private function onMainMapLoad(event:MapEvent):void
		{
			event.map.removeEventListener(MapEvent.LOAD, onMainMapLoad);
			updateOverviewExtent();
		}

		private function onMainMapExtentChange(event:ExtentEvent):void
		{
			updateOverviewExtent();
		}

		private function onBoxDragStart(event:MouseEvent):void
		{
			// Check if this map interaction is enabled
			if (!overviewDragExtentBoxEnabled)
			{
				return;
			}

			if (!_dragging && loaded)
			{
				addEventListener(MouseEvent.MOUSE_MOVE, onBoxDragUpdate);
				addEventListener(MouseEvent.MOUSE_UP, onBoxDragEnd);
				stage.addEventListener(MouseEvent.MOUSE_UP, stage_onBoxDragEnd);

				// Start dragging the extent box
				var mapPoint:MapPoint=toMapFromStage(event.stageX, event.stageY);
				_dragStartPoint=mapPoint;
				_dragStartExtent=Extent(_extentBox.geometry);
				_dragging=true;
			}
		}

		private function onBoxDragUpdate(event:MouseEvent):void
		{
			if (_dragging && loaded)
			{
				// Calculate the drag delta and reposition the extent box
				var mapPoint:MapPoint=toMapFromStage(event.stageX, event.stageY);
				var dx:Number=mapPoint.x - _dragStartPoint.x;
				var dy:Number=mapPoint.y - _dragStartPoint.y;
				var newExtent:Extent=_dragStartExtent.offset(dx, dy);
				updateExtentBox(newExtent);
			}
		}

		private function onBoxDragEnd(event:MouseEvent):void
		{
			if (_dragging && loaded)
			{
				removeEventListener(MouseEvent.MOUSE_MOVE, onBoxDragUpdate);
				removeEventListener(MouseEvent.MOUSE_UP, onBoxDragEnd);
				stage.removeEventListener(MouseEvent.MOUSE_UP, stage_onBoxDragEnd);

				// Calculate the drag delta and reposition the extent box
				var mapPoint:MapPoint=toMapFromStage(event.stageX, event.stageY);
				var dx:Number=mapPoint.x - _dragStartPoint.x;
				var dy:Number=mapPoint.y - _dragStartPoint.y;
				var newExtent:Extent=_dragStartExtent.offset(dx, dy);
				updateOverviewExtent(newExtent);

				// Update the extent of the main map when dragging has finished
				if (mainMap)
				{
					mainMap.extent=newExtent;
				}

				_dragStartPoint=null;
				_dragStartExtent=null;
				_dragging=false;
			}
		}

		private function stage_onBoxDragEnd(event:MouseEvent):void
		{
			if (_dragging && loaded)
			{
				removeEventListener(MouseEvent.MOUSE_MOVE, onBoxDragUpdate);
				removeEventListener(MouseEvent.MOUSE_UP, onBoxDragEnd);
				stage.removeEventListener(MouseEvent.MOUSE_UP, stage_onBoxDragEnd);

				// Use the previously set extent box location (from onBoxDragUpdate)
				// rather than calculating the delta based on the current mouse coordinates.
				// This is because the mouse is now outside the overview map and we don't
				// want to move the extent box to some far off location.
				var newExtent:Extent=_extentBox.geometry as Extent;
				updateOverviewExtent(newExtent);

				// Update the extent of the main map when dragging has finished
				if (mainMap)
				{
					mainMap.extent=newExtent;
				}

				_dragStartPoint=null;
				_dragStartExtent=null;
				_dragging=false;
			}
		}

		private function onBoxClick(event:MouseEvent):void
		{
			if (overviewDragExtentBoxEnabled)
			{
				// Prevent the click event from bubbling up to the map.
				// Otherwise the drag-end action is interfered with.
				event.stopPropagation();
			}
		}

		private function onOverviewClick(event:MouseEvent):void
		{
			// Check if this map interaction is enabled
			if (!overviewClickRecenterEnabled)
			{
				return;
			}

			if (!_dragging && loaded)
			{
				var mapPoint:MapPoint=toMapFromStage(event.stageX, event.stageY);

				// Recenter the extent box
				if (_extentBox && _extentBox.geometry)
				{
					var newExtent:Extent=Extent(_extentBox.geometry).centerAt(mapPoint);
					updateOverviewExtent(newExtent);
				}

				// Recenter the main map on the click point
				if (mainMap && mainMap.loaded)
				{
					mainMap.centerAt(mapPoint);
				}
			}
		}

		/**
		 * Updates the geometry of the map extent indicator box to the specified extent.
		 * If the specified extent is null, then the extent of the main map is used.
		 *
		 * If this overview map is in dynamic overview mode, then the extent of this
		 * overview is updated to track the main map's extent.
		 */
		private function updateOverviewExtent(mainMapExtent:Extent=null):void
		{
			if (!mainMapExtent)
			{
				mainMapExtent=mainMap.extent;
			}

			// Set the location of the extent indicator box
			updateExtentBox(mainMapExtent);

			if (overviewMode == DYNAMIC_MODE)
			{
				// Set the extent of this overview map to be 9 times larger (3x3) than the main map extent
				super.extent=mainMapExtent.expand(3);
			}
		}

		/**
		 * Updates the geometry of the map extent indicator box.
		 */
		private function updateExtentBox(extent:Extent):void
		{
			if (_extentBox)
			{
				_extentBox.geometry=extent;
			}
		}

		/**
		 * Returns the actual fill symbol to use for the extent indicator box.
		 */
		private function effectiveBoxFillSymbol():FillSymbol
		{
			if (_boxFillSymbol)
			{
				return _boxFillSymbol;
			}

			// Default fill symbol, similar to the Map rubberband zoom box style
			return new SimpleFillSymbol(SimpleFillSymbol.STYLE_SOLID, 0x666666, 0.4, new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, 0xFF0000, 1.0, 2));
		}
	}

}
