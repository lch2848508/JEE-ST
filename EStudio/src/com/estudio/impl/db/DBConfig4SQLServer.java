package com.estudio.impl.db;

import java.sql.Connection;

import com.estudio.intf.db.IDBHelper;

public class DBConfig4SQLServer extends DBConfig {

    @Override
    protected boolean checkSchema(Connection con, IDBHelper dbHelper) throws Exception {
        if (dbHelper.executeScalarInt("SELECT COUNT(*) FROM view_sys_user_tables WHERE UPPER(table_name)=UPPER('sys_config')", con) == 0)
            dbHelper.execute("create table sys_config(c varchar(200),k varchar2(200),content text)", con);
        return true;
    }

    private DBConfig4SQLServer() {

    }

    public static final DBConfig4SQLServer instance = new DBConfig4SQLServer();

}
