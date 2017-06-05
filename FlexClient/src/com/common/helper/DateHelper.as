package vendy.com.common.helper
{
import mx.formatters.DateFormatter;

/*******************************************
 * 描述: 时间帮助
 * <p>版权所有: horizo
 * <p>创建者: user
 * <p>创建日期: Oct 22, 2010
 * <p>修改者:
 * <p>修改日期:
 * <p>修改说明:
 *******************************************/
public class DateHelper
{
	public function DateHelper()
	{
	}

	/**
	 * 方法含义：计算年龄<br/>
	 * 参数birthday为出生日期<br/>
	 * 返回为负值时，表示出生日期输入有误<br/>
	 * 1岁以下，为'0.x'的格式
	 */
	public static function countAge(birthdate:Date):String
	{
		var age:String="0";
		var year:Number=1000 * 60 * 60 * 24 * 365;
		var now:Number=new Date().getTime() / year;
		var old:Number=birthdate.getTime() / year;
		var tmp:Number=now - old
		if (tmp < 0)
		{
			tmp=0 - tmp;
			if (tmp >= 1)
				age=tmp.toFixed(0);
			else
				age="1";
			age="-" + age;
		}
		else
		{
			if (tmp >= 1)
				age=tmp.toFixed(0);
			else
				age=tmp.toFixed(1);
		}
		return age;
	}

	/**
	 * 方法含义：把日期转换为字符串
	 * @param date为时间，类型可以是字符串，也可是是时间类型；其他格式则默认的设为当前时间
	 * @例如：20101101120101（只由数字组成的字符串），即2010年11月1号12点1分1秒
	 * @param dateformatter为转换后的时间字符串格式，参数详见DateFormatter类型的formatString属性
	 * 掩码常用标识如下：
	 * Y:年；
	 * M:月；
	 * D:日；
	 * E:星期；
	 * A:am/pm标记；
	 * J:小时；
	 * K:am/pm 中的小时数 (0-11)；
	 * L:am/pm 中的小时数 (1-12)；
	 * N:分钟；
	 * S:秒数；
	 * @see DateFormatter
	 */
	public static function dateToString(date:*, dateformatter:String):String
	{
		var dateTmp:Date;
		if (date is String)
		{
			//字符串格式的时间，注只由数字组成的字符串
			var datestr:String=date as String;
			var y:uint=uint(datestr.slice(0, 4));
			var m:uint=uint(datestr.slice(4, 6)) - 1;
			m=(m > 11) ? 11 : m;
			var d:uint=uint(datestr.slice(6, 8));
			switch (m + 1)
			{
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
					d=(d > 31 || d == 0) ? 31 : d;
					break;
				case 2:
					d=(d > 28 || d == 0) ? 28 : d;
					break;
				case 4:
				case 6:
				case 9:
				case 11:
					d=(d > 30 || d == 0) ? 30 : d;
					break;
			}
			var h:uint=uint(datestr.slice(8, 10));
			h=(h > 23) ? 23 : h;
			var f:uint=uint(datestr.slice(10, 12));
			f=(f > 59) ? 59 : f;
			var s:uint=uint(datestr.slice(12, 14));
			s=(s > 59) ? 59 : s;
			dateTmp=new Date(y, m, d, h, f, s);

		}
		else if (date as Date)
		{
			//
			dateTmp=date as Date;
		}
		else
			dateTmp=new Date();
		var format:DateFormatter=new DateFormatter();
		format.formatString=dateformatter;
		return format.format(dateTmp);
	}


	/**
		*
		* @return   2008
		* @function 获得两个时间的天数
	*/

	public static function getDateDiff(startDate:Date, endDate:Date):int
	{
		var diff:Number=(Number(endDate) - Number(startDate)) / (3600000 * 24);
		return diff;
	}


	/**
	 *
	 * @param year 365
	 * @return 获得给定年的天数
	 *
	 */
	public static function getDaysOfYear(year:int):int
	{
		var startDate:Date=new Date(year, 0, 1);
		var endDate:Date=new Date(year + 1, 0, 1);
		return (getDateDiff(startDate, endDate));
	}

	/**
	 *
	 * @param year  2008
	 * @param month 10
	 * @return  30
	 * @function 获得给定年、月的天数
	 */
	public static function getDaysOfMonth(year:int, month:int):int
	{
		var startDate:Date=new Date(year, month, 1);
		var endDate:Date=new Date(year, month + 1, 1);
		return (getDateDiff(startDate, endDate));

	}

	/**
	 *
	 * @param year  2008
	 * @return 12
	 * @function 获得给定年 全年月份的天数
	 *
	 */
	public static function getMonthlyDayCounts(year:int):Array
	{
		var result:Array=[];
		var startDate:Date;
		var endDate:Date;
		var monthlyDayCount:int;
		for (var i:int=0; i < 12; i++)
		{
			startDate=new Date(year, i, 1);
			endDate=new Date(year, i + 1, 1);
			monthlyDayCount=getDateDiff(startDate, endDate);
			result.push(monthlyDayCount);
		}
		return result;
	}

	/**
	 *
	 * @param date1
	 * @param date2
	 * @return 1 date1 < date2,		0 date1 = date2，		-1 date1 >date2
	 * @function: 比较两个日期大小
	 */
	public static function compare(date1:Date, date2:Date):int
	{
		if (Number(date1) < Number(date2))
		{
			return -1
		}
		if (Number(date1) == Number(date2))
		{
			return 0;
		}
		return 1;
	}

}
}
