package com.estudio.define.webclient.portal;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public class SQLColumnURL {
    String type;
    String[] params;
    String label;
    JSONObject json;
    boolean function;

    public boolean isFunction() {
        return function;
    }

    public void setFunction(final boolean function) {
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public String[] getParams() {
        return params;
    }

    public String getLabel() {
        return label;
    }

    /**
     * ���캯��
     * 
     * @param type
     *            ����������
     * @param params
     *            �����Ӳ���
     * @param label
     *            �����ӱ���
     */
    public SQLColumnURL(final String type, final String params, final String label) {
        super();
        this.type = type;
        this.label = label;
        if (StringUtils.equals("Function", type)) {
            function = true;
            try {
                if (!StringUtils.isEmpty(params))
                    json = JSONUtils.parserJSONObject(params);
            } catch (final Exception e) {
                ExceptionUtils.loggerException(e);

            }
        } else this.params = params.split(",");
    }

    public JSONObject getJson() {
        return json;
    }

}
