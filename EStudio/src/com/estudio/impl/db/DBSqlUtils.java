package com.estudio.impl.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class DBSqlUtils {
    // �Ƿ��Ǳ�ʾ�ַ�
    private static boolean isIdientChar(final char c) {
        return ((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || (c == '_') || (c == '$') || (c == '#') || ((c >= 128) && (c <= 256));
    }

    /**
     * ParserSQLParam ����SQL����еĲ��� ͬʱ�������������뵽��������
     * 
     * @param arrayList
     */

    public static void parserSQLParam(final String sql, final Map<String, List<String>> paramsMap, final List<String> arrayList, final List<String> extParamsList) {
        final String fullSQL = sql + "\n";
        char Mode = 'S';
        String VarName = "";
        final long SQLLength = fullSQL.length();
        int Index = 1;

        char C = 0;
        for (int i = 0; i < SQLLength; i++) {
            C = fullSQL.charAt(i);
            switch (Mode) {
            case 'S':
                if ((C == ':') && (fullSQL.charAt(i + 1) != '=')) {
                    Mode = 'V';
                    VarName = "";
                } else if (C == '\'')
                    Mode = 'Q';
                else if ((C == '/') && (fullSQL.charAt(i + 1) == '*'))
                    Mode = 'C';
                else if ((C == '-') && (fullSQL.charAt(i + 1) == '-'))
                    Mode = 'c';
                /*
                 * else if (C == '@') { Mode = 'E'; VarName = "@"; }
                 */
                break;
            case 'V':
                if ((C <= 255) && !DBSqlUtils.isIdientChar(C)) {
                    if (!VarName.equals("")) {
                        DBSqlUtils.registerParam(paramsMap, VarName, Index, arrayList);
                        Index++;
                        Mode = 'S';
                    }
                } else VarName += C;
                break;
            case 'E':
                if ((C <= 255) && !DBSqlUtils.isIdientChar(C)) {
                    if (!VarName.equals("")) {
                        extParamsList.add(VarName);
                        Mode = 'S';
                    }
                } else VarName += C;
                break;
            case 'c':
                if (C == '\n')
                    Mode = 'S';
                break;
            case 'C':
                if ((C == '*') && (fullSQL.charAt(i + 1) == '/'))
                    Mode = 'S';
                break;
            case 'Q':
                if (C == '\'')
                    Mode = 'S';
                break;
            }
        }
    }

    /**
     * registerParam ע����������λ��
     * 
     * @param paramsMap
     *            ������ֵ��Ӧ��ϵ
     * @param paramName
     *            ������
     * @param Index
     *            ����λ��
     * @param arrayList
     */

    private static void registerParam(final Map<String, List<String>> paramsMap, final String paramName, final int Index, final List<String> arrayList) {
        final String paramNameLower = paramName.toLowerCase();
        List<String> List = null;
        if (paramsMap.get(paramNameLower) == null) {
            List = new ArrayList<String>();
            paramsMap.put(paramNameLower, List);
            arrayList.add(paramNameLower);
        } else List = paramsMap.get(paramNameLower);
        List.add(Integer.toString(Index));
    }

    /**
     * TransNamedParam ��Script�������Ĳ���ת��ΪJDBC֧�ֵĲ���?
     * 
     * @param SQL
     * @return
     */
    public static String deleteComment(final String SQL) {
        return StringUtils.isEmpty(SQL) ? "" : Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/").matcher(SQL).replaceAll("$1");
    }

    public static String deleteComment1(final String SQL) {
        return StringUtils.isEmpty(SQL) ? "" : Pattern.compile("(?ms)('(?:''|[^'])*')|/\\*.*?\\*/").matcher(SQL).replaceAll("$1");
    }

    /**
     * TransNamedParam ��Script�������Ĳ���ת��ΪJDBC֧�ֵĲ���?
     * 
     * @param SQL
     * @return
     */
    public static String transNamedParam(final String sql) {
        final StringBuffer strBuf = new StringBuffer();
        final String fullSQL = sql + "\n";
        char Mode = 'S';
        final long SQLLength = fullSQL.length();

        char C = 0;
        for (int i = 0; i < SQLLength; i++) {
            C = fullSQL.charAt(i);
            switch (Mode) {
            case 'S':
                if ((C == ':') && (fullSQL.charAt(i + 1) != '=')) {
                    Mode = 'V';
                    strBuf.append("?");
                } else if (C == '\'')
                    Mode = 'Q';
                else if ((C == '/') && (fullSQL.charAt(i + 1) == '*'))
                    Mode = 'C';
                else if ((C == '-') && (fullSQL.charAt(i + 1) == '-'))
                    Mode = 'c';
                break;
            case 'V':
                if ((C <= 255) && !DBSqlUtils.isIdientChar(C))
                    Mode = 'S';
                break;
            case 'c':
                if (C == '\n')
                    Mode = 'S';
                break;
            case 'C':
                if ((C == '*') && (fullSQL.charAt(i + 1) == '/'))
                    Mode = 'S';
                break;
            case 'Q':
                if (C == '\'')
                    Mode = 'S';
                break;
            }
            if (Mode != 'V')
                strBuf.append(C);
        }
        return strBuf.toString();
    }

    /**
     * ˽�й��캯�� �������������ʵ��
     */
    private DBSqlUtils() {
        super();
    }

    /**
     * ����Oracle��Script������ȷ����\r\n������
     * 
     * @param sQL
     * @return
     */
    public static String processEnterChart(final String SQL) {
        return StringUtils.replaceEach(SQL, new String[] { "\r\n" }, new String[] { "\n" });
    }

}
