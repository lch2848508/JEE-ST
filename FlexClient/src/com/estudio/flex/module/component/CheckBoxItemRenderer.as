
package com.estudio.flex.module.component
{
	import flash.events.Event;
	import flash.events.MouseEvent;
	import mx.controls.CheckBox;
	import mx.controls.DataGrid;

	public class CheckBoxItemRenderer extends CenterCheckBox
	{
		private var currentData:Object; //保存当前一行值的对象 

		public function CheckBoxItemRenderer()
		{
			super();
			this.addEventListener(Event.CHANGE, onClickCheckBox);
			this.toolTip="选择";
		}

		override public function set data(value:Object):void
		{
			this.selected=value.dgSelected;
			this.currentData=value; //保存整行的引用 
		}

		//点击check box时，根据状况向selectedItems array中添加当前行的引用，或者从array中移除 
		private function onClickCheckBox(e:Event):void
		{
			var dg:DataGrid=DataGrid(listData.owner); //获取DataGrid对象 
			var column:CheckBoxColumn=dg.columns[listData.columnIndex]; //获取整列的显示对象 
			var selectItems:Array=column.selectItems;
			this.currentData.dgSelected=this.selected; //根据是否选中的状态，更改数据源中选中的标记 
			if (this.selected)
			{
				selectItems.push(this.currentData);
			}
			else
			{
				for (var i:int=0; i < selectItems.length; i++)
				{
					if (selectItems[i] == this.currentData)
					{
						selectItems.splice(i, 1)
					}
				}
			}
		}
	}
}
