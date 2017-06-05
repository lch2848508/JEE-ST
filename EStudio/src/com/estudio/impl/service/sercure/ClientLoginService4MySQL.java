package com.estudio.impl.service.sercure;

import net.minidev.json.JSONObject;

/**
 * 用户登录服务</br> 用户登录服务提供用户登录</br> 更改密码</br> 注销功能</br>
 * 
 * @author Administrator
 * 
 */
public class ClientLoginService4MySQL extends ClientLoginService {

    /**
     * 获取更改密码SQL
     * 
     * @return
     */
    @Override
    protected String getChangePasswordSQL() {
        return "update sys_userinfo set password=? where id=?";
    }

    /**
     * @return
     */
    @Override
    protected String getUserInfoRoleSQL() {
        return "select r_id from sys_user2role where u_id=?";
    }

    /**
     * @return
     */
    @Override
    protected String getLoginInfoSQL() {
        return "select id,realname,loginname,ifnull(password,'') password ,ifnull(p_id,-1) p_id,ext1,ext2,ext3,duty,1 is_mis_role,1 is_gis_role from sys_userinfo where id=? LIMIT 0,1";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateLoginByOthersSQL() {
        return "update sys_userlogin_by_others set isvalid=0 where uuid=?";
    }

    /**
     * @return
     */
    @Override
    protected String getLoginByOtherWaySQL() {
        return "select id,realname,loginname,ifnull(password,'') password,ifnull(p_id,-1) p_id,ext1,ext2,ext3,duty from sys_userinfo where id in (select login_userid from sys_userlogin_by_others where rndcode=? and uuid=? and isvalid=1) LIMIT 0,1";
    }

    @Override
    protected String getUserIdSQL() {
        return "select fun_usermanager_get_userid(?,?,?,?,?) id";
    }

    protected ClientLoginService4MySQL() {
        super();
    }

    private static final ClientLoginService4MySQL INSTANCE = new ClientLoginService4MySQL();

    public static ClientLoginService4MySQL getInstance() {
        return INSTANCE;
    }



}
