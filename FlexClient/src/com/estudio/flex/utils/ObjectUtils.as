package com.estudio.flex.utils
{

	import flash.display.DisplayObject;
	import flash.net.getClassByAlias;
	import flash.net.registerClassAlias;
	import flash.utils.ByteArray;
	import flash.utils.getQualifiedClassName;

	import mx.core.UIComponent;

	public class ObjectUtils
	{

		static public function deepClone(obj:*):*
		{
			var aliasClass:Class;
			var classDefinition:Class=Object(obj).constructor as Class;
			var className:String=getQualifiedClassName(obj);
			try
			{
				aliasClass=getClassByAlias(className);
			}
			catch (err:Error)
			{
			}
			if (!aliasClass)
			{
				registerClassAlias(className, classDefinition);
			}
			else if (aliasClass != classDefinition)
			{
				registerClassAlias(className + ":/:" + className, classDefinition);
			}
			var byteArray:ByteArray=new ByteArray();
			byteArray.writeObject(obj);
			byteArray.position=0;
			return byteArray.readObject();
		}

		public static function mergeParams(... params):Object
		{
			var result:Object={};
			for (var i:int=0; i < params.length; i++)
			{
				var param:Object=params[i];
				if (param != null)
				{
					for (var k:String in param)
					{
						if (result.hasOwnProperty(k))
							continue;
						if (StringUtils.isEmpty(k))
							continue;
						else if (param[k] is Function)
							continue;
						else if (param[k] is DisplayObject)
							continue;
						result[k]=param[k];
					}
				}
			}
			return result;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function toJSON(data:Object):String
		{
			var result:String=JSON.stringify(data);
			return result;
		}

		public static function parserJSON(str:String):Object
		{
			var result:Object=null;
			try
			{
				result=JSON.parse(str);
			}
			catch (e)
			{

			}
			return result;
		}

		public static function unescape4flex(obj:Object):Object
		{
			var result:Object=obj;
			var type:String=Object.prototype.toString.apply(obj);
			//if (type === "[object String]")
			//	result=unescape((String)(obj));
			//else 
			if (type === '[object Array]')
			{
				result=[];
				for (var i:int=0; i < obj.length; i++)
					result.push(unescape4flex(obj[i]));
			}
			else if (type === '[object Object]' && type !== null)
			{
				result={};
				for (var k:String in obj)
					result[k]=unescape4flex(obj[k]);
			}
			return result;
		}

		// ---------------------------------------------------------------------------------------------
		public static function escape4js(obj:Object):Object
		{
			var result:Object=obj;
			var type:String=Object.prototype.toString.apply(obj);
			//if (type === "[object String]")
			//{
			//	result=escape((String)(obj));
			//}
			//else 
			if (type === '[object Array]')
			{
				result=[];
				for (var i:int=0; i < obj.length; i++)
					result.push(escape4js(obj[i]));
			}
			else if (type === '[object Object]' && type !== null)
			{
				result={};
				for (var k:String in obj)
					result[k]=escape4js(obj[k]);
			}
			return result;
		}

		// --------------------------------------------------------------------------------------------
		public static function forceToArray(obj:Object):Array
		{
			var result:Array=[];
			for (var k:String in obj)
				result[parseInt(k)]=obj[k];
			return result;
		}
	}
}
