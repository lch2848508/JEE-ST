package com.estudio.impl.service.sercure;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.codehaus.xfire.client.Client;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.ThreadUtils;

public class ClientWebService4LineRef {
	
	private String lineRefServiceUrl="";
	private String server="";
	private String user="";
	private String password="";
	private String sdeversion="";
	private String port="";
	private String sdename="";
	private String orcle_sid;
	private String oracle_user;
	private String oracle_pwd;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	private Client client =null;
	private int flag=0;
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	//<server>127.0.0.1</server>
	//<user>sde</user>
	//<password>sde</password>
	//<sdeversion>SDE.DEFAULT</sdeversion>
	//<port>5151</port>
	//初始化参数
    public void initParams(final String orcle_sid,final String oracle_user,final String oracle_pwd,final String lineRefServiceUrl,final String server,final String user,final String password,final String sdeversion,final String port,final String sdename) throws MalformedURLException, Exception {
        this.lineRefServiceUrl=lineRefServiceUrl;
        try{
        	client =new Client(new URL(lineRefServiceUrl));
        }catch(Exception e){
        	e.printStackTrace();
        }
        this.orcle_sid=orcle_sid;
        this.oracle_user=oracle_user;
        this.oracle_pwd=oracle_pwd;
        this.server=server;
        this.user=user;
        this.password=password;
        this.sdeversion=sdeversion;
        this.port=port;
        this.sdename=sdename;
        flag=1;
    }
    //////////////////////////////////////////////////////
    //调用webservice方法
    // String sdefeaturelayer SDE中的图层名
    //,String sdefeaturelayerZD SDE字段名
    //String SDEtableName SDE事件表明
    //String tGLZD SDE事件表关联字段 
    //String tQSZH SDE表 起始桩号
    //String tJZZH SDE表截止桩号
    //String resultFilterZD 结果过滤条件
    //String resultFilter 结果过滤条件
    public Object[] clentInvoke(String sdefeaturelayer,String sdefeaturelayerZD,String SDEtableName,String tGLZD,String tQSZH,String tJZZH,String resultFilterZD,String resultFilter){
    	Object[] reslut = null;
		try {
			if(client!=null){
				reslut = client.invoke("GetFeatureLayer", new Object[]{orcle_sid,oracle_user,oracle_pwd,server,user,password,sdeversion,port,sdename,sdefeaturelayer,sdefeaturelayerZD,SDEtableName,tGLZD,tQSZH,tJZZH,resultFilterZD,resultFilter});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 return reslut;
    }
    
	//////////////////////////////////////////////////////
	//调用webservice方法
	// String sdefeaturelayer SDE中的图层名
	//,String sdefeaturelayerZD SDE字段名
	//String SDEtableName SDE事件表明
	//String tGLZD SDE事件表关联字段 
	//String tQSZH SDE表 起始桩号
	//String tJZZH SDE表截止桩号
	//String resultFilterZD 结果过滤条件
	//String resultFilter 结果过滤条件
	public Object[] clentPointInvoke(String sdefeaturelayer,String sdefeaturelayerZD,String SDEtableName,String tGLZD,String tQSZH,String tJZZH,String resultFilterZD,String resultFilter){
		Object[] reslut = null;
		try {
		if(client!=null){
			reslut = client.invoke("GetPointFeatureLayer", new Object[]{orcle_sid,oracle_user,oracle_pwd,server,user,password,sdeversion,port,sdename,sdefeaturelayer,sdefeaturelayerZD,SDEtableName,tGLZD,tQSZH,tJZZH,resultFilterZD,resultFilter});
		}
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return reslut;
	}
    
	
    public Client getLineRefClientinstance(){
    	return client;
    }
    
	protected ClientWebService4LineRef(){
		super();
	}
	
	private static final ClientWebService4LineRef instance=new ClientWebService4LineRef();
	public static ClientWebService4LineRef getInstance(){
		return instance;
	}
	
	private final IDBHelper DBHelper = RuntimeContext.getDbHelper(); 
 	private String LINEREFBASE="LINEREFBASE";;
	
	public static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
	public void exceute(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
				// TODO Auto-generated method stub
					produceMileAgePoint();
					ThreadUtils.sleepMinute(5);
				}
			}
		}).start();
	}
	private void produceMileAgePoint() {
		// TODO Auto-generated method stub
		Connection con=null;
		try{
			con = DBHelper.getNativeConnection();
			String sql="select id,name from webgis_services t where t.type='ArcGISRouteMapService'";
			JSONArray resultService=DBHelper.executeQuery(sql, con);
			if(resultService.size()==0){
				return;
			}else{
				//清楚之前数据
				for(int i=0;i<resultService.size();i++){
					JSONObject json=resultService.getJSONObject(i);
					json.getString("NAME");
					json.getInt("ID");
					sql="select ID from webgis_layer t where t.route_status in (0,1) and t.download_status=1 and t.p_id="+json.getInt("ID");
					JSONArray resultLayer=DBHelper.executeQuery(sql, con);
					
					for(int j=0;j<resultLayer.size();j++){
						sql="delete from webgis_dynamic_p_mileage where layerid="+resultLayer.getJSONObject(j).getInt("ID");
						DBHelper.execute(sql, con);
						produceMielAgeLayer(resultLayer.getJSONObject(j),con);
						sql="update webgis_layer t set t.route_status=2 where t.id="+resultLayer.getJSONObject(j).getInt("ID");
						DBHelper.execute(sql, con);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBHelper.closeConnection(con);
		}
	}
	/**
	 * 根据route服务 获取线路编号
	 * @param json
	 * @throws Exception 
	 */
	private void produceMielAgeLayer(JSONObject json,Connection con) throws Exception{
		String sdeServerName = ClientWebService4LineRef.getInstance().getUser();	
		String sql="update webgis_layer t set t.route_status=1 where t.id="+json.getInt("ID");
		DBHelper.execute(sql, con);
		sql="select a.caption layername,b.field_name fieldname from webgis_dynamic_route_layer a,webgis_dynamic_route_field b where a.id=b.p_id and b.is_xlbh=1 and a.caption like '%HB%'";
		JSONArray layerAndfield=DBHelper.executeQuery(sql, con);
		
	    sql="select t.xlbh, min(t.start_zh_m) start_min,min(t.end_zh_m) end_min,max(t.start_zh_m) start_max,max(t.end_zh_m) end_max from spatial_fs_"+json.getInt("ID")+" t group by t.xlbh";
		JSONArray layerData=DBHelper.executeQuery(sql, con);
		for(int i=0;i<layerData.size();i++){
			JSONObject spatial=layerData.getJSONObject(i);
			String xlbh=spatial.getString("XLBH");
			xlbh=xlbh.replaceAll(" ", "");
			Double start_min=spatial.getDouble("START_MIN");
			Double end_min=spatial.getDouble("END_MIN");
			Double start_max=spatial.getDouble("START_MAX");
			Double end_max=spatial.getDouble("END_MAX");
			Double min=Math.min(start_min,end_min);
			Double max=Math.max(start_max, end_max);
			JSONArray routeLayerLink= getLayerName(xlbh,layerAndfield);
			
			DBHelper.execute("delete from  " + sdeServerName + "."+LINEREFBASE,con);
			int k=0;
			while(min<=max){
				String sqlins="insert into "+sdeServerName+"."+LINEREFBASE+"(id, xlbh, qszh) values(seq_for_j2ee_webgis.nextval,'"+xlbh+"',"+min+")";
				DBHelper.execute(sqlins,con);
				if(min==max){
					break;
				}
				min+=50;
				if(min>max){
					min=max;
				}
				k++;
				if(k%1000==0){
					String lineStr=getPointMileage(con,xlbh,min,max,routeLayerLink);
					if(lineStr!=""||!lineStr.equals("")){
						net.sf.json.JSONArray sfarray=net.sf.json.JSONArray.fromObject("["+lineStr+"]");
						net.sf.json.JSONObject json4sf=sfarray.getJSONObject(0);
						storagePointMileage(con,xlbh,min,json4sf.getString("geometries"),json.getInt("ID"));
					}
					k=0;
					DBHelper.execute("delete from  " + sdeServerName + "."+LINEREFBASE,con);
				}
			}
			
			String lineStr=getPointMileage(con,xlbh,min,max,routeLayerLink);
			if(lineStr!=""||!lineStr.equals("")){
				net.sf.json.JSONArray sfarray=net.sf.json.JSONArray.fromObject("["+lineStr+"]");
				net.sf.json.JSONObject json4sf=sfarray.getJSONObject(0);
				storagePointMileage(con,xlbh,min,json4sf.getString("geometries"),json.getInt("ID"));
			}
			
		}
	}
	private void storagePointMileage(Connection con, String xlbh, Double min, String geometry,int layerid) {
		// TODO Auto-generated method stub
		JSONArray array=JSONUtils.parserJSONArray(geometry);
		String sql="insert into webgis_dynamic_p_mileage (id, xlbh, mileage, mileafe_point,layerid) values (SEQ_MILEAGEAGE.NEXTVAL, ?, ?, ?, ?)";
		PreparedStatement stmt=null;
		try {
		    stmt=con.prepareStatement(sql);
		    for(int i=0;i<array.size();i++){
		    	JSONObject geoObj=array.getJSONObject(i);
		    	if(geoObj.getString("m")==null){
		    		continue;
		    	}
		        Double mileage=geoObj.getDouble("m");
		    	String geostr=array.getJSONObject(i).toJSONString();
			    stmt.setString(1, xlbh);
			    stmt.setDouble(2, mileage);
			    stmt.setBytes(3, Convert.str2Bytes("["+geostr+"]"));
			    stmt.setInt(4, layerid);
			    stmt.executeUpdate(); 
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBHelper.closeStatement(stmt);
		}
		Convert.str2Bytes(geometry);
	}
	private JSONArray getLayerName(String XLBH,JSONArray layerAndfield){
		JSONArray layerNameFields=new JSONArray();
		for(int i=0;i<layerAndfield.size();i++){
			if(layerAndfield.getJSONObject(i).getString("LAYERNAME").equals("GS_GD_SD_XD_YD_HB_2013")){
				JSONObject json=new JSONObject();
				json.put("LAYER_NAME",layerAndfield.getJSONObject(i).getString("LAYERNAME"));
				json.put("FIELD_NAME",layerAndfield.getJSONObject(i).getString("FIELDNAME"));
				layerNameFields.add(json);
				break;
			}
		}
		return layerNameFields;
	}
	/**
	 * 获取桩号图斑
	 * @param con
	 * @param sdeServerName sde数据局名
	 * @param resultseldata 关联表数据
	 * @param schema_table_name 数据库表名
	 * @throws Exception
	 */
	public String getPointMileage(Connection con,String xlbh,Double qszh,Double jzzh,JSONArray routeLayerLink) throws Exception{
		String sdeServerName = ClientWebService4LineRef.getInstance().getUser();	
		String resultStr2 ="";
		Object[] resultGetLayer=null;
		for(int j=0;j<routeLayerLink.size();j++){
			String layerName=routeLayerLink.getJSONObject(j).getString("LAYER_NAME");
			String sdeLayerName=sdeServerName.toUpperCase() +"."+layerName;
			String sdeLayerField=routeLayerLink.getJSONObject(j).getString("FIELD_NAME");
			resultGetLayer = ClientWebService4LineRef.getInstance().clentPointInvoke(sdeLayerName, sdeLayerField.toUpperCase(),LINEREFBASE, "XLBH", "QSZH", "JZZH","XLBH","");
			if(resultGetLayer==null){
				continue;
			}
			resultStr2 = resultGetLayer[0].toString();
//			if (!resultStr2.contains("NaN")) {
//				break;
//			}
		}
		if (resultStr2.contains("[]")||resultStr2=="") {
			return "";
		} 
		return resultStr2;
	}
	
	 /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    	String x="GS_HB_30_2013";
        System.out.println(x.substring(0, 3));
        System.out.println("Double.MAX_VALUE = " + Double.MAX_VALUE); 
    }
    
    /**
	 * 获取桩号图斑
	 * @param con
	 * @param sdeServerName sde数据局名
	 * @param resultseldata 关联表数据
	 * @param schema_table_name 数据库表名
	 * @throws Exception
	 */
	public String getMileage(Connection con,String xlbh,Double qszh,Double jzzh,JSONArray routeLayerLink) throws Exception{
		String sdeServerName = ClientWebService4LineRef.getInstance().getUser();	
	    DBHelper.execute("delete from  " + sdeServerName + "."+LINEREFBASE,con);
		qszh=qszh==-1?0.0:qszh;
		jzzh=jzzh==-1?9999999999999.0:jzzh;
		
		xlbh=xlbh.replaceAll(" ", "");
		String sqlins="insert into "+sdeServerName+"."+LINEREFBASE+" values(seq_for_j2ee_webgis.nextval,'"+xlbh+"',"+qszh+","+jzzh+")";
		DBHelper.execute(sqlins,con);
		String resultStr2 ="";
		Object[] resultGetLayer=null;
		int xlbh_length=xlbh.length();
		for(int j=0;j<routeLayerLink.size();j++){
			String layerName=routeLayerLink.getJSONObject(j).getString("LAYER_NAME");
			String sdeLayerName=sdeServerName.toUpperCase() +"."+layerName;
			String sdeLayerField=routeLayerLink.getJSONObject(j).getString("FIELD_NAME");
			if(!layerName.substring(0, 1).equals(xlbh.substring(0, 1))){
				continue;
			}
			String num=layerName.substring(3, 4);
			if(!isNum(num)){
				continue;
			}
			if(Integer.parseInt(num)!=0&&xlbh_length!=Integer.parseInt(num)){
				continue;
			}
			resultGetLayer = ClientWebService4LineRef.getInstance().clentInvoke(sdeLayerName, sdeLayerField.toUpperCase(),LINEREFBASE, "XLBH", "QSZH", "JZZH","XLBH","");
			if(resultGetLayer==null){
				continue;
			}
			resultStr2 = resultGetLayer[0].toString();
			if (!resultStr2.contains("[]")) {
				break;
			}
		}
		if (resultStr2.contains("[]")||resultStr2=="") {
			return "";
		} 
		return resultStr2;
	}
	
	/**
	 * 获取桩号图斑
	 * @param con
	 * @param sdeServerName sde数据局名
	 * @param resultseldata 关联表数据
	 * @param schema_table_name 数据库表名
	 * @throws Exception
	 */
	public String getMileage(Connection con,String xlbh,Double qszh,Double jzzh) throws Exception{
		String sql="select id, xlbh, mileage, mileafe_point from webgis_dynamic_p_mileage where xlbh=? and mileage between ? and ?";
		JSONArray resultPointList=new JSONArray();
		PreparedStatement stmt=null;
		try{
			stmt=con.prepareStatement(sql);
			stmt.setString(1, xlbh);
			stmt.setDouble(2, qszh);
			stmt.setDouble(3, jzzh);
			ResultSet rs=stmt.executeQuery();
			while(rs.next()){
				JSONObject json=new JSONObject();
				json.put("xlbh", rs.getString("xlbh"));
				json.put("mileage", rs.getString("mileage"));
				json.put("mileage_point", Convert.bytes2Str(rs.getBytes("mileafe_point")));
				resultPointList.add(json);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBHelper.closeStatement(stmt);
		}
	    return resultPointList.size()!=0?resultPointList.toJSONString():"";
	}
	/**
	 * 获取桩号图斑
	 * @param con
	 * @param sdeServerName sde数据局名
	 * @throws Exception
	 */
	public String getMileage(Connection con,JSONArray paramMileArray) throws Exception{
		String sql="select id, xlbh, mileage, mileafe_point from webgis_dynamic_p_mileage where xlbh=? and mileage between ? and ? order by mileage";
		JSONArray resultLineList=new JSONArray();
		PreparedStatement stmt=null;
		try{
			stmt=con.prepareStatement(sql);
			for(int i=0;i<paramMileArray.size();i++){
				JSONObject reaultObject=new JSONObject();
				JSONArray resultPointList=new JSONArray();
				JSONObject paramObject=paramMileArray.getJSONObject(i);
				
				String xlbh=paramObject.getString("xlbh");
				Double qszh=paramObject.getDouble("qs");
				Double jzzh=paramObject.getDouble("jz");
				reaultObject.put("xlbh", xlbh);
				reaultObject.put("qszh", qszh);
				reaultObject.put("jzzh", jzzh);
				
				stmt.setString(1, xlbh);
				stmt.setDouble(2, qszh);
				stmt.setDouble(3, jzzh);
				ResultSet rs=stmt.executeQuery();
				while(rs.next()){
					resultPointList.add(Convert.bytes2Str(rs.getBytes("mileafe_point")));
				}
				reaultObject.put("mileage_points", resultPointList);
			resultLineList.add(reaultObject);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBHelper.closeStatement(stmt);
		}
//		System.out.println(resultLineList.toJSONString());
	    return resultLineList.size()!=0?resultLineList.toJSONString():"";
	}
}
