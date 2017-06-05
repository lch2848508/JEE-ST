package com.estudio.define.design.user;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class RoleRecord extends DBRecord {
    long id; // 唯一标识号
    long pid; // 类型ID
    String name; // 角色名称
    byte[] descript; // 角色描述信息

    /**
     * 获取唯一标识号
     * 
     * @return 返回唯一标识号
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
     * 设置唯一标识号
     * 
     * @param value
     *            唯一标识号
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取角色名称
     * 
     * @return 返回角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     * 
     * @param value
     *            角色名称
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * 获取角色描述信息
     * 
     * @return 返回角色描述信息
     */
    public byte[] getDescript() {
        return descript;
    }

    /**
     * 设置角色描述信息
     * 
     * @param value
     *            角色描述信息
     */
    public void setDescript(final byte[] value) {
        descript = value;
    }

    /**
     * @return 生成JSON对象
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
