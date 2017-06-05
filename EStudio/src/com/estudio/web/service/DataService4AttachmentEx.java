package com.estudio.web.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.blazeds.services.WebGISService;
import com.estudio.context.RuntimeContext;
import com.estudio.impl.service.sercure.ClientWebService4LineRef;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.ThreadUtils;

public class DataService4AttachmentEx {
	private static final String SERVER_URL = RuntimeContext
			.getAttachmentService().getServerURL();
	private static DataService4AttachmentEx instance = new DataService4AttachmentEx();

	public static DataService4AttachmentEx getInstance() {
		return instance;
	}

	private DataService4AttachmentEx() {

	}

	private IDBHelper DBHelper = RuntimeContext.getDbHelper();

	/**
	 * 后台线程
	 */
	public void startDaemonThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					processAttachment();
					processPicture();
					ThreadUtils.sleepMinute(10);
				}
			}

			/**
			 * 处理图片
			 */
			private void processPicture() {
				Connection con = null;
				PreparedStatement selectStmt = null;
				PreparedStatement deleteStmt = null;
				try {
					con = DBHelper.getConnection();
					selectStmt = con
							.prepareStatement("select id,pic_url,PIC_THUMBNAIL_URL from SYS_EXT_PICTURES where isvalid=0");
					deleteStmt = con
							.prepareStatement("delete from SYS_EXT_PICTURES where id=?");
					ResultSet rs = selectStmt.executeQuery();
					while (rs.next()) {
						FileUtils.forceDelete(new File(RuntimeContext
								.getAttachmentService().getServerPath()
								+ rs.getString(2)));
						FileUtils.forceDelete(new File(RuntimeContext
								.getAttachmentService().getServerPath()
								+ rs.getString(3)));
						deleteStmt.setLong(1, rs.getLong(1));
						deleteStmt.execute();
					}
				} catch (Exception e) {
				} finally {
					DBHelper.closeStatement(selectStmt);
					DBHelper.closeStatement(deleteStmt);
					DBHelper.closeConnection(con);
				}
			}

			/**
			 * 处理附件
			 */
			private void processAttachment() {
				Connection con = null;
				PreparedStatement selectStmt = null;
				PreparedStatement deleteStmt = null;
				try {
					con = DBHelper.getConnection();
					selectStmt = con
							.prepareStatement("select id, url,type from sys_attachment_ex_tree where is_valid=0");
					deleteStmt = con
							.prepareStatement("delete from sys_attachment_ex_tree where id=?");
					ResultSet rs = selectStmt.executeQuery();
					while (rs.next()) {
						if (rs.getInt(3) == 0) {
							FileUtils.forceDelete(new File(RuntimeContext
									.getAttachmentService().getServerPath()
									+ rs.getString(2)));
						}
						deleteStmt.setLong(1, rs.getLong(1));
						deleteStmt.execute();
					}
				} catch (Exception e) {
				} finally {
					DBHelper.closeStatement(selectStmt);
					DBHelper.closeStatement(deleteStmt);
					DBHelper.closeConnection(con);
				}
			}
		}).start();
	}

	/**
	 * 重命名
	 * 
	 * @param id
	 * @param caption
	 * @param descript
	 * @return
	 * @throws Exception
	 */
	public JSONObject rename(long id, String caption, String descript)
			throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("update sys_attachment_ex_tree set caption=?,file_descript=? where id=?");
			stmt.setString(1, caption);
			stmt.setString(2, descript);
			stmt.setLong(3, id);
			stmt.execute();
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 新建目录
	 * 
	 * @param caption
	 * @param p_id
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject createFolder(String caption, String recordId, long p_id,
			long userId) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("insert into sys_attachment_ex_tree (id, p_id, record_id, caption, type,create_date, create_user_id, is_valid) values (?, ?, ?, ?, 1, ?, ?, 1)");
			long id = DBHelper.getUniqueID(con);
			stmt.setLong(1, id);
			stmt.setLong(2, p_id);
			stmt.setString(3, recordId);
			stmt.setString(4, caption);
			Date date = Calendar.getInstance().getTime();
			stmt.setTimestamp(5, Convert.date2SQLDateTime(date));
			stmt.setLong(6, userId);
			stmt.execute();
			json.put("r", true);
			json.put("id", id);
			json.put("userId", userId);
			json.put("createDate", Convert.datetime2Str(date));
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 创建文件
	 * 
	 * @param caption
	 * @param descript
	 * @param recordId
	 * @param p_id
	 * @param userId
	 * @param url
	 * @param phy
	 * @return
	 * @throws Exception
	 */
	public JSONObject createFile(String caption, int fileSize, String descript,
			String recordId, long p_id, long userId, String url, String phy)
			throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("insert into sys_attachment_ex_tree (id, p_id, record_id, caption, type,create_date, create_user_id, is_valid,url, file_descript, phy_fileuri,filesize) values (?, ?, ?, ?, 0, ?, ?, 1,?,?,?,?)");
			long id = DBHelper.getUniqueID(con);
			stmt.setLong(1, id);
			stmt.setLong(2, p_id);
			stmt.setString(3, recordId);
			stmt.setString(4, caption);
			Date date = Calendar.getInstance().getTime();
			stmt.setTimestamp(5, Convert.date2SQLDateTime(date));
			stmt.setLong(6, userId);
			stmt.setString(7, url);
			stmt.setString(8, descript);
			stmt.setString(9, phy);
			stmt.setLong(10, fileSize);
			stmt.execute();
			json.put("r", true);
			json.put("id", id);
			json.put("createDate", Convert.datetime2Str(date));
			json.put("userId", userId);
			json.put("url", SERVER_URL + url);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 删除文件
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public JSONObject deleteFiles(String ids) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("update sys_attachment_ex_tree set is_valid=0 where id=?");
			for (String id : ids.split(",")) {
				stmt.setLong(1, Convert.str2Long(id));
				stmt.execute();
			}
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 获取流程图内容
	 * 
	 * @param diagramName
	 * @return
	 * @throws Exception
	 */
	public JSONObject getAttachments(String recordId) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("select id, caption, type, url, create_date, create_user_id, is_valid, file_descript, phy_fileuri,filesize from sys_attachment_ex_tree where is_valid=1 and record_id=? and p_id=?");
			JSONObject rootJSON = new JSONObject();
			rootJSON.put("caption", "附件列表");
			rootJSON.put("isFolder", true);
			rootJSON.put("isRoot", true);
			rootJSON.put("id", -1L);
			getAttachments(rootJSON, stmt, recordId, -1);
			json.put("attachments", rootJSON);
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 获取附件列表
	 * 
	 * @param json
	 * @param stmt
	 * @param recordId
	 * @param p_id
	 * @throws SQLException
	 */
	private void getAttachments(JSONObject json, PreparedStatement stmt,
			String recordId, long p_id) throws SQLException {
		stmt.setString(1, recordId);
		stmt.setLong(2, p_id);
		ResultSet rs = stmt.executeQuery();
		List<JSONObject> folderList = new ArrayList<JSONObject>();
		while (rs.next()) {
			JSONObject fileJSON = new JSONObject();
			fileJSON.put("id", rs.getLong(1));
			fileJSON.put("caption", rs.getString(2));
			fileJSON.put("createDate", Convert.datetime2Str(rs.getTimestamp(5)));
			fileJSON.put("userId", rs.getLong(6));
			if (rs.getInt(3) == 1) {
				fileJSON.put("isFolder", true);
				folderList.add(fileJSON);
			} else {
				String descript = rs.getString(8);
				fileJSON.put("descript", StringUtils.isEmpty(descript) ? ""
						: descript);
				fileJSON.put("filesize", rs.getLong(10));
				fileJSON.put("url", SERVER_URL + rs.getString(4));
			}
			JSONUtils.append(json, "children", fileJSON);
		}
		for (JSONObject fileJSON : folderList)
			getAttachments(fileJSON, stmt, recordId, fileJSON.getLong("id"));
	}

	/**
	 * 获取属性
	 * 
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getProperty(String recordId) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("select attributes from sys_ext_property where record_id=?");
			stmt.setString(1, recordId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				json.put("property", Convert.bytes2Str(rs.getBytes(1)));
			else
				json.put("attributes", "[]");
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 保存属性
	 * 
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public JSONObject saveProperty(String recordId, String attributes)
			throws Exception {
		Connection con = null;
		PreparedStatement updateStmt = null;
		PreparedStatement selectStmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			selectStmt = con
					.prepareStatement("select count(1) from sys_ext_property where record_id=?");
			selectStmt.setString(1, recordId);
			ResultSet rs = selectStmt.executeQuery();
			rs.next();
			if (rs.getInt(1) == 0)
				updateStmt = con
						.prepareStatement("insert into sys_ext_property (attributes,record_id) values (?, ?)");
			else
				updateStmt = con
						.prepareStatement("update sys_ext_property set attributes=? where record_id=?");
			updateStmt.setBytes(1, Convert.str2Bytes(attributes));
			updateStmt.setString(2, recordId);
			updateStmt.execute();
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(selectStmt);
			DBHelper.closeStatement(updateStmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPicture(String recordId) throws Exception {
		Connection con = null;
		PreparedStatement selectStmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			selectStmt = con
					.prepareStatement("select id,pic_url,pic_thumbnail_url,pic_descript,href_url,create_user_id,create_date,sortorder,pic_category from sys_ext_pictures where isvalid=1 and record_id=? order by sortorder");
			selectStmt.setString(1, recordId);
			ResultSet rs = selectStmt.executeQuery();
			while (rs.next()) {
				JSONObject recordJSON = new JSONObject();
				recordJSON.put("id", rs.getLong(1));
				recordJSON.put("pic_url", SERVER_URL + rs.getString(2));
				recordJSON.put("small_url", SERVER_URL + rs.getString(3));
				recordJSON.put("descript", rs.getString(4));
				recordJSON.put("href", rs.getString(5));
				recordJSON.put("userId", rs.getLong(6));
				recordJSON.put("createDate",
						Convert.datetime2Str(rs.getTimestamp(7)));
				recordJSON.put("sortorder", rs.getLong(8));
				recordJSON.put("category", rs.getString(9));

				JSONUtils.append(json, "pictures", recordJSON);
			}
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(selectStmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 保存图片
	 * 
	 * @param recordId
	 * @param picURL
	 * @param thumbURL
	 * @param descript
	 * @param href
	 * @param userId
	 * @param category
	 * @return
	 * @throws Exception
	 */
	public JSONObject savePicture(String recordId, String picURL,
			String thumbURL, String descript, String href, long userId,
			String category) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("insert into sys_ext_pictures (record_id, pic_url, pic_thumbnail_url, pic_descript, href_url, isvalid, create_user_id, create_date, sortorder, id,pic_category) values (?, ?, ?, ?, ?, 1, ?, ?, ?, ?,?)");
			long id = DBHelper.getUniqueID(con);
			stmt.setString(1, recordId);
			stmt.setString(2, picURL);
			stmt.setString(3, thumbURL);
			stmt.setString(4, descript);
			stmt.setString(5, href);
			stmt.setLong(6, userId);
			Date date = Calendar.getInstance().getTime();
			stmt.setTimestamp(7, Convert.date2SQLDateTime(date));
			stmt.setLong(8, id);
			stmt.setLong(9, id);
			stmt.setString(10, category);
			stmt.execute();
			json.put("r", true);
			json.put("id", id);
			json.put("pic_url", SERVER_URL + picURL);
			json.put("small_url", SERVER_URL + thumbURL);
			json.put("descript", descript);
			json.put("href", href);
			json.put("userId", userId);
			json.put("createDate", Convert.datetime2Str(date));
			json.put("category", category);
			json.put("sortorder", id);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 删除图片
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public JSONObject deletePicture(String ids) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("update sys_ext_pictures set isvalid=0 where id=?");
			for (String id : ids.split(",")) {
				stmt.setLong(1, Convert.str2Long(id));
				stmt.execute();
			}
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 更改顺序
	 * 
	 * @param paramLongs
	 * @return
	 * @throws Exception
	 */
	public JSONObject exchangePicturePosition(long[] ids) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con
					.prepareStatement("update sys_ext_pictures set sortorder=? where id=?");
			for (int i = 0; i < ids.length; i += 2) {
				stmt.setLong(1, ids[i + 1]);
				stmt.setLong(2, ids[i]);
				stmt.execute();
			}
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 获取图斑
	 * 
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getGeometry(String recordId) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con.prepareStatement("select geometry from SYS_EXT_GEOMETRY where record_id=?");
			stmt.setString(1, recordId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				json.put("geometry", Convert.bytes2Str(rs.getBytes(1)));
			else
				json.put("geometry", "[]");
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

//<<<<<<< .mine
	/**
	 * 保存GIS图斑
	 * 
	 * @param recordId
	 * @param geometry
	 * @return
	 * @throws Exception
	 */
	public JSONObject saveGeometry(String recordId, String geometry)
			throws Exception {
		Connection con = null;
		PreparedStatement updateStmt = null;
		PreparedStatement selectStmt = null;
		JSONObject json = new JSONObject();
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			selectStmt = con
					.prepareStatement("select count(1) from SYS_EXT_GEOMETRY where record_id=?");
			selectStmt.setString(1, recordId);
			ResultSet rs = selectStmt.executeQuery();
			rs.next();
			if (rs.getInt(1) == 0)
				updateStmt = con
						.prepareStatement("insert into SYS_EXT_GEOMETRY (geometry,record_id) values (?, ?)");
			else
				updateStmt = con
						.prepareStatement("update SYS_EXT_GEOMETRY set geometry=? where record_id=?");
			updateStmt.setBytes(1, Convert.str2Bytes(geometry));
			updateStmt.setString(2, recordId);
			updateStmt.execute();
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(selectStmt);
			DBHelper.closeStatement(updateStmt);
			DBHelper.closeConnection(con);
		}
		return json;
	}

	/**
	 * 获取图斑
	 * 
	 * @param layerId
	 * @param keyField
	 * @param keyValue
	 * @return
	 * @throws Exception
	 */
	public String getGeometryFromResource(String layerId, String keyField,
			String keyValue, boolean isStringValue) throws Exception {
		return WebGISService.getLayerFeature(layerId, keyField, keyValue,
				isStringValue, false);
	}

	// //////////////////////////////////////////////////////////////////////////////
	/**
	 * gl
	 */
	public void getJSONPoint(JSONObject json4zhparam,JSONObject json4zhgeometries) {
		try {
			String xlbh = json4zhparam.get("xlbh").toString();
			String roadType = xlbh.substring(0, 1);
			String sdeLayerName = "";
			if ("G".equals(roadType) || "g".equals(roadType)) { // 国道
				sdeLayerName = "SDE.国道ROUTE";
			} else if ("S".equals(roadType) || "s".equals(roadType)) {
				sdeLayerName = "SDE.省道ROUTE";
			} else if ("X".equals(roadType) || "x".equals(roadType)) {
				sdeLayerName = "SDE.县道ROUTE";
			} else {
				sdeLayerName = "SDE.高速2ROUTE";
			}
			if (ClientWebService4LineRef.getInstance() != null) {
				Object[] resultGetLayer = ClientWebService4LineRef
						.getInstance().clentInvoke(sdeLayerName, "XLBH",
								"LINEREFBASE", "XLBH", "QSZH", "JZZH", "XLBH",
								"");
//				System.out.println("resultGetLayer[0]=" + resultGetLayer[0]); // SDE.高速ROUTE
				String resultStr = resultGetLayer[0].toString();
				if (resultStr.contains("[]")) {
					Object[] resultGetLayer2 = ClientWebService4LineRef
							.getInstance().clentInvoke("SDE.高速2ROUTE", "XLBH",
									"LINEREFBASE", "XLBH", "QSZH", "JZZH",
									"XLBH", "");
//					System.out.println(resultGetLayer2[0]); // SDE.高速ROUTE
					String resultStr2 = resultGetLayer2[0].toString();
					if (resultStr2.contains("[]")) {
						json4zhgeometries.put("geometries", null);
					} else {
						json4zhgeometries.put("geometries", resultGetLayer2[0]);
						insertIntoLineref(json4zhparam,
								resultGetLayer2[0].toString());
					}
				} else {
					json4zhgeometries.put("geometries", resultGetLayer[0]);
					insertIntoLineref(json4zhparam,
							resultGetLayer[0].toString());
				}
			} else {
				json4zhgeometries.put("geometries", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public void insertIntoLineref(JSONObject json4zhparam, String resultGetLayer) {
		Connection con = null;
		PreparedStatement stmt4insglj = null;
		PreparedStatement selectStmt = null;
		try {
			con = DBHelper.getConnection();
			selectStmt = con
					.prepareStatement("select count(1) from glj_lineref where id=? and xlbh=? and qszh=? and jzzh=?");
			selectStmt.setString(1, json4zhparam.get("zhid").toString());
			selectStmt.setString(2, json4zhparam.get("xlbh").toString());
			selectStmt.setDouble(3,
					Double.parseDouble(json4zhparam.get("qszh").toString()));
			selectStmt.setDouble(4,
					Double.parseDouble(json4zhparam.get("jzzh").toString()));
			ResultSet rs = selectStmt.executeQuery();
			rs.next();
			if (rs.getInt(1) == 0) {
				stmt4insglj = con
						.prepareStatement("insert into glj_lineref(id, xlbh, qszh, jzzh, linegeo,IDFORNUM,IDFORTABLE) values(?, ?, ?, ?, ?,?,?)");
				stmt4insglj.setString(1, json4zhparam.get("zhid").toString());
				stmt4insglj.setString(2, json4zhparam.get("xlbh").toString());
				stmt4insglj
						.setDouble(3, Double.parseDouble(json4zhparam.get(
								"qszh").toString()));
				stmt4insglj
						.setDouble(4, Double.parseDouble(json4zhparam.get(
								"jzzh").toString()));
				stmt4insglj.setBytes(5, Convert.str2Bytes(resultGetLayer));
				stmt4insglj.setString(6, json4zhparam.get("id").toString());
				stmt4insglj.setString(7, json4zhparam.get("tablename")
						.toString());
				stmt4insglj.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBHelper.closeStatement(selectStmt);
			DBHelper.closeStatement(stmt4insglj);
			DBHelper.closeConnection(con);
		}
	}

	public void getJSONPoint(JSONObject json4zhparam,
			ArrayList<JSONObject> jsonGeoArray) {
		try {
			String xlbh = json4zhparam.get("xlbh").toString();
			String roadType = xlbh.substring(0, 1);
			String sdeLayerName = "";
			if ("G".equals(roadType) || "g".equals(roadType)) { // 国道
				sdeLayerName = "SDE.国道ROUTE";
			} else if ("S".equals(roadType) || "s".equals(roadType)) {
				sdeLayerName = "SDE.省道ROUTE";
			} else if ("X".equals(roadType) || "x".equals(roadType)) {
				sdeLayerName = "SDE.县道ROUTE";
			} else {
				sdeLayerName = "SDE.高速2ROUTE";
			}
			if (ClientWebService4LineRef.getInstance() != null) {
				Object[] resultGetLayer = ClientWebService4LineRef
						.getInstance().clentInvoke(sdeLayerName, "XLBH",
								"LINEREFBASE", "XLBH", "QSZH", "JZZH", "XLBH",
								"");
//				System.out.println("resultGetLayer[0]=" + resultGetLayer[0]); // SDE.高速ROUTE
				String resultStr = resultGetLayer[0].toString();
				if (resultStr.contains("[]")) {
					Object[] resultGetLayer2 = ClientWebService4LineRef
							.getInstance().clentInvoke("SDE.高速2ROUTE", "XLBH",
									"LINEREFBASE", "XLBH", "QSZH", "JZZH",
									"XLBH", "");
					String resultStr2 = resultGetLayer2[0].toString();
					if (resultStr2.contains("[]")) {
					} else {
						JSONObject jsonSingle=new JSONObject();
						jsonSingle.put("id", json4zhparam.get("id").toString());
						jsonSingle.put("tablename", json4zhparam.get("tablename")
								.toString());
						jsonSingle.put("geometry", resultGetLayer2[0].toString());
						jsonSingle.put("tablename", json4zhparam.get("tablename")
								.toString());
						//jsonGeoArray.add(resultGetLayer2[0].toString());
						jsonSingle.put("xlbh", json4zhparam.get("xlbh"));
						jsonSingle.put("qszh", json4zhparam.get("qszh"));
						jsonSingle.put("jzzh", json4zhparam.get("jzzh"));
						//jsonGeoArray.add(resultGetLayer2[0].toString());
						jsonGeoArray.add(jsonSingle);
						insertIntoLineref(json4zhparam,
								resultGetLayer2[0].toString());
					}
				} else {
					JSONObject jsonSingle=new JSONObject();
					jsonSingle.put("id", json4zhparam.get("id").toString());
					jsonSingle.put("tablename", json4zhparam.get("tablename")
							.toString());
					jsonSingle.put("geometry", resultGetLayer[0].toString());
					jsonSingle.put("tablename", json4zhparam.get("tablename")
							.toString());
					jsonSingle.put("xlbh", json4zhparam.get("xlbh"));
					jsonSingle.put("qszh", json4zhparam.get("qszh"));
					jsonSingle.put("jzzh", json4zhparam.get("jzzh"));
					jsonGeoArray.add(jsonSingle);
//					jsonGeoArray.add(resultGetLayer[0].toString());
					insertIntoLineref(json4zhparam,
							resultGetLayer[0].toString());
				}
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public JSONObject getConditionByrecordPrefix(String tname) throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("r", false);
		if (ClientWebService4LineRef.getInstance().getFlag()==1){
			Connection con = null;
			PreparedStatement pstmt4NF = null; // 查询年份
			PreparedStatement pstmt4DWMC = null;// 查询单位名称
			PreparedStatement pstmt4LX = null;// 查询路线
			ResultSet rs4Result = null;
			
			try {
				con = DBHelper.getConnection();
				pstmt4NF = con.prepareStatement("select b.id,b.bmz from FS_bmlx a,FS_BMMXB b where a.id=b.pid and a.id=281963 order by b.px");
				// pstmt4DWMC=con.prepareStatement("select b.id,b.bmz from FS_bmlx a,FS_BMMXB b where a.id=b.pid and a.id=281951 order by b.px");
				pstmt4DWMC = con.prepareStatement("select id, name bmz from sys_department where p_id = -1 order by sortorder");
				pstmt4LX = con.prepareStatement("select  distinct t.xlbh from "+ tname + " t where t.valid = 1 order by t.xlbh");
				rs4Result = pstmt4NF.executeQuery();
				while (rs4Result.next()) {
					JSONObject jsonDetail = new JSONObject();
					jsonDetail.put("name", rs4Result.getString("bmz"));
					jsonDetail.put("id", rs4Result.getString("id"));
					JSONUtils.append(json, "nf", jsonDetail);
				}
				rs4Result = pstmt4DWMC.executeQuery();
				while (rs4Result.next()) {
					JSONObject jsonDetail = new JSONObject();
					jsonDetail.put("name", rs4Result.getString("bmz"));
					jsonDetail.put("id", rs4Result.getString("id"));
					JSONUtils.append(json, "dwmc", jsonDetail);
				}
				if(ClientWebService4LineRef.getInstance().getFlag()==1){
					rs4Result = pstmt4LX.executeQuery();
					while (rs4Result.next()) {
						JSONObject jsonDetail = new JSONObject();
						jsonDetail.put("name", rs4Result.getString("xlbh"));
						jsonDetail.put("id", rs4Result.getString("xlbh"));
						JSONUtils.append(json, "lx", jsonDetail);
					}
				}
				json.put("r", "true");
			} finally {
				DBHelper.closeStatement(pstmt4NF);
				DBHelper.closeStatement(pstmt4DWMC);
				DBHelper.closeStatement(pstmt4LX);
				DBHelper.closeConnection(con);
			}
			json.put("flag", "false");
		}else{
			json.put("flag", "true");
		}
		return json;
	}

	// 按条件获取项目位置
	public JSONObject getGeometry4Condtion(String tname, String filterGeoByNF,
			String filterGeoByDWMC, String filterGeoByLX) throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		Connection con = null;
		PreparedStatement stmt4ywb = null;
		ResultSet rs = null;
		// 把空格变成百分号 为字符串添加单引号
		filterGeoByNF = filterGeoByNF.replaceAll(" ", "%");
		ArrayList<JSONObject> jsonGeoArray = new ArrayList<JSONObject>();
		JSONObject json4zhparam = new JSONObject();
		if (ClientWebService4LineRef.getInstance().getFlag() == 1) {
			try {
				con = DBHelper.getConnection();
				int j=0;
				String  sql4ZHinfo=getZHField(tname,con);
				sql4ZHinfo+=" where valid=1 and sftj=1 ";
				
				stmt4ywb = con.prepareStatement(sql4ZHinfo);
				if (!StringUtils.isEmpty(filterGeoByNF)) {
					sql4ZHinfo = sql4ZHinfo + " and nf='" + filterGeoByNF + "'";
				}
				if (!StringUtils.isEmpty(filterGeoByDWMC)) {
					sql4ZHinfo = sql4ZHinfo + " and DWID=" + filterGeoByDWMC;
				}
				if (!StringUtils.isEmpty(filterGeoByLX)) {
					sql4ZHinfo = sql4ZHinfo + " and XLBH='" + filterGeoByLX + "'";
				}
				stmt4ywb = con.prepareStatement(sql4ZHinfo);
				ResultSet rs4ywb = stmt4ywb.executeQuery();
				while (rs4ywb.next()) {
					j++;
					json4zhparam.put("zhid", tname + rs4ywb.getInt("id"));
					json4zhparam.put("id",  rs4ywb.getInt("id"));
					json4zhparam.put("xlbh", rs4ywb.getString("xlbh"));
					json4zhparam.put("qszh", rs4ywb.getDouble("zh1"));
					json4zhparam.put("jzzh", rs4ywb.getDouble("zh2"));
					json4zhparam.put("zh3", rs4ywb.getString("zh3"));
					json4zhparam.put("tablename", tname);
					
					JSONArray zharray= new JSONArray();;
					if(json4zhparam.getString("zh3")!=null&&json4zhparam.getString("zh3").toString()!=""){
						String zh3= json4zhparam.getString("zh3").toString();
						zharray= getZHinfo(zh3);
						zharray=hebingzh(json4zhparam,zharray);
					}else{
						JSONObject jsonone=new JSONObject();
						jsonone.put("0", json4zhparam.get("qszh"));
						jsonone.put("1", json4zhparam.get("jzzh"));
						zharray.add(jsonone);
					}
					
					for(int i=0;i<zharray.size();i++){
						JSONObject jsonzh=(JSONObject)zharray.get(i);
						JSONObject jsonParam = new JSONObject();
						jsonParam.put("tablename", tname);
						jsonParam.put("zhid", json4zhparam.get("zhid"));
						jsonParam.put("xlbh", json4zhparam.get("xlbh"));
						jsonParam.put("id", json4zhparam.get("id"));
						jsonParam.put("qszh", jsonzh.get("0"));
						jsonParam.put("jzzh", jsonzh.get("1"));
						getGeometryFromOracle(jsonGeoArray, jsonParam, con);
					}
				}
//				System.out.println("j=" + j);
				json.put("geometry4all", jsonGeoArray);
				json.put("r", true);
			} finally {
				DBHelper.closeStatement(stmt4ywb);
				DBHelper.closeConnection(con);
			}
		}
		return json;
	}

	private void getGeometryFromOracle(ArrayList<JSONObject> jsonGeoArray,
			JSONObject jsonParam, Connection con) throws Exception {
		PreparedStatement stmt4SelGLJ = null;
		ResultSet rs4selglj = null;
		String str4geometry = "";
		try {
			String sql4SelGLJ = "select id, xlbh, qszh, jzzh, linegeo from glj_lineref where id=? and xlbh=? and qszh=? and jzzh=?";
			stmt4SelGLJ = con.prepareStatement(sql4SelGLJ);
			stmt4SelGLJ.setString(1, jsonParam.get("zhid").toString());
			stmt4SelGLJ.setString(2, jsonParam.get("xlbh").toString());
			stmt4SelGLJ.setDouble(3,
					Double.parseDouble(jsonParam.get("qszh").toString()));
			stmt4SelGLJ.setDouble(4,
					Double.parseDouble(jsonParam.get("jzzh").toString()));
			rs4selglj = stmt4SelGLJ.executeQuery();
			while (rs4selglj.next()) {
				str4geometry = Convert.bytes2Str(rs4selglj.getBytes("linegeo"));
//				System.out.println("已存在 " + "   "
//						+ jsonParam.get("zhid").toString());
				JSONObject jsonSingle=new JSONObject();
				jsonSingle.put("id", jsonParam.get("id").toString());
				jsonSingle.put("tablename", jsonParam.get("tablename")
						.toString());
				jsonSingle.put("geometry", str4geometry);
				//jsonGeoArray.add(resultGetLayer2[0].toString());
				jsonSingle.put("xlbh", jsonParam.get("xlbh"));
				jsonSingle.put("qszh", jsonParam.get("qszh"));
				jsonSingle.put("jzzh", jsonParam.get("jzzh"));
				jsonGeoArray.add(jsonSingle);
				//jsonGeoArray.add(str4geometry);
			}
			if (str4geometry == "" && jsonParam != null) {
				getGeometryFromService(jsonParam, jsonGeoArray, con);
			}
		} finally {
			DBHelper.closeStatement(stmt4SelGLJ);
		}
	}

	private void getGeometryFromService(JSONObject jsonParam,
			ArrayList<JSONObject> jsonGeoArray, Connection con) throws Exception {
		PreparedStatement stmt4del = null;
		PreparedStatement stmt4ins = null;
		String sdeServerName = ClientWebService4LineRef.getInstance().getUser();
		try {
			String sql4Del = "delete from  " + sdeServerName + ".linerefbase";
			stmt4del = con.prepareStatement(sql4Del);
			stmt4del.executeUpdate();

			stmt4ins = con
					.prepareStatement("insert into "
							+ sdeServerName
							+ ".linerefbase(id, xlbh, qszh, jzzh) values (seq_for_j2ee_webgis.nextval, ?, ?, ?)");
			stmt4ins.setString(1, jsonParam.get("xlbh").toString());
			stmt4ins.setDouble(2,
					Double.parseDouble(jsonParam.get("qszh").toString()));
			stmt4ins.setDouble(3,
					Double.parseDouble(jsonParam.get("jzzh").toString()));
			stmt4ins.executeUpdate();

			getJSONPoint(jsonParam, jsonGeoArray);
		} finally {
			DBHelper.closeStatement(stmt4ins);
			DBHelper.closeStatement(stmt4del);
		}
	}

	public JSONObject getProtectInfo(String pro_id, String pro_tablename)
			throws Exception {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("r", false);
		PreparedStatement stmt4tableColumn = null;
		PreparedStatement stmt4projectinfo=null;
		Connection con = null;
		///获取所需的数据列
		String sql4tabcol = "select table_filed,bm from ywb_tab_field where table_name = upper(?) and sfxs=1 order by px";
		//获取数据列对应的项目信息
		String sql4proinfo="select * from "+pro_tablename+" where id="+pro_id;
		JSONArray projectinfo=new JSONArray();
		try {
			con = DBHelper.getConnection();
			stmt4tableColumn = con.prepareStatement(sql4tabcol);
			stmt4tableColumn.setString(1, pro_tablename);
			ResultSet rs4tableColumn = stmt4tableColumn.executeQuery();
			LinkedHashMap <String, String> map4tableColumn = new LinkedHashMap<String, String>();
			while (rs4tableColumn.next()) {
				map4tableColumn.put(rs4tableColumn.getString("table_filed"),
						rs4tableColumn.getString("bm"));
			}
			
			String captionfield=generateRecordCaption(pro_tablename,con);
			
			stmt4projectinfo=con.prepareStatement(sql4proinfo);
			ResultSet rs4projectinfo=stmt4projectinfo.executeQuery();
			while(rs4projectinfo.next()){
				Iterator it=map4tableColumn.entrySet().iterator();
				while(it.hasNext()){
					JSONObject proFieldInfo=new JSONObject();
					Entry entry=(Entry)it.next();
					String key= entry.getKey().toString();
					String value=entry.getValue().toString();
					proFieldInfo.put("name",value);
					proFieldInfo.put("value", rs4projectinfo.getString(key));
					if(key.equals(captionfield)){
						json.put("label", rs4projectinfo.getString(key));
					}
					projectinfo.add(proFieldInfo);
				}
			}
			json.put("projectinfo", projectinfo);
			
			json.put("r", true);
		} finally {
			DBHelper.closeStatement(stmt4tableColumn);
			DBHelper.closeConnection(con);
		}
		return json;
	}
	
	public String generateRecordCaption(String pro_tablename,Connection con) throws SQLException{
		String caption=""; 
		PreparedStatement pstmt=null;
		String sql4caption="select table_filed,bm,is_caption from ywb_tab_field where table_name = upper(?) and sfxs=1 order by px";
		try{
			pstmt=con.prepareStatement(sql4caption);
			pstmt.setString(1, pro_tablename);
			ResultSet rs=pstmt.executeQuery();
			int i=0;
			while(rs.next()){
				if(i==0){
					caption=rs.getString(1);
				}
				if(rs.getInt(3)==1){
					caption=rs.getString(1);
					break;
				}
				i++;
			}
		}finally{
			DBHelper.closeStatement(pstmt);
		}
		return caption;
	}
	
	/*
	 * 获取zh3字段中的桩号信息
	 */
	public static JSONArray getZHinfo(String str){
		int idx1=str.indexOf("(");
		int idx2=str.indexOf(")");
		if(idx1==-1){
			idx1=str.indexOf("（");
		}
		if(idx2==-1){
			idx2=str.indexOf("）");
		}
		JSONArray jsonarray=new JSONArray(); 
		if(idx1!=-1&&idx2!=-1){
			String zhinfo=str.substring(idx1+1,idx2);
			String[] zhinfos=null;
			if(zhinfo.contains(",")){
				zhinfos=zhinfo.split(",");
			}else if(zhinfo.contains("，")){
				zhinfos=zhinfo.split("，");
			}else{
				zhinfos=zhinfo.split(",");
			}
			if(zhinfo!=null){
				for(String zh:zhinfos){
					JSONObject jsonobject=new JSONObject();
					String[] zhs=null;
					if(zh.contains("-")){
					   zhs=zh.split("-");
					}
					if(zh.contains("—")){
					   zhs=zh.split("—");
					}
					if(zhs!=null){
						for(int i=0;i<zhs.length;i++){
							String zhao=zhs[i];
							String zh1="";
						    if(!isChineseChar(zh1)){
								if(zhao.contains("K")){
								    zh1=zhao.replaceAll("K", "");
								}
								if(zhao.contains("k")){
								    zh1=zhao.replaceAll("k", "");	
								}
								if(zh1.contains("+")){
									zh1=zh1.replace("+", "");
								}else{
									zh1=zh1+"000";
								}
								Double dou=Double.valueOf(zh1);
								jsonobject.put(i+"", dou);
						    }
						} 
						jsonarray.add(jsonobject);
					}
				}
			}
		}
		return jsonarray;
	}
	//判断有无汉字
	public static boolean isChineseChar(String str){
        boolean temp = false;
        Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
        Matcher m=p.matcher(str); 
        if(m.find()){ 
            temp =  true;
//=======
//	/**
//	 * 保存GIS图斑
//	 * 
//	 * @param recordId
//	 * @param geometry
//	 * @return
//	 * @throws Exception
//	 */
//	public JSONObject saveGeometry(String recordId, String geometry)
//			throws Exception {
//		Connection con = null;
//		PreparedStatement updateStmt = null;
//		PreparedStatement selectStmt = null;
//		JSONObject json = new JSONObject();
//		json.put("r", false);
//		try {
//			con = DBHelper.getConnection();
//			selectStmt = con
//					.prepareStatement("select count(1) from SYS_EXT_GEOMETRY where record_id=?");
//			selectStmt.setString(1, recordId);
//			ResultSet rs = selectStmt.executeQuery();
//			rs.next();
//			if (rs.getInt(1) == 0)
//				updateStmt = con
//						.prepareStatement("insert into SYS_EXT_GEOMETRY (geometry,record_id) values (?, ?)");
//			else
//				updateStmt = con
//						.prepareStatement("update SYS_EXT_GEOMETRY set geometry=? where record_id=?");
//			updateStmt.setBytes(1, Convert.str2Bytes(geometry));
//			updateStmt.setString(2, recordId);
//			updateStmt.execute();
//			json.put("r", true);
//		} finally {
//			DBHelper.closeStatement(selectStmt);
//			DBHelper.closeStatement(updateStmt);
//			DBHelper.closeConnection(con);
//		}
//		return json;
//	}
//
//	/**
//	 * 获取图斑
//	 * 
//	 * @param layerId
//	 * @param keyField
//	 * @param keyValue
//	 * @return
//	 * @throws Exception
//	 */
//	public String getGeometryFromResource(String layerId, String keyField,
//			String keyValue, boolean isStringValue) throws Exception {
//		return WebGISService.getLayerFeature(layerId, keyField, keyValue,
//				isStringValue, false);
//	}
//
//	// //////////////////////////////////////////////////////////////////////////////
//	/**
//	 * gl
//	 */
//	public void getJSONPoint(JSONObject json4zhparam,JSONObject json4zhgeometries) {
//		try {
//			String xlbh = json4zhparam.get("xlbh").toString();
//			String roadType = xlbh.substring(0, 1);
//			String sdeLayerName = "";
//			if ("G".equals(roadType) || "g".equals(roadType)) { // 国道
//				sdeLayerName = "SDE.国道ROUTE";
//			} else if ("S".equals(roadType) || "s".equals(roadType)) {
//				sdeLayerName = "SDE.省道ROUTE";
//			} else if ("X".equals(roadType) || "x".equals(roadType)) {
//				sdeLayerName = "SDE.县道ROUTE";
//			} else {
//				sdeLayerName = "SDE.高速2ROUTE";
//			}
//			if (ClientWebService4LineRef.getInstance() != null) {
//				Object[] resultGetLayer = ClientWebService4LineRef
//						.getInstance().clentInvoke(sdeLayerName, "XLBH",
//								"LINEREFBASE", "XLBH", "QSZH", "JZZH", "XLBH",
//								"");
//				System.out.println("resultGetLayer[0]=" + resultGetLayer[0]); // SDE.高速ROUTE
//				String resultStr = resultGetLayer[0].toString();
//				if (resultStr.contains("[]")) {
//					Object[] resultGetLayer2 = ClientWebService4LineRef
//							.getInstance().clentInvoke("SDE.高速2ROUTE", "XLBH",
//									"LINEREFBASE", "XLBH", "QSZH", "JZZH",
//									"XLBH", "");
//					System.out.println(resultGetLayer2[0]); // SDE.高速ROUTE
//					String resultStr2 = resultGetLayer2[0].toString();
//					if (resultStr2.contains("[]")) {
//						json4zhgeometries.put("geometries", null);
//					} else {
//						json4zhgeometries.put("geometries", resultGetLayer2[0]);
//						insertIntoLineref(json4zhparam,
//								resultGetLayer2[0].toString());
//					}
//				} else {
//					json4zhgeometries.put("geometries", resultGetLayer[0]);
//					insertIntoLineref(json4zhparam,
//							resultGetLayer[0].toString());
//				}
//			} else {
//				json4zhgeometries.put("geometries", null);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//		}
//	}
//
//	public void insertIntoLineref(JSONObject json4zhparam, String resultGetLayer) {
//		Connection con = null;
//		PreparedStatement stmt4insglj = null;
//		PreparedStatement selectStmt = null;
//		try {
//			con = DBHelper.getConnection();
//			selectStmt = con
//					.prepareStatement("select count(1) from glj_lineref where id=? and xlbh=? and qszh=? and jzzh=?");
//			selectStmt.setString(1, json4zhparam.get("zhid").toString());
//			selectStmt.setString(2, json4zhparam.get("xlbh").toString());
//			selectStmt.setDouble(3,
//					Double.parseDouble(json4zhparam.get("qszh").toString()));
//			selectStmt.setDouble(4,
//					Double.parseDouble(json4zhparam.get("jzzh").toString()));
//			ResultSet rs = selectStmt.executeQuery();
//			rs.next();
//			if (rs.getInt(1) == 0) {
//				stmt4insglj = con
//						.prepareStatement("insert into glj_lineref(id, xlbh, qszh, jzzh, linegeo,IDFORNUM,IDFORTABLE) values(?, ?, ?, ?, ?,?,?)");
//				stmt4insglj.setString(1, json4zhparam.get("zhid").toString());
//				stmt4insglj.setString(2, json4zhparam.get("xlbh").toString());
//				stmt4insglj
//						.setDouble(3, Double.parseDouble(json4zhparam.get(
//								"qszh").toString()));
//				stmt4insglj
//						.setDouble(4, Double.parseDouble(json4zhparam.get(
//								"jzzh").toString()));
//				stmt4insglj.setBytes(5, Convert.str2Bytes(resultGetLayer));
//				stmt4insglj.setString(6, json4zhparam.get("id").toString());
//				stmt4insglj.setString(7, json4zhparam.get("tablename")
//						.toString());
//				stmt4insglj.executeUpdate();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			DBHelper.closeStatement(selectStmt);
//			DBHelper.closeStatement(stmt4insglj);
//			DBHelper.closeConnection(con);
//		}
//	}
//
//	public void getJSONPoint(JSONObject json4zhparam,
//			ArrayList<JSONObject> jsonGeoArray) {
//		try {
//			String xlbh = json4zhparam.get("xlbh").toString();
//			String roadType = xlbh.substring(0, 1);
//			String sdeLayerName = "";
//			if ("G".equals(roadType) || "g".equals(roadType)) { // 国道
//				sdeLayerName = "SDE.国道ROUTE";
//			} else if ("S".equals(roadType) || "s".equals(roadType)) {
//				sdeLayerName = "SDE.省道ROUTE";
//			} else if ("X".equals(roadType) || "x".equals(roadType)) {
//				sdeLayerName = "SDE.县道ROUTE";
//			} else {
//				sdeLayerName = "SDE.高速2ROUTE";
//			}
//			if (ClientWebService4LineRef.getInstance() != null) {
//				Object[] resultGetLayer = ClientWebService4LineRef
//						.getInstance().clentInvoke(sdeLayerName, "XLBH",
//								"LINEREFBASE", "XLBH", "QSZH", "JZZH", "XLBH",
//								"");
//				System.out.println("resultGetLayer[0]=" + resultGetLayer[0]); // SDE.高速ROUTE
//				String resultStr = resultGetLayer[0].toString();
//				if (resultStr.contains("[]")) {
//					Object[] resultGetLayer2 = ClientWebService4LineRef
//							.getInstance().clentInvoke("SDE.高速2ROUTE", "XLBH",
//									"LINEREFBASE", "XLBH", "QSZH", "JZZH",
//									"XLBH", "");
//					String resultStr2 = resultGetLayer2[0].toString();
//					if (resultStr2.contains("[]")) {
//					} else {
//						JSONObject jsonSingle=new JSONObject();
//						jsonSingle.put("id", json4zhparam.get("id").toString());
//						jsonSingle.put("tablename", json4zhparam.get("tablename")
//								.toString());
//						jsonSingle.put("geometry", resultGetLayer2[0].toString());
//						jsonSingle.put("tablename", json4zhparam.get("tablename")
//								.toString());
//						//jsonGeoArray.add(resultGetLayer2[0].toString());
//						jsonSingle.put("xlbh", json4zhparam.get("xlbh"));
//						jsonSingle.put("qszh", json4zhparam.get("qszh"));
//						jsonSingle.put("jzzh", json4zhparam.get("jzzh"));
//						//jsonGeoArray.add(resultGetLayer2[0].toString());
//						jsonGeoArray.add(jsonSingle);
//						insertIntoLineref(json4zhparam,
//								resultGetLayer2[0].toString());
//					}
//				} else {
//					JSONObject jsonSingle=new JSONObject();
//					jsonSingle.put("id", json4zhparam.get("id").toString());
//					jsonSingle.put("tablename", json4zhparam.get("tablename")
//							.toString());
//					jsonSingle.put("geometry", resultGetLayer[0].toString());
//					jsonSingle.put("tablename", json4zhparam.get("tablename")
//							.toString());
//					jsonSingle.put("xlbh", json4zhparam.get("xlbh"));
//					jsonSingle.put("qszh", json4zhparam.get("qszh"));
//					jsonSingle.put("jzzh", json4zhparam.get("jzzh"));
//					jsonGeoArray.add(jsonSingle);
////					jsonGeoArray.add(resultGetLayer[0].toString());
//					insertIntoLineref(json4zhparam,
//							resultGetLayer[0].toString());
//				}
//			} else {
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//		}
//	}
//
//	public JSONObject getConditionByrecordPrefix(String tname) throws Exception {
//		// TODO Auto-generated method stub
//		JSONObject json = new JSONObject();
//		json.put("r", false);
//		if (ClientWebService4LineRef.getInstance().getFlag()==1){
//			Connection con = null;
//			PreparedStatement pstmt4NF = null; // 查询年份
//			PreparedStatement pstmt4DWMC = null;// 查询单位名称
//			PreparedStatement pstmt4LX = null;// 查询路线
//			ResultSet rs4Result = null;
//			
//			try {
//				con = DBHelper.getConnection();
//				pstmt4NF = con
//						.prepareStatement("select b.id,b.bmz from FS_bmlx a,FS_BMMXB b where a.id=b.pid and a.id=281963 order by b.px");
//				// pstmt4DWMC=con.prepareStatement("select b.id,b.bmz from FS_bmlx a,FS_BMMXB b where a.id=b.pid and a.id=281951 order by b.px");
//				pstmt4DWMC = con
//						.prepareStatement("select id, name bmz from sys_department where p_id = -1 order by sortorder");
//				pstmt4LX = con.prepareStatement("select  distinct t.xlbh from "
//						+ tname + " t where t.valid = 1 order by t.xlbh");
//				rs4Result = pstmt4NF.executeQuery();
//				while (rs4Result.next()) {
//					JSONObject jsonDetail = new JSONObject();
//					jsonDetail.put("name", rs4Result.getString("bmz"));
//					jsonDetail.put("id", rs4Result.getString("id"));
//					JSONUtils.append(json, "nf", jsonDetail);
//				}
//				rs4Result = pstmt4DWMC.executeQuery();
//				while (rs4Result.next()) {
//					JSONObject jsonDetail = new JSONObject();
//					jsonDetail.put("name", rs4Result.getString("bmz"));
//					jsonDetail.put("id", rs4Result.getString("id"));
//					JSONUtils.append(json, "dwmc", jsonDetail);
//				}
//				if(ClientWebService4LineRef.getInstance().getFlag()==1){
//					rs4Result = pstmt4LX.executeQuery();
//					while (rs4Result.next()) {
//						JSONObject jsonDetail = new JSONObject();
//						jsonDetail.put("name", rs4Result.getString("xlbh"));
//						jsonDetail.put("id", rs4Result.getString("xlbh"));
//						JSONUtils.append(json, "lx", jsonDetail);
//					}
//				}
//				json.put("r", "true");
//			} finally {
//				DBHelper.closeStatement(pstmt4NF);
//				DBHelper.closeStatement(pstmt4DWMC);
//				DBHelper.closeStatement(pstmt4LX);
//				DBHelper.closeConnection(con);
//			}
//			json.put("flag", "false");
//		}else{
//			json.put("flag", "true");
//		}
//		return json;
//	}
//
//	// 按条件获取项目位置
//	public JSONObject getGeometry4Condtion(String tname, String filterGeoByNF,
//			String filterGeoByDWMC, String filterGeoByLX) throws Exception {
//		// TODO Auto-generated method stub
//		JSONObject json = new JSONObject();
//		Connection con = null;
//		PreparedStatement stmt4ywb = null;
//		ResultSet rs = null;
//		// 把空格变成百分号 为字符串添加单引号
//		filterGeoByNF = filterGeoByNF.replaceAll(" ", "%");
//		ArrayList<JSONObject> jsonGeoArray = new ArrayList<JSONObject>();
//		JSONObject json4zhparam = new JSONObject();
//		if (ClientWebService4LineRef.getInstance().getFlag() == 1) {
//			try {
//				con = DBHelper.getConnection();
//				int j=0;
//				String  sql4ZHinfo=getZHField(tname,con);
//				sql4ZHinfo+=" where valid=1 and sftj=1 ";
//				
//				stmt4ywb = con.prepareStatement(sql4ZHinfo);
//				if (!StringUtils.isEmpty(filterGeoByNF)) {
//					sql4ZHinfo = sql4ZHinfo + " and nf='" + filterGeoByNF + "'";
//				}
//				if (!StringUtils.isEmpty(filterGeoByDWMC)) {
//					sql4ZHinfo = sql4ZHinfo + " and DWID=" + filterGeoByDWMC;
//				}
//				if (!StringUtils.isEmpty(filterGeoByLX)) {
//					sql4ZHinfo = sql4ZHinfo + " and XLBH='" + filterGeoByLX + "'";
//				}
//				stmt4ywb = con.prepareStatement(sql4ZHinfo);
//				ResultSet rs4ywb = stmt4ywb.executeQuery();
//				while (rs4ywb.next()) {
//					j++;
//					json4zhparam.put("zhid", tname + rs4ywb.getInt("id"));
//					json4zhparam.put("id",  rs4ywb.getInt("id"));
//					json4zhparam.put("xlbh", rs4ywb.getString("xlbh"));
//					json4zhparam.put("qszh", rs4ywb.getDouble("zh1"));
//					json4zhparam.put("jzzh", rs4ywb.getDouble("zh2"));
//					json4zhparam.put("zh3", rs4ywb.getString("zh3"));
//					json4zhparam.put("tablename", tname);
//					
//					JSONArray zharray= new JSONArray();;
//					if(json4zhparam.getString("zh3")!=null&&json4zhparam.getString("zh3").toString()!=""){
//						String zh3= json4zhparam.getString("zh3").toString();
//						zharray= getZHinfo(zh3);
//						zharray=hebingzh(json4zhparam,zharray);
//					}else{
//						JSONObject jsonone=new JSONObject();
//						jsonone.put("0", json4zhparam.get("qszh"));
//						jsonone.put("1", json4zhparam.get("jzzh"));
//						zharray.add(jsonone);
//					}
//					
//					for(int i=0;i<zharray.size();i++){
//						JSONObject jsonzh=(JSONObject)zharray.get(i);
//						JSONObject jsonParam = new JSONObject();
//						jsonParam.put("tablename", tname);
//						jsonParam.put("zhid", json4zhparam.get("zhid"));
//						jsonParam.put("xlbh", json4zhparam.get("xlbh"));
//						jsonParam.put("id", json4zhparam.get("id"));
//						jsonParam.put("qszh", jsonzh.get("0"));
//						jsonParam.put("jzzh", jsonzh.get("1"));
//						getGeometryFromOracle(jsonGeoArray, jsonParam, con);
//					}
//				}
//				System.out.println("j=" + j);
//				json.put("geometry4all", jsonGeoArray);
//				json.put("r", true);
//			} finally {
//				DBHelper.closeStatement(stmt4ywb);
//				DBHelper.closeConnection(con);
//			}
//		}
//		return json;
//	}
//
//	private void getGeometryFromOracle(ArrayList<JSONObject> jsonGeoArray,
//			JSONObject jsonParam, Connection con) throws Exception {
//		PreparedStatement stmt4SelGLJ = null;
//		ResultSet rs4selglj = null;
//		String str4geometry = "";
//		try {
//			String sql4SelGLJ = "select id, xlbh, qszh, jzzh, linegeo from glj_lineref where id=? and xlbh=? and qszh=? and jzzh=?";
//			stmt4SelGLJ = con.prepareStatement(sql4SelGLJ);
//			stmt4SelGLJ.setString(1, jsonParam.get("zhid").toString());
//			stmt4SelGLJ.setString(2, jsonParam.get("xlbh").toString());
//			stmt4SelGLJ.setDouble(3,
//					Double.parseDouble(jsonParam.get("qszh").toString()));
//			stmt4SelGLJ.setDouble(4,
//					Double.parseDouble(jsonParam.get("jzzh").toString()));
//			rs4selglj = stmt4SelGLJ.executeQuery();
//			while (rs4selglj.next()) {
//				str4geometry = Convert.bytes2Str(rs4selglj.getBytes("linegeo"));
//				System.out.println("已存在 " + "   "
//						+ jsonParam.get("zhid").toString());
//				JSONObject jsonSingle=new JSONObject();
//				jsonSingle.put("id", jsonParam.get("id").toString());
//				jsonSingle.put("tablename", jsonParam.get("tablename")
//						.toString());
//				jsonSingle.put("geometry", str4geometry);
//				//jsonGeoArray.add(resultGetLayer2[0].toString());
//				jsonSingle.put("xlbh", jsonParam.get("xlbh"));
//				jsonSingle.put("qszh", jsonParam.get("qszh"));
//				jsonSingle.put("jzzh", jsonParam.get("jzzh"));
//				jsonGeoArray.add(jsonSingle);
//				//jsonGeoArray.add(str4geometry);
//			}
//			if (str4geometry == "" && jsonParam != null) {
//				getGeometryFromService(jsonParam, jsonGeoArray, con);
//			}
//		} finally {
//			DBHelper.closeStatement(stmt4SelGLJ);
//		}
//	}
//
//	private void getGeometryFromService(JSONObject jsonParam,
//			ArrayList<JSONObject> jsonGeoArray, Connection con) throws Exception {
//		PreparedStatement stmt4del = null;
//		PreparedStatement stmt4ins = null;
//		String sdeServerName = ClientWebService4LineRef.getInstance().getUser();
//		try {
//			String sql4Del = "delete from  " + sdeServerName + ".linerefbase";
//			stmt4del = con.prepareStatement(sql4Del);
//			stmt4del.executeUpdate();
//
//			stmt4ins = con
//					.prepareStatement("insert into "
//							+ sdeServerName
//							+ ".linerefbase(id, xlbh, qszh, jzzh) values (seq_for_j2ee_webgis.nextval, ?, ?, ?)");
//			stmt4ins.setString(1, jsonParam.get("xlbh").toString());
//			stmt4ins.setDouble(2,
//					Double.parseDouble(jsonParam.get("qszh").toString()));
//			stmt4ins.setDouble(3,
//					Double.parseDouble(jsonParam.get("jzzh").toString()));
//			stmt4ins.executeUpdate();
//
//			getJSONPoint(jsonParam, jsonGeoArray);
//		} finally {
//			DBHelper.closeStatement(stmt4ins);
//			DBHelper.closeStatement(stmt4del);
//		}
//	}
//
//	public JSONObject getProtectInfo(String pro_id, String pro_tablename)
//			throws Exception {
//		// TODO Auto-generated method stub
//		JSONObject json = new JSONObject();
//		json.put("r", false);
//		PreparedStatement stmt4tableColumn = null;
//		PreparedStatement stmt4projectinfo=null;
//		Connection con = null;
//		///获取所需的数据列
//		String sql4tabcol = "select table_filed,bm from ywb_tab_field where table_name = upper(?) and sfxs=1 order by px";
//		//获取数据列对应的项目信息
//		String sql4proinfo="select * from "+pro_tablename+" where id="+pro_id;
//		JSONArray projectinfo=new JSONArray();
//		try {
//			con = DBHelper.getConnection();
//			stmt4tableColumn = con.prepareStatement(sql4tabcol);
//			stmt4tableColumn.setString(1, pro_tablename);
//			ResultSet rs4tableColumn = stmt4tableColumn.executeQuery();
//			LinkedHashMap <String, String> map4tableColumn = new LinkedHashMap<String, String>();
//			while (rs4tableColumn.next()) {
//				map4tableColumn.put(rs4tableColumn.getString("table_filed"),
//						rs4tableColumn.getString("bm"));
//			}
//			
//			String captionfield=generateRecordCaption(pro_tablename,con);
//			
//			stmt4projectinfo=con.prepareStatement(sql4proinfo);
//			ResultSet rs4projectinfo=stmt4projectinfo.executeQuery();
//			while(rs4projectinfo.next()){
//				Iterator it=map4tableColumn.entrySet().iterator();
//				while(it.hasNext()){
//					JSONObject proFieldInfo=new JSONObject();
//					Entry entry=(Entry)it.next();
//					String key= entry.getKey().toString();
//					String value=entry.getValue().toString();
//					proFieldInfo.put("name",value);
//					proFieldInfo.put("value", rs4projectinfo.getString(key));
//					if(key.equals(captionfield)){
//						json.put("label", rs4projectinfo.getString(key));
//					}
//					projectinfo.add(proFieldInfo);
//				}
//			}
//			json.put("projectinfo", projectinfo);
//			
//			json.put("r", true);
//		} finally {
//			DBHelper.closeStatement(stmt4tableColumn);
//			DBHelper.closeConnection(con);
//		}
//		return json;
//	}
//	
//	public String generateRecordCaption(String pro_tablename,Connection con) throws SQLException{
//		String caption=""; 
//		PreparedStatement pstmt=null;
//		String sql4caption="select table_filed,bm,is_caption from ywb_tab_field where table_name = upper(?) and sfxs=1 order by px";
//		try{
//			pstmt=con.prepareStatement(sql4caption);
//			pstmt.setString(1, pro_tablename);
//			ResultSet rs=pstmt.executeQuery();
//			int i=0;
//			while(rs.next()){
//				if(i==0){
//					caption=rs.getString(1);
//				}
//				if(rs.getInt(3)==1){
//					caption=rs.getString(1);
//					break;
//				}
//				i++;
//			}
//		}finally{
//			DBHelper.closeStatement(pstmt);
//		}
//		return caption;
//	}
//	
//	/*
//	 * 获取zh3字段中的桩号信息
//	 */
//	public static JSONArray getZHinfo(String str){
//		int idx1=str.indexOf("(");
//		int idx2=str.indexOf(")");
//		if(idx1==-1){
//			idx1=str.indexOf("（");
//		}
//		if(idx2==-1){
//			idx2=str.indexOf("）");
//		}
//		JSONArray jsonarray=new JSONArray(); 
//		if(idx1!=-1&&idx2!=-1){
//			String zhinfo=str.substring(idx1+1,idx2);
//			String[] zhinfos=null;
//			if(zhinfo.contains(",")){
//				zhinfos=zhinfo.split(",");
//			}else if(zhinfo.contains("，")){
//				zhinfos=zhinfo.split("，");
//			}else{
//				zhinfos=zhinfo.split(",");
//			}
//			if(zhinfo!=null){
//				for(String zh:zhinfos){
//					JSONObject jsonobject=new JSONObject();
//					String[] zhs=null;
//					if(zh.contains("-")){
//					   zhs=zh.split("-");
//					}
//					if(zh.contains("—")){
//					   zhs=zh.split("—");
//					}
//					if(zhs!=null){
//						for(int i=0;i<zhs.length;i++){
//							String zhao=zhs[i];
//							String zh1="";
//						    if(!isChineseChar(zh1)){
//								if(zhao.contains("K")){
//								    zh1=zhao.replaceAll("K", "");
//								}
//								if(zhao.contains("k")){
//								    zh1=zhao.replaceAll("k", "");	
//								}
//								if(zh1.contains("+")){
//									zh1=zh1.replace("+", "");
//								}else{
//									zh1=zh1+"000";
//								}
//								Double dou=Double.valueOf(zh1);
//								jsonobject.put(i+"", dou);
//						    }
//						} 
//						jsonarray.add(jsonobject);
//					}
//				}
//			}
//		}
//		return jsonarray;
//	}
//	//判断有无汉字
//	public static boolean isChineseChar(String str){
//        boolean temp = false;
//        Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
//        Matcher m=p.matcher(str); 
//        if(m.find()){ 
//            temp =  true;
//>>>>>>> .r149
        }
        return temp;
    }
	
	
	public static JSONArray hebingzh(JSONObject json4zhparam,JSONArray array){
		JSONArray jsonarray=new JSONArray();
		jsonarray=array;
		int flag=0;
		for(int i=0;i<array.size();i++){
			JSONObject zh=(JSONObject)array.get(i);
			String x1= zh.get("0").toString();
			String x2=json4zhparam.get("qszh").toString();
			String y1= zh.get("1").toString();
			String y2=json4zhparam.get("jzzh").toString();
			if((x1.equals(x2))&&(y1.equals(y2))){
				flag=1;
				break;
			};
		}
		if(flag==0){
			JSONObject jsonsimgle=new JSONObject();
			jsonsimgle.put("0", json4zhparam.get("qszh"));
			jsonsimgle.put("1", json4zhparam.get("jzzh"));
			jsonarray.add(jsonsimgle);
		}
		return jsonarray;
	}
	/**
	 * 获取图斑
	 * 
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getGeometry(String recordId, String recordIdNum,String tablename) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt4ywb = null;

//<<<<<<< .mine
		JSONObject json = new JSONObject();
		JSONObject json4zhparam = new JSONObject();
		ArrayList<JSONObject> jsonGeoArray = new ArrayList<JSONObject>();

		json4zhparam.put("id", recordIdNum);
		json4zhparam.put("tablename", tablename);
		json.put("r", false);
		try {
			con = DBHelper.getConnection();
			stmt = con.prepareStatement("select geometry from SYS_EXT_GEOMETRY where record_id=?");
			stmt.setString(1, recordId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				json.put("geometry", Convert.bytes2Str(rs.getBytes(1)));
			else
				json.put("geometry", "[]");
			
			if (ClientWebService4LineRef.getInstance().getFlag() == 1) {
				String  sql4ZHinfo=getZHField(tablename,con);
				if(StringUtils.isNotEmpty(sql4ZHinfo)){
					sql4ZHinfo+=" where id=?";
					stmt4ywb = con.prepareStatement(sql4ZHinfo);
					stmt4ywb.setString(1, recordIdNum);
					ResultSet rs4ywb = stmt4ywb.executeQuery();
					if (rs4ywb.next()) {
						json4zhparam.put("zhid", tablename + rs4ywb.getInt("id"));
						json4zhparam.put("xlbh", rs4ywb.getString("xlbh"));
						json4zhparam.put("qszh", rs4ywb.getDouble("zh1"));
						json4zhparam.put("jzzh", rs4ywb.getDouble("zh2"));
						json4zhparam.put("zh3", rs4ywb.getString("zh3"));
						json4zhparam.put("id", recordIdNum);
						json4zhparam.put("tablename", tablename);
					}
					JSONArray zharray= new JSONArray();;
					if(json4zhparam.getString("zh3")!=null&&json4zhparam.getString("zh3").toString()!=""){
						String zh3= json4zhparam.getString("zh3").toString();
						zharray= getZHinfo(zh3);
						zharray=hebingzh(json4zhparam,zharray);
					}else{
						JSONObject jsonone=new JSONObject();
						jsonone.put("0", json4zhparam.get("qszh"));
						jsonone.put("1", json4zhparam.get("jzzh"));
						zharray.add(jsonone);
					}
					
					for(int i=0;i<zharray.size();i++){
						JSONObject jsonzh=(JSONObject)zharray.get(i);
						JSONObject jsonParam = new JSONObject();
						jsonParam.put("id", recordIdNum);
						jsonParam.put("tablename", tablename);
						jsonParam.put("zhid", json4zhparam.get("zhid"));
						jsonParam.put("xlbh", json4zhparam.get("xlbh"));
						jsonParam.put("qszh", jsonzh.get("0"));
						jsonParam.put("jzzh", jsonzh.get("1"));
						getGeometryFromOracle(jsonGeoArray, jsonParam, con);
					}
				}
			}
			json.put("geometry4all", jsonGeoArray);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DBHelper.closeStatement(stmt4ywb);
			DBHelper.closeStatement(stmt);
			DBHelper.closeConnection(con);
		}
		json.put("r", true);
		return json;
	}
	//一次性获取全部桩号位置
	public JSONObject getGeometry4AllZH(String tname) throws Exception {
		JSONObject json = new JSONObject();
		Connection con = null;
		PreparedStatement stmt4ywb = null;

		JSONObject json4zhparam = new JSONObject();

		ArrayList<JSONObject> jsonGeoArray=new ArrayList<JSONObject>();
		
		int j = 0;
		json.put("r", false);
		if (ClientWebService4LineRef.getInstance().getFlag() == 1) {
			try {
				con = DBHelper.getConnection();
				String  sql4ZHinfo=getZHField(tname,con);
				if(StringUtils.isNotEmpty(sql4ZHinfo)){
					sql4ZHinfo+=" where valid=1 and sftj=1 order by tjsj";
					stmt4ywb = con.prepareStatement(sql4ZHinfo);
					
					ResultSet rs4ywb = stmt4ywb.executeQuery();
					while (rs4ywb.next()) {
						j++;
						json4zhparam.put("zhid", tname + rs4ywb.getInt("id"));
						json4zhparam.put("id",  rs4ywb.getInt("id"));
						json4zhparam.put("xlbh", rs4ywb.getString("xlbh"));
						json4zhparam.put("qszh", rs4ywb.getDouble("zh1"));
						json4zhparam.put("jzzh", rs4ywb.getDouble("zh2"));
						json4zhparam.put("zh3", rs4ywb.getString("zh3"));
						json4zhparam.put("tablename", tname);
						
						JSONArray zharray= new JSONArray();;
						if(json4zhparam.getString("zh3")!=null&&json4zhparam.getString("zh3").toString()!=""){
							String zh3= json4zhparam.getString("zh3").toString();
							zharray= getZHinfo(zh3);
							zharray=hebingzh(json4zhparam,zharray);
						}else{
							JSONObject jsonone=new JSONObject();
							jsonone.put("0", json4zhparam.get("qszh"));
							jsonone.put("1", json4zhparam.get("jzzh"));
							zharray.add(jsonone);
						}
						
						for(int i=0;i<zharray.size();i++){
							JSONObject jsonzh=(JSONObject)zharray.get(i);
							JSONObject jsonParam = new JSONObject();
							jsonParam.put("tablename", tname);
							jsonParam.put("zhid", json4zhparam.get("zhid"));
							jsonParam.put("xlbh", json4zhparam.get("xlbh"));
							jsonParam.put("id", json4zhparam.get("id"));
							jsonParam.put("qszh", jsonzh.get("0"));
							jsonParam.put("jzzh", jsonzh.get("1"));
							getGeometryFromOracle(jsonGeoArray, jsonParam, con);
						}
					}
				}
				json.put("geometry4all", jsonGeoArray);
				json.put("r", true);
			} finally {
				DBHelper.closeStatement(stmt4ywb);
				DBHelper.closeConnection(con);
			}
		}
		return json;
	}
	
	public String getZHField(String table_name,Connection con) throws SQLException{
		String sql4ZHinfo="select id,xlbh,";
		ArrayList<String> field_list=new ArrayList<String>();
		String sql4field="select table_filed from ywb_tab_field where table_name=? and zh=1";
		PreparedStatement pstmt=null;
		try{
			pstmt=con.prepareStatement(sql4field);
			pstmt.setString(1, table_name);
			ResultSet rs4field=pstmt.executeQuery();
			while(rs4field.next()){
				field_list.add(rs4field.getString("table_filed"));
			}
			if(field_list.size()!=0){
				for(int i=0;i<field_list.size();i++){
					if("zh1".equals(field_list.get(i))||"ZH1".equals(field_list.get(i))){
						sql4ZHinfo+="to_number(replace(replace(replace(replace("+field_list.get(i)+",'K',''),'k',''),'+',''),'--','')) zh1 ";
					}else if("zh2".equals(field_list.get(i))||"ZH2".equals(field_list.get(i))){
						sql4ZHinfo+="to_number(replace(replace(replace(replace("+field_list.get(i)+",'K',''),'k',''),'+',''),'--','')) zh2 ";
					}else {
						sql4ZHinfo+= field_list.get(i)+" zh3 ";
					}
					if(i!=field_list.size()-1){
						sql4ZHinfo+=",";
					}
				}
			}else{
				return "";
			}
			sql4ZHinfo+="from "+ table_name + " ";
		}finally{
			DBHelper.closeStatement(pstmt);
		}
		return sql4ZHinfo;
	}
//=======
//		JSONObject json = new JSONObject();
//		JSONObject json4zhparam = new JSONObject();
//		ArrayList<JSONObject> jsonGeoArray = new ArrayList<JSONObject>();
//
//		json4zhparam.put("id", recordIdNum);
//		json4zhparam.put("tablename", tablename);
//		json.put("r", false);
//		try {
//			con = DBHelper.getConnection();
//			stmt = con.prepareStatement("select geometry from SYS_EXT_GEOMETRY where record_id=?");
//			stmt.setString(1, recordId);
//			ResultSet rs = stmt.executeQuery();
//			if (rs.next())
//				json.put("geometry", Convert.bytes2Str(rs.getBytes(1)));
//			else
//				json.put("geometry", "[]");
//			
//			if (ClientWebService4LineRef.getInstance().getFlag() == 1) {
//				String  sql4ZHinfo=getZHField(tablename,con);
//				sql4ZHinfo+=" where id=?";
//				stmt4ywb = con.prepareStatement(sql4ZHinfo);
//				stmt4ywb.setString(1, recordIdNum);
//				ResultSet rs4ywb = stmt4ywb.executeQuery();
//				if (rs4ywb.next()) {
//					json4zhparam.put("zhid", tablename + rs4ywb.getInt("id"));
//					json4zhparam.put("xlbh", rs4ywb.getString("xlbh"));
//					json4zhparam.put("qszh", rs4ywb.getDouble("zh1"));
//					json4zhparam.put("jzzh", rs4ywb.getDouble("zh2"));
//					json4zhparam.put("zh3", rs4ywb.getString("zh3"));
//					json4zhparam.put("id", recordIdNum);
//					json4zhparam.put("tablename", tablename);
//				}
//				JSONArray zharray= new JSONArray();;
//				if(json4zhparam.getString("zh3")!=null&&json4zhparam.getString("zh3").toString()!=""){
//					String zh3= json4zhparam.getString("zh3").toString();
//					zharray= getZHinfo(zh3);
//					zharray=hebingzh(json4zhparam,zharray);
//				}else{
//					JSONObject jsonone=new JSONObject();
//					jsonone.put("0", json4zhparam.get("qszh"));
//					jsonone.put("1", json4zhparam.get("jzzh"));
//					zharray.add(jsonone);
//				}
//				
//				for(int i=0;i<zharray.size();i++){
//					JSONObject jsonzh=(JSONObject)zharray.get(i);
//					JSONObject jsonParam = new JSONObject();
//					jsonParam.put("id", recordIdNum);
//					jsonParam.put("tablename", tablename);
//					jsonParam.put("zhid", json4zhparam.get("zhid"));
//					jsonParam.put("xlbh", json4zhparam.get("xlbh"));
//					jsonParam.put("qszh", jsonzh.get("0"));
//					jsonParam.put("jzzh", jsonzh.get("1"));
//					getGeometryFromOracle(jsonGeoArray, jsonParam, con);
//				}
//			}
//			json.put("geometry4all", jsonGeoArray);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		} finally {
//			DBHelper.closeStatement(stmt4ywb);
//			DBHelper.closeStatement(stmt);
//			DBHelper.closeConnection(con);
//		}
//		json.put("r", true);
//		return json;
//	}
//	//一次性获取全部桩号位置
//	public JSONObject getGeometry4AllZH(String tname) throws Exception {
//		JSONObject json = new JSONObject();
//		Connection con = null;
//		PreparedStatement stmt4ywb = null;
//
//		JSONObject json4zhparam = new JSONObject();
//
//		ArrayList<JSONObject> jsonGeoArray=new ArrayList<JSONObject>();
//		
//		int j = 0;
//		json.put("r", false);
//		if (ClientWebService4LineRef.getInstance().getFlag() == 1) {
//			try {
//				con = DBHelper.getConnection();
//				String  sql4ZHinfo=getZHField(tname,con);
//				sql4ZHinfo+=" where valid=1 and sftj=1 order by tjsj";
//				stmt4ywb = con.prepareStatement(sql4ZHinfo);
//				
//				ResultSet rs4ywb = stmt4ywb.executeQuery();
//				while (rs4ywb.next()) {
//					j++;
//					json4zhparam.put("zhid", tname + rs4ywb.getInt("id"));
//					json4zhparam.put("id",  rs4ywb.getInt("id"));
//					json4zhparam.put("xlbh", rs4ywb.getString("xlbh"));
//					json4zhparam.put("qszh", rs4ywb.getDouble("zh1"));
//					json4zhparam.put("jzzh", rs4ywb.getDouble("zh2"));
//					json4zhparam.put("zh3", rs4ywb.getString("zh3"));
//					json4zhparam.put("tablename", tname);
//					
//					JSONArray zharray= new JSONArray();;
//					if(json4zhparam.getString("zh3")!=null&&json4zhparam.getString("zh3").toString()!=""){
//						String zh3= json4zhparam.getString("zh3").toString();
//						zharray= getZHinfo(zh3);
//						zharray=hebingzh(json4zhparam,zharray);
//					}else{
//						JSONObject jsonone=new JSONObject();
//						jsonone.put("0", json4zhparam.get("qszh"));
//						jsonone.put("1", json4zhparam.get("jzzh"));
//						zharray.add(jsonone);
//					}
//					
//					for(int i=0;i<zharray.size();i++){
//						JSONObject jsonzh=(JSONObject)zharray.get(i);
//						JSONObject jsonParam = new JSONObject();
//						jsonParam.put("tablename", tname);
//						jsonParam.put("zhid", json4zhparam.get("zhid"));
//						jsonParam.put("xlbh", json4zhparam.get("xlbh"));
//						jsonParam.put("id", json4zhparam.get("id"));
//						jsonParam.put("qszh", jsonzh.get("0"));
//						jsonParam.put("jzzh", jsonzh.get("1"));
//						getGeometryFromOracle(jsonGeoArray, jsonParam, con);
//					}
//				}
//				System.out.println("j=" + j);
//				json.put("geometry4all", jsonGeoArray);
//				json.put("r", true);
//			} finally {
//				DBHelper.closeStatement(stmt4ywb);
//				DBHelper.closeConnection(con);
//			}
//		}
//		return json;
//	}
//	
//	public String getZHField(String table_name,Connection con) throws SQLException{
//		String sql4ZHinfo="select id,xlbh,";
//		ArrayList<String> field_list=new ArrayList<String>();
//		String sql4field="select table_filed from ywb_tab_field where table_name=? and zh=1";
//		PreparedStatement pstmt=null;
//		try{
//			pstmt=con.prepareStatement(sql4field);
//			pstmt.setString(1, table_name);
//			ResultSet rs4field=pstmt.executeQuery();
//			while(rs4field.next()){
//				field_list.add(rs4field.getString("table_filed"));
//			}
//			for(int i=0;i<field_list.size();i++){
//				if("zh1".equals(field_list.get(i))||"ZH1".equals(field_list.get(i))){
//					sql4ZHinfo+="to_number(replace(replace(replace(replace("+field_list.get(i)+",'K',''),'k',''),'+',''),'--','')) zh1 ";
//				}else if("zh2".equals(field_list.get(i))||"ZH2".equals(field_list.get(i))){
//					sql4ZHinfo+="to_number(replace(replace(replace(replace("+field_list.get(i)+",'K',''),'k',''),'+',''),'--','')) zh2 ";
//				}else {
//					sql4ZHinfo+= field_list.get(i)+" zh3 ";
//				}
//				if(i!=field_list.size()-1){
//					sql4ZHinfo+=",";
//				}
//			}
//			sql4ZHinfo+="from "+ table_name + " ";
//		}finally{
//			DBHelper.closeStatement(pstmt);
//		}
//		return sql4ZHinfo;
//	}
//>>>>>>> .r149
}
