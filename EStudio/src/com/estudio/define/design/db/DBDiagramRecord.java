package com.estudio.define.design.db;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class DBDiagramRecord extends DBRecord {
    long id; // 唯一标识号
    String name; // 模型名称
    byte[] descript; // 模型描述信息
    long sortorder; // 排列顺序

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
     * 获取模型名称
     * 
     * @return 返回模型名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置模型名称
     * 
     * @param value
     *            模型名称
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * 获取模型描述信息
     * 
     * @return 返回模型描述信息
     */
    public byte[] getDescript() {
        return descript;
    }

    /**
     * 设置模型描述信息
     * 
     * @param value
     *            模型描述信息
     */
    public void setDescript(final byte[] value) {
        descript = value;
    }

    /**
     * 获取排列顺序
     * 
     * @return 返回排列顺序
     */
    public long getSortorder() {
        return sortorder;
    }

    /**
     * 设置排列顺序
     * 
     * @param value
     *            排列顺序
     */
    public void setSortorder(final long value) {
        sortorder = value;
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
