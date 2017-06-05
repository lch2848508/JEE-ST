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
     * 保存模型视图
     * 
     * @param id
     * @param entrys
     * @return
     */
    public abstract JSONObject saveDiagramDBEntrys(long id, String entrys);

    /**
     * 得到Diagram的DBEntry列表信息
     * 
     * @param v
     * @param version
     * 
     * @param str2Int
     * @return
     */
    public abstract JSONObject getDiagramDBEntrys(long id, long version, long linkVersion);

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
    public abstract boolean saveRecord(Connection con, DBDiagramRecord record) throws Exception;

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
    public abstract void fillRecord(IDBCommand cmd, DBDiagramRecord record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract DBDiagramRecord getRecord(Connection con, long id) throws Exception;

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
    public abstract ArrayList<DBDiagramRecord> getRecords(Connection con) throws Exception;

    /**
     * 得到列表 需要处理线程安全
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ArrayList<JSONObject> getDiagramsJSON(Connection con) throws Exception;

    /**
     * 增加一条记录
     * 
     * @return
     */
    public abstract DBDiagramRecord newRecord();

    /**
     * 得到Tree的Version
     * 
     * @return
     */
    public abstract JSONObject getTreeVersion();

    /**
     * 批量删除Diagram对象
     * 
     * @param is
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean deleteRecords(Connection con, long[] ids) throws Exception;

}
