package com.estudio.impl.design.objects;

public final class DBObjectComboBoxTemplateService4Oracle extends DBObjectComboBoxTemplateService {

    public static DBObjectComboBoxTemplateService4Oracle getInstance() {
        return instance;
    }

    private static DBObjectComboBoxTemplateService4Oracle instance = new DBObjectComboBoxTemplateService4Oracle();

    private DBObjectComboBoxTemplateService4Oracle() {
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
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from SYS_COMBOBOX_TEMPLATE where id = :id1; select sortorder into idx_2 from SYS_COMBOBOX_TEMPLATE where id = :id2; update SYS_COMBOBOX_TEMPLATE set sortorder = idx_2 where id = :id1;  update SYS_COMBOBOX_TEMPLATE set sortorder = idx_1 where id = :id2; end;";
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
