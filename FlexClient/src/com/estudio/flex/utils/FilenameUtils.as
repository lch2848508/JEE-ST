package com.estudio.flex.utils
{

	public final class FilenameUtils
	{
		public function FilenameUtils()
		{
		}

		public static function getFileExt(filename:String):String
		{
			var index:int=filename.lastIndexOf(".");
			if (index == -1)
				return "";
			return filename.substr(index + 1);
		}

		public static function getFileName(url:String):String
		{
			var index:int=url.lastIndexOf("/");
			if (index == -1)
				return url;
			return url.substr(index + 1);
		}

		public static function getFileBaseName(url:String):String
		{
			var index:int=url.lastIndexOf(".");
			if (index == -1)
				return url;
			return url.substr(0, index);
		}
	}
}
