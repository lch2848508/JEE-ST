package com.estudio.impl.design.objects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.WorkFlowDesignInfo;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectWorkFlowService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.workflow.web.WorkFlowUIDefineService;

public abstract class DBWorkFlowDesignService implements IObjectWorkFlowService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private IDBCommand selectCMD = null;

    protected abstract String getSelectSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getListSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getMoveSQL();

    protected abstract String getVersionSQL();

    protected abstract String getInsertWFPropertySQL();

    protected abstract String getDeleteWFPropertySQL();

    protected abstract String getInsertRoleCMD();

    protected abstract String getInsertFormSQL();

    protected abstract String getInsertActionSQL();

    protected abstract String getInsertLinkSQL();

    protected abstract String getInsertVarsSQL();

    protected abstract String getSQL4ExchangeWorkFlowUIInfo();

    private String getSQL4UpdateWorkFlowUIInfo() {
        return "update sys_workflow_d_ui_define set name=:name,ui_define=:ui_define where id=:id";
    }

    private String getSQL4InsertWorkFlowUIInfo() {
        return "insert into sys_workflow_d_ui_define(id,name,ui_define,sortorder) values (:id,:name,:ui_define,:id)";
    }

    private String getSQL4CountWorkFlowUIInfo() {
        return "select count(*) from sys_workflow_d_ui_define where id=:id";
    }

    private IDBCommand updateCMD = null;
    private IDBCommand insertCMD = null;
    private IDBCommand deleteCMD = null;
    private IDBCommand listCMD = null;
    private IDBCommand exchangeCMD = null;
    private IDBCommand movetoCMD = null;
    private IDBCommand versionCMD = null;
    private IDBCommand insertWorkFlowPropertyCMD = null;
    private IDBCommand deleteWorkFlowActionAndLinkPropertyCMD = null;
    private IDBCommand insertRoleCMD = null;
    private IDBCommand insertFormCMD = null;
    private IDBCommand insertActionCMD = null;
    private IDBCommand insertLinkCMD = null;
    private IDBCommand insertVarsCMD = null;

    protected void initDBCommand() {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMoveSQL());
            versionCMD = DBHELPER.getCommand(null, getVersionSQL());
            insertWorkFlowPropertyCMD = DBHELPER.getCommand(null, getInsertWFPropertySQL());
            deleteWorkFlowActionAndLinkPropertyCMD = DBHELPER.getCommand(null, getDeleteWFPropertySQL());
            insertRoleCMD = DBHELPER.getCommand(null, getInsertRoleCMD());
            insertFormCMD = DBHELPER.getCommand(null, getInsertFormSQL());
            insertActionCMD = DBHELPER.getCommand(null, getInsertActionSQL());
            insertLinkCMD = DBHELPER.getCommand(null, getInsertLinkSQL());
            insertVarsCMD = DBHELPER.getCommand(null, getInsertVarsSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#getVersion
     * (java.sql.Connection, long)
     */
    @Override
    public long getVersion(final Connection con, final long id) throws Exception {
        long result = -1;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = versionCMD.clone(tempCon);
            cmd.setParam("id", id);
            cmd.executeQuery();
            if (cmd.next())
                result = cmd.getLong(1);

        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#moveTo(java
     * .sql.Connection, long, long)
     */
    @Override
    public boolean moveTo(final Connection con, final long id, final long p_id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = movetoCMD.clone(tempCon);
            cmd.setParam("id", id);
            cmd.setParam("p_id", p_id);
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#exchange(java
     * .sql.Connection, long, long)
     */
    @Override
    public boolean exchange(final Connection con, final long id_1, final long id_2) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = exchangeCMD.clone(tempCon);
            cmd.setParam("id1", id_1);
            cmd.setParam("id2", id_2);
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#saveRecord
     * (java.sql.Connection,
     * com.estudio.service.portal.workflow.WorkFlowDesignInfo)
     */
    @Override
    public boolean saveRecord(final Connection con, final WorkFlowDesignInfo record) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            if (record.isNew())
                cmd = insertCMD.clone(tempCon);
            else cmd = updateCMD.clone(tempCon);
            cmd.setParam("id", record.getId());
            cmd.setParam("version", record.getVersion());
            cmd.setParam("status", record.getStatus());
            cmd.setParam("descript", record.getDescript());
            cmd.setParam("dfm", record.getDfm());
            cmd.setParam("property", record.getProperty());
            record.setOld();
            cmd.execute();
            record.setVersion(getVersion(tempCon, record.getId()));

            saveWorkFlowProperty(con, record, JSONUtils.parserJSONObject(Convert.bytes2Str(record.getProperty())));

            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;

    }

    /**
     * 保存工作流属性信息
     * 
     * @param con
     * @param record
     * @param workflowJson
     * @throws Exception
     */
    private void saveWorkFlowProperty(final Connection con, final WorkFlowDesignInfo record, final JSONObject workflowJson) throws Exception {
        IDBCommand wfCMD = null;
        IDBCommand delPropCMD = null;
        IDBCommand insRoleCMD = null;
        IDBCommand insFormCMD = null;
        IDBCommand insActionCMD = null;
        IDBCommand insLinkCMD = null;
        IDBCommand insVarsCMD = null;

        try {
            // 保存工作流总体属性
            wfCMD = insertWorkFlowPropertyCMD.clone(con);
            wfCMD.setParam("id", record.getId());
            wfCMD.setParam("name", RuntimeContext.getObjectTreeService().getRecord(con, record.getId()).getCaption());
            final JSONObject propJson = workflowJson.getJSONObject("Property");
            wfCMD.setParam("descript", propJson.getString("descript"));
            wfCMD.setParam("status", propJson.getString("status"));
            wfCMD.setParam("limit_num", propJson.getString("days"));
            wfCMD.setParam("limit_unit", propJson.getString("timeunit"));
            wfCMD.setParam("version", record.getVersion());
            wfCMD.execute();

            // 删错所有已经保存的属性
            delPropCMD = deleteWorkFlowActionAndLinkPropertyCMD.clone(con);
            delPropCMD.setParam("id", record.getId());
            delPropCMD.execute();

            // 保存工作流对象到Action中,用以建立完整的外键关系
            insActionCMD = insertActionCMD.clone(con);
            insActionCMD.setParam("id", record.getId());
            insActionCMD.setParam("pid", record.getId());
            insActionCMD.setNullParam("name");
            insActionCMD.setNullParam("descript");
            insActionCMD.setNullParam("limit_num");
            insActionCMD.setNullParam("limit_unit");
            insActionCMD.setNullParam("taskscript");
            insActionCMD.setNullParam("issplit");
            insActionCMD.setNullParam("isjoin");
            insActionCMD.setNullParam("variables");
            insActionCMD.setNullParam("x");
            insActionCMD.setNullParam("y");
            insActionCMD.setNullParam("width");
            insActionCMD.setNullParam("height");
            insActionCMD.setNullParam("background");
            insActionCMD.setNullParam("fontcolor");
            insActionCMD.setNullParam("type");
            insActionCMD.setNullParam("caption");
            insActionCMD.setNullParam("user_filter");
            insActionCMD.setNullParam("receive_event");
            insActionCMD.setNullParam("send_event");
            insActionCMD.setParam("ext01", propJson.getString("risk_level"));
            insActionCMD.setParam("ext02", propJson.getString("risk_content"));
            insActionCMD.setParam("ext03", propJson.getString("risk_method"));
            insActionCMD.setParam("ext04", propJson.getString("EXT04"));
            insActionCMD.setParam("ext05", propJson.getString("EXT05"));
            insActionCMD.setParam("ext06", propJson.getString("EXT06"));
            insActionCMD.setParam("ext07", propJson.getString("EXT07"));
            insActionCMD.setParam("ext08", propJson.getString("EXT08"));
            insActionCMD.setParam("ext09", propJson.getString("EXT09"));
            insActionCMD.setParam("ext10", propJson.getString("EXT10"));
            insActionCMD.execute();

            // 保存工作流表单及角色属性
            insRoleCMD = insertRoleCMD.clone(con);
            insFormCMD = insertFormCMD.clone(con);
            saveFormOrActionFormsAndRoles(workflowJson, insRoleCMD, insFormCMD, record.getId());

            // 保存工作流变量
            insVarsCMD = insertVarsCMD.clone(con);
            saveWorkFlowOrActionVariables(record.getId(), workflowJson, insVarsCMD);

            final JSONArray actionsArray = workflowJson.getJSONArray("Actions");
            for (int i = 0; i < actionsArray.size(); i++) {
                final JSONObject actionJson = actionsArray.getJSONObject(i);
                final JSONObject propertyJson = actionJson.getJSONObject("Property");
                final long actionId = DBHELPER.getUniqueID(con);
                insActionCMD.setParam("id", actionId);
                insActionCMD.setParam("pid", record.getId());
                insActionCMD.setParam("name", actionJson.getString("Name"));
                insActionCMD.setParam("descript", propertyJson.getString("descript"));
                insActionCMD.setParam("limit_num", propertyJson.getString("days"));
                insActionCMD.setParam("limit_unit", propertyJson.getString("timeunit"));
                insActionCMD.setParam("taskscript", Convert.str2Bytes(propertyJson.getString("task")));
                insActionCMD.setParam("issplit", propertyJson.getBoolean("join") ? 1 : 0);
                insActionCMD.setParam("isjoin", propertyJson.getBoolean("split") ? 1 : 0);
                insActionCMD.setParam("variables", Convert.str2Bytes(propertyJson.getString("vars")));
                insActionCMD.setParam("x", actionJson.getString("X"));
                insActionCMD.setParam("y", actionJson.getString("Y"));
                insActionCMD.setParam("width", actionJson.getString("Width"));
                insActionCMD.setParam("height", actionJson.getString("height"));
                insActionCMD.setParam("background", actionJson.getString("Background"));
                insActionCMD.setParam("fontcolor", actionJson.getString("FontColor"));
                insActionCMD.setParam("type", actionJson.getString("Type"));
                insActionCMD.setParam("caption", actionJson.getString("Caption"));
                insActionCMD.setParam("user_filter", Convert.str2Bytes(propertyJson.getString("userFilter")));
                insActionCMD.setParam("receive_event", Convert.str2Bytes(propertyJson.getString("receiveEvent")));
                insActionCMD.setParam("send_event", Convert.str2Bytes(propertyJson.getString("sendEvent")));
                insActionCMD.setParam("ext01", propertyJson.getString("risk_level"));
                insActionCMD.setParam("ext02", propertyJson.getString("risk_content"));
                insActionCMD.setParam("ext03", propertyJson.getString("risk_method"));
                insActionCMD.setParam("ext04", propertyJson.getString("EXT04"));
                insActionCMD.setParam("ext05", propertyJson.getString("EXT05"));
                insActionCMD.setParam("ext06", propertyJson.getString("EXT06"));
                insActionCMD.setParam("ext07", propertyJson.getString("EXT07"));
                insActionCMD.setParam("ext08", propertyJson.getString("EXT08"));
                insActionCMD.setParam("ext09", propertyJson.getString("EXT09"));
                insActionCMD.setParam("ext10", propertyJson.getString("EXT10"));
                insActionCMD.execute();
                saveFormOrActionFormsAndRoles(actionJson, insRoleCMD, insFormCMD, actionId);
                saveWorkFlowOrActionVariables(actionId, actionJson, insVarsCMD);
            }

            insLinkCMD = insertLinkCMD.clone(con);
            final JSONArray linksArray = workflowJson.getJSONArray("Links");
            for (int i = 0; i < linksArray.size(); i++) {
                final JSONObject linkJson = linksArray.getJSONObject(i);
                final JSONObject linkPropJson = linkJson.getJSONObject("Property");

                insLinkCMD.setParam("id", DBHELPER.getUniqueID(con));
                insLinkCMD.setParam("pid", record.getId());
                insLinkCMD.setParam("name", linkJson.getString("Name"));

                insLinkCMD.setParam("descript", linkPropJson.getString("descript"));
                insLinkCMD.setParam("color", linkJson.getString("Color"));
                insLinkCMD.setParam("width", linkJson.getString("Width"));
                insLinkCMD.setParam("style", linkJson.getString("Type"));
                insLinkCMD.setParam("condition", Convert.str2Bytes(linkPropJson.getString("condition")));
                insLinkCMD.setParam("namepos", linkJson.getString("CaptionPosition"));
                insLinkCMD.setParam("source_id", linkJson.getString("Source"));
                insLinkCMD.setParam("target_id", linkJson.getString("Target"));
                insLinkCMD.setParam("source_pos", linkJson.getString("SourceHandle"));
                insLinkCMD.setParam("target_pos", linkJson.getString("Targethandle"));
                insLinkCMD.setParam("points", linkJson.getString("Points"));
                insLinkCMD.setParam("caption", linkJson.getString("Caption"));

                insLinkCMD.execute();
            }

            // 通知消息
            RuntimeContext.getWfStorage().notifyWFProcessChange(record.getId(), false, con);
            NotifyService4Cluster.getInstance().notifyClusterMessage(1, record.getId(), 0, con);

        } finally {
            DBHELPER.closeCommand(insLinkCMD);
            DBHELPER.closeCommand(wfCMD);
            DBHELPER.closeCommand(delPropCMD);
            DBHELPER.closeCommand(insRoleCMD);
            DBHELPER.closeCommand(insFormCMD);
            DBHELPER.closeCommand(insActionCMD);
            DBHELPER.closeCommand(insVarsCMD);
        }
    }

    /**
     * 保存变量
     * 
     * @param pid
     * @param json
     * @param insVarsCMD
     * @throws JSONException
     * @throws SQLException
     * @throws DBException
     */
    private void saveWorkFlowOrActionVariables(final long pid, final JSONObject json, final IDBCommand insVarsCMD) throws Exception {
        final String varsStr = json.getJSONObject("Property").getString("vars");
        if (!StringUtils.isEmpty(varsStr)) {
            final JSONArray FArray = JSONUtils.parserJSONArray(varsStr);
            for (int i = 0; i < FArray.size(); i++) {
                final JSONObject varJson = FArray.getJSONObject(i);
                insVarsCMD.setParam("id", DBHELPER.getUniqueID(insVarsCMD.getConnection()));
                insVarsCMD.setParam("pid", pid);
                insVarsCMD.setParam("name", varJson.getString("Name"));
                insVarsCMD.setParam("datatype", varJson.getString("Type"));
                insVarsCMD.setParam("paramcomment", varJson.getString("Comment"));
                insVarsCMD.setParam("defvalue", varJson.getString("Value"));
                insVarsCMD.execute();
            }
        }
    }

    /**
     * 保存活动体或工作流的表单权限及角色权限
     * 
     * @param json
     * @param insRoleCmd
     * @param insFormCmd
     * @param formOrActionID
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    private void saveFormOrActionFormsAndRoles(final JSONObject json, final IDBCommand insRoleCmd, final IDBCommand insFormCmd, final long formOrActionID) throws Exception {
        final JSONArray rolesArray = json.getJSONArray("Roles");
        for (int i = 0; i < rolesArray.size(); i++) {
            final String roleId = rolesArray.getString(i);
            insRoleCmd.setParam("id", DBHELPER.getUniqueID(insRoleCmd.getConnection()));
            insRoleCmd.setParam("pid", formOrActionID);
            insRoleCmd.setParam("roleid", roleId);
            insRoleCmd.execute();
        }

        final JSONArray formsArray = json.getJSONArray("Forms");
        for (int i = 0; i < formsArray.size(); i++) {
            final JSONObject formJSON = formsArray.getJSONObject(i);
            final String formID = formJSON.getString("ID");
            final String formProp = formJSON.getJSONObject("Controls").toString();
            insFormCmd.setParam("id", DBHELPER.getUniqueID(insRoleCmd.getConnection()));
            insFormCmd.setParam("pid", formOrActionID);
            insFormCmd.setParam("formid", formID);
            insFormCmd.setParam("controls", Convert.str2Bytes(formProp));
            insFormCmd.execute();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#deleteRecord
     * (java.sql.Connection, long)
     */
    @Override
    public boolean deleteRecord(final Connection con, final long id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = deleteCMD.clone(tempCon);
            cmd.setParam(1, id);
            result = cmd.execute();

            RuntimeContext.getWfStorage().notifyWFProcessChange(id, true, con);
            NotifyService4Cluster.getInstance().notifyClusterMessage(1, id, 1, con);

        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#fillRecord
     * (com.estudio.intf.db.IDBCommand,
     * com.estudio.service.portal.workflow.WorkFlowDesignInfo)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final WorkFlowDesignInfo record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setVersion(cmd.getLong("VERSION"));
        record.setStatus(cmd.getString("STATUS"));
        record.setDescript(cmd.getString("DESCRIPT"));
        record.setDfm(cmd.getBytes("DFM"));
        record.setProperty(cmd.getBytes("PROPERTY"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#getRecord(
     * java.sql.Connection, long)
     */
    @Override
    public WorkFlowDesignInfo getRecord(final Connection con, final long id) throws Exception {
        WorkFlowDesignInfo record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new WorkFlowDesignInfo();
                fillRecord(cmd, record);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#getRecords
     * (java.sql.Connection, long)
     */
    @Override
    public ArrayList<WorkFlowDesignInfo> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<WorkFlowDesignInfo> records = new ArrayList<WorkFlowDesignInfo>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final WorkFlowDesignInfo record = new WorkFlowDesignInfo();
                    fillRecord(cmd, record);
                    records.add(record);
                }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return records;
    }

    @Override
    public JSONObject copyWorkFlowDesignInfo(final long fromId, final String newName) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, "select ui_define from sys_workflow_d_ui_define where id=:id");
            cmd.setParam("id", fromId);
            cmd.executeQuery();
            if (cmd.next()) {
                final String content = Convert.bytes2Str(cmd.getBytes(1));
                final long id = DBHELPER.getUniqueID(con);
                cmd.setSQL(getSQL4InsertWorkFlowUIInfo());
                cmd.setParam("id", id);
                cmd.setParam("name", newName);
                cmd.setParam("ui_define", Convert.str2Bytes(content));
                cmd.execute();
                json.put("id", id);
                json.put("name", newName);
                json.put("content", content);
                json.put("r", true);
            } else json.put("msg", "源工作流已经被删除!");

        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject saveWorkFlowItemDesignInfo(final long itemId, final String name, final String content) throws Exception {
        final JSONObject json = new JSONObject();
        long id = itemId;
        json.put("r", false);
        Connection tempCon = null;
        IDBCommand cmd = null;
        try {
            tempCon = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(tempCon, getSQL4CountWorkFlowUIInfo());
            cmd.setParam("id", id);
            cmd.executeQuery();
            cmd.next();
            String sql = "";
            if (cmd.getLong(1) == 0) {
                id = DBHELPER.getUniqueID(tempCon);
                sql = getSQL4InsertWorkFlowUIInfo();
            } else sql = getSQL4UpdateWorkFlowUIInfo();
            cmd.setSQL(sql);
            cmd.setParam("id", id);
            cmd.setParam("name", name);
            cmd.setParam("ui_define", Convert.str2Bytes(content));
            cmd.execute();
            json.put("id", id);
            json.put("name", name);
            json.put("content", content);
            json.put("r", true);

            WorkFlowUIDefineService.getInstance().notifyDesignInfoChange(id);
            NotifyService4Cluster.getInstance().notifyClusterMessage(6, id, 0, tempCon);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    @Override
    public JSONObject listWorkFlowItemDesignInfos() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, "select id,name from sys_workflow_d_ui_define where valid=1 order by sortorder");
            cmd.executeQuery();
            while (cmd.next()) {
                final JSONObject itemJson = new JSONObject();
                itemJson.put("id", cmd.getLong(1));
                itemJson.put("name", cmd.getString(2));
                itemJson.put("content", "");
                JSONUtils.append(json, "items", itemJson);
                // json.append("items", itemJson);
            }
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject deleteWorkFlowDesignInfos(final long id) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, "update sys_workflow_d_ui_define set valid=0 where id=:id");
            cmd.setParam("id", id);
            cmd.execute();
            json.put("r", true);

            WorkFlowUIDefineService.getInstance().notifyDesignInfoChange(id);
            NotifyService4Cluster.getInstance().notifyClusterMessage(6, id, 0, con);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject exchangeWorkFlowDesignInfo(final long id1, final long id2) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(con, getSQL4ExchangeWorkFlowUIInfo());
            cmd.setParam("id1", id1);
            cmd.setParam("id2", id2);
            cmd.execute();
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject getWorkFlowDesignInfo(final long id, final Connection con) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(tempCon, "select id,name,ui_define from sys_workflow_d_ui_define where id=:id order by sortorder");
            cmd.setParam("id", id);
            cmd.executeQuery();
            if (cmd.next()) {
                json.put("id", cmd.getLong(1));
                json.put("name", cmd.getString(2));
                json.put("content", Convert.bytes2Str(cmd.getBytes(3)));
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.workflow.IWorkFlowDesignService#newRecord()
     */
    @Override
    public WorkFlowDesignInfo newRecord() {
        return new WorkFlowDesignInfo();
    }

    protected DBWorkFlowDesignService() {
        super();
    }

}
