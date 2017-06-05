<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
    String jsFile = request.getParameter("js");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<title>空间分析结果展示</title>
<script type="text/javascript" src="../js/jslib/jquery/jquery.js"></script>
<script type="text/javascript" src="../js/jslib/jquery/jquery.json.js"></script>
<script type="text/javascript" src="../js/table_utils.js"></script>
<script type="text/javascript" src="../js/jslib/utils.js"></script>
<script type="text/javascript"
	src="../temp/webgis_spatial_temp/<%=jsFile%>"></script>
<script type="text/javascript"
	src="../js/jslib/echarts-2.2.7/build/dist/echarts.pie.js"></script>

<script language="javascript" type="text/javascript">
    var echartInstance = null;
    var echartOption = {
        tooltip : {
            trigger : 'item',
            formatter : "{b}({d}%)"
        },
        legend : {
            orient : 'vertical',
            x : 'left',
            data : []
        },
        calculable : true,
        series : [ {
            type : 'pie',
            data : []
        } ]
    };

    var layerInfos = null;
    var statisticInfos = null;
    var statisticCategory2Values = {};

    ////////////////////////////////////////////////////////////////////////////////////
    //显示空间分析结果
    function setSpatialResult() {
        //if (spatialAnalyCondition.o == "bufAnaly") {
        var groupFields = spatialAnalyCondition.groupFields;
        var groupFieldLabels = spatialAnalyCondition.groupFieldLables;
        var statisticFunctions = spatialAnalyCondition.statisticFunctions;
        var layerType = spatialAnalyCondition.layerType;
        showPlanGridTable(groupFields, groupFieldLabels, statisticFunctions, spatialAnalyResult.records, layerType);
        //} else if (spatialAnalyCondition.o == "districtAnaly") {

        //} else if (spatialAnalyCondition.o == "groupAnaly") { //分组分析

        //}
    }

    //////////////////////////////////////////////////////////////////////////////////
    //显示普通的表格
    function showPlanGridTable(groupFields, groupFieldLabels, statisticFunctions, records, layerType) {
        var fieldList = [];
        var statisticFieldList = [];
        var tableHtml = "<table id='tableSpatialResult' width='100%' border='1' cellpadding='0' cellspacing='0'><tr>";
        if (spatialAnalyCondition.o == "districtAnaly") {
            tableHtml += "<th scope='col'>行政区域</th>";
            fieldList.push("CITY_NAME");
        }
        for ( var i = 0; i < groupFields.length; i++) {
            tableHtml += "<th scope='col'>" + groupFieldLabels[i] + "</th>";
            fieldList.push(groupFields[i]);
        }

        for ( var i = 0; i < statisticFunctions.length; i++) {
            var funName = statisticFunctions[i];
            if (funName == "max" || funName == "min" || funName == "sum") {
                if (layerType == 0)
                    continue;
                if (funName == "max") {
                    statisticFieldList.push("MAX_GEO_LENGTH");
                    fieldList.push("MAX_GEO_LENGTH");
                    tableHtml += "<th scope='col'>最大长度</th>";
                    if (layerType == 2) {
                        statisticFieldList.push("MAX_GEO_AREA");
                        fieldList.push("MAX_GEO_AREA");
                        tableHtml += "<th scope='col'>最大面积</th>";
                    }
                } else if (funName == "min") {
                    statisticFieldList.push("MIN_GEO_LENGTH");
                    fieldList.push("MIN_GEO_LENGTH");
                    tableHtml += "<th scope='col'>最小长度</th>";
                    if (layerType == 2) {
                        statisticFieldList.push("MIN_GEO_AREA");
                        fieldList.push("MIN_GEO_AREA");
                        tableHtml += "<th scope='col'>最小面积</th>";
                    }
                } else if (funName == "sum") {
                    statisticFieldList.push("SUM_GEO_LENGTH");
                    fieldList.push("SUM_GEO_LENGTH");
                    tableHtml += "<th scope='col'>总长度</th>";
                    if (layerType == 2) {
                        statisticFieldList.push("SUM_GEO_AREA");
                        fieldList.push("SUM_GEO_AREA");
                        tableHtml += "<th scope='col'>总面积</th>";
                    }
                }
            } else if (funName == "count") {
                tableHtml += "<th scope='col'>总数</th>";
                fieldList.push("COUNT");
                statisticFieldList.push("COUNT");
            } else if (funName == "CITY_CODE") {
                tableHtml += "<th scope='col'>编号</th>";
                fieldList.push("CITY_CODE");
                statisticFieldList.push("CITY_CODE");
            } else if (funName == "GDP") {
                tableHtml += "<th scope='col'>GDP</th>";
                fieldList.push("GDP");
                statisticFieldList.push("GDP");
            } else if (funName == "PER_POP") {
                tableHtml += "<th scope='col'>人均里程（m/千人）</th>";
                fieldList.push("PER_POP");
                statisticFieldList.push("PER_POP");
            } else if (funName == "CITY_NAME") {
                tableHtml += "<th scope='col'>城市名称</th>";
                fieldList.push("CITY_NAME");
                statisticFieldList.push("CITY_NAME");
            } else if (funName == "GEOMETRY_AREA") {
                tableHtml += "<th scope='col'>行政面积</th>";
                fieldList.push("GEOMETRY_AREA");
                statisticFieldList.push("GEOMETRY_AREA");
            } else if (funName == "SUMLENGTH") {
                tableHtml += "<th scope='col'>路网总长度</th>";
                fieldList.push("SUMLENGTH");
                statisticFieldList.push("SUMLENGTH");
            } else if (funName == "POP") {
                tableHtml += "<th scope='col'>人口</th>";
                fieldList.push("POP");
                statisticFieldList.push("POP");
             } else if (funName == "FUGAILV") {
                tableHtml += "<th scope='col'>道路覆盖率（km/每人）</th>";
                fieldList.push("FUGAILV");
                statisticFieldList.push("FUGAILV");
            } else {
                tableHtml += "<th scope='col'>自定义</th>";
                fieldList.push("CUSTOM_STATISTIC");
                statisticFieldList.push("CUSTOM_STATISTIC");
            }

        }
        tableHtml += "</tr>";

        for ( var i = 0; i < records.length; i++) {
            var record = records[i];
            tableHtml += "<tr>";
            for ( var j = 0; j < fieldList.length; j++) {
                var v = record[fieldList[j]];
                if (v == null)
                    v = "";
                if (fieldList.length - statisticFieldList.length > j)
                    tableHtml += "<td >" + v + "</td>";
                else
                    tableHtml += "<td class='num'>" + v + "</td>";
            }
            tableHtml += "</tr>";
        }

        tableHtml += "</table>";

        $("#div_content").html(tableHtml);
        if (checktable('tableSpatialResult')) {
            for ( var i = 0; i < fieldList.length - statisticFieldList.length - 1; i++)
                coltogather($('#tableSpatialResult tr'), i);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////
    $(function() {
        setSpatialResult();
    });
</script>

<style>
* {
	font-size: 12px;
	zoom: 1;
}

html {
	width: 100%;
	height: 100%;
}

body {
	margin: 0px;
	padding: 0px;
	background-color: #FFFFFF;
	width: 100%;
	height: 100%;
	zoom: 1;
}

table {
	border-collapse: collapse;
	border-color: #009;
}

tr {
	height: 28px;
}

th {
	font-weight: bold;
	background: #FFc;
}

td {
	padding-left: 5px;
	padding-right: 5px;
}

.num {
	font-family: "Courier New", Courier, monospace;
	text-align: right;
}

#div_content {
	font-family: "微软雅黑", "新宋体";
	font-size: 4px;
	padding: 4px;
}

#div_content * {
	line-height: 150%;
}

#divDetail {
	
}
</style>
</head>
<body>
	<div id="div_content"></div>
</body>
</html>