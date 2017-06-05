package com.estudio.impl.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.minidev.json.JSONArray;

import com.estudio.define.db.DBException;
import com.estudio.intf.db.CallableStmtParamDefineAndValue;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.ICallableStmtAction;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.ISQLTrans;

public final class DBHelper4MySQL extends DBHelper {

    /**
     * 私有构造函数 只允许使用类的静态成员函数 不允许创建该类类实例
     */
    private DBHelper4MySQL() {
        super();
    }

    private static final DBHelper4MySQL INSTANCE = new DBHelper4MySQL();

    public static DBHelper4MySQL getInstance() {
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
        return DBConnProvider4MySQL.getInstance().getConnection();
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
        return new DBCommand4MySQL(con, SQL, isSkipParserSQL);
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
        Connection con = null;
        long result = 0l;
        try {
            con = getConnection();
            result = getUniqueID(con);
        } finally {
            if (con != null)
                con.close();
        }
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
        return isMySQLTableExists(storageTableName);
    }

    /**
     * 判断MySQL数据库是否存在
     * 
     * @param storageTableName
     * @return
     * @throws Exception
     */
    private boolean isMySQLTableExists(String storageTableName) throws Exception {
        Connection con = null;
        IDBCommand cmd = null;
        Boolean result = false;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM information_schema.TABLES T WHERE T.TABLE_SCHEMA=DATABASE() AND UPPER(T.TABLE_NAME)=UPPER(:tablename)";
            cmd = getCommand(con, sql);
            cmd.setParam(1, storageTableName);
            cmd.executeQuery();
            cmd.next();
            result = cmd.getInt(1) != 0;
        } finally {
            closeCommand(cmd);
            closeConnection(con);
        }
        return result;
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
        final long[] result = new long[1];
        final CallableStmtParamDefineAndValue[] paramDefine = new CallableStmtParamDefineAndValue[] { new CallableStmtParamDefineAndValue(0l, DBParamDataType.Long, true) };
        executeProcedure(con, "proc_general_global_sequence", paramDefine, new ICallableStmtAction() {
            @Override
            public void processStatement(CallableStatement stmt) throws SQLException {
                result[0] = stmt.getLong(1);
            }
        });
        return result[0];
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
        final long[] result = new long[1];
        final CallableStmtParamDefineAndValue[] paramDefine = new CallableStmtParamDefineAndValue[] { new CallableStmtParamDefineAndValue(seqname, DBParamDataType.String, false), new CallableStmtParamDefineAndValue(0l, DBParamDataType.Long, true) };
        executeProcedure(con, "proc_general_sequence_by_name", paramDefine, new ICallableStmtAction() {
            @Override
            public void processStatement(CallableStatement stmt) throws SQLException {
                result[0] = stmt.getLong(2);
            }
        });
        return result[0];
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
                result[i] = Long.toString(stmt.getLong(1));
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
        return DBSQLTrans4MySQL.getInstance();
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
        return DBConnProvider4MySQL.getInstance().getNativeConnection();
    }



}
