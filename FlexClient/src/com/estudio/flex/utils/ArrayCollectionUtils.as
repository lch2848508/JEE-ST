package com.estudio.flex.utils
{
	import mx.collections.ArrayCollection;
	import mx.utils.StringUtil;

	public class ArrayCollectionUtils
	{
		public function ArrayCollectionUtils()
		{
		}

		//查找数据
		public static function indexOf(collection:ArrayCollection, fieldName:String, value:String):int
		{
			var result:int=-1;
			if (collection && collection.length != 0)
			{
				for (var i:int=0; i < collection.length; i++)
				{
					if (StringUtils.equal(collection.getItemAt(i)[fieldName], value))
					{
						result=i;
						break;
					}
				}
			}
			return result;
		}

		/**
		 *平面数据分组为层次数据
		 */
		public static function FlatToHierarchData(sources:Array, groupFieldName:String):Array
		{
			var result:Array=[];
			var groupValue:String="";
			var groupObject:Object=null;
			var map:Object={};
			for (var i:int=0; i < sources.length; i++)
			{
				var obj:Object=sources[i];
				var objG:String=StringUtils.nvl(obj[groupFieldName], "");
				if (!StringUtils.equal(objG, groupValue) || !groupObject)
				{
					groupValue=objG;
					var key:String=groupValue;
					if (StringUtils.isEmpty(groupValue))
						key="??key??";
					groupObject=map[key];
					if (!groupObject)
					{
						groupObject={children: [], __group__: objG};
						result.push(groupObject);
						map[key]=groupObject;
					}
				}
				groupObject.children.push(obj);
			}
			return result;
		}

		public static function TreeData2List(Sources:Array, To:Array, generateLevel:Boolean=false, levelField:String="level", startLevel:int=0):void
		{
			for (var i:int=0; i < Sources.length; i++)
			{
				var record:Object=Sources[i];
				To.push(record);
				if (generateLevel)
					record[levelField]=startLevel;
				if (record.children && record.children.length != 0)
					TreeData2List(record.children, To, generateLevel, levelField, startLevel + 1);
			}
		}

		public static function Filter(Source:Array, To:Array, filterFun:Function):void
		{
			var temp:Array=[];
			TreeData2List(Source, temp);
			for (var i:int=0; i < temp.length; i++)
			{
				if (filterFun(temp[i]))
					To.push(temp[i]);
			}
		}

		public static function find(Source:Array, filterFun:Function):Object
		{
			var temp:Array=[];
			TreeData2List(Source, temp);
			for (var i:int=0; i < temp.length; i++)
			{
				if (filterFun(temp[i]))
					return temp[i];
			}
			return null;
		}

		public static function flagRecordModified(record:Object):void
		{
			if (!record.__isnew__)
				record.__modified__=true;
		}

		public static function flagRecordNew(record:Object):void
		{
			record.__isnew__=true;
		}


		public static function unflagRecordModified(record:Object):void
		{
			record.__isnew__=false;
			record.__modified__=false;
		}

		//获取新增的或删除的数据
		public static function getNewAndModifiedRecords(source:Array, newRecords:Array, modifiedRecords:Array):void
		{
			var list:Array=[];
			TreeData2List(source, list);
			for (var i:int=0; i < list.length; i++)
			{
				var record:Object=list[i];
				if (record.__isnew__)
					newRecords.push(record);
				else if (record.__modified__)
					modifiedRecords.push(record);
			}
		}

	}
}
