package com.estudio.impl.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import oracle.jdbc.OracleTypes;

import com.estudio.define.db.DBException;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.ISQLTrans;

public final class DBHelper4Oracle extends DBHelper {

    /**
     * 私有构造函数 只允许使用类的静态成员函数 不允许创建该类类实例
     */
    private DBHelper4Oracle() {
        super();
    }

    private static DBHelper4Oracle instance = new DBHelper4Oracle();

    public static DBHelper4Oracle getInstance() {
        return instance;
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
        return DBConnProvider4Oracle.getInstance().getConnection();
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
        return new DBCommand4Oracle(con, SQL, isSkipParserSQL);
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
        final Connection con = getConnection();
        final IDBCommand cmd = getCommand(con, "select SEQ_FOR_J2EE_UNIQUEID.nextval from dual");
        cmd.executeQuery();
        cmd.next();
        final long result = cmd.getLong(1);
        closeCommand(cmd);
        closeConnection(con);
        return result;
    }

    /**
     * 测试Oracle数据库是否存在
     * 
     * @param storageTableName
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private boolean isOracleTableExists(final String storageTableName) throws Exception {
        final Connection con = getConnection();
        final IDBCommand cmd = getCommand(con, "select count(*) from user_all_tables where table_name = ?");
        cmd.setParam(1, storageTableName.toUpperCase());
        cmd.executeQuery();
        cmd.next();
        final boolean result = cmd.getLong(1) != 0;
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
        return isOracleTableExists(storageTableName);
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
        final ResultSet resultSet = con.createStatement().executeQuery("select SEQ_FOR_J2EE_UNIQUEID.nextval from dual");
        resultSet.next();
        final long result = resultSet.getLong(1);
        resultSet.getStatement().close();
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
        try {
            if (tempCon == null)
                tempCon = getConnection();
            final ResultSet resultSet = tempCon.createStatement().executeQuery("select " + seqname + ".nextval from dual");
            resultSet.next();
            final long result = resultSet.getLong(1);
            resultSet.getStatement().close();
            return result;
        } finally {
            if ((tempCon != con) && (tempCon != null))
                closeConnection(tempCon);
        }
    }

    @Override
    public String[] getUniqueIDS(final int cached) throws Exception {
        final String[] result = new String[cached];
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("select SEQ_FOR_J2EE_UNIQUEID.nextval from dual");
            for (int i = 0; i < cached; i++) {
                final ResultSet rs = stmt.executeQuery();
                rs.next();
                result[i] = rs.getString(1);
            }
        } finally {
            closeStatement(stmt);
            closeConnection(con);
        }
        return result;
    }

    @Override
    public String[] getUniqueIDS(Connection con, int cached, String seqName) throws Exception {
        final String[] result = new String[cached];
        Connection tempCon = con;
        PreparedStatement stmt = null;
        try {
            if (tempCon == null)
                tempCon = getConnection();
            stmt = tempCon.prepareStatement("select " + seqName + ".nextval from dual");
            for (int i = 0; i < cached; i++) {
                final ResultSet rs = stmt.executeQuery();
                rs.next();
                result[i] = rs.getString(1);
            }
        } finally {
            closeStatement(stmt);
            if (tempCon != con && tempCon != null)
                closeConnection(tempCon);
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
        return DBSQLTrans4Oracle.getInstance();
    }

    @Override
    protected int getOutputParamType(final DBParamDataType type) {
        int result = OracleTypes.VARCHAR;
        switch (type) {
        case String:
            result = OracleTypes.VARCHAR;
            break;
        case Int:
        case Long:
            result = OracleTypes.INTEGER;
            break;
        case Float:
        case Double:
            result = OracleTypes.DOUBLE;
            break;
        case Date:
        case Time:
        case DateTime:
            result = OracleTypes.DATE;
            break;
        case Timestampe:
            result = OracleTypes.TIMESTAMP;
            break;
        case Bytes:
            result = OracleTypes.BLOB;
            break;
        case Text:
            result = OracleTypes.CLOB;
            break;
        case Cursor:
            result = OracleTypes.CURSOR;
            break;
        default:
            result = OracleTypes.VARCHAR;
            break;
        }
        return result;
    }

    @Override
    protected void setCursorParam(final CallableStatement stmt, final int index, final Object object) throws SQLException {
        // stmt.setRef(index, object);
        // ((OracleCallableStatement) stmt).setCursor(index, (ResultSet)
        // object);
        // DruidPooledCallableStatement st;
        // st.
    }

    @Override
    public String getSysdate(final String format) throws Exception {
        final String sql = "select to_char(sysdate,:f) from dual";
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("f", format);
        return (String) executeScalar(sql, params, null);
    }

    @Override
    public Connection getNativeConnection() throws Exception {
        return DBConnProvider4Oracle.getInstance().getNativeConnection();
    }

}
