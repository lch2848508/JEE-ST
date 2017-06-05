package com.estudio.define.webclient.report;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.impl.db.DBSqlUtils;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;

public class PrinterDataSource {
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    String name;
    String sql;
    List<String> fields = new ArrayList<String>();
    List<SQLParam4Form> params = new ArrayList<SQLParam4Form>();
    List<PrinterDataSource> children = new ArrayList<PrinterDataSource>();
    IDBCommand cmd = null;

    public String getSQL() {
        return sql;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSQL(final String sQL) {
        sql = DBSqlUtils.deleteComment(sQL);
    }

    public String getName() {
        return name;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<SQLParam4Form> getParams() {
        return params;
    }

    public List<PrinterDataSource> getChildren() {
        return children;
    }

    public IDBCommand getCommand(final Connection con) throws Exception {
        if (cmd == null)
            cmd = DBHELPER.getCommand(null, sql);
        return cmd.clone(con);
    }
}
