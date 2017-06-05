import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.estudio.context.RuntimeContext;
import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.JSONUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class TestInputXZQH {

	public static String readFile(String Path) {
		String laststr = "";
		BufferedReader reader = null;
		try {
			FileInputStream fileInputStram = new FileInputStream(Path);
			InputStreamReader inputStreamReader = new InputStreamReader(
					fileInputStram, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String jsonstring = readFile("F:/2016年行政区域代码-JSON数据库/2016年行政区域代码-JSON数据库/towngds.json");
		JSONArray jsonArray = JSONUtils.parserJSONArray(jsonstring);
		int size = jsonArray.size();
		DBConnProvider4Oracle.getInstance().initParams("127.0.0.1", 1521, "orcl", "prjdbgdsjtt", "prjdbgdsjtt", 10, false);
		Connection con = DBConnProvider4Oracle.getInstance().getConnection();
		PreparedStatement stmt=null;
		try{
			for (int i = 0; i < size; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				System.out.println(jsonObject.getString("province") + ":"
						+ jsonObject.getString("name") + "  "
						+ jsonObject.getString("type"));
				String sqlpre = "insert into sys_qgxzqh(";
				String sqllast = "values(";
				String id = "";
				String p_id = "";
				if (jsonObject.containsKey("province")) {
					id = jsonObject.getString("province");
					p_id = "-1";
					sqlpre += "province,";
					sqllast +="'"+ jsonObject.getString("province")+"'"+",";
				}
				if (jsonObject.containsKey("city")) {
					p_id = id;
					id += jsonObject.getString("city");
					sqlpre += "city,";
					sqllast +="'"+ jsonObject.getString("city")+"'"+",";
				}
				if (jsonObject.containsKey("country")) {
					p_id = id;
					id += jsonObject.getString("country");
					sqlpre += "country,";
					sqllast += "'"+jsonObject.getString("country")+"'"+",";
				}
				if (jsonObject.containsKey("town")) {
					p_id = id;
					id += jsonObject.getString("town");
					sqlpre += "town,";
					sqllast += "'"+jsonObject.getString("town")+"'"+",";
				}
				if (jsonObject.containsKey("village")) {
					p_id = id;
					id += jsonObject.getString("village");
					sqlpre += "village,";
					sqllast += "'"+jsonObject.getString("village")+"'"+",";
				}
				if (jsonObject.containsKey("name")) {
					sqlpre += "name,";
					sqllast += "'"+jsonObject.getString("name")+"'"+",";
				}
				if (jsonObject.containsKey("type")) {
					sqlpre += "type,";
					sqllast += "'"+jsonObject.getString("type")+"'"+",";
				}
				sqlpre += "id,";
				sqllast += id+",";
				sqlpre += "p_id";
				sqllast += p_id;
				
				sqlpre += ")";
				sqllast += ")";
				String sql = sqlpre + sqllast;
				System.out.println(sql);
				stmt=con.prepareStatement(sql);
				stmt.executeUpdate();
			    if (stmt!=null) {
				  stmt.close();//关闭预编译对象
	            }
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
		  if (con!=null) {
			  con.close();//关闭预编译对象
          }
		}
	}
}
