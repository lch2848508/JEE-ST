package com.estudio.impl.webclient.form;

import org.dom4j.Document;

import com.estudio.define.webclient.form.FormDefine;

public class XLSFormParser {

    /**
     * ����Windows����� ����DOM����HTML����
     * 
     * @param formDefine
     * 
     * @param formDefine
     * @param dom
     * @param jsSb
     * @param offsetX
     *            X����ƫ������
     * @param offsetY
     *            Y����ƫ������
     */
    public String parser(final FormDefine formDefine, final Document dom, final StringBuilder jsSb) {
        final StringBuffer sb = new StringBuffer();
        return sb.toString();
    }

    private static final XLSFormParser INSTANCE = new XLSFormParser();

    public static XLSFormParser getInstance() {
        return INSTANCE;
    }
}
