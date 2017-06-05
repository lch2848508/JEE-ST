package com.utilities
{
	import flash.events.MouseEvent;

	import mx.core.FlexGlobals;
	import mx.core.UIComponent;
	import mx.events.FlexMouseEvent;
	import mx.managers.CursorManager;

	public class ResizeUtils
	{
		private var control:UIComponent=null;
		private var parentControl:UIComponent=null;
		public var enabledTop:Boolean=false;
		public var enabledLeft:Boolean=false;
		public var enabledRight:Boolean=false;
		public var enabledBottom:Boolean=false;
		public var minWidth:int=120;
		public var minHeight:int=120;
		public var maxWidth:int=500;
		public var maxHeight:int=500;
		public var isLeft:Boolean=false;
		public var isRight:Boolean=false;
		public var isTop:Boolean=false;
		public var isBottom:Boolean=false;

		public function ResizeUtils(control:UIComponent)
		{
			this.control=control;
		}

		public function active():void
		{
			parentControl=control.parent as UIComponent;
			control.addEventListener(MouseEvent.MOUSE_DOWN, mouseEvent4Resize);
			control.addEventListener(MouseEvent.MOUSE_UP, mouseEvent4Resize);
			control.addEventListener(MouseEvent.MOUSE_MOVE, mouseEvent4Resize);
			control.addEventListener(MouseEvent.MOUSE_OUT, mouseEvent4Resize);
			control.addEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE, mouseEvent4Resize);
			FlexGlobals.topLevelApplication.addEventListener(MouseEvent.MOUSE_MOVE, mouseEvent4Application);
		}

		public function inactive():void
		{
			control.removeEventListener(MouseEvent.MOUSE_DOWN, mouseEvent4Resize);
			control.removeEventListener(MouseEvent.MOUSE_UP, mouseEvent4Resize);
			control.removeEventListener(MouseEvent.MOUSE_MOVE, mouseEvent4Resize);
			control.removeEventListener(MouseEvent.MOUSE_OUT, mouseEvent4Resize);
			control.removeEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE, mouseEvent4Resize);
			if (parentControl)
				parentControl.removeEventListener(MouseEvent.MOUSE_MOVE, mouseEvent4Application);
		}


		private var isMouseResizeClick:Boolean=false;

		private function mouseEvent4Resize(event:MouseEvent):void
		{
			var mouseX:int=control.mouseY;
			var mouseY:int=control.mouseY;
			if (event.type == MouseEvent.MOUSE_MOVE)
			{
				if ((enabledLeft && mouseX <= 5) || (enabledRight && control.width - mouseX <= 5))
					onChangeCursor(CursorH, -9, -9);
				if ((enabledTop && mouseY <= 5) || (enabledBottom && control.height - mouseY <= 5))
					onChangeCursor(CursorV, -9, -9);
				else
					onChangeCursor(CursorNull, -9, -9);
			}
			else if (event.type == MouseEvent.MOUSE_DOWN)
			{
				isLeft=false;
				isRight=false;
				isTop=false;
				isBottom=false;
				if ((enabledLeft && mouseX <= 5) || (enabledRight && control.width - mouseX <= 5) || (enabledTop && mouseY <= 5) || (enabledBottom && control.height - mouseY <= 5))
				{
					if (enabledLeft && mouseX <= 5)
						isLeft=true;
					else if (enabledRight && control.width - mouseX <= 5)
						isRight=true;
					if (enabledTop && mouseY <= 5)
						isTop=true;
					else
						isBottom=false;
					isMouseResizeClick=true;
//					trace(isTop);
				}
			}
			else if (event.type == MouseEvent.MOUSE_UP || event.type == FlexMouseEvent.MOUSE_DOWN_OUTSIDE)
			{
				onChangeCursor(CursorNull, -9, -9);
				isMouseResizeClick=false;
			}
			else if (event.type == MouseEvent.MOUSE_OUT && !isMouseResizeClick)
			{
				onChangeCursor(CursorNull, -9, -9);
				isMouseResizeClick=false;
			}
		}

		private function mouseEvent4Application(event:MouseEvent):void
		{
			if (isMouseResizeClick)
			{
				var isOutSide:Boolean=false;
				if (isLeft)
				{

				}
				else if (isRight)
				{

				}
				else if (isTop)
				{

					var willHeight:int=0;
					willHeight=parentControl.height - int(control.bottom) - parentControl.mouseY;
					if (willHeight >= minHeight && willHeight <= maxHeight)
						control.height=willHeight;
					else
						isOutSide=true;
				}
				else if (isBottom)
				{

				}
				if (isOutSide)
				{
					onChangeCursor(CursorNull, -9, -9);
					isMouseResizeClick=false;
				}
			}
		}

		[Embed("/assets/titleWinIcon/resizeCursorH.png")]
		private var CursorH:Class;

		[Embed("/assets/titleWinIcon/resizeCursorV.png")]
		private var CursorV:Class;
		public var currentType:Class=null;
		private var CursorNull:Class=null;

		private function onChangeCursor(type:Class, xOffset:Number=0, yOffset:Number=0):void
		{
			if (currentType != type)
			{
				currentType=type;
				CursorManager.removeCursor(CursorManager.currentCursorID);
				if (type != null)
				{
					CursorManager.setCursor(type, 2, xOffset, yOffset);
				}
			}
		}
	}
}
