package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalGridExSQLBase extends PortalGridExControl {

    private SQLDefineBase selectSQLDefine = null;
    private SQLDefineBase insertSQLDefine = null;
    private SQLDefineBase updateSQLDefine = null;
    private SQLDefineBase deleteSQLDefine = null;
    private SQLDefineBase exchangeSQLDefine = null;
    private SQLDefineBase rootSQLDefine = null;

    private Map<String, String> options = new HashMap<String, String>();
    private List<BindForm> bindForms = new ArrayList<BindForm>();
    private List<String> relationFields = new ArrayList<String>();
    private List<String> childControls = new ArrayList<String>();

    public PortalGridExSQLBase(String controlName, String controlComment, int controlType) {
        super(controlName, controlComment, controlType);
    }

    public SQLDefineBase getSelectSQLDefine() {
        return selectSQLDefine;
    }

    public void setSelectSQLDefine(SQLDefineBase selectSQLDefine) {
        this.selectSQLDefine = selectSQLDefine;
    }

    public SQLDefineBase getInsertSQLDefine() {
        return insertSQLDefine;
    }

    public void setInsertSQLDefine(SQLDefineBase insertSQLDefine) {
        this.insertSQLDefine = insertSQLDefine;
    }

    public SQLDefineBase getUpdateSQLDefine() {
        return updateSQLDefine;
    }

    public void setUpdateSQLDefine(SQLDefineBase updateSQLDefine) {
        this.updateSQLDefine = updateSQLDefine;
    }

    public SQLDefineBase getDeleteSQLDefine() {
        return deleteSQLDefine;
    }

    public void setDeleteSQLDefine(SQLDefineBase deleteSQLDefine) {
        this.deleteSQLDefine = deleteSQLDefine;
    }

    public SQLDefineBase getExchangeSQLDefine() {
        return exchangeSQLDefine;
    }

    public void setExchangeSQLDefine(SQLDefineBase exchangeSQLDefine) {
        this.exchangeSQLDefine = exchangeSQLDefine;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public List<BindForm> getBindForms() {
        return bindForms;
    }

    public List<String> getRelationFields() {
        return relationFields;
    }

    public List<String> getChildControls() {
        return childControls;
    }

    public void registerRelationField(String fieldName) {
        if (!relationFields.contains(fieldName))
            relationFields.add(fieldName);
    }

    public void registerChildControl(String controlName) {
        if (!childControls.contains(controlName))
            childControls.add(controlName);
    }

    public SQLDefineBase getRootSQLDefine() {
        return rootSQLDefine;
    }

    public void setRootSQLDefine(SQLDefineBase rootSQLDefine) {
        this.rootSQLDefine = rootSQLDefine;
    }

}