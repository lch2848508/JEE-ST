package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.List;

public class PortalGridExGrid extends PortalGridExSQLBase {

    private boolean groupAble = false;
    private String groupField = "";
    private boolean pagination = false;

    public boolean isGroupAble() {
        return groupAble;
    }

    public void setGroupAble(boolean groupAble) {
        this.groupAble = groupAble;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public boolean isPagination() {
        return pagination;
    }

    public void setPagination(boolean pagination) {
        this.pagination = pagination;
    }

    /**
     * ���캯��
     * 
     * @param controlName
     * @param controlComment
     * @param controlType
     */
    public PortalGridExGrid(String controlName, String controlComment, int controlType) {
        super(controlName, controlComment, controlType);
    }

    // ����������
    private List<PortalGridExColumn> columns = new ArrayList<PortalGridExColumn>();

    // �������ֶ�
    private List<String> columnFields = new ArrayList<String>();

    // ���������ֶ�
    private List<String> noColumnFields = new ArrayList<String>();

    /**
     * ��ȡ������
     * 
     * @return
     */
    public List<PortalGridExColumn> getColumns() {
        return columns;
    }

    public List<String> getColumnFields() {
        return columnFields;
    }

    public List<String> getNoColumnFields() {
        return noColumnFields;
    }

    /**
     * �Ƿ����������
     * 
     * @param fieldName
     * @return
     */
    public boolean isExistsColumnByFieldName(String fieldName) {
        return columnFields.contains(fieldName);
    }

    /**
     * �����������ֶ��б�
     */
    public void generalNoColumnFields() {
        for (SQLField field : getSelectSQLDefine().fieldList) {
            String fieldName = field.getFieldName();
            if (!isExistsColumnByFieldName(fieldName))
                noColumnFields.add(fieldName);
        }
    }
}
