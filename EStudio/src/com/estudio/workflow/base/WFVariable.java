package com.estudio.workflow.base;

/**
 * ��������������
 * 
 * @author ShengHongL
 * 
 */
public class WFVariable {
    private String name; // ��������
    private String descript;// ����������Ϣ
    private WFVariableDataType dataType; // ������������

    /**
     * ���캯��
     * 
     * @param name
     * @param descript
     * @param dataType
     */
    public WFVariable(final String name, final String descript, final WFVariableDataType dataType) {
        super();
        this.name = name;
        this.descript = descript;
        this.dataType = dataType;
    }

    public WFVariable() {
        super();
    }

    /**
     * ���캯��
     * 
     * @param name
     * @param descript
     * @param dataType
     */
    public WFVariable(final String name, final String descript, final int dataType) {
        super();
        this.name = name;
        this.descript = descript;
        this.dataType = WFVariableDataType.fromInt(dataType);
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(final String descript) {
        this.descript = descript;
    }

    public WFVariableDataType getDataType() {
        return dataType;
    }

    public void setDataType(final WFVariableDataType dataType) {
        this.dataType = dataType;
    }

}
