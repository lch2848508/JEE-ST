<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Simple Map</title>
    <link rel="stylesheet" type="text/css" href="http://127.0.0.1:8080/EStudio/arcgis_js_api/library/3.16/3.16/dijit/themes/tundra/tundra.css"/>
    <link rel="stylesheet" type="text/css" href="http://127.0.0.1:8080/EStudio/arcgis_js_api/library/3.16/3.16/esri/css/esri.css" />
    <script type="text/javascript" src="http://127.0.0.1:8080/EStudio/arcgis_js_api/library/3.16/3.16/init.js"></script>
    <script type="text/javascript">
      dojo.require("esri.map");
      function init() {
        var myMap = new esri.Map("mapDiv");
        //note that if you do not have public Internet access then you will need to point this url to your own locally accessible cached service.
        var myTiledMapServiceLayer = new esri.layers.ArcGISTiledMapServiceLayer("http://localhost:6080/arcgis/rest/services/ST/XZQH/MapServer");
		myMap.addLayer(myTiledMapServiceLayer);
		
		var DLLayer = new esri.layers.ArcGISDynamicMapServiceLayer("http://localhost:6080/arcgis/rest/services/ST/JTLL/MapServer");
		myMap.addLayer(DLLayer);
      }
      dojo.addOnLoad(init);
    </script>
  </head>
  <body class="tundra">
    <div id="mapDiv" style="width:900px; height:600px; border:1px solid #000;"></div>
  </body>
</html>