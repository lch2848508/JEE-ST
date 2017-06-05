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
     * ��ȡ���ݿ�����
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Connection getConnection() throws Exception;

    
    /**
     * �ر����ݿ�����
     * 
     * @param con
     */
    public void closeConnection(Connection con);

    /**
     * ��ʼһ������
     * 
     * @param con
     * @throws SQLException
     */
    public void beginTransaction(Connection con) throws SQLException;

    /**
     * �ύһ������
     * 
     * @param con
     * @throws SQLException
     */
    public void commit(Connection con) throws SQLException;

    /**
     * ����һ������
     * 
     * @param con
     * @throws SQLException
     */
    public void endTransaction(Connection con) throws SQLException;

    /**
     * ��ȡ���ݿ�Command����
     * 
     * @param con
     * @param SQL
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getCommand(Connection con, String SQL) throws Exception;

    /**
     * ��ȡ���ݿ�Command����
     * 
     * @param con
     * @param SQL
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getCommand(Connection con, String SQL, boolean isSkipParserParams) throws Exception;

    /**
     * �ر����ݿ�Command����
     * 
     * @param cmd
     */
    public void closeCommand(IDBCommand cmd);

    /**
     * �ر�Statement����
     * 
     * @param stmt
     */
    public void closeStatement(Statement stmt);

    /**
     * �ر����ݼ�
     * 
     * @param rs
     */
    public void closeResultSet(ResultSet rs);
    

    /**
     * �ع����� = rollback(con,true)
     * 
     * @param con
     */
    public void rollback(Connection con);

    /**
     * �ع������Ƿ����� con �� AutoCommit ����
     * 
     * @param con
     * @param setAutoCommit
     */
    public void rollback(Connection con, boolean setAutoCommit);

    /**
     * ��ȡȫ��Ψһֵ
     * 
     * @return
     * @throws Exception
     */
    public long getUniqueID() throws Exception;

    /**
     * ��ȡȫ��Ψһֵ
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public long getUniqueID(Connection con) throws Exception;

    /**
     * ���ݶ������ƻ�ȡȫ��Ψһֵ
     * 
     * @param connection
     * @param seqname
     * @return
     * @throws SQLException
     *             , DBException
     */
    public long getUniqueID(Connection connection, String seqname) throws Exception;

    /**
     * ������ȡȫ��Ψһֵ
     * 
     * @param cached
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String[] getUniqueIDS(int cached) throws Exception;

    /**
     * ��ȡBLOB����
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
     * cmdתΪJson����
     * 
     * @param cmd
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONObject cmdRecord2Json(IDBCommand cmd) throws Exception;

    /**
     * cmd ת��Ϊ��Json��¼
     * 
     * @param cmd
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public JSONObject appendNullRecord2Json(IDBCommand cmd) throws Exception;

    /**
     * ���ݿ���Ƿ��Ѿ�����
     * 
     * @param storageTableName
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public boolean isTableExists(String storageTableName) throws Exception;

    /**
     * ��ȡ��䷭�����
     * 
     * @return
     */
    public ISQLTrans getSQLTrans();

    /**
     * ִ��SQL���
     * 
     * @param sql
     * @param con
     * @throws SQLException
     *             , DBException
     */
    public void execute(String sql, Connection con) throws Exception;

    /**
     * ִ��SQL���
     * 
     * @param sQL
     * @param connection
     * @param params
     * @return
     * @throws Exception
     */
    public boolean execute(String sql, Map<String, Object> params, Connection connection) throws Exception;

    /**
     * ִ��SQL���
     * 
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public boolean execute(String sql, Object... params) throws Exception;

    /**
     * ִ��SQL���
     * 
     * @param con
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public boolean execute(Connection con, String sql, Object... params) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е�ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Object executeScalar(String sql, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е�����ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public int executeScalarInt(String sql, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�еĳ�����ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public long executeScalarLong(String sql, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�еĸ�����
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public double executeScalarDouble(String sql, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е��ַ���
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public String executeScalarString(String sql, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е�����ʱ��ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Date executeScalarDatetime(String sql, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е�ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Object executeScalar(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е�����ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public int executeScalarInt(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�еĳ�����ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public long executeScalarLong(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�еĸ�����
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public double executeScalarDouble(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е��ַ���
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public String executeScalarString(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢���ص�һ�е�һ�е�����ʱ��ֵ
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public Date executeScalarDatetime(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * ִ��SQL��䲢����JSON��ʽ�����ݼ�
     * 
     * @param sql
     * @param con
     * @return
     * @throws Exception
     */
    public JSONArray executeQuery(String sql, Connection con) throws Exception;
    

    /**
     * ִ��SQL��䲢����JSON��ʽ�����ݼ�
     * 
     * @param sql
     * @param params
     * @param con
     * @return
     * @throws Exception
     */
    public JSONArray executeQuery(String sql, Map<String, Object> params, Connection con) throws Exception;

    /**
     * ��ȡCallableStatement���ڷ��ؽ����
     * 
     * @param SQL
     * @return
     * @throws SQLException
     */
    public CallableStatement getCallableStatment(Connection con, String SQL) throws SQLException;

    /**
     * ִ�д洢����
     * 
     * @param procedureName
     * @param params
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    public void executeProcedure(Connection con, String procedureName, CallableStmtParamDefineAndValue[] params, ICallableStmtAction processAction) throws Exception;

    /**
     * ִ�д洢����
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
     * ִ�д洢����
     * 
     * @param procedureName
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    public void executeProcedure(String procedureName, CallableStmtParamDefineAndValue[] params) throws Exception;

    /**
     * ����ִ�д洢����
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
     * ��ȡϵͳ����
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
