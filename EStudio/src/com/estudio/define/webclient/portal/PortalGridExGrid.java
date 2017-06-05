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
     * 构造函数
     * 
     * @param controlName
     * @param controlComment
     * @param controlType
     */
    public PortalGridExGrid(String controlName, String controlComment, int controlType) {
        super(controlName, controlComment, controlType);
    }

    // 定义数据列
    private List<PortalGridExColumn> columns = new ArrayList<PortalGridExColumn>();

    // 数据列字段
    private List<String> columnFields = new ArrayList<String>();

    // 非数据列字段
    private List<String> noColumnFields = new ArrayList<String>();

    /**
     * 获取数据列
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
     * 是否存在数据列
     * 
     * @param fieldName
     * @return
     */
    public boolean isExistsColumnByFieldName(String fieldName) {
        return columnFields.contains(fieldName);
    }

    /**
     * 产生孤立的字段列表
     */
    public void generalNoColumnFields() {
        for (SQLField field : getSelectSQLDefine().fieldList) {
            String fieldName = field.getFieldName();
            if (!isExistsColumnByFieldName(fieldName))
                noColumnFields.add(fieldName);
        }
    }
}
