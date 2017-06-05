package com.estudio.web.servlet;

import java.util.Random;

import javax.servlet.http.HttpSession;

public class VerifyUtils {
    /**
     * 判断校验码是否正确
     * 
     * @param type
     * @param session
     * @param key
     * @return
     */
    public static boolean isVerifyOK(final String type, final HttpSession session, final String key) {
        final String str = (String) session.getAttribute("Verify_" + type);
        return (str != null) && str.equals(key);
    }

    /**
     * 生成随机数
     * 
     * @param from
     * @param to
     * @return
     */
    private static int randomInt(final int from, final int to) {
        final Random r = new Random();
        return from + r.nextInt(to - from);
    }

    /**
     * 生成随机字符串
     * 
     * @param length
     * @return
     */
    private static String randomStr(final long length) {
        String charValue = "";
        // 生成随机数字串
        for (int i = 0; i < length; i++)
            charValue += String.valueOf(VerifyUtils.randomInt(0, 10));
        return charValue;
    }

    /**
     * 注册校验码
     * 
     * @param type
     * @param session
     * @return
     */
    public static String registerVerify(final String type, final HttpSession session) {
        final String result = VerifyUtils.randomStr(4);
        session.setAttribute("Verify_" + type, result);
        return result;
    }
}
