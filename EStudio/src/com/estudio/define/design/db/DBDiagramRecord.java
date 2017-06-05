package com.estudio.define.design.db;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class DBDiagramRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    String name; // ģ������
    byte[] descript; // ģ��������Ϣ
    long sortorder; // ����˳��

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
    public String getName() {
        return name;
    }

    /**
     * ����ģ������
     * 
     * @param value
     *            ģ������
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * ��ȡģ��������Ϣ
     * 
     * @return ����ģ��������Ϣ
     */
    public byte[] getDescript() {
        return descript;
    }

    /**
     * ����ģ��������Ϣ
     * 
     * @param value
     *            ģ��������Ϣ
     */
    public void setDescript(final byte[] value) {
        descript = value;
    }

    /**
     * ��ȡ����˳��
     * 
     * @return ��������˳��
     */
    public long getSortorder() {
        return sortorder;
    }

    /**
     * ��������˳��
     * 
     * @param value
     *            ����˳��
     */
    public void setSortorder(final long value) {
        sortorder = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("n", name);
        json.put("dsc", Convert.bytes2Str(descript));
        return json;
    }
}
