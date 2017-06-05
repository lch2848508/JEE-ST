package com.estudio.web.servlet.webclient;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import com.estudio.define.db.DBException;
import com.estudio.officeservice.ExcelUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.service.DataService4Form;
import com.estudio.web.service.DataService4Portal;
import com.estudio.web.servlet.BaseServlet;

public class ExcelServlet extends BaseServlet {
    private static final long serialVersionUID = -4944708705406699020L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("downtemplate", operation))
            response.getWriter().println(createTemplateFile());
        else if (StringUtils.equals("upload", operation))
            response.getWriter().println(updateDataSetOrPortalSQL());
        else if (StringUtils.equals("exportexcel", operation))
            response.getWriter().println(exportDataSetOrPortal2Excel());
        else if (StringUtils.equals("data2excel", operation))
            response.getWriter().println(ExcelUtils.getInstance().createExcelByData(JSONUtils.parserJSONObject(getParamStr("define"))));
        else if (StringUtils.equals("jsonTemplate2Excel", operation))
            response.getWriter().println(ExcelUtils.getInstance().createExcelByJsonDefine(JSONUtils.parserJSONObject(getParamStr("define"))));
        else if (StringUtils.equals("downTemplate3DataGridImport", operation))
            response.getWriter().println(ExcelUtils.getInstance().createExcelTemplate4DataGrid(getParamStr("header").split(",")));
        else if (StringUtils.equals("upload4Import4DataGrid", operation))
            response.getWriter().println(uploadExcel4DataGridImport());
        
    }

    private JSONObject uploadExcel4DataGridImport() throws Exception {
        JSONObject result = new JSONObject();
        final String excelFileName = getAttachmentFileName("Filedata");
        final JSONArray dataRecords = ExcelUtils.getInstance().getExcelData(excelFileName);
        result.put("records", dataRecords);
        result.put("r", true);
        return result;
    }

    /**
     * 
     * @return
     * @throws JSONException
     * @throws ParseException
     * @throws IOException
     * @throws BiffException
     * @throws DocumentException
     * @throws SQLException
     *             , DBException
     * @throws WriteException
     * @throws DBException
     */
    private JSONObject exportDataSetOrPortal2Excel() throws Exception {
        final JSONObject result = new JSONObject();
        result.put("r", false);
        final JSONObject exportDefineJSON = JSONUtils.parserJSONObject(getParamStr("exportdefine"));
        JSONArray dataArrays = null;
        if (exportDefineJSON.getLong("portalID") != -1)
            dataArrays = DataService4Portal.getInstance().exportData4ExportExcel(exportDefineJSON, exportDefineJSON.getLong("portalID"), getParams());
        else
            dataArrays = DataService4Form.getInstance().exportData4ExportExcel(exportDefineJSON, getParams());
        final String fileName = ExcelUtils.getInstance().createExcelByData(exportDefineJSON, dataArrays);
        result.put("r", true);
        result.put("path", "../excel_temp/" + fileName);
        return result;
    }

    /**
     * 根据Excel更新表单DataSet或Portal
     * 
     * @return
     * @throws JSONException
     * @throws IOException
     * @throws BiffException
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws ParseException
     * @throws DBException
     */
    private JSONObject updateDataSetOrPortalSQL() throws Exception {
        JSONObject result = new JSONObject();
        result.put("r", false);
        final JSONObject templateDefineJSON = JSONUtils.parserJSONObject(getParamStr("edit_dataformat"));
        final JSONObject editTemplateJSON = JSONUtils.parserJSONObject(getParamStr("edit_template"));
        final String excelFileName = getAttachmentFileName("Filedata");
        final JSONObject param2Col = templateDefineJSON.getJSONArray("execute").getJSONObject(0).getJSONObject("params2Col");
        Map<String, String> requestParams = getParams();
        String extAttributeParamName = requestParams.get("extAttributeParamName");
        boolean isIncludeExtAttributes = "true".equals(requestParams.get("isIncludeExtAttributes")) && !StringUtils.isEmpty(extAttributeParamName);
        final JSONObject dataJSON = ExcelUtils.getInstance().getExcelData(templateDefineJSON, editTemplateJSON, excelFileName, isIncludeExtAttributes);
        if (dataJSON.getLong("portal_id") != -1)
            result = DataService4Portal.getInstance().importExcel(dataJSON.getLong("portal_id"), dataJSON, param2Col, requestParams);
        else
            result = DataService4Form.getInstance().importExcel(dataJSON, requestParams);
        return result;
    }

    /**
     * 生成模版文件
     * 
     * @param response
     * @throws JSONException
     * @throws IOException
     * @throws WriteException
     */
    private JSONObject createTemplateFile() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        final String templateJSON = getParamStr("edit_template");
        final String path = ExcelUtils.getInstance().createExcelTemplate(templateJSON);
        if (!StringUtils.isEmpty(path)) {
            json.put("r", true);
            json.put("path", "../excel_temp/" + path);
        }
        return json;
    }

}
