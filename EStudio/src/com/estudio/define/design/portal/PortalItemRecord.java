package com.estudio.define.design.portal;

import java.util.Date;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBRecord;
import com.estudio.define.webclient.portal.PortalUtils;

public class PortalItemRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    long pId; // ���ڵ��ʶ��
    String name; // ��Ŀ����
    long sortorder; // ����˳��
    long type; // ��Ŀ���
    String property; // ��Ŀ����
    long published; // ��Ŀ�Ƿ��Ѿ�����
    Date createdate; // ��������
    long version; // �汾��Ϣ
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
     * ��ȡΨһ��ʶ��
     * 
     * @return ����Ψһ��ʶ��
     */
    public long getId() {
        return id;
    }

    /**
     * ����Ψһ��ʶ��
     * 
     * @param value
     *            Ψһ��ʶ��
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * ��ȡ���ڵ��ʶ��
     * 
     * @return ���ظ��ڵ��ʶ��
     */
    public long getPId() {
        return pId;
    }

    /**
     * ���ø��ڵ��ʶ��
     * 
     * @param value
     *            ���ڵ��ʶ��
     */
    public void setPId(final long value) {
        pId = value;
    }

    /**
     * ��ȡ��Ŀ����
     * 
     * @return ������Ŀ����
     */
    public String getName() {
        return name;
    }

    /**
     * ������Ŀ����
     * 
     * @param value
     *            ��Ŀ����
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * ��ȡ����˳��
     * 
     * @return ��������˳��
     */
    public long getSortorder() {
        return sortorder;
    }

    /**
     * ��������˳��
     * 
     * @param value
     *            ����˳��
     */
    public void setSortorder(final long value) {
        sortorder = value;
    }

    /**
     * ��ȡ��Ŀ���
     * 
     * @return ������Ŀ���
     */
    public long getType() {
        return type;
    }

    /**
     * ������Ŀ���
     * 
     * @param value
     *            ��Ŀ���
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * ��ȡ��Ŀ����
     * 
     * @return ������Ŀ����
     */
    public String getProperty() {
        return property;
    }

    /**
     * ������Ŀ����
     * 
     * @param value
     *            ��Ŀ����
     */
    public void setProperty(final String value) {
        property = value;
    }

    /**
     * ��ȡ��Ŀ�Ƿ��Ѿ�����
     * 
     * @return ������Ŀ�Ƿ��Ѿ�����
     */
    public long getPublished() {
        return published;
    }

    /**
     * ������Ŀ�Ƿ��Ѿ�����
     * 
     * @param value
     *            ��Ŀ�Ƿ��Ѿ�����
     */
    public void setPublished(final long value) {
        published = value;
    }

    /**
     * ��ȡ��������
     * 
     * @return ���ش�������
     */
    public Date getCreatedate() {
        return createdate;
    }

    /**
     * ���ô�������
     * 
     * @param value
     *            ��������
     */
    public void setCreatedate(final Date value) {
        createdate = value;
    }

    /**
     * ��ȡ�汾��Ϣ
     * 
     * @return ���ذ汾��Ϣ
     */
    public long getVersion() {
        return version;
    }

    /**
     * ���ð汾��Ϣ
     * 
     * @param value
     *            �汾��Ϣ
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
     * @return ����JSON����
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
