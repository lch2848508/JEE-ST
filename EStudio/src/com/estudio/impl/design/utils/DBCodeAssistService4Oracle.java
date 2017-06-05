package com.estudio.impl.design.utils;

public final class DBCodeAssistService4Oracle extends DBCodeAssistService {

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
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_code_assistent where id = :id1; select sortorder into idx_2 from sys_code_assistent where id = :id2; update sys_code_assistent set sortorder = idx_2 where id = :id1;  update sys_code_assistent set sortorder = idx_1 where id = :id2; end;";
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

    private static DBCodeAssistService4Oracle instance = new DBCodeAssistService4Oracle();

    public static DBCodeAssistService4Oracle getInstance() {
        return instance;
    }

    private DBCodeAssistService4Oracle() {
        super();
    }

}
