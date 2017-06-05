package com.estudio.gis.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKB;
import oracle.spatial.util.WKT;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeConfig;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.gis.GeometryUtils;
import com.estudio.gis.WebGISDistrictItem;
import com.estudio.gis.WebGISSpatialConfig;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.net.WebClient;
import com.estudio.officeservice.ExcelUtils;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.SecurityUtils;
import com.estudio.utils.StringUtilsLocal;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;

public class WebGISResourceService4Oracle {
    private final IDBHelper DBHELPER = DBHelper4Oracle.getInstance();// RuntimeContext.getDbHelper();
    private WebGISSpatialConfig spatialConfig = null;

    /**
     * 地理坐标配置
     * 
     * @param spatialConfig
     */
    public void initSpatialParams(WebGISSpatialConfig spatialConfig) {
        this.spatialConfig = spatialConfig;
    }

    // 注册地图资源
    public JSONObject registerMapServer(long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select url,type,MD5_VERSION from webgis_services where id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String url = rs.getString(1);
                String type = rs.getString(2);
                String md5Version = rs.getString(3);
                if (StringUtils.equals(type, "ArcGISDynamicMapService") || StringUtils.equals(type, "ArcGISDynamicMapService")|| StringUtils.equals(type, "ArcGISRouteMapService")) {
                    Map<String, String> extParams = getServerExtParams(id, con);
                    registerArcGISMapService(con, id, url, extParams, md5Version);
                }
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
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
     * 注册ArcGIS服务
     * 
     * @param con
     * @param id
     * @param url
     * @throws Exception
     */
    private void registerArcGISMapService(Connection con, long id, String url, Map<String, String> extParams, String md5Version) throws Exception {
        String tokenString = extParams.get("token");
        String tokenLoginUrl = extParams.get("getTokenURL");
        // 第一步首先获取地图服务器资源
        // 检查是否需要登录地图服务
        if (!StringUtils.isEmpty(StringUtils.trim(tokenLoginUrl)))
            WebClient.get(tokenLoginUrl);
        String serverURL = url;
        if (serverURL.endsWith("/"))
            serverURL = serverURL.substring(0, serverURL.length() - 1);
        String serverContent = WebClient.get(serverURL + "?f=json", "utf-8");
        if (StringUtils.isEmpty(serverContent))
            throw new Exception("无法获取服务器资源!");
        JSONObject serverInfo = JSONUtils.parserJSONObject(serverContent);

        // 图层信息
        String layerURL = serverURL + "/layers?f=json";
        String layerContent = WebClient.get(layerURL, "utf-8");
        if (StringUtils.isEmpty(layerContent))
            throw new Exception("无法获取服务器资源!");
        JSONObject layerJson = JSONUtils.parserJSONObject(layerContent);

        String newMD5Version = SecurityUtils.md5(layerContent);
        if (StringUtils.equals(md5Version, newMD5Version))
            return;

        // 第二步首先生成字典
        Map<String, JSONObject> id2LayerInfo = new HashMap<String, JSONObject>();
        Map<String, JSONObject> id2FieldInfo = new HashMap<String, JSONObject>();
        backupLayerAndFields(con, id, id2LayerInfo, id2FieldInfo);

        // 把需要删除的图层注册到删除图层列表中
        DBHELPER.execute("insert into webgis_layer_garbage (id, layer_id)  (select seq_for_j2ee_webgis.nextval,id from webgis_layer where p_id=" + id + ")", con);

        // 第三部删除所有的图层对象
        DBHELPER.execute("delete from webgis_layer where p_id=" + id, con);

        // 保存新的图层对象
        // 1 保存服务器信息
        Statement tempStmt = null;
        PreparedStatement stmt = null;
        PreparedStatement layerStmt = null;
        PreparedStatement fieldStmt = null;
        try {
            tempStmt = con.createStatement();
            stmt = con.prepareStatement("update webgis_services set map_extend=?,init_extend=?,spacial_ref=?,MD5_VERSION=? where id=?");
            if (serverInfo.containsKey("fullExtent")) {
                JSONObject extentObject = serverInfo.getJSONObject("fullExtent");
                String extentStr = extentObject.getString("xmin") + "," + extentObject.getString("ymin") + "," + extentObject.getString("xmax") + "," + extentObject.getString("ymax");
                stmt.setString(1, extentStr);
                stmt.setString(3, extentObject.containsKey("spatialReference") ? extentObject.getJSONObject("spatialReference").toString() : "");
            } else {
                stmt.setString(1, "");
                stmt.setString(3, "");
            }
            if (serverInfo.containsKey("initialExtent")) {
                JSONObject extentObject = serverInfo.getJSONObject("initialExtent");
                String extentStr = extentObject.getString("xmin") + "," + extentObject.getString("ymin") + "," + extentObject.getString("xmax") + "," + extentObject.getString("ymax");
                stmt.setString(2, extentStr);
            } else {
                stmt.setString(2, "");
            }
            stmt.setString(4, newMD5Version);
            stmt.setLong(5, id);
            stmt.execute();

            // 保存图层字段信息
            layerStmt = con.prepareStatement("insert into webgis_layer (id, p_id, name, title, type, query_enabled, ident_enabled, layer_level, layer_comment, p_layer_id, sortorder,is_special,RECORD_CAPTION_SETTING,equal_layer_as,user_marker_id,user_marker_primary_field) values (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)");
            fieldStmt = con.prepareStatement("insert into webgis_layer_fields (id, name, title, data_type, sortorder, visible_enabled, p_id, query_enabled,is_special,special_type,SCHEMA_FIELD_NAME) values (?,?,?, ?, ?, ?, ?, ?, ?, ?,?)");

            JSONArray layers = layerJson.getJSONArray("layers");
            Map<String, Integer> layerId2Level = new HashMap<String, Integer>();
            Map<String, Long> layerId2dbID = new HashMap<String, Long>();
            for (int i = 0; i < layers.size(); i++) {
                JSONObject layerInfo = layers.getJSONObject(i);
                String layerTitle = layerInfo.getString("name");
                String layerId = layerInfo.getString("id");
                JSONObject oldLayerInfo = id2LayerInfo.get(layerId);

                long dbLayerId = DBHELPER.getUniqueID(con);
                int layerLevel = layerInfo.get("parentLayer") == null ? 0 : layerId2Level.get(layerInfo.getJSONObject("parentLayer").getString("id")) + 1;
                long pLayerId = layerInfo.get("parentLayer") == null ? -1 : layerId2dbID.get(layerInfo.getJSONObject("parentLayer").getString("id"));
                layerId2Level.put(layerId, layerLevel);
                layerId2dbID.put(layerId, dbLayerId);

                layerStmt.setLong(1, dbLayerId);
                layerStmt.setLong(2, id);
                layerStmt.setString(3, layerId);
                layerStmt.setString(4, layerTitle);
                layerStmt.setInt(5, guessLayerType(layerInfo.getString("geometryType")));
                layerStmt.setInt(6, oldLayerInfo != null ? oldLayerInfo.getInt("query") : 1);
                layerStmt.setInt(7, oldLayerInfo != null ? oldLayerInfo.getInt("ident") : 1);
                layerStmt.setInt(8, layerLevel); // level
                layerStmt.setString(9, oldLayerInfo != null ? oldLayerInfo.getString("comment") : layerTitle); // comment
                layerStmt.setLong(10, pLayerId); // p_layer_id
                layerStmt.setLong(11, i); // sortorder
                layerStmt.setInt(12, oldLayerInfo != null ? oldLayerInfo.getInt("is_special") : 0); // is_special
                layerStmt.setString(13, oldLayerInfo != null ? oldLayerInfo.getString("caption_setting") : ""); // caption_setting
                layerStmt.setString(14, oldLayerInfo != null ? oldLayerInfo.getString("as") : ""); // caption_setting
                layerStmt.setString(15, oldLayerInfo != null ? oldLayerInfo.getString("mi") : "0"); // caption_setting
                layerStmt.setString(16, oldLayerInfo != null ? oldLayerInfo.getString("mf") : ""); // caption_setting
                layerStmt.execute();

                fieldStmt.setLong(7, dbLayerId); // p_id
                JSONArray fieldArray = layerInfo.getJSONArray("fields");
                if (fieldArray != null) {
                    List<String> okFieldList = new ArrayList<String>();
                    for (int j = 0; j < fieldArray.size(); j++) {
                        JSONObject fieldJson = fieldArray.getJSONObject(j);
                        String shapeFieldName = fieldJson.getString("name");
                        JSONObject oldFieldJson = id2FieldInfo.get(layerTitle + "-" + shapeFieldName);
                        fieldStmt.setLong(1, DBHELPER.getUniqueID(con));
                        fieldStmt.setString(2, shapeFieldName);
                        fieldStmt.setString(3, oldFieldJson != null ? oldFieldJson.getString("title") : fieldJson.getString("alias"));
                        fieldStmt.setInt(4, guessFieldDateType(fieldJson.getString("type"))); // 数据类型
                        fieldStmt.setInt(5, oldFieldJson != null ? oldFieldJson.getInt("sortorder") : j);
                        fieldStmt.setInt(6, oldFieldJson != null ? oldFieldJson.getInt("visible") : 1);
                        fieldStmt.setInt(8, oldFieldJson != null ? oldFieldJson.getInt("query") : 0);
                        fieldStmt.setInt(9, oldFieldJson != null ? oldFieldJson.getInt("is_special") : 0);
                        fieldStmt.setInt(10, oldFieldJson != null ? oldFieldJson.getInt("special_type") : 0);
                        fieldStmt.setString(11, fixShapeFieldName2OracleFieldName(tempStmt, shapeFieldName, okFieldList));
                        fieldStmt.execute();
                    }
                }
            }
        } finally {

            DBHELPER.closeStatement(tempStmt);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(layerStmt);
            DBHELPER.closeStatement(fieldStmt);
        }

    }

    private int guessFieldDateType(String type) {
        if (StringUtils.containsIgnoreCase(type, "String") || StringUtils.containsIgnoreCase(type, "GUID") || StringUtils.containsIgnoreCase(type, "GlobalID"))
            return 0;
        else if (StringUtils.containsIgnoreCase(type, "Double") || StringUtils.containsIgnoreCase(type, "Single") || StringUtils.containsIgnoreCase(type, "Float") || StringUtils.containsIgnoreCase(type, "Number") || StringUtils.containsIgnoreCase(type, "Int") || StringUtils.containsIgnoreCase(type, "Long"))
            return 1;
        else if (StringUtils.containsIgnoreCase(type, "Date"))
            return 2;
        else if (StringUtils.containsIgnoreCase(type, "esriFieldTypeOID"))
            return 4;
        else if (StringUtils.containsIgnoreCase(type, "esriFieldTypeGeometry"))
            return 5;
        else
            return 3;
    }

    private int guessLayerType(String type) {
        if (StringUtils.contains(type, "Point"))
            return 0;
        else if (StringUtils.contains(type, "Polyline"))
            return 1;
        else if (StringUtils.contains(type, "Polygon"))
            return 2;
        else
            return 3;

    }

    /**
     * @param con
     * @param id
     * @param id2LayerInfo
     * @param id2FieldInfo
     * @throws SQLException
     */
    private void backupLayerAndFields(Connection con, long id, Map<String, JSONObject> id2LayerInfo, Map<String, JSONObject> id2FieldInfo) throws SQLException {
        PreparedStatement layerStmt = null;
        PreparedStatement fieldStmt = null;
        Map<Long, String> layerId2Title = new HashMap<Long, String>();
        try {
            layerStmt = con.prepareStatement("select name,t.query_enabled,t.ident_enabled,t.layer_comment,id,is_special,RECORD_CAPTION_SETTING,equal_layer_as,user_marker_id,user_marker_primary_field from webgis_layer t where p_id=?");
            fieldStmt = con.prepareStatement("select t.id,t.name,t.title,t.sortorder,t.visible_enabled,t.p_id,t.query_enabled,is_special,special_type from webgis_layer_fields t where p_id in (select id from webgis_layer where p_id=?)");
            layerStmt.setLong(1, id);
            ResultSet rs = layerStmt.executeQuery();
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("query", rs.getInt(2));
                json.put("ident", rs.getInt(3));
                json.put("comment", rs.getString(4));
                json.put("id", rs.getLong(5));
                json.put("is_special", rs.getInt(6));
                json.put("caption_setting", rs.getString("RECORD_CAPTION_SETTING"));
                json.put("as", rs.getString("equal_layer_as"));
                json.put("mi", rs.getString("user_marker_id"));
                json.put("mf", rs.getString("user_marker_primary_field"));
                layerId2Title.put(rs.getLong(5), rs.getString(1));
                id2LayerInfo.put(rs.getString(1), json);
            }

            fieldStmt.setLong(1, id);
            rs = fieldStmt.executeQuery();
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("id", rs.getLong(1));
                json.put("name", rs.getString(2));
                json.put("title", rs.getString(3));
                json.put("sortorder", rs.getLong(4));
                json.put("visible", rs.getInt(5));
                json.put("layerId", rs.getLong(6));
                json.put("query", rs.getInt(7));
                json.put("is_special", rs.getInt(8));
                json.put("special_type", rs.getInt(9));
                id2FieldInfo.put(layerId2Title.get(rs.getLong(6)) + "-" + rs.getString(2), json);
            }
        } finally {
            DBHELPER.closeStatement(fieldStmt);
            DBHELPER.closeStatement(layerStmt);
        }
    }

    /**
     * 注册图集项
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject registerMapLayerCollectionItem(Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        long pId = Convert.str2Long(params.get("p_id"));
        long collectionId = Convert.str2Long(params.get("collection_id"));
        String[] resourceIds = params.get("ids").split(",");
        Connection con = null;
        CallableStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareCall("{call proc_webgis_layer2collection(?,?,?)}");
            for (String id : resourceIds) {
                stmt.setLong(1, pId);
                stmt.setLong(2, collectionId);
                stmt.setLong(3, Convert.str2Long(id));
                stmt.execute();
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 移动资源
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject MoveResourceTo(Map<String, String> params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        long pId = Convert.str2Long(params.get("pid"));
        String[] resourceIds = params.get("ids").split(",");
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("update webgis_layer_collection_item set p_id = ? where id = ? ");
            for (String id : resourceIds) {
                stmt.setLong(1, pId);
                stmt.setLong(2, Convert.str2Long(id));
                stmt.execute();
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取地图配置
     * 
     * @param appId
     * @param userId
     * @return
     * @throws Exception
     */
    public String getAppConfig(long appId, long userId, long appVersion, long layerVersion) throws Exception {
        JSONObject json = new JSONObject();
        Connection con = null;
        PreparedStatement appStmt = null;
        PreparedStatement layerCollectionVersionStmt = null;
        PreparedStatement userDistrictStmt = null;
        PreparedStatement userDistrictStmtEx = null;
        try {
            con = DBHELPER.getConnection();

            long layerCollectionId = -1l;
            long containId = -1;
            long serverAppVersion = -1;
            long serverLayerCollectionVersion = -1;
            int wkid = spatialConfig.wkid;

            // 获取地图应用的基本信息
            appStmt = con.prepareStatement("select id,nvl(l_c_id,-1),max_level,widget_contain_id,version,wkid from webgis_app where id=?");
            appStmt.setLong(1, appId);
            ResultSet rs = appStmt.executeQuery();
            if (rs.next()) {
                appId = rs.getLong(1);
                layerCollectionId = rs.getLong(2);
                containId = rs.getLong(4);
                serverAppVersion = rs.getLong(5);
            }

            // 获取地图应用图集的基本信息
            layerCollectionVersionStmt = con.prepareStatement("select version from webgis_layer_collection where id=?");
            layerCollectionVersionStmt.setLong(1, layerCollectionId);
            rs = layerCollectionVersionStmt.executeQuery();
            if (rs.next())
                serverLayerCollectionVersion = rs.getLong(1);

            // 地图应用APP配置
            json.put("appVersion", serverAppVersion);
            json.put("wkid", wkid);
            json.put("appId", appId);

            if (serverAppVersion != appVersion) {
                JSONObject appConfigJson = (JSONObject) SystemCacheManager.getInstance().getWebClientObject(appId + "-" + serverAppVersion);
                if (appConfigJson == null) {
                    SystemCacheManager.getInstance().removeWebClientByPrefix(appId + "-");
                    appConfigJson = generalMapAppCommonConfig(con, appId, containId);
                    SystemCacheManager.getInstance().putWebClientObject(appId + "-" + serverAppVersion, appConfigJson);
                }
                json.put("appConfig", appConfigJson);
            }

            // 地图图集配置
            json.put("layerVersion", serverLayerCollectionVersion);

            if (serverLayerCollectionVersion != layerVersion) {
                JSONObject layerConfigJson = (JSONObject) SystemCacheManager.getInstance().getWebClientObject(layerCollectionId + "-" + serverLayerCollectionVersion);
                if (layerConfigJson == null) {
                    SystemCacheManager.getInstance().removeWebClientByPrefix(layerCollectionId + "-");
                    layerConfigJson = generalMapLayerConfig(con, layerCollectionId);
                    SystemCacheManager.getInstance().putWebClientObject(layerCollectionId + "-" + serverLayerCollectionVersion, layerConfigJson);
                }
                json.put("layerConfig", layerConfigJson);
            }

            String userLayerOverlay = getWebGISUserConfig(con, "layerOverlay", "" + userId, "" + layerCollectionId, "", "");
            if (StringUtils.isEmpty(userLayerOverlay)) {
                json.put("layerSortConfig", new JSONArray());
                json.put("serverId2Alpha", new JSONObject());
            } else {
                JSONObject layerOverlayJson = JSONUtils.parserJSONObject(userLayerOverlay);
                json.put("layerSortConfig", layerOverlayJson.getJSONArray("serverIds"));
                json.put("serverId2Alpha", layerOverlayJson.get("serverId2Alpha"));
            }
            long[] districtIds = new long[] { -1 };
            json.put("userMapExtent", getUserDistrictExtent(con, userId, wkid, districtIds));
            userDistrictStmt = con.prepareStatement("select  id,name,code,geometry_area,geometry_length from sys_district where p_id=? order by sortorder");
            userDistrictStmtEx = con.prepareStatement("select  id,name,code,geometry_area,geometry_length from sys_district_Ex where p_id=? order by sortorder");
            json.put("userDistrictTree", getUserDistrictTree(userDistrictStmt, districtIds[0], 0));
            json.put("userDistrictTreeEx", getUserDistrictTree(userDistrictStmtEx, districtIds[0], 0));
            json.put("cityname", getCityName());
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(layerCollectionVersionStmt);
            DBHELPER.closeStatement(appStmt);
            DBHELPER.closeStatement(userDistrictStmt);
            DBHELPER.closeStatement(userDistrictStmtEx);
            DBHELPER.closeConnection(con);
        }
        System.out.println(json.toString());
        return json.toString();
    }

    /**
     * 获取用户行政区域
     * 
     * @param con
     * @param stmt
     * @param userId
     * @param level
     * @return
     * @throws ExceptionW
     */
    private JSONArray getUserDistrictTree(PreparedStatement stmt, long pId, int level) throws Exception {
        stmt.setLong(1, pId); 
        JSONArray result = null;
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            if (result == null)
                result = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("id", rs.getLong(1));
            json.put("name", rs.getString(2));
            json.put("code", rs.getString(3));
            json.put("geometry_area", rs.getDouble("geometry_area"));
            json.put("geometry_length", rs.getDouble("geometry_length"));
            result.add(json);
        }
        if (result != null && level < 3) {
            level++;
            for (int i = 0; i < result.size(); i++) {
                JSONObject json = result.getJSONObject(i);
                JSONArray child = getUserDistrictTree(stmt, json.getLong("id"), level);
                if (child != null)
                    json.put("children", child);
            }
        }
        return result;
    }
    
    /**
     * 获取市区名称
     */
    private String getCityName(){
    	String cityName="";
    	cityName=RuntimeConfig.getInstance().getCityName();
    	return cityName;
    }
    /**
     * 获取地图配置
     * 
     * @param appName
     * @param userId
     * @return
     * @throws Exception
     */
    public String getAppConfig(String appName, long userId, long appVersion, long layerVersion) throws Exception {
        JSONObject json = new JSONObject();
        Connection con = null;
        PreparedStatement appStmt = null;
        PreparedStatement layerCollectionVersionStmt = null;
        try {
            con = DBHELPER.getConnection();

            long layerCollectionId = -1l;
            long containId = -1;
            long serverAppVersion = -1;
            long serverLayerCollectionVersion = -1;
            int wkid = spatialConfig.wkid;

            // 获取地图应用的基本信息
            appStmt = con.prepareStatement("select id,nvl(l_c_id,-1),max_level,widget_contain_id,version,wkid from webgis_app where name=?");
            appStmt.setString(1, appName);
            ResultSet rs = appStmt.executeQuery();
            long appId = -1;
            if (rs.next()) {
                appId = rs.getLong(1);
                layerCollectionId = rs.getLong(2);
                containId = rs.getLong(4);
                serverAppVersion = rs.getLong(5);
            }

            // 获取地图应用图集的基本信息
            layerCollectionVersionStmt = con.prepareStatement("select version from webgis_layer_collection where id=?");
            layerCollectionVersionStmt.setLong(1, layerCollectionId);
            rs = layerCollectionVersionStmt.executeQuery();
            if (rs.next())
                serverLayerCollectionVersion = rs.getLong(1);

            // 地图应用APP配置
            json.put("appVersion", serverAppVersion);
            json.put("wkid", wkid);
            json.put("appId", appId);

            if (serverAppVersion != appVersion) {
                JSONObject appConfigJson = (JSONObject) SystemCacheManager.getInstance().getWebClientObject(appId + "-" + serverAppVersion);
                if (appConfigJson == null) {
                    SystemCacheManager.getInstance().removeWebClientByPrefix(appId + "-");
                    appConfigJson = generalMapAppCommonConfig(con, appId, containId);
                    SystemCacheManager.getInstance().putWebClientObject(appId + "-" + serverAppVersion, appConfigJson);
                }
                json.put("appConfig", appConfigJson);
            }

            // 地图图集配置
            json.put("layerVersion", serverLayerCollectionVersion);

            if (serverLayerCollectionVersion != layerVersion) {
                JSONObject layerConfigJson = (JSONObject) SystemCacheManager.getInstance().getWebClientObject(layerCollectionId + "-" + serverLayerCollectionVersion);
                if (layerConfigJson == null) {
                    SystemCacheManager.getInstance().removeWebClientByPrefix(layerCollectionId + "-");
                    layerConfigJson = generalMapLayerConfig(con, layerCollectionId);
                    SystemCacheManager.getInstance().putWebClientObject(layerCollectionId + "-" + serverLayerCollectionVersion, layerConfigJson);
                }
                json.put("layerConfig", layerConfigJson);
            }

            long[] ids = new long[1];
            json.put("userMapExtent", getUserDistrictExtent(con, userId, wkid, ids));
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(layerCollectionVersionStmt);
            DBHELPER.closeStatement(appStmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 获取用户行政区划矩形边界范围
     * 
     * @param con
     * @param userId
     * @return
     * @throws Exception
     */
    private double[] getUserDistrictExtent(Connection con, long userId, int toWKID, long[] ids) throws Exception {
        PreparedStatement stmt = null;
        double[] result = null;
        try {
            stmt = con.prepareStatement("select wkt,wkid,id from sys_district where code in (select district_code from sys_userinfo where id=?)");
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ids[0] = rs.getLong(3);
                JGeometry geometry = OracleSpatialUtils.wkt2geom(rs.getBytes(1));
                geometry.setSRID(rs.getInt(2));
                result = geometry.getMBR();
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return result;
    }

    /**
     * 
     * @param con
     * @param layerCollectionId
     * @return
     * @throws SQLException
     * @throws Exception
     */
    private JSONObject generalMapLayerConfig(Connection con, long layerCollectionId) throws SQLException, Exception {
        ResultSet rs = null;
        PreparedStatement layerStmt = null;
        PreparedStatement layerInfostmt = null;
        PreparedStatement serverStmt = null;
        PreparedStatement paramStmt = null;
        JSONObject json = new JSONObject();
        try {
            // 地图图集
            layerStmt = con.prepareStatement("select id,name,layer_id,resource_type,type from webgis_layer_collection_item where p_id = ? order by sortorder");
            JSONObject layerTreeJson = new JSONObject();
            layerTreeJson.put("label", "一张图图集");
            layerTreeJson.put("root", true);
            getLayerTreeConfig(layerTreeJson, layerStmt, layerCollectionId);
            json.put("layerTree", layerTreeJson);

            // 获取所有的地图服务及图层信息
            layerInfostmt = con.prepareStatement("select id,p_id service_id,query_enabled,ident_enabled,name,sortorder from webgis_layer where exists (select 'x' from webgis_layer_collection_item where layer_id=webgis_layer.id and collection_id=?)");
            layerInfostmt.setLong(1, layerCollectionId);
            rs = layerInfostmt.executeQuery();
            while (rs.next()) {
                JSONObject layerInfoJson = new JSONObject();
                layerInfoJson.put("id", rs.getLong(1));
                layerInfoJson.put("serverId", rs.getLong(2));
                layerInfoJson.put("query", rs.getInt(3));
                layerInfoJson.put("ident", rs.getInt(4));
                layerInfoJson.put("name", rs.getString(5));
                layerInfoJson.put("sortorder", rs.getLong(6));
                JSONUtils.append(json, "layerInfos", layerInfoJson);
            }

            paramStmt = con.prepareStatement("select * from webgis_service_params where p_id=?");

            // 获取地图服务信息
            serverStmt = con
                    .prepareStatement("select id,type,url,map_extend,init_extend,ident_enabled,query_enabled,nvl(proxy_cache,0),proxy_url,name,nvl((select max(type) from webgis_layer where type in (0,1,2) and p_id=webgis_services.id),0) as maxLayerType from webgis_services where id in (select distinct p_id from webgis_layer  where exists (select 'x' from webgis_layer_collection_item where layer_id=webgis_layer.id and collection_id=?) union select layer_id p_id from webgis_layer_collection_item where collection_id=?)");
            serverStmt.setLong(1, layerCollectionId);
            serverStmt.setLong(2, layerCollectionId);
            rs = serverStmt.executeQuery();
            while (rs.next()) {
                JSONObject serverInfoJson = new JSONObject();
                serverInfoJson.put("id", rs.getLong(1));
                serverInfoJson.put("type", rs.getString(2));
                serverInfoJson.put("url", rs.getString(3));
                serverInfoJson.put("mapExtent", rs.getString(4));
                serverInfoJson.put("initExtent", rs.getString(5));
                serverInfoJson.put("ident", rs.getInt(6) == 0);
                serverInfoJson.put("query", rs.getInt(7) == 0);
                serverInfoJson.put("proxyCache", rs.getInt(8) == 1);
                serverInfoJson.put("proxyUrl", rs.getString(9));
                serverInfoJson.put("name", rs.getString(10));
                serverInfoJson.put("maxLayerType", rs.getInt(11));
                paramStmt.setLong(1, rs.getLong(1));
                ResultSet paramRs = paramStmt.executeQuery();
                JSONObject extParamJson = new JSONObject();
                while (paramRs.next())
                    extParamJson.put(paramRs.getString("param_name"), paramRs.getString("param_value"));
                if (!extParamJson.isEmpty())
                    serverInfoJson.put("extParams", extParamJson);

                JSONUtils.append(json, "serverInfo", serverInfoJson);
            }
        } finally {
            DBHELPER.closeStatement(serverStmt);
            DBHELPER.closeStatement(layerInfostmt);
            DBHELPER.closeStatement(layerStmt);
            DBHELPER.closeStatement(paramStmt);
        }
        return json;
    }

    /**
     * 生成通用配置
     * 
     * @param json
     * @param con
     * @param appId
     * @param containId
     * @throws Exception
     */
    private JSONObject generalMapAppCommonConfig(Connection con, long appId, long containId) throws Exception {
        ResultSet rs = null;
        PreparedStatement baseStmt = null;
        PreparedStatement containStmt = null;
        PreparedStatement controlStmt = null;
        PreparedStatement paramStmt = null;
        JSONObject json = new JSONObject();
        try {
            // 获取容器信息
            containStmt = con.prepareStatement("select name,url,w,w_unit,h,h_unit,left_v,top_v,right_v,bottom_v from webgis_map_module where id=?");
            containStmt.setLong(1, containId);
            rs = containStmt.executeQuery();
            if (rs.next()) {
                JSONObject containJson = new JSONObject();
                containJson.put("name", rs.getString(1));
                containJson.put("url", rs.getString(2));
                JSONObject posJson = new JSONObject();
                posJson.put("width", rs.getString(3));
                posJson.put("width_unit", rs.getInt(4));
                posJson.put("height", rs.getString(5));
                posJson.put("height_unit", rs.getInt(6));
                posJson.put("left", rs.getString(7));
                posJson.put("top", rs.getString(8));
                posJson.put("right", rs.getString(9));
                posJson.put("bottom", rs.getString(10));
                containJson.put("pos", posJson);
                json.put("widgetContain", containJson);
            }

            // 控件信息
            controlStmt = con.prepareStatement("select name,url,w,w_unit,h,h_unit,left_v,top_v,right_v,bottom_v from webgis_app_module where app_id=?");
            controlStmt.setLong(1, appId);
            rs = controlStmt.executeQuery();
            while (rs.next()) {
                JSONObject controlJson = new JSONObject();
                controlJson.put("name", rs.getString(1));
                controlJson.put("url", rs.getString(2));
                JSONObject posJson = new JSONObject();
                posJson.put("width", rs.getString(3));
                posJson.put("width_unit", rs.getInt(4));
                posJson.put("height", rs.getString(5));
                posJson.put("height_unit", rs.getInt(6));
                posJson.put("left", rs.getString(7));
                posJson.put("top", rs.getString(8));
                posJson.put("right", rs.getString(9));
                posJson.put("bottom", rs.getString(10));
                controlJson.put("pos", posJson);
                JSONUtils.append(json, "widgetConrol", controlJson);

            }

            paramStmt = con.prepareStatement("select * from webgis_service_params where p_id=?");

            // 获取地图底图
            baseStmt = con.prepareStatement("select b.type, b.url, a.name, a.purpose,a.icon_url,b.init_extend,b.map_extend,b.proxy_cache,b.id server_id,decode(nvl(a.group_number,0),0,rownum+1000,a.group_number) group_number from webgis_basemap a, webgis_services b where a.s_id = b.id and a.p_id =? order by a.sortorder");
            baseStmt.setLong(1, appId);
            rs = baseStmt.executeQuery();
            Map<Integer, JSONObject> groupId2Json = new HashMap<Integer, JSONObject>();
            while (rs.next()) {
                int groupId = rs.getInt("group_number");
                int purpose = rs.getInt(4);
                JSONObject baseJson = new JSONObject();
                baseJson.put("type", rs.getString(1));
                baseJson.put("url", rs.getString(2));
                baseJson.put("label", rs.getString(3));
                baseJson.put("purpose", purpose);
                baseJson.put("icon", rs.getString(5));
                baseJson.put("initExtent", rs.getString(6));
                baseJson.put("mapExtent", rs.getString(7));
                baseJson.put("proxyCache", rs.getInt("proxy_cache") == 1);
                long serverId = rs.getLong("server_id");
                baseJson.put("id", serverId);

                paramStmt.setLong(1, serverId);
                ResultSet paramRs = paramStmt.executeQuery();
                JSONObject extParamJson = new JSONObject();
                while (paramRs.next())
                    extParamJson.put(paramRs.getString("param_name"), paramRs.getString("param_value"));
                if (!extParamJson.isEmpty())
                    baseJson.put("extParams", extParamJson);

                if (groupId2Json.containsKey(groupId)) {
                    if (purpose == 3)
                        JSONUtils.append(groupId2Json.get(groupId), "children", baseJson);
                    else if (purpose == 0)
                        JSONUtils.append(groupId2Json.get(groupId), "layers", baseJson);
                } else {
                    JSONUtils.append(json, "baseLayers", baseJson);
                    groupId2Json.put(groupId, baseJson);
                }
            }
        } finally {
            DBHELPER.closeStatement(baseStmt);
            DBHELPER.closeStatement(containStmt);
            DBHELPER.closeStatement(controlStmt);
            DBHELPER.closeStatement(paramStmt);
        }
        return json;
    }

    /**
     * 获取图集图层树
     * 
     * @param json
     * @param stmt
     * @param layerCollectionId
     * @throws Exception
     */
    private void getLayerTreeConfig(JSONObject json, PreparedStatement stmt, long p_id) throws Exception {
        List<JSONObject> list = new ArrayList<JSONObject>();
        stmt.setLong(1, p_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            JSONObject layerJson = new JSONObject();
            layerJson.put("id", rs.getLong(1));
            layerJson.put("label", rs.getString(2));
            layerJson.put("layerId", rs.getString(3));
            layerJson.put("layerType", rs.getInt(4));
            layerJson.put("type", rs.getInt(5));
            JSONUtils.append(json, "children", layerJson);
            if (rs.getInt(5) == 0)
                list.add(layerJson);
        }
        for (JSONObject layerJson : list)
            getLayerTreeConfig(layerJson, stmt, layerJson.getLong("id"));
    }

    /**
     * 得到一个图层的具体实体内容
     * 
     * @param layerId
     * @param keyField
     * @param keyValue
     * @return
     */
    public String getLayerFeatureProperty(String layerId, String keyField, String keyValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 获取地图比对方案
     * 
     * @param appId
     * @param userId
     * @return
     * @throws Exception
     */
    public String getCompareSchema(long appId, long userId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select id, name,config,type from webgis_map_compare_schema where app_id=? and user_id=? and p_id=? and rownum<500 order by sortorder desc");
            stmt.setLong(1, appId);
            stmt.setLong(2, userId);
            getCompareSchema(stmt, -1l, json);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 获取地图比对方案
     * 
     * @param stmt
     * @param i
     * @param json
     * @throws SQLException
     */
    private void getCompareSchema(PreparedStatement stmt, long pId, JSONObject json) throws SQLException {
        stmt.setLong(3, pId);
        ResultSet rs = stmt.executeQuery();
        List<JSONObject> list = new ArrayList<JSONObject>();
        while (rs.next()) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("id", rs.getLong(1));
            itemJson.put("caption", rs.getString(2));
            if (rs.getInt(4) == 1)
                itemJson.put("config", JSONUtils.parserJSONObject(Convert.bytes2Str(rs.getBytes(3))));
            itemJson.put("type", rs.getInt(4));
            JSONUtils.append(json, "children", itemJson);
            list.add(itemJson);
        }
        for (JSONObject itemJson : list) {
            if (itemJson.getInt("type") == 0)
                getCompareSchema(stmt, itemJson.getLong("id"), itemJson);
        }
    }

    /**
     * 新建地图比对方案
     * 
     * @param appId
     * @param userId
     * @param type
     * @param pId
     * @param config
     * @param config2
     * @return
     * @throws Exception
     */
    public String newCompareSchema(long appId, long userId, long pId, int type, String caption, String config) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            long uId = DBHELPER.getUniqueID(con);
            stmt = con.prepareStatement("insert into webgis_map_compare_schema (id, app_id, user_id, type, name, sortorder, config,p_id) values (?, ?, ?, ?, ?, ?, ?,?)");
            stmt.setLong(1, uId);
            stmt.setLong(2, appId);
            stmt.setLong(3, userId);
            stmt.setLong(4, type);
            stmt.setString(5, caption);
            stmt.setLong(6, uId);
            stmt.setBytes(7, Convert.str2Bytes(config));
            stmt.setLong(8, pId);

            stmt.execute();
            json.put("id", uId);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 删除地图比对方案
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public String deleteCompareSchema(long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.execute("delete from webgis_map_compare_schema where id=" + id, con);
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 得到一个具体的地图比对方案配置
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public String getCompareSchemaConfig(long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select config from webgis_map_compare_schema where id=" + id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("config", Convert.bytes2Str(rs.getBytes(1)));
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }
    /**
     * 创建分享链接
     * 
     * @param appId
     * @param userId
     * @return
     * @throws Exception
     */
    public String newMapShare(long state,long appId, long userId, long pId, int type, String caption, String config) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            long uId = DBHELPER.getUniqueID(con);
            stmt = con.prepareStatement("insert into webgis_map_share (id, app_id, user_id, type, name, sortorder, config,p_id,state) values (?, ?, ?, ?, ?, ?, ?,?, ?)");
            stmt.setLong(1, uId);
            stmt.setLong(2, appId);
            stmt.setLong(3, userId);
            stmt.setLong(4, type);
            stmt.setString(5, caption);
            stmt.setLong(6, uId);
            stmt.setBytes(7, Convert.str2Bytes(config));
            stmt.setLong(8, pId);
            stmt.setLong(9, state);
            stmt.execute();
            json.put("id", uId);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }
    /**
     * 得到一个具体的地图分享配置
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public String getMapShareConfig(long state) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select config from webgis_map_share where state=" + state);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("config", Convert.bytes2Str(rs.getBytes(1)));
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }
    /**
     * 获取地图收藏夹
     * 
     * @param appId
     * @param userId
     * @return
     * @throws Exception
     */
    public String getMapFavorite(long appId, long userId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select id, name,config,type from webgis_map_favorite where app_id=? and user_id=? and p_id=? and rownum<500 order by sortorder desc");
            stmt.setLong(1, appId);
            stmt.setLong(2, userId);
            getMapFavorite(stmt, -1l, json);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 获取地图收藏夹
     * 
     * @param stmt
     * @param i
     * @param json
     * @throws SQLException
     */
    private void getMapFavorite(PreparedStatement stmt, long pId, JSONObject json) throws SQLException {
        stmt.setLong(3, pId);
        ResultSet rs = stmt.executeQuery();
        List<JSONObject> list = new ArrayList<JSONObject>();
        while (rs.next()) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("id", rs.getLong(1));
            itemJson.put("caption", rs.getString(2));
            if (rs.getInt(4) == 1)
                itemJson.put("config", JSONUtils.parserJSONObject(Convert.bytes2Str(rs.getBytes(3))));
            itemJson.put("type", rs.getInt(4));
            JSONUtils.append(json, "children", itemJson);
            list.add(itemJson);
        }
        for (JSONObject itemJson : list) {
            if (itemJson.getInt("type") == 0)
                getMapFavorite(stmt, itemJson.getLong("id"), itemJson);
        }
    }

    /**
     * 新建地图收藏夹
     * 
     * @param appId
     * @param userId
     * @param type
     * @param pId
     * @param config
     * @param config2
     * @return
     * @throws Exception
     */
    public String newMapFavorite(long appId, long userId, long pId, int type, String caption, String config) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            long uId = DBHELPER.getUniqueID(con);
            stmt = con.prepareStatement("insert into webgis_map_favorite (id, app_id, user_id, type, name, sortorder, config,p_id) values (?, ?, ?, ?, ?, ?, ?,?)");
            stmt.setLong(1, uId);
            stmt.setLong(2, appId);
            stmt.setLong(3, userId);
            stmt.setLong(4, type);
            stmt.setString(5, caption);
            stmt.setLong(6, uId);
            stmt.setBytes(7, Convert.str2Bytes(config));
            stmt.setLong(8, pId);

            stmt.execute();
            json.put("id", uId);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 删除地图收藏夹
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public String deleteMapFavorite(long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.execute("delete from webgis_map_favorite where id=" + id, con);
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 得到一个具体的地图收藏夹配置
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public String getMapFavoriteConfig(long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select config from webgis_map_favorite where id=" + id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("config", Convert.bytes2Str(rs.getBytes(1)));
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 获取服务或图层信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String getServerOrLayerInfo(String params) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            JSONObject paramJson = JSONUtils.parserJSONObject(params);
            if (paramJson.getBoolean("isServer"))
                getServerLayerInfos(con, json, paramJson.getString("objectId"));
            else
                getLayerInfos(con, json, paramJson.getString("objectId"));
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 
     * @param con
     * @param json
     * @param layerId
     * @throws Exception
     */
    private void getLayerInfos(Connection con, JSONObject json, String layerId) throws Exception {
        String sql = "select id,nvl(layer_comment,title) title from webgis_layer where id in (" + layerId + ") order by sortorder";
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject layerJson = new JSONObject();
                WebGISLayerFields layerField = getLayerFields(con, rs.getLong(1));
                layerJson.put("id", rs.getString(1));
                layerJson.put("name", rs.getString(2));
                layerJson.put("fields", layerField.fieldJsonArray);
                JSONUtils.append(json, "layers", layerJson);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 获取图层字段信息
     * 
     * @param con
     * @param layerId
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public WebGISLayerFields getLayerFields(Connection con, long layerId) throws Exception, SQLException {
        String cacheKey = "WebGISLayer-FieldInfo-" + layerId;
        WebGISLayerFields layerFields = (WebGISLayerFields) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
        long version = DBHELPER.executeScalarLong("select version from webgis_layer where id=" + layerId, con);
        if (layerFields != null && layerFields.version == version)
            return layerFields;

        PreparedStatement stmt = null;

        try {
            layerFields = new WebGISLayerFields();
            layerFields.captionTemplate = StringUtils.trim(DBHELPER.executeScalarString("select record_caption_setting from webgis_layer where id=" + layerId, con));
            layerFields.version = version;
            layerFields.fieldJsonArray = new JSONArray();
            stmt = con.prepareStatement("select SCHEMA_FIELD_NAME,name,title,visible_enabled,data_type,query_enabled,group_enabled,is_caption from webgis_layer_fields where p_id=? order by sortorder");
            stmt.setLong(1, layerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int dataType = rs.getInt(5);
                boolean visible = rs.getInt(4) == 1;
                boolean isCaption = rs.getInt(8) == 1;
                boolean isQuery = rs.getInt(6) == 1;
                String schemaFieldName = rs.getString(1);
                String arcGISFieldName = rs.getString(2);
                String arcGISFieldTitle = rs.getString(3);
                if (visible && dataType <= 2) {
                    layerFields.schemaFieldList.add(schemaFieldName);
                    layerFields.fieldList.add(arcGISFieldName);
                    layerFields.fieldComment.add(arcGISFieldTitle);
                    layerFields.schemaField2Comment.put(schemaFieldName, arcGISFieldTitle);
                }
                layerFields.schemaFieldName2LayerFieldName.put(schemaFieldName, arcGISFieldName);
                if (dataType == 4) {
                    layerFields.objectFieldName = arcGISFieldName;
                    layerFields.schemaFieldName2LayerFieldName.put("Q_UID", arcGISFieldName);
                    layerFields.schemaFieldName2LayerFieldName.put("Q_OBJECTID", arcGISFieldName);

                }
                layerFields.schemaFieldName2DataType.put(schemaFieldName, dataType);
                if (isCaption)
                    layerFields.captionFieldList.add(schemaFieldName);
                layerFields.schemaFieldName2IsQuery.put(schemaFieldName, isQuery);

                JSONObject fieldJson = new JSONObject();
                fieldJson.put("name", schemaFieldName);
                fieldJson.put("title", arcGISFieldTitle);
                fieldJson.put("visible", rs.getInt(4) == 1);
                fieldJson.put("query", rs.getInt(6) == 1);
                fieldJson.put("group", rs.getInt(7) == 1);
                fieldJson.put("caption", rs.getInt(8) == 1);
                layerFields.fieldJsonArray.add(fieldJson);
            }
            if (StringUtils.isEmpty(layerFields.captionTemplate)) {
                if (!layerFields.captionFieldList.isEmpty()) {
                    layerFields.captionTemplate = "{";
                    for (String str : layerFields.captionFieldList)
                        layerFields.captionTemplate += str + "}{";
                    layerFields.captionTemplate = StringUtils.substring(layerFields.captionTemplate, 0, layerFields.captionTemplate.length() - 1);
                } else {
                    layerFields.captionTemplate = "{" + layerFields.objectFieldName + "}";
                }
            }
            layerFields.captionFieldList.clear();
            for (Entry<String, String> entry : layerFields.schemaFieldName2LayerFieldName.entrySet()) {
                if (StringUtils.contains(layerFields.captionTemplate, "{" + entry.getKey() + "}"))
                    layerFields.captionFieldMap.put(entry.getKey(), entry.getValue());
            }
            SystemCacheManager.getInstance().putWebGISItem(cacheKey, layerFields);
        } finally {
            DBHELPER.closeStatement(stmt);
        }

        return layerFields;
    }

    /**
     * 纠正字段名称
     * 
     * @param fieldName
     * @return
     */
    public static String fixShapeFieldName2OracleFieldName(Statement stmt, String fieldName, List<String> okFieldList) {
        fieldName = fieldName.toUpperCase();
        if (fieldName.getBytes().length != fieldName.length() || fieldName.getBytes().length >= 31) {
            int index = 1;
            fieldName = "F" + index;
            while (okFieldList.indexOf(fieldName) != -1) {
                fieldName = fieldName + (++index);
            }
        } else {
            String sql = "select 1 as" + fieldName + " from dual";
            try {
                stmt.executeQuery(sql);
            } catch (Exception e) {
                fieldName = "F" + SecurityUtils.md5(fieldName).substring(8, 24);
            }
        }
        okFieldList.add(fieldName);
        return fieldName;
    }

    /**
     * 
     * @param con
     * @param json
     * @param serverId
     * @throws Exception
     */
    private void getServerLayerInfos(Connection con, JSONObject json, String serverId) throws Exception {
        String sql = "select id,nvl(layer_comment,title) title from webgis_layer where name=equal_layer_as and p_id = ? order by sortorder";
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, serverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject layerJson = new JSONObject();
                layerJson.put("id", rs.getString(1));
                layerJson.put("name", rs.getString(2));
                layerJson.put("fields", getLayerFields(con, rs.getLong(1)).fieldJsonArray);
                JSONUtils.append(json, "layers", layerJson);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 获取地图应用列表
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public JSONObject getMapAppList(ClientLoginInfo loginInfo) throws Exception {
        JSONObject json = new JSONObject();
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            String sql = "";
            if (loginInfo.isRole(-1)) {
                sql = "select id, name from webgis_app  order by sortorder";
                stmt = con.prepareStatement(sql);
            } else {
                sql = "select id, name\n" + //
                        "  from webgis_app\n" + //
                        " where id in (select distinct app_id\n" + //
                        "                from webgis_app_right\n" + //
                        "               where role_id in\n" + //
                        "                     (select r_id from sys_user2role where u_id = ?)\n" + //
                        "                 and app_id is not null)\n" + //
                        " order by sortorder";
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, loginInfo.getId());
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject appJson = new JSONObject();
                appJson.put("id", rs.getString(1));
                appJson.put("name", rs.getString(2));
                JSONUtils.append(json, "mapApps", appJson);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取区域矢量数据
     * 
     * @param appId
     * @return
     * @throws Exception
     */
    public JSONObject getMapAreaNavigatorFeatures(long appId) throws Exception {
        Connection con = null;
        Statement stmt = null;
        PreparedStatement tableStmt = null;
        JSONObject json = null;
        try {
            con = DBHELPER.getNativeConnection();
            long appVersion = DBHELPER.executeScalarLong("select version from webgis_app where id=" + appId, con);

            json = (JSONObject) SystemCacheManager.getInstance().getWebClientObject("area-" + appId + "-" + appVersion);
            if (json == null) {
                SystemCacheManager.getInstance().removeWebClientByPrefix("area-" + appId + "-");
                json = new JSONObject();
                double simpValue = DBHELPER.executeScalarDouble("select area_simp_value from webgis_app where id=" + appId, con);
                tableStmt = con.prepareStatement("select layer_id,display_field,key_field,query_field,sort_field from webgis_app_area_navigator where app_id=? order by id");
                tableStmt.setLong(1, appId);
                ResultSet rs = tableStmt.executeQuery();
                stmt = con.createStatement();
                if (rs.next()) {
                    String layerId = rs.getString(1);
                    String displayField = rs.getString(2);
                    String keyField = rs.getString(3);
                    getMapAreaNavigatorGeometrys(stmt, layerId, displayField, keyField, simpValue, json);
                }
                json.put("r", true);
                SystemCacheManager.getInstance().putWebClientObject("area-" + appId + "-" + appVersion, json);
            }

        } finally {
            DBHELPER.closeStatement(tableStmt);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取区域几何实体
     * 
     * @param stmt
     * @param sql
     * @param keyField
     * @param displayField
     * @param simpValue
     * @param json
     */
    private void getMapAreaNavigatorGeometrys(Statement stmt, String layerId, String displayField, String keyField, double simpValue, JSONObject json) {
        String sql = "select " + displayField + "," + keyField + ",sdo_util.to_wktgeometry(sdo_util.simplify(geometry," + simpValue + ")) from spatial_fs_" + layerId + " where " + keyField + "<>' '";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            List<JSONObject> records = new ArrayList<JSONObject>();
            while (rs.next()) {
                try {
                    JSONObject record = new JSONObject();
                    record.put("label", rs.getString(1));
                    record.put("key", rs.getString(2));
                    String wktStr = rs.getString(3);
                    record.put("wkt", GeometryUtils.wkt2ArcGISGeometryJSON(wktStr));
                    // record.put("center",
                    // GeometryUtils.wkt2ArcGISGeometryJSON(new
                    // String(wkt.fromJGeometry(JGeometry.load(rs.getBytes(4))))));
                    records.add(record);
                } catch (Exception e) {
                    ExceptionUtils.printExceptionTrace(e);
                }
            }
            JSONUtils.append(json, "records", records);
        } catch (Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 获取社会经济指标目录树
     * 
     * @param appId
     * @return
     * @throws Exception
     */
    public JSONObject getStatisticTree(long appId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select id,name,type from tab_statistic_tree where p_id=? order by sortorder");
            JSONObject rootJson = new JSONObject();
            rootJson.put("id", -1);
            rootJson.put("name", "社会经济指标数据");
            rootJson.put("root", true);
            getStatisticTree(rootJson, stmt, -1);
            json.put("items", rootJson);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 递归获取目录树节点
     * 
     * @param parentJson
     * @param stmt
     * @param i
     * @throws SQLException
     */
    private void getStatisticTree(JSONObject parentJson, PreparedStatement stmt, long pId) throws SQLException {
        stmt.setLong(1, pId);
        List<JSONObject> list = new ArrayList<JSONObject>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            JSONObject json = new JSONObject();
            json.put("name", rs.getString(2));
            json.put("id", rs.getLong(1));
            json.put("type", rs.getInt(3));
            if (rs.getInt(3) == 0)
                list.add(json);
            JSONUtils.append(parentJson, "children", json);
        }
        for (JSONObject json : list)
            getStatisticTree(json, stmt, json.getLong("id"));
    }

    /**
     * 获取社会经济指标数据
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public JSONObject getStatisticData(long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select columns_info,records_info from tab_statistic_data where id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("columnsInfo", Convert.bytes2Str(rs.getBytes(1)));
                json.put("records", Convert.bytes2Str(rs.getBytes(2)));
                getStatisticFeatures(con, id, json);
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;

    }

    /**
     * 获取统计实体等信息
     * 
     * @param con
     * @param id
     * @throws SQLException
     */
    private void getStatisticFeatures(Connection con, long id, JSONObject json) throws SQLException {
        PreparedStatement stmt = null;
        PreparedStatement layerStmt = null;
        try {
            stmt = con.prepareStatement("select id,name,show_type,main_fieldname,nvl(chart_type,0) chart_type,value_fieldnames,feature_name,nvl(CHART_USE_CHILDREN_DATA,0) from tab_statistic_special_layer where p_id=? order by sortorder");
            layerStmt = con.prepareStatement("select layer_id,query_field,map_start_level,map_end_level,data_level,data_detail_level,data_scale from tab_statistic_special_features where p_id=? order by id");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject layerJson = new JSONObject();
                layerJson.put("id", rs.getLong(1));
                layerJson.put("name", rs.getString(2));
                layerJson.put("showType", rs.getInt(3));
                layerJson.put("mainFieldName", rs.getString(4));
                layerJson.put("chartType", rs.getInt(5));
                layerJson.put("keyFieldName", rs.getString(7));
                layerJson.put("isChartChildren", rs.getInt(8));
                String valueFieldName = rs.getString(6);
                if (!StringUtils.isEmpty(valueFieldName)) {
                    String[] fieldList = valueFieldName.split(",");
                    for (String field : fieldList) {
                        String label = StringUtils.substringBefore(field, "[");
                        String name = StringUtils.substringBetween(field, "[", "]");
                        if (!StringUtils.isEmpty(label) && !StringUtils.isEmpty(name)) {
                            JSONObject fieldJson = new JSONObject();
                            fieldJson.put("name", name);
                            fieldJson.put("label", label);
                            JSONUtils.append(layerJson, "valueFields", fieldJson);
                        }
                    }
                }

                // 实体配置信息
                layerStmt.setLong(1, rs.getLong(1));
                ResultSet layerRs = layerStmt.executeQuery();
                while (layerRs.next()) {
                    JSONObject featureJson = new JSONObject();
                    featureJson.put("layerId", layerRs.getString(1));
                    featureJson.put("queryField", layerRs.getString(2));
                    featureJson.put("startLevel", layerRs.getInt(3));
                    featureJson.put("endLevel", layerRs.getInt(4));
                    featureJson.put("dataLevel", layerRs.getInt(5));
                    featureJson.put("detailLevel", layerRs.getInt(6));
                    featureJson.put("scale", layerRs.getDouble(7));
                    JSONUtils.append(layerJson, "layers", featureJson);
                }
                JSONUtils.append(json, "charts", layerJson);
            }

        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(layerStmt);
        }
    }

    /**
     * 
     * @param layerId
     * @param scale
     * @param extFieldName
     * @return
     * @throws Exception
     */
    public JSONObject getScaleLayerFeatures(String layerId, double scale, String extFieldName, boolean isCalcCenterPoint) throws Exception {
        String key = layerId + "-" + scale;
        JSONObject json = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(key);
        if (json == null) {
            json = new JSONObject();
            json.put("r", false);
            Connection con = null;
            PreparedStatement stmt = null;
            PreparedStatement featureStmt = null;
            PreparedStatement simpFeatureStmt = null;
            try {
                con = DBHELPER.getNativeConnection();
                stmt = con.prepareStatement("select Q_UID," + extFieldName + " from spatial_fs_" + layerId);
                featureStmt = con.prepareStatement("select geometry from spatial_fs_" + layerId + " where q_uid=?");
                simpFeatureStmt = con.prepareStatement("select sdo_util.simplify(geometry,?) from spatial_fs_" + layerId + " where q_uid=?");
                simpFeatureStmt.setDouble(1, scale);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    JGeometry geometry = getSimpGeometry(featureStmt, simpFeatureStmt, rs.getLong(1));
                    if (geometry != null) {
                        JSONObject recordJson = new JSONObject();
                        recordJson.put("caption", rs.getString(2));
                        recordJson.put("wkt", GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(geometry))));
                        recordJson.put("centerPoint", OracleSpatialUtils.center(geometry));
                        JSONUtils.append(json, "records", recordJson);
                    }
                }
                json.put("r", true);
                SystemCacheManager.getInstance().putWebGISItem(key, json, 30 * 60);
            } finally {
                DBHELPER.closeStatement(featureStmt);
                DBHELPER.closeStatement(simpFeatureStmt);
                DBHELPER.closeConnection(con);
            }

        }
        return json;
    }

    /**
     * 
     * @param featureStmt
     * @param simpFeatureStmt
     * @param id
     * @return
     */
    private JGeometry getSimpGeometry(PreparedStatement featureStmt, PreparedStatement simpFeatureStmt, long id) {
        boolean isOK = false;
        JGeometry result = null;
        try {
            simpFeatureStmt.setLong(2, id);
            ResultSet rs = simpFeatureStmt.executeQuery();
            if (rs.next()) {
                JGeometry shape = JGeometry.load(rs.getBytes(1));
                result = shape;
                isOK = true;
            }
        } catch (Exception e) {
        }
        if (!isOK) {
            try {
                featureStmt.setLong(1, id);
                ResultSet rs = featureStmt.executeQuery();
                if (rs.next()) {
                    JGeometry shape = JGeometry.load(rs.getBytes(1));
                    result = shape;
                    isOK = true;
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 获取位置实体Feature
     * 
     * @param wkid
     * @param id
     * @return
     */
    public JSONObject getDistrictFeature(String id) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        JSONObject result = new JSONObject();
        result.put("r", false);
        try {
            con = DBHELPER.getNativeConnection();
            stmt = con.prepareStatement("select wkt,wkid,id from sys_district where id=?");
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JGeometry geometry = OracleSpatialUtils.wkt2geom(rs.getBytes(1));
                geometry.setSRID(rs.getInt(2));
                result.put("wkt", GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(geometry))));
                result.put("r", true);
            }

        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 获取行政区域实体
     * 
     * @param names
     * @return
     * @throws Exception
     */
    private JSONObject getDistrictFeatures(JSONArray names) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        JSONObject result = new JSONObject();
        result.put("r", false);
        try {
            con = DBHELPER.getNativeConnection();
            stmt = con.prepareStatement("select wkt,wkid,id from sys_district where name=?");
            for (int i = 0; i < names.size(); i++) {
                String name = names.getString(i);
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JSONObject districtJson = new JSONObject();
                    districtJson.put("label", name);
                    String wktStr = Convert.bytes2Str(rs.getBytes(1));
                    districtJson.put("geometry", GeometryUtils.wkt2ArcGISGeometryJSON(wktStr));
                    Point p = new WKTReader().read(wktStr).getCentroid();
                    JSONObject pJson = new JSONObject();
                    pJson.put("x", p.getX());
                    pJson.put("y", p.getY());
                    districtJson.put("center", pJson);
                    JSONUtils.append(result, "records", districtJson);
                }
            }
            result.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 获取地图中心点所在的行政区域
     * 
     * @param wkid
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject getMapCenterInDistrict(String params) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            String districtCacheKey = "map_district_";
            @SuppressWarnings("unchecked")
            List<WebGISDistrictItem> districtList = (List<WebGISDistrictItem>) SystemCacheManager.getInstance().getDataSourceResultSet(districtCacheKey);
            if (districtList == null) {
                con = DBHELPER.getConnection();
                districtList = loadDistrictFromDB(con, -1);
                SystemCacheManager.getInstance().putDataSourceResultSet(districtCacheKey, districtList);
            }
            JSONObject paramJson = JSONUtils.parserJSONObject(params).getJSONObject("geometry");
            Geometry mapPoint = new GeometryFactory().createPoint(new Coordinate(paramJson.getDouble("x"), paramJson.getDouble("y")));
            List<String> nameList = new ArrayList<String>();
            List<WebGISDistrictItem> itemList = new ArrayList<WebGISDistrictItem>();
            getDistrictByPoint(districtList, mapPoint, nameList, itemList);
            if (!itemList.isEmpty()) {
                for (int m = itemList.size() - 1; m >= 0; m--)
                    if (itemList.get(m).geometry == null)
                        itemList.remove(m);
                json.put("name", StringUtils.join(nameList, "-"));
                if (!itemList.isEmpty()) {
                    WebGISDistrictItem item = itemList.get(itemList.size() - 1);
                    json.put("code", item.code);
                    json.put("uid", item.id);
                    json.put("r", true);
                }
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    private void getDistrictByPoint(List<WebGISDistrictItem> districtList, Geometry mapPoint, List<String> nameList, List<WebGISDistrictItem> itemList) {
        for (WebGISDistrictItem item : districtList) {
            if (item.geometry == null || isGeometryIntersect(item.geometry, mapPoint)) {
                if (item.geometry != null)
                    nameList.add(item.name);
                itemList.add(item);
                if (item.children != null)
                    getDistrictByPoint(item.children, mapPoint, nameList, itemList);
                if (item.geometry != null)
                    break;
            }
        }
    }

    /**
     * 判断是否相交
     * 
     * @param geometry
     * @param mapPoint
     * @return
     */
    private boolean isGeometryIntersect(Geometry geometry, Geometry mapPoint) {
        boolean result = false;
        if (geometry instanceof GeometryCollection) {
            GeometryCollection c = (GeometryCollection) geometry;
            for (int i = 0; i < c.getNumGeometries(); i++)
                result = result | isGeometryIntersect(c.getGeometryN(i), mapPoint);

        } else
            result = geometry.intersects(mapPoint);
        return result;

    }

    /**
     * 获取实体列表
     * 
     * @param con
     * 
     * @param wkid
     * @return
     * @throws Exception
     */
    private List<WebGISDistrictItem> loadDistrictFromDB(Connection con, long p_id) throws Exception {
        PreparedStatement stmt = null;
        List<WebGISDistrictItem> list = null;
        try {
            con = DBHELPER.getNativeConnection();
            stmt = con.prepareStatement("select name,code,wkt,wkid,id from sys_district where code is not null and p_id=? order by sortorder");
            list = loadDistrictFromDB(p_id, stmt);
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return list;
    }

    /**
     * 获取实体列表 树状
     * 
     * @param wkid
     * @param p_id
     * @param stmt
     * @param croodStmt
     * @return
     * @throws Exception
     */
    private List<WebGISDistrictItem> loadDistrictFromDB(long p_id, PreparedStatement stmt) throws Exception {
        List<WebGISDistrictItem> list = new ArrayList<WebGISDistrictItem>();
        stmt.setLong(1, p_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String name = rs.getString(1);
            String code = rs.getString(2);
            String wkt = Convert.bytes2Str(rs.getBytes(3));
            long id = rs.getLong(5);
            WebGISDistrictItem item = new WebGISDistrictItem();
            item.id = id;
            item.code = code;
            item.name = name;
            if (!StringUtils.isEmpty(wkt))
                item.geometry = new WKTReader().read(wkt);
            list.add(item);
        }
        for (WebGISDistrictItem item : list) {
            List<WebGISDistrictItem> children = loadDistrictFromDB(item.id, stmt);
            if (children != null)
                item.children = children;
        }
        return list;
    }

    /**
     * 获取行政区域过滤后的Geometry
     * 
     * @param con
     * @param userId
     * @param geometry
     * @param wkid
     * @return
     * @throws Exception
     */
    public JGeometry getGeometryInstractDistrictGeometry(Connection con, long userId, JGeometry geometry, int wkid) throws Exception {
        JGeometry districtGeometry = getUserDistrictGeometry(con, userId, wkid);
        if (districtGeometry == null)
            return geometry;
        Geometry g1 = JGeometry2Geometry(districtGeometry);
        Geometry g2 = JGeometry2Geometry(geometry);
        return g1.intersects(g2) ? Geometry2JGeometry(g1.intersection(g2)) : null;
    }

    private JGeometry Geometry2JGeometry(Geometry g) throws Exception {
        return new WKB().toJGeometry(new WKBWriter().write(g));
    }

    private Geometry JGeometry2Geometry(JGeometry g) throws Exception {
        return new WKBReader().read(new WKB().fromJGeometry(g));
    }

    /**
     * 获取行政区域Geometry
     * 
     * @param con
     * @param userId
     * @return
     * @throws Exception
     */
    public JGeometry getUserDistrictGeometry(Connection con, long userId, int wkid) throws Exception {
        PreparedStatement stmt = null;
        JGeometry geometry = null;
        String cacheKey = "USER_DISTRICT_" + userId;
        byte[] bs = (byte[]) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
        if (bs == null) {
            try {
                stmt = con.prepareStatement("select wkt,wkid,id from sys_district where code in (select district_code from sys_userinfo where id=?)");
                stmt.setLong(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    bs = rs.getBytes(1);
                    geometry = OracleSpatialUtils.wkt2geom(bs);
                    geometry.setSRID(wkid);
                } else
                    bs = new byte[0];
                SystemCacheManager.getInstance().putWebGISItem(cacheKey, bs, 60 * 5);
            } finally {
                DBHELPER.closeStatement(stmt);
            }
        } else if (bs.length != 0) {
            geometry = OracleSpatialUtils.wkt2geom(bs);
            geometry.setSRID(wkid);
        }
        return geometry;
    }

    /**
     * 获取行政区域Geometry
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public JGeometry getDistrictGeometry(Connection con, long id) throws Exception {
        PreparedStatement stmt = null;
        JGeometry geometry = null;
        try {
            stmt = con.prepareStatement("select wkt,wkid,id from sys_district where id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                geometry = OracleSpatialUtils.wkt2geom(rs.getBytes(1));
                geometry.setSRID(rs.getInt(2));
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return geometry;
    }

    /**
     * 删除用户标注
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public JSONObject deleteUserMarker(long id) throws Exception {
        Connection con = null;
        IDBCommand stmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            String sql = "begin delete from webgis_user_marker where id=:id; estudio_attachment.del_by_pid(:id); end;";
            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, sql);
            stmt.setParam("id", id);
            stmt.execute();
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 增加用户标注
     * 
     * @param userId
     * @param caption
     * @param content
     * @param attributes
     * @param geometry
     * @param symbol
     * @param is_share
     * @param type
     * @return
     * @throws Exception
     */
    public JSONObject addUserMarker(long userId, String caption, String content, String attributes, String geometry, String symbol, boolean is_share, String type, String pictures, String attachments) throws Exception {
        Connection con = null;
        IDBCommand stmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            String sql = "insert into webgis_user_marker\n" + //
                    "  (id,\n" + //
                    "   caption,\n" + //
                    "   content,\n" + //
                    "   user_id,\n" + //
                    "   attributes,\n" + //
                    "   geometry,\n" + //
                    "   symbol,\n" + //
                    "   is_share,\n" + //
                    "   type,pictures,attachments)\n" + //
                    "values\n" + //
                    "  (:id,\n" + //
                    "   :caption,\n" + //
                    "   :content,\n" + //
                    "   :user_id,\n" + //
                    "   :attributes,\n" + //
                    "   :geometry,\n" + //
                    "   :symbol,\n" + //
                    "   :is_share,\n" + //
                    "   :type,:pictures,:attachments)";
            con = DBHELPER.getConnection();
            long uid = DBHELPER.getUniqueID(con);
            stmt = DBHELPER.getCommand(con, sql);
            stmt.setParam("id", uid);
            stmt.setParam("caption", caption);
            stmt.setParam("content", Convert.str2Bytes(content));
            stmt.setParam("user_id", userId);
            stmt.setParam("attributes", Convert.str2Bytes(attributes));
            stmt.setParam("geometry", Convert.str2Bytes(geometry));
            stmt.setParam("symbol", Convert.str2Bytes(symbol));
            stmt.setParam("is_share", is_share ? 1 : 0);
            stmt.setParam("type", type);
            stmt.setParam("pictures", Convert.str2Bytes(pictures));
            stmt.setParam("attachments", Convert.str2Bytes(attachments));
            stmt.execute();
            json.put("id", uid);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    public JSONObject updateUserMarker(long id, String caption, String content, String attributes, String geometry, String symbol, boolean is_share, String type, String pictures, String attachments) throws Exception {
        Connection con = null;
        IDBCommand stmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            String sql = "update webgis_user_marker\n" + //
                    "   set caption = :caption,\n" + //
                    "       content = :content,\n" + //
                    "       attributes = :attributes,\n" + //
                    "       geometry = :geometry,\n" + //
                    "       symbol = :symbol,\n" + //
                    "       is_share = :is_share,\n" + //
                    "       type = :type,pictures=:pictures,attachments=:attachments\n" + //
                    " where id = :id";

            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, sql);
            stmt.setParam("id", id);
            stmt.setParam("caption", caption);
            stmt.setParam("content", Convert.str2Bytes(content));
            stmt.setParam("attributes", Convert.str2Bytes(attributes));
            stmt.setParam("geometry", Convert.str2Bytes(geometry));
            stmt.setParam("symbol", Convert.str2Bytes(symbol));
            stmt.setParam("is_share", (int) (is_share ? 1 : 0));
            stmt.setParam("type", type);
            stmt.setParam("pictures", Convert.str2Bytes(pictures));
            stmt.setParam("attachments", Convert.str2Bytes(attachments));
            stmt.execute();
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取用户标注
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public JSONObject getUserMarker(long userId) throws Exception {
        Connection con = null;
        IDBCommand stmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            String sql = "select a.id,\n" + //
                    "       caption,\n" + //
                    "       content,\n" + //
                    "       to_char(regdate, 'yyyy-mm-dd hh24:mi:ss') regdate,\n" + //
                    "       user_id,\n" + //
                    "       b.realname realname,\n" + //
                    "       attributes,\n" + //
                    "       geometry,\n" + //
                    "       symbol,\n" + //
                    "       is_share,\n" + //
                    "       nvl(type,'无类型') type,pictures,attachments\n" + //
                    "  from webgis_user_marker a,sys_userinfo b\n" + //
                    " where a.user_id=b.id and decode(is_share,1,:user_id,user_id) = :user_id";

            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, sql);
            stmt.setParam(1, userId);
            stmt.setParam(2, userId);
            stmt.executeQuery();
            while (stmt.next()) {
                JSONUtils.append(json, "records", DBHELPER.cmdRecord2Json(stmt));
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取MIS图层列表
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public JSONObject getMISLayers(long userId) throws Exception {
        Connection con = null;
        IDBCommand groupStmt = null;
        IDBCommand serviceStmt = null;
        IDBCommand groupFieldStmt = null;
        IDBCommand attribFieldStmmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            String groupSQL = "select id, caption from webgis_dynamic_server_category where is_valid = 1 and is_visible = 1 and exists (select 'x' from webgis_dynamic_service where p_id = webgis_dynamic_server_category.id and is_valid = 1 and is_visible = 1) and p_id=? order by sortorder";
            con = DBHELPER.getConnection();
            groupStmt = DBHELPER.getCommand(con, groupSQL);
            serviceStmt = DBHELPER.getCommand(con, "select id,caption,server_type,IS_SPECIAL,IS_SUPPORT_AREA_SPECIAL from webgis_dynamic_service where is_valid=1 and is_complete=1 and is_visible=1 and p_id=? order by sortorder");
            groupFieldStmt = DBHELPER.getCommand(con, "select name,group_fields from WEBGIS_DYNAMIC_FIELD_GROUP where p_id=?");
            attribFieldStmmt = DBHELPER.getCommand(con, "select id, field_name, field_comment, schema_field_name, is_visible, is_query, is_enum, is_relate_parent, data_type, is_primary from webgis_dynamic_field where IS_VISIBLE=1 and p_id=? order by sortorder");
            generateMISLayerTree(groupStmt, serviceStmt, attribFieldStmmt, groupFieldStmt, -1, json);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(attribFieldStmmt);
            DBHELPER.closeCommand(serviceStmt);
            DBHELPER.closeCommand(groupStmt);
            DBHELPER.closeCommand(groupFieldStmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }
    
    private void generateMISLayerTree(IDBCommand groupStmt, IDBCommand serviceStmt, IDBCommand attribFieldStmt, IDBCommand groupFieldStmt, long pid, JSONObject json) throws Exception {
        groupStmt.setParam(1, pid);
        groupStmt.executeQuery();
        List<JSONObject> categoryList = new ArrayList<JSONObject>();
        while (groupStmt.next()) {
            JSONObject categoryJson = new JSONObject();
            categoryJson.put("label", groupStmt.getString(2));
            categoryJson.put("id", groupStmt.getLong(1));
            categoryJson.put("folder", true);
            categoryList.add(categoryJson);
            JSONUtils.append(json, "children", categoryJson);
        }
        for (int i = 0; i < categoryList.size(); i++)
            generateMISLayerTree(groupStmt, serviceStmt, attribFieldStmt, groupFieldStmt, categoryList.get(i).getLong("id"), categoryList.get(i));
        serviceStmt.setParam(1, pid);
        serviceStmt.executeQuery();
        while (serviceStmt.next()) {
            JSONObject serviceJson = new JSONObject();
            serviceJson.put("id", serviceStmt.getLong(1));
            serviceJson.put("label", serviceStmt.getString(2));
            serviceJson.put("type", serviceStmt.getInt(3));
            serviceJson.put("is_special", serviceStmt.getInt(4));
            JSONUtils.append(json, "children", serviceJson);

            attribFieldStmt.setParam(1, serviceStmt.getLong(1));
            attribFieldStmt.executeQuery();

            boolean isExistsQueryFields = false;
            while (attribFieldStmt.next()) {
                JSONObject attribJson = new JSONObject();
                attribJson.put("name", attribFieldStmt.getString("SCHEMA_FIELD_NAME"));
                attribJson.put("comment", attribFieldStmt.getString("FIELD_COMMENT"));
                attribJson.put("datatype", attribFieldStmt.getString("DATA_TYPE"));
                attribJson.put("isenum", attribFieldStmt.getInt("IS_ENUM") == 1);
                attribJson.put("isquery", attribFieldStmt.getInt("IS_QUERY") == 1);
                attribJson.put("isrelateparent", attribFieldStmt.getInt("IS_RELATE_PARENT") == 1);
                if (attribFieldStmt.getInt("IS_QUERY") == 1)
                    isExistsQueryFields = true;
                JSONUtils.append(serviceJson, "attributeFields", attribJson);
            }

            groupFieldStmt.setParam(1, serviceStmt.getLong(1));
            groupFieldStmt.executeQuery();
            while (groupFieldStmt.next()) {
                JSONObject groupJson = new JSONObject();
                groupJson.put("label", groupFieldStmt.getString("name"));
                String[] fields = groupFieldStmt.getString("group_fields").split(",");
                for (int i = 0; i < fields.length; i++)
                    fields[i] = StringUtils.substringBetween(fields[i], "[", "]");
                groupJson.put("fields", fields);
                JSONUtils.append(serviceJson, "groupFields", groupJson);
            }
            serviceJson.put("isQuery", isExistsQueryFields);
            serviceJson.put("isMapSpecial", serviceStmt.getInt("IS_SUPPORT_AREA_SPECIAL") == 1);
        }
    }
   
    /**
     * 获取MIS图层列表中的数据
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public JSONObject getMISLayerRecords(long id) throws Exception {
        Connection con = null;
        IDBCommand layerStmt = null;
        IDBCommand layerInkStmt = null;
        IDBCommand stmt = null;
        IDBCommand fieldStmt = null;
        JSONObject json = new JSONObject();
        JSONArray jsonarray4LayerInk=new JSONArray();
       
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            layerStmt = DBHELPER.getCommand(con, "select id,server_type,schema_table_name,zh_status,type from webgis_dynamic_service where id=?");
            layerStmt.setParam(1, id);
            layerStmt.executeQuery();
            layerStmt.next();

//            String tableName = layerStmt.getString(3);
            String tableName ="TMP_GIS_DY_LAYER_"+id;
            
            
            String keyField = layerStmt.getInt(2) == 0 ? "ID" : "PRIMARY_ID";
            keyField=layerStmt.getInt(4)!=0?"PRIMARY_ID":keyField;
            String geo_type=layerStmt.getString(5);
            json.put("geo_type", geo_type);
            String geomField = "geometry";
            //excel 为ID shape为 PRIMATRY_ID
            String captionField = keyField;
            
            layerInkStmt=DBHELPER.getCommand(con, "select t.service_id,t.layer_id,t.layer_field_id from webgis_dynamic_link t where t.misservice_id=? and t.type=1");
            layerInkStmt.setParam(1, id);
            layerInkStmt.executeQuery();
            while(layerInkStmt.next()){
            	 JSONObject json4layerInk = new JSONObject();
            	 json4layerInk.put("service_id", layerInkStmt.getInt(1));
            	 json4layerInk.put("layer_id", layerInkStmt.getInt(2));
            	 json4layerInk.put("layer_field_id", layerInkStmt.getInt(3));
            	 jsonarray4LayerInk.add(json4layerInk);
            }
            
            // 第一步需要把所有的必须的字段加进去
            List<String> fieldList = new ArrayList<String>();
            List<String> existsFieldList = new ArrayList<String>();

            fieldList.add(keyField);
            existsFieldList.add(StringUtils.trim(StringUtils.upperCase(keyField)));

            fieldList.add(geomField);
            existsFieldList.add(StringUtils.trim(StringUtils.upperCase(geomField)));

            Map<String, Integer> fieldName2ColumnIndex = new HashMap<String, Integer>();
            fieldStmt = DBHELPER.getCommand(con, "select upper(schema_field_name),upper(schema_field_name),is_caption,STATISTICS_STANDARD,MAP_GLZD from WEBGIS_DYNAMIC_FIELD where is_visible=1 and p_id=? order by sortorder");
            fieldStmt.setParam(1, id);
            fieldStmt.executeQuery();
            List<String> attributesList = new ArrayList<String>();
            String map_glzd="";
            int captiopnIndex=0;
            
            while (fieldStmt.next()) {
                String fieldName = fieldStmt.getString(1);
                String upperFieldName = StringUtils.trim(StringUtils.upperCase(fieldName));
                if (fieldStmt.getInt(3) == 1) {
                    captionField = upperFieldName;
                    fieldList.add(2, captionField);
                    existsFieldList.add(2, captionField);
                }
                if (existsFieldList.indexOf(upperFieldName) == -1) {
                    fieldList.add(fieldName);
                    fieldName2ColumnIndex.put(fieldStmt.getString(2), fieldList.size() - 1);
                    attributesList.add(fieldStmt.getString(2));
                    existsFieldList.add(upperFieldName);
                } else if(captiopnIndex==0){
                	captiopnIndex=fieldList.size() - 1;
                }
                if(fieldStmt.getString("STATISTICS_STANDARD").equals("1")){
                	json.put("statistics_standard", fieldName);
                }
                if(fieldStmt.getString("MAP_GLZD").equals("1")){
                	map_glzd=fieldName;
                }
            }
            String sql = "";
            JSONArray jsonarray4Link=getDynamicLayerLink(con,stmt,fieldList,tableName,jsonarray4LayerInk, map_glzd);
            JSONUtils.sort(jsonarray4Link, "count", true);
            if(StringUtils.isNotEmpty(map_glzd)&&jsonarray4LayerInk.size()!=0&&jsonarray4Link.getJSONObject(0).getInt("count")!=0){
            	getExceldataWithGeometry(con,stmt,json,fieldList,tableName,map_glzd,attributesList,fieldName2ColumnIndex,captionField,keyField,jsonarray4Link);
            }else if(keyField=="PRIMARY_ID"){
            	sql = "select " + StringUtils.join(fieldList, ",") + " from view_" + tableName+" a";
            	getShapeFiledata(con,stmt,json,sql,attributesList,fieldName2ColumnIndex,captionField,keyField,captiopnIndex);
            }else{
            	sql = "select " + StringUtils.join(fieldList, ",") + " from view_" + tableName+" a";
            	getExceldataNoGeometry(con,stmt,json,sql,attributesList,fieldName2ColumnIndex,captionField,keyField,captiopnIndex);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(layerStmt);
            DBHELPER.closeCommand(layerInkStmt);
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }
    //检查是否存在geometry数据
    public JSONArray getDynamicLayerLink(Connection con,IDBCommand stmt,List<String> fieldList,String tableName,JSONArray jsonarray4LayerInk,String map_glzd) throws Exception{
    	JSONArray jsonarray4Link=new JSONArray();
        List<String> fieldLista= StringUtilsLocal.joinPreLabel(fieldList,"a.");
        for(int i=0;i<jsonarray4LayerInk.size();i++){
      	  JSONObject param= (JSONObject) jsonarray4LayerInk.get(i);
      	  JSONObject result=new JSONObject();
      	  stmt=DBHELPER.getCommand(con, "select name from webgis_layer_fields a where a.id="+param.getLong("layer_field_id"));
      	  stmt.executeQuery();
      	  stmt.next();
      	  String fieldName=stmt.getString("name");
      	  result.put("sql4fieldname", "select name from webgis_layer_fields a where a.id="+param.getLong("layer_field_id"));
      	  result.put("fieldName", fieldName);
      	  stmt=DBHELPER.getCommand(con, "select count(b."+fieldName+") count from view_" + tableName+" a,  spatial_fs_"+param.getLong("layer_id")+" b where a."+map_glzd +" = b."+ fieldName);//   substr(b."+fieldName+",1,length("+map_glzd+"))=a."+map_glzd);
      	  stmt.executeQuery();
      	  stmt.next();
      	  int count=stmt.getInt("count");
      	  result.put("sql4count", "select count(b."+fieldName+") count from view_" + tableName+" a,  spatial_fs_"+param.getLong("layer_id")+" b wherea."+map_glzd +" = b."+ fieldName); //substr(b."+fieldName+",1,length("+map_glzd+"))=a."+map_glzd);
      	  result.put("count", count);
      	  String sql4queryData="select b.Geometry geometry1," + StringUtils.join(fieldLista, ",") +" from view_" + tableName+" a , spatial_fs_"+param.getLong("layer_id") +" b where a."+map_glzd +" = b."+fieldName+"(+)";
      	  result.put("sql4queryData", sql4queryData);
      	  jsonarray4Link.add(result);
        }
        return jsonarray4Link;
    }
    
    //获取存在geometry的excel数据
    public void getExceldataWithGeometry(Connection con,IDBCommand stmt,JSONObject json,List<String> fieldList,String tableName,String map_glzd,List<String> attributesList,Map<String, Integer> fieldName2ColumnIndex,String captionField,String keyField,JSONArray jsonarray4Link) throws Exception{
      //选取匹配度最高的sql语句惊醒查询。
      String sql=jsonarray4Link.getJSONObject(0).getString("sql4queryData");
      String fieldName=jsonarray4Link.getJSONObject(0).getString("fieldName");
      stmt = DBHELPER.getCommand(con, sql);
      stmt.executeQuery();
      while (stmt.next()) {
          JSONObject recordJson = new JSONObject();
          for (int i = 0; i <attributesList.size(); i++) {
        	  int columnIndex =Integer.parseInt(StringUtils.substring(attributesList.get(i), 1))<Integer.parseInt(StringUtils.substring(captionField, 1))?fieldName2ColumnIndex.get(attributesList.get(i))+3:fieldName2ColumnIndex.get(attributesList.get(i))+2;
        	  Object value = stmt.getValue(columnIndex); //获取列对应值
              if (value == null || StringUtils.isEmpty(String.valueOf(value)))
                  continue;
              recordJson.put(stmt.getFieldNames().get(columnIndex-1), value);//将列名 列值放到recordJson中
          }
         Object geo3= stmt.getValue(3);
         if(!StringUtilsLocal.isNullOrEmpty(geo3)){
         	recordJson.put("geom", geo3);
         }else{
        	Object geo1= stmt.getValue(1);
        	if(!StringUtilsLocal.isNullOrEmpty(geo1)){
        		recordJson.put("geom", geo1);
        	}else{
//        		String fieldValue=recordJson.getString(fieldName);
//        		System.out.println("fieldValue"+fieldValue);
        	}
         }
         int captionIndex = StringUtils.equals(captionField, keyField) ? 2 : 4;
         recordJson.put("caption", stmt.getValue(captionIndex));
         recordJson.put(captionField, stmt.getValue(captionIndex));
         recordJson.put(keyField, stmt.getValue(2));
         recordJson.put("id", stmt.getValue(2));
         JSONUtils.append(json, "records", recordJson);
      }
    }
    //获取从不同图层中获取geometry的excel数据
    public Object getGeometry(String geoCode,JSONArray jsonarray4Link){
    	Object geometry=null;
    	
    	return geometry;
    }
    
    //获取没有geometry的excel数据
    public void getExceldataNoGeometry(Connection con,IDBCommand stmt,JSONObject json,String sql,List<String> attributesList,Map<String, Integer> fieldName2ColumnIndex,String captionField,String keyField,int captiopnIndex) throws Exception{
    	  stmt = DBHELPER.getCommand(con, sql);
          stmt.executeQuery();
          while (stmt.next()) {
              JSONObject recordJson = new JSONObject();
              for (int i = 0; i < attributesList.size(); i++) {
//            	  int columnIndex =Integer.parseInt(StringUtils.substring(attributesList.get(i), 1))<Integer.parseInt(StringUtils.substring(captionField, 1)) ?fieldName2ColumnIndex.get(attributesList.get(i))+2:fieldName2ColumnIndex.get(attributesList.get(i))+1;
            	  int columnIndex =Integer.parseInt(StringUtils.substring(attributesList.get(i), 1))<captiopnIndex ?fieldName2ColumnIndex.get(attributesList.get(i))+2:fieldName2ColumnIndex.get(attributesList.get(i))+1;
                  Object value = stmt.getValue(columnIndex); //获取列对应值
                  if (value == null || StringUtils.isEmpty(String.valueOf(value)))
                      continue;
                  recordJson.put(stmt.getFieldNames().get(columnIndex-1), value);//将列名 列值放到recordJson中
              }
              recordJson.put("geom", stmt.getValue(2));
              int captionIndex = StringUtils.equals(captionField, keyField) ? 1 : 3;
              recordJson.put("caption", stmt.getValue(captionIndex));
              recordJson.put(captionField, stmt.getValue(captionIndex));
              recordJson.put(keyField, stmt.getValue(1));
              recordJson.put("id", stmt.getValue(1));
              JSONUtils.append(json, "records", recordJson);
          }    
    }
    
    //获取shape文件中的属性表数据
    public void getShapeFiledata(Connection con,IDBCommand stmt,JSONObject json,String sql,List<String> attributesList,Map<String, Integer> fieldName2ColumnIndex,String captionField,String keyField,int captiopnIndex) throws Exception{
  	    stmt = DBHELPER.getCommand(con, sql);
        stmt.executeQuery();
        while (stmt.next()) {
            JSONObject recordJson = new JSONObject();
            for (int i = 0; i < attributesList.size(); i++) {
//                int columnIndex = fieldName2ColumnIndex.get(attributesList.get(i)) + 1;
                int columnIndex = fieldName2ColumnIndex.get(attributesList.get(i))<captiopnIndex?fieldName2ColumnIndex.get(attributesList.get(i))+2:fieldName2ColumnIndex.get(attributesList.get(i))+1;
                Object value = stmt.getValue(columnIndex); //获取列对应值
                if (value == null || StringUtils.isEmpty(String.valueOf(value)))
                    continue;
                recordJson.put(stmt.getFieldNames().get(columnIndex-1), value);//将列名 列值放到recordJson中
            }
            recordJson.put("geom", stmt.getValue(2));
            int captionIndex = StringUtils.equals(captionField, keyField) ? 1 : 3;
            recordJson.put("caption", stmt.getValue(captionIndex));
            recordJson.put(captionField, stmt.getValue(captionIndex));
            recordJson.put(keyField, stmt.getValue(1));
            recordJson.put("id", stmt.getValue(1));
            JSONUtils.append(json, "records", recordJson);
        }    
  }
    /**
     * 获取专题图层列表
     * 
     * @param isServer
     * @param serverId
     * @param layerId
     * @return
     * @throws Exception
     */
    public JSONObject getSpecialLayers(boolean isServer, long serverId, long layerId) throws Exception {
        Connection con = null;
        JSONObject json = null;
        PreparedStatement stmt = null;
        List<Long> layerIds = new ArrayList<Long>();
        try {
            con = DBHELPER.getConnection();
            String versionSQL = isServer ? "select sum(version) from webgis_layer where p_id=" : "select version from webgis_layer where id=";
            versionSQL += isServer ? serverId : layerId;
            long version = DBHELPER.executeScalarLong(versionSQL, con);
            String cacheKey = "SPECIAL_LAYER_SETTINF-" + (isServer ? serverId : layerId);
            json = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
            if (json == null || json.getLong("version") != version) { // 不能从缓存中读取
                json = new JSONObject();
                List<String> extLayers = new ArrayList<String>();
                if (isServer) {
                    stmt = con.prepareStatement("select id,name,nvl(layer_comment,title) title,nvl(is_special,0) from webgis_layer where equal_layer_as=name and p_id = ?");
                    stmt.setLong(1, serverId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (rs.getInt(4) != 0)
                            layerIds.add(rs.getLong(1));
                        extLayers.add(Long.toString(rs.getLong(1)));
                    }
                } else {
                    layerIds.add(layerId);
                }
                for (long id : layerIds) {
                    getSpecialLayerDetails(con, json, id);
                    extLayers.add(Long.toString(id));
                }
                if (isServer && !extLayers.isEmpty()) {
                    json.put("allLayers", DBHELPER.executeScalarString("select wm_concat(name) from webgis_layer where (title,p_id,equal_layer_as) in ( select title,p_id,equal_layer_as from webgis_layer where id in (" + StringUtils.join(extLayers, ",") + "))", con).split(","));
                }
                json.put("version", version);
                json.put("r", true);
                SystemCacheManager.getInstance().putWebGISItem(cacheKey, json);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }
    
    /**
     * 获取图层的专题详细信息
     * 
     * @param con
     * @param json
     * @param id
     * @throws Exception
     */
    private boolean getSpecialLayerDetails(Connection con, JSONObject json, long layerId) throws Exception {
        JSONObject layerJson = null;
        List<JSONObject> items = new ArrayList<JSONObject>();
        Statement stmt = null;
        Statement stmt2 = null;
        try {
            stmt = con.createStatement();
            stmt2 = con.createStatement();
            String tempSql = "select c.url||'/'||a.name||'/query' as url, a.download_status,b.TABLE_NAME,a.version from user_tables b,webgis_services c, webgis_layer a where c.id=a.p_id and b.TABLE_NAME(+)='SPATIAL_FS_'||a.id and a.id=" + layerId;
            ResultSet rs2 = stmt2.executeQuery(tempSql);
            rs2.next();
            long version = rs2.getLong(4);

            String cacheKey = "SpecialLayerDetails-" + layerId;
            layerJson = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);

            if (layerJson == null || layerJson.getLong("version") != version) { // 是否已经缓存数据
                layerJson = new JSONObject();
                layerJson.put("r", false);
                layerJson.put("version", version);
                boolean isCache = rs2.getInt(2) == 1 && !StringUtils.isEmpty(rs2.getString(3));
                String url = rs2.getString(1);

                ResultSet rs = stmt.executeQuery("select SCHEMA_FIELD_NAME,title,special_type,data_type,name from webgis_layer_fields where special_type in (1,2) and is_special=1 and p_id=" + layerId);
                while (rs.next()) {
                    boolean isEnumType = rs.getInt(3) == 1;
                    String schemaFieldName = rs.getString(1);
                    List<JSONObject> valueList = getSpecialLayerFieldValues(con, layerId, schemaFieldName, isEnumType, isCache, url);
                    if (valueList.isEmpty())
                        continue;
                    JSONObject fieldJson = new JSONObject();
                    String shapeFieldName = rs.getString(5);
                    fieldJson.put("name", shapeFieldName);
                    fieldJson.put("schema_fieldname", schemaFieldName);
                    fieldJson.put("title", rs.getString(2));
                    fieldJson.put("type", isEnumType);
                    fieldJson.put("datatype", rs.getInt(4));
                    fieldJson.put("values", valueList);
                    items.add(fieldJson);
                } // end while (rs.next())

                if (!items.isEmpty()) {
                    layerJson.put("names", DBHELPER.executeScalarString("select wm_concat(name) from webgis_layer where (p_id,title,equal_layer_as) = (select p_id, title,equal_layer_as from webgis_layer where id=" + layerId + ") group by title", con).split(","));
                    layerJson.put("title", DBHELPER.executeScalarString("select nvl(layer_comment,title) from webgis_layer where id=" + layerId, con));
                    layerJson.put("type", DBHELPER.executeScalarString("select type from webgis_layer where id=" + layerId, con));
                    layerJson.put("items", items);
                    layerJson.put("r", true);
                }

                SystemCacheManager.getInstance().putWebGISItem(cacheKey, layerJson);
            }
            if (layerJson.getBoolean("r"))
                JSONUtils.append(json, "layers", layerJson);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(stmt2);
        }

        return layerJson.getBoolean("r");
    }
    
    /**
     * 获取动态MIS专题图层列表
     * 
     * @param isServer
     * @param serverId
     * @param layerId
     * @return
     * @throws Exception
     */
    public JSONObject getSpecialMisLayers(boolean isServer, long serverId, long layerId) throws Exception {
        Connection con = null;
        JSONObject json = null;
        PreparedStatement stmt = null;
        List<Long> layerIds = new ArrayList<Long>();
        try {
            con = DBHELPER.getConnection();
            String versionSQL = isServer ? "select sum(version) from webgis_dynamic_service where p_id=" : "select version from webgis_dynamic_service where id=";
            versionSQL += isServer ? serverId : layerId;
            long version = DBHELPER.executeScalarLong(versionSQL, con);
            String cacheKey = "SPECIAL_LAYER_SETTINF-" + (isServer ? serverId : layerId);
            json = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
            if (json == null || json.getLong("version") != version) { // 不能从缓存中读取
                json = new JSONObject();
                List<String> extLayers = new ArrayList<String>();
                if (isServer) {
                    stmt = con.prepareStatement("select id,name,nvl(layer_comment,title) title,nvl(is_special,0) from webgis_layer where equal_layer_as=name and p_id = ?");
                    stmt.setLong(1, serverId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (rs.getInt(4) != 0)
                            layerIds.add(rs.getLong(1));
                        extLayers.add(Long.toString(rs.getLong(1)));
                    }
                } else {
                    layerIds.add(layerId);
                }
                for (long id : layerIds) {
                	getSpecialMisLayerDetails(con, json, id);
                    extLayers.add(Long.toString(id));
                }
                if (isServer && !extLayers.isEmpty()) {
                    json.put("allLayers", DBHELPER.executeScalarString("select wm_concat(name) from webgis_layer where (title,p_id,equal_layer_as) in ( select title,p_id,equal_layer_as from webgis_layer where id in (" + StringUtils.join(extLayers, ",") + "))", con).split(","));
                }
                json.put("version", version);
                json.put("r", true);
                SystemCacheManager.getInstance().putWebGISItem(cacheKey, json);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }
    
    /**
     * 获取图层的专题详细信息
     * 
     * @param con
     * @param json
     * @param id
     * @throws Exception
     */
    private boolean getSpecialMisLayerDetails(Connection con, JSONObject json, long layerId) throws Exception {
      JSONObject layerJson=null;
      List<JSONObject> items = new ArrayList<JSONObject>();
      Statement stmt = null;
      Statement stmt2 = null;
      try{
    	  stmt = con.createStatement();
          stmt2 = con.createStatement();
          String tempSql = "select t.version,t.schema_table_name from webgis_dynamic_service t where t.id=" + layerId;
          ResultSet rs2 = stmt2.executeQuery(tempSql);
          rs2.next();
          long version = rs2.getLong(1);
          String cacheKey = "SpecialLayerDetails-" + layerId;
    	  layerJson = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
    	  
    	  boolean isCache =!StringUtils.isEmpty(rs2.getString(2));
    	  ResultSet rs = stmt.executeQuery("select t.schema_field_name,t.field_comment title,t.special_type,t.data_type1 data_type,t.field_name from webgis_dynamic_field t where t.is_special=1 and t.special_type in (1,2) and t.p_id=" + layerId);
          if (layerJson == null || layerJson.getLong("version") != version) { 
        	  layerJson = new JSONObject();
              layerJson.put("r", false);
              layerJson.put("version", version);
              while (rs.next()) {
                  boolean isEnumType = rs.getInt(3) == 1;
                  String schemaFieldName = rs.getString(1);
                  List<JSONObject> valueList = getSpecialLayerFieldValues(con, layerId, schemaFieldName, isEnumType, isCache, null);
                  if (valueList.isEmpty())
                      continue;
                  JSONObject fieldJson = new JSONObject();
                  String shapeFieldName = rs.getString(5);
                  fieldJson.put("name", shapeFieldName);
                  fieldJson.put("schema_fieldname", schemaFieldName);
                  fieldJson.put("title", rs.getString(2));
                  fieldJson.put("type", isEnumType);
                  fieldJson.put("datatype", rs.getInt(4));
                  fieldJson.put("values", valueList);
                  items.add(fieldJson);
              } // end while (rs.next())
              
              if (!items.isEmpty()) {
                  layerJson.put("names", DBHELPER.executeScalarString("select wm_concat(caption) from webgis_dynamic_service t where (p_id, caption) =(select p_id, caption from webgis_dynamic_service where id = " + layerId + ")group by t.caption", con).split(","));
                  layerJson.put("title", DBHELPER.executeScalarString("select nvl(CAPTION,SCHEMA_TABLE_NAME) from webgis_dynamic_service where id=" + layerId, con));
                  layerJson.put("type", DBHELPER.executeScalarString("select type from webgis_dynamic_service where id=" + layerId, con));
                  layerJson.put("items", items);
                  layerJson.put("r", true);
              }
              SystemCacheManager.getInstance().putWebGISItem(cacheKey, layerJson);

          }
          if (layerJson.getBoolean("r"))
              JSONUtils.append(json, "layers", layerJson);
        
      }finally{
    	  DBHELPER.closeStatement(stmt);
          DBHELPER.closeStatement(stmt2);
      }
    
      return layerJson.getBoolean("r");
    }

    /**
     * 获取图层字段枚举及最大最小值
     * 
     * @param con
     * @param layerId
     * @param string
     * @param isEnumType
     * @param url
     * @param isCache
     * @return
     * @throws SQLException
     */
    private List<JSONObject> getSpecialLayerFieldValues(Connection con, long layerId, String fieldName, boolean isEnumType, boolean isCache, String url) throws SQLException {
        List<JSONObject> valueList = new ArrayList<JSONObject>();
        if (isCache) { // 从缓存中读取
            PreparedStatement stmt = null;
            try {
                if (isEnumType) {
//                	String sql=url==null?"select count(*), " + fieldName + " from tmp_gis_dy_layer_" + layerId + " group by " + fieldName + " order by " + fieldName :"select count(*), " + fieldName + " from spatial_fs_" + layerId + " group by " + fieldName + " order by " + fieldName;
//                    stmt = con.prepareStatement("select count(*), " + fieldName + " from spatial_fs_" + layerId + " group by " + fieldName + " order by " + fieldName);
                	String sql=url==null?"select count(*), " + fieldName + " from view_tmp_gis_dy_layer_" + layerId + " group by " + fieldName + " order by " + fieldName :"select count(*), " + fieldName + " from spatial_fs_" + layerId + " group by " + fieldName + " order by " + fieldName;
                	stmt = con.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        JSONObject valueJson = new JSONObject();
                        valueJson.put("value", rs.getString(2));
                        valueJson.put("count", rs.getInt(1));
                        valueList.add(valueJson);
                    }
                } else {
//                	String sql=url==null?"select min(to_number(nvl(" + fieldName + ",0))), max(to_number(nvl(" + fieldName + ",0))) from tmp_gis_dy_layer_" + layerId:"select min(to_number(nvl(" + fieldName + ",0))), max(to_number(nvl(" + fieldName + ",0))) from spatial_fs_" + layerId;
//                  stmt = con.prepareStatement("select min(to_number(nvl(" + fieldName + ",0))), max(to_number(nvl(" + fieldName + ",0))) from spatial_fs_" + layerId);
                	String sql=url==null?"select min(to_number(nvl(" + fieldName + ",0))), max(to_number(nvl(" + fieldName + ",0))) from view_tmp_gis_dy_layer_" + layerId:"select min(to_number(nvl(" + fieldName + ",0))), max(to_number(nvl(" + fieldName + ",0))) from spatial_fs_" + layerId;
                	stmt = con.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    JSONObject valueJson = new JSONObject();
                    valueJson.put("minValue", rs.getString(1));
                    valueJson.put("maxValue", rs.getString(2));
                    valueList.add(valueJson);
                }
            } catch (Exception e) {
                ExceptionUtils.printExceptionTrace(e);
            } finally {
                DBHELPER.closeStatement(stmt);
            }
        } else { // 从地图服务中读取
            if (isEnumType) {
                JSONObject statisticCount = ArcGISServiceRestService.statisticCount(url, fieldName);
                if (statisticCount != null) {
                    JSONArray featuresArray = statisticCount.getJSONArray("features");
                    if (featuresArray != null && !featuresArray.isEmpty()) {
                        for (int i = 0; i < featuresArray.size(); i++) {
                            JSONObject attribJson = featuresArray.getJSONObject(i).getJSONObject("attributes");
                            int c = attribJson.getInt("C");
                            if (c == 0)
                                continue;
                            JSONObject valueJson = new JSONObject();
                            valueJson.put("value", attribJson.getString(fieldName));
                            valueJson.put("count", c);
                            valueList.add(valueJson);
                        }
                    }
                }
            } else {
                JSONObject queryJson = ArcGISServiceRestService.statisticMaxAndMin(url, fieldName);
                if (queryJson != null) {
                    JSONArray featuresArray = queryJson.getJSONArray("features");
                    if (featuresArray != null) {
                        JSONObject attribJson = featuresArray.getJSONObject(0).getJSONObject("attributes");
                        JSONObject valueJson = new JSONObject();
                        valueJson.put("minValue", attribJson.getDouble("MIN"));
                        valueJson.put("maxValue", attribJson.getDouble("MAX"));
                        valueList.add(valueJson);
                    }
                }
            }
        }
        return valueList;
    }

    /**
     * 获取专题图层列表
     * 
     * @param isServer
     * @param serverId
     * @param layerId
     * @return
     * @throws Exception
     */
    public JSONObject getQueryLayers(boolean isServer, long serverId, long layerId) throws Exception {
        Connection con = null;
        JSONObject json = new JSONObject();
        PreparedStatement stmt = null;
        List<Long> layerIds = new ArrayList<Long>();
        json.put("r", false);
        try {
            con = DBHELPER.getConnection();
            if (isServer) {
                stmt = con.prepareStatement("select id,name,nvl(layer_comment,title) title from webgis_layer where query_enabled=1 and name=equal_layer_as and p_id = ?");
                stmt.setLong(1, serverId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next())
                    layerIds.add(rs.getLong(1));
            } else {
                layerIds.add(layerId);
            }
            for (long id : layerIds) {
                getQueryLayerDetails(con, json, id);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取图层的专题详细信息
     * 
     * @param con
     * @param json
     * @param id
     * @throws Exception
     */
    private JSONObject getQueryLayerDetails(Connection con, JSONObject json, long layerId) throws Exception {
        JSONObject layerJson = null;
        List<JSONObject> items = new ArrayList<JSONObject>();
        PreparedStatement resourceStmt = null;
        Statement stmt = null;
        try {

            String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid,(select name from webgis_layer_fields where data_type=4 and p_id=a.id) as objectfieldname,a.version from user_tables c,webgis_services b,webgis_layer a where a.name=a.equal_layer_as and a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.id = "
                    + layerId;
            resourceStmt = con.prepareStatement(sql2);
            ResultSet resourceRs = resourceStmt.executeQuery();
            resourceRs.next();
            long version = resourceRs.getLong("version");
            String cacheKey = "QUERY_LAYER_DETAILS-" + layerId;
            layerJson = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);

            if (layerJson == null || layerJson.getLong("version") != version) {
                layerJson = new JSONObject();
                boolean isCache = resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4));
                String url = resourceRs.getString(2);
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select SCHEMA_FIELD_NAME name,title,special_type,data_type from webgis_layer_fields where query_enabled=1 and p_id=" + layerId);
                while (rs.next()) {
                    int fieldSpecialType = rs.getInt(3);
                    boolean isEnumType = fieldSpecialType == 1;
                    String shapeFieldName = rs.getString(1);
                    JSONObject fieldJson = new JSONObject();
                    fieldJson.put("name", shapeFieldName);
                    fieldJson.put("title", rs.getString(2));
                    fieldJson.put("type", isEnumType);
                    fieldJson.put("datatype", rs.getInt(4));
                    if (fieldSpecialType != 0) {
                        List<JSONObject> valueList = getQueryLayerFieldValues(con, layerId, shapeFieldName, isEnumType, url, isCache);
                        fieldJson.put("values", valueList);
                    }
                    items.add(fieldJson);
                }
                if (!items.isEmpty()) {
                    layerJson.put("layerId", layerId);
                    layerJson.put("title", DBHELPER.executeScalarString("select nvl(layer_comment,title) from webgis_layer where id=" + layerId, con));
                    layerJson.put("names", DBHELPER.executeScalarString("select wm_concat(name) from webgis_layer where (p_id,title,equal_layer_as) = (select p_id, title,equal_layer_as from webgis_layer where id=" + layerId + ") group by title", con).split(","));
                    layerJson.put("type", DBHELPER.executeScalarString("select type from webgis_layer where id=" + layerId, con));
                    layerJson.put("items", items);

                }
                layerJson.put("version", version);
                SystemCacheManager.getInstance().putWebGISItem(cacheKey, layerJson);
            }
        } finally {
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeStatement(stmt);
        }
        if (layerJson.getJSONArray("items") != null)
            JSONUtils.append(json, "layers", layerJson);

        return layerJson;
    }

    /**
     * 获取图层字段枚举及最大最小值
     * 
     * @param con
     * @param layerId
     * @param string
     * @param isEnumType
     * @return
     * @throws SQLException
     */
    private List<JSONObject> getQueryLayerFieldValues(Connection con, long layerId, String fieldName, boolean isEnumType, String url, boolean isCache) throws SQLException {
        List<JSONObject> valueList = new ArrayList<JSONObject>();
        if (isCache) {
            PreparedStatement stmt = null;
            try {
                if (isEnumType) {
                    stmt = con.prepareStatement("select count(*), " + fieldName + " from spatial_fs_" + layerId + " group by " + fieldName + " order by " + fieldName);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        JSONObject valueJson = new JSONObject();
                        valueJson.put("value", rs.getString(2));
                        valueJson.put("count", rs.getInt(1));
                        valueList.add(valueJson);
                    }
                }
            } catch (Exception e) {
                ExceptionUtils.printExceptionTrace(e);
            } finally {
                DBHELPER.closeStatement(stmt);
            }
        } else {
            JSONObject queryJson = ArcGISServiceRestService.statisticCount(url, fieldName);
            if (queryJson != null) {
                JSONArray featuresArray = queryJson.getJSONArray("features");
                if (featuresArray != null) {
                    for (int i = 0; i < featuresArray.size(); i++) {
                        JSONObject attribJson = featuresArray.getJSONObject(i).getJSONObject("attributes");
                        int c = attribJson.getInt("C");
                        if (c == 0)
                            continue;
                        JSONObject valueJson = new JSONObject();
                        valueJson.put("value", attribJson.getString(fieldName));
                        valueJson.put("count", c);
                        valueList.add(valueJson);
                    }
                }
            }
        }
        return valueList;
    }

    /**
     * 获取图层目录树摘要信息
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public JSONObject getLayerTreeItemAbstractContent(long id) throws Exception {
        JSONObject json = null;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            String sql = "select nvl(type,0) type,layer_id,resource_type from webgis_layer_collection_item where id=" + id;
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int folderType = rs.getInt(2);
            if (folderType != 0) {
                int resourceType = rs.getInt(3);
                long resourceId = rs.getLong(2);
                String versionSQL = resourceId == 4 ? "select sum(version) from webgis_layer where p_id=" : "select version from webgis_layer where id=";
                versionSQL += resourceId;
                long version = DBHELPER.executeScalarLong(versionSQL, con);
                String cacheKey = "LayerTreeItemAbstractContent-" + id;
                json = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
                if (json == null || json.getLong("version") != version) {
                    json = new JSONObject();
                    getLayerTreeItemAbstractMetaAndAnalyInfo(con, json, resourceType, resourceId);
                    json.put("version", version);
                    json.put("r", true);
                    SystemCacheManager.getInstance().putWebGISItem(cacheKey, json);
                }
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取服务(图层)元数据及分类统计信息
     * 
     * @param con
     * @param json
     * @param int1
     * @param long1
     * @throws Exception
     */
    private void getLayerTreeItemAbstractMetaAndAnalyInfo(Connection con, JSONObject json, int type, long serverOrLayerId) throws Exception {
        String sql = (type == 4 ? "select a.id,a.title,a.type,a.download_status,c.TABLE_NAME,b.url||'/'||a.name||'/query' from user_tables c,webgis_services b,webgis_layer a where c.TABLE_NAME(+)='SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.p_id=b.id and a.p_id="
                : "select a.id,a.title,a.type,a.download_status,c.TABLE_NAME,b.url||'/'||a.name||'/query' from user_tables c,webgis_services b,webgis_layer a where c.TABLE_NAME(+)='SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.p_id=b.id and a.id=") + serverOrLayerId;
        PreparedStatement stmt = null;
        PreparedStatement fieldStmt = null;
        try {
            stmt = con.prepareStatement(sql);
            fieldStmt = con.prepareStatement("select rownum no,SCHEMA_FIELD_NAME name,title,decode(data_type,0,'字符串',1,'数字',2,'日期时间',4,'内部ID',5,'Geometry','其他') data_type,is_special,special_type from webgis_layer_fields where p_id=?");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject metaJson = new JSONObject();
                long layerId = rs.getLong(1);
                String layerName = rs.getString(2);
                metaJson.put("layerName", layerName);
                metaJson.put("layerId", layerId);
                metaJson.put("layerType", rs.getInt(3));

                fieldStmt.setLong(1, layerId);
                ResultSet fieldRs = fieldStmt.executeQuery();
                Map<String, String> specialFieldMap = new HashMap<String, String>();
                while (fieldRs.next()) {
                    JSONObject fieldJson = new JSONObject();
                    fieldJson.put("no", fieldRs.getInt(1));
                    fieldJson.put("name", fieldRs.getString(2));
                    fieldJson.put("title", fieldRs.getString(3));
                    fieldJson.put("datatype", fieldRs.getString(4));
                    JSONUtils.append(metaJson, "children", fieldJson);
                    if (fieldRs.getInt(5) == 1 && fieldRs.getInt(6) == 1) {
                        specialFieldMap.put(fieldRs.getString(2), fieldRs.getString(3));
                    }
                }

                if (rs.getInt(4) == 1 && !StringUtils.isEmpty(rs.getString(5)))
                    JSONUtils.append(json, "statistic", getLayerTreeItemAnalyInfo(con, layerName, layerId, specialFieldMap));
                else
                    JSONUtils.append(json, "statistic", getLayerTreeItemAnalyInfo(rs.getString(6), layerName, layerId, specialFieldMap));

                JSONUtils.append(json, "layers", metaJson);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeStatement(fieldStmt);
        }
    }

    /**
     * 
     * @param url
     * @param specialFieldMap
     * @return
     */
    private Object getLayerTreeItemAnalyInfo(String url, String layerName, long layerId, Map<String, String> specialFieldMap) {
        JSONObject json = new JSONObject();
        json.put("layerName", layerName);
        json.put("layerId", layerId);
        for (Entry<String, String> entry : specialFieldMap.entrySet()) {
            String fieldName = entry.getKey();
            String fieldTitle = entry.getValue();
            JSONObject fieldJson = new JSONObject();
            fieldJson.put("fieldTitle", fieldTitle);
            fieldJson.put("fieldName", fieldName);
            JSONUtils.append(json, "children", fieldJson);

            JSONObject queryJson = ArcGISServiceRestService.statisticCount(url, fieldName);
            JSONArray featuresArray = queryJson.getJSONArray("features");
            for (int m = 0; m < featuresArray.size(); m++) {
                JSONObject featureJson = featuresArray.getJSONObject(m).getJSONObject("attributes");
                int c = featureJson.getInt("C");
                if (c == 0)
                    continue;
                JSONObject recordJson = new JSONObject();
                recordJson.put("value", c);
                String valueStr = StringUtils.trim(featureJson.getString(fieldName));
                if (StringUtils.isEmpty(valueStr))
                    valueStr = "其他";
                recordJson.put("name", valueStr + "(" + c + ")");
                JSONUtils.append(fieldJson, "children", recordJson);
            }

        }

        return json;
    }

    /**
     * 获取图层分类统计信息
     * 
     * @param con
     * 
     * @param layerName
     * @param layerId
     * @param specialLayerFields
     * @return
     * @throws SQLException
     */
    private Object getLayerTreeItemAnalyInfo(Connection con, String layerName, long layerId, Map<String, String> specialLayerFields) throws SQLException {
        JSONObject json = new JSONObject();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            json.put("layerName", layerName);
            json.put("layerId", layerId);
            for (Entry<String, String> entry : specialLayerFields.entrySet()) {
                String fieldName = entry.getKey();
                String fieldTitle = entry.getValue();
                JSONObject fieldJson = new JSONObject();
                fieldJson.put("fieldTitle", fieldTitle);
                fieldJson.put("fieldName", fieldName);
                JSONUtils.append(json, "children", fieldJson);
                ResultSet rs = stmt.executeQuery("select count(*)," + fieldName + " from spatial_fs_" + layerId + " group by " + fieldName);
                while (rs.next()) {
                    JSONObject recordJson = new JSONObject();
                    recordJson.put("value", rs.getInt(1));
                    String valueStr = StringUtils.trim(rs.getString(2));
                    if (StringUtils.isEmpty(valueStr))
                        valueStr = "其他";
                    recordJson.put("name", valueStr + "(" + rs.getInt(1) + ")");
                    JSONUtils.append(fieldJson, "children", recordJson);
                }
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return json;
    }

    /**
     * 获取专题图层的详细信息
     * 
     * @param layerId
     * @return
     */
    public JSONObject getSpecialLayerDetails(long layerId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     * @param operation
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject executeMethod(String operation, String params) throws Exception {
        JSONObject paramJson = JSONUtils.parserJSONObject(params);
        JSONObject json = new JSONObject();
        Connection con = null;
        PreparedStatement appStmt = null;
        try {
            con = DBHELPER.getConnection();
            if (operation.equals("saveLayerOverlay")) {
                appStmt = con.prepareStatement("select nvl(l_c_id,-1) from webgis_app where id=?");
                appStmt.setString(1, paramJson.getString("appId"));
                ResultSet rs = appStmt.executeQuery();
                rs.next();
                saveWebGISUserConfig(con, "layerOverlay", "", paramJson.getString("userId"), rs.getString(1), "", "", paramJson.getString("content"));
            } else if (operation.equals("saveLayerSpecialSetting")) {
                saveWebGISUserConfig(con, "layerSpecialSetting", "", paramJson.getString("userId"), paramJson.getString("serverId"), "", "", paramJson.getString("content"));
            } else if (operation.equals("getLayerSpecialSetting")) {
                json.put("content", getWebGISUserConfig(con, "layerSpecialSetting", paramJson.getString("userId"), paramJson.getString("serverId"), "", ""));
            } else if (operation.equals("getResourceExtProperty")) {
                json.put("property", getResourceExtProperty(con, paramJson.getLong("userId"), paramJson.getLong("id")));
            } else if (operation.equals("saveResourceExtProperty")) {
                saveResourceExtProperty(con, paramJson.getLong("userId"), paramJson.getLong("id"), paramJson.getString("attributes"), paramJson.getString("pictures"), paramJson.getString("attachments"));
            } else if (operation.equals("saveSpatialResult")) {
                json.put("id", saveSpatialAnalyResult(con, paramJson.getLong("userId"), paramJson));
                json.put("r", true);
            } else if (operation.equals("getSpatialResultList")) {
                json.put("records", getSpatialAnalyResultList(con, paramJson.getLong("userId")));
                json.put("r", true);
            } else if (operation.equals("getSpatialResultContent")) {
                getSpatialResultContent(con, paramJson.getLong("id"), json);
                json.put("r", true);
            } else if (operation.equals("deleteSpatialAnalyResult")) {
                deleteSpatialAnalyResult(con, paramJson.getLong("id"));
                json.put("r", true);
            } else if (operation.equals("exportDataRecord2Excel")) {
                json = ExcelUtils.getInstance().createExcelByData(paramJson);
            } else if (operation.equals("getDistrictFeatures")) {
                json = getDistrictFeatures(paramJson.getJSONArray("names"));
            }

        } finally {
            DBHELPER.closeStatement(appStmt);
            DBHELPER.closeConnection(con);
        }
        json.put("r", true);
        return json;
    }

    /**
     * 
     * @param con
     * @param id
     * @param json
     * @throws SQLException
     */
    private void getSpatialResultContent(Connection con, long id, JSONObject json) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select analy_params,analy_result from WEBGIS_SPATIAL_ANALY_RESULT t where id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("analyParams", JSONUtils.parserJSONObject(Convert.bytes2Str(rs.getBytes(1))));
                json.put("analyResult", JSONUtils.parserJSONObject(Convert.bytes2Str(rs.getBytes(2))));
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 删除选定的记录
     * 
     * @param con
     * @param id
     * @throws SQLException
     */
    private void deleteSpatialAnalyResult(Connection con, long id) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("delete from WEBGIS_SPATIAL_ANALY_RESULT where id=?");
            stmt.setLong(1, id);
            stmt.execute();
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 
     * @param con
     * @param userId
     * @return
     * @throws SQLException
     */
    private JSONArray getSpatialAnalyResultList(Connection con, long userId) throws SQLException {
        PreparedStatement stmt = null;
        JSONArray result = new JSONArray();
        try {
            stmt = con.prepareStatement("select id,save_type,save_name,save_userid,is_share from webgis_spatial_analy_result order by save_datetime desc");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt(5) == 1 || rs.getLong(4) == userId) {
                    JSONObject json = new JSONObject();
                    json.put("type", rs.getString(2));
                    json.put("name", rs.getString(3));
                    json.put("id", rs.getLong(1));
                    json.put("userId", rs.getLong(4));
                    result.add(json);
                }
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return result;
    }

    /**
     * 
     * @param con
     * @param userId
     * @param paramJson
     * @return
     * @throws Exception
     */
    private long saveSpatialAnalyResult(Connection con, long userId, JSONObject paramJson) throws Exception {
        long id = DBHELPER.getUniqueID(con, "seq_for_j2ee_webgis");
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("insert into webgis_spatial_analy_result (id, layer_id, analy_type, analy_params, analy_result, save_type, save_name, save_datetime, save_userid,IS_SHARE) values (?, ?, ?, ?, ?, ?, ?, sysdate, ?,?)");
            stmt.setLong(1, id);
            stmt.setLong(2, paramJson.getLong("layerId"));
            stmt.setString(3, paramJson.getString("analyType"));
            stmt.setBytes(4, Convert.str2Bytes(paramJson.getString("analyParams")));
            stmt.setBytes(5, Convert.str2Bytes(paramJson.getString("analyResult")));
            stmt.setString(6, paramJson.getString("saveType"));
            stmt.setString(7, paramJson.getString("saveName"));
            stmt.setLong(8, userId);
            stmt.setInt(9, paramJson.getInt("isShare"));
            stmt.execute();
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return id;
    }

    /**
     * 保存附加资源
     * 
     * @param con
     * @param userId
     * @param resourceId
     * @param attributes
     * @param pictures
     * @param attachments
     * @throws Exception
     */
    private void saveResourceExtProperty(Connection con, long userId, long resourceId, String attributes, String pictures, String attachments) throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("begin update WEBGIS_RESOURCE_EXT_PROPERTY set attributes=?,pictures=?,attachments=? where id=?; if sql%notfound then insert into WEBGIS_RESOURCE_EXT_PROPERTY (attributes,pictures,attachments,id) values (?,?,?,?); end if;  end;");
            stmt.setBytes(1, Convert.str2Bytes(attributes));
            stmt.setBytes(2, Convert.str2Bytes(pictures));
            stmt.setBytes(3, Convert.str2Bytes(attachments));
            stmt.setLong(4, resourceId);
            stmt.setBytes(5, Convert.str2Bytes(attributes));
            stmt.setBytes(6, Convert.str2Bytes(pictures));
            stmt.setBytes(7, Convert.str2Bytes(attachments));
            stmt.setLong(8, resourceId);
            stmt.execute();
            String sql = StringUtils.replace("update webgis_resource_ext_property set resource_title = (select replace(wm_concat(name),',','-') from (select id, name from webgis_services where id=197633 union select id, title from webgis_layer where id in (select p_id from webgis_layer where id=197633) union select id, title from webgis_layer where id=197633 order by id)) where id=197633",
                    "197633", "" + resourceId);
            DBHELPER.execute(sql, con);
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 获取资源扩展属性
     * 
     * @param con
     * @param long1
     * @return
     * @throws SQLException
     */
    private JSONObject getResourceExtProperty(Connection con, long userId, long resourceId) throws SQLException {
        JSONObject json = null;
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select attributes,pictures,attachments from WEBGIS_RESOURCE_EXT_PROPERTY where id=?");
            stmt.setLong(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json = new JSONObject();
                json.put("attributes", JSONUtils.parserJSONArray(Convert.bytes2Str(rs.getBytes(1))));
                json.put("pictures", JSONUtils.parserJSONArray(Convert.bytes2Str(rs.getBytes(2))));
                json.put("attachments", JSONUtils.parserJSONArray(Convert.bytes2Str(rs.getBytes(3))));
                json.put("id", resourceId);
            } else if (userId == -1) {
                json = new JSONObject();
                json.put("attributes", new JSONArray());
                json.put("pictures", new JSONArray());
                json.put("attachments", new JSONArray());
                json.put("id", resourceId);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
        return json;
    }

    /**
     * 获取配置
     * 
     * @param con
     * @param category
     * @param userId
     * @param key1
     * @param key2
     * @param key3
     * @return
     * @throws SQLException
     */
    private String getWebGISUserConfig(Connection con, String category, String userId, String key1, String key2, String key3) throws SQLException {
        String result = "";
        if (StringUtils.isEmpty(category))
            category = "-";
        if (StringUtils.isEmpty(key1))
            key1 = "-";
        if (StringUtils.isEmpty(key2))
            key2 = "-";
        if (StringUtils.isEmpty(key3))
            key3 = "-";
        PreparedStatement stmt1 = null;
        try {
            String sql = "select config_content from webgis_user_config where category_key=? and key1=? and key2=? and key3=? and user_id=?";
            stmt1 = con.prepareStatement(sql);
            stmt1.setString(1, category);
            stmt1.setString(2, key1);
            stmt1.setString(3, key2);
            stmt1.setString(4, key3);
            stmt1.setString(5, userId);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next())
                result = Convert.bytes2Str(rs.getBytes(1));

        } finally {
            DBHELPER.closeStatement(stmt1);
        }
        return result;
    }

    /**
     * 保存配置
     * 
     * @param con
     * @param category
     * @param caption
     * @param userId
     * @param key1
     * @param key2
     * @param key3
     * @param content
     * @throws SQLException
     */
    private void saveWebGISUserConfig(Connection con, String category, String caption, String userId, String key1, String key2, String key3, String content) throws SQLException {
        if (StringUtils.isEmpty(category))
            category = "-";
        if (StringUtils.isEmpty(caption))
            caption = "-";
        if (StringUtils.isEmpty(key1))
            key1 = "-";
        if (StringUtils.isEmpty(key2))
            key2 = "-";
        if (StringUtils.isEmpty(key3))
            key3 = "-";
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        try {
            String sql = "select count(*) from webgis_user_config where category_key=? and key1=? and key2=? and key3=? and user_id=?";
            stmt1 = con.prepareStatement(sql);
            stmt1.setString(1, category);
            stmt1.setString(2, key1);
            stmt1.setString(3, key2);
            stmt1.setString(4, key3);
            stmt1.setString(5, userId);
            ResultSet rs = stmt1.executeQuery();
            rs.next();
            if (rs.getInt(1) == 1) {
                stmt2 = con.prepareStatement("update webgis_user_config set caption=?,config_content=? where category_key=? and key1=? and key2=? and key3=? and user_id=?");
            } else {
                stmt2 = con.prepareStatement("insert into webgis_user_config (caption,config_content,category_key, key1, key2, key3,user_id) values (?, ?, ?, ?, ?, ?, ?)");
            }
            stmt2.setString(1, caption);
            stmt2.setBytes(2, Convert.str2Bytes(content));
            stmt2.setString(3, category);
            stmt2.setString(4, key1);
            stmt2.setString(5, key2);
            stmt2.setString(6, key3);
            stmt2.setString(7, userId);
            stmt2.execute();
        } finally {
            DBHELPER.closeStatement(stmt2);
            DBHELPER.closeStatement(stmt1);
        }
    }

    /**
     * 保存扩展属性
     * 
     * @param layerId
     * @param uid
     * @param recordId
     * @param attributes
     * @return
     * @throws Exception
     */
    public String saveLayerFeatureExtProperty(String layerId, String uid, String recordId, String attributes, String pictures, String attachments) throws Exception {
        Connection con = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        PreparedStatement stmt4 = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        try {
            con = DBHELPER.getConnection();
            if (Convert.str2Long(recordId) != -1) {
                stmt1 = con.prepareStatement("update webgis_layer_marker_records set attributes=?,pictures=?,attachments=? where id=?");
                stmt1.setBytes(1, Convert.str2Bytes(attributes));
                stmt1.setBytes(2, Convert.str2Bytes(pictures));
                stmt1.setBytes(3, Convert.str2Bytes(attachments));
                stmt1.setLong(4, Convert.str2Long(recordId));
                stmt1.execute();
            } else {
                stmt2 = con.prepareStatement("select nvl(user_marker_id,0),user_marker_primary_field from webgis_layer where id=?");
                stmt2.setLong(1, Convert.str2Long(layerId));
                ResultSet rs = stmt2.executeQuery();
                rs.next();
                long userMarkerId = rs.getLong(1);
                String primarayFieldName = rs.getString(2);

                recordId = Long.toString(DBHELPER.getUniqueID(con));
                stmt1 = con.prepareStatement("insert into webgis_layer_marker_records (id,layer_id,attributes,pictures,attachments) values (?,?,?,?,?)");
                stmt1.setLong(1, Convert.str2Long(recordId));
                stmt1.setLong(2, Convert.str2Long(layerId));
                stmt1.setBytes(3, Convert.str2Bytes(attributes));
                stmt1.setBytes(4, Convert.str2Bytes(pictures));
                stmt1.setBytes(5, Convert.str2Bytes(attachments));
                stmt1.execute();

                String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id = " + layerId + " order by a.type,to_number(a.name)";
                resourceStmt = con.prepareStatement(sql2);
                ResultSet resourceRs = resourceStmt.executeQuery();
                resourceRs.next();
                boolean isLayerCached = resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4));
                String sql = "";
                if (isLayerCached) {
                    sql = "update webgis_layer_marker_records set (template_id,key_value,geometry)=(select " + userMarkerId + "," + primarayFieldName + ",c2b(sdo_util.to_wktgeometry(geometry)) from spatial_fs_" + layerId + " where q_uid=?) where id=?";
                    stmt3 = con.prepareStatement(sql);
                    stmt3.setLong(1, Convert.str2Long(uid));
                    stmt3.setLong(2, Convert.str2Long(recordId));
                    stmt3.execute();
                } else {
                    WebGISLayerFields layerFields = getLayerFields(con, Long.parseLong(layerId));
                    String url = resourceRs.getString(2);
                    JSONObject featureJson = ArcGISServiceRestService.query(url, layerFields.objectFieldName + "=" + uid, null, null, spatialConfig.wkid + "", true, true, null);
                    featureJson = featureJson.getJSONArray("features").getJSONObject(0);
                    JSONObject attribObject = featureJson.getJSONObject("attributes");
                    sql = "update webgis_layer_marker_records set template_id=" + userMarkerId + ",key_value='" + attribObject.getString(primarayFieldName) + "',geometry=? where id=?";
                    stmt3 = con.prepareStatement(sql);
                    stmt3.setBytes(1, Convert.str2Bytes(featureJson.getJSONObject("geometry").toString()));
                    stmt3.setLong(2, Convert.str2Long(recordId));
                    stmt3.execute();
                }

            }
            json.put("recordId", recordId);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt4);
            DBHELPER.closeStatement(stmt3);
            DBHELPER.closeStatement(stmt2);
            DBHELPER.closeStatement(stmt1);
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    /**
     * 获取附加属性
     * 
     * @param layerId
     * @param uid
     * @return
     */
    public String getLayerFeatureExtProperty(String layerId, String uid) throws Exception {
        Connection con = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        PreparedStatement stmt4 = null;
        PreparedStatement resourceStmt = null;
        JSONObject json = new JSONObject();
        try {
            con = DBHELPER.getConnection();
            stmt1 = con.prepareStatement("select nvl(user_marker_id,0),user_marker_primary_field from webgis_layer where id=?");
            stmt1.setLong(1, Convert.str2Long(layerId));
            ResultSet rs = stmt1.executeQuery();
            long userMarkerId = -1;
            String primarayFieldName = "";
            String primaryFieldName = "";
            if (rs.next()) {
                userMarkerId = rs.getLong(1);
                primarayFieldName = rs.getString(2);
                primaryFieldName = rs.getString(2);
            }
            if (userMarkerId != 0 && !StringUtils.isEmpty(primaryFieldName)) {
                stmt2 = con.prepareStatement("select is_attachment,is_pictures,is_attributes,nvl(picture_types,''),nvl(attachment_types,'') from webgis_layer_marker where id=?");
                stmt2.setLong(1, userMarkerId);
                rs = stmt2.executeQuery();
                if (rs.next()) {
                    json.put("isAttachment", rs.getInt(1) == 1);
                    json.put("isPicture", rs.getInt(2) == 1);
                    json.put("isExtParam", rs.getInt(3) == 1);
                    json.put("pictureTypes", rs.getString(4));
                    json.put("attachmentTypes", rs.getString(5));
                }
                if (json.getBoolean("isExtParam") || json.getBoolean("isPicture") || json.getBoolean("isAttachment")) { // 附加信息
                    stmt4 = con.prepareStatement("select caption from webgis_layer_marker_field where p_id = ? order by sortorder");
                    stmt4.setLong(1, userMarkerId);
                    List<String> fieldList = new ArrayList<String>();
                    rs = stmt4.executeQuery();
                    while (rs.next())
                        fieldList.add(rs.getString(1));

                    String sql2 = "select a.id,b.url||'/'||a.name||'/query',a.download_status,c.TABLE_NAME,b.id, nvl(layer_comment,title),b.id serverid from user_tables c,webgis_services b,webgis_layer a where a.p_id = b.id and c.TABLE_NAME(+) = 'SPATIAL_FS_'||a.id and a.name=a.equal_layer_as and a.id = " + layerId + " order by a.type,to_number(a.name)";
                    resourceStmt = con.prepareStatement(sql2);
                    ResultSet resourceRs = resourceStmt.executeQuery();
                    resourceRs.next();

                    boolean isLayerCached = resourceRs.getInt(3) == 1 && !StringUtils.isEmpty(resourceRs.getString(4));
                    String sql = "";
                    if (isLayerCached) {
                        sql = "select attributes,pictures,attachments,id from webgis_layer_marker_records a where /* a.layer_id=" + layerId + "  and */ key_value in (select " + primarayFieldName + " from spatial_fs_" + layerId + " where q_uid=?) and TEMPLATE_ID=" + userMarkerId;
                    } else {
                        WebGISLayerFields layerFields = getLayerFields(con, Long.parseLong(layerId));
                        String url = resourceRs.getString(2);
                        JSONObject featureJson = ArcGISServiceRestService.query(url, layerFields.objectFieldName + "=" + uid, null, null, spatialConfig.wkid + "", true, true, null);
                        featureJson = featureJson.getJSONArray("features").getJSONObject(0);
                        JSONObject attribObject = featureJson.getJSONObject("attributes");
                        sql = "select attributes,pictures,attachments,id from webgis_layer_marker_records a where /* a.layer_id=" + layerId + "  and */ key_value ='" + attribObject.getString(primarayFieldName) + "' and TEMPLATE_ID=" + userMarkerId;
                    }
                    stmt3 = con.prepareStatement(sql);
                    if (isLayerCached)
                        stmt3.setLong(1, Convert.str2Long(uid));

                    rs = stmt3.executeQuery();
                    JSONArray records = null;
                    JSONArray pictures = null;
                    JSONArray attachments = null;
                    if (rs.next()) {
                        records = JSONUtils.parserJSONArray(Convert.bytes2Str(rs.getBytes(1)));
                        pictures = JSONUtils.parserJSONArray(Convert.bytes2Str(rs.getBytes(2)));
                        attachments = JSONUtils.parserJSONArray(Convert.bytes2Str(rs.getBytes(3)));
                        json.put("recordId", rs.getLong(4));
                    } else {
                        records = new JSONArray();
                        pictures = new JSONArray();
                        attachments = new JSONArray();
                    }
                    Map<String, String> field2Value = new HashMap<String, String>();
                    for (int i = 0; i < records.size(); i++) {
                        JSONObject record = records.getJSONObject(i);
                        field2Value.put(record.getString("name"), record.getString("value"));
                    }
                    JSONArray extParamsArray = new JSONArray();
                    for (String fieldName : fieldList) {
                        JSONObject record = new JSONObject();
                        record.put("name", fieldName);
                        record.put("value", field2Value.get(fieldName));
                        extParamsArray.add(record);
                    }
                    for (Entry<String, String> entry : field2Value.entrySet()) {
                        if (fieldList.indexOf(entry.getKey()) == -1) {
                            JSONObject record = new JSONObject();
                            record.put("name", entry.getKey());
                            record.put("value", entry.getValue());
                            extParamsArray.add(record);
                        }
                    }
                    json.put("extParams", extParamsArray);
                    json.put("extAttachments", attachments);
                    json.put("extPictures", pictures);
                }
            } else {
                json.put("isExtParams", false);
                json.put("isAttachment", false);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(stmt4);
            DBHELPER.closeStatement(stmt3);
            DBHELPER.closeStatement(stmt2);
            DBHELPER.closeStatement(stmt1);
            DBHELPER.closeStatement(resourceStmt);
            DBHELPER.closeConnection(con);
        }
        return json.toString();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // add by cyh
    /**
     * 社会经济统计数据目录树的生成
     * 
     * @param parentJson
     * @param stmt
     * @param i
     * @throws SQLException
     */
    public JSONObject getStatisticTreePoint(long appId) throws Exception {
        JSONObject rootJson = new JSONObject();
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select id,name,type from tab_statistic_tree where p_id=? order by sortorder");
            // rootJson.put("id", -1);
            rootJson.put("id", 0);
            rootJson.put("text", "社会经济指标数据");
            rootJson.put("root", true);
            getStatisticTreePoint(rootJson, stmt, -1);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return rootJson;
    }

    /**
     * 递归获取社会经济统计数据目录树节点
     * 
     * @param parentJson
     * @param stmt
     * @param i
     * @throws SQLException
     */
    private void getStatisticTreePoint(JSONObject parentJson, PreparedStatement stmt, long pId) throws SQLException {
        stmt.setLong(1, pId);
        List<JSONObject> list = new ArrayList<JSONObject>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            JSONObject json = new JSONObject();
            json.put("text", rs.getString(2));
            json.put("id", rs.getLong(1));
            json.put("type", rs.getInt(3));
            if (rs.getInt(3) == 0) {
                json.put("state", "closed");
                list.add(json);
            }
            // JSONUtils.append(parentJson, "children", json);
            JSONUtils.append(parentJson, "item", json);
        }
        for (JSONObject json : list)
            getStatisticTreePoint(json, stmt, json.getLong("id"));
    }

    // end by cyh
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 构造函数
     */
    private WebGISResourceService4Oracle() {

    }

    private static WebGISResourceService4Oracle instance = new WebGISResourceService4Oracle();

    public static WebGISResourceService4Oracle getInstance() {
        return instance;
    }

	public JSONObject registerYeWuBiao(Map<String, String> params) throws Exception {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		json.put("r", false);
		Connection con=null;   
		String ywbbh=params.get("ywbbh");
		String sql4tabdel="delete from ywb_tab";
		String sql4tabfielddel="delete from YWB_TAB_FIELD";
		String sql4tab="insert into ywb_tab(id, table_name, table_annotation)  (select seq_for_j2ee_uniqueid.nextval,table_name,comments from user_tab_comments where table_name LIKE upper(RTRIM(LTRIM('"+ywbbh+"'))) ||'%')";
		String sql4yabfield="insert into ywb_tab_field(id, table_name, table_filed, field_type,lzs) ( select seq_for_j2ee_uniqueid.nextval id, b.table_name, b.column_name,b.DATA_TYPE, a.comments from user_col_comments a, user_tab_columns b where a.column_name=b.COLUMN_NAME and a.table_name=b.TABLE_NAME  and b.TABLE_NAME LIKE upper(RTRIM(LTRIM('"+ywbbh+"'))) || '%')";
		String sql4px="update YWB_TAB_FIELD set px=id ,bm=lzs";
		try{
			con=DBHELPER.getConnection();
			DBHELPER.execute(sql4tabdel, con);
			DBHELPER.execute(sql4tabfielddel, con);
			DBHELPER.execute(sql4tab, con);
			DBHELPER.execute(sql4yabfield, con);
			DBHELPER.execute(sql4px, con);
		}finally{
			DBHELPER.closeConnection(con);
		}
		return json;
	}
}
