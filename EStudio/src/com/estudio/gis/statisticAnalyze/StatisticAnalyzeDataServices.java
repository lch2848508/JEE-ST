package com.estudio.gis.statisticAnalyze;

import com.estudio.gis.oracle.WebGISResourceService4Oracle;
import org.apache.commons.lang3.StringUtils;
import net.minidev.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONObject;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.Convert;
import com.estudio.intf.db.IDBHelper;
import com.estudio.context.RuntimeContext;
import java.sql.Connection;
import java.sql.*;
import java.io.*;
import net.minidev.json.JSONObject;

public class StatisticAnalyzeDataServices {
	public String getStatisticTreeJSON(long id) throws Exception {
		return WebGISResourceService4Oracle.getInstance()
				.getStatisticTreePoint(id).toJSONString();
	}
	public String getStatisticDataJSON(long id) throws Exception {
		return WebGISResourceService4Oracle.getInstance()
				.getStatisticData(id).toJSONString();
	}
	public JSONObject getSubjectParam(String selectID) throws Exception {
		IDBHelper dbHelper = RuntimeContext.getDbHelper();
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		try {
			con = dbHelper.getConnection();
			String str = "select * from TAB_STATISTIC_SUBJECT_EX where id="
					+ selectID;
			stmt = con.prepareStatement(str);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				json.put("ID", rs.getInt("id"));
				json.put("SubjectName", rs.getString("name"));
				json.put("ChartType", rs.getString("chartType"));
				json.put("firstIndex", rs.getString("index_first"));
				json.put("secondIndex", rs.getString("index_second"));
				json.put("thirdIndex", rs.getString("index_third"));
				json.put("fourthIndex", rs.getString("index_fourth"));
				json.put("fifthIndex", rs.getString("index_fifth"));
				json.put("xfirstIndex", rs.getString("xindex_first"));
				json.put("xsecondIndex", rs.getString("xindex_second"));
				json.put("xthirdIndex", rs.getString("xindex_third"));
				json.put("xfourthIndex", rs.getString("xindex_fourth"));
				json.put("xfifthIndex", rs.getString("xindex_fifth"));
			}
		} catch (Exception e) {
			ExceptionUtils.printExceptionTrace(e);
		} finally {
			dbHelper.closeStatement(stmt);
			dbHelper.closeConnection(con);
		}
		return json;
	}
	public JSONArray SubjectListDataService(String Dataid) throws Exception{
		IDBHelper dbHelper = RuntimeContext.getDbHelper();
		Connection con = null;
		PreparedStatement stmt = null;
		JSONArray array = new JSONArray();
		int i = 0;
		try {
			con = dbHelper.getConnection();
			String str = "select * from TAB_STATISTIC_SUBJECT_EX where data_id="
					+ Dataid;
			stmt = con.prepareStatement(str);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject json = new JSONObject();
				JSONArray GridData = new JSONArray();
				GridData.add(rs.getString("name"));
				json.put("id", rs.getInt("id"));
				json.put("data", GridData);
				array.add(i, json);
				i++;

			}
		} catch (Exception e) {
			ExceptionUtils.printExceptionTrace(e);
		} finally {
			dbHelper.closeStatement(stmt);
			dbHelper.closeConnection(con);
		}
		return array;
	}
	public void RemoveSubjectEvent(String selectID)throws Exception{
		IDBHelper dbHelper = RuntimeContext.getDbHelper();
		Connection con = null;
		Statement stmt = null;
		try
		{
			con = dbHelper.getConnection();
			String str = "delete TAB_STATISTIC_SUBJECT_EX where id="+selectID;
			String resort = "update TAB_STATISTIC_SUBJECT_EX set id =rownum";
	        stmt=con.createStatement();
	        stmt.executeUpdate(str);
	        stmt.execute(resort);
		} catch (Exception e) {
			ExceptionUtils.printExceptionTrace(e);
	    } finally{
			dbHelper.closeStatement(stmt);
			dbHelper.closeConnection(con);
		}
	}
	public JSONArray SubjectCount() throws Exception{
		IDBHelper dbHelper = RuntimeContext.getDbHelper();
		Connection con = null;
		PreparedStatement stmt = null;
		JSONArray array = new JSONArray();
		int i=0;
		
		try
		{
			con = dbHelper.getConnection();
	        stmt = con.prepareStatement("select * from TAB_STATISTIC_SUBJECT_EX where id<10000");
	        ResultSet rs = stmt.executeQuery();
	        while(rs.next()) {
				JSONObject json = new JSONObject();
	        	json.put("ID", rs.getInt("id"));
		        json.put("SubjectName", rs.getString("name"));
		        json.put("ChartType", rs.getString("chartType"));
		        array.add(i, json);
		        i++;
	        }
		} catch (Exception e) {
			ExceptionUtils.printExceptionTrace(e);
	    } finally{
			dbHelper.closeStatement(stmt);
			dbHelper.closeConnection(con);
		}
		return array;
	}
	public JSONObject SubjectJudge(String subjectname)throws Exception{
		IDBHelper dbHelper = RuntimeContext.getDbHelper();
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject json = new JSONObject();
		
		try
		{
			con = dbHelper.getConnection();
			String str = "select * from TAB_STATISTIC_SUBJECT_EX where name='"+subjectname+"'";
	        stmt = con.prepareStatement(str);
	        ResultSet rs = stmt.executeQuery();
	        while(rs.next()) {
	        	json.put("SubjectName", rs.getString("name"));
		        json.put("ChartType", rs.getString("chartType"));
	        }
		} catch (Exception e) {
			ExceptionUtils.printExceptionTrace(e);
	    } finally{
			dbHelper.closeStatement(stmt);
			dbHelper.closeConnection(con);
		}
		return json;
	}
	public void SubjectSave2OracleTable(String subjectid,String subjectname,String charttype,String DataID,String firstIndex,String secondIndex,String thirdIndex,String fourthIndex,String fifthIndex,String xfirstIndex,String xsecondIndex,String xthirdIndex,String xfourthIndex,String xfifthIndex)throws Exception{
		IDBHelper dbHelper = RuntimeContext.getDbHelper();
		Connection con = null;
		Statement stmt = null;
		try
		{
			con = dbHelper.getConnection();
			stmt=con.createStatement();
			String insertSQL = "insert into TAB_STATISTIC_SUBJECT_EX(id,name,chartType,data_id,index_first,index_second,index_third,index_fourth,index_fifth,xindex_first,xindex_second,xindex_third,xindex_fourth,xindex_fifth) values("+subjectid+",'"+subjectname+"','"+charttype+"',"+DataID+",'"+firstIndex+"','"+secondIndex+"','"+thirdIndex+"','"+fourthIndex+"','"+fifthIndex+"','"+xfirstIndex+"','"+xsecondIndex+"','"+xthirdIndex+"','"+xfourthIndex+"','"+xfifthIndex+"')";
			stmt.execute(insertSQL);
		} catch (Exception e) {
			ExceptionUtils.printExceptionTrace(e);
	    } finally{
			dbHelper.closeStatement(stmt);
			dbHelper.closeConnection(con);
		}
	}
    private StatisticAnalyzeDataServices() {

    }

    private static StatisticAnalyzeDataServices instance = new StatisticAnalyzeDataServices();

    public static StatisticAnalyzeDataServices getInstance() {
        return instance;
    }
}
