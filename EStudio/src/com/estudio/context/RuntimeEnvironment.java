package com.estudio.context;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.estudio.impl.db.DBConfig4MySQL;
import com.estudio.impl.db.DBConfig4Oracle;
import com.estudio.impl.db.DBConfig4SQLServer;
import com.estudio.impl.db.DBConnProvider4MySQL;
import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.impl.db.DBConnProvider4SQLServer;
import com.estudio.impl.db.DBHelper4MySQL;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.impl.db.DBHelper4SQLServer;
import com.estudio.impl.db.DBRuntimeConfig;
import com.estudio.impl.design.db.DBDiagramService4MySQL;
import com.estudio.impl.design.db.DBDiagramService4Oracle;
import com.estudio.impl.design.db.DBDiagramService4SQLServer;
import com.estudio.impl.design.db.DBEntryService4MySQL;
import com.estudio.impl.design.db.DBEntryService4Oracle;
import com.estudio.impl.design.db.DBEntryService4SQLServer;
import com.estudio.impl.design.objects.DBObjectComboBoxTemplateService4MySQL;
import com.estudio.impl.design.objects.DBObjectComboBoxTemplateService4Oracle;
import com.estudio.impl.design.objects.DBObjectComboBoxTemplateService4SQLServer;
import com.estudio.impl.design.objects.DBObjectFormService4MySQL;
import com.estudio.impl.design.objects.DBObjectFormService4Oracle;
import com.estudio.impl.design.objects.DBObjectFormService4SQLServer;
import com.estudio.impl.design.objects.DBObjectQueryService4MySQL;
import com.estudio.impl.design.objects.DBObjectQueryService4Oracle;
import com.estudio.impl.design.objects.DBObjectQueryService4SQLServer;
import com.estudio.impl.design.objects.DBObjectReportService4MySQL;
import com.estudio.impl.design.objects.DBObjectReportService4Oracle;
import com.estudio.impl.design.objects.DBObjectReportService4SQLServer;
import com.estudio.impl.design.objects.DBObjectTreeService4MySQL;
import com.estudio.impl.design.objects.DBObjectTreeService4Oracle;
import com.estudio.impl.design.objects.DBObjectTreeService4SQLServer;
import com.estudio.impl.design.objects.DBWorkFlowDesignService4MySQL;
import com.estudio.impl.design.objects.DBWorkFlowDesignService4Oracle;
import com.estudio.impl.design.objects.DBWorkFlowDesignService4SQLServer;
import com.estudio.impl.design.portal.DBPortalGroupService4MySQL;
import com.estudio.impl.design.portal.DBPortalGroupService4Oracle;
import com.estudio.impl.design.portal.DBPortalGroupService4SQLServer;
import com.estudio.impl.design.portal.DBPortalItemService4MySQL;
import com.estudio.impl.design.portal.DBPortalItemService4Oracle;
import com.estudio.impl.design.portal.DBPortalItemService4SQLServer;
import com.estudio.impl.design.portal.DBPortalRightService4MySQL;
import com.estudio.impl.design.portal.DBPortalRightService4Oracle;
import com.estudio.impl.design.portal.DBPortalRightService4SQLServer;
import com.estudio.impl.design.user.DBDepartmentService4MySQL;
import com.estudio.impl.design.user.DBDepartmentService4Oracle;
import com.estudio.impl.design.user.DBDepartmentService4SQLServer;
import com.estudio.impl.design.user.DBRoleService4MySQL;
import com.estudio.impl.design.user.DBRoleService4Oracle;
import com.estudio.impl.design.user.DBRoleService4SQLServer;
import com.estudio.impl.design.user.DBUserInfoService4MySQL;
import com.estudio.impl.design.user.DBUserInfoService4Oracle;
import com.estudio.impl.design.user.DBUserInfoService4SQLServer;
import com.estudio.impl.design.user.DBUserManagerService4MySQL;
import com.estudio.impl.design.user.DBUserManagerService4Oracle;
import com.estudio.impl.design.user.DBUserManagerService4SQLServer;
import com.estudio.impl.design.utils.DBCodeAssistService4MySQL;
import com.estudio.impl.design.utils.DBCodeAssistService4Oracle;
import com.estudio.impl.design.utils.DBCodeAssistService4SQLServer;
import com.estudio.impl.design.utils.DBSQLParserService4MySQL;
import com.estudio.impl.design.utils.DBSQLParserService4Oracle;
import com.estudio.impl.design.utils.DBSQLParserService4SQLServer;
import com.estudio.impl.design.utils.DBVersionService4MySQL;
import com.estudio.impl.design.utils.DBVersionService4Oracle;
import com.estudio.impl.design.utils.DBVersionService4SQLServer;
import com.estudio.impl.service.sercure.ClientLoginService4MySQL;
import com.estudio.impl.service.sercure.ClientLoginService4Oracle;
import com.estudio.impl.service.sercure.ClientLoginService4SQLServer;
import com.estudio.impl.webclient.form.DBFormDefineService;
import com.estudio.impl.webclient.portal.DBPortal4ClientGridDefineService;
import com.estudio.impl.webclient.portal.DBPortal4ClientService4MySQL;
import com.estudio.impl.webclient.portal.DBPortal4ClientService4Oracle;
import com.estudio.impl.webclient.portal.DBPortal4ClientService4SQLServer;
import com.estudio.impl.webclient.report.DBReportDefineService;
import com.estudio.impl.webclient.utils.DBAttachmentService4MySQL;
import com.estudio.impl.webclient.utils.DBAttachmentService4Oracle;
import com.estudio.impl.webclient.utils.DBAttachmentService4SQLServer;
import com.estudio.impl.webclient.utils.DBPictureService4MySQL;
import com.estudio.impl.webclient.utils.DBPictureService4Oracle;
import com.estudio.impl.webclient.utils.DBPictureService4SQLServer;
import com.estudio.utils.Config;
import com.estudio.utils.Convert;
import com.estudio.workflow.engine.WFEngineer;
import com.estudio.workflow.storage.WFDBStorage4MySQL;
import com.estudio.workflow.storage.WFDBStorage4Oracle;
import com.estudio.workflow.storage.WFDBStorage4SQLServer;

public class RuntimeEnvironment {

    private static String configName;

    /**
     * 换取环境配置文件名称
     * 
     * @return
     */
    public static String getConfigName() {
        return configName;
    }

    /**
     * 设置环境配置文件名称
     * 
     * @param configName
     */
    public static void setConfigName(final String configName) {
        RuntimeEnvironment.configName = configName;
    }

    /**
     * 从配置文件配置环境
     * @throws SQLException 
     */
    public static void loadConfig() throws SQLException {
        final Config config = Config.getInstance(configName);

        // 配置首台数据库及数据库备份服务
        final String dbtype = config.getString("RDMS", "dbtype");
        if (StringUtils.equalsIgnoreCase(dbtype, "oracle"))
            loadOracleDatabaseParams(config);
        else if (StringUtils.equalsIgnoreCase(dbtype, "sqlserver"))
            loadSQLServerDatabaseParams(config);
        else if (StringUtils.equalsIgnoreCase(dbtype, "mysql"))
            loadMySQLDatabaseParams(config);
        // 工作流引擎初始化
        DBRuntimeConfig.instance.isRelease = RuntimeConfig.getInstance().isRelease();
        WFEngineer.getInstance().init();
    }

    /**
     * 加载MySQL数据库配置
     * 
     * @param config
     * @throws SQLException 
     */
    private static void loadMySQLDatabaseParams(Config config) throws SQLException {
        // 数据库连接
        final String server = config.getString("MySQL", "server");
        final String sid = config.getString("MySQL", "database");
        final String user = config.getString("MySQL", "user");
        final String password = config.getString("MySQL", "password");
        final String maxConnection = config.getString("MySQL", "maxconnection");
        final int port = config.getInt("MySQL", "port");
        final boolean isMonitor = StringUtils.equals("1", config.getString("MySQL", "monitor", "1"));
        final String jdbcUrl = config.getString("MySQL", "jdbcUrl");
        final boolean isUseDruid = Integer.parseInt(config.getString("MySQL", "useDruid", "1")) == 1;
        DBConnProvider4MySQL.getInstance().initParams(jdbcUrl, server, port, sid, user, password, Convert.try2Int(maxConnection, 50), isMonitor, isUseDruid);
        RuntimeContext.setDbHelper(DBHelper4MySQL.getInstance());

        // 设置辅助类
        RuntimeContext.setVersionService(DBVersionService4MySQL.getInstance());
        RuntimeContext.setCodeAssistService(DBCodeAssistService4MySQL.getInstance());
        RuntimeContext.setSqlParserService(DBSQLParserService4MySQL.getInstance());

        // 用户登录服务
        RuntimeContext.setClientLoginService(ClientLoginService4MySQL.getInstance());

        // 数据表设计服务
        RuntimeContext.setDbEntryService(DBEntryService4MySQL.getInstance());
        // 数据库模型设计服务
        RuntimeContext.setDbDiagramService(DBDiagramService4MySQL.getInstance());

        // 用户管理部分
        RuntimeContext.setRoleService(DBRoleService4MySQL.getInstance());
        RuntimeContext.setDepartmentService(DBDepartmentService4MySQL.getInstance());
        RuntimeContext.setUserInfoService(DBUserInfoService4MySQL.getInstance());
        RuntimeContext.setUserManagerService(DBUserManagerService4MySQL.getInstance());

        // 栏目设计
        RuntimeContext.setPortalGroupService(DBPortalGroupService4MySQL.getInstance());
        RuntimeContext.setPortalRightService(DBPortalRightService4MySQL.getInstance());
        RuntimeContext.setPortalItemService(DBPortalItemService4MySQL.getInstance());

        // 表单 报表 工作流设计器
        RuntimeContext.setObjectComboboxTemplateService(DBObjectComboBoxTemplateService4MySQL.getInstance());
        RuntimeContext.setObjectFormService(DBObjectFormService4MySQL.getInstance());
        RuntimeContext.setObjectReportService(DBObjectReportService4MySQL.getInstance());
        RuntimeContext.setObjectTreeService(DBObjectTreeService4MySQL.getInstance());
        RuntimeContext.setObjectWorkFlowService(DBWorkFlowDesignService4MySQL.getInstance());
        RuntimeContext.setObjectQueryService(DBObjectQueryService4MySQL.getInstance());

        // 报表模版
        RuntimeContext.setReportDefineService(DBReportDefineService.getInstance());
        RuntimeContext.setFormDefineService(DBFormDefineService.getInstance());

        // WebClient 运行支持
        RuntimeContext.setPortal4ClientService(DBPortal4ClientService4MySQL.getInstance());
        RuntimeContext.setPortal4ClientGridDefineService(DBPortal4ClientGridDefineService.getInstance());

        // 杂项
        RuntimeContext.setAttachmentService(DBAttachmentService4MySQL.getInstance());
        RuntimeContext.setPictureService(DBPictureService4MySQL.getInstance());

        // 工作流服务
        RuntimeContext.setWfStorage(WFDBStorage4MySQL.getInstance());

        RuntimeContext.setDBConfig(DBConfig4MySQL.instance);

        // NotifyService4Cluster.getInstance().start();
    }

    /**
     * 加载SQLServer数据库
     * 
     * @param config
     * @throws SQLException 
     */
    private static void loadSQLServerDatabaseParams(Config config) throws SQLException {
        // 数据库连接
        final String server = config.getString("SQLSERVER", "server");
        final String sid = config.getString("SQLSERVER", "sid");
        final String user = config.getString("SQLSERVER", "user");
        final String password = config.getString("SQLSERVER", "password");
        final String maxConnection = config.getString("SQLSERVER", "maxconnection");
        final int port = config.getInt("SQLSERVER", "port");
        final boolean isMonitor = StringUtils.equals("1", config.getString("SQLSERVER", "monitor", "1"));
        final String jdbcUrl = config.getString("SQLSERVER", "jdbcUrl");
        final boolean isUseDruid = Integer.parseInt(config.getString("SQLSERVER", "useDruid", "1")) == 1;
        DBConnProvider4SQLServer.getInstance().initParams(jdbcUrl, server, port, sid, user, password, Convert.try2Int(maxConnection, 50), isMonitor, isUseDruid);
        RuntimeContext.setDbHelper(DBHelper4SQLServer.getInstance());

        // 设置辅助类
        RuntimeContext.setVersionService(DBVersionService4SQLServer.getInstance());
        RuntimeContext.setCodeAssistService(DBCodeAssistService4SQLServer.getInstance());
        RuntimeContext.setSqlParserService(DBSQLParserService4SQLServer.getInstance());

        // 用户登录服务
        RuntimeContext.setClientLoginService(ClientLoginService4SQLServer.getInstance());

        // 数据表设计服务
        RuntimeContext.setDbEntryService(DBEntryService4SQLServer.getInstance());
        // 数据库模型设计服务
        RuntimeContext.setDbDiagramService(DBDiagramService4SQLServer.getInstance());

        // 用户管理部分
        RuntimeContext.setRoleService(DBRoleService4SQLServer.getInstance());
        RuntimeContext.setDepartmentService(DBDepartmentService4SQLServer.getInstance());
        RuntimeContext.setUserInfoService(DBUserInfoService4SQLServer.getInstance());
        RuntimeContext.setUserManagerService(DBUserManagerService4SQLServer.getInstance());

        // 栏目设计
        RuntimeContext.setPortalGroupService(DBPortalGroupService4SQLServer.getInstance());
        RuntimeContext.setPortalRightService(DBPortalRightService4SQLServer.getInstance());
        RuntimeContext.setPortalItemService(DBPortalItemService4SQLServer.getInstance());

        // 表单 报表 工作流设计器
        RuntimeContext.setObjectComboboxTemplateService(DBObjectComboBoxTemplateService4SQLServer.getInstance());
        RuntimeContext.setObjectFormService(DBObjectFormService4SQLServer.getInstance());
        RuntimeContext.setObjectReportService(DBObjectReportService4SQLServer.getInstance());
        RuntimeContext.setObjectTreeService(DBObjectTreeService4SQLServer.getInstance());
        RuntimeContext.setObjectWorkFlowService(DBWorkFlowDesignService4SQLServer.getInstance());
        RuntimeContext.setObjectQueryService(DBObjectQueryService4SQLServer.getInstance());

        // 报表模版
        RuntimeContext.setReportDefineService(DBReportDefineService.getInstance());
        RuntimeContext.setFormDefineService(DBFormDefineService.getInstance());

        // WebClient 运行支持
        RuntimeContext.setPortal4ClientService(DBPortal4ClientService4SQLServer.getInstance());
        RuntimeContext.setPortal4ClientGridDefineService(DBPortal4ClientGridDefineService.getInstance());

        // 杂项
        RuntimeContext.setAttachmentService(DBAttachmentService4SQLServer.getInstance());
        RuntimeContext.setPictureService(DBPictureService4SQLServer.getInstance());

        // 工作流服务
        RuntimeContext.setWfStorage(WFDBStorage4SQLServer.getInstance());

        RuntimeContext.setDBConfig(DBConfig4SQLServer.instance);

        // NotifyService4Cluster.getInstance().start();
    }

    /**
     * 加载Oracle数据库参数
     * 
     * @param config
     * @throws SQLException 
     */
    private static void loadOracleDatabaseParams(final Config config) throws SQLException {
        // 数据库连接
        final String server = config.getString("ORACLE", "server");
        final String sid = config.getString("ORACLE", "sid");
        final String user = config.getString("ORACLE", "user");
        final String password = config.getString("ORACLE", "password");
        final String maxConnection = config.getString("ORACLE", "maxconnection");
        final int port = config.getInt("ORACLE", "port");
        final boolean isMonitor = StringUtils.equals("1", config.getString("ORACLE", "monitor", "1"));
        final String jdbcUrl = config.getString("ORACLE", "jdbcUrl");
        final boolean isUseDruid = Integer.parseInt(config.getString("ORACLE", "useDruid", "1")) == 1;
        DBConnProvider4Oracle.getInstance().initParams(jdbcUrl, server, port, sid, user, password, Convert.try2Int(maxConnection, 50), isMonitor, isUseDruid);
        RuntimeContext.setDbHelper(DBHelper4Oracle.getInstance());

        // 设置辅助类
        RuntimeContext.setVersionService(DBVersionService4Oracle.getInstance());
        RuntimeContext.setCodeAssistService(DBCodeAssistService4Oracle.getInstance());
        RuntimeContext.setSqlParserService(DBSQLParserService4Oracle.getInstance());

        // 用户登录服务
        RuntimeContext.setClientLoginService(ClientLoginService4Oracle.getInstance());

        // 数据表设计服务
        RuntimeContext.setDbEntryService(DBEntryService4Oracle.getInstance());
        // 数据库模型设计服务
        RuntimeContext.setDbDiagramService(DBDiagramService4Oracle.getInstance());

        // 用户管理部分
        RuntimeContext.setRoleService(DBRoleService4Oracle.getInstance());
        RuntimeContext.setDepartmentService(DBDepartmentService4Oracle.getInstance());
        RuntimeContext.setUserInfoService(DBUserInfoService4Oracle.getInstance());
        RuntimeContext.setUserManagerService(DBUserManagerService4Oracle.getInstance());

        // 栏目设计
        RuntimeContext.setPortalGroupService(DBPortalGroupService4Oracle.getInstance());
        RuntimeContext.setPortalRightService(DBPortalRightService4Oracle.getInstance());
        RuntimeContext.setPortalItemService(DBPortalItemService4Oracle.getInstance());

        // 表单 报表 工作流设计器
        RuntimeContext.setObjectComboboxTemplateService(DBObjectComboBoxTemplateService4Oracle.getInstance());
        RuntimeContext.setObjectFormService(DBObjectFormService4Oracle.getInstance());
        RuntimeContext.setObjectReportService(DBObjectReportService4Oracle.getInstance());
        RuntimeContext.setObjectTreeService(DBObjectTreeService4Oracle.getInstance());
        RuntimeContext.setObjectWorkFlowService(DBWorkFlowDesignService4Oracle.getInstance());
        RuntimeContext.setObjectQueryService(DBObjectQueryService4Oracle.getInstance());

        RuntimeContext.setReportDefineService(DBReportDefineService.getInstance());
        RuntimeContext.setFormDefineService(DBFormDefineService.getInstance());

        // WebClient 运行支持
        RuntimeContext.setPortal4ClientService(DBPortal4ClientService4Oracle.getInstance());
        RuntimeContext.setPortal4ClientGridDefineService(DBPortal4ClientGridDefineService.getInstance());

        // 杂项
        RuntimeContext.setAttachmentService(DBAttachmentService4Oracle.getInstance());
        RuntimeContext.setPictureService(DBPictureService4Oracle.getInstance());

        // 工作流服务
        RuntimeContext.setWfStorage(WFDBStorage4Oracle.getInstance());

        RuntimeContext.setDBConfig(DBConfig4Oracle.instance);

        // NotifyService4Cluster.getInstance().start();
    }

    /**
     * 保存环境配置
     */
    public static void saveConfig() {
    }
}
