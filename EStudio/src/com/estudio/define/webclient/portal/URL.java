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
     * 取得URL
     * 
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置URL
     * 
     * @param url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * 得到表单参数迭代器
     * 
     * @return
     */
    public Iterator<Map.Entry<String, String>> getParamsIterator() {
        return params.entrySet().iterator();
    }

    /**
     * 注册表单参数
     * 
     * @param paramName
     * @param paramInitValue
     */
    public void registerParam(final String paramName, final String paramInitValue) {
        params.put(paramName, paramInitValue);
    }

}
