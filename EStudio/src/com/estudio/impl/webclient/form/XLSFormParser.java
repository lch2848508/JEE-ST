package com.estudio.impl.webclient.form;

import org.dom4j.Document;

import com.estudio.define.webclient.form.FormDefine;

public class XLSFormParser {

    /**
     * 解析Windows风格窗体 根据DOM生成HTML代码
     * 
     * @param formDefine
     * 
     * @param formDefine
     * @param dom
     * @param jsSb
     * @param offsetX
     *            X方向偏移修正
     * @param offsetY
     *            Y方向偏移修正
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
