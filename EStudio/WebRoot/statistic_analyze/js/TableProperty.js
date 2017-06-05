
var tableProperty = {};

/*
 * 表头列数的计算
 * 
 * @HeaderData 表头数据源
 */
tableProperty.columnsCalculate = (function(HeaderData) {
	var columnNumber = 0;
	var lengthChildren = [];
	for(var i=0,l=HeaderData.length;i<l;i++)
	{
		lengthChildren[i] = HeaderData[i].children==undefined?1:HeaderData[i].children.length;
		if(lengthChildren[i]!=1)
		{
			var NextCloumn = tableProperty.columnsCalculate(HeaderData[i].children);
			columnNumber = columnNumber + NextCloumn;
		}
		else
		{
			columnNumber = columnNumber + 1;
		}
	}
	return columnNumber;
});

/*
 * 表头行数的计算
 * 
 * @HeaderData	表头数据
 */
tableProperty.rowsCalculate = (function(HeaderData){
	var s = [];
	var g = 0;
	var k =0;
	var rowNumber =0;
	for(var i=0,l=HeaderData.length;i<l;i++)
	{
		if(HeaderData[i].children!=undefined)
		{
			s[g] = 0;
			s[g] += 1;
			s[g] += tableProperty.rowsCalculate(HeaderData[i].children);
			g++;
			k++;
		}
	}
	for(var j = 0;j<k;j++)
	{
		rowNumber = rowNumber>s[j]?rowNumber:s[j];
	}
	return rowNumber;
});




