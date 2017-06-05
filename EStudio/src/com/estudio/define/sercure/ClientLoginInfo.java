package com.estudio.define.sercure;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientLoginInfo implements Serializable {
    private static final long serialVersionUID = -640502166287743191L;
    long id; // 唯一标识号
    private String realName; // 真实姓名
    private String loginName; // 登录名
    private String ext1;
    private String ext2;
    private String ext3;
    private String sessionId;
    private String duty;
    private boolean gisRole = false;
    private boolean misRole = false;

    public boolean isGisRole() {
        return gisRole;
    }

    public void setGisRole(boolean gisRole) {
        this.gisRole = gisRole;
    }

    public boolean isMisRole() {
        return misRole;
    }

    public void setMisRole(boolean misRole) {
        this.misRole = misRole;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(final String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(final String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(final String ext3) {
        this.ext3 = ext3;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setRealName(final String realName) {
        this.realName = realName;
    }

    public void setLoginName(final String loginName) {
        this.loginName = loginName;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    String password; // 用户密码
    private long departmentId;
    ArrayList<Long> roles = new ArrayList<Long>();

    public long getId() {
        return id;
    }

    public String getRealName() {
        return realName;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Long> getRoles() {
        return roles;
    }

    /**
     * 判断用户是否隶属于角色
     * 
     * @param roleID
     * @return
     */
    public boolean isRole(final long roleID) {
        return roles.indexOf(roleID) != -1;
    }

    /**
     * 构造函数
     * 
     * @param id
     *            用户ID
     * @param realName
     *            真实姓名
     * @param loginName
     *            登录名
     * @param password
     *            用户密码
     */
    public ClientLoginInfo(final long id, final String realName, final String loginName, final String password, final long departmentId, final String ext1, final String ext2, final String ext3, final String sessionId, String duty,boolean misRole,boolean gisRole) {
        super();
        this.id = id;
        this.realName = realName;
        this.loginName = loginName;
        this.password = password;
        this.departmentId = departmentId;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.sessionId = sessionId;
        this.duty = duty;
        this.gisRole = gisRole;
        this.misRole = misRole;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(final long departmentId) {
        this.departmentId = departmentId;
    }
}
