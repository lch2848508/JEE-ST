package com.estudio.web.servlet.design.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.ResultInfo;
import com.estudio.web.servlet.BaseServlet;

public class SQLServlet extends BaseServlet {

    private static final long serialVersionUID = 9079433387706453103L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String operation = getParamStr("o");
        if (StringUtils.equals("getfields", operation)) {
            if (!StringUtils.isEmpty(getParamStr("sql"))) {
                String[] tables = null;
                if (!StringUtils.isEmpty(getParamStr("tables")))
                    tables = getParamStr("tables").split(";");
                response.getWriter().println(RuntimeContext.getSqlParserService().parser(getParamStr("sql"), tables, getParamStr("paramTypes")));
            } else response.getWriter().println(new ResultInfo(false, "SQL语句为空或不是正确的SELECT语句!").toJSON());
        } else if (StringUtils.equals("merge", operation))
            response.getWriter().println(RuntimeContext.getSqlParserService().merge(getParamStr("insert"), getParamStr("update")));
    }

}
