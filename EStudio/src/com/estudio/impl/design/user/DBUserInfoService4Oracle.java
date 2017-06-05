package com.estudio.impl.design.user;

import com.estudio.intf.design.user.IUserInfoService;

public final class DBUserInfoService4Oracle extends DBUserInfoService {

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
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_userinfo where id = :id1; select sortorder into idx_2 from sys_userinfo where id = :id2; update sys_userinfo set sortorder = idx_2 where id = :id1;  update sys_userinfo set sortorder = idx_1 where id = :id2; end;";
    }

    @Override
    protected String getMovetoSQL() {
        return "update sys_userinfo set p_id=:p_id where id = :id";
    }

    @Override
    protected String getRegisterUser2RoleSQL() {
        return "begin update sys_user2role set id = id where u_id = :u_id and r_id = :r_id; if sql%notfound then insert into sys_user2role (id, u_id, r_id) values (seq_for_j2ee_uniqueid.nextval, :u_id, :r_id); end if; end;";
    }

    @Override
    protected String getUnregisterUser2RoleSQL() {
        return "delete sys_user2role where u_id = :u_id and r_id = :r_id";
    }

    @Override
    protected String getUserRoleListSQL() {
        return "select b.id from sys_role b,sys_user2role a where b.valid=1 and a.r_id=b.id and a.u_id=:u_id";
    }

    @Override
    protected String getListByRoleSQL() {
        return "select a.id,realname,loginname,sex,password,mobile,phone,address,postcode,email,duty,photo,p_id,ext1,ext2,ext3 from (select id from sys_department connect by prior id = p_id start with p_id = get_department_rootid(:u_id) and valid = 1) c,sys_userinfo a, sys_user2role b where a.valid=1 and c.id=a.p_id and a.id = b.u_id and b.r_id=:r_id";
    }

    @Override
    protected String getLoginNameExistSQL() {
        return " select count(*) from Sys_Userinfo t where Upper(t.Loginname) = Upper(:loginname) and Id != :id";
    }

    private DBUserInfoService4Oracle() {
        super();
    }

    private static IUserInfoService instance = new DBUserInfoService4Oracle();

    public static IUserInfoService getInstance() {
        return instance;
    }

}
