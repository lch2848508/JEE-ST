package com.estudio.flex.component
{
    import flash.geom.Matrix;
    import flash.text.TextField;

    import mx.controls.Menu;
    import mx.controls.listClasses.ListData;
    import mx.controls.menuClasses.MenuItemRenderer;
    import mx.core.UIComponent;

    import spark.components.Image;

    public class CustomMenuItemRenderer extends MenuItemRenderer
    {
        private var image:spark.components.Image = new spark.components.Image ();

        public function CustomMenuItemRenderer()
        {
        }

        override protected function measure():void
        {
            super.measure ();

            if (separatorIcon || listData == null)
            {
                return;
            }

            //var imageAsset:IImageAsset = Utils.getImageAsset(data.@iconName);
            //if(imageAsset == null){
            //	return;
            //}
            //measuredWidth += imageAsset.width;
            //if(imageAsset.height > measuredHeight){
            //	measuredHeight = imageAsset.height;
            //}
        }

        override protected function commitProperties():void
        {
            super.commitProperties ();

            if (separatorIcon || listData == null)
            {
                return;
            }

            //var imageAsset:IImageAsset = Utils.getImageAsset(data.@iconName);
            //if(imageAsset == null){
            //	return;
            //}
            //image.width = imageAsset.width;
            //image.height = imageAsset.height;
            //var xoffset:Number = getStyle("horizontalGap")/2;
            //var yoffset:Number = (Menu(this.listData.owner).rowHeight - imageAsset.height)/2;
            //var matrix:Matrix = new Matrix(1, 0, 0, 1, xoffset, yoffset);
            //image.graphics.beginBitmapFill(imageAsset.getBitmapData(), matrix, false);
            //image.graphics.drawRect(xoffset, yoffset, image.width, image.height);
            //image.graphics.endFill();
            image.source = "../images/delete.png";
            if (!this.contains (image))
            {
                this.addChild (image);
            }
        }

        override public function get measuredIconWidth():Number
        {
            //var imageAsset:IImageAsset = Utils.getImageAsset(data.@iconName);
            //if(imageAsset == null){
            //	return 0 ;
            //}else{
            //	var horizontalGap:Number = getStyle("horizontalGap");
            //	return imageAsset.width + horizontalGap;
            //}
            return 16;
        }
    }
}
