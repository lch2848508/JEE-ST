package com.estudio.flex.client
{
    import flash.display.GradientType;
    import flash.geom.Matrix;

    import mx.skins.ProgrammaticSkin;

    public class NoBorderMenuBarSkin extends ProgrammaticSkin
    {
        public function NoBorderMenuBarSkin()
        {
            super ();
        }

        override protected function updateDisplayList(w:Number , h:Number):void
        {
            //自绘背景
            /*
			graphics.clear ();
            var matrix:Matrix = new Matrix ();
            matrix.createGradientBox (w , h , Math.PI / 2 , 0 , 0);
            var colors:Array = [0xFFFFFF , 0xD9D9D9];
            var alphas:Array = [100 , 100];
            var ratios:Array = [0x00 , 0xFF];
            graphics.beginGradientFill (GradientType.LINEAR , colors , alphas , ratios , matrix);
            graphics.drawRoundRect (0 , 0 , w , h , 4);
            graphics.endFill ();
			*/
        }
    }
}
