package com.estudio.define.design.user;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class RoleRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    long pid; // ����ID
    String name; // ��ɫ����
    byte[] descript; // ��ɫ������Ϣ

    /**
     * ��ȡΨһ��ʶ��
     * 
     * @return ����Ψһ��ʶ��
     */
    public long getId() {
        return id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(final long pid) {
        this.pid = pid;
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
     * ��ȡ��ɫ����
     * 
     * @return ���ؽ�ɫ����
     */
    public String getName() {
        return name;
    }

    /**
     * ���ý�ɫ����
     * 
     * @param value
     *            ��ɫ����
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * ��ȡ��ɫ������Ϣ
     * 
     * @return ���ؽ�ɫ������Ϣ
     */
    public byte[] getDescript() {
        return descript;
    }

    /**
     * ���ý�ɫ������Ϣ
     * 
     * @param value
     *            ��ɫ������Ϣ
     */
    public void setDescript(final byte[] value) {
        descript = value;
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
