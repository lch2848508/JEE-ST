package com.estudio.define.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.db.ISQLTrans;
import com.estudio.utils.JSONUtils;

public class DesignDataSource {
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    String name;
    String label;
    boolean readOnly;
    long cacheLevel;
    String cacheKey;
    boolean formDataSet;
    boolean appendNullRecord;
    boolean primary;
    boolean clientCache;
    boolean forceExecute;

    private Map<String, String[]> key2SQL = new HashMap<String, String[]>();

    ArrayList<DSField> fields = new ArrayList<DSField>();
    ArrayList<DataSetRelation> relations = new ArrayList<DataSetRelation>();

    HashMap<String, Integer> fieldName2Index = new HashMap<String, Integer>();
    DesignDataSourceCommand select = new DesignDataSourceCommand();
    DesignDataSourceCommand insert = new DesignDataSourceCommand();
    DesignDataSourceCommand update = new DesignDataSourceCommand();
    DesignDataSourceCommand delete = new DesignDataSourceCommand();
    private IDBCommand singleCmd = null;
    private JSONArray linkJsonArray = null;
    private JSONObject datasetJson = null;
    private final Object datasetJsonLock = new Object();
    private boolean asyncLoad = false;
    private long formID;
    private String formCaption;

    public long getFormID() {
        return formID;
    }

    public void setFormID(final long formID) {
        this.formID = formID;
    }

    public boolean isAsyncLoad() {
        return asyncLoad;
    }

    public JSONArray getLinkJsonArray() {
        return linkJsonArray;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(final String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public boolean isClientCache() {
        return clientCache;
    }

    public void setClientCache(final boolean clientCache) {
        this.clientCache = clientCache;
    }

    public boolean isForceExecute() {
        return forceExecute;
    }

    public void setForceExecute(final boolean forceExecute) {
        this.forceExecute = forceExecute;
    }

    /**
     * 生成DataSet描述对象
     * 
     * @return
     * @throws JSONException
     */
    public JSONObject getDataSetJson() {
        synchronized (datasetJsonLock) {
            if (datasetJson == null) {
                datasetJson = new JSONObject();
                datasetJson.put("Name", name);
                datasetJson.put("Readonly", readOnly);
                datasetJson.put("Keyfield", getPrimaryField().name);
                datasetJson.put("formId", formID);
                datasetJson.put("cacheKey", cacheKey);
                datasetJson.put("clientCache", clientCache);
                String keyFieldName = "";

                for (int i = 0; i < fields.size(); i++) {
                    final DSField field = fields.get(i);
                    JSONUtils.append(datasetJson, "Fields", field.getName());
                    // datasetJson.append("Fields", field.getName());
                    if (field.isPrimary())
                        keyFieldName = field.getName();
                }

                datasetJson.put("Linkage", getLinkJsonArray());
                datasetJson.put("primary", primary); // 是否主要

                if (!StringUtils.isEmpty(keyFieldName)) {
                    final JSONObject initFieldValueJson = new JSONObject();
                    datasetJson.put("initFieldValue", initFieldValueJson);
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        if (StringUtils.isEmpty(param.getInitDS()) && StringUtils.equalsIgnoreCase(param.getName(), keyFieldName) && StringUtils.startsWithIgnoreCase(param.getInitValue(), "REQ."))
                            initFieldValueJson.put(keyFieldName, StringUtils.substringAfter(param.getInitValue(), "REQ."));
                    }
                } // end if (!StringUtils.isEmpty(keyFieldName))

                if (asyncLoad) { // 如果为异步加载则需要将初始化参数传递至客户端
                    final JSONArray paramsArray = new JSONArray();
                    for (int i = 0; i < select.getParamCount(); i++) {
                        final SQLParam4Form param = select.getParam(i);
                        final JSONObject paramJson = new JSONObject();
                        paramJson.put("ds", param.getFirstInitDS());
                        paramJson.put("field", param.getFirstInitField());
                        paramJson.put("name", param.getName());
                        paramsArray.add(paramJson);
                    }
                    datasetJson.put("asyncParams", paramsArray);
                    datasetJson.put("async", true && !paramsArray.isEmpty());
                    if (paramsArray.isEmpty()) {
                        datasetJson.put("cacheKey", name);
                        datasetJson.put("clientCache", true);
                    }
                }
            }
        }
        return datasetJson;
    }

    /**
     * 得到取单条记录的SQL ICommand
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getSingleCmd(final Connection con) throws Exception {
        if (singleCmd == null) {
            final DSField dsField = fields.get(getPrimaryFieldIndex());
            final String singleSQL = DBHELPER.getSQLTrans().transSQL4SearchByKeyField(select.getSql(), dsField.getName(), "__singlekey__");
            singleCmd = DBHELPER.getCommand(null, singleSQL);
            // "select * from (" + select.getSql() + ") where " +
            // dsField.getName() + " = :__singlekey__"
        }
        return singleCmd.clone(con);
    }

    /**
     * 注册父子关系
     * 
     * @param pDataSetName
     * @param pFieldName
     * @param linkFieldName
     */
    public void registerRelation(final String pDataSetName, final String pFieldName, final String linkFieldName, final String initDataSetName, final String initFieldName) {
        relations.add(new DataSetRelation(pDataSetName, pFieldName, linkFieldName, initDataSetName, initFieldName));
    }

    /**
     * 得到DataSet的数据列定义
     * 
     * @return
     * @throws JSONException
     */
    public JSONObject getDataSetFields() {
        final JSONObject json = new JSONObject();
        for (int i = 0; i < getFieldCount(); i++)
            json.put(getField(i).name, i);
        return json;
    }

    /**
     * 根据字段名称求字段的所引
     * 
     * @param fieldName
     * @return
     */
    public long getFieldIndex(final String fieldName) {
        return fieldName2Index.get(fieldName);
    }

    /**
     * 构造函数
     * 
     * @param name
     *            名称
     * @param label
     *            备注
     */
    public DesignDataSource(final String name, final String label, final boolean primary, final boolean asyncLoad, final long formID, final String formCaption, final String cacheKey, final boolean clientCache) {
        super();
        this.name = name;
        this.label = label;
        this.primary = primary;
        this.asyncLoad = asyncLoad;
        this.formID = formID;
        this.cacheKey = StringUtils.isEmpty(cacheKey) ? name : cacheKey;
        this.formCaption = formCaption;
        this.clientCache = clientCache;
    }

    public String getFormCaption() {
        return formCaption;
    }

    public void setFormCaption(final String formCaption) {
        this.formCaption = formCaption;
    }

    /**
     * 得到名称
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 得到备注信息
     * 
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * 得到字段总数
     * 
     * @return
     */
    public long getFieldCount() {
        return fields.size();
    }

    /**
     * 得到字段
     * 
     * @param index
     * @return
     */
    public DSField getField(final int index) {
        return fields.get(index);
    }

    /**
     * 注册字段
     * 
     * @param name
     * @param label
     * @param isPrimary
     * @param string
     * @param isSplit
     * @param isGroup
     * @return
     */
    public DSField registerField(final String name, final String label, final boolean isPrimary, final boolean isGroup, final boolean isSplit, final String columnWidth) {
        final DSField result = new DSField(name, label, isPrimary, isGroup, isSplit, StringUtils.isEmpty(columnWidth) ? "*" : columnWidth);
        fieldName2Index.put(result.name, fields.size());
        fields.add(result);
        return result;
    }

    /**
     * 注册字段
     * 
     * @param field
     */
    public void registerField(final DSField field) {
        fields.add(field);
    }

    /**
     * 选择命令集
     * 
     * @return
     */
    public DesignDataSourceCommand getSelect() {
        return select;
    }

    /**
     * 插入命令集
     * 
     * @return
     */
    public DesignDataSourceCommand getInsert() {
        return insert;
    }

    /**
     * 更新命令集
     * 
     * @return
     */
    public DesignDataSourceCommand getUpdate() {
        return update;
    }

    /**
     * 删除命令集
     * 
     * @return
     */
    public DesignDataSourceCommand getDelete() {
        return delete;
    }

    public DSField getPrimaryField() {
        for (int i = 0; i < fields.size(); i++)
            if (fields.get(i).isPrimary())
                return fields.get(i);
        return fields.size() == 0 ? null : fields.get(0);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * 得到主键顺序号
     * 
     * @return
     */
    public int getPrimaryFieldIndex() {
        for (int i = 0; i < fields.size(); i++)
            if (fields.get(i).isPrimary())
                return i;
        return 0;
    }

    public ArrayList<DataSetRelation> getRelations() {
        return relations;
    }

    public long getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(final long cacheLevel) {
        this.cacheLevel = cacheLevel;
    }

    public boolean isFormDataSet() {
        return formDataSet;
    }

    public void setFormDataSet(final boolean formDataSet) {
        this.formDataSet = formDataSet;
    }

    public void setLinkJsonArray(final JSONArray value) {
        linkJsonArray = value;
    }

    public boolean isAppendNullRecord() {
        return appendNullRecord;
    }

    public void setAppendNullRecord(final boolean appendNullRecord) {
        this.appendNullRecord = appendNullRecord;
    }

    /**
     * 
     * @param invalidParamList
     * @return
     * @throws Exception
     */
    public String[] getSelectSQLWithRemoveParams(List<String> invalidParamList) throws Exception {
        String key = invalidParamList != null ? StringUtils.join(invalidParamList, "-") : "--none--";
        if (StringUtils.isEmpty(key))
            key = "--none--";
        synchronized (key2SQL) {
            if (!key2SQL.containsKey(key)) {
                String[] r = new String[3];
                ISQLTrans sqlTrans = DBHELPER.getSQLTrans();
                String selectSQL = sqlTrans.removeSQLParams(getSelect().getSql(), invalidParamList);
                r[0] = isSupportPageOptimize() ? sqlTrans.transSQL4Page(selectSQL, getPrimaryField().getName()) : sqlTrans.transSQL4Page(selectSQL);
                r[1] = sqlTrans.transCountSQL4Page(selectSQL);
                r[2] = isSupportPageOptimize() ? sqlTrans.generatePageOptimizeIDSQL(selectSQL, null, getPrimaryField().getName()) : "";
                key2SQL.put(key, r);
            }
        }
        return key2SQL.get(key);
    }

    public boolean isSupportPageOptimize() throws Exception {
        return getSelect().isSupportPageOptimize();
    }
}
