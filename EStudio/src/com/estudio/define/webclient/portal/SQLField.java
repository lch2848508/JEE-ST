package com.estudio.define.webclient.portal;

/**
 * ����SQL�ֶ�
 * 
 * @author Administrator
 * 
 */
public class SQLField {
    String fieldName;
    String fieldLabel;
    String extProp;
    boolean visible;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getExtProp() {
        return extProp;
    }

    public void setExtProp(final String extProp) {
        this.extProp = extProp;
    }

    boolean caption;
    boolean key;
    String dataType;

    public String getDataType() {
        return dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public boolean isCaption() {
        return caption;
    }

    public boolean isKey() {
        return key;
    }

    /**
     * ���캯��
     * 
     * @param fieldName
     *            �ֶ�����
     * @param fieldLabel
     *            �ֶα�ǩ
     * @param caption
     *            �Ƿ��Ǳ���
     * @param key
     *            �Ƿ�������
     */
    public SQLField(final String fieldName, final String fieldLabel, final boolean isCaption, final boolean isKey, final String dataType, boolean isVisible) {
        super();
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        caption = isCaption;
        key = isKey;
        this.dataType = dataType;
        this.visible = isVisible;
    }

}
