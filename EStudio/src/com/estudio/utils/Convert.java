package com.estudio.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

public final class Convert {

    private static Boolean isEqualBytes(byte[] bs1, byte[] bs2) {
        if (bs1.length == bs2.length) {
            for (int i = 0; i < bs1.length; i++)
                if (bs1[i] != bs2[i])
                    return false;
            return true;
        }
        return false;
    }

    /**
     * �Ʋ����
     * 
     * @param bs
     * @return
     */
    public static Charset guessCharset(byte bs[]) {
        Charset charsets[] = new Charset[] { Charset.forName("utf-8"), Charset.forName("gb18030"), Charset.forName("utf-16") };
        for (Charset charset : charsets) {
            if (isEqualBytes(bs, new String(bs, charset).getBytes(charset)))
                return charset;
        }
        return Charset.forName("utf-8");
    }

    /**
     * Blobת��Ϊ�ַ���
     * 
     * @param blob
     *            Blob
     * @return blob��Ӧ���ַ���
     */
    public static String blob2Str(final Blob blob) {
        String result = "";
        if (blob != null)
            try {
                final OutputStream stmt = new ByteArrayOutputStream();
                final byte[] tmpByte = new byte[1024 * 4];
                final InputStream inpStm = blob.getBinaryStream();
                int readLength = 0;
                do {
                    readLength = inpStm.read(tmpByte);
                    if (readLength != -1)
                        stmt.write(tmpByte, 0, readLength);
                } while (readLength != -1);

                stmt.flush();
                result = stmt.toString();
                inpStm.close();
                stmt.close();
            } catch (final SQLException e) {
            } catch (final IOException e) {
            }
        return result;
    }

    /**
     * ���ֽ������浽�ļ���
     * 
     * @param bytes
     * @param fileName
     * @return
     * @throws IOException
     */
    public static boolean bytes2File(final byte[] bytes, final String fileName) throws IOException {
        if (bytes == null)
            return false;
        File f = new File(fileName);
        f = f.getParentFile();
        if (!f.exists())
            f.mkdirs();
        final BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(fileName));
        bf.write(bytes);
        bf.close();
        return true;
    }

    /**
     * ���ֽ���ת��Ϊ�ַ���
     * 
     * @param value
     * @return
     */
    public static String bytes2Str(final byte[] value) {
        if (value == null || value.length == 0)
            return "";
        return new String(value, guessCharset(value));
    }

    public static String bytes2Str(final byte[] value, final Charset chartset) {
        if (value == null)
            return "";
        return new String(value, chartset);
    }

    /**
     * ��ͨ��������ת��ΪSQL��������
     * 
     * @param d
     * @return
     */
    public static java.sql.Date date2SQLDate(final java.util.Date d) {
        return new java.sql.Date(d.getTime());
    }

    /**
     * ��ͨ��������ת��ΪSQL��������
     * 
     * @param d
     * @return
     */
    public static Timestamp date2SQLDateTime(final java.util.Date d) {
        if (d == null)
            return null;
        return new java.sql.Timestamp(d.getTime());
    }

    /**
     * ���ڷ���Ϊ�ַ���
     * 
     * @param date
     * @return
     */
    public static String date2Str(final Date date) {
        if (date == null)
            return null;
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date).replace(" 00:00:00", "");
    }

    /**
     * ���ڷ���Ϊ�ַ���
     * 
     * @param date
     * @return
     */
    public static String datetime2Str(final Date date) {
        if (date == null)
            return null;
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * ��ȡ�ļ����ڴ��в������ļ����ֽ���
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static byte[] file2Bytes(final String fileName) throws IOException {
        final BufferedInputStream bf = new BufferedInputStream(new FileInputStream(fileName));
        byte[] result = null;
        if (bf.available() > 0) {
            result = new byte[bf.available()];
            bf.read(result);
        }
        bf.close();
        return result;
    }

    public static byte[] inputStream2Bytes(final InputStream inpStm) {
        byte[] result = null;
        if (inpStm == null)
            return result;
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            final byte[] tmpByte = new byte[1024 * 4];
            int readLength = 0;
            do {
                readLength = inpStm.read(tmpByte);
                if (readLength != -1)
                    outputStream.write(tmpByte, 0, readLength);
            } while (readLength != -1);
            outputStream.flush();
            result = outputStream.toByteArray();
        } catch (final IOException e) {
        } finally {
            try {
                inpStm.close();
            } catch (IOException e1) {
            }
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
        }
        return result;
    }

    public static void inputStream2OutputStream(final InputStream inputStream, final OutputStream outputStream) {
        if (inputStream == null)
            return;
        try {
            final byte[] tmpByte = new byte[1024 * 4];
            int readLength = 0;
            do {
                readLength = inputStream.read(tmpByte);
                if (readLength != -1)
                    outputStream.write(tmpByte, 0, readLength);
            } while (readLength != -1);
            outputStream.flush();
        } catch (final IOException e) {
        } finally {
            try {
                inputStream.close();
            } catch (IOException e1) {
            }
        }
    }

    public static void inputStream2File(final InputStream inputStream, final File f) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(f);
            inputStream2OutputStream(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
        }
    }

    /**
     * ��inputStream��
     * 
     * @param inpStm
     * @return
     */
    public static String inputStream2Str(final InputStream inpStm) {
        return Convert.bytes2Str(inputStream2Bytes(inpStm));
    }

    /**
     * ����ת��Ϊֱ����
     * 
     * @param i
     * @return
     */
    public static byte[] intToByte(final long i) {
        final byte[] bt = new byte[4];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        bt[2] = (byte) ((0xff0000 & i) >> 16);
        bt[3] = (byte) ((0xff000000 & i) >> 24);
        return bt;

    }

    /**
     * ģ��Oracle��NVL����
     * 
     * @param str
     * @param value
     * @return
     */
    public static String nvl(final String str, final String value) {
        return StringUtils.isEmpty(str) ? value : str;
    }

    /**
     * �ַ���ת��Ϊ�߾�������
     * 
     * @param str
     * @return
     */
    public static BigDecimal str2BigDecimal(final String str) {
        return new BigDecimal(str);
    }

    /**
     * �ַ���ת��ΪBoolean����
     * 
     * @param primary
     * @return
     */
    public static boolean str2Boolean(final String primary) {
        return StringUtils.isEmpty(primary) ? false : (BooleanUtils.toBoolean(primary) || (try2Int(primary, 0) != 0));
    }

    /**
     * �ַ���ת��ΪBoolean����
     * 
     * @param primary
     * @return
     */
    public static boolean str2Boolean(final String primary, final boolean defaultValue) {
        return StringUtils.isEmpty(primary) ? defaultValue : (BooleanUtils.toBoolean(primary) || (try2Int(primary, 0) != 0));
    }

    /**
     * ���ַ���ת��Ϊ�ֽ���
     * 
     * @param value
     * @return
     */
    public static byte[] str2Bytes(final String value) {
        if (StringUtils.isEmpty(value))
            return null;
        return value.getBytes(Charset.forName("utf-8"));
    }

    /**
     * �ַ���ת����
     * 
     * @param value
     * @return
     */
    public static Date str2Date(final String str) {
        Date result = null;
        try {
            if (!StringUtils.isEmpty(str)) {
                String datestr = "yyyy-MM-dd";
                String datetimestr = "yyyy-MM-dd HH:mm:ss";
                if (StringUtils.contains(str, " "))
                    result = DateUtils.parseDate(str, new String[] { datetimestr });
                else
                    result = DateUtils.parseDate(str, new String[] { datestr });
            }
        } catch (final Exception e) {

        }
        return result;
    }

    /**
     * �ַ���ת��Ϊʱ���ʽ
     * 
     * @param value
     * @return
     */
    public static Date str2DateTime(final String str) {
        Date result = null;
        try {
            result = DateUtils.parseDate(str, new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" });
        } catch (final Exception e) {

        }
        return result;
    }

    public static double str2Double(final String str) {
        return try2Double(str, 0);
    }

    /**
     * �ַ���ת��Ϊ����������
     * 
     * @param str
     * @return
     */
    public static float str2Float(final String str) {
        return try2Float(str, 0);
    }

    /**
     * Str2InputStream �ַ���ת��Ϊ������
     * 
     * @param str
     * @return
     */
    public static InputStream str2InputStream(final String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    /**
     * @param size
     * @return
     */
    public static int str2Int(final String str) {
        return try2Int(str, 0);
    }

    /**
     * �ַ���ת��ΪLong
     * 
     * @param str
     * @return
     */
    public static Long str2Long(final String str) {
        return try2Long(str, 0);
    }

    /**
     * �ַ���ת��Ϊʱ��
     * 
     * @param str
     * @return
     */
    public static Time str2Time(final String str) {
        return str2Time(str, null);
    }

    /**
     * �ַ���ת��Ϊʱ��
     * 
     * @param str
     * @param time
     * @return
     */
    public static Time str2Time(final String str, final Time time) {
        Time result = time;
        try {
            result = Time.valueOf(str);
        } catch (final Exception e) {

        }
        return result;
    }

    /**
     * �ַ���ת��Ϊ����ʱ��
     * 
     * @param str
     * @param defaultValue
     * @return
     */
    public static Date try2Date(final String str, final Date defaultValue) {
        final Date d = str2Date(str);
        return d != null ? d : defaultValue;
    }

    /**
     * �ַ���ת��Ϊ������
     * 
     * @param paramValue
     * @param d
     * @return
     */
    public static BigDecimal try2Decimal(final String str, final BigDecimal d) {
        BigDecimal result = d;
        try {
            result = new BigDecimal(str);
        } catch (final Exception e) {

        }
        return result;
    }

    /**
     * �ַ���ת��ΪDouble
     * 
     * @param str
     * @param d
     * @return
     */
    public static double try2Double(final String str, final double d) {
        final double value = NumberUtils.isNumber(str) ? NumberUtils.createDouble(str) : d;
        return value;
    }

    /**
     * �ַ���ת��Ϊfloat
     * 
     * @param str
     * @param f
     * @return
     */
    public static float try2Float(final String str, final float f) {
        return NumberUtils.isNumber(str) ? NumberUtils.createFloat(str) : f;
    }

    /**
     * �ַ���ת��Ϊ����
     * 
     * @param str
     * @param defaultValue
     * @return
     */
    public static int try2Int(final String str, final int defaultValue) {
        return NumberUtils.isNumber(str) ? NumberUtils.createNumber(str).intValue() : defaultValue;
    }

    /**
     * �ַ�ת��Ϊlong��������
     * 
     * @param str
     * @param defaultValue
     * @return
     */
    public static long try2Long(final String str, final long defaultValue) {
        return NumberUtils.isNumber(str) ? NumberUtils.createNumber(str).longValue() : defaultValue;
    }

    /**
     * Convert ��ֹ������ʵ��
     */
    private Convert() {
    }

    /**
     * 
     * @param eval
     * @return
     */
    public static boolean obj2Boolean(final Object value) {
        if (value == null)
            return false;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Number)
            return ((Number) value).intValue() != 0;
        if ((value instanceof String) && NumberUtils.isNumber((String) value))
            return NumberUtils.createNumber((String) value).intValue() != 0;
        return StringUtils.equalsIgnoreCase(String.valueOf(value), "true");
    }

    /**
     * ת��Ϊ����
     * 
     * @param obj
     * @return
     */
    public static double obj2Number(final Object obj) {
        return obj2Double(obj, 0);
    }

    /**
     * object ת�����¼�
     * 
     * @param obj
     * @return
     */
    public static Date obj2Date(final Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof Date)
            return (Date) obj;
        if (obj instanceof String)
            return Convert.try2Date((String) obj, null);
        return null;
    }

    /**
     * ��������ת��
     * 
     * @param obj
     * @return
     */
    public static java.sql.Date obj2SQLDate(final Object obj) {
        final Date d = obj2Date(obj);
        return d == null ? null : date2SQLDate(d);
    }

    /**
     * ��������ת��
     * 
     * @param obj
     * @param defaultValue
     * @return
     */
    public static int obj2Int(final Object obj, final int defaultValue) {
        if (obj == null)
            return defaultValue;
        if (obj instanceof Number)
            return ((Number) obj).intValue();
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1 : 0;
        if (obj instanceof Date)
            return (int) ((Date) obj).getTime();
        if (obj instanceof String)
            return try2Int(String.valueOf(obj), defaultValue);
        return defaultValue;
    }

    /**
     * ����ת��ΪLong
     * 
     * @param obj
     * @param defaultValue
     * @return
     */
    public static long obj2Long(final Object obj, final long defaultValue) {
        if (obj == null)
            return defaultValue;
        if (obj instanceof Number)
            return ((Number) obj).intValue();
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1 : 0;
        if (obj instanceof Date)
            return ((Date) obj).getTime();
        if (obj instanceof String)
            return try2Long(String.valueOf(obj), defaultValue);
        return defaultValue;
    }

    /**
     * ����ת��Ϊ������
     * 
     * @param obj
     * @param defaultValue
     * @return
     */
    public static double obj2Double(final Object obj, final double defaultValue) {
        if (obj == null)
            return defaultValue;
        if (obj instanceof Number)
            return ((Number) obj).doubleValue();
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1 : 0;
        if (obj instanceof Date)
            return ((Date) obj).getTime();
        if (obj instanceof String)
            return try2Double(String.valueOf(obj), defaultValue);
        return defaultValue;
    }

    /**
     * ����ת��Ϊ������
     * 
     * @param obj
     * @param defaultValue
     * @return
     */
    public static float obj2Float(final Object obj, final float defaultValue) {
        if (obj == null)
            return defaultValue;
        if (obj instanceof Number)
            return ((Number) obj).floatValue();
        if (obj instanceof Boolean)
            return (Boolean) obj ? 1 : 0;
        if (obj instanceof Date)
            return ((Date) obj).getTime();
        if (obj instanceof String)
            return try2Float(String.valueOf(obj), defaultValue);
        return defaultValue;
    }

    /**
     * ����ת��Ϊֱ������
     * 
     * @param obj
     * @return
     */
    public static byte[] obj2Bytes(final Object obj) {
        byte[] result = null;
        if (obj != null)
            if (obj instanceof byte[])
                result = (byte[]) obj;
            else if (obj instanceof String)
                result = str2Bytes((String) obj);
        return result;
    }

    /**
     * �ַ���תΪʮ���Ƹ�����
     * 
     * @param paramValue
     * @return
     */
    public static BigDecimal str2Decimal(String paramValue) {
        return BigDecimal.valueOf(try2Double(paramValue, 0));
    }

    /**
     * Object ת�ַ���
     * 
     * @param obj
     * @return
     */
    public static String obj2String(final Object obj) {
        if (obj == null)
            return "";
        return String.valueOf(obj);
    }

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static byte[] getBytes(String data) {
        return getBytes(data, "GBK");
    }

    public static short getShort(byte[] bytes) {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static char getChar(byte[] bytes) {
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static int getInt(byte[] bytes) {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    public static long getLong(byte[] bytes) {
        return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16)) | (0xff000000L & ((long) bytes[3] << 24)) | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
    }

    public static float getFloat(byte[] bytes) {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static double getDouble(byte[] bytes) {
        long l = getLong(bytes);
        System.out.println(l);
        return Double.longBitsToDouble(l);
    }

    public static String bytes2Base64(byte[] bytes) {
        bytes = bytes == null ? "".getBytes() : bytes;
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64ToBytes(String str) {
        return Base64.decodeBase64(str);
    }

    public static Convert instance = new Convert();

}
