package com.estudio.web.servlet.webclient;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.utils.Convert;
import com.estudio.utils.ImageUtils;
import com.estudio.web.service.DataService4AttachmentEx;
import com.estudio.web.servlet.BaseServlet;

public class AttachmentExServlet extends BaseServlet {
    private static final long serialVersionUID = 911725680289574816L;

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = getParamStr("o");
        if (StringUtils.equals(operation, "get"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().getAttachments(getParamStr("recordId")));
        else if (StringUtils.equals(operation, "md"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().createFolder(getParamStr("caption"), getParamStr("recordId"), getParamLong("pid"), this.getLoginInfo().getId()));
        else if (StringUtils.equals(operation, "rd"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().deleteFiles(getParamStr("id")));
        else if (StringUtils.equals(operation, "rename"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().rename(getParamLong("id"), getParamStr("caption"), getParamStr("descript")));
        else if (StringUtils.equals(operation, "upload"))
            response.getWriter().println(uploadFile());
        else if (StringUtils.equals(operation, "getProperty"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().getProperty(getParamStr("recordId")));
        else if (StringUtils.equals(operation, "saveProperty"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().saveProperty(getParamStr("recordId"), getParamStr("property")));
        else if (StringUtils.equals(operation, "getPicture"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().getPicture(getParamStr("recordId")));
        else if (StringUtils.equals(operation, "uploadPicture"))
            response.getWriter().println(uploadPicture());
        else if (StringUtils.equals(operation, "deletePicture"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().deletePicture(getParamStr("id")));
        else if (StringUtils.equals(operation, "exchangePicturePosition"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().exchangePicturePosition(getParamLongs("id")));
//        else if (StringUtils.equals(operation, "getGeometry"))
//            response.getWriter().println(DataService4AttachmentEx.getInstance().getGeometry(getParamStr("recordId")));
//        else if (StringUtils.equals(operation, "saveGeometry"))
//            response.getWriter().println(DataService4AttachmentEx.getInstance().saveGeometry(getParamStr("recordId"), getParamStr("geometry")));
//        else if (StringUtils.equals(operation, "getGeometryFromResource"))
//            response.getWriter().println(DataService4AttachmentEx.getInstance().getGeometryFromResource(getParamStr("layerId"), getParamStr("keyField"), getParamStr("keyValue"), getParamInt("dataType") == 1));
        
        else if (StringUtils.equals(operation, "getGeometry"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().getGeometry(getParamStr("recordId"),getParamStr("recordIdNum"),getParamStr("tablename")));
        else if (StringUtils.equals(operation, "saveGeometry"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().saveGeometry(getParamStr("recordId"), getParamStr("geometry")));
        else if (StringUtils.equals(operation, "getGeometryFromResource"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().getGeometryFromResource(getParamStr("layerId"), getParamStr("keyField"), getParamStr("keyValue"), getParamInt("dataType") == 1));
        else if (StringUtils.equals(operation, "getGeometry4AllZH"))
            response.getWriter().println(DataService4AttachmentEx.getInstance().getGeometry4AllZH(getParamStr("tablename")));
        else if (StringUtils.equals(operation, "getConditionByrecordPrefix"))
        	response.getWriter().println(DataService4AttachmentEx.getInstance().getConditionByrecordPrefix(getParamStr("recordPrefix")));
        else if(StringUtils.equals(operation, "getGeometry4Condtion")){
        	response.getWriter().println(DataService4AttachmentEx.getInstance().getGeometry4Condtion(getParamStr("tablename"),getParamStr("filterGeoByNF"),getParamStr("filterGeoByDWMC"),getParamStr("filterGeoByLX")));
        }else if(StringUtils.equals(operation, "getProtectInfo")){
        	response.getWriter().println(DataService4AttachmentEx.getInstance().getProtectInfo(getParamStr("pro_id"),getParamStr("pro_tablename")));
        }
    }

    /**
     * 上传图片
     * 
     * @return
     * @throws Exception
     */
    private JSONObject uploadPicture() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String tempUrl = "attachment_pictures/" + uuid.substring(0, 2) + "/";
        String saveFileDir = RuntimeContext.getAttachmentService().getServerPath() + tempUrl;
        String fileName = getAttachmentFileName("Filedata");
        String fileExt = "";
        if (fileName.lastIndexOf(".") != -1) {
            final String tempStr = fileName;
            fileExt = tempStr.substring(tempStr.lastIndexOf("."));
        }
        String uuidFileName = StringUtils.replaceEach(uuid, new String[] { "{", "}", "-" }, new String[] { "", "", "" });
        String bigFileName = uuidFileName + fileExt;
        String thumFileName = uuidFileName + ".thumb" + fileExt;
        fileName = saveFileDir + bigFileName;
        File saveFile = new File(fileName);
        FileUtils.forceMkdir(saveFile.getParentFile());
        byte[] fileBytes = getParamBytes("Filedata");
        Convert.bytes2File(fileBytes, fileName);
        ImageUtils.getInstance().resizeImage(fileName, saveFileDir + thumFileName, 256, 256);
        return DataService4AttachmentEx.getInstance().savePicture(getParamStr("recordId"), tempUrl + bigFileName, tempUrl + thumFileName, getParamStr("descript"), getParamStr("href"), getLoginInfo().getId(), getParamStr("category"));
    }

    /**
     * 上传文件
     * 
     * @return
     * @throws Exception
     */
    private JSONObject uploadFile() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String tempUrl = "attachment_ex/" + uuid.substring(0, 2) + "/";
        String saveFileDir = RuntimeContext.getAttachmentService().getServerPath() + tempUrl;
        String fileName = getAttachmentFileName("Filedata");
        String caption = FilenameUtils.getName(fileName);
        String fileExt = "";
        if (fileName.lastIndexOf(".") != -1) {
            final String tempStr = fileName;
            fileExt = tempStr.substring(tempStr.lastIndexOf("."));
        }
        String bigFileName = StringUtils.replaceEach(uuid, new String[] { "{", "}", "-" }, new String[] { "", "", "" }) + fileExt;
        fileName = saveFileDir + bigFileName;
        File saveFile = new File(fileName);
        FileUtils.forceMkdir(saveFile.getParentFile());
        byte[] fileBytes = getParamBytes("Filedata");
        Convert.bytes2File(fileBytes, fileName);
        String url = tempUrl + bigFileName;
        return DataService4AttachmentEx.getInstance().createFile(caption, fileBytes.length, getParamStr("descript"), getParamStr("recordId"), getParamLong("pid"), getLoginInfo().getId(), url, url);
    }
}
