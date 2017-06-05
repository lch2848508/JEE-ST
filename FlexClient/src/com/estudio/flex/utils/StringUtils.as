package com.estudio.flex.utils
{
	import flash.system.Security;
	import flash.utils.ByteArray;
	
	import mx.utils.SHA256;
	import mx.utils.SecurityUtil;
	import mx.utils.StringUtil;

	public class StringUtils
	{
		public function StringUtils()
		{
		}

		//相当于Oracle的NVL
		public static function nvl(str:String, asStr:String):String
		{
			if (str == null || str == "")
			{
				return asStr;
			}
			return str;
		}

		//判断是否是空字符串
		public static function isEmpty(str:String):Boolean
		{
			return str == null || str == "";
		}

		//取子串
		public static function before(str:String, substr:String):String
		{
			if (isEmpty(str) || isEmpty(substr))
				return "";
			var index:int=str.indexOf(substr);
			if (index != -1)
				return str.substr(0, index);
			return "";
		}

		//取子串
		public static function after(str:String, substr:String):String
		{
			if (isEmpty(str) || isEmpty(substr))
				return "";
			var index:int=str.indexOf(substr);
			if (index != -1)
				return str.substr(index + substr.length);
			return "";
		}

		//取中间字符串
		public static function between(str:String, substr1:String, substr2:String):String
		{
			return before(after(str, substr1), substr2);
		}

		//判断字符串是否相等
		public static function equal(str1:String, str2:String):Boolean
		{
			if (isEmpty(str1) && isEmpty(str2))
				return true;
			return !isEmpty(str1) && !isEmpty(str2) && str1 == str2;
		}

		public static function isNull(obj:*):Boolean
		{
			return obj == null || obj == undefined;
		}

		//
		public static function startWith(str:String, substr:String):Boolean
		{
			if (!isEmpty(str) && !isEmpty(substr))
				return str.indexOf(substr) == 0;
			return false;
		}

		//
		public static function endWith(str:String, substr:String):Boolean
		{
			if (!isEmpty(str) && !isEmpty(substr))
			{
				var index:int=str.lastIndexOf(substr);
				if (index != -1 && index + substr.length == str.length)
					return true;
			}
			return false;
		}

		public static function contain(str:String, substr:String):Boolean
		{
			return str.indexOf(substr) != -1;
		}

		//
		public static function replace(str:String, oldStr:String, newStr:String):String
		{
			if (isEmpty(str))
				return "";

			return str.split(oldStr).join(newStr);
		}

		public static function trim(str:String):String
		{
			if (!isEmpty(str))
			{
				str=str.replace("\b", "");
				str=str.replace("\f", "");
				str=str.replace("\v", "");
				return StringUtil.trim(str);
			}
			return "";
		}

		public static function lPad(str:String, pad:String, length:int):String
		{
			var result:String=str;
			for (var i:int=str.length; i < length; i++)
				result=pad + result;
			return result;
		}

		public static function Format(format:String, ... params):String
		{
			if (params.length == 0)
			{
				return format;
			}

			var re:RegExp=/\{(\d+)\}/g;
			var getParam:Function=function(result:String, match:String, position:int, source:String):String
			{
				if (params[match] == null)
					throw new ArgumentError("参数数量不足");

				return params[match];
			}
			return format.replace(re, getParam);
		}

		public static function lengthb(str:String):int
		{
			if (str == "" || str == null)
				return 0;
			else
				return str.replace(/[^\x00-\xff]/g, "xx").length;
		}

		public static function cssStr2Object(str:String):Object
		{
			var result:Object={};
			if (!StringUtils.isEmpty(str))
			{
				var list:Array=str.split(";");
				for (var i:int=0; i < list.length; i++)
				{
					var styleStr:String=list[i];
					if (!StringUtils.isEmpty(styleStr))
						result[StringUtils.before(styleStr, ":")]=StringUtils.after(styleStr, ":").replace("px", "");
				}
			}
			return result;
		}


		public static function createUID():String
		{
			var uid:String="";
			var ALPHA_CHARS:String="0123456789abcdef";
			var i:Number;
			var j:Number;
			for (i=0; i < 8; i++)
			{ //先成成前8位
				uid+=ALPHA_CHARS.charAt(Math.round(Math.random() * 15));
			}
			for (i=0; i < 3; i++)
			{ //中间的三个4位16进制数
				for (j=0; j < 4; j++)
				{
					uid+=ALPHA_CHARS.charAt(Math.round(Math.random() * 15));
				}
			}
			var time:Number=new Date().getTime();
			uid+=("0000000" + time.toString(16).toUpperCase()).substr(-8); //取后边8位
			for (i=0; i < 4; i++)
			{
				uid+=ALPHA_CHARS.charAt(Math.round(Math.random() * 15)); //再循环4次随机拿出4位
			}
			return uid;
		}

		public static function str2HTML(str:String):String
		{
			if (!isEmpty(str))
			{
				str=str.replace("&", "&amp;");
				str=str.replace("<", "&lt;");
				str=str.replace(">", "&gt;");
				str=str.replace('"', "&quot;");
				str=str.replace("'", "&apos;");
			}
			return str;
		}

		public static function lower(str:String):String
		{
			return isEmpty(str) ? "" : str.toLocaleLowerCase();
		}


		public static function getHTMLContent(str:String, fontColor:String, fontSize:String, isBold:Boolean):String
		{
			str=str2HTML(str);
			if (isBold)
				str="<b>" + str + "</b>";
			return "<font color='" + fontColor + "' size='" + fontSize + "'>" + str + "</font>"
		}

		public static function urlencodeGB2312(str:String):String
		{
			var result:String="";
			var byte:ByteArray=new ByteArray();
			byte.writeMultiByte(str, "utf-8");
			for (var i:int; i < byte.length; i++)
			{
				result+=escape(String.fromCharCode(byte[i]));
			}
			return result;
		}

		public static function split(str:String, splitChars:Array=null):Array
		{
			if (splitChars == null)
				splitChars=[";", ",", "|"];
			var result:Array=null;
			if (isEmpty(str))
			{
				result=[];
			}
			else
			{
				var isSplit:Boolean=false;
				for (var i:int=0; i < splitChars.length; i++)
				{
					if (contain(str, splitChars[i]))
					{
						result=str.split(splitChars[i]);
						isSplit=true;
						break;
					}
				}
				if (!isSplit)
					result=[str];
			}
			return result;
		}

		/**
		 *
		 *  Secure Hash Algorithm (SHA1)
		 *  http://www.webtoolkit.info/
		 *
		 **/

		public static function SHA1(msg:String):String
		{
			var bs:ByteArray=new ByteArray();
			bs.writeUTFBytes(msg);
			return SHA256.computeDigest(bs);
		}
		
		public static function checkNumber(str:String):Boolean
		{
			var regex:RegExp = /(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)/;
			var isNumber:Boolean=regex.test(str);
			return isNumber;
		}

	}
}
