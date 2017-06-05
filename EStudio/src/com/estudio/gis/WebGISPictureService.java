package com.estudio.gis;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.utils.Convert;
import com.estudio.utils.ImageUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class WebGISPictureService extends BaseServlet {

    private static final long serialVersionUID = -7593488011766003188L;

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = getParamStr("o");
        if (StringUtils.equals(operation, "upload"))
            response.getWriter().println(uploadPicture2File());
        if (StringUtils.equals(operation, "uploadFile"))
            response.getWriter().println(uploadFile());
        else if (StringUtils.equals(operation, "delete"))
            response.getWriter().println(deletePictures());
    }

    /**
     * 
     * @return
     */
    private JSONObject deletePictures() {
        JSONObject json = new JSONObject();
        JSONArray array = JSONUtils.parserJSONArray(getParamStr("files"));
        for (int i = 0; i < array.size(); i++) {
            String fileName = array.getString(i);
            fileName = StringUtils.replace(fileName, RuntimeContext.getAttachmentService().getServerURL(), RuntimeContext.getAttachmentService().getServerPath());
            FileUtils.deleteQuietly(new File(fileName));
        }
        json.put("r", true);
        return json;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    private JSONObject uploadPicture2File() throws Exception {
        JSONObject json = new JSONObject();
        long layerId = getParamLong("layerId");
        String tempUrl = "gis/" + layerId + "/pictures/" + getParamLong("id") + "/";
        String saveFileDir = RuntimeContext.getAttachmentService().getServerPath() + tempUrl;
        String fileName = getAttachmentFileName("Filedata");
        String fileExt = "";
        if (fileName.lastIndexOf(".") != -1) {
            final String tempStr = fileName;
            fileExt = tempStr.substring(tempStr.lastIndexOf("."));
        }
        String bigFileName = StringUtils.replaceEach(UUID.randomUUID().toString(), new String[] { "{", "}", "-" }, new String[] { "", "", "" }) + fileExt;
        String smallFileName = StringUtils.replaceEach(UUID.randomUUID().toString(), new String[] { "{", "}", "-" }, new String[] { "", "", "" }) + fileExt;
        fileName = saveFileDir + bigFileName;
        File saveFile = new File(fileName);
        FileUtils.forceMkdir(saveFile.getParentFile());
        Convert.bytes2File(getParamBytes("Filedata"), fileName);
        ImageUtils.getInstance().resizeImage(fileName, saveFileDir + smallFileName, 256, 256);
        json.put("url", RuntimeContext.getAttachmentService().getServerURL() + tempUrl + bigFileName);
        json.put("smallUrl", RuntimeContext.getAttachmentService().getServerURL() + tempUrl + smallFileName);
        json.put("r", true);
        return json;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    private JSONObject uploadFile() throws Exception {
        JSONObject json = new JSONObject();
        long layerId = getParamLong("layerId");
        String tempUrl = "gis/" + layerId + "/attachments/" + getParamLong("id") + "/";
        String saveFileDir = RuntimeContext.getAttachmentService().getServerPath() + tempUrl;
        String fileName = getAttachmentFileName("Filedata");
        String fileExt = "";
        if (fileName.lastIndexOf(".") != -1) {
            final String tempStr = fileName;
            fileExt = tempStr.substring(tempStr.lastIndexOf("."));
        }
        String bigFileName = StringUtils.replaceEach(UUID.randomUUID().toString(), new String[] { "{", "}", "-" }, new String[] { "", "", "" }) + fileExt;
        fileName = saveFileDir + bigFileName;
        File saveFile = new File(fileName);
        FileUtils.forceMkdir(saveFile.getParentFile());
        Convert.bytes2File(getParamBytes("Filedata"), fileName);
        json.put("url", RuntimeContext.getAttachmentService().getServerURL() + tempUrl + bigFileName);
        json.put("r", true);
        return json;
    }

}
