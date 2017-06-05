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

	/**
	 * The DateRangeFilter class defines MDataGrid column filter
	 * which filters rows containing dates based on specified minimum
	 * and maximum values.
	 *
	 * <p>This filter also provides information about range of dates
	 * found in MDataGrid data provider.</p>
	 */
	public class DateRangeFilter extends ColumnFilterBase
	{
		/**
		 * Constructor.
		 */
		public function DateRangeFilter(column:MDataGridColumn)
		{
			super(column);
			dataGrid.addEventListener(MDataGridEvent.ORIGINAL_COLLECTION_CHANGE, originalCollectionChandeHandler, false, 0, true);
			updateOriginalDateRange();
		}

		/**
		 * Minimum date found in MDataGrid column related to this filter.
		 */
		[Bindable]
		public var dataMinimum:Date;

		/**
		 * Maximum date found in MDataGrid column related to this filter
		 */
		[Bindable]
		public var dataMaximum:Date;

		/**
		 * Minimum date allowed in MDataGrid column related to this filter.
		 * Items with date less than <code>minimum</code> will be eliminated.
		 */
		[Bindable]
		public function get minimum():Date
		{
			return _minimum;
		}

		/**
		 * @private
		 */
		public function set minimum(value:Date):void
		{
			_minimum=value;
			commitFilterChange();
		}
		/**
		 * @private
		 * Storage variable for minimum property.
		 */
		protected var _minimum:Date;

		/**
		 * Maximum date allowed in MDataGrid column related to this filter.
		 * Items with date greater than <code>minimum</code> will be eliminated.
		 */
		[Bindable]
		public function get maximum():Date
		{
			return _maximum;
		}

		/**
		 * @private
		 */
		public function set maximum(value:Date):void
		{
			_maximum=value;
			commitFilterChange();
		}
		/**
		 * @private
		 * Storage variable for maximum.
		 */
		protected var _maximum:Date;

		/**
		 * Update <code>dataMinimum</code> and <code>dataMaximum</code> values.
		 */
		protected function updateOriginalDateRange():void
		{
			var min:Date=new Date(10000, 0, 1); //is there infinity date?
			var max:Date=new Date(-10000, 0, 1);
			for each (var item:Object in dataGrid.originalCollection)
			{
				var value:Date=itemToDate(item);
				if (value)
				{
					if (value < min)
					{
						min=value;
					}
					if (value > max)
					{
						max=value;
					}
				}
			}
			if (minimum == null)
			{
				minimum=min;
			}
			if (maximum == null)
			{
				maximum=max;
			}
			dataMinimum=min;
			dataMaximum=max;
		}

		/**
		 * @private
		 * Collection change event handler attached to original collection of MDataGrid.
		 */
		protected function originalCollectionChandeHandler(event:Event):void
		{
			updateOriginalDateRange();
		}

		/**
		 * Return date for the given item.
		 * @param item MDataGrid item.
		 * @return numeric value for the given item.
		 */
		protected function itemToDate(item:Object):Date
		{
			var value:Date
			try
			{
				if (column.dataField && item[column.dataField] is Date)
				{
					value=item[column.dataField];
				}
				else
				{
					value=new Date(column.itemToLabel(item));
				}
			}
			catch (e:Error)
			{
			}
			return value;
		}

		/**
		 * Flag indicating wether this filter is active
		 * i.e may eliminate some items from MDataGrid data provider.
		 */
		override public function get isActive():Boolean
		{
			return (minimum != dataMinimum || maximum != dataMaximum);
		}

		/**
		 * Test if given MDataGrid item should remain in MDataGrid data provider.
		 */
		override public function filterFunction(obj:Object):Boolean
		{
			var value:Date=itemToDate(obj);

			if (value)
			{
				if (minimum && value < minimum)
				{
					return false;
				}
				if (maximum && value > maximum)
				{
					return false;
				}
			}
			return true;
		}
	}
}
