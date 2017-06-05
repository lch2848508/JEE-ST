package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnionQuery;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.estudio.define.db.SQLParam;
import com.estudio.intf.db.ISQLTrans;
import com.estudio.utils.SecurityUtils;

public final class DBSQLTrans4MySQL extends DBSQLTrans {

    private DBSQLTrans4MySQL() {
        super();
    }

    /**
     * 创建临时对象
     * 
     * @param string
     * @param con
     * @throws SQLException
     *             , DBException
     */
    private void createTemplateObject(final String sql, final Connection con) throws Exception {
        DBHelper4MySQL.getInstance().execute(sql, con);
    }

    @Override
    public String transSQL4ProcSQL(final String sql, final List<SQLParam> params, final Connection con) throws Exception {
        String sqlWithoutComment = StringUtils.trim(DBSqlUtils.deleteComment(sql));
        String sqlLower = StringUtils.trim(StringUtils.lowerCase(sqlWithoutComment));
        if (StringUtils.startsWithAny(sqlLower, "select", "insert", "update", "delete") && !StringUtils.endsWithAny(sqlLower, ";", "end"))
            return sqlWithoutComment;

        List<SQLStatement> stmts = null;
        try {
            stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        SQLStatement stmt = stmts != null && stmts.size() == 1 ? stmts.get(0) : null;
        if (stmt != null && (stmt instanceof SQLCallStatement || stmt instanceof SQLInsertStatement || stmt instanceof SQLUpdateStatement || stmt instanceof SQLDeleteStatement || stmt instanceof SQLSelectStatement))
            return sqlWithoutComment;
        String tempProcName = "prj_proc_temp_" + SecurityUtils.md5(sql);
        String result = "{call " + tempProcName + "(";

        StringBuilder sb = new StringBuilder();
        sb.append("drop procedure if exists ").append(tempProcName);
        createTemplateObject(sb.toString(), con); // 首先删除旧的存储过程

        sb = new StringBuilder();
        sb.append("create procedure ").append(tempProcName).append("\n(\n");
        for (int i = 0; i < params.size(); i++) {
            SQLParam param = params.get(i);
            sb.append("var_").append(param.getName()).append(" ");
            switch (param.getDataType()) {
            case String:
                sb.append("varchar(4000)");
                break;
            case Int:
                sb.append("int");
                break;
            case Long:
                sb.append("bigint");
                break;
            case Float:
                sb.append("float");
                break;
            case Double:
                sb.append("double");
                break;
            case Decimal:
                sb.append("decimal");
                break;
            case Date:
                sb.append("date");
                break;
            case Time:
                sb.append("time");
                break;
            case DateTime:
                sb.append("datetime");
                break;
            case Timestampe:
                sb.append("timestamp");
                break;
            case Bytes:
                sb.append("blob");
                break;
            case Text:
                sb.append("text");
                break;
            default:
                sb.append("varchar(4000)");
                break;
            }
            result += ":" + param.getName();
            if (i != params.size() - 1) {
                sb.append(",");
                result += ",";
            }
            sb.append("\n");
        }
        sb.append(")\nbegin\n").append(StringUtils.replace(sqlWithoutComment, ":", "var_")).append("end;\n");
        createTemplateObject(sb.toString(), con); // 创建新的存储过程
        result += ")}";
        return result;
    }

    @Override
    public String transSQL4Page(final String sql, final String beginIndexParamName, final String endIndexParamName, final Connection con) throws Exception {
        String tempSQL = PagerUtils.limit(sql, JdbcConstants.MYSQL, 10086000, 20172000);
        return StringUtils.replaceEachRepeatedly(tempSQL, new String[] { "10086000", "20172000" }, new String[] { beginIndexParamName, endIndexParamName });
    }

    @Override
    public String transCountSQL4Page(String sql, String countFieldName, Connection con) throws Exception {
        return PagerUtils.count(sql, JdbcConstants.MYSQL);
    }

    @Override
    public String transSQL4SearchByKeyField(String sql, String keyFieldName, String paramName) throws Exception {
        try {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
            if (stmtList != null && stmtList.size() == 1 && stmtList.get(0) instanceof SQLSelectStatement) {
                SQLSelectStatement selectStmt = (SQLSelectStatement) stmtList.get(0);
                SQLSelect select = selectStmt.getSelect();
                select.setOrderBy(null);
                sql = SQLUtils.toSQLString(select, JdbcConstants.MYSQL);
            }
        } catch (final Exception e) {
            // ExceptionUtils.printExceptionTrace(e);
        }
        sql = "select * from (" + sql + ") T where t." + keyFieldName + "=:" + paramName;
        return sql;
    }

    @Override
    public String removeSQLParams(final String sql, List<String> params) {
        String result = sql;
        if (params != null && !params.isEmpty()) {
            try {
                List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
                if (statements != null && statements.size() == 1 && statements.get(0) instanceof SQLSelectStatement) {
                    SQLStatement stmt = statements.get(0);
                    SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
                    removeWhereParam(selectStmt.getSelect(), params);
                }
                result = SQLUtils.toSQLString(statements, JdbcConstants.MYSQL);
            } catch (final Exception e) {
                // ExceptionUtils.printExceptionTrace(e);
            }
        }
        return result;
    }

    @Override
    protected void removeWhereParam(SQLSelect select, List<String> removeParams) {
        SQLSelectQuery query = select.getQuery();
        if (query instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
            removeQueryBlockWhereParams(queryBlock, removeParams);
        } else if (query instanceof MySqlUnionQuery) {
            MySqlUnionQuery unionQuery = (MySqlUnionQuery) query;
            removeUnionQueryBlockWhereParams(unionQuery, removeParams);
        }
    }

    private void removeUnionQueryBlockWhereParams(MySqlUnionQuery unionQuery, List<String> removeParams) {
        if (unionQuery.getLeft() instanceof MySqlSelectQueryBlock)
            removeQueryBlockWhereParams((MySqlSelectQueryBlock) unionQuery.getLeft(), removeParams);
        else if (unionQuery.getLeft() instanceof MySqlUnionQuery)
            removeUnionQueryBlockWhereParams((MySqlUnionQuery) unionQuery.getLeft(), removeParams);

        if (unionQuery.getRight() instanceof MySqlSelectQueryBlock)
            removeQueryBlockWhereParams((MySqlSelectQueryBlock) unionQuery.getRight(), removeParams);
        else if (unionQuery.getRight() instanceof MySqlUnionQuery)
            removeUnionQueryBlockWhereParams((MySqlUnionQuery) unionQuery.getRight(), removeParams);
    }

    private void removeQueryBlockWhereParams(MySqlSelectQueryBlock queryBlock, List<String> removeParams) {
        SQLExpr whereExpr = queryBlock.getWhere();
        queryBlock.setWhere(builderSpecialWhere(removeParams, whereExpr));
        SQLTableSource tableSource = queryBlock.getFrom();
        removeSubqueryWhereParam(tableSource, removeParams);
    }

    /**
     * 删除子查询中的参数
     * 
     * @param tableSource
     * @param removeParams
     */
    private void removeSubqueryWhereParam(SQLTableSource tableSource, List<String> removeParams) {
        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) tableSource;
            SQLTableSource left = join.getLeft();
            SQLTableSource right = join.getRight();
            removeSubqueryWhereParam(left, removeParams);
            removeSubqueryWhereParam(right, removeParams);
            join.setCondition(builderSpecialWhere(removeParams, join.getCondition()));
        } else if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subTableSource = (SQLSubqueryTableSource) tableSource;
            removeWhereParam(subTableSource.getSelect(), removeParams);
        } else if (tableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource unionTableSource = (SQLUnionQueryTableSource) tableSource;
            SQLUnionQuery unionQuery = unionTableSource.getUnion();
            removeUnionQueryBlockWhereParams((MySqlUnionQuery) unionQuery, removeParams);
        }
    }

    @Override
    public String transSQL4Page(String sql, String keyFieldName) throws Exception {
        String result = "";
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        if (statements != null && statements.size() == 1 && statements.get(0) instanceof SQLSelectStatement) {
            SQLStatement stmt = statements.get(0);
            SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
            SQLSelect sqlSelect = selectStmt.getSelect();
            SQLSelectQuery query = sqlSelect.getQuery();
            if (query instanceof MySqlSelectQueryBlock) {
                MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) query;
                SQLExpr keyFieldSQLExpr = getKeyFieldSQLExpr(sql, keyFieldName);
                if (keyFieldSQLExpr == null)
                    keyFieldSQLExpr = new SQLIdentifierExpr(keyFieldName);
                String existsSQL = "SELECT id FROM sys_utils_page_optimize WHERE thread_id=:thread_id AND session_id=:session_id";
                SQLTableSource tableSource = queryBlock.getFrom();
                SQLInSubQueryExpr inQueryExpr = new SQLInSubQueryExpr();
                inQueryExpr.setExpr(keyFieldSQLExpr);
                inQueryExpr.setSubQuery(str2SQLSelect(existsSQL));
                if (tableSource instanceof SQLExprTableSource) { // 单个表
                    queryBlock.setWhere(inQueryExpr);
                } else {
                    SQLExpr sqlExp = removeSpecialTableParams("a", queryBlock.getWhere());
                    if (sqlExp == null)
                        queryBlock.setWhere(inQueryExpr);
                    else {
                        queryBlock.setWhere(new SQLBinaryOpExpr(sqlExp, SQLBinaryOperator.BooleanAnd, inQueryExpr));
                    }
                }
            } else {
                result = transSQL4Page(sql);
            }
        }
        result = SQLUtils.toSQLString(statements, JdbcConstants.MYSQL);
        return result;
    }

    protected SQLExpr removeSpecialTableParams(String tableName, SQLExpr whereExp) {
        SQLExpr result = whereExp;
        if (whereExp instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binary = (SQLBinaryOpExpr) whereExp;
            SQLExpr left = binary.getLeft();
            SQLExpr right = binary.getRight();
            if (binary.getOperator().isRelational()) {
                if (StringUtils.startsWithIgnoreCase(left.toString(), tableName + ".") && StringUtils.startsWithIgnoreCase(right.toString(), ":")) {
                    right = null;
                    left = null;
                } else {
                    left = removeSpecialTableParams(tableName, left);
                    right = removeSpecialTableParams(tableName, right);
                }
            } else {
                left = removeSpecialTableParams(tableName, left);
                right = removeSpecialTableParams(tableName, right);
            }
            if (left != null && right != null) {
                binary.setLeft(left);
                binary.setRight(right);
            } else if (left != null)
                result = left;
            else if (right != null)
                result = right;
            else
                result = null;
        } else if (whereExp instanceof SQLBetweenExpr) {
            SQLBetweenExpr between = (SQLBetweenExpr) whereExp;
            if (StringUtils.startsWithIgnoreCase(between.testExpr.toString(), tableName + "."))
                result = null;
        } else if (whereExp instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr inSubQuery = (SQLInSubQueryExpr) whereExp;
            if (StringUtils.startsWithIgnoreCase(inSubQuery.getExpr().toString(), tableName + "."))
                result = null;
        } // in可以去掉了
        return result;
    }

    private SQLSelect str2SQLSelect(String sql) {
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        return selectStmt.getSelect();
    }

    private SQLExpr getKeyFieldSQLExpr(String sql, String keyFieldName) throws Exception {
        SQLExpr result = null;
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        SQLSelect sqlSelect = selectStmt.getSelect();
        SQLSelectQuery query = sqlSelect.getQuery();
        MySqlSelectQueryBlock selectQueryBlock = (MySqlSelectQueryBlock) query;
        List<SQLSelectItem> fieldList = selectQueryBlock.getSelectList();

        for (SQLSelectItem item : fieldList) {
            String fieldAlias = item.getAlias();
            SQLExpr sqlExpr = item.getExpr();
            if (StringUtils.isEmpty(fieldAlias)) {
                if (sqlExpr instanceof SQLPropertyExpr) {
                    SQLPropertyExpr propExpr = (SQLPropertyExpr) sqlExpr;
                    fieldAlias = propExpr.getName();
                } else if (sqlExpr instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identifyExpr = (SQLIdentifierExpr) sqlExpr;
                    fieldAlias = identifyExpr.getName();
                }
            }
            if (StringUtils.equalsIgnoreCase(fieldAlias, keyFieldName)) {
                result = sqlExpr;
                break;
            }
        }
        return result;
    }

    private String generateOptimizeIDSQL(String sql, String keyFieldName) throws Exception {
        String result = sql;
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        SQLSelect sqlSelect = selectStmt.getSelect();
        SQLSelectQuery query = sqlSelect.getQuery();
        MySqlSelectQueryBlock selectQueryBlock = (MySqlSelectQueryBlock) query;
        List<SQLSelectItem> fieldList = selectQueryBlock.getSelectList();

        boolean isExistsKeyField = false;
        for (SQLSelectItem item : fieldList) {
            String fieldAlias = item.getAlias();
            SQLExpr sqlExpr = item.getExpr();
            if (StringUtils.isEmpty(fieldAlias)) {
                if (sqlExpr instanceof SQLPropertyExpr) {
                    SQLPropertyExpr propExpr = (SQLPropertyExpr) sqlExpr;
                    fieldAlias = propExpr.getName();
                } else if (sqlExpr instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identifyExpr = (SQLIdentifierExpr) sqlExpr;
                    fieldAlias = identifyExpr.getName();
                }
            }

            if (StringUtils.equalsIgnoreCase(fieldAlias, keyFieldName)) {
                fieldList.clear();
                fieldList.add(item);
                isExistsKeyField = true;
                break;
            }
        }
        if (!isExistsKeyField) {
            fieldList.clear();
            fieldList.add(new SQLSelectItem(new SQLIdentifierExpr(keyFieldName)));
            isExistsKeyField = true;
        }
        result = "INSERT into sys_utils_page_optimize (id, thread_id, session_id) select " + keyFieldName + ",:thread_id,:session_id from (" + transSQL4Page(stmt.toString()) + ") T";
        return result;
    }

    @Override
    public boolean isSupportPageOptimize() {
        return true;
    }

    @Override
    public String generatePageOptimizeIDSQL(String sql, List<String> invalidParamList, String keyFieldName) throws Exception {
        sql = removeSQLParams(sql, invalidParamList);
        return generateOptimizeIDSQL(sql, keyFieldName);
    }

    private static final ISQLTrans INSTANCE = new DBSQLTrans4MySQL();

    public static ISQLTrans getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isSelectFieldContainExpress(String sql, String keyFieldName) throws Exception {
        boolean result = true;
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        SQLSelect sqlSelect = selectStmt.getSelect();
        SQLSelectQuery query = sqlSelect.getQuery();
        MySqlSelectQueryBlock selectQueryBlock = (MySqlSelectQueryBlock) query;
        List<SQLSelectItem> fieldList = selectQueryBlock.getSelectList();

        for (SQLSelectItem item : fieldList) {
            String fieldAlias = item.getAlias();
            SQLExpr sqlExpr = item.getExpr();
            if (StringUtils.isEmpty(fieldAlias)) {
                if (sqlExpr instanceof SQLPropertyExpr) {
                    SQLPropertyExpr propExpr = (SQLPropertyExpr) sqlExpr;
                    fieldAlias = propExpr.getName();
                } else if (sqlExpr instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identifyExpr = (SQLIdentifierExpr) sqlExpr;
                    fieldAlias = identifyExpr.getName();
                }
            }

            if (StringUtils.equalsIgnoreCase(fieldAlias, keyFieldName)) {
                result = !(sqlExpr instanceof SQLPropertyExpr || sqlExpr instanceof SQLIdentifierExpr);
                break;
            }
        }
        return result;
    }

}
