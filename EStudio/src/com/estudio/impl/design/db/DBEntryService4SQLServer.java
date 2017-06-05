package com.estudio.impl.design.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import com.estudio.context.SystemCacheManager;
import com.estudio.define.db.DBException;
import com.estudio.define.design.db.DBFieldDataType;
import com.estudio.define.design.db.DBFieldDataType.DataType2JavaDataType;
import com.estudio.impl.db.DBHelper4SQLServer;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.db.IDBEntryService;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public final class DBEntryService4SQLServer extends DBEntryService {
    private static final IDBHelper DBHELPER = DBHelper4SQLServer.getInstance();

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
            sb.append("drop index ").append(drop_array.getString(i)).append(" on ").append(tableCode);
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        for (int i = 0; i < new_array.size(); i++) {
            sb.clear();
            sb.append("create index index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" on ").append(tableCode).append(" (").append(new_array.getString(i).replace(";", ",")).append(")");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        for (int i = 0; i < modify_array.size(); i++) {
            final JSONObject json = modify_array.getJSONObject(i);

            final String code = json.getString("n");
            final String column = json.getString("c").replace(";", ",");

            sb.clear();
            sb.append("drop index ").append(code).append(" on ").append(tableCode);
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

        // 获取关系
        List<String> refSQLList = new ArrayList<String>();
        sb.clear().append("select index_name,p_tablename,p_columnname,c_tablename,c_columnname from view_sys_user_fks where UPPER('").append(tableName).append("') in (upper(p_tablename),upper(c_tablename))");
        ResultSet rs = stmt.executeQuery(sb.toString());
        while (rs.next()) {
            refSQLList.add("alter table " + rs.getString(4) + " drop constraint " + rs.getString(1));
            refSQLList.add("alter table " + rs.getString(4) + " add constraint " + rs.getString(1) + " FOREIGN KEY (" + rs.getString(5) + ") REFERENCES " + rs.getString(2) + "(" + rs.getString(3) + ")" + rs.getString(1));
        }
        rs.close();

        // 处理字段的重命名
        JSONArray jsonarray = ddlJSON.getJSONArray("rc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject rc_json = jsonarray.getJSONObject(i);
            sb.clear().append("{call sp_rename ('").append(tableName).append(".").append(rc_json.getString("old")).append("','").append(rc_json.getString("new")).append("','COLUMN')}");
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
            sb.clear().append("{call proc_drop_dbentry_column('").append(tableName).append("','").append(jsonarray.getString(i)).append("')}");
            result &= exeSQL(stmt, sb.toString(), errorSB);
        }

        // 处理修改的字段
        final ArrayList<String> modifyFields = new ArrayList<String>();
        final HashMap<String, Boolean> fieldCode2NotNULL = getTableFieldNotNULL(stmt.getConnection(), tableName);
        jsonarray = ddlJSON.getJSONArray("mc");
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject mc_json = jsonarray.getJSONObject(i);
            String columnName = mc_json.getString("c");
            modifyFields.add(columnName);

            // 删除所有的约束
            sb.clear().append("{call proc_drop_dbentry_column_constraint('").append(tableName).append("','").append(columnName).append("')}");
            result &= exeSQL(stmt, sb.toString(), errorSB);

            // 修改字段信息
            sb.clear().append("alter table ").append(tableName).append(" alter column ").append(columnName).append(" ").append(getDataTypeStr(mc_json.getString("type"), mc_json.getInt("length")));
            if ((fieldCode2NotNULL.get(columnName) != null) && mc_json.containsKey("unnullable") && (fieldCode2NotNULL.get(columnName) != mc_json.getBoolean("unnullable")))
                sb.append(fieldCode2NotNULL.get(columnName) ? " null" : " not null");

            result &= exeSQL(stmt, sb.toString(), errorSB);
            // 缺省值
            final String default_value = mc_json.getString("default");
            if (!StringUtils.isEmpty(default_value)) {
                sb.clear().append("ALTER TABLE ").append(tableName).append(" ADD CONSTRAINT DF_CONSTRAINT_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" DEFAULT ").append(default_value).append(" FOR ").append(columnName);
                result &= exeSQL(stmt, sb.toString(), errorSB);
            }

            if (mc_json.getBoolean("primary"))
                primaryKey = columnName;
            else if (mc_json.getBoolean("unique"))
                uniqueKeys.add(columnName);
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
        Map<String, String> column2Comment = new HashMap<String, String>();
        for (int i = 0; i < jsonarray.size(); i++) {
            final JSONObject json_comment = jsonarray.getJSONObject(i);
            column2Comment.put(json_comment.getString("c"), json_comment.getString("comment"));
        }
        result &= commentTableColumn(stmt.getConnection(), tableName, column2Comment, errorSB);

        // 生成注释信息
        result &= commentTable(stmt.getConnection(), tableName, ddlJSON.getString("n"), errorSB);

        // 修改表结构版本信息
        sb.clear().append("alter table ").append(tableName).append(" add temp_column_4_inc_version int");
        result &= exeSQL(stmt, sb.toString(), errorSB);

        // 修改表结构版本信息
        sb.clear().append("alter table ").append(tableName).append(" drop column temp_column_4_inc_version");
        result &= exeSQL(stmt, sb.toString(), errorSB);

        // 最后生成关系
        for (String sql : refSQLList)
            result &= exeSQL(stmt, sql, errorSB);

        return result;
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
            stmt = DBHELPER.getCommand(con, "select is_nullable, column_name from view_sys_user_tab_columns where upper(table_name) = UPPER(?)", true);
            stmt.setParam(1, tableName);
            stmt.executeQuery();
            while (stmt.next())
                result.put(stmt.getString(2), stmt.getInt(1) == 0);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

        } finally {
            DBHELPER.closeCommand(stmt);
        }
        return result;
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
            sb.append("  CONSTRAINT ").append("index_").append(DBHELPER.getUniqueID(stmt.getConnection())).append(" PRIMARY KEY (").append(primaryKey).append(")");
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
        // 表
        result &= commentTable(stmt.getConnection(), tableName, ddlJSON.getString("n"), errorSB);

        // 字段
        jsonArray = ddlJSON.getJSONArray("cc");
        Map<String, String> column2Comment = new HashMap<String, String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject json_comment = jsonArray.getJSONObject(i);
            column2Comment.put(json_comment.getString("c"), json_comment.getString("comment"));
        }
        result &= commentTableColumn(stmt.getConnection(), tableName, column2Comment, errorSB);

        return result;
    }

    /**
     * 注释数据库表
     * 
     * @param con
     * @param tableName
     * @param comment
     * @param errorSb
     * @return
     */
    public boolean commentTable(Connection con, String tableName, String comment, StringBuilder errorSb) {
        boolean result = false;
        IDBCommand cmd = null;
        try {
            String SQL = "declare @c int;\n" + //
                    "  select @c = COUNT(*) from sys.extended_properties where name='MS_Description' and minor_id=0 and major_id=OBJECT_ID(:table_name);\n" + //
                    "if @c<>0\n" + //
                    "begin\n" + //
                    "  execute sp_dropextendedproperty 'MS_Description','SCHEMA','dbo','table', :table_name\n" + //
                    "end\n" + //
                    "execute sp_addextendedproperty  'MS_Description',:comment,'user','dbo','table',:table_name,null,null";//
            cmd = DBHELPER.getCommand(con, SQL);
            cmd.setParam("table_name", tableName);
            cmd.setParam("comment", StringUtils.isEmpty(comment) ? "" : comment);
            cmd.execute();
            result = true;
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);

        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    /**
     * 注释数据库数据列
     * 
     * @param con
     * @param tableName
     * @param columnName2Comment
     * @param errorSb
     * @return
     */

    public boolean commentTableColumn(Connection con, String tableName, Map<String, String> columnName2Comment, StringBuilder errorSb) {
        boolean result = false;
        IDBCommand cmd = null;
        try {
            String SQL = "declare @c int;\n" + //
                    "select @c=COUNT(*) from sys.extended_properties t\n" + //
                    "  where t.name='MS_Description' and t.major_id = OBJECT_ID(:table_name)\n" + //
                    "        and t.minor_id in (select column_id from sys.all_columns where UPPER(name)=upper(:field_name) and object_id=t.major_id)\n" + //
                    "if @c<>0\n" + //
                    "begin\n" + //
                    "    EXEC sp_dropextendedproperty 'MS_Description','user', 'dbo', 'TABLE', :table_name, 'COLUMN', :field_name\n" + //
                    "end\n" + //
                    "EXEC sp_addextendedproperty 'MS_Description', :comment ,'user', 'dbo', 'TABLE', :table_name, 'COLUMN', :field_name\n";
            cmd = DBHELPER.getCommand(con, SQL);
            cmd.setParam("table_name", tableName);
            for (Map.Entry<String, String> entry : columnName2Comment.entrySet()) {
                cmd.setParam("comment", entry.getValue());
                cmd.setParam("field_name", entry.getKey());
                cmd.execute();
            }
            result = true;
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            ExceptionUtils.printExceptionTrace(e);
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return result;
    }

    /**
     * @return
     */
    @Override
    protected String getTableIsExistsSQL() {
        return "select COUNT(*) c from sys.objects where object_id = OBJECT_ID(?)";
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
                if (this.getDBEntryVersion(con, code) != -1)
                    result &= exeSQL(stmt, "{call proc_drop_dbentry('" + code + "')}", errorSB);
                SystemCacheManager.getInstance().removeDesignObject("DBENTRY-" + code);
                json.put("r", result);
                json.put("msg", errorSB.toString());
            }
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
        try {
            con = DBHELPER.getConnection();
            stmt = con.createStatement();
            if (!StringUtils.equals(oldCode, newCode))
                exeSQL(stmt, "{call proc_rename_dbentry('" + oldCode + "', '" + newCode + "')}", errorSB);
            commentTable(con, newCode, comment, errorSB);
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
        return "select index_name,ColumnName,case IndexType when 'PRIMARY_KEY_CONSTRAINT' then 'P' when 'UNIQUE_CONSTRAINT' then 'U' else ' ' end as indextype from view_sys_user_indexs where UPPER(table_name)=upper(?)";
    }

    @Override
    protected String getDBEntryColumnsSQL() {
        return "select column_name,data_type,data_length,ABS(is_nullable-1) notnullable,column_comment comments,replace(replace(default_value,'((',''),'))','') data_default from view_sys_user_tab_columns where UPPER(table_name)=UPPER(?) order by column_order";
    }

    @Override
    protected String getEntryCodeAndNameSQL() {
        return "select table_comment,dbo.fun_date_to_bigint(ISNULL(modify_date,create_date)) as table_version from view_sys_user_tables where upper(table_name) = upper(:table_name)";
    }

    @Override
    protected String getDDLVersionSQL() {
        return "select dbo.fun_date_to_bigint(MAX(isnull(modify_date,create_date))) as table_version from sys.objects where type='U'";
    }

    @Override
    protected String getDBEntrysSQL() {
        return "select table_name,table_comment from view_sys_user_tables where upper(table_name) not in (select UPPER(OBJECT_NAME) from dbo.SYS_DB_OBJECTS)";
    }

    @Override
    protected String getDBEntryIndexSQL() {
        return "select index_name, columnNames=stuff((select ','+columnName from view_sys_user_indexs t where index_name=tb.index_name for xml path('')), 1, 1, '') from view_sys_user_indexs tb where IndexType not in ('PRIMARY_KEY_CONSTRAINT','UNIQUE_CONSTRAINT') and upper(table_name)=UPPER(?) group by index_name";
    }

    @Override
    protected String getDBEntryForeignKeys() {
        return "select * from view_sys_user_fks";
    }

    @Override
    protected String getDBEntryVersionSQL() {
        return "select dbo.fun_date_to_bigint(MAX(isnull(modify_date,create_date))) as table_version from sys.objects where upper(name)=upper(?)";
    }

    @Override
    protected String getIncDDLVersionSQL() {
        return "";
    }

    @Override
    protected int getMaxObjectNameLength() {
        return 128;
    }

    @Override
    protected boolean getObjectNameIsUpperCaseStyle() {
        return false;
    }

    @Override
    protected String getDBEntryPchFileName() {
        return "sqlserverdbentrypch.json";
    }

    @Override
    protected String getLexFileName() {
        return "sqlserver.lex";
    }

    @Override
    protected String getDatabaseType() {
        return "SQLServer";
    }

    /**
     * 构造函数
     */
    private DBEntryService4SQLServer() {
        super();
        supportDBFieldDataTypes.add(new DBFieldDataType("varchar", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 8000, DBParamDataType.String, "可变长度字符串"));
        supportDBFieldDataTypes.add(new DBFieldDataType("nvarchar", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 8000, DBParamDataType.String, "可变长度字符串(unicode)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("datetime", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.DateTime, "日期时间"));
        supportDBFieldDataTypes.add(new DBFieldDataType("bigint", DBFieldDataType.categoryNumber, DataType2JavaDataType.Long, true, true, true, true, true, 0, 0, DBParamDataType.Long, "64位长整数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("float", DBFieldDataType.categoryNumber, DataType2JavaDataType.Float, true, true, true, true, true, 0, 0, DBParamDataType.Float, "单精浮点数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("real", DBFieldDataType.categoryNumber, DataType2JavaDataType.Double, true, true, true, true, true, 0, 0, DBParamDataType.Double, "双精浮点数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("char", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 4000, DBParamDataType.String, "固定长度字符串"));
        supportDBFieldDataTypes.add(new DBFieldDataType("nchar", DBFieldDataType.categoryString, DataType2JavaDataType.String, true, true, true, true, false, 1, 4000, DBParamDataType.String, "固定长度字符串(unicode)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("date", DBFieldDataType.categoryDateTime, DataType2JavaDataType.DateTime, true, true, true, true, true, 0, 0, DBParamDataType.Date, "日期"));
        supportDBFieldDataTypes.add(new DBFieldDataType("int", DBFieldDataType.categoryNumber, DataType2JavaDataType.Integer, true, true, true, true, true, 0, 0, DBParamDataType.Int, "整数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("decimal", DBFieldDataType.categoryNumber, DataType2JavaDataType.Decimal, true, true, true, true, true, 0, 0, DBParamDataType.Decimal, "十进制数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("numeric", DBFieldDataType.categoryNumber, DataType2JavaDataType.Decimal, true, true, true, true, true, 0, 0, DBParamDataType.Decimal, "十进制数"));
        supportDBFieldDataTypes.add(new DBFieldDataType("image", DBFieldDataType.categoryBinary, DataType2JavaDataType.Bytes, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "二进制字段(2G)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("binary", DBFieldDataType.categoryBinary, DataType2JavaDataType.Bytes, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "二进制字段(8000)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("varbinary", DBFieldDataType.categoryBinary, DataType2JavaDataType.Bytes, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "二进制字段(2G)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("text", DBFieldDataType.categoryBinary, DataType2JavaDataType.String, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "长字符串(2G)"));
        supportDBFieldDataTypes.add(new DBFieldDataType("ntext", DBFieldDataType.categoryBinary, DataType2JavaDataType.String, false, false, false, false, true, 0, 0, DBParamDataType.Bytes, "长字符串(2G unicode)"));
    }

    private static final IDBEntryService INSTANCE = new DBEntryService4SQLServer();

    public static IDBEntryService getInstance() {
        return INSTANCE;
    }

}
