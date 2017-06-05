package com.estudio.impl.design.objects;

public final class DBObjectReportService4Oracle extends DBObjectReportService {

    public static DBObjectReportService4Oracle getInstance() {
        return INSTANCE;
    }

    private static final DBObjectReportService4Oracle INSTANCE = new DBObjectReportService4Oracle();

    private DBObjectReportService4Oracle() {
        super();
    }

    /**
     * @return
     */
    @Override
    protected String getDeleteSQL() {
        return "delete from sys_object_report where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return "insert into sys_object_report(id,content,report_params,template) values (:id,:content,:report_params,:template)";
    }

    /**
     * @return
     */
    @Override
    protected String getReportTemplateSQL() {
        return "select template from sys_object_report where id=?";
    }

    /**
     * @return
     */
    @Override
    protected String getSelectSQL() {
        return "select id,content,version,report_params from sys_object_report where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateSQL() {
        return "update sys_object_report set id=:id,content=:content,version=version+1,report_params=:report_params,template=:template where id=:id";
    }

}
