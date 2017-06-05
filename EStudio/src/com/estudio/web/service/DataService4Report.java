package com.estudio.web.service;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.define.webclient.SQLParamUtils;
import com.estudio.define.webclient.report.PrinterDataSource;
import com.estudio.define.webclient.report.ReportTemplateDefine;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;

public final class DataService4Report {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private final ThreadLocal<ReportTemplateDefine> reportTemplateDefine = new ThreadLocal<ReportTemplateDefine>();
    private final ThreadLocal<Connection> reportTemplateConnection = new ThreadLocal<Connection>();

    public void registerReportTemplateDefine(final ReportTemplateDefine define) {
        reportTemplateDefine.set(define);
    }

    public void unregisterReportTemplateDefine(final ReportTemplateDefine define) {
        reportTemplateDefine.set(null);
        reportTemplateConnection.set(null);
        final Connection con = reportTemplateConnection.get();
        DBHELPER.closeConnection(con);
    }

    /**
     * 获取数据列表
     * 
     * @param con
     * @param define
     * @param params
     * @return
     * @throws JSONException
     * @throws SQLException
     * @throws DBException
     */
    public Map<String, List> getRecords(final Connection con, final ReportTemplateDefine define, final Map<String, String> params) throws Exception {
        final Map<String, List> result = new HashMap<String, List>();
        for (int i = 0; i < define.getDataSources().size(); i++) {
            final PrinterDataSource ps = define.getDataSources().get(i);
            result.put(ps.getName(), getDataSourceRecords(con, ps, params));
        }
        return result;
    }

    /**
     * 获取单条数据
     * 
     * @param ds
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     */
    public Map<String, String> getDataSourceRecord(final Connection con, final PrinterDataSource ds, final Map<String, String> params) throws Exception {
        final Map<String, String> result = new HashMap<String, String>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = ds.getCommand(tempCon);
            for (int i = 0; i < ds.getParams().size(); i++) {
                final SQLParam4Form param = ds.getParams().get(i);
                final String paramValue = params.get(param.getName());
                SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
            }
            cmd.executeQuery();
            if (cmd.next())
                commandValue2ArrayList(cmd, result);
        } finally {
            DBHELPER.closeCommand(cmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 获取记录
     * 
     * @param datasetName
     * @param params
     * @return
     * @throws Exception
     */
    public ArrayList<Map<String, String>> getDataSetRecords(final String datasetName, final Map<String, String> params) throws Exception {
        final ReportTemplateDefine templateDefine = reportTemplateDefine.get();
        final PrinterDataSource ds = templateDefine.findDataSource(datasetName);
        Connection con = reportTemplateConnection.get();
        if (con == null) {
            con = DBHELPER.getConnection();
            reportTemplateConnection.set(con);
        }
        return getDataSourceRecords(con, ds, params);
    }

    public List<Map<String, String>> getSQLRecords(final String sql, final Map<String, String> params) throws Exception {
        Connection con = reportTemplateConnection.get();
        if (con == null) {
            con = DBHELPER.getConnection();
            reportTemplateConnection.set(con);
        }
        final List<Map<String, String>> records = new ArrayList<Map<String, String>>();

        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, sql);
            final Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, String> entry = iterator.next();
                cmd.setParam(entry.getKey(), entry.getValue());
            }
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final Map<String, String> record = new HashMap<String, String>();
                    commandValue2ArrayList(cmd, record);
                    records.add(record);
                }
        } finally {
            DBHELPER.closeCommand(cmd);
        }

        return records;
    }

    /**
     * 取得多条记录
     * 
     * @param con
     * @param ds
     * @param params
     * @return
     * @throws SQLException
     *             , DBException
     */
    public ArrayList<Map<String, String>> getDataSourceRecords(final Connection con, final PrinterDataSource ds, final Map<String, String> params) throws Exception {
        final ArrayList<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = ds.getCommand(tempCon);
            for (int i = 0; i < ds.getParams().size(); i++) {
                final SQLParam4Form param = ds.getParams().get(i);
                String paramName = param.getName();
                if (StringUtils.startsWith(param.getInitValue(), "REQ."))
                    paramName = StringUtils.substringAfter(param.getInitValue(), "REQ.");
                else if (!StringUtils.isEmpty(param.getInitField()))
                    paramName = param.getInitField();
                final String paramValue = params.get(paramName);
                SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), paramValue);
            }
            cmd.executeQuery();
            while (cmd.next()) {
                final Map<String, String> record = new HashMap<String, String>();
                commandValue2ArrayList(cmd, record);
                resultList.add(record);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }

        return resultList;
    }

    /**
     * 获取数据
     * 
     * @param cmd
     * @param record
     * @throws SQLException
     *             , DBException
     */
    private void commandValue2ArrayList(final IDBCommand cmd, final Map<String, String> record) throws Exception {
        final ResultSetMetaData metaData = cmd.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String value = "";
            final int dataType = metaData.getColumnType(i);
            switch (dataType) {
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.DOUBLE:
            case Types.REAL:
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.CLOB:
            case Types.NCLOB:
                value = cmd.getString(i);
                break;
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.VARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
                value = cmd.getString(i);
                break;
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
                value = Convert.date2Str(cmd.getDateTime(i));
                break;
            case Types.BLOB:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                value = flexFilter(Convert.bytes2Str(cmd.getBytes(i)));
                break;
            default:
                value = cmd.getString(i);
                break;
            }
            if (StringUtils.isEmpty(value))
                value = "";
            String columnName = metaData.getColumnLabel(i).toUpperCase();
            String content = StringEscapeUtils.escapeHtml3(value);
            content = StringUtils.replace(content, "\n", "</br>");
            record.put(columnName, content);
        }
    }

    private String flexFilter(final String str) {
        if (!StringUtils.isEmpty(str) && StringUtils.startsWith(str, "<FLEXBEGIN--"))
            return StringUtils.substringAfter(str, "--FLEXBEGIN>");
        return str;
    }

    private static final DataService4Report INSTANCE = new DataService4Report();

    public static DataService4Report getInstance() {
        return INSTANCE;
    }

    private DataService4Report() {

    }
}
