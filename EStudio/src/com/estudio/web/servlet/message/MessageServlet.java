package com.estudio.web.servlet.message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.estudio.web.servlet.BaseServlet;

public class MessageServlet extends BaseServlet {
    private static final long serialVersionUID = -3100123189703807269L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        // String operation = getParamStr("o");
        // if (StringUtils.equals("connection", operation)) {
        // MessageService.getInstance().addAsync(request, response);
        // } else if (StringUtils.equals("broadcast", operation)) { // 广播消息
        // MessageService.getInstance().broadcasdMessage(request.getParameter("content"));
        // } else if (StringUtils.equals("close", operation)) { // 广播消息
        // MessageService.getInstance().closeMessageClient(request.getSession());
        // }
    }

}
