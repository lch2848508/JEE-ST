package com.estudio.workflow.storage;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.define.ClientException;
import com.estudio.define.webclient.form.FormDefine;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;

public class WFDBService {
    // 业务类型名称
    ThreadLocal<Long> processId = new ThreadLocal<Long>();
    ThreadLocal<Connection> con = new ThreadLocal<Connection>();
    ThreadLocal<Map<String, String>> formHttpParams = new ThreadLocal<Map<String, String>>();
    ThreadLocal<JSONObject> dataJson = new ThreadLocal<JSONObject>();
    ThreadLocal<HttpSession> httpSession = new ThreadLocal<HttpSession>();
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    /**
     * 设置业务ID
     * 
     * @param v
     */
    public void setProcessId(final long v) {
        processId.set(v);
        dataJson.set(new JSONObject());
    }

    /**
     * 设置数据库连接
     * 
     * @param con
     */
    public void setConnection(final Connection con) {
        this.con.set(con);
    }

    /**
     * 表单参数
     * 
     * @param v
     */
    public void setHttpParams(final Map<String, String> v) {
        formHttpParams.set(v);
    }

    public boolean execute(final String SQL, final Map<String, Object> params) throws Exception {
        return DBHELPER.execute(SQL, params, con.get());
    }

    public int queryInt(final String SQL, final Map<String, Object> params) throws Exception {
        return Convert.obj2Int(DBHELPER.executeScalar(SQL, params, con.get()), 0);
    }

    public double queryDouble(final String SQL, final Map<String, Object> params) throws Exception {
        return Convert.obj2Double(DBHELPER.executeScalar(SQL, params, con.get()), 0.0f);
    }

    public String queryString(final String SQL, final Map<String, Object> params) throws Exception {
        return String.valueOf(DBHELPER.executeScalar(SQL, params, con.get()));
    }

    public Date queryDate(final String SQL, final Map<String, Object> params) throws Exception {
        return Convert.obj2Date(DBHELPER.executeScalar(SQL, params, con.get()));
    }

    public JSONArray queryRecords(final String SQL, final Map<String, Object> params) throws Exception {
        return DBHELPER.executeQuery(SQL, params, con.get());
    }

    public JSONObject queryRecord(final String SQL, final Map<String, Object> params) throws Exception {
        final JSONArray array = queryRecords(SQL, params);
        return array.size() != 0 ? array.getJSONObject(0) : null;
    }

    public void raiseError(final String msg) throws Exception {
        throw new ClientException(msg);
    }

    public void raiseMessage(final String msg) {
        GlobalContext.getClientMessage().append(msg);
    }

    public void raiseAlert(final String msg) {
        GlobalContext.getAlertMessage().append(msg);
    }

    public void printMsg(final String msg) throws Exception {
        System.out.print(msg);
    }

    /**
     * 取值
     * 
     * @param formId
     * @param datasetName
     * @param fieldName
     * @return
     */
    public String getValue(final long formId, final String datasetName, final String fieldName) {
        String result = "";
        try {
            JSONObject formData = null;
            final String key = "F_" + formId;
            if (!dataJson.get().containsKey(key)) {
                final FormDefine form = RuntimeContext.getFormDefineService().getFormDefine(formId, con.get());
                formData = new JSONObject();
                form.getData(con.get(), formData, formHttpParams.get(), httpSession.get());
                dataJson.get().put(key, formData);
            } else
                formData = dataJson.get().getJSONObject(key);
            if (formData.containsKey(datasetName)) {
                final JSONArray array = formData.getJSONArray(datasetName);
                if ((array.size() != 0) && array.getJSONObject(0).containsKey(fieldName))
                    result = array.getJSONObject(0).getString(fieldName);
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con.get());
        }
        return result;
    }

    private static final WFDBService INSTANCE = new WFDBService();

    public static WFDBService getInstance() {
        return INSTANCE;
    }
}
