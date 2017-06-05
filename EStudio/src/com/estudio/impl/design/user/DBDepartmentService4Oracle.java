package com.estudio.impl.design.user;

import com.estudio.intf.design.user.IDepartmentService;

public final class DBDepartmentService4Oracle extends DBDepartmentService {

    @Override
    protected String getSelectSQL() {
        return "select id,name,p_id from sys_department where id=:id";
    }

    @Override
    protected String getUpdateSQL() {
        return "update sys_department set name=:name where id=:id";
    }

    @Override
    protected String getInsertSQL() {
        return "insert into sys_department(id,p_id,name,sortorder) values (:id,:p_id,:name,:id)";
    }

    @Override
    protected String getDeleteSQL() {
        return "update sys_department set valid=0 where id=:id";
    }

    @Override
    protected String getListSQL() {
        return "select id,name,p_id from sys_department where is_role_readable(id,:u_id)=1 and p_id=:p_id order by sortorder";
    }

    @Override
    protected String getExchangeSQL() {
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_department where id = :id1; select sortorder into idx_2 from sys_department where id = :id2; update sys_department set sortorder = idx_2 where id = :id1;  update sys_department set sortorder = idx_1 where id = :id2; end;";
    }

    @Override
    protected String getMovetoSQL() {
        return "update sys_department set p_id=:p_id where id = :id";
    }

    /**
     * ¹¹Ôìº¯Êý
     */
    private DBDepartmentService4Oracle() {
        super();
    }

    private static IDepartmentService instance = new DBDepartmentService4Oracle();

    public static IDepartmentService getInstance() {
        return instance;
    }

}
