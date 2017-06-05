package com.estudio.define.utils;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class CodeAssistRecord extends DBRecord {
    long id; // 唯一标识号
    String caption; // 标题
    String content; // 内容
    long pid; // 父节点ID 有三个固定类别 -1:javascript -2:SQL -3:JAVA
    String help; // 帮助信息
    long sortorder; // 排列顺序
    long type; // 0:类型分组 1:函数 3:代码片段
    String extType;

    public String getExtType() {
        return extType;
    }

    public void setExtType(final String extType) {
        this.extType = extType;
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
     * 获取内容
     * 
     * @return 返回内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     * 
     * @param value
     *            内容
     */
    public void setContent(final String value) {
        content = value;
    }

    /**
     * 获取父节点ID 有三个固定类别 -1:javascript -2:SQL -3:JAVA
     * 
     * @return 返回父节点ID 有三个固定类别 -1:javascript -2:SQL -3:JAVA
     */
    public long getPid() {
        return pid;
    }

    /**
     * 设置父节点ID 有三个固定类别 -1:javascript -2:SQL -3:JAVA
     * 
     * @param value
     *            父节点ID 有三个固定类别 -1:javascript -2:SQL -3:JAVA
     */
    public void setPid(final long value) {
        pid = value;
    }

    /**
     * 获取帮助信息
     * 
     * @return 返回帮助信息
     */
    public String getHelp() {
        return help;
    }

    /**
     * 设置帮助信息
     * 
     * @param value
     *            帮助信息
     */
    public void setHelp(final String value) {
        help = value;
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
     * 获取0:类型分组 1:函数 3:代码片段
     * 
     * @return 返回0:类型分组 1:函数 3:代码片段
     */
    public long getType() {
        return type;
    }

    /**
     * 设置0:类型分组 1:函数 3:代码片段
     * 
     * @param value
     *            0:类型分组 1:函数 3:代码片段
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("caption", caption);
        json.put("content", content);
        json.put("help", help);
        json.put("type", type);
        return json;
    }
}
