package com.estudio.intf.webclient.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

public interface IAttachmentService {

    /**
     * ɾ���ļ�
     * 
     * @param con
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract boolean deleteFile(Connection con, long id) throws Exception;

    /**
     * ɾ�������ļ�
     * 
     * @param id
     * @param fileName
     * @param subDirectory
     * @return
     * @throws Exception
     */
    public abstract boolean deletePhyhicsFile(long id, String fileName, String subDirectory) throws Exception;

    /**
     * ����������URL
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract String getDownloadURL(long id) throws Exception;

    /**
     * ȡ���ļ���
     * 
     * @param id
     * @param fileName
     * @return
     * @throws Exception
     */
    public abstract String getSaveUniqueFileName(long id, String fileName) throws Exception;

    public abstract String getSaveUniqueFileName(long id, String fileName, String subDirectory) throws Exception;

    public abstract String getServerPath();

    /**
     * ����ID��ȡ��������ַ
     * 
     * @param id
     * @return
     */
    public abstract String getServerPath(long id);

    public abstract String getServerPath(long id, String subDirectory) throws Exception;

    public abstract String getServerURL();

    /**
     * ����ID��ȡ������URL
     * 
     * @param id
     * @return
     */
    public abstract String getServerURL(long id);

    public abstract String getServerURL(long id, String subDirectory) throws Exception;

    /**
     * �б��ļ�
     * 
     * @param con
     * @param type
     * @param p_id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract ArrayList<JSONObject> listFiles(Connection con, String type, String p_id) throws Exception;

    public abstract void setServerPath(String serverPath);

    public abstract void setServerURL(String serverURL);

    /**
     * �����ػ��߳�
     */
    public abstract void startDaemonThread();

    /**
     * �ϴ��ļ�
     * 
     * @param type
     * @param p_id
     * @param caption
     * @param filename
     * @param filesize
     * @param content
     * @return
     * @throws Exception
     */
    public abstract boolean uploadFile(Connection con, long content_id, String type, String p_id, String caption, String descript, String filename, long filesize, long user_id, byte[] content, String contentType) throws Exception;

    /**
     * @return
     */
    public abstract String getServerType();

    /**
     * @param serverType
     */
    public abstract void setServerType(String serverType);

    /**
     * @return
     */
    public abstract String getServerUserName();

    /**
     * @param serverUserName
     */
    public abstract void setServerUserName(String serverUserName);

    /**
     * @return
     */
    public abstract String getServerUserPassword();

    /**
     * @param serverUserPassword
     */
    public abstract void setServerUserPassword(String serverUserPassword);

    /**
     * @return
     */
    public abstract int getServerPort();

    /**
     * @param serverPort
     */
    public abstract void setServerPort(int serverPort);

    /**
     * �ƶ��ļ�
     * 
     * @param saveFileName
     * @param willSaveFile
     * @throws Exception
     */
    public abstract void moveFile(String saveFileName, String willSaveFile) throws Exception;

    String getServerIp();

    void setServerIp(String serverIp);

    /**
     * ע���ļ��ƶ�����
     * 
     * @param con
     * @param content_id
     * @param saveFileName
     * @param willSaveFile
     * @throws Exception
     */
    public abstract void registerCopyFileTask(Connection con, long content_id, String saveFileName, String willSaveFile) throws Exception;

}
