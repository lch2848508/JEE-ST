package com.estudio.flex.utils
{
	import flash.external.ExternalInterface;

	public class JSFunUtils
	{
		public function JSFunUtils()
		{

		}

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//执行JavaScript函数
		public static function JSFun(funName:String, ... params):Object
		{
			//var result:Object=flash.external.ExternalInterface.call("serialExternalInterfaceCall", funName, ObjectUtils.escape4js(params));
			var result:Object=flash.external.ExternalInterface.call(funName, ObjectUtils.escape4js(params));
			result=result ? ObjectUtils.unescape4flex(result) : null;
			return result;
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
