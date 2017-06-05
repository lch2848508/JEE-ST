package com.estudio.intf.design.objects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ObjectFormsRecord;
import com.estudio.intf.db.IDBCommand;

public interface IObjectFormService {

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
     * /** 填充记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, ObjectFormsRecord record) throws Exception;

    /**
     * 获取表单控件及数据源列表 WorkFlow
     * 
     * @param con
     * @param id
     * @param version
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract JSONObject getFormControls(Connection con, long id, long version) throws Exception;

    /**
     * 得到Form对象版本信息
     * 
     * @param con
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract long getFormVersion(Connection con, long id) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ObjectFormsRecord getRecord(Connection con, long id) throws Exception;

    /**
     * 得到对象列表
     * 
     * @param id
     * @param version
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    public abstract JSONObject getRecordInfo(long id, long version) throws Exception, DBException;

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
    public abstract ArrayList<ObjectFormsRecord> getRecords(Connection con, long pid) throws Exception;

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
     * 增加一条记录
     * 
     * @return
     */
    public abstract ObjectFormsRecord newRecord();

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
    public abstract boolean saveRecord(Connection con, ObjectFormsRecord record) throws Exception;

    /**
     * 保存表单内容 内容用于工作流系统使用
     * 
     * @param con
     * @param id
     * @param ds4wf
     * @param cs4wf
     * @return
     * @throws DBException
     * @throws Exception
     */
    public abstract JSONObject saveRecord4WorkFlow(Connection con, long id, byte[] ds4wf, byte[] cs4wf) throws Exception, DBException;

    /**
     * 列表表单数据源CacheName列表
     * 
     * @param con
     * @return
     * @throws Exception
     */
    public abstract JSONObject getDataSourceCacheKeyList(Connection con) throws Exception;

    /**
     * 删除表单CacheNameList
     * 
     * @param con
     * @param key
     * @return
     * @throws Exception
     */
    public abstract JSONObject deleteDataSourceCacheKey(Connection con, String key) throws Exception;

    /**
     * 保存表单CacheKey
     * 
     * @param con
     * @param key
     * @return
     * @throws Exception
     */
    public abstract JSONObject saveDataSourceCacheKey(Connection con, String key) throws Exception;

    /**
     * 获取数据源模版列表
     * 
     * @param con
     * @return
     * @throws Exception
     */
    public abstract JSONObject getDataSourceTemplateList(Connection con) throws Exception;

    /**
     * 参数数据源模版
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public abstract JSONObject deleteDataSourceTemplate(Connection con, String category, String caption) throws Exception;

    /**
     * 保存数据源模版
     * 
     * @param con
     * @param category
     * @param caption
     * @param content
     * @return
     * @throws Exception
     */
    public abstract JSONObject saveDataSourceTemplate(Connection con, String category, String caption, String content) throws Exception;

    /**
     * 获取数据源模版内容
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public abstract JSONObject getDataSourceTemplate(Connection con, String category, String caption) throws Exception;
}
