<%@page import="java.sql.ResultSet"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page import="com.estudio.utils.Convert"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="com.estudio.intf.db.IDBHelper"%>
<%@page import="com.estudio.context.RuntimeContext"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
    IDBHelper dbHelper = RuntimeContext.getDbHelper();
    Connection con = null;
    PreparedStatement stmt = null;
    String operation = request.getParameter("o");
    JSONObject json = new JSONObject();
    json.put("r", false);
    try {
        con = dbHelper.getConnection();
        if (StringUtils.equals(operation, "get")) {
            String sql = "select caption, type, summary, source, author, regdate, content from tab_common_news where id=?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, Convert.str2Long(request.getParameter("id")));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("caption", rs.getString(1));
                json.put("type", rs.getString(2));
                json.put("summary", rs.getString(3));
                json.put("source", rs.getString(4));
                json.put("author", rs.getString(5));
                json.put("regdate", rs.getString(6));
                json.put("content", Convert.bytes2Str(rs.getBytes(7)));
                json.put("id", request.getParameter("id"));
                json.put("r", true);
            }
        } else if (StringUtils.equals(operation, "save")) {
            String idStr = request.getParameter("id");
            boolean isNew = StringUtils.isEmpty(idStr);
            long id = 0l;
            if (isNew) {
                id = dbHelper.getUniqueID(con);
                String sql = "insert into tab_common_news (id, caption, type, summary, source, author, regdate, content, sortorder, write_userid) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, id);
                stmt.setString(2, request.getParameter("caption"));
                stmt.setString(3, request.getParameter("type"));
                stmt.setString(4, request.getParameter("summary"));
                stmt.setString(5, request.getParameter("source"));
                stmt.setString(6, request.getParameter("author"));
                stmt.setString(7, request.getParameter("regdate"));
                stmt.setBytes(8, Convert.str2Bytes(request.getParameter("richEditor")));
                stmt.setLong(9, id);
                stmt.setLong(10, RuntimeContext.getClientLoginService().getLoginInfo(session).getId());
                stmt.execute();
            } else {
                id = Convert.str2Long(idStr);
                String sql = "update tab_common_news set caption = ?, type = ?, summary = ?, source  = ?, author  = ?, regdate = ?, content = ? where id = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, request.getParameter("caption"));
                stmt.setString(2, request.getParameter("type"));
                stmt.setString(3, request.getParameter("summary"));
                stmt.setString(4, request.getParameter("source"));
                stmt.setString(5, request.getParameter("author"));
                stmt.setString(6, request.getParameter("regdate"));
                stmt.setBytes(7, Convert.str2Bytes(request.getParameter("richEditor")));
                stmt.setLong(8, id);
                stmt.execute();
            }
            json.put("r", true);
            json.put("id", id);
        }
    } finally {
        dbHelper.closeStatement(stmt);
        dbHelper.closeConnection(con);
    }
%>

<%=json%>