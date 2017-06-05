package com.estudio.web.servlet.webclient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.web.service.DataService4Diagram;
import com.estudio.web.servlet.BaseServlet;

public class DiagramServlet extends BaseServlet {
    private static final long serialVersionUID = 6722010889476074500L;

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = getParamStr("o");
        if (StringUtils.equals(operation, "get")) {
            response.getWriter().println(DataService4Diagram.getInstance().getDiagram(getParamStr("diagramName")));
        }
    }

}
