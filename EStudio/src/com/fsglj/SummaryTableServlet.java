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
	 * ��ȡ����
	 */
	private void getGridData(HttpServletRequest request,
			HttpServletResponse response,JSONObject json,JSONArray objArr) {	
		Connection con = null;		
		try {
			con = DBHELPER.getConnection();
			//String sql = "select  sum(t.jhtz) r18  from fs_shyfjqxfjhap t where (SFTJ = 1 OR DWID = 282210)   and valid = 1   and sfth = 0   and nf ='2016'";
			//��ȡ���
			String nf=request.getParameter("selectValue");
			if(nf==null||nf==""){
				nf=Calendar.getInstance().get(Calendar.YEAR)+"";//Ĭ�ϵ�ǰ���
			}
		
			setLmData(nf,con,objArr); //r13 1��·��
			//request.setAttribute("lmdata", lmdataArr.toString());
			setXlData(nf,con,objArr);//��ȡ��·�������
			
			setSHdData(nf,con,objArr);//ˮ��
			setGZGJGSGCData(nf,con,objArr);//��·�Ľ����ƹ���
			
			setDKZCData(nf,con,objArr);//���������ɿ�֧��
			setDBFCData(nf,con,objArr);//���෿
			int[] ylnum={8,12,18,19,32,37,38,39,40,41};
			setAddDataArr(nf,con,objArr,"һ����·���̷�",ylnum,7);//һ����·���̷�
			setYLSYFCData(nf,con,objArr);//��·��ҵ��
			setZCJFdATA(nf,con,objArr);//�γ�����
			//����Ͷ�ʰ��ɻ���
			int[] yhbgNum={7,42,58};
			setAddDataArr(nf,con,objArr,"����Ͷ�ʰ��ɻ���",yhbgNum,6);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			DBHELPER.closeConnection(con);
		}

	}
	/*�����޹���
	 * R13 1��·�� ������  16 �ź�
	 */
	private  void  setLmData(String nf,Connection con,JSONArray objArr){
		String lmdzxsql ="select dwid, SUM(GCTZDNJHTZBXHJ) rnum "+
					          "from FS_LMDZXJHAPB t "+
					         "where t.nf = ? "+
					           "AND t.VALID = 1 "+
					           "AND (SFTJ = 1 OR DWID = 282210) "+
					           "AND SFTH = 0 "+
					         "group by dwid";
		JSONArray lmdataArr =getDataARR(lmdzxsql,nf,"1��·    ��",con,13,objArr);	
		//�ź�������		
		String qhsql="select dwid,sum(t.jhtz) rnum "+
					  "from Fs_Qhdzxjhap T "+
					 "where (SFTJ = 1 OR DWID = 282210) "+
					   "and valid = 1 "+
					   "and sfth = 0 "+
					   "and nf = ? "+
					   " group by dwid";
		JSONArray qhArr =getDataARR(qhsql,nf,"2����    ��",con,16,objArr);		
		//�����޹���
		JSONArray dzxArr =new JSONArray();
		dzxArr.add(0, "(��)�����޹���");
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
		//���Ͽ��е�
		objArr.add(getEmptyLine("(1) ��    ·",14));
		objArr.add(getEmptyLine("(2) ˮ �� ·",15));
	}
	/*
	 * ��3��·
	 * 8,9,10,11,24,26,37,40,41
	 */
	private  void  setXlData(String nf,Connection con,JSONArray objArr){		
		ResultSet rs = null;
		PreparedStatement stmt = null;		
		JSONArray[] xldataArr=new JSONArray[8];//��������
		//JSONArray xxbyArr=new JSONArray();//С�ޱ���
		//xxbyArr.add(0, "(һ)С�ޱ���");
		//xxbyArr.add(1,0);
		double[] xxbyData={0,0,0,0,0,0,0,0,0};//С�ޱ�������
		int[] linNum={9,10,11,24,26,37,40,41};//�к�
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
					 "order by dep.sortorder";	//�о����ڵ�һλ	
		try {
			
			stmt=con.prepareStatement(xlsql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//.prepareStatement(xlsql);
			stmt.setString(1, nf);
			rs=stmt.executeQuery();
			for(int i=0;i<xldataArr.length;i++){
				xldataArr[i]=new JSONArray();
				switch(i){
					case 0:
						xldataArr[i].add(0, "1����    ·");
						break;
					case 1:
						xldataArr[i].add(0, "2����    ��");
						break;
					case 2:
						xldataArr[i].add(0, "3����    ��");
						break;
					case 3:
						xldataArr[i].add(0, "5��GBM���̺Ͱ�������");
						break;
					case 4:
						xldataArr[i].add(0, "7����ͨ������ʩ");
						break;
					case 5:
						xldataArr[i].add(0, "(��)��·�̻�");
						break;
					case 6:
						xldataArr[i].add(0, "(ʮ)�������ƹ��̲����");
						break;
					case 7:
						xldataArr[i].add(0, "(ʮһ)������е");
						break;
				}
				xldataArr[i].add(1, 0);
				double sum_qj=0;//ȫ������
				int k = 1;
				double sj_data=0;//�оּ�����
				rs.beforeFirst();//ָ��ص���һ��֮ǰ
				while(rs.next()){					
					sum_qj=addDouble(sum_qj,rs.getDouble(i+1));//�ۼ�
					if(k==1){//�оֵ�����
						sj_data=rs.getDouble(i+1);
						//xxbyData[8]=addDouble(xxbyData[8],sj_data);//С�ޱ���������
					}else if(k!=10){
						if(rs.getDouble(i+1)==0){
							xldataArr[i].add(k,"");
						}else{
							xldataArr[i].add(k,rs.getDouble(i+1));
						}						
						xxbyData[k-2]=addDouble(xxbyData[k-2],rs.getDouble(i+1));//С�ޱ���������
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
			//С�ޱ����������ۼ�
			int[] xxbyNum={9,10,11};
			setAddDataArr(nf,con,objArr,"(һ)С�ޱ���",xxbyNum,8);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBHELPER.closeStatement(stmt);
			DBHELPER.closeResultSet(rs);
		}		
	}
	/*
	 * ��5ˮ��  18
	 */
	private void setSHdData(String nf,Connection con,JSONArray objArr){			
		String shsql="select dwid, sum(t.jhtz) rnum "+
				          "from fs_shyfjqxfjhap t "+
				         "where (SFTJ = 1 OR DWID = 282210) "+
				           "and valid = 1 "+
				           "and sfth = 0 "+
				           "and nf = ? "+
				         "group by dwid";
		getDataARR(shsql,nf,"(��)ˮ��Ԥ�������޸�",con,18,objArr);
	}
	
	/*
	 * (��)��·�Ľ����ƹ���
	 */
	private void setGZGJGSGCData(String nf,Connection con,JSONArray objArr){
		//ȡСά�ѱ����24��26������
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
						"AND (SFTJ = 1 OR DWID = 282210)  AND SFTH = 0  group by dwid";//�߸�		
		JSONArray xgDataArr =getDataARR(xgsql,nf,"1����    ��",con,20,objArr);
		String qhgjsql="select dwid, sum(Bndjhtzbxhj) rnum  from Fs_Qhgjjhap where (SFTJ = 1 OR DWID = 282210)"+
												" and valid = 1   and sfth = 0  and nf = ? group by dwid";
		JSONArray qhgjDataArr=getDataARR(qhgjsql,nf,"4����    ��",con,23,objArr);//�ź��Ľ�
		
		//���������޸�
		String qxxfSql="select 282210 dwid,fs_utils.fun_count_xxf(?) rnum  from dual";
		JSONArray qxxfDataArr=getDataARR(qxxfSql,nf,"8����������--�����޸�",con,27,objArr);
		
		//(��)��·�Ľ����ƹ���    �ۼ�
		int[] glnum={20,23,24,26,27};
		setAddDataArr(nf,con,objArr,"(��)��·�Ľ����ƹ���",glnum,19);
		//���Ͽ��е�
		objArr.add(getEmptyLine("2������ˮ��·",21));
		objArr.add(getEmptyLine("3��������·",22));
		objArr.add(getEmptyLine("6����������·����",25));
		objArr.add(getEmptyLine("9������վ",28));
		objArr.add(getEmptyLine("(��)�½����̷�",29));
		objArr.add(getEmptyLine("1����   ·",30));
		objArr.add(getEmptyLine("2����   ��",31));
	}
	/*
	 * ���ɿ�֧��
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
		JSONArray[] dkdataArr=new JSONArray[5];//��������
		try {
			stmt=con.prepareStatement(dkSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, nf);
			rs=stmt.executeQuery();
			for(int i=0;i<5;i++){
				dkdataArr[i]=new JSONArray();
				int lineNum=i+32;
				switch(lineNum){
					case 32:
						dkdataArr[i].add(0,"(��)�ɿ�֧��");
						break;
					case 33:
						dkdataArr[i].add(0,"1��С ά ��");
						break;
					case 34:
						dkdataArr[i].add(0,"2���� �� ��");
						break;
					case 35:
						dkdataArr[i].add(0,"3�������½�ά��");
						break;
					case 36:
						dkdataArr[i].add(0,"4���ɿ���ͷ����");
						break;
				}
				dkdataArr[i].add(1, 0);
				double sum_qj=0;//ȫ������
				int k = 1;
				double sj_data=0;//�оּ�����
				rs.beforeFirst();//ָ��ص���һ��֮ǰ
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
	 * (��)��(��)������
	 */
	private void setDBFCData(String nf,Connection con,JSONArray objArr){
		String dbfSql="select dwid, sum(bnaptz) rnum from Fs_Dbfjsap where (SFTJ = 1 OR DWID = 282210) "+
							"and valid = 1 and sfth = 0 and nf = ? group by dwid ";
		getDataARR(dbfSql,nf,"(��)��(��)������",con,38,objArr);	
	}
	
	/*
	 * ������·��ҵ��
	 */
	private void setYLSYFCData(String nf,Connection con,JSONArray objArr){
		//���п�����
		String kySql="SELECT dwid, SUM(KYBZTZ) rnum  FROM FS_GLFAPB "+
						 "WHERE (DWID <> 282210 or DWMC like '%������%')  " +
						 "AND NF = ?  AND VALID = 1 group by dwid";
		getDataARR(kySql,nf,"2�����м�����������",con,44,objArr);
		
		//3��������������ѵ��
		String xqjySql="SELECT dwid, SUM(DGTXCJYPXF) rnum  FROM FS_GLFAPB "+
				 "WHERE (DWID <> 282210 or DWMC like '%������%')  " +
				 "AND NF = ?  AND VALID = 1 group by dwid";
		getDataARR(xqjySql,nf,"3��������������ѵ��",con,45,objArr);
		
		//4��·����ͨ�������
		String lkjtSql="SELECT dwid, SUM(LKJJTF) rnum  FROM FS_GLFAPB "+
				 "WHERE (DWID <> 282210 or DWMC like '%������%')  " +
				 "AND NF = ?  AND VALID = 1 group by dwid";		
		getDataARR(lkjtSql,nf,"4��·����ͨ�������",con,46,objArr);
		//8��·���ѹ����
		String lzSql="SELECT dwid, SUM(LZJF) rnum  FROM FS_GLFAPB "+
				 "WHERE (DWID <> 282210 or DWMC like '%������%')  " +
				 "AND NF = ?  AND VALID = 1 group by dwid";
		getDataARR(lzSql,nf,"8��·���ѹ����",con,50,objArr);
		//���������
		String xzglfSql="SELECT decode(dwid,0,282210,dwid) dwid, SUM(HZGLFZJ) rnum FROM FS_GLFAPB "+
							 "WHERE (DWID <> 282210 or DWMC like '%�ֻ���%') "+
							   "AND NF = ? AND VALID = 1 group by dwid";
		getDataARR(xzglfSql,nf,"9�����������",con,51,objArr);
		//10��ר���
		String zxjfSql="select  decode(dwid,0,282210,dwid) dwid,sum(hzglfzj) rnum "+
				" from fs_glfapb where dwmc like '������ר��'   and nf =?  and valid=1   group by dwid";
		getDataARR(zxjfSql,nf,"10��ר���",con,52,objArr);
		
		//��·��ҵ��
		int[] ylnum={44,45,46,50,51,52};
		setAddDataArr(nf,con,objArr,"������·��ҵ��",ylnum,42);
		
		objArr.add(getEmptyLine("1�������������",43));
		objArr.add(getEmptyLine("5��ְ�����Ὠ���",47));
		objArr.add(getEmptyLine("6�������������",48));
		objArr.add(getEmptyLine("7����������",49));
	}
	
	/*������·�����ѵĿ���
	 * �ġ��γ�����
	 */
	private void setZCJFdATA(String nf,Connection con,JSONArray objArr){
		objArr.add(getEmptyLine("������·������",53));
		objArr.add(getEmptyLine("1��ְ�����ݻ���",54));
		objArr.add(getEmptyLine("2���Ͷ�����",55));
		objArr.add(getEmptyLine("2����   ��",56));
		objArr.add(getEmptyLine("3������Ԥ����",57));
		//�γ�����
		String zcjfSql="SELECT dwid, SUM(ZCJF) rnum  FROM FS_GLFAPB "+
						" WHERE NF = ? AND VALID = 1 group by dwid";
		getDataARR(zcjfSql,nf,"�ġ��γ�����",con,58,objArr);
		objArr.add(getEmptyLine("",59));//����һ�����У��Ա���ʾ���һ��
	}
	
	
	/*
	 * objArr�ڵ��������
	 * dwmc��λ���ƣ�addLine��ӵ�������lineNum���ݴ洢��������
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
	 * ѭ��ȡ���ݲ�����objArr��
	 */
	private JSONArray getDataARR(String sql,String nf,String dwmc,Connection con,int lineNum,JSONArray objArr){
		//��װsql
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
			double sj_data=0;//�оּ�����
			double sum_qj=0;//ȫ���ۼ�
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
	 * ��ӿ��е�����
	 */
	private JSONObject getEmptyLine(String dwmc,int lineNum){
		JSONArray dataArr =new JSONArray();	
		dataArr.add(0, dwmc);
		for(int i=1;i<11;i++){
			dataArr.add(i, "");//������
		}
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("id",lineNum);
		jsonObj.put("data", dataArr);
		return jsonObj;	
	}
	
	/*
	 * ��������Ӵ���
	 */
	private Double addDouble(Double a ,Double b){
		BigDecimal  a1 = new BigDecimal(a.toString());
		BigDecimal b1=new BigDecimal(b.toString());	
		return new Double(a1.add(b1).doubleValue());
	}

	/*
	 * ��ȡ���������
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
