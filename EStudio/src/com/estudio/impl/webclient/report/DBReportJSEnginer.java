package com.estudio.impl.webclient.report;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.report.ReportTemplateDefine;
import com.estudio.intf.db.IDBHelper;
import com.estudio.officeservice.ExcelService;
import com.estudio.officeservice.ExcelServiceConst;
import com.estudio.service.script.ScriptService;

public class DBReportJSEnginer {
    /**
     * 
     * @param ids
     * @param httpParams
     */
    private DBReportJSEnginer(long[] ids, Map<String, String> httpParams) {
        this.reportIds = new long[ids.length];
        for (int i = 0; i < ids.length; i++)
            this.reportIds[i] = ids[i];
        this.httpParams = httpParams;
    }

    /**
     * 
     * @param id
     * @param httpParams
     * @return
     */
    public static DBReportJSEnginer create(long id, Map<String, String> httpParams) {
        long[] ids = new long[1];
        ids[0] = id;
        return new DBReportJSEnginer(ids, httpParams);
    }

    public static DBReportJSEnginer create(long[] ids, Map<String, String> httpParams) {
        return new DBReportJSEnginer(ids, httpParams);
    }

    private long[] reportIds;
    private Map<String, String> httpParams;
    private IDBHelper dBHelper = RuntimeContext.getDbHelper();
    private List<ExcelService> excelServiceList = new ArrayList<ExcelService>();

    /**
     * 
     * @return
     * @throws Exception
     */
    public void execute(String filename) throws Exception {
        Connection con = null;
        try {
            con = dBHelper.getConnection();
            for (long reportId : this.reportIds)
                this.excelServiceList.add(executeReportTemplate(con, reportId));
            ExcelService.mergeMultiWorkbook(excelServiceList, filename);
        } finally {
            dBHelper.closeConnection(con);
        }
    }

    private ExcelService executeReportTemplate(Connection con, long reportId) throws Exception {
        ExcelService excelService = null;
        ReportTemplateDefine reportDefine = RuntimeContext.getReportDefineService().getDefine(con, reportId);
        byte[] officeTemplate = reportDefine.getOfficeTemplate();
        if (officeTemplate == null || officeTemplate.length == 0)
            excelService = ExcelService.getInstance();
        else
            excelService = ExcelService.getInstance(new ByteArrayInputStream(officeTemplate));
        Map<String, Object> jsContext = new HashMap<String, Object>();
        jsContext.put("excelInstance", excelService);
        jsContext.put("reportDBHelper", new ReportDBHelper(con, reportDefine));
        jsContext.put("httpParams", httpParams);
        jsContext.put("sessionId", httpParams.get("sessionId"));
        jsContext.put("USER_ID", httpParams.get("USER_ID"));
        jsContext.put("excelConst", ExcelServiceConst.instance);
        jsContext.put("reportUtils", DBReportUtils.instance);
        jsContext.put("ExcelConst", ExcelServiceConst.instance);
        jsContext.put("ReportUtils", DBReportUtils.instance);
        jsContext.put("ExcelInstance", excelService);
        jsContext.put("ReportDBHelper", new ReportDBHelper(con, reportDefine));
        String jsScript = "function getParam(name){return httpParams.get(name);}\n";
        jsScript += reportDefine.getTemplate();
        ScriptService.getInstance().eval(jsScript, jsContext);
        return excelService;
    }

    /**
     * ÊÍ·Å×ÊÔ´
     */
    public void disponse() {
        for (ExcelService excelService : excelServiceList)
            excelService.dispose();
    }
}
