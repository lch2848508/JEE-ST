package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class WorkFlowDesignInfo extends DBRecord {
    long id; // Ψһ��ʶ��
    long version; // �汾��
    String status; // ״̬
    String descript; // ������Ϣ
    byte[] dfm; // �������Ϣ
    byte[] property; // ������Ϣ

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
     * ��ȡ�汾��
     * 
     * @return ���ذ汾��
     */
    public long getVersion() {
        return version;
    }

    /**
     * ���ð汾��
     * 
     * @param value
     *            �汾��
     */
    public void setVersion(final long value) {
        version = value;
    }

    /**
     * ��ȡ״̬
     * 
     * @return ����״̬
     */
    public String getStatus() {
        return status;
    }

    /**
     * ����״̬
     * 
     * @param value
     *            ״̬
     */
    public void setStatus(final String value) {
        status = value;
    }

    /**
     * ��ȡ������Ϣ
     * 
     * @return ����������Ϣ
     */
    public String getDescript() {
        return descript;
    }

    /**
     * ����������Ϣ
     * 
     * @param value
     *            ������Ϣ
     */
    public void setDescript(final String value) {
        descript = value;
    }

    /**
     * ��ȡ�������Ϣ
     * 
     * @return �����������Ϣ
     */
    public byte[] getDfm() {
        return dfm;
    }

    /**
     * �����������Ϣ
     * 
     * @param value
     *            �������Ϣ
     */
    public void setDfm(final byte[] value) {
        dfm = value;
    }

    /**
     * ��ȡ������Ϣ
     * 
     * @return ����������Ϣ
     */
    public byte[] getProperty() {
        return property;
    }

    /**
     * ����������Ϣ
     * 
     * @param value
     *            ������Ϣ
     */
    public void setProperty(final byte[] value) {
        property = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("version", version);
        return json;
    }

}
