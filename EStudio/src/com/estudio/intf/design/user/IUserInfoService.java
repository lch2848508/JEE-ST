package com.estudio.intf.design.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.estudio.define.design.user.UserInfoRecord;
import com.estudio.intf.db.IDBCommand;

public interface IUserInfoService {

    /**
     * 得到共同的工作组
     * 
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<Long> getCommonRoles(Connection con, String ids) throws Exception;

    /**
     * 得到用户角色ID列表
     * 
     * @param con
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<Long> getUserRoleIDS(Connection con, long id) throws Exception;

    /**
     * 反注册用户组到角色组
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
     * 注册用户组到角色组
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
     * 移动节点
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
     * 交换顺序
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
     * 保存记录
     * 
     * @param con
     * @param record
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean saveRecord(Connection con, UserInfoRecord record) throws Exception;

    /**
     * 删除记录
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean deleteRecord(Connection con, long id) throws Exception;

    /**
     * 删除多个用户
     * 
     * @param object
     * @param ids
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean deleteRecords(Connection con, String[] ids) throws Exception;

    /**
     * /** 填充记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, UserInfoRecord record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract UserInfoRecord getRecord(Connection con, long id) throws Exception;

    /**
     * 根据角色得到数据集
     * 
     * @param object
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<UserInfoRecord> getRecordsByRole(Connection con, long rid, long uid) throws Exception;

    /**
     * 得到数据集
     * 
     * @param con
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<UserInfoRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * 增加一条记录
     * 
     * @return
     */
    public abstract UserInfoRecord newRecord();

    /**
     * 判断登录用户名是否已经存在
     * 
     * @param id
     * @param loginName
     * @return
     * @throws Exception
     */
    public abstract boolean isLoginNameExists(String id, String loginName) throws Exception;

}
