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
     * ���캯��
     * 
     * @param name
     *            ��������
     * @param label
     *            ����ע��
     * @param dataType
     *            ������������
     * @param initValue
     *            ������ʼֵ
     * @param initDS
     *            ������ʼ����Դ
     * @param initField
     *            ������ʼ�ֶ�
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
     * ������ʼֵ
     * 
     * @return
     */
    public String getInitValue() {
        return initValue;
    }

    /**
     * ������ʼ����Դ
     * 
     * @return
     */
    public String getInitDS() {
        return initDS;
    }

    /**
     * ������Ӧ���ֶ�
     * 
     * @return
     */
    public String getInitField() {
        return initField;
    }
}
