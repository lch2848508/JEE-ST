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
package com.iwobanas.controls.dataGridClasses.filterEditors
{
	import com.iwobanas.controls.dataGridClasses.MDataGridColumn;

	import mx.containers.Box;

	/**
	 * The FilterEditorBase class provides default implementation
	 * of IColumnFilterEditor interface and defines base class
	 * for some column filter editors.
	 *
	 * <p>If you want to create column filter editor using box layout extend this class.
	 * If you want to use different layout you will have to use different base class e.g. Form.</p>
	 */
	public class FilterEditorBase extends Box implements IColumnFilterEditor
	{
		/**
		 * Construcotr.
		 */
		public function FilterEditorBase()
		{
			super();
			this.setStyle("backgroundColor", "#F5F5F5");
			this.setStyle("borderStyle", "#solid");
		}

		/**
		 * Column related with this filter editor.
		 */
		[Bindable]
		public var column:MDataGridColumn;

		/**
		 * Start editing filter for the given column.
		 *
		 * <p>Subclases usually override this function to update columns <code>filter</code> property.
		 * When overriding this function it is important to call <code>super.startEdit(column)</code>.</p>
		 */
		public function startEdit(column:MDataGridColumn):void
		{
			this.column=column;
			column.filterEditorInstance=this;
		}


		/**
		 * Stop editing filter for the given column.
		 */
		public function endEdit():void
		{
			column.filterEditorInstance=null;
		}

	}
}
