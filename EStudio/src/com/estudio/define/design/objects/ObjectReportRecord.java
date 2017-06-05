package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class ObjectReportRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    String content; // ģ������
    long version; // �汾��Ϣ
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
     * ��ȡΨһ��ʶ��
     * 
     * @return ����Ψһ��ʶ��
     */
    public long getId() {
        return id;
    }

    /**
     * ����Ψһ��ʶ��
     * 
     * @param value
     *            Ψһ��ʶ��
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * ��ȡģ������
     * 
     * @return ����ģ������
     */
    public String getContent() {
        return content;
    }

    /**
     * ����ģ������
     * 
     * @param value
     *            ģ������
     */
    public void setContent(final String value) {
        content = value;
    }

    /**
     * ��ȡ�汾��Ϣ
     * 
     * @return ���ذ汾��Ϣ
     */
    public long getVersion() {
        return version;
    }

    /**
     * ���ð汾��Ϣ
     * 
     * @param value
     *            �汾��Ϣ
     */
    public void setVersion(final long value) {
        version = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("content", content);
        return json;
    }
}
