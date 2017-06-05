package com.estudio.web.gis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.gis.oracle.WebGISQueryService4Oracle;
import com.estudio.intf.db.IDBHelper;
import com.estudio.web.servlet.BaseServlet;

public class GeometryService extends BaseServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 5104274611815744972L;
    private static IDBHelper dBHelper = RuntimeContext.getDbHelper();

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = getParamStr("o");
        if (StringUtils.equals(operation, "getGeometry")) {
            long layerId = getParamLong("layerId");
            String keyValue = getParamStr("keyValue");
            String keyFieldName = getParamStr("keyField");
            String sql = "select q_uid from spatial_fs_" + layerId + " where " + keyFieldName + "='" + keyValue + "'";
            //response.getWriter().println(WebGISQueryService4Oracle.getInstance().getFeatureProperty(dBHelper.executeScalarLong(sql, null)));
        }
    }

}
