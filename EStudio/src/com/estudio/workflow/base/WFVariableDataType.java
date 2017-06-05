package com.estudio.workflow.base;

/**
 * 工作流变量数据类型
 * 
 * @author ShengHongL
 * 
 */
public enum WFVariableDataType {
    STRING, NUMBER, DATETIME, BOOLEAN;

    /**
     * 转化为整数
     * 
     * @param type
     * @return
     */
    public static int toInt(final WFVariableDataType type) {
        int result = 0;
        switch (type) {
        case STRING:
            result = 0;
            break;
        case NUMBER:
            result = 1;
            break;
        case DATETIME:
            result = 2;
            break;
        case BOOLEAN:
            result = 3;
            break;
        }
        return result;
    }

    /**
     * 数字转化为变量数据类型
     * 
     * @param type
     * @return
     */
    public static WFVariableDataType fromInt(final int type) {
        WFVariableDataType result = STRING;
        switch (type) {
        case 0:
            result = STRING;
            break;
        case 1:
            result = NUMBER;
            break;
        case 2:
            result = DATETIME;
            break;
        case 3:
            result = BOOLEAN;
            break;
        }
        return result;
    }
}
