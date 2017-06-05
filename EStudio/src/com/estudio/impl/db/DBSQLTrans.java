package com.estudio.impl.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.estudio.define.db.SQLParam;
import com.estudio.intf.db.ISQLTrans;

public abstract class DBSQLTrans implements ISQLTrans {

    @Override
    public abstract String transSQL4ProcSQL(String sql, List<SQLParam> params, Connection con) throws Exception;

    @Override
    public abstract String transSQL4Page(String sql, String beginIndexParamName, String endIndexParamName, Connection con) throws Exception;

    @Override
    public String transSQL4Page(final String sql) throws Exception {
        return transSQL4Page(sql, ":B", ":E", null);
    }

    @Override
    public String transCountSQL4Page(final String sql) throws Exception {
        return transCountSQL4Page(sql, "c", null);
    }

    protected abstract void removeWhereParam(SQLSelect select, List<String> removeParams);

    /**
     * 参数是否在过滤列表中
     * 
     * @param params
     * @param removeParams
     * @return
     */
    protected static boolean isParamValueIncludeFilterParams(List<String> params, List<String> removeParams) {
        for (String str : params) {
            for (String str1 : removeParams) {
                if (StringUtils.equalsIgnoreCase(str, str1) || StringUtils.equalsIgnoreCase(str, ":" + str1))
                    return true;
            }
        }
        return false;
    }

    /**
     * 根据参数过滤列表重新构建查询条件
     * 
     * @param filterParams
     * @param whereExp
     * @return
     */
    protected SQLExpr builderSpecialWhere(List<String> filterParams, SQLExpr whereExp) {
        SQLExpr result = whereExp;
        if (whereExp instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binary = (SQLBinaryOpExpr) whereExp;
            SQLExpr left = binary.getLeft();
            SQLExpr right = binary.getRight();
            if (binary.getOperator().isRelational()) {
                List<String> params = getVariantList(right);
                if (isParamValueIncludeFilterParams(params, filterParams)) {
                    right = null;
                    left = null;
                } else {
                    left = builderSpecialWhere(filterParams, left);
                    right = builderSpecialWhere(filterParams, right);
                }
            } else {
                left = builderSpecialWhere(filterParams, left);
                right = builderSpecialWhere(filterParams, right);
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
            SQLExpr begin = between.beginExpr;
            SQLExpr end = between.endExpr;
            List<String> params = getVariantList(begin);
            if (isParamValueIncludeFilterParams(params, filterParams))
                begin = null;
            params = getVariantList(end);
            if (isParamValueIncludeFilterParams(params, filterParams))
                end = null;
            if (begin == null && end == null)
                result = null;
            else if (begin == null) {
                SQLBinaryOpExpr bin = new SQLBinaryOpExpr();
                bin.setOperator(SQLBinaryOperator.LessThanOrEqual);
                bin.setLeft(between.testExpr);
                bin.setRight(end);
                result = bin;
            } else if (end == null) {
                SQLBinaryOpExpr bin = new SQLBinaryOpExpr();
                bin.setOperator(SQLBinaryOperator.GreaterThanOrEqual);
                bin.setLeft(between.testExpr);
                bin.setRight(begin);
                result = bin;
            }

        } else if (whereExp instanceof SQLExistsExpr) {
            SQLExistsExpr exists = (SQLExistsExpr) whereExp;
            removeWhereParam(exists.subQuery, filterParams);
            result = exists;
        } else if (whereExp instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr inSubQuery = (SQLInSubQueryExpr) whereExp;
            removeWhereParam(inSubQuery.getSubQuery(), filterParams);
            result = inSubQuery;
        } else if (whereExp instanceof SQLQueryExpr) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) whereExp;
            removeWhereParam(queryExpr.getSubQuery(), filterParams);
        }
        return result;
    }

    private List<String> getVariantList(SQLExpr expr) {
        List<String> params = new ArrayList<String>();
        if (expr instanceof SQLVariantRefExpr)
            params.add(((SQLVariantRefExpr) expr).toString());
        else if (expr instanceof SQLMethodInvokeExpr)
            for (SQLExpr methodParam : ((SQLMethodInvokeExpr) expr).getParameters())
                if (methodParam instanceof SQLVariantRefExpr)
                    params.add(((SQLVariantRefExpr) methodParam).toString());
        return params;
    }

    @Override
    public boolean isSelectSQL(String sql) {
        return StringUtils.containsIgnoreCase(StringUtils.trim(sql), "select");
    }
}
