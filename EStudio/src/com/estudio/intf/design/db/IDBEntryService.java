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
     * �õ�Entry��ص������Ϣ
     * 
     * @param code
     * @return
     */
    public JSONObject getDBEntryLinks(Connection con, long version);

    /**
     * �õ����ݿ����ʵ���ϵ�б�
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
     * �õ�DDL�ĵ�ǰʱ���
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public long getDDLVersion(Connection con) throws Exception;

    /**
     * �õ�ʵ�����汾
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
     * ����Entry�б�JSON
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public List<JSONObject> getEntrysJSON(Connection con) throws Exception;

    /**
     * �������ݿ����
     * 
     * @param parameter
     * @return
     */
    public JSONObject saveDBEntry(String ddl_json);

    /**
     * �õ�ʵ��DDL JSON����
     * 
     * @param code
     * @param version
     * @return
     */
    public JSONObject getEntryInfo(String code, long version);

    /**
     * �õ�����ʵ��
     * 
     * @param con
     * @param code2Version
     * @return
     */
    public JSONObject getEntryInfos(Connection con, HashMap<String, Integer> code2Version);

    /**
     * ������ȡtable�ṹ
     * 
     * @param codes
     * @return
     */
    public JSONObject getEntryInfos(Connection con, String codes);

    /**
     * �õ�ʵ��DDL JSON����
     * 
     * @param code
     * @param cacheVersion
     * @return
     */
    public JSONObject getEntryInfo(Connection con, String code, long version);

    /**
     * �õ�DBEntry�İ汾��Ϣ
     * 
     * @param parameter
     * @return
     */
    public JSONObject getDBEntryVersion(String code);

    /**
     * ɾ�����ݿ����
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
     * ɾ�����ݿ����
     * 
     * @param parameter
     * @param parameter2
     * @return
     */
    public JSONObject dropDBEntryKeyIndex(String tableName, String indexName);

    /**
     * ����������ʵ��
     * 
     * @param parameter
     * @param parameter2
     * @param parameter3
     * @return
     */
    public JSONObject renameDBEntry(String oldCode, String newCode, String comment);

    /**
     * �õ�����Դ
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
     * ��ȡ֧�ֶ������������
     * 
     * @return
     */
    public ArrayList<DBFieldDataType> getSupportDBFieldDataTypes();

    /**
     * ��ȡ֧�ֵ���������JSON�����б�
     * 
     * @return
     */
    public JSONArray getSupportDBFieldDataTypeJson();

    /**
     * ��ȡ���ݿ��һЩ������Ϣ
     * 
     * @return
     */
    public JSONObject getDatabasePropertys();

    /**
     * �����������͵��ַ�����ʶ��ȡ�������Ͷ���
     * 
     * @param dataType
     * @return
     */
    public DBFieldDataType getDBFieldDataTypeByTypeString(String dataType);

    /**
     * 
     * �����ֶ�����Ӣ�����ƻ�ȡ�������������е��б�
     * 
     * @param columnTypeName
     * @return
     */
    public long getColumnDataTypeIndex(String columnTypeName);

    /**
     * ��ȡԤ�����ֶ���Ϣ
     * 
     * @return
     * @throws Exception
     */
    public JSONObject getDBEntryPchJson() throws Exception;

    /**
     * �����ֶ�Ԥ����
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public JSONObject saveDBEntryPchJson(String str) throws Exception;

    /**
     * ��ȡSQL���﷨�ļ�
     * 
     * @param paramStr
     * @return
     * @throws Exception
     */
    public JSONObject getLex(String paramStr) throws Exception;
}
