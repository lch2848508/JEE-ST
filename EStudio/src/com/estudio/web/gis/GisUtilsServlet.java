package com.estudio.web.gis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.gis.oracle.WebGISResourceService4Oracle;
import com.estudio.web.servlet.BaseServlet;

public class GisUtilsServlet extends BaseServlet {

    private static final long serialVersionUID = -5119381750018087639L;

    /**
     * Constructor of the object.
     */
    public GisUtilsServlet() {
        super();
    }

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = getParamStr("o");
        if (StringUtils.equals("registerService", operation))
            response.getWriter().println(WebGISResourceService4Oracle.getInstance().registerMapServer(getParamLong("id")));
        else if (StringUtils.equals("registerResource", operation))
            response.getWriter().println(WebGISResourceService4Oracle.getInstance().registerMapLayerCollectionItem(getParams()));
        else if (StringUtils.equals("moveResourceTo", operation))
            response.getWriter().println(WebGISResourceService4Oracle.getInstance().MoveResourceTo(getParams()));
        else if(StringUtils.equals("registerYeWuBiao",operation)){
        	response.getWriter().println(WebGISResourceService4Oracle.getInstance().registerYeWuBiao(getParams()));
        }

    }

}
