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

import com.estudio.context.SystemCacheManager;
import com.estudio.define.db.DBException;
import com.estudio.define.design.db.DBFieldDataType;
import com.estudio.define.design.db.DBFieldDataType.DataType2JavaDataType;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.design.db.IDBEntryService;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public final class DBEntryService4Oracle extends DBEntryService {

    private static final String SQL_GET_TABLE_KEYS = "select c.constraint_name, decode(t.constraint_type, 'P', 1, 0), c.column_name from user_constraints t, user_cons_columns c where c.constraint_name = t.constraint_name and c.column_name not like 'SYS_NC%$' and t.constraint_type in ('P', 'U') and t.table_name = ?";

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

        for (int i = 0; i < drop_array.size(); i++) {
            sb.clear();
            sb.append("drop index ").append(drop_array.getString(i));
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        for (int i = 0; i < new_array.size(); i++) {
            sb.clear();
            sb.append("create index idx_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" on ").append(tableCode).append(" (").append(new_array.getString(i).replace(";", ",")).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        for (int i = 0; i < modify_array.size(); i++) {
            final JSONObject json = modify_array.getJSONObject(i);

            final String code = json.getString("n");
            final String column = json.getString("c").replace(";", ",");

            sb.clear();
            sb.append("drop index ").append(code);
            result &= exeSQL(stmt, sb.toString(), errorSB);

            sb.clear();
            sb.append("create index ").append(code).append(" on ").append(tableCode).append(" (").append(column).append(")");
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

        // 处理字段的重命名
        JSONArray jsonarray = ddlJSON.getJSONArray("rc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject rc_json = jsonarray.getJSONObject(i);
            sb.clear().appendln("alter table ").append(tableName).append(" rename column ").append(rc_json.getString("old")).append(" to ").append(rc_json.getString("new")).append("");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        final ArrayList<String> uniqueKeys = new ArrayList<String>();
        String primaryKey = "";

        // 处理增加的字段
        jsonarray = ddlJSON.getJSONArray("nc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject nc_json = jsonarray.getJSONObject(i);
            sb.clear().append("alter table ").append(tableName).append(" add ").append(nc_json.getString("c")).append(" ").append(getDataTypeStr(nc_json.getString("type"), nc_json.getInt("length")));
            final String default_value = nc_json.getString("default");
            if (!StringUtils.isEmpty(default_value))
                sb.append(" default ").append(default_value);
            if (nc_json.getBoolean("unnullable"))
                sb.append(" not null");

            if (nc_json.getBoolean("primary"))
                primaryKey = nc_json.getString("c");
            else if (nc_json.getBoolean("unique"))
                uniqueKeys.add(nc_json.getString("c"));
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 处理删除的字段
        jsonarray = ddlJSON.getJSONArray("dc");
        for (int i = 0; i < jsonarray.size(); i++) {
            sb.clear().append("alter table ").append(tableName).append(" drop column ").append(jsonarray.getString(i));
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 处理修改的字段
        final ArrayList<String> modifyFields = new ArrayList<String>();
        final HashMap<String, Boolean> fieldCode2NotNULL = getTableFieldNotNULL(stmt.getConnection(), tableName);
        jsonarray = ddlJSON.getJSONArray("mc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject mc_json = jsonarray.getJSONObject(i);
            modifyFields.add(mc_json.getString("c"));

            sb.clear().append("alter table ").append(tableName).append(" modify ").append(mc_json.getString("c")).append(" ").append(getDataTypeStr(mc_json.getString("type"), mc_json.getInt("length")));

            final String default_value = mc_json.getString("default");
            if (!StringUtils.isEmpty(default_value))
                sb.append(" default ").append(default_value);
            else
                sb.append(" default null");

            if ((fieldCode2NotNULL.get(mc_json.getString("c")) != null) && mc_json.containsKey("unnullable") && (fieldCode2NotNULL.get(mc_json.getString("c")) != mc_json.getBoolean("unnullable")))
                sb.append(fieldCode2NotNULL.get(mc_json.getString("c")) ? " null" : " not null");

            if (mc_json.getBoolean("primary"))
                primaryKey = mc_json.getString("c");
            else if (mc_json.getBoolean("unique"))
                uniqueKeys.add(mc_json.getString("c"));
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
                        sb.clear().append("alter table ").append(tableName).append(" drop constraint ").append(index_name).append(" cascade");
                        result &= exeSQL(stmt, sb.toString(), errorSB);
                    } else
                        primaryKey = "";
                } else { // unique
                    final int index = indexOf(uniqueKeys, fieldName);
                    if (index != -1)
                        uniqueKeys.remove(index);
                    else {
                        sb.clear().append("alter table ").append(tableName).append(" drop constraint ").append(index_name).append(" cascade");
                        result &= exeSQL(stmt, sb.toString(), errorSB);
                    }
                }
            }
        }

        // 创建primary key值
        if (!StringUtils.isEmpty(primaryKey)) {
            sb.clear().append("alter table ").append(tableName).append(" add constraint index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" primary key (").append(primaryKey).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 创建unique key值
        for (int i = 0; i < uniqueKeys.size(); i++) {
            sb.clear().append("alter table ").append(tableName).append(" add constraint index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" unique (").append(uniqueKeys.get(i)).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 生成注释
        jsonarray = ddlJSON.getJSONArray("cc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject json_comment = jsonarray.getJSONObject(i);
            final String ddlSQL = "comment on column " + tableName + "." + json_comment.getString("c") + " is '" + json_comment.getString("comment") + "'";
            result &= exeSQL(stmt, ddlSQL, errorSB);
        }

        // 生成注释信息
        result &= exeSQL(stmt, "comment on table " + tableName + " is '" + ddlJSON.getString("n") + "'", errorSB);

        // 修改表结构版本信息
        sb.clear().append("alter table ").append(tableName).append(" add temp_column_4_inc_version integer");
        result &= exeSQL(stmt, sb.toString(), errorSB);

        // 修改表结构版本信息
        sb.clear().append("alter table ").append(tableName).append(" drop column temp_column_4_inc_version");
        result &= exeSQL(stmt, sb.toString(), errorSB);

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
                keysType.put(columnName, type == 0);
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

        } finally {
            DBHELPER.closeCommand(stmt);
        }

    }

    /**
     * 得到字段是否NOT NULL
     * 
     * @param connection
     * @param tableName
     * @return
     * @throws SQLException
     *             , DBException
     */
    private HashMap<String, Boolean> getTableFieldNotNULL(final Connection con, final String tableName) {
        final HashMap<String, Boolean> result = new HashMap<String, Boolean>();
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, "select nullable,column_name from user_tab_columns where table_name=?", true);
            stmt.setParam(1, tableName);
            stmt.executeQuery();
            while (stmt.next())
                result.put(stmt.getString(2), StringUtils.equals("N", stmt.getString(1)));
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

        } finally {
            DBHELPER.closeCommand(stmt);
        }
        return result;
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
            sb.append("  ").append(json_column.getString("c")).append(" ").append(getDataTypeStr(json_column.getString("type"), json_column.getInt("length")));

            final String default_value = json_column.getString("default");
            if (!StringUtils.isEmpty(default_value))
                sb.append(" default ").append(default_value);

            if (json_column.getBoolean("unnullable"))
                sb.append(" not null");

            if (json_column.getBoolean("primary"))
                primaryKey = json_column.getString("c");
            else if (json_column.getBoolean("unique"))
                uniqueKeys.add(json_column.getString("c"));

            if ((i != (jsonArray.size() - 1)) || (uniqueKeys.size() != 0) || !StringUtils.isEmpty(primaryKey))
                sb.append(",");
            sb.appendln("");
        }

        if (!StringUtils.isEmpty(primaryKey))
            sb.append("  ").append("CONSTRAINT index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" PRIMARY KEY (").append(primaryKey).append(")");
        if (uniqueKeys.size() != 0)
            sb.append(",\n");

        for (int i = 0; i < uniqueKeys.size(); i++) {
            sb.append("  CONSTRAINT index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" UNIQUE (").append(uniqueKeys.get(i)).append(")");
            if (i != (uniqueKeys.size() - 1))
                sb.append(",");
            sb.append("\n");
        }
        sb.appendln(")");
        result &= exeSQL(stmt, sb.toString(), errorSB);

        sb.clear();

        final String tableName = ddlJSON.getString("c");
        // 生成注释信息
        result &= exeSQL(stmt, "comment on table " + tableName + " is '" + ddlJSON.getString("n") + "'", errorSB);

        jsonArray = ddlJSON.getJSONArray("cc");
        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject json_comment = jsonArray.getJSONObject(i);
            final String ddlSQL = "comment on column " + tableName + "." + json_comment.getString("c") + " is '" + json_comment.getString("comment") + "'";
            result &= exeSQL(stmt, ddlSQL, errorSB);
        }

        return result;
    }

    /**
     * @return
     */
    @Override
    protected String getTableIsExistsSQL() {
        return "select count(*) from user_tables where table_name=?";
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
            for (final String code : codes) {
                if (this.getDBEntryVersion(con, code) != -1) {
                    result &= exeSQL(stmt, "drop table " + code + " cascade constraints", errorSB);
                    result &= exeSQL(stmt, "begin proc_drop_dbentry('" + code + "'); end;", errorSB);
                }

                SystemCacheManager.getInstance().removeDesignObject("DBENTRY-" + code);
                json.put("r", result);
                json.put("msg", errorSB.toString());
            }
            result &= exeSQL(stmt, "alter table SYS_UTILS_4_DDL_VERSION add name integer", errorSB);
            result &= exeSQL(stmt, "alter table SYS_UTILS_4_DDL_VERSION drop column name", errorSB);
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
            String indexName = "index_" + DBHELPER.getUniqueID(con);
            sb.append("alter table ").append(childTable).append(" add constraint ").append(indexName).append(" foreign key (").append(childField).append(") references ").append(parentTable).append(" (").append(parentField).append(") on delete cascade");
            stmt.execute(sb.toString());
            json.put("r", true);
            json.put("n", indexName);
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
            sb.append("alter table ").append(tableName).append(" drop constraint ").append(indexName);
            stmt.execute(sb.toString());
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
        boolean result = true;
        try {
            con = DBHELPER.getConnection();
            stmt = con.createStatement();
            if (!StringUtils.equals(oldCode, newCode)) {
                result &= exeSQL(stmt, "rename " + oldCode + " to " + newCode, errorSB);
                if (result)
                    result &= exeSQL(stmt, "begin proc_rename_dbentry('" + oldCode + "','" + newCode + "'); end;", errorSB);
            }
            result &= exeSQL(stmt, "COMMENT ON table " + (result ? newCode : oldCode) + " is '" + comment + "'", errorSB);

            // 强制更新表版本
            result &= exeSQL(stmt, "alter table " + newCode + " add F$$_$$_ varchar2(1)", errorSB);
            result &= exeSQL(stmt, "alter table " + newCode + " drop column F_$$_$$_", errorSB);

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
        return "select t.constraint_name, c.column_name, t.constraint_type from user_constraints t, user_cons_columns c where c.constraint_name = t.constraint_name and c.column_name not like 'SYS_NC%$' and t.constraint_type in ('P', 'U')  and t.table_name = ?";
    }

    @Override
    protected String getDBEntryColumnsSQL() {
        return "select a.column_name, data_type, b.DATA_LENGTH, decode(nullable,'Y',0,1) notnullable, comments, data_default from user_col_comments a, user_tab_columns b where a.table_name = b.TABLE_NAME and a.column_name = b.COLUMN_NAME and a.table_name = ? order by b.COLUMN_ID";
    }

    @Override
    protected String getEntryCodeAndNameSQL() {
        return "select nvl(comments,table_name),to_number(to_char(b.last_ddl_time,'YYYYMMDDHH24MISS'))  from user_objects b, user_tab_comments a where a.table_name=b.object_name and a.table_name=:table_name";
    }

    @Override
    protected String getDDLVersionSQL() {
        return "select to_number(to_char(max(last_ddl_time),'YYYYMMDDHH24MISS')) from user_objects where OBJECT_TYPE='TABLE'";
    }

    @Override
    protected String getDBEntrysSQL() {
        return "select a.table_name,a.comments from user_tab_comments a,user_tables b where a.table_name = b.table_name and b.TABLE_NAME not like 'MDRT_%$' and b.TABLE_NAME not like 'SPATIAL_FS_%' and b.TABLE_NAME not like 'SPATIAL_STAT_%' and b.table_name not in (select object_name from sys_db_objects)";
    }

    @Override
    protected String getDBEntryIndexSQL() {
        return "select index_name,(select wm_concat(column_name) from user_ind_columns where index_name = user_indexes.index_name) column_name from user_indexes where index_type='NORMAL' and uniqueness = 'NONUNIQUE' and table_name=?";
    }

    @Override
    protected String getDBEntryForeignKeys() {
        return "select * from (select a.constraint_name, p.TABLE_NAME p_table_name,p.COLUMN_NAME p_column_name,c.table_name c_table_name,c.column_name c_column_name,d.id i1,e.id i2 from sys_db_objects d,sys_db_objects e,user_cons_columns p, user_cons_columns c, user_constraints a where d.object_name(+) = p.table_name and e.object_name(+) = c.table_name and a.constraint_name = c.constraint_name and a.r_constraint_name = p.CONSTRAINT_NAME and constraint_type = 'R') t where t.i1 is null and t.i2 is null";
    }

    @Override
    protected String getDBEntryVersionSQL() {
        return "select to_number(to_char(last_ddl_time,'YYYYMMDDHH24MISS')) from user_objects b where b.object_type='TABLE' and object_name=?";
    }

    @Override
    protected String getIncDDLVersionSQL() {
        return "comment on table SYS_DB_DIAGRAM is '数据库模型'";
    }

    @Override
    protected int getMaxObjectNameLength() {
        return 30;
    }

    @Override
    protected boolean getObjectNameIsUpperCaseStyle() {
        return true;
    }

    @Override
    protected String getDBEntryPchFileName() {
        return "oracledbentrypch.json";
    }

    @Override
    protected String getLexFileName() {
        return "oracle.lex";
    }

    @Override
    protected String getDatabaseType() {
        return "Oracle";
    }

    /**
     * 构造函数
     */
    private DBEntryService4Oracle() {
        super();
        supportDBFieldDataTypes.add(new DBFieldDataType("varchar2", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 4000, DBParamDataType.String, "可变长度字符串"));
        supportDBFieldDataTypes.add(new DBFieldDataType("nvarchar2", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 4000, DBParamDataType.String, "可变长度字符串(UNICODE)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("char", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 4000, DBParamDataType.String, "固定长度字符串"));
        supportDBFieldDataTypes.add(new DBFieldDataType("nchar", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 4000, DBParamDataType.String, "固定长度字符串(UNICODE)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("date", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.Date, "日期时间"));
        supportDBFieldDataTypes.add(new DBFieldDataType("timestamp", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.Date, "高精度日期时间"));
        supportDBFieldDataTypes.add(new DBFieldDataType("number", DBFieldDataType.categoryNumber, DataType2JavaDataType.Double, true, true, true, true, true, 0, 0, DBParamDataType.Long, "高精度数字"));
        supportDBFieldDataTypes.add(new DBFieldDataType("blob", DBFieldDataType.categoryBinary, DataType2JavaDataType.Bytes, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "二进制大字段"));
        supportDBFieldDataTypes.add(new DBFieldDataType("clob", DBFieldDataType.categoryBinary, DataType2JavaDataType.String, false, false, false, false, true, 0, 0, DBParamDataType.String, "不限长度字符串"));
        DBHELPER = DBHelper4Oracle.getInstance();
    }

    private static final IDBEntryService INSTANCE = new DBEntryService4Oracle();

    public static IDBEntryService getInstance() {
        return INSTANCE;
    }

}
