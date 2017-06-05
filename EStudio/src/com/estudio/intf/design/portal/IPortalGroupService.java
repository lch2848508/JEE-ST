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
     * 发布或取消发布对象
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
     * 取得栏目组及栏目项信息
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getGroupsAndItems() throws Exception, DBException;

    /**
     * 列表Portal Group及Group下的Item
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getGroupsAndItems(boolean includeRight, boolean includeRoles) throws Exception, DBException;

    /**
     * 移动节点
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
     * 交换顺序
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
     * 保存记录
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
     * 删除记录
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     * @throws DBException
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
    public abstract void fillRecord(IDBCommand cmd, PortalGroupRecord record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract PortalGroupRecord getRecord(Connection con, long id) throws Exception;

    /**
     * 得到数据集
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
     * 增加一条记录
     * 
     * @return
     */
    public abstract PortalGroupRecord newRecord();

    public abstract Map<String, String> getDesignEvnParams();

}
