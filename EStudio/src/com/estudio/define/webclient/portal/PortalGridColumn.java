package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.List;

public class PortalGridColumn {
    String fieldName;
    String fieldLabel;
    String alignment;
    String width;
    boolean readonlyAble;
    boolean fixedWidth;
    boolean special;
    boolean editor;
    boolean checkBox;
    String style;

    public boolean isCheckBox() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    List<SQLColumnURL> urlList = new ArrayList<SQLColumnURL>();

    /**
     * ���ӳ�����
     * 
     * @param url
     */
    public void addURL(final SQLColumnURL url) {
        urlList.add(url);
    }

    /**
     * �õ�����������
     * 
     * @return
     */
    public long getURLCount() {
        return urlList.size();
    }

    /**
     * ��������ȡ�ó�����
     * 
     * @param index
     * @return
     */
    public SQLColumnURL getURL(final int index) {
        return urlList.get(index);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public String getWidth() {
        return width;
    }

    public boolean isReadonlyAble() {
        return readonlyAble;
    }

    /**
     * ���캯��
     * 
     * @param fieldName
     *            �ֶ���
     * @param fieldLabel
     *            �ֶα���
     * @param width
     *            �п��
     * @param alignment
     * @param canFilter
     *            ���Ƿ��������
     * @param editColumn
     *            �Ƿ��Ǳ༭��
     */
    public PortalGridColumn(final String fieldName, final String fieldLabel, final String width, boolean isFixedWidth, final String alignment, final String style, final boolean editColumn) {
        super();
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.width = width;
        this.readonlyAble = editColumn;
        this.alignment = alignment;
        this.style = style;
        this.fixedWidth = isFixedWidth;
    }

    public PortalGridColumn(String fieldName, String fieldLabel) {
        super();
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
    }

    public String getAlignment() {
        return alignment;
    }

    public String getStyle() {
        return style;
    }

    public void setFixedWidth(final boolean value) {
        fixedWidth = value;
    }

    public boolean getFixedWidth() {
        return fixedWidth;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(final boolean special) {
        this.special = special;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }

}
