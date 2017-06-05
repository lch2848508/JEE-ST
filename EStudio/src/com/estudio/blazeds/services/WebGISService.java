package com.estudio.blazeds.services;

import net.minidev.json.JSONObject;

import com.estudio.gis.oracle.WebGISQueryService4Oracle;
import com.estudio.gis.oracle.WebGISResourceService4Oracle;
import com.estudio.gis.oracle.WebGISSpatialAnalyService4Oracle;
import com.estudio.utils.ExceptionUtils;

public class WebGISService {
	public String getTrafficTableName(String lineName,String startMileage){
		String result="{没有数据}";
		try{
			result=WebGISQueryService4Oracle.getInstance().getTrafficFlowTableName(lineName,startMileage);
		}catch(final Exception e)
		{
			ExceptionUtils.loggerException(e);
		}
		return result;
	}
	public String getTrafficHeaderData(String selectedTable){
		String result="{没有数据}";
		try
		{
			result=WebGISQueryService4Oracle.getInstance().getTrafficFlowGridHeaderData(selectedTable);
		}catch(final Exception e)
		{
			ExceptionUtils.loggerException(e);
		}
	return result;
	}
	public String getTrafficDataRecords(String startMileage,String lineName,String selectedTable){
		JSONObject result=new JSONObject();
		String dataRecord="{没有数据}";
		String headerData="{没有数据}";
		try
		{
			dataRecord=WebGISQueryService4Oracle.getInstance().getTrafficFlowDatas(startMileage,lineName, selectedTable);
			headerData=WebGISQueryService4Oracle.getInstance().getTrafficFlowGridHeaderData(selectedTable);
			result.put("dataRecord", dataRecord);
			result.put("headerData", headerData);
			//System.out.println("dataRecord:"+dataRecord);
			//System.out.println("headerData:"+headerData);
		}catch(final Exception e)
		{
			ExceptionUtils.loggerException(e);
		}
	return result.toString();
	}
    public String getAppConfig(long appId, long userId, long appVersion, long layerVersion) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getAppConfig(appId, userId, appVersion, layerVersion);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getAppConfigByName(String appName, long userId, long appVersion, long layerVersion) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getAppConfig(appName, userId, appVersion, layerVersion);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    /**
     * 
     * @param wkid
     * @param params
     * @return
     */
    public String getMapCenterInDistrict(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getMapCenterInDistrict(params).toString();
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result; 
    }

    public String getLayerFeatureProperty(String layerId, String keyField, String keyValue) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getLayerFeatureProperty(layerId, keyField, keyValue);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    /**
     * 得到一个具体的实例
     * 
     * @param layerId
     * @param keyField
     * @param keyValue
     * @param returnAttributes
     * @return
     */
    public static String getLayerFeature(String layerId, String keyField, String keyValue, boolean isStringValue, boolean returnAttributes) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().getLayerFeature(layerId, keyField, keyValue, isStringValue, returnAttributes);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    /**
     * 点选识别
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String identify(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().identify(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;

    }

    /**
     * 查询
     * 
     * @param wkid
     * @param params
     * @return
     * @throws Exception
     */
    public String search(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().search(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    public String searchEx(long userId, long layerId, String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().searchEx(userId, layerId, params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    // ///////////////////////////////////////////////////////////////////////////////////

    /**
     * 空间查询
     * 
     * @param wkid
     * @param params
     * @return
     * @throws Exception
     */
    public String spatialSearch(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().spatialSearch(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

 // ///////////////////////////////////////////////////////////////////////////////////

    /**
     * 空间查询 公交线路
     * 
     * @param wkid
     * @param params
     * @return
     * @throws Exception
     */
    public String spatialSearchByGJXL(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().spatialSearchByGJXL(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }
    
    public String projectMileage(String params){
    	 String result = "{\"r\" : false}";
         try {
             result = WebGISQueryService4Oracle.getInstance().projectMileage(params);
         } catch (final Exception e) {
             ExceptionUtils.loggerException(e);
         }
         return result;
    }
    /**
     * 空间分析
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String spatialAnaly(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISSpatialAnalyService4Oracle.instance.spatialAnaly(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    /**
     * 获取图层或服务信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public String getServerOrLayerInfo(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getServerOrLayerInfo(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String exportServerOrLayerFeature2File(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().exportServerOrLayerFeature2File(params);
        } catch (final Exception e) {

            ExceptionUtils.printExceptionTrace(e);
        }
        return result;
    }

    public String getLayerFeatures(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().getLayerFeatures(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String exportLayerFeature2File(String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().exportLayerFeature2File(params);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    /**
     * 
     * @param wkid
     * @param id
     * @return
     * @throws Exception
     */
    public String getFeatureProperty(long layerId, long uniqueId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().getFeatureProperty(layerId, uniqueId);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getFeaturesProperty(long layerId, String ids) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISQueryService4Oracle.getInstance().getFeaturesProperty(layerId, ids);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getCompareSchema(long appId, long userId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getCompareSchema(appId, userId);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String newCompareSchema(long appId, long userId, long pId, int type, String caption, String config) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().newCompareSchema(appId, userId, pId, type, caption, config);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String deleteCompareSchema(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().deleteCompareSchema(id);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getCompareSchemaConfig(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getCompareSchemaConfig(id);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }
    public String newMapShare(long state,long appId, long userId, long pId, int type, String caption, String config) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().newMapShare(state,appId, userId, pId, type, caption, config);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }
    public String getMapShareConfig(long state) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getMapShareConfig(state);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getMapFavorite(long appId, long userId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getMapFavorite(appId, userId);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String newMapFavorite(long appId, long userId, long pId, int type, String caption, String config) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().newMapFavorite(appId, userId, pId, type, caption, config);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String deleteMapFavorite(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().deleteMapFavorite(id);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getMapFavoriteConfig(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getMapFavoriteConfig(id);
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getMapAreaNavigatorFeatures(long appId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getMapAreaNavigatorFeatures(appId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getStatisticTree(long appId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getStatisticTree(appId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getStatisticData(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getStatisticData(id).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getScaleLayerFeatures(String layerId, double scale, String extFieldName, boolean isCalcCenterPoint) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getScaleLayerFeatures(layerId, scale, extFieldName, isCalcCenterPoint).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getDistrictFeature(String id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getDistrictFeature(id).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String deleteUserMarker(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().deleteUserMarker(id).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String addUserMarker(long userId, String caption, String content, String attributes, String geometry, String symbol, boolean is_share, String type, String pictures, String attachments) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance ().addUserMarker(userId, caption, content, attributes, geometry, symbol, is_share, type, pictures, attachments).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String updateUserMarker(long id, String caption, String content, String attributes, String geometry, String symbol, boolean is_share, String type, String pictures, String attachments) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().updateUserMarker(id, caption, content, attributes, geometry, symbol, is_share, type, pictures, attachments).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getUserMarker(long userId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getUserMarker(userId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getMISLayers(long userId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getMISLayers(userId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getMISLayerRecords(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getMISLayerRecords(id).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getSpecialLayers(boolean isServer, long serverId, long layerId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getSpecialLayers(isServer, serverId, layerId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }
    
    public String getSpecialMisLayers(boolean isServer, long serverId, long layerId) {
    	String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getSpecialMisLayers(isServer, serverId, layerId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }
    

    public String getQueryLayers(boolean isServer, long serverId, long layerId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getQueryLayers(isServer, serverId, layerId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getSpecialLayerDetails(long layerId) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getSpecialLayerDetails(layerId).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getLayerTreeItemAbstractContent(long id) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getLayerTreeItemAbstractContent(id).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String executeMethod(String operation, String params) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().executeMethod(operation, params).toString();
        } catch (Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String getFeatureExtProperty(String layerId, String uid) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().getLayerFeatureExtProperty(layerId, uid);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    public String saveLayerFeatureExtProperty(String layerId, String uid, String recordId, String attributes, String pictures, String attachments) {
        String result = "{\"r\" : false}";
        try {
            result = WebGISResourceService4Oracle.getInstance().saveLayerFeatureExtProperty(layerId, uid, recordId, attributes, pictures, attachments);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

}
