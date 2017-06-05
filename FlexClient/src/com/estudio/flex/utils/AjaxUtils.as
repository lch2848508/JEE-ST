package com.estudio.flex.utils
{
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	import flash.utils.ByteArray;


	public class AjaxUtils
	{
		public function AjaxUtils()
		{
		}

		public static function postDataAndReturnBoolean(url:String, params:Object, callFunction:Object):void
		{
			var request:URLRequest=new URLRequest(url);
			var variables:URLVariables=null;
			request.method=URLRequestMethod.POST;
			if (params != null)
			{
				variables=new URLVariables();
				for (var k:String in params)
					variables[k]=params[k];
				request.data=variables;
			}

			var loader:URLLoader=new URLLoader();
			loader.dataFormat=URLLoaderDataFormat.BINARY;

			//成功事件处理函数
			loader.addEventListener(Event.COMPLETE, function(event:Event):void
			{
				var loader:URLLoader=URLLoader(event.target);
				if (callFunction != null)
					callFunction(readUTF8Str(loader));
				loader.close();
			});
			//失败事件处理函数
			loader.addEventListener(IOErrorEvent.IO_ERROR, function(event:IOErrorEvent):void
			{
				var loader:URLLoader=URLLoader(event.target);
				loader.close();
			});

			loader.load(request);
		}


		//从服务器获取数据
		public static function getData(url:String, callFunction:Object, token:Object=null):void
		{
			var request:URLRequest=new URLRequest(url);
			var variables:URLVariables=null;
			request.method=URLRequestMethod.GET;
			var loader:URLLoader=new URLLoader();
			loader.dataFormat=URLLoaderDataFormat.BINARY;
			//成功事件处理函数
			loader.addEventListener(Event.COMPLETE, function(event:Event):void
			{
				var loader:URLLoader=URLLoader(event.target);
				if (callFunction != null)
					callFunction(readUTF8Str(loader), token);
				loader.close();
			});
			//失败事件处理函数
			loader.addEventListener(IOErrorEvent.IO_ERROR, function(event:IOErrorEvent):void
			{
				var loader:URLLoader=URLLoader(event.target);
				loader.close();
			});
			loader.load(request);
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//向服务器提交数据
		public static function postData(url:String, params:Object, callFunction:Object, token:Object=null):void
		{
			var request:URLRequest=new URLRequest(url);
			var variables:URLVariables=null;
			request.method=URLRequestMethod.POST;
			if (params != null)
			{
				variables=new URLVariables();
				for (var k:String in params)
					variables[k]=params[k];
				request.data=variables;
			}

			var loader:URLLoader=new URLLoader();
			loader.dataFormat=URLLoaderDataFormat.BINARY;
			//成功事件处理函数
			loader.addEventListener(Event.COMPLETE, function(event:Event):void
			{
				var loader:URLLoader=URLLoader(event.target);
				if (callFunction != null)
					callFunction(readUTF8Str(loader), token);
				loader.close();
			});
			//失败事件处理函数
			loader.addEventListener(IOErrorEvent.IO_ERROR, function(event:IOErrorEvent):void
			{
				var loader:URLLoader=URLLoader(event.target);
				loader.close();
			});

			loader.load(request);
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static function uploadFile(url:String, formInputName:String, fileName:String, fileContent:ByteArray, completeFun:Function, progressFun:Function, errorFunction:Function, formData:Object=null):void
		{
			var fileupload:MultipartURLLoader=new MultipartURLLoader();
			fileupload.dataFormat=URLLoaderDataFormat.BINARY;
			if (formData)
			{
				for (var k:String in formData)
					fileupload.addVariable(k, formData[k]);
			}

			fileupload.addFile(fileContent, fileName);
			fileupload.addEventListener(MultipartURLLoaderEvent.DATA_PREPARE_PROGRESS, function(event:MultipartURLLoaderEvent):void
			{
				if (progressFun != null)
					progressFun(event.bytesWritten, event.bytesTotal);
			});
			fileupload.addEventListener(IOErrorEvent.IO_ERROR, function(event:IOErrorEvent):void
			{
				var loader:MultipartURLLoader=MultipartURLLoader(event.currentTarget);
				if (errorFunction != null)
					errorFunction(event);
				loader.dispose();
			});
			fileupload.addEventListener(Event.COMPLETE, function(event:Event):void
			{
				var loader:MultipartURLLoader=MultipartURLLoader(event.currentTarget);
				if (completeFun != null)
					completeFun(readUTF8Str(loader.loader));
				loader.dispose();
			});
			fileupload.addEventListener(ProgressEvent.PROGRESS, function(evt:ProgressEvent):void
			{
				if (progressFun != null)
					progressFun(evt.bytesLoaded, evt.bytesTotal);
			});
			fileupload.load(url);
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////
		private static function readUTF8Str(loader:URLLoader):String
		{
			var _byteArray:ByteArray=new ByteArray;
			_byteArray.writeBytes(loader.data);
			_byteArray.position=0;
			var data:String=_byteArray.readMultiByte(_byteArray.length, "UTF-8");
			return data;
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
