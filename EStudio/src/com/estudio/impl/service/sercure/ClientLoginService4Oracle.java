package com.estudio.impl.service.sercure;

/**
 * 用户登录服务</br> 用户登录服务提供用户登录</br> 更改密码</br> 注销功能</br>
 * 
 * @author Administrator
 * 
 */
public class ClientLoginService4Oracle extends ClientLoginService {

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
        return "select id,realname,loginname,nvl(password,''),nvl(p_id,-1),ext1,ext2,ext3,duty,estudio_usermanager.is_mis_role(id) is_mis_role,estudio_usermanager.is_gis_role(id) is_gis_role from sys_userinfo where id=? and rownum=1";
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
        return "select id,realname,loginname,nvl(password,''),nvl(p_id,-1),ext1,ext2,ext3,duty,estudio_usermanager.is_mis_role(id) is_mis_role,estudio_usermanager.is_gis_role(id) is_gis_role from sys_userinfo where id = (select login_userid from sys_userlogin_by_others where rndcode=? and uuid=? and isvalid=1)";
    }

    @Override
    protected String getUserIdSQL() {
        return "select estudio_usermanager.get_userid(?,?,?,?,?) id from dual";
    }

    protected ClientLoginService4Oracle() {
        super();
    }

    private static final ClientLoginService4Oracle INSTANCE = new ClientLoginService4Oracle();

    public static ClientLoginService4Oracle getInstance() {
        return INSTANCE;
    }

}
