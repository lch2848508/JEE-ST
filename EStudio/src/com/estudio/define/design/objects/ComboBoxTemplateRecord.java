package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class ComboBoxTemplateRecord extends DBRecord {
    long id; // null
    String name; // null
    byte[] content; // null

    /**
     * 获取null
     * 
     * @return 返回null
     */
    public long getId() {
        return id;
    }

    /**
     * 设置null
     * 
     * @param value
     *            null
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取null
     * 
     * @return 返回null
     */
    public String getName() {
        return name;
    }

    /**
     * 设置null
     * 
     * @param value
     *            null
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * 获取null
     * 
     * @return 返回null
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * 设置null
     * 
     * @param value
     *            null
     */
    public void setContent(final byte[] value) {
        content = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("content", Convert.bytes2Str(content));
        return json;
    }
}
