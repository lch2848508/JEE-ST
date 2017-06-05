package com.webgis.module.layertree
{
	import flash.events.Event;
	
	public class SuperPanelEvent extends Event
	{
		//--------------------------------------------------------------------------
		//
		//  Class constants
		//
		//--------------------------------------------------------------------------
		//我们可以通过YourUI.addEventListener(SuperPanelEvent.MAXIMIZE,yourFunctionName);让我们的组件来监听这里的事件，事件触发时会调我们的函数
		//比如有如下代码:
		//var myEvent:SuperPanelEvent = new SuperPanelEvent(SuperPanelEvent.MAXIMIZE);
		//	dispatchEvent(myEvent);
		//就是定义了一个SuperPanelEvent.MAXIMIZE事件并且广播出去，这时所有监听此事件的控件都会收到该事件触发的消息
		//这个有点类似PureMVC中sendNotification的味道，发送消息后会执行消息对应的Command或者执行关心此消息的Mediator的handleNotification方法
		static public const MAXIMIZE:String =     "maximize";
		static public const MINIMIZE:String =     "minimize";
		static public const RESTORE:String =      "restore";
		static public const DRAG_START:String =   "dragStart";
		static public const DRAG:String =         "drag";
		static public const DRAG_END:String =     "dragEnd";
		static public const RESIZE_START:String = "resizeStart";
		static public const RESIZE_END:String =   "resizeEnd";
		
		
		
		//--------------------------------------------------------------------------
		//
		//  Constructor
		//
		//--------------------------------------------------------------------------
		
		public function SuperPanelEvent(type:String, 
										cancelable:Boolean=false,
										bubbles:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}