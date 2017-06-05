package com.estudio.impl.db;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import com.estudio.define.db.SQLParam;
import com.estudio.intf.db.IDBCommand;

public final class DBSQLTrans4SQLServer extends DBSQLTrans {

    IDBCommand cmd = null;

    private DBSQLTrans4SQLServer() {
        super();
    }

    @Override
    public String transSQL4ProcSQL(final String sql, final List<SQLParam> params, final Connection con) throws Exception {
        return sql;
    }

    @Override
    public String transSQL4Page(final String sql, final String beginIndexParamName, final String endIndexParamName, final Connection con) throws Exception {
        String tempSQL = PagerUtils.limit(sql, JdbcConstants.SQL_SERVER, 10086000, 10086000);
        return StringUtils.replaceEachRepeatedly(tempSQL, new String[] { "10086000", "20172000" }, new String[] { beginIndexParamName, endIndexParamName });
    }

    @Override
    public String transCountSQL4Page(String sql, String countFieldName, Connection con) throws Exception {
        return PagerUtils.count(sql, JdbcConstants.SQL_SERVER);
    }

    @Override
    public String transSQL4SearchByKeyField(final String sql, final String keyFieldName, final String paramName) throws Exception {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SQL_SERVER);
        if (stmtList.size() == 0)
            return "";
        SQLSelectStatement selectStmt = (SQLSelectStatement) stmtList.get(0);
        SQLSelect select = selectStmt.getSelect();
        select.setOrderBy(null);
        String SQL = "select * from (" + SQLUtils.toSQLString(select, JdbcConstants.SQL_SERVER) + ") T where t." + keyFieldName + "=:" + paramName;
        return SQL;
    }

    @Override
    public String removeSQLParams(final String sql, List<String> params) {
        String result = sql;
        if (!params.isEmpty()) {
            try {
                List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.SQL_SERVER);
                if (statements != null && statements.size() == 1 && statements.get(0) instanceof SQLSelectStatement) {
                    SQLStatement stmt = statements.get(0);
                    SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
                    removeWhereParam(selectStmt.getSelect(), params);
                }
                result = SQLUtils.toSQLString(statements, JdbcConstants.SQL_SERVER);
            } catch (final Exception e) {
                // ExceptionUtils.printExceptionTrace(e);
            }
        }
        return result;
    }

    /**
     * 从Select中删除参数
     * 
     * @param select
     * @param removeParams
     */
    @Override
    protected void removeWhereParam(SQLSelect select, List<String> removeParams) {
        SQLSelectQuery query = select.getQuery();
        if (query instanceof SQLServerSelectQueryBlock) {
            SQLServerSelectQueryBlock selectQueryBlock = (SQLServerSelectQueryBlock) query;
            removeSelectQueryBlockParams(selectQueryBlock, removeParams);
        } else if (query instanceof SQLUnionQuery) {
            SQLUnionQuery unionQuery = (SQLUnionQuery) query;
            removeSQLUnionQueryParams(unionQuery, removeParams);
        }
    }

    private void removeSQLUnionQueryParams(SQLUnionQuery unionQuery, List<String> removeParams) {
        if (unionQuery.getLeft() instanceof SQLServerSelectQueryBlock)
            removeSelectQueryBlockParams((SQLServerSelectQueryBlock) unionQuery.getLeft(), removeParams);
        else if (unionQuery.getLeft() instanceof SQLUnionQuery)
            removeSQLUnionQueryParams((SQLUnionQuery) unionQuery.getLeft(), removeParams);

        if (unionQuery.getRight() instanceof SQLServerSelectQueryBlock)
            removeSelectQueryBlockParams((SQLServerSelectQueryBlock) unionQuery.getRight(), removeParams);
        else if (unionQuery.getRight() instanceof SQLUnionQuery)
            removeSQLUnionQueryParams((SQLUnionQuery) unionQuery.getRight(), removeParams);
    }

    private void removeSelectQueryBlockParams(SQLServerSelectQueryBlock selectQueryBlock, List<String> removeParams) {
        SQLExpr whereExpr = selectQueryBlock.getWhere();
        selectQueryBlock.setWhere(builderSpecialWhere(removeParams, whereExpr));
        SQLTableSource tableSource = selectQueryBlock.getFrom();
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
            removeSQLUnionQueryParams((SQLUnionQuery) unionQuery, removeParams);
        }
    }

    @Override
    public String transSQL4Page(String sql, String keyFieldName) throws Exception {
        return transSQL4Page(sql);
    }

    private static final DBSQLTrans4SQLServer INSTANCE = new DBSQLTrans4SQLServer();

    public static DBSQLTrans4SQLServer getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isSupportPageOptimize() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String generatePageOptimizeIDSQL(String sql, List<String> invalidParamList, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSelectFieldContainExpress(String sql, String keyFieldName) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

}
