package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.estudio.intf.db.IDBConnProvider;

public abstract class DBConnProviderBase implements IDBConnProvider {

    // 定义数据源
    protected DruidDataSource datasource = new DruidDataSource();

    // 参数配置
    private String serverIp = "";
    private int port = 0;
    private String databaseName = "";
    private String userName = "";
    private String password = "";
    private int maxConnection = 50;
    private boolean isMonitor = false;
    protected boolean isExistsJdbcUrl = false;
    protected String jdbcUrl = "";
    private boolean useDruid;

    public boolean isUseDruid() {
        return useDruid;
    }

    /**
     * 构造函数
     * 
     * @param serverIp
     * @param port
     * @param databaseName
     * @param userName
     * @param password
     * @param maxConnection
     */
    protected DBConnProviderBase() {
        super();
    }

    /**
     * 清除临时存储过程
     * 
     * @throws Exception
     */
    protected abstract void clearRuntimeTempProcedure() throws Exception;

    /**
     * 获取数据库连接
     */
    @Override
    public Connection getConnection() throws SQLException {
        return useDruid ? datasource.getConnection() : getNativeConnection();
    }

    /**
     * 获取Jdbc连接URL
     * 
     * @return
     */
    protected abstract String getJdbcUrl();

    /**
     * 获取JDBC驱动程序名称
     * 
     * @return
     */
    protected abstract String getJdbcDriverClassName();

    /**
     * 
     * @param jdbcUrl
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * 初始化DataSource参数
     */
    private void initDataSourceParams() {
        try {
            datasource.setDriverClassName(getJdbcDriverClassName());
            datasource.setUrl(getJdbcUrl());
            datasource.setUsername(userName);
            datasource.setPassword(password);
            datasource.setMaxActive(maxConnection);
            datasource.setTestOnBorrow(false);
            datasource.setTestOnReturn(false);
            datasource.setTestWhileIdle(true);
            datasource.setRemoveAbandoned(true);
            datasource.setClearFiltersEnable(true);
            datasource.setMaxWait(60);
            if (isMonitor)
                datasource.addFilters("stat");
            else
                datasource.setFilters("default");
            datasource.setRemoveAbandonedTimeout(60 * 60 * 24);
        } catch (final Exception e) {
            // ExceptionUtils.printExceptionTrace(e);
            e.printStackTrace();
            // System.out.println(datasource.getFilterClassNames());
        }
    }

    /**
     * 初始化数据源附加参数
     * 
     * @throws SQLException
     */
    protected abstract void initDataSourceExtParams() throws SQLException;

    /**
     * 设置参数
     * 
     * @param serverIp
     * @param port
     * @param databaseName
     * @param userName
     * @param password
     * @param maxConnection
     * @throws SQLException
     */
    @Override
    public void initParams(final String jdbcUrl, final String serverIp, final int port, final String databaseName, final String userName, final String password, final int maxConnection, final boolean isMonitor, final boolean isUseDruid) throws SQLException {
        this.serverIp = serverIp;
        this.port = port;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        this.maxConnection = maxConnection;
        this.isMonitor = isMonitor;
        this.jdbcUrl = jdbcUrl;
        this.isExistsJdbcUrl = !StringUtils.isEmpty(jdbcUrl);
        this.useDruid = isUseDruid;
        initDataSourceParams();
        initDataSourceExtParams();
        try {
            clearRuntimeTempProcedure();
        } catch (final Exception e) {
            e.printStackTrace();
            // ExceptionUtils.printExceptionTrace(e);
        }
    }

    @Override
    public void initParams(final String serverIp, final int port, final String databaseName, final String userName, final String password, final int maxConnection, final boolean isMonitor) throws SQLException {
        initParams("", serverIp, port, databaseName, userName, password, maxConnection, isMonitor, true);
    }

    /**
     * 数据库服务器IP
     * 
     * @return
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * 数据库服务器端口
     * 
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * 数据库名称
     * 
     * @return
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * 数据库用户名
     * 
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 数据库用户密码
     * 
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * 数据库允许的最大连接数
     * 
     * @return
     */
    public int getMaxConnection() {
        return maxConnection;
    }

    @Override
    public void reset() throws SQLException {
        synchronized (datasource) {
            datasource.close();
        }
    }

    public abstract Connection getNativeConnection() throws SQLException;

}
 