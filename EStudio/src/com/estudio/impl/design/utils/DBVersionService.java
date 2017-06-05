package com.estudio.impl.design.utils;

import java.sql.Connection;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.utils.IVersionService;

public abstract class DBVersionService implements IVersionService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    public DBVersionService() {
        super();
    }

    protected abstract String getIncVersionSQL();

    protected abstract String getVersionSQL();

    @Override
    public long getVersion(final Connection con, final String type) throws Exception {
        long result = 0;
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getVersionSQL(), true);
            stmt.setParam(1, type);
            stmt.executeQuery();
            if (stmt.next())
                result = stmt.getLong(1);
            else result = incVersion(tempCon, type);
        } finally {
            DBHELPER.closeCommand(stmt);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public long incVersion(final Connection con, final String type) throws Exception {
        long result = 0;
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getIncVersionSQL());
            stmt.setParam(1, type);
            stmt.execute();
            result = getVersion(tempCon, type);
        } finally {
            DBHELPER.closeCommand(stmt);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

}
