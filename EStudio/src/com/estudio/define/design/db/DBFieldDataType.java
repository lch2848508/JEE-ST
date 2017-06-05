package com.estudio.define.design.db;

import com.estudio.intf.db.DBParamDataType;

public class DBFieldDataType {

    /**
     * 对应的JAVA数据类型
     * 
     * @author LSH
     * 
     */
    public enum DataType2JavaDataType {
        String, Integer, Long, Float, Double, DateTime, Bytes, Decimal
    }

    public static long categoryString = 0;
    public static long categoryNumber = 1;
    public static long categoryDateTime = 2;
    public static long categoryBinary = 3;

    private final String typeName;
    private final boolean notNullAble;
    private final boolean primaryKeyAble;
    private final boolean uniqueKeyAble;
    private final boolean indexAble;
    private final boolean fixedSize;
    private final long maxLength;
    private final long minLength;
    private final String comment;
    private final DBParamDataType paramDataType;
    private long dataTypeCategory;
    private final DataType2JavaDataType javaType;

    public DataType2JavaDataType getJavaType() {
        return javaType;
    }

    public long getDataTypeCategory() {
        return dataTypeCategory;
    }

    public void setDataTypeCategory(final long dataTypeCategory) {
        this.dataTypeCategory = dataTypeCategory;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isNotNullAble() {
        return notNullAble;
    }

    public boolean isPrimaryKeyAble() {
        return primaryKeyAble;
    }

    public boolean isUniqueKeyAble() {
        return uniqueKeyAble;
    }

    public boolean isIndexAble() {
        return indexAble;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public long getMaxLength() {
        return maxLength;
    }

    public long getMinLength() {
        return minLength;
    }

    public DBParamDataType getParamDataType() {
        return paramDataType;
    }

    public String getComment() {
        return comment;

    }

    /**
     * 构造函数
     * 
     * @param typeName
     * @param notNullAble
     * @param primaryKeyAble
     * @param uniqueKeyAble
     * @param indexAble
     * @param fixedSize
     * @param maxLength
     * @param minLength
     */
    public DBFieldDataType(final String typeName, final long dataTypeCategory, final DataType2JavaDataType javaType, final boolean notNullAble, final boolean primaryKeyAble, final boolean uniqueKeyAble, final boolean indexAble, final boolean fixedSize, final long minLength, final long maxLength, final DBParamDataType paramDataType, final String comment) {
        super();
        this.typeName = typeName;
        this.notNullAble = notNullAble;
        this.primaryKeyAble = primaryKeyAble;
        this.uniqueKeyAble = uniqueKeyAble;
        this.indexAble = indexAble;
        this.fixedSize = fixedSize;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.paramDataType = paramDataType;
        this.comment = comment;
        this.dataTypeCategory = dataTypeCategory;
        this.javaType = javaType;
    }

}
