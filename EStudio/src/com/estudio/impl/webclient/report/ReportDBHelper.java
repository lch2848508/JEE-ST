package com.estudio.impl.webclient.report;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import sun.org.mozilla.javascript.internal.NativeObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.define.webclient.SQLParamUtils;
import com.estudio.define.webclient.report.PrinterDataSource;
import com.estudio.define.webclient.report.ReportTemplateDefine;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;

public class ReportDBHelper {
    Connection con = null;
    ReportTemplateDefine reportDefine = null;
    IDBHelper dbHelper = RuntimeContext.getDbHelper();

    /**
     * 构造函数
     * 
     * @param con
     * @param reportDefine
     */
    public ReportDBHelper(Connection con, ReportTemplateDefine reportDefine) {
        super();
        this.con = con;
        this.reportDefine = reportDefine;
    };

    /**
     * 
     * @param sql
     * @param params
     * @throws Exception
     */
    public void executeSQL(String sql, Object params) throws Exception {
        dbHelper.execute(sql, nativeObject2Map((NativeObject) params), con);
    }

    /**
     * 
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public JSONArray querySQLAsJSONArray(String sql, Object params) throws Exception {
        return dbHelper.executeQuery(sql, nativeObject2Map((NativeObject) params), con);
    }

    /**
     * 
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public JSONArray querySQLAsJSONArray(String sql) throws Exception {
        return dbHelper.executeQuery(sql, new HashMap<String, Object>(), con);
    }

    /**
     * 
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject querySQLAsJSONObject(String sql, Object params) throws Exception {
        JSONArray array = querySQLAsJSONArray(sql, params);
        return array.isEmpty() ? new JSONObject() : null;
    }

    /**
     * 
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public String queryScaleAsString(String sql, Object params) throws Exception {
        return dbHelper.executeScalarString(sql, nativeObject2Map((NativeObject) params), con);
    }

    public int queryScaleAsInt(String sql, Object params) throws Exception {
        return dbHelper.executeScalarInt(sql, nativeObject2Map((NativeObject) params), con);
    }

    public long queryScaleAsLong(String sql, Object params) throws Exception {
        return dbHelper.executeScalarLong(sql, nativeObject2Map((NativeObject) params), con);
    }

    public double queryScaleAsDouble(String sql, Object params) throws Exception {
        return dbHelper.executeScalarDouble(sql, nativeObject2Map((NativeObject) params), con);
    }

    public Date queryScaleAsDateTime(String sql, Object params) throws Exception {
        return dbHelper.executeScalarDatetime(sql, nativeObject2Map((NativeObject) params), con);
    }

    /**
     * 执行DataSet中的SQL语句
     * 
     * @param datasetName
     * @param params
     * @throws Exception
     */
    public void executeDatasource(String datasetName, Object params) throws Exception {
        PrinterDataSource datasource = reportDefine.findDataSource(datasetName);
        IDBCommand cmd = null;
        try {
            cmd = datasource.getCommand(con);
            setDBCommandParams(datasource, cmd, nativeObject2Map((NativeObject) params));
            cmd.execute();
        } finally {
            dbHelper.closeCommand(cmd);
        }
    }

    /**
     * 
     * @param datasetName
     * @param params
     * @return
     * @throws Exception
     */
    public JSONArray queryDatasourceAsJSONArray(String datasetName, Object params) throws Exception {
        JSONArray result = null;
        PrinterDataSource datasource = reportDefine.findDataSource(datasetName);
        IDBCommand cmd = null;
        try {
            cmd = datasource.getCommand(con);
            setDBCommandParams(datasource, cmd, nativeObject2Map((NativeObject) params));
            cmd.executeQuery();
            result = dbHelper.command2JSONArray(cmd);
        } finally {
            dbHelper.closeCommand(cmd);
        }
        return result;
    }

    /**
     * 设置参数
     * 
     * @param datasource
     * @param cmd
     * @param params
     * @throws Exception
     */
    private void setDBCommandParams(PrinterDataSource datasource, IDBCommand cmd, Map<String, Object> params) throws Exception {
        for (SQLParam4Form param : datasource.getParams()) {
            String paramName = param.getName();
            String paramValue = String.valueOf(params.get(paramName));
            SQLParamUtils.setParam(cmd, param.getDataType(), paramName, paramValue);
        }
    }

    /**
     * 
     * @param datasetName
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject queryDatasourceAsJSONObject(String datasetName, Object params) throws Exception {
        JSONObject result = null;
        Map<String, Object> datasetParams = nativeObject2Map((NativeObject) params);
        PrinterDataSource datasource = reportDefine.findDataSource(datasetName);
        IDBCommand cmd = null;
        try {
            cmd = datasource.getCommand(con);
            setDBCommandParams(datasource, cmd, datasetParams);
            cmd.executeQuery();
            result = cmd.next() ? dbHelper.cmdRecord2Json(cmd) : new JSONObject();
        } finally {
            dbHelper.closeCommand(cmd);
        }
        return result;
    }

    /**
     * 
     * @param params
     * @return
     */
    private Map<String, Object> nativeObject2Map(NativeObject params) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params != null) {
            for (Entry<Object, Object> entry : params.entrySet())
                result.put(entry.getKey().toString(), entry.getValue());
        }
        return result;
    }

}
