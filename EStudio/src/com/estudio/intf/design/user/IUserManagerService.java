package com.estudio.intf.design.user;

import net.minidev.json.JSONObject;

public interface IUserManagerService {

    /**
     * �õ����ż��û��б�
     * 
     * @param pid
     * @param id
     * @return
     */
    public abstract JSONObject getDepartmentsAndUsers(long pid, long userID);

    /**
     * �õ����ż��û��б�
     * 
     * @param pid
     * @param userID
     * @return
     */
    public abstract JSONObject getDepartmentsAndRoles(long pid, long userID);

    /**
     * �õ������е��û��б�
     * 
     * @param pid
     * @param userID
     * @return
     */
    public abstract JSONObject getUsersByDepartment(long pid, long userID);

    /**
     * �õ��������е��û��б�
     * 
     * @param pid
     * @param userID
     * @return
     */
    public abstract JSONObject getUsersByRole(long pid, long userID);

    /**
     * ��ȡϵͳ��ɫ�б�
     * 
     * @return
     */
    public abstract JSONObject getSystemRoles();

}
