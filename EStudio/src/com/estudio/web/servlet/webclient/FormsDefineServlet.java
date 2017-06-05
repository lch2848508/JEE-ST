package com.estudio.web.servlet.webclient;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.form.DesignDataSource;
import com.estudio.define.webclient.form.FormDefine;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class FormsDefineServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    /**
     * 
     */
    private static final long serialVersionUID = 2231549206438996027L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getdefine", operation))
            response.getWriter().println(procGetDefine());
        else if (StringUtils.equals("getformsize", operation))
            response.getWriter().println(getFormSize(request));
    }

    /**
     * 获取表单尺寸
     * 
     * @param request
     * @return
     * @throws Exception
     */
    private JSONObject getFormSize(final HttpServletRequest request) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = RuntimeContext.getDbHelper().getConnection();
            final int[] gridFormSize = RuntimeContext.getFormDefineService().getFormMaxSize(request.getParameter("formids").split(","), con);
            json.put("r", true);
            json.put("w", gridFormSize[0]);
            json.put("h", gridFormSize[1]);
        } finally {
            RuntimeContext.getDbHelper().closeConnection(con);
        }
        return json;

    }

    /**
     * 获取表单定义
     * 
     * @return
     * @throws JSONException
     * @throws IOException
     */
    private JSONObject procGetDefine() throws IOException {
        final boolean isOnlyData = getParamInt("onlydata") == 1;
        final JSONArray formsArray = isOnlyData ? null : new JSONArray();
        final JSONArray datasetsArray = isOnlyData ? null : new JSONArray();
        final JSONObject datasetData = new JSONObject();
        final StringBuilder sb = new StringBuilder();

        final JSONObject formDefine = new JSONObject();
        formDefine.put("forms", formsArray);
        formDefine.put("datasets", datasetsArray);

        final String ids[] = getParamStr("formids").split(",");
        final Map<String, String> httpRequestParams = getParams();
        Connection con = null;

        try {
            con = DBHELPER.getConnection();
            for (final String id : ids) {
                final FormDefine form = RuntimeContext.getFormDefineService().getFormDefine(Convert.try2Long(id, -1), con);
                if (!isOnlyData) {
                    formsArray.add(form.getFormJson());
                    for (int j = 0; j < form.getDataSetCount(); j++) {
                        final DesignDataSource dataset = form.getDataSet(j);
                        if (dataset.isFormDataSet())
                            datasetsArray.add(dataset.getDataSetJson());
                    }
                    sb.append(form.getJscript());
                    sb.append("\n");
                }
                form.getData(con, datasetData, httpRequestParams, getSession());
            }
            formDefine.put("JS", sb.toString());
            formDefine.put("data", datasetData);
            formDefine.put("r", true);

        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            JSONUtils.except2JSON(formDefine, e);
        } finally {
            DBHELPER.closeConnection(con);
        }

        return formDefine;
    }

}
