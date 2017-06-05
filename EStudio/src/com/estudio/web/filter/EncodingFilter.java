package com.estudio.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.estudio.utils.ExceptionUtils;

public class EncodingFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    /**
     * 构造函数
     */
    public EncodingFilter() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    @Override
    public void destroy() {
        // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * 过滤器执行函数
     */
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain filterChain) {
        try {
            final HttpServletRequest request = (HttpServletRequest) req;
            final HttpServletResponse response = (HttpServletResponse) res;
            request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            // 这里放到达目的地前(进入)处理代码
            filterChain.doFilter(req, res);
            // 这里放到达目的地(离开)的处理代码
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 初始化配置参数
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
}
