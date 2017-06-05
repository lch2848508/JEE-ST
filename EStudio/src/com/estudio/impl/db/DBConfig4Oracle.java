package com.estudio.impl.db;

import java.sql.Connection;

import com.estudio.intf.db.IDBHelper;

public class DBConfig4Oracle extends DBConfig {

    @Override
    protected boolean checkSchema(Connection con, IDBHelper dbHelper) throws Exception {
        if (dbHelper.executeScalarInt("SELECT COUNT(*) FROM user_tables WHERE UPPER(table_name)=UPPER('sys_config')", con) == 0)
            dbHelper.execute("create table sys_config(c varchar2(200),k varchar2(200),content blob)", con);
        return true;
    }

    private DBConfig4Oracle() {

    }

    public static final DBConfig4Oracle instance = new DBConfig4Oracle();
}
