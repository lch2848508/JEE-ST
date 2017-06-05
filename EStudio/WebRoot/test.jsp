<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="js/jslib/jquery/jquery.js"/>
<script type="text/javascript" src="js/jslib/jquery/jquery.cookie.js"></script>
<script type="text/javascript">
//	var data="{"r9dataArr":["1、线　　路",0,218.0,57.2,1802.6,2013.17,1144.94,1248.89,0.0,0.0,0.0]}";
   $(function(){
   	   $.post("DciDataManage",{
   	   		o:"saveservices",
   	   		u:"admin",
   	   		p:"qwer@qaz",
   	   		name:"服务",
   	   		type:"tile",
   	   		url:"http://127.0.0.1:6080/arcgis/rest/services/GDS/GLXZ2013/MapServer"
   	   },ajaxSuccess);
   });
   
   function ajaxSuccess(responseText, statusText){
   		document.write(responseText);
   }
</script>
</head>
<body>
	
</body>
</html>