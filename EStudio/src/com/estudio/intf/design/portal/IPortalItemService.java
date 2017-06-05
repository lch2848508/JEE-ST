package com.estudio.intf.design.portal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.define.design.portal.PortalItemRecord;
import com.estudio.intf.db.IDBCommand;

public interface IPortalItemService {

    /**
     * 发布或取消发布对象
     * 
     * @param paramInt
     * @param str2Boolean
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract JSONObject published(long id, boolean isPublished) throws Exception;

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
    public abstract boolean saveRecord(Connection con, PortalItemRecord record) throws Exception;

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
    public abstract void fillRecord(IDBCommand cmd, PortalItemRecord record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract PortalItemRecord getRecord(Connection con, long id) throws Exception;

    /**
     * 得到数据集
     * 
     * @param con
     * @param pid
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract ArrayList<PortalItemRecord> getRecords(Connection con, long pid) throws Exception;

    /**
     * 增加一条记录
     * 
     * @return
     */
    public abstract PortalItemRecord newRecord();

}
