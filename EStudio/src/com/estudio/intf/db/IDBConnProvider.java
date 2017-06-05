package com.estudio.intf.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ���ݿ����ӹ�Ӧ���ӿ�
 * 
 * @author LSH
 */
public interface IDBConnProvider {

    /**
     * ���ò���
     * @param serverIp
     * @param port
     * @param databaseName
     * @param userName
     * @param password
     * @param maxConnection
     * @param isMonitor
     * @throws SQLException 
     */
    public void initParams(String jdbcUrl, String serverIp, int port, String databaseName, String userName, String password, int maxConnection, boolean isMonitor, boolean isUseDruid) throws SQLException;

    /**
     * 
     * @param serverIp
     * @param port
     * @param databaseName
     * @param userName
     * @param password
     * @param maxConnection
     * @param isMonitor
     * @throws SQLException 
     */
    public void initParams(final String serverIp, final int port, final String databaseName, final String userName, final String password, final int maxConnection, final boolean isMonitor) throws SQLException;
    /**
     * ��ȡ���ݿ�����
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Connection getConnection() throws SQLException;

    /**
     * �������ݿ�����
     * 
     * @throws SQLException
     */
    public void reset() throws SQLException;
}
