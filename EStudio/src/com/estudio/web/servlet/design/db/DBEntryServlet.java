package com.estudio.web.servlet.design.db;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.web.servlet.BaseServlet;

public class DBEntryServlet extends BaseServlet {

    private static final long serialVersionUID = 8810688932563466314L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = request.getParameter("o");
        if (StringUtils.equals(operation, "getinfo"))
            response.getWriter().println(RuntimeContext.getDbEntryService().getEntryInfo(request.getParameter("c"), getParamLong("v", -1l)));
        else if (StringUtils.equals(operation, "getinfos"))
            response.getWriter().println(RuntimeContext.getDbEntryService().getEntryInfos(null, request.getParameter("cs")));
        else if (StringUtils.equals(operation, "save"))
            response.getWriter().println(RuntimeContext.getDbEntryService().saveDBEntry(request.getParameter("ddl_json")));
        else if (StringUtils.equals(operation, "getversion"))
            response.getWriter().println(RuntimeContext.getDbEntryService().getDBEntryVersion(request.getParameter("c")));
        else if (StringUtils.equals(operation, "delete"))
            response.getWriter().println(RuntimeContext.getDbEntryService().dropDBEntry(request.getParameter("cs").split(";")));
        else if (StringUtils.equals(operation, "foreign"))
            response.getWriter().println(RuntimeContext.getDbEntryService().createForeighKey(request.getParameter("pt"), request.getParameter("pf"), request.getParameter("ct"), request.getParameter("cf")));
        else if (StringUtils.equals(operation, "getlinks"))
            response.getWriter().println(RuntimeContext.getDbEntryService().getDBEntryLinks(null, getParamLong("v", -1l)));
        else if (StringUtils.equals(operation, "dropkeyidx"))
            response.getWriter().println(RuntimeContext.getDbEntryService().dropDBEntryKeyIndex(request.getParameter("c"), request.getParameter("i")));
        else if (StringUtils.equals(operation, "rename"))
            response.getWriter().println(RuntimeContext.getDbEntryService().renameDBEntry(request.getParameter("oc"), request.getParameter("nc"), request.getParameter("cc")));
        else if (StringUtils.equals("getallentrys", operation))
            response.getWriter().println(RuntimeContext.getDbEntryService().getAllEntrys(request.getParameter("evs"), getParamBoolean("includediagram")));
        else if (StringUtils.equals("getsupportdatatypes", operation))
            response.getWriter().println(RuntimeContext.getDbEntryService().getSupportDBFieldDataTypeJson());
        else if (StringUtils.equals("getdatabaseproperty", operation))
            response.getWriter().println(RuntimeContext.getDbEntryService().getDatabasePropertys());
        else if (StringUtils.equals("getpchinfo", operation))
            response.getWriter().println(RuntimeContext.getDbEntryService().getDBEntryPchJson());
        else if (StringUtils.equals("savepchinfo", operation))
            response.getWriter().println(RuntimeContext.getDbEntryService().saveDBEntryPchJson(getParamStr("c")));
        else if (StringUtils.equals("getlex", operation))
            response.getWriter().println(RuntimeContext.getDbEntryService().getLex(getParamStr("name")));
    }
}
