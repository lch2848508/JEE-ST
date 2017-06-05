package com.estudio.impl.design.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.intf.design.user.IRoleService;

public final class DBRoleService4MySQL extends DBRoleService implements IRoleService {

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
        return "{call proc_design_delete_role(?,?,?)}";

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
        return "{call proc_design_delete_roletype(?,?,?)}";
    }

    @Override
    public boolean deleteRole(Connection con, long id) throws Exception {
        String SQL = getDeleteRoleSQL();
        return deleteRoleOrRoleTypeSQL(con, id, SQL);
    }

    @Override
    public boolean deleteRoleType(Connection con, long id) throws Exception {
        String SQL = getDeleteRoleTypeSQL();
        return deleteRoleOrRoleTypeSQL(con, id, SQL);
    }

    /**
     * 删除用户角色或角色类型
     * 
     * @param con
     * @param id
     * @param SQL
     * @return
     * @throws SQLException
     * @throws Exception
     */
    private boolean deleteRoleOrRoleTypeSQL(Connection con, long id, String SQL) throws SQLException, Exception {
        boolean result = false;
        CallableStatement stmt = null;
        try {
            stmt = con.prepareCall(SQL);
            stmt.registerOutParameter(2, java.sql.Types.INTEGER);
            stmt.registerOutParameter(3, java.sql.Types.VARCHAR);
            stmt.setLong(1, id);
            stmt.setInt(2, 0);
            stmt.setNull(3, java.sql.Types.VARCHAR);
            stmt.execute();
            if (stmt.getInt(2) != 1)
                throw new Exception(stmt.getString(3));
        } finally {
            if (stmt != null)
                stmt.close();
        }
        return result;
    }

    @Override
    public JSONObject listRoleType(final Connection con) throws Exception {
        return null;
    }

    private DBRoleService4MySQL() {
        super();
    }

    private static IRoleService instance = new DBRoleService4MySQL();

    public static IRoleService getInstance() {
        return instance;
    }

}
