/**
 * 该文件用于定义一些同列表栏目相关的接口
 *
 * */
package com.estudio.flex.common
{

    import flash.events.TextEvent;

    import mx.controls.DataGrid;
    import mx.controls.Tree;
    import mx.controls.listClasses.ListBase;
    import mx.core.UIComponent;

    import spark.components.Group;
    import spark.components.NavigatorContent;

    /******************************************************************************************************
     * 接口定义:定义一些同列表栏目相关的函数
     *****************************************************************************************************/
    public interface InterfacePortalGrid
    {

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 根据预定义创建列表栏目
         * @param gridDefine 列表栏目定义
         * @return 返回创建成功后的UI组件
         *
         */
        function createUI(gridDefine:Object):UIComponent;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         *刷新数据
         */
        function update():void;



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 得到栏目中同Grid或Tree绑定的表单定义
         * @param isTree true-取得同Tree绑定的表单定义 false-取得同Grid绑定的表单定义
         * @return 表单定义
         *
         */
        function getForms(isTree:Boolean):Object;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 取得栏目新增或保存数据后需要调用的回调函数的定义
         * @param isTree true-获取同Tree绑定的回调函数 false-获取同Grid绑定的回调函数
         * @return 回调函数
         *
         */
        function getCallfun(isTree:Boolean):Object;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 取得编辑或新增栏目数据时需要传递给表单的参数
         * @param isTree true-获取同Tree相关的参数 false-获取同Grid相关的参数
         * @param isNew 是否为新增数据 新增数据在返回的对象中一些唯一标识性的字段值为 null
         * @return 参数对象
         *
         */
        function getParams(isTree:Boolean , isNew:Boolean):Object;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 取得编辑或新增栏目数据时弹出表单的标题
         * @param isTree true-显示同Tree相关的标题 false-显示同Grid相关的标题
         * @param isCaption
         * @return 表单标题
         *
         */
        function getFormCaption(isTree:Boolean , isCaption:Boolean):String;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 取得栏目中Tree或Grid中当前选中数据的唯一主键
         * @param isTree true-取得Tree选中数据的唯一主键 false-取得Grid选中数据的唯一主键
         * @param isNew 是否记录为新增的记录 true-返回null false 正常返回
         * @param defaultValue 缺省值
         * @return
         *
         */
        function getSelectedID(isTree:Boolean , isNew:Boolean , defaultValue:String):Object;


        function getSelectedKey(isTree:Boolean , isNew:Boolean):String;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取列表栏目的ID
         * @return 栏目ID
         *
         */
        function getPortalID():String;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 设置列表栏目是否只读
         * @param value true-栏目只读 false-栏目可读写
         *
         */

        function set readonly(value:Boolean):void;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * 获取列表栏目是否只读
         * @return
         *
         */
        function get readonly():Boolean;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取列表栏目中预定义的布局区域
         * @param name 布局区域名称 参数值有固定的定义
         * @return 布局中的某一固定区域
         *
         */
        function getLayout(name:String):Group;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取列表栏目中DataGrid实例
         * @return DataGrid对象
         *
         */
        function getGrid():DataGrid;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 获取当前列表栏目中的Tree实例
         * @return Tree对象
         *
         */
        function getTree():ListBase;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 在数据列表中新增一条记录
         */
        function funGridNew():void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 在Tree中新增一条记录
         * @param isSameLevel true-增加同级节点 false-当前节点的子节点
         *
         */
        function funTreeNew(isSameLevel:Boolean):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 删除Tree中选中的记录
         * @param isDeleteAllCheck 是否删除所有CheckBox被选中的记录
         *
         */
        function funTreeDelete(isDeleteAllCheck:Boolean = true):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 编辑Tree中被选中的记录
         */
        function funTreeEdit():void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 交换Tree中被选定记录的上下顺序
         * @param isUp true-向上移动 false-向下移动
         *
         */
        function funTreeExchange(isUp:Boolean):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 编辑Grid中被选中的记录
         */
        function funGridEdit():void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 删除被选择的Grid记录
         * @param isDeleteAllCheck true-删除所有CheckBox被选中的记录 false-只删除当前被选中的记录
         *
         */
        function funGridDelete(isDeleteAllCheck:Boolean = true):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 交换Grid中当前被选中记录的上下顺序
         * @param isGridUp true-向上移动 false-向下移动
         *
         */
        function funGridExchange(isGridUp:Boolean):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 刷新Tree中的数据
         *
         */
        function funTreeRefresh():void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 刷新Grid中的数据
         *
         */
        function funGridRefresh():void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 得到Tree中显示的所有数据
         * @return 无数据返回空数组
         *
         */
        function getTreeDatas():Array;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 得到Tree中所有被选中的数据
         * @return 无选中数据时返回空数组
         *
         */
        function getTreeSelectedItems():Array;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 得到Tree中当前被选中的数据
         * @return 无选中数据返回null
         *
         */
        function geTreeSelectedItem():Object;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 得到Grid中显示的当前页的所有数据
         * @return 无数据返回空数组
         *
         */
        function getGridDatas():Array;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 得到Grid中所有被选中的数据
         * @return 无选中数据返回空数组
         *
         */
        function getGridSelectedItems():Array;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 得到Grid中被选中的数据
         * @return 返回选中的记录 如果无选中的记录返回 null
         */
        function getGridSelectedItem():Object;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 翻页列表栏目中的DataGrid
         * @param page 页码 起始页为:1
         *
         */
        function gotoPage(page:int):void;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 翻页列表栏目中的DataGrid
         * @param recordPerPage 每页需要显示的条数
         * @param page 页码 起始页为:1
         *
         */
        function goPage(recordPerPage:int , page:int):void;

        function get TabSheet():NavigatorContent;

        function set TabSheet(value:NavigatorContent):void;

        function getToolBarItem(name:String):UIComponent;

        function setToolBarItemEnabled(name:String , enabled:Boolean):void;

        function setGridCellValue(keyValue:String , fieldname:String , value:* , refresh:Boolean = true):void;

        function setGridCellsValue(keyValues:Array , fieldname:String , value:* , refresh:Boolean = true):void;

        function selectGridItem(keyValue:String , refresh:Boolean = true):void;

        function batchSetGridCellsValue(keyValues:Array , fieldnames:* , records:Array , refresh:Boolean = true):void;

        function refreshGridSelectedItem():void;

        function refresh():void;

        function getIFrameID():String;

        function get tag():String;

        function set tag(value:String):void;

        function get isCommonSearch():Boolean;
    }
}
