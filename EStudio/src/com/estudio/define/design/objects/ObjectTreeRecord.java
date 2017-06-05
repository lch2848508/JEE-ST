package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class ObjectTreeRecord extends DBRecord {
    String memo; // 备注信息
    long id; // 唯一标识符
    String caption; // 标题
    long type; // 类型 0 跟节点 1目录 2业务 3表单 4报表 5查询 6工作流
    long version; // 版本
    long sortorder; // 排列顺序
    long pid; // 父节点id
    long lockby; // 当前锁定人
    long propId; // 属性ID

    /**
     * 获取备注信息
     * 
     * @return 返回备注信息
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置备注信息
     * 
     * @param value
     *            备注信息
     */
    public void setMemo(final String value) {
        memo = value;
    }

    /**
     * 获取唯一标识符
     * 
     * @return 返回唯一标识符
     */
    public long getId() {
        return id;
    }

    /**
     * 设置唯一标识符
     * 
     * @param value
     *            唯一标识符
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取标题
     * 
     * @return 返回标题
     */
    public String getCaption() {
        return caption;
    }

    /**
     * 设置标题
     * 
     * @param value
     *            标题
     */
    public void setCaption(final String value) {
        caption = value;
    }

    /**
     * 获取类型 0 跟节点 1目录 2业务 3表单 4报表 5查询 6工作流
     * 
     * @return 返回类型 0 跟节点 1目录 2业务 3表单 4报表 5查询 6工作流
     */
    public long getType() {
        return type;
    }

    /**
     * 设置类型 0 跟节点 1目录 2业务 3表单 4报表 5查询 6工作流
     * 
     * @param value
     *            类型 0 跟节点 1目录 2业务 3表单 4报表 5查询 6工作流
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * 获取版本
     * 
     * @return 返回版本
     */
    public long getVersion() {
        return version;
    }

    /**
     * 设置版本
     * 
     * @param value
     *            版本
     */
    public void setVersion(final long value) {
        version = value;
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
     * 获取父节点id
     * 
     * @return 返回父节点id
     */
    public long getPid() {
        return pid;
    }

    /**
     * 设置父节点id
     * 
     * @param value
     *            父节点id
     */
    public void setPid(final long value) {
        pid = value;
    }

    /**
     * 获取当前锁定人
     * 
     * @return 返回当前锁定人
     */
    public long getLockby() {
        return lockby;
    }

    /**
     * 设置当前锁定人
     * 
     * @param value
     *            当前锁定人
     */
    public void setLockby(final long value) {
        lockby = value;
    }

    /**
     * 获取属性ID
     * 
     * @return 返回属性ID
     */
    public long getPropId() {
        return propId;
    }

    /**
     * 设置属性ID
     * 
     * @param value
     *            属性ID
     */
    public void setPropId(final long value) {
        propId = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("memo", memo);
        json.put("id", id);
        json.put("caption", caption);
        json.put("lockby", lockby);
        json.put("prop_id", propId);
        return json;
    }
}
