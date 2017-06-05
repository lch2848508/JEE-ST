package com.estudio.intf.design.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.define.design.user.RoleRecord;
import com.estudio.intf.db.IDBCommand;

public interface IRoleService {

    /**
     * �ƶ��ڵ�
     * 
     * @param con
     * @param id
     * @param p_id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean moveTo(Connection con, long id, long p_id) throws Exception;

    /**
     * �ƶ���ɫ����
     * 
     * @param con
     * @param try2Long
     * @param toID
     * @return
     */
    public abstract boolean moveRoleTypeTo(Connection con, long try2Long, long toID) throws Exception;

    /**
     * ����˳��
     * 
     * @param con
     * @param id_1
     * @param id_2
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean exchange(Connection con, long id_1, long id_2) throws Exception;

    /**
     * ����˳��
     * 
     * @param object
     * @param id1
     * @param id2
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean exchangeRoleType(Connection con, long id1, long id2) throws Exception;

    /**
     * �����¼
     * 
     * @param con
     * @param record
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean saveRecord(Connection con, RoleRecord record) throws Exception;

    /**
     * ɾ����¼
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean deleteRole(Connection con, long id) throws Exception;

    /**
     * /** ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, RoleRecord record) throws Exception;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract RoleRecord getRecord(Connection con, long id) throws Exception;

    /**
     * ��ȡ��ɫ������Ϣ
     * 
     * @param object
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract JSONObject getRoleTypeInfp(Connection con, long id) throws Exception;

    /**
     * �õ����ݼ�
     * 
     * @param con
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<RoleRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * ����һ����¼
     * 
     * @return
     */
    public abstract RoleRecord newRecord();

    /**
     * �����ɫ����
     * 
     * @param con
     * @param pid
     * @param id
     * @param roleTypeName
     * @return
     */
    public abstract JSONObject saveRoleType(Connection con, long id, String roleTypeName) throws Exception;

    /**
     * ɾ����ɫ����
     * 
     * @param pid
     * @param id
     * @param roleTypeName
     * @return
     */
    public abstract boolean deleteRoleType(Connection con, long id) throws Exception;

    public abstract JSONObject listRoleType(Connection con) throws Exception;

}
