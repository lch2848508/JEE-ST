package com.estudio.impl.webclient.form;

import java.sql.Connection;

import org.dom4j.Document;

import com.estudio.define.webclient.form.FormDefine;
import com.estudio.utils.XML2JSON;

public class WinForm2JsonParser {

    /**
     * ����Windows����� ����DOM����HTML����
     * 
     * @param formDefine
     * @param dom
     * @param jsSb
     * @param offsetX
     *            X����ƫ������
     * @param offsetY
     *            Y����ƫ������
     * @throws Exception
     */
    public String parser(final FormDefine formDefine, final Document dom, final StringBuilder jsSb, final Connection con) throws Exception {
        // formDefine.setFormJson(XML.toJSONObject(dom.asXML()));
        formDefine.setFormJson(XML2JSON.xml2Json(dom));
        return null;
    }

    private static final WinForm2JsonParser INSTANCE = new WinForm2JsonParser();

    public static WinForm2JsonParser getInstance() {
        return INSTANCE;
    }
}
