package com.estudio.impl.design.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.impl.db.DBSqlUtils;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;

public final class DBSQLParserService4MySQL extends DBSQLParserService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static DBSQLParserService4MySQL instance = new DBSQLParserService4MySQL();

    public static DBSQLParserService4MySQL getInstance() {
        return instance;
    }

    private DBSQLParserService4MySQL() {
        super();
    }

    /**
     * 得到数据库表字段注释
     * 
     * @param con
     * @param tables
     * @return
     * @throws SQLException
     *             , DBException
     */
    @Override
    protected Map<String, String> getTablesFieldComment(final Connection con, final String[] tables) throws Exception {
        final Map<String, String> fieldName2Comment = new HashMap<String, String>();
        if (tables != null) {
            String sql = "select upper(column_name) N,column_comment C from view_sys_user_tab_columns where UPPER(table_name) in ('','" + StringUtils.join(tables, "','") + "')";
            IDBCommand stmt = null;
            try {
                stmt = DBHELPER.getCommand(con, sql, true);
                stmt.executeQuery();
                while (stmt.next()) {
                    final String columnName = stmt.getString("N");
                    if (fieldName2Comment.containsKey(columnName))
                        continue;
                    fieldName2Comment.put(columnName, stmt.getString("C"));
                }
            } finally {
                DBHELPER.closeCommand(stmt);
            }
        }
        return fieldName2Comment;

    }

    @Override
    public JSONObject merge(final String insert, final String update) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        if (!isBlankSQL(insert) && !isBlankSQL(update) && !isPLSQL(insert) && !isPLSQL(update)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("begin\n");
            sb.append(DBSqlUtils.deleteComment1(update)).append("\n;");
            sb.append("\n IF (row_count() = 0) then\n");
            sb.append(DBSqlUtils.deleteComment1(insert)).append("\n;\nend if;\nend;");
            json.put("sql", sb.toString());
            json.put("r", true);
        }
        return json;
    }

    private boolean isBlankSQL(final String sql) {
        return StringUtils.isEmpty(StringUtils.trim(DBSqlUtils.deleteComment(sql)));
    }

    private boolean isPLSQL(final String sql) {
        return StringUtils.endsWithIgnoreCase(StringUtils.trim(DBSqlUtils.deleteComment(sql)), "end;");
    }

}
