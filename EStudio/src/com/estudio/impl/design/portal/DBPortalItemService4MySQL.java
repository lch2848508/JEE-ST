package com.estudio.impl.design.portal;

public final class DBPortalItemService4MySQL extends DBPortalItemService {

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
        return "{call proc_exchange_record_sortorder('sys_portal_item','id','sortorder',:id1,:id2)}";
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

    private static DBPortalItemService4MySQL instance = new DBPortalItemService4MySQL();

    public static DBPortalItemService4MySQL getInstance() {
        return instance;
    }

    private DBPortalItemService4MySQL() {
        super();
    }

}
