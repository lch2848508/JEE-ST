package com.estudio.define.webclient.portal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BindForm {
    String formId;
    HashMap<String, String> params = new HashMap<String, String>();

    /**
     * 得到表单参数
     * 
     * @return
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * 得到表单ID
     * 
     * @return
     */
    public String getFormId() {
        return formId;
    }

    public void setFormId(final String formId) {
        this.formId = formId;
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
