<%@page import="com.estudio.utils.Convert"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
    Connection con = null;
    PreparedStatement stmt = null;
    String caption = "";
    String content = "";
    String regdate = "";
    String summary = "";
    try {
        String type = request.getParameter("type"); //1 三重一大 0 新闻通告
        String id = request.getParameter("id");
        con = RuntimeContext.getDbHelper().getConnection();
        String sql = StringUtils.equals(type, "1") ? "select bt caption,gsnr content,to_char(gssj,'YYYY-MM-DD') regdate,'   决议结果 赞成票:'||t.typs||' 反对票:'||t.fdps||' 弃权票:'||qqps summary from CD_SZYDNSNR t where id=?" : "select caption, content, regdate, summary from common_news where id=?";
        stmt = con.prepareStatement(sql);
        stmt.setString(1, id);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        caption = StringEscapeUtils.escapeHtml3(rs.getString(1));
        content = Convert.bytes2Str(rs.getBytes(2));
        if (StringUtils.equals(type, "1"))
            content = StringEscapeUtils.escapeHtml3(content);
        content = StringUtils.replace(content, "\n", "</br>");
        regdate = StringEscapeUtils.escapeHtml3(rs.getString(3));
        summary = StringEscapeUtils.escapeHtml3(rs.getString(4));
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        RuntimeContext.getDbHelper().closeConnection(con);
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml">
<HEAD>
<TITLE><%=caption%></TITLE>
<META content="text/html; charset=utf-8" http-equiv=Content-Type>
<META content=IE=7 http-equiv=X-UA-Compatible>
<LINK rel=stylesheet type=text/css href="others/felixReset.css">
<LINK rel=stylesheet type=text/css href="others/global.css">
<LINK rel=stylesheet type=text/css href="others/ny_style.css">
<DIV class="con w1000">
	<DIV class="m_t_10 bg_fff p_8">
		<DIV class=b_01>
			<H1><%=caption%></H1>
			<SPAN>发布时间：<%=regdate%></SPAN>
		</DIV>
		<DIV class=content1 style="font-size:12pt">
			<%=content%></br>
			<div style="font-weight:bold;font-size:12pt">
				<%=(StringUtils.isEmpty(summary) ? "" : summary)%>
			</DIV>
		</DIV>
		</BODY>
</HTML>
