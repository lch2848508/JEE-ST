package com.estudio.impl.design.user;

import net.minidev.json.JSONObject;

import com.estudio.intf.design.user.IUserManagerService;

public final class DBUserManagerService4MySQL extends DBUserManagerService {

    private DBUserManagerService4MySQL() {
        super();
    }

    private static final IUserManagerService INSTANCE = new DBUserManagerService4MySQL();

    public static IUserManagerService getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getSystemRoleSQL() {
        return "select sys_role.id,sys_role.name,ifnull(sys_role_type.name,'ÎÞ·Ö×é') groupname from sys_role left join sys_role_type on sys_role_type.id=sys_role.p_id and sys_role_type.valid=1  where sys_role.valid=1 order by sys_role_type.sortorder,sys_role.sortorder";
    }

    @Override
    protected String getDepartmentsByParentDepartmentIdSQL() {
        return "select id,name from sys_department where valid=1 and p_id=? order by sortorder";
    }

    @Override
    protected String getSysUserInfosByDepartmentIdSQL() {
        return "select id,concat(realname,'[',loginname,']') from sys_userinfo where valid=1 and p_id=? order by sortorder";
    }

    @Override
    protected String getDepartmentRootIdSQL() {
        return "select fun_usermanager_Get_Department_Rootid(?)";
    }

    @Override
    protected String getRolesInfoSQL() {
        return "select id,name,descript,p_id from sys_role where type=1 and fun_usermanager_IS_ROLE_READABLE(id,?)=1 and valid=1 order by sortorder";
    }

    @Override
    protected String getUsersListByDepartmentIdSQL() {
        return "select id,realname from sys_userinfo where valid=1 and ifnull(p_id,-1)=? order by sortorder";
    }

    @Override
    protected String getRoleTypesSQL() {
        return "select id,name from sys_role_type where valid=1 and fun_usermanager_IS_ROLE_type_READABLE(id,?) =1 order by sortorder";
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
