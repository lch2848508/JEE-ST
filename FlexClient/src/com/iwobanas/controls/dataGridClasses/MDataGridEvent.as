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
package com.iwobanas.controls.dataGridClasses
{
	import flash.events.Event;

	/**
	 * The MDataGridEvent class represents associated with MDataGrid features.
	 *
	 * @see com.iwobanas.controls.MDataGridEvent
	 * @see com.iwobanas.controls.dataGridClasses.filters.ColumnFilterBase
	 */
	public class MDataGridEvent extends Event
	{
		/**
		 * The MDataGridEvent.FILTER_CHANGE constant defines the value of the
	   * <code>type</code> property of the event object for a
			 * <code>filterChange</code> event, which indicates that
	   * the MDataGridColumn filter property was modified.
			   */
		public static const FILTER_CHANGE:String="filterChange";

		/**
		 * The MDataGridEvent.FILTER_VALUE_CHANGE constant defines the value of the
	   * <code>type</code> property of the event object for a
			 * <code>filterValueChange</code> event, which indicates that
	   * the ColumnFilterBase subclass filter value was modified.
			   */
		public static const FILTER_VALUE_CHANGE:String="filterValueChange";

		/**
		 * The MDataGridEvent.ORIGINAL_COLLECTION_CHANGE constant defines the value of the
	   * <code>type</code> property of the event object for a
			 * <code>originalCollectionChange</code> event, which indicates that
	   * the MDataGrid <code>originalCollectionChange</code> property was modified.
			   */
		public static const ORIGINAL_COLLECTION_CHANGE:String="originalCollectionChange"

		/**
		 * Constructor.
		 */
		public function MDataGridEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}

	}
}
