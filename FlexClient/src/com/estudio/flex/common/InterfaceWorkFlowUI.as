package com.estudio.flex.common
{

    public interface InterfaceWorkFlowUI
    {
        //创建界面
        function createUI(uiDefine:Object):Boolean;

        //刷新数据
        function refresh():void;

        //IFrame ID
        function getIFrameID():String;

        //附加信息
        function get tag():String;

        //附加信息
        function set tag(value:String):void;

        function get ui_id():String;

        function set ui_id(v:String):void;

        function getUIOptions():Object;

    }
}
