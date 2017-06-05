package com.estudio.define.webclient;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.estudio.define.webclient.portal.SQLDefine4Portal;

public class BaseQueryUIDefine {
    private SQLDefine4Portal sqlDefine = null;
    private JSONObject uiDefine = null;
    private final Map<String, String> comboboxFilterControl2SQL = new HashMap<String, String>();
    private boolean pagination = true;

    public SQLDefine4Portal getSqlDefine() {
        return sqlDefine;
    }

    public void setSqlDefine(final SQLDefine4Portal sqlDefine) {
        this.sqlDefine = sqlDefine;
    }

    public JSONObject getUiDefine() {
        return uiDefine;
    }

    public void setUiDefine(final JSONObject uiDefine) {
        this.uiDefine = uiDefine;
    }

    public Map<String, String> getComboboxFilterControl2SQL() {
        return comboboxFilterControl2SQL;
    }

    public boolean isPagination() {
        return pagination;
    }

    public void setPagination(boolean pagination) {
        this.pagination = pagination;
    }

}
