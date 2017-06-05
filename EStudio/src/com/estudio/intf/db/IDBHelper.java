package com.estudio.intf.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;

public interface IDBHelper {
    // public enum ParamType
    /**
     * 获取数据库连接
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Connection getConnection() throws Exception;

    
    /**
     * 关闭数据库连接
     * 
     * @param con
     */
    public void closeConnection(Connection con);

    /**
     * 开始一个事务
     * 
     * @param con
     * @throws SQLException
     */
    public void beginTransaction(Connection con) throws SQLException;

    /**
     * 提交一个事务
     * 
     * @param con
     * @throws SQLException
     */
    public void commit(Connection con) throws SQLException;

    /**
     * 结束一个事务
     * 
     * @param con
     * @throws SQLException
     */
    public void endTransaction(Connection con) throws SQLException;

    /**
     * 获取数据库Command对象
     * 
     * @param con
     * @param SQL
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getCommand(Connection con, String SQL) throws Exception;

    /**
     * 获取数据库Command对象
     * 
     * @param con
     * @param SQL
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getCommand(Connection con, String SQL, boolean isSkipParserParams) throws Exception;

    /**
     * 关闭数据库Command对象
     * 
     * @param cmd
     */
    public void closeCommand(IDBCommand cmd);

    /**
     * 关闭Statement对象
     * 
     * @param stmt
     */
    public void closeStatement(Statement stmt);

    /**
     * 关闭数据集
     * 
     * @param rs
     */
    public void closeResultSet(ResultSet rs);
    

    /**
     * 回滚事务 = rollback(con,true)
     * 
     * @param con
     */
    public void rollback(Connection con);

    /**
     * 回滚事务并是否设置 con 的 AutoCommit 属性
     * 
     * @param con
     * @param setAutoCommit
     */
    public void rollback(Connection con, boolean setAutoCommit);

    /**
     * 获取全局唯一值
     * 
     * @return
     * @throws Exception
     */
    public long getUniqueID() throws Exception;

    /**
     * 获取全局唯一值
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public long getUniqueID(Connection con) throws Exception;

    /**
     * 根据队列名称获取全局唯一值
     * 
     * @param connection
     * @param seqname
     * @return
     * @throws SQLException
     *             , DBException
     */
    public long getUniqueID(Connection connection, String seqname) throws Exception;

    /**
     * 批量获取全局唯一值
     * 
     * @param cached
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String[] getUniqueIDS(int cached) throws Exception;

    /**
     * 获取BLOB内容
     * 
     * @param tablename
     * @param blobField
     * @param id
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public byte[] getBlob(String tablename, String blobField, long id, Connection con) throws Exception;

    /**
     * cmd转为Json对象
     * 
     * @param cmd
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONObject cmdRecord2Json(IDBCommand cmd) throws Exception;

    /**
     * cmd 转化为空Json记录
     * 
     * @param cmd
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONObject appendNullRecord2Json(IDBCommand cmd) throws Exception;

    /**
     * 数据库表是否已经存在
     * 
     * @param storageTableName
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public boolean isTableExists(String storageTableName) throws Exception;

    /**
     * 获取语句翻译服务
     * 
     * @return
     */
    public ISQLTrans getSQLTrans();

    /**
     * 执行SQL语句
     * 
     * @param sql
     * @param con
     * @throws SQLException
     *             , DBException
     */
    public void execute(String sql, Connection con) throws Exception;

    /**
     * 执行SQL语句
     * 
     * @param sQL
     * @param connection
     * @param params
     * @return
     * @throws Exception
     */
    public boolean execute(String sql, Map<String, Object> params, Connection connection) throws Exception;

    /**
     * 执行SQL语句
     * 
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public boolean execute(String sql, Object... params) throws Exception;

    /**
     * 执行SQL语句
     * 
     * @param con
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public boolean execute(Connection con, String sql, Object... params) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Object executeScalar(String sql, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的整数值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public int executeScalarInt(String sql, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的长整数值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public long executeScalarLong(String sql, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的浮点数
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public double executeScalarDouble(String sql, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的字符串
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public String executeScalarString(String sql, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的日期时间值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Date executeScalarDatetime(String sql, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Object executeScalar(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的整数值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public int executeScalarInt(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的长整数值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public long executeScalarLong(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的浮点数
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public double executeScalarDouble(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的字符串
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public String executeScalarString(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回第一行第一列的日期时间值
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Date executeScalarDatetime(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * 执行SQL语句并返回JSON格式的数据集
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public JSONArray executeQuery(String sql, Connection con) throws Exception;
    

    /**
     * 执行SQL语句并返回JSON格式的数据集
     * 
     * @param sql
     * @param params
     * @param con
     * @return
     * @throws Exception
     */
    public JSONArray executeQuery(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * 获取CallableStatement用于返回结果集
     * 
     * @param SQL
     * @return
     * @throws SQLException
     */
    public CallableStatement getCallableStatment(Connection con, String SQL) throws SQLException;

    /**
     * 执行存储过程
     * 
     * @param procedureName
     * @param params
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    public void executeProcedure(Connection con, String procedureName, CallableStmtParamDefineAndValue[] params, ICallableStmtAction processAction) throws Exception;

    /**
     * 执行存储过程
     * 
     * @param procedureName
     * @param params
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    public void executeProcedure(String procedureName, CallableStmtParamDefineAndValue[] params, ICallableStmtAction processAction) throws Exception;

    // public void executeProcedure(String procedureName,CallableStatement[]
    // params,)

    /**
     * 执行存储过程
     * 
     * @param procedureName
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    public void executeProcedure(String procedureName, CallableStmtParamDefineAndValue[] params) throws Exception;

    /**
     * 批量执行存储过程
     * 
     * @param con
     * @param procedureName
     * @param params
     * @param paramValuesList
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    public void executeProcedure(Connection con, String procedureName, CallableStmtParamDefine[] params, List<List<Object>> paramValuesList, ICallableStmtAction processAction) throws Exception;

    /**
     * 获取系统日期
     * 
     * @param format
     * @return
     * @throws Exception
     */
    public String getSysdate(String format) throws Exception;

    /**
     * 
     * @param connection
     * @param size
     * @param string
     * @return
     * @throws Exception
     */
    public String[] getUniqueIDS(Connection connection, int size, String string) throws Exception;

    /**
     * 
     * @param rs
     * @return
     * @throws Exception
     */
    public JSONArray resultSet2JSONArray(ResultSet rs) throws Exception;

    /**
     * 
     * @param rs
     * @return
     * @throws Exception
     */
    public JSONArray resultSet2JSONArray(ResultSet rs, boolean isBytesAsBase64) throws Exception;

    /**
     * 
     * @param rs
     * @return
     * @throws Exception
     */
    public JSONObject resultSet2JSONObject(ResultSet rs) throws Exception;

    /**
     * 
     * @param rs
     * @return
     * @throws Exception
     */
    public JSONObject resultSet2JSONObject(ResultSet rs, boolean isBytesAsBase64) throws Exception;

    /**
     * 
     * @param rs
     * @param columnIndex
     * @return
     * @throws Exception
     */
    public Object getResultSetValue(ResultSet rs, final int columnIndex, boolean isBytesAsBase64) throws Exception;

    /**
     * 
     * @param rs
     * @param columnIndex
     * @return
     * @throws Exception
     */
    public Object getResultSetValue(ResultSet rs, final int columnIndex) throws Exception;


    public Object getDBCommandValue(IDBCommand cmd, final int columnIndex) throws Exception;
    
    public Object getDBCommandValue(IDBCommand cmd, final int columnIndex, boolean isBytesAsBase64) throws Exception;
    
    /**
     * 
     * @param cmd
     * @param isBytesAsBase64
     * @return
     * @throws Exception
     */
    public JSONObject command2JSONObject(IDBCommand cmd, boolean isBytesAsBase64) throws Exception;
    
    /**
     * 
     * @param cmd
     * @return
     * @throws Exception
     */
    public JSONObject command2JSONObject(IDBCommand cmd) throws Exception;
    
    /**
     * 
     * @param rs
     * @param isBytesAsBase64
     * @return
     * @throws Exception
     */
    public JSONArray command2JSONArray(IDBCommand cmd, boolean isBytesAsBase64) throws Exception;
    
    /**
     * 
     * @param cmd
     * @return
     * @throws Exception
     */
    public JSONArray command2JSONArray(IDBCommand cmd) throws Exception;
    
    /**
     * 
     * @param con
     * @return
     */
    public Connection getNaviteConnection(Connection con);

    public Connection getNativeConnection() throws Exception;

}
