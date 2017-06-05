package com.estudio.intf.design.utils;

import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.define.utils.CodeAssistRecord;
import com.estudio.intf.db.IDBCommand;

public interface ICodeAssistService {

    /**
     * 得到代码助手
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract JSONObject getCodeAssist(long version) throws Exception;

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
    public abstract boolean saveRecord(Connection con, CodeAssistRecord record) throws Exception;

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
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, CodeAssistRecord record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract CodeAssistRecord getRecord(Connection con, long id) throws Exception;

    /**
     * 增加一条记录
     * 
     * @return
     */
    public abstract CodeAssistRecord newRecord();

}
