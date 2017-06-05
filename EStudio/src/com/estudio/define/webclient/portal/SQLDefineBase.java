package com.estudio.define.webclient.portal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.db.SQLParam;
import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.impl.db.DBSqlUtils;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.db.ISQLTrans;

public class SQLDefineBase {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    protected String sql;
    protected String orgSQL;
    private String keyFieldName;
    private String captionFieldName;
    IDBCommand cmd;
    IDBCommand pageCmd;
    IDBCommand countCmd;
    IDBCommand singleCmd;
    private final ArrayList<SQLParam4Portal> paramList = new ArrayList<SQLParam4Portal>();
    protected ArrayList<SQLField> fieldList = new ArrayList<SQLField>();
    private List<String> canRemoveParamList = new ArrayList<String>();
    private Map<String, String> cacheSQL = new HashMap<String, String>();
    private static List<String> filterInitValues = Arrays.asList("MAXDATE", "MINDATE", "%");
    private boolean isCalcKeyFieldContainExpress = false;
    private boolean calcKeyFieldContainExpressValue = false;
    private Object lock = new Object();

    private boolean isKeyFieldContainExpress() throws Exception {
        synchronized (lock) {
            if (!isCalcKeyFieldContainExpress) {
                calcKeyFieldContainExpressValue = DBHELPER.getSQLTrans().isSelectFieldContainExpress(sql, getKeyFieldName());
                isCalcKeyFieldContainExpress = true;
            }
        }
        return calcKeyFieldContainExpressValue;
    }

    public SQLDefineBase() {
        super();
    }

    /**
     * 获取参数列表
     * 
     * @return
     */
    public List<SQLParam4Portal> getParams() {
        return this.paramList;
    }

    /**
     * 得到一个获取单条记录集操作的Command对象
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getSingleCmd(final Connection con, Map<String, String> param2Values) throws Exception {
        if (!canRemoveParamList.isEmpty() && param2Values != null) {
            List<String> removeParams = new ArrayList<String>();
            for (String str : canRemoveParamList) {
                if (StringUtils.isEmpty(StringUtils.replace(param2Values.get(str), "%", "")))
                    removeParams.add(":" + str);
            }
            if (removeParams.isEmpty())
                return singleCmd.clone(con);
            String key = StringUtils.join(removeParams.toArray()) + "-single";
            String tmpSQL = cacheSQL.get(key);
            if (StringUtils.isEmpty(tmpSQL)) {
                tmpSQL = DBHELPER.getSQLTrans().removeSQLParams(this.sql, removeParams);
                tmpSQL = DBHELPER.getSQLTrans().transSQL4SearchByKeyField(tmpSQL, getKeyFieldName(), "K");
                cacheSQL.put(key, tmpSQL);
            }

            return DBHELPER.getCommand(con, tmpSQL);
        }
        return singleCmd.clone(con);
    }

    /**
     * 得到一个具体的数据操作对象
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getCmd(final Connection con, Map<String, String> param2Values) throws Exception {
        if (!canRemoveParamList.isEmpty() && param2Values != null) {
            List<String> removeParams = new ArrayList<String>();
            for (String str : canRemoveParamList) {
                if (StringUtils.isEmpty(StringUtils.replace(param2Values.get(str), "%", "")))
                    removeParams.add(":" + str);
            }
            if (removeParams.isEmpty())
                return cmd.clone(con);

            String key = StringUtils.join(removeParams.toArray()) + "-cmd";
            String tmpSQL = cacheSQL.get(key);
            if (StringUtils.isEmpty(tmpSQL)) {
                tmpSQL = DBHELPER.getSQLTrans().removeSQLParams(this.sql, removeParams);
                cacheSQL.put(key, tmpSQL);
            }
            return DBHELPER.getCommand(con, tmpSQL);
        }
        return cmd.clone(con);
    }

    /**
     * 初始化获取单条记录的记录集
     * 
     * @throws SQLException
     *             , DBException
     */
    public void initSingleCmd() throws Exception {
        String keyFieldName = "";
        for (int i = 0; i < fieldList.size(); i++) {
            final SQLField field = fieldList.get(i);
            if (field.isKey()) {
                keyFieldName = field.getFieldName();
                break;
            }
        }
        if (StringUtils.isEmpty(keyFieldName))
            keyFieldName = fieldList.get(0).getFieldName();
        final String newSQL = DBHELPER.getSQLTrans().transSQL4SearchByKeyField(sql, keyFieldName, "K");
        // "select * from (" + SQL + ") where " + keyFieldName + " = :K";
        singleCmd = DBHELPER.getCommand(null, newSQL);
    }

    /**
     * 初始化分页及求总的记录集
     * 
     * @throws SQLException
     *             , DBException
     */
    public void initPageAndCountCmd() throws Exception {
        ISQLTrans sqlTrans = DBHELPER.getSQLTrans();
        String newSQL = DBSqlUtils.deleteComment(sqlTrans.transCountSQL4Page(orgSQL));
        if (sqlTrans.isSelectSQL(newSQL)) {
            countCmd = DBHELPER.getCommand(null, newSQL);
            newSQL = isSupportPageOptimize() ? sqlTrans.transSQL4Page(sql, getKeyFieldName()) : sqlTrans.transSQL4Page(sql);
            pageCmd = DBHELPER.getCommand(null, newSQL);
        }
    }

    /**
     * 得到求记录总数的Command对象
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getRCCmd(final Connection con, final Map<String, String> extParsms, Map<String, String> param2Values) throws Exception {
        if (!canRemoveParamList.isEmpty() && param2Values != null) {
            List<String> removeParams = new ArrayList<String>();
            for (String str : canRemoveParamList) {
                if (StringUtils.isEmpty(StringUtils.replace(param2Values.get(str), "%", "")))
                    removeParams.add(":" + str);
            }
            if (removeParams.isEmpty())
                return countCmd.clone(con);

            String key = StringUtils.join(removeParams.toArray()) + "-rcmd";
            String tmpSQL = cacheSQL.get(key);
            if (StringUtils.isEmpty(tmpSQL)) {
                tmpSQL = DBHELPER.getSQLTrans().removeSQLParams(this.sql, removeParams);
                tmpSQL = DBHELPER.getSQLTrans().transCountSQL4Page(tmpSQL);
                cacheSQL.put(key, tmpSQL);
            }
            return DBHELPER.getCommand(con, tmpSQL);
        }
        return countCmd.clone(con, extParsms);
    }

    /**
     * 得到支持分页的Command对象
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getPCmd(final Connection con, final Map<String, String> extParsms, Map<String, String> param2Values) throws Exception {
        if (!canRemoveParamList.isEmpty() && param2Values != null) {
            List<String> removeParams = new ArrayList<String>();
            for (String str : canRemoveParamList) {
                if (StringUtils.isEmpty(StringUtils.replace(param2Values.get(str), "%", "")))
                    removeParams.add(":" + str);
            }
            if (removeParams.isEmpty())
                return pageCmd.clone(con);

            String key = StringUtils.join(removeParams.toArray()) + "-pcmd";
            String tmpSQL = cacheSQL.get(key);
            if (StringUtils.isEmpty(tmpSQL)) {
                ISQLTrans sqlTrans = DBHELPER.getSQLTrans();
                tmpSQL = sqlTrans.removeSQLParams(this.sql, removeParams);
                tmpSQL = isSupportPageOptimize() ? sqlTrans.transSQL4Page(tmpSQL, getKeyFieldName()) : sqlTrans.transSQL4Page(tmpSQL);
                cacheSQL.put(key, tmpSQL);
            }
            return DBHELPER.getCommand(con, tmpSQL);
        }
        return pageCmd.clone(con, extParsms);
    }

    /**
     * 
     * @param con
     * @param controlFilterParams
     * @return
     * @throws Exception
     */
    public IDBCommand getPageIdsCmd(Connection con, Map<String, String> param2Values) throws Exception {
        List<String> removeParams = new ArrayList<String>();
        for (String str : canRemoveParamList) {
            if (StringUtils.isEmpty(StringUtils.replace(param2Values.get(str), "%", "")))
                removeParams.add(":" + str);
        }
        String key = StringUtils.join(removeParams.toArray()) + "-pcmd-ids";
        String tmpSQL = cacheSQL.get(key);
        if (StringUtils.isEmpty(tmpSQL)) {
            ISQLTrans sqlTrans = DBHELPER.getSQLTrans();
            tmpSQL = sqlTrans.generatePageOptimizeIDSQL(this.sql, removeParams, getKeyFieldName());
            cacheSQL.put(key, tmpSQL);
        }
        return DBHELPER.getCommand(con, tmpSQL);
    }

    /**
     * 增加参数
     * 
     * @param param
     */
    public void addParam(final SQLParam4Portal param) {
        paramList.add(param);
        String paramInitValue = param.getInitValue();
        if (param.isFilter() && (param.isSkipNull() || StringUtils.isEmpty(paramInitValue) || ArrayUtils.contains(filterInitValues.toArray(), paramInitValue)))
            canRemoveParamList.add(param.getName());
    }

    /**
     * 增加字段
     * 
     * @param field
     */
    public void addField(final SQLField field) {
        fieldList.add(field);
    }

    /**
     * 得到字段总数
     * 
     * @return
     */
    public long getFieldCount() {
        return fieldList.size();
    }

    /**
     * 根据索引取字段
     * 
     * @param index
     * @return
     */
    public SQLField getField(final int index) {
        return fieldList.get(index);
    }

    /**
     * 得到SQL语句
     * 
     * @return
     */
    public String getSQL() {
        return sql;
    }

    /**
     * 得到参数总个数
     * 
     * @return
     */
    public long getParamCount() {
        return paramList.size();
    }

    /**
     * 根据索引取得参数
     * 
     * @param index
     * @return
     */
    public SQLParam4Portal getParam(final int index) {
        return paramList.get(index);
    }

    /**
     * 得到主键
     * 
     * @return
     */
    public String getKeyFieldName() {
        if (StringUtils.isEmpty(keyFieldName)) {
            for (int i = 0; i < fieldList.size(); i++) {
                final SQLField field = fieldList.get(i);
                if (field.isKey()) {
                    keyFieldName = field.getFieldName();
                    break;
                }
            }
            if (StringUtils.isEmpty(keyFieldName) && !fieldList.isEmpty())
                keyFieldName = fieldList.get(0).getFieldName();
        }
        return keyFieldName;
    }

    /**
     * 获取标题字段
     * 
     * @return
     */
    public String getLabelFieldName() {
        if (StringUtils.isEmpty(captionFieldName))
            for (int i = 0; i < fieldList.size(); i++) {
                final SQLField field = fieldList.get(i);
                if (field.isCaption()) {
                    captionFieldName = field.getFieldName();
                    break;
                }

            }
        return captionFieldName;
    }

    /**
     * 初始化cmd对象 增加语句翻译功能
     * 
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public void initCommand(final Connection con) throws Exception {
        final ArrayList<SQLParam> params = new ArrayList<SQLParam>();
        params.addAll(paramList);
        cmd = DBHELPER.getCommand(null, DBHELPER.getSQLTrans().transSQL4ProcSQL(sql, params, con));
    }

    // 构造函数
    public SQLDefineBase(final String sQL) {
        super();
        sql = DBSqlUtils.deleteComment(sQL);
        orgSQL = sQL;
    }

    /**
     * 得到对应的字段数据类型
     * 
     * @param fieldName
     * @return
     */
    public String getFieldDataType(final String fieldName) {
        String result = "";
        for (int i = 0; i < getFieldCount(); i++) {
            final SQLField field = getField(i);
            if (StringUtils.equalsIgnoreCase(field.getFieldName(), fieldName)) {
                result = field.getDataType();
                break;
            }
        }
        return result;
    }

    public SQLParam4Portal getParam(String paramName) {
        SQLParam4Portal result = null;
        for (SQLParam4Portal param : paramList) {
            if (StringUtils.equalsIgnoreCase(paramName, param.getName())) {
                result = param;
                break;
            }
        }
        return result;
    }

    public boolean isSupportPageOptimize() throws Exception {
        return DBHELPER.getSQLTrans().isSupportPageOptimize() && !isKeyFieldContainExpress();
    }

}
