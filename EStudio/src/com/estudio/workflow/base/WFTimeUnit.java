package com.estudio.workflow.base;

import org.apache.commons.lang3.StringUtils;

/**
 * 办文时限单位 G工作日 D自然日
 * 
 * @author ShengHongL
 * 
 */
public enum WFTimeUnit {
    WORKDAY, // 工作日
    DAY, // 自然日
    WORKHOUR, HOUR, WORKMINUTE, MINUTE;

    /**
     * 转化为中文字符串
     * 
     * @param unit
     * @return
     */
    public static String toChineseStr(final WFTimeUnit unit) {
        String result = "";
        if (unit == WORKDAY)
            result = "工作日";
        else if (unit == DAY)
            result = "自然日";
        else if (unit == WORKHOUR)
            result = "工作时";
        else if (unit == HOUR)
            result = "自然时";
        else if (unit == WORKMINUTE)
            result = "工作分";
        else if (unit == MINUTE)
            result = "自然分";
        return result;
    }

    /**
     * 中文字符串转化为类型
     * 
     * @param str
     * @return
     */
    public static WFTimeUnit fromChineseStr(final String str) {
        WFTimeUnit result = WFTimeUnit.WORKDAY;
        if (StringUtils.equals(str, "工作日"))
            result = WORKDAY;
        else if (StringUtils.equals(str, "自然日"))
            result = DAY;
        else if (StringUtils.equals(str, "工作时"))
            result = WORKHOUR;
        else if (StringUtils.equals(str, "自然时"))
            result = HOUR;
        else if (StringUtils.equals(str, "工作分"))
            result = WORKMINUTE;
        else if (StringUtils.equals(str, "自然分"))
            result = MINUTE;
        return result;
    }

}
