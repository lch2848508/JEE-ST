package com.fsglj;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.alibaba.druid.util.StringUtils;
import com.estudio.context.RuntimeContext;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.intf.db.IDBHelper;

public class SummaryTableServlet extends HttpServlet {
	private final IDBHelper DBHELPER = DBHelper4Oracle.getInstance();

	public SummaryTableServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final JSONObject json = new JSONObject();
		final JSONArray objArr=new JSONArray();
		getGridData(request, response,json,objArr);		
		response.getWriter().println(objArr.toJSONString());
		//request.getRequestDispatcher("../fsglj_page/glyhtzjh_zb.jsp").forward(
		//		request, response);
		
	}

	/*
	 * 获取数据
	 */
	private void getGridData(HttpServletRequest request,
			HttpServletResponse response,JSONObject json,JSONArray objArr) {	
		Connection con = null;		
		try {
			con = DBHELPER.getConnection();
			//String sql = "select  sum(t.jhtz) r18  from fs_shyfjqxfjhap t where (SFTJ = 1 OR DWID = 282210)   and valid = 1   and sfth = 0   and nf ='2016'";
			//获取年份
			String nf=request.getParameter("selectValue");
			if(nf==null||nf==""){
				nf=Calendar.getInstance().get(Calendar.YEAR)+"";//默认当前年份
			}
		
			setLmData(nf,con,objArr); //r13 1、路面
			//request.setAttribute("lmdata", lmdataArr.toString());
			setXlData(nf,con,objArr);//获取线路表的数据
			
			setSHdData(nf,con,objArr);//水毁
			setGZGJGSGCData(nf,con,objArr);//公路改建改善工程
			
			setDKZCData(nf,con,objArr);//（六）、渡口支出
			setDBFCData(nf,con,objArr);//道班房
			int[] ylnum={8,12,18,19,32,37,38,39,40,41};
			setAddDataArr(nf,con,objArr,"一、养路工程费",ylnum,7);//一、养路工程费
			setYLSYFCData(nf,con,objArr);//养路事业费
			setZCJFdATA(nf,con,objArr);//治超经费
			//养护投资包干基数
			int[] yhbgNum={7,42,58};
			setAddDataArr(nf,con,objArr,"养护投资包干基数",yhbgNum,6);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			DBHELPER.closeConnection(con);
		}

	}
	/*大中修工程
	 * R13 1、路面 的数据  16 桥涵
	 */
	private  void  setLmData(String nf,Connection con,JSONArray objArr){
		String lmdzxsql ="select dwid, SUM(GCTZDNJHTZBXHJ) rnum "+
					          "from FS_LMDZXJHAPB t "+
					         "where t.nf = ? "+
					           "AND t.VALID = 1 "+
					           "AND (SFTJ = 1 OR DWID = 282210) "+
					           "AND SFTH = 0 "+
					         "group by dwid";
		JSONArray lmdataArr =getDataARR(lmdzxsql,nf,"1、路    面",con,13,objArr);	
		//桥涵大中修		
		String qhsql="select dwid,sum(t.jhtz) rnum "+
					  "from Fs_Qhdzxjhap T "+
					 "where (SFTJ = 1 OR DWID = 282210) "+
					   "and valid = 1 "+
					   "and sfth = 0 "+
					   "and nf = ? "+
					   " group by dwid";
		JSONArray qhArr =getDataARR(qhsql,nf,"2、桥    涵",con,16,objArr);		
		//大中修工程
		JSONArray dzxArr =new JSONArray();
		dzxArr.add(0, "(二)大中修工程");
		for(int i=1;i<lmdataArr.size();i++){
			Double lmData;
			Object lmobj=lmdataArr.get(i);
			if(lmobj==""||lmobj==null){
				lmData=0.0;
			}else{
				lmData=(Double)lmobj;
			}
			Double qhData;
			Object qhobj=qhArr.get(i);
			if(qhobj==""||qhobj==null){
				qhData=0.0;
			}else{
				qhData=(Double)qhobj;
			}
			dzxArr.add(i, addDouble(lmData,qhData));
		}
		JSONObject jsonObjdzx = new JSONObject();
		jsonObjdzx.put("id",12);
		jsonObjdzx.put("data",dzxArr);
		objArr.add(jsonObjdzx);
		//加上空行的
		objArr.add(getEmptyLine("(1) 油    路",14));
		objArr.add(getEmptyLine("(2) 水 泥 路",15));
	}
	/*
	 * 表3线路
	 * 8,9,10,11,24,26,37,40,41
	 */
	private  void  setXlData(String nf,Connection con,JSONArray objArr){		
		ResultSet rs = null;
		PreparedStatement stmt = null;		
		JSONArray[] xldataArr=new JSONArray[8];//多行数据
		//JSONArray xxbyArr=new JSONArray();//小修保养
		//xxbyArr.add(0, "(一)小修保养");
		//xxbyArr.add(1,0);
		double[] xxbyData={0,0,0,0,0,0,0,0,0};//小修保养数据
		int[] linNum={9,10,11,24,26,37,40,41};//行号
		String xlsql="select k.r9, k.r10, k.r11, k.r24, k.r26, k.r37, k.r40, k.r41 "+
					  "from (SELECT dwid,"+
					               "SUM(NVL(T.Ljlmrcxwf, 0) + NVL(t.qhrcxwf, 0) + NVL(t.lmjcf, 0)) r9,"+
					               "SUM(T.QHJCF) r10,"+
					               "SUM(T.QTXJ) r11,"+
					               "SUM(T.GBMWMLGGJSBZTZHJ) r24,"+
					               "SUM(T.JTGCSS) r26,"+
					               "SUM(T.LHJF) r37,"+
					               "SUM(T.YHGSGCCSF) r40,"+
					               "SUM(T.YHJXTZ) r41 "+
					          "FROM FS_XLQHXWFJGBMJTGCTZJH T, SYS_USERINFO U "+
					         "WHERE T.CJRID = U.ID "+
					           "AND T.NF = ? "+
					           "AND T.VALID = 1 "+
					           "AND (SFTJ = 1 OR DWID = 282210 OR U.P_ID = 282210) "+
					           "AND SFTH = 0 "+
					         "group by t.dwid) k, "+
					       "sys_department dep "+
					 "where k.dwid(+)= dep.id "+
					   "and dep.id in (282210,282211,282212,282213,282214,282215,282284,282285,282286) "+
					 "order by dep.sortorder";	//市局排在第一位	
		try {
			
			stmt=con.prepareStatement(xlsql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//.prepareStatement(xlsql);
			stmt.setString(1, nf);
			rs=stmt.executeQuery();
			for(int i=0;i<xldataArr.length;i++){
				xldataArr[i]=new JSONArray();
				switch(i){
					case 0:
						xldataArr[i].add(0, "1、线    路");
						break;
					case 1:
						xldataArr[i].add(0, "2、桥    涵");
						break;
					case 2:
						xldataArr[i].add(0, "3、其    他");
						break;
					case 3:
						xldataArr[i].add(0, "5、GBM工程和安保工程");
						break;
					case 4:
						xldataArr[i].add(0, "7、交通工程设施");
						break;
					case 5:
						xldataArr[i].add(0, "(七)公路绿化");
						break;
					case 6:
						xldataArr[i].add(0, "(十)养护改善工程测设费");
						break;
					case 7:
						xldataArr[i].add(0, "(十一)养护机械");
						break;
				}
				xldataArr[i].add(1, 0);
				double sum_qj=0;//全局总数
				int k = 1;
				double sj_data=0;//市局及待批
				rs.beforeFirst();//指针回到第一行之前
				while(rs.next()){					
					sum_qj=addDouble(sum_qj,rs.getDouble(i+1));//累加
					if(k==1){//市局的数据
						sj_data=rs.getDouble(i+1);
						//xxbyData[8]=addDouble(xxbyData[8],sj_data);//小修保养的数据
					}else if(k!=10){
						if(rs.getDouble(i+1)==0){
							xldataArr[i].add(k,"");
						}else{
							xldataArr[i].add(k,rs.getDouble(i+1));
						}						
						xxbyData[k-2]=addDouble(xxbyData[k-2],rs.getDouble(i+1));//小修保养的数据
					}
					k++;
				}
				xldataArr[i].add(k,sj_data);
				xldataArr[i].set(1, sum_qj);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", linNum[i]);
				jsonObj.put("data", xldataArr[i]);
				objArr.add(jsonObj);				
			}
			//double sum_xxby=0;
			//for(int f=0;f<xxbyData.length;f++){
				//xxbyArr.add(f+2,xxbyData[f]);
				//sum_xxby=addDouble(sum_xxby,xxbyData[f]);
			//}
			//xxbyArr.set(1, sum_xxby);
			//JSONObject jsonObj = new JSONObject();
			//jsonObj.put("id", 8);
			//jsonObj.put("data", xxbyArr);
			//objArr.add(jsonObj);
			//小修保养，数据累加
			int[] xxbyNum={9,10,11};
			setAddDataArr(nf,con,objArr,"(一)小修保养",xxbyNum,8);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBHELPER.closeStatement(stmt);
			DBHELPER.closeResultSet(rs);
		}		
	}
	/*
	 * 表5水毁  18
	 */
	private void setSHdData(String nf,Connection con,JSONArray objArr){			
		String shsql="select dwid, sum(t.jhtz) rnum "+
				          "from fs_shyfjqxfjhap t "+
				         "where (SFTJ = 1 OR DWID = 282210) "+
				           "and valid = 1 "+
				           "and sfth = 0 "+
				           "and nf = ? "+
				         "group by dwid";
		getDataARR(shsql,nf,"(三)水毁预防及抢修复",con,18,objArr);
	}
	
	/*
	 * (四)公路改建改善工程
	 */
	private void setGZGJGSGCData(String nf,Connection con,JSONArray objArr){
		//取小维费表里的24和26的数据
		JSONArray gbmDataArr=new JSONArray();
		JSONArray jtgcssDataArr=new JSONArray();
		for(int i=0;i<objArr.size();i++){
			if(objArr.getJSONObject(i).getInt("id")==24){
				gbmDataArr=objArr.getJSONObject(i).getJSONArray("data");
			}else if(objArr.getJSONObject(i).getInt("id")==26){
				jtgcssDataArr=objArr.getJSONObject(i).getJSONArray("data");
			}
		}
		String xgsql="select dwid,sum(XJ) rnum   from FS_XGXPSNLYLGCJH   where nf = ? AND VALID = 1"+
						"AND (SFTJ = 1 OR DWID = 282210)  AND SFTH = 0  group by dwid";//线改		
		JSONArray xgDataArr =getDataARR(xgsql,nf,"1、线    改",con,20,objArr);
		String qhgjsql="select dwid, sum(Bndjhtzbxhj) rnum  from Fs_Qhgjjhap where (SFTJ = 1 OR DWID = 282210)"+
												" and valid = 1   and sfth = 0  and nf = ? group by dwid";
		JSONArray qhgjDataArr=getDataARR(qhgjsql,nf,"4、桥    涵",con,23,objArr);//桥涵改建
		
		//其他抢险修复
		String qxxfSql="select 282210 dwid,fs_utils.fun_count_xxf(?) rnum  from dual";
		JSONArray qxxfDataArr=getDataARR(qxxfSql,nf,"8、其他工程--抢险修复",con,27,objArr);
		
		//(四)公路改建改善工程    累加
		int[] glnum={20,23,24,26,27};
		setAddDataArr(nf,con,objArr,"(四)公路改建改善工程",glnum,19);
		//加上空行的
		objArr.add(getEmptyLine("2、新铺水泥路",21));
		objArr.add(getEmptyLine("3、新铺油路",22));
		objArr.add(getEmptyLine("6、文明样板路建设",25));
		objArr.add(getEmptyLine("9、养征站",28));
		objArr.add(getEmptyLine("(五)新建工程费",29));
		objArr.add(getEmptyLine("1、线   路",30));
		objArr.add(getEmptyLine("2、桥   涵",31));
	}
	/*
	 * 六渡口支出
	 */
	private void setDKZCData(String nf,Connection con,JSONArray objArr){
		String dkSql="select nvl(R33,0)+nvl(R34,0)+nvl(R35,0)+nvl(R36,0) R32,R33, R34, R35, R36 "+
				"FROM (SELECT DWID,SUM(T.XWF) R33,SUM(T.DZXJHAP) R34,SUM(T.CBXJWXJHAP) R35,SUM(T.DKMTGZJHAP) R36 "+
				         " FROM FS_DKZCJHAP T, SYS_USERINFO U WHERE T.CJRID = U.ID "+
				           "AND (SFTJ = 1 OR U.P_ID = 282210) AND T.VALID = 1 "+
				           "AND SFTH = 0  AND NF =? group by dwid) K,SYS_DEPARTMENT DEP "+
				 "WHERE K.DWID(+) = DEP.ID "+
				  " AND DEP.ID IN (282210,282211,282212,282213,282214,282215,282284,282285,282286) "+
				 "ORDER BY DEP.SORTORDER";
		ResultSet rs = null;
		PreparedStatement stmt = null;		
		JSONArray[] dkdataArr=new JSONArray[5];//多行数据
		try {
			stmt=con.prepareStatement(dkSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, nf);
			rs=stmt.executeQuery();
			for(int i=0;i<5;i++){
				dkdataArr[i]=new JSONArray();
				int lineNum=i+32;
				switch(lineNum){
					case 32:
						dkdataArr[i].add(0,"(六)渡口支出");
						break;
					case 33:
						dkdataArr[i].add(0,"1、小 维 费");
						break;
					case 34:
						dkdataArr[i].add(0,"2、大 中 修");
						break;
					case 35:
						dkdataArr[i].add(0,"3、船舶新建维修");
						break;
					case 36:
						dkdataArr[i].add(0,"4、渡口码头改造");
						break;
				}
				dkdataArr[i].add(1, 0);
				double sum_qj=0;//全局总数
				int k = 1;
				double sj_data=0;//市局及待批
				rs.beforeFirst();//指针回到第一行之前
				while(rs.next()){
					sum_qj=addDouble(sum_qj,rs.getDouble(i+1));
					if(k==1){
						sj_data=rs.getDouble(i+1);
					}else{
						if(rs.getDouble(i+1)==0){
							dkdataArr[i].add(k,"");
						}else{
							dkdataArr[i].add(k,rs.getDouble(i+1));
						}
					}
					k++;
				}
				dkdataArr[i].add(k, sj_data);
				dkdataArr[i].set(1, sum_qj);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", lineNum);
				jsonObj.put("data", dkdataArr[i]);
				objArr.add(jsonObj);
			}		
		
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBHELPER.closeStatement(stmt);
			DBHELPER.closeResultSet(rs);
		}
	}
	
	/*
	 * (八)道(渡)房建设
	 */
	private void setDBFCData(String nf,Connection con,JSONArray objArr){
		String dbfSql="select dwid, sum(bnaptz) rnum from Fs_Dbfjsap where (SFTJ = 1 OR DWID = 282210) "+
							"and valid = 1 and sfth = 0 and nf = ? group by dwid ";
		getDataARR(dbfSql,nf,"(八)道(渡)房建设",con,38,objArr);	
	}
	
	/*
	 * 二、养路事业费
	 */
	private void setYLSYFCData(String nf,Connection con,JSONArray objArr){
		//科研开发费
		String kySql="SELECT dwid, SUM(KYBZTZ) rnum  FROM FS_GLFAPB "+
						 "WHERE (DWID <> 282210 or DWMC like '%待安排%')  " +
						 "AND NF = ?  AND VALID = 1 group by dwid";
		getDataARR(kySql,nf,"2、科研及技术开发费",con,44,objArr);
		
		//3、宣传、教育培训费
		String xqjySql="SELECT dwid, SUM(DGTXCJYPXF) rnum  FROM FS_GLFAPB "+
				 "WHERE (DWID <> 282210 or DWMC like '%待安排%')  " +
				 "AND NF = ?  AND VALID = 1 group by dwid";
		getDataARR(xqjySql,nf,"3、宣传、教育培训费",con,45,objArr);
		
		//4、路况交通量调查费
		String lkjtSql="SELECT dwid, SUM(LKJJTF) rnum  FROM FS_GLFAPB "+
				 "WHERE (DWID <> 282210 or DWMC like '%待安排%')  " +
				 "AND NF = ?  AND VALID = 1 group by dwid";		
		getDataARR(lkjtSql,nf,"4、路况交通量调查费",con,46,objArr);
		//8、路政费管理费
		String lzSql="SELECT dwid, SUM(LZJF) rnum  FROM FS_GLFAPB "+
				 "WHERE (DWID <> 282210 or DWMC like '%待安排%')  " +
				 "AND NF = ?  AND VALID = 1 group by dwid";
		getDataARR(lzSql,nf,"8、路政费管理费",con,50,objArr);
		//行政管理费
		String xzglfSql="SELECT decode(dwid,0,282210,dwid) dwid, SUM(HZGLFZJ) rnum FROM FS_GLFAPB "+
							 "WHERE (DWID <> 282210 or DWMC like '%局机关%') "+
							   "AND NF = ? AND VALID = 1 group by dwid";
		getDataARR(xzglfSql,nf,"9、行政管理费",con,51,objArr);
		//10、专项经费
		String zxjfSql="select  decode(dwid,0,282210,dwid) dwid,sum(hzglfzj) rnum "+
				" from fs_glfapb where dwmc like '局其他专项'   and nf =?  and valid=1   group by dwid";
		getDataARR(zxjfSql,nf,"10、专项经费",con,52,objArr);
		
		//养路事业费
		int[] ylnum={44,45,46,50,51,52};
		setAddDataArr(nf,con,objArr,"二、养路事业费",ylnum,42);
		
		objArr.add(getEmptyLine("1、厂、场建设费",43));
		objArr.add(getEmptyLine("5、职工宿舍建设费",47));
		objArr.add(getEmptyLine("6、养征房建设费",48));
		objArr.add(getEmptyLine("7、养征经费",49));
	}
	
	/*三、养路其他费的空行
	 * 四、治超经费
	 */
	private void setZCJFdATA(String nf,Connection con,JSONArray objArr){
		objArr.add(getEmptyLine("三、养路其它费",53));
		objArr.add(getEmptyLine("1、职工退休基金",54));
		objArr.add(getEmptyLine("2、劳动保险",55));
		objArr.add(getEmptyLine("2、其   他",56));
		objArr.add(getEmptyLine("3、机动预备费",57));
		//治超经费
		String zcjfSql="SELECT dwid, SUM(ZCJF) rnum  FROM FS_GLFAPB "+
						" WHERE NF = ? AND VALID = 1 group by dwid";
		getDataARR(zcjfSql,nf,"四、治超经费",con,58,objArr);
		objArr.add(getEmptyLine("",59));//增加一个空行，以便显示最后一行
	}
	
	
	/*
	 * objArr内的数据相加
	 * dwmc单位名称，addLine相加的行数，lineNum数据存储到的行数
	 */
	private void setAddDataArr(String nf,Connection con,JSONArray objArr,String dwmc,int[] addLine,int lineNum){
		JSONArray dataArr=new JSONArray();
		dataArr.add(0,dwmc);
		for(int i=1;i<11;i++){
			Double ylData=0.0000;
			for(int j=0;j<objArr.size();j++){
				int dataId=objArr.getJSONObject(j).getInt("id");
				for(int k=0;k<addLine.length;k++){
					if(dataId==addLine[k]){
						Double arrData;
						Object dataObj=objArr.getJSONObject(j).getJSONArray("data").get(i);
						if(dataObj==""||dataObj==null){
							arrData=0.0;
						}else{
							arrData=(Double)dataObj;
						}						
						ylData=addDouble(ylData,arrData);
						break;
					}					
				}
			}
			dataArr.add(i,ylData);
		}
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("id", lineNum);
		jsonObj.put("data", dataArr);
		objArr.add(jsonObj);
	}
	/*
	 * 循环取数据并填入objArr中
	 */
	private JSONArray getDataARR(String sql,String nf,String dwmc,Connection con,int lineNum,JSONArray objArr){
		//包装sql
		sql="select k.rnum from ("+sql+") k,sys_department dep   where k.dwid(+)=dep.id "+
			   " and dep.id in (282210,282211,282212,282213,282214,282215,282284,282285,282286)"+
			   " order by dep.sortorder";
		JSONArray dataArr=new JSONArray();
		dataArr.add(0,dwmc);
		dataArr.add(1,0);
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt=con.prepareStatement(sql);
			stmt.setString(1, nf);
			rs=stmt.executeQuery();
			int k=1;
			double sj_data=0;//市局及待批
			double sum_qj=0;//全局累计
			while(rs.next()){
				sum_qj=addDouble(sum_qj,rs.getDouble(1));
				if(k==1){
					sj_data=rs.getDouble(1);
				}else{
					double rsdata=rs.getDouble(1);
					if(rsdata==0){
						dataArr.add(k,"");
					}else{
						dataArr.add(k,rsdata);
					}
				}
				k++;
			}
			dataArr.add(k, sj_data);
			dataArr.set(1,sum_qj);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id",lineNum);
			jsonObj.put("data",dataArr);
			objArr.add(jsonObj);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBHELPER.closeStatement(stmt);
			DBHELPER.closeResultSet(rs);
		}
		return dataArr;
	}
	/*
	 * 添加空行的数据
	 */
	private JSONObject getEmptyLine(String dwmc,int lineNum){
		JSONArray dataArr =new JSONArray();	
		dataArr.add(0, dwmc);
		for(int i=1;i<11;i++){
			dataArr.add(i, "");//空数据
		}
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("id",lineNum);
		jsonObj.put("data", dataArr);
		return jsonObj;	
	}
	
	/*
	 * 浮点数相加处理
	 */
	private Double addDouble(Double a ,Double b){
		BigDecimal  a1 = new BigDecimal(a.toString());
		BigDecimal b1=new BigDecimal(b.toString());	
		return new Double(a1.add(b1).doubleValue());
	}

	/*
	 * 获取年份下拉框
	 */
	public String getYearCombox(){
		String str="";
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String year=Calendar.getInstance().get(Calendar.YEAR)+"";
		try {
			con=DBHELPER.getConnection();
			String sql="select bmz,bmmc from fs_BMMXb where PID=281963";
			stmt=con.prepareStatement(sql);
			rs=stmt.executeQuery();
			while(rs.next()){
				String nf=rs.getString("bmz");
				if(StringUtils.equals(year, nf)){
					str+="<option value='"+nf+"' selected>"+rs.getString("bmmc")+"</option>";
				}else{
					str+="<option value='"+nf+"'>"+rs.getString("bmmc")+"</option>";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {			
			DBHELPER.closeConnection(con);
			DBHELPER.closeStatement(stmt);
			DBHELPER.closeResultSet(rs);
		}
		return str;
	}

}
