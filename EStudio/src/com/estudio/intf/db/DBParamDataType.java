package com.estudio.intf.db;

public enum DBParamDataType {
    String, Int, Long, Float, Double, Decimal, Date, Time, DateTime, Timestampe, Bytes, Text, Cursor, unknow;

    /**
     * 整数转化为枚举
     * 
     * @param value
     * @return
     */
    public static DBParamDataType fromInt(final int value) {
        DBParamDataType result = unknow;
        switch (value) {
        case 0:
            result = String;
            break;
        case 1:
            result = Int;
            break;
        case 2:
            result = Long;
            break;
        case 3:
            result = Float;
            break;
        case 4:
            result = Double;
            break;
        case 5:
            result = Decimal;
            break;
        case 6:
            result = Date;
            break;
        case 7:
            result = Time;
            break;
        case 8:
            result = DateTime;
            break;
        case 9:
            result = Timestampe;
            break;
        case 10:
            result = Bytes;
            break;
        case 11:
            result = Cursor;
            break;
        case 12:
            result = Text;
            break;
        case -1:
            result = unknow;
            break;
        }
        return result;
    }

    /**
     * 枚举类型转为整数
     * 
     * @param value
     * @return
     */
    public static long toInt(final DBParamDataType value) {
        switch (value) {
        case String:
            return 0;
        case Int:
            return 1;
        case Long:
            return 2;
        case Float:
            return 3;
        case Double:
            return 4;
        case Decimal:
            return 5;
        case Date:
            return 6;
        case Time:
            return 7;
        case DateTime:
            return 8;
        case Timestampe:
            return 9;
        case Bytes:
            return 10;
        case Cursor:
            return 11;
        case Text:
            return 12;
        }
        return -1;
    }
}
