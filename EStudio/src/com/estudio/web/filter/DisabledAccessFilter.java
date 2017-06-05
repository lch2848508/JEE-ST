package com.estudio.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.estudio.utils.ExceptionUtils;

public class DisabledAccessFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    public DisabledAccessFilter() {
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
            final HttpServletResponse response = (HttpServletResponse) res;
            response.sendRedirect("../disable.jsp");
            // ����ŵ���Ŀ�ĵ�(�뿪)�Ĵ������
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
