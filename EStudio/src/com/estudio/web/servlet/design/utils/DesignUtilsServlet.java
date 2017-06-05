package com.estudio.web.servlet.design.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.impl.design.utils.DBDesignUtilsService;
import com.estudio.utils.Convert;
import com.estudio.web.servlet.BaseServlet;

public class DesignUtilsServlet extends BaseServlet {
    private static final long serialVersionUID = -1551894980986911497L;

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = getParamStr("o");
        if (StringUtils.equals(operation, "export"))
            response.getWriter().println(DBDesignUtilsService.instance.exportDesign(getParamLongs("ids"), getParamInt("type")));
        else if (StringUtils.equals(operation, "import")) {
            String str = Convert.bytes2Str(getParamBytes("File"));
            response.getWriter().println(DBDesignUtilsService.instance.importDesign(str, getParamInt("type")));
        }
    }

}
