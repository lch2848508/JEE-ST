package com.estudio.gis;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.SecurityUtils;
import com.estudio.utils.ThreadUtils;

public class WebGISMapServerProxy {

    private WebGISMapServerProxy() {

    }

    public static final WebGISMapServerProxy instance = new WebGISMapServerProxy();
    private String cacheFileDirectory;

    /**
     * 
     * @param cacheFileDirectory
     */
    public void setCacheFileDirectory(String cacheFileDirectory) {
        this.cacheFileDirectory = cacheFileDirectory;
    }

    private boolean cacheEnabled = false;
    private String proxyUrl = "";

    public void setCacheEnabled(boolean value) {
        cacheEnabled = value;
    }

    /**
     * 获取代理内容
     * 
     * @param proxyURL
     * @return
     * @throws IOException
     */
    public WebGISProxyItem getGetMethodProxyItem(String serverId, String url, long version) throws Exception {
        WebGISProxyItem result = null;
        String key = SecurityUtils.md5(url) + "-" + version;
        if (cacheEnabled)
            result = (WebGISProxyItem) SystemCacheManager.getInstance().getWebGISItem(key);
        if (result == null) {
            String fileName = "";
            if (!StringUtils.equals(serverId, "-65535")) {
                String fileDir = cacheFileDirectory + serverId + File.separator + version + File.separator + key.substring(0, 2);
                File dir = new File(fileDir);
                if (!dir.exists())
                    FileUtils.forceMkdir(dir);
                fileName = fileDir + File.separator + key + ".bin";
            }
            result = WebClientWebGIS.getWebGISProxyItem(url, fileName);
            if (result != null && cacheEnabled && !result.isError && result.isImage)
                SystemCacheManager.getInstance().putWebGISItem(key, result, 60 * 60 * 12); // 12小时
        }
        return result;
    }

    /**
     * 
     * @param serverId
     * @param proxyURL
     * @param params
     * @return
     * @throws Exception
     */
    public WebGISProxyItem getPostMethodProxyItem(String serverId, String url, Map<String, String> params, long version) throws Exception {
        WebGISProxyItem result = null;
        String key = SecurityUtils.md5(url + StringUtils.join(params.values(), "")) + "-" + version;
        boolean isNoCache = Convert.try2Int(params.get("nocache"), 0) == 1;
        if (!isNoCache && cacheEnabled && !StringUtils.equals(serverId, "-65535"))
            result = (WebGISProxyItem) SystemCacheManager.getInstance().getWebGISItem(key);
        String fileName = "";
        if (result == null) {
            if (!StringUtils.equals(serverId, "-65535") && !isNoCache) {
                String fileDir = cacheFileDirectory + serverId + File.separator + version + File.separator + key.substring(0, 2);
                File dir = new File(fileDir);
                if (!dir.exists())
                    FileUtils.forceMkdir(dir);
                fileName = fileDir + File.separator + key + ".bin";
            }
            result = WebClientWebGIS.getWebGISProxyItem(url, fileName, params);
            if (result != null && cacheEnabled && !StringUtils.equals(serverId, "-65535") && result.isImage)
                SystemCacheManager.getInstance().putWebGISItem(key, result, 60 * 60 * 12);
        }
        return result;
    }

    /**
     * 获取缓存文件目录
     * 
     * @return
     */
    public String getCacheFileDirectory() {
        return cacheFileDirectory;
    }

    public void setProxyUrl(String value) {
        this.proxyUrl = value;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void startDaemon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    IDBHelper dbHelper = RuntimeContext.getDbHelper();
                    Connection con = null;
                    PreparedStatement stmt = null;
                    Map<Long, Long> serverId2Version = new HashMap<Long, Long>();
                    try {
                        con = dbHelper.getConnection();
                        stmt = con.prepareStatement("select id,version from webgis_services");
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next())
                            serverId2Version.put(rs.getLong(1), rs.getLong(2));

                        // 删除文件
                        for (Entry<Long, Long> entry : serverId2Version.entrySet()) {
                            long serverId = entry.getKey();
                            long version = entry.getValue();
                            String serverCacheDir = cacheFileDirectory + serverId;
                            File serverDir = new File(serverCacheDir);
                            if (serverDir.exists() && serverDir.isDirectory()) {
                                File[] subFiles = serverDir.listFiles();
                                for (File subFile : subFiles) {
                                    if (StringUtils.equals(version + "", subFile.getName()))
                                        continue;
                                    if (subFile.isDirectory())
                                        FileUtils.deleteDirectory(subFile);
                                    else
                                        FileUtils.deleteQuietly(subFile);
                                }
                            }
                        }

                    } catch (Exception e) {
                        ExceptionUtils.printExceptionTrace(e);
                    } finally {
                        dbHelper.closeStatement(stmt);
                        dbHelper.closeConnection(con);
                    }

                    ThreadUtils.sleepHour(12);
                }
            }
        }).start();
    }

}
