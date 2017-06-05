
var publicFunction = {};

/*
 * 指标DIV控件的构建并填充到页面上
 * 
 */
publicFunction.indexDivPadding = (function() {
	$('#indexParam').html("");
	var paramform = "";
	paramform += "<div id='first-lable' style='padding:5px;'><table id='first-Index'></table></div>";
	paramform += "<div id='second-lable' style='padding:5px;'><table id='second-Index'></table></div>";
	paramform += "<div id='third-lable' style='padding:5px;'><table id='third-Index'></table></div>";
	paramform += "<div id='fourth-lable' style='padding:5px;'><table id='fourth-Index'></table></div>";
	paramform += "<div id='fifth-lable' style='padding:5px;'><table id='fifth-Index'></table></div>";
	$('#indexParam').html(paramform);
	///lc
	$('#xindexParam').html("");
	var xparamform = "";
	xparamform += "<div id='xfirst-lable' style='padding:10px;' float='left'> <table id='xfirst-Index'></table></div>";
	xparamform += "<div id='xsecond-lable' style='padding:10px;'> <table id='xsecond-Index'></table></div>";
	xparamform += "<div id='xthird-lable' style='padding:10px;'><table id='xthird-Index'></table></div>";
	xparamform += "<div id='xfourth-lable' style='padding:10px;'><table id='xfourth-Index'></table></div>";
	xparamform += "<div id='xfifth-lable' style='padding:10px;'><table id='xfifth-Index'></table></div>";
	$('#xindexParam').html(xparamform);
});
	///
/*
 * 指标DIV控件的重置
 * 
 * @count	指标级数，1为一级指标，隐藏所有指标控件；2为二级指标，隐藏一级指标下的所有指标；……
 */
publicFunction.indexDivReset = (function(count) {
	if(count<=5)
	{
		document.getElementById("fifth-lable").style.display = "none";$("#fifth-Index").html("");
	}
	if(count<=4)
	{
		document.getElementById("fourth-lable").style.display = "none";$("#fourth-Index").html("");
	}
	if(count<=3)
	{
		document.getElementById("third-lable").style.display = "none";$("#third-Index").html("");
	}
	if(count<=2)
	{
		document.getElementById("second-lable").style.display = "none";$("#second-Index").html("");
	}
	if(count<=1)
	{
		document.getElementById("first-lable").style.display = "none";$("#first-Index").html("");
		
	}

	
});

publicFunction.xindexDivReset = (function(count) {
	if(count<=5)
	{
		document.getElementById("xfifth-lable").style.display = "none";$("#xfifth-Index").html("");
	}
	if(count<=4)
	{
		document.getElementById("xfourth-lable").style.display = "none";$("#xfourth-Index").html("");
	}
	if(count<=3)
	{
		document.getElementById("xthird-lable").style.display = "none";$("#xthird-Index").html("");
	}
	if(count<=2)
	{
		document.getElementById("xsecond-lable").style.display = "none";$("#xsecond-Index").html("");
	}
	if(count<=1)
	{
		document.getElementById("xfirst-lable").style.display = "none";$("#xfirst-Index").html("");
		
	}
});
function _valueofIndex(controlofIndex){
	var result = "";
	if(controlofIndex[0].type=="radio")
	{
		for(var i=0,l=controlofIndex.length;i<l;i++)
		{
			if(controlofIndex[i].checked)
			{
				result = controlofIndex[i].defaultValue;
			}
		}
	}
	else if(controlofIndex[0].type=="checkbox")
	{
		var count = _numberofChecked(controlofIndex);
		if(count==1)
		{
			for(var i=0,l=controlofIndex.length;i<l;i++)
			{
				if(controlofIndex[i].checked)
				{
					result = controlofIndex[i].defaultValue;
				}
			}
		}
		else if(count>1)
		{
			var k=1;
			for(var i=0,l=controlofIndex.length;i<l;i++)
			{
				if(controlofIndex[i].checked)
				{
					result += controlofIndex[i].defaultValue;
					if(k<count)
					{
						result += ",";
						k++;
					}
				}
			}
		}
	}
	return result;
}

//判断数组是否为空来确定xlevel的值
publicFunction.getXlevel=(function(_array)
{
	var xlevel=0;
	var arraylevel=_array;
	for(var i=0;i<=4;i++)
	{
		if(_array[i].length!=0)
		{
			xlevel++;
		}
	}
	return xlevel;
});

