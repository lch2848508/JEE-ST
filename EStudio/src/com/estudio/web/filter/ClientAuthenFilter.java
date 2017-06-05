package com.estudio.web.filter;

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

public class ClientAuthenFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    public ClientAuthenFilter() {
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
            final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(((HttpServletRequest) req).getSession());
            if (loginInfo != null) {
                req.setCharacterEncoding("utf-8");
                ((HttpServletResponse) res).setHeader("Pragma", "no-cache");
                ((HttpServletResponse) res).setHeader("Cache-Control", "no-cache");
                ((HttpServletResponse) res).setDateHeader("Expires", 0);
                filterChain.doFilter(req, res);
            } else {
                final String path = ((HttpServletRequest) req).getRequestURI();
                if (StringUtils.contains(path, "client/") && !StringUtils.contains(path, ".jsp")) {
                    res.setCharacterEncoding("utf-8");
                    res.setContentType("text/html; charset=UTF-8");
                    res.getWriter().println("{r:false,errorCode:-65535,msg:'同服务器失去连接.\\n请关闭所有窗口然后重新登录.'}");
                } else ((HttpServletResponse) res).sendRedirect("../index.jsp");
            }
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
