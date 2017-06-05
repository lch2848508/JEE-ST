package com.webgis.module.statistic
{
	import com.esri.ags.Map;
	import com.esri.ags.geometry.Geometry;
	import com.esri.ags.geometry.MapPoint;
	import com.esri.ags.symbols.Symbol;
	import com.estudio.flex.utils.Convert;
	
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.text.TextFormat;

	public class PieSymbol extends Symbol
	{
		public var fields:Array=[];
		public var values:Array=[];
		public var colors:Array=[];
		public var maxValue:Number=0;
		public var maxHeight:Number=120;
		public var columnWidth:Number=20;
		private var sum:Number=-1;

		public function PieSymbol()
		{
			super();
		}
		
//		override public function draw(sprite:Sprite, geometry:Geometry, attributes:Object, map:Map):void
//		{
//			// TODO Auto Generated method stub
//			super.draw(sprite, geometry, attributes, map);
//		}
		
		
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
			if (sum == -1)
			{
				for (var i:int=0; i < fields.length; i++){
					var val:Number=0;
					var targetRegExp:RegExp=/^\d*(\.\d+)?%$/;
					if(targetRegExp.test(attributes[fields[i].toString()])){
						val=attributes[fields[i].toString()].substring(0,attributes[fields[i].toString()].length-1);
					}else{
						val=attributes[fields[i].toString()];
					}
					sum+=val;
				}
			}
			this.drawPie(sprite, attributes, map, point);
		}

		//////////////////////////////////////////////////////////////////////////////////////
		private function drawPie(sprite:Sprite, attributes:Object, map:Map, point:MapPoint):void
		{
			var h:Number=this.maxHeight;
			var w:Number=this.maxHeight;
			var radius:Number=Math.max((w * sum / maxValue) / 2, 10);
			sprite.x=Math.ceil((toScreenX(map, point.x) - w / 2));
			sprite.y=Math.ceil((toScreenY(map, point.y) - h / 2));
			sprite.width=w;
			sprite.height=h;
			var angle:Number=0;
			var totalAngle:Number=0;
			var targetRegExp:RegExp=/^\d*(\.\d+)?%$/;
			for (var i:uint=0; i < fields.length; i++)
			{
//				var val:Number=Convert.str2Number(attributes[fields[i].toString()]);
				var val:Number=0;
				if(targetRegExp.test(attributes[fields[i].toString()])){
					val=attributes[fields[i].toString()].substring(0,attributes[fields[i].toString()].length-1);
				}else{
					val=attributes[fields[i].toString()];
				}
				
				if (val == 0)
					continue;
				var color:uint=this.colors[i];
				angle=val * 360 / sum;
				DrawSector(sprite, w / 2, h / 2, radius, angle, totalAngle, color);
				totalAngle+=angle;
			}
		}

		///////////////////////////////////////////////////////////////////////////////////////
		//绘制扇形
		function DrawSector(mc:Sprite, x:Number=200, y:Number=200, r:Number=100, angle:Number=27, startFrom:Number=270, color:Number=0xff0000):void
		{
			mc.graphics.beginFill(color, 50);
			//remove this line to unfill the sector  
			/* the border of the secetor with color 0xff0000 (red) , you could replace it with any color
			* you want like 0x00ff00(green) or 0x0000ff (blue).
			*/
			// mc.graphics.lineStyle(0,0xff0000);  //自定义颜色  
			mc.graphics.lineStyle(0, color); //使用传递进来的颜色  
			mc.graphics.moveTo(x, y);
			angle=(Math.abs(angle) > 360) ? 360 : angle;
			var n:Number=Math.ceil(Math.abs(angle) / 45);
			var angleA:Number=angle / n;
			angleA=angleA * Math.PI / 180;
			startFrom=startFrom * Math.PI / 180;
			mc.graphics.lineTo(x + r * Math.cos(startFrom), y + r * Math.sin(startFrom));
			for (var i:int=1; i <= n; i++)
			{
				startFrom+=angleA;
				var angleMid=startFrom - angleA / 2;
				var bx=x + r / Math.cos(angleA / 2) * Math.cos(angleMid);
				var by=y + r / Math.cos(angleA / 2) * Math.sin(angleMid);
				var cx=x + r * Math.cos(startFrom);
				var cy=y + r * Math.sin(startFrom);
				mc.graphics.curveTo(bx, by, cx, cy);
			}
			if (angle != 360)
			{
				mc.graphics.lineTo(x, y);
			}
			mc.graphics.endFill(); // if you want a sector without filling color , please remove this line.  
		}
	}
}
