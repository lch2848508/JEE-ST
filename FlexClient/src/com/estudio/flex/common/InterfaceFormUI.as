/**
 * 该文件用于定义一些同表单相关的接口函数
 *
 * */
package com.estudio.flex.common
{


    import mx.collections.ArrayCollection;
    import mx.core.UIComponent;


    /******************************************************************************************************
     * 接口定义:定义一些同输入表单相关公共接口
     *****************************************************************************************************/
    public interface InterfaceFormUI
    {

		function close():void;
		
		function get isCreateCompleted():Boolean;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 根据表单定义创建输入表单
         * @param formDefine 表单定义
         * @param isDialog 显示方式 true-表单以对话框的方式显示此时系统会自动在表单中添加保存及关闭按钮 alse-正常显示方式
         * @return 表单定义实例
         *
         */
        function createUI(formDefine:Object):UIComponent;

        /**
         * 设置控件值
         */
        function setControlStatus():void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 初始化表单参数 相当于Http URL 中?后面的内容
         * @param paramsDefine 参数定义
         *
         */
        function initParams(paramsDefine:Object):void;

		function initFormParams(param:Object):void;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取表单参数
         * @return 表单参数对象
         *
         */
        function getFormParams():Object;

        function get params():Object

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 初始化表单数据
         * @param formData 表单中各个DataSet的数据集合
         * @param isFullInit 是否全部初始化 true-初始化全部DataSet的值 通常用于第一次加载时 false-不初始化readonly的DataSet的值
         *
         */
        function initFormData(formData:Object , isFullInit:Boolean = true , isFirstRun:Boolean = false , isCreateIFrame:Boolean = false):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 设置表单是否为只读
         * @param value
         *
         */
        function set readonly(value:Boolean):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取表单是否为只读
         * @return true:表单只读 false:表单可读写
         *
         */
        function get readonly():Boolean;


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取表单中DataSet的值
         * @param datasetName dataset的名称
         * @param fieldName dataset字段名称
         * @return 返回字符串格式的值
         *
         */
        function getDataSetValue(datasetName:String , fieldName:String):String;

        /**
         * 表单数据是否为新数据
         * @return 真返回 True
         *
         */
        function isNew():Boolean;

        /**
         * 是否存在数据
         * */
        function existsRecord(datasetName:String):Boolean;

        /**
         * 是否存在数据集
         * */
        function existsDataSet(datasetName:String):Boolean;

        /**
         *保存表单数据
         */
        function save(forceRefreshGrid:Boolean = false):Boolean;


        /**
         * 保存表单数据扩展
         */
        function saveEx(isSendAfterSave:Boolean):void;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 设置表单中DataSet的值
         * @param datasetName dataset的名称
         * @param fieldName dataset的字段名称
         * @param value 需要设置的值
         *
         */
        function setDataSetValue(datasetName:String , fieldName:String , value:String , exceptControls:Array = null):void;

        function setDataSetValues(datasetName:String , values:Object):void;

        function batchSetDatasetRecordsByKeys(datasetName:String , keys:Array , records:Array):void;
		
		function updateDatasetValues(datasetName:String,records:Array):void;

		function clearDataSetRecords(datasetName:String):void;
		
		function deleteDataSetRecord(datasetName:String):void;
		
		function deleteDataSetRecordByKeys(datasetName:String,keys:Array):void;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取表单中控件的值
         * @param controlName 表单中控件的名称
         * @return 字符串格式的值
         *
         */
        function getControlValue(controlName:String):String;

		function getControlValueEx(controlName:String):String;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 设置表单中控件的值
         * @param controlName 表单中控件的名称
         * @param value 需要设置的控件值
         *
         */
        function setControlValue(controlName:String , value:String , extValue:String = null , isSettingFormDataService:Boolean = true):void;
		
        function setControlsEnabled(controlNames:Array , isEnabled:Boolean):void;

        function setControlsVisible(ControlNames:Array , isVisible:Boolean):void;

        function setGridBehaviour(controlName:String , addEnabled:Boolean , deleteEnabled:Boolean):void;

		function setTabSheetActivePage(pageControlName:String,tabSheetName:String):void;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 根据控件名称获取控件的实例
         * @param controlName 表单中控件的名称
         * @return 控件实例 如果控件不存在返回 null
         *
         */
        function getControl(controlName:String):UIComponent;


        function triggerAfterSaveEvent():void;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        function test():String;


        function resumePageControlSelectedIndex():void;

        function getDataSetRecords(datasetName:String):Array;

        function batchAppendRecords(datasetName:String , records:Array):int;

        function getDBGridSelectedItem(controlName:String):Object;


        function getMainDataSetKey():String;

        function selectDBGridByKeyField(controlName:String , keyFieldName:String , value:String):void;

        function getIFrameID():String;

        function get tag():String;

        function set tag(value:String):void;

        function copyForm(params:Object , datasetNames:Array):Boolean;

        function prepareFormShow():void;

        function clearComboboxItems(controlName:String):void;

        function getFormCaptions():ArrayCollection;
        function selectForm(index:int):void;

    }
}
