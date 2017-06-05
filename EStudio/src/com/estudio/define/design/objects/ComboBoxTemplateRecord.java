package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class ComboBoxTemplateRecord extends DBRecord {
    long id; // null
    String name; // null
    byte[] content; // null

    /**
     * ��ȡnull
     * 
     * @return ����null
     */
    public long getId() {
        return id;
    }

    /**
     * ����null
     * 
     * @param value
     *            null
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * ��ȡnull
     * 
     * @return ����null
     */
    public String getName() {
        return name;
    }

    /**
     * ����null
     * 
     * @param value
     *            null
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * ��ȡnull
     * 
     * @return ����null
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * ����null
     * 
     * @param value
     *            null
     */
    public void setContent(final byte[] value) {
        content = value;
    }

    /**
     * @return ����JSON����
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
