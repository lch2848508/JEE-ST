package com.estudio.define.webclient.portal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BindForm {
    String formId;
    HashMap<String, String> params = new HashMap<String, String>();

    /**
     * �õ�������
     * 
     * @return
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * �õ���ID
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
