package com.estudio.define.webclient.form;

public class DSField {

    String name;
    String label;
    boolean primary;
    private final boolean group;
    private final boolean split;
    private final String columnWidth;

    /**
     * 构造函数
     * 
     * @param name
     *            字段名称
     * @param label
     *            字段备注
     * @param primary
     *            是否是主键
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
     * 字段名称
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 字段标签
     * 
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * 是否是主键
     * 
     * @return
     */
    public boolean isPrimary() {
        return primary;
    }

}
