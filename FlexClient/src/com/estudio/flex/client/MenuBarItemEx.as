package com.estudio.flex.client
{
    import mx.controls.Image;
    import mx.controls.menuClasses.MenuBarItem;

    public class MenuBarItemEx extends MenuBarItem
    {
        private var image:Image = new Image ();
        private var offset:int = 4;

        public function MenuBarItemEx()
        {
            super ();
            this.image.width = 16;
            this.image.height = 16;
        }

        override protected function createChildren():void
        {
            super.createChildren ();
            super.addChildAt (image , 0);
        }

        /**
         *  @private
         */
        override protected function updateDisplayList(unscaledWidth:Number , unscaledHeight:Number):void
        {
            super.updateDisplayList (unscaledWidth , unscaledHeight);
            this.image.x = 2 + offset;
            this.image.y = (unscaledHeight - 16) / 2;
            this.image.source = data.iconUrl;
            this.label.x = this.image.x + 17;
        }

        override protected function measure():void
        {
            super.measure ();
            measuredWidth += offset * 2;
        }
    }
}
