/**********************************************************************************
 * 创建布局辅助类
 * *******************************************************************************/
package com.estudio.flex.module
{
	import com.estudio.flex.component.BorderWrapContain;
	import com.estudio.flex.module.component.ToolbarGroup;
	import com.estudio.flex.utils.UIUtils;
	
	import flash.display.DisplayObject;
	import flash.net.SharedObject;
	
	import mx.containers.DividedBox;
	import mx.containers.HDividedBox;
	import mx.containers.VDividedBox;
	import mx.core.UIComponent;
	import mx.events.DividerEvent;
	
	import spark.components.Group;
	import spark.components.VGroup;

	public class LayoutClassEx
	{
		private var id2Instance:Object={}; //名称对组件字典

		public static const BOX_A:String="a";
		public static const BOX_B:String="b";
		public static const BOX_C:String="c";
		public static const TOOLBAR_A:String="ta";
		public static const TOOLBAR_B:String="tb";
		public static const TOOLBAR_C:String="tc";

		private static const BOX_BG_COLOR:String="#FFFFFF";
		private static const BOX_BORDER_COLOR:String="#7B889C";
		private static const BOX_TOOLBAR_BG_COLOR:uint=0xDBEBFF;

		private static const TOOLBAR_HEIGHT:int=30;

		private var _dividedBoxs:Array=[];
		private var _strogeKey:String=null;
		private var _layoutType:String=null;
		private var _version:String=null;
		private var _toolbarVisibles:Array=[];
		private var _splitSizes:Array=[];

		public function get version():String
		{
			
			return _version;
		}

		public function set version(value:String):void
		{
			_version=value;
		}


		public function get layoutType():String
		{
			return _layoutType;
		}

		public function set layoutType(value:String):void
		{
			_layoutType=value;
		}


		////////////////////////////////////////////////////////////////////////////////////////////////////
		//加载布局
		public function loadLayoutSizes():void
		{
			var layObject:Object=SharedObject.getLocal(this._strogeKey).data[this._strogeKey];
			var i:int=0;
			var j:int=0;
			if (layObject && _layoutType == layObject["layoutType"] && _version == layObject["version"])
			{
				layObject=layObject["layObject"];

				for (i=0; i < _dividedBoxs.length; i++)
				{
					var divBox:DividedBox=_dividedBoxs[i] as DividedBox;
					var sizes:Array=layObject[i];
					if (divBox is HDividedBox)
					{
						for (j=0; j < divBox.numChildren; j++)
							(divBox.getChildAt(j) as UIComponent).percentWidth=sizes[j]

					}
					else
					{
						for (j=0; j < divBox.numChildren; j++)
							(divBox.getChildAt(j) as UIComponent).percentHeight=sizes[j]
					}
				}
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		//保存布局
		public function saveLayoutSizes():void
		{
			var layObject:Object=[];
			var i:int=0;
			var j:int=0;
			var comp:UIComponent=null;
			for (i=0; i < _dividedBoxs.length; i++)
			{
				var divBox:DividedBox=_dividedBoxs[i] as DividedBox;
				var sizes:Array=[];
				if (divBox is HDividedBox)
				{
					for (j=0; j < divBox.numChildren; j++)
					{
						comp=divBox.getChildAt(j) as UIComponent;
						var percentWidth:int=Math.round(comp.width * 100 / (divBox.width - divBox.numDividers * 4));
						sizes.push(percentWidth);
					}
				}
				else
				{
					for (j=0; j < divBox.numChildren; j++)
					{
						comp=divBox.getChildAt(j) as UIComponent;
						var percentHeight:int=Math.round(comp.height * 100 / (divBox.height - divBox.numDividers * 4));
						sizes.push(percentHeight);
					}
				}
				layObject.push(sizes);
			}

			var so:Object=SharedObject.getLocal(this._strogeKey);
			so.data[this._strogeKey]={layObject: layObject, layoutType: _layoutType, version: _version};
			so.flush();
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		//拖动布局结束
		private function event_DividedBoxOnRelease(event:DividerEvent):void
		{
			var u1:DisplayObject=event.currentTarget.getChildAt(event.dividerIndex);
			var u2:DisplayObject=event.currentTarget.getChildAt(event.dividerIndex + 1);
			if (event.currentTarget is HDividedBox)
			{
				u1.width+=event.delta;
				u2.width-=event.delta;
			}
			else
			{
				u1.height+=event.delta;
				u2.height-=event.delta;
			}
			saveLayoutSizes();
		}


		public function get strogeKey():String
		{
			return _strogeKey;
		}

		public function set strogeKey(value:String):void
		{
			_strogeKey=value;
		}


		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		public function LayoutClassEx()
		{

		}

		//获取边框对象
		public function getBox(key:String):Group
		{
			return id2Instance[key] as Group;
		}


		//布局3L
		private function createLayout_3L():UIComponent
		{
			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 4);
			hSplit.setStyle("verticalGap", 4);
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(hSplit);
			_dividedBoxs.push(hSplit);

			//a
			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			UIUtils.fullAlign(box_a);
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, true, false);

			if (_splitSizes[0] != 0)
				wrap_a.width=_splitSizes[0];

			hSplit.addElement(wrap_a);
			id2Instance[BOX_A]=box_a;


			var vSplit:VDividedBox=new VDividedBox();
			vSplit.setStyle("horizontalGap", 4);
			vSplit.setStyle("verticalGap", 4);
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);
			UIUtils.fullAlign(vSplit);

			if (_splitSizes[0] == 0 && _splitSizes[1] != 0)
				vSplit.width=_splitSizes[1];

			hSplit.addElement(vSplit);
			_dividedBoxs.push(vSplit);

			//b
			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			UIUtils.fullAlign(box_b);
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, true, false, false, true);
			if (_splitSizes[2] != 0)
				wrap_b.height=_splitSizes[2];
			vSplit.addElement(wrap_b);
			id2Instance[BOX_B]=box_b;

			//c
			var box_c:VGroup=new VGroup();
			box_c.gap=0;
			UIUtils.fullAlign(box_c);
			var wrap_c:UIComponent=BorderWrapContain.wrap(box_c, true, true, false, false);
			if (_splitSizes[2] == 0 && _splitSizes[3] != 0)
				wrap_c.height=_splitSizes[3];
			vSplit.addElement(wrap_c);
			id2Instance[BOX_C]=box_c;

			if (_toolbarVisibles[0])
				id2Instance[TOOLBAR_A]=createToolbar(box_a);

			if (_toolbarVisibles[1])
				id2Instance[TOOLBAR_B]=createToolbar(box_b);

			if (_toolbarVisibles[2])
				id2Instance[TOOLBAR_C]=createToolbar(box_c);

			return hSplit;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局3U
		private function createLayout_3U():UIComponent
		{
			//工作区域
			var vSplit:VDividedBox=new VDividedBox();
			UIUtils.fullAlign(vSplit);
			vSplit.setStyle("horizontalGap", 4);
			vSplit.setStyle("verticalGap", 4);
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			var hSplit:HDividedBox=new HDividedBox();
			UIUtils.fullAlign(hSplit);
			hSplit.setStyle("horizontalGap", 4);
			hSplit.setStyle("verticalGap", 4);
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			if (_splitSizes[2] != 0)
				vSplit.height=_splitSizes[2];
			vSplit.addElement(hSplit);

			_dividedBoxs.push(hSplit);
			//a
			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			UIUtils.fullAlign(box_a);
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, true, true);
			if (_splitSizes[0] != 0)
				wrap_a.width=_splitSizes[0];
			hSplit.addElement(wrap_a);
			id2Instance[BOX_A]=box_a;

			//b
			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			UIUtils.fullAlign(box_b);
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, true, false, false, true);
			if (_splitSizes[0] == 0 && _splitSizes[1] != 0)
				wrap_b.width=_splitSizes[1];
			hSplit.addElement(wrap_b);
			id2Instance[BOX_B]=box_b;

			//c
			var box_c:VGroup=new VGroup();
			box_c.gap=0;
			UIUtils.fullAlign(box_c);
			var wrap_c:UIComponent=BorderWrapContain.wrap(box_c, false, true, false, false);
			if (_splitSizes[2] == 0 && _splitSizes[3] != 0)
				wrap_c.height=_splitSizes[3];

			vSplit.addElement(wrap_c);
			id2Instance[BOX_C]=box_c;

			if (_toolbarVisibles[0])
				id2Instance[TOOLBAR_A]=createToolbar(box_a);

			if (_toolbarVisibles[1])
				id2Instance[TOOLBAR_B]=createToolbar(box_b);

			if (_toolbarVisibles[2])
				id2Instance[TOOLBAR_C]=createToolbar(box_c);

			return vSplit;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		private function createLayout_3T():UIComponent
		{
			var vSplit:VDividedBox=new VDividedBox();
			UIUtils.fullAlign(vSplit);
			vSplit.setStyle("horizontalGap", 4);
			vSplit.setStyle("verticalGap", 4);
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			_dividedBoxs.push(vSplit);
			//a
			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			UIUtils.fullAlign(box_a);
			UIUtils.boxBorder(box_a, BOX_BORDER_COLOR, false, false, false, true);
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, false, true);
			if (_splitSizes[2] != 0)
				wrap_a.height=_splitSizes[2];
			vSplit.addElement(wrap_a);
			id2Instance[BOX_A]=box_a;

			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 4);
			hSplit.setStyle("verticalGap", 4);
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(hSplit);
			if (_splitSizes[2] == 0 && _splitSizes[3] != 0)
				hSplit.height=_splitSizes[3];

			vSplit.addElement(hSplit);

			_dividedBoxs.push(hSplit);
			//b
			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			UIUtils.fullAlign(box_b);
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, false, true, true, false);
			if (_splitSizes[0] != 0)
				wrap_b.width=_splitSizes[0];
			hSplit.addElement(wrap_b);
			id2Instance[BOX_B]=box_b;

			//c
			var box_c:VGroup=new VGroup();
			box_c.gap=0;
			UIUtils.fullAlign(box_c);
			UIUtils.boxBorder(box_c, BOX_BORDER_COLOR, true, true, false, false);
			var wrap_c:UIComponent=BorderWrapContain.wrap(box_c, true, true, false, false);
			if (_splitSizes[1] != 0 && _splitSizes[0] == 0)
				wrap_c.width=_splitSizes[1];
			hSplit.addElement(wrap_c);
			id2Instance[BOX_C]=box_c;

			if (_toolbarVisibles[0])
				id2Instance[TOOLBAR_A]=createToolbar(box_a);

			if (_toolbarVisibles[1])
				id2Instance[TOOLBAR_B]=createToolbar(box_b);

			if (_toolbarVisibles[2])
				id2Instance[TOOLBAR_C]=createToolbar(box_c);

			return vSplit;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		private function createLayout_3J():UIComponent
		{
			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 4)
			hSplit.setStyle("verticalGap", 4)
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(hSplit);
			_dividedBoxs.push(hSplit);

			var vSplit:VDividedBox=new VDividedBox();
			vSplit.setStyle("horizontalGap", 4)
			vSplit.setStyle("verticalGap", 4)
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(vSplit);
			if (_splitSizes[0] != 0)
				vSplit.width=_splitSizes[0];
			hSplit.addElement(vSplit);
			_dividedBoxs.push(vSplit);

			//a
			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			UIUtils.fullAlign(box_a);
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, true, true);
			if (_splitSizes[2] != 0)
				wrap_a.height=_splitSizes[2];
			vSplit.addElement(wrap_a);
			id2Instance[BOX_A]=box_a;

			//b
			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			UIUtils.fullAlign(box_b);
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, false, true, true, false);
			if (_splitSizes[2] == 0 && _splitSizes[3] != 0)
				wrap_b.height=_splitSizes[3];
			vSplit.addElement(wrap_b);
			id2Instance[BOX_B]=box_b;

			//c
			var box_c:VGroup=new VGroup();
			box_c.gap=0;
			UIUtils.fullAlign(box_c);
			var wrap_c:UIComponent=BorderWrapContain.wrap(box_c, true, false, false, false);
			if (_splitSizes[0] == 0 && _splitSizes[1] != 0)
				wrap_c.width=_splitSizes[1];
			hSplit.addElement(wrap_c);
			id2Instance[BOX_C]=box_c;

			if (_toolbarVisibles[0])
				id2Instance[TOOLBAR_A]=createToolbar(box_a);

			if (_toolbarVisibles[1])
				id2Instance[TOOLBAR_B]=createToolbar(box_b);

			if (_toolbarVisibles[2])
				id2Instance[TOOLBAR_C]=createToolbar(box_c);

			return hSplit;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		private function createLayout_2E():UIComponent
		{
			//工作区域
			var vSplit:VDividedBox=new VDividedBox();
			UIUtils.fullAlign(vSplit);
			vSplit.setStyle("horizontalGap", 4)
			vSplit.setStyle("verticalGap", 4)
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);
			_dividedBoxs.push(vSplit);

			//A
			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			UIUtils.fullAlign(box_a);
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, false, true);
			UIUtils.fullAlign(wrap_a);
			if (_splitSizes[2] != 0)
				wrap_a.height=_splitSizes[2];

			vSplit.addElement(wrap_a);
			id2Instance[BOX_A]=box_a;

			//B
			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			UIUtils.fullAlign(box_b);
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, false, true, false, false);
			UIUtils.fullAlign(wrap_b);

			if (_splitSizes[2] == 0 && _splitSizes[3] != 0)
				wrap_b.height=_splitSizes[3];

			vSplit.addElement(wrap_b);
			id2Instance[BOX_B]=box_b;

			if (_toolbarVisibles[0])
				id2Instance[TOOLBAR_A]=createToolbar(box_a);

			if (_toolbarVisibles[1])
				id2Instance[TOOLBAR_B]=createToolbar(box_b);

			return vSplit;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		private function createLayout_2U():UIComponent
		{
			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 4)
			hSplit.setStyle("verticalGap", 4)
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);
			UIUtils.fullAlign(hSplit);
			_dividedBoxs.push(hSplit);

			//A
			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, true, false);
			UIUtils.fullAlign(wrap_a);

			if (_splitSizes[0] != 0)
				wrap_a.width=_splitSizes[0];

			hSplit.addElement(wrap_a);
			this.id2Instance[BOX_A]=box_a;

			//B
			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, true, false, false, false);
			UIUtils.fullAlign(wrap_b);

			if (_splitSizes[0] == 0 && _splitSizes[1] != 0)
				wrap_b.width=_splitSizes[1];

			hSplit.addElement(wrap_b);
			this.id2Instance[BOX_B]=box_b;

			if (_toolbarVisibles[0])
				id2Instance[TOOLBAR_A]=createToolbar(box_a);

			if (_toolbarVisibles[1])
				id2Instance[TOOLBAR_B]=createToolbar(box_b);

			return hSplit;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局1C
		private function createLayout_1C():UIComponent
		{
			var box:VGroup=new VGroup();
			box.gap=0;
			UIUtils.fullAlign(box);
			UIUtils.setBgColor(box, BOX_BG_COLOR);
			if (_toolbarVisibles[0])
				id2Instance[TOOLBAR_A]=createToolbar(box);
			this.id2Instance[BOX_A]=box;
			return box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		private function createToolbar(box:Group):UIComponent
		{
			var top_box:ToolbarGroup=new ToolbarGroup();
			var boxWrap:UIComponent=BorderWrapContain.wrap(top_box, false, false, false, true, BOX_TOOLBAR_BG_COLOR);
			UIUtils.topAlign(boxWrap, TOOLBAR_HEIGHT);
			box.addElement(boxWrap);
			return top_box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		public function createLayout(layerType:String, toolbarVisibles:Array, splitSizes:Array):UIComponent
		{
			this._toolbarVisibles=toolbarVisibles;
			this._splitSizes=splitSizes;
			return this["createLayout_" + layerType]();
		}
	}
}
