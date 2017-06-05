/**
 * 该文件用于定义一个类实例 FlexApp可以通过此类的实例动态生成ActionScript控件
 *
 * */
package com.estudio.flex.client
{

    import com.estudio.flex.component.DataGridEx;
    import com.estudio.flex.component.mx.RichEditorEx;
    import com.estudio.flex.component.mx.datagrid.render.CheckBoxHeaderRender;
    import com.estudio.flex.component.mx.datagrid.render.CheckBoxItemRender;
    import com.estudio.flex.component.mx.datagrid.render.CommonHeaderRender;
    import com.estudio.flex.component.mx.datagrid.render.CommonItemRender;
    import com.estudio.flex.component.mx.datagrid.render.EventItemRender;
    import com.estudio.flex.component.mx.datagrid.render.IconColumnRender4DynamicUI;
    import com.estudio.flex.component.mx.treeview.render.IconItemRender;
    import com.estudio.flex.utils.AlertUtils;
    import com.estudio.flex.utils.Convert;
    import com.estudio.flex.utils.StringUtils;
    import com.estudio.flex.utils.UIUtils;
    import com.iwobanas.controls.MDataGrid;
    import com.iwobanas.controls.dataGridClasses.MDataGridColumn;
    import com.iwobanas.controls.dataGridClasses.filterEditors.MultipleChoiceFilterEditor;
    
    import mx.controls.DataGrid;
    import mx.controls.MXFTETextInput;
    import mx.controls.RichTextEditor;
    import mx.controls.SWFLoader;
    import mx.controls.Text;
    import mx.controls.TextInput;
    import mx.controls.dataGridClasses.DataGridColumn;
    import mx.core.ClassFactory;
    import mx.events.DataGridEvent;
    import mx.managers.SystemManager;
    
    import spark.components.TextArea;



    /**
     * JavaScript动态创建Flex控件支持类
     * @author Administrator
     *
     */
    public class DynamicUI
    {

        private var swfMap:Object = {};

        /**
         * 构造函数 此函数全局只有一个唯一实例
         */
        public function DynamicUI()
        {
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //创建副文本内容
        public function createRichTextEditor():RichTextEditor
        {
            var result:RichEditorEx = new RichEditorEx ();
            result.percentWidth = 100;
            result.percentHeight = 100;
            return result;
        }

        public function createMemo():TextArea
        {
            var result:TextArea = new TextArea ();
            result.percentWidth = 100;
            result.percentHeight = 100;
            result.setStyle ("borderVisible" , false);
            result.setStyle ("borderThickness" , "0");
            return result;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * 根据columns的定义创建一个DataGrid
         * @param columns DataGrid中的数据列定义
         * @return 创建成功后的DataGrid
         *
         */
        public function createGrid(columns:Array):DataGrid
        {
            var result:DataGrid = null;
            var grid:DataGridEx = new DataGridEx ();
            grid.editable = true;
            grid.headerHeight = 28;
            grid.rowHeight = 25;
            grid.setStyle ("alternatingItemColors" , [0xFFFFFF , 0xFFFFFF]);
            grid.horizontalScrollPolicy = "auto";
            grid.verticalScrollPolicy = "auto";
            grid.setStyle ("borderStyle" , "none");
            UIUtils.fullAlign (grid);
            var editor:ClassFactory = null;

            var gridColumns:Array = grid.columns;
            for (var i:int = 0 ; i < columns.length ; i++)
            {
                var gridColumn:DataGridColumn = new DataGridColumn ();
                gridColumn.dataField = columns[i].dataField;
                gridColumn.headerText = columns[i].headerText;
                gridColumn.sortable = false; //columns[i].sortable;

                var render:ClassFactory = null;
                if (columns[i].isIcon)
                {
                    gridColumn.itemRenderer = new ClassFactory (IconColumnRender4DynamicUI);
                    gridColumn.width = 25
                    gridColumn.resizable = false;
                }
                else if (columns[i].isChecked)
                {
                    editor = new ClassFactory (CheckBoxHeaderRender);
                    gridColumn.headerRenderer = editor;
                    gridColumn.setStyle ("textAlign" , "center");
                    gridColumn.itemRenderer = new ClassFactory (CheckBoxItemRender);
                    gridColumn.width = StringUtils.isEmpty (columns[i].headerText) ? 25 : columns[i].width;
                    gridColumn.resizable = StringUtils.isEmpty (columns[i].headerText);
                }
                else
                {
                    gridColumn.headerRenderer = new ClassFactory (CommonHeaderRender);
                    if (columns[i].supportEvent)
                    {
                        editor = new ClassFactory (EventItemRender);
                        editor.properties = {frameID: columns[i].frameID , funName: columns[i].funName};
                        gridColumn.itemRenderer = editor;
                    }
                    else
                    {
                        gridColumn.itemRenderer = new ClassFactory (CommonItemRender);
                        ClassFactory (gridColumn.itemRenderer).properties = {columnStyle: columns[i].style};
                    }

                    gridColumn.width = columns[i].width;
                    gridColumn.setStyle ("textAlign" , StringUtils.nvl (columns[i].align , "left"));
                }
                gridColumn.editable = columns[i].editable;
                gridColumns.push (gridColumn);
            }
            grid.columns = gridColumns;
            return grid;
        }

        //////////////////////////////////////////////////////////////////////////////
        public function createSWF(url:String , iFrameID:String):SWFLoader
        {
            var swfLoader:SWFLoader = new SWFLoader ();
            swfLoader.x = 0;
            swfLoader.y = 0;
            swfLoader.percentWidth = 100;
            swfLoader.percentHeight = 100;
            swfLoader.load (url);
            swfLoader.visible = true;
            swfMap[iFrameID + url] = swfLoader;
            return swfLoader;
        }

        public function executeSWFFunction(url:String , iFrameID:String , funname:String , params:Object):void
        {
			var swfLoader:SWFLoader = swfMap[iFrameID + url] as SWFLoader;
			var app:Object = (swfLoader.content as SystemManager).application;
			app[funname](params);
        }

        /////////////////////////////////////////////////////////////////////////////

        //创建扩展DataGrid
        public function createGridEx(columns:Array):DataGrid
        {
            var grid:MDataGrid = new MDataGrid ();
            grid.editable = true;
            grid.headerHeight = 28;
            grid.rowHeight = 25;
            grid.setStyle ("alternatingItemColors" , [0xFFFFFF , 0xFFFFFF]);
            grid.horizontalScrollPolicy = "auto";
            grid.verticalScrollPolicy = "auto";
            grid.setStyle ("borderStyle" , "none");
            UIUtils.fullAlign (grid);
            var editor:ClassFactory = null;

            var gridColumns:Array = grid.columns;
            for (var i:int = 0 ; i < columns.length ; i++)
            {
                var gridColumn:DataGridColumn = null;
                var isFilter:Boolean = columns[i].isFilter;
                if (isFilter)
                {
                    var mDataGridColumn:MDataGridColumn = new MDataGridColumn ();
                    var filterType:String = columns[i].filterControl;
                    if (filterType == "multiCombobox")
                        mDataGridColumn.filterEditor = new ClassFactory (MultipleChoiceFilterEditor);
                    gridColumn = mDataGridColumn;
                }
                else
                {
                    gridColumn = new DataGridColumn ();
                }

                var render:ClassFactory = null;
                if (columns[i].isIcon)
                {
                    gridColumn.itemRenderer = new ClassFactory (IconColumnRender4DynamicUI);
                    gridColumn.width = 25
                    gridColumn.resizable = false;
                }
                else if (columns[i].isChecked)
                {
                    editor = new ClassFactory (CheckBoxHeaderRender);
                    if (!isFilter)
                        gridColumn.headerRenderer = editor;
                    gridColumn.setStyle ("textAlign" , "center");
                    gridColumn.itemRenderer = new ClassFactory (CheckBoxItemRender);
                    gridColumn.width = StringUtils.isEmpty (columns[i].headerText) ? 25 : columns[i].width;
                    gridColumn.resizable = StringUtils.isEmpty (columns[i].headerText);
                }
                else
                {
                    if (!isFilter)
                        gridColumn.headerRenderer = new ClassFactory (CommonHeaderRender);
                    if (columns[i].supportEvent)
                    {
                        editor = new ClassFactory (EventItemRender);
                        editor.properties = {frameID: columns[i].frameID , funName: columns[i].funName};
                        gridColumn.itemRenderer = editor;
                    }
                    else
                    {
                        gridColumn.itemRenderer = new ClassFactory (CommonItemRender);
                        ClassFactory (gridColumn.itemRenderer).properties = {columnStyle: columns[i].style};
                    }

                    gridColumn.width = columns[i].width;
                    gridColumn.setStyle ("textAlign" , StringUtils.nvl (columns[i].align , "left"));
                }

                gridColumn.dataField = columns[i].dataField;
                gridColumn.headerText = columns[i].headerText;
                gridColumn.sortable = false; //columns[i].sortable;
                gridColumn.editable = columns[i].editable;
                gridColumns.push (gridColumn);
            }
            grid.columns = gridColumns;
            return grid;
        }
    }
}
