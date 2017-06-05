package com.estudio.impl.design.objects;

import java.sql.Connection;

public final class DBObjectTreeService4Oracle extends DBObjectTreeService {
    private static final DBObjectTreeService4Oracle INSTANCE = new DBObjectTreeService4Oracle();

    public static DBObjectTreeService4Oracle getInstance() {
        return INSTANCE;
    }

    private DBObjectTreeService4Oracle() {
        super();
    }

    /**
     * @return
     */
    @Override
    protected String getDeleteSQL() {
        return "begin proc_delete_object(:id); end;";
    }

    /**
     * @return
     */
    @Override
    protected String getExchangeSQL() {
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_object_tree where id = :id1; select sortorder into idx_2 from sys_object_tree where id = :id2; update sys_object_tree set sortorder = idx_2 where id = :id1;  update sys_object_tree set sortorder = idx_1 where id = :id2; end;";
    }

    /**
     * @return
     */
    @Override
    protected String getFormTreeSQL(final Connection con) {
        return "select a.id, a.pid, a.caption, a.type, b.form_params,nvl(b.version,0) version from (select distinct id, caption, type, sortorder, pid from sys_object_tree start with id in (select id from sys_object_forms) connect by prior pid = id) a, sys_object_forms b where a.id = b.id(+) order by a.type, a.id, a.sortorder desc";
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
        return "select sys_object_tree.id,caption,type,version,decode(lockby,?,1,null,0,2) vss,prop_id,sys_userinfo.realname from sys_userinfo, sys_object_tree where sys_object_tree.lockby=sys_userinfo.id(+) and pid=? order by sys_object_tree.type, sys_object_tree.sortorder";
    }

    /**
     * @return
     */
    @Override
    protected String getObjectVersionAndLockStatusByUserIdSQL() {
        return "select version,decode(lockby,?,1,null,0,2) from sys_userinfo, sys_object_tree where sys_object_tree.lockby = sys_userinfo.id(+) and sys_object_tree.id=?";
    }

    /**
     * @return
     */
    @Override
    protected String getReportTreeSQL(final Connection con) {
        return "select a.id, a.pid, a.caption, a.type, b.report_params from (select distinct id, caption, type, sortorder, pid from sys_object_tree start with id in (select id from sys_object_report) connect by prior pid = id) a, sys_object_report b where a.id = b.id(+) order by a.type, a.id, a.sortorder desc";
    }

    @Override
    protected String getQueryTreeSQL(final Connection con) {
        return "select a.Id, a.Pid, a.Caption, a.Type from (select distinct Id, Caption, type, Sortorder, Pid from Sys_Object_Tree start with Id in (select Id from Sys_Object_Query) connect by prior Pid = Id) a, Sys_Object_Query b where a.Id = b.Id(+) order by a.type, a.Id, a.Sortorder desc";
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
