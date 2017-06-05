package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.jtds.jdbcx.JtdsDataSource;

/**
 * ����ͬOracle���ݿ���ص�һЩ�����༰���̺���<br>
 * ���಻��������ʵ�� ֻ��ͨ����̬���� instance()��ȡΨһʵ��<br>
 * 
 * @author ������
 * @version 1.0
 */
public final class DBConnProvider4SQLServer extends DBConnProviderBase {

    private JtdsDataSource ds = new JtdsDataSource();

    @Override
    protected void initDataSourceExtParams() {
        ds.setServerName(getServerIp());
        ds.setPortNumber(getPort());
        ds.setDatabaseName(getDatabaseName());
        ds.setUser(getUserName());
        ds.setPassword(getPassword());
    }

    @Override
    protected String getJdbcUrl() {
        return isExistsJdbcUrl ? this.jdbcUrl : "jdbc:jtds:sqlserver://" + getServerIp() + ":" + getPort() + "/" + getDatabaseName();
    }

    @Override
    protected String getJdbcDriverClassName() {
        return "net.sourceforge.jtds.jdbc.Driver";
    }

    private static final DBConnProvider4SQLServer INSTANCE = new DBConnProvider4SQLServer();

    public static DBConnProvider4SQLServer getInstance() {
        return DBConnProvider4SQLServer.INSTANCE;
    }

    @Override
    protected void clearRuntimeTempProcedure() {
        // TODO Auto-generated method stub

    }

    @Override
    public Connection getNativeConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
}
