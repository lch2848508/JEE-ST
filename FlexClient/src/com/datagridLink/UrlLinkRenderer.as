// ActionScript file
package com.datagridLink
{
	import flash.events.MouseEvent;
	
	import mx.controls.Label;
	import mx.controls.LinkButton;

	public class UrlLinkRenderer extends LinkButton
	{
		private var newUrlLink:Label;
		private var orderByFilter:String;
		[Bindable]
		private var _linkButtonLabel:String="";
		[Bindable]
		private var _rowObject:Object=new Object();
		
		public function UrlLinkRenderer()
		{
			super();
			//this.setStyle("textDecoration","underline");
			this.setStyle("textAlign","left");
			this.addEventListener(MouseEvent.CLICK,linkButtonClickHandler);
		}
		
		override public function set data(value:Object):void
		{
			super.data=value;
			if(value!=null)
			{
				var str:String="";
				for(var i:Object in value)
				{
					 str+=i+"||"+value[i]+"/n";
				}
				this._rowObject=value;
				this.label=value[_linkButtonLabel];
			}
		}
		public function set linkButtonLabel(value:String):void
		{
			_linkButtonLabel=value;		
		}
		public function get linkButtonLabel():String
		{
			return _linkButtonLabel;
		}
		private function linkButtonClickHandler(e:MouseEvent):void
		{
			var event:LinkButtonDynamicEvent=new LinkButtonDynamicEvent("DataGridLinkButtonClickEvent",_rowObject);			
			dispatchEvent(event);
		}

	}
}