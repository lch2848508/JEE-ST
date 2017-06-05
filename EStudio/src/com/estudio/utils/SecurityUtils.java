package com.estudio.utils;

import org.apache.commons.codec.binary.Base64;

public class SecurityUtils {

    /**
     * MD5×Ö·û´®
     * 
     * @param str
     * @return
     */
    public static String md5(final String str) {
        try {
            final java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            final byte[] array = md.digest(str.getBytes());
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i)
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            return sb.toString();
        } catch (final java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * Base64±àÂë
     * 
     * @param bs
     * @return
     */
    public static String encodeBae64(byte[] bs) {
        return Base64.encodeBase64String(bs);
    }

    /**
     * Base64½âÂë
     * 
     * @param str
     * @return
     */
    public static byte[] decodeBase64(String str) {
        return Base64.decodeBase64(str);
    }

}
