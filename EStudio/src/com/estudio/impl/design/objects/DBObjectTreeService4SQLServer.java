package com.estudio.impl.design.objects;

import java.sql.Connection;

public final class DBObjectTreeService4SQLServer extends DBObjectTreeService {
    private static final DBObjectTreeService4SQLServer INSTANCE = new DBObjectTreeService4SQLServer();

    public static DBObjectTreeService4SQLServer getInstance() {
        return INSTANCE;
    }

    private DBObjectTreeService4SQLServer() {
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
        return "with tree as (\n" + //
                "select * from SYS_OBJECT_TREE where ID in (select ID from SYS_OBJECT_FORMS)\n" + //
                "UNION ALL\n" + //
                "SELECT SYS_OBJECT_TREE.* FROM SYS_OBJECT_TREE, TREE\n" + //
                "WHERE SYS_OBJECT_TREE.id = TREE.PID)\n" + //
                "select distinct tree.id, tree.pid, tree.caption, tree.type, SYS_OBJECT_FORMS.form_params,isnull(tree.version,0) version,tree.sortorder\n" + //
                "from tree left join SYS_OBJECT_FORMS on tree.id=SYS_OBJECT_FORMS.id order by type, id, sortorder desc";//
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
        return "select sys_object_tree.id,caption,type,version,case isnull(lockby,-2) when ? then 1 when -2 then 0 else 2 end as vss,prop_id,sys_userinfo.realname from sys_object_tree left join sys_userinfo on sys_object_tree.lockby=sys_userinfo.id where pid=? order by sys_object_tree.type, sys_object_tree.sortorder";
    }

    /**
     * @return
     */
    @Override
    protected String getObjectVersionAndLockStatusByUserIdSQL() {
        return "select version,case isnull(lockby,-2) when ? then 1 when -2 then 0 else 2 end as vss from sys_object_tree left join sys_userinfo on sys_object_tree.lockby = sys_userinfo.id where sys_object_tree.id=?";
    }

    /**
     * @return
     */
    @Override
    protected String getReportTreeSQL(final Connection con) {
        return "with tree as (\n" + //
                "select * from SYS_OBJECT_TREE where ID in (select ID from SYS_OBJECT_REPORT)\n" + //
                "UNION ALL\n" + //
                "SELECT SYS_OBJECT_TREE.* FROM SYS_OBJECT_TREE, TREE\n" + //
                "WHERE SYS_OBJECT_TREE.id = TREE.PID)\n" + //
                "select distinct tree.id, tree.pid, tree.caption, tree.type, SYS_OBJECT_REPORT.report_params,isnull(tree.version,0) version,tree.sortorder\n" + //
                "from tree left join SYS_OBJECT_REPORT on tree.id=SYS_OBJECT_REPORT.id order by type, id, sortorder desc";//
    }

    @Override
    protected String getQueryTreeSQL(final Connection con) {
        return "with tree as (\n" + //
                "select * from SYS_OBJECT_TREE where ID in (select ID from sys_object_query)\n" + //
                "UNION ALL\n" + //
                "SELECT SYS_OBJECT_TREE.* FROM SYS_OBJECT_TREE, TREE\n" + //
                "WHERE SYS_OBJECT_TREE.id = TREE.PID)\n" + //
                "select distinct tree.id, tree.pid, tree.caption, tree.type, isnull(tree.version,0) version,tree.sortorder\n" + //
                "from tree left join sys_object_query on tree.id=sys_object_query.id order by type, id, sortorder desc";//
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
