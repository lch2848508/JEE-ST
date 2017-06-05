package com.estudio.define.webclient;

import org.apache.commons.lang3.StringUtils;

import com.estudio.define.db.SQLParam;
import com.estudio.intf.db.DBParamDataType;

public class SQLParam4Portal extends SQLParam {
    String initField;
    String initValue;
    String initSQLName;

    boolean filter;
    String filterControl;
    String addition;
    boolean fromDB;
    String comboBoxItems;
    String comboBoxDB;
    String paramValueSQL = "";

    int pos = 1;
    int controlWidth;
    
    public int getControlWidth() {
        return controlWidth;
    }

    private boolean skipNull;

    public boolean isSkipNull() {
        return skipNull;
    }

    public void setSkipNull(boolean skipNull) {
        this.skipNull = skipNull;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(final int pos) {
        this.pos = pos;
    }

    public boolean isFromDB() {
        return fromDB;
    }

    public boolean isFormDB() {
        return fromDB;
    }

    public String getComboBoxItems() {
        return comboBoxItems;
    }

    public String getComboBoxDB() {
        return comboBoxDB;
    }

    public String getInitField() {
        return initField;
    }

    public String getInitValue() {
        return initValue;
    }

    public String getInitSQLName() {
        return initSQLName;
    }

    public boolean isFilter() {
        return filter;
    }

    public String getFilterControl() {
        return filterControl;
    }

    public String getAddition() {
        return addition;
    }

    public String getParamValueSQL() {
        return paramValueSQL;
    }

    public void setParamValueSQL(String paramValueSQL) {
        this.paramValueSQL = paramValueSQL;
    }

    /**
     * ¹¹Ôìº¯Êý
     * 
     * @param paramName
     * @param paramLabel
     * @param initField
     * @param initValue
     * @param dataType
     * @param filter
     * @param filterControl
     * @param addition
     */
    public SQLParam4Portal(final String paramName, final String paramLabel, final DBParamDataType dataType, final String initFieldStr, final String initValue, final boolean filter, final String filterControl, final String addition, final boolean isFormDB, final String comboBoxItems, final String comboBoxDB, final boolean isWholeWord, final int pos, final boolean isSkipNull, String paramValueSQL,int controlWidth) {
        super(paramName, paramLabel, dataType);
        name = paramName;
        label = paramLabel;
        String initField = initFieldStr;
        if (!StringUtils.isEmpty(initField)) {
            initField = StringUtils.substringBetween(initField, "[", "]");
            initSQLName = StringUtils.substringBefore(initField, ".");
            this.initField = StringUtils.substringAfter(initField, ".");
        }
        if (!StringUtils.isEmpty(initValue))
            this.initValue = StringUtils.substringBetween(initValue, "[", "]");
        this.filter = filter;
        this.filterControl = filterControl;
        this.addition = addition;
        fromDB = isFormDB;
        this.comboBoxItems = comboBoxItems;
        partMatch = !isWholeWord;
        if (!StringUtils.isEmpty(comboBoxDB))
            this.comboBoxDB = StringUtils.substringBetween(comboBoxDB, "[", "]");
        this.pos = pos;
        this.skipNull = isSkipNull;
        this.paramValueSQL = paramValueSQL;
        this.controlWidth = controlWidth;
    }

}
