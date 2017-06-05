package com.estudio.workflow.base;

/**
 * ���ؼ�����
 * 
 * @author ShengHongL
 * 
 */
public class WFFormSettingItem {
    String name; // �ؼ�����
    boolean hidden; // �Ƿ�����
    boolean readonly; // �Ƿ�ֻ��
    boolean require; // �Ƿ����

    /**
     * ���캯��
     * 
     * @param name
     *            �ؼ�����
     * @param hidden
     *            �Ƿ�����
     * @param readonly
     *            �Ƿ�ֻ��
     */
    public WFFormSettingItem(final String name, final boolean hidden, final boolean readonly, final boolean require) {
        super();
        this.name = name;
        this.hidden = hidden;
        this.readonly = readonly;
        this.require = require;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(final boolean require) {
        this.require = require;
    }

}
