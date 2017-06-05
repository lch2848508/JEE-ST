package com.estudio.define.design.portal;

import java.util.Date;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;
import com.estudio.utils.Convert;

public class PortalGroupRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    String name; // ��Ŀ����
    long sortorder; // ��Ŀ˳��
    byte[] memo; // ��Ŀ��ע��Ϣ
    Date createdate; // null
    long published; // �Ƿ񷢲� 1 ���� 0δ����
    String icon;

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
     * ��ȡ��Ŀ˳��
     * 
     * @return ������Ŀ˳��
     */
    public long getSortorder() {
        return sortorder;
    }

    /**
     * ������Ŀ˳��
     * 
     * @param value
     *            ��Ŀ˳��
     */
    public void setSortorder(final long value) {
        sortorder = value;
    }

    /**
     * ��ȡ��Ŀ��ע��Ϣ
     * 
     * @return ������Ŀ��ע��Ϣ
     */
    public byte[] getMemo() {
        return memo;
    }

    /**
     * ������Ŀ��ע��Ϣ
     * 
     * @param value
     *            ��Ŀ��ע��Ϣ
     */
    public void setMemo(final byte[] value) {
        memo = value;
    }

    /**
     * ��ȡnull
     * 
     * @return ����null
     */
    public Date getCreatedate() {
        return createdate;
    }

    /**
     * ����null
     * 
     * @param value
     *            null
     */
    public void setCreatedate(final Date value) {
        createdate = value;
    }

    /**
     * ��ȡ�Ƿ񷢲� 1 ���� 0δ����
     * 
     * @return �����Ƿ񷢲� 1 ���� 0δ����
     */
    public long getPublished() {
        return published;
    }

    /**
     * �����Ƿ񷢲� 1 ���� 0δ����
     * 
     * @param value
     *            �Ƿ񷢲� 1 ���� 0δ����
     */
    public void setPublished(final long value) {
        published = value;
    }

    /**
     * @return ����JSON����
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
