package com.estudio.impl.design.portal;

public final class DBPortalRightService4MySQL extends DBPortalRightService {

    private static DBPortalRightService4MySQL instance = new DBPortalRightService4MySQL();

    public static DBPortalRightService4MySQL getInstance() {
        return instance;
    }

    private DBPortalRightService4MySQL() {
        super();
    }

    /**
     * @return
     */
    @Override
    protected String getDeletePortalRightSQL() {
        return "delete from sys_portal_right";
    }

    /**
     * @return
     */
    @Override
    protected String getGroupPortalRightSQL() {
        return "select a.name,a.id role_id,ifnull(readable, 0) readable, ifnull(writeable, 0) writeable from sys_role a left join sys_portal_right b on a.id=b.role_id where a.id in (select role_id from sys_portal_right where portal_id = -1) and a.valid = 1 and b.portal_id=? and a.id > 0 order by a.sortorder";
    }

    /**
     * @return
     */
    @Override
    protected String getInsertPortalRightSQL() {
        return "insert into sys_portal_right (id, portal_id, role_id, readable, writeable) values (?, ?, ?, ?, ?)";
    }

    /**
     * @return
     */
    @Override
    protected String getPortalItemRightSQL() {
        return "select a.name,a.id role_id,ifnull(readable, 0) readable,ifnull(writeable, 0) writeable from sys_role a left join sys_portal_right b on a.id=b.role_id where a.id in (select role_id from sys_portal_right where portal_id in (select p_id from sys_portal_item where id = ?)) and a.valid = 1 and b.portal_id= ? and a.id > 0 order by a.sortorder";
    }

    /**
     * @return
     */
    @Override
    protected String getPortalRightByUserIdSQL() {
        return "select sum(readable),sum(writeable) from sys_portal_right where portal_id=? and role_id in (select r_id from sys_user2role where u_id=?)";
    }

    /**
     * @return
     */
    @Override
    protected String getRootPortalRightSQL() {
        return "select a.name,a.id role_id,ifnull(readable, 0) readable,ifnull(writeable, 0) writeable from sys_role a left join sys_portal_right b on a.id = b.role_id and b.portal_id=-1 where a.id>0 and a.valid=1 order by a.sortorder";
    }
}
