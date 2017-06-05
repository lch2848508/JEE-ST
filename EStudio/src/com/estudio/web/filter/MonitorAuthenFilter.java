package com.estudio.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.estudio.context.RuntimeContext;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.utils.ExceptionUtils;

public class MonitorAuthenFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    public MonitorAuthenFilter() {
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
            if ((loginInfo != null) && (loginInfo.getRoles().indexOf(-1L) != -1)) {
                req.setCharacterEncoding("utf-8");
                res.setCharacterEncoding("utf-8");
                ((HttpServletResponse) res).setHeader("Pragma", "No-cache");
                ((HttpServletResponse) res).setHeader("Cache-Control", "no-cache");
                ((HttpServletResponse) res).setDateHeader("Expires", 0);
                filterChain.doFilter(req, res);
            } else ((HttpServletResponse) res).sendRedirect("../flexclient/index.jsp");
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
