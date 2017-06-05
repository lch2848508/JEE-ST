package com.estudio.define.webclient.portal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SQLDefine4Portal extends SQLDefineBase {
    private final String Name;
    private long bindType;
    private String captionFieldName;
    private Object tempLock = new Object();
    private List<String> extFields = null;

    private final ArrayList<PortalGridColumn> columnList = new ArrayList<PortalGridColumn>();

    /**
     * 构造函数
     * 
     * @param name
     * @param sql
     * @param bindType
     * @throws SQLException
     *             , DBException
     */
    public SQLDefine4Portal(final String name, final String sql, final long bindType) throws Exception {
        super(sql);
        Name = name;
        this.bindType = bindType;
    }

    /**
     * 根据索引取得列
     * 
     * @param index
     * @return
     */
    public PortalGridColumn getColumn(final int index) {
        return columnList.get(index);
    }

    /**
     * 得到列总数
     * 
     * @return
     */
    public long getColumnCount() {
        return columnList.size();
    }

    /**
     * 增加列
     * 
     * @param column
     */
    public void addColumn(final PortalGridColumn column) {
        columnList.add(column);
    }

    /**
     * 得到SQL的名称
     * 
     * @return
     */
    public String getName() {
        return Name;
    }

    public long getBindType() {
        return bindType;
    }

    public void setBindType(final long bindType) {
        this.bindType = bindType;
    }

    public String getCaptionFieldName() {
        if (StringUtils.isEmpty(captionFieldName))
            synchronized (tempLock) {
                for (int i = 0; i < fieldList.size(); i++) {
                    final SQLField field = fieldList.get(i);
                    if (field.isCaption()) {
                        captionFieldName = field.getFieldName();
                        break;
                    }
                }
            }

        return captionFieldName;
    }

    public List<String> getExtFields() {
        if (extFields == null)
            synchronized (tempLock) {
                extFields = new ArrayList<String>();
                for (int i = 0; i < fieldList.size(); i++) {
                    final SQLField field = fieldList.get(i);
                    if (!field.isCaption() && !field.isKey()) {
                        String fieldName = field.getFieldName();
                        extFields.add(fieldName);
                    }
                }
            }
        return extFields;
    }

}
