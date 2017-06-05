package com.estudio.define.design.portal;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class PortalRightRecord extends DBRecord {
    long id; // 主键ID
    long portalId; // portal_id
    long workgroupId; // workgroup_id
    long readable; // 读权限
    long writeable; // 写权限

    /**
     * 获取主键ID
     * 
     * @return 返回主键ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置主键ID
     * 
     * @param value
     *            主键ID
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取portal_id
     * 
     * @return 返回portal_id
     */
    public long getPortalId() {
        return portalId;
    }

    /**
     * 设置portal_id
     * 
     * @param value
     *            portal_id
     */
    public void setPortalId(final long value) {
        portalId = value;
    }

    /**
     * 获取workgroup_id
     * 
     * @return 返回workgroup_id
     */
    public long getWorkgroupId() {
        return workgroupId;
    }

    /**
     * 设置workgroup_id
     * 
     * @param value
     *            workgroup_id
     */
    public void setWorkgroupId(final long value) {
        workgroupId = value;
    }

    /**
     * 获取读权限
     * 
     * @return 返回读权限
     */
    public long getReadable() {
        return readable;
    }

    /**
     * 设置读权限
     * 
     * @param value
     *            读权限
     */
    public void setReadable(final long value) {
        readable = value;
    }

    /**
     * 获取写权限
     * 
     * @return 返回写权限
     */
    public long getWriteable() {
        return writeable;
    }

    /**
     * 设置写权限
     * 
     * @param value
     *            写权限
     */
    public void setWriteable(final long value) {
        writeable = value;
    }

    /**
     * @return 生成JSON对象
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
