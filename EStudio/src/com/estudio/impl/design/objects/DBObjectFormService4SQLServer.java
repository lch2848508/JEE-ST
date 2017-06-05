package com.estudio.impl.design.objects;

public final class DBObjectFormService4SQLServer extends DBObjectFormService {

    private static final DBObjectFormService4SQLServer INSTANCE = new DBObjectFormService4SQLServer();

    public static DBObjectFormService4SQLServer getInstance() {
        return INSTANCE;
    }

    private DBObjectFormService4SQLServer() {
        super();
    }

    /**
     * @return
     */
    @Override
    protected String getControlSQL() {
        return "select dss,controls from sys_object_forms_4_workflow where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getDeleteSQL() {
        return "begin\n  delete from sys_object_forms where id=:id;\n delete sys_object_forms_4_workflow where id=:id;\nend;";
    }

    /**
     * @return
     */
    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('SYS_OBJECT_FORMS','id','sortorder',:id1,:id2)}";
    }

    /**
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return "insert into sys_object_forms(id,dfmstream,xmlstream,datasource,jsscript,type,form_params) values (:id,:dfmstream,:xmlstream,:datasource,:jsscript,:type,:form_params)";
    }

    /**
     * @return
     */
    @Override
    protected String getListSQL() {
        return "select id,dfmstream,xmlstream,datasource,jsscript,version,type from sys_object_forms";
    }

    /**
     * @return
     */
    @Override
    protected String getMoveSQL() {
        return "update SYS_OBJECT_FORMS set p_id=:p_id where id = :id";
    }

    /**
     * @return
     */
    @Override
    protected String getSelectSQL() {
        return "select id,dfmstream,null xmlstream,null datasource,null jsscript,version,type,form_params from sys_object_forms where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateSQL() {
        return "update sys_object_forms set dfmstream=:dfmstream,xmlstream=:xmlstream,datasource=:datasource,jsscript=:jsscript,form_params=:form_params,version= version + 1 where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateWFSQL() {
        return "begin\n update sys_object_forms_4_workflow set dss = :dss, controls = :controls where id = :id;\n if @@rowcount=0 begin\n    insert into sys_object_forms_4_workflow (id, dss, controls) values (:id, :dss, :controls);\n  end;\nend;";
    }

}
