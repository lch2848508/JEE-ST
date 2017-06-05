package com.estudio.flex.utils
{

	public class IFrameUtils
	{
		public function IFrameUtils()
		{
		}

		///////////////////////////////////////////////////////////////////////////////////////////
		//创建一个IFrame
		public static function createIFrameByHTML(frameID:String, html:String):Boolean
		{
			return JSFunUtils.JSFun("createIFrame", frameID, html) as Boolean;
		}

		public static function createIFrameBySrc(frameID:String, src:String):Boolean
		{
			return JSFunUtils.JSFun("createIFrameBySrc", frameID, src) as Boolean;
		}
		/////////////////////////////////////////////////////////////////////////////////////////////
		//删除IFrame
		public static function removeIFrame(frameID:String):Boolean
		{
			return JSFunUtils.JSFun("removeIFrame", frameID) as Boolean;
		}

		public static function execute(frameID:String, funName:String, params:Object):Object
		{
			return JSFunUtils.JSFun("executeFrameFunction", frameID, funName, params);
		}
	}
}
