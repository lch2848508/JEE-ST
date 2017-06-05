package com.estudio.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public class MessUtils {

    private static String macAddress = null;

    // ��ȡMAC��ַ�ķ���
    public static String getMACAddress() {
        if (StringUtils.isEmpty(macAddress))
            try {
                final InetAddress ia = InetAddress.getLocalHost();//
                // �������ӿڶ��󣨼�������,���õ�mac��ַ,mac��ַ������һ��byte�����С�
                final byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

                // ��������ǰ�mac��ַƴװ��String
                final StringBuffer sb = new StringBuffer();

                for (int i = 0; i < mac.length; i++) {
                    if (i != 0)
                        sb.append("-");
                    // mac[i] & 0xFF ��Ϊ�˰�byteת��Ϊ������
                    final String s = Integer.toHexString(mac[i] & 0xFF);
                    sb.append(s.length() == 1 ? 0 + s : s);
                }

                // ���ַ�������Сд��ĸ��Ϊ��д��Ϊ�����mac��ַ������
                macAddress = sb.toString().toUpperCase();
            } catch (final Exception e) {
                ExceptionUtils.printExceptionTrace(e);
            }
        return macAddress;
    }

    /**
     * ��ȡGUID����
     * 
     * @return
     */
    public static String getGUID() {
        return UUID.randomUUID().toString();
    }

}
