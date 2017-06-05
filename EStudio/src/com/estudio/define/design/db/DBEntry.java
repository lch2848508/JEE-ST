package com.estudio.define.design.db;

import net.minidev.json.JSONObject;

public class DBEntry {
    String name; // ���ݿ�ʵ���ע��
    String code; // ���ݿ�ʵ�����ʵ����
    String descript;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescript() {
        return descript;
    }

    /**
     * ���캯��
     * 
     * @param name
     * @param code
     * @param descript
     */
    public DBEntry(final String name, final String code, final String descript) {
        super();
        this.name = name;
        this.code = code;
        this.descript = descript;
    }

    /**
     * ���캯��
     * 
     * @param name
     * @param code
     */
    public DBEntry(final String name, final String code) {
        super();
        this.name = name;
        this.code = code;
    }

    /**
     * ����JSON����
     * 
     * @return
     */
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put("n", name);
        json.put("c", code);
        return json;
    }

}
