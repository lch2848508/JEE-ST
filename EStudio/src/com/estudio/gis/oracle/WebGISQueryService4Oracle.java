package com.estudio.gis.oracle;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.spatial.geometry.DataException;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;
import oracle.sql.STRUCT;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.gis.GeometryUtils;
import com.estudio.gis.WebGISExportTaskItem;
import com.estudio.gis.WebGISExportUtils;
import com.estudio.gis.WebGISMapServerProxy;
import com.estudio.gis.WebGISSpatialConfig;
import com.estudio.impl.service.sercure.ClientWebService4LineRef;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.SecurityUtils;
import com.estudio.utils.ZipUtils;

public final class WebGISQueryService4Oracle {
    private WebGISSpatialConfig spatialConfig = null;
    private static IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private int MAX_RECORD = 2500;
    private int MAX_EXPORT_RECORD = 50000;
    private Map<String, String> staticticFunctionName2Label = new HashMap<String, String>();

    /**
     * 初始化坐标配置
     * 
     * @param spatialConfig
     * @throws Exception
     */
    public void initSpatialConfig(WebGISSpatialConfig spatialConfig) {
        this.spatialConfig = spatialConfig;
    }

    /**
     * 高级查询
     * 
     * @param con
     * @param userId
     * @param layerId
     * @param params
     * @return
     * @throws Exception
     */
    public String searchEx(long userId, long layerId, String params) throws Exception {
        JSONObject paramsObject = JSONUtils.parserJSONObject(params);
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement searchStmt = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            // 链接数据库
            con = DBHELPER.getConnection();

            // 空间范围
            JGeometry geometry = paramsObject.containsKey("geometry") ? GeometryUtils.arcGISGeometry2OracleSpatial(paramsObject.getString("geometryType"), paramsObject.getJSONObject("geometry"), spatialConfig.wkid) : WebGISResourceService4Oracle.getInstance().getDistrictGeometry(con, paramsObject.getLong("districtId"));
            if (geometry != null)
                geometry = WebGISResourceService4Oracle.getInstance().getGeometryInstractDistrictGeometry(con, userId, geometry, spatialConfig.wkid);
            if (geometry != null) { // 检查空间范围是否越界

                String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid,(select name from webgis_layer_fields where data_type=4 and p_id=a.id) as objectfieldname from user_tables c,webgis_services b,webgis_layer a where a.name=a.equal_layer_as and a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.id = "
                        + layerId;
                resourceStmt = con.prepareStatement(sql2);
                ResultSet resourceRs = resourceStmt.executeQuery();
                resourceRs.next();

                WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
                int recordNumber = 0;
                if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))) { // 从数据库读取
                    String SQL = "select b.q_uid id, b.q_objectid object_id,b.object_caption, b.geometry simp_geometry,object_attributes from spatial_fs_" + layerId + " b where SDO_ANYINTERACT(b.geometry,?) = 'TRUE' ";
                    String filterStr = paramsObject.getString("searthText");
                    String filterSQL = generateFilterSQLCondition(filterStr);
                    if (!StringUtils.isEmpty(filterSQL))
                        SQL += " and " + filterSQL;

                    // where 条件
                    JSONArray wheres = paramsObject.getJSONArray("wheres");
                    for (int i = 0; i < wheres.size(); i++) {
                        SQL += " and " + generateWhereSQL(wheres.getJSONObject(i));
                    }

                    String layerSQL = StringUtils.replace(SQL, "spatial_fs_layerid", "b");

                    String orderFieldStr = generateOrderSQL(paramsObject.getJSONArray("order"), "b");
                    if (!StringUtils.isEmpty(orderFieldStr))
                        layerSQL += " order by " + orderFieldStr;

                    // 查询
                    searchStmt = con.prepareStatement(layerSQL);

                    // 设定空间范围
                    searchStmt.setObject(1, JGeometry.store(geometry, DBHELPER.getNaviteConnection(con)));

                    ResultSet rs = searchStmt.executeQuery();

                    while (rs.next() && recordNumber < MAX_RECORD) {
                        JSONObject recordJson = new JSONObject();
                        recordJson.put("id", rs.getString("id"));
                        recordJson.put("objectId", rs.getString("object_id"));
                        recordJson.put("label", generateRecordCaption(JSONUtils.parserJSONObject(rs.getString("object_attributes")), layerFields));
                        recordJson.put("layerId", layerId);
                        JSONUtils.append(json, "records", recordJson);
                        recordNumber++;
                    }
                } else {
                    String url = resourceRs.getString(2);
                    String whereStr = "";
                    JSONArray wheres = paramsObject.getJSONArray("wheres");
                    for (int i = 0; i < wheres.size(); i++) {
                        whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ") + generateSpatialWhereStr(wheres.getJSONObject(i), layerFields);
                    }
                    String orderFieldStr = generateSpatialOrderFieldStr(paramsObject.getJSONArray("order"), layerFields);
                    JSONObject queryResultJSON = ArcGISServiceRestService.query(url, whereStr, OracleSpatialUtils.toArcGISJson(geometry), null, spatialConfig.wkid + "", false, true, orderFieldStr);//
                    if (queryResultJSON != null) {
                        JSONArray featureArray = queryResultJSON.getJSONArray("features");
                        if (featureArray != null && !featureArray.isEmpty()) {
                            String objectIdFieldName = resourceRs.getString("objectfieldname");
                            for (int m = 0; m < featureArray.size() && recordNumber++ < MAX_RECORD; m++) {
                                JSONObject featureJson = featureArray.getJSONObject(m);
                                long featureObjectId = featureJson.getJSONObject("attributes").getLong(objectIdFieldName);
                                JSONObject recordJson = new JSONObject();
                                recordJson.put("id", featureObjectId);
                                recordJson.put("objectId", featureObjectId);
                                recordJson.put("label", generateRecordCaption(featureJson.getJSONObject("attributes"), layerFields));
                                recordJson.put("layerId", layerId + "");
                                JSONUtils.append(json, "records", recordJson);
                            }
                        }
                    }
                }

                // 查询结果图层信息
                List<Long> layerIds = new ArrayList<Long>();
                layerIds.add(layerId);
                Map<String, JSONObject> layerInfos = generateSearchResultLayerInfos(con, layerIds);
                json.put("layerInfos", layerInfos);
                json.put("objectFieldName", layerInfos.get("" + layerId).getString("objectFieldName"));
                json.put("isSearchResult", true);
                json.put("r", true);
            } else {
                json.put("msg", "没有设定空间范围或设定的空间范围超出权限许可。");
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(searchStmt);
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 生成Where条件
     * 
     * @param fieldName2DataType
     * 
     * @param jsonObject
     * @return
     */
    private String generateWhereSQL(JSONObject where) {
        String result = "";
        String compare = where.getString("compare");
        String fieldName = where.getString("fieldName");
        String value = where.getString("value");
        int dataType = where.getInt("datatype");
        boolean isNumber = dataType == 1 || dataType == 4;
        if (dataType == 2)
            fieldName = "to_char(b." + fieldName + ",'yyyy-mm-dd')";
        else
            fieldName = "b." + fieldName;

        if (StringUtils.equalsIgnoreCase(compare, ">") || StringUtils.equalsIgnoreCase(compare, ">=") || StringUtils.equalsIgnoreCase(compare, "<") || StringUtils.equalsIgnoreCase(compare, "<=")) {
            result = fieldName + " " + compare + (!isNumber ? " '" : " ") + processValueForOracleSQL(value) + (!isNumber ? "'" : "");
        } else if (StringUtils.equalsIgnoreCase(compare, "=") || StringUtils.equalsIgnoreCase(compare, "<>")) {
            if (where.getBoolean("isEnumType")) {
                String[] items = value.split(",");
                List<String> list = new ArrayList<String>();
                for (String v : items) {
                    if (v == " ")
                        list.add((isNumber ? "" : "'") + v + (isNumber ? "" : "'"));
                    else
                        list.add((isNumber ? "" : "'") + processValueForOracleSQL(v) + (isNumber ? "" : "'"));
                }
                result += fieldName + (StringUtils.equalsIgnoreCase(compare, "=") ? " in (" : " not in (") + StringUtils.join(list, ",") + ")";
            } else
                result = fieldName + compare + (isNumber ? " " : " '") + processValueForOracleSQL(value) + (isNumber ? "" : "'");
        } else if (StringUtils.equalsIgnoreCase(compare, "like")) {
            result = (isNumber ? "to_char(" + fieldName + ")" : fieldName) + " " + compare + " '%" + StringUtils.replace(processValueForOracleSQL(value), "%", "") + "%'";
        } else if (StringUtils.equalsIgnoreCase(compare, "between")) {
            result = fieldName + " between " + (isNumber ? "" : "'") + where.getString("v1") + (isNumber ? "" : "'") + " and " + (isNumber ? "" : "'") + where.getString("v2") + (isNumber ? "" : "'");
        }
        result = "(" + result + ")";
        return result;
    }

    private String generateSpatialWhereStr(JSONObject where, WebGISLayerFields layerFields) {
        String result = "";
        String compare = where.getString("compare");
        String fieldName = where.getString("fieldName");
        fieldName = layerFields.schemaFieldName2LayerFieldName.get(fieldName);
        String value = where.getString("value");
        int dataType = where.getInt("datatype");
        boolean isNumber = dataType == 1 || dataType == 4;
        boolean isDate = dataType == 2;
        if (dataType == 2)
            value = " date '" + value + "'";

        if (StringUtils.equalsIgnoreCase(compare, ">") || StringUtils.equalsIgnoreCase(compare, ">=") || StringUtils.equalsIgnoreCase(compare, "<") || StringUtils.equalsIgnoreCase(compare, "<=")) {
            result = fieldName + " " + compare + (!isNumber && !isDate ? " '" : " ") + processValueForOracleSQL(value) + (!isNumber && !isDate ? "'" : "");
        } else if (StringUtils.equalsIgnoreCase(compare, "=") || StringUtils.equalsIgnoreCase(compare, "<>")) {
            if (where.getBoolean("isEnumType")) {
                String[] items = value.split(",");
                List<String> list = new ArrayList<String>();
                for (String v : items) {
                    if (v == " ")
                        list.add((isNumber ? "" : "'") + v + (isNumber ? "" : "'"));
                    else
                        list.add((isNumber ? "" : "'") + processValueForOracleSQL(v) + (isNumber ? "" : "'"));
                }
                result += fieldName + (StringUtils.equalsIgnoreCase(compare, "=") ? " in (" : " not in (") + StringUtils.join(list, ",") + ")";
            } else
                result = fieldName + compare + (isNumber || isDate ? " " : " '") + processValueForOracleSQL(value) + (isNumber || isDate ? "" : "'");
        } else if (StringUtils.equalsIgnoreCase(compare, "like")) {
            result = fieldName + " " + compare + " '%" + StringUtils.replace(processValueForOracleSQL(value), "%", "") + "%'";
        } else if (StringUtils.equalsIgnoreCase(compare, "between")) {
            result = fieldName + " between " + (isNumber || isDate ? "" : "'") + where.getString("v1") + (isNumber || isDate ? "" : "'") + " and " + (isNumber || isDate ? "" : "'") + where.getString("v2") + (isNumber || isDate ? "" : "'");
        } else if (StringUtils.equalsIgnoreCase(compare, "in")) {
            result = fieldName + " in (" + value + ")";
        }
        return result;
    }

    /**
     * 
     * @param orderFields
     * @param layerFields
     * @return
     */
    private String generateSpatialOrderFieldStr(JSONArray orderFields, WebGISLayerFields layerFields) {
        String result = "";
        if (orderFields != null) {
            for (int i = 0; i < orderFields.size(); i++) {
                JSONObject orderField = orderFields.getJSONObject(i);
                String tempStr = layerFields.schemaFieldName2LayerFieldName.get(orderField.getString("fieldName")) + " " + orderField.getString("type");
                if (i == 0)
                    result = tempStr;
                else
                    result += "," + tempStr;
            }
        }
        return result;
    }

    /**
     * 
     * @param filterStr
     * @param layerFields
     * @return
     */
    private String generateSpatialSearchTextWhere(String filterStr, WebGISLayerFields layerFields) {
        if (StringUtils.equals(filterStr, "%"))
            return null;
        String result = "";
        Map<String, Integer> schemaFieldName2DataType = layerFields.schemaFieldName2DataType;
        for (Entry<String, String> entry : layerFields.schemaFieldName2LayerFieldName.entrySet()) {
            String schemaFieldName = entry.getKey();
            if (schemaFieldName2DataType.containsKey(schemaFieldName) && schemaFieldName2DataType.get(schemaFieldName) == 0) {
                String tempStr = entry.getValue() + " like '" + filterStr + "'";
                if (StringUtils.isEmpty(result))
                    result = tempStr;
                else
                    result += " or " + tempStr;
            }
        }
        if (!StringUtils.isEmpty(result))
            result = "(" + result + ")";
        return result;
    }

    /**
     * 
     * @param orderFields
     * @return
     */
    private String generateOrderSQL(JSONArray orderFields, String tableAlias) {
        String result = "";
        if (orderFields != null) {
            for (int i = 0; i < orderFields.size(); i++) {
                JSONObject orderField = orderFields.getJSONObject(i);
                String tempStr = tableAlias + "." + orderField.getString("fieldName") + " " + orderField.getString("type");
                if (i == 0)
                    result = tempStr;
                else
                    result += "," + tempStr;
            }
        }
        return result;
    }

    /**
     * 字符串转化为SQL 避免注入
     * 
     * @param value
     * @return
     */
    private String processValueForOracleSQL(String value) {
        return StringUtils.replace(StringUtils.replace(value, " ", ""), "'", "''");
    }

    /**
     * 加强版识别功能 新版本的功能首先监测缓存是否存在，如果不存在缓存直接从服务器读取
     * 
     * @param con
     * 
     * @param params
     * @return
     * @throws Exception
     * @throws DataException
     * @throws SQLException
     */
    public String identify(String params) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement layerStmt = null;
        PreparedStatement resourceStmt = null;

        JSONObject paramJson = JSONUtils.parserJSONObject(params);
        WKT wkt = new WKT();
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            String geometryType = paramJson.getString("geometryType");
            double resolution = Convert.str2Double(paramJson.getString("resolution"));
            JGeometry geometry = OracleSpatialUtils.arcGISGeometryJson2JGeometry(paramJson.getJSONObject("geometry"), geometryType, resolution, spatialConfig.wkid);
            long userId = paramJson.getLong("userId");
            geometry = WebGISResourceService4Oracle.getInstance().getGeometryInstractDistrictGeometry(con, userId, geometry, spatialConfig.wkid);
            if (geometry != null) {
                geometry.setSRID(spatialConfig.wkid);

                // 计算所有的图层
                List<Long> layerIds = new ArrayList<Long>();
                JSONArray layerArray = paramJson.getJSONArray("layerIds");
                for (int i = 0; i < layerArray.size(); i++) {
                    if (layerArray.getLong(i) != -1)
                        layerIds.add(layerArray.getLong(i));
                }

                JSONArray serverArray = paramJson.getJSONArray("serverIds");
                List<Long> serverIds = new ArrayList<Long>();
                for (int i = 0; i < serverArray.size(); i++) {
                    if (serverArray.getLong(i) != -1)
                        serverIds.add(serverArray.getLong(i));
                }

                if (!serverIds.isEmpty()) {
                    String tempSQL = "select id from webgis_layer where type in (0,1,2) and query_enabled+ident_enabled>0 and equal_layer_as=name and p_id in (" + StringUtils.join(serverIds, ",") + ")";
                    layerStmt = con.prepareStatement(tempSQL);
                    ResultSet layerRs = layerStmt.executeQuery();
                    while (layerRs.next())
                        layerIds.add(layerRs.getLong(1));
                }

                // 获取状态
                String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid,b.proxy_cache from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id in (" + StringUtils.join(layerIds, ",") + ") order by a.type,to_number(a.name)";
                resourceStmt = con.prepareStatement(sql2);
                ResultSet resourceRs = resourceStmt.executeQuery();
                while (resourceRs.next()) {
                    long layerId = resourceRs.getLong(1);
                    long serverId = resourceRs.getLong(7);
                    String layerTitle = resourceRs.getString(6);
                    if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))&&!StringUtils.isEmpty(resourceRs.getString(8))&&resourceRs.getString(8)=="1") {
                        String sql = "select object_caption, object_attributes,geometry shape,q_uid id from spatial_fs_" + layerId + " where SDO_ANYINTERACT(geometry, ?) = 'TRUE'";
                        stmt = con.prepareStatement(sql);
                        stmt.setObject(1, JGeometry.store(geometry, DBHELPER.getNaviteConnection(con)));
                        ResultSet rs = stmt.executeQuery();
                        WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
                        while (rs.next()) {
                            JGeometry shape = JGeometry.load(rs.getBytes("shape"));
                            String wktStr = new String(wkt.fromJGeometry(shape));
                            JSONObject recordJson = new JSONObject();
                            recordJson.put("wkt", GeometryUtils.wkt2ArcGISGeometryJSON(wktStr));
                            JSONObject recordAttribJson = JSONUtils.parserJSONObject(rs.getString("object_attributes"));
                            JSONArray featureAttributes = generateFeatureAttributes(recordAttribJson, layerFields);
                            JSONObject item = new JSONObject();
                            item.put("name", "图层名称");
                            item.put("value", layerTitle);
                            featureAttributes.add(0, item);
                            recordJson.put("attributes", featureAttributes);
                            recordJson.put("layerId", layerId);
                            recordJson.put("serverId", serverId);
                            recordJson.put("uid", rs.getLong("ID"));
                            recordJson.put("caption", generateRecordCaption(recordAttribJson, layerFields));
                            JSONUtils.append(json, "records", recordJson);
                        }
                        stmt.close();
                        stmt = null;
                    } else { // 从地图服务资源获取
                        String mapSRID = paramJson.getJSONObject("geometry").getJSONObject("spatialReference").getString("wkid");
                        double x = paramJson.getJSONObject("geometry").getDouble("x");
                        double y = paramJson.getJSONObject("geometry").getDouble("y");
                        String geometryFilterStr = "{\"rings\":[[";
                        geometryFilterStr += "[" + (x - resolution) + "," + (y - resolution) + ",],";
                        geometryFilterStr += "[" + (x + resolution) + "," + (y - resolution) + ",],";
                        geometryFilterStr += "[" + (x + resolution) + "," + (y + resolution) + ",],";
                        geometryFilterStr += "[" + (x - resolution) + "," + (y + resolution) + ",],";
                        geometryFilterStr += "[" + (x - resolution) + "," + (y - resolution) + ",]";
                        geometryFilterStr += "]]}";
                        String url = resourceRs.getString(2);
                        JSONObject queryResultJSON = ArcGISServiceRestService.query(url, null, geometryFilterStr, null, mapSRID, true, true, null);
                        if (queryResultJSON == null)
                            continue;
                        JSONArray featureArray = queryResultJSON.getJSONArray("features");
                        if (featureArray == null || featureArray.isEmpty())
                            continue;

                        WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
                        for (int j = 0; j < featureArray.size(); j++) {
                            JSONObject featureJSON = featureArray.getJSONObject(j);
                            JSONObject recordJson = new JSONObject();
                            recordJson.put("wkt", featureJSON.getJSONObject("geometry"));
                            JSONObject attributeJSON = featureJSON.getJSONObject("attributes");

                            JSONArray featureAttributes = generateFeatureAttributes(attributeJSON, layerFields);
                            JSONObject item = new JSONObject();
                            item.put("name", "图层名称");
                            item.put("value", layerTitle);
                            featureAttributes.add(0, item);
                            recordJson.put("attributes", featureAttributes);
                            recordJson.put("layerId", layerId);
                            recordJson.put("serverId", resourceRs.getLong(5));
                            recordJson.put("caption", generateRecordCaption(attributeJSON, layerFields));
                            recordJson.put("uid", attributeJSON.getString(layerFields.objectFieldName));
                            JSONUtils.append(json, "records", recordJson);
                        }
                    }
                }
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeStatement(layerStmt);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }
    /**
     * 获取交通流量中所选路线对应表格名
     * 
     * 
     */
	public String getTrafficFlowTableName(String lineName,String startMileage)throws Exception
    {
    	Connection con=null;
    	PreparedStatement stmt =null;
    	
    	JSONArray arr = new JSONArray();//存放下拉框数据源
    	String dbName=null; //存放具体一个表格名称
    	String lineColumnName = null; //存放路线编号的列名
    	String startMileageName=null;
    	String tableName =null;//存放表格中文名
    	String id=null;//表格ID
    	try
    	{ 
    		con=DBHELPER.getConnection();
    		String sql="select SCHEMA_TABLE_NAME,CAPTION,ID from WEBGIS_DYNAMIC_SERVICE where IS_VALID='1'"; //获取所有可能相关表格名
    		stmt=con.prepareStatement(sql);
    		ResultSet rs = stmt.executeQuery();
    		while(rs.next()) //遍历每一个表格
    		{
    			PreparedStatement stmt2=null;
    			dbName=rs.getString(1);
    			tableName=rs.getString(2);
    			id=rs.getString(3);
    			lineColumnName=getTrafficFlowLineColumnName(id,con);
    			startMileageName=getTrafficFlowStartMileageName(id,con);
    			if(lineColumnName!=null&&dbName!=null)
    			{
    				if(startMileageName==null)
    				{
    					String sql2 = "select * from "+dbName+" where instr('"+lineName+"',"+lineColumnName+")>0"; 	//获取该表对应线路的数据   	
    					//System.out.println(sql2);
            			stmt2=con.prepareStatement(sql2);
            			ResultSet rs2 = stmt2.executeQuery();
            			if(rs2.next())
            			{
            				arr.add(tableName);
            			}        		
    				}
    				else
    				{
    					String sql2="select * from "+dbName+" where instr('"+lineName+"',"+lineColumnName+")>0 and substr("+startMileageName+",2)>"+startMileage+" order by "+startMileageName;
    					//System.out.println(sql2);
    					stmt2=con.prepareStatement(sql2);
            			ResultSet rs2 = stmt2.executeQuery();
            			if(rs2.next())
            			{
            				arr.add(tableName);
            			}        		
    				}
    			}
        		DBHELPER.closeStatement(stmt2);
    		}
    	}finally
    	{
    		
    		DBHELPER.closeStatement(stmt);
    		DBHELPER.closeConnection(con);
    	}
    	String str=arr.toString();
    	str=str.replace("[","");
    	str=str.replace("]","");
    	str=str.replace("\"","" );
    	return str;
    }
/**
 * 获取交通流量数据
 * 
 * 
 */
    public JSONObject getTrafficFlowData(String startMileage,String lineName,String tableName,String id) throws Exception
    {
    	Connection con = null;
    	PreparedStatement stmt=null;
    	PreparedStatement stmt1=null;
    	PreparedStatement stmt2=null;
    	PreparedStatement stmt3=null;
    	PreparedStatement stmt4=null;
    	JSONObject json=new JSONObject();
    	String lineColumnName=null;
    	String startMileageName=null;
    	try
    	{
    		con=DBHELPER.getConnection();

    		//获得列标注数组
    		String sql1="select column_name from user_tab_columns where table_name='"+tableName+"' and column_name!='GEOMETRY' order by COLUMN_ID";
    		stmt1=con.prepareStatement(sql1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
    		ResultSet rs1=stmt1.executeQuery();
    		rs1.last();
    		int row=0;
    		int jj=0;
    		row=rs1.getRow();
    		String[]strarr=new String[row];
    		stmt4=con.prepareStatement(sql1);
    		ResultSet rs4=stmt4.executeQuery();
    		while(rs4.next())
    		{
    			strarr[jj]=rs4.getString(1);
    			jj++;
    		}
    		
    		
    		//获取父表
    		String pID=null;
    		String ptableName=null;
    		String sql2="select P_ID,ID from WEBGIS_DYNAMIC_SERVICE where SCHEMA_TABLE_NAME='"+tableName+"' ";
    		stmt2=con.prepareStatement(sql2);
    		ResultSet rs2=stmt2.executeQuery();
    		if(rs2.next())
    		{
    			pID=rs2.getString(1);
    		}
    		String sql3="select CAPTION from WEBGIS_DYNAMIC_SERVER_CATEGORY where ID='"+pID+"'";
    		stmt3=con.prepareStatement(sql3);
    		ResultSet rs3=stmt3.executeQuery();
    		if(rs3.next())
    		{
    			ptableName=rs3.getString(1);
    		} 		
    		json.put("F0", ptableName);
    		
    		
    		lineColumnName=getTrafficFlowLineColumnName(id,con);
    		startMileageName=getTrafficFlowStartMileageName(id,con);
    		String sql="";
    		if(startMileageName==null)
    		{
    			sql="select * from "+tableName+"  where instr('"+lineName+"',"+lineColumnName+")>0";
    		}
    		else
    		{
    			sql="select * from "+tableName+" where instr('"+lineName+"',"+lineColumnName+")>0 and substr("+startMileageName+",2)>"+startMileage+" order by "+startMileageName;  			
    		}
    		stmt=con.prepareStatement(sql);
    		ResultSet rs=stmt.executeQuery();
    		if(rs.next())
    		{
    			for(int ii=0;ii<strarr.length;ii++)
    			{
    				json.put(strarr[ii], rs.getString(ii+1));
    			}
    		}
    	}finally
    	{
    		DBHELPER.closeStatement(stmt4);
    		DBHELPER.closeStatement(stmt3);
    		DBHELPER.closeStatement(stmt2);
    		DBHELPER.closeStatement(stmt1);
    		DBHELPER.closeStatement(stmt);
    		DBHELPER.closeConnection(con);
    	}
    	//System.out.println("一行表格数据"+json.toString());
    	return json;
    }
    public String getTrafficFlowGridHeaderData(String selectedTable)throws Exception
    {
    	Connection con=null;
    	PreparedStatement stmt=null;
    	PreparedStatement stmt1=null;
    	JSONArray arr=new JSONArray();
    	String tableName=null;
    	try{
    		con=DBHELPER.getConnection();
    		
    		String sql1="select schema_table_name from WEBGIS_DYNAMIC_SERVICE where CAPTION ='"+selectedTable+"'";
    		stmt1=con.prepareStatement(sql1);
    		ResultSet rs1=stmt1.executeQuery();
    		if(rs1.next())
    		{
    			tableName=rs1.getString(1);
    		}
    		
    		
    		String sql ="select COLUMN_NAME,COMMENTS from user_col_comments where table_name='"+tableName+"' and COLUMN_NAME like 'F%'";               //获取该交通流量表中列名对应的标注
    		stmt=con.prepareStatement(sql);
    		ResultSet rs=stmt.executeQuery();
    		JSONObject json=new JSONObject();
    		json.put("datafield","F0");
    		json.put("headertext","主表");
    		arr.add(json);
    		while(rs.next())
    		{
    			JSONObject json1=new JSONObject();
    			json1.put("datafield",rs.getString(1));
    			json1.put("headertext", rs.getString(2));
    			arr.add(json1);
    		}
    	}finally
    	{
    		DBHELPER.closeStatement(stmt);
    		DBHELPER.closeStatement(stmt1);
    		DBHELPER.closeConnection(con);
    	}
    	return arr.toString();   			
    }

    public String getTrafficFlowLineColumnName(String id,Connection con) throws Exception
    {
    	PreparedStatement stmt=null;
    	String lineColumnName=null;
    	String sql="select  SCHEMA_FIELD_NAME from WEBGIS_DYNAMIC_FIELD where (field_comment ='线路编号' or  field_comment ='路线编号') and p_id =  '"+id+"'";
    	stmt=con.prepareStatement(sql);
    	ResultSet rs=stmt.executeQuery();
    	if(rs.next())
    		lineColumnName=rs.getString(1);
    	return lineColumnName;
    }
    public String getTrafficFlowStartMileageName(String id,Connection con) throws Exception
    {
    	PreparedStatement stmt=null;
    	String startMileageName=null;
    	String sql="select  SCHEMA_FIELD_NAME from WEBGIS_DYNAMIC_FIELD where (field_comment ='起点桩号' or  field_comment ='起始桩号') and p_id =  '"+id+"'";
    	stmt=con.prepareStatement(sql);
    	ResultSet rs=stmt.executeQuery();
    	if(rs.next())
    		startMileageName=rs.getString(1);
    	return startMileageName;
    }
    public String getTrafficFlowDatas(String startMileage,String lineName,String selectedTable) throws Exception
    {
    	Connection con=null;
    	PreparedStatement stmt=null;
    	String tableName=null;
    	JSONArray arr=new JSONArray();
    	String id=null;
    	try
    	{
    		con=DBHELPER.getConnection();
    		String sql="select schema_table_name,id from WEBGIS_DYNAMIC_SERVICE where CAPTION ='"+selectedTable+"'";
    		stmt=con.prepareStatement(sql);
    		ResultSet rs=stmt.executeQuery();
    		while(rs.next())
    		{
    			id=rs.getString(2);
    			tableName=rs.getString(1);
    			JSONObject json=new JSONObject();
    			json=getTrafficFlowData(startMileage, lineName, tableName,id);
    			arr.add(json);
    		}
    	}finally
    	{
    		DBHELPER.closeStatement(stmt);
    		DBHELPER.closeConnection(con);
    	}
    	//System.out.println("表格所有数据"+arr.toString());
    	return arr.toString();
    }
    /**
     * 生成记录查询或点选的标题
     * 
     * @param attributeJSON
     * @param layerFields
     * @return
     */
    private Object generateRecordCaption(JSONObject attributeJSON, WebGISLayerFields layerFields) {
        String result = layerFields.captionTemplate;
        for (Entry<String, String> entry : layerFields.captionFieldMap.entrySet())
            result = StringUtils.replace(result, "{" + entry.getKey() + "}", attributeJSON.getString(entry.getValue()));
        return result;
    }

    /**
     * 根据object_id获得一个具体的对象
     * 
     * @param object_id
     * @return
     * @throws Exception
     */
    public String getFeatureProperty(long layerId, long uniqueId) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();

            String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id = " + layerId;
            resourceStmt = con.prepareStatement(sql2);
            ResultSet resourceRs = resourceStmt.executeQuery();
            resourceRs.next();

            if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))) { // 从数据库读取
                String sql = "select q_uid id,q_objectid object_id,object_caption, object_attributes,geometry shape from spatial_fs_" + layerId + " where q_uid=?";
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, uniqueId);
                ResultSet rs = stmt.executeQuery();
                WKT wkt = new WKT();
                if (rs.next()) {
                    JGeometry shape = JGeometry.load(rs.getBytes("shape"));
                    String wktStr = new String(wkt.fromJGeometry(shape));
                    JSONObject recordJson = new JSONObject();
                    recordJson.put("wkt", GeometryUtils.wkt2ArcGISGeometryJSON(wktStr));
                    recordJson.put("layerId", layerId);
                    recordJson.put("serverId", resourceRs.getLong("serverid"));
                    json.put("record", recordJson);
                }
            } else {
                JSONObject queryResultJson = ArcGISServiceRestService.query(resourceRs.getString(2), null, null, uniqueId + "", spatialConfig.wkid + "", true, false, null);
                if (queryResultJson != null) {
                    JSONArray featuresArray = queryResultJson.getJSONArray("features");
                    if (featuresArray != null && !featuresArray.isEmpty()) {
                        JSONObject featureJson = featuresArray.getJSONObject(0);
                        JSONObject recordJson = new JSONObject();
                        recordJson.put("wkt", featureJson.getJSONObject("geometry"));
                        recordJson.put("layerId", layerId);
                        recordJson.put("serverId", resourceRs.getLong("serverid"));
                        json.put("record", recordJson);
                    }
                }
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeConnection(con);
        }
        json.put("r", true);
        return json.toString();
    }

    /**
     * 获取多个实体的属性
     * 
     * @param ids
     * @return
     * @throws Exception
     */
    public String getFeaturesProperty(long layerId, String ids) throws Exception {
        Connection con = null;
        PreparedStatement layerStmt = null;
        PreparedStatement tempStmt = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        String tempTableName = "TMP_" + System.currentTimeMillis();
        try {
            con = DBHELPER.getConnection();

            String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id = " + layerId;
            resourceStmt = con.prepareStatement(sql2);
            ResultSet resourceRs = resourceStmt.executeQuery();
            resourceRs.next();

            if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))) { // 从数据库中读取数据
                DBHELPER.execute("CREATE GLOBAL TEMPORARY Table " + tempTableName + "(id number) ON COMMIT PRESERVE ROWS", con);
                tempStmt = con.prepareStatement("insert into " + tempTableName + "(id) values (?)");
                for (String str : ids.split(",")) {
                    tempStmt.setLong(1, Convert.str2Long(str));
                    tempStmt.addBatch();
                }
                tempStmt.executeBatch();
                tempStmt.clearBatch();

                // 50米精度
                WKT wkt = new WKT();
                String layerSQL = "select geometry shape from spatial_fs_" + layerId + " where exists (select 'x' from " + tempTableName + " where id=q_uid)";
                layerStmt = con.prepareStatement(layerSQL);
                ResultSet tempRs = layerStmt.executeQuery();
                while (tempRs.next()) {
                    JGeometry shape = JGeometry.load(tempRs.getBytes("shape"));
                    String wktStr = new String(wkt.fromJGeometry(shape));
                    JSONObject recordJson = new JSONObject();
                    recordJson.put("wkt", GeometryUtils.wkt2ArcGISGeometryJSON(wktStr));
                    JSONUtils.append(json, "records", recordJson);
                }
                layerStmt.close();
                layerStmt = null;
                DBHELPER.execute("truncate table " + tempTableName, con);
                DBHELPER.execute("drop TABLE " + tempTableName + " PURGE", con);
            } else { // 从缓存中读取数据
                JSONObject queryJson = ArcGISServiceRestService.query(resourceRs.getString(2), null, null, ids, spatialConfig.wkid + "", true, false, null);
                if (queryJson != null) {
                    JSONArray featuresArray = queryJson.getJSONArray("features");
                    if (featuresArray != null && !featuresArray.isEmpty()) {
                        for (int m = 0; m < featuresArray.size(); m++) {
                            JSONObject featureJson = featuresArray.getJSONObject(m);
                            JSONObject recordJson = new JSONObject();
                            recordJson.put("wkt", featureJson.getJSONObject("geometry"));
                            JSONUtils.append(json, "records", recordJson);
                        }
                    }
                }
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeStatement(tempStmt);
            DBHELPER.closeStatement(layerStmt);
            DBHELPER.closeConnection(con);
        }
        json.put("r", true);
        return json.toString();
    }

    /**
     * 
     * @param con
     * @param array
     * @param layerId
     * @return
     * @throws Exception
     */
    private JSONArray generateFeatureAttributes(JSONObject record, WebGISLayerFields layerFields) throws Exception {
        JSONArray result = new JSONArray();
        for (int i = 0; i < layerFields.fieldList.size(); i++) {
            String fieldName = layerFields.fieldList.get(i);
            String value = record.getString(fieldName);
            if (StringUtils.equalsIgnoreCase(value, "null"))
                value = "";
            JSONObject item = new JSONObject();
            item.put("name", layerFields.fieldComment.get(i));
            item.put("value", value);
            result.add(item);
        }
        return result;
    }

    /**
     * 查询
     * 
     * @param con
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String search(String params) throws Exception {
        JSONObject paramJson = JSONUtils.parserJSONObject(params);
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement layerStmt = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            JGeometry geometry = null;
            if (!StringUtils.equals("-1", paramJson.getString("geometryUid"))) // 包含行政区域
                geometry = WebGISResourceService4Oracle.getInstance().getDistrictGeometry(con, paramJson.getLong("geometryUid"));
            else
                geometry = OracleSpatialUtils.arcGISGeometryJson2JGeometry(paramJson.getJSONObject("geometry"), paramJson.getString("geometryType"), 0, spatialConfig.wkid);

            long userId = paramJson.getLong("userId");
            geometry = WebGISResourceService4Oracle.getInstance().getGeometryInstractDistrictGeometry(con, userId, geometry, spatialConfig.wkid);

            if (geometry != null) {
                geometry.setSRID(spatialConfig.wkid);
                String filterStr = paramJson.getString("text");
                String filterSQL = generateFilterSQLCondition(filterStr);
                String querySQL = "select q_uid id, q_objectid object_id, object_caption, geometry simp_geometry,object_attributes from spatial_fs_layerid where SDO_ANYINTERACT(spatial_fs_layerid.geometry,?) = 'TRUE' ";
                if (!StringUtils.isEmpty(filterSQL))
                    querySQL += " and " + filterSQL + " and rownum<=" + MAX_RECORD;

                JSONArray layerArray = paramJson.getJSONArray("layerIds");
                List<Long> layerIds = new ArrayList<Long>();
                for (int i = 0; i < layerArray.size(); i++) {
                    if (layerArray.getLong(i) <= 0)
                        continue;
                    layerIds.add(layerArray.getLong(i));
                }

                JSONArray serverArray = paramJson.getJSONArray("serverIds");
                List<Long> serverIds = new ArrayList<Long>();
                for (int i = 0; i < serverArray.size(); i++) {
                    if (serverArray.getLong(i) <= 0)
                        continue;
                    serverIds.add(serverArray.getLong(i));
                }

                if (!serverIds.isEmpty()) {
                    layerStmt = con.prepareStatement("select id from webgis_layer a where a.p_id in (" + StringUtils.join(serverIds, ",") + ")");
                    ResultSet rs1 = layerStmt.executeQuery();
                    while (rs1.next())
                        layerIds.add(rs1.getLong(1));
                }

                int recordNumber = 0;

                String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid,(select name from webgis_layer_fields where data_type=4 and p_id=a.id) as objectfieldname from user_tables c,webgis_services b,webgis_layer a where a.name=a.equal_layer_as and a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.id in ("
                        + StringUtils.join(layerIds, ",") + ") order by a.type,to_number(a.name)";
                resourceStmt = con.prepareStatement(sql2);
                ResultSet resourceRs = resourceStmt.executeQuery();
                List<Long> validLayerList = new ArrayList<Long>();
                while (resourceRs.next()) {
                    boolean existsRecord = false;
                    long layerId = resourceRs.getLong(1);
                    WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
                    if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))) { // 已经缓存过数据的查询数据库表
                        String layerSQL = StringUtils.replace(querySQL, "spatial_fs_layerid", "spatial_fs_" + layerId);
                        stmt = con.prepareStatement(layerSQL);
                        stmt.setObject(1, JGeometry.store(geometry, DBHELPER.getNaviteConnection(con)));
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next() && recordNumber < MAX_RECORD) {
                            JSONObject recordJson = new JSONObject();
                            recordJson.put("id", rs.getString("id"));
                            recordJson.put("objectId", rs.getString("object_id"));
                            recordJson.put("label", generateRecordCaption(JSONUtils.parserJSONObject(rs.getString("object_attributes")), layerFields));
                            recordJson.put("layerId", layerId + "");
                            JSONUtils.append(json, "records", recordJson);
                            recordNumber++;
                            existsRecord = true;
                        }
                        if (existsRecord)
                            validLayerList.add(layerId);
                    } else { // 没有缓存过的查询地图服务
                        String url = resourceRs.getString(2);
                        filterStr = "%" + StringUtils.replaceEach(filterStr, new String[] { " ", "　" }, new String[] { "%", "%" }) + "%";
                        while (filterStr.contains("%%"))
                            filterStr = StringUtils.replace(filterStr, "%%", "%");

                        JSONObject queryResultJSON = ArcGISServiceRestService.query(url, generateSpatialSearchTextWhere(filterStr, layerFields), OracleSpatialUtils.toArcGISJson(geometry), null, spatialConfig.wkid + "", false, true, null);//
                        if (queryResultJSON == null)
                            continue;
                        JSONArray featureArray = queryResultJSON.getJSONArray("features");
                        if (featureArray == null || featureArray.isEmpty())
                            continue;
                        for (int m = 0; m < featureArray.size() && recordNumber++ < MAX_RECORD; m++) {
                            JSONObject featureJson = featureArray.getJSONObject(m);
                            JSONObject attributeJSON = featureJson.getJSONObject("attributes");
                            long featureObjectId = attributeJSON.getLong(layerFields.objectFieldName);
                            JSONObject recordJson = new JSONObject();
                            recordJson.put("id", featureObjectId);
                            recordJson.put("objectId", featureObjectId);
                            recordJson.put("label", generateRecordCaption(attributeJSON, layerFields));
                            recordJson.put("layerId", layerId + "");
                            JSONUtils.append(json, "records", recordJson);
                            if (m == 0)
                                validLayerList.add(layerId);
                        }
                    }
                }
                json.put("layerInfos", generateSearchResultLayerInfos(con, validLayerList));
                json.put("isSearchResult", true);
                json.put("r", true);
            }
        } finally {

            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(layerStmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 生成查询结果的图层信息
     * 
     * @param con
     * @param layerIds
     * @param layerId2MBR
     * @param layerId2CacheStatus
     * @return
     * @throws SQLException
     */
    private Map<String, JSONObject> generateSearchResultLayerInfos(Connection con, List<Long> layerIds) throws SQLException {
        Map<String, JSONObject> layerInfos = new HashMap<String, JSONObject>();
        PreparedStatement stmt = null;
        if (!layerIds.isEmpty()) {
            try {
                String sql = "select a.id layerId,a.p_id serverId,nvl(a.layer_comment,a.title) layerTitle,b.SCHEMA_FIELD_NAME as name,(select wm_concat(name) from webgis_layer where (p_id,title,equal_layer_as) in (select p_id,title,equal_layer_as from webgis_layer where id=a.id)), a.type from webgis_layer_fields b,webgis_layer a where a.id = b.p_id and b.data_type=4 and a.id in ("
                        + StringUtils.join(layerIds, ",") + ")";
                stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    JSONObject layerInfo = new JSONObject();
                    layerInfo.put("serverId", rs.getLong(2));
                    layerInfo.put("title", rs.getString(3));
                    layerInfo.put("objectFieldName", rs.getString(4));
                    layerInfo.put("layers", rs.getString(5));
                    layerInfo.put("type", rs.getInt(6));
                    layerInfos.put(rs.getString(1), layerInfo);
                }
            } finally {
                DBHELPER.closeStatement(stmt);
            }
        }
        return layerInfos;
    }

    /**
     * 空间查询
     * 
     * @param con
     * @param params
     * @return
     * @throws Exception
     */
    public String spatialSearch(String params) throws Exception {
        JSONObject paramJson = JSONUtils.parserJSONObject(params);
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            String filterStr = paramJson.getString("filterStr");
            String filterSQL = generateFilterSQLCondition(filterStr);
            String geometryType = paramJson.getString("geometryType");
            JGeometry geometry = OracleSpatialUtils.arcGISGeometryJson2JGeometry(paramJson.getJSONObject("geometry"), geometryType, 0, spatialConfig.wkid);
            String sql = "select q_uid id, q_objectid object_id,object_caption, geometry simp_geometry,object_attributes from spatial_fs_layerid where ";
            if (!StringUtils.isEmpty(filterSQL))
                sql += filterSQL + " and ";
            sql += " SDO_ANYINTERACT(spatial_fs_layerid.geometry,?) = 'TRUE'";

            JSONArray layerArray = paramJson.getJSONArray("layerIds");
            List<Long> layerIds = new ArrayList<Long>();
            for (int i = 0; i < layerArray.size(); i++) {
                if (layerArray.getLong(i) <= 0)
                    continue;
                layerIds.add(layerArray.getLong(i));
            }

            JSONArray serverArray = paramJson.getJSONArray("serverIds");
            List<Long> serverIds = new ArrayList<Long>();
            for (int i = 0; i < serverArray.size(); i++) {
                if (serverArray.getLong(i) <= 0)
                    continue;
                serverIds.add(serverArray.getLong(i));
            }

            if (!serverIds.isEmpty()) {
                stmt1 = con.prepareStatement("select id from webgis_layer a where exists (select 'x' from user_tables where table_name = 'SPATIAL_FS_'||a.id) and name=a.equal_layer_as and download_status=1 and a.p_id in (" + StringUtils.join(serverIds, ",") + ")");
                ResultSet rs1 = stmt1.executeQuery();
                while (rs1.next())
                    layerIds.add(rs1.getLong(1));
            }

            int recordCount = 0;
            String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid,(select name from webgis_layer_fields where data_type=4 and p_id=a.id) as objectfieldname from user_tables c,webgis_services b,webgis_layer a where a.name=a.equal_layer_as and a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.id in ("
                    + StringUtils.join(layerIds, ",") + ") order by a.type,to_number(a.name)";
            resourceStmt = con.prepareStatement(sql2);
            ResultSet resourceRs = resourceStmt.executeQuery();
            List<Long> validLayerList = new ArrayList<Long>();
            while (resourceRs.next()) {
                boolean existsRecord = false;
                long layerId = resourceRs.getLong(1);
                WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
                if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))) { // 已经缓存过数据的查询数据库表
                    String layerSQL = StringUtils.replace(sql, "spatial_fs_layerid", "spatial_fs_" + layerId);
                    stmt = con.prepareStatement(layerSQL);
                    stmt.setObject(1, JGeometry.store(geometry, DBHELPER.getNaviteConnection(con)));
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next() && recordCount < MAX_RECORD) {
                        JSONObject recordJson = new JSONObject();
                        recordJson.put("id", rs.getString("id"));
                        recordJson.put("label", generateRecordCaption(JSONUtils.parserJSONObject(rs.getString("object_attributes")), layerFields));
                        recordJson.put("layerId", layerId);
                        recordJson.put("objectId", rs.getString("object_id"));
                        JGeometry shape = JGeometry.load(rs.getBytes("simp_geometry"));
                        recordJson.put("geometry", GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(shape))));
                        JSONUtils.append(json, "records", recordJson);
                        recordCount++;
                        existsRecord = true;
                    }
                    stmt.close();
                    stmt = null;
                    if (existsRecord)
                        validLayerList.add(layerId);
                } else { // 空间查询数据
                    String url = resourceRs.getString(2);
                    filterStr = "%" + StringUtils.replaceEach(filterStr, new String[] { " ", "　" }, new String[] { "%", "%" }) + "%";
                    while (filterStr.contains("%%"))
                        filterStr = StringUtils.replace(filterStr, "%%", "%");

                    JSONObject queryResultJSON = ArcGISServiceRestService.query(url, generateSpatialSearchTextWhere(filterStr, layerFields), OracleSpatialUtils.toArcGISJson(geometry), null, spatialConfig.wkid + "", false, true, null);//
                    if (queryResultJSON == null)
                        continue;
                    JSONArray featureArray = queryResultJSON.getJSONArray("features");
                    if (featureArray == null || featureArray.isEmpty())
                        continue;
                    String objectIdFieldName = resourceRs.getString("objectfieldname");
                    for (int m = 0; m < featureArray.size() && recordCount++ < MAX_RECORD; m++) {
                        JSONObject featureJson = featureArray.getJSONObject(m);
                        long featureObjectId = featureJson.getJSONObject("attributes").getLong(objectIdFieldName);
                        JSONObject recordJson = new JSONObject();
                        recordJson.put("id", featureObjectId);
                        recordJson.put("objectId", featureObjectId);
                        recordJson.put("label", generateRecordCaption(featureJson.getJSONObject("attributes"), layerFields));
                        recordJson.put("layerId", layerId + "");
                        JSONUtils.append(json, "records", recordJson);
                        if (m == 0)
                            validLayerList.add(layerId);
                    }
                }
            }
            json.put("layerInfos", generateSearchResultLayerInfos(con, validLayerList));
            json.put("isSearchResult", true);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeStatement(stmt1);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }
    
    /**
     * 空间查询
     * 
     * @param con
     * @param params
     * @return
     * @throws Exception
     */
    public String spatialSearchByGJXL(String params) throws Exception {
        JSONObject paramJson = JSONUtils.parserJSONObject(params);
//        System.out.println("paramJson="+paramJson);
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement stmtzd = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try{
        	String sql4XLLayer="select TJZD,xlmc from spatial_fs_layerid t where t.q_uid=?";
        	String recordIdentify=paramJson.getString("recordIdentify");
        	JSONObject recordIdentifyJSON = JSONUtils.parserJSONObject(recordIdentify);
        	String recordString=recordIdentifyJSON.getString("records");
        	JSONArray recordJSONArray =  JSONUtils.parserJSONArray(recordString);
        	JSONObject recordJSONObject=JSONUtils.parserJSONObject(recordJSONArray.getString(0));
        	String layerId=recordJSONObject.getString("layerId");
        	String u_id=recordJSONObject.getString("uid");
        	sql4XLLayer=StringUtils.replace(sql4XLLayer, "spatial_fs_layerid", "spatial_fs_" + layerId);
        	JSONArray layerIds=paramJson.getJSONArray("layerIds"); 
        	
        	String layerid4zd="";
        	for(int j=0;j<layerIds.size();j++){
        	 	String arrayLayers=layerIds.get(j)+"";
        	 	if(arrayLayers!=layerId){
        	 		layerid4zd=arrayLayers;
        	 		break;
        	 	}
        	}
        	con=DBHELPER.getConnection();
        	stmt=con.prepareStatement(sql4XLLayer);
        	stmt.setString(1, u_id);
        	ResultSet rs4XLLayer=stmt.executeQuery();
        	while(rs4XLLayer.next()){
        		String tjzd=rs4XLLayer.getString("TJZD");
        		String xlmc=rs4XLLayer.getString("xlmc");
        		String[] tjzds=tjzd.split("、");
        		for(int i=0;i<tjzds.length;i++){
        			String sql4ZD="select t.geometry simp_geometry from spatial_fs_layerid t where t.zdmc ='"+tjzds[i]+"'";
        			if(!StringUtils.isEmpty(xlmc)){
        				sql4ZD+=" and t.tjxl LIKE '%"+xlmc+"%'";
        			}
            	 	sql4ZD=StringUtils.replace(sql4ZD, "spatial_fs_layerid", "spatial_fs_" + layerid4zd);
            	 	stmtzd=con.prepareStatement(sql4ZD);
            	 	ResultSet rs4ZD=stmtzd.executeQuery();
            	 	while(rs4ZD.next()){
            	 		JSONObject recordJson = new JSONObject();
            	 		JGeometry shape = JGeometry.load(rs4ZD.getBytes("simp_geometry"));
                        recordJson.put("geometry", GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(shape))));
                        JSONUtils.append(json, "records", recordJson);
            	 	}
        		}
        	}
        	json.put("r", true);
        }finally{
        	DBHELPER.closeStatement(stmt);
        	DBHELPER.closeConnection(con);
        }
        return json.toString();
    }
    
    /**
     * 桩号定位
     * 
     * @param con
     * @param params
     * @return
     * @throws Exception
     */
    public String projectMileage(String params) throws Exception {
        JSONObject paramJson = JSONUtils.parserJSONObject(params);
        JSONArray paramMileArray=JSONUtils.parserJSONArray(paramJson.getString("mileArr"));
        Connection con = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try{
        	con=DBHELPER.getConnection();

        	String resultgeo= ClientWebService4LineRef.getInstance().getMileage(con,paramMileArray);
        	json.put("resultgeo", resultgeo);
        	json.put("r", true);
        }finally{
        	DBHELPER.closeConnection(con);
        }
        return json.toString();
    }
    
    private JSONArray getSdeLayerAndField(Connection con) throws Exception{
    	JSONArray layerAndField=new JSONArray();
    	PreparedStatement stmt=null;
    	try{
    		String sql="select a.caption LAYER_NAME,b.field_name FIELD_NAME from webgis_dynamic_route_layer a, webgis_dynamic_route_field b where a.id = b.p_id and is_valid=1 and b.is_xlbh=1";
    		layerAndField=DBHELPER.executeQuery(sql, con);
    	}finally{
    		DBHELPER.closeStatement(stmt);
    	}
    	return layerAndField;
    }
    
    /**
     * 生成过滤条件
     * 
     * @param filterStr
     * @return
     */
    private String generateFilterSQLCondition(String filterStr) {
        if (StringUtils.isEmpty(filterStr))
            return "";
        while (StringUtils.contains(filterStr, "  "))
            filterStr = StringUtils.replace(filterStr, "  ", " ");
        String[] ss = filterStr.split(" ");
        String result = "";
        for (String s : ss) {
            s = StringUtils.replace(s, "'", "''");
            if (StringUtils.isEmpty(result))
                result = "( (spatial_fs_layerid.search_caption like '%" + s + "%')";
            else
                result += " and (spatial_fs_layerid.search_caption like '%" + s + "%')";
        }
        result += ")";
        return result;
    }

    /**
     * 查询图层数据
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String getLayerFeatures(String params) throws Exception {
        JSONObject paramJson = JSONUtils.parserJSONObject(params);
        return getLayerFeatures(paramJson.getLong("layerId"), paramJson.getInt("page"), paramJson.getJSONArray("where"), paramJson.getJSONArray("order"));
    }

    /**
     * 获取记录
     * 
     * @param con
     * @param string
     * @param int1
     * @param jsonObject
     * @throws Exception
     */
    private String getLayerFeatures(long layerId, long page, JSONArray whereParams, JSONArray orderFields) throws Exception {
        Connection con = null;
        IDBCommand cmd = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        List<String> tempTables = new ArrayList<String>();
        try {
            con = DBHELPER.getConnection();
            String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id = " + layerId;
            resourceStmt = con.prepareStatement(sql2);
            ResultSet resourceRs = resourceStmt.executeQuery();
            boolean isExistsLayer = resourceRs.next();

            if (isExistsLayer) {
                int recordPerPage = 1000;
                if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))) { // 从数据库读取
                    String sql = (String) DBHELPER.executeScalar("select wm_concat(decode(DATA_TYPE,'DATE','to_char('||column_name||',''YYYY-MM-DD HH24:MI:SS'')' || ' ' || column_name, column_name)) from user_tab_cols where hidden_column<>'YES' and column_name not in ('GEOMETRY','GEOMETRY_LENGTH','GEOMETRY_AREA','OBJECT_CAPTION','SEARCH_CAPTION','OBJECT_ATTRIBUTES') and table_name='SPATIAL_FS_"
                            + layerId + "'", con);
                    sql = "select " + sql + " from " + "SPATIAL_FS_" + layerId + " t";
                    if (whereParams != null && whereParams.size() != 0) {
                        String whereStr = generalSQLWhere(con, whereParams, tempTables, "t");
                        if (!StringUtils.isEmpty(whereStr))
                            sql += " where " + whereStr;
                        recordPerPage = MAX_RECORD;
                    }
                    String orderFieldSql = generateOrderSQL(orderFields, "t");
                    if (!StringUtils.isEmpty(orderFieldSql))
                        sql += " order by " + orderFieldSql;

                    String countSQL = DBHELPER.getSQLTrans().transCountSQL4Page(sql);
                    long totalRecord = Convert.obj2Long(DBHELPER.executeScalar(countSQL, con), 1);
                    if (page < 1)
                        page = 1;
                    long totalPage = totalRecord % recordPerPage == 0 ? totalRecord / recordPerPage : totalRecord / recordPerPage + 1;
                    if (page > totalPage)
                        page = totalPage;
                    sql = DBHELPER.getSQLTrans().transSQL4Page(sql);
                    cmd = DBHELPER.getCommand(con, sql);
                    cmd.setParam("B", new Long(((page - 1) * recordPerPage)));
                    cmd.setParam("R", new Long(page * recordPerPage));
                    json.put("totalRecord", totalRecord);
                    json.put("page", page);
                    json.put("totalPage", totalPage);
                    json.put("recordPerPage", recordPerPage);
                    cmd.executeQuery();
                    while (cmd.next())
                        JSONUtils.append(json, "records", DBHELPER.cmdRecord2Json(cmd));
                    json.put("r", true);
                    for (String tableName : tempTables) {
                        DBHELPER.execute("truncate table " + tableName, con);
                        DBHELPER.execute("drop table " + tableName + " PURGE", con);
                    }
                } else { // 从ArcGIS服务器读取
                    long totalRecord = 0;
                    long totalPage = 0;
                    WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
                    String url = resourceRs.getString(2);
                    String whereStr = "";
                    if (whereParams != null) {
                        for (int i = 0; i < whereParams.size(); i++) {
                            whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ") + generateSpatialWhereStr(whereParams.getJSONObject(i), layerFields);
                        }
                    }
                    String orderFieldStr = generateSpatialOrderFieldStr(orderFields, layerFields);
                    boolean isBrowseMode = false;
                    if (StringUtils.isEmpty(whereStr)) { // 存在
                        String searchKey = SecurityUtils.md5(url);
                        JSONArray objectIdArray = (JSONArray) SystemCacheManager.getInstance().getWebGISItem(searchKey);
                        if (objectIdArray == null) {
                            objectIdArray = new JSONArray();
                            long[] objectLimit = ArcGISServiceRestService.getObjectLimit(url, layerFields.objectFieldName);
                            objectIdArray.add(objectLimit[0]);
                            objectIdArray.add(objectLimit[1]);
                            SystemCacheManager.getInstance().putWebGISItem(searchKey, objectIdArray);
                        }
                        totalRecord = objectIdArray.getLong(1) - objectIdArray.getLong(0) + 1;
                        if (page < 1)
                            page = 1;
                        totalPage = totalRecord % recordPerPage == 0 ? totalRecord / recordPerPage : totalRecord / recordPerPage + 1;
                        if (page > totalPage)
                            page = totalPage;

                        long startIndex = (page - 1) * recordPerPage + objectIdArray.getLong(0);
                        long endIndex = Math.min(page * recordPerPage, totalRecord) + objectIdArray.getLong(0);
                        whereStr = layerFields.objectFieldName + ">=" + startIndex + " and " + layerFields.objectFieldName + "<=" + endIndex;
                        isBrowseMode = true;
                    }
                    JSONObject queryResultJSON = ArcGISServiceRestService.query(url, whereStr, null, null, spatialConfig.wkid + "", false, true, orderFieldStr);
                    if (queryResultJSON != null) {
                        JSONArray featureArray = queryResultJSON.getJSONArray("features");
                        if (featureArray != null && !featureArray.isEmpty()) {
                            if (!isBrowseMode) {
                                totalRecord = featureArray.size();
                                page = 1;
                                totalPage = 1;
                            }
                            for (int m = 0; m < featureArray.size(); m++) {
                                JSONObject featureJson = featureArray.getJSONObject(m);
                                JSONObject recordJson = new JSONObject();
                                for (Entry<String, String> entry : layerFields.schemaFieldName2LayerFieldName.entrySet()) {
                                    recordJson.put(entry.getKey(), featureJson.getJSONObject("attributes").getString(entry.getValue()));
                                }
                                JSONUtils.append(json, "records", recordJson);
                            }
                        }
                    }
                    json.put("totalRecord", totalRecord);
                    json.put("page", page);
                    json.put("totalPage", totalPage);
                    json.put("recordPerPage", recordPerPage);
                    json.put("r", true);
                }
            } else {
                json.put("r", false);
            }
        } finally {
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 
     * @param whereParams
     * @return
     * @throws Exception
     */
    private AtomicLong tempTableIndex = new AtomicLong();

    private String generalSQLWhere(Connection con, JSONArray whereParams, List<String> tempTables, String tableAlias) throws Exception {
        String result = "";
        for (int i = 0; i < whereParams.size(); i++) {
            JSONObject whereJson = whereParams.getJSONObject(i);
            String fieldName = tableAlias + "." + whereJson.getString("fieldName");
            String operation = whereJson.getString("compare");
            String str = "";
            if (StringUtils.equalsIgnoreCase(operation, "in")) {
                String[] idArray = whereJson.getString("value").split(",");
                if (idArray.length <= 1000)
                    str = fieldName + " in (" + StringUtils.join(idArray, ",") + ")";
                else {
                    PreparedStatement stmt = null;
                    try {
                        String tempTableName = "tmp_4_query_" + (tempTableIndex.getAndIncrement());
                        while (Convert.obj2Int(DBHELPER.executeScalar("select count(*) from user_tables where table_name = upper('" + tempTableName + "')", con), 0) != 0)
                            tempTableName = "tmp_4_query_" + (tempTableIndex.getAndIncrement());
                        String sql = "CREATE GLOBAL TEMPORARY table " + tempTableName + " (id_value varchar2(400)) ON COMMIT PRESERVE ROWS";
                        DBHELPER.execute(sql, con);
                        stmt = con.prepareStatement("insert into " + tempTableName + " (id_value) values (?)");
                        for (int j = 0; j < idArray.length; j++) {
                            stmt.setString(1, idArray[j]);
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                        str = "(exists (select 'x' from " + tempTableName + " where id_value=" + fieldName + "))";
                        tempTables.add(tempTableName);
                    } finally {
                        DBHELPER.closeStatement(stmt);
                    }
                }
            }
            if (StringUtils.isEmpty(str))
                continue;
            if (StringUtils.isEmpty(result))
                result = str;
            else
                result += " and " + str;
        }
        return result;
    }

    /**
     * 生成Shape文件
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String exportLayerFeature2File(String params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        JSONObject paramJson = JSONUtils.parserJSONObject(params);
        WebGISExportTaskItem taskItem = new WebGISExportTaskItem();
        taskItem.wheres = new JSONObject();
        taskItem.wheres.put(paramJson.getString("layerId"), paramJson.getJSONArray("where"));
        taskItem.layerIds = new ArrayList<Long>();
        taskItem.layerIds.add(paramJson.getLong("layerId"));
        taskItem.email = paramJson.getString("email");
        taskItem.type = paramJson.getInt("type");
        taskItem.userId = paramJson.getLong("userId");
        taskItem.wkid = paramJson.getInt("wkid");
        if (executeTask(taskItem)) {
            json.put("url", WebGISMapServerProxy.instance.getProxyUrl() + "temp/" + taskItem.resultFileName);
            json.put("r", true);
        }
        return json.toString();
    }

    /**
     * 获取记录
     * 
     * @param con
     * @param districtGeometry
     * @param url
     * @param isCache
     * @param string
     * @param int1
     * @param jsonObject
     * @throws Exception
     */
    private boolean generalLayerShapeFile(Connection con, long layerId, JSONArray whereParams, List<String> shapeFiles, JGeometry districtGeometry, boolean isCache, String url) throws Exception {
        IDBCommand cmd = null;
        Boolean result = false;
        List<String> tempTables = new ArrayList<String>();
        String geometryType = (String) DBHELPER.executeScalar("select decode(type,0,'Point',1,'LineString',2,'Polygon','') from webgis_layer where id=" + layerId, con);
        WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        if (isCache) {
            try {
                String sql = "select " + StringUtils.join(layerFields.schemaFieldList, ",") + " ,geometry shape from " + "spatial_fs_" + layerId + " t";
                boolean hasWhere = false;
                if (whereParams != null && whereParams.size() != 0) {
                    String whereStr = generalSQLWhere(con, whereParams, tempTables, "t");
                    if (!StringUtils.isEmpty(whereStr)) {
                        sql += " where " + whereStr;
                        hasWhere = true;
                    }
                }
                if (districtGeometry != null) {
                    sql += hasWhere ? " and SDO_ANYINTERACT(geometry,?) = 'TRUE'" : " where SDO_ANYINTERACT(geometry,?) = 'TRUE'";
                    hasWhere = true;
                }

                if (hasWhere)
                    sql += " and rownum<=" + MAX_EXPORT_RECORD;
                else
                    sql += " where rownum<=" + MAX_EXPORT_RECORD;

                cmd = DBHELPER.getCommand(con, sql);
                if (districtGeometry != null)
                    cmd.setObject(1, JGeometry.store(con, districtGeometry));
                cmd.executeQuery();
                WKT wkt = new WKT();

                while (cmd.next()) {
                    Map<String, String> record = new HashMap<String, String>();
                    for (String fieldName : layerFields.schemaFieldList)
                        record.put(fieldName, cmd.getString(fieldName));
                    byte[] bytes = cmd.getBytes("shape");
                    if (bytes != null && bytes.length != 0) {
                        String shapeStr = new String(wkt.fromJGeometry(JGeometry.load(bytes)));
                        record.put("shape", shapeStr);
                        records.add(record);
                    }
                }
                for (String tableName : tempTables) {
                    DBHELPER.execute("truncate table " + tableName, con);
                    DBHELPER.execute("drop table " + tableName + " PURGE", con);
                }
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        } else { // ArcGIS Server读取
            String whereStr = "";
            if (whereParams != null && whereParams.size() != 0) {
                for (int i = 0; i < whereParams.size(); i++) {
                    String tempStr = generateSpatialWhereStr(whereParams.getJSONObject(i), layerFields);
                    if (StringUtils.isEmpty(whereStr))
                        whereStr = tempStr;
                    else
                        whereStr += " and " + tempStr;
                }
            }
            String geometryStr = "";
            if (districtGeometry != null) {
                geometryStr = GeometryUtils.wkt2ArcGISGeometryJSON(OracleSpatialUtils.geom2wkt(districtGeometry)).toString();
            }

            String arcgisGeometryType = "Point".equals(geometryType) ? "esriGeometryPoint" : "LineString".equals(geometryType) ? "esriGeometryPolyline" : "esriGeometryPolygon";
            if (!StringUtils.isEmpty(whereStr)) { // 有查询条件最多方位1000条记录
                JSONObject queryJson = ArcGISServiceRestService.query(url, whereStr, geometryStr, null, spatialConfig.wkid + "", true, true, null);
                if (queryJson != null) {
                    JSONArray featuresArray = queryJson.getJSONArray("features");
                    if (featuresArray != null) {
                        for (int m = 0; m < featuresArray.size(); m++) {
                            JSONObject attribJson = featuresArray.getJSONObject(m).getJSONObject("attributes");
                            Map<String, String> record = new HashMap<String, String>();
                            for (String fieldName : layerFields.schemaFieldList)
                                record.put(fieldName, attribJson.getString(layerFields.schemaFieldName2LayerFieldName.get(fieldName)));
                            record.put("shape", GeometryUtils.arcGISGeometry2WKT(arcgisGeometryType, featuresArray.getJSONObject(m).getJSONObject("geometry")));
                            records.add(record);
                        }
                    }
                }
            } else {
                String searchKey = SecurityUtils.md5(url);
                JSONArray objectIdArray = (JSONArray) SystemCacheManager.getInstance().getWebGISItem(searchKey);
                if (objectIdArray == null) {
                    objectIdArray = new JSONArray();
                    long[] objectLimit = ArcGISServiceRestService.getObjectLimit(url, layerFields.objectFieldName);
                    objectIdArray.add(objectLimit[0]);
                    objectIdArray.add(objectLimit[1]);
                    SystemCacheManager.getInstance().putWebGISItem(searchKey, objectIdArray);
                }
                int minObjectId = objectIdArray.getInt(0);
                int maxObjectId = objectIdArray.getInt(1);
                int totalRecordCount = 0;
                for (int objectId = 0; objectId <= maxObjectId && totalRecordCount <= MAX_EXPORT_RECORD; objectId += 1000) {
                    whereStr = layerFields.objectFieldName + ">=" + (objectId + minObjectId) + " and " + layerFields.objectFieldName + " <=" + (objectId + minObjectId + 1000);
                    JSONObject queryJson = ArcGISServiceRestService.query(url, whereStr, geometryStr, null, spatialConfig.wkid + "", true, true, null);
                    if (queryJson == null)
                        continue;
                    JSONArray featuresArray = queryJson.getJSONArray("features");
                    if (featuresArray == null)
                        continue;
                    for (int m = 0; m < featuresArray.size(); m++) {
                        JSONObject attribJson = featuresArray.getJSONObject(m).getJSONObject("attributes");
                        Map<String, String> record = new HashMap<String, String>();
                        for (String fieldName : layerFields.schemaFieldList)
                            record.put(fieldName, attribJson.getString(layerFields.schemaFieldName2LayerFieldName.get(fieldName)));
                        record.put("shape", GeometryUtils.arcGISGeometry2WKT(arcgisGeometryType, featuresArray.getJSONObject(m).getJSONObject("geometry")));
                        records.add(record);
                        totalRecordCount++;
                    }
                }
            }
            // /////
        }

        if (!records.isEmpty()) { // 生成Shape文件
            String layerName = (String) DBHELPER.executeScalar("select title||'['||to_char(sysdate,'YYYYMMDDHH24MISS')||']' from webgis_layer where id=" + layerId, con);
            String shapeFileDir = WebGISMapServerProxy.instance.getCacheFileDirectory() + "temp" + File.separator;
            FileUtils.forceMkdir(new File(shapeFileDir));
            WebGISExportUtils.createShapeFile(shapeFileDir + layerName, geometryType, layerFields.schemaFieldList, records, shapeFiles);
            result = true;
        }
        return result;
    }

    /**
     * 批量下载Shape文件
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String exportServerOrLayerFeature2File(String params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            JSONObject paramJson = JSONUtils.parserJSONObject(params);
            List<Long> layerIds = new ArrayList<Long>();
            if (paramJson.getBoolean("isServer")) {
                stmt = con.prepareStatement("select id,nvl(layer_comment,title) as title from webgis_layer where name=equal_layer_as and p_id = ? order by sortorder");
                stmt.setString(1, paramJson.getString("objectId"));
                ResultSet rs = stmt.executeQuery();
                while (rs.next())
                    layerIds.add(rs.getLong(1));
            } else {
                String[] ids = paramJson.getString("objectId").split(",");
                for (String id : ids)
                    layerIds.add(Long.parseLong(id));
            }
            WebGISExportTaskItem taskItem = new WebGISExportTaskItem();
            taskItem.wheres = paramJson.getJSONObject("wheres");
            taskItem.layerIds = layerIds;
            taskItem.email = paramJson.getString("email");
            taskItem.type = paramJson.getInt("type");
            taskItem.wkid = paramJson.getInt("wkid");
            taskItem.userId = paramJson.getLong("userId");
            if (executeTask(taskItem)) {
                json.put("url", WebGISMapServerProxy.instance.getProxyUrl() + "temp/" + taskItem.resultFileName);
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 执行任务
     * 
     * @param taskItem
     * @throws Exception
     */
    private boolean executeTask(WebGISExportTaskItem taskItem) {
        Connection con = null;
        boolean result = false;
        try {
            con = DBHELPER.getNativeConnection();
            JGeometry districtGeometry = WebGISResourceService4Oracle.getInstance().getUserDistrictGeometry(con, taskItem.userId, taskItem.wkid);
            if (taskItem.type == 1)
                executeExportShapeFile(taskItem, con, districtGeometry);
            else if (taskItem.type == 0)
                executeExportExcel(taskItem, con, districtGeometry);
            result = true;
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 导出图层到Excel
     * 
     * @param taskItem
     * @param con
     * @param districtGeometry
     * @throws Exception
     */
    private boolean executeExportExcel(WebGISExportTaskItem taskItem, Connection con, JGeometry districtGeometry) throws Exception {
        Boolean result = false;
        List<String> tempTables = new ArrayList<String>();
        PreparedStatement queryStmt = null;
        PreparedStatement resourceStmt = null;
        List<JSONObject> sheetList = new ArrayList<JSONObject>();
        try {
            String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id in (" + StringUtils.join(taskItem.layerIds, ",") + ") order by a.type,to_number(a.name)";
            resourceStmt = con.prepareStatement(sql2);
            ResultSet resourceRs = resourceStmt.executeQuery();
            while (resourceRs.next()) {
                long layerId = resourceRs.getLong(1);
                WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);
                JSONArray whereParams = taskItem.wheres.getJSONArray("" + layerId);
                if (resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4))) {
                    String sql = "select " + StringUtils.join(layerFields.schemaFieldList, ",") + " from spatial_fs_" + layerId + " t";

                    boolean hasWhere = false;
                    if (whereParams != null && whereParams.size() != 0) {
                        String whereStr = generalSQLWhere(con, whereParams, tempTables, "t");
                        if (!StringUtils.isEmpty(whereStr)) {
                            sql += " where " + whereStr;
                            hasWhere = true;
                        }
                    }
                    if (districtGeometry != null) {
                        sql += hasWhere ? " and SDO_ANYINTERACT(geometry,?) = 'TRUE'" : " where SDO_ANYINTERACT(geometry,?) = 'TRUE'";
                        hasWhere = true;
                    }

                    sql += (hasWhere ? " and " : " where ") + "rownum<=" + MAX_EXPORT_RECORD;
                    queryStmt = con.prepareStatement(sql);
                    if (districtGeometry != null)
                        queryStmt.setObject(1, JGeometry.store(con, districtGeometry));

                    ResultSet rs = queryStmt.executeQuery();
                    JSONArray records = new JSONArray();
                    while (rs.next()) {
                        JSONObject record = new JSONObject();
                        for (String fieldName : layerFields.schemaFieldList)
                            record.put(fieldName, rs.getString(fieldName));
                        records.add(record);
                    }
                    JSONObject layerRecordInfo = new JSONObject();
                    layerRecordInfo.put("name", (DBHELPER.executeScalar("select nvl(layer_comment,title) title from webgis_layer where id=" + layerId, con)));
                    layerRecordInfo.put("columns", layerFields.schemaFieldList);
                    layerRecordInfo.put("columnComment", layerFields.schemaField2Comment);
                    layerRecordInfo.put("records", records);
                    sheetList.add(layerRecordInfo);
                    for (String tableName : tempTables) {
                        DBHELPER.execute("truncate table " + tableName, con);
                        DBHELPER.execute("drop table " + tableName + " PURGE", con);
                    }
                    queryStmt.close();
                    queryStmt = null;
                    tempTables.clear();
                } else { // 需要从ArcGIS中读取
                    String url = resourceRs.getString(2);
                    String whereStr = "";
                    if (whereParams != null && whereParams.size() != 0) {
                        for (int i = 0; i < whereParams.size(); i++) {
                            String tempStr = generateSpatialWhereStr(whereParams.getJSONObject(i), layerFields);
                            if (StringUtils.isEmpty(whereStr))
                                whereStr = tempStr;
                            else
                                whereStr += " and " + tempStr;
                        }
                    }
                    String geometryStr = "";
                    if (districtGeometry != null) {
                        geometryStr = GeometryUtils.wkt2ArcGISGeometryJSON(OracleSpatialUtils.geom2wkt(districtGeometry)).toString();
                    }

                    JSONArray records = new JSONArray();
                    if (!StringUtils.isEmpty(whereStr)) { // 有查询条件最多方位1000条记录
                        JSONObject queryJson = ArcGISServiceRestService.query(url, whereStr, geometryStr, null, spatialConfig.wkid + "", false, true, null);
                        if (queryJson != null) {
                            JSONArray featuresArray = queryJson.getJSONArray("features");
                            if (featuresArray != null) {
                                for (int m = 0; m < featuresArray.size(); m++) {
                                    JSONObject record = new JSONObject();
                                    JSONObject attribJson = featuresArray.getJSONObject(m).getJSONObject("attributes");
                                    for (String fieldName : layerFields.schemaFieldList)
                                        record.put(fieldName, attribJson.getString(layerFields.schemaFieldName2LayerFieldName.get(fieldName)));
                                    records.add(record);
                                }
                            }
                        }
                    } else {
                        String searchKey = SecurityUtils.md5(url);
                        JSONArray objectIdArray = (JSONArray) SystemCacheManager.getInstance().getWebGISItem(searchKey);
                        if (objectIdArray == null) {
                            objectIdArray = new JSONArray();
                            long[] objectLimit = ArcGISServiceRestService.getObjectLimit(url, layerFields.objectFieldName);
                            objectIdArray.add(objectLimit[0]);
                            objectIdArray.add(objectLimit[1]);
                            SystemCacheManager.getInstance().putWebGISItem(searchKey, objectIdArray);
                        }
                        int minObjectId = objectIdArray.getInt(0);
                        int maxObjectId = objectIdArray.getInt(1);
                        int totalRecordCount = 0;
                        for (int objectId = 0; objectId <= maxObjectId && totalRecordCount <= MAX_EXPORT_RECORD; objectId += 1000) {
                            whereStr = layerFields.objectFieldName + ">=" + (objectId + minObjectId) + " and " + layerFields.objectFieldName + " <=" + (objectId + minObjectId + 1000);
                            JSONObject queryJson = ArcGISServiceRestService.query(url, whereStr, geometryStr, null, spatialConfig.wkid + "", false, true, null);
                            if (queryJson == null)
                                continue;
                            JSONArray featuresArray = queryJson.getJSONArray("features");
                            if (featuresArray == null)
                                continue;
                            for (int m = 0; m < featuresArray.size(); m++) {
                                JSONObject record = new JSONObject();
                                JSONObject attribJson = featuresArray.getJSONObject(m).getJSONObject("attributes");
                                for (String fieldName : layerFields.schemaFieldList)
                                    record.put(fieldName, attribJson.getString(layerFields.schemaFieldName2LayerFieldName.get(fieldName)));
                                records.add(record);
                                totalRecordCount++;
                            }
                        }
                    }

                    JSONObject layerRecordInfo = new JSONObject();
                    layerRecordInfo.put("name", (DBHELPER.executeScalar("select nvl(layer_comment,title) title from webgis_layer where id=" + layerId, con)));
                    layerRecordInfo.put("columns", layerFields.schemaFieldList);
                    layerRecordInfo.put("columnComment", layerFields.schemaField2Comment);
                    layerRecordInfo.put("records", records);
                    sheetList.add(layerRecordInfo);

                }
            }

            if (!sheetList.isEmpty()) {
                String fileName = System.currentTimeMillis() + ".xls";
                taskItem.resultFileName = fileName;
                WebGISExportUtils.createExcelFile(WebGISMapServerProxy.instance.getCacheFileDirectory() + "temp" + File.separator + fileName, sheetList);
            }
        } finally {
            DBHELPER.closeStatement(queryStmt);
            DBHELPER.closeStatement(resourceStmt);

        }
        return result;
    }

    /**
     * 导出到Shape文件
     * 
     * @param taskItem
     * @param con
     * @param districtGeometry
     * @throws Exception
     */
    private void executeExportShapeFile(WebGISExportTaskItem taskItem, Connection con, JGeometry districtGeometry) throws Exception {
        List<String> shapeFiles = new ArrayList<String>();

        PreparedStatement resourceStmt = null;
        try {
            String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id in (" + StringUtils.join(taskItem.layerIds, ",") + ") order by a.type,to_number(a.name)";
            resourceStmt = con.prepareStatement(sql2);
            ResultSet resourceRs = resourceStmt.executeQuery();
            while (resourceRs.next()) {
                boolean isCache = resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4));
                String url = resourceRs.getString(2);
                long layerId = resourceRs.getLong(1);
                generalLayerShapeFile(con, layerId, taskItem.wheres.getJSONArray("" + layerId), shapeFiles, districtGeometry, isCache, url);
            }
            String zipFile = System.currentTimeMillis() + ".zip";
            taskItem.resultFileName = zipFile;
            ZipUtils.zip(WebGISMapServerProxy.instance.getCacheFileDirectory() + "temp" + File.separator + zipFile, shapeFiles);
            for (String fileName : shapeFiles)
                FileUtils.deleteQuietly(new File(fileName));
        } finally {
            DBHELPER.closeStatement(resourceStmt);
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private WebGISQueryService4Oracle() {
        staticticFunctionName2Label.put("count", "数量");
        staticticFunctionName2Label.put("max", "最大值");
        staticticFunctionName2Label.put("count", "最小值");
        staticticFunctionName2Label.put("sum", "合计");
    }

    /**
     * 
     * @param layerId
     * @param keyField
     * @param keyValue
     * @param returnAttributes
     * @return
     * @throws Exception
     */
    public String getLayerFeature(String layerId, String keyField, String keyValue, boolean isStringValue, boolean returnAttributes) throws Exception {
        JSONObject json = new JSONObject();
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, Long.parseLong(layerId));
            // 首先判断数据是否已经缓存完毕
            String sql = "select count(*) from webgis_layer where download_status=1 and exists (select 'x' from user_tables where table_name = 'SPATIAL_FS_'||webgis_layer.id) and id=" + layerId;
            JSONArray list = new JSONArray();
            String whereStr = "";
            keyValue = StringUtils.replace(keyValue, "'", "''");
            if (DBHELPER.executeScalarInt(sql, con) == 1) {
                whereStr = isStringValue ? (keyField + "='" + keyValue + "'") : (keyField + "=" + keyValue);
                sql = "select object_attributes,sdo_util.to_wktgeometry(geometry) from SPATIAL_FS_" + layerId + " where " + whereStr;
                stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    JSONObject geometryJson = GeometryUtils.wkt2ArcGISGeometryJSON(rs.getString(2));
                    JSONObject graphicJson = new JSONObject();
                    JSONObject attributesJson = new JSONObject();
                    attributesJson.put("caption", generateRecordCaption(JSONUtils.parserJSONObject(Convert.bytes2Str(rs.getBytes(1))), layerFields));
                    graphicJson.put("attributes", attributesJson);
                    graphicJson.put("geometry", geometryJson);
                    list.add(graphicJson);
                }
            } else {
                sql = "select b.url||'/'||a.name||'/query' from webgis_services b, webgis_layer a where a.p_id=b.id and a.id=" + layerId;
                String url = DBHELPER.executeScalarString(sql, con);
                String wkid = "" + spatialConfig.wkid;
                keyField = DBHELPER.executeScalarString("select name from webgis_layer_fields where upper(schema_field_name)=upper('" + keyField + "') and P_id=" + layerId, con);
                whereStr = isStringValue ? (keyField + "='" + keyValue + "'") : (keyField + "=" + keyValue);
                JSONObject queryResultJson = ArcGISServiceRestService.query(url, whereStr, null, null, wkid, true, false, null);
                if (queryResultJson != null) {
                    JSONArray features = queryResultJson.getJSONArray("features");
                    if (features != null && !features.isEmpty()) {
                        for (int i = 0; i < features.size(); i++) {
                            JSONObject feature = features.getJSONObject(i);
                            JSONObject geometryJson = feature.getJSONObject("geometry");
                            JSONObject graphicJson = new JSONObject();
                            JSONObject attributesJson = new JSONObject();
                            attributesJson.put("caption", generateRecordCaption(feature.getJSONObject("attributes"), layerFields));
                            graphicJson.put("attributes", attributesJson);
                            graphicJson.put("geometry", geometryJson);
                            list.add(graphicJson);
                        }
                    }
                }
            }
            json.put("geometry", list.toString());
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static WebGISQueryService4Oracle instance = new WebGISQueryService4Oracle();

    public static WebGISQueryService4Oracle getInstance() {
        return instance;
    }

}
