package com.estudio.gis.oracle;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.SystemCacheManager;
import com.estudio.net.WebClient;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.SecurityUtils;

public class ArcGISServiceRestService {

    private static int MAX_CONNECTION_TIMEOUT = 5*60;//300秒
    /**
     * 
     * @param url
     * @param searchText
     * @param whereStr
     * @param geometry
     * @return
     */
    public static JSONObject getCount(String url, String searchText, String whereStr, String geometry) {
        JSONObject result = null;
        Map<String, String> httpParams = new HashMap<String, String>();
        httpParams.put("f", "json");
        // 查询条件
        searchText = StringUtils.trim(searchText);
        if (!StringUtils.isEmpty(searchText))
            httpParams.put("text", searchText);
        else
            httpParams.put("where", StringUtils.isEmpty(whereStr) ? "1=1" : whereStr);

        // 空间条件
        if (!StringUtils.isEmpty(geometry)) {
            httpParams.put("geometry", geometry);
            httpParams.put("geometryType", StringUtils.containsIgnoreCase(geometry, "point") ? "esriGeometryPoint" : StringUtils.containsIgnoreCase(geometry, "rings") ? "esriGeometryPolygon" : "esriGeometryPolyline");
        }
        httpParams.put("spatialRel", "esriSpatialRelIntersects");

        httpParams.put("returnCountOnly", "true");
        httpParams.put("returnIdsOnly", "false");
        try {
            String content = WebClient.post(url, "utf-8", httpParams, MAX_CONNECTION_TIMEOUT);
            result = JSONUtils.parserJSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 
     * @param url
     * @param searchText
     * @param whereStr
     * @param geometry
     * @return
     */
    public static JSONObject getObjectIds(String url, String searchText, String whereStr, String geometry, String orderFields) {
        JSONObject result = null;
        Map<String, String> httpParams = new HashMap<String, String>();
        httpParams.put("f", "json");

        // 查询条件
        searchText = StringUtils.trim(searchText);
        if (!StringUtils.isEmpty(searchText))
            httpParams.put("text", searchText);
        else
            httpParams.put("where", StringUtils.isEmpty(whereStr) ? "1=1" : whereStr);

        // 空间条件
        if (!StringUtils.isEmpty(geometry)) {
            httpParams.put("geometry", geometry);
            httpParams.put("geometryType", StringUtils.containsIgnoreCase(geometry, "point") ? "esriGeometryPoint" : StringUtils.containsIgnoreCase(geometry, "rings") ? "esriGeometryPolygon" : "esriGeometryPolyline");
        }
        httpParams.put("spatialRel", "esriSpatialRelIntersects");

        httpParams.put("returnCountOnly", "false");
        httpParams.put("returnIdsOnly", "true");
        try {
            String content = WebClient.post(url, "utf-8", httpParams, MAX_CONNECTION_TIMEOUT);
            result = JSONUtils.parserJSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询数据
     * 
     * @param url
     * @param whereStr
     * @param geometry
     * @param objectIds
     * @param wkid
     * @param isReturnGeometry
     * @param isReturnAttrib
     * @return
     */
    public static JSONObject query(String url, String whereStr, String geometry, String objectIds, String wkid, boolean isReturnGeometry, boolean isReturnAttrib, String orderFields) {
        JSONObject result = null;
        Map<String, String> httpParams = new HashMap<String, String>();
        httpParams.put("f", "json");
        httpParams.put("outSR", wkid);
        httpParams.put("inSR", wkid);

        // 查询条件
        httpParams.put("where", StringUtils.isEmpty(whereStr) ? "1=1" : whereStr);

        // 空间条件
        if (!StringUtils.isEmpty(geometry)) {
            httpParams.put("geometry", geometry);
            httpParams.put("geometryType", StringUtils.containsIgnoreCase(geometry, "point") ? "esriGeometryPoint" : StringUtils.containsIgnoreCase(geometry, "rings") ? "esriGeometryPolygon" : "esriGeometryPolyline");
        }
        httpParams.put("spatialRel", "esriSpatialRelIntersects");

        // objectIds
        if (!StringUtils.isEmpty(objectIds))
            httpParams.put("objectIds", objectIds);

        // 是否返回空间数据
        httpParams.put("returnGeometry", isReturnGeometry ? "true" : "false");

        // 排序字段
        if (!StringUtils.isEmpty(orderFields))
            httpParams.put("orderByFields", orderFields);

        if (isReturnAttrib)
            httpParams.put("outFields", "*");

        httpParams.put("returnCountOnly", "false");
        httpParams.put("returnIdsOnly", "false");
        try {
            String content = WebClient.post(url, "utf-8", httpParams, MAX_CONNECTION_TIMEOUT);
            result = JSONUtils.parserJSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 
     * @param url
     * @param fieldName
     * @return
     */
    public static JSONObject statisticCount(String url, String fieldName) {
        String cacheKey = SecurityUtils.md5(url + "-" + fieldName + "COUNT");
        JSONObject result = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
        if (result == null) {
            Map<String, String> httpParams = new HashMap<String, String>();
            httpParams.put("f", "json");
            // httpParams.put("where", "1=1");
            httpParams.put("spatialRel", "esriSpatialRelIntersects");
            httpParams.put("orderByFields", fieldName);
            httpParams.put("returnCountOnly", "false");
            httpParams.put("returnIdsOnly", "false");
            httpParams.put("groupByFieldsForStatistics", fieldName);

            String statisticStr = "[{statisticType:count,onStatisticField:" + fieldName + ",outStatisticFieldName:C}]";
            httpParams.put("outStatistics", statisticStr);

            try {
                String content = WebClient.post(url, "utf-8", httpParams, MAX_CONNECTION_TIMEOUT);
                if (!content.contains("error") && !StringUtils.isEmpty(content)) {
                    result = JSONUtils.parserJSONObject(content);
                    SystemCacheManager.getInstance().putWebGISItem(cacheKey, result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static JSONObject statisticMaxAndMin(String url, String fieldName) {
        String cacheKey = SecurityUtils.md5(url + "-" + fieldName + "MINMAX");
        JSONObject result = (JSONObject) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
        if (result == null) {
            Map<String, String> httpParams = new HashMap<String, String>();
            httpParams.put("f", "json");
            // httpParams.put("where", StringUtils.isEmpty(whereStr) ? "1=1" :
            // whereStr);
            // if (!StringUtils.isEmpty(geometry)) {
            // httpParams.put("geometry", geometry);
            // httpParams.put("geometryType",
            // StringUtils.containsIgnoreCase(geometry, "point") ?
            // "esriGeometryPoint" : StringUtils.containsIgnoreCase(geometry,
            // "rings") ? "esriGeometryPolygon" : "esriGeometryPolyline");
            // }
            httpParams.put("spatialRel", "esriSpatialRelIntersects");
            httpParams.put("returnCountOnly", "false");
            httpParams.put("returnIdsOnly", "false");

            String statisticStr = "[{statisticType:max,onStatisticField:" + fieldName + ",outStatisticFieldName:MAX},{statisticType:min,onStatisticField:" + fieldName + ",outStatisticFieldName:MIN}]";
            httpParams.put("outStatistics", statisticStr);

            try {
                String content = WebClient.post(url, "utf-8", httpParams, MAX_CONNECTION_TIMEOUT);
                if (!content.contains("error") && !StringUtils.isEmpty(content)) {
                    result = JSONUtils.parserJSONObject(content);
                    SystemCacheManager.getInstance().putWebGISItem(cacheKey, result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 
     * @param url
     * @param objectFieldName
     * @return
     */
    public static long[] getObjectLimit(String url, String objectFieldName) {
        long[] result = null;
        String cacheKey = SecurityUtils.md5(url + "-" + objectFieldName + "Limit");
        result = (long[]) SystemCacheManager.getInstance().getWebGISItem(cacheKey);
        if (result == null) {
            JSONObject queryJson = statisticMaxAndMin(url, objectFieldName);
            if (queryJson != null) {
                JSONArray featuresArray = queryJson.getJSONArray("features");
                if (featuresArray != null && !featuresArray.isEmpty()) {
                    JSONObject attribJson = featuresArray.getJSONObject(0).getJSONObject("attributes");
                    result = new long[] { attribJson.getLong("MIN"), attribJson.getLong("MAX") };
                }
            } else if (StringUtils.equalsIgnoreCase(objectFieldName, "FID")) {
                queryJson = getCount(url, null, null, null);
                if (queryJson != null) {
                    long count = queryJson.getLong("count");
                    result = new long[] { 0, count - 1 };
                }
            }
            if (result != null)
                SystemCacheManager.getInstance().putWebGISItem(cacheKey, result);
        }
        return result;
    }

}
