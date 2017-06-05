package com.estudio.intf.design.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.estudio.define.design.user.UserInfoRecord;
import com.estudio.intf.db.IDBCommand;

public interface IUserInfoService {

    /**
     * �õ���ͬ�Ĺ�����
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<Long> getCommonRoles(Connection con, String ids) throws Exception;

    /**
     * �õ��û���ɫID�б�
     * 
     * @param con
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<Long> getUserRoleIDS(Connection con, long id) throws Exception;

    /**
     * ��ע���û��鵽��ɫ��
     * 
     * @param con
     * @param uids
     * @param rids
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean unregisterUsers2Roles(Connection con, String[] uids, String[] rids) throws Exception;

    /**
     * ע���û��鵽��ɫ��
     * 
     * @param con
     * @param uids
     * @param rids
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean registerUsers2Roles(Connection con, String[] uids, String[] rids) throws Exception;

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
     * �����¼
     * 
     * @param con
     * @param record
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean saveRecord(Connection con, UserInfoRecord record) throws Exception;

    /**
     * ɾ����¼
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean deleteRecord(Connection con, long id) throws Exception;

    /**
     * ɾ������û�
     * 
     * @param object
     * @param ids
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean deleteRecords(Connection con, String[] ids) throws Exception;

    /**
     * /** ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, UserInfoRecord record) throws Exception;

    /**
     * �õ�һ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract UserInfoRecord getRecord(Connection con, long id) throws Exception;

    /**
     * ���ݽ�ɫ�õ����ݼ�
     * 
     * @param object
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<UserInfoRecord> getRecordsByRole(Connection con, long rid, long uid) throws Exception;

    /**
     * �õ����ݼ�
     * 
     * @param con
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<UserInfoRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * ����һ����¼
     * 
     * @return
     */
    public abstract UserInfoRecord newRecord();

    /**
     * �жϵ�¼�û����Ƿ��Ѿ�����
     * 
     * @param id
     * @param loginName
     * @return
     * @throws Exception
     */
    public abstract boolean isLoginNameExists(String id, String loginName) throws Exception;

}
