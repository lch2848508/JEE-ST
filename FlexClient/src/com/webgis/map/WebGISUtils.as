package com.webgis.map
{
	import com.esri.ags.Map;
	import com.esri.ags.geometry.MapPoint;
	import com.esri.ags.utils.WebMercatorUtil;
	import com.estudio.flex.utils.Convert;
	import com.estudio.flex.utils.StringUtils;

	public class WebGISUtils
	{
		public function WebGISUtils()
		{

		}


		public static function number2DFM(du:Number):String
		{
			var str1:Array=(du + "").split(".");
			var du1:String=str1[0];
			var tp="0." + str1[1]
			var tp=String(tp * 60); //这里进行了强制类型转换
			var str2=tp.split(".");
			var fen=str2[0];
			tp="0." + str2[1];
			tp=tp * 60;
			var miao=Math.round(tp);
			return du1 + "°" + fen + "′" + miao + "″";
		}

		public static function dfm2Number(str:String):Number
		{
			str=StringUtils.replace(str, "°", ";");
			str=StringUtils.replace(str, "′", ";");
			str=StringUtils.replace(str, "″", ";");
			str=StringUtils.replace(str, "'", ";");
			str=StringUtils.replace(str, "\"", ";");
			var ss:Array=str.split(";");

			var d:Number=Convert.str2Number(ss[0], 0);
			var f:Number=Convert.str2Number(ss[1], 0);
			var m:Number=Convert.str2Number(ss[2], 0);
			return d + f / 60 + m / 3600;
		}

		private static var EARTH_RADIUS:Number=6378137; //地球半径

		private static function rad(d:Number):Number
		{
			return d * Math.PI / 180.0;
		}

		public static function getDistance(p1:MapPoint,p2:MapPoint, isDegress:Boolean):Number
		{
			if (isDegress)
			{
				var radp1:Number=rad(p1.y);
				var radp2:Number=rad(p2.y);
				var a:Number=radp1 - radp2;
				var b:Number=rad(p1.x) - rad(p2.x);

				var s:Number=2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radp1) * Math.cos(radp2) * Math.pow(Math.sin(b / 2), 2)));
				s=s * EARTH_RADIUS;
				return s;
			}
			else
			{
				return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2))
			}
		}

	}
}
