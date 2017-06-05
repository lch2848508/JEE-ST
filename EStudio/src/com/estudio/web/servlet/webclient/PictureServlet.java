package com.estudio.web.servlet.webclient;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.webclient.utils.PictureProperty;
import com.estudio.utils.Convert;
import com.estudio.utils.ImageUtils;
import com.estudio.web.servlet.BaseServlet;
import com.estudio.web.servlet.FormValuesContain.FormFile;

public class PictureServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1053522471739479996L;
    private static String picSubDirectory = "PICTURE";

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals(operation, "upload"))
            response.getWriter().println(uploadPicture());
        else if (StringUtils.equals("download", operation))
            downloadPicture(response, false);
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deletePicture());
        else if (StringUtils.equals("savetolocal", operation))
            downloadPicture(response, true);
        else if (StringUtils.equals("download4photo", operation))
            downloadPicture4Photo(response, false);
    }

    /**
     * 下载头像
     * 
     * @param response
     * @throws Exception
     */
    private void downloadPicture4Photo(final HttpServletResponse response, final boolean isLarge) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        // final long id = getParamLong("id");
        long id = GlobalContext.getLoginInfo().getId();
        final String type = getParamStr("type");
        Connection con = null;
        String url = "../images/nopic.jpg";
        try {
            con = DBHELPER.getConnection();
            final PictureProperty prop = RuntimeContext.getPictureService().getContent(con, type, id);
            if (prop != null) {
                final String saveFileName = prop.getSaveFileName();
                final String thumbnailFileName = prop.getThumbnailFileName();
                url = RuntimeContext.getAttachmentService().getServerURL(id, picSubDirectory);
                if (!isLarge && (getParamInt("showthumbnail") == 1) && !StringUtils.isEmpty(thumbnailFileName))
                    url += thumbnailFileName;
                else
                    url += saveFileName;
            }
            json.put("r", true);
            json.put("url", url);
        } finally {
            DBHELPER.closeConnection(con);
        }
        response.getWriter().println(json);
    }

    /**
     * 删除图片
     * 
     * @return
     * @throws Exception
     */
    private JSONObject deletePicture() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            RuntimeContext.getPictureService().deletePicture(con, "system_" + getParamStr("type"), getParamLong("id"));
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 下载图片
     * 
     * @param response
     * @throws Exception
     */
    private void downloadPicture(final HttpServletResponse response, final boolean isLarge) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        final long id = getParamLong("id");
        final String type = "system_" + getParamStr("type");
        Connection con = null;
        String url = "../images/nopic.jpg";
        try {
            con = DBHELPER.getConnection();
            final PictureProperty prop = RuntimeContext.getPictureService().getContent(con, type, id);
            if (prop != null) {
                final String saveFileName = prop.getSaveFileName();
                final String thumbnailFileName = prop.getThumbnailFileName();
                url = RuntimeContext.getAttachmentService().getServerURL(id, picSubDirectory);
                if (!isLarge && (getParamInt("showthumbnail") == 1) && !StringUtils.isEmpty(thumbnailFileName))
                    url += thumbnailFileName;
                else
                    url += saveFileName;
            }
            json.put("r", true);
            json.put("url", url);
        } finally {
            DBHELPER.closeConnection(con);
        }
        response.getWriter().println(json);
    }

    /**
     * 上传图片
     * 
     * @return
     * @throws Exception
     */
    private JSONObject uploadPicture() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            if (localFormValuesContain.get() != null) {
                con = DBHELPER.getConnection();
                final String type = "system_" + getParamStr("type");
                final long id = getParamLong("id");
                final long picWidth = getParamInt("w");
                final long picHeight = getParamInt("h");
                final boolean saveToDB = getParamInt("savetodb") == 1;
                final boolean resizeAble = getParamInt("resize") == 1;
                final boolean isCamera = getParamInt("forceresize", 0) == 1;
                final int resizeWidth = getParamInt("maxwidth");
                final int resizeHeight = getParamInt("maxheight");
                final int thumbnailWidth = getParamInt("thumbnailwidth");
                final int thumbnailHeight = getParamInt("thumbnailheight");
                final boolean isCreateThumbnail = getParamInt("createthumbnail") == 1;
                final Iterator<FormFile> i = localFormValuesContain.get().getFileIterator();
                while (i.hasNext()) {
                    final FormFile f = i.next();
                    final String fileName = f.getFileName();
                    final String willSaveFile = RuntimeContext.getAttachmentService().getSaveUniqueFileName(id, fileName, picSubDirectory);

                    String thumbnailFileName = "";
                    String thumbnailWillSaveFileName = "";
                    // 调整图像大小
                    if (resizeAble && !isCamera && ((picWidth > resizeWidth) || (picHeight > resizeHeight)))
                        ImageUtils.getInstance().resizeImage(f.getSaveFileName(), f.getSaveFileName(), resizeWidth, resizeHeight);

                    // 生成缩略图
                    if (isCreateThumbnail && (isCamera || (picWidth > thumbnailWidth) || (picHeight > thumbnailHeight))) {
                        thumbnailFileName = FilenameUtils.getFullPath(f.getSaveFileName()) + FilenameUtils.getBaseName(f.getSaveFileName()) + ".thumbnail." + FilenameUtils.getExtension(f.getSaveFileName());
                        ImageUtils.getInstance().resizeImage(f.getSaveFileName(), thumbnailFileName, thumbnailWidth, thumbnailHeight);
                        thumbnailWillSaveFileName = RuntimeContext.getAttachmentService().getSaveUniqueFileName(id, FilenameUtils.getName(thumbnailFileName), picSubDirectory);
                    }

                    // 向文件服务器传送文件
                    RuntimeContext.getAttachmentService().moveFile(f.getSaveFileName(), willSaveFile);
                    if (!StringUtils.isEmpty(thumbnailFileName))
                        RuntimeContext.getAttachmentService().moveFile(thumbnailFileName, thumbnailWillSaveFileName);
                    RuntimeContext.getPictureService().uploadPicture(con, id, type, fileName, FilenameUtils.getName(willSaveFile), FilenameUtils.getName(thumbnailWillSaveFileName), f.getSize(), picWidth, picHeight, f.getContentType(), saveToDB ? Convert.file2Bytes(f.getSaveFileName()) : null);

                    FileUtils.deleteQuietly(new File(f.getSaveFileName()));
                    if (!StringUtils.isEmpty(thumbnailFileName))
                        FileUtils.deleteQuietly(new File(thumbnailFileName));
                }
                final PictureProperty prop = RuntimeContext.getPictureService().getContent(con, type, id);
                String url = RuntimeContext.getAttachmentService().getServerURL(id, picSubDirectory);
                if (prop != null) {
                    final String saveFileName = prop.getSaveFileName();
                    final String thumbnailFileName = prop.getThumbnailFileName();
                    if ((getParamInt("showthumbnail") == 1) && !StringUtils.isEmpty(thumbnailFileName))
                        url += thumbnailFileName;
                    else
                        url += saveFileName;
                }
                json.put("url", url);
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

}
