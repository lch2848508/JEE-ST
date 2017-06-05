package vendy.com.common.helper
{
import mx.controls.Alert;

/*******************************************
 * 描述: String的工具类
 * <p>版权所有: horizo
 * <p>创建者:
 * <p>创建日期: 2010-11-08
 * <p>修改者:
 * <p>修改日期:
 * <p>修改说明:
 *******************************************/
public class StringHelper
{
	public function StringHelper()
	{
	}

	/**
	 *
	 * @param str
	 * @return true or false
	 * @function 判断当前串是否为空串;
	 *
	 */
	public static function isEmpty(str:String):Boolean
	{
		if (str == null)
			return true;
		if (str == "")
			return true;
		return false;
	}

	/**
	 *
	 * @param str
	 * @return true or false
	 * @function 判断当前串不为空串;
	 *
	 */
	public static function isNotEmpty(str:String):Boolean
	{

		if (str != null && str.length > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 方法含义：处理超长字符串的显示问题
	 * 参数：str为要处理的超长字符串
	 * 参数：objec为存放该字符串的可视化组件
	 * 返回值：简化后的字符串
	 */
	public static function simpleStr(str:String, objec:*):String
	{
		var backStr:String=str;
		if (str && objec)
		{
			var minnum:Number=int(objec.width / (objec.getStyle("fontSize")));
			if (str.length > minnum - 2)
			{
				//字符串比object组件的长度长时做处理
				backStr=str.slice(0, minnum - 1) + "...";
			}
		}
		return backStr;
	}

	/**
	 * 是否是数字
	 */
	public static function isNumeric(src:String):Boolean
	{
		if (StringHelper.isEmpty(src))
			return false;
		var regx:RegExp=/^[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?$/;
		return regx.test(src);
	}

	/**
	 * 返回姓名的字符串格式，只能输入汉字、字母（大小写）
	 */
	public static function nameFormat():String
	{
		var _str:String="\u4e00-\u9fa5\a-z\A-Z";
		return _str;
	}

	/**
	 *	Does a case insensitive compare or two strings and returns true if
	 *	they are equal.
	 *
	 *	@param s1 The first string to compare.
	 *
	 *	@param s2 The second string to compare.
	 *
	 *	@returns A boolean value indicating whether the strings' values are
	 *	equal in a case sensitive compare.
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function stringsAreEqual(s1:String, s2:String, caseSensitive:Boolean):Boolean
	{
		if (caseSensitive)
		{
			return (s1 == s2);
		}
		else
		{
			return (s1.toUpperCase() == s2.toUpperCase());
		}
	}

	/**
	 *	Removes whitespace from the front and the end of the specified
	 *	string.
	 *
	 *	@param input The String whose beginning and ending whitespace will
	 *	will be removed.
	 *
	 *	@returns A String with whitespace removed from the begining and end
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function trim(input:String):String
	{
		return StringHelper.ltrim(StringHelper.rtrim(input));
	}

	/**
	 *	Removes whitespace from the front of the specified string.
	 *
	 *	@param input The String whose beginning whitespace will will be removed.
	 *
	 *	@returns A String with whitespace removed from the begining
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function ltrim(input:String):String
	{
		var size:Number=input.length;
		for (var i:Number=0; i < size; i++)
		{
			if (input.charCodeAt(i) > 32)
			{
				return input.substring(i);
			}
		}
		return "";
	}

	/**
	 *	Removes whitespace from the end of the specified string.
	 *
	 *	@param input The String whose ending whitespace will will be removed.
	 *
	 *	@returns A String with whitespace removed from the end
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function rtrim(input:String):String
	{
		var size:Number=input.length;
		for (var i:Number=size; i > 0; i--)
		{
			if (input.charCodeAt(i - 1) > 32)
			{
				return input.substring(0, i);
			}
		}

		return "";
	}

	/**
	 *	Determines whether the specified string begins with the spcified prefix.
	 *
	 *	@param input The string that the prefix will be checked against.
	 *
	 *	@param prefix The prefix that will be tested against the string.
	 *
	 *	@returns True if the string starts with the prefix, false if it does not.
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function beginsWith(input:String, prefix:String):Boolean
	{
		return (prefix == input.substring(0, prefix.length));
	}

	/**
	 *	Determines whether the specified string ends with the spcified suffix.
	 *
	 *	@param input The string that the suffic will be checked against.
	 *
	 *	@param prefix The suffic that will be tested against the string.
	 *
	 *	@returns True if the string ends with the suffix, false if it does not.
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function endsWith(input:String, suffix:String):Boolean
	{
		return (suffix == input.substring(input.length - suffix.length));
	}

	/**
	 *	Removes all instances of the remove string in the input string.
	 *
	 *	@param input The string that will be checked for instances of remove
	 *	string
	 *
	 *	@param remove The string that will be removed from the input string.
	 *
	 *	@returns A String with the remove string removed.
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function remove(input:String, remove:String):String
	{
		return StringHelper.replace(input, remove, "");
	}

	/**
	 *	Replaces all instances of the replace string in the input string
	 *	with the replaceWith string.
	 *
	 *	@param input The string that instances of replace string will be
	 *	replaces with removeWith string.
	 *
	 *	@param replace The string that will be replaced by instances of
	 *	the replaceWith string.
	 *
	 *	@param replaceWith The string that will replace instances of replace
	 *	string.
	 *
	 *	@returns A new String with the replace string replaced with the
	 *	replaceWith string.
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function replace(input:String, replace:String, replaceWith:String):String
	{
		return input.split(replace).join(replaceWith);
	}


	/**
	 *	Specifies whether the specified string is either non-null, or contains
	 *  	characters (i.e. length is greater that 0)
	 *
	 *	@param s The string which is being checked for a value
	 *
	 * 	@langversion ActionScript 3.0
	 *	@playerversion Flash 9.0
	 *	@tiptext
	 */
	public static function stringHasValue(s:String):Boolean
	{
		//todo: this needs a unit test
		return (s != null && s.length > 0);
	}

}
}
