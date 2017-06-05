package com.estudio.define.design.portal;

import java.util.Date;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBRecord;
import com.estudio.define.webclient.portal.PortalUtils;

public class PortalItemRecord extends DBRecord {
    long id; // 唯一标识号
    long pId; // 父节点标识号
    String name; // 栏目名称
    long sortorder; // 排列顺序
    long type; // 栏目类别
    String property; // 栏目属性
    long published; // 栏目是否已经发布
    Date createdate; // 创建日期
    long version; // 版本信息
    String icon;
    long win;
    int autorun;
    int disableClose;
    int hidden;

    public int getAutorun() {
        return autorun;
    }

    public void setAutorun(final int autorun) {
        this.autorun = autorun;
    }

    public int getDisableClose() {
        return disableClose;
    }

    public void setDisableClose(final int disableClose) {
        this.disableClose = disableClose;
    }

    public long getWin() {
        return win;
    }

    public void setWin(final long win) {
        this.win = win;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
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
     * 获取父节点标识号
     * 
     * @return 返回父节点标识号
     */
    public long getPId() {
        return pId;
    }

    /**
     * 设置父节点标识号
     * 
     * @param value
     *            父节点标识号
     */
    public void setPId(final long value) {
        pId = value;
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
     * 获取栏目类别
     * 
     * @return 返回栏目类别
     */
    public long getType() {
        return type;
    }

    /**
     * 设置栏目类别
     * 
     * @param value
     *            栏目类别
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * 获取栏目属性
     * 
     * @return 返回栏目属性
     */
    public String getProperty() {
        return property;
    }

    /**
     * 设置栏目属性
     * 
     * @param value
     *            栏目属性
     */
    public void setProperty(final String value) {
        property = value;
    }

    /**
     * 获取栏目是否已经发布
     * 
     * @return 返回栏目是否已经发布
     */
    public long getPublished() {
        return published;
    }

    /**
     * 设置栏目是否已经发布
     * 
     * @param value
     *            栏目是否已经发布
     */
    public void setPublished(final long value) {
        published = value;
    }

    /**
     * 获取创建日期
     * 
     * @return 返回创建日期
     */
    public Date getCreatedate() {
        return createdate;
    }

    /**
     * 设置创建日期
     * 
     * @param value
     *            创建日期
     */
    public void setCreatedate(final Date value) {
        createdate = value;
    }

    /**
     * 获取版本信息
     * 
     * @return 返回版本信息
     */
    public long getVersion() {
        return version;
    }

    /**
     * 设置版本信息
     * 
     * @param value
     *            版本信息
     */
    public void setVersion(final long value) {
        version = value;
    }

    
    
    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("type", type);
        json.put("property", property);
        json.put("published", published);
        json.put("icon", icon);
        json.put("win", win);
        json.put("autorun", autorun == 1);
        json.put("disableclose", disableClose == 1);
        json.put("ishidden", hidden==1);
        final String url = PortalUtils.generalPortalItemURL(type, property, 1, 0, id, RuntimeContext.getPortalGroupService().getDesignEvnParams());
        json.put("url", url);
        // json.put("createdate", createdate);
        // json.put("version", version);
        return json;
    }
}
