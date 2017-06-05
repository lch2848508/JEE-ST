package com.estudio.intf.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.estudio.define.db.DBException;
import com.estudio.define.db.SQLParam;

/**
 * SQL语句翻译服务
 * 
 * @author LSH
 * 
 */
public interface ISQLTrans {

    /**
     * 翻译类似PL/SQL类型的SQL语句 主要解决开源数据库无法支持PL/SQL类似语句 需要将这些语句转化为存储过程的问题
     * 
     * @param sql
     * @return
     * @throws DBException
     */
    public String transSQL4ProcSQL(String sql, List<SQLParam> params, Connection con) throws Exception;

    /**
     * 翻译分页SQL语句
     * 
     * @param sql
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4Page(String sql, String beginIndexParamName, String endIndexParamName, Connection con) throws Exception;

    /**
     * 分页求总SQL语句
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4Page(String sql, String keyFieldName) throws Exception;

    /**
     * 分页求总SQL语句
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4Page(String sql) throws Exception;

    /**
     * 分页求总SQL语句
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
     * 分页求总SQL语句
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transCountSQL4Page(String sql) throws Exception;

    /**
     * 根据记录查找
     * 
     * @param sql
     * @return
     * @throws SQLException
     *             , DBException
     */
    public String transSQL4SearchByKeyField(String sql, String keyFieldName, String paramName) throws Exception;

    /**
     * 去除SQL语句中的参数
     * 
     * @param sql
     * @param params
     * @return
     */
    public String removeSQLParams(String sql, List<String> params);

    /**
     * 判断一条语句是否为SQL语句
     * 
     * @param sql
     * @return
     */
    public boolean isSelectSQL(String sql);

    public boolean isSupportPageOptimize();

    public String generatePageOptimizeIDSQL(String sql, List<String> invalidParamList, String name) throws Exception;

    public boolean isSelectFieldContainExpress(String sql, String keyFieldName) throws Exception;
}
