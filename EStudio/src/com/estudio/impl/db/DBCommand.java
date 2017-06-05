package com.estudio.impl.db;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;

import org.apache.commons.lang3.StringUtils;

import com.estudio.define.db.DBException;
import com.estudio.gis.GeometryUtils;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;

public abstract class DBCommand implements IDBCommand {
    private Connection connection = null;
    private final ArrayList<String> fieldNamesList = new ArrayList<String>();

    private final Map<String, List<String>> params = new HashMap<String, List<String>>();
    private final ArrayList<String> paramsList = new ArrayList<String>();
    private String preparedSQL;
    protected ResultSet resultSet = null;
    private String sql = "";
    private PreparedStatement statement = null;
    private boolean skipParserSQL = false;

    private final Map<Integer, String> paramIndex2Value = new HashMap<Integer, String>(); // 参数索引对应值
    private final Map<String, String> paramName2Value = new HashMap<String, String>(); // 参数名称对应值

    // 添加支持动态参数的支持
    private final ArrayList<String> extParamsList = new ArrayList<String>();
    private ResultSetMetaData metaData;

    public DBCommand() {
    }

    /**
     * 构造函数<br>
     * 
     * @param connection
     *            数据库连接
     * @param sql
     *            SQL语句
     * @throws SQLException
     *             , DBException
     */
    public DBCommand(final Connection connection, final String sql, final boolean isSkipParserSQL) throws Exception {
        super();
        skipParserSQL = isSkipParserSQL;
        this.connection = connection;
        setSQL(sql);
    }

    /**
     * 构造函数<br>
     * 
     * @param connection
     *            数据库连接
     * @param sql
     *            SQL语句
     * @throws SQLException
     *             , DBException
     */
    public DBCommand(final Connection connection, final String sql) throws Exception {
        super();
        this.connection = connection;
        setSQL(sql);
    }

    /**
     * 克隆一个Command用于加快记录
     * 
     * @throws SQLException
     *             , DBException
     */

    @Override
    public IDBCommand clone(final Connection con) throws Exception {
        return clone(con, null);
    }

    /**
     * 克隆 提供扩展参数的支持
     */
    @Override
    public IDBCommand clone(final Connection con, final Map<String, String> extParamName2Value) throws Exception {
        final DBCommand cmd = (DBCommand) getDBHelper().getCommand(con, "");
        cmd.connection = con;
        cmd.sql = sql;
        cmd.preparedSQL = preparedSQL;
        for (int i = 0; i < extParamsList.size(); i++) {
            final String extParamName = extParamsList.get(i);
            String extParamValue = extParamName2Value != null ? extParamName2Value.get(extParamName) : " 1=1 ";
            if (StringUtils.isEmpty(extParamValue))
                extParamValue = " 1=1 ";
            cmd.preparedSQL = StringUtils.replace(cmd.preparedSQL, extParamName, extParamValue);
        }
        cmd.statement = con.prepareStatement(cmd.preparedSQL);
        cmd.params.putAll(params);
        cmd.paramsList.addAll(paramsList);
        cmd.fieldNamesList.addAll(fieldNamesList);
        cmd.extParamsList.addAll(extParamsList);
        return cmd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#close()
     */
    @Override
    public final void close() throws Exception {
        if (statement != null)
            statement.close();
        if (resultSet != null)
            resultSet.close();
        statement = null;
        resultSet = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#execute()
     */
    @Override
    public boolean execute() throws Exception {
        try {
            statement.execute();
        } catch (final SQLException e) {
            throw new DBException(debugInfo(), e);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#execute(java.lang.String)
     */
    @Override
    public boolean execute(final String SQL) throws Exception {
        setSQL(SQL);
        return this.execute();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#executeQuery()
     */
    @Override
    public boolean executeQuery() throws Exception {
        boolean result = false;
        try {
            fieldNamesList.clear();
            result = (resultSet = statement.executeQuery()) != null;
            if (result) {
                metaData = resultSet.getMetaData();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                    fieldNamesList.add(resultSet.getMetaData().getColumnLabel(i).toUpperCase());
            }
        } catch (final SQLException e) {
            throw new DBException(debugInfo(), e);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#executeQuery(java.lang.String)
     */
    @Override
    public boolean executeQuery(final String SQL) throws Exception {
        setSQL(SQL);
        return this.executeQuery();
    }

    /*
     * sss (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getBlob(int)
     */
    @Override
    public InputStream getBlob(final int index) throws Exception {
        final Blob b = resultSet.getBlob(index);
        if (b != null)
            return b.getBinaryStream();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getBlob(java.lang.String)
     */
    @Override
    public InputStream getBlob(final String columnName) throws Exception {
        final Blob b = resultSet.getBlob(columnName);
        if (b != null)
            return b.getBinaryStream();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getBlobStr(int)
     */
    @Override
    public String getBlobStr(final int index) throws Exception {
        return Convert.blob2Str(resultSet.getBlob(index));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getBlobStr(java.lang.String)
     */
    @Override
    public String getBlobStr(final String columnName) throws Exception {
        return Convert.blob2Str(resultSet.getBlob(columnName));
    }

    @Override
    public byte[] getBytes(final int index) throws Exception {
        return resultSet.getBytes(index);
    }

    @Override
    public byte[] getBytes(final String columnName) throws Exception {
        return resultSet.getBytes(columnName);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getDate(int)
     */
    @Override
    public Date getDate(final int index) throws Exception {
        return resultSet.getDate(index);
    }

    /**
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public Time getDateTime(final int index) throws Exception {
        final Timestamp time = resultSet.getTimestamp(index);
        if (time == null)
            return null;
        return new Time(time.getTime());
    }

    @Override
    public Time getDateTime(final String fieldName) throws Exception {
        final Timestamp time = resultSet.getTimestamp(fieldName);
        if (time == null)
            return null;
        return new Time(time.getTime());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getDate(java.lang.String)
     */
    @Override
    public Date getDate(final String columnName) throws Exception {
        return resultSet.getDate(columnName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getDouble(int)
     */
    @Override
    public double getDouble(final int index) throws Exception {
        return resultSet.getDouble(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getDouble(java.lang.String)
     */
    @Override
    public double getDouble(final String columnName) throws Exception {
        return resultSet.getDouble(columnName);
    }

    @Override
    public ArrayList<String> getFieldNames() {
        return fieldNamesList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getFloat(int)
     */
    @Override
    public float getFloat(final int index) throws Exception {
        return resultSet.getFloat(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getFloat(java.lang.String)
     */
    @Override
    public float getFloat(final String columnName) throws Exception {
        return resultSet.getFloat(columnName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getInt(int)
     */
    @Override
    public int getInt(final int index) throws Exception {
        return resultSet.getInt(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getInt(java.lang.String)
     */
    @Override
    public int getInt(final String columnName) throws Exception {
        return resultSet.getInt(columnName);
    }

    @Override
    public ArrayList<String> getParams() {
        return paramsList;
    }

    @Override
    public ResultSetMetaData getResultSetMetaData() throws Exception {
        if (resultSet != null)
            return resultSet.getMetaData();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getString(int)
     */
    @Override
    public String getString(final int index) throws Exception {
        return resultSet.getString(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#getString(java.lang.String)
     */
    @Override
    public String getString(final String columnName) throws Exception {
        return resultSet.getString(columnName);
    }

    @Override
    public Object getObject(final int i) throws SQLException {
        return resultSet.getObject(i);
    }

    @Override
    public Object getObject(final String fieldName) throws SQLException {
        return resultSet.getObject(fieldName);
    }

    /**
     * 判断字段是否存在
     */
    @Override
    public boolean isExistsField(final String name) {
        return fieldNamesList.indexOf(name.toUpperCase()) != -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setFloat(java.lang.String, float)
     */

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#next()
     */
    @Override
    public boolean next() throws Exception {
        return resultSet.next();
    }

    @Override
    public boolean setFloat(final int paramIndex, final float value) throws Exception {
        statement.setFloat(paramIndex, value);
        registerParamValue(paramIndex, "float:" + value);
        return true;
    }

    @Override
    public boolean setFloat(final String paramName, final float value) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));// (new
                                                             // Integer()).intValue();
            statement.setFloat(ID, value);
        }
        registerParamValue(paramName, "float:" + value);
        return true;
    }

    @Override
    public boolean setNullParam(final String paramName) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));
            statement.setObject(ID, null);
        }
        registerParamValue(paramName, "set null");
        return true;
    }

    @Override
    public boolean setParam(final int paramIndex, final byte[] value) throws Exception {
        statement.setBytes(paramIndex, value);
        registerParamValue(paramIndex, "bytes:length=" + (value == null ? 0 : value.length));
        return true;
    }

    @Override
    public boolean setParam(final int paramIndex, final Date value) throws Exception {
        statement.setDate(paramIndex, new java.sql.Date(value.getTime()));
        registerParamValue(paramIndex, ("date:" + value) == null ? "null" : Convert.datetime2Str(value));
        return true;
    }

    @Override
    public boolean setParam(final int paramIndex, final double value) throws Exception {
        statement.setDouble(paramIndex, value);
        registerParamValue(paramIndex, "double:" + value);
        return true;
    }

    @Override
    public boolean setParam(final int paramIndex, final InputStream value) throws Exception {
        statement.setBinaryStream(paramIndex, value);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setParam(java.lang.String,
     * byte[])
     */

    @Override
    public boolean setParam(final int paramIndex, final int value) throws Exception {
        statement.setLong(paramIndex, value);
        registerParamValue(paramIndex, "int:" + value);
        return true;
    }

    @Override
    public boolean setParam(final int paramIndex, final long value) throws Exception {
        statement.setLong(paramIndex, value);
        registerParamValue(paramIndex, "long:" + value);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setParam(java.lang.String,
     * java.util.Date)
     */

    @Override
    public boolean setParam(final int paramIndex, final String value) throws Exception {
        statement.setString(paramIndex, value);
        registerParamValue(paramIndex, "string:" + value);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setParam(java.lang.String,
     * double)
     */

    @Override
    public boolean setParam(final String paramName, final byte[] value) throws Exception {
        final List<?> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf((String) paramList.get(i));// (new
                                                                      // Integer().intValue();
            statement.setBytes(ID, value);
        }
        registerParamValue(paramName, "bytes:length=" + (value == null ? 0 : value.length));
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setParam(java.lang.String,
     * java.io.InputStream)
     */

    @Override
    public boolean setParam(final String paramName, final Date value) throws Exception {
        if (value == null)
            setNullParam(paramName);
        else {
            final Timestamp sqlValue = Convert.date2SQLDateTime(value);
            final List<String> paramList = params.get(paramName.toLowerCase());
            if (paramList == null)
                return false;
            for (int i = 0; i < paramList.size(); i++) {
                final int ID = Integer.valueOf(paramList.get(i));// (new
                                                                 // Integer()).intValue();
                statement.setTimestamp(ID, sqlValue);
            }
        }
        registerParamValue(paramName, "date:" + (value == null ? "null" : Convert.datetime2Str(value)));
        return true;
    }

    @Override
    public void setObject(int index, Object obj) throws Exception {
        statement.setObject(index, obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setParam(java.lang.String, int)
     */

    @Override
    public boolean setParam(final String paramName, final double value) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));// (new
                                                             // Integer()).intValue();
            statement.setDouble(ID, value);
        }
        registerParamValue(paramName, "double:" + value);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setParam(java.lang.String,
     * java.lang.String)
     */

    @Override
    public boolean setParam(final String paramName, final InputStream value) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));// (new
                                                             // Integer()).intValue();
            statement.setBlob(ID, value);
        }
        return true;
    }

    @Override
    public boolean setParam(final String paramName, final int value) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));// (new
                                                             // Integer(paramList.get(i))).intValue();
            statement.setLong(ID, value);
        }
        registerParamValue(paramName, "int:" + value);
        return true;
    }

    @Override
    public boolean setParam(final String paramName, final String value) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));
            statement.setString(ID, value);
        }
        registerParamValue(paramName, "string:" + value);
        return true;
    }

    @Override
    public boolean setParam(final String paramName, final Object obj) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));
            ;
            statement.setObject(ID, obj);
        }
        registerParamValue(paramName, "obj:" + String.valueOf(obj));
        return true;
    }

    @Override
    public boolean setParam(final int index, final Object obj) throws Exception {
        statement.setObject(index, obj);
        registerParamValue(index, "obj:" + String.valueOf(obj));
        return true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chinadci.gt.db.oracle.ICommand#setSQL(java.lang.String)
     */
    @Override
    public final void setSQL(final String sql) throws Exception {
        close();
        params.clear();
        paramsList.clear();
        this.sql = DBSqlUtils.processEnterChart(sql);
        if (!skipParserSQL) {
            preparedSQL = DBSqlUtils.transNamedParam(this.sql);
            DBSqlUtils.parserSQLParam(this.sql, params, paramsList, extParamsList);
        } else
            preparedSQL = this.sql;
        if ((connection != null) && !StringUtils.isEmpty(this.sql))
            statement = connection.prepareStatement(preparedSQL);
    }

    @Override
    public ResultSetMetaData getMetaData() throws Exception {
        return metaData;
    }

    /**
     * 获取CMD
     * 
     * @param cmd
     * @param columnIndex
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    public Object getValue(final int columnIndex) throws Exception {
        Object value = "";
        int columnType = metaData.getColumnType(columnIndex);
        switch (columnType) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
        case Types.NCHAR:
        case Types.NVARCHAR:
        case Types.LONGNVARCHAR:
            value = getString(columnIndex);
            break;
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
            value = getInt(columnIndex);
            break;
        case Types.BIGINT:
            value = getLong(columnIndex);
            break;
        case Types.FLOAT:
        case Types.REAL:
        case Types.DOUBLE:
        case Types.NUMERIC:
            value = getDouble(columnIndex);
            break;
        case Types.DECIMAL: // xxxx
            value = getDouble(columnIndex);
            break;
        case Types.DATE:
        case Types.TIMESTAMP:
        case Types.TIME:
            value = Convert.date2Str(getDateTime(columnIndex));
            break;
        case Types.BLOB:
        case Types.BINARY:
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
            value = Convert.bytes2Str(getBytes(columnIndex));
            break;
        case Types.CLOB:
        case Types.NCLOB:
            value = getString(columnIndex);
            break;
        case Types.BOOLEAN:
            value = getBoolean(columnIndex);
            break;
//<<<<<<< .mine
        case 2002:
        	byte[] geoBytes= getBytes(columnIndex);
        	if(geoBytes!=null&&geoBytes.length!=0){
        		value =GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(JGeometry.load(geoBytes))));
        	}
            break;
//=======
//        case 2002:
//            value =GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(JGeometry.load(getBytes(columnIndex)))));
//            break;
//>>>>>>> .r149
        default:
            value = getString(columnIndex);
            break;
        }
        return value;
    }

    @Override
    public long getLong(final int index) throws Exception {
        return resultSet.getLong(index);
    }

    @Override
    public long getLong(final String filename) throws Exception {
        return resultSet.getLong(filename);
    }

    @Override
    public boolean getBoolean(final String fieldname) throws Exception {
        return resultSet.getBoolean(fieldname);
    }

    @Override
    public boolean getBoolean(final int index) throws Exception {
        return resultSet.getBoolean(index);
    }

    @Override
    public BigDecimal getBigDecimal(final String fieldname) throws Exception {
        return resultSet.getBigDecimal(fieldname);
    }

    @Override
    public BigDecimal getBigDecimal(final int index) throws Exception {
        return resultSet.getBigDecimal(index);
    }

    @Override
    public boolean setParam(final String paramName, final BigDecimal value) throws Exception {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return false;
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));
            ;
            statement.setBigDecimal(ID, value);
        }
        registerParamValue(paramName, "bigdecimal:" + value);
        return true;
    }

    @Override
    public boolean setParam(final int index, final BigDecimal value) throws Exception {
        statement.setBigDecimal(index, value);
        registerParamValue(index, "bigdecimal:" + value);
        return true;

    }

    @Override
    public void addBatch() throws Exception {
        statement.addBatch();
    }

    @Override
    public int[] executeBatch() throws Exception {
        return statement.executeBatch();
    }

    @Override
    public String debugInfo() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SQL:\n").append(sql).append("\n");
        // if (!skipParserSQL)
        // sb.append("Transform SQL:\n").append(this.preparedSQL).append("\n");
        if (!DBRuntimeConfig.instance.isRelease) {
            sb.append("Params:");
            if (!paramIndex2Value.isEmpty()) {
                final Iterator<Entry<Integer, String>> iterator = paramIndex2Value.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Entry<Integer, String> entry = iterator.next();
                    sb.append("  index:").append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
                }
            }
            if (!paramName2Value.isEmpty()) {
                final Iterator<Entry<String, String>> iterator = paramName2Value.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Entry<String, String> entry = iterator.next();
                    sb.append("  Name:").append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @param paramIndex
     * @param value
     */
    private void registerParamValue(final int paramIndex, final String value) {
        if (!DBRuntimeConfig.instance.isRelease) {
            if (paramIndex2Value.containsKey(paramIndex))
                paramIndex2Value.remove(paramIndex);
            paramIndex2Value.put(paramIndex, value);
        }
    }

    /**
     * 
     * @param paramName
     * @param value
     */
    private void registerParamValue(final String paramName, final String value) {
        if (!DBRuntimeConfig.instance.isRelease) {
            if (paramName2Value.containsKey(paramName))
                paramName2Value.remove(paramName);
            paramName2Value.put(paramName, value);
        }
    }

    protected abstract IDBHelper getDBHelper();

    @Override
    public abstract boolean setParam(String paramName, String[] values) throws Exception;

    @Override
    public abstract boolean setParam(String paramName, long[] values) throws Exception;

    @Override
    public abstract boolean setParam(String paramName, int[] values) throws Exception;

    @Override
    public abstract boolean setParam(String paramName, double[] values) throws Exception;

    @Override
    public abstract boolean setParam(int index, long[] values) throws Exception;

    @Override
    public abstract boolean setParam(int index, int[] values) throws Exception;

    @Override
    public abstract boolean setParam(int index, double[] values) throws Exception;

    /**
     * 根据参数名称获取参数索引
     * 
     * @param paramName
     * @return
     */
    protected int[] getParamIndexsByName(final String paramName) {
        final List<String> paramList = params.get(paramName.toLowerCase());
        if (paramList == null)
            return new int[0];
        final int[] result = new int[paramList.size()];
        for (int i = 0; i < paramList.size(); i++) {
            final int ID = Integer.valueOf(paramList.get(i));
            result[i] = ID;
        }
        return result;
    }

}
