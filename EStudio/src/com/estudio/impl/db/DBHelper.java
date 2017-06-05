package com.estudio.impl.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.estudio.define.db.DBException;
import com.estudio.gis.GeometryUtils;
import com.estudio.intf.db.CallableStmtParamDefine;
import com.estudio.intf.db.CallableStmtParamDefineAndValue;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.ICallableStmtAction;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;

public abstract class DBHelper implements IDBHelper {

    /**
     * 设置参数
     * 
     * @param cmd
     * @param params
     * @throws
     * @throws SQLException
     */
    private void setParams(final IDBCommand cmd, final Map<String, Object> params) throws Exception {
        for (final Map.Entry<String, Object> entry : params.entrySet()) {
            final String paramName = entry.getKey();
            final Object obj = entry.getValue();
            if (obj == null)
                cmd.setNullParam(paramName);
            else if (obj instanceof String)
                cmd.setParam(paramName, (String) obj);
            else if (obj instanceof Integer)
                cmd.setParam(paramName, ((Integer) obj).intValue());
            else if (obj instanceof Long)
                cmd.setParam(paramName, ((Long) obj).longValue());
            else if (obj instanceof Double)
                cmd.setParam(paramName, ((Double) obj).doubleValue());
            else if (obj instanceof Float)
                cmd.setParam(paramName, ((Float) obj).floatValue());
            else if (obj instanceof Date)
                cmd.setParam(paramName, ((Date) obj));
            else if (obj instanceof byte[])
                cmd.setParam(paramName, (byte[]) obj);
            else
                cmd.setParam(paramName, obj);
        }
    }

    /**
     * 设置参数
     * 
     * @param stmt
     * @param index
     * @param paramValue
     * @throws SQLException
     */
    protected void setParam(final CallableStatement stmt, final int index, final DBParamDataType dataType, final Object paramValue) throws SQLException {
        if (paramValue == null) {
            if (dataType != DBParamDataType.Cursor)
                stmt.setObject(index, null);
            else
                setCursorParam(stmt, index, null);
        } else
            switch (dataType) {
            case String:
            case Text:
                stmt.setString(index, String.valueOf(paramValue));
                break;
            case Int:
                stmt.setInt(index, Convert.obj2Int(paramValue, 0));
                break;
            case Long:
                stmt.setLong(index, Convert.obj2Long(paramValue, 0L));
                break;
            case Float:
                stmt.setFloat(index, Convert.obj2Float(paramValue, 0f));
                break;
            case Double:
                stmt.setDouble(index, Convert.obj2Double(paramValue, 0.0));
                break;
            case Date:
            case Time:
            case DateTime:
            case Timestampe:
                stmt.setDate(index, Convert.obj2SQLDate(paramValue));
                break;
            case Bytes:
                stmt.setBytes(index, Convert.obj2Bytes(paramValue));
                break;
            case Cursor:
                setCursorParam(stmt, index, null);
                break;
            default:
                break;
            }
    }

    /**
     * 开始一个事务
     * 
     * @param con
     * @throws SQLException
     */
    @Override
    public void beginTransaction(final Connection con) throws SQLException {
        con.setAutoCommit(false);
    }

    /**
     * 提交一个事务
     * 
     * @param con
     * @throws SQLException
     */
    @Override
    public void commit(final Connection con) throws SQLException {
        con.commit();
    }

    /**
     * 结束一个事务
     * 
     * @param con
     * @throws SQLException
     */
    @Override
    public void endTransaction(final Connection con) throws SQLException {
        con.setAutoCommit(true);
    }

    @Override
    public boolean execute(final String sql, final Map<String, Object> params, final Connection con) throws Exception {
        final boolean result = false;
        IDBCommand cmd = null;
        try {
            cmd = getCommand(con, sql);
            if (params != null)
                setParams(cmd, params);
            cmd.execute();
        } finally {
            closeCommand(cmd);
        }
        return result;
    }

    @Override
    public Object executeScalar(final String sql, final Connection con) throws Exception {
        return executeScalar(sql, null, con);
    }

    @Override
    public Object executeScalar(final String sql, final Map<String, Object> params, final Connection con) throws Exception {
        Object result = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = getConnection();
            cmd = getCommand(tempCon, sql);
            if (params != null)
                setParams(cmd, params);
            if (cmd.executeQuery() && cmd.next())
                result = cmd.getObject(1);
        } finally {
            closeCommand(cmd);
            if (tempCon != con)
                closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public int executeScalarInt(String sql, Connection con) throws Exception {
        return Convert.obj2Int(executeScalar(sql, con), 0);
    }

    @Override
    public long executeScalarLong(String sql, Connection con) throws Exception {
        return Convert.obj2Long(executeScalar(sql, con), 0l);
    }

    @Override
    public double executeScalarDouble(String sql, Connection con) throws Exception {
        return Convert.obj2Double(executeScalar(sql, con), 0.0f);
    }

    @Override
    public String executeScalarString(String sql, Connection con) throws Exception {
        Object result = executeScalar(sql, con);
        return result == null ? "" : Convert.obj2String(result);
    }

    @Override
    public Date executeScalarDatetime(String sql, Connection con) throws Exception {
        Object result = executeScalar(sql, con);
        return result == null ? null : Convert.obj2Date(result);
    }

    @Override
    public int executeScalarInt(String sql, final Map<String, Object> params, Connection con) throws Exception {
        return Convert.obj2Int(executeScalar(sql, params, con), 0);
    }

    @Override
    public long executeScalarLong(String sql, final Map<String, Object> params, Connection con) throws Exception {
        return Convert.obj2Long(executeScalar(sql, params, con), 0l);
    }

    @Override
    public double executeScalarDouble(String sql, final Map<String, Object> params, Connection con) throws Exception {
        return Convert.obj2Double(executeScalar(sql, params, con), 0.0f);
    }

    @Override
    public String executeScalarString(String sql, final Map<String, Object> params, Connection con) throws Exception {
        Object result = executeScalar(sql, params, con);
        return result == null ? "" : Convert.obj2String(result);
    }

    @Override
    public Date executeScalarDatetime(String sql, final Map<String, Object> params, Connection con) throws Exception {
        Object result = executeScalar(sql, params, con);
        return result == null ? null : Convert.obj2Date(result);
    }

    @Override
    public boolean execute(String sql, Object... params) throws Exception {
        return execute(null, sql, params);
    }

    @Override
    public boolean execute(Connection con, String sql, Object... params) throws Exception {
        Connection tempCon = con;
        PreparedStatement stmt = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = getConnection();
            stmt = tempCon.prepareStatement(sql);
            if (params != null && params.length != 0) {
                for (int i = 0; i < params.length; i++) {
                    Object v = params[i];
                    stmt.setObject(i+1, v);
                }
            }
            stmt.execute();
        } finally {
            closeStatement(stmt);
            if (tempCon != con && tempCon != null)
                closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public JSONArray executeQuery(final String sql, final Connection con) throws Exception {
        return executeQuery(sql, null, con);
    }

    @Override
    public JSONArray executeQuery(final String sql, final Map<String, Object> params, final Connection con) throws Exception {
        final JSONArray result = new JSONArray();
        IDBCommand cmd = null;
        try {
            cmd = getCommand(con, sql);
            if (params != null)
                setParams(cmd, params);
            cmd.executeQuery();
            while (cmd.next()) {
                final JSONObject json = new JSONObject();
                for (final String fieldName : cmd.getFieldNames())
                    json.put(fieldName, cmd.getObject(fieldName));
                result.add(json);
            }
        } finally {
            closeCommand(cmd);
        }
        return result;
    }

    /**
     * 关闭数据库连接
     * 
     * @param con
     */
    @Override
    public void closeConnection(final Connection con) {
        try {
            if (con != null)
                con.close();
        } catch (final Exception e) {
            // ExceptionUtils.printExceptionTrace(e);
        }

    }

    /**
     * 关闭Command 对象
     * 
     * @param cmd
     */
    @Override
    public void closeCommand(final IDBCommand cmd) {
        try {
            if (cmd != null)
                cmd.close();
        } catch (final Exception e) {
            // ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 关闭数据库声明
     * 
     * @param stmt
     */
    @Override
    public void closeStatement(final Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (final Exception e) {
            // ExceptionUtils.printExceptionTrace(e);
        }
    }

    @Override
    public void closeResultSet(ResultSet rs) {
        if (rs != null)
            try {
                closeStatement(rs.getStatement());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
    }

    /**
     * 回滚数据
     * 
     * @param con
     */
    @Override
    public void rollback(final Connection con) {
        rollback(con, true);
    }

    /**
     * 回滚数据
     * 
     * @param con
     */
    @Override
    public void rollback(final Connection con, final boolean setAutoCommit) {
        if (con != null)
            try {
                con.rollback();
                if (con.getAutoCommit() != setAutoCommit)
                    con.setAutoCommit(setAutoCommit);
            } catch (final Exception e) {
                // ExceptionUtils.printExceptionTrace(e);
            }
    }

    /**
     * Record转为JSON对象
     * 
     * @param cmd
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    @Override
    public JSONObject cmdRecord2Json(final IDBCommand cmd) throws Exception {
        final JSONObject result = new JSONObject();
        final ResultSetMetaData resultsetMetaData = cmd.getResultSetMetaData();
        for (int i = 1; i <= resultsetMetaData.getColumnCount(); i++)
            result.put(cmd.getResultSetMetaData().getColumnLabel(i).toUpperCase(), getValue(cmd, i, false));
        return result;
    }

    public JSONArray resultSet2JSONArray(ResultSet rs) throws Exception {
        JSONArray result = new JSONArray();
        while (rs.next())
            result.add(resultSet2JSONObject(rs));
        return result;
    }

    public JSONArray resultSet2JSONArray(ResultSet rs, boolean isBytesAsBase64) throws Exception {
        JSONArray result = new JSONArray();
        while (rs.next())
            result.add(resultSet2JSONObject(rs, isBytesAsBase64));
        return result;
    }

    public JSONArray command2JSONArray(IDBCommand cmd) throws Exception {
        JSONArray result = new JSONArray();
        while (cmd.next())
            result.add(command2JSONObject(cmd));
        return result;
    }

    public JSONArray command2JSONArray(IDBCommand cmd, boolean isBase64) throws Exception {
        JSONArray result = new JSONArray();
        while (cmd.next())
            result.add(command2JSONObject(cmd, true));
        return result;
    }

    public JSONObject command2JSONObject(IDBCommand cmd) throws Exception {
        final JSONObject result = new JSONObject();
        final ResultSetMetaData resultsetMetaData = cmd.getMetaData();
        for (int i = 1; i <= resultsetMetaData.getColumnCount(); i++)
            result.put(resultsetMetaData.getColumnLabel(i).toUpperCase(), getValue(cmd, i, false));
        return result;
    }

    public JSONObject command2JSONObject(IDBCommand cmd, boolean isBytesAsBase64) throws Exception {
        final JSONObject result = new JSONObject();
        final ResultSetMetaData resultsetMetaData = cmd.getMetaData();
        for (int i = 1; i <= resultsetMetaData.getColumnCount(); i++)
            result.put(resultsetMetaData.getColumnLabel(i).toUpperCase(), getValue(cmd, i, isBytesAsBase64));
        return result;
    }

    public JSONObject resultSet2JSONObject(ResultSet rs) throws Exception {
        final JSONObject result = new JSONObject();
        final ResultSetMetaData resultsetMetaData = rs.getMetaData();
        for (int i = 1; i <= resultsetMetaData.getColumnCount(); i++){
        	result.put(resultsetMetaData.getColumnLabel(i).toUpperCase(), getValue(rs, resultsetMetaData.getColumnType(i), i, false));
        }
        return result;
    }

    public JSONObject resultSet2JSONObject(ResultSet rs, boolean isBytesAsBase64) throws Exception {
        final JSONObject result = new JSONObject();
        final ResultSetMetaData resultsetMetaData = rs.getMetaData();
        for (int i = 1; i <= resultsetMetaData.getColumnCount(); i++)
            result.put(resultsetMetaData.getColumnLabel(i).toUpperCase(), getValue(rs, resultsetMetaData.getColumnType(i), i, isBytesAsBase64));
        return result;
    }

    public Object getResultSetValue(ResultSet rs, final int columnIndex) throws Exception {
        return getValue(rs, rs.getMetaData().getColumnType(columnIndex), columnIndex, false);
    }

    public Object getResultSetValue(ResultSet rs, final int columnIndex, boolean isBytesAsBase64) throws Exception {
        return getValue(rs, rs.getMetaData().getColumnType(columnIndex), columnIndex, isBytesAsBase64);
    }

    public Object getDBCommandValue(IDBCommand cmd, final int columnIndex) throws Exception {
        return getValue(cmd, columnIndex, false);
    }

    public Object getDBCommandValue(IDBCommand cmd, final int columnIndex, boolean isBytesAsBase64) throws Exception {
        return getValue(cmd, columnIndex, isBytesAsBase64);
    }

    /**
     * 
     * @param cmd
     * @param columnType
     * @param columnIndex
     * @param isBytesAsBase64
     * @return
     * @throws Exception
     */
    private Object getValue(IDBCommand cmd, int columnIndex, boolean isBytesAsBase64) throws Exception {
        Object value = "";
        switch (cmd.getMetaData().getColumnType(columnIndex)) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
        case Types.NCHAR:
        case Types.NVARCHAR:
        case Types.LONGNVARCHAR:
            value = cmd.getString(columnIndex);
            break;
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
            value = cmd.getInt(columnIndex);
            break;
        case Types.BIGINT:
            value = cmd.getLong(columnIndex);
            break;
        case Types.FLOAT:
        case Types.REAL:
        case Types.DOUBLE:
        case Types.NUMERIC:
            value = cmd.getDouble(columnIndex);
            break;
        case Types.DECIMAL: // xxxx
            value = cmd.getDouble(columnIndex);
            break;
        case Types.DATE:
        case Types.TIMESTAMP:
        case Types.TIME:
            final Date time = cmd.getDateTime(columnIndex);
            if (time == null)
                value = "";
            else
                value = Convert.date2Str(new Time(time.getTime()));
            break;
        case Types.BLOB:
        case Types.BINARY:
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
            value = isBytesAsBase64 ? Convert.bytes2Base64(cmd.getBytes(columnIndex)) : Convert.bytes2Str(cmd.getBytes(columnIndex));
            break;
        case Types.CLOB:
        case Types.NCLOB:
            value = cmd.getString(columnIndex);
            break;
        case Types.BOOLEAN:
            value = cmd.getBoolean(columnIndex);
            break;
        default:
            value = cmd.getString(columnIndex);
            break;
        }
        return value;
    }

    private Object getValue(ResultSet rs, int columnType, final int columnIndex, boolean isBytesAsBase64) throws Exception {
        Object value = "";
        switch (columnType) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
        case Types.NCHAR:
        case Types.NVARCHAR:
        case Types.LONGNVARCHAR:
            value = rs.getString(columnIndex);
            break;
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
            value = rs.getInt(columnIndex);
            break;
        case Types.BIGINT:
            value = rs.getLong(columnIndex);
            break;
        case Types.FLOAT:
        case Types.REAL:
        case Types.DOUBLE:
        case Types.NUMERIC:
            value = rs.getDouble(columnIndex);
            break;
        case Types.DECIMAL: // xxxx
            value = rs.getDouble(columnIndex);
            break;
        case Types.DATE:
        case Types.TIMESTAMP:
        case Types.TIME:
            final Timestamp time = rs.getTimestamp(columnIndex);
            if (time == null)
                value = "";
            else
                value = Convert.date2Str(new Time(time.getTime()));
            break;
        case Types.BLOB:
        case Types.BINARY:
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
            value = isBytesAsBase64 ? Convert.bytes2Base64(rs.getBytes(columnIndex)) : Convert.bytes2Str(rs.getBytes(columnIndex));
            break;
        case Types.CLOB:
        case Types.NCLOB:
            value = rs.getString(columnIndex);
            break;
        case Types.BOOLEAN:
            value = rs.getBoolean(columnIndex);
            break;
        case 2002:
            value =GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(JGeometry.load(rs.getBytes(columnIndex)))));
            break;
        default:
            value = rs.getString(columnIndex);
            break;
        }
        return value;
    }

    /**
     * Record转为JSON对象
     * 
     * @param cmd
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    @Override
    public JSONObject appendNullRecord2Json(final IDBCommand cmd) throws Exception {
        final JSONObject result = new JSONObject();
        final ResultSetMetaData resultsetMetaData = cmd.getResultSetMetaData();
        for (int i = 1; i <= resultsetMetaData.getColumnCount(); i++)
            result.put(cmd.getResultSetMetaData().getColumnLabel(i).toUpperCase(), "");
        return result;
    }

    @Override
    public void execute(final String sql, final Connection con) throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sql);
            stmt.execute();
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    public IDBCommand getCommand(final Connection con, final String SQL) throws Exception {
        return getCommand(con, SQL, false);
    }

    @Override
    public CallableStatement getCallableStatment(final Connection con, final String SQL) throws SQLException {
        return con.prepareCall(SQL);
    }

    /**
     * 执行存储过程
     * 
     * @param procedureName
     * @param params
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    @Override
    public void executeProcedure(final String procedureName, final CallableStmtParamDefineAndValue[] params, final ICallableStmtAction processAction) throws Exception {
        executeProcedure(null, procedureName, params, processAction);
    }

    /**
     * 执行存储过程
     * 
     * @param procedureName
     * @param processAction
     * @throws SQLException
     * @throws DBException
     */
    @Override
    public void executeProcedure(final String procedureName, final CallableStmtParamDefineAndValue[] params) throws Exception {
        executeProcedure(null, procedureName, params, null);
    }

    @Override
    public void executeProcedure(final Connection con, final String procedureName, final CallableStmtParamDefine params[], final List<List<Object>> paramValuesList, final ICallableStmtAction processAction) throws Exception {
        Connection tempCon = con;
        CallableStatement stmt = null;
        try {
            if (tempCon == null)
                tempCon = getConnection();
            final StringBuilder sb = new StringBuilder();
            sb.append("{ call ").append(procedureName).append("(");
            for (int i = 0; i < params.length; i++) {
                sb.append("?");
                if (i != (params.length - 1))
                    sb.append(",");
            }
            sb.append(") }");
            stmt = getCallableStatment(tempCon, sb.toString());
            for (int i = 0; i < params.length; i++) {
                final CallableStmtParamDefine param = params[i];

                if (param.isOutput)
                    stmt.registerOutParameter(i + 1, getOutputParamType(param.type));
            }

            for (final List<Object> ps : paramValuesList) {
                for (int i = 0; i < ps.size(); i++)
                    setParam(stmt, i + 1, params[i].type, ps.get(i));
                stmt.execute();
                if (processAction != null)
                    processAction.processStatement(stmt);
            }

        } finally {
            closeStatement(stmt);
            if (tempCon != con)
                closeConnection(tempCon);
        }
    }

    @Override
    public void executeProcedure(final Connection con, final String procedureName, final CallableStmtParamDefineAndValue[] params, final ICallableStmtAction processAction) throws Exception {
        final List<Object> paramValues = new ArrayList<Object>();
        final List<List<Object>> paramValuesList = new ArrayList<List<Object>>();
        for (final CallableStmtParamDefineAndValue p : params)
            paramValues.add(p.value);
        paramValuesList.add(paramValues);
        executeProcedure(con, procedureName, params, paramValuesList, processAction);
    }

    /**
     * 注册参数返回类型
     * 
     * @param type
     * @return
     */
    protected abstract int getOutputParamType(DBParamDataType type);

    /**
     * 设置游标参数
     * 
     * @param stmt
     * @param index
     * @param object
     * @throws SQLException
     */
    protected abstract void setCursorParam(CallableStatement stmt, int index, Object object) throws SQLException;

    /**
     * 构造函数
     */
    protected DBHelper() {
        super();
    }

    @Override
    public Connection getNaviteConnection(Connection con) {
        Connection tempConnection = con;
        if (tempConnection instanceof DruidPooledConnection) {
            tempConnection = ((DruidPooledConnection) con).getConnection();
            if (tempConnection instanceof ConnectionProxy)
                tempConnection = ((ConnectionProxy) tempConnection).getRawObject();
        }
        return tempConnection;
    }

}
