package com.estudio.flex.common
{

    public interface InterfaceQueryGroup
    {
        //创建界面
        function initQueryList(items:Array,isSameQueryCondition:Boolean):Boolean;

        //刷新数据
        function refresh():void;

        //IFrame ID
        function getIFrameID():String;

        //附加信息
        function get tag():String;

        //附加信息
        function set tag(value:String):void;
		
		function setFilterValues(params:Object):void;
    }
}
