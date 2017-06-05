package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;

/**
 * ����ͬOracle���ݿ���ص�һЩ�����༰���̺���<br>
 * ���಻��������ʵ�� ֻ��ͨ����̬���� instance()��ȡΨһʵ��<br>
 * 
 * @author ������
 * @version 1.0
 */
public final class DBConnProvider4Oracle extends DBConnProviderBase {

    private OracleDataSource oraDS = null;

    @Override
    protected void initDataSourceExtParams() throws SQLException {
        datasource.setValidationQuery("select 1 from dual");
        
        oraDS = new OracleConnectionPoolDataSource();
        oraDS.setDriverType("thin");
        oraDS.setURL(getJdbcUrl());
        oraDS.setUser(this.getUserName());
        oraDS.setPassword(this.getPassword());
    }

    @Override
    protected String getJdbcUrl() {
        return isExistsJdbcUrl ? this.jdbcUrl : "jdbc:oracle:thin:@" + getServerIp() + ":" + getPort() + ":" + getDatabaseName();
    }

    @Override
    protected String getJdbcDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    private static final DBConnProvider4Oracle INSTANCE = new DBConnProvider4Oracle();

    public static DBConnProvider4Oracle getInstance() {
        return DBConnProvider4Oracle.INSTANCE;
    }

    @Override
    protected void clearRuntimeTempProcedure() {
        // TODO Auto-generated method stub

    }

    @Override
    public Connection getNativeConnection() throws SQLException {
        return oraDS.getConnection();
    }

}
