package com.estudio.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public class MessUtils {

    private static String macAddress = null;

    // 获取MAC地址的方法
    public static String getMACAddress() {
        if (StringUtils.isEmpty(macAddress))
            try {
                final InetAddress ia = InetAddress.getLocalHost();//
                // 获得网络接口对象（即网卡）,并得到mac地址,mac地址存在于一个byte数组中。
                final byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

                // 下面代码是把mac地址拼装成String
                final StringBuffer sb = new StringBuffer();

                for (int i = 0; i < mac.length; i++) {
                    if (i != 0)
                        sb.append("-");
                    // mac[i] & 0xFF 是为了把byte转化为正整数
                    final String s = Integer.toHexString(mac[i] & 0xFF);
                    sb.append(s.length() == 1 ? 0 + s : s);
                }

                // 把字符串所有小写字母改为大写成为正规的mac地址并返回
                macAddress = sb.toString().toUpperCase();
            } catch (final Exception e) {
                ExceptionUtils.printExceptionTrace(e);
            }
        return macAddress;
    }

    /**
     * 获取GUID对象
     * 
     * @return
     */
    public static String getGUID() {
        return UUID.randomUUID().toString();
    }

}
