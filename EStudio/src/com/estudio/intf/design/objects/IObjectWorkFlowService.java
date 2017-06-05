package com.estudio.intf.design.objects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.WorkFlowDesignInfo;
import com.estudio.intf.db.IDBCommand;

public interface IObjectWorkFlowService {

    /**
     * ��ȡ�汾��
     * 
     * @param con
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract long getVersion(Connection con, long id) throws Exception;

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
     * @throws JSONException
     * @throws DBException
     * @throws Exception
     */
    public abstract boolean saveRecord(Connection con, WorkFlowDesignInfo record) throws Exception;

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
     * /** ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, WorkFlowDesignInfo record) throws Exception;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract WorkFlowDesignInfo getRecord(Connection con, long id) throws Exception;

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
    public abstract ArrayList<WorkFlowDesignInfo> getRecords(Connection con, long pid) throws Exception;

    /**
     * ����һ����¼
     * 
     * @return
     */
    public abstract WorkFlowDesignInfo newRecord();

    /**
     * ��������
     * 
     * @param fromId
     * @param newName
     * @return
     */
    public abstract JSONObject copyWorkFlowDesignInfo(long fromId, String newName) throws Exception;

    /**
     * ��������
     * 
     * @param id
     * @param name
     * @param content
     * @return
     */
    public abstract JSONObject saveWorkFlowItemDesignInfo(long id, String name, String content) throws Exception;

    /**
     * �б�������
     * 
     * @return
     * @throws Exception
     */
    public abstract JSONObject listWorkFlowItemDesignInfos() throws Exception;

    /**
     * ɾ���������б���
     * 
     * @return
     * @throws Exception
     */
    public abstract JSONObject deleteWorkFlowDesignInfos(long id) throws Exception;

    /**
     * �����������б���˳��
     * 
     * @param id1
     * @param id2
     * @return
     * @throws Exception
     */
    public abstract JSONObject exchangeWorkFlowDesignInfo(long id1, long id2) throws Exception;

    /**
     * ��ȡ��Ϣ
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public abstract JSONObject getWorkFlowDesignInfo(long id, Connection con) throws Exception;

}
