package com.estudio.impl.design.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.db.DBException;
import com.estudio.define.design.db.DBFieldDataType;
import com.estudio.define.design.db.DBFieldDataType.DataType2JavaDataType;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.db.IDBEntryService;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public final class DBEntryService4MySQL extends DBEntryService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final String SQL_GET_TABLE_KEYS = "SELECT INDEX_NAME,PrimaryKey,COLUMN_NAME FROM view_sys_user_indexs WHERE (PrimaryKey=1 OR UQIQUE=1) and UPPER(TABLE_NAME)=UPPER(?)";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#saveDBEntry(java.lang
     * .String)
     */
    @Override
    public JSONObject saveDBEntry(final String ddl_json) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        Statement stmt = null;
        String tableCode = "";
        boolean result = true;
        final StringBuilder errorSB = new StringBuilder();
        try {
            con = DBHELPER.getConnection();
            stmt = con.createStatement();
            final JSONObject ddlJSON = JSONUtils.parserJSONObject(ddl_json);
            tableCode = ddlJSON.getString("c");
            final boolean isNew = ddlJSON.getBoolean("isnew");
            if (isNew) {
                if (!tableIsExists(con, tableCode))
                    result = createTable(stmt, ddlJSON, errorSB);
                else {
                    result = false;
                    errorSB.append("已经存在同名称的数据库对象！");
                }
            } else
                result = modifyTable(stmt, ddlJSON, errorSB);

            if (!isNew || result)
                result &= createTableIndex(stmt, tableCode, ddlJSON.getJSONArray("di"), ddlJSON.getJSONArray("ni"), ddlJSON.getJSONArray("mi"), errorSB);
            json.put("r", result);
            json.put("msg", errorSB.toString());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

            JSONUtils.except2JSON(json, e);
        } finally {
            json.put("entryInfo", this.getEntryInfo(con, tableCode, -1));
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 创建索引
     * 
     * @param stmt
     * @param tableCode
     * @param array
     * @param array2
     * @param array3
     * @param errorSB
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private boolean createTableIndex(final Statement stmt, final String tableCode, final JSONArray drop_array, final JSONArray new_array, final JSONArray modify_array, final StringBuilder errorSB) throws Exception {
        final StrBuilder sb = new StrBuilder();
        boolean result = true;

        // 删错索引
        for (int i = 0; i < drop_array.size(); i++) {
            sb.clear();
            sb.append("ALTER TABLE ").append(tableCode).append("  DROP INDEX ").append(drop_array.getString(i));
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 新增索引
        for (int i = 0; i < new_array.size(); i++) {
            sb.clear();
            sb.append("ALTER TABLE ").append(tableCode).append(" ADD INDEX index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" (").append(new_array.getString(i).replace(";", ",")).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 修改索引
        for (int i = 0; i < modify_array.size(); i++) {
            final JSONObject json = modify_array.getJSONObject(i);

            final String code = json.getString("n");
            final String column = json.getString("c").replace(";", ",");

            sb.clear();
            sb.append("ALTER TABLE ").append(tableCode).append("  DROP INDEX ").append(code);
            result &= exeSQL(stmt, sb.toString(), errorSB);

            sb.clear();
            sb.append("ALTER TABLE ").append(tableCode).append(" ADD INDEX index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" (").append(column).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }
        return result;
    }

    /**
     * 修改数据库定义
     * 
     * @param stmt
     * @param ddlJSON
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private boolean modifyTable(final Statement stmt, final JSONObject ddlJSON, final StringBuilder errorSB) throws Exception {
        final String tableName = ddlJSON.getString("c");
        final StrBuilder sb = new StrBuilder();
        boolean result = true;

        Map<String, String> field2comment = new HashMap<String, String>();
        JSONArray jsonarray = ddlJSON.getJSONArray("cc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject json_comment = jsonarray.getJSONObject(i);
            field2comment.put(json_comment.getString("c"), json_comment.getString("comment"));
        }

        // 处理字段的重命名
        jsonarray = ddlJSON.getJSONArray("rc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject rc_json = jsonarray.getJSONObject(i);
            sb.clear().append("{call proc_design_rename_column_name('").append(tableName).append("','").append(rc_json.getString("old")).append("','").append(rc_json.getString("new")).append("','").append(rc_json.getString("comment")).append("')}");
            field2comment.remove(rc_json.getString("new"));
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        final ArrayList<String> uniqueKeys = new ArrayList<String>();
        String primaryKey = "";
        // 处理增加的字段
        jsonarray = ddlJSON.getJSONArray("nc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject nc_json = jsonarray.getJSONObject(i);
            String fieldName = nc_json.getString("c");
            sb.clear().append("alter table ").append(tableName).append(" add column ").append(fieldName).append(" ").append(getDataTypeStr(nc_json.getString("type"), nc_json.getInt("length")));
            if (nc_json.getBoolean("unnullable"))
                sb.append(" not null");
            else
                sb.append(" null");
            final String default_value = nc_json.getString("default");
            if (!StringUtils.isEmpty(default_value))
                sb.append(" default ").append(default_value);
            sb.append(" comment '").append(nc_json.getString("comment")).append("'");
            if (nc_json.getBoolean("primary"))
                primaryKey = fieldName;
            else if (nc_json.getBoolean("unique"))
                uniqueKeys.add(fieldName);
            result &= exeSQL(stmt, sb.toString(), errorSB);
            if (nc_json.getBoolean("primary"))
                primaryKey = fieldName;
            else if (nc_json.getBoolean("unique"))
                uniqueKeys.add(fieldName);
            field2comment.remove(fieldName);
        }

        // 处理删除的字段
        jsonarray = ddlJSON.getJSONArray("dc");
        for (int i = 0; i < jsonarray.size(); i++) {
            sb.clear().append("{call proc_design_remove_column_name('").append(tableName).append("','").append(jsonarray.getString(i)).append("')}");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 处理修改的字段
        final ArrayList<String> modifyFields = new ArrayList<String>();
        jsonarray = ddlJSON.getJSONArray("mc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject mc_json = jsonarray.getJSONObject(i);
            String fieldName = mc_json.getString("c");
            modifyFields.add(fieldName);
            final String default_value = mc_json.getString("default");

            String dataTypeStr = getDataTypeStr(mc_json.getString("type"), mc_json.getInt("length"));
            sb.clear().append("{call proc_design_change_column('").append(tableName).append("','").append(fieldName).append("','").append(dataTypeStr).append("',").append(mc_json.getBoolean("unnullable") ? 0 : 1).append(",'").append(default_value).append("','").append(mc_json.getString("comment")).append("')}");
            if (mc_json.getBoolean("primary"))
                primaryKey = fieldName;
            else if (mc_json.getBoolean("unique"))
                uniqueKeys.add(fieldName);
            result &= exeSQL(stmt, sb.toString(), errorSB);
            field2comment.remove(fieldName);
        }

        // 处理单独注释发生改变的字段
        for (Map.Entry<String, String> entry : field2comment.entrySet()) {
            sb.clear().append("{call proc_design_rename_column_name('").append(tableName).append("','").append(entry.getKey()).append("','").append(entry.getKey()).append("','").append(entry.getValue()).append("')}");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 处理Key值
        final Map<String, String> column2KeyIndex = new HashMap<String, String>();
        final HashMap<String, Boolean> keysType = new HashMap<String, Boolean>();
        getTableKeysInfo(stmt.getConnection(), tableName, column2KeyIndex, keysType);

        final Iterator<String> iterator = keysType.keySet().iterator();
        while (iterator.hasNext()) {
            final String fieldName = iterator.next();
            if (modifyFields.indexOf(fieldName) != -1) {
                final String index_name = column2KeyIndex.get(fieldName);
                final boolean isPrimary = keysType.get(fieldName);
                if (isPrimary) { // primary
                    if (!StringUtils.equals(primaryKey, fieldName)) {
                        sb.clear().append("{call proc_design_remove_column_ref('").append(tableName).append("','").append(fieldName).append("')}");
                        result &= exeSQL(stmt, sb.toString(), errorSB);
                        sb.clear().append("alter table ").append(tableName).append(" DROP PRIMARY KEY");
                        result &= exeSQL(stmt, sb.toString(), errorSB);
                    } else
                        primaryKey = "";
                } else { // unique
                    final int index = indexOf(uniqueKeys, fieldName);
                    if (index != -1)
                        uniqueKeys.remove(index);
                    else {
                        sb.clear().append("alter table ").append(tableName).append(" drop index ").append(index_name);
                        result &= exeSQL(stmt, sb.toString(), errorSB);
                    }
                }
            }
        }

        // 创建primary key值
        if (!StringUtils.isEmpty(primaryKey)) {
            sb.clear().append("alter table ").append(tableName).append(" ADD PRIMARY KEY(").append(primaryKey).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 创建unique key值
        for (int i = 0; i < uniqueKeys.size(); i++) {
            sb.clear().append("alter table ").append(tableName).append(" add UNIQUE INDEX index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" (").append(uniqueKeys.get(i)).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        exeSQL(stmt, "{CALL proc_design_inc_version('" + tableName + "')}", errorSB);

        return result;
    }

    /**
     * 达到数据库表key信息
     * 
     * @param connection
     * @param tableName
     * @param column2KeyIndex
     * @param keysType
     */
    private void getTableKeysInfo(final Connection con, final String tableName, final Map<String, String> column2KeyIndex, final HashMap<String, Boolean> keysType) {
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, SQL_GET_TABLE_KEYS, true);
            stmt.setParam(1, tableName);
            stmt.executeQuery();
            while (stmt.next()) {
                final String index_name = stmt.getString(1);
                final long type = stmt.getLong(2);
                final String columnName = stmt.getString(3);
                column2KeyIndex.put(columnName, index_name);
                keysType.put(columnName, type == 1);
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

        } finally {
            DBHELPER.closeCommand(stmt);
        }

    }

    /**
     * 检索字符串列表
     * 
     * @param list
     * @param str
     * @return
     */
    private int indexOf(final ArrayList<String> list, final String str) {
        int index = -1;
        for (int i = 0; i < list.size(); i++)
            if (StringUtils.equals(str, list.get(i))) {
                index = i;
                break;
            }
        return index;
    }

    /**
     * 创建表结构
     * 
     * @param stmt
     * @param ddlJSON
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private boolean createTable(final Statement stmt, final JSONObject ddlJSON, final StringBuilder errorSB) throws Exception {
        boolean result = true;
        final StrBuilder sb = new StrBuilder();
        final String code = ddlJSON.getString("c");
        sb.append("create table ").appendln(code);
        sb.appendln("(");
        JSONArray jsonArray = ddlJSON.getJSONArray("nc");
        String primaryKey = "";
        final ArrayList<String> uniqueKeys = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject json_column = jsonArray.getJSONObject(i);
            // 名称 数据类型
            sb.append("  ").append(json_column.getString("c")).append(" ").append(getDataTypeStr(json_column.getString("type"), json_column.getInt("length")));

            // 是否允许空
            if (json_column.getBoolean("unnullable"))
                sb.append(" not null");

            // 缺省值
            final String default_value = json_column.getString("default");
            if (!StringUtils.isEmpty(default_value))
                sb.append(" default ").append(default_value);

            // 注释
            sb.append(" comment '").append(json_column.getString("comment")).append("'");

            // 键值类型
            if (json_column.getBoolean("primary"))
                primaryKey = json_column.getString("c");
            else if (json_column.getBoolean("unique"))
                uniqueKeys.add(json_column.getString("c"));

            if ((i != (jsonArray.size() - 1)) || (uniqueKeys.size() != 0) || !StringUtils.isEmpty(primaryKey))
                sb.append(",");
            sb.appendln("");
        }

        if (!StringUtils.isEmpty(primaryKey))
            sb.append("  PRIMARY KEY (").append(primaryKey).append(")");
        if (uniqueKeys.size() != 0)
            sb.append(",\n");

        for (int i = 0; i < uniqueKeys.size(); i++) {
            sb.append("  UNIQUE INDEX uq_index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" (").append(uniqueKeys.get(i)).append(")");
            if (i != (uniqueKeys.size() - 1))
                sb.append(",");
            sb.append("\n");
        }
        sb.append(") comment = '").append(ddlJSON.getString("n")).append("'");
        result &= exeSQL(stmt, sb.toString(), errorSB);
        return result;
    }

    /**
     * @return
     */
    @Override
    protected String getTableIsExistsSQL() {
        return "select count(*) from view_sys_user_tables where upper(table_name)=upper(?)";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getDBEntryVersion(java
     * .lang.String)
     */
    @Override
    public JSONObject getDBEntryVersion(final String code) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            json.put("v", this.getDBEntryVersion(con, code));
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#dropDBEntry(java.lang
     * .String[])
     */
    @Override
    public JSONObject dropDBEntry(final String[] codes) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        Statement stmt = null;
        boolean result = true;
        final StringBuilder errorSB = new StringBuilder();
        try {
            con = DBHELPER.getConnection();
            stmt = con.createStatement();
            stmt.execute("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0");
            for (final String code : codes) {
                if (this.getDBEntryVersion(con, code) != -1) {
                    result &= exeSQL(stmt, "drop table " + code, errorSB);
                    result &= exeSQL(stmt, "call proc_drop_dbentry('" + code + "')", errorSB);
                }
                SystemCacheManager.getInstance().removeDesignObject("DBENTRY-" + code);
                json.put("r", result);
                json.put("msg", errorSB.toString());
            }
            incDDLVersion(stmt);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

            JSONUtils.except2JSON(json, e);
        } finally {
            if (stmt != null)
                exeSQL(stmt, "SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS", errorSB);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#createForeighKey(java
     * .lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public JSONObject createForeighKey(final String parentTable, final String parentField, final String childTable, final String childField) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        Statement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.createStatement();
            final StringBuilder sb = new StringBuilder();
            String indexName = "fk_index_" + DBHELPER.getUniqueID(con);
            sb.append("ALTER TABLE ").append(childTable).append(" ADD CONSTRAINT ").append(indexName).append(" FOREIGN KEY (").append(childField).append(") REFERENCES ").append(parentTable).append("(").append(parentField).append(") ON DELETE CASCADE ON UPDATE CASCADE");
            stmt.execute(sb.toString());
            json.put("n", indexName);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#dropDBEntryKeyIndex
     * (java.lang.String, java.lang.String)
     */
    @Override
    public JSONObject dropDBEntryKeyIndex(final String tableName, final String indexName) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        Statement stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = con.createStatement();
            final StringBuilder sb = new StringBuilder();
            sb.append("alter table ").append(tableName).append(" drop FOREIGN KEY ").append(indexName).append(",drop index ").append(indexName);
            stmt.execute(sb.toString().toLowerCase());
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#renameDBEntry(java.
     * lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public JSONObject renameDBEntry(final String oldCode, final String newCode, final String comment) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        Statement stmt = null;
        final StringBuilder errorSB = new StringBuilder();
        try {
            con = DBHELPER.getConnection();
            stmt = con.createStatement();
            exeSQL(stmt, "{call proc_rename_dbentry('" + oldCode + "','" + newCode + "','" + comment + "')}", errorSB);
            json.put("r", true);
            json.put("entryInfo", this.getEntryInfo(con, newCode, -1));
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * @return
     */
    @Override
    protected String getDiagramDBEntrySQL() {
        return "select b.name diagramname, a.name dbentryname from sys_db_diagram_item a, sys_db_diagram b where a.p_id = b.id order by b.sortorder, a.name";
    }

    @Override
    protected String getDBEntryConstraintSQL() {
        return "select index_name,Column_Name,case IndexType when 'PRIMARY_KEY_CONSTRAINT' then 'P' when 'UNIQUE_CONSTRAINT' then 'U' else ' ' end as indextype from view_sys_user_indexs where UPPER(table_name)=upper(?)";
    }

    @Override
    protected String getDBEntryColumnsSQL() {
        return "select column_name,data_type,data_length,ABS(is_nullable-1) notnullable,column_comment comments,replace(replace(default_value,'((',''),'))','') data_default from view_sys_user_tab_columns where UPPER(table_name)=UPPER(?) order by column_order";
    }

    @Override
    protected String getEntryCodeAndNameSQL() {
        return "select IFNULL(table_comment,'') table_comment,fun_design_get_version(table_name) as table_version from view_sys_user_tables where upper(table_name) = upper(:table_name)";
    }

    @Override
    protected String getDDLVersionSQL() {
        return "SELECT MAX(fun_design_get_version(table_name)) FROM view_sys_user_tables";
    }

    @Override
    protected String getDBEntrysSQL() {
        return "select table_name,table_comment from view_sys_user_tables where upper(table_name) not in (select UPPER(OBJECT_NAME) from SYS_DB_OBJECTS)";
    }

    @Override
    protected String getDBEntryIndexSQL() {
        return "SELECT INDEX_NAME,GROUP_CONCAT(column_name) columnNames FROM view_sys_user_indexs WHERE  indextype NOT IN ('PRIMARY_KEY_CONSTRAINT','UNIQUE_CONSTRAINT') AND upper(TABLE_NAME)=upper(:table_name)  GROUP BY INDEX_NAME";
    }

    @Override
    protected String getDBEntryForeignKeys() {
        return "select * from view_sys_user_fks";
    }

    @Override
    protected String getDBEntryVersionSQL() {
        return "select fun_design_get_version(?)";
    }

    @Override
    protected String getIncDDLVersionSQL() {
        return "{call proc_design_inc_ddl_version()}";
    }

    @Override
    protected int getMaxObjectNameLength() {
        return 63;
    }

    @Override
    protected boolean getObjectNameIsUpperCaseStyle() {
        return false;
    }

    @Override
    protected String getDBEntryPchFileName() {
        return "mysqldbentrypch.json";
    }

    @Override
    protected String getLexFileName() {
        return "mysql.lex";
    }

    @Override
    protected String getDatabaseType() {
        return "MySQL";
    }

    /**
     * 构造函数
     */
    private DBEntryService4MySQL() {
        super();
        supportDBFieldDataTypes.add(new DBFieldDataType("varchar", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 8000, DBParamDataType.String, "可变长度字符串"));
        supportDBFieldDataTypes.add(new DBFieldDataType("char", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 4000, DBParamDataType.String, "固定长度字符串"));
        supportDBFieldDataTypes.add(new DBFieldDataType("int", DBFieldDataType.categoryNumber, DataType2JavaDataType.Integer, true, true, true, true, true, 0, 0, DBParamDataType.Int, "32位整数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("bigint", DBFieldDataType.categoryNumber, DataType2JavaDataType.Long, true, true, true, true, true, 0, 0, DBParamDataType.Long, "64位长整数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("smallint", DBFieldDataType.categoryNumber, DataType2JavaDataType.Long, true, true, true, true, true, 0, 0, DBParamDataType.Long, "64位长整数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("mediumint", DBFieldDataType.categoryNumber, DataType2JavaDataType.Long, true, true, true, true, true, 0, 0, DBParamDataType.Long, "64位长整数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("double", DBFieldDataType.categoryNumber, DataType2JavaDataType.Double, true, true, true, true, true, 0, 0, DBParamDataType.Double, "双精浮点数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("float", DBFieldDataType.categoryNumber, DataType2JavaDataType.Float, true, true, true, true, true, 0, 0, DBParamDataType.Float, "单精浮点数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("decimal", DBFieldDataType.categoryNumber, DataType2JavaDataType.Decimal, true, true, true, true, true, 0, 0, DBParamDataType.Decimal, "十进制数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("datetime", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.DateTime, "日期时间"));
        supportDBFieldDataTypes.add(new DBFieldDataType("date", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.Date, "日期"));
        supportDBFieldDataTypes.add(new DBFieldDataType("date", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.Date, "日期"));
        supportDBFieldDataTypes.add(new DBFieldDataType("timestamp", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.Date, "日期"));
        supportDBFieldDataTypes.add(new DBFieldDataType("time", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.Date, "日期"));
        supportDBFieldDataTypes.add(new DBFieldDataType("text", DBFieldDataType.categoryBinary, DataType2JavaDataType.String, false, false, false, false, true, 0, 0, DBParamDataType.String, "不限长度字符串"));
        supportDBFieldDataTypes.add(new DBFieldDataType("blob", DBFieldDataType.categoryBinary, DataType2JavaDataType.Bytes, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "二进制字段2G"));
        supportDBFieldDataTypes.add(new DBFieldDataType("mediumblob", DBFieldDataType.categoryBinary, DataType2JavaDataType.Bytes, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "二进制字段16M"));
        supportDBFieldDataTypes.add(new DBFieldDataType("longblob", DBFieldDataType.categoryBinary, DataType2JavaDataType.Bytes, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "二进制字段4G"));
    }

    private static final IDBEntryService INSTANCE = new DBEntryService4MySQL();

    public static IDBEntryService getInstance() {
        return INSTANCE;
    }

}
