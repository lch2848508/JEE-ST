package com.estudio.gis.oracle;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.gis.GeometryUtils;
import com.estudio.gis.WebGISSpatialConfig;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.JSONUtils;

public class WebGISSpatialAnalyService4Oracle {
	private static IDBHelper DBHELPER = RuntimeContext.getDbHelper();
	private AtomicLong resultJSIndex = new AtomicLong(10000);
	private WebGISSpatialConfig spatialConfig = null;
	private double[] districtSumGeometryInfo = new double[] { 0, 0 };
	private double geometryPrecision = 50; // 地图精度50M
	private int maxGeometryNum = 1000;

	/**
	 * 初始化坐标配置
	 * 
	 * @param spatialConfig
	 * @throws Exception
	 */
	public void initSpatialConfig(WebGISSpatialConfig spatialConfig) {
		this.spatialConfig = spatialConfig;
		startDeleteTemplateFileThread();
	}

	/**
	 * 
	 * @param paramJson
	 * @param json
	 * @return
	 * @throws Exception
	 */
	private String generateWhereAndResultJSFile(JSONObject paramJson,
			JSONObject json) throws Exception {
		String fileName = resultJSIndex.incrementAndGet() + ".js";
		String jsContent = "var spatialAnalyCondition = "
				+ paramJson.toString() + ";\n";
		jsContent += "var spatialAnalyResult = " + json.toString() + ";";
		String saveFileName = RuntimeContext.getAppTempDir()
				+ "/webgis_spatial_temp/";// fileName;
		File dirFile = new File(saveFileName);
		if (!dirFile.exists())
			dirFile.mkdirs();
		FileUtils.writeStringToFile(new File(saveFileName + fileName),
				jsContent, Charset.forName("utf-8"));
		return fileName;
	}

	/**
	 * 执行后台删除文件代码
	 */
	private void startDeleteTemplateFileThread() {
		Runnable runnable = new Runnable() {
			public void run() {
				File dir = new File(RuntimeContext.getAppTempDir()
						+ "/webgis_spatial_temp/");
				if (dir.exists()) {
					File[] files = dir.listFiles();
					if (files != null)
						for (File f : files) {
							if (!f.isFile())
								continue;
							if (Calendar.getInstance().getTimeInMillis()
									- f.lastModified() > 60 * 60 * 1000)
								FileUtils.deleteQuietly(f);
						}
				}
			}
		};
		ScheduledExecutorService service = Executors
				.newSingleThreadScheduledExecutor();
		// 第二个参数为首次执行的延时时间,第三个参数为定时执行的间隔时间
		service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.HOURS);
	}

//<<<<<<< .mine
	/**
	 * 空间分析
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String spatialAnaly(String params) throws Exception {
		String result = "{\"r\":false}";
		Connection con = null;
		try {
			con = DBHELPER.getConnection();
			JSONObject paramJson = JSONUtils.parserJSONObject(params);
			String operation = paramJson.getString("o");
			if (StringUtils.equalsIgnoreCase(operation, "bufAnaly")) // 空间缓冲分析
				result = spatial4BufAnaly(con, paramJson);
			else if (StringUtils.equalsIgnoreCase(operation, "districtAnaly")) // 行政区域分析
				result = spatial4DistrictAnaly(con, paramJson);
			else if (StringUtils.equalsIgnoreCase(operation, "groupAnaly")) // 分类汇总分析
				result = spatial4GroupAnaly(con, paramJson);
			else if (StringUtils.equalsIgnoreCase(operation, "pathAnaly")) // 路径分析
				result = spatial4PathAnaly(con, paramJson);
			else if (StringUtils.equalsIgnoreCase(operation,
					"getSpatialAnalyTemplate"))
				result = getSpatialAnalyTemplate(con);
			else if (StringUtils.equalsIgnoreCase(operation,
					"findMaxOrMinGeometry"))
				result = getSpatialGeometryByAnalyMaxOrMinResult(con, paramJson);
			else if (StringUtils.equalsIgnoreCase(operation,
					"getSpatialResultGeometrys"))
				result = getSpatialAnalyResultGeometrys(con, paramJson);
			else if (StringUtils.equalsIgnoreCase(operation,
					"roadnetworkcoveragerate")) // 路网分析 行政区域分析
				result = spatial4RoadCoveragre(con, paramJson);
		} finally {
			DBHELPER.closeConnection(con);
		}
		return result;
	}
//=======
////<<<<<<< .mine
//	/**
//	 * 空间分析
//	 * 
//	 * @param params
//	 * @return
//	 * @throws Exception
//	 */
//	public String spatialAnaly(String params) throws Exception {
//		String result = "{\"r\":false}";
//		Connection con = null;
//		try {
//			con = DBHELPER.getConnection();
//			JSONObject paramJson = JSONUtils.parserJSONObject(params);
//			String operation = paramJson.getString("o");
//			if (StringUtils.equalsIgnoreCase(operation, "bufAnaly")) // 空间缓冲分析
//				result = spatial4BufAnaly(con, paramJson);
//			else if (StringUtils.equalsIgnoreCase(operation, "districtAnaly")) // 行政区域分析
//				result = spatial4DistrictAnaly(con, paramJson);
//			else if (StringUtils.equalsIgnoreCase(operation, "groupAnaly")) // 分类汇总分析
//				result = spatial4GroupAnaly(con, paramJson);
//			else if (StringUtils.equalsIgnoreCase(operation, "pathAnaly")) // 路径分析
//				result = spatial4PathAnaly(con, paramJson);
//			else if (StringUtils.equalsIgnoreCase(operation,
//					"getSpatialAnalyTemplate"))
//				result = getSpatialAnalyTemplate(con);
//			else if (StringUtils.equalsIgnoreCase(operation,
//					"findMaxOrMinGeometry"))
//				result = getSpatialGeometryByAnalyMaxOrMinResult(con, paramJson);
//			else if (StringUtils.equalsIgnoreCase(operation,
//					"getSpatialResultGeometrys"))
//				result = getSpatialAnalyResultGeometrys(con, paramJson);
//			else if (StringUtils.equalsIgnoreCase(operation,
//					"roadnetworkcoveragerate")) // 路网分析 行政区域分析
//				result = spatial4RoadCoveragre(con, paramJson);
//		} finally {
//			DBHELPER.closeConnection(con);
//		}
//		return result;
//	}
//=======
    /**
     * 空间分析
     * 
     * @param params
     * @return
     * @throws Exception
     */
//    public String spatialAnaly(String params) throws Exception {
//        String result = "{\"r\":false}";
//        Connection con = null;
//        try {
//            con = DBHELPER.getConnection();
//            JSONObject paramJson = JSONUtils.parserJSONObject(params);
//            String operation = paramJson.getString("o");
//            if (StringUtils.equalsIgnoreCase(operation, "bufAnaly")) // 空间缓冲分析
//                result = spatial4BufAnaly(con, paramJson);
//            else if (StringUtils.equalsIgnoreCase(operation, "districtAnaly")) // 行政区域分析
//                result = spatial4DistrictAnaly(con, paramJson);
//            else if (StringUtils.equalsIgnoreCase(operation, "groupAnaly")) // 分类汇总分析
//                result = spatial4GroupAnaly(con, paramJson);
//            else if (StringUtils.equalsIgnoreCase(operation, "pathAnaly")) // 路径分析
//                result = spatial4PathAnaly(con, paramJson);
//            else if (StringUtils.equalsIgnoreCase(operation, "getSpatialAnalyTemplate"))
//                result = getSpatialAnalyTemplate(con);
//            else if (StringUtils.equalsIgnoreCase(operation, "findMaxOrMinGeometry"))
//                result = getSpatialGeometryByAnalyMaxOrMinResult(con, paramJson);
//            else if (StringUtils.equalsIgnoreCase(operation, "getSpatialResultGeometrys"))
//                result = getSpatialAnalyResultGeometrys(con, paramJson);
//            else if (StringUtils.equalsIgnoreCase(operation, "roadnetworkcoveragerate")) // 行政区域分析
//                result = spatial4RoadCoveragre(con, paramJson);
//        } finally {
//            DBHELPER.closeConnection(con);
//        }
//        return result;
//    }
//>>>>>>> .r149
//>>>>>>> .r170

	/**
	 * 
	 * @param con
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private String getSpatialAnalyResultGeometrys(Connection con,
			JSONObject paramJson) throws Exception {
		JSONObject json = new JSONObject();
		WKT wkt = new WKT();
		json.put("r", false);
		String sql = "";
		String layerId = paramJson.getString("layerId");
		int layerType = paramJson.getInt("layerType");
		JSONObject whereJson = paramJson.getJSONObject("where");
		String whereStr = "";
		for (Entry<String, Object> entry : whereJson.entrySet()) {
			String whereFieldName = entry.getKey();
			if (!StringUtils.equals(whereFieldName,
					whereFieldName.toUpperCase()))
				continue;
			if (entry.getValue() == null)
				whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
						+ "nvl(a." + whereFieldName + ",' ')=' '";
			else
				whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
						+ "a." + whereFieldName + "='" + entry.getValue() + "'";
		}

		JGeometry filterGeometry = null;
		if (whereJson.containsKey("geometry")) {
			filterGeometry = OracleSpatialUtils.arcGISGeometryJson2JGeometry(
					whereJson.getJSONObject("geometry"),
					whereJson.getString("geometryType"), 0, spatialConfig.wkid);
			whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
					+ "SDO_ANYINTERACT(a.geometry,?)='TRUE' ";
		}

		String filterStr = paramJson.getJSONObject("where").getString(
				"filterStr");
		String filterSQL = generateFilterSQLCondition(filterStr);
		if (!StringUtils.isEmpty(filterSQL))
			whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
					+ filterSQL + " ";

		if (paramJson.getBoolean("isDistrictAnaly")) {
			sql = "select a.q_uid from spatial_fs_" + layerId
					+ " a,spatial_stat_" + layerId
					+ " b where {WHERE} a.q_uid=b.q_uid and b.city_code='"
					+ whereJson.getString("city_code") + "'";
			if (layerType == 1)
				sql += " order by b.geometry_length desc";
			else if (layerType == 2)
				sql += " order by b.geometry_area desc";
			if (!StringUtils.isEmpty(whereStr))
				whereStr += " and ";
			sql = StringUtils.replace(sql, "{WHERE}", whereStr);
		} else {
			sql = "select a.q_uid from spatial_fs_" + layerId
					+ " a where {WHERE} ";
			if (layerType == 1)
				sql += "order by a.geometry_length desc";
			else if (layerType == 2)
				sql += "order by a.geometry_area desc";
			sql = StringUtils.replace(sql, "{WHERE}", whereStr);
		}
		PreparedStatement stmt = null;
		try {
			sql = "select sdo_util.simplify(geometry," + geometryPrecision
					+ ") from spatial_fs_" + layerId
					+ " where q_uid in (select q_uid from (" + sql
					+ ") where rownum<" + maxGeometryNum + ")";
			stmt = con.prepareStatement(sql);
			if (filterGeometry != null)
				stmt.setObject(1, JGeometry.store(
						DBHELPER.getNaviteConnection(con), filterGeometry));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				byte[] bytes = rs.getBytes(1);
				if (bytes != null && bytes.length != 0) {
					JGeometry shape = JGeometry.load(bytes);
					String wktStr = new String(wkt.fromJGeometry(shape));
					JSONUtils.append(json, "records",
							GeometryUtils.wkt2ArcGISGeometryJSON(wktStr));
				}
			}
			json.put("r", true);
		} finally {
			DBHELPER.closeStatement(stmt);
		}
		return json.toString();
	}

	/**
	 * 
	 * @param con
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private String getSpatialGeometryByAnalyMaxOrMinResult(Connection con,
			JSONObject paramJson) throws Exception {
		JSONObject json = new JSONObject();
		json.put("r", false);
		String sql = "";
		String layerId = paramJson.getString("layerId");
		JSONObject whereJson = paramJson.getJSONObject("where");
		String whereStr = "";
		for (Entry<String, Object> entry : whereJson.entrySet()) {
			String whereFieldName = entry.getKey();
			if (!StringUtils.equals(whereFieldName,
					whereFieldName.toUpperCase()))
				continue;
			if (entry.getValue() == null)
				whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
						+ "nvl(a." + whereFieldName + ",' ')=' '";
			else
				whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
						+ "a." + whereFieldName + "='" + entry.getValue() + "'";
		}
		JGeometry filterGeometry = null;
		if (whereJson.containsKey("geometry")) {
			filterGeometry = OracleSpatialUtils.arcGISGeometryJson2JGeometry(
					whereJson.getJSONObject("geometry"),
					whereJson.getString("geometryType"), 0, spatialConfig.wkid);
			whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
					+ "SDO_ANYINTERACT(a.geometry,?)='TRUE' ";
		}
		String filterStr = whereJson.getString("filterStr");
		String filterSQL = generateFilterSQLCondition(filterStr);
		if (!StringUtils.isEmpty(filterSQL))
			whereStr += (StringUtils.isEmpty(whereStr) ? "" : " and ")
					+ filterSQL + " ";

		String clickField = paramJson.getString("click_field");
		if (paramJson.getBoolean("isDistrictAnaly")) {
			sql = "select a.geometry from spatial_fs_" + layerId
					+ " a,spatial_stat_" + layerId
					+ " b where {WHERE} a.q_uid=b.q_uid and b.city_code="
					+ whereJson.getString("city_code") + " order by ";
			if (StringUtils.equals(clickField, "MAX_GEO_LENGTH"))
				sql += "b.geometry_length desc";
			else if (StringUtils.equals(clickField, "MIN_GEO_LENGTH"))
				sql += "b.geometry_length asc";
			else if (StringUtils.equals(clickField, "MAX_GEO_AREA"))
				sql += "b.geometry_area desc";
			else if (StringUtils.equals(clickField, "MIN_GEO_AREA"))
				sql += "b.geometry_area asc";
			if (!StringUtils.isEmpty(whereStr))
				whereStr += " and ";
			sql = StringUtils.replace(sql, "{WHERE}", whereStr);
		} else {
			sql = "select a.geometry from spatial_fs_" + layerId
					+ " a where {WHERE} order by ";
			if (StringUtils.equals(clickField, "MAX_GEO_LENGTH"))
				sql += "a.geometry_length desc";
			else if (StringUtils.equals(clickField, "MIN_GEO_LENGTH"))
				sql += "a.geometry_length asc";
			else if (StringUtils.equals(clickField, "MAX_GEO_AREA"))
				sql += "a.geometry_area desc";
			else if (StringUtils.equals(clickField, "MIN_GEO_AREA"))
				sql += "a.geometry_area asc";
			if (StringUtils.isEmpty(whereStr))
				whereStr = "1=1";
			sql = StringUtils.replace(sql, "{WHERE}", whereStr);
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			if (filterGeometry != null)
				stmt.setObject(1, JGeometry.store(
						DBHELPER.getNaviteConnection(con), filterGeometry));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				byte[] bytes = rs.getBytes(1);
				if (bytes != null && bytes.length != 0) {
					JGeometry shape = JGeometry.load(bytes);
					String wktStr = new String(new WKT().fromJGeometry(shape));
					json.put("wkt",
							GeometryUtils.wkt2ArcGISGeometryJSON(wktStr));
				}
				json.put("r", true);
			}

		} finally {
			DBHELPER.closeStatement(stmt);
		}
		return json.toString();
	}

	/**
	 * 获取模板
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	private String getSpatialAnalyTemplate(Connection con) throws Exception {
		JSONObject json = new JSONObject();
		json.put("r", false);
		PreparedStatement stmt = null;
		try {
			stmt = con
					.prepareStatement("select caption,type,content from webgis_spatial_analy_template order by sortorder");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject recordJson = new JSONObject();
				recordJson.put("caption", rs.getString(1));
				recordJson.put("content", rs.getString(3));
				if (rs.getInt(2) == 0)
					JSONUtils.append(json, "functions", recordJson);
				else
					JSONUtils.append(json, "formulas", recordJson);
			}
			json.put("r", true);
		} finally {
			DBHELPER.closeStatement(stmt);
		}
		return json.toString();
	}

	/**
	 * 空间缓冲区分析
	 * 
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private String spatial4BufAnaly(Connection con, JSONObject paramJson)
			throws Exception {
		return spatial4CommonStatisticAnaly(con, paramJson);
	}

	/**
	 * 行政区域分析
	 * 
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private String spatial4DistrictAnaly(Connection con, JSONObject paramJson)
			throws Exception {
		JSONObject json = new JSONObject();
		json.put("r", false);
		long layerId = paramJson.getLong("layerId");
		PreparedStatement stmt = null;
		List<String> groupFields = JSONUtils.array2StringList(paramJson
				.getJSONArray("groupFields"));
		List<String> statisticFunctions = JSONUtils.array2StringList(paramJson
				.getJSONArray("statisticFunctions"));
		List<String> cityCodes = JSONUtils.array2StringList(paramJson
				.getJSONArray("cityCodes"));
		String filterStr = paramJson.getString("filterStr");
		String filterSQL = generateFilterSQLCondition(filterStr);
		boolean isGeometryFilter = paramJson.containsKey("geometry")
				&& paramJson.containsKey("geometryType");
		JGeometry filterGeometry = null;
		try {
			if (isGeometryFilter)
				filterGeometry = OracleSpatialUtils
						.arcGISGeometryJson2JGeometry(
								paramJson.getJSONObject("geometry"),
								paramJson.getString("geometryType"), 0,
								spatialConfig.wkid);

			String sql = "select b.city_name,b.city_code,";
			for (String fieldName : groupFields)
				sql += "a." + fieldName + ",";
			for (String funName : statisticFunctions) {
				if ("count".equals(funName))
					sql += "count(1) as count,";
				else if ("sum".equals(funName))
					sql += "fun_round_tow_decimal(sum(b.geometry_length)) as sum_geo_length,fun_round_tow_decimal(sum(b.geometry_area)) as sum_geo_area,";
				else if ("max".equals(funName))
					sql += "fun_round_tow_decimal(max(b.geometry_length)) as max_geo_length,fun_round_tow_decimal(max(b.geometry_area)) as max_geo_area,";
				else if ("min".equals(funName))
					sql += "fun_round_tow_decimal(min(b.geometry_length)) as min_geo_length,fun_round_tow_decimal(min(b.geometry_area)) as min_geo_area,";
				else
					sql += StringUtils.replaceEach(funName, new String[] {
							"BUF_GEOMETRY_LENGTH", "BUF_GEOMETRY_AREA",
							"LAYER_GEOMETRY_LENGTH", "LAYER_GEOMETRY_AREA" },
							new String[] { "c.geometry_length",
									"c.geometry_area", "b.geometry_length",
									"b.geometry_area" })
							+ ",";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += " from spatial_fs_"
					+ layerId
					+ " a,spatial_stat_"
					+ layerId
					+ " b,sys_district c where a.q_uid=b.q_uid and c.code=b.city_code";
			if (!cityCodes.isEmpty()) {
				sql += " and b.city_code in (";
				for (String cityCode : cityCodes)
					sql += "'" + cityCode + "',";
				sql = sql.substring(0, sql.length() - 1);
				sql += ")";
			}
			// where 条件
			if (!StringUtils.isEmpty(filterSQL))
				sql += " and " + filterSQL;

			if (isGeometryFilter)
				sql += " and SDO_ANYINTERACT(a.geometry,?)='TRUE'";

			sql += " group by b.city_name,b.city_code,";
			for (String fieldName : groupFields)
				sql += "a." + fieldName + ",";
			sql = sql.substring(0, sql.length() - 1);

			sql += " order by b.city_code";

			for (int i = 0; i < groupFields.size(); i++)
				sql += "," + (i + 3);

			stmt = con.prepareStatement(sql);

//<<<<<<< .mine
			if (isGeometryFilter)
				stmt.setObject(1, JGeometry.store(
						DBHELPER.getNaviteConnection(con), filterGeometry));
//=======
////<<<<<<< .mine
//			if (isGeometryFilter)
//				stmt.setObject(1, JGeometry.store(
//						DBHELPER.getNaviteConnection(con), filterGeometry));
////=======
////            stmt = con.prepareStatement(sql);
////            
////            if (isGeometryFilter)
////                stmt.setObject(1, JGeometry.store(DBHELPER.getNaviteConnection(con), filterGeometry));
////>>>>>>> .r149
//>>>>>>> .r170

			// 设置参数
			ResultSet rs = stmt.executeQuery();

//<<<<<<< .mine
			json.put("records", DBHELPER.resultSet2JSONArray(rs));
			json.put("r", true);
			json.put("jsFile", generateWhereAndResultJSFile(paramJson, json));
		} finally {
			DBHELPER.closeStatement(stmt);
		}
		return json.toString();
	}

	/**
	 * 道路覆盖率分析
	 * 
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private String spatial4RoadCoveragre(Connection con, JSONObject paramJson)
			throws Exception {
		JSONObject json = new JSONObject();
		json.put("r", false);
		long layerId = paramJson.getLong("layerId");
		List<String> groupFields = JSONUtils.array2StringList(paramJson
				.getJSONArray("groupFields"));
		List<String> cityCodes = JSONUtils.array2StringList(paramJson
				.getJSONArray("cityCodes"));
		List<String> qx = JSONUtils.array2StringList(paramJson
				.getJSONArray("qx"));
		List<String> xz = JSONUtils.array2StringList(paramJson
				.getJSONArray("xz"));
		List<String> statisticFunctions = JSONUtils.array2StringList(paramJson
				.getJSONArray("statisticFunctions"));
		PreparedStatement stmt = null;
		PreparedStatement stmtxz = null;

		try {
			JSONArray resultArray = new JSONArray();
			for (int j = 0; j < cityCodes.size(); j++) {
				List<String> cityCode = new ArrayList<String>();
				cityCode.add(cityCodes.get(j));
				resultArray = mergeJSONArray(
						resultArray,
						getSQL4RoadCoveragre(groupFields, statisticFunctions,
								layerId, cityCode, con, 0, 0));

				if (qx.size() != 0 && qx.get(0) == "true") {
					String sql4qx = "select * from sys_district_ex t where t.p_id=?";
					List<String> tempqxList = new ArrayList<String>();
					List<String> tempqxList1 = new ArrayList<String>();
					int p_id = Integer.parseInt(cityCodes.get(j));
					stmt = con.prepareStatement(sql4qx);
					stmt.setInt(1, p_id);
					ResultSet rs_qx = stmt.executeQuery();
					while (rs_qx.next()) {
						String xz_p_id = String.valueOf(rs_qx.getInt("id"));
						tempqxList.add(xz_p_id);
						tempqxList1.add(xz_p_id);
						if (tempqxList.size() != 0) {
							JSONArray resultArrayQX = getSQL4RoadCoveragre(
									groupFields, statisticFunctions, layerId,
									tempqxList, con, p_id, 0);
							resultArray = mergeJSONArray(resultArray,
									resultArrayQX);
						}
						List<String> tempxzList = new ArrayList<String>();
						if (xz.size() != 0 && xz.get(0) == "true") {
							stmtxz = con.prepareStatement(sql4qx);
							stmtxz.setInt(1, Integer.parseInt(xz_p_id));
							ResultSet rs_xz = stmtxz.executeQuery();
							while (rs_xz.next()) {
								tempxzList.add(String.valueOf(rs_xz
										.getInt("id")));
							}
							DBHELPER.closeStatement(stmtxz);
							if (tempxzList.size() != 0) {
								JSONArray resultArrayXZ = getSQL4RoadCoveragre(
										groupFields, statisticFunctions,
										layerId, tempxzList, con, p_id,
										Integer.parseInt(xz_p_id));
								resultArray = mergeJSONArray(resultArray,
										resultArrayXZ);
							}
						}
						tempqxList.clear();
					}
				}
//=======
//			json.put("records", DBHELPER.resultSet2JSONArray(rs));
//			json.put("r", true);
//			json.put("jsFile", generateWhereAndResultJSFile(paramJson, json));
//		} finally {
//			DBHELPER.closeStatement(stmt);
//		}
//		return json.toString();
//	}
//    /**
//     * 道路覆盖率分析
//     * 
//     * @param paramJson
//     * @return
//     * @throws Exception
//     */
//    private String spatial4RoadCoveragre(Connection con, JSONObject paramJson) throws Exception {
//    	JSONObject json = new JSONObject();
//        json.put("r", false);
//        long layerId=paramJson.getLong("layerId");
//        List<String> groupFields=JSONUtils.array2StringList(paramJson.getJSONArray("groupFields"));
//        List<String> cityCodes=JSONUtils.array2StringList(paramJson.getJSONArray("cityCodes"));
//        List<String> qx=JSONUtils.array2StringList(paramJson.getJSONArray("qx"));
//        List<String> xz=JSONUtils.array2StringList(paramJson.getJSONArray("xz"));
//        List<String> statisticFunctions=JSONUtils.array2StringList(paramJson.getJSONArray("statisticFunctions"));
//        PreparedStatement stmt=null;
//        PreparedStatement stmtxz=null;
//        try{
//        	JSONArray resultArray=new JSONArray();
//        	for(int j=0;j<cityCodes.size();j++){
//        		List<String> cityCode=new ArrayList<String>();
//        		cityCode.add(cityCodes.get(j));
//	        	resultArray=mergeJSONArray(resultArray,getSQL4RoadCoveragre(groupFields,statisticFunctions,layerId,cityCode,con,0,0));
//	        	
//	        	if(qx.size()!=0&&qx.get(0)=="true"){
//	        		String sql4qx="select * from sys_district_ex t where t.p_id=?";
//        			List<String> tempqxList=new ArrayList<String>();
//        			int p_id=Integer.parseInt(cityCodes.get(j));
//        			stmt=con.prepareStatement(sql4qx);
//        			stmt.setInt(1, p_id);
//        			ResultSet rs_qx=stmt.executeQuery();
//        			while(rs_qx.next()){
//        				String xz_p_id=String.valueOf(rs_qx.getInt("id"));
//        				tempqxList.add(xz_p_id);
//        				if(tempqxList.size()!=0){
//    	        			JSONArray resultArrayQX= getSQL4RoadCoveragre(groupFields,statisticFunctions,layerId,tempqxList,con,p_id,0);
//    	    				resultArray=mergeJSONArray(resultArray,resultArrayQX);
//            			}
//        				List<String> tempxzList=new ArrayList<String>();
//        				if(xz.size()!=0&&xz.get(0)=="true"){
//	        				stmtxz=con.prepareStatement(sql4qx);
//	        				stmtxz.setInt(1, Integer.parseInt(xz_p_id));
//	        				ResultSet rs_xz=stmtxz.executeQuery();
//	        				while(rs_xz.next()){
//	        					tempxzList.add(String.valueOf(rs_xz.getInt("id")));
//	        				}
//	        				DBHELPER.closeStatement(stmtxz);
//	        				if(tempxzList.size()!=0){
//		        				JSONArray resultArrayXZ= getSQL4RoadCoveragre(groupFields,statisticFunctions,layerId,tempxzList,con,p_id,Integer.parseInt(xz_p_id));
//		        				resultArray=mergeJSONArray(resultArray,resultArrayXZ);
//	        				}
//        				}
//        				tempqxList.clear();
//        			}
//	            }
//           }
//           json.put("records", resultArray); 
//           json.put("r", true);
//           
//           JSONArray staiistFunc=paramJson.getJSONArray("statisticFunctions");
//           staiistFunc.add("CITY_CODE");
//           staiistFunc.add("PER_POP");
//           staiistFunc.add("GDP");
//           staiistFunc.add("CITY_NAME");
//           staiistFunc.add("GEOMETRY_AREA");
//           staiistFunc.add("SUMLENGTH");
//           staiistFunc.add("POP");
//           staiistFunc.add("FUGAILV");
//           
//           paramJson.put("statisticFunctions", staiistFunc);
//           
//           json.put("jsFile", generateWhereAndResultJSFile(paramJson, json));
//        }finally{
//        	DBHELPER.closeStatement(stmt);
//        }
//        return json.toString();
//    }
//    private JSONArray mergeJSONArray(JSONArray a,JSONArray b) throws Exception{
//    	for(int i=0;i<b.size();i++){
//    		JSONObject object=b.getJSONObject(i);
//    		a.add(object);
//    	}
//    	return a;
//    }
//    
//    /**
//     * 根据城市编号获取道路覆盖率
//     * @param groupFields
//     * @param statisticFunctions
//     * @param layerId
//     * @param cityCodes
//     * @param stmt
//     * @param con
//     * @return
//     * @throws Exception
//     */
//    private JSONArray getSQL4RoadCoveragre(List<String> groupFields,List<String> statisticFunctions,long layerId,List<String> cityCodes,Connection con,int djscode,int qxcode) throws Exception{
//    	JSONArray resultArray=new JSONArray();
//    	PreparedStatement stmt=null;
//    	ResultSet rs =null;
//    	try{
//    		String sql="select b.city_name ";
//            if(qxcode!=0){
//            	sql=sql+"xzname";
//    		}else if(djscode!=0){
//    			sql=sql+"qxname";
//    		}else{
//    			sql=sql+"djsname";
//    		}
//    		sql=sql+",b.city_code,";
////			String sql="select b.city_name,b.city_code,";
//			for(String fieldName:groupFields){
//				sql+="a."+fieldName+",";
//>>>>>>> .r170
			}
			json.put("records", resultArray);
			json.put("r", true);

			JSONArray staiistFunc = paramJson
					.getJSONArray("statisticFunctions");
			staiistFunc.add("CITY_CODE");
			staiistFunc.add("PER_POP");
			staiistFunc.add("GDP");
			staiistFunc.add("CITY_NAME");
			staiistFunc.add("GEOMETRY_AREA");
			staiistFunc.add("SUMLENGTH");
			staiistFunc.add("POP");
			staiistFunc.add("FUGAILV");

			paramJson.put("statisticFunctions", staiistFunc);

			json.put("jsFile", generateWhereAndResultJSFile(paramJson, json));
		} finally {
			DBHELPER.closeStatement(stmt);
		}
		return json.toString();
	}

	private JSONArray mergeJSONArray(JSONArray a, JSONArray b) throws Exception {
		for (int i = 0; i < b.size(); i++) {
			JSONObject object = b.getJSONObject(i);
			a.add(object);
		}
		return a;
	}

	/**
	 * 根据城市编号获取道路覆盖率
	 * 
	 * @param groupFields
	 * @param statisticFunctions
	 * @param layerId
	 * @param cityCodes
	 * @param stmt
	 * @param con
	 * @return
	 * @throws Exception
	 */
	private JSONArray getSQL4RoadCoveragre(List<String> groupFields,
			List<String> statisticFunctions, long layerId,
			List<String> cityCodes, Connection con, int djscode, int qxcode)
			throws Exception {
		JSONArray resultArray = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select b.city_name ";
			if (qxcode != 0) {
				sql = sql + "xzname";
			} else if (djscode != 0) {
				sql = sql + "qxname";
			} else {
				sql = sql + "djsname";
			}
			sql = sql + ",b.city_code,";
			for (String fieldName : groupFields) {
				sql += "a." + fieldName + ",";
			}
			for (String funName : statisticFunctions) {
				if ("count".equals(funName))
					sql += "count(1) as count,";
				else if ("sum".equals(funName))
					sql += "fun_round_tow_decimal(sum(b.geometry_length)) as sum_geo_length,fun_round_tow_decimal(sum(b.geometry_area)) as sum_geo_area,";
				else if ("max".equals(funName))
					sql += "fun_round_tow_decimal(max(b.geometry_length)) as max_geo_length,fun_round_tow_decimal(max(b.geometry_area)) as max_geo_area,";
				else if ("min".equals(funName))
					sql += "fun_round_tow_decimal(min(b.geometry_length)) as min_geo_length,fun_round_tow_decimal(min(b.geometry_area)) as min_geo_area,";
				else
					sql += StringUtils.replaceEach(funName, new String[] {
							"BUF_GEOMETRY_LENGTH", "BUF_GEOMETRY_AREA",
							"LAYER_GEOMETRY_LENGTH", "LAYER_GEOMETRY_AREA" },
							new String[] { "c.geometry_length",
									"c.geometry_area", "b.geometry_length",
									"b.geometry_area" })
							+ ",";
			}
			sql += "fun_round_tow_decimal(sum(b.geometry_length)/1000)/2 as sumlength,";
			sql = sql.substring(0, sql.length() - 1);
			sql+=" from spatial_fs_" + layerId + " a,spatial_stat_" + layerId + " b,sys_district_ex c where a.q_uid=b.q_uid and c.code=b.city_code";
			if (!cityCodes.isEmpty()) {
//<<<<<<< .mine
				sql += " and b.city_code in (";
				for (int i = 0; i < cityCodes.size(); i++) {
					if (i == (cityCodes.size() - 1)) {
						sql += (cityCodes.get(i));
					} else if ((i % 999) == 0 && i > 0) {
						sql += cityCodes.get(i) + ") or b.city_code in ("; // 解决ORA-01795问题
					} else {
						sql += cityCodes.get(i) + (",");
					}
				}
				sql += ")";
			}
			sql += " group by b.city_name,b.city_code,";
			for (String fieldName : groupFields)
				sql += "a." + fieldName + ",";
			sql = sql.substring(0, sql.length() - 1);
			sql += " order by b.city_code";
			String sqldjs = "select";
			if (djscode != 0) {
				sqldjs += " (select name from sys_district_ex where code="
						+ djscode + ") djsname, ";
			}
			if (qxcode != 0) {
				sqldjs += " (select name from sys_district_ex where code="
						+ qxcode + ") qxname, ";
			}
			if (qxcode != 0) {
				sqldjs += " case when d.xzname is null then e.name else d.xzname end xzname,";
			} else if (djscode != 0) {
				sqldjs += " case when d.qxname is null then e.name else d.qxname end qxname,";
			} else {
				sqldjs = sqldjs + " djsname,";
			}
			sql = sqldjs
					+ " case when d.city_code is null then e.code else d.city_code end city_code,"
					+ " case when d.count is null then 0 else d.count end count,"
					+ " case when d.sumlength is null then 0 else d.sumlength end sumlength,"
					+ "fun_round_tow_decimal(e.geometry_area / 1000000) geometry_area,"
					+ "fun_round_tow_decimal((case when d.sumlength is null then 0 else d.sumlength end) / (geometry_area / 1000000) * 100) || '%' fugailv,"
					+ " round(e.pop/1000) pop,fun_round_tow_decimal((case when d.sumlength is null then 0 else d.sumlength end)*1000/round(e.pop/1000)) per_pop,"
					+ " e.geometry geometry,e.gdp from (" + sql
					+ ")d,sys_district_ex e where e.code=d.city_code(+) and"
					+ " e.code in (";
			
//=======
//		        sql += " and b.city_code in (";
//		        for(int i=0;i<cityCodes.size();i++){
//		        	 if (i == (cityCodes.size() - 1)) {  
//		        		 sql+=(cityCodes.get(i));  
//		             }else if((i%999)==0 && i>0){  
//		            	 sql+=cityCodes.get(i)+") or b.city_code in ("; //解决ORA-01795问题  
//		             }else{  
//		            	 sql+=cityCodes.get(i)+(",");  
//		             }  
//		        }
//		        sql += ")";
//		    }
//		   sql += " group by b.city_name,b.city_code,";
//		   for (String fieldName : groupFields)
//		      sql += "a." + fieldName + ",";
//		   sql = sql.substring(0, sql.length() - 1);
//		   sql += " order by b.city_code";
//		   String sqldjs="select";
//		   if(djscode!=0){
//			   sqldjs+=" (select name from sys_district_ex where code="+djscode+") djsname, ";
//		   }
//		   if(qxcode!=0){
//			   sqldjs+=" (select name from sys_district_ex where code="+qxcode+") qxname, ";
//		   }
//		   sql=sqldjs+" d.*,"+ //"select d.*,"+
//			   "fun_round_tow_decimal(e.geometry_area / 1000000) geometry_area,"+
//			   "fun_round_tow_decimal((sumlength) / (geometry_area / 1000000) * 100) || '%' fugailv,"+
//			   " round(e.pop/1000) pop,fun_round_tow_decimal(sumlength*1000/round(e.pop/1000)) per_pop,"+
//			   " e.geometry geometry,e.gdp from ("+sql+")d,sys_district_ex e where e.code=d.city_code order by e.sortorder";
//		   stmt=con.prepareStatement(sql);
//		   rs = stmt.executeQuery();
//		   resultArray=DBHELPER.resultSet2JSONArray(rs);
//       }finally{
//    	   DBHELPER.closeResultSet(rs);
//    	   DBHELPER.closeStatement(stmt);
//       }
//       return resultArray;
//    }
////    /**
////     * 获取地区信息
////     * 
////     * @param paramJson
////     * @return
////     * @throws Exception
////     */
////    private JSONArray getDistictArea(Connection con,JSONObject directbject,int p_id) throws Exception {
////        String sql="select id, p_id, name, code, sortorder, geometry, geometry_area, geometry_length, pop, gdp from sys_district where p_id="+p_id+" order by sortorder";
////    	PreparedStatement stmt=con.prepareStatement(sql);
////    	ResultSet rs = stmt.executeQuery();
////    	JSONArray array=new JSONArray();
////    	try{
////    		array=DBHELPER.resultSet2JSONArray(rs);
////    		if(array!=null&&array.size()>0){
////	    		directbject.put("children", array);
////	    		for(int i=0;i<array.size();i++){
////	    			JSONObject object=array.getJSONObject(i);
////	    			getDistictArea(con,object,object.getInt("ID"));
////	    		}
////    		}
////    	}finally{
////    		DBHELPER.closeStatement(stmt);
////    	}
////    	return array; 
////    }
//>>>>>>> .r170

//<<<<<<< .mine
			for (int i = 0; i < cityCodes.size(); i++) {
				if (i == (cityCodes.size() - 1)) {
					sql += (cityCodes.get(i));
				} else if ((i % 999) == 0 && i > 0) {
					sql += cityCodes.get(i) + ") or e.code in ("; // 解决ORA-01795问题
				} else {
					sql += cityCodes.get(i) + (",");
				}
			}
			sql += ") order by e.sortorder";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			resultArray = DBHELPER.resultSet2JSONArray(rs);
		} finally {
			DBHELPER.closeResultSet(rs);
			DBHELPER.closeStatement(stmt);
		}
		return resultArray;
	}
//=======
////<<<<<<< .mine
//	/**
//	 * 道路覆盖率分析
//	 * 
//	 * @param paramJson
//	 * @return
//	 * @throws Exception
//	 */
////	private String spatial4RoadCoveragre(Connection con, JSONObject paramJson)
////			throws Exception {
////		JSONObject json = new JSONObject();
////		json.put("r", false);
////		long layerId = paramJson.getLong("layerId");
////		List<String> groupFields = JSONUtils.array2StringList(paramJson
////				.getJSONArray("groupFields"));
////		List<String> cityCodes = JSONUtils.array2StringList(paramJson
////				.getJSONArray("cityCodes"));
////		List<String> qx = JSONUtils.array2StringList(paramJson
////				.getJSONArray("qx"));
////		List<String> xz = JSONUtils.array2StringList(paramJson
////				.getJSONArray("xz"));
////		List<String> statisticFunctions = JSONUtils.array2StringList(paramJson
////				.getJSONArray("statisticFunctions"));
////		PreparedStatement stmt = null;
////		PreparedStatement stmtxz = null;
//////=======
//////    
//////    /**
//////     * 数据分组分析
//////     * 
//////     * @param paramJson
//////     * @return
//////     * @throws Exception
//////     */
//////    private String spatial4GroupAnaly(Connection con, JSONObject paramJson) throws Exception {
//////        return spatial4CommonStatisticAnaly(con, paramJson);
//////    }
//////>>>>>>> .r149
////
////		try {
////			JSONArray resultArray = new JSONArray();
////			for (int j = 0; j < cityCodes.size(); j++) {
////				List<String> cityCode = new ArrayList<String>();
////				cityCode.add(cityCodes.get(j));
////				resultArray = mergeJSONArray(
////						resultArray,
////						getSQL4RoadCoveragre(groupFields, statisticFunctions,
////								layerId, cityCode, con, 0, 0));
////
////				if (qx.size() != 0 && qx.get(0) == "true") {
////					String sql4qx = "select * from sys_district_ex t where t.p_id=?";
////					List<String> tempqxList = new ArrayList<String>();
////					List<String> tempqxList1 = new ArrayList<String>();
////					int p_id = Integer.parseInt(cityCodes.get(j));
////					stmt = con.prepareStatement(sql4qx);
////					stmt.setInt(1, p_id);
////					ResultSet rs_qx = stmt.executeQuery();
////					while (rs_qx.next()) {
////						String xz_p_id = String.valueOf(rs_qx.getInt("id"));
////						tempqxList.add(xz_p_id);
////						tempqxList1.add(xz_p_id);
////						if (tempqxList.size() != 0) {
////							JSONArray resultArrayQX = getSQL4RoadCoveragre(
////									groupFields, statisticFunctions, layerId,
////									tempqxList, con, p_id, 0);
////							resultArray = mergeJSONArray(resultArray,
////									resultArrayQX);
////						}
////						List<String> tempxzList = new ArrayList<String>();
////						if (xz.size() != 0 && xz.get(0) == "true") {
////							stmtxz = con.prepareStatement(sql4qx);
////							stmtxz.setInt(1, Integer.parseInt(xz_p_id));
////							ResultSet rs_xz = stmtxz.executeQuery();
////							while (rs_xz.next()) {
////								tempxzList.add(String.valueOf(rs_xz
////										.getInt("id")));
////							}
////							DBHELPER.closeStatement(stmtxz);
////							if (tempxzList.size() != 0) {
////								JSONArray resultArrayXZ = getSQL4RoadCoveragre(
////										groupFields, statisticFunctions,
////										layerId, tempxzList, con, p_id,
////										Integer.parseInt(xz_p_id));
////								resultArray = mergeJSONArray(resultArray,
////										resultArrayXZ);
////							}
////						}
////						tempqxList.clear();
////					}
////				}
////			}
////			json.put("records", resultArray);
////			json.put("r", true);
////
////			JSONArray staiistFunc = paramJson
////					.getJSONArray("statisticFunctions");
////			staiistFunc.add("CITY_CODE");
////			staiistFunc.add("PER_POP");
////			staiistFunc.add("GDP");
////			staiistFunc.add("CITY_NAME");
////			staiistFunc.add("GEOMETRY_AREA");
////			staiistFunc.add("SUMLENGTH");
////			staiistFunc.add("POP");
////			staiistFunc.add("FUGAILV");
////
////			paramJson.put("statisticFunctions", staiistFunc);
////
////			json.put("jsFile", generateWhereAndResultJSFile(paramJson, json));
////		} finally {
////			DBHELPER.closeStatement(stmt);
////		}
////		return json.toString();
////	}
//>>>>>>> .r170

//<<<<<<< .mine
	/**
	 * 根据城市编号获取道路覆盖率
	 * 
	 * @param groupFields
	 * @param statisticFunctions
	 * @param layerId
	 * @param cityCodes
	 * @param stmt
	 * @param con
	 * @return
	 * @throws Exception
	 */
	/*
	private JSONArray getSQL4RoadCoveragreOld(List<String> groupFields,
			List<String> statisticFunctions, long layerId,
			List<String> cityCodes, Connection con, int djscode, int qxcode)
			throws Exception {
		JSONArray resultArray = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select b.city_name ";
			if (qxcode != 0) {
				sql = sql + "xzname";
			} else if (djscode != 0) {
				sql = sql + "qxname";
			} else {
				sql = sql + "djsname";
			}
			sql = sql + ",b.city_code,";
			for (String fieldName : groupFields) {
				sql += "a." + fieldName + ",";
			}
			for (String funName : statisticFunctions) {
				if ("count".equals(funName))
					sql += "count(1) as count,";
				else if ("sum".equals(funName))
					sql += "fun_round_tow_decimal(sum(b.geometry_length)) as sum_geo_length,fun_round_tow_decimal(sum(b.geometry_area)) as sum_geo_area,";
				else if ("max".equals(funName))
					sql += "fun_round_tow_decimal(max(b.geometry_length)) as max_geo_length,fun_round_tow_decimal(max(b.geometry_area)) as max_geo_area,";
				else if ("min".equals(funName))
					sql += "fun_round_tow_decimal(min(b.geometry_length)) as min_geo_length,fun_round_tow_decimal(min(b.geometry_area)) as min_geo_area,";
				else
					sql += StringUtils.replaceEach(funName, new String[] {
							"BUF_GEOMETRY_LENGTH", "BUF_GEOMETRY_AREA",
							"LAYER_GEOMETRY_LENGTH", "LAYER_GEOMETRY_AREA" },
							new String[] { "c.geometry_length",
									"c.geometry_area", "b.geometry_length",
									"b.geometry_area" })
							+ ",";
			}
			sql += "fun_round_tow_decimal(sum(b.geometry_length)/1000)/2 as sumlength,";
			sql = sql.substring(0, sql.length() - 1);
			sql+=" from spatial_fs_" + layerId + " a,spatial_stat_" + layerId + " b,sys_district_ex c where a.q_uid=b.q_uid and c.code=b.city_code";
//			sql += " from spatial_stat_" + layerId + " b where ";
			if (!cityCodes.isEmpty()) {
				sql += " and b.city_code in (";
				for (int i = 0; i < cityCodes.size(); i++) {
					if (i == (cityCodes.size() - 1)) {
						sql += (cityCodes.get(i));
					} else if ((i % 999) == 0 && i > 0) {
						sql += cityCodes.get(i) + ") or b.city_code in ("; // 解决ORA-01795问题
					} else {
						sql += cityCodes.get(i) + (",");
					}
				}
				sql += ")";
			}
			sql += " group by b.city_name,b.city_code,";
			for (String fieldName : groupFields)
				sql += "a." + fieldName + ",";
			sql = sql.substring(0, sql.length() - 1);
			sql += " order by b.city_code";
			String sqldjs = "select";
			if (djscode != 0) {
				sqldjs += " (select name from sys_district_ex where code="
						+ djscode + ") djsname, ";
			}
			if (qxcode != 0) {
				sqldjs += " (select name from sys_district_ex where code="
						+ qxcode + ") qxname, ";
			}
			sql = sqldjs
					+ " d.*,"
					+ "fun_round_tow_decimal(e.geometry_area / 1000000) geometry_area,"
					+ "fun_round_tow_decimal((sumlength) / (geometry_area / 1000000) * 100) || '%' fugailv,"
					+ " round(e.pop/1000) pop,fun_round_tow_decimal(sumlength*1000/round(e.pop/1000)) per_pop,"
					+ " e.geometry geometry,e.gdp from ("
					+ sql
					+ ")d,sys_district_ex e where e.code=d.city_code order by e.sortorder";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			resultArray = DBHELPER.resultSet2JSONArray(rs);
		} finally {
			DBHELPER.closeResultSet(rs);
			DBHELPER.closeStatement(stmt);
		}
		return resultArray;
	}*/
//=======
////	private JSONArray mergeJSONArray(JSONArray a, JSONArray b) throws Exception {
////		for (int i = 0; i < b.size(); i++) {
////			JSONObject object = b.getJSONObject(i);
////			a.add(object);
////		}
////		return a;
////	}
//>>>>>>> .r170

//<<<<<<< .mine
	
	/**
	 * 获取地区信息
	 * 
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private JSONArray getDistictArea(Connection con, JSONObject directbject,
			int p_id) throws Exception {
		String sql = "select id, p_id, name, code, sortorder, geometry, geometry_area, geometry_length, pop, gdp from sys_district where p_id="
				+ p_id + " order by sortorder";
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		JSONArray array = new JSONArray();
		try {
			array = DBHELPER.resultSet2JSONArray(rs);
			if (array != null && array.size() > 0) {
				directbject.put("children", array);
				for (int i = 0; i < array.size(); i++) {
					JSONObject object = array.getJSONObject(i);
					getDistictArea(con, object, object.getInt("ID"));
				}
			}
		} finally {
			DBHELPER.closeStatement(stmt);
		}
		return array;
	}
//=======
//	/**
//	 * 根据城市编号获取道路覆盖率
//	 * 
//	 * @param groupFields
//	 * @param statisticFunctions
//	 * @param layerId
//	 * @param cityCodes
//	 * @param stmt
//	 * @param con
//	 * @return
//	 * @throws Exception
//	 */
//	private JSONArray getSQL4RoadCoveragre(List<String> groupFields,
//			List<String> statisticFunctions, long layerId,
//			List<String> cityCodes, Connection con, int djscode, int qxcode)
//			throws Exception {
//		JSONArray resultArray = new JSONArray();
//		PreparedStatement stmt = null;
//		ResultSet rs = null;
//		try {
//			String sql = "select b.city_name ";
//			if (qxcode != 0) {
//				sql = sql + "xzname";
//			} else if (djscode != 0) {
//				sql = sql + "qxname";
//			} else {
//				sql = sql + "djsname";
//			}
//			sql = sql + ",b.city_code,";
//			for (String fieldName : groupFields) {
//				sql += "a." + fieldName + ",";
//			}
//			for (String funName : statisticFunctions) {
//				if ("count".equals(funName))
//					sql += "count(1) as count,";
//				else if ("sum".equals(funName))
//					sql += "fun_round_tow_decimal(sum(b.geometry_length)) as sum_geo_length,fun_round_tow_decimal(sum(b.geometry_area)) as sum_geo_area,";
//				else if ("max".equals(funName))
//					sql += "fun_round_tow_decimal(max(b.geometry_length)) as max_geo_length,fun_round_tow_decimal(max(b.geometry_area)) as max_geo_area,";
//				else if ("min".equals(funName))
//					sql += "fun_round_tow_decimal(min(b.geometry_length)) as min_geo_length,fun_round_tow_decimal(min(b.geometry_area)) as min_geo_area,";
//				else
//					sql += StringUtils.replaceEach(funName, new String[] {
//							"BUF_GEOMETRY_LENGTH", "BUF_GEOMETRY_AREA",
//							"LAYER_GEOMETRY_LENGTH", "LAYER_GEOMETRY_AREA" },
//							new String[] { "c.geometry_length",
//									"c.geometry_area", "b.geometry_length",
//									"b.geometry_area" })
//							+ ",";
//			}
//			sql += "fun_round_tow_decimal(sum(b.geometry_length)/1000)/2 as sumlength,";
//			sql = sql.substring(0, sql.length() - 1);
//			sql+=" from spatial_fs_" + layerId + " a,spatial_stat_" + layerId + " b,sys_district_ex c where a.q_uid=b.q_uid and c.code=b.city_code";
//			if (!cityCodes.isEmpty()) {
//				sql += " and b.city_code in (";
//				for (int i = 0; i < cityCodes.size(); i++) {
//					if (i == (cityCodes.size() - 1)) {
//						sql += (cityCodes.get(i));
//					} else if ((i % 999) == 0 && i > 0) {
//						sql += cityCodes.get(i) + ") or b.city_code in ("; // 解决ORA-01795问题
//					} else {
//						sql += cityCodes.get(i) + (",");
//					}
//				}
//				sql += ")";
//			}
//			sql += " group by b.city_name,b.city_code,";
//			for (String fieldName : groupFields)
//				sql += "a." + fieldName + ",";
//			sql = sql.substring(0, sql.length() - 1);
//			sql += " order by b.city_code";
//			String sqldjs = "select";
//			if (djscode != 0) {
//				sqldjs += " (select name from sys_district_ex where code="
//						+ djscode + ") djsname, ";
//			}
//			if (qxcode != 0) {
//				sqldjs += " (select name from sys_district_ex where code="
//						+ qxcode + ") qxname, ";
//			}
//			if (qxcode != 0) {
//				sqldjs += " case when d.xzname is null then e.name else d.xzname end xzname,";
//			} else if (djscode != 0) {
//				sqldjs += " case when d.qxname is null then e.name else d.qxname end qxname,";
//			} else {
//				sqldjs = sqldjs + " djsname,";
//			}
//			sql = sqldjs
//					+ " case when d.city_code is null then e.code else d.city_code end city_code,"
//					+ " case when d.count is null then 0 else d.count end count,"
//					+ " case when d.sumlength is null then 0 else d.sumlength end sumlength,"
//					+ "fun_round_tow_decimal(e.geometry_area / 1000000) geometry_area,"
//					+ "fun_round_tow_decimal((case when d.sumlength is null then 0 else d.sumlength end) / (geometry_area / 1000000) * 100) || '%' fugailv,"
//					+ " round(e.pop/1000) pop,fun_round_tow_decimal((case when d.sumlength is null then 0 else d.sumlength end)*1000/round(e.pop/1000)) per_pop,"
//					+ " e.geometry geometry,e.gdp from (" + sql
//					+ ")d,sys_district_ex e where e.code=d.city_code(+) and"
//					+ " e.code in (";
//			
//
//			for (int i = 0; i < cityCodes.size(); i++) {
//				if (i == (cityCodes.size() - 1)) {
//					sql += (cityCodes.get(i));
//				} else if ((i % 999) == 0 && i > 0) {
//					sql += cityCodes.get(i) + ") or e.code in ("; // 解决ORA-01795问题
//				} else {
//					sql += cityCodes.get(i) + (",");
//				}
//			}
//			sql += ") order by e.sortorder";
//			stmt = con.prepareStatement(sql);
//			rs = stmt.executeQuery();
//			resultArray = DBHELPER.resultSet2JSONArray(rs);
//		} finally {
//			DBHELPER.closeResultSet(rs);
//			DBHELPER.closeStatement(stmt);
//		}
//		return resultArray;
//	}
//>>>>>>> .r170

//<<<<<<< .mine
	/**
	 * 数据分组分析
	 * 
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private String spatial4GroupAnaly(Connection con, JSONObject paramJson)
			throws Exception {
		return spatial4CommonStatisticAnaly(con, paramJson);
	}
//=======
	/**
	 * 根据城市编号获取道路覆盖率
	 * 
	 * @param groupFields
	 * @param statisticFunctions
	 * @param layerId
	 * @param cityCodes
	 * @param stmt
	 * @param con
	 * @return
	 * @throws Exception
	 */
	/*
	private JSONArray getSQL4RoadCoveragreOld(List<String> groupFields,
			List<String> statisticFunctions, long layerId,
			List<String> cityCodes, Connection con, int djscode, int qxcode)
			throws Exception {
		JSONArray resultArray = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select b.city_name ";
			if (qxcode != 0) {
				sql = sql + "xzname";
			} else if (djscode != 0) {
				sql = sql + "qxname";
			} else {
				sql = sql + "djsname";
			}
			sql = sql + ",b.city_code,";
			for (String fieldName : groupFields) {
				sql += "a." + fieldName + ",";
			}
			for (String funName : statisticFunctions) {
				if ("count".equals(funName))
					sql += "count(1) as count,";
				else if ("sum".equals(funName))
					sql += "fun_round_tow_decimal(sum(b.geometry_length)) as sum_geo_length,fun_round_tow_decimal(sum(b.geometry_area)) as sum_geo_area,";
				else if ("max".equals(funName))
					sql += "fun_round_tow_decimal(max(b.geometry_length)) as max_geo_length,fun_round_tow_decimal(max(b.geometry_area)) as max_geo_area,";
				else if ("min".equals(funName))
					sql += "fun_round_tow_decimal(min(b.geometry_length)) as min_geo_length,fun_round_tow_decimal(min(b.geometry_area)) as min_geo_area,";
				else
					sql += StringUtils.replaceEach(funName, new String[] {
							"BUF_GEOMETRY_LENGTH", "BUF_GEOMETRY_AREA",
							"LAYER_GEOMETRY_LENGTH", "LAYER_GEOMETRY_AREA" },
							new String[] { "c.geometry_length",
									"c.geometry_area", "b.geometry_length",
									"b.geometry_area" })
							+ ",";
			}
			sql += "fun_round_tow_decimal(sum(b.geometry_length)/1000)/2 as sumlength,";
			sql = sql.substring(0, sql.length() - 1);
			sql+=" from spatial_fs_" + layerId + " a,spatial_stat_" + layerId + " b,sys_district_ex c where a.q_uid=b.q_uid and c.code=b.city_code";
//			sql += " from spatial_stat_" + layerId + " b where ";
			if (!cityCodes.isEmpty()) {
				sql += " and b.city_code in (";
				for (int i = 0; i < cityCodes.size(); i++) {
					if (i == (cityCodes.size() - 1)) {
						sql += (cityCodes.get(i));
					} else if ((i % 999) == 0 && i > 0) {
						sql += cityCodes.get(i) + ") or b.city_code in ("; // 解决ORA-01795问题
					} else {
						sql += cityCodes.get(i) + (",");
					}
				}
				sql += ")";
			}
			sql += " group by b.city_name,b.city_code,";
			for (String fieldName : groupFields)
				sql += "a." + fieldName + ",";
			sql = sql.substring(0, sql.length() - 1);
			sql += " order by b.city_code";
			String sqldjs = "select";
			if (djscode != 0) {
				sqldjs += " (select name from sys_district_ex where code="
						+ djscode + ") djsname, ";
			}
			if (qxcode != 0) {
				sqldjs += " (select name from sys_district_ex where code="
						+ qxcode + ") qxname, ";
			}
			sql = sqldjs
					+ " d.*,"
					+ "fun_round_tow_decimal(e.geometry_area / 1000000) geometry_area,"
					+ "fun_round_tow_decimal((sumlength) / (geometry_area / 1000000) * 100) || '%' fugailv,"
					+ " round(e.pop/1000) pop,fun_round_tow_decimal(sumlength*1000/round(e.pop/1000)) per_pop,"
					+ " e.geometry geometry,e.gdp from ("
					+ sql
					+ ")d,sys_district_ex e where e.code=d.city_code order by e.sortorder";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			resultArray = DBHELPER.resultSet2JSONArray(rs);
		} finally {
			DBHELPER.closeResultSet(rs);
			DBHELPER.closeStatement(stmt);
		}
		return resultArray;
	}*/
//>>>>>>> .r170

//<<<<<<< .mine
	/**
	 * 路径分析
	 * 
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
	private String spatial4PathAnaly(Connection con, JSONObject paramJson)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
//=======
	
	/**
	 * 获取地区信息
	 * 
	 * @param paramJson
	 * @return
	 * @throws Exception
	 */
//	private JSONArray getDistictArea(Connection con, JSONObject directbject,int p_id) throws Exception {
//		String sql = "select id, p_id, name, code, sortorder, geometry, geometry_area, geometry_length, pop, gdp from sys_district where p_id="
//				+ p_id + " order by sortorder";
//		PreparedStatement stmt = con.prepareStatement(sql);
//		ResultSet rs = stmt.executeQuery();
//		JSONArray array = new JSONArray();
//		try {
//			array = DBHELPER.resultSet2JSONArray(rs);
//			if (array != null && array.size() > 0) {
//				directbject.put("children", array);
//				for (int i = 0; i < array.size(); i++) {
//					JSONObject object = array.getJSONObject(i);
//					getDistictArea(con, object, object.getInt("ID"));
//				}
//			}
//		} finally {
//			DBHELPER.closeStatement(stmt);
//		}
//		return array;
//	}
//>>>>>>> .r170

//<<<<<<< .mine
	/**
	 * 普通汇总分析
	 * 
	 * @param con
	 * @param paramJson
	 * @param isGeometryFilter
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	private String spatial4CommonStatisticAnaly(Connection con,
			JSONObject paramJson) throws SQLException, Exception {
		JSONObject json = new JSONObject();
		json.put("r", false);
		long layerId = paramJson.getLong("layerId");
		PreparedStatement stmt = null;
		List<String> groupFields = JSONUtils.array2StringList(paramJson
				.getJSONArray("groupFields"));
		List<String> statisticFunctions = JSONUtils.array2StringList(paramJson
				.getJSONArray("statisticFunctions"));
		String filterStr = paramJson.getString("filterStr");
		String filterSQL = generateFilterSQLCondition(filterStr);
		JGeometry filterGeometry = null;
		double filterGeometryLength = 1;
		double filterGeometryArea = 1;
		boolean isGeometryFilter = paramJson.containsKey("geometry")
				&& paramJson.containsKey("geometryType");
		try {
			if (isGeometryFilter) {
				filterGeometry = OracleSpatialUtils
						.arcGISGeometryJson2JGeometry(
								paramJson.getJSONObject("geometry"),
								paramJson.getString("geometryType"), 0,
								spatialConfig.wkid);
				double[] r = OracleSpatialUtils.lengthAndArea(con,
						filterGeometry);
				filterGeometryLength = r[0];
				filterGeometryArea = r[1];
			} else {
				getDistrictTotalLengthAndArea(con);
				filterGeometryLength = districtSumGeometryInfo[0];
				filterGeometryArea = districtSumGeometryInfo[1];
			}
//=======
//	/**
//	 * 数据分组分析
//	 * 
//	 * @param paramJson
//	 * @return
//	 * @throws Exception
//	 */
//	private String spatial4GroupAnaly(Connection con, JSONObject paramJson)
//			throws Exception {
//		return spatial4CommonStatisticAnaly(con, paramJson);
//	}
//>>>>>>> .r170

//<<<<<<< .mine
			// select from
			String sql = "select ";
			for (String fieldName : groupFields)
				sql += "a." + fieldName + ",";
			for (String funName : statisticFunctions) {
				if ("count".equals(funName))
					sql += "count(1) as count,";
				else if ("sum".equals(funName))
					sql += "fun_round_tow_decimal(sum(a.geometry_length)) as sum_geo_length,fun_round_tow_decimal(sum(a.geometry_area)) as sum_geo_area,";
				else if ("max".equals(funName))
					sql += "fun_round_tow_decimal(max(a.geometry_length)) as max_geo_length,fun_round_tow_decimal(max(a.geometry_area)) as max_geo_area,";
				else if ("min".equals(funName))
					sql += "fun_round_tow_decimal(min(a.geometry_length)) as min_geo_length,fun_round_tow_decimal(min(a.geometry_area)) as min_geo_area,";
				else
					sql += StringUtils.replaceEach(
							funName,
							new String[] { "BUF_GEOMETRY_LENGTH",
									"BUF_GEOMETRY_AREA",
									"LAYER_GEOMETRY_LENGTH",
									"LAYER_GEOMETRY_AREA" },
							new String[] {
									Double.toString(filterGeometryLength),
									Double.toString(filterGeometryArea),
									"a.geometry_length", "a.geometry_area" })
							+ ",";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += " from spatial_fs_" + layerId + " a ";
//=======
//	/**
//	 * 路径分析
//	 * 
//	 * @param paramJson
//	 * @return
//	 * @throws Exception
//	 */
//	private String spatial4PathAnaly(Connection con, JSONObject paramJson)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//>>>>>>> .r170

//<<<<<<< .mine
			// where 条件
			if (isGeometryFilter && !StringUtils.isEmpty(filterSQL))
				sql += " where " + filterSQL
						+ " and SDO_ANYINTERACT(a.geometry,?)='TRUE'";
			else if (!StringUtils.isEmpty(filterSQL))
				sql += " where " + filterSQL;
			else if (isGeometryFilter)
				sql += " where SDO_ANYINTERACT(a.geometry,?)='TRUE'";
//=======
//	/**
//	 * 普通汇总分析
//	 * 
//	 * @param con
//	 * @param paramJson
//	 * @param isGeometryFilter
//	 * @return
//	 * @throws SQLException
//	 * @throws Exception
//	 */
//	private String spatial4CommonStatisticAnaly(Connection con,
//			JSONObject paramJson) throws SQLException, Exception {
//		JSONObject json = new JSONObject();
//		json.put("r", false);
//		long layerId = paramJson.getLong("layerId");
//		PreparedStatement stmt = null;
//		List<String> groupFields = JSONUtils.array2StringList(paramJson
//				.getJSONArray("groupFields"));
//		List<String> statisticFunctions = JSONUtils.array2StringList(paramJson
//				.getJSONArray("statisticFunctions"));
//		String filterStr = paramJson.getString("filterStr");
//		String filterSQL = generateFilterSQLCondition(filterStr);
//		JGeometry filterGeometry = null;
//		double filterGeometryLength = 1;
//		double filterGeometryArea = 1;
//		boolean isGeometryFilter = paramJson.containsKey("geometry")
//				&& paramJson.containsKey("geometryType");
//		try {
//			if (isGeometryFilter) {
//				filterGeometry = OracleSpatialUtils
//						.arcGISGeometryJson2JGeometry(
//								paramJson.getJSONObject("geometry"),
//								paramJson.getString("geometryType"), 0,
//								spatialConfig.wkid);
//				double[] r = OracleSpatialUtils.lengthAndArea(con,
//						filterGeometry);
//				filterGeometryLength = r[0];
//				filterGeometryArea = r[1];
//			} else {
//				getDistrictTotalLengthAndArea(con);
//				filterGeometryLength = districtSumGeometryInfo[0];
//				filterGeometryArea = districtSumGeometryInfo[1];
//			}
//>>>>>>> .r170

//<<<<<<< .mine
			// 分组
			if (!groupFields.isEmpty()) {
				sql += " group by ";
				for (String fieldName : groupFields)
					sql += "a." + fieldName + ",";
				sql = sql.substring(0, sql.length() - 1);
			}
//=======
//			// select from
//			String sql = "select ";
//			for (String fieldName : groupFields)
//				sql += "a." + fieldName + ",";
//			for (String funName : statisticFunctions) {
//				if ("count".equals(funName))
//					sql += "count(1) as count,";
//				else if ("sum".equals(funName))
//					sql += "fun_round_tow_decimal(sum(a.geometry_length)) as sum_geo_length,fun_round_tow_decimal(sum(a.geometry_area)) as sum_geo_area,";
//				else if ("max".equals(funName))
//					sql += "fun_round_tow_decimal(max(a.geometry_length)) as max_geo_length,fun_round_tow_decimal(max(a.geometry_area)) as max_geo_area,";
//				else if ("min".equals(funName))
//					sql += "fun_round_tow_decimal(min(a.geometry_length)) as min_geo_length,fun_round_tow_decimal(min(a.geometry_area)) as min_geo_area,";
//				else
//					sql += StringUtils.replaceEach(
//							funName,
//							new String[] { "BUF_GEOMETRY_LENGTH",
//									"BUF_GEOMETRY_AREA",
//									"LAYER_GEOMETRY_LENGTH",
//									"LAYER_GEOMETRY_AREA" },
//							new String[] {
//									Double.toString(filterGeometryLength),
//									Double.toString(filterGeometryArea),
//									"a.geometry_length", "a.geometry_area" })
//							+ ",";
//			}
//			sql = sql.substring(0, sql.length() - 1);
//			sql += " from spatial_fs_" + layerId + " a ";
//>>>>>>> .r170

//<<<<<<< .mine
			if (!groupFields.isEmpty()) {
				sql += " order by ";
				for (int i = 0; i < groupFields.size(); i++)
					sql += (i + 1) + ",";
				sql = sql.substring(0, sql.length() - 1);
			}
//=======
//			// where 条件
//			if (isGeometryFilter && !StringUtils.isEmpty(filterSQL))
//				sql += " where " + filterSQL
//						+ " and SDO_ANYINTERACT(a.geometry,?)='TRUE'";
//			else if (!StringUtils.isEmpty(filterSQL))
//				sql += " where " + filterSQL;
//			else if (isGeometryFilter)
//				sql += " where SDO_ANYINTERACT(a.geometry,?)='TRUE'";
//>>>>>>> .r170

//<<<<<<< .mine
			stmt = con.prepareStatement(sql);
			if (isGeometryFilter)
				stmt.setObject(1, JGeometry.store(
						DBHELPER.getNaviteConnection(con), filterGeometry));
//=======
//			// 分组
//			if (!groupFields.isEmpty()) {
//				sql += " group by ";
//				for (String fieldName : groupFields)
//					sql += "a." + fieldName + ",";
//				sql = sql.substring(0, sql.length() - 1);
//			}
//>>>>>>> .r170

//<<<<<<< .mine
			// 设置参数
			ResultSet rs = stmt.executeQuery();
//=======
//			if (!groupFields.isEmpty()) {
//				sql += " order by ";
//				for (int i = 0; i < groupFields.size(); i++)
//					sql += (i + 1) + ",";
//				sql = sql.substring(0, sql.length() - 1);
//			}
//>>>>>>> .r170

//<<<<<<< .mine
			json.put("records", DBHELPER.resultSet2JSONArray(rs));
//=======
//			stmt = con.prepareStatement(sql);
//			if (isGeometryFilter)
//				stmt.setObject(1, JGeometry.store(
//						DBHELPER.getNaviteConnection(con), filterGeometry));
//>>>>>>> .r170

//<<<<<<< .mine
			if (!groupFields.isEmpty()) {
				JSONObject fieldName2SchemaName = new JSONObject();
				for (String str : groupFields)
					fieldName2SchemaName.put(str, str);
				json.put("fieldNameDictionary", fieldName2SchemaName);
			}
			json.put("r", true);
			json.put("jsFile", generateWhereAndResultJSFile(paramJson, json));
		} finally {
			DBHELPER.closeStatement(stmt);
		}
		return json.toString();
	}
//=======
//			// 设置参数
//			ResultSet rs = stmt.executeQuery();
//>>>>>>> .r170

//<<<<<<< .mine
	/**
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
				result = "( (a.search_caption like '%" + s + "%')";
			else
				result += " or (a.search_caption like '%" + s + "%')";
		}
		result += " )";
		return result;
	}
//=======
//			json.put("records", DBHELPER.resultSet2JSONArray(rs));
//>>>>>>> .r170

//<<<<<<< .mine
	/**
	 * 
	 * @param con
	 * @throws SQLException
	 */
	private void getDistrictTotalLengthAndArea(Connection con)
			throws SQLException {
		synchronized (districtSumGeometryInfo) {
			if (districtSumGeometryInfo[0] == 0
					&& districtSumGeometryInfo[1] == 0) {
				PreparedStatement distStmt = null;
				try {
					distStmt = con
							.prepareStatement("select sum(geometry_length),sum(geometry_area) from sys_district where geometry is not null and p_id=-1");
					ResultSet rs = distStmt.executeQuery();
					rs.next();
					districtSumGeometryInfo[0] = rs.getDouble(1);
					districtSumGeometryInfo[1] = rs.getDouble(2);
				} finally {
					DBHELPER.closeStatement(distStmt);
				}
			}
		}
	}

	private WebGISSpatialAnalyService4Oracle() {
	}

	public static final WebGISSpatialAnalyService4Oracle instance = new WebGISSpatialAnalyService4Oracle();

//=======
//			if (!groupFields.isEmpty()) {
//				JSONObject fieldName2SchemaName = new JSONObject();
//				for (String str : groupFields)
//					fieldName2SchemaName.put(str, str);
//				json.put("fieldNameDictionary", fieldName2SchemaName);
//			}
//			json.put("r", true);
//			json.put("jsFile", generateWhereAndResultJSFile(paramJson, json));
//		} finally {
//			DBHELPER.closeStatement(stmt);
//		}
//		return json.toString();
//	}
//
//	/**
//	 * 
//	 * @param filterStr
//	 * @return
//	 */
//	private String generateFilterSQLCondition(String filterStr) {
//		if (StringUtils.isEmpty(filterStr))
//			return "";
//		while (StringUtils.contains(filterStr, "  "))
//			filterStr = StringUtils.replace(filterStr, "  ", " ");
//		String[] ss = filterStr.split(" ");
//		String result = "";
//		for (String s : ss) {
//			s = StringUtils.replace(s, "'", "''");
//			if (StringUtils.isEmpty(result))
//				result = "( (a.search_caption like '%" + s + "%')";
//			else
//				result += " or (a.search_caption like '%" + s + "%')";
//		}
//		result += " )";
//		return result;
//	}
//
//	/**
//	 * 
//	 * @param con
//	 * @throws SQLException
//	 */
//	private void getDistrictTotalLengthAndArea(Connection con)
//			throws SQLException {
//		synchronized (districtSumGeometryInfo) {
//			if (districtSumGeometryInfo[0] == 0
//					&& districtSumGeometryInfo[1] == 0) {
//				PreparedStatement distStmt = null;
//				try {
//					distStmt = con
//							.prepareStatement("select sum(geometry_length),sum(geometry_area) from sys_district where geometry is not null and p_id=-1");
//					ResultSet rs = distStmt.executeQuery();
//					rs.next();
//					districtSumGeometryInfo[0] = rs.getDouble(1);
//					districtSumGeometryInfo[1] = rs.getDouble(2);
//				} finally {
//					DBHELPER.closeStatement(distStmt);
//				}
//			}
//		}
//	}
//
//	private WebGISSpatialAnalyService4Oracle() {
//	}
//
//	public static final WebGISSpatialAnalyService4Oracle instance = new WebGISSpatialAnalyService4Oracle();
//
//>>>>>>> .r170
}
