package com.estudio.define.webclient.query;

import net.minidev.json.JSONObject;

import com.estudio.define.webclient.BaseQueryUIDefine;
import com.estudio.define.webclient.portal.SQLDefine4Portal;

public class QueryUIDefine extends BaseQueryUIDefine {
    private SQLDefine4Portal initSQLDefine = null;
    private SQLDefine4Portal cleanSQLDefine = null;
    private JSONObject toolbarJson = null;

    public JSONObject getToolbarJson() {
        return toolbarJson;
    }

    public void setToolbarJson(JSONObject toolbarJson) {
        this.toolbarJson = toolbarJson;
    }

    private String js = "";

    public String getJs() {
        return js;
    }

    public void setJs(final String js) {
        this.js = js;
    }

    public SQLDefine4Portal getInitSQLDefine() {
        return initSQLDefine;
    }

    public void setInitSQLDefine(final SQLDefine4Portal initSQLDefine) {
        this.initSQLDefine = initSQLDefine;
    }

    public SQLDefine4Portal getCleanSQLDefine() {
        return cleanSQLDefine;
    }

    public void setCleanSQLDefine(final SQLDefine4Portal cleanSQLDefine) {
        this.cleanSQLDefine = cleanSQLDefine;
    }
}
