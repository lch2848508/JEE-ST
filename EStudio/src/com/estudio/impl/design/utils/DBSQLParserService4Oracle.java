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

public final class DBSQLParserService4Oracle extends DBSQLParserService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static DBSQLParserService4Oracle instance = new DBSQLParserService4Oracle();

    public static DBSQLParserService4Oracle getInstance() {
        return instance;
    }

    private DBSQLParserService4Oracle() {
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
            String sql = "select upper(COLUMN_NAME) N,COMMENTS C from user_col_comments where upper(table_name) in (''";
            for (final String table : tables) {
                final String tableName = table.toUpperCase();
                sql += ",'" + tableName + "'";
            }
            sql += ")";
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
            sb.append("\n if SQL%NOTFOUND then \n");
            sb.append(DBSqlUtils.deleteComment1(insert)).append("\n;");
            sb.append("\n  end if;\n");
            sb.append("end;");
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
