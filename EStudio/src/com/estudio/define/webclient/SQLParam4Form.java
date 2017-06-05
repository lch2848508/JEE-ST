package com.estudio.define.webclient;

import org.apache.commons.lang3.StringUtils;

import com.estudio.define.db.SQLParam;
import com.estudio.intf.db.DBParamDataType;

public class SQLParam4Form extends SQLParam {

    String initValue;
    String initDS;
    String initField;
    String controlType;
    String controlAddition;
    String defaultValue;
    int controlWidth;
    int controlPos;
    private final String firstInitDS;
    private final String firstInitField;

    public int getControlWidth() {
        return controlWidth;
    }

    public int getControlPos() {
        return controlPos;
    }

    public String getFirstInitDS() {
        return firstInitDS;
    }

    public String getFirstInitField() {
        return firstInitField;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(final String controlType) {
        this.controlType = controlType;
    }

    public String getControlAddition() {
        return controlAddition;
    }

    public void setControlAddition(final String controlAddition) {
        this.controlAddition = controlAddition;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 构造函数
     * 
     * @param name
     *            参数名称
     * @param label
     *            参数注释
     * @param dataType
     *            参数数据类型
     * @param initValue
     *            参数初始值
     * @param initDS
     *            参数初始数据源
     * @param initField
     *            参数初始字段
     */
    public SQLParam4Form(final String name, final String label, final DBParamDataType dataType, final String initDS, final String initField, final String controlType, final String controlAddition, final String defaultValue, final String firstInitDataSource, final String firstInitFieldName, final boolean isWholeWord, final int controlWidth, final int controlPos) {
        super(name, label, dataType);
        this.initDS = initDS;
        this.initField = initField;
        if (StringUtils.isEmpty(this.initDS))
            initValue = this.initField;
        this.controlType = controlType;
        this.controlAddition = controlAddition;
        this.defaultValue = defaultValue;
        firstInitDS = firstInitDataSource;
        firstInitField = firstInitFieldName;
        partMatch = !isWholeWord;
        this.controlWidth = controlWidth;
        this.controlPos = controlPos;
    }

    /**
     * 参数初始值
     * 
     * @return
     */
    public String getInitValue() {
        return initValue;
    }

    /**
     * 参数初始数据源
     * 
     * @return
     */
    public String getInitDS() {
        return initDS;
    }

    /**
     * 参数对应的字段
     * 
     * @return
     */
    public String getInitField() {
        return initField;
    }
}
