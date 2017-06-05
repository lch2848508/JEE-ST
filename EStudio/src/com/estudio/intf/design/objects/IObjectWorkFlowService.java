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
     * 获取版本号
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
     * @throws JSONException
     * @throws DBException
     * @throws Exception
     */
    public abstract boolean saveRecord(Connection con, WorkFlowDesignInfo record) throws Exception;

    /**
     * 删除记录
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
     * /** 填充记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, WorkFlowDesignInfo record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract WorkFlowDesignInfo getRecord(Connection con, long id) throws Exception;

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
    public abstract ArrayList<WorkFlowDesignInfo> getRecords(Connection con, long pid) throws Exception;

    /**
     * 增加一条记录
     * 
     * @return
     */
    public abstract WorkFlowDesignInfo newRecord();

    /**
     * 复制内容
     * 
     * @param fromId
     * @param newName
     * @return
     */
    public abstract JSONObject copyWorkFlowDesignInfo(long fromId, String newName) throws Exception;

    /**
     * 保存内容
     * 
     * @param id
     * @param name
     * @param content
     * @return
     */
    public abstract JSONObject saveWorkFlowItemDesignInfo(long id, String name, String content) throws Exception;

    /**
     * 列表工作流项
     * 
     * @return
     * @throws Exception
     */
    public abstract JSONObject listWorkFlowItemDesignInfos() throws Exception;

    /**
     * 删除工作流列表项
     * 
     * @return
     * @throws Exception
     */
    public abstract JSONObject deleteWorkFlowDesignInfos(long id) throws Exception;

    /**
     * 交换工作流列表项顺序
     * 
     * @param id1
     * @param id2
     * @return
     * @throws Exception
     */
    public abstract JSONObject exchangeWorkFlowDesignInfo(long id1, long id2) throws Exception;

    /**
     * 获取信息
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public abstract JSONObject getWorkFlowDesignInfo(long id, Connection con) throws Exception;

}
