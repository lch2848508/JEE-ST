package com.estudio.intf.design.objects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ObjectTreeRecord;
import com.estudio.intf.db.IDBCommand;

public interface IObjectTreeService {

    public static String OBJECT_FORM = "ObjectForm";
    public static String OBJECT_REPORT = "ObjectReport";
    public static String OBJECT_WORKFLOW = "ObjectReport";

    /**
     * ɾ����¼
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     * @throws DBException
     * @throws Exception
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
    public abstract void fillRecord(IDBCommand cmd, ObjectTreeRecord record) throws Exception;

    /**
     * �õ�������
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getFormObjects(long version) throws Exception, DBException;

    /**
     * @throws DBException
     *             ��ȡĿ¼��
     * 
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws
     */
    public abstract JSONObject getObjectsTree(long pid, long userID) throws Exception, DBException;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ObjectTreeRecord getRecord(Connection con, long id) throws Exception;

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
    public abstract ArrayList<ObjectTreeRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * �õ�������
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getReportObjects() throws Exception, DBException;

    /**
     * ��ȡ��ѯ�б�
     * 
     * @return
     * @throws Exception
     */
    public abstract JSONObject getQueryObjects() throws Exception;

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
    public abstract ObjectTreeRecord newRecord();

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
    public abstract boolean saveRecord(Connection con, ObjectTreeRecord record) throws Exception;

    /**
     * ǩ��ǩ������
     * 
     * @param id
     * @param isCheckIn
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject versionControlObject(long id, boolean isCheckIn, long userID) throws Exception, DBException;

    /**
     * �������
     * 
     * @param objectIds
     * @throws Exception
     */
    public abstract void sortObjects(List<String> objectIds) throws Exception;

}
