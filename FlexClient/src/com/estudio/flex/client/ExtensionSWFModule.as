import ext.intf.IExtensionSWF;

import flash.events.Event;
import flash.external.ExternalInterface;

import mx.controls.Alert;
import mx.controls.SWFLoader;
import mx.events.FlexEvent;
import mx.managers.SystemManager;

import spark.modules.ModuleLoader;

private var _swfInstances:Array=[];
//////////////////////////////////////////////////////////////////////////////////////////////////////
private var index:int=0;

//////////////////////////////////////////////////////////////////////////////////////////////////////
private function registerJSFunction(namespace:String, funMap:Object):void
{
	var js:Array=[];
	js.push("ExtFunction." + namespace + " = {};");
	for (var k:String in funMap)
	{
		var funname:String="__" + namespace + "_" + k + "__";
		ExternalInterface.addCallback(funname, funMap[k]);
		js.push("ExtFunction." + namespace + "." + k + " = MainForm." + funname + ";");
	}
	ExternalInterface.call("registerExtFunction", js.join("\n"));
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
public function loadExtensionsSWFModule():void
{
	var swfFiles:Array=["./flash/ext/swf/Module4TourMIS.swf"];
	for (var i:int=0; i < swfFiles.length; i++)
	{
		var swfFileName:String=swfFiles[i];
		var module:ModuleLoader = new ModuleLoader();
		module.left = -100;
		module.top = -100;
		module.width = 0;
		module.height = 0;
		this.addElementAt(module,0);
		module.loadModule(swfFileName);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//加载SWF
private function loadExtensionSWFModule(filename:String):void
{
	var swf:SWFLoader=new SWFLoader();
	swf.addEventListener(Event.COMPLETE, eventSWFComplete);
	_swfInstances.push(swf);
	swf.load(filename);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//SWF加载完成
private function eventSWFComplete(event:Event):void
{
	var loaderSWF:SWFLoader=event.target as SWFLoader;
	var obj:SystemManager=loaderSWF.content as SystemManager;
	obj.addEventListener(FlexEvent.APPLICATION_COMPLETE, eventSWFReady);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//注册通知
private function eventSWFReady(event:Event):void
{
	var app:Object=event.target.application;
	if (app is IExtensionSWF)
	{
		var funMap:Object=IExtensionSWF(app).getName2Function();
		var namespace:String=IExtensionSWF(app).getNamespace();
		registerJSFunction(namespace, funMap);
	}
}

