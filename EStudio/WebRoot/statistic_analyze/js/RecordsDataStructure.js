
var recordsDataStructure = {};

/*
 *  表格数据格式的构建
 * 
 *  @HeaderData		表头数据
 *  @recordsData	表详细数据
 */
recordsDataStructure.recordsData = (function(HeaderData,recordsData) {
	var fieldList = [];
	recordsDataStructure.getFieldList(HeaderData, fieldList);
	var newRecords = recordsDataStructure.changeDataStyle(recordsData,fieldList,1);
	var result = [];
	result.push({rows:newRecords});
	return result;
});

/*
 * 获取表头最低行的编号
 * @columns		表头数据
 * @fieldList	表头最底行编号数组
 */
recordsDataStructure.getFieldList = (function(columns, fieldList) {
	for ( var i = 0; i < columns.length; i++) {
		var column = columns[i];
		if (columns[i].children)
			recordsDataStructure.getFieldList(columns[i].children, fieldList);
		else
			fieldList.push(column.columnName);
	}
});

/*
 * 根据数据是否含有子节点（children）对数据的拼接	(每一级文件最多包含1000个文件)
 * @records		表的详细数据
 * @fieldList	表头最底行编号数组
 * @g			表第一列的级数编号(用于图标设置)
 */
recordsDataStructure.changeDataStyle = (function(records, fieldList, g) {
	var newRecords = [];
	for(var i = 0; i < records.length; i++)
	{
		if (records[i].children)
		{
			newRecords.push({id:++g,data:recordsDataStructure.transformData(records[i], fieldList, g),rows:recordsDataStructure.changeDataStyle(records[i].children, fieldList,g*1000)});
		}
		else
		{
			newRecords.push({id:++g,data:recordsDataStructure.transformData(records[i], fieldList, g)});
		}
	}
	return newRecords;
});

/*
 * 每一行数据的构建
 * @records		表的详细数据
 * @fieldList	表头最底行编号数组
 * @g			数据体列的编号(用于图标设置)
 */
recordsDataStructure.transformData = (function(record, fieldList, g) {
	var result = [];
	for ( var i = 0; i < fieldList.length; i++)
	{
		if(i==0&&g<1000)
		{
			if(record.children)
				result.push({value:record[fieldList[i]],image:"folder.gif"});
			else
				result.push({value:record[fieldList[i]]});
		}
		else
		{
			result.push(record[fieldList[i]]);
		}
	}
	return result;
});



