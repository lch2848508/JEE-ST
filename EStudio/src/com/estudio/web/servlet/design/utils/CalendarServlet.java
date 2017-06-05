package com.estudio.web.servlet.design.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.web.servlet.BaseServlet;
import com.estudio.workflow.utils.WFCalendarService;

public class CalendarServlet extends BaseServlet {

    private static final long serialVersionUID = 3014641850208876932L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals(operation, "save"))
            response.getWriter().println(WFCalendarService.getInstance().saveSetting(getParamInt("year"), getParamStr("days"), getParamStr("times")));
        else if (StringUtils.equals(operation, "get"))
            response.getWriter().println(WFCalendarService.getInstance().getSetting(getParamInt("year")));
    }

}
