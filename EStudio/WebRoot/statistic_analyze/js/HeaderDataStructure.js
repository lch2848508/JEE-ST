
var headerDataStructure = {};

/*
 * 表头数据的构建
 * 
 * @HeaderData	表头数据
 */
headerDataStructure.headerData = (function(HeaderData) {

	//表头列数
	var NumberofCol = tableProperty.columnsCalculate(HeaderData);
	
	//表头行数
	var NumberofROW = tableProperty.rowsCalculate(HeaderData)+1;
	
	//结果数组变量的构建
	var result = headerDataStructure.ArrayStructure(NumberofROW);
	
	//表头行存储数组变量的构建(存储easyUI的Grid需要的表头数据格式)
	var GridHeader = headerDataStructure.ArrayStructure(NumberofROW);
	
	//表头数据的构建(数据格式为easyUI的Grid)
	headerDataStructure.columnsData(HeaderData,NumberofROW,0,GridHeader);
	
	for(var i=0,l=GridHeader.length;i<l;i++)
	{
		var h = 0;
		for(var j=0,ll=GridHeader[i].length;j<ll;j++)
		{
			if(result[i][h]==undefined)
			{
				if(GridHeader[i][j].rowspan>1)
				{
					for(var k=i;k<(i+GridHeader[i][j].rowspan-1);k++)
					{
						result[k+1][h] = '#rspan';
					}
				}
				result[i][h++] = GridHeader[i][j].title;
			}
			else
			{
				for(var g=h;g<NumberofCol;g++)
				{
					var temp = headerDataStructure.headerCellHandle(result[i][g]);
					if(temp)
					{
						h += temp;
					}
					else
					{
						break;
					}
				}
				if(GridHeader[i][j].rowspan>1)
				{
					for(var k=i;k<(i+GridHeader[i][j].rowspan-1);k++)
					{
						result[k+1][h] = '#rspan';
					}
				}
				result[i][h++] = GridHeader[i][j].title;
			}
			
			if(GridHeader[i][j].colspan>1)
			{
				for(var k=0;k<GridHeader[i][j].colspan-1;k++)
				{
					result[i][h++] = '#cspan';
				}
			}
		}
	}
	return result;
});

/*
 * 变量构建(根据行数构建数据组的个数，用于装载不同行的数据)
 * 
 * @NR	表头的行数
 */
headerDataStructure.ArrayStructure = (function(NR) {
	var result = [];
	for(var i=0;i<NR;i++)
	{
		result.push([]);
	}
	return result;
});

/*
 *  表头数据的初步构建，格式为easyUI的TreeGrid表头所需格式。
 *  
 *  @HeaderData	表头数据
 *  @RN			表头行数
 *  @k			表头数组构建的序号，初始值为k=0为第一行的记录，k=1为第二行记录。
 *  @GridHeader 表头数据构建结果
 */

headerDataStructure.columnsData = (function(HeaderData,RN,k,GridHeader) {
	var GridRow = RN;
	var length = HeaderData.length;
	var lengthChildren = [];
	var rowspanCount = 0;
	for(var i=0;i<length;i++)
	{
		lengthChildren[i] = 0;
		lengthChildren[i] = HeaderData[i].children==undefined?1:(tableProperty.columnsCalculate(HeaderData[i].children));
		rowspanCount = HeaderData[i].children==undefined||HeaderData[i].children==""?GridRow:1;
		if(k==0&&i==0)
		{
			GridHeader[k].push({
			field : HeaderData[i].columnName,
			title : HeaderData[i].columnLabel,
			width : 150,//GridColumnsInfo[i].columnWidth,
			colspan : lengthChildren[i],
			rowspan : rowspanCount
			});
		}
		else
		{
			GridHeader[k].push({
			field : HeaderData[i].columnName,
			title : HeaderData[i].columnLabel,
			width : HeaderData[i].columnWidth,
			colspan : lengthChildren[i],
			rowspan : rowspanCount,
			align : 'center'
			});
		}
	}
	RN = RN-1;
	k++;
	for(var j=0;j<length;j++)
	{
		if(HeaderData[j].children!=undefined)
		{
			headerDataStructure.columnsData(HeaderData[j].children,RN,k,GridHeader);
		}
	}
	RN = RN+1;
});

/*
 * 中间过程数据的判断。若数据格已经定义，返回数值1，数据格向后移动一个；若数据格未定义，返回数值0，数据格不移动并进行数据填充。
 * 
 * @cellData	单元格，若cellData=undefined，则单元格未存储任何数据。
 */
headerDataStructure.headerCellHandle = (function(cellData) {
	return cellData==undefined?0:1;
});

/*
 * 表头单元格的宽度
 * 
 * @CN 表格总列数
 */
headerDataStructure.headerCellWidth = (function(CN) {
	var dataBoxWidth = myLayout.cells('b').getWidth();
	var cellWidth = "150,";
	var differentWidthValue = dataBoxWidth - 150 - 120*(CN-1);
	if(differentWidthValue<=0)
	{
		for(var i=1;i<CN;i++)
		{
			cellWidth += i!=CN-1?"120,":"120";
		}
	}
	else
	{
		var averageWidth = (dataBoxWidth - 150)/(CN-1);
		for(var i=1;i<CN;i++)
		{
			cellWidth += i!=CN-1?(averageWidth+","):averageWidth;
		}
	}
	return cellWidth;
});

/*
 * 表头单元格的数据类型
 * 
 * @CN 表格总列数
 */
headerDataStructure.headerCellType = (function(CN) {
	var cellType = "tree,";
	for(var i=1;i<CN;i++)
	{
		cellType += i!=CN-1?"ro,":"ro";
	}
	return cellType;
});

/*
 * 表头单元格的字体样式
 * 
 * @CN 表格总列数
 */
headerDataStructure.headerTextAlign = (function(CN) {
	var textAlign = [];
	for(var i=0;i<CN;i++)
	{
		textAlign.push("text-align:center");
	}
	return textAlign;
});

/*
 * 表头右键功能菜单显示情况
 * 
 * @CN 表格总列数
 */
headerDataStructure.headerMenu = (function(CN) {
	var headerMenuState = "false,";
	for(var i=1;i<CN;i++)
	{
		headerMenuState += i!=CN-1?"true,":"true";
	}
	return headerMenuState;
});


