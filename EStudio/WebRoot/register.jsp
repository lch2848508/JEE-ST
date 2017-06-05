<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="js/jslib/utils.js"></script>
<title>用户注册</title>
<style type="text/css">
* 
{
	padding:0px;
	margin:0px;
}
html {
	width:100%;
	height:100%;
}

body
{
	width: 100%;
	height: 100%;
	font-family: "微软雅黑", "新宋体";
	font-size: 14px;
}

td
{
	padding: 4px;
	border: 1px dotted #CCC;
}

.text100
{
	height: 100%;
	width: 810px;
	font-family: "微软雅黑", "新宋体";
	font-size: 14px;
	padding:8px;
}
.info {
	color: #F00;
}
.x1 {
	font-weight: normal;
}
.x1 {
	font-weight: normal;
}
.info strong {
	color: #000;
}
.xred {
	color: #F00;
}
</style>

<script>
function getParams()
{
	var result = {};
	result.user = StringUtils.trim(text_user.value);
	result.sex = text_sex1.checked?1:0;
	result.cardid = StringUtils.trim(text_cardid.value);
	result.address= StringUtils.trim(text_address.value);
	result.email = StringUtils.trim(text_email.value);
	result.mobile = StringUtils.trim(text_mobile.value);
	result.code = StringUtils.trim(text_code.value);
	if(StringUtils.isEmpty(result.user) || StringUtils.isEmpty(result.cardid) || StringUtils.isEmpty(result.address)
	   || StringUtils.isEmpty(result.mobile) || StringUtils.isEmpty(result.code))
	    return null;
	return result;
}
</script>

</head>

<body>
<table border="1" cellspacing="0" cellpadding="0" style="border-collapse: collapse;">
  <tr>
    <td width="125" > 姓名</td>
    <td>
      <input type="text" name="text_user" id="text_user" class="text100"></td>
  </tr>
  <tr>
    <td width="125" >性别</td>
    <td><input name="text_sex" type="radio" id="text_sex1" value="男" checked>
      <span class="x1">男</span>
<input type="radio" name="text_sex" id="text_sex2" value="女">
    <span class="x1">女</span></td>
  </tr>
  <tr>
    <td width="125" >身份证号码</td>
    <td><input name="text_cardid" type="text" class="text100" id="text_cardid" style="font-family:'Courier New'"></td>
  </tr>
  <tr>
    <td width="125" >手机号码</td>
    <td><input name="text_mobile" type="text" class="text100" id="text_mobile" style="font-family:'Courier New'"></td>
  </tr>
  <tr>
    <td width="125" >联系地址</td>
    <td><input name="text_address" type="text" class="text100" id="text_address"></td>
  </tr>
  <tr>
    <td width="125" >电子邮箱</td>
    <td><input name="text_email" type="text" class="text100" id="text_email" style="font-family:'Courier New'"></td>
  </tr>
  <tr>
    <td width="125" >校验码</td>
    <td><input name="text_code" type="text" class="text100" style="width:200px" id="text_code" style="font-family:'Courier New'">
    <img name="imgcode" id="imgcode" src="servlet/verifyimage?t=register" /> 
    </td>
  </tr>
  <tr>
  	<td colspan="2" style="padding-top:12px;padding-bottom:12px;">
    <span class="xred"><strong>注意：</strong></span><span class="info"><strong>以上内容都必须正确输入，每个手机号只能注册一次，注册完毕后，您的登录密码将以短信的方式通知您。</strong></span><strong></strong></td>
  </tr>
</table>

</body>
</html>
