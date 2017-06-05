package com.estudio.impl.db;

import java.sql.Connection;

import com.estudio.intf.db.IDBHelper;

public class DBConfig4MySQL extends DBConfig {

    @Override
    protected boolean checkSchema(Connection con, IDBHelper dbHelper) throws Exception {
        if (dbHelper.executeScalarInt("SELECT COUNT(*) FROM view_sys_user_tables WHERE UPPER(table_name)=UPPER('sys_config')", con) == 0)
            dbHelper.execute("CREATE TABLE sys_config (c varchar(200),k varchar(200),content blob)", con);
        return true;
    }

    private DBConfig4MySQL() {

    }

    public static final DBConfig4MySQL instance = new DBConfig4MySQL();

}
