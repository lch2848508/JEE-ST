package com.estudio.define.utils;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class CodeAssistRecord extends DBRecord {
    long id; // Ψһ��ʶ��
    String caption; // ����
    String content; // ����
    long pid; // ���ڵ�ID �������̶���� -1:javascript -2:SQL -3:JAVA
    String help; // ������Ϣ
    long sortorder; // ����˳��
    long type; // 0:���ͷ��� 1:���� 3:����Ƭ��
    String extType;

    public String getExtType() {
        return extType;
    }

    public void setExtType(final String extType) {
        this.extType = extType;
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
     * ��ȡ����
     * 
     * @return ���ر���
     */
    public String getCaption() {
        return caption;
    }

    /**
     * ���ñ���
     * 
     * @param value
     *            ����
     */
    public void setCaption(final String value) {
        caption = value;
    }

    /**
     * ��ȡ����
     * 
     * @return ��������
     */
    public String getContent() {
        return content;
    }

    /**
     * ��������
     * 
     * @param value
     *            ����
     */
    public void setContent(final String value) {
        content = value;
    }

    /**
     * ��ȡ���ڵ�ID �������̶���� -1:javascript -2:SQL -3:JAVA
     * 
     * @return ���ظ��ڵ�ID �������̶���� -1:javascript -2:SQL -3:JAVA
     */
    public long getPid() {
        return pid;
    }

    /**
     * ���ø��ڵ�ID �������̶���� -1:javascript -2:SQL -3:JAVA
     * 
     * @param value
     *            ���ڵ�ID �������̶���� -1:javascript -2:SQL -3:JAVA
     */
    public void setPid(final long value) {
        pid = value;
    }

    /**
     * ��ȡ������Ϣ
     * 
     * @return ���ذ�����Ϣ
     */
    public String getHelp() {
        return help;
    }

    /**
     * ���ð�����Ϣ
     * 
     * @param value
     *            ������Ϣ
     */
    public void setHelp(final String value) {
        help = value;
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
     * ��ȡ0:���ͷ��� 1:���� 3:����Ƭ��
     * 
     * @return ����0:���ͷ��� 1:���� 3:����Ƭ��
     */
    public long getType() {
        return type;
    }

    /**
     * ����0:���ͷ��� 1:���� 3:����Ƭ��
     * 
     * @param value
     *            0:���ͷ��� 1:���� 3:����Ƭ��
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * @return ����JSON����
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
