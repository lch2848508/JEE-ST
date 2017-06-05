package com.estudio.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.estudio.DaemonService.WordPressDaemonService;
import com.estudio.blazeds.services.JMSHelper;
import com.estudio.context.RuntimeConfig;
import com.estudio.context.RuntimeContext;
import com.estudio.context.RuntimeEnvironment;
import com.estudio.context.SystemCacheManager;
import com.estudio.gis.WebGISDaemonService;
import com.estudio.gis.WebGISMapServerProxy;
import com.estudio.gis.WebGISSpatialConfig;
import com.estudio.gis.oracle.WebGISQueryService4Oracle;
import com.estudio.gis.oracle.WebGISResourceService4Oracle;
import com.estudio.gis.oracle.WebGISSpatialAnalyService4Oracle;
import com.estudio.impl.service.sercure.ClientWebService4LineRef;
import com.estudio.gis.service.DynamicSpecialLayerService;
import com.estudio.intf.webclient.utils.IAttachmentService;
import com.estudio.officeservice.ExcelUtils;
import com.estudio.officeservice.FileConvertService;
import com.estudio.officeservice.LicenseService;
import com.estudio.utils.Config;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.ImageUtils;
import com.estudio.utils.JSCompress;
import com.estudio.web.service.DataService4AttachmentEx;
import com.estudio.web.servlet.config.WebParamService;

public class FirstStartMe extends HttpServlet {

    private static final long serialVersionUID = -3561990285162196538L;
    private String appParentPath = "";
    private String appPath = "";
    private String pathPrefix = "";

    /**
     * 服务器启动后立即运行此Servlet</br> 此servlet主要进行一些初始化工作</br> 包括初始化数据库参数</br>
     * 用户管理的照片路径等</br>
     */
    @Override
    public void init() throws ServletException {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        boolean isWin = StringUtils.startsWithIgnoreCase(os, "win");

        appPath = getServletContext().getRealPath("/");
        if (isWin)
            pathPrefix = appPath.substring(0, 2);

        if (!StringUtils.endsWithAny(appPath, new String[] { "/", "\\" }))
            appPath = appPath + File.separator;
        RuntimeContext.setAppTempDir(appPath + "temp" + "/");

        final String paramFileName = appPath + "config/web_app.xml";
        WebParamService.getInstance().init(paramFileName);

        // 日志服务
        appParentPath = new File(appPath).getParent();
        final String loggerPath = appParentPath + "/logger/";
        if (!new File(loggerPath).exists())
            new File(loggerPath).mkdir();
        System.setProperty("WebLoggerPath", loggerPath);
        PropertyConfigurator.configure(appPath + "config/log4j.properties");

        // 加载配置文件
        final String path = appPath + "config/web_config.xml";
        final Config config = Config.getInstance(path);

        // 运行时环境
        RuntimeConfig.getInstance().setVersion(config.getInt("RuntimeConfig", "version"));
        RuntimeConfig.getInstance().setRelease(config.getInt("RuntimeConfig", "release") == 1);
        RuntimeConfig.getInstance().setEnabledUserRegister(config.getInt("RuntimeConfig", "newUserRegister") == 1);
        RuntimeConfig.getInstance().setEnableForgetPassword(config.getInt("RuntimeConfig", "forgetPassword") == 1);
        RuntimeConfig.getInstance().setCityName(config.getString("WEBGIS", "cityname"));

        // 脚本压缩
        JSCompress.getInstance().setEnabled(config.getInt("JSCompress", "compress") == 1);

        // 运行环境配置
        RuntimeEnvironment.setConfigName(path);
        try {
            RuntimeEnvironment.loadConfig();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final String tempPath = appParentPath + "/temp";
        new File(tempPath).mkdirs();
        BaseServlet.setTempFilePath(tempPath);

        // 附件管理
        configAttachmentService(config);

        // Excel缓存
        ExcelUtils.getInstance().setTempPath(appPath + "excel_temp/");
        ExcelUtils.getInstance().startDaemonThread();

        // 杂项设置
        ImageUtils.getInstance().setGrapicsImagePath(fixPath4USBPortable(pathPrefix, config.getString("UTILS", "grapicMagicPath")));

        // 启动后台监视服务
        RuntimeContext.getPictureService().startDaemonThread();

        // 缓存管理服务
        SystemCacheManager.getInstance().init(appPath + "config/user_ehcache.xml");

        // JMS服务
        configJMS(config);

        //线性参考
        try {
			configLineReference(config);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // 配置WEBGIS
        configWebGIS(config);

        // 配置富文本编辑器
        configHTMLEditorAttachment(appPath, config);

        // WordPress
        configWordPressService(config);

        // 天气预报
        // WeatherService.execute();
        
        LicenseService.loadOfficeLicense();
        FileConvertService.instance.start();
    }
    
    //配置线性参考是否可用
    private void configLineReference(Config config) throws MalformedURLException, Exception{
    	if(config.getInt("lineref", "enabled")==1){
    		ClientWebService4LineRef.getInstance().initParams(config.getString("ORACLE", "sid"),config.getString("ORACLE", "user"),config.getString("ORACLE", "password"),config.getString("lineref", "url"),config.getString("lineref","server"),config.getString("lineref", "user"),config.getString("lineref", "password"),config.getString("lineref", "sdeversion"),config.getString("lineref", "sdeinstance"),config.getString("lineref", "sdename"));
    		if(config.getInt("lineref", "daemonThread")==1){
    			ClientWebService4LineRef.getInstance().exceute();
    		}
    	}
    }
    

    private void configWordPressService(Config config) {
        if (config.getInt("WordPress", "enabled") == 1) {
            WordPressDaemonService.getInstance().initParams(config.getString("WordPress", "url"), config.getString("WordPress", "user"), config.getString("WordPress", "password"), config.getInt("WordPress", "sleepTimes"));
            WordPressDaemonService.getInstance().start();
        }
    }

    /**
     * 配置附件管理模块
     * 
     * @param config
     */
    private void configAttachmentService(final Config config) {
        final IAttachmentService attachmentService = RuntimeContext.getAttachmentService();
        attachmentService.setServerType(config.getString("ATTACHMENT", "serverType"));
        attachmentService.setServerUserName(config.getString("ATTACHMENT", "user"));
        attachmentService.setServerUserPassword(config.getString("ATTACHMENT", "password"));
        attachmentService.setServerIp(config.getString("ATTACHMENT", "server"));
        attachmentService.setServerPort(config.getInt("ATTACHMENT", "port"));
        attachmentService.setServerPath(fixPath4USBPortable(pathPrefix, config.getString("ATTACHMENT", "path")));
        attachmentService.setServerURL(config.getString("ATTACHMENT", "url"));
        attachmentService.startDaemonThread();
        DataService4AttachmentEx.getInstance().startDaemonThread();
    }

    private String fixPath4USBPortable(String prefix, String str) {
        if (StringUtils.isEmpty(prefix) || StringUtils.contains(str, ":"))
            return str;
        if (!StringUtils.startsWithIgnoreCase(str, prefix))
            str = prefix + str;
        return str;
    }

    /**
     * 配置JMS服务器
     * 
     * @param config
     */
    private void configJMS(final Config config) {
        try {

            if (config.getInt("JMS", "enabled") == 1) {
                JMSHelper.getInstance().initContextName(config.getString("JMS", "connectionContextName"), config.getString("JMS", "topicContextName"), config.getString("JMS", "queueContextName"));
                JMSHelper.getInstance().initJNDI();
                JMSHelper.getInstance().start();
                RuntimeConfig.getInstance().setJMSEnabled(true);
                RuntimeConfig.getInstance().setICQEnabled(config.getInt("JMS", "icqEnabled") == 1);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 配置WebGIS
     * 
     * @param config
     */
    private void configWebGIS(final Config config) {
        if (config.getInt("WEBGIS", "enabled") == 1) {
            RuntimeConfig.getInstance().setWEBGISEnabled(true);
            // WEBGis缓存服务
            WebGISMapServerProxy.instance.setCacheFileDirectory(config.getString("WEBGIS", "dynamicCacheDirectory"));
            WebGISMapServerProxy.instance.setProxyUrl(config.getString("WEBGIS", "dynamicCacheUrl"));
            WebGISMapServerProxy.instance.setCacheEnabled(config.getInt("WEBGIS", "cacheEnabled") == 1);
            WebGISMapServerProxy.instance.startDaemon();

            int wkid = config.getInt("WEBGIS", "spatialWKID");
            String extent = config.getString("WEBGIS", "spatialExtent");
            double tolerance = config.getDouble("WEBGIS", "spatialTolerance");
            double simplify = config.getDouble("WEBGIS", "spatialSimplify");
            String unit = config.getString("WEBGIS", "spatialUnit");

            WebGISSpatialConfig spatialConfig = new WebGISSpatialConfig();
            spatialConfig.wkid = wkid;
            spatialConfig.extent = extent;
            spatialConfig.tolerance = tolerance;
            spatialConfig.simplify = simplify;
            spatialConfig.unit = StringUtils.equals(unit, "meter");
            spatialConfig.tablespace = config.getString("WEBGIS", "tablespace");
            WebGISResourceService4Oracle.getInstance().initSpatialParams(spatialConfig);
            WebGISDaemonService.getInstance().initSpatialParams(spatialConfig);
            WebGISQueryService4Oracle.getInstance().initSpatialConfig(spatialConfig);
            WebGISSpatialAnalyService4Oracle.instance.initSpatialConfig(spatialConfig);

            // GIS地图服务
            if (config.getInt("WEBGIS", "daemonThread") == 1) {
                WebGISDaemonService.getInstance().start();
            }

            String fromConfigFileName = appPath + "config/webgis_config.js";
            String toConfigFileName = appPath + "WebGIS/webgis_config.js";
            try {
                FileUtils.copyFile(new File(fromConfigFileName), new File(toConfigFileName));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            DynamicSpecialLayerService.instance.execute();
        }
    }

    /**
     * 配置富文本编辑器
     * 
     * @param appPath
     * @param config
     */
    private void configHTMLEditorAttachment(String appPath, final Config config) {
        String ckFindConfigName = appPath + "WEB-INF/ckfind_config.xml";
        if (new File(ckFindConfigName).exists()) {
            try {
                Document dom = new SAXReader().read(new File(ckFindConfigName));
                String ckFindBaseDir = dom.getRootElement().element("baseDir").getText();
                String okCkFindBaseDir = config.getString("HTMLEditor", "baseDir");
                if (!StringUtils.equals(ckFindBaseDir, okCkFindBaseDir)) {
                    dom.getRootElement().element("baseDir").setText(okCkFindBaseDir);
                    dom.getRootElement().element("baseURL").setText(config.getString("HTMLEditor", "url"));
                    FileUtils.writeStringToFile(new File(ckFindConfigName), dom.toString(), "utf-8");
                    XMLWriter writer = new XMLWriter(new FileOutputStream(new File(ckFindConfigName)), new OutputFormat("  ", false));
                    writer.write(dom);
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
