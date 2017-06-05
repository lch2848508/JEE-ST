package com.estudio.workflow.base;

/**
 * 工作流变量定义
 * 
 * @author ShengHongL
 * 
 */
public class WFVariable {
    private String name; // 变量名称
    private String descript;// 变量描述信息
    private WFVariableDataType dataType; // 变量数据类型

    /**
     * 构造函数
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
     * 构造函数
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
