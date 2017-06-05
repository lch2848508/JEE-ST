package com.estudio.web.servlet.external;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;

public class DciDataManage {
	private static final IDBHelper DBHELPER=RuntimeContext.getDbHelper();
	
	protected DciDataManage(){
		super();
	}
	
	public String saveServices(String serviceName,String serviceType,String serviceUrl) throws Exception{
		Connection con=DBHELPER.getConnection();
		PreparedStatement pstmt=null;
		int flag=0;
		String resStr="";
		try{
			String sql="insert into webgis_services (id, name, type, url, sortorder) values (SEQ_FOR_J2EE_UNIQUEID.Nextval,?,?,?,SEQ_FOR_J2EE_UNIQUEID.CURRVAL)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, serviceName);
			pstmt.setString(2, serviceType);
			pstmt.setString(3, serviceUrl);
			flag=pstmt.executeUpdate();
		}catch(Exception e){
			 resStr=e.getMessage();
			 return resStr;
		}
		finally{
			DBHELPER.closeStatement(pstmt);
			DBHELPER.closeConnection(con);
		}
		if(flag!=0){
			return "true";
		}
		return "false";
	}
}
