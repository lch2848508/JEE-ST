package com.estudio.third_interface.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

public class DBExtendService {
    // 表单数据源对应关系
    private static Map<String, DataSource> url2DataSource = new HashMap<String, DataSource>();

    /**
     * 获取Oracle数据库连接
     * 
     * @param serverIP
     * @param serverPort
     * @param SID
     * @param userName
     * @param password
     * @return
     * @throws Exception
     */
    public static Connection getOracleConnection(final String serverIP, final int serverPort, final String SID, final String userName, final String password) throws Exception {
        final String jdbcURL = "jdbc:oracle:thin:@" + serverIP + ":" + serverPort + ":" + SID;
        DataSource ds = null;
        synchronized (url2DataSource) {
            ds = url2DataSource.get(jdbcURL);
            if (ds == null) {
                final OracleDataSource oraDS = new OracleDataSource();
                oraDS.setURL(jdbcURL);
                ds = oraDS;
                url2DataSource.put(jdbcURL, ds);
            }
        }
        return ds.getConnection(userName, password);
    }

}
