package com.estudio.define.design.user;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class UserInfoRecord extends DBRecord {
    long id; // �û�Ψһ��ʶ��
    String realname; // �û���ʵ����
    String loginname; // �û���¼��
    long sex; // �Ա� 1�� 0Ů -1��ȷ��
    String password; // �û�����
    String mobile; // �ƶ��绰
    String phone; // �绰
    String address; // ͨѶ��ַ
    String postcode; // ��������
    String email; // ��������
    String duty; // ְ��
    String ext1;
    String ext2;
    String ext3;
    byte[] photo; // ��Ƭ
    long pId; // ����ID

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
     * ��ȡ�û�Ψһ��ʶ��
     * 
     * @return �����û�Ψһ��ʶ��
     */
    public long getId() {
        return id;
    }

    /**
     * �����û�Ψһ��ʶ��
     * 
     * @param value
     *            �û�Ψһ��ʶ��
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * ��ȡ�û���ʵ����
     * 
     * @return �����û���ʵ����
     */
    public String getRealname() {
        return realname;
    }

    /**
     * �����û���ʵ����
     * 
     * @param value
     *            �û���ʵ����
     */
    public void setRealname(final String value) {
        realname = value;
    }

    /**
     * ��ȡ�û���¼��
     * 
     * @return �����û���¼��
     */
    public String getLoginname() {
        return loginname;
    }

    /**
     * �����û���¼��
     * 
     * @param value
     *            �û���¼��
     */
    public void setLoginname(final String value) {
        loginname = value;
    }

    /**
     * ��ȡ�Ա� 1�� 0Ů -1��ȷ��
     * 
     * @return �����Ա� 1�� 0Ů -1��ȷ��
     */
    public long getSex() {
        return sex;
    }

    /**
     * �����Ա� 1�� 0Ů -1��ȷ��
     * 
     * @param value
     *            �Ա� 1�� 0Ů -1��ȷ��
     */
    public void setSex(final long value) {
        sex = value;
    }

    /**
     * ��ȡ�û�����
     * 
     * @return �����û�����
     */
    public String getPassword() {
        return password;
    }

    /**
     * �����û�����
     * 
     * @param value
     *            �û�����
     */
    public void setPassword(final String value) {
        password = value;
    }

    /**
     * ��ȡ�ƶ��绰
     * 
     * @return �����ƶ��绰
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * �����ƶ��绰
     * 
     * @param value
     *            �ƶ��绰
     */
    public void setMobile(final String value) {
        mobile = value;
    }

    /**
     * ��ȡ�绰
     * 
     * @return ���ص绰
     */
    public String getPhone() {
        return phone;
    }

    /**
     * ���õ绰
     * 
     * @param value
     *            �绰
     */
    public void setPhone(final String value) {
        phone = value;
    }

    /**
     * ��ȡͨѶ��ַ
     * 
     * @return ����ͨѶ��ַ
     */
    public String getAddress() {
        return address;
    }

    /**
     * ����ͨѶ��ַ
     * 
     * @param value
     *            ͨѶ��ַ
     */
    public void setAddress(final String value) {
        address = value;
    }

    /**
     * ��ȡ��������
     * 
     * @return ������������
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * ������������
     * 
     * @param value
     *            ��������
     */
    public void setPostcode(final String value) {
        postcode = value;
    }

    /**
     * ��ȡ��������
     * 
     * @return ���ص�������
     */
    public String getEmail() {
        return email;
    }

    /**
     * ���õ�������
     * 
     * @param value
     *            ��������
     */
    public void setEmail(final String value) {
        email = value;
    }

    /**
     * ��ȡְ��
     * 
     * @return ����ְ��
     */
    public String getDuty() {
        return duty;
    }

    /**
     * ����ְ��
     * 
     * @param value
     *            ְ��
     */
    public void setDuty(final String value) {
        duty = value;
    }

    /**
     * ��ȡ��Ƭ
     * 
     * @return ������Ƭ
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * ������Ƭ
     * 
     * @param value
     *            ��Ƭ
     */
    public void setPhoto(final byte[] value) {
        photo = value;
    }

    /**
     * ��ȡ����ID
     * 
     * @return ���ز���ID
     */
    public long getPId() {
        return pId;
    }

    /**
     * ���ò���ID
     * 
     * @param value
     *            ����ID
     */
    public void setPId(final long value) {
        pId = value;
    }

    /**
     * @return ����JSON����
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
