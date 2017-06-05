package com.estudio.gis.service;

import java.io.File;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.impl.service.sercure.ClientWebService4LineRef;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.StringUtilsLocal;
import com.estudio.utils.ThreadUtils;

public class DynamicSpecialLayerService {

    public final static DynamicSpecialLayerService instance = new DynamicSpecialLayerService();
    private final IDBHelper DBHelper = RuntimeContext.getDbHelper();
//    private static IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    public void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                	executeImportSDELayerAndTable();
                    executeImportSpecialLayerService();
                    executeGenerateMilege();
                    executeDeleteSpecialLayerService();
                    ThreadUtils.sleepMinute(5);
                }
            }
        }).start();
    }
    
    protected void executeImportSDELayerAndTable(){
    	 Connection con = null;
    	 PreparedStatement stmt = null;
    	 String sdeServerName = ClientWebService4LineRef.getInstance().getUser();
         try {
             con = DBHelper.getNativeConnection();
             String sqlField="select a.TABLE_NAME,b.column_name,b.comments from dba_tab_columns a,dba_col_comments b where a.COLUMN_NAME=b.column_name and a.OWNER='"+sdeServerName.toUpperCase()+"' " +
             		" and a.TABLE_NAME=b.table_name " +
             		"and (a.TABLE_NAME like 'GD\\_%' escape '\\'" +
             		" or a.TABLE_NAME like 'GS\\_%' escape '\\' or a.TABLE_NAME like 'SD\\_%' escape '\\' "+
             		" or a.TABLE_NAME like 'XD\\_%'  escape '\\' "+
					" or a.TABLE_NAME like 'YD\\_%'  escape '\\') ";
             JSONArray resultLayerAndField=DBHelper.executeQuery(sqlField, con);
             String sqlLayer="select distinct a.TABLE_NAME from dba_tab_columns a where  a.OWNER='"+sdeServerName.toUpperCase()+"' and (a.TABLE_NAME like 'GD\\_%' escape '\\' "+
							" or a.TABLE_NAME like 'GS\\_%' escape '\\' "+
							" or a.TABLE_NAME like 'SD\\_%' escape '\\' "+
							" or a.TABLE_NAME like 'XD\\_%'  escape '\\'"+
							" or a.TABLE_NAME like 'YD\\_%'  escape '\\')";
             JSONArray resultLayer=DBHelper.executeQuery(sqlLayer, con);
             DBHelper.execute("delete from webgis_dynamic_route_layer", con);
             DBHelper.execute("delete from webgis_dynamic_route_field", con);
             String  insertLayer="insert into webgis_dynamic_route_layer (id, caption, is_valid, sortorder) values (?, ? , 1, ?)";
             String insertField="insert into webgis_dynamic_route_field (id, p_id, field_name, field_comment,IS_XLBH) values (?, ?, ?, ?,?)";
             int pri_Layer_id=1;
             int pri_Field_id=1;
             for(int i=0;i<resultLayer.size();i++){
            	 JSONObject layer= resultLayer.getJSONObject(i);
            	 String layerName=layer.getString("TABLE_NAME");
            	 stmt=con.prepareStatement(insertLayer);
            	 stmt.setInt(1, pri_Layer_id);
            	 stmt.setString(2, layer.getString("TABLE_NAME"));
            	 stmt.setInt(3, pri_Layer_id);
            	 stmt.execute();
            	  for(int j=0;j<resultLayerAndField.size();j++){
            		 JSONObject field= resultLayerAndField.getJSONObject(j);
            		 String fieldLayerName= field.getString("TABLE_NAME");
            		 if(layerName.equals(fieldLayerName)){
            			 stmt=con.prepareStatement(insertField);;
    		        	 stmt.setInt(1, pri_Field_id);
    		        	 stmt.setInt(2, pri_Layer_id);
    		        	 String fieldComments= field.getString("COMMENTS");
    		        	 if(fieldComments==null||fieldComments.equals("")){
    		        		 fieldComments=field.getString("COLUMN_NAME");
    		        	 }
    		        	 stmt.setString(3, field.getString("COLUMN_NAME"));
    		        	 stmt.setString(4, fieldComments);
    		        	 stmt.setInt(5, field.getString("COLUMN_NAME").equals("XLBH")?1:0);
    		        	 stmt.execute();
    		        	 pri_Field_id++;
            		 }
            	  }
            	  pri_Layer_id++;
            	 
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             DBHelper.closeStatement(stmt);
             DBHelper.closeConnection(con);
         }
    }

    /**
     * 删除没用的图层
     */
    protected void executeDeleteSpecialLayerService() {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement exeStmt = null;
        try {
            con = DBHelper.getNativeConnection();
            exeStmt = con.createStatement();
            exeStmt.execute("update webgis_dynamic_service set is_valid=0 where p_id in (select id from WEBGIS_DYNAMIC_SERVER_CATEGORY start with is_valid=0 connect by prior id = p_id)");
            stmt = con.prepareStatement("select * from webgis_dynamic_service where is_valid=0");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                RuntimeContext.getAttachmentService().deleteFile(con, id);
                String tableName = rs.getString("schema_table_name");
                if (StringUtils.isEmpty(tableName))
                    continue;
                if (exeStmt.executeQuery("select 'x' from user_objects where object_name='" + tableName + "'").next())
                    exeStmt.execute("drop table " + tableName + " PURGE");
                if (exeStmt.executeQuery("select 'x' from user_objects where object_name='VIEW_" + tableName + "'").next())
                    exeStmt.execute("drop view VIEW_" + tableName);
            }
            exeStmt.execute("delete from WEBGIS_DYNAMIC_SERVER_CATEGORY where id in (select id from WEBGIS_DYNAMIC_SERVER_CATEGORY start with is_valid=0 connect by prior id = p_id)");
            exeStmt.execute("delete from webgis_dynamic_service where is_valid=0");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeStatement(exeStmt);
            DBHelper.closeStatement(stmt);
            DBHelper.closeConnection(con);
        }
    }

    /**
     * 
     */
    protected void executeImportSpecialLayerService() {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement exeStmt = null;
        long id = -1l;
        try {
            con = DBHelper.getNativeConnection();
            stmt = con.prepareStatement("select id,caption,server_type,schema_table_name from webgis_dynamic_service where is_valid=1 and is_post=1 and is_complete=0 order by sortorder");
            exeStmt = con.createStatement();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                id = rs.getLong(1);
                int serverType = rs.getInt(3);
                List<JSONObject> attachmentList = RuntimeContext.getAttachmentService().listFiles(con, "Attachment", id + "");
                String schemaTableName = rs.getString(4);
                String caption = rs.getString(2);
                if(serverType == 1|| serverType == 0){
	                if (StringUtils.isEmpty(schemaTableName))
	                    schemaTableName = "TMP_GIS_DY_LAYER_" + rs.getLong(1);
	                // 删除没用的数据库表
	                if (exeStmt.executeQuery("select 1 from user_tables where table_name = '" + schemaTableName + "'").next())
	                    exeStmt.execute("drop table " + schemaTableName);
	                if (exeStmt.executeQuery("select * from user_objects where object_name='VIEW_" + schemaTableName + "'").next())
	                    exeStmt.execute("drop view VIEW_" + schemaTableName);
	
	                if (serverType == 0)
	                    executeExcelService(con, exeStmt, id, caption, schemaTableName, attachmentList);
	                else if (serverType == 1)
	                    executeShapeService(id, con, exeStmt, schemaTableName, attachmentList, caption);
                }else if(serverType == 2){
                	executeTableService(id, con, exeStmt, schemaTableName, attachmentList, caption);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeStatement(exeStmt);
            DBHelper.closeStatement(stmt);
            DBHelper.closeConnection(con);
        }
    }
    /**
     * 从处理数据库Table表中的数据
     * @param id
     * @param con
     * @param exeStmt
     * @param schemaTableName
     * @param attachmentList
     * @param caption
     * @throws SQLException 
     */
    
    private void executeTableService(long id, Connection con,Statement exeStmt, String schemaTableName,List<JSONObject> attachmentList, String caption) throws SQLException {
		// TODO Auto-generated method stub  seq_for_j2ee_webgis.nextval
    	exeStmt.execute("delete from webgis_dynamic_field t where t.p_id="+id);
    	PreparedStatement stmt = null;
    	try{
    		String sql="insert into webgis_dynamic_field (id, p_id, field_name, field_comment, schema_field_name, is_visible, data_type, sortorder)"+
    				"select seq_for_j2ee_webgis.nextval id,"+id+"  p_id,a.column_name, a.comments, a.column_name,1 is_visible, data_type , seq_for_j2ee_webgis.currval sortorder"+
    		       " from user_col_comments a, user_tab_cols b where a.column_name = b.COLUMN_NAME and b.TABLE_NAME = upper('"+schemaTableName+"') and a.table_name = upper('"+schemaTableName+"')";
    		stmt=con.prepareStatement(sql);
    		int isok= stmt.executeUpdate();
    		if(isok!=0){
    			exeStmt.execute("update webgis_dynamic_service set status=2,is_complete=1 where id=" + id);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		DBHelper.closeStatement(stmt);
    	}
	}
	/**
     * 检测Excel数据是否存在需要处理的桩号信息 有的话生成桩号
     */
	private void executeGenerateMilege() {
		// TODO Auto-generated method stub
		if(ClientWebService4LineRef.getInstance()==null){
			return;
		}
		Connection con=null;
		PreparedStatement stmt=null;
		PreparedStatement stmt2=null;
		try{
		    con = DBHelper.getNativeConnection();
		    stmt = con.prepareStatement("select id,caption,server_type,schema_table_name from webgis_dynamic_service where is_valid=1 and is_post=1 and is_complete=1 and zh_status in (0,1) order by sortorder");
		    stmt2=con.prepareStatement("select t.field_name,t.zh_type from webgis_dynamic_field t where t.p_id=? and t.zh_type in (1,2,3)");
		    //zh_type 为1 路线编号 2 起始桩号 3 截止桩号     serviceType 0:Excel 1:Shape 2:Table
		    ResultSet rs=stmt.executeQuery();
		    while(rs.next()){
		    	int serviceType=rs.getInt("server_type");
		    	if(serviceType==0||serviceType==2){
		    		int serviceId=rs.getInt("id");
		    		String schema_table_name=rs.getString("schema_table_name");
		    		stmt2.setInt(1, serviceId);
		    		ResultSet rs2=stmt2.executeQuery();
		    		String lxbhField="";
		    		String qszhField="";
		    		String jzzhField="";
		    		while(rs2.next()){
		    			int zhlx=rs2.getInt("zh_type");
		    			if(zhlx==1){
		    				lxbhField=rs2.getString("field_name");
		    			}else if(zhlx==2){
		    				qszhField=rs2.getString("field_name");
		    			}else if(zhlx==3){
		    				jzzhField=rs2.getString("field_name");
		    			}
		    		}
		    		//存在桩号数据的Excel文件
		    		if(serviceType==0&&StringUtils.isNotEmpty(lxbhField)&&StringUtils.isNotEmpty(qszhField)&&StringUtils.isNotEmpty(jzzhField)){
		    			DBHelper.execute("update webgis_dynamic_service t set t.zh_status=1 where t.id="+serviceId,con);
		    			//获取与Excel文件关联的Route信息
		    			JSONArray routeLayerLink= getWebgis_Dynamic_Link(con,serviceId);
		    			if(routeLayerLink.size()>0){
		    				getMileage4Excel(con,serviceId,lxbhField,qszhField,jzzhField,routeLayerLink);
		    				
		    				DBHelper.execute("update webgis_dynamic_service t set t.zh_status=2 where t.id="+serviceId,con);
			    			createViewMileage4Excel(con,serviceId);
		    			}
		    		
		    		}
		    		//存在桩号的数据库表
		    		if(serviceType==2&&StringUtils.isNotEmpty(lxbhField)&&StringUtils.isNotEmpty(qszhField)&&StringUtils.isNotEmpty(jzzhField)){
		    			DBHelper.execute("update webgis_dynamic_service t set t.zh_status=1 where t.id="+serviceId,con);
		    			getMileage4Table(con,serviceId,schema_table_name,lxbhField,qszhField,jzzhField);
		    		}
		    	}
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBHelper.closeStatement(stmt2);
            DBHelper.closeStatement(stmt);
            DBHelper.closeConnection(con);
		}
	}
	
	private void createViewMileage4Excel(Connection con,int serviceId) throws Exception {
		// TODO Auto-generated method stub
		String sql4judge="select count(1) isexist from user_tab_columns t where t.TABLE_NAME=upper('TMP_GIS_DY_LAYER_"+serviceId+"') and t.COLUMN_NAME=upper('PRIMARY_ID')";
    	JSONArray resultjudge=DBHelper.executeQuery(sql4judge, con);
    	JSONObject obj=resultjudge.getJSONObject(0);
    	String tableName= "TMP_GIS_DY_LAYER_"+serviceId;
		String viewName= "VIEW_TMP_GIS_DY_LAYER_"+serviceId;
    	String sqlview="create or replace view "+viewName+" as select ";
		if("0".equals(obj.getString("ISEXIST"))){
			sqlview+="t.id PRIMARY_ID,";
		}
		sqlview+="t.* from "+tableName+" t";
		DBHelper.execute(sqlview,con);
	}

	private JSONArray getWebgis_Dynamic_Link(Connection con,int serviceId) throws Exception{
		String sql="select layer_name,layer_field_name from WEBGIS_DYNAMIC_LINK a where type=2 and a.misservice_id="+serviceId;
//				"select layer_name,b.field_name from WEBGIS_DYNAMIC_LINK a,webgis_dynamic_route_field b where a.misservice_id="+serviceId+" and a.layer_field_id=b.id and type=2";
		JSONArray jsonarray=DBHelper.executeQuery(sql, con);
		return jsonarray;
	} 
	
	/**
	 * 获取Table表中需要处理的到桩号
	 * @param con
	 * @param serviceId
	 * @param lxbhField
	 * @param qszhField
	 * @param jzzhField
	 * @throws Exception
	 */
	private void getMileage4Table(Connection con,int serviceId,String schema_table_name, String lxbhField, String qszhField,String jzzhField)throws Exception{
		String sdeServerName = ClientWebService4LineRef.getInstance().getUser();
		String sql4selExcel="select t.id,t."+lxbhField+" xlbh,t."+qszhField+" qszh,t."+jzzhField+" jzzh from "+schema_table_name+" t where t.VALID=1 and t.sftj=1 and t."+lxbhField+" is not null and t."+qszhField+" is not null and t."+jzzhField+" is not null";
		JSONArray resultseldata=DBHelper.executeQuery(sql4selExcel, con);
		
		getMileage(con,sdeServerName,resultseldata,schema_table_name);
		DBHelper.execute("update webgis_dynamic_service t set t.zh_status=2 where t.id="+serviceId,con);
		
		String sql4view="create or replace view view_TMP_GIS_DY_LAYER_"+serviceId+" as select a.idfornum PRIMARY_ID,a.linegeo geometry,b.* from GLJ_LINEREF a,"+schema_table_name+" b where a.idfornum=b.id";
		DBHelper.execute(sql4view,con);
	}
	
	/**
	 * 获取桩号图斑
	 * @param con
	 * @param sdeServerName sde数据局名
	 * @param resultseldata 关联表数据
	 * @param schema_table_name 数据库表名
	 * @throws Exception
	 */
	private void getMileage(Connection con,String sdeServerName,JSONArray resultseldata,String schema_table_name,String LINEREFBASE,JSONArray routeLayerLink) throws Exception{
		if(resultseldata.size()!=0){
    		for(int i=0;i<resultseldata.size();i++){
    			System.out.println("i="+i);
        		DBHelper.execute("delete from  " + sdeServerName + "."+LINEREFBASE,con);
    			double qszh=dealMileageString(resultseldata.getJSONObject(i).getString("QSZH"));
    			double jzzh=dealMileageString(resultseldata.getJSONObject(i).getString("JZZH"));
//    			if(qszh==-1||jzzh==-1){
//    				continue;
//    			}
    			qszh=qszh==-1?0.0:qszh;
    			jzzh=jzzh==-1?9999999999999.0:jzzh;
    			
    			String xlbh=resultseldata.getJSONObject(i).getString("XLBH");
    			xlbh=xlbh.replaceAll(" ", "");
//    			xlbh=xlbh.length()>4?xlbh.substring(0, 4):xlbh;
//    			System.out.println("XLBH"+xlbh+"qszh="+qszh+"jzzh="+jzzh);
    			String sqlins="insert into "+sdeServerName+"."+LINEREFBASE+" values(seq_for_j2ee_webgis.nextval,'"+xlbh+"',"+qszh+","+jzzh+")";
    			DBHelper.execute(sqlins,con);
    			String resultStr2 ="";
    			Object[] resultGetLayer=null;
    			for(int j=0;j<routeLayerLink.size();j++){
    				String sdeLayerName=sdeServerName.toUpperCase() +"."+routeLayerLink.getJSONObject(j).getString("LAYER_NAME");
    				String sdeLayerField=routeLayerLink.getJSONObject(j).getString("LAYER_FIELD_NAME");
    				
	    			resultGetLayer = ClientWebService4LineRef.getInstance().clentInvoke(sdeLayerName, sdeLayerField.toUpperCase(),LINEREFBASE, "XLBH", "QSZH", "JZZH","XLBH","");
	    			if(resultGetLayer==null){
	    				continue;
	    			}
	    			resultStr2 = resultGetLayer[0].toString();
	    			if (!resultStr2.contains("[]")) {
	    				break;
					}
    			}
//    			System.out.println(resultStr2);
    			if (resultStr2.contains("[]")||resultStr2=="") {
    				continue;
				} 
    			
    			PreparedStatement stmt=null;
    			try{
    				String sql="update "+schema_table_name+" set geometry = ? where id="+resultseldata.getJSONObject(i).getString("ID");
    				stmt=con.prepareStatement(sql);
    				net.sf.json.JSONArray jsonarray4sf=net.sf.json.JSONArray.fromObject(resultGetLayer);
    				net.sf.json.JSONObject json4sf=jsonarray4sf.getJSONObject(0);
//    				net.sf.json.JSONArray geoArrayStr= getSimplifyLine(json4sf.getString("geometries"));
//    				stmt.setBytes(1, Convert.str2Bytes(geoArrayStr.toString()));
    				stmt.setBytes(1, Convert.str2Bytes(json4sf.getString("geometries")));
    			    stmt.executeUpdate();
    			}finally{
    				DBHelper.closeStatement(stmt);
    			}
    		}
    	}
	}
	
	private net.sf.json.JSONArray getSimplifyLine(String geometry){
		net.sf.json.JSONArray geo_Obj1= net.sf.json.JSONArray.fromObject(geometry);
		net.sf.json.JSONArray geo_Obj1_ex=new net.sf.json.JSONArray();
		net.sf.json.JSONObject geo_Obj2=geo_Obj1.getJSONObject(0);
		String str_paths=geo_Obj2.getString("paths");
		net.sf.json.JSONArray geo_Obj3= net.sf.json.JSONArray.fromObject(str_paths);
		net.sf.json.JSONArray geo_Obj3_ex=new net.sf.json.JSONArray();
		int i2=0;
		for(int i=0;i<geo_Obj3.size();i++){
			net.sf.json.JSONArray geo_Obj4=geo_Obj3.getJSONArray(i);
//			net.sf.json.JSONArray geo_Obj4_ex=new net.sf.json.JSONArray();
			for(int k=0;k<geo_Obj4.size();k++){
				if(k%10==0){
					String xx= geo_Obj4.getString(k);
//					geo_Obj4_ex.add(xx);
					geo_Obj3_ex.add(xx);
				}
				i2++;
			}
//			geo_Obj3_ex.add(geo_Obj4_ex);
		}
		net.sf.json.JSONArray geo_Obj4_ex=new net.sf.json.JSONArray();
		geo_Obj4_ex.add(geo_Obj3_ex);
		geo_Obj2.put("paths", geo_Obj4_ex.toString());
		geo_Obj1_ex.add(geo_Obj2);
		return geo_Obj1_ex;
	}
	
	/**
	 * 获取桩号图斑
	 * @param con
	 * @param sdeServerName sde数据局名
	 * @param resultseldata 关联表数据
	 * @param schema_table_name 数据库表名
	 * @throws Exception
	 */
	private void getMileage(Connection con,String sdeServerName,JSONArray resultseldata,String schema_table_name) throws Exception{
		if(resultseldata.size()!=0){
    		for(int i=0;i<resultseldata.size();i++){
    			boolean isexist= isExistMileageInfo(con,schema_table_name,resultseldata.getJSONObject(i).getString("ID"));
    			if(!isexist){
	        		DBHelper.execute("delete from  " + sdeServerName + ".linerefbase",con);
	    			double qszh=dealMileageString(resultseldata.getJSONObject(i).getString("QSZH"));
	    			double jzzh=dealMileageString(resultseldata.getJSONObject(i).getString("JZZH"));
	    			if(qszh==-1||jzzh==-1){
	    				continue;
	    			}
	    			String sqlins="insert into "+sdeServerName+".LINEREFBASE values(seq_for_j2ee_webgis.nextval,'"+resultseldata.getJSONObject(i).getString("XLBH")+"',"+qszh+","+jzzh+")";
	    			DBHelper.execute(sqlins,con);
	    			String sdeLayerName=getSDELayerName(resultseldata.getJSONObject(i).getString("XLBH"));
	    			String sdeLayerField="XLBH";
	    			String LINEREFBASE="LINEREFBASE";
	    			Object[] resultGetLayer = ClientWebService4LineRef.getInstance().clentInvoke(sdeLayerName, sdeLayerField,LINEREFBASE, sdeLayerField, "QSZH", "JZZH",sdeLayerField,"");
	    			String resultStr2 = resultGetLayer[0].toString();
	    			if (resultStr2.contains("[]")) {
	    				continue;
					} 
	    			storageMileageInfo(con,resultseldata.getJSONObject(i),schema_table_name,qszh,jzzh,resultGetLayer);
    			}
    		}
    	}
	}
	private boolean isExistMileageInfo(Connection con,String schema_table_name,String id) throws SQLException{
		PreparedStatement stmt=null;
		String sql="select count(1) from glj_lineref where id=?";
		try{
			stmt=con.prepareStatement(sql);
			stmt.setString(1, schema_table_name+id);
			ResultSet rs= stmt.executeQuery();
			rs.next();
			int flag= rs.getInt(1);
			if(flag>0){
				return true;
			}
			return false;
		}finally{
			DBHelper.closeStatement(stmt);
		}
	}
	
	/**
	 * 存储图斑到glj_lineref
	 * @param con
	 * @param resultseldata
	 * @param schema_table_name
	 * @param qszh
	 * @param jzzh
	 * @param resultGetLayer
	 * @throws SQLException
	 */
	private void storageMileageInfo(Connection con,JSONObject resultseldata,String schema_table_name,double qszh,double jzzh,Object[] resultGetLayer) throws SQLException{
		PreparedStatement stmt=null;
		try{
			String sql="insert into glj_lineref (id, xlbh, qszh, jzzh, linegeo, idfornum, idfortable) values (?, ?, ?, ?, ?, ?, ?)";
			stmt=con.prepareStatement(sql);
			stmt.setString(1, schema_table_name+resultseldata.getString("ID"));
			stmt.setString(2, resultseldata.getString("XLBH"));
			stmt.setString(3, String.valueOf(qszh));
			stmt.setString(4, String.valueOf(jzzh));
			stmt.setBytes(5, Convert.str2Bytes(resultGetLayer[0].toString()));
			stmt.setString(6,resultseldata.getString("ID"));
			stmt.setString(7,schema_table_name);
		    stmt.executeUpdate();
		}finally{
			DBHelper.closeStatement(stmt);
		}
	}
	//存储桩号信息的Sde数据库表名
	private String lineRefBase= "LINEREFBASE";
	/**
	 * 跟局serviceId 获取数据存储嫑  根据线路编号 起始 终止桩号字段获取桩号信息
	 * @param con
	 * @param serviceId
	 * @param lxbhField
	 * @param qszhField
	 * @param jzzhField
	 * @throws Exception 
	 */
	 private void getMileage4Excel(Connection con,int serviceId, String lxbhField, String qszhField,String jzzhField,JSONArray routeLayerLink) throws Exception {
		// TODO Auto-generated method stub
    	//判断是否已存在 MDSYS_GEOMENTRY列
//    	String sql4judge="select count(1) isexist from user_tab_columns t where t.TABLE_NAME=upper('TMP_GIS_DY_LAYER_"+serviceId+"') and t.COLUMN_NAME=upper('the_geom') and t.DATA_TYPE=upper('sdo_geometry') and t.DATA_TYPE_OWNER=upper('mdsys')";
		String sql4judge="select count(1) isexist from user_tab_columns t where t.TABLE_NAME=upper('TMP_GIS_DY_LAYER_"+serviceId+"') and t.DATA_TYPE=upper('blob') and t.COLUMN_NAME=upper('geometry')";
    	JSONArray resultjudge=DBHelper.executeQuery(sql4judge, con);
    	JSONObject obj=resultjudge.getJSONObject(0);
		if("0".equals(obj.getString("ISEXIST"))){
			DBHelper.execute("alter table TMP_GIS_DY_LAYER_"+serviceId+" add geometry blob", con);
		}
		DBHelper.execute("update TMP_GIS_DY_LAYER_"+serviceId+" t set t.geometry = null", con);
		
//		String sql4selExcel="select t.id,t."+lxbhField+" xlbh,t."+qszhField+" qszh,t."+jzzhField+" jzzh from TMP_GIS_DY_LAYER_"+serviceId+" t where t."+lxbhField+" is not null and t."+qszhField+" is not null and t."+jzzhField+" is not null";
		String sql4selExcel="select t.id,t."+lxbhField+" xlbh,t."+qszhField+" qszh,t."+jzzhField+" jzzh from TMP_GIS_DY_LAYER_"+serviceId+" t where t."+lxbhField+" is not null";
		JSONArray resultseldata=DBHelper.executeQuery(sql4selExcel, con);
    	String sdeServerName = ClientWebService4LineRef.getInstance().getUser();
        String schema_table_name="TMP_GIS_DY_LAYER_"+serviceId;
    	
    	if(resultseldata.size()!=0){
    		String sql="delete from  " + sdeServerName + ".linerefbase";
    		DBHelper.execute(sql,con);
//    		for(int i=0;i<resultseldata.size();i++){
    			getMileage(con,sdeServerName,resultseldata,schema_table_name,lineRefBase,routeLayerLink);
//    		}
    	}
	}
    public String getSDELayerName(String xlbh){
    	String roadType = xlbh.substring(0, 1);
    	String sdeLayerName="";
    	if ("G".equals(roadType) || "g".equals(roadType)) { // 国道
			sdeLayerName = "SDE.国道ROUTE";
		} else if ("S".equals(roadType) || "s".equals(roadType)) {
			sdeLayerName = "SDE.省道ROUTE";
		} else if ("X".equals(roadType) || "x".equals(roadType)) {
			sdeLayerName = "SDE.县道ROUTE";
		} else {
			sdeLayerName = "SDE.高速2ROUTE";
		}
    	return sdeLayerName;
    }
    
    /**
     * 处理桩号信息
     * @param qszh
     * @return
     */
    public double dealMileageString(String qszh){
    	if(qszh==null||qszh==""){
    		return -1;
    	}
    	double qszhnum=0;
    	boolean haveK=false;
    	boolean havePlus=false;
    	if(qszh.contains("k")||qszh.contains("K")){
    		qszh=qszh.contains("k")?qszh.replaceAll("k", ""):qszh;
    		qszh=qszh.contains("K")?qszh.replaceAll("K", ""):qszh;
    		haveK=true;
    	}
    	if(qszh.contains("+")){
    		qszh=qszh.contains("+")?qszh.replaceAll("\\+", ""):qszh;
    		havePlus=true;
    	}
    	boolean isNum = StringUtilsLocal.checkNumber(qszh); 
    	if(!isNum){
    		return -1;
    	}
    	if(haveK&&!havePlus){
    		qszhnum=Double.parseDouble(qszh)*1000;
    	}else{
    		qszhnum=Double.parseDouble(qszh);
    	}
    	return qszhnum;
    }
    
    public static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
    
    public void getJSONPoint(JSONObject json4zhparam) {
    	ArrayList<JSONObject> jsonGeoArray=new ArrayList<JSONObject>();
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
				Object[] resultGetLayer = ClientWebService4LineRef.getInstance().clentInvoke(sdeLayerName, "XLBH","LINEREFBASE", "XLBH", "QSZH", "JZZH", "XLBH","");
				System.out.println("resultGetLayer[0]=" + resultGetLayer[0]); // SDE.高速ROUTE
				String resultStr = resultGetLayer[0].toString();
				if (resultStr.contains("[]")) {
					Object[] resultGetLayer2 = ClientWebService4LineRef.getInstance().clentInvoke("SDE.高速2ROUTE", "XLBH","LINEREFBASE", "XLBH", "QSZH", "JZZH","XLBH", "");
					String resultStr2 = resultGetLayer2[0].toString();
					if (resultStr2.contains("[]")) {
					} else {
						JSONObject jsonSingle=new JSONObject();
						jsonSingle.put("id", json4zhparam.get("id").toString());
						jsonSingle.put("tablename", json4zhparam.get("tablename").toString());
						jsonSingle.put("geometry", resultGetLayer2[0].toString());
						jsonSingle.put("tablename", json4zhparam.get("tablename").toString());
						jsonSingle.put("xlbh", json4zhparam.get("xlbh"));
						jsonSingle.put("qszh", json4zhparam.get("qszh"));
						jsonSingle.put("jzzh", json4zhparam.get("jzzh"));
						jsonGeoArray.add(jsonSingle);
						insertIntoLineref(json4zhparam,resultGetLayer2[0].toString());
					}
				} else {
					JSONObject jsonSingle=new JSONObject();
					jsonSingle.put("id", json4zhparam.get("id").toString());
					jsonSingle.put("tablename", json4zhparam.get("tablename").toString());
					jsonSingle.put("geometry", resultGetLayer[0].toString());
					jsonSingle.put("tablename", json4zhparam.get("tablename").toString());
					jsonSingle.put("xlbh", json4zhparam.get("xlbh"));
					jsonSingle.put("qszh", json4zhparam.get("qszh"));
					jsonSingle.put("jzzh", json4zhparam.get("jzzh"));
					jsonGeoArray.add(jsonSingle);
					insertIntoLineref(json4zhparam,resultGetLayer[0].toString());
				}
			} else {
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
//>>>>>>> .r170

	/**
     * 
     * @param id
     * @param con
     * @param exeStmt
     * @param schemaTableName
     * @param attachmentList
     * @param caption
     * @throws Exception
     */
    private void executeShapeService(long id, Connection con, Statement exeStmt, String schemaTableName, List<JSONObject> attachmentList, String caption) throws Exception {
        // 标识开始运行后台服务
        exeStmt.execute("update webgis_dynamic_service set status=1,schema_table_name='" + schemaTableName + "' where id=" + id);

        // 检查附件文件
        JSONObject json = null;
        boolean checkAttachemt = false;
        String shpFileName = "";
        String shxFileName = "";
        String dbfFileName = "";
        Map<String, String> fileName2Path = new HashMap<String, String>();

        for (int i = 0; i < attachmentList.size(); i++) {
            json = attachmentList.get(i);
            String filePath = RuntimeContext.getAttachmentService().getServerPath(json.getLong("id")) + FilenameUtils.getName(json.getString("url"));
            String fileName = json.getString("caption");
            fileName2Path.put(fileName, filePath);
            if (StringUtils.endsWithIgnoreCase(filePath, ".shp"))
                shpFileName = fileName;
            else if (StringUtils.endsWithIgnoreCase(filePath, ".shx"))
                shxFileName = fileName;
            else if (StringUtils.endsWithIgnoreCase(filePath, ".dbf"))
                dbfFileName = fileName;
        }
        String f1 = FilenameUtils.getBaseName(shpFileName);
        String f2 = FilenameUtils.getBaseName(shxFileName);
        String f3 = FilenameUtils.getBaseName(dbfFileName);
        checkAttachemt = StringUtils.equalsIgnoreCase(f1, f2) && StringUtils.equalsIgnoreCase(f1, f3) && !StringUtils.isEmpty(f1);
        if (!checkAttachemt) {
            exeStmt.execute("update webgis_dynamic_service set last_error_msg='附件文件必须包括(shp shx dbf)文件' where id=" + id);
            return;
        }

        String baseFileName = FilenameUtils.getBaseName(shpFileName);
        for (Entry<String, String> entry : fileName2Path.entrySet()) {
            if (!new File(entry.getValue()).exists()) {
                exeStmt.execute("update webgis_dynamic_service set last_error_msg='附件文件:" + entry.getKey() + "已经被删除' where id=" + id);
                return;
            }
            String newFileName = baseFileName + "." + FilenameUtils.getExtension(entry.getKey());
            FileUtils.copyFile(new File(entry.getValue()), new File(RuntimeContext.getAppTempDir() + newFileName));
        }

        // 数据入库
        boolean isOK = Shape2Oracle.execute(con, exeStmt, id, caption, schemaTableName, RuntimeContext.getAppTempDir() + baseFileName + ".shp");

        // 清除文件
        for (Entry<String, String> entry : fileName2Path.entrySet()) {
            String newFileName = baseFileName + "." + FilenameUtils.getExtension(entry.getKey());
            FileUtils.deleteQuietly(new File(RuntimeContext.getAppTempDir() + newFileName));
        }

        // 完成运行后台服务
        if (isOK)
            exeStmt.execute("update webgis_dynamic_service set status=2,is_complete=1 where id=" + id);
    }

    /**
     * 
     * @param con
     * @param exeStmt
     * @param id
     * @param schemaTableName
     * @param attachmentList
     * @throws Exception
     */
    private void executeExcelService(Connection con, Statement exeStmt, long id, String caption, String schemaTableName, List<JSONObject> attachmentList) throws Exception {
        // 标识开始运行后台服务
        exeStmt.execute("update webgis_dynamic_service set status=1,schema_table_name='" + schemaTableName + "' where id=" + id);

        // 检查附件文件
        JSONObject json = null;
        boolean checkAttachemt = false;
        if (attachmentList.size() == 1) {
            json = attachmentList.get(0);
            String fileName = json.getString("caption");
            checkAttachemt = StringUtils.endsWithIgnoreCase(fileName, ".xls") || StringUtils.endsWithIgnoreCase(fileName, ".xlsx");
        }
        if (!checkAttachemt) {
            exeStmt.execute("update webgis_dynamic_service set last_error_msg='必须只能有一个附件(excel文件)' where id=" + id);
            return;
        }
        String filePath = RuntimeContext.getAttachmentService().getServerPath(json.getLong("id")) + FilenameUtils.getName(json.getString("url"));
        if (!new File(filePath).exists()) {
            exeStmt.execute("update webgis_dynamic_service set last_error_msg='附件文件已经被删除' where id=" + id);
            return;
        }

        // 附件文件入库
        boolean isOK = Excel2Oracle.execute(con, exeStmt, id, caption, schemaTableName, filePath);

        // 完成运行后台服务
        if (isOK)
            exeStmt.execute("update webgis_dynamic_service set status=2,is_complete=1 where id=" + id);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
