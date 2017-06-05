package com.estudio.define.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.db.SQLParam;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.impl.db.DBSqlUtils;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;

public class DesignDataSourceCommand {
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    String sql;
    String originalSQL;
    IDBCommand cmd = null;
    IDBCommand singleCmd = null;
    ArrayList<SQLParam4Form> params = new ArrayList<SQLParam4Form>();
    private IDBCommand countCmd;
    private IDBCommand pageCmd;
    private String keyFieldName;

    public String getKeyFieldName() {
        return keyFieldName;
    }

    public void setKeyFieldName(String keyFieldName) {
        this.keyFieldName = keyFieldName;
    }

    /**
     * 增加一个参数
     * 
     * @param param
     */
    public void addParam(final SQLParam4Form param) {
        params.add(param);
    }

    /**
     * 得到SQL语句
     * 
     * @return
     */
    public String getSql() {
        return sql;
    }

    /**
     * 得到参数的总数
     * 
     * @return
     */
    public long getParamCount() {
        return params.size();
    }

    /**
     * 根据索引获取参数
     * 
     * @param index
     * @return
     */
    public SQLParam4Form getParam(final int index) {
        return params.get(index);
    }

    /**
     * 设置SQL语句
     * 
     * @param sql
     * @throws SQLException
     *             , DBException
     */
    public void setSql(final String sql) throws Exception {
        originalSQL = sql;
        this.sql = DBSqlUtils.deleteComment(sql);
        // cmd = RuntimeContext.getDbHelper().getCommand(null, this.sql);
    }

    /**
     * 初始化SQL语句
     * 
     * @param con
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public void initCommand(final Connection con) throws Exception {
        final ArrayList<SQLParam> params = new ArrayList<SQLParam>();
        params.addAll(this.params);
        cmd = DBHELPER.getCommand(null, DBHELPER.getSQLTrans().transSQL4ProcSQL(sql, params, con));
    }

    /**
     * 取得Command
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getCmd(final Connection con) throws Exception {
        return cmd != null ? cmd.clone(con) : null;
    }

    /**
     * 初始化分页及求总的记录集
     * 
     * @throws SQLException
     *             , DBException
     */
    public void initPageAndCountCmd() throws Exception {
        String newSQL = DBSqlUtils.deleteComment(DBHELPER.getSQLTrans().transCountSQL4Page(originalSQL));
        countCmd = DBHELPER.getCommand(null, newSQL);
        newSQL = isSupportPageOptimize() ? DBHELPER.getSQLTrans().transSQL4Page(sql, keyFieldName) : DBHELPER.getSQLTrans().transSQL4Page(sql);
        pageCmd = DBHELPER.getCommand(null, newSQL);
    }

    /**
     * 得到求记录总数的Command对象
     * 
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     */
    public IDBCommand getRCCmd(final Connection con, final Map<String, String> extParsms) throws Exception {
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
    public IDBCommand getPCmd(final Connection con, final Map<String, String> extParsms) throws Exception {
        return pageCmd.clone(con, extParsms);
    }

    public boolean isSupportPageOptimize() throws Exception {
        return DBHELPER.getSQLTrans().isSupportPageOptimize() && !isKeyFieldContainExpress();
    }

    private boolean isCalcKeyFieldContainExpress = false;
    private boolean calcKeyFieldContainExpressValue = false;
    private Object lock = new Object();

    private boolean isKeyFieldContainExpress() throws Exception {
        synchronized (lock) {
            if (!isCalcKeyFieldContainExpress) {
                calcKeyFieldContainExpressValue = DBHELPER.getSQLTrans().isSelectFieldContainExpress(sql, keyFieldName);
                isCalcKeyFieldContainExpress = true;
            }
        }
        return calcKeyFieldContainExpressValue;
    }
}
