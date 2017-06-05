package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class WorkFlowDesignInfo extends DBRecord {
    long id; // 唯一标识号
    long version; // 版本号
    String status; // 状态
    String descript; // 描述信息
    byte[] dfm; // 设计器信息
    byte[] property; // 属性信息

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
     * 获取版本号
     * 
     * @return 返回版本号
     */
    public long getVersion() {
        return version;
    }

    /**
     * 设置版本号
     * 
     * @param value
     *            版本号
     */
    public void setVersion(final long value) {
        version = value;
    }

    /**
     * 获取状态
     * 
     * @return 返回状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     * 
     * @param value
     *            状态
     */
    public void setStatus(final String value) {
        status = value;
    }

    /**
     * 获取描述信息
     * 
     * @return 返回描述信息
     */
    public String getDescript() {
        return descript;
    }

    /**
     * 设置描述信息
     * 
     * @param value
     *            描述信息
     */
    public void setDescript(final String value) {
        descript = value;
    }

    /**
     * 获取设计器信息
     * 
     * @return 返回设计器信息
     */
    public byte[] getDfm() {
        return dfm;
    }

    /**
     * 设置设计器信息
     * 
     * @param value
     *            设计器信息
     */
    public void setDfm(final byte[] value) {
        dfm = value;
    }

    /**
     * 获取属性信息
     * 
     * @return 返回属性信息
     */
    public byte[] getProperty() {
        return property;
    }

    /**
     * 设置属性信息
     * 
     * @param value
     *            属性信息
     */
    public void setProperty(final byte[] value) {
        property = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("version", version);
        return json;
    }

}
