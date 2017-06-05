package com.estudio.impl.webclient.utils;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.webclient.utils.IPictureService;
import com.estudio.intf.webclient.utils.PictureProperty;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.ThreadUtils;

public abstract class DBPictureService implements IPictureService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private IDBCommand cmdIns = null;
    private IDBCommand cmdDel = null;
    private IDBCommand cmdSel = null;
    private IDBCommand cmdGarbage = null;

    /**
     * ¹¹Ôìº¯Êý
     */
    protected DBPictureService() {
        super();
        try {
            initCmd();
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    private void initCmd() throws Exception {
        cmdIns = DBHELPER.getCommand(null, getInsertSQL());
        cmdDel = DBHELPER.getCommand(null, getDeleteSQL());
        cmdSel = DBHELPER.getCommand(null, getSelectSQL());
        cmdGarbage = DBHELPER.getCommand(null, getGarbageSQL());
    }

    protected abstract String getGarbageSQL();

    /**
     * @return
     */
    protected abstract String getSelectSQL();

    /**
     * @return
     */
    protected abstract String getDeleteSQL();

    /**
     * @return
     */
    protected abstract String getInsertSQL();

    @Override
    public boolean uploadPicture(final Connection con, final long id, final String type, final String filename, final String saveFileName, final String thumbnailFileName, final long filesize, final long width, final long height, final String contentType, final byte[] content) throws Exception {
        boolean result = false;
        IDBCommand cmd = null;
        try {
            deletePicture(con, type, id);
            cmd = cmdIns.clone(con);
            cmd.setParam("id", id);
            cmd.setParam("picture_type", type);
            cmd.setParam("file_name", filename);
            cmd.setParam("content_type", contentType);
            cmd.setParam("file_size", filesize);
            cmd.setParam("pic_width", width);
            cmd.setParam("pic_height", height);
            cmd.setParam("content", content);
            cmd.setParam("save_filename", saveFileName);
            cmd.setParam("thumbnail_filename", thumbnailFileName);
            cmd.execute();
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    @Override
    public boolean deletePicture(final Connection con, final String type, final long id) throws Exception {
        boolean result = false;
        IDBCommand cmd = null;
        try {
            garbagePicture(con, type, id);
            cmd = cmdDel.clone(con);
            cmd.setParam("type", type);
            cmd.setParam("id", id);
            cmd.execute();
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    @Override
    public PictureProperty getContent(final Connection con, final String type, final long id) throws Exception {
        PictureProperty result = null;
        IDBCommand cmd = null;
        try {
            cmd = cmdSel.clone(con);
            cmd.setParam(1, type);
            cmd.setParam(2, id);
            cmd.executeQuery();
            if (cmd.next()) {
                result = new PictureProperty();
                result.setFileName(cmd.getString(1));
                result.setContentType(cmd.getString(2));
                result.setWidth(cmd.getInt(3));
                result.setHeight(cmd.getInt(4));
                result.setSaveFileName(cmd.getString(5));
                result.setThumbnailFileName(cmd.getString(6));
            }

        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    @Override
    public void garbagePicture(final Connection con, final String type, final long id) throws Exception {
        final PictureProperty property = getContent(con, type, id);
        if (property != null) {
            IDBCommand cmd = null;
            try {
                cmd = cmdGarbage.clone(con);
                cmd.setParam("id", DBHELPER.getUniqueID(con));
                cmd.setParam("key_id", id);
                cmd.setParam("save_filename", property.getSaveFileName());
                cmd.execute();
                if (!StringUtils.isEmpty(property.getThumbnailFileName())) {
                    cmd.setParam("key_id", id);
                    cmd.setParam("save_filename", property.getThumbnailFileName());
                    cmd.execute();
                }
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        }
    }

    @Override
    public void startDaemonThread() {
        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    Connection con = null;
                    IDBCommand stmt = null;
                    IDBCommand delStmt = null;
                    try {
                        con = DBHELPER.getConnection();
                        stmt = DBHELPER.getCommand(con, "select id,key_id,save_filename from SYS_ATTACHMENT_GARBAGE t order by id", true);
                        delStmt = DBHELPER.getCommand(con, "delete from SYS_ATTACHMENT_GARBAGE where id=?", true);
                        stmt.executeQuery();
                        while (stmt.next()) {
                            final long id = stmt.getLong(1);
                            final long keyId = stmt.getLong(2);
                            final String fileName = stmt.getString(3);
                            try {
                                RuntimeContext.getAttachmentService().deletePhyhicsFile(keyId, fileName, "PICTURE");
                            } catch (final Exception e) {
                                ExceptionUtils.printExceptionTrace(e);
                            }
                            delStmt.setParam(1, id);
                            delStmt.execute();
                        }
                    } catch (final Exception e) {
                        ExceptionUtils.printExceptionTrace(e);
                    } finally {
                        DBHELPER.closeCommand(delStmt);
                        DBHELPER.closeCommand(stmt);
                        DBHELPER.closeConnection(con);
                    }
                    ThreadUtils.sleepMinute(5);
                }

            }
        });
        thread.start();
    }
}
