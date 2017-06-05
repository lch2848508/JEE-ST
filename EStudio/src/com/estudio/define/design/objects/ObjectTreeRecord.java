package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class ObjectTreeRecord extends DBRecord {
    String memo; // ��ע��Ϣ
    long id; // Ψһ��ʶ��
    String caption; // ����
    long type; // ���� 0 ���ڵ� 1Ŀ¼ 2ҵ�� 3�� 4���� 5��ѯ 6������
    long version; // �汾
    long sortorder; // ����˳��
    long pid; // ���ڵ�id
    long lockby; // ��ǰ������
    long propId; // ����ID

    /**
     * ��ȡ��ע��Ϣ
     * 
     * @return ���ر�ע��Ϣ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * ���ñ�ע��Ϣ
     * 
     * @param value
     *            ��ע��Ϣ
     */
    public void setMemo(final String value) {
        memo = value;
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
     * ��ȡ���� 0 ���ڵ� 1Ŀ¼ 2ҵ�� 3�� 4���� 5��ѯ 6������
     * 
     * @return �������� 0 ���ڵ� 1Ŀ¼ 2ҵ�� 3�� 4���� 5��ѯ 6������
     */
    public long getType() {
        return type;
    }

    /**
     * �������� 0 ���ڵ� 1Ŀ¼ 2ҵ�� 3�� 4���� 5��ѯ 6������
     * 
     * @param value
     *            ���� 0 ���ڵ� 1Ŀ¼ 2ҵ�� 3�� 4���� 5��ѯ 6������
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * ��ȡ�汾
     * 
     * @return ���ذ汾
     */
    public long getVersion() {
        return version;
    }

    /**
     * ���ð汾
     * 
     * @param value
     *            �汾
     */
    public void setVersion(final long value) {
        version = value;
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
     * ��ȡ���ڵ�id
     * 
     * @return ���ظ��ڵ�id
     */
    public long getPid() {
        return pid;
    }

    /**
     * ���ø��ڵ�id
     * 
     * @param value
     *            ���ڵ�id
     */
    public void setPid(final long value) {
        pid = value;
    }

    /**
     * ��ȡ��ǰ������
     * 
     * @return ���ص�ǰ������
     */
    public long getLockby() {
        return lockby;
    }

    /**
     * ���õ�ǰ������
     * 
     * @param value
     *            ��ǰ������
     */
    public void setLockby(final long value) {
        lockby = value;
    }

    /**
     * ��ȡ����ID
     * 
     * @return ��������ID
     */
    public long getPropId() {
        return propId;
    }

    /**
     * ��������ID
     * 
     * @param value
     *            ����ID
     */
    public void setPropId(final long value) {
        propId = value;
    }

    /**
     * @return ����JSON����
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("memo", memo);
        json.put("id", id);
        json.put("caption", caption);
        json.put("lockby", lockby);
        json.put("prop_id", propId);
        return json;
    }
}
