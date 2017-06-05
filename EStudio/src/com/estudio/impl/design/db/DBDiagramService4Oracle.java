package com.estudio.impl.design.db;

import com.estudio.intf.design.db.IDBDiagramService;

public final class DBDiagramService4Oracle extends DBDiagramService {

    private DBDiagramService4Oracle() {
        super();
    }

    private static final IDBDiagramService INSTANCE = new DBDiagramService4Oracle();

    public static IDBDiagramService getInstance() {
        return INSTANCE;
    }

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
        return "declare idx_1 integer; idx_2 integer; begin select sortorder into idx_1 from sys_db_diagram where id = :id1; select sortorder into idx_2 from sys_db_diagram where id = :id2; update sys_db_diagram set sortorder = idx_2 where id = :id1;  update sys_db_diagram set sortorder = idx_1 where id = :id2; end;";
    }

    @Override
    protected String getInsertDiagramItemSQL() {
        return "insert into sys_db_diagram_item (id, name, left, top, width, height,color, p_id) values (?, ?, ?, ?, ?, ?, ?,?)";
    }

    @Override
    protected String getDeleteDiagramItemSQL() {
        return "delete sys_db_diagram_item where p_id=?";
    }

    @Override
    protected String getDiagramItemPositionSQL() {
        return "select a.name,a.left,a.top,a.width,a.height,a.color from user_objects b,sys_db_diagram_item a where OBJECT_TYPE='TABLE' and a.name=b.OBJECT_NAME  and a.p_id=?";
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
        return "select a.name,to_number(to_char(b.LAST_DDL_TIME,'YYYYMMDDHH24MISS')) from user_objects b,sys_db_diagram_item a where OBJECT_TYPE='TABLE' and a.name=b.OBJECT_NAME  and a.p_id=?";
    }

}
