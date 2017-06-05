package com.estudio.intf.db;

// 运行有返回值存储过程的参数
public class CallableStmtParamDefineAndValue extends CallableStmtParamDefine {
    public Object value; // 参数值

    /**
     * 构造函数
     * 
     * @param value
     *            参数值
     * @param type
     *            数据类型
     * @param isOutput
     *            是否返回值
     */
    public CallableStmtParamDefineAndValue(final Object value, final DBParamDataType type, final boolean isOutput) {
        super();
        this.value = value;
        this.type = type;
        this.isOutput = isOutput;
    }

    /**
     * 构造函数
     * 
     * @param value
     *            参数值
     * @param type
     *            数据类型
     */
    public CallableStmtParamDefineAndValue(final Object value, final DBParamDataType type) {
        super();
        this.value = value;
        this.type = type;
    }

}
