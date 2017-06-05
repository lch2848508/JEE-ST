package com.estudio.impl.design.objects;

public final class DBObjectComboBoxTemplateService4MySQL extends DBObjectComboBoxTemplateService {

    public static DBObjectComboBoxTemplateService4MySQL getInstance() {
        return instance;
    }

    private static DBObjectComboBoxTemplateService4MySQL instance = new DBObjectComboBoxTemplateService4MySQL();

    private DBObjectComboBoxTemplateService4MySQL() {
        super();
    }

    /**
     * @return
     */
    @Override
    protected String getDeleteSQL() {
        return "delete from sys_combobox_template where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('SYS_COMBOBOX_TEMPLATE','id','sortorder',:id1,:id2)}";
    }

    /**
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return "insert into sys_combobox_template(id,name,content) values (:id,:name,:content)";
    }

    /**
     * @return
     */
    @Override
    protected String getListSQL() {
        return "select id,name,content from sys_combobox_template";
    }

    /**
     * @return
     */
    @Override
    protected String getMoveSQL() {
        return "update SYS_COMBOBOX_TEMPLATE set p_id=:p_id where id = :id";
    }

    /**
     * @return
     */
    @Override
    protected String getSelectSQL() {
        return "select id,name,content from sys_combobox_template where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateSQL() {
        return "update sys_combobox_template set id=:id,name=:name,content=:content where id=:id";
    }
}
