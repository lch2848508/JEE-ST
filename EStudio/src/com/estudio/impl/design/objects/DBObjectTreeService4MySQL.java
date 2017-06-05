package com.estudio.impl.design.objects;

import java.sql.Connection;

public final class DBObjectTreeService4MySQL extends DBObjectTreeService {
    private static final DBObjectTreeService4MySQL INSTANCE = new DBObjectTreeService4MySQL();

    public static DBObjectTreeService4MySQL getInstance() {
        return INSTANCE;
    }

    private DBObjectTreeService4MySQL() {
        super();
    }

    /**
     * @return
     */
    @Override
    protected String getDeleteSQL() {
        return "{call proc_delete_object(:id)}";
    }

    /**
     * @return
     */
    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('sys_object_tree','id','sortorder',:id1,:id2)}";
    }

    /**
     * @return
     */
    @Override
    protected String getFormTreeSQL(final Connection con) {
        return "{call proc_design_query_form()}";//
    }

    /**
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return "insert into sys_object_tree(memo,id,caption,type,version,sortorder,pid,lockby,prop_id) values (:memo,:id,:caption,:type,:version,:sortorder,:pid,:lockby,:prop_id)";
    }

    /**
     * @return
     */
    @Override
    protected String getListSQL() {
        return "select memo,id,caption,type,version,sortorder,pid,lockby,prop_id from sys_object_tree where pid=:pid";
    }

    /**
     * @return
     */
    @Override
    protected String getLockObjectSQL() {
        return "update sys_object_tree set lockby=? where lockby is null and id =?";
    }

    /**
     * @return
     */
    @Override
    protected String getMoveSQL() {
        return "update sys_object_tree set p_id=:p_id where id = :id";
    }

    /**
     * @return
     */
    @Override
    protected String getObjectTreeByUserIdSQL() {
        return "select sys_object_tree.id,caption,type,version,case ifnull(lockby,-2) when ? then 1 when -2 then 0 else 2 end as vss,prop_id,sys_userinfo.realname from sys_object_tree left join sys_userinfo on sys_object_tree.lockby=sys_userinfo.id where pid=? order by sys_object_tree.type, sys_object_tree.sortorder";
    }

    /**
     * @return
     */
    @Override
    protected String getObjectVersionAndLockStatusByUserIdSQL() {
        return "select version,case ifnull(lockby,-2) when ? then 1 when -2 then 0 else 2 end as vss from sys_object_tree left join sys_userinfo on sys_object_tree.lockby = sys_userinfo.id where sys_object_tree.id=?";
    }

    /**
     * @return
     */
    @Override
    protected String getReportTreeSQL(final Connection con) {
        return "call proc_design_query_report()";//
    }

    @Override
    protected String getQueryTreeSQL(final Connection con) {
        return "CALL proc_design_query_query()";
    }

    /**
     * @return
     */
    @Override
    protected String getSelectSQL() {
        return "select memo,id,caption,type,version,sortorder,pid,lockby,prop_id from sys_object_tree where id=:id";
    }

    /**
     * @return
     */
    @Override
    protected String getUnLockObjectSQL() {
        return "update sys_object_tree set lockby=null where id =?";
    }

    /**
     * @return
     */
    @Override
    protected String getUpdateSQL() {
        return "update sys_object_tree set memo=:memo,caption=:caption,version=version+1 where id=:id";
    }

}
