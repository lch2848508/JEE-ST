package com.estudio.impl.design.objects;

public final class DBObjectFormService4MySQL extends DBObjectFormService {

    private static final DBObjectFormService4MySQL INSTANCE = new DBObjectFormService4MySQL();

    public static DBObjectFormService4MySQL getInstance() {
        return INSTANCE;
    }

    private DBObjectFormService4MySQL() {
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
        return "{call proc_design_delete_form(:id)}";
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
        return "{call proc_design_update_form4workflow(:id,:dss,:controls)}";
    }
}
