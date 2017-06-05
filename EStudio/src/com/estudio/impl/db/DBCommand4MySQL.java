package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.estudio.intf.db.IDBHelper;

public class DBCommand4MySQL extends DBCommand {

    /**
     * 构造函数
     * 
     * @param connection
     * @param SQL
     * @throws SQLException
     *             , DBException
     */
    public DBCommand4MySQL(final Connection connection, final String SQL) throws Exception {
        super(connection, SQL);
    }

    /**
     * 构造函数
     * 
     * @param connection
     * @param SQL
     * @throws SQLException
     *             , DBException
     */
    public DBCommand4MySQL(final Connection connection, final String SQL, final boolean isSkipParserParams) throws Exception {
        super(connection, SQL, isSkipParserParams);
    }

    @Override
    public boolean setParam(final String paramName, final String[] values) throws Exception {
        //
        return false;
    }

    @Override
    public boolean setParam(final String paramName, final long[] values) throws Exception {
        //
        return false;
    }

    @Override
    public boolean setParam(final String paramName, final int[] values) throws Exception {
        //
        return false;
    }

    @Override
    public boolean setParam(final String paramName, final double[] values) throws Exception {
        //
        return false;
    }

    @Override
    public boolean setParam(final int index, final long[] values) throws Exception {
        //
        return false;
    }

    @Override
    public boolean setParam(final int index, final int[] values) throws Exception {
        //
        return false;
    }

    @Override
    public boolean setParam(final int index, final double[] values) throws Exception {
        //
        return false;
    }

    @Override
    public String getClob(int index) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getClob(String columnName) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected IDBHelper getDBHelper()
    {
        return DBHelper4MySQL.getInstance();
    }
}
