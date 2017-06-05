package com.estudio.impl.design.objects;

public final class DBObjectFormService4Oracle extends DBObjectFormService {

    private static final DBObjectFormService4Oracle INSTANCE = new DBObjectFormService4Oracle();

    public static DBObjectFormService4Oracle getInstance() {
        return INSTANCE;
    }

    private DBObjectFormService4Oracle() {
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
        return "begin delete from sys_object_forms where id=:id; delete sys_object_forms_4_workflow where id=:id; end;";
    }

    /**
     * @return
     */
    @Override
    protected String getExchangeSQL() {
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from SYS_OBJECT_FORMS where id = :id1; select sortorder into idx_2 from SYS_OBJECT_FORMS where id = :id2; update SYS_OBJECT_FORMS set sortorder = idx_2 where id = :id1;  update SYS_OBJECT_FORMS set sortorder = idx_1 where id = :id2; end;";
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
        return "begin update sys_object_forms_4_workflow set dss = :dss, controls = :controls where id = :id; if SQL%NOTFOUND then insert into sys_object_forms_4_workflow (id, dss, controls) values (:id, :dss, :controls); end if; end;";
    }

}
