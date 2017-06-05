package com.estudio.flex.utils
{
	import mx.events.IndexChangedEvent;

	public class ArrayUtils
	{
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function remove(source:Array, index:int):Object
		{
			var result:Object=source[index];
			source.splice(index, 1);
			return result;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function indexOf(source:Array, obj:Object):int
		{
			return source.indexOf(obj);
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function insert(source:Array, obj:Object, index:int):void
		{
			source.push(null);
			for (var i:int=source.length - 1; i > index; i--) 
				source[i]=source[i - 1];
			source[index]=obj;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function find(source:Array, keyName:String, value:String):int
		{
			var result:int=-1;
			if (!StringUtils.isEmpty(value))
			{
				for (var i:int=0; i < source.length; i++)
				{
					if (StringUtils.equal(source[i][keyName], value))
					{
						result=i;
						break;
					}
				}
			}
			return result;
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function contain(source:Array, obj:Object):Boolean
		{
			return source.indexOf(obj) != -1;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function isEmpty(array:Array):Boolean
		{
			return array == null || array.length == 0;
		}

		public static function addAll(toArray:Array, formArray:Array):void
		{
			for (var i:int=0; i < formArray.length; i++)
				toArray.push(formArray[i]);
		}

		public static function addItemAt(toArray:Array, v:Object, index:int):void
		{
			toArray.push(null);
			for (var i:int=toArray.length - 1; i > index; i--)
				toArray[i]=toArray[i - 1];
			toArray[index]=v;
		}

	}
}
