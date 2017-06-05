package com.estudio.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;

import com.estudio.context.ClientInfo;
import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.FormValuesContain.FormFile;
import com.estudio.web.servlet.FormValuesContain.FormInput;

public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 8419442667454184507L;
    private static String tempFilePath = null;
    ThreadLocal<HttpServletRequest> localRequest = new ThreadLocal<HttpServletRequest>();
    protected ThreadLocal<FormValuesContain> localFormValuesContain = new ThreadLocal<FormValuesContain>();
    protected ThreadLocal<Boolean> localFormIncludeAttachment = new ThreadLocal<Boolean>();
    protected ThreadLocal<ClientLoginInfo> loginInfo = new ThreadLocal<ClientLoginInfo>();
    protected boolean isClearTempFileAfterPost = true; // 服务结束后是否清除临时文件

    public ClientLoginInfo getLoginInfo() {
        return loginInfo.get();
    }

    /**
     * 得到上传的附件的保存文件名
     * 
     * @param name
     * @return
     */
    protected String getAttachmentFileName(final String name) {
        return localFormValuesContain.get().getFormFile(name).getSaveFileName();
    }

    protected FormFile getFormFile(final String name) {
        return localFormValuesContain.get().getFormFile(name);
    }

    /**
     * 获取Post的内容
     * 
     * @return
     * @throws Exception
     */
    public String getPostContent() throws Exception {
        InputStream is = null;
        String contentStr = "";
        try {
            is = localRequest.get().getInputStream();
            contentStr = IOUtils.toString(is, "utf-8");
        } finally {
            if (is != null)
                IOUtils.closeQuietly(is);
        }
        return contentStr;
    }

    /**
     * 得到所有相关的内容
     * 
     * @return
     */
    protected Map<String, String> getParams() {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("SESSIONID", localRequest.get().getSession().getId());
        result.put("USER_ID", Long.toString(loginInfo.get().getId()));
        result.put("USER_LOGINNAME", loginInfo.get().getLoginName());
        result.put("USER_NAME", loginInfo.get().getRealName());
        result.put("DEPARTMENT_ID", Long.toString(loginInfo.get().getDepartmentId()));
        result.put("DEPARTMENT_NAME", "");
        result.put("-1", "-1");
        result.put("%", "%");
        result.put("MINDATE", "1900-01-01");
        result.put("MAXDATE", "2300-12-31");

        if (localFormIncludeAttachment.get()) {
            final FormValuesContain formValuesContain = localFormValuesContain.get();
            final Iterator<FormInput> iterator = formValuesContain.getParamIterator();
            while (iterator.hasNext()) {
                final FormInput fi = iterator.next();
                if (StringUtils.equals("null", fi.value))
                    continue;
                result.put(fi.name, fi.value);
            }
        } else {
            final HttpServletRequest request = localRequest.get();
            final Enumeration<?> ei = request.getParameterNames();
            while (ei.hasMoreElements()) {
                final String paramName = (String) ei.nextElement();
                final String v = request.getParameter(paramName);
                if (StringUtils.equals("null", v))
                    continue;
                result.put(paramName, v);
            }
        }
        return result;
    }

    /**
     * 返回请求过来的日期 日期格式为 yyyy-MM-dd 如格式失败返回缺省值
     * 
     * @param paramName
     * @return
     */
    protected Date getParamDate(final String paramName, final Date defaultValue) {
        return Convert.try2Date(getParamStr(paramName), defaultValue);
    }

    /**
     * 返回请求过来的日期 日期格式为 yyyy-MM-dd
     * 
     * @param paramName
     * @return
     */
    protected Date getParamDate(final String paramName) {
        return Convert.str2Date(getParamStr(paramName));
    }

    /**
     * 返回请求过来的二进制数据(可能是附件也可能是字符串)
     * 
     * @param paramName
     * @return
     */
    protected byte[] getParamBytes(final String paramName) {
        return localFormIncludeAttachment.get() ? localFormValuesContain.get().getParamBytes(paramName) : Convert.str2Bytes(localRequest.get().getParameter(paramName));
    }

    /**
     * 返回请求过来的二进制数据(可能是附件也可能是字符串)
     * 
     * @param paramName
     * @return
     */
    protected byte[] getParamBase64Bytes(final String paramName) {
        final String str = getParamStr(paramName);
        if (!StringUtils.isEmpty(str))
            return Base64.decodeBase64(str);
        return new byte[0];
    }

    /**
     * 返回请求过来的浮点数
     * 
     * @param paramName
     * @return
     */
    protected double getParamDouble(final String paramName) {
        return Convert.str2Double(getParamStr(paramName));
    }

    /**
     * 返回请求过来的浮点数 如果格式失败返回缺省值
     * 
     * @param paramName
     * @param defaultValue
     * @return
     */
    protected double getParamDouble(final String paramName, final double defaultValue) {
        return Convert.try2Double(getParamStr(paramName), defaultValue);
    }

    /**
     * 得到IDS列表
     * 
     * @param str
     * @return
     */
    public int[] getParamInts(final String paramName) {
        final String str = getParamStr(paramName);
        int[] result = null;
        if (StringUtils.isEmpty(str))
            result = new int[0];
        else {
            final String[] ids = str.split(",");
            result = new int[ids.length];
            for (int i = 0; i < ids.length; i++)
                result[i] = Integer.parseInt(ids[i]);
        }
        return result;
    }

    /**
     * 得到IDS列表
     * 
     * @param str
     * @return
     */
    public long[] getParamLongs(final String paramName) {
        final String str = getParamStr(paramName);
        long[] result = null;
        if (StringUtils.isEmpty(str))
            result = new long[0];
        else {
            final String[] ids = str.split(",");
            result = new long[ids.length];
            for (int i = 0; i < ids.length; i++)
                result[i] = Long.parseLong(ids[i]);
        }
        return result;
    }

    /**
     * 返回求过来的整型参数
     * 
     * @param paramName
     * @return
     */
    protected int getParamInt(final String paramName) {
        return Convert.try2Int(getParamStr(paramName), 0);
    }

    /**
     * 返回求过来的整型参数 如格式失败返回缺省值
     * 
     * @param paramName
     * @param defaultValue
     * @return
     */
    protected int getParamInt(final String paramName, final int defaultValue) {
        return Convert.try2Int(getParamStr(paramName), defaultValue);
    }

    /**
     * 返回请求过来的字符串参数
     * 
     * @param paramName
     * @return
     */
    protected String getParamStr(final String paramName) {
        return localFormIncludeAttachment.get() ? localFormValuesContain.get().getParamValue(paramName) : localRequest.get().getParameter(paramName);
    }

    /**
     * 返回请求过来的字符串参数
     * 
     * @param paramName
     * @return
     */
    protected long getParamLong(final String paramName) {
        final String str = getParamStr(paramName);
        return Convert.try2Long(str, 0L);
    }

    /**
     * 字符串转Long
     * 
     * @param paramName
     * @param defaultValue
     * @return
     */
    protected long getParamLong(final String paramName, final long defaultValue) {
        final String str = getParamStr(paramName);
        return Convert.try2Long(str, defaultValue);
    }

    /**
     * 返回请求过来的布尔型参数
     * 
     * @param paramName
     * @return
     */
    protected boolean getParamBoolean(final String paramName) {
        return Convert.str2Boolean(getParamStr(paramName));
    }

    /**
     * 处理数据
     */
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 处理数据
     */
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        //long time = System.currentTimeMillis();
        try {
            request.setCharacterEncoding("utf-8");

            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html; charset=utf-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            loginInfo.set(RuntimeContext.getClientLoginService().getLoginInfo(request.getSession()));

            localRequest.set(request);
            localFormValuesContain.set(FormValuesContain.getInstance(request, tempFilePath));
            localFormIncludeAttachment.set(localFormValuesContain.get() != null);
            if (localFormIncludeAttachment.get()) {
                @SuppressWarnings("unchecked")
                final Enumeration<String> pns = request.getParameterNames();
                while (pns.hasMoreElements()) {
                    final String pn = pns.nextElement();
                    localFormValuesContain.get().addParam(pn, request.getParameter(pn));
                }
            }

            // 设置全局上下文
            GlobalContext.setLoginInfo(getLoginInfo());
            GlobalContext.setClientMessage(new StringBuffer());
            GlobalContext.setAlertMessage(new StringBuffer());
            GlobalContext.setClientInfo(new ClientInfo(getAddr(request), ""));

            doRequest(request, response);

            // 获取全局上下文
            GlobalContext.setLoginInfo(null);
            GlobalContext.setClientMessage(null);
            GlobalContext.setAlertMessage(null);
            GlobalContext.setClientInfo(null);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            response.getWriter().println(JSONUtils.except2JSON(e));
            // e.printStackTrace();
        } finally {
            clearTempFile();
        }
        //System.out.println("totalTime:" + (System.currentTimeMillis()-time));
    }

    /**
     * 获取客户端IP地址
     * 
     * @param request
     * @return
     */
    private String getAddr(final HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("Proxy-Client-IP");
        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("WL-Proxy-Client-IP");
        if ((ip == null) || (ip.length() == 0) || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();
        return ip;
    }

    /**
     * 获取回话Session
     * 
     * @return
     */
    protected HttpSession getSession() {
        return localRequest.get().getSession();
    }

    /**
     * 设置上传文件的临时目录
     * 
     * @param value
     */
    public static void setTempFilePath(final String value) {
        tempFilePath = value;
    }

    public static String getTempFilePath() {
        return tempFilePath;
    }

    /**
     * 定义处理请求总函数
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected abstract void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 设置缓存事件
     * 
     * @param response
     * @param i
     */
    protected void setCacheTime(final HttpServletResponse response, final int second) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, second);
        response.setHeader("Pragma", "cache");
        response.setHeader("Cache-Control", "max-age=" + second);
        response.setDateHeader("Expires", calendar.getTime().getTime());
    }

    /**
     * 清理客户端表单上传的临时文件
     */
    private void clearTempFile() {
        if (localFormIncludeAttachment.get() && isClearTempFileAfterPost) {
            final Iterator<FormFile> iterator = localFormValuesContain.get().getFileIterator();
            while (iterator.hasNext()) {
                final FormFile formFile = iterator.next();
                FileUtils.deleteQuietly(new File(formFile.getSaveFileName()));
            }
        }
    }
}
