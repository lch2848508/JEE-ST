package com.estudio.workflow.base;

import org.apache.commons.lang3.StringUtils;

/**
 * ����ʱ�޵�λ G������ D��Ȼ��
 * 
 * @author ShengHongL
 * 
 */
public enum WFTimeUnit {
    WORKDAY, // ������
    DAY, // ��Ȼ��
    WORKHOUR, HOUR, WORKMINUTE, MINUTE;

    /**
     * ת��Ϊ�����ַ���
     * 
     * @param unit
     * @return
     */
    public static String toChineseStr(final WFTimeUnit unit) {
        String result = "";
        if (unit == WORKDAY)
            result = "������";
        else if (unit == DAY)
            result = "��Ȼ��";
        else if (unit == WORKHOUR)
            result = "����ʱ";
        else if (unit == HOUR)
            result = "��Ȼʱ";
        else if (unit == WORKMINUTE)
            result = "������";
        else if (unit == MINUTE)
            result = "��Ȼ��";
        return result;
    }

    /**
     * �����ַ���ת��Ϊ����
     * 
     * @param str
     * @return
     */
    public static WFTimeUnit fromChineseStr(final String str) {
        WFTimeUnit result = WFTimeUnit.WORKDAY;
        if (StringUtils.equals(str, "������"))
            result = WORKDAY;
        else if (StringUtils.equals(str, "��Ȼ��"))
            result = DAY;
        else if (StringUtils.equals(str, "����ʱ"))
            result = WORKHOUR;
        else if (StringUtils.equals(str, "��Ȼʱ"))
            result = HOUR;
        else if (StringUtils.equals(str, "������"))
            result = WORKMINUTE;
        else if (StringUtils.equals(str, "��Ȼ��"))
            result = MINUTE;
        return result;
    }

}
