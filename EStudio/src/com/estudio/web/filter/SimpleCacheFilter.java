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
     * ������ִ�к���
     */
    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain filterChain) {
        try {
            java.util.Date date = new java.util.Date();    
            ((HttpServletResponse) res).setDateHeader("Last-Modified",date.getTime()); //Last-Modified:ҳ����������ʱ�� 
            ((HttpServletResponse) res).setDateHeader("Expires",date.getTime()+3600*24*30); //Expires:��ʱ����ֵ 
            ((HttpServletResponse) res).setHeader("Cache-Control", "public"); //Cache-Control������ҳ��Ļ������,public:������ͻ�������������Ի���ҳ����Ϣ��
            ((HttpServletResponse) res).setHeader("Pragma", "Pragma"); //Pragma:����ҳ���Ƿ񻺴�,ΪPragma�򻺴�,no-cache�򲻻���

            filterChain.doFilter(req, res);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
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
