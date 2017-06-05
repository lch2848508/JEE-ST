package com.estudio.intf.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.estudio.define.db.DBException;
import com.estudio.define.db.SQLParam;

/**
 * SQL��䷭�����
 * 
 * @author LSH
 * 
 */
public interface ISQLTrans {

    /**
     * ��������PL/SQL���͵�SQL��� ��Ҫ�����Դ���ݿ��޷�֧��PL/SQL������� ��Ҫ����Щ���ת��Ϊ�洢���̵�����
     * 
     * @param sql
     * @return
     * @throws DBException
     */
    public String transSQL4ProcSQL(String sql, List<SQLParam> params, Connection con) throws Exception;

    /**
     * �����ҳSQL���
     * 
     * @param sql
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4Page(String sql, String beginIndexParamName, String endIndexParamName, Connection con) throws Exception;

    /**
     * ��ҳ����SQL���
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4Page(String sql, String keyFieldName) throws Exception;

    /**
     * ��ҳ����SQL���
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4Page(String sql) throws Exception;

    /**
     * ��ҳ����SQL���
     * 
     * @param sql
     * @param countFieldName
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transCountSQL4Page(String sql, String countFieldName, Connection con) throws Exception;

    /**
     * ��ҳ����SQL���
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transCountSQL4Page(String sql) throws Exception;

    /**
     * ���ݼ�¼����
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4SearchByKeyField(String sql, String keyFieldName, String paramName) throws Exception;

    /**
     * ȥ��SQL����еĲ���
     * 
     * @param sql
     * @param params
     * @return
     */
    public String removeSQLParams(String sql, List<String> params);

    /**
     * �ж�һ������Ƿ�ΪSQL���
     * 
     * @param sql
     * @return
     */
    public boolean isSelectSQL(String sql);

    public boolean isSupportPageOptimize();

    public String generatePageOptimizeIDSQL(String sql, List<String> invalidParamList, String name) throws Exception;

    public boolean isSelectFieldContainExpress(String sql, String keyFieldName) throws Exception;
}
