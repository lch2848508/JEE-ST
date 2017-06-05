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
     * ɾ����¼
     * 
     * @param con
     * @param id
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract boolean deleteRecord(Connection con, long id) throws Exception;

    /**
     * /** ����¼
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract void fillRecord(IDBCommand cmd, ObjectReportRecord record) throws Exception;

    /**
     * �õ�һ����¼
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
     * ����һ����¼
     * 
     * @return
     */
    public abstract ObjectReportRecord newRecord();

    /**
     * �����¼
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
     * ����ģ��
     * 
     * @param con
     * @param id
     * @param json
     */
    public abstract void downloadTemplate(Connection con, long id, JSONObject json) throws SQLException;;

    /**
     * ����ģ��
     * 
     * @param con
     * @param id
     * @param filename
     * @param templateContent
     * @throws SQLException 
     */
    public abstract void saveTemplate(Connection con, long id, String filename, String templateContent) throws SQLException;

}
