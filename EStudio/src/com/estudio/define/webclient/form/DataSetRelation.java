package com.estudio.define.webclient.form;

/**
 * �������DataSet�ĸ��ӹ�ϵ
 * 
 * @author Administrator
 * 
 */
public class DataSetRelation {
    String parentDataSet;
    String parentFieldName;
    String linkFieldName;
    String initDataSet;
    String initFieldName;

    public String getParentDataSet() {
        return parentDataSet;
    }

    public void setParentDataSet(final String parentDataSet) {
        this.parentDataSet = parentDataSet;
    }

    public String getParentFieldName() {
        return parentFieldName;
    }

    public void setParentFieldName(final String parentFieldName) {
        this.parentFieldName = parentFieldName;
    }

    public String getLinkFieldName() {
        return linkFieldName;
    }

    public void setLinkFieldName(final String linkFieldName) {
        this.linkFieldName = linkFieldName;
    }

    /**
     * ���캯��
     * 
     * @param parentDataSet
     * @param parentFieldName
     * @param linkFieldName
     * @param initDataSet
     * @param initFieldName
     */
    public DataSetRelation(final String parentDataSet, final String parentFieldName, final String linkFieldName, final String initDataSet, final String initFieldName) {
        super();
        this.parentDataSet = parentDataSet;
        this.parentFieldName = parentFieldName;
        this.linkFieldName = linkFieldName;
        this.initDataSet = initDataSet;
        this.initFieldName = initFieldName;
    }

    public String getInitDataSet() {
        return initDataSet;
    }

    public String getInitFieldName() {
        return initFieldName;
    }

}
