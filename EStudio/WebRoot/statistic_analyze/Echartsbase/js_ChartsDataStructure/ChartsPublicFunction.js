
var chartsPublicFunction = {};

/* 
 * 根据指标查询查询数据的列名，如F2
 * @HeaderData  数据表头的详细数据信息
 * @index        指标数组
 * @k			   指标级数，index[k]→index[0]：一级指标；index[1]：二级指标……以此类推
 */
chartsPublicFunction.getIndexColumnName = (function(HeaderData,index,k) {
	var indexColumnName = "";
	//var result={indexColumnName:"",indexColumnLabel:""};
	for(var i=0,Length=HeaderData.length;i<Length;i++)
	{
		if(HeaderData[i].columnLabel==index[k]&&HeaderData[i].children[0].children!=undefined)
		{
			indexColumnName = chartsPublicFunction.getIndexColumnName(HeaderData[i].children,index,++k);
			//indexColumnLabel = chartsPublicFunction.getIndexColumnName(HeaderData[i].children,index,++k).indexColumnLabel;
		}
		else if(HeaderData[i].columnLabel==index[k]&&HeaderData[i].children!=undefined&&HeaderData[i].children[0].children==undefined)
		{
			indexColumnName = HeaderData[i].children[0].columnName;

		}
	}
	return indexColumnName;
});
/* 
 * 根据指标查询查询数据的单位
 * @HeaderData  数据表头的详细数据信息
 * @index        指标数组
 * @k			   指标级数，index[k]→index[0]：一级指标；index[1]：二级指标……以此类推
 */
chartsPublicFunction.getIndexColumnLabel = (function(HeaderData,index,k) {
	var indexColumnLabel = "";
	var NumberofROW = tableProperty.rowsCalculate(HeaderData);
	for(var i=0,Length=HeaderData.length;i<Length;i++)
	{
			if(HeaderData[i].columnLabel==index[k]&&HeaderData[i].children[0].children!=undefined)
			{
				
				indexColumnLabel = chartsPublicFunction.getIndexColumnLabel(HeaderData[i].children,index,++k);

			}
			else if(HeaderData[i].columnLabel==index[k]&&HeaderData[i].children!=undefined&&HeaderData[i].children[0].children==undefined)
			{
				indexColumnLabel= HeaderData[i].children[0].columnLabel;		
			}
		
		
	}

	return indexColumnLabel;
});
/*
 * 地图的Option的dataRange参数获取
 * 
 * @MapParam	Map图表serise下data参数数据
 */
chartsPublicFunction.setDataRange = (function(MapParam) {
	var resultArray = new Array();
	for(var i=0,l=MapParam.length;i<l;i++)
	{
		resultArray[i] = MapParam[i].value;
	}
	return resultArray;
});

/*
 * 获取geoJSON地图数据
 * 
 * @cityLength		城市或区域数量，4：四大区域，21或22：21或22（包含顺德）地市
 * @firstCityName	数据表记录第一条记录的名称，用于二次判断，后续开发辅助作用。
 */
chartsPublicFunction.getMapGeoJson = (function(cityLength,firstCityName) {
	var result = "";
	if(cityLength==4)
	{
		result = "guang_dong_geo_QY.json";
	}
	else if(cityLength==21)
	{
		result = "guang_dong_geo_DS.json";
	}
	else if(cityLength==22)
	{
		dhtmlx.alert({
			type:"alert-warning",
			ok:"确定",
			text:"数据包含顺德区域，找不到数据源，请联系开发人员！ ",
		});
	}
	return result;
});




