package com.estudio.web.servlet.design.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.design.portal.IPortalRightService;
import com.estudio.utils.Convert;
import com.estudio.web.servlet.BaseServlet;

public class PortalRightServlet extends BaseServlet {

    private static final long serialVersionUID = -1675429552446285962L;
    private final IPortalRightService service = RuntimeContext.getPortalRightService();

    /**
     * ¥¶¿ÌHTTP«Î«Û
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getRight", operation))
            response.getWriter().println(service.getPortalRight(getParamLong("id", -1l), Convert.str2Boolean(getParamStr("isgroup"))));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(service.savePortalRight(getParamLong("id", -1l), getParamStr("rights")));
        else if (StringUtils.equals("saverights", operation))
            response.getWriter().println(service.savePortalRight(getParamLong("portal_id", -1l), getParamStr("roleids"), getParamStr("rights")));

    }

}
