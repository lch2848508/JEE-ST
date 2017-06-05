<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String idcard = request.getParameter("id");
    String result = "";
    if (!StringUtils.isEmpty(idcard)) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = RuntimeContext.getDbHelper().getConnection();
            stmt = con.prepareStatement("select address from sys_ext_idcard2address where code=?");
            stmt.setString(1, idcard.substring(0, 6));
            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next())
                result = rs.getString(1);
        } finally {
            RuntimeContext.getDbHelper().closeStatement(stmt);
            RuntimeContext.getDbHelper().closeConnection(con);
        }
    }
%>
<%=result%>