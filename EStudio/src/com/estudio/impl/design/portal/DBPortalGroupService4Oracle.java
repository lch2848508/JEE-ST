package com.estudio.impl.design.portal;

public final class DBPortalGroupService4Oracle extends DBPortalGroupService {

    /**
     * @return
     */

    @Override
    protected String getMoveSQL() {
        return "update Sys_portal_group set p_id=:p_id where id = :id";
    }

    /**
     * @return
     */

    @Override
    protected String getExchangeSQL() {
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from Sys_portal_group where id = :id1; select sortorder into idx_2 from Sys_portal_group where id = :id2; update Sys_portal_group set sortorder = idx_2 where id = :id1;  update Sys_portal_group set sortorder = idx_1 where id = :id2; end;";
    }

    /**
     * @return
     */

    @Override
    protected String getListSQL() {
        return "select id,name,sortorder,memo,createdate,published,icon from sys_portal_group";
    }

    /**
     * @return
     */

    @Override
    protected String getDeleteSQL() {
        return "delete from sys_portal_group where id=:id";
    }

    /**
     * @return
     */

    @Override
    protected String getInsertSQL() {
        return "insert into sys_portal_group(id,name,sortorder,memo,published,icon) values (:id,:name,:id,:memo,:published,:icon)";
    }

    /**
     * @return
     */

    @Override
    protected String getSelectSQL() {
        return "select * from sys_portal_group where id=:id";
    }

    /**
     * @return
     */

    @Override
    protected String getPublishedPortalGroupSQL() {
        return "update sys_portal_group set published=? where id=?";
    }

    /**
     * @return
     */

    @Override
    protected String getSystemRoleListSQL() {
        return "select sys_role.id,sys_role.name,nvl(sys_role_type.name,'ÎÞ·Ö×é') groupname from sys_role left join sys_role_type on sys_role_type.id=sys_role.p_id and sys_role_type.valid=1  where sys_role.valid=1 order by sys_role_type.sortorder,sys_role.sortorder";
    }

    /**
     * @return
     */

    @Override
    protected String getPortalRightSettingSQL() {
        return "select id,portal_id,role_id,readable,writeable from sys_portal_right t";
    }

    /**
     * @return
     */

    @Override
    protected String getPortalItemListSQL() {
        return "select id,type,published,name,icon,property property from sys_portal_item where p_id=? order by sortorder";
    }

    /**
     * @return
     */

    @Override
    protected String getPortalGroupListSQL() {
        return "select id,name,published,icon from sys_portal_group order by sortorder";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateSQL() {
        return "update sys_portal_group set name=:name,memo=:memo,published=:published,icon=:icon where id=:id";
    }

    private DBPortalGroupService4Oracle() {
        super();
    }

    private static final DBPortalGroupService4Oracle INSTANCE = new DBPortalGroupService4Oracle();

    public static DBPortalGroupService4Oracle getInstance() {
        return INSTANCE;
    }

}
