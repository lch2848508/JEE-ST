package com.webgis.module.plotting
{
//	import com.esri.ags.symbols.TextSymbol;
//	
//	import flash.text.TextFormat;
//	
//	public class ExtTextSymbol extends TextSymbol
//	{
//		public function ExtTextSymbol(text:String=null, htmlText:String=null, color:uint=0, alpha:Number=1, border:Boolean=false, borderColor:uint=0, background:Boolean=false, backgroundColor:uint=16777215, placement:String="middle", angle:Number=0, xoffset:Number=0, yoffset:Number=0, textFormat:TextFormat=null, textAttribute:String=null, textFunction:Function=null)
//		{
//			super(text, htmlText, color, alpha, border, borderColor, background, backgroundColor, placement, angle, xoffset, yoffset, textFormat, textAttribute, textFunction);
//		}
//	}
//	
	
	import com.esri.ags.symbols.TextSymbol;  
	
	import flash.text.TextFormat;  
	
	public class ExtTextSymbol extends TextSymbol  
	{  
		private var formerText:String;  
		
		
		
		public function ExtTextSymbol(Vertical:Boolean=false, text:String=null, htmlText:String=null, color:uint=0, alpha:Number=1, border:Boolean=false, borderColor:uint=0, background:Boolean=false, backgroundColor:uint=16777215, placement:String="middle", angle:Number=0, xoffset:Number=0, yoffset:Number=0, textFormat:TextFormat=null, textAttribute:String=null, textFunction:Function=null)  
		{  
			//TODO: implement function  
			super(text, htmlText, color, alpha, border, borderColor, background, backgroundColor, placement, angle, xoffset, yoffset, textFormat, textAttribute, textFunction);  
			if(Vertical)  
			{  
				formerText = text;  
				init();  
			}  
		}  
		
		private function init():void {  
			
			var tempText:String = "";  
			
			for (var i:int = 0; i < formerText.length; i++) {  
				
				tempText += formerText.charAt(i) + "\n";  
				
			}  
			
			this.text = tempText;  
			
		}  
		
		
	}  
}  
