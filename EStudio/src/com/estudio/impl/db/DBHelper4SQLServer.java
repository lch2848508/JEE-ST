package com.estudio.impl.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.estudio.define.db.DBException;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.ISQLTrans;

public final class DBHelper4SQLServer extends DBHelper {

    /**
     * 私有构造函数 只允许使用类的静态成员函数 不允许创建该类类实例
     */
    private DBHelper4SQLServer() {
        super();
    }

    private static final DBHelper4SQLServer INSTANCE = new DBHelper4SQLServer();

    public static DBHelper4SQLServer getInstance() {
        return INSTANCE;
    }

    /**
     * 得到数据库连接
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public Connection getConnection() throws Exception {
        return DBConnProvider4SQLServer.getInstance().getConnection();
    }

    /**
     * 得到ICommand
     * 
     * @param con
     *            数据库连接
     * @param SQL
     *            SQL语句
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public IDBCommand getCommand(final Connection con, final String SQL, final boolean isSkipParserSQL) throws Exception {
        return new DBCommand4SQLServer(con, SQL, isSkipParserSQL);
    }

    /**
     * 得到唯一标识号
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public long getUniqueID() throws Exception {
        long result = 0;
        final Connection con = getConnection();
        CallableStatement stmt = con.prepareCall("{call proc_general_global_sequence(?)}");
        stmt.registerOutParameter(1, java.sql.Types.BIGINT);
        stmt.execute();
        result = stmt.getLong(1);
        stmt.close();
        con.close();
        return result;
    }

    /**
     * 测试Pg数据库是否存在
     * 
     * @param storageTableName
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private boolean isSQLServerTableExists(final String storageTableName) throws Exception {
        final Connection con = getConnection();
        final IDBCommand cmd = getCommand(con, "SELECT COUNT(*) FROM view_sys_user_tables where upper(table_name) = upper(?)");
        cmd.setParam(1, storageTableName.toUpperCase());
        cmd.executeQuery();
        cmd.next();
        final boolean result = cmd.getInt(1) != 0;
        cmd.close();
        con.close();
        return result;
    }

    /**
     * 判断数据库表是否存在
     * 
     * @param storageTableName
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    @Override
    public boolean isTableExists(final String storageTableName) throws Exception {
        return isSQLServerTableExists(storageTableName);
    }

    /**
     * 得到全局唯一索引值
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public long getUniqueID(final Connection con) throws Exception {
        CallableStatement stmt = con.prepareCall("{call proc_general_global_sequence(?)}");
        stmt.registerOutParameter(1, java.sql.Types.BIGINT);
        stmt.execute();
        long result = stmt.getLong(1);
        stmt.close();
        return result;
    }

    /**
     * 取的唯一索引值
     * 
     * @param connection
     * @param string
     *            队列名称
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public long getUniqueID(final Connection con, final String seqname) throws Exception {
        Connection tempCon = con;
        CallableStatement stmt = null;
        long result = -1;
        try {
            if (tempCon == null)
                tempCon = getConnection();
            stmt = tempCon.prepareCall("{call proc_general_sequence_by_name(?,?)}");
            stmt.registerOutParameter(2, java.sql.Types.BIGINT);
            stmt.setString(1, seqname);
            stmt.execute();
            result = stmt.getLong(2);
        } finally {
            if (stmt != null)
                stmt.close();
            if ((tempCon != con) && (tempCon != null))
                closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public String[] getUniqueIDS(final int cached) throws Exception {
        final String[] result = new String[cached];
        Connection con = null;
        CallableStatement stmt = null;
        try {
            con = getConnection();
            stmt = con.prepareCall("{call proc_general_global_sequence(?)}");
            stmt.registerOutParameter(1, java.sql.Types.BIGINT);
            for (int i = 0; i < cached; i++) {
                stmt.execute();
                result[cached - i - 1] = stmt.getString(1);
            }
        } finally {
            closeStatement(stmt);
            closeConnection(con);
        }
        return result;
    }

    /**
     * 得到附件
     * 
     * @param tablename
     * @param id
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public byte[] getBlob(final String tablename, final String blobField, final long id, final Connection con) throws Exception {
        byte[] result = null;
        Connection tempCon = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            tempCon = con == null ? getConnection() : con;
            final String sql = "select " + blobField + " from " + tablename + " where " + blobField + " is not null and id=?";
            stmt = tempCon.prepareStatement(sql);
            stmt.setLong(1, id);
            resultSet = stmt.executeQuery();
            if (resultSet.next())
                result = resultSet.getBytes(1);
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (stmt != null)
                stmt.close();
            if ((tempCon != con) && (tempCon != null))
                closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public ISQLTrans getSQLTrans() {
        return DBSQLTrans4SQLServer.getInstance();
    }

    @Override
    protected int getOutputParamType(final DBParamDataType type) {
        int result = java.sql.Types.VARCHAR;
        switch (type) {
        case String:
            result = java.sql.Types.VARCHAR;
            break;
        case Int:
            result = java.sql.Types.INTEGER;
            break;
        case Long:
            result = java.sql.Types.BIGINT;
            break;
        case Float:
        case Double:
            result = java.sql.Types.DOUBLE;
            break;
        case Date:
            result = java.sql.Types.DATE;
            break;
        case Time:
            result = java.sql.Types.TIME;
            break;
        case DateTime:
            result = java.sql.Types.DATE;
            break;
        case Timestampe:
            result = java.sql.Types.TIMESTAMP;
            break;
        case Bytes:
            result = java.sql.Types.BLOB;
            break;
        case Text:
            result = java.sql.Types.CLOB;

            break;
        case Cursor:
            result = java.sql.Types.REF;
            break;
        default:
            result = java.sql.Types.VARCHAR;
            break;
        }
        return result;
    }

    @Override
    protected void setCursorParam(final CallableStatement stmt, final int index, final Object object) throws SQLException {
        //

    }

    @Override
    public String getSysdate(final String format) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getUniqueIDS(Connection connection, int size, String string) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection getNativeConnection() throws Exception {
        return DBConnProvider4SQLServer.getInstance().getNativeConnection();
    }

}
