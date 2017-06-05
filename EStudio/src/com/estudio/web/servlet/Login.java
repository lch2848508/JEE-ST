package com.estudio.web.servlet;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.ResultInfo;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public class Login extends BaseServlet {
    private static final long ID_ADMINISTRATOR = -1;

    private final static long serialVersionUID = 1788661008840556493L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String operation = request.getParameter("o");
        if (StringUtils.equals(operation, "login")) {
            final ClientLoginInfo clientLoginInfo = RuntimeContext.getClientLoginService().getLoginInfo(getSession());
            if ((clientLoginInfo != null) && !clientLoginInfo.getLoginName().equalsIgnoreCase(getParamStr("u"))) {
                final JSONObject json = new JSONObject();
                json.put("r", false);
                json.put("msg", "系统已经检测到您当前使用的浏览器已经登录系统.\n这样会导致严重的问题（第一个登录用户会自动变为第二个用户）.\n请您注销第一个用户后或者重新打开一个新的WEB浏览器登录!!!");
                json.put("extMsg", "系统已经检测到您当前使用的浏览器已经登录系统.\n这样会导致严重的问题（第一个登录用户会自动变为第二个用户）.\n请您注销第一个用户后或者重新打开一个新的WEB浏览器登录!!!");
                response.getWriter().println(json);
            } else
                loginByUserAndPassword(request, response);
        } else if (StringUtils.equals(operation, "keepsession"))
            keepClientSession(request, response);
        else if (StringUtils.equals(operation, "changepwd"))
            changePassword(request, response);
        else if (StringUtils.equals(operation, "logoff"))
            logoff(request, response);
        else if (StringUtils.equals("loginbyother", operation))
            loginByOther(request, response);
        else if (StringUtils.equals(operation, "loginbyuuid"))
            loginByUUID(request, response);
        else if (StringUtils.equals(operation, "regisger"))
            try {
                registerTempUser(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * 注册新用户
     * 
     * @param request
     * @param response
     * @throws Exception 
     */
    private void registerTempUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String code = request.getParameter("code");
        if (VerifyUtils.isVerifyOK("register", request.getSession(), code)) {
            String sessionId = request.getSession().getId();
            String userName = request.getParameter("user");
            int sex = Integer.parseInt(request.getParameter("sex"));
            String cardId = request.getParameter("cardid");
            String mobile = request.getParameter("mobile");
            String address = request.getParameter("address");
            String email = request.getParameter("email");
            response.getWriter().println(RuntimeContext.getClientLoginService().registerTempUserInfo(sessionId, userName, sex, cardId, mobile, address, email));
        } else {
            JSONObject json = new JSONObject();
            json.put("r", false);
            json.put("msg", "检验码不正确。");
            response.getWriter().println(json);
        }
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    private void loginByUUID(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        {
            final String uuid = request.getParameter("uuid");
            final String code = request.getParameter("code");
            final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().loginByOther(uuid, code, request.getSession().getId());
            if (loginInfo != null)
                RuntimeContext.getClientLoginService().registerLoginInfo(request.getSession(), loginInfo);
            response.sendRedirect("../preview.jsp");
        }
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    private void loginByOther(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        {
            final ClientLoginInfo info = RuntimeContext.getClientLoginService().getLoginInfo(request.getSession());
            if (info != null) {
                final LoginByOtherHelperRecord record = LoginByOtherHelperService.getInstance().newRecord();
                record.setUuid(UUID.randomUUID().toString());
                record.setRndcode(RandomUtils.nextInt(100, 500));
                record.setLoginUserid(info.getId());
                try {
                    LoginByOtherHelperService.getInstance().saveRecord(null, record);
                    final JSONObject json = new JSONObject();
                    json.put("uuid", record.getUuid());
                    json.put("code", record.rndcode);
                    json.put("r", true);
                    response.getWriter().println(json);
                } catch (final Exception e) {
                    ExceptionUtils.loggerException(e);
                    response.getWriter().println(JSONUtils.except2JSON(e));
                }
            } else
                response.getWriter().println(new ResultInfo(false, "用户没有登录!"));
        }
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    private void logoff(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        {
            RuntimeContext.getClientLoginService().logoff(request.getSession());
            response.getWriter().println(new ResultInfo().toJSON());
        }
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    private void changePassword(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final ClientLoginInfo info = RuntimeContext.getClientLoginService().getLoginInfo(request.getSession());
        if (info != null) {
            final String oldpwd = request.getParameter("edit_oldpassword");
            final String newpwd = request.getParameter("edit_newpassword");
            response.getWriter().println(RuntimeContext.getClientLoginService().changePassword(info, oldpwd, newpwd).toJSON());
        } else
            response.getWriter().println(new ResultInfo(false, "用户没有登录!").toJSON());
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    private void keepClientSession(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        {
            final ClientLoginInfo info = RuntimeContext.getClientLoginService().getLoginInfo(request.getSession());
            final JSONObject json = new JSONObject();
            json.put("r", info != null);
            response.getWriter().println(json);
        }
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    private void loginByUserAndPassword(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String user = request.getParameter("u");
        final String pwd = request.getParameter("p");
        final String code = request.getParameter("c");

        final String userType = request.getParameter("userType");
        final String userArea = request.getParameter("userArea");
        final String orgId = request.getParameter("orgId");
        final String auto=request.getParameter("autoLogin");
       
        final boolean isSkipCode = getParamInt("skip_code", 0) == 1;
        if(auto.equals("false"))
        {
        	if (!isSkipCode && !VerifyUtils.isVerifyOK("login", request.getSession(), code))
                response.getWriter().println(new ResultInfo(false, "校验码错误!").toJSON());
            else {
                final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().login(user, pwd, userType, userArea, orgId, request.getSession().getId());
                if (loginInfo != null) {
                    RuntimeContext.getClientLoginService().registerLoginInfo(request.getSession(), loginInfo);
                    final JSONObject json = new ResultInfo().toJSON();
                    json.put("realname", loginInfo.getRealName());
                    json.put("loginname", loginInfo.getLoginName());
                    json.put("id", loginInfo.getId());
                    if (loginInfo.getId() <= -1) {
                        json.put("role_admin", true);
                        json.put("department_admin", true);
                        json.put("administrator", true);
                        json.put("db_admin", true);
                        json.put("designer", true);
                        json.put("document_manager", true);
                        json.put("is_gis_role", loginInfo.isGisRole());
                        json.put("is_mis_role", loginInfo.isMisRole());
                    } else {
                        json.put("role_admin", loginInfo.isRole(ID_ADMINISTRATOR));
                        json.put("department_admin", loginInfo.isRole(ID_ADMINISTRATOR));
                        json.put("administrator", loginInfo.isRole(ID_ADMINISTRATOR));
                        json.put("db_admin", loginInfo.isRole(ID_ADMINISTRATOR));
                        json.put("designer", loginInfo.isRole(ID_ADMINISTRATOR));
                        json.put("document_manager", loginInfo.isRole(ID_ADMINISTRATOR));
                        json.put("is_gis_role", loginInfo.isGisRole());
                        json.put("is_mis_role", loginInfo.isMisRole());
                    }
                    response.getWriter().println(json);
                } else
                    response.getWriter().println(new ResultInfo(false, "用户登录失败!").toJSON());
            }
        }
        else if(auto.equals("true")){
            final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().login(user, pwd, userType, userArea, orgId, request.getSession().getId());
            if (loginInfo != null) {
                RuntimeContext.getClientLoginService().registerLoginInfo(request.getSession(), loginInfo);
                final JSONObject json = new ResultInfo().toJSON();
                json.put("realname", loginInfo.getRealName());
                json.put("loginname", loginInfo.getLoginName());
                json.put("id", loginInfo.getId());
                if (loginInfo.getId() <= -1) {
                    json.put("role_admin", true);
                    json.put("department_admin", true);
                    json.put("administrator", true);
                    json.put("db_admin", true);
                    json.put("designer", true);
                    json.put("document_manager", true);
                    json.put("is_gis_role", loginInfo.isGisRole());
                    json.put("is_mis_role", loginInfo.isMisRole());
                } else {
                    json.put("role_admin", loginInfo.isRole(ID_ADMINISTRATOR));
                    json.put("department_admin", loginInfo.isRole(ID_ADMINISTRATOR));
                    json.put("administrator", loginInfo.isRole(ID_ADMINISTRATOR));
                    json.put("db_admin", loginInfo.isRole(ID_ADMINISTRATOR));
                    json.put("designer", loginInfo.isRole(ID_ADMINISTRATOR));
                    json.put("document_manager", loginInfo.isRole(ID_ADMINISTRATOR));
                    json.put("is_gis_role", loginInfo.isGisRole());
                    json.put("is_mis_role", loginInfo.isMisRole());
                }
                response.getWriter().println(json);
                
            } else
                response.getWriter().println(new ResultInfo(false, "用户登录失败!").toJSON());
           
        }
    }

}
