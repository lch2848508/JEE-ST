package com.estudio.web.servlet.webclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.web.servlet.BaseServlet;
import com.estudio.web.servlet.FormValuesContain.FormFile;

public class AttachmentServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = 591699241024676343L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("upload", operation))
            response.getWriter().println(uploadFiles(request));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listFiles());
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteFile(getParamLongs("ids")));
        else if (StringUtils.equals("download", operation))
            response.sendRedirect(getDownloadURL(request.getRequestURI()));
        else if (StringUtils.equals("getHtml4Flex", operation))
            response.getWriter().println(getHtml4Flex(request));
    }

    /**
     * 
     * @param request
     * @return
     * @throws Exception
     */
    private String getHtml4Flex(HttpServletRequest request) throws Exception {
        String p_id = getParamStr("p_id");
        String type = getParamStr("type");
        String result = "";
        ArrayList<JSONObject> records = RuntimeContext.getAttachmentService().listFiles(null, type, p_id);
        for (JSONObject record : records) {
            String caption = record.getString("caption");
            if (StringUtils.equals("", caption))
                result = caption;
            else
                result += " " + caption;
        }
        return result;
    }

    /**
     * 产生下载URL
     * 
     * @param callURL
     * @return
     * @throws SQLException
     *             , DBException
     */
    private String getDownloadURL(final String callURL) throws Exception {
        return RuntimeContext.getAttachmentService().getDownloadURL(getParamLong("id"));
    }

    /**
     * 删除文件
     * 
     * @param id
     * @return
     * @throws Exception
     */
    private JSONObject deleteFile(final long[] ids) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            for (final long id : ids)
                RuntimeContext.getAttachmentService().deleteFile(con, id);
            json.put("records", RuntimeContext.getAttachmentService().listFiles(con, getParamStr("type"), getParamStr("p_id")));
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 列表数据
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private JSONObject listFiles() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("records", RuntimeContext.getAttachmentService().listFiles(null, getParamStr("type"), getParamStr("p_id")));
        json.put("r", true);
        return json;
    }

    /**
     * 上传文件
     * 
     * @param request
     * @return
     * @throws Exception
     */
    private JSONObject uploadFiles(final HttpServletRequest request) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            if (localFormValuesContain.get() != null) {
                con = DBHELPER.getConnection();
                final String type = getParamStr("type");
                final String p_id = getParamStr("p_id");
                final String descript = getParamStr("descript");
                final boolean saveToDB = getParamInt("savetodb") == 1;
                final Iterator<FormFile> i = localFormValuesContain.get().getFileIterator();
                while (i.hasNext()) {
                    final FormFile f = i.next();
                    final byte[] bs = saveToDB ? getParamBytes(f.getName()) : null;
                    final long content_id = DBHELPER.getUniqueID(con);
                    final String caption = FilenameUtils.getBaseName(f.getFileName());
                    final String fileName = f.getFileName();
                    final String willSaveFile = RuntimeContext.getAttachmentService().getSaveUniqueFileName(content_id, fileName);
                    RuntimeContext.getAttachmentService().uploadFile(con, content_id, type, p_id, caption, descript, FilenameUtils.getName(willSaveFile), f.getSize(), loginInfo.get().getId(), bs, f.getContentType());
                    RuntimeContext.getAttachmentService().registerCopyFileTask(con, content_id, f.getSaveFileName(), willSaveFile);
                }
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.isClearTempFileAfterPost = false;
    }

}
