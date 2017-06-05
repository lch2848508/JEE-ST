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
 * ����Command����ӿ�
 * 
 * @author LSH
 * 
 */
public interface IDBCommand {
    /**
     * ��¡����
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract IDBCommand clone(Connection con) throws Exception;

    /**
     * ��¡����
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract IDBCommand clone(Connection con, Map<String, String> extParamName2Value) throws Exception;

    /**
     * �رն����ͷ���Դ
     * 
     * @throws SQLException
     *             , DBException
     */
    public abstract void close() throws Exception;

    /**
     * ִ��SQL���
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean execute() throws Exception;

    /**
     * ִ��SQL���
     * 
     * @param SQL
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean execute(String SQL) throws Exception;

    /**
     * ִ��SQL��䲢����һ�����
     * 
     * @return �ɹ�����true ���򷵻� false
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean executeQuery() throws Exception;

    /**
     * ִ��һ��SQL���
     * 
     * @param SQL
     * @return �ɹ�����true ���򷵻� false
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean executeQuery(String SQL) throws Exception;

    /**
     * ��ȡBLOB�ֶ�ֵ
     * 
     * @param index
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract InputStream getBlob(int index) throws Exception;

    /**
     * ��ȡBLOB�ֶ�ֵ
     * 
     * @param columnName
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract InputStream getBlob(String columnName) throws Exception;

    /**
     * ��ȡBLOB�ֶ�ֵ
     * 
     * @param index
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract String getBlobStr(int index) throws Exception;

    /**
     * ��ȡBLOB�ֶ�ֵ
     * 
     * @param columnName
     *            �ֶ�����
     * @return ֵ
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
     * �õ����ݿ�����
     * 
     * @return
     */
    public abstract Connection getConnection();

    /**
     * ��ȡ����ʱ�����ֶ�ֵ
     * 
     * @param index
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract Date getDate(int index) throws Exception;

    /**
     * ��ȡ����ʱ�����ֶ�
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Time getDateTime(int index) throws Exception;

    /**
     * ��ȡ����ʱ�����ֶ�
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Time getDateTime(String fieldName) throws Exception;

    /**
     * ��ȡ����ʱ�����ֶ�ֵ
     * 
     * @param columnName
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract Date getDate(String columnName) throws Exception;

    /**
     * ��ȡ˫���ȸ����ֶ�ֵ
     * 
     * @param index
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract double getDouble(int index) throws Exception;

    /**
     * ��ȡ˫���ȸ����ֶ�ֵ
     * 
     * @param columnName
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract double getDouble(String columnName) throws Exception;

    /**
     * �õ��ֶ��б�ֵ
     * 
     * @return
     */
    public abstract ArrayList<String> getFieldNames();

    /**
     * ��ȡ�����ֶ�ֵ
     * 
     * @param index
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract float getFloat(int index) throws Exception;

    /**
     * ��ȡ�����ֶ�ֵ
     * 
     * @param columnName
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract float getFloat(String columnName) throws Exception;

    /**
     * ��ȡ�����ֶ�ֵ
     * 
     * @param index
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract int getInt(int index) throws Exception;

    /**
     * ��ȡ�����ֶ�ֵ
     * 
     * @param columnName
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract int getInt(String columnName) throws Exception;

    /**
     * �õ������б�
     * 
     * @return
     */
    public abstract ArrayList<String> getParams();

    public abstract ResultSetMetaData getResultSetMetaData() throws Exception;

    /**
     * ��ȡ�ַ����ֶ�ֵ
     * 
     * @param index
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract String getString(int index) throws Exception;

    /**
     * ��ȡ�ַ����ֶ�ֵ
     * 
     * @param columnName
     *            �ֶ�����
     * @return ֵ
     * @throws SQLException
     *             , DBException
     */
    public abstract String getString(String columnName) throws Exception;

    /**
     * �ж��ֶ��Ƿ����
     * 
     * @param name
     * @return
     */
    public abstract boolean isExistsField(String name);

    /**
     * ���ݼ��α������ƶ�
     * 
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean next() throws Exception;

    /**
     * ���ø������Ͳ���
     * 
     * @param paramIndex
     *            ����˳��
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setFloat(int paramIndex, float value) throws Exception;

    /**
     * ���ø������Ͳ���
     * 
     * @param paramName
     *            ��������
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setFloat(String paramName, float value) throws Exception;

    public abstract boolean setNullParam(String paramName) throws Exception;

    /**
     * ����BLOB���Ͳ���
     * 
     * @param paramIndex
     *            ����˳��
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, byte[] value) throws Exception;

    /**
     * ��������ʱ�����Ͳ���
     * 
     * @param paramIndex
     *            ����˳��
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, Date value) throws Exception;

    /**
     * ����˫���ȸ��������Ͳ���
     * 
     * @param paramIndex
     *            ����˳��
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, double value) throws Exception;

    /**
     * ����BLOB���Ͳ���
     * 
     * @param paramIndex
     *            ����˳��
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int paramIndex, InputStream value) throws Exception;

    /**
     * �����������Ͳ���
     * 
     * @param paramIndex
     *            ����˳��
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(int paramIndex, int value) throws Exception;

    /**
     * ���ó���������
     * 
     * @param paramIndex
     * @param value
     * @return
     * @throws Exception
     */
    public abstract boolean setParam(int paramIndex, long value) throws Exception;

    /**
     * �����ַ������Ͳ���
     * 
     * @param paramIndex
     *            ����˳��
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(int paramIndex, String value) throws Exception;

    /**
     * ����BLOB���Ͳ���
     * 
     * @param paramName
     *            ��������
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, byte[] value) throws Exception;

    /**
     * ��������ʱ�����Ͳ���
     * 
     * @param paramName
     *            ��������
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, Date value) throws Exception;

    /**
     * ����˫���ȸ��������Ͳ���
     * 
     * @param paramName
     *            ��������
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, double value) throws Exception;

    /**
     * ����BLOB���Ͳ���
     * 
     * @param paramName
     *            ��������
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, InputStream value) throws Exception;

    /**
     * �����������Ͳ���
     * 
     * @param paramName
     *            ��������
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    public abstract boolean setParam(String paramName, int value) throws Exception;

    /**
     * �����ַ������Ͳ���
     * 
     * @param paramName
     *            ��������
     * @param value
     *            ����ֵ
     * @return �ɹ����� true ���� ���� false
     * @throws SQLException
     *             , DBException
     */

    /**
     * ���ò���
     * 
     * @param paramName
     * @param value
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, String value) throws Exception;

    /**
     * ���ò���
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, String[] values) throws Exception;

    /**
     * ���ò���
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, long[] values) throws Exception;

    /**
     * ���ò���
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, int[] values) throws Exception;

    /**
     * ���ò���
     * 
     * @param paramName
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(String paramName, double[] values) throws Exception;

    /**
     * ���ò���
     * 
     * @param index
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(int index, long[] values) throws Exception;

    /**
     * ���ò���
     * 
     * @param index
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(int index, int[] values) throws Exception;

    /**
     * ���ò���
     * 
     * @param index
     * @param values
     * @return
     * @throws SQLException
     * @throws DBException
     */
    public abstract boolean setParam(int index, double[] values) throws Exception;

    /**
     * ����SQL���<br>
     * ����SQL�����Ҫ�������� <br>
     * 1.�ر�ԭ������Դ <br>
     * 2.������䲢����SQL�еĲ���
     * 
     * @param SQL
     * @throws SQLException
     *             , DBException
     */
    public abstract void setSQL(String SQL) throws Exception;

    
    public abstract ResultSetMetaData getMetaData() throws Exception;

    /**
     * ��ȡCMD
     * 
     * @param cmd
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract Object getValue(int index) throws Exception;

    /**
     * ��ȡ������
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long getLong(int index) throws Exception;

    /**
     * ��ȡ������
     * 
     * @param filename
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long getLong(String filename) throws Exception;

    /**
     * ��ȡ����ֵ
     * 
     * @param filename
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean getBoolean(String filename) throws Exception;

    /**
     * ��ȡ����ֵ
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean getBoolean(int index) throws Exception;

    /**
     * ��ȡ��ȡ������
     * 
     * @param filename
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract BigDecimal getBigDecimal(String filename) throws Exception;

    /**
     * ��ȡ������
     * 
     * @param index
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract BigDecimal getBigDecimal(int index) throws Exception;

    /**
     * ���ò���
     * 
     * @param paramName
     * @param value
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(String paramName, BigDecimal value) throws Exception;

    /**
     * ���ò���
     * 
     * @param index
     * @param value
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean setParam(int index, BigDecimal value) throws Exception;

    /**
     * ���ò���
     * 
     * @param paramName
     * @param obj
     * @throws Exception
     */
    public abstract boolean setParam(String paramName, Object obj) throws Exception;

    /**
     * ���ò���
     * 
     * @param index
     * @param obj
     */
    public abstract boolean setParam(int index, Object obj) throws Exception;

    /**
     * �����������
     * 
     * @throws SQLException
     *             , DBException
     */
    public abstract void addBatch() throws Exception;

    /**
     * ִ����������
     * 
     * @throws SQLException
     *             , DBException
     */
    public abstract int[] executeBatch() throws Exception;

    /**
     * ��ȡCLOBֵ
     * 
     * @param index
     * @return
     * @throws Exception
     */
    public abstract String getClob(int index) throws Exception;

    /**
     * ��ȡCLOBֵ
     * 
     * @param columnName
     * @return
     * @throws Exception
     */
    public abstract String getClob(String columnName) throws Exception;

    /**
     * ��ȡ������Ϣ
     * 
     * @return
     */
    public abstract String debugInfo();

    public abstract Object getObject(int i) throws SQLException;

    public abstract Object getObject(String fieldName) throws SQLException;

    public abstract void setObject(int i, Object store) throws Exception;

}
