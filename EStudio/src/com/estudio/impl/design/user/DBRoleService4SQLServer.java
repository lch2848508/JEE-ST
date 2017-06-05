package com.estudio.impl.design.user;

import java.sql.Connection;

import net.minidev.json.JSONObject;

import com.estudio.intf.design.user.IRoleService;

public final class DBRoleService4SQLServer extends DBRoleService implements IRoleService {

    @Override
    protected String getSelectSQL() {
        return "select id,name,descript from sys_role where id=:id";
    }

    @Override
    protected String getUpdateSQL() {
        return "update sys_role set name=:name,descript=:descript where id=:id";
    }

    @Override
    protected String getInsertSQL() {
        return "insert into sys_role(id,name,descript,sortorder,p_id) values (:id,:name,:descript,:id,:p_id)";
    }

    @Override
    protected String getDeleteRoleSQL() {
        return new StringBuilder().append("declare @v integer;\n") //
                .append("begin\n") //
                .append("    select @v = isnull(Allow_Del, 1) from Sys_Role where Id = :Id;\n") //
                .append("    if @v = 1 \n") //
                .append("        update Sys_Role set Valid = 0 where Id = :Id;\n") //
                .append("    else\n") //
                .append("        RAISERROR('系统保留角色,不能被删除!',16,1) WITH NOWAIT;\n") //
                .append("end;").toString();

    }

    @Override
    protected String getListSQL() {
        return "select id,name,descript from sys_role where valid=0 and type=1 order by sortorder";
    }

    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('sys_role','id','sortorder',:id1,:id2)}";
    }

    @Override
    protected String getExchangeRoleTypeSQL() {
        return "{call proc_exchange_record_sortorder('sys_role_type','id','sortorder',:id1,:id2)}";
    }

    @Override
    protected String getMovetoSQL() {
        return "update sys_role set p_id=:p_id where id = :id";
    }

    @Override
    protected String getMoveRoleToTypeSQL() {
        return "update sys_role set p_id=:p_id where id = :id";
    }

    @Override
    protected String getSelectTypeSQL() {
        return "select id,name from sys_role_type where id=:id";
    }

    @Override
    protected String getInsertTypeSQL() {
        return "insert into sys_role_type (id, name, sortorder) values (:id, :name, :id)";
    }

    @Override
    protected String getUpdateTypeSQL() {
        return "update sys_role_type set name=:name where id=:id";
    }

    @Override
    protected String getDeleteRoleTypeSQL() {
        return new StringBuilder().append("declare @v integer;\n") //
                .append("begin\n") //
                .append("    select @v = isnull(Allow_Del, 1) from Sys_Role_Type where Id = :Id;\n") //
                .append("    if @v = 1 begin\n") //
                .append("        update Sys_Role_Type set Valid = 0 where Id = :Id;\n") //
                .append("        update sys_role set p_id=-1 where p_id=:Id;\n") //
                .append("    end else begin\n") //
                .append("        RAISERROR('系统保留分组,不能被删除!',16,1) WITH NOWAIT;\n") //
                .append("    end;\n") //
                .append("end;").toString();
    }

    @Override
    public JSONObject listRoleType(final Connection con) throws Exception {
        return null;
    }

    private DBRoleService4SQLServer() {
        super();
    }

    private static IRoleService instance = new DBRoleService4SQLServer();

    public static IRoleService getInstance() {
        return instance;
    }

}
