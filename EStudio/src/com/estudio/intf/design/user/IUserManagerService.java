package com.estudio.intf.design.user;

import net.minidev.json.JSONObject;

public interface IUserManagerService {

    /**
     * 得到部门及用户列表
     * 
     * @param pid
     * @param id
     * @return
     */
    public abstract JSONObject getDepartmentsAndUsers(long pid, long userID);

    /**
     * 得到部门及用户列表
     * 
     * @param pid
     * @param userID
     * @return
     */
    public abstract JSONObject getDepartmentsAndRoles(long pid, long userID);

    /**
     * 得到部门中的用户列表
     * 
     * @param pid
     * @param userID
     * @return
     */
    public abstract JSONObject getUsersByDepartment(long pid, long userID);

    /**
     * 得到工作组中的用户列表
     * 
     * @param pid
     * @param userID
     * @return
     */
    public abstract JSONObject getUsersByRole(long pid, long userID);

    /**
     * 获取系统角色列表
     * 
     * @return
     */
    public abstract JSONObject getSystemRoles();

}
