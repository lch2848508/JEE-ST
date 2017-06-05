package com.estudio.gis.oracle;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.spatial.geometry.JGeometry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.estudio.context.RuntimeContext;
import com.estudio.gis.GeometryUtils;
import com.estudio.gis.WebGISSpatialConfig;
import com.estudio.intf.db.IDBHelper;
import com.estudio.net.WebClient;
import com.estudio.utils.Convert;
import com.estudio.utils.ThreadUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public final class WebGISCacheService4Oracle {
    private WebGISSpatialConfig spatialConfig = null;
    private static IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private Logger logger = Logger.getLogger("WebGISCache");
    private Map<String, Geometry> cityCode2Geometry = new HashMap<String, Geometry>();
    private void info(String str) {
        logger.info(str);
    }

    private void error(String str) {
        logger.error(str);
    }

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
     * 执行
     * 
     * @param spatialConfig
     * 
     * @throws Exception
     */
    public void execute() throws Exception {
        Connection con = null;
        try {
            info("开始执行后台服务程序...");
            con = DBHELPER.getNativeConnection();
            executeCache(con); // 下载服务
            executeStatistic(con);// 行政区域分析功能
            executeDelete(con);// 数据清理服务
            info("完成执行后台服务程序.");
        } finally {
            DBHELPER.closeConnection(con);
        }

    }

    /**
     * 行政区域分析功能
     * 
     * @param con
     * @throws Exception
     */
    private void executeStatistic(Connection con) throws Exception {
        info("开始读取行政区数据...");
        loadDistrictGeomery(con);
        if (cityCode2Geometry.isEmpty())
            return;
        Statement stmt = null;
        Statement exeStmt = null;
        PreparedStatement flagStmt = null;
        try {
            flagStmt = con.prepareStatement("update webgis_layer set statistic_status=1 where id=?");
            stmt = con.createStatement();
            exeStmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select a.id,b.name||'-'||a.title as title,a.type from webgis_layer a,webgis_services b where a.name=a.equal_layer_as and a.p_id=b.id and a.download_status=1 and a.statistic_status=0");
            while (rs.next()) {
                long layerId = rs.getLong(1);
                String serverAndLayerName = rs.getString(2);
                String featureTableName = "SPATIAL_FS_" + layerId;
                if (DBHELPER.isTableExists(featureTableName)) {
                    info("开始执行空间行政区域分析(" + serverAndLayerName + ")...");
                    String statisticFeatureTableName = "SPATIAL_STAT_" + layerId;
                    executeSQL(exeStmt, "drop table " + statisticFeatureTableName + " PURGE");
                    String sql = "create table " + statisticFeatureTableName + " as select b.q_uid,a.code city_code,a.name city_name, sdo_geom.sdo_length(sdo_geom.sdo_intersection(b.geometry,a.geometry,2),2) as geometry_length,sdo_geom.sdo_area(sdo_geom.sdo_intersection(b.geometry,a.geometry,2),2) as geometry_area from sys_district a," + featureTableName
                            + " b where SDO_ANYINTERACT(b.geometry,a.geometry)='TRUE' and a.geometry is not null";
                    executeSQL(exeStmt, sql);
                    
                    sql = "CREATE INDEX idx_" + statisticFeatureTableName + " ON " + statisticFeatureTableName + "(q_uid) INDEXTYPE IS MDSYS.SPATIAL_INDEX";
                    executeSQL(exeStmt, sql);
                   
                    if (DBHELPER.isTableExists(statisticFeatureTableName)) {
                        flagStmt.setLong(1, layerId);
                        flagStmt.execute();
                    }
                    info("执行空间行政区域分析(" + serverAndLayerName + ")完毕.");
                } else {
                    flagStmt.setLong(1, layerId);
                    flagStmt.execute();
                }
            }
        } finally {
            DBHELPER.closeStatement(exeStmt);
            DBHELPER.closeStatement(flagStmt);
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 数据清理服务
     * 
     * @param con
     * @throws SQLException
     */
    private void executeDelete(Connection con) throws SQLException {
        Statement stmt = null;
        Statement stmt1 = null;
        try {
            info("开始执行数据清理...");
            stmt1 = con.createStatement();
            stmt = con.createStatement();
            String sql = "select 'drop table ' || table_name || ' PURGE' as XX from user_tables where table_name not in (select 'SPATIAL_FS_'||id from webgis_layer where name=equal_layer_as) and table_name like 'SPATIAL_FS_%'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
                executeSQL(stmt1, rs.getString(1));

            sql = "select 'drop table ' || table_name || ' PURGE' as XX from user_tables where table_name not in (select 'SPATIAL_STAT_'||id from webgis_layer where name=equal_layer_as) and table_name like 'SPATIAL_STAT_%'";
            rs = stmt.executeQuery(sql);
            while (rs.next())
                executeSQL(stmt1, rs.getString(1));

            info("完成执行数据清理.");
        } finally {
            DBHELPER.closeStatement(stmt1);
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 执行缓存服务
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void executeCache(Connection con) throws ClassNotFoundException, SQLException {
        PreparedStatement stmt = null;
        PreparedStatement flagStmt = null;
        Statement tempStmt = null;
        try {
            tempStmt = con.createStatement();

            info("开始执行数据缓存任务...");
            DBHELPER.execute("update webgis_layer set statistic_status=0 where download_status=0", con);
            stmt = con
                    .prepareStatement("select a.id server_id,b.id layer_id,a.url,b.name,b.title,b.type,a.spacial_ref,a.name server_name,trim(nvl(b.record_caption_setting,'')) record_caption_setting,b.p_layer_id from webgis_services a, webgis_layer b where b.name=b.equal_layer_as and (a.spacial_ref is not null) and b.type in (0,1,2) and a.id = b.p_id and (nvl(b.query_enabled,0) + nvl(b.ident_enabled,0))>0 and b.download_status=0 order by a.id, b.id");
            flagStmt = con.prepareStatement("update webgis_layer set download_status=1 where id=?");

            List<String> errorHostList = new ArrayList<String>();

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // 检查wkid
                long serverId = rs.getLong(1);
                long layerId = rs.getLong(2);
                int layerType = rs.getInt("type");

                String layerCaption = rs.getString("server_name") + " - " + rs.getString("title");
                info("开始处理图层:" + layerCaption + "...");

                String mapServerURL = rs.getString(3);
                if (errorHostList.indexOf(mapServerURL) != -1) {
                    info("忽略处理图层:" + layerCaption + " 原因:检测出连接地图服务器失败.");
                    continue;
                }
                String url = mapServerURL + "/" + rs.getString(4) + "/query";
                if (downloadServerObjectToOracleSpatial(con, serverId, layerId, layerType, url, rs.getString("title"), mapServerURL, errorHostList)) {
                    flagStmt.setLong(1, layerId);
                    flagStmt.execute();
                }
                info("完成处理图层:" + layerCaption + ".");
            }
            info("完成执行数据缓存任务.");
        } catch (final Exception e) {
            e.printStackTrace();
            error("执行缓存任务失败 错误信息:" + e.getMessage());
        } finally {
            DBHELPER.closeStatement(flagStmt);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(tempStmt);
        }
    }

    /**
     * 读取行政区信息
     * 
     * @param con
     * @throws Exception
     */
    private void loadDistrictGeomery(Connection con) throws Exception {
        cityCode2Geometry.clear();
        WKTReader wktReader = new WKTReader();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select code,wkt from sys_district where wkt is not null");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cityCode2Geometry.put(rs.getString(1), wktReader.read(Convert.bytes2Str(rs.getBytes(2))));
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 执行SQL语句
     * 
     * @param con
     * @param sql
     */
    private void executeSQL(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (final Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * 获取地图附加资源
     * 
     * @param id
     * @param con
     * @return
     * @throws SQLException
     */
    private Map<String, String> getServerExtParams(long id, Connection con) throws SQLException {
        Map<String, String> extParams = new HashMap<String, String>();
        PreparedStatement extStmt = null;
        try {
            extStmt = con.prepareStatement("select param_name,param_value,data_type from webgis_service_params where p_id=?");
            extStmt.setLong(1, id);
            ResultSet extRs = extStmt.executeQuery();
            while (extRs.next())
                extParams.put(extRs.getString(1), extRs.getString(2));
        } finally {
            DBHELPER.closeStatement(extStmt);
        }
        return extParams;
    }

    /**
     * 下载服务器上的对象到OracleSpatial中
     * 
     * @param con
     * @param layerId
     * @param layerType
     * @param captionTemplate
     * @param errorHostList
     * @param mapServerURL
     * @return
     * @throws Exception
     */
    private boolean downloadServerObjectToOracleSpatial(Connection con, long serverId, long layerId, int layerType, String url, String layerTitle, String mapServerURL, List<String> errorHostList) throws Exception {
        PreparedStatement delStmt = null;
        PreparedStatement insFeatureTableStmt = null;
        Statement tempStmt = null;
        boolean result = false;
        int maxRecordPerTimes = 100;
        info("开始下载数据 url:" + url + "...");
        WebGISLayerFields layerFields = WebGISResourceService4Oracle.getInstance().getLayerFields(con, layerId);

        Map<String, String> extParams = getServerExtParams(serverId, con);
        String tokenLoginUrl = extParams.get("getTokenURL");
        // 第一步首先获取地图服务器资源
        // 检查是否需要登录地图服务
        if (!StringUtils.isEmpty(StringUtils.trim(tokenLoginUrl)))
            WebClient.get(tokenLoginUrl);

        // 第一步首先查询所有的ID顺便检测一下网络是否通
        long[] objectIdLimit = ArcGISServiceRestService.getObjectLimit(url, layerFields.objectFieldName);

        if (objectIdLimit != null) {
            try {
                long minIndex = objectIdLimit[0];
                long maxIndex = objectIdLimit[1];

                String featureTableName = "SPATIAL_FS_" + layerId;
                String statisticFeatureTableName = "SPATIAL_STAT_" + layerId;

                // 删除属性表
                tempStmt = con.createStatement();

                String ddlSQL = "drop table " + featureTableName + " PURGE";
                executeSQL(tempStmt, ddlSQL);

                ddlSQL = "drop table " + statisticFeatureTableName + " PURGE";
                executeSQL(tempStmt, ddlSQL);

                ddlSQL = "create table " + featureTableName + "(q_uid number,q_objectid number,";
                String insPropSQL = "insert into " + featureTableName + "(q_uid,";

                // 添加到系统表中
                executeSQL(tempStmt, "delete from sys_db_objects where object_name = '" + featureTableName + "'");
                executeSQL(tempStmt, "insert into sys_db_objects (id,object_name) values (seq_for_j2ee_uniqueid.nextval,'" + featureTableName + "')");
                executeSQL(tempStmt, "insert into sys_db_objects (id,object_name) values (seq_for_j2ee_uniqueid.nextval,'" + statisticFeatureTableName + "')");

                List<String> fieldNameList = new ArrayList<String>();
                for (Entry<String, String> entry : layerFields.schemaFieldName2LayerFieldName.entrySet()) {
                    String fieldName = entry.getKey();
                    if (!layerFields.schemaFieldName2DataType.containsKey(fieldName))
                        continue;
                    int dataType = layerFields.schemaFieldName2DataType.get(fieldName);
                    if (dataType == 5)
                        continue;

                    String schemaFieldName = entry.getKey();
                    if (dataType == 2)
                        ddlSQL += schemaFieldName + " date,";
                    else if (dataType == 1 || dataType == 4)
                        ddlSQL += schemaFieldName + " number,";
                    else
                        ddlSQL += schemaFieldName + " varchar2(2000),";

                    insPropSQL += schemaFieldName + ",";

                    fieldNameList.add(schemaFieldName);
                }

                insPropSQL += "geometry,object_caption,search_caption,object_attributes,q_objectid";

                ddlSQL += "geometry MDSYS.SDO_GEOMETRY,geometry_length number,geometry_area number,object_caption varchar2(800), search_caption varchar2(4000),OBJECT_ATTRIBUTES CLOB)";
                if (!StringUtils.isEmpty(this.spatialConfig.tablespace))
                    ddlSQL += " tablespace " + this.spatialConfig.tablespace;
                insPropSQL += ") values (?,";
                for (int i = 0; i < fieldNameList.size(); i++)
                    insPropSQL += "?,";
                insPropSQL += "?,?,?,?,?)";

                // 创建数据库表
                executeSQL(tempStmt, ddlSQL);

                // 注册数据库表并创建索引
                ddlSQL = "delete from user_sdo_geom_metadata where table_name = upper('" + featureTableName + "')";
                executeSQL(tempStmt, ddlSQL);

                // 元数据
                String[] spatialExtents = spatialConfig.extent.split(",");
                ddlSQL = "INSERT INTO user_sdo_geom_metadata VALUES ( '" + featureTableName + "', 'geometry', MDSYS.SDO_DIM_ARRAY( MDSYS.SDO_DIM_ELEMENT('X', " + spatialExtents[0] + "," + spatialExtents[2] + ", " + spatialConfig.tolerance + "), MDSYS.SDO_DIM_ELEMENT('Y', " + spatialExtents[1] + "," + spatialExtents[3] + "," + spatialConfig.tolerance + ")), " + spatialConfig.wkid + " )";
                executeSQL(tempStmt, ddlSQL);

                // 空间索引
                String layerTypeStr = layerType == 0 ? "MULTIPOINT" : layerType == 1 ? "MULTILINE" : "MULTIPOLYGON";
                ddlSQL = "CREATE INDEX idx_" + featureTableName + "_01 ON " + featureTableName + "(geometry) INDEXTYPE IS MDSYS.SPATIAL_INDEX PARAMETERS ('layer_gtype=" + layerTypeStr + "')";
                executeSQL(tempStmt, ddlSQL);

                ddlSQL = "alter table " + featureTableName + " add constraint idx_features_" + layerId + " primary key (q_uid)";
                executeSQL(tempStmt, ddlSQL);

                executeSQL(tempStmt, "alter table " + featureTableName + " NOLOGGING");

                for (Entry<String, String> entry : layerFields.schemaFieldName2LayerFieldName.entrySet()) {
                    executeSQL(tempStmt, "comment on column " + featureTableName + "." + entry.getKey() + " is '" + entry.getValue() + "'");
                }
                executeSQL(tempStmt, "comment on table " + featureTableName + " is '" + layerTitle + "'");

                insFeatureTableStmt = con.prepareStatement(insPropSQL);
                for (long m = minIndex; m <= maxIndex; m += maxRecordPerTimes) {
                    String whereStr = layerFields.objectFieldName + ">=" + m + " and " + layerFields.objectFieldName + "<=" + (m + maxRecordPerTimes - 1);
                    JSONObject queryJson = ArcGISServiceRestService.query(url, whereStr, null, null, spatialConfig.wkid + "", true, true, null);
                    saveServerObjectToOracleSpatial(con, insFeatureTableStmt, queryJson, fieldNameList, layerFields);
                    ThreadUtils.sleep(500);
                }
                //
                info("完成下载数据 url:" + url + "...");
                result = true;
            } catch (final Exception e) {
                e.printStackTrace();
                Throwable exceptionCause = e.getCause();
                if (exceptionCause != null && exceptionCause instanceof ConnectException)
                    errorHostList.add(mapServerURL);
                error("下载数据失败 url:" + url + " 错误信息:" + e.getMessage());
                result = false;
            } finally {
                DBHELPER.closeStatement(tempStmt);
                DBHELPER.closeStatement(delStmt);
                DBHELPER.closeStatement(insFeatureTableStmt);
            }
        } else {
            error("下载数据失败 url:" + url + " 错误信息:无法获取图层的最大最小ObjectId值.");
        }
        return result;
    }

    /**
     * 保存实体到数据库中
     * 
     * @param instStmt
     * @param updStmt
     * @param statStmt
     * @param objectIdFieldName
     * @param settingCaptionFieldTitle
     * @param settingCaptionFieldName
     * @param fieldTitleList
     * @param fieldNameList
     * @param varcharFieldList
     * @param dateFieldList
     * @param fieldNameList2
     * @param insPropertyStmt
     * @param numberFieldList
     * @param schemaFieldName2ShapeFieldName
     * @param parserJSONObject
     * @throws Exception
     */
    private void saveServerObjectToOracleSpatial(Connection con, PreparedStatement insPropertyStmt, JSONObject json, List<String> fieldNameList, WebGISLayerFields layerFields) throws Exception {
        String geometryType = json.getString("geometryType");
        JSONArray features = json.getJSONArray("features");
        int featurescount=features.size();

        for (int i = 0; i < features.size(); i++) {
            JSONObject feature = features.getJSONObject(i);
            JSONObject attributeObject = feature.getJSONObject("attributes");
            JSONObject recordAttributes = generateRecordAttributes(attributeObject, layerFields);
            long objectId = attributeObject.getLong(layerFields.objectFieldName);
            String objectCaption = "";
            String searchCaption = generalFeatureSearchCaption(attributeObject, layerFields);
            if (StringUtils.isEmpty(objectCaption))
                objectCaption = Long.toString(objectId);
            long qUid = objectId;
            String objectAttributes = recordAttributes.toString();
            // 保存属性
            insPropertyStmt.setLong(1, qUid);
            for (int m = 0; m < fieldNameList.size(); m++) {
                String oraFieldName = fieldNameList.get(m);
                String shapeFieldName = layerFields.schemaFieldName2LayerFieldName.get(oraFieldName);
                int dataType = layerFields.schemaFieldName2DataType.get(oraFieldName);
                String value = StringUtils.trim(attributeObject.getString(shapeFieldName));
                if (StringUtils.isEmpty(value)) {
                    insPropertyStmt.setObject(m + 2, null);
                    continue;
                }
                if (dataType == 2)
                    insPropertyStmt.setDate(m + 2, new java.sql.Date(Convert.str2Long(value)));
                else if (dataType == 1 || dataType == 4)
                    insPropertyStmt.setDouble(m + 2, Convert.obj2Double(value, 0));
                else
                    insPropertyStmt.setString(m + 2, value);
            }
            JSONObject geometryJson = feature.getJSONObject("geometry");
            JGeometry geometry = GeometryUtils.arcGISGeometry2OracleSpatial(geometryType, geometryJson, spatialConfig.wkid);
            if (geometry == null)
                continue;
            insPropertyStmt.setObject(fieldNameList.size() + 2, JGeometry.store(con, geometry));
            insPropertyStmt.setString(fieldNameList.size() + 3, objectCaption);
            insPropertyStmt.setString(fieldNameList.size() + 4, searchCaption);
            insPropertyStmt.setString(fieldNameList.size() + 5, objectAttributes);
            insPropertyStmt.setLong(fieldNameList.size() + 6, objectId);
            insPropertyStmt.addBatch();
            if(i%100!=0&&i!=featurescount-1){
            	continue;
            }
            
            if (i % 100 == 0) {
	            insPropertyStmt.executeBatch();
	            insPropertyStmt.clearBatch();
	            continue;
	        }
            if(i==featurescount-1){
		        insPropertyStmt.executeBatch();
		        insPropertyStmt.clearBatch();
            }
//            if (i % 100 == 0) {
//                insPropertyStmt.executeBatch();
//                insPropertyStmt.clearBatch();
//            }
//
//            insPropertyStmt.executeBatch();
//            insPropertyStmt.clearBatch();
        }
    }

    /**
     * 
     * @param recordJson
     * @param fieldNameList
     * @param fieldTitleList
     * @param newFieldName2OldFieldName
     * @param layerFields
     * @return
     */
    private JSONObject generateRecordAttributes(JSONObject recordJson, WebGISLayerFields layerFields) {
        for (Entry<String, Integer> entry : layerFields.schemaFieldName2DataType.entrySet()) {
            if (entry.getValue() == 2) {
                String name = layerFields.schemaFieldName2LayerFieldName.get(entry.getKey());
                String value = StringUtils.trim(recordJson.getString(name));
                if (!StringUtils.isEmpty(value)) {
                    value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.sql.Date(Convert.try2Long(value, 0)));
                    value = StringUtils.trim(StringUtils.replace(value, "00:00:00", ""));
                    recordJson.put(name, value);

                }
            }
        }
        return recordJson;
    }

    /**
     * 
     * @param jsonObject
     * @param fieldNameList
     * @param fieldTitleList
     * @return
     */
    private String generalFeatureSearchCaption(JSONObject recordJson, WebGISLayerFields layerFields) {
        String result = "";
        for (Entry<String, Integer> entry : layerFields.schemaFieldName2DataType.entrySet()) {
            if (entry.getValue() == 0) {
                String name = layerFields.schemaFieldName2LayerFieldName.get(entry.getKey());
                String value = StringUtils.trim(recordJson.getString(name));
                result += value;
            }
        }
        result += recordJson.getString(layerFields.objectFieldName);
        if (!StringUtils.isEmpty(result))
            while (result.getBytes().length >= 4000)
                result = result.substring(0, result.length() - 1);
        return StringUtils.upperCase(result);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static WebGISCacheService4Oracle instance = new WebGISCacheService4Oracle();

    public static WebGISCacheService4Oracle getInstance() {
        return instance;
    }

}
