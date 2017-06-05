package com.estudio.workflow.base;

/**
 * 工作流办文时限设置
 * 
 * @author ShengHongL
 * 
 */
public class WFTimeLimit {
    private int time; // 时间限制
    private WFTimeUnit unit; // 时间限制单位

    /**
     * 构造函数
     * 
     * @param time
     *            时间限制
     * @param unit
     *            时间限制单位
     */

    public WFTimeLimit(final int time, final WFTimeUnit unit) {
        super();
        this.time = time;
        this.unit = unit;
    }

    public int getTime() {
        return time;
    }

    public void setTime(final int time) {
        this.time = time;
    }

    public WFTimeUnit getUnit() {
        return unit;
    }

    public void setUnit(final WFTimeUnit unit) {
        this.unit = unit;
    }

}
