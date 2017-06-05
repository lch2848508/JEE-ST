package com.estudio.web.servlet;

import java.util.Date;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class LoginByOtherHelperRecord extends DBRecord {
    long id; // 唯一标识符
    long loginUserid; // 登录用户ID
    String uuid; // uuid
    long rndcode; // 登录随机码
    Date regdate; // null
    long isvalid; // 是否有效

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
     * 获取登录用户ID
     * 
     * @return 返回登录用户ID
     */
    public long getLoginUserid() {
        return loginUserid;
    }

    /**
     * 设置登录用户ID
     * 
     * @param value
     *            登录用户ID
     */
    public void setLoginUserid(final long value) {
        loginUserid = value;
    }

    /**
     * 获取uuid
     * 
     * @return 返回uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 设置uuid
     * 
     * @param value
     *            uuid
     */
    public void setUuid(final String value) {
        uuid = value;
    }

    /**
     * 获取登录随机码
     * 
     * @return 返回登录随机码
     */
    public long getRndcode() {
        return rndcode;
    }

    /**
     * 设置登录随机码
     * 
     * @param value
     *            登录随机码
     */
    public void setRndcode(final long value) {
        rndcode = value;
    }

    /**
     * 获取null
     * 
     * @return 返回null
     */
    public Date getRegdate() {
        return regdate;
    }

    /**
     * 设置null
     * 
     * @param value
     *            null
     */
    public void setRegdate(final Date value) {
        regdate = value;
    }

    /**
     * 获取是否有效
     * 
     * @return 返回是否有效
     */
    public long getIsvalid() {
        return isvalid;
    }

    /**
     * 设置是否有效
     * 
     * @param value
     *            是否有效
     */
    public void setIsvalid(final long value) {
        isvalid = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("uuid", uuid);
        json.put("rndcode", rndcode);
        return json;
    }
}
