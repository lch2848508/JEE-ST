package com.estudio.define.design.user;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class DepartmentRecord extends DBRecord {
    long id; // 唯一标识号
    String name; // 部门名称
    long pId; // 父节点ID

    /**
     * 获取父节点ID
     * 
     * @return 返回父节点ID
     */
    public long getPId() {
        return pId;
    }

    /**
     * 设置父节点ID
     * 
     * @param value
     *            父节点ID
     */
    public void setPId(final long value) {
        pId = value;
    }

    /**
     * 获取唯一标识号
     * 
     * @return 返回唯一标识号
     */
    public long getId() {
        return id;
    }

    /**
     * 设置唯一标识号
     * 
     * @param value
     *            唯一标识号
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取部门名称
     * 
     * @return 返回部门名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置部门名称
     * 
     * @param value
     *            部门名称
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * @return 生成JSON对象
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
