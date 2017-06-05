package com.estudio.impl.design.user;

import java.sql.Connection;

import net.minidev.json.JSONObject;

import com.estudio.intf.design.user.IRoleService;

public final class DBRoleService4Oracle extends DBRoleService implements IRoleService {

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
        return new StringBuilder().append("declare\n") //
                .append("    v integer;\n") //
                .append("begin\n") //
                .append("    select Nvl(Allow_Del, 1) into v from Sys_Role where Id = :Id;\n") //
                .append("    if v = 1 then\n") //
                .append("        update Sys_Role set Valid = 0 where Id = :Id;\n") //
                .append("    else\n") //
                .append("        Raise_Application_Error(-20100, '系统保留角色,不能被删除!');\n") //
                .append("    end if;\n") //
                .append("end;").toString();

    }

    @Override
    protected String getListSQL() {
        return "select id,name,descript from sys_role where valid=0 and type=1 order by sortorder";
    }

    @Override
    protected String getExchangeSQL() {
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_role where id = :id1; select sortorder into idx_2 from sys_role where id = :id2; update sys_role set sortorder = idx_2 where id = :id1;  update sys_role set sortorder = idx_1 where id = :id2; end;";
    }

    @Override
    protected String getExchangeRoleTypeSQL() {
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_role_type where id = :id1; select sortorder into idx_2 from sys_role_type where id = :id2; update sys_role_type set sortorder = idx_2 where id = :id1;  update sys_role_type set sortorder = idx_1 where id = :id2; end;";
    }

    @Override
    protected String getMovetoSQL() {
        return "update sys_role set p_id=:p_id where id = :id";
    }

    @Override
    protected String getMoveRoleToTypeSQL() {
        return "update sys_role set p_id=:p_id where id = :id";
    }

    private DBRoleService4Oracle() {
        super();
    }

    private static IRoleService instance = new DBRoleService4Oracle();

    public static IRoleService getInstance() {
        return instance;
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
        return new StringBuilder().append("declare\n") //
                .append("    v integer;\n") //
                .append("begin\n") //
                .append("    select Nvl(Allow_Del, 1) into v from Sys_Role_Type where Id = :Id;\n") //
                .append("    if v = 1 then\n") //
                .append("        update Sys_Role_Type set Valid = 0 where Id = :Id;\n") //
                .append("        update sys_role set p_id=-1 where p_id=:Id;\n") //
                .append("    else\n") //
                .append("        Raise_Application_Error(-20100, '系统保留分组,不能被删除!');\n") //
                .append("    end if;\n") //
                .append("end;").toString();
    }

    @Override
    public JSONObject listRoleType(final Connection con) throws Exception {
        return null;
    }

}
