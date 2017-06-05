package com.estudio.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilsLocal {
	public static String replaceBlank(String str){
		String dist="";
		if(str!=null){
			Pattern p=Pattern.compile("\\s*|\t|\r|\n|\r\n");
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
	
	public static List<String> joinPreLabel(List<String> arraylist,String preStr){
		for(int j=0;j<arraylist.size();j++){
			String org=arraylist.get(j);
			String org1=preStr+org;
			arraylist.set(j, org1);
		}
		return arraylist;
	}
	
	public static boolean checkNumber(String str){
		String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
		boolean isNumber=str.matches(regex);
		return isNumber;
	}
	
	public static void main(String args[]){
	     //创建了一个哈希表的对象hash，初始容量为2，装载因子为0.8
		System.out.println(replaceBlank("just do \r\n" +
				                        " it!"));
		
//		String xx="人       力           畜力车 （辆/日）-人力车";
//		System.out.println("x="+xx);
//		
//		xx=xx.replace(" ", "");
//		System.out.println("xx="+xx);
//		
//		List<String> fieldList = new ArrayList<String>();
//		
//		for(int i=0;i<5;i++){
//			fieldList.add(i+"");
//		}
//		
//		System.out.println(joinPreLabel(fieldList,"a."));
   }
}
