
var indexStructure = {};

/*
 * 指标控件初始化
 * 
 * @NR	表头行数，即指标的级数
 */
indexStructure.xindexInit=(function(xNR){
	var result = [];
	var xcontrolofFirstIndex = document.getElementsByName('xfirstIndex');
	var xcontrolofSecondIndex = document.getElementsByName('xsecondIndex');
	var xcontrolofThirdIndex = document.getElementsByName('xthirdIndex');
	var xcontrolofFourthIndex = document.getElementsByName('xfourthIndex');
	var xcontrolofFifthIndex = document.getElementsByName('xfifthIndex');

	if(xNR>=1)
	{
		result.push(xcontrolofFirstIndex);
	}
	if(xNR>=2)
	{
		result.push(xcontrolofSecondIndex);
	}
	if(xNR>=3)
	{
		result.push(xcontrolofThirdIndex);
	}
	if(xNR>=4)
	{
		result.push(xcontrolofFourthIndex);
	}
	if(xNR>=5)
	{
		result.push(xcontrolofFifthIndex);
	}
	
	return result;
});


indexStructure.indexInit = (function(NR) {
	var result = [];
	var controlofFirstIndex = document.getElementsByName('firstIndex');
	var controlofSecondIndex = document.getElementsByName('secondIndex');
	var controlofThirdIndex = document.getElementsByName('thirdIndex');
	var controlofFourthIndex = document.getElementsByName('fourthIndex');
	var controlofFifthIndex = document.getElementsByName('fifthIndex');
	if(NR>=1)
	{
		result.push(controlofFirstIndex);
		
	}
	if(NR>=2)
	{
		result.push(controlofSecondIndex);
	}
	if(NR>=3)
	{
		result.push(controlofThirdIndex);
	}
	if(NR>=4)
	{
		result.push(controlofFourthIndex);
	}
	if(NR>=5)
	{
		result.push(controlofFifthIndex);
	}
	
	return result;
});

/*
 * 指标选择是否为空的判断
 * 
 * @index	指标数组
 * @series	指标级数
 */
indexStructure.indexJudge = (function(index,series) {
	var judgeResult = 0;
	for(var i=0;i<index[series].length;i++)
	{
		if(index[series][i].checked)
		{
			judgeResult=1;
			break;
		}
	}
	return judgeResult;
});


/*
 * 指标选择结果
 * 
 * @index		指标数组
 * @headerData	表头数据
 */
indexStructure.indexSelectResult = (function(headerData,index,xindex,recordsData) {
	var selectResult = {x:[],y:[]};
	//表头行数
	//var NumberofROW = tableProperty.rowsCalculate(headerData);
	var NumberofROW=ycount;
	var ColumnName=headerData[0].columnName;
	var xarray=indexPadding.getxFirstIndex(recordsData,ColumnName);
	//var xNumberofROW=publicFunction.getXlevel(xarray);
	var xNumberofROW=xcount;
	if(NumberofROW>=1)
	{
		if(indexStructure.getIndexValue(index[0]).split(',').length>1)
		{
			selectResult.y.push(indexStructure.getIndexValue(index[0]).split(',')[0]);
		}
		else
		{
			selectResult.y.push(indexStructure.getIndexValue(index[0]));
		}
	}
	if(NumberofROW>=2)
	{
		if(indexStructure.getIndexValue(index[1]).split(',').length>1)
		{
			selectResult.y.push(indexStructure.getIndexValue(index[1]).split(',')[0]);
		}
		else
		{
			selectResult.y.push(indexStructure.getIndexValue(index[1]));
		}
	}
	if(NumberofROW>=3)
	{
		if(indexStructure.getIndexValue(index[2]).split(',').length>1)
		{
			selectResult.y.push(indexStructure.getIndexValue(index[2]).split(',')[0]);
		}
		else
		{
			selectResult.y.push(indexStructure.getIndexValue(index[2]));
		}
	}
	if(NumberofROW>=4)
	{
		if(indexStructure.getIndexValue(index[3]).split(',').length>1)
		{
			selectResult.y.push(indexStructure.getIndexValue(index[3]).split(',')[0]);
		}
		else
		{
			selectResult.y.push(indexStructure.getIndexValue(index[3]));
		}
	}
	if(NumberofROW>=5)
	{
		if(indexStructure.getIndexValue(index[4]).split(',').length>1)
		{
			selectResult.y.push(indexStructure.getIndexValue(index[4]).split(',')[0]);
		}
		else
		{
			selectResult.y.push(indexStructure.getIndexValue(index[4]));
		}
	}
	if(NumberofROW>=6)
	{
		alert("数据表表头层数超过5层！请修改表头或联系开发人员！");
		location.reload();
	}
	if(xNumberofROW>=1&&xindex[0].length!=0)
	{
		if(indexStructure.getIndexValue(xindex[0]).split(',').length>1)
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[0]).split(',')[0]);
		}
		else
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[0]));
		}
	}
	if(xNumberofROW>=2&&xindex[1].length!=0)
	{
		if(indexStructure.getIndexValue(xindex[1]).split(',').length>1)
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[1]).split(',')[0]);
		}
		else
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[1]));
		}
	}
	if(xNumberofROW>=3&&xindex[2].length!=0)
	{
		if(indexStructure.getIndexValue(xindex[2]).split(',').length>1)
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[2]).split(',')[0]);
		}
		else
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[2]));
		}
	}
	if(xNumberofROW>=4&&xindex[3].length!=0)
	{
		if(indexStructure.getIndexValue(xindex[3]).split(',').length>1)
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[3]).split(',')[0]);
		}
		else
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[3]));
		}
	}
	if(xNumberofROW>=5&&xindex[4].length!=0)
	{
		if(indexStructure.getIndexValue(xindex[4]).split(',').length>1)
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[4]).split(',')[0]);
		}
		else
		{
			selectResult.x.push(indexStructure.getIndexValue(xindex[4]));
		}
	}
	return selectResult;
});

/*
 * 指标值的获取
 * 
 * @index	指标数组
 */
indexStructure.getIndexValue = (function(index) {
	var judgeResult = 0;
	for(var i=0;i<index.length;i++)
	{
		if(index[i].checked)
		{
			judgeResult=1;
			break;
		}
	}
if(judgeResult==1){
	var result = "";
	if(index[0].type=="radio"&&index[0]!=undefined)
	{
		for(var i=0,l=index.length;i<l;i++)
		{
			if(index[i].checked)
			{
				result = index[i].defaultValue;
			}
		}
	}
	else if(index[0].type=="checkbox"&&index[0]!=undefined)
	{
		var count = indexStructure.getCheckboxQuantity(index);
		if(count==1)
		{
			for(var i=0,l=index.length;i<l;i++)
			{
				if(index[i].checked)
				{
					result = index[i].defaultValue;
				}
			}
		}
		else if(count>1)
		{
			var k=1;
			for(var i=0,l=index.length;i<l;i++)
			{
				if(index[i].checked)
				{
					result += index[i].defaultValue;
					if(k<count)
					{
						result += ",";
						k++;
					}
				}
			}
		}
	}
}
	
	return result;
});


/*
 * 计算checkbox控件选中个数
 * 
 * @index	指标数组
 */
indexStructure.getCheckboxQuantity = (function(index) {
	var checkedQuantity = 0;
	for(var i=0,l=index.length;i<l;i++)
	{
		if(index[i].checked)
		{
			checkedQuantity += 1;
		}
	}
	return checkedQuantity;
});






