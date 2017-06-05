package com.estudio.intf.design.objects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONArray;

import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ComboBoxTemplateRecord;
import com.estudio.intf.db.IDBCommand;

public interface IObjectComboBoxTemplateService {

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
    public abstract void fillRecord(IDBCommand cmd, ComboBoxTemplateRecord record) throws Exception;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ComboBoxTemplateRecord getRecord(Connection con, long id) throws Exception;

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
    public abstract ArrayList<ComboBoxTemplateRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * �õ�ģ���б�
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONArray getTemplate(Connection con) throws Exception, DBException;

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
    public abstract ComboBoxTemplateRecord newRecord();

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
    public abstract boolean saveRecord(Connection con, ComboBoxTemplateRecord record) throws Exception;

}
