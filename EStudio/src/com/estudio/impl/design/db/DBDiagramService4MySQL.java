package com.estudio.impl.design.db;

import com.estudio.intf.design.db.IDBDiagramService;

public final class DBDiagramService4MySQL extends DBDiagramService {

    @Override
    protected String getSelectSQL() {
        return "select id,name,descript from sys_db_diagram where id=:id";
    }

    @Override
    protected String getUpdateSQL() {
        return "update sys_db_diagram set name=:name,descript=:descript where id=:id";
    }

    @Override
    protected String getInsertSQL() {
        return "insert into sys_db_diagram(id,name,descript,sortorder) values (:id,:name,:descript,:id)";
    }

    @Override
    protected String getDeleteSQL() {
        return "delete from sys_db_diagram where id=:id";
    }

    @Override
    protected String getListSQL() {
        return "select id,name,descript,sortorder from sys_db_diagram where id<>-1 order by sortorder";
    }

    @Override
    protected String getMovetoSQL() {
        return "update sys_db_diagram set p_id=:p_id where id = :id";
    }

    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('sys_db_diagram','id','sortorder',:id1,:id2)}";
    }

    @Override
    protected String getInsertDiagramItemSQL() {
        return "insert into sys_db_diagram_item (id, name, x, y, width, height,color, p_id) values (?, ?, ?, ?, ?, ?, ?,?)";
    }

    @Override
    protected String getDeleteDiagramItemSQL() {
        return "delete from sys_db_diagram_item where p_id=?";
    }

    @Override
    protected String getDiagramItemPositionSQL() {
        return "select a.name,a.X ,a.Y,a.width,a.height,a.color from view_sys_user_tables b,sys_db_diagram_item a where UPPER( a.name)=upper(b.table_name) and a.p_id=?";
    }

    @Override
    protected String getIncDiagramVersionSQL() {
        return "update sys_db_diagram set version=version+1 where id=?";
    }

    @Override
    protected String getDiagramVersionSQL() {
        return "select version from sys_db_diagram where id=?";
    }

    @Override
    protected String getDBEntrysVersionSQL() {
        return "select a.name,fun_design_get_version(b.table_name) version from view_sys_user_tables b,sys_db_diagram_item a where upper(a.name)=upper(b.table_name) and a.p_id=?";
    }

    private DBDiagramService4MySQL() {
        super();
    }

    private static final IDBDiagramService INSTANCE = new DBDiagramService4MySQL();

    public static IDBDiagramService getInstance() {
        return INSTANCE;
    }

}
