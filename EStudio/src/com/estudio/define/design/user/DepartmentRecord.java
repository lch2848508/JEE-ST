package com.estudio.define.design.user;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class DepartmentRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    String name; // ��������
    long pId; // ���ڵ�ID

    /**
     * ��ȡ���ڵ�ID
     * 
     * @return ���ظ��ڵ�ID
     */
    public long getPId() {
        return pId;
    }

    /**
     * ���ø��ڵ�ID
     * 
     * @param value
     *            ���ڵ�ID
     */
    public void setPId(final long value) {
        pId = value;
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
     * ��ȡ��������
     * 
     * @return ���ز�������
     */
    public String getName() {
        return name;
    }

    /**
     * ���ò�������
     * 
     * @param value
     *            ��������
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("n", name);
        json.put("p_id", pId);
        return json;
    }
}
