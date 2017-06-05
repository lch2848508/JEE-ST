/**********************************************************************************
 * 创建布局辅助类
 * *******************************************************************************/
package com.estudio.flex.module
{
	import com.estudio.flex.component.BorderWrapContain;
	import com.estudio.flex.utils.StringUtils;
	import com.estudio.flex.utils.UIUtils;

	import flash.display.DisplayObject;
	import flash.net.SharedObject;

	import mx.containers.DividedBox;
	import mx.containers.HDividedBox;
	import mx.containers.VDividedBox;
	import mx.core.UIComponent;
	import mx.events.DividerEvent;
	import mx.skins.Border;

	import spark.components.BorderContainer;
	import spark.components.Group;
	import spark.components.VGroup;

	public class LayoutClass
	{
		private const TOOLBAR_HEIGHT:int=30; //工具条及分页栏的高度

		private var id2Instance:Object={}; //名称对组件字典

		public static const TOOLBAR_TOP:String="ToolbarTop";
		public static const TOOLBAR_TREE:String="ToolbarTree";
		public static const TOOLBAR_GRID:String="ToolbarGrid";
		public static const TOOLBAR_DETAIL:String="ToolbarDetail";
		public static const TOOLBAR_PAGINATION:String="ToolbarPagination";
		public static const BOX_TREE:String="BoxTree";
		public static const BOX_GRID:String="BoxGrid";
		public static const BOX_DETAIL:String="BoxDetail";
		public static const BOX_A:String="a";
		public static const BOX_B:String="b";
		public static const BOX_C:String="c";

		private static const BOX_BG_COLOR:String="#FFFFFF";
		private static const BOX_BORDER_COLOR:String="#7B889C";
		private static const BOX_TOOLBAR_BG_COLOR:uint=0xDCEBFE;

		private var _dividedBoxs:Array=[];
		private var _strogeKey:String=null;
		private var _layoutType:String=null;
		private var _version:String=null;
		private var _filterPanelContain:Group=null;

		public var treeSize:int=250;
		public var detailSize:int=250;


		public function get version():String
		{
			return _version;
		}

		public function set version(value:String):void
		{
			_version=value;
		}

		public function getFilterPanelContain():Group
		{
			return _filterPanelContain;
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
		public function LayoutClass()
		{

		}

		//获取边框对象
		public function getBox(key:String):Group
		{
			return id2Instance[key] as Group;
		}


		//布局3L
		public function createLayout_3L(treeView:Boolean, toolbarTree:Boolean, gridView:Boolean, toolbarGrid:Boolean, gridPagination:Boolean, toolbarSplit:Boolean, treeCell:String, gridCell:String):UIComponent
		{
			var box:VGroup=new VGroup();
			UIUtils.fullAlign(box);
			UIUtils.setGap(box, 0, 0);
			UIUtils.setBgColor(box, BOX_BG_COLOR);
			createTopToolbar(box, toolbarTree, toolbarGrid, toolbarSplit);

			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 4);
			hSplit.setStyle("verticalGap", 4);
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(hSplit);
			box.addElement(hSplit);

			_dividedBoxs.push(hSplit);

			//a
			var box_a:VGroup=new VGroup();
			UIUtils.fullAlign(box_a);
			box_a.gap=0;
			hSplit.addElement(BorderWrapContain.wrap(box_a, false, false, true, false));


			var vSplit:VDividedBox=new VDividedBox();
			vSplit.setStyle("horizontalGap", 4);
			vSplit.setStyle("verticalGap", 4);
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(vSplit);
			hSplit.addElement(vSplit);

			_dividedBoxs.push(vSplit);

			//b
			var box_b:VGroup=new VGroup();
			UIUtils.fullAlign(box_b);
			box_b.gap=0;
			vSplit.addElement(BorderWrapContain.wrap(box_b, true, false, false, true));

			//c
			var box_c:VGroup=new VGroup();
			box_c.gap=0;
			UIUtils.fullAlign(box_c);
			vSplit.addElement(BorderWrapContain.wrap(box_c, true, true, false, false));

			createTreeAndGridLayout(box_a, box_b, box_c, treeCell, gridCell, toolbarTree, toolbarGrid, toolbarSplit, gridPagination, treeView, gridView);

			return box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局3U
		public function createLayout_3U(treeView:Boolean, toolbarTree:Boolean, gridView:Boolean, toolbarGrid:Boolean, gridPagination:Boolean, toolbarSplit:Boolean, treeCell:String, gridCell:String):UIComponent
		{
			var box:VGroup=new VGroup();
			UIUtils.fullAlign(box);
			box.gap=0;
			UIUtils.setGap(box, 0, 0);

			createTopToolbar(box, toolbarTree, toolbarGrid, toolbarSplit);

			//工作区域
			var vSplit:VDividedBox=new VDividedBox();
			UIUtils.fullAlign(vSplit);
			vSplit.setStyle("horizontalGap", 4);
			vSplit.setStyle("verticalGap", 4);
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			box.addElement(vSplit);

			var hSplit:HDividedBox=new HDividedBox();
			UIUtils.fullAlign(hSplit);
			hSplit.setStyle("horizontalGap", 4);
			hSplit.setStyle("verticalGap", 4);
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			vSplit.addElement(hSplit);

			_dividedBoxs.push(hSplit);
			//a
			var box_a:VGroup=new VGroup();
			UIUtils.fullAlign(box_a);
			box_a.gap=0;
			hSplit.addElement(BorderWrapContain.wrap(box_a, false, false, true, true));

			//b
			var box_b:VGroup=new VGroup();
			UIUtils.fullAlign(box_b);
			box_b.gap=0;
			hSplit.addElement(BorderWrapContain.wrap(box_b, true, false, false, true));

			//c
			var box_c:VGroup=new VGroup();
			UIUtils.fullAlign(box_c);
			box_c.gap=0;
			vSplit.addElement(BorderWrapContain.wrap(box_c, false, true, false, false));

			createTreeAndGridLayout(box_a, box_b, box_c, treeCell, gridCell, toolbarTree, toolbarGrid, toolbarSplit, gridPagination, treeView, gridView);
			return box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		public function createLayout_3T(treeView:Boolean, toolbarTree:Boolean, gridView:Boolean, toolbarGrid:Boolean, gridPagination:Boolean, toolbarSplit:Boolean, treeCell:String, gridCell:String):UIComponent
		{
			var box:VGroup=new VGroup();
			UIUtils.fullAlign(box);
			box.gap=0;
			createTopToolbar(box, toolbarTree, toolbarGrid, toolbarSplit);

			var vSplit:VDividedBox=new VDividedBox();
			UIUtils.fullAlign(vSplit);
			vSplit.setStyle("horizontalGap", 4);
			vSplit.setStyle("verticalGap", 4);
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			box.addElement(vSplit);

			_dividedBoxs.push(vSplit);
			//a
			var box_a:VGroup=new VGroup();
			UIUtils.fullAlign(box_a);
			box_a.gap=0;
			UIUtils.boxBorder(box_a, BOX_BORDER_COLOR, false, false, false, true);
			vSplit.addElement(BorderWrapContain.wrap(box_a, false, false, false, true));

			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 4);
			hSplit.setStyle("verticalGap", 4);
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(hSplit);
			vSplit.addElement(hSplit);

			_dividedBoxs.push(hSplit);
			//b
			var box_b:VGroup=new VGroup();
			UIUtils.fullAlign(box_b);
			box_b.gap=0;
			hSplit.addElement(BorderWrapContain.wrap(box_b, false, true, true, false));

			//c
			var box_c:VGroup=new VGroup();
			UIUtils.fullAlign(box_c);
			box_c.gap=0;
			UIUtils.boxBorder(box_c, BOX_BORDER_COLOR, true, true, false, false);
			hSplit.addElement(BorderWrapContain.wrap(box_c, true, true, false, false));

			createTreeAndGridLayout(box_a, box_b, box_c, treeCell, gridCell, toolbarTree, toolbarGrid, toolbarSplit, gridPagination, treeView, gridView);
			return box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		public function createLayout_3J(treeView:Boolean, toolbarTree:Boolean, gridView:Boolean, toolbarGrid:Boolean, gridPagination:Boolean, toolbarSplit:Boolean, treeCell:String, gridCell:String):UIComponent
		{
			var box:VGroup=new VGroup();
			UIUtils.fullAlign(box);
			box.gap=0;
			UIUtils.setBgColor(box, BOX_BG_COLOR);

			createTopToolbar(box, toolbarTree, toolbarGrid, toolbarSplit);

			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 4)
			hSplit.setStyle("verticalGap", 4)
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(hSplit);
			box.addElement(hSplit);
			_dividedBoxs.push(hSplit);

			var vSplit:VDividedBox=new VDividedBox();
			vSplit.setStyle("horizontalGap", 4)
			vSplit.setStyle("verticalGap", 4)
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(vSplit);
			vSplit.width=detailSize;
			hSplit.addElement(vSplit);
			_dividedBoxs.push(vSplit);
			//a
			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			UIUtils.fullAlign(box_a);
			vSplit.addElement(BorderWrapContain.wrap(box_a, false, false, true, true));

			//b
			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			UIUtils.fullAlign(box_b);
			vSplit.addElement(BorderWrapContain.wrap(box_b, false, true, true, false));

			//c
			var box_c:VGroup=new VGroup();
			box_c.gap=0;
			UIUtils.fullAlign(box_c);
			hSplit.addElement(BorderWrapContain.wrap(box_c, true, false, false, false));

			createTreeAndGridLayout(box_a, box_b, box_c, treeCell, gridCell, toolbarTree, toolbarGrid, toolbarSplit, gridPagination, treeView, gridView);
			return box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		public function createLayout_2E(treeView:Boolean, toolbarTree:Boolean, gridView:Boolean, toolbarGrid:Boolean, gridPagination:Boolean, toolbarSplit:Boolean, treeCell:String, gridCell:String):UIComponent
		{
			var box:VGroup=new VGroup();
			box.gap=0;
			UIUtils.fullAlign(box);

			createTopToolbar(box, toolbarTree, toolbarGrid, toolbarSplit);

			//工作区域
			var vSplit:VDividedBox=new VDividedBox();
			UIUtils.fullAlign(vSplit);
			vSplit.setStyle("horizontalGap", 5)
			vSplit.setStyle("verticalGap", 5)
			vSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			box.addElement(vSplit);
			_dividedBoxs.push(vSplit);

			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			UIUtils.fullAlign(box_a);
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, false, true);
			UIUtils.fullAlign(wrap_a);
			vSplit.addElement(wrap_a);

			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			UIUtils.fullAlign(box_b);
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, false, true, false, false);
			UIUtils.fullAlign(wrap_b);
			vSplit.addElement(wrap_b);


			createTreeAndGridLayout(box_a, box_b, null, treeCell, gridCell, toolbarTree, toolbarGrid, toolbarSplit, gridPagination, treeView, gridView);

			return box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局2E
		public function createLayout_2U(treeView:Boolean, toolbarTree:Boolean, gridView:Boolean, toolbarGrid:Boolean, gridPagination:Boolean, toolbarSplit:Boolean, treeCell:String, gridCell:String):UIComponent
		{
			var box:VGroup=new VGroup();
			box.gap=0;
			var wrapBox:UIComponent=BorderWrapContain.wrap(box, false, false, false, false);

			createTopToolbar(box, toolbarTree, toolbarGrid, toolbarSplit);

			//工作区域
			var hSplit:HDividedBox=new HDividedBox();
			hSplit.setStyle("horizontalGap", 5)
			hSplit.setStyle("verticalGap", 5)
			hSplit.addEventListener(DividerEvent.DIVIDER_RELEASE, event_DividedBoxOnRelease);

			UIUtils.fullAlign(hSplit);
			box.addElement(hSplit);
			_dividedBoxs.push(hSplit);

			var box_a:VGroup=new VGroup();
			box_a.gap=0;
			var wrap_a:UIComponent=BorderWrapContain.wrap(box_a, false, false, true, false);
			UIUtils.fullAlign(wrap_a);
			hSplit.addElement(wrap_a);

			var box_b:VGroup=new VGroup();
			box_b.gap=0;
			var wrap_b:UIComponent=BorderWrapContain.wrap(box_b, true, false, false, false);
			UIUtils.fullAlign(wrap_b);
			hSplit.addElement(wrap_b);


			createTreeAndGridLayout(box_a, box_b, null, treeCell, gridCell, toolbarTree, toolbarGrid, toolbarSplit, gridPagination, treeView, gridView);

			return box;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//布局1C
		public function createLayout_1C(treeView:Boolean, toolbarTree:Boolean, gridView:Boolean, toolbarGrid:Boolean, gridPagination:Boolean):UIComponent
		{
			var box:VGroup=new VGroup();
			UIUtils.fullAlign(box);
			box.gap=0;
			UIUtils.setBgColor(box, BOX_BG_COLOR);

			//工具按钮区域
			if ((treeView && toolbarTree) || (gridView && toolbarGrid))
			{
				var topBox:Group=new Group();
				UIUtils.fullAlign(topBox);
				var topBoxWrap:UIComponent=BorderWrapContain.wrap(topBox, false, false, false, true, BOX_TOOLBAR_BG_COLOR);
				id2Instance[TOOLBAR_TOP]=topBox;
				id2Instance[treeView ? TOOLBAR_TREE : TOOLBAR_GRID]=topBox;
				UIUtils.topAlign(topBoxWrap, TOOLBAR_HEIGHT);
				box.addElement(topBoxWrap);

			}

			//工作区域
			var centerBox:VGroup=new VGroup();
			centerBox.gap=0;
			UIUtils.fullAlign(centerBox);
			box.addElement(centerBox);
			id2Instance[treeView ? BOX_TREE : BOX_GRID]=centerBox;

			//分页面板区域
			if (gridPagination && gridView)
			{
				var bottomBox:Group=new Group();
				id2Instance[TOOLBAR_PAGINATION]=bottomBox;
				UIUtils.fullAlign(bottomBox);
				var bottomBoxWrap:UIComponent=BorderWrapContain.wrap(bottomBox, false, true, false, false, BOX_TOOLBAR_BG_COLOR);
				UIUtils.bottomAlign(bottomBoxWrap, TOOLBAR_HEIGHT);
				box.addElement(bottomBoxWrap);
			}

			this.id2Instance[BOX_A]=box;
			this.id2Instance[BOX_B]=null;
			this.id2Instance[BOX_C]=null;
			return box;
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////
		//创建详细布局
		public function createDetailLayout(detailCell:String):Boolean
		{
			var result:Boolean=false;
			if (!StringUtils.isEmpty(detailCell))
			{
				var group:Group=getBox(detailCell);
				if (group)
				{
					var topBox:Group=new Group();
					var topBoxWrap:UIComponent=BorderWrapContain.wrap(topBox, false, false, false, true, BOX_TOOLBAR_BG_COLOR);
					id2Instance[TOOLBAR_DETAIL]=topBox;
					UIUtils.topAlign(topBoxWrap, TOOLBAR_HEIGHT);
					group.addElement(topBoxWrap);

					var centerBox:Group=new Group();
					id2Instance[BOX_DETAIL]=centerBox;
					UIUtils.fullAlign(centerBox);
					group.addElement(centerBox);

					result=true;
				}
			}
			return result;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//创建顶部工具栏
		private function createTopToolbar(box:Group, toolbarTree:Boolean, toolbarGrid:Boolean, toolbarSplit:Boolean):void
		{
			//工具条区域
			if ((toolbarTree || toolbarGrid) && !toolbarSplit)
			{
				var top_box:Group=new Group();
				id2Instance[TOOLBAR_TOP]=top_box;
				var boxWrap:UIComponent=BorderWrapContain.wrap(top_box, false, false, false, true, BOX_TOOLBAR_BG_COLOR);
				UIUtils.topAlign(boxWrap, TOOLBAR_HEIGHT);
				box.addElement(boxWrap);
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//创建树布局及列表布局
		private function createTreeAndGridLayout(box_a:Group, box_b:Group, box_c:Group, treeCell:String, gridCell:String, toolbarTree:Boolean, toolbarGrid:Boolean, toolbarSplit:Boolean, gridPagination:Boolean, treeView:Boolean, gridView:Boolean):void
		{
			var cellNames:Object={a: box_a, b: box_b, c: box_c};
			if (box_a)
				cellNames.a=box_a;
			if (box_b)
				cellNames.b=box_b;
			if (box_c)
				cellNames.c=box_c;

			if (treeView)
			{
				if (treeCell == "a")
					createTreeLayout(box_a, toolbarTree, toolbarSplit);
				else if (treeCell == "b" && box_b != null)
					createTreeLayout(box_b, toolbarTree, toolbarSplit);
				else if (treeCell == "c" && box_c != null)
					createTreeLayout(box_c, toolbarTree, toolbarSplit);
				delete cellNames[treeCell];
			}

			if (gridView)
			{
				if (gridCell == "a")
					createGridLayout(box_a, toolbarGrid, gridPagination, toolbarSplit);
				else if (gridCell == "b" && box_b != null)
					createGridLayout(box_b, toolbarGrid, gridPagination, toolbarSplit);
				else if (gridCell == "c" && box_c != null)
					createGridLayout(box_c, toolbarGrid, gridPagination, toolbarSplit);
				delete cellNames[gridCell];
			}

			for (var k:String in cellNames)
			{
				this.id2Instance[BOX_DETAIL]=cellNames[k];
				break;
			}
			this.id2Instance[BOX_A]=box_a;
			this.id2Instance[BOX_B]=box_b;
			this.id2Instance[BOX_C]=box_c;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//创建Tree布局
		private function createTreeLayout(box:Group, toolbarTree:Boolean, toolbarSplit:Boolean):void
		{
			//工具按钮区域
			if (toolbarTree && toolbarSplit)
			{
				var topBox:Group=new Group();
				var topBoxWrap:UIComponent=BorderWrapContain.wrap(topBox, false, false, false, true, BOX_TOOLBAR_BG_COLOR);
				id2Instance[TOOLBAR_TREE]=topBox;
				UIUtils.topAlign(topBoxWrap, TOOLBAR_HEIGHT);
				box.addElement(topBoxWrap);
			}

			//工作区域
			var centerBox:Group=new Group();
			id2Instance[BOX_TREE]=centerBox;
			UIUtils.fullAlign(centerBox);
			box.addElement(centerBox);

			var p:UIComponent=box.parent.parent.parent as UIComponent;
			if (p.parent is HDividedBox)
				p.width=treeSize;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//创建Grid布局
		private function createGridLayout(box:Group, toolbarGrid:Boolean, gridPagination:Boolean, toolbarSplit:Boolean):void
		{
			//工具按钮区域
			if (toolbarGrid && toolbarSplit)
			{
				var topBox:Group=new Group();
				var topBoxWrap:UIComponent=BorderWrapContain.wrap(topBox, false, false, false, true, BOX_TOOLBAR_BG_COLOR);
				id2Instance[TOOLBAR_GRID]=topBox;
				UIUtils.topAlign(topBoxWrap, TOOLBAR_HEIGHT);
				box.addElement(topBoxWrap);
			}

			//工作区域
			var centerBox:VGroup=new VGroup();
			centerBox.gap=0;
			id2Instance[BOX_GRID]=centerBox;
			UIUtils.fullAlign(centerBox);
			box.addElement(centerBox);


			//分页面板区域
			if (gridPagination)
			{
				var bottomBox:Group=new Group();
				var bottomWrap:UIComponent=BorderWrapContain.wrap(bottomBox, false, true, false, false, BOX_TOOLBAR_BG_COLOR);
				UIUtils.bottomAlign(bottomWrap, TOOLBAR_HEIGHT);
				id2Instance[TOOLBAR_PAGINATION]=bottomBox;
				box.addElement(bottomWrap);
			}
		}


		////////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
