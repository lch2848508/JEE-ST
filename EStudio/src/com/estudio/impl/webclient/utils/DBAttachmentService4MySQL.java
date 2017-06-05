package com.estudio.impl.webclient.utils;

public class DBAttachmentService4MySQL extends DBAttachmentService {

    private static final DBAttachmentService4MySQL INSTANCE = new DBAttachmentService4MySQL();

    public static DBAttachmentService4MySQL getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getDeleteAttachmentContentSQL() {
        return "delete from sys_attachment_content where id=?";
    }

    @Override
    protected String getDeleteFileSQL() {
        return "{call proc_attachment_del(?)}";
    }

    @Override
    protected String getDownloadURLSQL() {
        return "select id,filename from sys_attachment_content where id=(select content_id from sys_attachment_reg where id=?)";
    }

    @Override
    protected String getInsertSQL() {
        return "{call proc_attachment_upload(:content_id, :type, :pid, :caption, :filename, :filesize, :contenttype, :content, :userid,:descript)}";
    }

    @Override
    protected String getListFilesSQL() {
        return "select a.id, b.filename, a.caption, b.filesize, b.fileext,fun_datetime2str(a.regdate) regdate,b.id c_id,b.descript from sys_attachment_content b, sys_attachment_reg a where a.content_id = b.id and a.p_type=? and a.p_id=? order by a.id";
    }

    @Override
    protected String getShouldDeleteFileListSQL() {
        return "select id,filename from sys_attachment_content where refnum=0";
    }

}
