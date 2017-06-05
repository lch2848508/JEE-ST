package com.estudio.define.webclient.portal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class URL {
    private String url;
    HashMap<String, String> params = new HashMap<String, String>();

    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * ȡ��URL
     * 
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * ����URL
     * 
     * @param url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * �õ�������������
     * 
     * @return
     */
    public Iterator<Map.Entry<String, String>> getParamsIterator() {
        return params.entrySet().iterator();
    }

    /**
     * ע�������
     * 
     * @param paramName
     * @param paramInitValue
     */
    public void registerParam(final String paramName, final String paramInitValue) {
        params.put(paramName, paramInitValue);
    }

}
