package com.estudio.impl.design.utils;

public final class DBCodeAssistService4MySQL extends DBCodeAssistService {

    /**
     * @return
     */
    @Override
    protected String getMoveSQL() {
        return "update sys_code_assistent set pid=:pid where id = :id";
    }

    /**
     * @return
     */
    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('sys_code_assistent','id','sortorder',:id1,:id2)}";
    }

    /**
     * @return
     */
    @Override
    protected String getDeleteSQL() {
        return "delete from sys_code_assistent where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return "insert into sys_code_assistent(id,caption,content,pid,help,sortorder,type,exttype) values (:id,:caption,:content,:pid,:help,:id,:type,:exttype)";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateSQL() {
        return "update sys_code_assistent set caption=:caption,content=:content,type=:type,exttype=:exttype where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getSelectSQL() {
        return "select id, caption,content,help,type from sys_code_assistent where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getCodeAssistSQL() {
        return "select id, caption, content, help, type,exttype from sys_code_assistent where pid = ? order by sortorder";
    }

    private static DBCodeAssistService4MySQL instance = new DBCodeAssistService4MySQL();

    public static DBCodeAssistService4MySQL getInstance() {
        return instance;
    }

    private DBCodeAssistService4MySQL() {
        super();
    }

}
