package com.estudio.workflow.utils;

import java.util.ArrayList;
import java.util.List;

import com.estudio.workflow.base.WFActivityType;
import com.estudio.workflow.base.WFVariableDataType;

public class WFUtils {

    /**
     * ����������ת��Ϊ����ʱʵ������
     * 
     * @param designStr
     * @return
     */
    public static WFActivityType designType2WFActivityType(final String designStr) {
        if (designStr.equals("TFlowBeginBlock"))
            return WFActivityType.BEGIN;
        else if (designStr.equals("TFlowEndBlock"))
            return WFActivityType.END;
        return WFActivityType.COMMON;

        /*
         * TFlowBeginBlock TFlowActionBlock TFlowActionBlock TFlowActionBlock
         * TFlowActionBlock TFlowEndBlock
         */
    }

    /**
     * �б��Ƿ��н���
     * 
     * @param ls1
     * @param ls2
     * @return
     */
    public static boolean hasIntersection(final List<Long> ls1, final List<Long> ls2) {
        final List<Long> l = intersection(ls1, ls2);
        return (l != null) && !l.isEmpty();
    }

    /**
     * �󽻼�
     * 
     * @param ls1
     * @param ls2
     * @return
     */
    private static List<Long> intersection(final List<Long> ls1, final List<Long> ls2) {
        final List<Long> result = new ArrayList<Long>();
        for (final Long l : ls1)
            for (final Long l2 : ls2)
                if (l2.equals(l)) {
                    result.add(l);
                    break;
                }
        return result;
    }

    /**
     * �������������ת��Ϊ����ʱ����
     * 
     * @param designStr
     * @return
     */
    public static WFVariableDataType designType2WFDataType(final String designStr) {
        if (designStr.equals("Boolean"))
            return WFVariableDataType.BOOLEAN;
        else if (designStr.equals("Number"))
            return WFVariableDataType.NUMBER;
        else if (designStr.equals("String"))
            return WFVariableDataType.STRING;
        return WFVariableDataType.DATETIME;
    }
}
