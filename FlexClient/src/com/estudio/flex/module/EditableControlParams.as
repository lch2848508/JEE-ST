package com.estudio.flex.module
{
	import com.estudio.flex.common.InterfaceFormUI;


//可编辑控件基础类
	public class EditableControlParams
	{
		public function EditableControlParams()
		{
		}

		public var databaseName:String="";
		public var fieldName:String="";
		public var extFieldName:String="";
		public var readonly:Boolean=false;
		public var defaultReadOnly:Boolean=false;
		public var isBindDatasource:Boolean=false;
		public var dataservice:FormDataService=null;
		public var formInstance:InterfaceFormUI=null;
		public var eventMap:Object={};

		public static const CONST_INPUTTEXT:int=0;
		public static const CONST_COMBOBOX:int=1;
		public static const CONST_LOOKUP_COMBOBOX:int=2;
		public static const CONST_CHECKBOX:int=3;
		public static const CONST_GRID:int=4;
		public static const CONST_FILEUPLOAD:int=5;
		public static const CONST_MEMO:int=6;
		public static const CONST_RICHEDIT:int=7;
		public static const CONST_PICTURE:int=8;
		public static const CONST_DATE_EX:int=9;
		public static const CONST_LABEL:int=10;
		public static const CONST_FILEUPLOADSIMP:int=11;
	}
}
