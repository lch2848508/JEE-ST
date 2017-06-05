package com.estudio.web.servlet.design.objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.design.objects.IObjectQueryService;
import com.estudio.web.servlet.BaseServlet;

public class ObjectQueryServlet extends BaseServlet {

    private static final long serialVersionUID = 8694741800539848386L;

    private static IObjectQueryService service = RuntimeContext.getObjectQueryService();

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("save", operation))
            response.getWriter().println(service.save(getParamLong("id"), getParamStr("content")));
        else if (StringUtils.equals("get", operation))
            response.getWriter().println(service.get(getParamLong("id")));

    }
}
