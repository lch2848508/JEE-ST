package com.estudio.web.servlet.webclient;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;
import com.estudio.web.servlet.BaseServlet;

public class UniqueIDServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = 8300482264615466909L;
    private Object lockObject = new Object();

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = request.getParameter("o");
        if (StringUtils.equals(operation, "get"))
            getUniqeID(request, response);
        else if (StringUtils.equals(operation, "getSysdate"))
            getSysdate(request, response);
        else if (StringUtils.equals(operation, "getSerialNumber"))
            getSerialNumber(request, response);
    }

    /**
     * ªÒ»°–Ú¡–∫≈
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    private void getSerialNumber(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        String format = request.getParameter("f");
        String serialTemplate = StringUtils.substringBetween(format, "<", ">");
        Connection con = null;
        CallableStatement callStmt = null;
        try {
            con = DBHELPER.getConnection();
            Calendar c = Calendar.getInstance();
            format = StringUtils.replaceEach(format, new String[] { "YYYY", "MM", "DD" }, new String[] { Integer.toString(c.get(Calendar.YEAR)), String.format("%02d", c.get(Calendar.MONTH) + 1), String.format("%02d", c.get(Calendar.DATE)) });
            callStmt = con.prepareCall("{call proc_generate_serial(?,?)}");
            callStmt.registerOutParameter(2, java.sql.Types.BIGINT);
            callStmt.setString(1, format);
            callStmt.execute();
            long serialNum = callStmt.getLong(2);
            format = StringUtils.replace(format, "<" + serialTemplate + ">", String.format("%0" + serialTemplate.length() + "d", serialNum));
            json.put("serial", format);
            json.put("r", true);
        } finally {
            DBHELPER.closeStatement(callStmt);
            DBHELPER.closeConnection(con);
        }
        response.getWriter().println(json.toString());
    }

    private void getSysdate(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final JSONObject json = new JSONObject();
        final String format = request.getParameter("f");
        json.put("sysdate", DBHELPER.getSysdate(format));
        json.put("r", true);
        response.getWriter().println(json.toString());
    }

    private void getUniqeID(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        synchronized (lockObject) {
            final JSONObject json = new JSONObject();
            final int cached = getParamInt("cached", 1);
            json.put("ids", DBHELPER.getUniqueIDS(cached));
            json.put("r", true);
            response.getWriter().println(json.toString());
        }
    }
}
