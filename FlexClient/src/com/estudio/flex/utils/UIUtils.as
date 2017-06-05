package com.estudio.flex.utils
{
	import flash.display.DisplayObject;

	import mx.core.UIComponent;

	public class UIUtils
	{
		public function UIUtils()
		{
		}

		////////////////////////////////////////////////////////////////////
		public static function left(c:UIComponent, value:int):void
		{
			c.left=value;
		}

		////////////////////////////////////////////////////////////////////
		public static function top(c:UIComponent, value:int):void
		{
			c.top=value;
		}

		////////////////////////////////////////////////////////////////////
		public static function right(c:UIComponent, value:int):void
		{
			c.right=value;
		}

		////////////////////////////////////////////////////////////////////
		public static function bottom(c:UIComponent, value:int):void
		{
			c.bottom=value
		}

		//////////////////////////////////////////////////////////////////
		//设置控件全尺寸
		public static function fullAlign(c:UIComponent):void
		{
			left(c, 0);
			top(c, 0);
			c.percentWidth=100;
			c.percentHeight=100;
		}

		////////////////////////////////////////////////////////////////
		//设置间隔
		public static function setGap(c:UIComponent, h:int, v:int):void
		{
			c.setStyle("horizontalGap", h);
			c.setStyle("verticalGap", v);
		}

		//////////////////////////////////////////////////////////////////
		//设置背景
		public static function setBgColor(c:UIComponent, color:String):void
		{
			c.setStyle("backgroundColor", color);
		}

		////////////////////////////////////////////////////////////////
		//设置空间顶端对其
		public static function topAlign(c:UIComponent, height:int):void
		{
			left(c, 0);
			top(c, 0);
			c.height=height;
			c.percentWidth=100;
		}

		////////////////////////////////////////////////////////////////
		//设置空间顶端对其
		public static function bottomAlign(c:UIComponent, height:int):void
		{
			c.height=height;
			c.percentWidth=100;
			bottom(c, 0);
			right(c, 0);
		}

		////////////////////////////////////////////////////////////////
		//设置空间顶端对其
		public static function leftAlign(c:UIComponent, width:int):void
		{
			top(c, 0);
			left(c, 0);
			c.percentHeight=100;
			c.width=width;
		}

		////////////////////////////////////////////////////////////////
		//设置空间顶端对其
		public static function rightAlign(c:UIComponent, width:int):void
		{
			top(c, 0);
			right(c, 0);
			c.percentHeight=100;
			c.width=width;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		//设置控件边框
		public static function border(c:UIComponent, left:Boolean, top:Boolean, right:Boolean, bottom:Boolean):void
		{
			var sidesArr:Array=[];
			if (left)
				sidesArr.push("left");
			if (top)
				sidesArr.push("top");
			if (right)
				sidesArr.push("right");
			if (bottom)
				sidesArr.push("bottom");
			c.setStyle("borderSides", sidesArr.join(" "));
		}

		public static function padding(c:UIComponent, left:int, top:int, right:int, bottom:int):void
		{
			c.setStyle("paddingLeft", left);
			c.setStyle("paddingTop", top);
			c.setStyle("paddingRight", right);
			c.setStyle("paddingBottom", bottom);
		}

		public static function boxBorder(c:UIComponent, borderColor:String, left:Boolean, top:Boolean, right:Boolean, bottom:Boolean):void
		{
			c.setStyle("borderStyle", "solid");
			c.setStyle("borderColor", borderColor);
			border(c, left, top, right, bottom);
		}


		public static function Position(c:UIComponent, left:int, top:int, right:int, bottom:int)
		{
			c.left=left;
			c.top=top;
			c.right=right;
			c.bottom=bottom;
		}

	}
}
