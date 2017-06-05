package com.estudio.intf.design.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.estudio.define.design.user.DepartmentRecord;
import com.estudio.intf.db.IDBCommand;

public interface IDepartmentService {

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
    public abstract boolean saveRecord(Connection con, DepartmentRecord record) throws Exception;

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
     * /** 填充记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, DepartmentRecord record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract DepartmentRecord getRecord(Connection con, long id) throws Exception;

    /**
     * 得到数据集
     * 
     * @param con
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<DepartmentRecord> getRecords(Connection con, long pid, long uid) throws Exception;

    /**
     * 增加一条记录
     * 
     * @return
     */
    public abstract DepartmentRecord newRecord();

}
