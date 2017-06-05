package com.estudio.intf.webclient.utils;

import java.sql.Connection;

public interface IPictureService {

    /**
     * 上传图片
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
     * 删除图片
     * 
     * @param con
     * @param id
     * @return
     * @throws Exception
     */
    public abstract boolean deletePicture(Connection con, String type, long id) throws Exception;

    /**
     * 获取图片内容
     * 
     * @param con
     * @param id
     * @param stream
     * @param response
     * @return
     */
    public abstract PictureProperty getContent(Connection con, String type, long id) throws Exception;

    /**
     * 回收废除的文件
     * 
     * @param con
     * @param type
     * @param id
     * @throws Exception
     */
    public abstract void garbagePicture(Connection con, String type, long id) throws Exception;

    /**
     * 启动监控线程
     */
    public abstract void startDaemonThread();
}
