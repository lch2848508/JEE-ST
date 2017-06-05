package com.estudio.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.estudio.define.ResultInfo;

public class Utils extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 5518193085410036607L;

    /**
     * Constructor of the object.
     */
    public Utils() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    @Override
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to
     * post.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTf-8");
        final String operation = request.getParameter("o");
        if ("testServer".equalsIgnoreCase(operation))
            response.getWriter().println("连接服务器成功!\n服务器版本 Ver1.0");
        else if ("nologin".equalsIgnoreCase(operation))
            response.getWriter().println(new ResultInfo(false, "没有登录系统\n或服务器登录信息丢失！").toJSON());
    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occurs
     */
    @Override
    public void init() throws ServletException {
        // Put your code here
    }

}
