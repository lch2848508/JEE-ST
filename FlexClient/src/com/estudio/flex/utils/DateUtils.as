package com.estudio.flex.utils
{

	public class DateUtils
	{

		public static function greToLunarArray(greYear:int, greMonth:int, greDay:int):Array
		{
			var nongDate:Array=new Array(7);
			var i:int=0;
			var temp:int=0;
			var leap:int=0;

			var offset:Number=(Number)((new Date(greYear, greMonth - 1, greDay).time - new Date(00, 0, 31).time) / 86400000);
			nongDate[5]=offset + 40;
			nongDate[4]=14;
			for (i=1900; i < 2050 && offset > 0; i++)
			{
				temp=lYearDays(i);
				offset-=temp;
				nongDate[4]+=12;
			}
			if (offset < 0)
			{
				offset+=temp;
				i--;
				nongDate[4]-=12;
			}
			nongDate[0]=i;
			nongDate[3]=i - 1864;
			leap=leapMonth(i); //闰哪个月
			nongDate[6]=0;
			for (i=1; i < 13 && offset > 0; i++)
			{ //闰月
				if (leap > 0 && i == (leap + 1) && nongDate[6] == 0)
				{
					i--;
					nongDate[6]=1;
					temp=leapDays((int)(nongDate[0]));
				}
				else
				{
					temp=monthDays((int)(nongDate[0]), i);
				}
				//解除闰月
				if (nongDate[6] == 1 && i == (leap + 1))
					nongDate[6]=0;
				offset-=temp;
				if (nongDate[6] == 0)
					nongDate[4]++;
			}

			if (offset == 0 && leap > 0 && i == leap + 1)
			{
				if (nongDate[6] == 1)
				{
					nongDate[6]=0;
				}
				else
				{
					nongDate[6]=1;
					i--;
					nongDate[4]--;
				}
			}
			if (offset < 0)
			{
				offset+=temp;
				i--;
				nongDate[4]--;
			}
			nongDate[1]=i;
			nongDate[2]=offset + 1;
			return nongDate;
		}


		/*返回农历年的总天数*/
		public static function lYearDays(year:int):int
		{
			var i:int;
			var sum:int=348;
			for (i=0x8000; i > 0x8; i>>=1)
			{
				if ((lunarInfoArr[year - 1900] & i) != 0)
					sum+=1;
			}
			return sum + leapDays(year);
		}

		/*返回农历年闰月的天数*/
		public static function leapDays(lunarYear:int):int
		{
			if (leapMonth(lunarYear) != 0)
				return ((lunarInfoArr[lunarYear - 1900] & 0x10000) != 0) ? 30 : 29;
			return 0;
		}

		/*返回农历年闰月 1-12 , 没闰返回 0*/
		public static function leapMonth(y:int):int
		{
			return (int)(lunarInfoArr[y - 1900] & 0xf);
		}

		/*返回农历年月的总天数*/
		public static function monthDays(y:int, m:int):int
		{
			return ((lunarInfoArr[y - 1900] & (0x10000 >> m)) == 0) ? 29 : 30;
		}

		public static var lunarInfoArr:Array=[0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0];

		private static var nlmonths:Array=["", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"];
		private static var nldays:Array=["", "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"];
		private static var weekdays:Array=["日", "一", "二", "三", "四", "五", "六"];

		public static function getTodayStr():String
		{
			var d:Date=new Date();
			var nl:Array=greToLunarArray(d.fullYear, d.month + 1, d.date);
			return d.fullYear + "-" + (d.month + 1) + "-" + d.date + " 星期" + weekdays[d.day] + " 农历" + nlmonths[nl[1]] + "月" + nldays[nl[2]] + " ";
		}
	}
}
