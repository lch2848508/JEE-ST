package com.estudio.define.webclient.report;

import java.util.ArrayList;

import net.minidev.json.JSONObject;

public class ReportTemplateDefine {
    ArrayList<PrinterDataSource> dataSources = new ArrayList<PrinterDataSource>(); // 数据源
    String documentType;
    String reportName;
    byte[] officeTemplate;
    long version;

    public byte[] getOfficeTemplate() {
        return officeTemplate;
    }

    public void setOfficeTemplate(byte[] officeTemplate) {
        this.officeTemplate = officeTemplate;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    JSONObject pageInfo = null;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(final String reportName) {
        this.reportName = reportName;
    }

    String template;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(final String template) {
        this.template = template;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(final String documentType) {
        this.documentType = documentType;
    }

    public ArrayList<PrinterDataSource> getDataSources() {
        return dataSources;
    }

    public JSONObject getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(final JSONObject paperParams) {
        pageInfo = paperParams;
    }

    /**
     * 根据名称查找数据源
     * 
     * @param name
     * @return
     */
    public PrinterDataSource findDataSource(final String name) {
        PrinterDataSource result = null;
        for (int i = 0; i < dataSources.size(); i++)
            if (dataSources.get(i).name.equals(name)) {
                result = dataSources.get(i);
                break;
            }
        return result;
    }
}
