package com.estudio.intf.design.portal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.portal.PortalGroupRecord;
import com.estudio.intf.db.IDBCommand;

public interface IPortalGroupService {

    /**
     * ������ȡ����������
     * 
     * @param paramInt
     * @param str2Boolean
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject published(long id, boolean isPublished) throws Exception, DBException;

    /**
     * ȡ����Ŀ�鼰��Ŀ����Ϣ
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getGroupsAndItems() throws Exception, DBException;

    /**
     * �б�Portal Group��Group�µ�Item
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getGroupsAndItems(boolean includeRight, boolean includeRoles) throws Exception, DBException;

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
     * �����¼
     * 
     * @param con
     * @param record
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean saveRecord(Connection con, PortalGroupRecord record) throws Exception;

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
     * /** ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, PortalGroupRecord record) throws Exception;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract PortalGroupRecord getRecord(Connection con, long id) throws Exception;

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
    public abstract ArrayList<PortalGroupRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * ����һ����¼
     * 
     * @return
     */
    public abstract PortalGroupRecord newRecord();

    public abstract Map<String, String> getDesignEvnParams();

}
