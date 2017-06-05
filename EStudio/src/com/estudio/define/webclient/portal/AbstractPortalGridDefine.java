package com.estudio.define.webclient.portal;

import java.util.HashMap;

import net.minidev.json.JSONObject;

public abstract class AbstractPortalGridDefine {

    protected HashMap<String, SQLDefineBase> name2SQLDefine = new HashMap<String, SQLDefineBase>();

    public abstract void createPortalJSON() throws Exception;

    protected int version;
    private String portalName = "";
    protected JSONObject portalJSON = new JSONObject();

    public String getPortalName() {
        return portalName;
    }

    public void setPortalName(final String portalName) {
        this.portalName = portalName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public AbstractPortalGridDefine() {
        super();
    }

    public abstract String getJavaScript();

    /**
     * �õ�Ԥ����Ľ���JSON����
     * 
     * @return
     */
    public JSONObject getPortalJSON() {
        return portalJSON;
    }

    /**
     * ����SQLDefine
     * 
     * @param name
     * @param sqlDefine
     */
    public void addSQLDefine(final String name, final SQLDefineBase sqlDefine) {
        name2SQLDefine.put(name, sqlDefine);
    }

    /**
     * �������Ƶõ�SQL����
     * 
     * @param name
     * @return
     */
    public SQLDefineBase getSQLDefineByName(final String name) {
        final SQLDefineBase result = name2SQLDefine.get(name);
        return result;
    }

    public HashMap<String, SQLDefineBase> getName2SQLDefine() {
        return name2SQLDefine;
    }

}