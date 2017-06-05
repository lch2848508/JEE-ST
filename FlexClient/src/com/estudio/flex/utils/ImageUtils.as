package com.estudio.flex.utils
{
	import flash.display.BitmapData;
	import flash.geom.Matrix;
	import flash.utils.ByteArray;

	import cmodule.aircall.CLibInit;


	public class ImageUtils
	{
		public function ImageUtils()
		{
		}

		private static var lib:Object=null;

		/**
		 * 编码为JPEG
		 */
		public static function bitmap2Jpeg(bitmap:BitmapData):ByteArray
		{
			if (lib == null)
				lib=new CLibInit().init();
			var ba:ByteArray=bitmap.getPixels(bitmap.rect);
			ba.position=0;
			var baout:ByteArray=new ByteArray();
			lib.encode(ba, baout, bitmap.width, bitmap.height, 85);
			return baout;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		//向左旋转90度 
		public static function rotat270(bmp:BitmapData):BitmapData
		{
			var m:Matrix=new Matrix();
			m.rotate(-Math.PI / 2);
			m.translate(0, bmp.width);
			var bd:BitmapData=new BitmapData(bmp.height, bmp.width, false);
			bd.draw(bmp, m);
			return bd;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function rotat90(bmp:BitmapData):BitmapData
		{
			var m:Matrix=new Matrix();
			m.rotate(Math.PI / 2);
			m.translate(bmp.height, 0);
			var bd:BitmapData=new BitmapData(bmp.height, bmp.width, false);
			bd.draw(bmp, m);
			return bd;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function rotat180(bmp:BitmapData):BitmapData
		{
			return rotat90(rotat90(bmp));
		}

	}
}
