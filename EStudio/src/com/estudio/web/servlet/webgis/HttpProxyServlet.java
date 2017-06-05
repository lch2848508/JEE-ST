package com.estudio.web.servlet.webgis;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.gis.WebClientWebGIS;
import com.estudio.gis.WebGISMapServerProxy;
import com.estudio.gis.WebGISProxyItem;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;

public class HttpProxyServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -1561799001169409313L;
    private IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    /**
     * Constructor of the object.
     */
    public HttpProxyServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    @Override
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String url = request.getQueryString();
        String serverId = StringUtils.substringBetween(request.getRequestURI(), "/proxy/", "/");
        if (StringUtils.isEmpty(serverId))
            serverId = "-65535";
        Long serverIdValue = Convert.str2Long(serverId);
        long version = getServerVersion(serverIdValue);
        if (loginServer(serverIdValue, version)) {
            String proxyURL = url;// StringUtils.substringAfter(url, "?");
            WebGISProxyItem item = null;
            try {
                item = WebGISMapServerProxy.instance.getGetMethodProxyItem(serverId, proxyURL, version);
                if (item != null)
                    sendBytesToClient(response, item);
            } catch (final Exception e) {
                ExceptionUtils.printExceptionTrace(e);
            }
        }
    }

    /**
     * The doPost method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to
     * post.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String url = request.getQueryString();
        String serverId = StringUtils.substringBetween(request.getRequestURI(), "/proxy/", "/");
        Map<String, String> params = new HashMap<String, String>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!StringUtils.containsAny(paramName, "\\/:".toCharArray()))
                params.put(paramName, request.getParameter(paramName));
        }
        if (StringUtils.isEmpty(serverId))
            serverId = "-65535";
        Long serverIdLong = Convert.str2Long(serverId);
        long version = getServerVersion(serverIdLong);
        if (loginServer(serverIdLong, version)) {
            String proxyURL = url;
            WebGISProxyItem item = null;
            try {
                item = WebGISMapServerProxy.instance.getPostMethodProxyItem(serverId, proxyURL, params, version);
                if (item != null)
                    sendBytesToClient(response, item);
            } catch (final Exception e) {
                ExceptionUtils.printExceptionTrace(e);
            }
        }
    }

    /**
     * 获取版本信息
     * 
     * @param serverId
     * @return
     */
    private Object lockServerVersion = new Object();

    private long getServerVersion(Long serverId) {
        synchronized (lockServerVersion) {
            Map<Long, Long> id2version = (Map<Long, Long>) SystemCacheManager.getInstance().getWebGISItem("SERVERID2VERSION");
            if (id2version == null) {
                id2version = getServerVersionList();
                SystemCacheManager.getInstance().putWebGISItem("SERVERID2VERSION", id2version, 5 * 60);
            }
            return id2version.containsKey(serverId) ? id2version.get(serverId) : -1;
        }
    }

    private Map<Long, Long> getServerVersionList() {
        Connection con = null;
        PreparedStatement stmt = null;
        Map<Long, Long> result = new HashMap<Long, Long>();
        try {
            con = DBHELPER.getConnection();
            stmt = con.prepareStatement("select id,version from webgis_services");
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                result.put(rs.getLong(1), rs.getLong(2));
        } catch (Exception e) {

        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return result;
    }

    /**
     * 
     * @param serverId
     * @return
     * @throws Exception
     */
    private Object lockLoginServer = new Object();

    private boolean loginServer(Long serverId, long version) {
        if (serverId != -65535) {
            try {
                String key = "extParams4GISServer-" + serverId + "-" + version;
                synchronized (lockLoginServer) {
                    Map<String, String> extParams = (Map<String, String>) SystemCacheManager.getInstance().getWebGISItem(key);
                    if (extParams == null) {
                        extParams = getServerExtParams(serverId);
                        SystemCacheManager.getInstance().putWebGISItem(key, extParams, 30 * 60);
                        String tokenLoginUrl = extParams.get("getTokenURL");
                        if (!StringUtils.isEmpty(StringUtils.trim(tokenLoginUrl)))
                            WebClientWebGIS.get(tokenLoginUrl);
                    }
                }
            } catch (Exception e) {
                ExceptionUtils.printExceptionTrace(e);
            }
        }
        return true;
    }

    private Map<String, String> getServerExtParams(long id) throws Exception {
        Map<String, String> extParams = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement extStmt = null;
        try {
            con = DBHELPER.getConnection();
            extStmt = con.prepareStatement("select param_name,param_value,data_type from webgis_service_params where p_id=?");
            extStmt.setLong(1, id);
            ResultSet extRs = extStmt.executeQuery();
            while (extRs.next())
                extParams.put(extRs.getString(1), extRs.getString(2));
        } finally {
            DBHELPER.closeStatement(extStmt);
            DBHELPER.closeConnection(con);
        }
        return extParams;
    }

    /**
     * 
     * @param response
     * @param item
     * @throws IOException
     */
    private void sendBytesToClient(HttpServletResponse response, WebGISProxyItem item) throws IOException {
        response.reset();
        if (item.isImage)
            response.setHeader("Cache-Control", "max-age=2592000");
        else if (!item.isError)
            response.setHeader("Cache-Control", "max-age=10800");
        else
            response.setHeader("Cache-Control", "max-age=60");
        response.setContentType(item.contentType);
        response.addHeader("Content-Length", "" + item.content.length);
        BufferedOutputStream stream = null;
        try {
            stream = new BufferedOutputStream(response.getOutputStream());
            stream.write(item.content);
            stream.flush();
        } catch (Exception e) {

        } finally {
            if (stream != null)
                stream.close();
        }
    }

}
