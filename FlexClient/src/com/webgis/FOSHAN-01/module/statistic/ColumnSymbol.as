package com.webgis.module.statistic
{
	import com.esri.ags.Map;
	import com.esri.ags.geometry.Geometry;
	import com.esri.ags.geometry.MapPoint;
	import com.esri.ags.symbols.Symbol;
	
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.text.TextFormat;

	public class ColumnSymbol extends Symbol
	{
		public var fields:Array=[];
		public var values:Array=[];
		public var colors:Array=[];
		public var maxValue:Number=0;
		public var maxHeight:Number=120;
		public var columnWidth:Number=14;

		public function ColumnSymbol()
		{
			super();
		}

		/////////////////////////////////////////////////////////////////////////////////////
		override public function draw(sprite:Sprite, geometry:Geometry, attributes:Object, map:Map):void
		{
			if (!geometry)
				return;
			if (!(geometry is MapPoint))
				return;
			var point:MapPoint=(geometry as MapPoint);
			sprite.graphics.clear();
			removeAllChildren(sprite);
			this.drawColumn(sprite, attributes, map, point);
		}

		//////////////////////////////////////////////////////////////////////////////////////
		private function drawColumn(sprite:Sprite, attributes:Object, map:Map, point:MapPoint):void
		{
			var h:Number=this.maxHeight;
			var w:Number=this.fields.length * columnWidth;
			sprite.x=Math.ceil((toScreenX(map, point.x) - w));
			sprite.y=Math.ceil((toScreenY(map, point.y) - h));
			sprite.width=w;
			sprite.height=h + 30;
			var columnHeight:Number=0;
			var columnX:Number=30;
			var columnY:Number=0;
			for (var j:uint=0; j < fields.length; j++)
			{
				var val:Number=attributes[fields[j].toString()];

				columnHeight=val * maxHeight / maxValue;
				columnY=maxHeight - columnHeight + 5;
				var color:uint=this.colors[j];
				sprite.graphics.lineStyle(null, 0x153A61);

				//画平面柱子
				sprite.graphics.beginFill(color);
				sprite.graphics.drawRect(columnX, columnY, columnWidth, columnHeight);
				sprite.graphics.endFill();

				//增加立体效果
				drawColumn3D(sprite, columnX, columnY, columnWidth, columnHeight, color);
				columnX+=columnWidth;

//				var dy2:Number=columnWidth * Math.cos(Math.PI / 6);
//				var _textField:TextField=new TextField();
//				_textField.text=val.toString();
//				_textField.x=columnX - 10;
//				_textField.y=columnY - dy2;
//				_textField.width=15;
//				_textField.height=15;
//				_textField.visible=true;
//				var _textFormat:TextFormat=new TextFormat();
//				_textFormat.size=10;
//				_textField.setTextFormat(_textFormat);
//				sprite.addChild(_textField);
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		private function drawColumn3D(sprite:Sprite, x:Number, y:Number, w:Number, h:Number, color:uint):void
		{
			var dw:Number=w * Math.sin(Math.PI / 6);
			var dx:Number=w * Math.sin(Math.PI / 6);
			var dy:Number=w * Math.cos(Math.PI / 6);
			sprite.graphics.beginFill(color);
			sprite.graphics.moveTo(x, y);
			sprite.graphics.lineTo(x + dx, y - dy);
			sprite.graphics.lineTo(x + dx + w, y - dy);
			sprite.graphics.lineTo(x + w, y);
			sprite.graphics.lineTo(x, y);
			sprite.graphics.endFill();
			sprite.graphics.beginFill(color);
			sprite.graphics.moveTo(x + w, y);
			sprite.graphics.lineTo(x + dx + w, y - dy);
			sprite.graphics.lineTo(x + dx + w, y + h - dy);
			sprite.graphics.lineTo(x + w, y + h);
			sprite.graphics.lineTo(x + w, y);
			sprite.graphics.endFill();
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		public function drawCylinde(sprite:Sprite, attributes:Object, map:Map, point:MapPoint):void
		{
			var gap:Number=0;
			var h:Number=this.maxHeight;
			var w:Number=this.fields.length * (columnWidth + gap);
			sprite.x=Math.ceil((toScreenX(map, point.x) - w / 2));
			sprite.y=Math.ceil((toScreenY(map, point.y) - w / 2));
			sprite.width=w;
			sprite.height=h;
			var columnHeight:Number=0;
			var columnX:Number=gap / 2;
			var columnY:Number=0;
			for (var j:uint=0; j < fields.length; j++)
			{
				var val:Number=attributes[fields[j].toString()];
				columnHeight=val * maxHeight / maxValue;
				columnY=maxHeight - columnHeight;
				var color:uint=this.colors[j];
				sprite.graphics.lineStyle(null, 0xffffff, 0);
				sprite.graphics.beginFill(color);
				sprite.graphics.drawRect(columnX, columnY, columnWidth, columnHeight);
				sprite.graphics.endFill();
				drawCylindeU(sprite, columnX, columnY, columnWidth, columnHeight, color);
				columnX+=columnWidth + gap;
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////////////
		private function drawCylindeUp(sprite:Sprite, x:Number, y:Number, w:Number, h:Number, color:uint):void
		{
			var dw:Number=w * Math.sin(Math.PI / 6);
			var dx:Number=w * Math.sin(Math.PI / 6);
			var dy:Number=w * Math.cos(Math.PI / 6);
			sprite.graphics.beginFill(color);
			sprite.graphics.moveTo(x, y);
			sprite.graphics.lineTo(x + dx, y - dy);
			sprite.graphics.lineTo(x + dx + w, y - dy);
			sprite.graphics.lineTo(x + w, y);
			sprite.graphics.lineTo(x, y);
			sprite.graphics.endFill();
			sprite.graphics.beginFill(color);
			sprite.graphics.moveTo(x + w, y);
			sprite.graphics.lineTo(x + dx + w, y - dy);
			sprite.graphics.lineTo(x + dx + w, y + h - dy);
			sprite.graphics.lineTo(x + w, y + h);
			sprite.graphics.lineTo(x + w, y);
			sprite.graphics.endFill();
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////
		private function drawCylindeU(sprite:Sprite, x:Number, y:Number, w:Number, h:Number, color:uint):void
		{
			var dy:Number=w * Math.cos(Math.PI / 6);
			sprite.graphics.lineStyle(0, 0xffffff, 0.5);
			sprite.graphics.beginFill(color);
			sprite.graphics.drawEllipse(x, y - (dy / 2), w, dy);
			sprite.graphics.drawEllipse(x, y + h - (dy / 2), w, dy);
			sprite.graphics.endFill();
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		private function drawCylindeD(sprite:Sprite, x:Number, y:Number, w:Number, h:Number, color:uint):void
		{
			var dy:Number=w * Math.cos(Math.PI / 6);
			y=y + h;
			sprite.graphics.lineStyle(0, 0xffffff, 0.5);
			sprite.graphics.beginFill(color);
			sprite.graphics.drawEllipse(x, y - (dy / 2), w, dy);
			sprite.graphics.endFill();
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
