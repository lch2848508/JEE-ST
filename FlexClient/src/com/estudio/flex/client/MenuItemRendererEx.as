package com.estudio.flex.client
{
    import mx.controls.Image;
    import mx.controls.menuClasses.MenuItemRenderer;

    public class MenuItemRendererEx extends MenuItemRenderer
    {
		private var offset:int = 3;
        public function MenuItemRendererEx()
        {
            super ();
            this.height = 25;
			this.setStyle("leftIconGap",18 + offset);
        }

        public static var menuIco:String = "iconUrl";

        override protected function commitProperties():void
        {
            super.commitProperties ();
            var iconPath:String = "";
            if (data)
            {
                if (data[menuIco])
                {
                    iconPath = data[menuIco];
                }
                if (iconPath != null && iconPath != "")
				{
					Image (icon).source = iconPath;
				}
            }
        } 
		
		override protected function measure():void
		{
			super.measure();
			measuredWidth += offset*2;
		}
		
    }
}
