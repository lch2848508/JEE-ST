package com.estudio.web.servlet.design.usermanager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.intf.design.user.IUserManagerService;
import com.estudio.web.servlet.BaseServlet;

public class UserProviderServlet extends BaseServlet {

    private static final long serialVersionUID = -3550582881380724658L;
    private final IUserManagerService service = RuntimeContext.getUserManagerService();

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String operation = request.getParameter("o");
        final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(request.getSession());
        if (StringUtils.equals(operation, "get")) {
            final long pid = getParamLong("pid", Long.MIN_VALUE);
            final String type = request.getParameter("type");
            if (StringUtils.equals("d_r", type))
                response.getWriter().println(service.getDepartmentsAndRoles(pid, loginInfo.getId()));
            else if (StringUtils.equals("u_b_d", type))
                response.getWriter().println(service.getUsersByDepartment(pid, loginInfo.getId()));
            else if (StringUtils.equals("u_b_r", type))
                response.getWriter().println(service.getUsersByRole(pid, loginInfo.getId()));
            else if (StringUtils.equals("d_u", type))
                response.getWriter().println(service.getDepartmentsAndUsers(pid, loginInfo.getId()));
            else if (StringUtils.equals("dirSystemRoles", type))
                response.getWriter().println(service.getSystemRoles());
        }
    }
}
