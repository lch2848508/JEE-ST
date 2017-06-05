package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class ObjectReportRecord extends DBRecord {
    long id; // 唯一标识号
    String content; // 模版正文
    long version; // 版本信息
    String params;
    byte[] template;

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(final byte[] bs) {
        template = bs;
    }

    public String getParams() {
        return params;
    }

    public void setParams(final String params) {
        this.params = params;
    }

    /**
     * 获取唯一标识号
     * 
     * @return 返回唯一标识号
     */
    public long getId() {
        return id;
    }

    /**
     * 设置唯一标识号
     * 
     * @param value
     *            唯一标识号
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取模版正文
     * 
     * @return 返回模版正文
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置模版正文
     * 
     * @param value
     *            模版正文
     */
    public void setContent(final String value) {
        content = value;
    }

    /**
     * 获取版本信息
     * 
     * @return 返回版本信息
     */
    public long getVersion() {
        return version;
    }

    /**
     * 设置版本信息
     * 
     * @param value
     *            版本信息
     */
    public void setVersion(final long value) {
        version = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("content", content);
        return json;
    }
}
