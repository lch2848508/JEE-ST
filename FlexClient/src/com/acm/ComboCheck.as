/*
* ComboCheck
* v1.6.07
* Arcadio Carballares Martín, 2011
* http://www.arcadiocarballares.es
* Creative Commons - http://creativecommons.org/licenses/by-sa/2.5/es/deed.en_GB
*/
package com.acm
{
import flash.display.DisplayObject;

import mx.collections.IList;
import mx.core.UIComponent;

import spark.components.ComboBox;
import spark.components.DropDownList;
import spark.events.IndexChangeEvent;

[Event(name="change", type="spark.events.IndexChangeEvent")]

[Event(name="itemAdded", type="com.acm.ComboCheckEvent")]
[Event(name="itemRemoved", type="com.acm.ComboCheckEvent")]
[Event(name="itemSelected", type="com.acm.ComboCheckEvent")]
[Event(name="itemsSelected", type="com.acm.ComboCheckEvent")]
[Event(name="selectAll", type="com.acm.ComboCheckEvent")]
[Event(name="deselectAll", type="com.acm.ComboCheckEvent")]

public class ComboCheck extends UIComponent implements IComboCheck
{
	private static const DEFAULT_HEIGHT:int=23;
	private static const DEFAULT_WIDTH:int=125;
	private static const DEFAULT_TYPE:String="combobox";
	private static const DEFAULT_LABEL:String="label";
	private static const DEFAULT_ROWCOUNT:int=12;
	private static const DEFAULT_SELECT_ALL:String="selectAll";
	private static const DEFAULT_SELECTED:String="selected";

	private var _type:String;

	[Bindable]
	[Inspectable(defaultValue="combobox", enumeration="combobox,combolist")]
	public function set type(value:String):void
	{
		_type=value;
		createCombo();
	}

	public function get type():String
	{
		return _type;
	}

	private var _dataProvider:IList;

	public function set dataProvider(value:IList):void
	{
		_dataProvider=value;
		combo.dataProvider=value;
	}

	public function get dataProvider():IList
	{
		return _dataProvider as IList;
	}

	private var combo:IComboCheckType;

	public function get component():IComboCheckType
	{
		return combo;
	}

	private var _labelField:String;

	[Bindable]
	public function set labelField(value:String):void
	{
		_labelField=value;
		combo.labelField=value;
	}

	public function get labelField():String
	{
		return _labelField;
	}

	private var _labelToItemFunction:Function;

	[Bindable]
	public function set labelToItemFunction(value:Function):void
	{
		_labelToItemFunction=value;
		if (type == 'combobox')
		{
			ComboBox(combo).labelToItemFunction=value;
		}
	}

	public function get labelToItemFunction():Function
	{
		return _labelToItemFunction;
	}

	private var _labelFunction:Function;

	[Bindable]
	public function set labelFunction(value:Function):void
	{
		_labelFunction=value;
		combo.labelFunction=value;
	}

	public function get labelFunction():Function
	{
		return _labelFunction;
	}

	private var _selectedIndex:int;

	[Bindable]
	public function set selectedIndex(value:int):void
	{
		_selectedIndex=value;
		combo.selectedIndex=value;
	}

	public function get selectedIndex():int
	{
		return _selectedIndex;
	}

	private var _rowCount:int;

	[Bindable]
	public function set rowCount(value:int):void
	{
		_rowCount=value;
		combo.rowCount=value;
	}

	public function get rowCount():int
	{
		return _rowCount;
	}

	private var _selectedItem:*;

	[Bindable("itemSelected")]
	public function set selectedItem(value:*):void
	{
		_selectedItem=value;
	}

	public function get selectedItem():*
	{
		return combo.getSelectedItem();
	}

	private var _selectedItems:Vector.<Object>;

	[Bindable("itemsSelected")]
	public function set selectedItems(value:Vector.<Object>):void
	{
		_selectedItems=value;
	}

	public function get selectedItems():Vector.<Object>
	{
		return combo.getSelectedItems();
	}

	private var _dropDownHeight:Number;

	[Bindable]
	public function set dropDownHeight(value:Number):void
	{
		_dropDownHeight=value;
		combo.dropDownHeight=value;
	}

	public function get dropDownHeight():Number
	{
		return _dropDownHeight;
	}

	private var _selectAllLabelField:String;

	[Bindable]
	public function set selectAllLabelField(value:String):void
	{
		_selectAllLabelField=value;
		combo.selectAllLabelField=value;
	}

	public function get selectAllLabelField():String
	{
		return _selectAllLabelField;
	}

	private var _selectedLabelField:String;

	[Bindable]
	public function set selectedLabelField(value:String):void
	{
		_selectedLabelField=value;
		combo.selectedLabelField=value;
	}

	public function get selectedLabelField():String
	{
		return _selectedLabelField;
	}

	public function setLabel(value:String):void
	{
		combo.setLabel(value);
	}

	private var _prompt:String;

	[Bindable]
	public function set prompt(value:String):void
	{
		_prompt=value;
		combo.prompt=value;
	}

	public function get prompt():String
	{
		return _prompt;
	}

	public function ComboCheck()
	{
		super();

		// Initialize default properties
		height=DEFAULT_HEIGHT;
		width=DEFAULT_WIDTH;
		type=DEFAULT_TYPE;
		labelField=DEFAULT_LABEL;
		;
		rowCount=DEFAULT_ROWCOUNT;
		selectAllLabelField=DEFAULT_SELECT_ALL;
		selectedLabelField=DEFAULT_SELECTED;
	}

	private function createCombo():void
	{
		switch (type)
		{
			case "combobox":
				combo=new ComboCheckBox();
				break;
			case "combolist":
				combo=new ComboCheckList();
				break;
			default:
				combo=new ComboCheckBox();
				break;
		}

		UIComponent(combo).addEventListener(ComboCheckEvent.ITEM_SELECTED, onItemSelected);
		UIComponent(combo).addEventListener(ComboCheckEvent.ITEMS_SELECTED, onItemsSelected);
		UIComponent(combo).addEventListener(ComboCheckEvent.ITEM_REMOVED, onItemRemoved);
		UIComponent(combo).addEventListener(ComboCheckEvent.ITEM_ADDED, onItemAdded);
		UIComponent(combo).addEventListener(ComboCheckEvent.SELECT_ALL, onSelectAll);
		UIComponent(combo).addEventListener(ComboCheckEvent.DESELECT_ALL, onDeselectAll);

		UIComponent(combo).addEventListener(IndexChangeEvent.CHANGE, onChange);

		// Init combo values
		combo.selectedLabelField=selectedLabelField;
		combo.selectAllLabelField=selectAllLabelField;
		combo.rowCount=rowCount;
	}

	private function onItemSelected(event:ComboCheckEvent):void
	{
		dispatchEvent(new ComboCheckEvent(ComboCheckEvent.ITEM_SELECTED));
	}

	private function onItemsSelected(event:ComboCheckEvent):void
	{
		dispatchEvent(new ComboCheckEvent(ComboCheckEvent.ITEMS_SELECTED));
	}

	private function onSelectAll(event:ComboCheckEvent):void
	{
		dispatchEvent(new ComboCheckEvent(ComboCheckEvent.SELECT_ALL));
	}

	private function onDeselectAll(event:ComboCheckEvent):void
	{
		dispatchEvent(new ComboCheckEvent(ComboCheckEvent.DESELECT_ALL));
	}

	private function onChange(event:IndexChangeEvent):void
	{
		dispatchEvent(new IndexChangeEvent(IndexChangeEvent.CHANGE));
	}

	private function onItemAdded(event:ComboCheckEvent):void
	{
		var evt:ComboCheckEvent=new ComboCheckEvent(ComboCheckEvent.ITEM_ADDED);
		evt.data=event.data;
		dispatchEvent(evt);
	}

	private function onItemRemoved(event:ComboCheckEvent):void
	{
		var evt:ComboCheckEvent=new ComboCheckEvent(ComboCheckEvent.ITEM_REMOVED);
		evt.data=event.data;
		dispatchEvent(evt);
	}

	override protected function commitProperties():void
	{
		super.commitProperties();
	}

	override protected function createChildren():void
	{
		super.createChildren();
		addChild(combo as DisplayObject)
	}

	override protected function measure():void
	{
		super.measure();
	}

	override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
	{
		super.updateDisplayList(unscaledWidth, unscaledHeight);
		UIComponent(combo).setActualSize(width, height);
	}

	public function selectAll():void
	{
		combo.selectAll();
	}

	public function deselectAll():void
	{
		combo.deselectAll();
	}
}
}
