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

public class DesignAuthenFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    public DesignAuthenFilter() {
        super();
    }

    @Override
    public void destroy() {
    }

    /**
     * ������ִ�к���
     */
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain filterChain) {
        try {
            final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(((HttpServletRequest) req).getSession());
            if ((loginInfo != null) && loginInfo.isRole(-1)) {
                req.setCharacterEncoding("utf-8");
                res.setCharacterEncoding("utf-8");
                ((HttpServletResponse) res).setHeader("Pragma", "No-cache");
                ((HttpServletResponse) res).setHeader("Cache-Control", "no-cache");
                ((HttpServletResponse) res).setDateHeader("Expires", 0);
                filterChain.doFilter(req, res);
            } else {
                res.setCharacterEncoding("utf-8");
                res.setContentType("text/html; charset=UTF-8");
                res.getWriter().println("{r:false,errorCode:-65535,msg:'ͬ������ʧȥ����.\\n��ر����д���Ȼ�����µ�¼.'}");
            }
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * ��ʼ�����ò���
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
}
