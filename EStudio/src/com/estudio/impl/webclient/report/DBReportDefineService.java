package com.estudio.impl.webclient.report;

import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.define.webclient.report.PrinterDataSource;
import com.estudio.define.webclient.report.ReportTemplateDefine;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.webclient.report.IReportDefineService;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

public class DBReportDefineService implements IReportDefineService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    /**
     * 获取模版定义SQL语句
     * 
     * @return
     */
    private static final String getTemplateSQL() {
        return "select content, template, b.caption,a.office_template,a.version from sys_object_report a,sys_object_tree b where a.id = b.id and a.id=?";
    }

    private static final IReportDefineService INSTANCE = new DBReportDefineService();

    private String getKey(final long id) {
        return "ReportTemplateDefine-" + id;
    }

    /**
     * 取得全局唯一实例
     * 
     * @return
     */
    public static IReportDefineService getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.report.IReportTemplateService#getDefine(java
     * .sql.Connection, long)
     */
    @Override
    public ReportTemplateDefine getDefine(final Connection con, final long templateid) throws Exception {
        final String cacheKey = getKey(templateid);
        ReportTemplateDefine result = (ReportTemplateDefine) SystemCacheManager.getInstance().getDesignObject(cacheKey);// id2Define.get(templateid);
        if (result == null || result.getVersion() != DBHELPER.executeScalarLong("select version from sys_object_report where id=" + templateid, con)) {
            result = loadTemplateFormDB(con, templateid);
            SystemCacheManager.getInstance().putDesignObject(cacheKey, result);
        }
        return result;
    }

    /*
     * { "DataSources": { "DFM": [], "JSON": [] }, "Params": [], "TemplateType":
     * "Word", "JS": "" }
     */
    /**
     * 
     * @param jsonObject
     * @param result
     * @throws JSONException
     */
    private PrinterDataSource loadJsonObject2DataSource(final JSONObject jsonObject, final ReportTemplateDefine define) {
        final PrinterDataSource result = new PrinterDataSource();

        result.setName(jsonObject.getString("Name"));
        result.setSQL(jsonObject.getString("SQL"));

        final JSONArray fields = jsonObject.getJSONArray("Fields");
        for (int i = 0; i < fields.size(); i++)
            result.getFields().add(fields.getString(i));

        final JSONArray params = jsonObject.getJSONArray("Params");
        for (int i = 0; i < params.size(); i++) {
            final JSONObject pj = params.getJSONObject(i);
            final String pName = pj.getString("Name");
            final String pLabel = "";
            final DBParamDataType pDataType = DBParamDataType.fromInt(Convert.str2Int(pj.getString("DataType")));
            final String pInitDataSource = pj.getString("InitDataSource");
            final String pInitField = StringUtils.substringBetween(pj.getString("Field"), "[", "]");
            final String pControlType = "";
            final String pControlAddition = "";
            final String pControlDefaultValue = "";
            final SQLParam4Form param = new SQLParam4Form(pName, pLabel, pDataType, pInitDataSource, pInitField, pControlType, pControlAddition, pControlDefaultValue, null, null, true, 0, 0);
            result.getParams().add(param);
            if (!StringUtils.isEmpty(pInitDataSource)) {
                final PrinterDataSource PDS = define.findDataSource(pInitDataSource);
                if (PDS != null)
                    PDS.getChildren().add(result);
            }
        }
        return result;
    }

    /**
     * 从数据库中读取模版配置并生成对象
     * 
     * @param con
     * @param templateid
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private ReportTemplateDefine loadTemplateFormDB(final Connection con, final long templateid) throws Exception {
        ReportTemplateDefine result = null;
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getTemplateSQL(), true);
            stmt.setParam(1, templateid);
            stmt.executeQuery();
            if (stmt.next()) {
                result = new ReportTemplateDefine();

                final String content = Convert.bytes2Str(stmt.getBytes(1));
                final JSONObject json = JSONUtils.parserJSONObject(content);
                final JSONArray dss = json.getJSONObject("DataSources").getJSONArray("JSON");
                for (int i = 0; i < dss.size(); i++)
                    result.getDataSources().add(loadJsonObject2DataSource(dss.getJSONObject(i), result));
                result.setPageInfo(json.getJSONObject("PageInfo"));
                result.setTemplate(json.getString("JS"));
                result.setReportName(stmt.getString(3));
                result.setOfficeTemplate(stmt.getBytes(4));
                result.setVersion(stmt.getLong(5));
            }
        } finally {
            DBHELPER.closeCommand(stmt);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.service.portal.report.IReportTemplateService#
     * NotifyTemplateIsModified(long)
     */
    @Override
    public void notifyTemplateIsModified(final long id) {
        SystemCacheManager.getInstance().removeDesignObject(getKey(id));
    }
}
