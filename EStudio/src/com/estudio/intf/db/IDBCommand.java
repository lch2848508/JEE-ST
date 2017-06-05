package com.estudio.intf.db;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.estudio.define.db.DBException;

/**
 * 数据Command定义接口
 * 
 * @author LSH
 * 
 */
public interface IDBCommand {
    /**
     * 克隆对象
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract IDBCommand clone(Connection con) throws Exception;

    /**
     * 克隆对象
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract IDBCommand clone(Connection con, Map<String, String> extParamName2Value) throws Exception;

    /**
     * 关闭对象并释放资源
     * 
     * @throws SQLException
     *             , DBException
     */
    public abstract void close() throws Exception;

    /**
     * 执行SQL语句
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean execute() throws Exception;

    /**
     * 执行SQL语句
     * 
     * @param SQL
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean execute(String SQL) throws Exception;

    /**
     * 执行SQL语句并返回一个结果
     * 
     * @return 成功返回true 否则返回 false
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean executeQuery() throws Exception;

    /**
     * 执行一条SQL语句
     * 
     * @param SQL
     * @return 成功返回true 否则返回 false
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean executeQuery(String SQL) throws Exception;

    /**
     * 读取BLOB字段值
     * 
     * @param index
     *            字段索引
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract InputStream getBlob(int index) throws Exception;

    /**
     * 读取BLOB字段值
     * 
     * @param columnName
     *            字段名称
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract InputStream getBlob(String columnName) throws Exception;

    /**
     * 读取BLOB字段值
     * 
     * @param index
     *            字段索引
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract String getBlobStr(int index) throws Exception;

    /**
     * 读取BLOB字段值
     * 
     * @param columnName
     *            字段名称
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract String getBlobStr(String columnName) throws Exception;

    /**
     * @param i
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract byte[] getBytes(int i) throws Exception;

    /**
     * @param columnName
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract byte[] getBytes(String columnName) throws Exception;

    /**
     * 得到数据库连接
     * 
     * @return
     */
    public abstract Connection getConnection();

    /**
     * 读取日期时间型字段值
     * 
     * @param index
     *            字段索引
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract Date getDate(int index) throws Exception;

    /**
     * 读取日期时间型字段
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Time getDateTime(int index) throws Exception;

    /**
     * 读取日期时间型字段
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Time getDateTime(String fieldName) throws Exception;

    /**
     * 读取日期时间型字段值
     * 
     * @param columnName
     *            字段名称
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract Date getDate(String columnName) throws Exception;

    /**
     * 读取双精度浮点字段值
     * 
     * @param index
     *            字段索引
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract double getDouble(int index) throws Exception;

    /**
     * 读取双精度浮点字段值
     * 
     * @param columnName
     *            字段名称
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract double getDouble(String columnName) throws Exception;

    /**
     * 得到字段列表值
     * 
     * @return
     */
    public abstract ArrayList<String> getFieldNames();

    /**
     * 读取浮点字段值
     * 
     * @param index
     *            字段索引
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract float getFloat(int index) throws Exception;

    /**
     * 读取浮点字段值
     * 
     * @param columnName
     *            字段名称
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract float getFloat(String columnName) throws Exception;

    /**
     * 读取整形字段值
     * 
     * @param index
     *            字段索引
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract int getInt(int index) throws Exception;

    /**
     * 读取整形字段值
     * 
     * @param columnName
     *            字段名称
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract int getInt(String columnName) throws Exception;

    /**
     * 得到参数列表
     * 
     * @return
     */
    public abstract ArrayList<String> getParams();

    public abstract ResultSetMetaData getResultSetMetaData() throws Exception;

    /**
     * 读取字符串字段值
     * 
     * @param index
     *            字段索引
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract String getString(int index) throws Exception;

    /**
     * 读取字符串字段值
     * 
     * @param columnName
     *            字段名称
     * @return 值
     * @throws SQLException
     *             , DBException
     */
    public abstract String getString(String columnName) throws Exception;

    /**
     * 判断字段是否存在
     * 
     * @param name
     * @return
     */
    public abstract boolean isExistsField(String name);

    /**
     * 数据集游标向下移动
     * 
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean next() throws Exception;

    /**
     * 设置浮点类型参数
     * 
     * @param paramIndex
     *            参数顺序
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setFloat(int paramIndex, float value) throws Exception;

    /**
     * 设置浮点类型参数
     * 
     * @param paramName
     *            参数名称
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setFloat(String paramName, float value) throws Exception;

    public abstract boolean setNullParam(String paramName) throws Exception;

    /**
     * 设置BLOB类型参数
     * 
     * @param paramIndex
     *            参数顺序
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, byte[] value) throws Exception;

    /**
     * 设置日期时间类型参数
     * 
     * @param paramIndex
     *            参数顺序
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, Date value) throws Exception;

    /**
     * 设置双精度浮点数类型参数
     * 
     * @param paramIndex
     *            参数顺序
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, double value) throws Exception;

    /**
     * 设置BLOB类型参数
     * 
     * @param paramIndex
     *            参数顺序
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, InputStream value) throws Exception;

    /**
     * 设置整形类型参数
     * 
     * @param paramIndex
     *            参数顺序
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(int paramIndex, int value) throws Exception;

    /**
     * 设置长整数数据
     * 
     * @param paramIndex
     * @param value
     * @return
     * @throws Exception
     */
    public abstract boolean setParam(int paramIndex, long value) throws Exception;

    /**
     * 设置字符串类型参数
     * 
     * @param paramIndex
     *            参数顺序
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(int paramIndex, String value) throws Exception;

    /**
     * 设置BLOB类型参数
     * 
     * @param paramName
     *            参数名称
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, byte[] value) throws Exception;

    /**
     * 设置日期时间类型参数
     * 
     * @param paramName
     *            参数名称
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, Date value) throws Exception;

    /**
     * 设置双精度浮点数类型参数
     * 
     * @param paramName
     *            参数名称
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, double value) throws Exception;

    /**
     * 设置BLOB类型参数
     * 
     * @param paramName
     *            参数名称
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, InputStream value) throws Exception;

    /**
     * 设置整形类型参数
     * 
     * @param paramName
     *            参数名称
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, int value) throws Exception;

    /**
     * 设置字符串类型参数
     * 
     * @param paramName
     *            参数名称
     * @param value
     *            参数值
     * @return 成功返回 true 否则 返回 false
     * @throws SQLException
     *             , DBException
     */

    /**
     * 设置参数
     * 
     * @param paramName
     * @param value
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, String value) throws Exception;

    /**
     * 设置参数
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, String[] values) throws Exception;

    /**
     * 设置参数
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, long[] values) throws Exception;

    /**
     * 设置参数
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, int[] values) throws Exception;

    /**
     * 设置参数
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, double[] values) throws Exception;

    /**
     * 设置参数
     * 
     * @param index
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(int index, long[] values) throws Exception;

    /**
     * 设置参数
     * 
     * @param index
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(int index, int[] values) throws Exception;

    /**
     * 设置参数
     * 
     * @param index
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(int index, double[] values) throws Exception;

    /**
     * 设置SQL语句<br>
     * 设置SQL语句需要两个步骤 <br>
     * 1.关闭原来的资源 <br>
     * 2.设置语句并分析SQL中的参数
     * 
     * @param SQL
     * @throws SQLException
     *             , DBException
     */
    public abstract void setSQL(String SQL) throws Exception;

    
    public abstract ResultSetMetaData getMetaData() throws Exception;

    /**
     * 获取CMD
     * 
     * @param cmd
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract Object getValue(int index) throws Exception;

    /**
     * 获取长整数
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long getLong(int index) throws Exception;

    /**
     * 获取长整数
     * 
     * @param filename
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long getLong(String filename) throws Exception;

    /**
     * 获取布尔值
     * 
     * @param filename
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean getBoolean(String filename) throws Exception;

    /**
     * 获取布尔值
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean getBoolean(int index) throws Exception;

    /**
     * 获取获取长整数
     * 
     * @param filename
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract BigDecimal getBigDecimal(String filename) throws Exception;

    /**
     * 获取长整数
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract BigDecimal getBigDecimal(int index) throws Exception;

    /**
     * 设置参数
     * 
     * @param paramName
     * @param value
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(String paramName, BigDecimal value) throws Exception;

    /**
     * 设置参数
     * 
     * @param index
     * @param value
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int index, BigDecimal value) throws Exception;

    /**
     * 设置参数
     * 
     * @param paramName
     * @param obj
     * @throws Exception
     */
    public abstract boolean setParam(String paramName, Object obj) throws Exception;

    /**
     * 设置参数
     * 
     * @param index
     * @param obj
     */
    public abstract boolean setParam(int index, Object obj) throws Exception;

    /**
     * 添加批量操作
     * 
     * @throws SQLException
     *             , DBException
     */
    public abstract void addBatch() throws Exception;

    /**
     * 执行批量操纵
     * 
     * @throws SQLException
     *             , DBException
     */
    public abstract int[] executeBatch() throws Exception;

    /**
     * 获取CLOB值
     * 
     * @param index
     * @return
     * @throws Exception
     */
    public abstract String getClob(int index) throws Exception;

    /**
     * 获取CLOB值
     * 
     * @param columnName
     * @return
     * @throws Exception
     */
    public abstract String getClob(String columnName) throws Exception;

    /**
     * 获取调试信息
     * 
     * @return
     */
    public abstract String debugInfo();

    public abstract Object getObject(int i) throws SQLException;

    public abstract Object getObject(String fieldName) throws SQLException;

    public abstract void setObject(int i, Object store) throws Exception;

}
