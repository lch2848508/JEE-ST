package com.estudio.define.design.user;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class UserInfoRecord extends DBRecord {
    long id; // 用户唯一标识号
    String realname; // 用户真实姓名
    String loginname; // 用户登录名
    long sex; // 性别 1男 0女 -1不确定
    String password; // 用户密码
    String mobile; // 移动电话
    String phone; // 电话
    String address; // 通讯地址
    String postcode; // 邮政编码
    String email; // 电子邮箱
    String duty; // 职务
    String ext1;
    String ext2;
    String ext3;
    byte[] photo; // 照片
    long pId; // 部门ID

    public String getExt1() {
        return ext1;
    }

    public void setExt1(final String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(final String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(final String ext3) {
        this.ext3 = ext3;
    }

    /**
     * 获取用户唯一标识号
     * 
     * @return 返回用户唯一标识号
     */
    public long getId() {
        return id;
    }

    /**
     * 设置用户唯一标识号
     * 
     * @param value
     *            用户唯一标识号
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取用户真实姓名
     * 
     * @return 返回用户真实姓名
     */
    public String getRealname() {
        return realname;
    }

    /**
     * 设置用户真实姓名
     * 
     * @param value
     *            用户真实姓名
     */
    public void setRealname(final String value) {
        realname = value;
    }

    /**
     * 获取用户登录名
     * 
     * @return 返回用户登录名
     */
    public String getLoginname() {
        return loginname;
    }

    /**
     * 设置用户登录名
     * 
     * @param value
     *            用户登录名
     */
    public void setLoginname(final String value) {
        loginname = value;
    }

    /**
     * 获取性别 1男 0女 -1不确定
     * 
     * @return 返回性别 1男 0女 -1不确定
     */
    public long getSex() {
        return sex;
    }

    /**
     * 设置性别 1男 0女 -1不确定
     * 
     * @param value
     *            性别 1男 0女 -1不确定
     */
    public void setSex(final long value) {
        sex = value;
    }

    /**
     * 获取用户密码
     * 
     * @return 返回用户密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户密码
     * 
     * @param value
     *            用户密码
     */
    public void setPassword(final String value) {
        password = value;
    }

    /**
     * 获取移动电话
     * 
     * @return 返回移动电话
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置移动电话
     * 
     * @param value
     *            移动电话
     */
    public void setMobile(final String value) {
        mobile = value;
    }

    /**
     * 获取电话
     * 
     * @return 返回电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置电话
     * 
     * @param value
     *            电话
     */
    public void setPhone(final String value) {
        phone = value;
    }

    /**
     * 获取通讯地址
     * 
     * @return 返回通讯地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置通讯地址
     * 
     * @param value
     *            通讯地址
     */
    public void setAddress(final String value) {
        address = value;
    }

    /**
     * 获取邮政编码
     * 
     * @return 返回邮政编码
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * 设置邮政编码
     * 
     * @param value
     *            邮政编码
     */
    public void setPostcode(final String value) {
        postcode = value;
    }

    /**
     * 获取电子邮箱
     * 
     * @return 返回电子邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置电子邮箱
     * 
     * @param value
     *            电子邮箱
     */
    public void setEmail(final String value) {
        email = value;
    }

    /**
     * 获取职务
     * 
     * @return 返回职务
     */
    public String getDuty() {
        return duty;
    }

    /**
     * 设置职务
     * 
     * @param value
     *            职务
     */
    public void setDuty(final String value) {
        duty = value;
    }

    /**
     * 获取照片
     * 
     * @return 返回照片
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * 设置照片
     * 
     * @param value
     *            照片
     */
    public void setPhoto(final byte[] value) {
        photo = value;
    }

    /**
     * 获取部门ID
     * 
     * @return 返回部门ID
     */
    public long getPId() {
        return pId;
    }

    /**
     * 设置部门ID
     * 
     * @param value
     *            部门ID
     */
    public void setPId(final long value) {
        pId = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("realname", realname);
        json.put("loginname", loginname);
        json.put("sex", sex);
        json.put("password", password);
        json.put("mobile", mobile);
        json.put("phone", phone);
        json.put("address", address);
        json.put("postcode", postcode);
        json.put("email", email);
        json.put("duty", duty);
        json.put("photo", "url:/usermanager/userinfo?id=" + id);
        json.put("pid", pId);
        json.put("ext1", ext1);
        json.put("ext2", ext2);
        json.put("ext3", ext3);
        return json;
    }
}
