package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.estudio.intf.db.IDBConnProvider;

public abstract class DBConnProviderBase implements IDBConnProvider {

    // ��������Դ
    protected DruidDataSource datasource = new DruidDataSource();

    // ��������
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
     * ���캯��
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
     * �����ʱ�洢����
     * 
     * @throws Exception
     */
    protected abstract void clearRuntimeTempProcedure() throws Exception;

    /**
     * ��ȡ���ݿ�����
     */
    @Override
    public Connection getConnection() throws SQLException {
        return useDruid ? datasource.getConnection() : getNativeConnection();
    }

    /**
     * ��ȡJdbc����URL
     * 
     * @return
     */
    protected abstract String getJdbcUrl();

    /**
     * ��ȡJDBC������������
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
     * ��ʼ��DataSource����
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
     * ��ʼ������Դ���Ӳ���
     * 
     * @throws SQLException
     */
    protected abstract void initDataSourceExtParams() throws SQLException;

    /**
     * ���ò���
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
     * ���ݿ������IP
     * 
     * @return
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * ���ݿ�������˿�
     * 
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * ���ݿ�����
     * 
     * @return
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * ���ݿ��û���
     * 
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * ���ݿ��û�����
     * 
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * ���ݿ���������������
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
 