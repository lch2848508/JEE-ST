package com.estudio.impl.webclient.utils;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import net.minidev.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.webclient.utils.IAttachmentService;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.ThreadUtils;

public abstract class DBAttachmentService implements IAttachmentService {

    private static final int NUMBER_4_UPLOAD_THREAD = 10;

    private static FileSystemManager fileSystemManager = null;

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getDeleteFileSQL();

    protected abstract String getDeleteAttachmentContentSQL();

    protected abstract String getShouldDeleteFileListSQL();

    protected abstract String getListFilesSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDownloadURLSQL();

    /**
     * 文件移动类
     * 
     * @author shenghongl
     * 
     */
    private class MoveFileItem {
        String sourceFile;
        String targetFile;

        public MoveFileItem(String sourceFile, String targetFile) {
            super();
            this.sourceFile = sourceFile;
            this.targetFile = targetFile;
        }
    }

    // 柱塞队列用于移动文件
    private LinkedBlockingQueue<MoveFileItem> moveFileItems = new LinkedBlockingQueue<MoveFileItem>();

    private String serverPath;
    private String serverURL;
    private String serverType;
    private String serverIp;
    private String serverUserName;
    private String serverUserPassword;
    private int serverPort;

    protected IDBCommand insertCMD = null;

    {
        try {
            fileSystemManager = VFS.getManager();
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    protected DBAttachmentService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#deleteFile(java.sql
     * .Connection, long)
     */
    @Override
    public boolean deleteFile(final Connection con, final long id) throws Exception {
        boolean result = false;
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            tempCon = con;
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getDeleteFileSQL(), true);
            stmt.setParam(1, id);
            stmt.execute();
            result = true;
        } finally {
            DBHELPER.closeCommand(stmt);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#getDownloadURL(long)
     */
    @Override
    public String getDownloadURL(final long id) throws Exception {
        String url = "";
        Connection tempCon = null;
        IDBCommand stmt = null;
        try {
            tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getDownloadURLSQL());
            stmt.setParam(1, id);
            stmt.executeQuery();
            if (stmt.next())
                url = getServerURL(stmt.getLong("ID")) + stmt.getString(2);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(tempCon);
        }
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#getSaveUniqueFileName
     * (long, java.lang.String)
     */
    @Override
    public String getSaveUniqueFileName(final long id, final String fileName) throws Exception {
        final String serverPath = getServerPath(id);
        // final String basefilename = FilenameUtils.getBaseName(fileName);
        final String basefilename = StringUtils.replaceEach(UUID.randomUUID().toString(), new String[] { "{", "}", "-" }, new String[] { "", "", "" });
        final String ext = FilenameUtils.getExtension(fileName);
        String result = serverPath + basefilename + "." + ext;
        // long index = 1;
        // while
        // (fileSystemManager.resolveFile(getResolveFilePath(result)).exists())
        // result = serverPath + basefilename + "[" + (index++) + "]." + ext;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.service.attachment.IAttachmentService#getServerPath()
     */
    @Override
    public String getServerPath() {
        return serverPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#getServerPath(long)
     */
    @Override
    public String getServerPath(final long id) {
        return serverPath + String.format("%08d", (Math.abs(id) / 10000)) + "/";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.service.attachment.IAttachmentService#getServerURL()
     */
    @Override
    public String getServerURL() {
        return serverURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.service.attachment.IAttachmentService#getServerURL(long)
     */
    @Override
    public String getServerURL(final long id) {
        return serverURL + String.format("%08d", Math.abs(id) / 10000) + "/";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#listFiles(java.sql.
     * Connection, java.lang.String, java.lang.String)
     */
    @Override
    public ArrayList<JSONObject> listFiles(final Connection con, final String type, final String p_id) throws Exception {
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>();

        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            tempCon = con;
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getListFilesSQL(), true);
            stmt.setParam(1, type);
            stmt.setParam(2, p_id);
            stmt.executeQuery();
            final NumberFormat nf = new DecimalFormat(",##0");
            while (stmt.next()) {
                final String caption = stmt.getString("CAPTION");
                final String filename = stmt.getString("FILENAME");
                final String regdate = stmt.getString("REGDATE");
                final String fileext = stmt.getString("FILEEXT");
                final String id = stmt.getString("ID");
                final String descript = stmt.getString("DESCRIPT");
                final JSONObject json = new JSONObject();
                json.put("id", id);
                json.put("caption", caption + "." + fileext);
                json.put("filesize", nf.format(stmt.getInt("FILESIZE")));
                json.put("regdate", regdate);
                json.put("fileext", fileext);
                json.put("url", getServerURL(stmt.getInt("C_ID")) + filename);
                json.put("descript", descript);
                result.add(json);
            }
        } finally {
            DBHELPER.closeCommand(stmt);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#setServerPath(java.
     * lang.String)
     */
    @Override
    public void setServerPath(final String value) {
        // serverPath = value;
        // if () serverPath += "/";
        serverPath = StringUtils.replace((!value.endsWith("\\") && !value.endsWith("/")) ? value + "/" : value, "\\", "/");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#setServerURL(java.lang
     * .String)
     */
    @Override
    public void setServerURL(final String value) {
        // serverURL = value;
        // if () serverURL += "/";
        serverURL = StringUtils.replace((!value.endsWith("\\") && !value.endsWith("/")) ? value + "/" : value, "\\", "/");
    }

    @Override
    public String getServerType() {
        return serverType;
    }

    @Override
    public void setServerType(final String serverType) {
        this.serverType = serverType;
    }

    @Override
    public String getServerUserName() {
        return serverUserName;
    }

    @Override
    public void setServerUserName(final String serverUserName) {
        this.serverUserName = serverUserName;
    }

    @Override
    public String getServerUserPassword() {
        return serverUserPassword;
    }

    @Override
    public void setServerUserPassword(final String serverUserPassword) {
        this.serverUserPassword = serverUserPassword;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public void setServerPort(final int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public String getServerIp() {
        return serverIp;
    }

    @Override
    public void setServerIp(final String serverIp) {
        this.serverIp = serverIp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#startDaemonThread()
     */
    @Override
    public void startDaemonThread() {
        startDeleteMonitorThread();
        startUploadMonitorThread();
    }

    /**
     * 上传文件线程
     */
    private void startUploadMonitorThread() {
        for (int i = 0; i < NUMBER_4_UPLOAD_THREAD; i++) {
            final Thread uploadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            MoveFileItem moveFileItem = moveFileItems.take();
                            moveFile(moveFileItem.sourceFile, moveFileItem.targetFile);
                            ThreadUtils.sleep(20);
                        } catch (final Exception e) {
                            ExceptionUtils.printExceptionTrace(e);
                        }
                    }
                }
            });
            uploadThread.start();
        }
    }

    /**
     * 删除文件线程
     */
    private void startDeleteMonitorThread() {
        final Thread deleteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Connection con = null;
                    IDBCommand stmt = null;
                    IDBCommand stmtRef = null;
                    try {
                        con = DBHELPER.getConnection();
                        stmt = DBHELPER.getCommand(con, getShouldDeleteFileListSQL(), true);
                        stmtRef = DBHELPER.getCommand(con, getDeleteAttachmentContentSQL(), true);
                        stmt.executeQuery();
                        while (stmt.next()) {
                            final long id = stmt.getLong(1);
                            final String fileName = stmt.getString(2);
                            FileObject fileObject = null;
                            try {
                                fileObject = fileSystemManager.resolveFile(getResolveFilePath(getServerPath(id) + fileName));
                                if ((fileObject != null) && fileObject.exists())
                                    fileObject.delete();
                            } catch (final Exception e) {
                                ExceptionUtils.printExceptionTrace(e);
                            } finally {
                                closeFileObject(fileObject);
                            }
                            stmtRef.setParam(1, id);
                            stmtRef.execute();
                        }
                    } catch (final Exception e) {
                        ExceptionUtils.printExceptionTrace(e);
                    } finally {
                        DBHELPER.closeCommand(stmtRef);
                        DBHELPER.closeCommand(stmt);
                        DBHELPER.closeConnection(con);
                    }
                    ThreadUtils.sleepMinute(5);
                }

            }
        });
        deleteThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.attachment.IAttachmentService#uploadFile(java.sql
     * .Connection, long, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, long, long, byte[], java.lang.String)
     */
    @Override
    public boolean uploadFile(final Connection con, final long content_id, final String type, final String p_id, final String caption, final String descript, final String filename, final long filesize, final long user_id, final byte[] content, final String contentType) throws Exception {
        boolean result = false;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            tempCon = con;
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = insertCMD.clone(tempCon);
            cmd.setParam("content_id", content_id);
            cmd.setParam("type", type);
            cmd.setParam("pid", p_id);
            cmd.setParam("caption", caption);
            cmd.setParam("filename", filename);
            cmd.setParam("filesize", filesize);
            cmd.setParam("contenttype", contentType);
            cmd.setParam("content", content);
            cmd.setParam("userid", user_id);
            cmd.setParam("descript", descript);
            cmd.execute();
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 移动文件
     * 
     * @throws Exception
     */
    @Override
    public void moveFile(final String saveFileName, final String willSaveFile) throws Exception {
        FileObject targetFileObject = null;
        FileObject sourceFileObject = null;
        FileObject parentFolder = null;
        try {
            sourceFileObject = fileSystemManager.resolveFile(saveFileName);
            targetFileObject = fileSystemManager.resolveFile(getResolveFilePath(willSaveFile));
            parentFolder = targetFileObject.getParent();
            createParentFolder(parentFolder);
            targetFileObject.copyFrom(sourceFileObject, Selectors.SELECT_ALL);
            sourceFileObject.delete();
        } finally {
            closeFileObject(sourceFileObject);
            closeFileObject(targetFileObject);
            closeFileObject(parentFolder);
        }
    }

    private void closeFileObject(FileObject obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 递归创建目录树
     * 
     * @param parentFolder
     * @throws Exception
     */
    private void createParentFolder(final FileObject value) throws Exception {
        FileObject parentFolder = value;
        final List<FileObject> files = new ArrayList<FileObject>();
        while ((parentFolder != null) && !parentFolder.exists()) {
            files.add(parentFolder);
            parentFolder = parentFolder.getParent();
        }
        for (int i = files.size() - 1; i >= 0; i--) {
            files.get(i).createFolder();
            files.get(i).close();
        }
    }

    /**
     * 获取路径URL
     * 
     * @param path
     * @return
     * @throws Exception
     */
    private String getResolveFilePath(final String path) throws Exception {
        String result = "";
        if (StringUtils.equalsIgnoreCase(serverType, "File") || StringUtils.isEmpty(serverType))
            result = path;
        else if (StringUtils.equalsIgnoreCase(serverType, "Ftp")) {
            result = "ftp://" + serverUserName + ":" + serverUserPassword + "@" + serverIp + ":" + serverPort + StringUtils.replace(path, "//", "/");
            // result = new String(result.getBytes("UTF-8"), "iso-8859-1");
        }
        return result;
    }

    @Override
    public String getSaveUniqueFileName(final long id, final String fileName, final String subDirectory) throws Exception {
        final String serverPath = getServerPath(id, subDirectory);
        String ext = FilenameUtils.getExtension(fileName);
        String basefilename = StringUtils.replaceEach(UUID.randomUUID().toString(), new String[] { "{", "}", "-" }, new String[] { "", "", "" });
        String result = serverPath + basefilename + "." + ext;
        return result;
    }

    @Override
    public String getServerPath(final long id, final String subDirectory) throws Exception {
        if (!StringUtils.isEmpty(subDirectory))
            return serverPath + subDirectory + "/" + String.format("%08d", (Math.abs(id) / 10000)) + "/";
        return getServerPath(id);
    }

    @Override
    public String getServerURL(final long id, final String subDirectory) throws Exception {
        if (!StringUtils.isEmpty(subDirectory))
            return serverURL + subDirectory + "/" + String.format("%08d", Math.abs(id) / 10000) + "/";
        return getServerURL(id);
    }

    @Override
    public boolean deletePhyhicsFile(final long id, final String fileName, final String subDirectory) throws Exception {
        FileObject fileObject = null;
        try {
            fileObject = fileSystemManager.resolveFile(getResolveFilePath(getServerPath(id, subDirectory) + fileName));
            if ((fileObject != null) && fileObject.exists())
                fileObject.delete();
        } finally {
            closeFileObject(fileObject);
        }
        return true;
    }

    @Override
    public void registerCopyFileTask(Connection con, long content_id, String sourceFileName, String targetFileName) throws Exception {
        this.moveFileItems.put(new MoveFileItem(sourceFileName, targetFileName));
    }
}
