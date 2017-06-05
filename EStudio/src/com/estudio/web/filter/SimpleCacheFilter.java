package com.estudio.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.utils.ExceptionUtils;

public class SimpleCacheFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    public SimpleCacheFilter() {
        super();
    }

    @Override
    public void destroy() {
    }

    /**
     * 过滤器执行函数
     */
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain filterChain) {
        try {
            java.util.Date date = new java.util.Date();    
            ((HttpServletResponse) res).setDateHeader("Last-Modified",date.getTime()); //Last-Modified:页面的最后生成时间 
            ((HttpServletResponse) res).setDateHeader("Expires",date.getTime()+3600*24*30); //Expires:过时期限值 
            ((HttpServletResponse) res).setHeader("Cache-Control", "public"); //Cache-Control来控制页面的缓存与否,public:浏览器和缓存服务器都可以缓存页面信息；
            ((HttpServletResponse) res).setHeader("Pragma", "Pragma"); //Pragma:设置页面是否缓存,为Pragma则缓存,no-cache则不缓存

            filterChain.doFilter(req, res);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
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
