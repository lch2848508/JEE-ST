package com.estudio.impl.design.user;

import com.estudio.intf.design.user.IDepartmentService;

public final class DBDepartmentService4MySQL extends DBDepartmentService {

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
        return "select id,name,p_id from sys_department where /*is_role_readable(id,:u_id)=1 and*/ p_id=:p_id order by sortorder";
    }

    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('sys_department','id','sortorder',:id1,:id2)}";
    }

    @Override
    protected String getMovetoSQL() {
        return "update sys_department set p_id=:p_id where id = :id";
    }

    /**
     * ¹¹Ôìº¯Êý
     */
    private DBDepartmentService4MySQL() {
        super();
    }

    private static IDepartmentService instance = new DBDepartmentService4MySQL();

    public static IDepartmentService getInstance() {
        return instance;
    }

}
