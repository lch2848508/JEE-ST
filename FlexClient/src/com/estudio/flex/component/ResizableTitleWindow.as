package com.estudio.flex.component
{

	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.Point;

	import mx.core.FlexGlobals;
	import mx.core.UIComponent;
	import mx.events.MoveEvent;
	import mx.events.SandboxMouseEvent;

	import spark.components.TitleWindow;

	/**
	 *  ResizableTitleWindow is a TitleWindow with
	 *  a resize handle.
	 */
	public class ResizableTitleWindow extends TitleWindow
	{

		//--------------------------------------------------------------------------
		//
		//  Constructor
		//
		//--------------------------------------------------------------------------

		/**
		 *  Constructor.
		 */
		public function ResizableTitleWindow()
		{
			super();
			this.addEventListener(MoveEvent.MOVE, moveMe);
		}

		//--------------------------------------------------------------------------
		//
		//  Variables
		//
		//--------------------------------------------------------------------------

		private var clickOffset:Point;

		//--------------------------------------------------------------------------
		//
		//  Properties 
		//
		//--------------------------------------------------------------------------

		//----------------------------------
		//  Resize Handle
		//---------------------------------- 

		[SkinPart(required="false")]

		/**
		 *  The skin part that defines the area where
		 *  the user may drag to resize the window.
		 */
		public var resizeHandle:UIComponent;

		//--------------------------------------------------------------------------
		//
		//  Overridden methods: UIComponent, SkinnableComponent
		//
		//--------------------------------------------------------------------------

		/**
		 *  @private
		 */
		override protected function partAdded(partName:String, instance:Object):void
		{
			super.partAdded(partName, instance);

			if (instance == resizeHandle)
			{
				resizeHandle.addEventListener(MouseEvent.MOUSE_DOWN, resizeHandle_mouseDownHandler);
			}
		}

		/**
		 *  @private
		 */
		override protected function partRemoved(partName:String, instance:Object):void
		{
			if (instance == resizeHandle)
			{
				resizeHandle.removeEventListener(MouseEvent.MOUSE_DOWN, resizeHandle_mouseDownHandler);
			}

			super.partRemoved(partName, instance);
		}

		//--------------------------------------------------------------------------
		// 
		// Event Handlers
		//
		//--------------------------------------------------------------------------

		private var prevWidth:Number;
		private var prevHeight:Number;

		protected function resizeHandle_mouseDownHandler(event:MouseEvent):void
		{
			if (enabled && isPopUp && !clickOffset)
			{
				clickOffset=new Point(event.stageX, event.stageY);
				prevWidth=width;
				prevHeight=height;

				var sbRoot:DisplayObject=systemManager.getSandboxRoot();

				sbRoot.addEventListener(MouseEvent.MOUSE_MOVE, resizeHandle_mouseMoveHandler, true);
				sbRoot.addEventListener(MouseEvent.MOUSE_UP, resizeHandle_mouseUpHandler, true);
				sbRoot.addEventListener(SandboxMouseEvent.MOUSE_UP_SOMEWHERE, resizeHandle_mouseUpHandler)
			}
		}

		/**
		 *  @private
		 */
		protected function resizeHandle_mouseMoveHandler(event:MouseEvent):void
		{
			// during a resize, only the TitleWindow should get mouse move events
			// we don't check the target since this is on the systemManager and the target
			// changes a lot -- but this listener only exists during a resize.
			event.stopImmediatePropagation();

			if (!clickOffset)
			{
				return;
			}

			width=prevWidth + (event.stageX - clickOffset.x);
			height=prevHeight + (event.stageY - clickOffset.y);
			event.updateAfterEvent();
		}

		/**
		 *  @private
		 */
		protected function resizeHandle_mouseUpHandler(event:Event):void
		{
			clickOffset=null;
			prevWidth=NaN;
			prevHeight=NaN;

			var sbRoot:DisplayObject=systemManager.getSandboxRoot();

			sbRoot.removeEventListener(MouseEvent.MOUSE_MOVE, resizeHandle_mouseMoveHandler, true);
			sbRoot.removeEventListener(MouseEvent.MOUSE_UP, resizeHandle_mouseUpHandler, true);
			sbRoot.removeEventListener(SandboxMouseEvent.MOUSE_UP_SOMEWHERE, resizeHandle_mouseUpHandler);
		}

		private function moveMe(e:Event):void
		{
			var target:UIComponent=e.target as UIComponent;
			var targetX:Number=target.x;
			var targetY:Number=target.y;
			var appWidth:Number=FlexGlobals.topLevelApplication.width;
			var appHeight:Number=FlexGlobals.topLevelApplication.height;

			if (targetX + this.width > appWidth)
				target.x=appWidth - this.width;
			if (targetX < 0)
				target.x=0;

			if (targetY + this.height > appHeight)
				target.y=appHeight - this.height;
			if (targetY < 0)
				target.y=0;
		}
	}
}
