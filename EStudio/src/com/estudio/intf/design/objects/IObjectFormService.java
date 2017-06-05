package com.estudio.intf.design.objects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ObjectFormsRecord;
import com.estudio.intf.db.IDBCommand;

public interface IObjectFormService {

    /**
     * ɾ����¼
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean deleteRecord(Connection con, long id) throws Exception;

    /**
     * ����˳��
     * 
     * @param con
     * @param id_1
     * @param id_2
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean exchange(Connection con, long id_1, long id_2) throws Exception;

    /**
     * /** ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, ObjectFormsRecord record) throws Exception;

    /**
     * ��ȡ���ؼ�������Դ�б� WorkFlow
     * 
     * @param con
     * @param id
     * @param version
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract JSONObject getFormControls(Connection con, long id, long version) throws Exception;

    /**
     * �õ�Form����汾��Ϣ
     * 
     * @param con
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract long getFormVersion(Connection con, long id) throws Exception;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ObjectFormsRecord getRecord(Connection con, long id) throws Exception;

    /**
     * �õ������б�
     * 
     * @param id
     * @param version
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getRecordInfo(long id, long version) throws Exception, DBException;

    /**
     * �õ����ݼ�
     * 
     * @param con
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ArrayList<ObjectFormsRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * �ƶ��ڵ�
     * 
     * @param con
     * @param id
     * @param p_id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean moveTo(Connection con, long id, long p_id) throws Exception;

    /**
     * ����һ����¼
     * 
     * @return
     */
    public abstract ObjectFormsRecord newRecord();

    /**
     * �����¼
     * 
     * @param con
     * @param record
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean saveRecord(Connection con, ObjectFormsRecord record) throws Exception;

    /**
     * ��������� �������ڹ�����ϵͳʹ��
     * 
     * @param con
     * @param id
     * @param ds4wf
     * @param cs4wf
     * @return
     * @throws DBException
     * @throws Exception
     */
    public abstract JSONObject saveRecord4WorkFlow(Connection con, long id, byte[] ds4wf, byte[] cs4wf) throws Exception, DBException;

    /**
     * �б������ԴCacheName�б�
     * 
     * @param con
     * @return
     * @throws Exception
     */
    public abstract JSONObject getDataSourceCacheKeyList(Connection con) throws Exception;

    /**
     * ɾ����CacheNameList
     * 
     * @param con
     * @param key
     * @return
     * @throws Exception
     */
    public abstract JSONObject deleteDataSourceCacheKey(Connection con, String key) throws Exception;

    /**
     * �����CacheKey
     * 
     * @param con
     * @param key
     * @return
     * @throws Exception
     */
    public abstract JSONObject saveDataSourceCacheKey(Connection con, String key) throws Exception;

    /**
     * ��ȡ����Դģ���б�
     * 
     * @param con
     * @return
     * @throws Exception
     */
    public abstract JSONObject getDataSourceTemplateList(Connection con) throws Exception;

    /**
     * ��������Դģ��
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public abstract JSONObject deleteDataSourceTemplate(Connection con, String category, String caption) throws Exception;

    /**
     * ��������Դģ��
     * 
     * @param con
     * @param category
     * @param caption
     * @param content
     * @return
     * @throws Exception
     */
    public abstract JSONObject saveDataSourceTemplate(Connection con, String category, String caption, String content) throws Exception;

    /**
     * ��ȡ����Դģ������
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public abstract JSONObject getDataSourceTemplate(Connection con, String category, String caption) throws Exception;
}
