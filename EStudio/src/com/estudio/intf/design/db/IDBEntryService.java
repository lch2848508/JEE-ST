package com.estudio.intf.design.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.db.DBFieldDataType;

public interface IDBEntryService {

    /**
     * 得到Entry相关的外键信息
     * 
     * @param code
     * @return
     */
    public JSONObject getDBEntryLinks(Connection con, long version);

    /**
     * 得到数据库对象实体关系列表
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public List<JSONObject> getDBEntryLinksList(Connection con) throws Exception, DBException;

    /**
     * 得到DDL的当前时间戳
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public long getDDLVersion(Connection con) throws Exception;

    /**
     * 得到实体对象版本
     * 
     * @param con
     * @param code
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public long getDBEntryVersion(Connection con, String code) throws Exception;

    /**
     * 生成Entry列表JSON
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public List<JSONObject> getEntrysJSON(Connection con) throws Exception;

    /**
     * 创建数据库对象
     * 
     * @param parameter
     * @return
     */
    public JSONObject saveDBEntry(String ddl_json);

    /**
     * 得到实体DDL JSON对象
     * 
     * @param code
     * @param version
     * @return
     */
    public JSONObject getEntryInfo(String code, long version);

    /**
     * 得到数据实体
     * 
     * @param con
     * @param code2Version
     * @return
     */
    public JSONObject getEntryInfos(Connection con, HashMap<String, Integer> code2Version);

    /**
     * 批量获取table结构
     * 
     * @param codes
     * @return
     */
    public JSONObject getEntryInfos(Connection con, String codes);

    /**
     * 得到实体DDL JSON对象
     * 
     * @param code
     * @param cacheVersion
     * @return
     */
    public JSONObject getEntryInfo(Connection con, String code, long version);

    /**
     * 得到DBEntry的版本信息
     * 
     * @param parameter
     * @return
     */
    public JSONObject getDBEntryVersion(String code);

    /**
     * 删除数据库对象
     * 
     * @param parameter
     * @return
     */
    public JSONObject dropDBEntry(String[] codes);

    /**
     * 
     * @param parentTable
     * @param parentField
     * @param childTable
     * @param childField
     * @return
     */
    public JSONObject createForeighKey(String parentTable, String parentField, String childTable, String childField);

    /**
     * 删除数据库外键
     * 
     * @param parameter
     * @param parameter2
     * @return
     */
    public JSONObject dropDBEntryKeyIndex(String tableName, String indexName);

    /**
     * 重命名对象实体
     * 
     * @param parameter
     * @param parameter2
     * @param parameter3
     * @return
     */
    public JSONObject renameDBEntry(String oldCode, String newCode, String comment);

    /**
     * 得到数据源
     * 
     * @param includeDiagrams
     * 
     * @param parameter
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public JSONObject getAllEntrys(String code2VersionStr, boolean includeDiagrams) throws Exception, DBException;

    /**
     * 获取支持对象的数据类型
     * 
     * @return
     */
    public ArrayList<DBFieldDataType> getSupportDBFieldDataTypes();

    /**
     * 获取支持的数据类型JSON数组列表
     * 
     * @return
     */
    public JSONArray getSupportDBFieldDataTypeJson();

    /**
     * 获取数据库的一些属性信息
     * 
     * @return
     */
    public JSONObject getDatabasePropertys();

    /**
     * 根据数据类型的字符串标识获取数据类型定义
     * 
     * @param dataType
     * @return
     */
    public DBFieldDataType getDBFieldDataTypeByTypeString(String dataType);

    /**
     * 
     * 根据字段类型英文名称获取其在数据类型中的列表
     * 
     * @param columnTypeName
     * @return
     */
    public long getColumnDataTypeIndex(String columnTypeName);

    /**
     * 获取预定义字段信息
     * 
     * @return
     * @throws Exception
     */
    public JSONObject getDBEntryPchJson() throws Exception;

    /**
     * 保存字段预定义
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public JSONObject saveDBEntryPchJson(String str) throws Exception;

    /**
     * 获取SQL的语法文件
     * 
     * @param paramStr
     * @return
     * @throws Exception
     */
    public JSONObject getLex(String paramStr) throws Exception;
}
