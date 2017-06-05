package com.estudio.intf.webclient.utils;

import java.sql.Connection;

public interface IPictureService {

    /**
     * �ϴ�ͼƬ
     * 
     * @param con
     * @param id
     * @param type
     * @param filename
     * @param filesize
     * @param width
     * @param height
     * @param content
     * @return
     * @throws Exception
     */
    public abstract boolean uploadPicture(Connection con, long id, String type, String filename, String saveFileName, String thumbnailFileName, long filesize, long width, long height, String contentType, byte[] content) throws Exception;

    /**
     * ɾ��ͼƬ
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public abstract boolean deletePicture(Connection con, String type, long id) throws Exception;

    /**
     * ��ȡͼƬ����
     * 
     * @param con
     * @param id
     * @param stream
     * @param response
     * @return
     */
    public abstract PictureProperty getContent(Connection con, String type, long id) throws Exception;

    /**
     * ���շϳ����ļ�
     * 
     * @param con
     * @param type
     * @param id
     * @throws Exception
     */
    public abstract void garbagePicture(Connection con, String type, long id) throws Exception;

    /**
     * ��������߳�
     */
    public abstract void startDaemonThread();
}
