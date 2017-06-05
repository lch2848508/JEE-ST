package com.estudio.web.servlet.config;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.web.servlet.BaseServlet;

public class WebParams extends BaseServlet {

    private static final long serialVersionUID = 614265636098953124L;

    @SuppressWarnings("unchecked")
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals(operation, "save")) {
            final AbstractParamService paramService = WebParamService.getInstance();
            final Enumeration<String> es = request.getParameterNames();
            while (es.hasMoreElements()) {
                final String paramName = es.nextElement();
                if (!StringUtils.equals("o", paramName))
                    paramService.setParamValue(paramName, request.getParameter(paramName));
            }
            paramService.save();
            final JSONObject json = new JSONObject();
            json.put("r", true);
            response.getWriter().println(json);
        }
    }

}
