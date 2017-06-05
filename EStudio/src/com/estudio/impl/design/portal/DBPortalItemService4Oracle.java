package com.estudio.impl.design.portal;

public final class DBPortalItemService4Oracle extends DBPortalItemService {

    /**
     * @return
     */
    @Override
    protected String getMoveSQL() {
        return "update sys_portal_item set p_id=:p_id where id = :id";
    }

    /**
     * @return
     */
    @Override
    protected String getExchangeSQL() {
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_portal_item where id = :id1; select sortorder into idx_2 from sys_portal_item where id = :id2; update sys_portal_item set sortorder = idx_2 where id = :id1;  update sys_portal_item set sortorder = idx_1 where id = :id2; end;";
    }

    /**
     * @return
     */
    @Override
    protected String getListSQL() {
        return "select id,p_id,name,sortorder,type,property,published,createdate,version,icon from sys_portal_item where p_id=:pid";
    }

    /**
     * @return
     */
    @Override
    protected String getDeleteSQL() {
        return "delete from sys_portal_item where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return "insert into sys_portal_item(id,p_id,name,sortorder,type,property,published,version,icon,win,autorun,disableclose,ishidden) values (:id,:p_id,:name,:id,:type,:property,:published,:version,:icon,:win,:autorun,:disableclose,:ishidden)";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateSQL() {
        return "update sys_portal_item set name=:name,type=:type,property=:property,published=:published,version=version+1,icon=:icon,win=:win,autorun=:autorun,disableclose=:disableclose,ishidden=:ishidden where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getSelectSQL() {
        return "select id,p_id,name,sortorder,type,property,published,createdate,version,icon,win,autorun,disableclose,ishidden from sys_portal_item where id=:id";
    }

    private static DBPortalItemService4Oracle instance = new DBPortalItemService4Oracle();

    public static DBPortalItemService4Oracle getInstance() {
        return instance;
    }

    private DBPortalItemService4Oracle() {
        super();
    }

}
