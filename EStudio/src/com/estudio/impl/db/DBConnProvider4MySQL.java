package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mariadb.jdbc.MariaDbDataSource;

public final class DBConnProvider4MySQL extends DBConnProviderBase {

    private MariaDbDataSource ds = new MariaDbDataSource();
    
    @Override
    protected String getJdbcUrl() {
        return isExistsJdbcUrl ? this.jdbcUrl : "jdbc:mariadb://" + getServerIp() + ":" + getPort() + "/" + getDatabaseName();
    }

    @Override
    protected String getJdbcDriverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    private static final DBConnProvider4MySQL INSTANCE = new DBConnProvider4MySQL();

    public static DBConnProvider4MySQL getInstance() {
        return DBConnProvider4MySQL.INSTANCE;
    }

    private DBConnProvider4MySQL() {
        super();
    }

    @Override
    protected void initDataSourceExtParams() throws SQLException {
        ds.setUrl(getJdbcUrl());
        ds.setUser(getUserName());
        ds.setPassword(getPassword());
    }

    @Override
    protected void clearRuntimeTempProcedure() throws Exception {
        String sql = "SELECT r.SPECIFIC_NAME FROM information_schema.ROUTINES r WHERE r.SPECIFIC_NAME LIKE 'prj_proc_temp_%' and r.ROUTINE_TYPE='PROCEDURE' and r.ROUTINE_SCHEMA=DATABASE()";
        Connection con = null;
        Statement stmt = null;
        Statement stmt1 = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                stmt1.execute("drop procedure if exists " + rs.getString(1));
            }
        } finally {
            if (stmt1 != null)
                stmt1.close();
            if (stmt != null)
                stmt.close();
            if (con != null)
                con.close();
        }
    }

    @Override
    public Connection getNativeConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
}
