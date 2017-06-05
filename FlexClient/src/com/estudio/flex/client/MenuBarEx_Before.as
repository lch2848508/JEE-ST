package com.estudio.flex.client
{
    
    import mx.controls.Image;
    import mx.controls.Menu;
    import mx.controls.MenuBar;
    import mx.core.ClassFactory;


    public class MenuBarEx_Before extends MenuBar
    {
        public function MenuBarEx_Before()
        {
            super ();
			this.setStyle("backgroundSkin",NoBorderMenuBarSkin);
            this.menuBarItemRenderer = new ClassFactory (MenuBarItemEx);
        }

        public override function getMenuAt(index:int):Menu
        {
            var menu:Menu = super.getMenuAt (index);
            menu.itemRenderer = new ClassFactory (MenuItemRendererEx);
            menu.iconFunction = getIcon;
            return menu;
        }



        private function getIcon(data:Object):Object
        {
            return Image;
        }
    }
}
