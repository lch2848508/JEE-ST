package com.estudio.define.design.db;

import net.minidev.json.JSONObject;

public class DBEntry {
    String name; // 数据库实体的注释
    String code; // 数据库实体的真实名称
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
     * 构造函数
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
     * 构造函数
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
     * 生成JSON对象
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
