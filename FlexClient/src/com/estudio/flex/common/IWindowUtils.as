package com.estudio.flex.common
{

    import mx.core.UIComponent;

    public interface IWindowUtils
    {
        //显示模式URL
        function modalURL(url:String , width:int , height:int , caption:String , callBackup:Object):void;
        //显示模式对话框
        function modalDialog(component:UIComponent , width:int , height:int , caption:String , callBackup:Object):void;
    }

}
