package com.estudio.impl.db;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import com.estudio.intf.db.IDBHelper;

public class DBCommand4Oracle extends DBCommand {

    /**
     * 构造函数
     * 
     * @param connection
     * @param SQL
     * @throws SQLException
     *             , DBException
     */
    public DBCommand4Oracle(final Connection connection, final String SQL) throws Exception {
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
    public DBCommand4Oracle(final Connection connection, final String SQL, final boolean isSkipParserParams) throws Exception {
        super(connection, SQL, isSkipParserParams);
    }

    @Override
    public String getClob(int columnIndex) throws Exception {
        return ClobToString((oracle.sql.CLOB) resultSet.getClob(columnIndex));
    }

    @Override
    public String getClob(String columnName) throws Exception {
        return ClobToString((oracle.sql.CLOB) resultSet.getClob(columnName));
    }

    /**
     * 
     * @param clob
     * @return
     * @throws Exception
     */
    private static String ClobToString(oracle.sql.CLOB clob) throws Exception {
        String reString = "";
        StringBuffer sb = null;
        Reader is = null;
        BufferedReader br = null;
        try {
            is = clob.getCharacterStream();// 得到流
            br = new BufferedReader(is);
            String s = br.readLine();
            sb = new StringBuffer();
            while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
                sb.append(s);
                s = br.readLine();
            }
            reString = sb.toString();
        } finally {
            if (br != null)
                br.close();
            if (is != null)
                is.close();
        }
        return reString;
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

    protected IDBHelper getDBHelper()
    {
        return DBHelper4Oracle.getInstance();
    }

}
