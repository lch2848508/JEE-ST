package com.estudio.intf.design.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.db.DBDiagramRecord;
import com.estudio.intf.db.IDBCommand;

public interface IDBDiagramService {

    public static String OBJECT_TYPE = "Diagram";

    /**
     * ����ģ����ͼ
     * 
     * @param id
     * @param entrys
     * @return
     */
    public abstract JSONObject saveDiagramDBEntrys(long id, String entrys);

    /**
     * �õ�Diagram��DBEntry�б���Ϣ
     * 
     * @param v
     * @param version
     * 
     * @param str2Int
     * @return
     */
    public abstract JSONObject getDiagramDBEntrys(long id, long version, long linkVersion);

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
    public abstract boolean saveRecord(Connection con, DBDiagramRecord record) throws Exception;

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
    public abstract void fillRecord(IDBCommand cmd, DBDiagramRecord record) throws Exception;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract DBDiagramRecord getRecord(Connection con, long id) throws Exception;

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
    public abstract ArrayList<DBDiagramRecord> getRecords(Connection con) throws Exception;

    /**
     * �õ��б� ��Ҫ�����̰߳�ȫ
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ArrayList<JSONObject> getDiagramsJSON(Connection con) throws Exception;

    /**
     * ����һ����¼
     * 
     * @return
     */
    public abstract DBDiagramRecord newRecord();

    /**
     * �õ�Tree��Version
     * 
     * @return
     */
    public abstract JSONObject getTreeVersion();

    /**
     * ����ɾ��Diagram����
     * 
     * @param is
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean deleteRecords(Connection con, long[] ids) throws Exception;

}
