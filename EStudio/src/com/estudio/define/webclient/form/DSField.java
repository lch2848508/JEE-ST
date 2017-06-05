package com.estudio.define.webclient.form;

public class DSField {

    String name;
    String label;
    boolean primary;
    private final boolean group;
    private final boolean split;
    private final String columnWidth;

    /**
     * ���캯��
     * 
     * @param name
     *            �ֶ�����
     * @param label
     *            �ֶα�ע
     * @param primary
     *            �Ƿ�������
     * @param columnWidth
     * @param isSplit
     * @param isGroup
     */
    public DSField(final String name, final String label, final boolean primary, final boolean isGroup, final boolean isSplit, final String columnWidth) {
        super();
        this.name = name;
        this.label = label;
        this.primary = primary;
        group = isGroup;
        split = isSplit;
        this.columnWidth = columnWidth;
    }

    public boolean isGroup() {
        return group;
    }

    public boolean isSplit() {
        return split;
    }

    public String getColumnWidth() {
        return columnWidth;
    }

    /**
     * �ֶ�����
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * �ֶα�ǩ
     * 
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * �Ƿ�������
     * 
     * @return
     */
    public boolean isPrimary() {
        return primary;
    }

}
