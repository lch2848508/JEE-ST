package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class ObjectFormsRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    byte[] dfmstream; // ����������õ���������
    byte[] xmlstream; // XML��ʽ������
    byte[] datasource; // ����Դ��XML��ʽ
    byte[] jsscript; // jsscript�ű�
    long version; // �汾��
    long type; // ���� 0 Win��� 1 Excel���
    String formParams;

    public String getFormParams() {
        return formParams;
    }

    public void setFormParams(final String formParams) {
        this.formParams = formParams;
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
     * ��ȡ����������õ���������
     * 
     * @return ���ر���������õ���������
     */
    public byte[] getDfmstream() {
        return dfmstream;
    }

    /**
     * ���ñ���������õ���������
     * 
     * @param value
     *            ����������õ���������
     */
    public void setDfmstream(final byte[] value) {
        dfmstream = value;
    }

    /**
     * ��ȡXML��ʽ������
     * 
     * @return ����XML��ʽ������
     */
    public byte[] getXmlstream() {
        return xmlstream;
    }

    /**
     * ����XML��ʽ������
     * 
     * @param value
     *            XML��ʽ������
     */
    public void setXmlstream(final byte[] value) {
        xmlstream = value;
    }

    /**
     * ��ȡ����Դ��XML��ʽ
     * 
     * @return ��������Դ��XML��ʽ
     */
    public byte[] getDatasource() {
        return datasource;
    }

    /**
     * ��������Դ��XML��ʽ
     * 
     * @param value
     *            ����Դ��XML��ʽ
     */
    public void setDatasource(final byte[] value) {
        datasource = value;
    }

    /**
     * ��ȡjsscript�ű�
     * 
     * @return ����jsscript�ű�
     */
    public byte[] getJsscript() {
        return jsscript;
    }

    /**
     * ����jsscript�ű�
     * 
     * @param value
     *            jsscript�ű�
     */
    public void setJsscript(final byte[] value) {
        jsscript = value;
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
     * ��ȡ���� 0 Win��� 1 Excel���
     * 
     * @return �������� 0 Win��� 1 Excel���
     */
    public long getType() {
        return type;
    }

    /**
     * �������� 0 Win��� 1 Excel���
     * 
     * @param value
     *            ���� 0 Win��� 1 Excel���
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("version", version);
        json.put("type", type);
        return json;
    }
}
