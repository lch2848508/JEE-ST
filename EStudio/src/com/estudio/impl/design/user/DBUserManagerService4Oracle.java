package com.estudio.impl.design.user;

import net.minidev.json.JSONObject;

import com.estudio.intf.design.user.IUserManagerService;

public final class DBUserManagerService4Oracle extends DBUserManagerService {

    private DBUserManagerService4Oracle() {
        super();
    }

    private static final IUserManagerService INSTANCE = new DBUserManagerService4Oracle();

    public static IUserManagerService getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getSystemRoleSQL() {
        return "select sys_role.id,sys_role.name,nvl(sys_role_type.name,'ÎÞ·Ö×é') groupname from sys_role left join sys_role_type on sys_role_type.id=sys_role.p_id and sys_role_type.valid=1  where sys_role.valid=1 order by sys_role_type.sortorder,sys_role.sortorder";
    }

    @Override
    protected String getDepartmentsByParentDepartmentIdSQL() {
        return "select id,name from sys_department where valid=1 and p_id=? order by sortorder";
    }

    @Override
    protected String getSysUserInfosByDepartmentIdSQL() {
        return "select id,realname||'['||loginname||']' from sys_userinfo where valid=1 and p_id=? order by sortorder";
    }

    @Override
    protected String getDepartmentRootIdSQL() {
        return "select GET_DEPARTMENT_ROOTID(?) from dual";
    }

    @Override
    protected String getRolesInfoSQL() {
        return "select id,name,descript,p_id from sys_role where IS_ROLE_READABLE(id,?)=1 and type=1 and valid=1 order by sortorder";
    }

    @Override
    protected String getUsersListByDepartmentIdSQL() {
        return "select id,realname from sys_userinfo where valid=1 and nvl(p_id,-1)=? order by sortorder";
    }

    @Override
    protected String getRoleTypesSQL() {
        return "select id,name from sys_role_type where valid=1 and IS_ROLE_type_READABLE(id,?)=1 order by sortorder";
    }

    @Override
    public JSONObject getUsersByDepartment(final long pid, final long userID) {
        return null;
    }

    @Override
    public JSONObject getUsersByRole(final long pid, final long userID) {
        return null;
    }

}
