package com.estudio.define.design.portal;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class PortalRightRecord extends DBRecord {
    long id; // ����ID
    long portalId; // portal_id
    long workgroupId; // workgroup_id
    long readable; // ��Ȩ��
    long writeable; // дȨ��

    /**
     * ��ȡ����ID
     * 
     * @return ��������ID
     */
    public long getId() {
        return id;
    }

    /**
     * ��������ID
     * 
     * @param value
     *            ����ID
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * ��ȡportal_id
     * 
     * @return ����portal_id
     */
    public long getPortalId() {
        return portalId;
    }

    /**
     * ����portal_id
     * 
     * @param value
     *            portal_id
     */
    public void setPortalId(final long value) {
        portalId = value;
    }

    /**
     * ��ȡworkgroup_id
     * 
     * @return ����workgroup_id
     */
    public long getWorkgroupId() {
        return workgroupId;
    }

    /**
     * ����workgroup_id
     * 
     * @param value
     *            workgroup_id
     */
    public void setWorkgroupId(final long value) {
        workgroupId = value;
    }

    /**
     * ��ȡ��Ȩ��
     * 
     * @return ���ض�Ȩ��
     */
    public long getReadable() {
        return readable;
    }

    /**
     * ���ö�Ȩ��
     * 
     * @param value
     *            ��Ȩ��
     */
    public void setReadable(final long value) {
        readable = value;
    }

    /**
     * ��ȡдȨ��
     * 
     * @return ����дȨ��
     */
    public long getWriteable() {
        return writeable;
    }

    /**
     * ����дȨ��
     * 
     * @param value
     *            дȨ��
     */
    public void setWriteable(final long value) {
        writeable = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("portal_id", portalId);
        json.put("workgroup_id", workgroupId);
        json.put("readable", readable);
        json.put("writeable", writeable);
        return json;
    }
}
