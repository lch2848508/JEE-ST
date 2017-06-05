package com.estudio.impl.design.objects;

import com.estudio.intf.design.objects.IObjectWorkFlowService;

public final class DBWorkFlowDesignService4MySQL extends DBWorkFlowDesignService {

    @Override
    protected String getSQL4ExchangeWorkFlowUIInfo() {
        return "{call proc_exchange_record_sortorder('sys_workflow_d_ui_define','id','sortorder',:id1,:id2)}";
    }

    @Override
    protected String getInsertVarsSQL() {
        return "insert into SYS_WORKFLOW_D_VARIABLES (id, pid, name, datatype, paramcomment, defvalue) values (:id, :pid, :name, :datatype, :paramcomment, :defvalue)";
    }

    @Override
    protected String getInsertLinkSQL() {
        return "insert into sys_workflow_d_link (id, pid, name, descript, color, width, style, link_condition, namepos, source_id, target_id, source_pos, target_pos, points,CAPTION) values (:id, :pid, :name, :descript, :color, :width, :style, :condition, :namepos, :source_id, :target_id, :source_pos, :target_pos, :points,:caption)";
    }

    @Override
    protected String getInsertActionSQL() {
        return "insert into sys_workflow_d_action (id, pid, name, descript, limit_num,limit_unit, taskscript, issplit, isjoin, x, y, width, height, background, fontcolor, type, caption,variables,receive_event,send_event,user_filter,EXT01,EXT02,EXT03,EXT04,EXT05,EXT06,EXT07,EXT08,EXT09,EXT10) values (:id, :pid, :name, :descript, :limit_num,:limit_unit, :taskscript, :issplit, :isjoin, :x, :y, :width, :height, :background, :fontcolor, :type, :caption,:variables,:receive_event,:send_event,:user_filter,:EXT01,:EXT02,:EXT03,:EXT04,:EXT05,:EXT06,:EXT07,:EXT08,:EXT09,:EXT10)";
    }

    @Override
    protected String getInsertFormSQL() {
        return "insert into sys_workflow_d_forms (id, pid, formid, controls) values (:id, :pid, :formid, :controls)";
    }

    @Override
    protected String getInsertRoleCMD() {
        return "insert into sys_workflow_d_roles (id, pid, roleid) values (:id, :pid, :roleid)";
    }

    @Override
    protected String getDeleteWFPropertySQL() {
        return "{call proc_del_wf_a_l_property(:id)}";
    }

    @Override
    protected String getInsertWFPropertySQL() {
        return "{call proc_design_insert_wf_property(:id, :name, :descript, :status, :version, :limit_num,:limit_unit)}";
    }

    @Override
    protected String getVersionSQL() {
        return "select version from SYS_WORKFLOW_D_PROCESS where id=:id";
    }

    @Override
    protected String getMoveSQL() {
        return "update SYS_WORKFLOW_D_PROCESS set p_id=:p_id where id = :id";
    }

    @Override
    protected String getExchangeSQL() {
        return "{call proc_exchange_record_sortorder('SYS_WORKFLOW_D_PROCESS','id','sortorder',:id1,:id2)}";

    }

    @Override
    protected String getListSQL() {
        return "select id,version,status,descript,dfm,property from SYS_WORKFLOW_D_PROCESS";

    }

    @Override
    protected String getDeleteSQL() {
        return "delete from SYS_WORKFLOW_D_PROCESS where id=:id";
    }

    @Override
    protected String getInsertSQL() {
        return "insert into SYS_WORKFLOW_D_PROCESS(id,version,status,descript,dfm,property) values (:id,1,:status,:descript,:dfm,:property)";
    }

    @Override
    protected String getUpdateSQL() {
        return "update SYS_WORKFLOW_D_PROCESS set id=:id,version=version+1,status=:status,descript=:descript,dfm=:dfm,property=:property where id=:id";
    }

    @Override
    protected String getSelectSQL() {
        return "select id,version,status,descript,dfm,property from SYS_WORKFLOW_D_PROCESS where id=:id";

    }

    /**
     * ¹¹Ôìº¯Êý
     */
    private DBWorkFlowDesignService4MySQL() {
        super();
        initDBCommand();
    }

    private static IObjectWorkFlowService instance = new DBWorkFlowDesignService4MySQL();

    public static IObjectWorkFlowService getInstance() {
        return instance;
    }

}
