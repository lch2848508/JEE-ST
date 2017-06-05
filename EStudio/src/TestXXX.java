import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.regex.Matcher;

//import java.util.regex.Pattern;
//
//import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;

import net.minidev.json.JSONObject;

import org.codehaus.xfire.client.Client;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

//<<<<<<< .mine

public class TestXXX {

	public static String replaceBlank(String str){
		String dist="";
		if(str!=null){
			Pattern p=Pattern.compile("\\s*|\t|\r|\n");
			Matcher m=p.matcher(str);
			dist=m.replaceAll("");
		}
		return dist;
	}
	
	 public static boolean isNullOrEmpty(Object obj) {  
        if (obj == null)  
        	return true;  
        
        if(obj instanceof String){
        	return ((String) obj).length()==0;
        }
        
        if (obj instanceof CharSequence)  
            return ((CharSequence) obj).length() == 0;  
  
        if (obj instanceof Collection)  
            return ((Collection) obj).isEmpty();  
  
        if (obj instanceof Map)  
            return ((Map) obj).isEmpty();  
  
        if (obj instanceof Object[]) {  
            Object[] object = (Object[]) obj;  
            if (object.length == 0) {  
                return true;  
            }  
            boolean empty = true;  
            for (int i = 0; i < object.length; i++) {  
                if (!isNullOrEmpty(object[i])) {  
                    empty = false;  
                    break;  
                }  
            }  
            return empty;  
        }  
        
        return false;  
	 }  
	
	public static void main(String args[]) throws MalformedURLException{
		XmlRpcClientConfigImpl config=new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://127.0.0.1:81/api/gp/"));
		XmlRpcClient client=new XmlRpcClient();
		client.setConfig(config);
//		JSONObject json=new JSONObject();
//		json.put("ip","192.168.1.196");
//		json.put("port", "1521");
//		json.put( "instance", "ORCL");
//		json.put("username", "sde");
//		json.put("password", "sde");
//		json.put("tlist","[['LXBH', 'TEXT'], ['XNQSZH', 'Double'], ['XNZZZH', 'Double']]");
//		json.put("tdata","[['S20', 0.0, 5644.792403], ['S20', 5644.792403, 15508.954324]]");
//		json.put("routelayer", "SDE.GSGLRoutes");
//		json.put("routeid", "LXBH");
//		json.put("project", "4326");
//		json.put("action", "dynamicsegmentation");
		
//		try {
//            // 返回的结果是字符串类型，强制转换res为String类型
//            String res = (String) client.execute("", new Object[]{"127.0.0.1","1521", "ORCL","sde","sde","[['LXBH', 'TEXT'], ['XNQSZH', 'Double'], ['XNZZZH', 'Double']]","[['S20', 0.0, 5644.792403], ['S20', 5644.792403, 15508.954324]]","SDE.GSGLRoutes","LXBH","4326","dynamicsegmentation"});
//            System.out.println(res);
//        } catch (XmlRpcException e11) {
//            e11.printStackTrace();
//        }
		
//		String layerName="GD_4_2013_XLBH";
//		String xlbh="G105";
//		String x=layerName.substring(0, 1);
//		String y=xlbh.substring(0, 1);
//		String x=layerName.substring(3, 4);
//		System.out.println(x);
//		if(layerName.substring(0, 1).equals(xlbh.substring(0, 1))){
//			System.out.println("1");
//		}else{
//			System.out.println("0");
//		}
		
//		String xlbh="G9411";
//		xlbh=xlbh.substring(0, 4);
//		System.out.println(xlbh);
//		for(int i=0;i<3;i++){  
//			  for(int k=0;k<4;k++){  
//			      if(k==2){  
//			         break;  
//			      }  
//			      System.out.println(k);
//			}  
//		}  
		
//		String x="tmp_gis_dy_layer_1062046";
//		System.out.println(StringUtils.substring(x, 8));  
		
//		Object xx="ww";
//		System.out.println(isNullOrEmpty(xx));
//		String captionField ="F24";
//		String x="F1";
//	    String result= StringUtils.substring(captionField, 1);
//	    int int1=Integer.parseInt(result);
//	    int int2=Integer.parseInt(StringUtils.substring(x, 1));
//	    
//	    
//	    System.out.println("int1="+int1);
//	    System.out.println("int2="+int2);
		
//		String connect_layer="{843945} {843939}";
//		connect_layer=connect_layer.replace("{", "");
//		connect_layer=connect_layer.replace("}", "");
//		String[] layerids=connect_layer.split(" ");
//		System.out.println(""+layerids.length);
//		
//		for(int i=0;i<layerids.length;i++){
//			System.out.println(layerids[i]);
//		}
	     //创建了一个哈希表的对象hash，初始容量为2，装载因子为0.8
		
//		System.out.println(replaceBlank("just do it!"));
//		
//		String xx="人       力           畜力车 （辆/日）-人力车";
//		System.out.println("x="+xx);
//		
//		xx=xx.replace(" ", "");
//		System.out.println("xx="+xx);
//		List<String> fieldList = new ArrayList<String>();
//		
//		for(int i=0;i<5;i++){
//			fieldList.add(i+"");
//		}
//		System.out.println(fieldList);
//
//		for(int j=0;j<fieldList.size();j++){
//			String org=fieldList.get(j);
//			String org1="a."+org;
//			fieldList.set(j, org1);
//		}
//		
//		System.out.println(fieldList);
		
		
//		String roadCoverage="(roadCoverage) as C1";
//		if(roadCoverage.contains("roadCoverage")){
//			System.out.println("true");
//		}
//		
//		String xx="[xxxxxxxx]";
//		int x1=xx.length();
//		System.out.println(xx);
//		
//		xx=xx.substring(1, x1-1);
//		System.out.println(xx);
   }
}
//	public static void main(String[] args) {  
//        // TODO Auto-generated method stub  
//        HashMap<String, String> hm=new HashMap<String,String>();  
//        hm.put("zs", "beijing");  
//        hm.put("ls", "nanjing");  
//        hm.put("was", "beijing");  
//        hm.put("zdds", "shenzhen");  
//        hm.put("ls", "tieling"); // 键相同时，存入后存的值  
//        //取出元素第一种方式：keySet()  
//        //将所有的键取出存在SET中，在通过键取出键所对应的值！  
//        Set<String> keSet=hm.keySet();  
//        for (Iterator<String> iterator = keSet.iterator(); iterator.hasNext();) {  
//            String string = iterator.next();  
//            System.out.println(string+" value: "+hm.get(string));  
//              
//        }  
//        System.out.println("------------------------------------------------------"); 
//        //第二种方式 entrySet()  
//        Set<Map.Entry<String, String>> set2=hm.entrySet();    
//        for (Iterator <Map.Entry<String, String>> iterator = set2.iterator(); iterator.hasNext();) {  
//            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator  
//                    .next();  
//            String key=entry.getKey();  
//            String valueString=entry.getValue();  
//            System.out.println(key+"...."+valueString);  
//              
//        }  
//        System.out.println("------------------------------------------------------");
////        //第三种取出方式 values()  
//        Collection<String> co=hm.values();  
//        for (Iterator<String> iterator = co.iterator(); iterator.hasNext();) {  
//            String string = (String) iterator.next();  
//            System.out.print(string);  
//        }  
//    }  
//}
	
	
//	 public static void main(String[] args) {
//			try {
////				Client client = new Client(new URL("http://127.0.0.1:9284/WebServiceLineRef.asmx?wsdl"));
////				Client client = new Client(new URL("http://192.168.200.135:88/WebServiceLineRef.asmx?wsdl"));
////				Client client = new Client(new URL("http://192.168.200.132:88/WebServiceLineRef.asmx?wsdl"));
////				Object[] results =client.invoke("addition",new Object[]{10.2,201111.2});
////				System.out.println(results[0]);
//				//Object[] resultsexcel =client.invoke("PrepareExcelData",new Object[]{"F:\\城信所\\聂锋→谷磊\\20150203线性参考\\国道交通量_2010.xls"});
//				//System.out.println(resultsexcel[0]);
//				//client.invoke("GetFeatureLayer", new Object[]{"4"});
//				
////				Object[] resultsdeconfig =client.invoke("SetSDEConfig", new Object[]{"localhost","sde","sde","SDE.DEFAULT","5151","localsde"}); EVENTSTABLE20160701135748 LINEREFBASE
////				System.out.println(resultsdeconfig[0]);  14
////				string orcle_sid,string oracle_user,string oracle_pwd,string SDESERVER, string SDEUSER, string SDEPASSWORD, string SDEVERSION, string SDEINSTANCE, string SDENAME, string layerName,string layCode, string inputFileTable,string ljbz,string qszh,string zzzh,string sxzd,string sxtj
////				Object[] resultGetLayer =client.invoke("GetFeatureLayer", new Object[]{"localhost","sde","sde","SDE.DEFAULT","5151","localsde","SDE.高速2ROUTE","XLBH","LINEREFBASE","XLBH","QSZH","JZZH","XLBH","G150"});
//				
////				Object[] resultGetLayer =client.invoke("GetFeatureLayer", new Object[]{"orcl","prjdbfsglj2","prjdbfsglj2","127.0.0.1","sde","sde","SDE.DEFAULT","5151","localsde","SDE.省道ROUTE","XLBH","LINEREFBASE","XLBH","QSZH","JZZH","XLBH",""});
////				Object[] resultGetLayer =client.invoke("GetFeatureLayer", new Object[]{"orcl","prjdbfsglj","prjdbfsglj","192.168.200.132","sde","sde","SDE.DEFAULT","5152","localsde","SDE.省道ROUTE","XLBH","LINEREFBASE","XLBH","QSZH","JZZH","XLBH",""});
////				Object[] resultGetLayer =client.invoke("GetFeatureLayer", new Object[]{"orcl","prjdbfsglj","prjdbfsglj","192.168.200.135","sdedbjtt","sdedbjtt","SDE.DEFAULT","5151","localsde","SDE.高速ROUTE","ROADCODE2","E:\\database\\公路局\\路面大中修1.xls","XLBH","ZH1NUM","ZH2NUM","XLBH","G150"});
////				System.out.println(resultGetLayer[0]);
//				
//				Client client=new Client(new URL("http://localhost:9284/WebServiceLineRef.asmx?wsdl"));
////				Client client=new Client(new URL("http://127.0.0.1:88/WebServiceLineRef.asmx?wsdl"));
//				//Object[] results =client.invoke("HelloWorld", new Object[]{"xx"});
//				Object[] resultGetLayer =client.invoke("GetFeatureLayer", new Object[]{"orcl","prjdbfsglj2","prjdbfsglj2","127.0.0.1","sde","sde","SDE.DEFAULT","5151","localsde","SDE.省道ROUTE","XLBH","LINEREFBASE","XLBH","QSZH","JZZH","XLBH",""});
//				System.out.println(resultGetLayer[0]);
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//}
//	
//	
//	/**
//	 * @param args
//	 */
////	public static void main(String[] args) {
////		// long id = 1455580;
////		// System.out.println(String.format("%08d", id / 10000));
////
////		String zh3 ="省道S269线界牌至大布沙路口段（K24+508-K30+899）路面改造工程";// "(K15+200-K21,K23-K26+000,K20-K36+025)砼路面挖补加铺改性沥青砼路面";
////		JSONArray zharray= getZHinfo(zh3);
//////		System.out.println(zharray);
////		for(int i=0;i<zharray.size();i++){
////			JSONObject json=(JSONObject)zharray.get(i);
////			System.out.println(json.get("0")+"  "+json.get("1"));
////		}
////		
//////		for(JSONObject zh :zharray){
//////			
//////		}
////		
//////		System.out.println("isfanzi="+isChineseChar("K20+200,呵呵"));
////		testfor();
////	}
//	
//	public static void testfor(){
//		String[] str=null;
//		for(String xx:str){
//			System.out.println(xx);
//		}
//	}
//	
//	public static boolean isChineseChar(String str){
//        boolean temp = false;
//        Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
//        Matcher m=p.matcher(str); 
//        if(m.find()){ 
//            temp =  true;
//        }
//        return temp;
//    }
//	
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
//			System.out.println(zhinfo);
//			String[] zhinfos=zhinfo.split(",");
//			for(String zh:zhinfos){
//	//			System.out.println(zh);
//				JSONObject jsonobject=new JSONObject();
//				String[] zhs=zh.split("-");
//				for(int i=0;i<zhs.length;i++){
//					String zhao=zhs[i];
//					String zh1=zhao.replaceAll("K", "");
//					if(zh1.contains("+")){
//						zh1=zh1.replace("+", "");
//					}else{
//						zh1=zh1+"000";
//					}
//					Double dou=Double.valueOf(zh1);
//					jsonobject.put(i+"", dou);
//	//				System.out.println(dou);
//				} 
//				jsonarray.add(jsonobject);
//	//			System.out.println("-------------------------------------");
//			}
//		}
//		return jsonarray;
//	}
//	
////	if (StringUtils.contains(zh3, "K")) {
////		String[] strs = zh3.split("K");
////		for (String str : strs) {
////			System.out.println("K" + str);
////		}
////	}
////	System.out.println("------------------------------------------------------------------");
////	System.out.println(StringUtils.center(zh3, 3, "K"));
////
////	System.out
////			.println("------------------------------------------------------------------");
////	String[] dist = new String[] { "(K15+200-K21,K23-K26+000)砼路面挖补加铺改性沥青砼路面" };
////	for (String li : dist) {
////		String[] nums = li.split("\\D+");
////		for (String num : nums) {
////			System.out.print(num + "\t");
////		}
////		System.out.println();
////	}
////	System.out
////			.println("------------------------------------------------------------------");
////	KCount(zh3);
//	
//	public static int KCount(String zh3) {
//		int count = 0;
//		String lk = "K";
//		for (int i = 0; i < lk.length(); i++) {
//			if (lk.charAt(i) == 'a') {
//				count++;
//			}
//		}
//		System.out.println(count);
//		return count;
//	}
//
//}
