package com.estudio.impl.design.user;

import com.estudio.intf.design.user.IUserInfoService;

public final class DBUserInfoService4MySQL extends DBUserInfoService {

    @Override
    protected String getSelectSQL() {
        return "select id,realname,loginname,sex,password,mobile,phone,address,postcode,email,duty,photo,p_id,ext1,ext2,ext3 from sys_userinfo where id=:id";
    }

    @Override
    protected String getUpdateSQL() {
        return "update sys_userinfo set realname=:realname,loginname=:loginname,sex=:sex,password=:password,mobile=:mobile,phone=:phone,address=:address,postcode=:postcode,email=:email,duty=:duty,photo=:photo,ext1=:ext1,ext2=:ext2,ext3=:ext3 where id=:id";

    }

    @Override
    protected String getInsertSQL() {
        return "insert into sys_userinfo(id,realname,loginname,sex,password,mobile,phone,address,postcode,email,duty,photo,sortorder,p_id,ext1,ext2,ext3) values (:id,:realname,:loginname,:sex,:password,:mobile,:phone,:address,:postcode,:email,:duty,:photo,:id,:p_id,:ext1,:ext2,:ext3)";
    }

    @Override
    protected String getDeleteSQL() {
        return "update sys_userinfo set valid=0 where id=:id";
    }

    @Override
    protected String getListSQL() {
        return "select id,realname,loginname,sex,password,mobile,phone,address,postcode,email,duty,photo,p_id,ext1,ext2,ext3 from sys_userinfo where valid=1 and p_id=:p_id order by sortorder";
    }

    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('sys_userinfo','id','sortorder',:id1,:id2)}";
    }

    @Override
    protected String getMovetoSQL() {
        return "update sys_userinfo set p_id=:p_id where id = :id";
    }

    @Override
    protected String getRegisterUser2RoleSQL() {
        return "{call proc_design_register_user2role(:u_id,:r_id)}";
    }

    @Override
    protected String getUnregisterUser2RoleSQL() {
        return "delete from sys_user2role where u_id = :u_id and r_id = :r_id";
    }

    @Override
    protected String getUserRoleListSQL() {
        return "select b.id from sys_role b,sys_user2role a where b.valid=1 and a.r_id=b.id and a.u_id=:u_id";
    }

    @Override
    protected String getListByRoleSQL() {
        return "select a.id,realname,loginname,sex,password,mobile,phone,address,postcode,email,duty,photo,p_id,ext1,ext2,ext3 from sys_userinfo a,sys_user2role b where a.valid=1 and a.id = b.u_id and b.r_id = :r_id";
    }

    @Override
    protected String getLoginNameExistSQL() {
        return "SELECT fun_usermanager_Is_Loginname_Exists(:loginname,:id)";
    }

    private DBUserInfoService4MySQL() {
        super();
    }

    private static IUserInfoService instance = new DBUserInfoService4MySQL();

    public static IUserInfoService getInstance() {
        return instance;
    }

}
