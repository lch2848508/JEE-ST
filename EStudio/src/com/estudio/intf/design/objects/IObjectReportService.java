package com.estudio.intf.design.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ObjectReportRecord;
import com.estudio.intf.db.IDBCommand;

public interface IObjectReportService {

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
    public abstract void fillRecord(IDBCommand cmd, ObjectReportRecord record) throws Exception;

    /**
     * 得到一条记录
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract ObjectReportRecord getRecord(Connection con, long id) throws Exception;

    /**
     * 
     * @param id
     * @param servletOutputStream
     * @return
     * @throws SQLException
     *             , DBException
     * @throws IOException
     * @throws DBException
     */
    public abstract void getTemplate(Connection con, long id, OutputStream outputStream) throws Exception, IOException, DBException;

    /**
     * 增加一条记录
     * 
     * @return
     */
    public abstract ObjectReportRecord newRecord();

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
    public abstract boolean saveRecord(Connection con, ObjectReportRecord record) throws Exception;

    /**
     * 下载模板
     * 
     * @param con
     * @param id
     * @param json
     */
    public abstract void downloadTemplate(Connection con, long id, JSONObject json) throws SQLException;;

    /**
     * 保存模板
     * 
     * @param con
     * @param id
     * @param filename
     * @param templateContent
     * @throws SQLException 
     */
    public abstract void saveTemplate(Connection con, long id, String filename, String templateContent) throws SQLException;

}
