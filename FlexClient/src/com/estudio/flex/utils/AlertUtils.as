package com.estudio.flex.utils
{
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;

	import mx.controls.Alert;
	import mx.core.FlexGlobals;
	import mx.formatters.DateFormatter;

	public class AlertUtils
	{
		public static var ALERT_INFO:int=0;

		public static var ALERT_WARNING:int=1;

		public static var ALERT_STOP:int=2;

		private static var dateFormatter:DateFormatter=null;

		public function AlertUtils()
		{

		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//向服务器提交数据
		public static function alert(content:String, type:int=1):void
		{
			AlertWindow.show(type == 2 ? "错误" : type == 1 ? "警告" : "提示", content, type);
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function confirm(content:String, okFun:Function, cancelFun:Function=null):void
		{
			ConfirmWindow.show("请确认", content, okFun, cancelFun);
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//显示MSN信息
		public static function msnMessage(sendName:String, content:String, isError:Boolean):void
		{
			if (!dateFormatter)
			{
				dateFormatter=new DateFormatter();
				dateFormatter.formatString="MM-DD JJ:NN:SS";
			}
			FlexGlobals.topLevelApplication.popupMessage({time: dateFormatter.format(new Date()), type: 0, sendName: sendName, content: content, isError: isError});
		}
	}
}
