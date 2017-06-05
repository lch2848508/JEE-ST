package com.estudio.flex.utils
{
	import flash.xml.XMLDocument;

	import mx.formatters.DateFormatter;
	import mx.rpc.xml.SimpleXMLDecoder;
	import mx.utils.ObjectProxy;

	public class Convert
	{
		public function Convert()
		{
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * 字符串转化为整数
		 * @param str
		 * @param defaultValue
		 * @return
		 *
		 */
		public static function str2int(str:String, defaultValue:int=0):int
		{
			if (StringUtils.isEmpty(str))
				return defaultValue;
			var result:Number=Number(str);
			if (isNaN(result))
				result=defaultValue;
			return int(result);
		}

		public static function str2Number(str:String, defaultValue:int=0):Number
		{
			if (StringUtils.isEmpty(str))
				return defaultValue;
			var result:Number=Number(str);
			if (isNaN(result))
				result=defaultValue;
			return result;
		}


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * 字符串转化为Boolean
		 * @param str
		 * @param defaultValue
		 * @return
		 *
		 */
		public static function object2Boolean(str:Object, defaultValue:Boolean=false):Boolean
		{
			var result:Boolean=defaultValue;
			if (str is Boolean)
			{
				result=str as Boolean;
			}
			if (str is Number)
			{
				result=(str as Number) != 0;
			}
			else if (str is String)
			{
				if (!isNaN(parseInt(String(str))))
					result=parseInt(String(str)) != 0;
				else
					result=StringUtils.equal("TRUE", str.toUpperCase());
			}
			return result;
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * XML字符串转化为对象
		 * @param xmlStr
		 * @return
		 *
		 */
		public static function xmlStr2Object(xmlStr:String):Object
		{
			var xml:XML=new XML(xmlStr);
			return xml2Object(xml);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * XML对象转化为原生对象
		 * @param xml
		 * @return
		 *
		 */
		public static function xml2Object(xml:XML):Object
		{
			var result:Object=new Object();
			var attributes:XMLList=xml.attributes();
			for each (var attrib:XML in attributes)
			{
				var attribName:String=String(attrib.name());
				var attribValue:String=String(attrib);
				result[attribName]=attribValue;
			}
			var elements:XMLList=xml.children();
			for each (var element:XML in elements)
			{
				var elementName:String=String(element.name());
				var array:Array=result[elementName];
				if (!array)
				{
					array=[];
					result[elementName]=array;
				}
				array.push(xml2Object(element));
			}

			var text:String=String(xml.text());
			if (!StringUtils.isEmpty(text))
				result["text"]=text;

			return result;
		}


		/////////////////////////////////////////////////////////////////////////////////////////
		public static function dateTime2Str(date:Date, formatStr:String):String
		{
			var formatter:DateFormatter=new DateFormatter();
			formatter.formatString=formatStr;
			return formatter.format(date);
		}

		// 短日期，形如 (2003-12-05)
		public static function str2Date(str:String, defaultValue:Date=null):Date
		{
			if (StringUtils.isEmpty(str))
				return defaultValue;
			var r:Array=str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
			return r == null ? defaultValue : new Date(r[1], r[3] - 1, r[4]);
		}

		// 长时间，形如 (2003-12-05 13:04:06)
		public static function str2DateTime(str:String, defaultValue:Date=null):Date
		{
			if (StringUtils.isEmpty(str))
				return defaultValue;
			var r:Array=str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/);
			return (r == null) ? str2Date(str, defaultValue) : new Date(r[1], r[3] - 1, r[4], r[5], r[6], r[7]);
		}



	}
}
