package com.estudio.service.script;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.Convert;

public final class StringUtilsHelper {

    public String before(final String str, final String substr) {
        return StringUtils.substringBefore(str, substr);
    }

    public String after(final String str, final String substr) {
        return StringUtils.substringAfter(str, substr);
    }

    public String between(final String str, final String str1, final String str2) {
        return StringUtils.substringBetween(str, str1, str2);

    }

    public String lTrim(final String str) {
        return trim(str);
    }

    public String rTrim(final String str) {
        return trim(str);
    }

    public String trim(final String str) {
        return StringUtils.trim(str);
    }

    public String replaceAll(final String str, final String searchString, final String replacement) {
        return StringUtils.replace(str, searchString, replacement);
    }

    public boolean isEmpty(final String str) {
        return StringUtils.isEmpty(str);
    }

    public String toHTML(String str) {
        return StringEscapeUtils.escapeHtml3(str);
    }

    public byte[] toBytes(String str) {
        return Convert.str2Bytes(str);
    }

    public void Test(Object obj) {
        System.out.println(obj);
        if(obj!=null)
        System.out.println(obj.getClass().toString());
        System.out.println();
    }

    private StringUtilsHelper() {

    }

    public static StringUtilsHelper instance = new StringUtilsHelper();
}
