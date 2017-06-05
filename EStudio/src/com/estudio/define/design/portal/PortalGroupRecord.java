package com.estudio.define.design.portal;

import java.util.Date;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class PortalGroupRecord extends DBRecord {
    long id; // 唯一标识符
    String name; // 栏目名称
    long sortorder; // 栏目顺序
    byte[] memo; // 栏目备注信息
    Date createdate; // null
    long published; // 是否发布 1 发布 0未发布
    String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
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
     * 获取栏目名称
     * 
     * @return 返回栏目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置栏目名称
     * 
     * @param value
     *            栏目名称
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * 获取栏目顺序
     * 
     * @return 返回栏目顺序
     */
    public long getSortorder() {
        return sortorder;
    }

    /**
     * 设置栏目顺序
     * 
     * @param value
     *            栏目顺序
     */
    public void setSortorder(final long value) {
        sortorder = value;
    }

    /**
     * 获取栏目备注信息
     * 
     * @return 返回栏目备注信息
     */
    public byte[] getMemo() {
        return memo;
    }

    /**
     * 设置栏目备注信息
     * 
     * @param value
     *            栏目备注信息
     */
    public void setMemo(final byte[] value) {
        memo = value;
    }

    /**
     * 获取null
     * 
     * @return 返回null
     */
    public Date getCreatedate() {
        return createdate;
    }

    /**
     * 设置null
     * 
     * @param value
     *            null
     */
    public void setCreatedate(final Date value) {
        createdate = value;
    }

    /**
     * 获取是否发布 1 发布 0未发布
     * 
     * @return 返回是否发布 1 发布 0未发布
     */
    public long getPublished() {
        return published;
    }

    /**
     * 设置是否发布 1 发布 0未发布
     * 
     * @param value
     *            是否发布 1 发布 0未发布
     */
    public void setPublished(final long value) {
        published = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("memo", Convert.bytes2Str(memo));
        json.put("published", published);
        json.put("icon", icon);
        return json;
    }
}
