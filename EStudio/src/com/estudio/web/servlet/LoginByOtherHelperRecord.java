package com.estudio.web.servlet;

import java.util.Date;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class LoginByOtherHelperRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    long loginUserid; // ��¼�û�ID
    String uuid; // uuid
    long rndcode; // ��¼�����
    Date regdate; // null
    long isvalid; // �Ƿ���Ч

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
     * ��ȡ��¼�û�ID
     * 
     * @return ���ص�¼�û�ID
     */
    public long getLoginUserid() {
        return loginUserid;
    }

    /**
     * ���õ�¼�û�ID
     * 
     * @param value
     *            ��¼�û�ID
     */
    public void setLoginUserid(final long value) {
        loginUserid = value;
    }

    /**
     * ��ȡuuid
     * 
     * @return ����uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * ����uuid
     * 
     * @param value
     *            uuid
     */
    public void setUuid(final String value) {
        uuid = value;
    }

    /**
     * ��ȡ��¼�����
     * 
     * @return ���ص�¼�����
     */
    public long getRndcode() {
        return rndcode;
    }

    /**
     * ���õ�¼�����
     * 
     * @param value
     *            ��¼�����
     */
    public void setRndcode(final long value) {
        rndcode = value;
    }

    /**
     * ��ȡnull
     * 
     * @return ����null
     */
    public Date getRegdate() {
        return regdate;
    }

    /**
     * ����null
     * 
     * @param value
     *            null
     */
    public void setRegdate(final Date value) {
        regdate = value;
    }

    /**
     * ��ȡ�Ƿ���Ч
     * 
     * @return �����Ƿ���Ч
     */
    public long getIsvalid() {
        return isvalid;
    }

    /**
     * �����Ƿ���Ч
     * 
     * @param value
     *            �Ƿ���Ч
     */
    public void setIsvalid(final long value) {
        isvalid = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("uuid", uuid);
        json.put("rndcode", rndcode);
        return json;
    }
}
