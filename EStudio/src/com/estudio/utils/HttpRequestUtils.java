package com.estudio.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.sercure.ClientLoginInfo;

public class HttpRequestUtils {
    /**
     * 得到Request请求的参数字典集合
     * 
     * @param request
     * @return
     */
    public static Map<String, String> getRequestParams(final HttpServletRequest request) {
        final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(request.getSession());
        final Map<String, String> result = new HashMap<String, String>();
        if (loginInfo != null) {
            result.put("USER_ID", Long.toString(loginInfo.getId()));
            result.put("DEPARTMENT_ID", Long.toString(loginInfo.getDepartmentId()));
        }
        result.put("-1", "-1");
        result.put("%", "%");
        result.put("MINDATE", "1900-01-01");
        result.put("MAXDATE", "2300-12-31");
        result.put("sessionId", request.getSession().getId());
        final Enumeration<?> ei = request.getParameterNames();
        while (ei.hasMoreElements()) {
            final String paramName = (String) ei.nextElement();
            final String value = request.getParameter(paramName);
            if (!StringUtils.equals("null", value))
                result.put(paramName, value);
        }
        return result;

    }

    /**
     * 将参数转化为JSON对象
     * 
     * @param request
     * @return
     * @throws JSONException
     */
    public static JSONObject requestParams2JSON(final HttpServletRequest request) {
        final JSONObject json = new JSONObject();
        final Enumeration<?> ei = request.getParameterNames();
        while (ei.hasMoreElements()) {
            final String paramName = (String) ei.nextElement();
            json.put(paramName, request.getParameter(paramName));
        }
        return json;

    }

}
