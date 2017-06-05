/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is: www.iwobanas.com code samples.

The Initial Developer of the Original Code is Iwo Banas.
Portions created by the Initial Developer are Copyright (C) 2009
the Initial Developer. All Rights Reserved.

Contributor(s):
*/
package com.iwobanas.controls.dataGridClasses.filters
{
	import com.iwobanas.controls.dataGridClasses.MDataGridColumn;
	import com.iwobanas.controls.dataGridClasses.MDataGridEvent;

	import flash.events.Event;

	import mx.collections.ArrayCollection;
	import mx.collections.Sort;

	/**
	 * The MultipleChoiceFilter class defines MDataGrid column filter
	 * exposing the list of different values appearing in MDataGrid
	 * column and allowing user to select which values should be displayed.
	 *
	 * This filter should be applied to columns containing repeating values.
	 */
	public class MultipleChoiceFilter extends ColumnFilterBase
	{
		/**
		 * Constructor.
		 */
		public function MultipleChoiceFilter(column:MDataGridColumn)
		{
			super(column);
			dataGrid.addEventListener(MDataGridEvent.ORIGINAL_COLLECTION_CHANGE, originalCollectionChandeHandler, false, 0, true);
			updateLabels();
		}

		/**
		 * List of all different labels appearing in column related to this filter.
		 */
		[Bindable]
		public var labels:ArrayCollection;

		/**
		 * List of selected labels.
		 * Only items with labels from this list will be included in MDataGrid data provider.
		 */
		[Bindable]
		public var selectedLabels:ArrayCollection=new ArrayCollection();

		/**
		 * Select given label by adding it to <code>selectedLabels</code> list.
		 */
		public function selectLabel(label:String):void
		{
			if (!selectedLabels.contains(label))
			{
				selectedLabels.addItem(label);
			}
			commitFilterChange();
		}

		/**
		 * Deselect given label by adding it to <code>selectedLabels</code> list.
		 */
		public function deselectLabel(label:String):void
		{
			if (selectedLabels.contains(label))
			{
				selectedLabels.removeItemAt(selectedLabels.getItemIndex(label));
			}
			commitFilterChange();
		}

		/**
		 * Select all labels.
		 */
		public function selectAll():void
		{
			for each (var label:String in labels)
			{
				if (!selectedLabels.contains(label))
				{
					selectedLabels.addItem(label);
				}
			}
			commitFilterChange();
		}

		/**
		 * Deselect all labels.
		 */
		public function deselectAll():void
		{
			selectedLabels.removeAll();
			commitFilterChange();
		}

		/**
		 * Update <code>isActive</code> and then inform MDataGrid about the change to this filter.
		 */
		override protected function commitFilterChange():void
		{
			var active:Boolean=false;
			for each (var label:String in labels)
			{
				if (!selectedLabels.contains(label))
				{
					active=true;
					break;
				}
			}
			_isActive=active;
			super.commitFilterChange();
		}

		/**
		 * Update labels list by iterating through MDataGrid original collection.
		 */
		protected function updateLabels():void
		{
			//TODO: save filter selection when data are updated
			var nl:ArrayCollection=new ArrayCollection();
			for each (var item:Object in dataGrid.originalCollection)
			{
				var label:String=column.itemToLabel(item);
				if (label && !nl.contains(label))
				{
					nl.addItem(label);
				}
			}
			nl.sort=new Sort();
			nl.refresh();
			labels=nl;
			deselectAll();
			selectAll();
		}

		/**
		 * MDataGrid original collection change event handler.
		 */
		protected function originalCollectionChandeHandler(event:Event):void
		{
			updateLabels();
		}

		/**
		 * Flag indicating wether this filter is active
		 * i.e may eliminate some items from MDataGrid data provider.
		 */
		override public function get isActive():Boolean
		{
			return _isActive;
		}
		/**
		 * @private
		 * Storage variable for <code>isActive</code> flag.
		 */
		protected var _isActive:Boolean=false;

		/**
		 * Test if given MDataGrid item should remain in MDataGrid data provider.
		 */
		override public function filterFunction(obj:Object):Boolean
		{
			return selectedLabels.contains(column.itemToLabel(obj));
		}
	}
}
