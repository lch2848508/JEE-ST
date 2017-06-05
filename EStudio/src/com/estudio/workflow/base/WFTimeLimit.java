package com.estudio.workflow.base;

/**
 * ����������ʱ������
 * 
 * @author ShengHongL
 * 
 */
public class WFTimeLimit {
    private int time; // ʱ������
    private WFTimeUnit unit; // ʱ�����Ƶ�λ

    /**
     * ���캯��
     * 
     * @param time
     *            ʱ������
     * @param unit
     *            ʱ�����Ƶ�λ
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
