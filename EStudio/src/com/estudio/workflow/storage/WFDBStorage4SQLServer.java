package com.estudio.workflow.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.define.db.DBException;
import com.estudio.impl.db.DBHelper4SQLServer;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.JSONUtils;
import com.estudio.workflow.base.WFActivity;
import com.estudio.workflow.base.WFActivityType;

public final class WFDBStorage4SQLServer extends WFDBStorage {

    private static final IDBHelper DBHELPER = DBHelper4SQLServer.getInstance();

    public void test() throws Exception {

    }

    /**
     * 生成业务标识号
     * 
     * @param con
     * @return
     * @throws Exception
     */
    @Override
    protected long generalProcessId(final Connection con) throws Exception {
        long result = 0;
        IDBCommand cmd = null;
        Connection tempCon = null;
        try {
            tempCon = DBHELPER.getConnection();
            cmd = DBHELPER.getCommand(tempCon, getSQL4GeneralProcessId());
            cmd.executeQuery();
            cmd.next();
            result = cmd.getLong(1);
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    // 获取人员目录树
    @Override
    protected JSONObject getActivityUserOrRoleTree(final Connection con, final WFActivity activity, final HashMap<String, Object> scriptContextParams) throws Exception {
        if (activity.getType() == WFActivityType.END)
            return generalEndActivityUserTree(activity);
        if (activity.getRoles().size() == 0)
            return null;
        return activity.isRoleAccept() ? getCommonActivityRoleTree(con, activity, scriptContextParams) : getCommonActivityUserTree(con, activity, scriptContextParams);
    }

    @Override
    protected List<UserHelperClass> getActivityReciverUserList(final Connection con, final WFActivity activity) throws Exception {
        final List<UserHelperClass> result = new ArrayList<UserHelperClass>();
        if (activity.getType() == WFActivityType.END) {
            final UserHelperClass user = new UserHelperClass();
            user.id = WORKFLOW_ENGINEER_USERID;
            user.name = WORKFLOW_ENGINEER_USERNAME;
            result.add(user);
        } else {
            IDBCommand cmd = null;
            try {
                if (activity.isRoleAccept()) {
                    cmd = DBHELPER.getCommand(con, "select name,id from sys_role where id>0 and id in(" + StringUtils.join(activity.getRoles().iterator(), ",") + ") order by sortorder");
                    cmd.executeQuery();
                    while (cmd.next()) {
                        final UserHelperClass role = new UserHelperClass();
                        role.id = cmd.getLong(2);
                        role.name = cmd.getString(1);
                        result.add(role);
                    }
                } else {
                    cmd = DBHELPER.getCommand(con, "select id,realname,duty,p_id,ext1,ext2,ext3 from sys_userinfo where valid=1 and exists (select 'x' from sys_user2role where u_id=sys_userinfo.id and r_id in (" + StringUtils.join(activity.getRoles().toArray(), ",") + ")) order by sortorder");
                    cmd.executeQuery();
                    while (cmd.next()) {
                        final UserHelperClass user = new UserHelperClass();
                        user.id = cmd.getLong("id");
                        user.departmentId = cmd.getLong("p_id");
                        user.duty = cmd.getString("duty");
                        user.name = cmd.getString("realname");
                        user.ext1 = cmd.getString("ext1");
                        user.ext2 = cmd.getString("ext2");
                        user.ext3 = cmd.getString("ext3");
                        result.add(user);
                    }
                }
            } finally {
                DBHELPER.closeCommand(cmd);
            }
        }

        return result;
    }

    /**
     * 获取角色目录树
     * 
     * @param con
     * @param activity
     * @param scriptContextParams
     * @return
     * @throws DBException
     * @throws SQLException
     */
    private JSONObject getCommonActivityRoleTree(final Connection con, final WFActivity activity, final HashMap<String, Object> scriptContextParams) throws Exception {
        JSONObject json = new JSONObject();
        json = new JSONObject();
        json.put("activityName", activity.getName());
        json.put("activityCaption", activity.getCaption());
        json.put("multiReciver", activity.isMultiReciver());

        final JSONObject treeJson = new JSONObject();
        treeJson.put("id", -1);
        treeJson.put("name", "接收角色列表");
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, "select name,id from sys_role where id>0 and id in(" + StringUtils.join(activity.getRoles().iterator(), ",") + ") order by sortorder");
            cmd.executeQuery();
            while (cmd.next()) {
                final JSONObject userJson = new JSONObject();
                userJson.put("id", cmd.getString(2));
                userJson.put("name", cmd.getString(1));
                userJson.put("type", TYPE_IS_ROLE);
                JSONUtils.append(treeJson, "users", userJson);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        JSONUtils.append(json, "tree", treeJson);
        return json;
    }

    /**
     * @param con
     * @param activity
     * @param scriptContextParams
     * @param json
     * @return
     * @throws SQLException
     * @throws DBException
     * @throws Exception
     * @throws JSONException
     */
    private JSONObject getCommonActivityUserTree(final Connection con, final WFActivity activity, final HashMap<String, Object> scriptContextParams) throws Exception, Exception {
        JSONObject json = null;
        IDBCommand cmd = null;
        try {
            String sql = "select id,realname,duty,p_id,ext1,ext2,ext3 from sys_userinfo where valid=1 and exists (select 'x' from sys_user2role where u_id=sys_userinfo.id and r_id in (" + StringUtils.join(activity.getRoles().toArray(), ",") + ")) order by sortorder";
            cmd = DBHELPER.getCommand(con, sql);
            cmd.executeQuery();
            final List<Long> userIDS = new ArrayList<Long>();
            final List<UserHelperClass> userList = new ArrayList<UserHelperClass>();
            while (cmd.next()) {
                final UserHelperClass candidate = new UserHelperClass();
                candidate.id = cmd.getLong("id");
                candidate.departmentId = cmd.getLong("p_id");
                candidate.duty = cmd.getString("duty");
                candidate.name = cmd.getString("realname");
                candidate.ext1 = cmd.getString("ext1");
                candidate.ext2 = cmd.getString("ext2");
                candidate.ext3 = cmd.getString("ext3");

                if (onFilterCandidateEvent(con, activity, candidate, scriptContextParams)) {
                    userIDS.add(candidate.id);
                    userList.add(candidate);
                }
            }
            final Map<Long, JSONObject> id2Json = new HashMap<Long, JSONObject>();
            if (userIDS.size() != 0) {
                sql = "declare @temp table (id bigint,p_id bigint,sortorder bigint,name varchar(400));\n" + //
                        "WITH\n" + //
                        "TREE AS(\n" + //
                        "    SELECT * FROM sys_department\n" + //
                        "    WHERE id in (select p_id from sys_userinfo where id in (" + StringUtils.join(userIDS.toArray(), ",") + "))\n" + //
                        "    UNION ALL\n" + //
                        "    SELECT sys_department.* FROM sys_department, TREE\n" + //
                        "    WHERE sys_department.id = TREE.p_id\n" + //
                        ")\n" + //
                        "insert into @temp (id,p_id,sortorder,name) select id,p_id,sortorder,name from TREE;\n" + //
                        "with tree as\n" + //
                        "(select * from @temp where p_id=-1\n" + //
                        "union all\n" + //
                        "select T.* from @temp T,TREE where TREE.id=T.p_id)\n" + //
                        "select distinct id,name,p_id from TREE order by sortorder";//
                cmd.setSQL(sql);
                cmd.executeQuery();
                json = new JSONObject();
                json.put("activityName", activity.getName());
                json.put("activityCaption", activity.getCaption());
                json.put("multiReciver", activity.isMultiReciver());
                while (cmd.next()) {
                    final JSONObject dep = new JSONObject();
                    dep.put("id", cmd.getLong(1));
                    dep.put("p_id", cmd.getLong(3));
                    dep.put("name", cmd.getString(2));
                    if (id2Json.containsKey(cmd.getLong(3)))
                        JSONUtils.append(id2Json.get(cmd.getLong(3)), "children", dep);
                    // id2Json.get(cmd.getLong(3)).append("children", dep);
                    id2Json.put(cmd.getLong(1), dep);
                }

                for (int i = 0; i < userList.size(); i++) {
                    final JSONObject userJson = new JSONObject();
                    userJson.put("id", userList.get(i).id);
                    userJson.put("name", userList.get(i).name);
                    userJson.put("type", TYPE_IS_USER);
                    long p_id = userList.get(i).departmentId;
                    JSONObject pJson = id2Json.get(p_id);
                    while ((pJson != null) && (p_id != -65535)) {
                        JSONUtils.append(pJson, "users", userJson);
                        // pJson.append("users", userJson);
                        p_id = pJson.getLong("p_id");
                        pJson = id2Json.get(p_id);
                    }
                }
                JSONUtils.append(json, "tree", id2Json.get(-1l));
                // json.append("tree", id2Json.get(-1l));
            }
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    /**
     * @param activity
     * @return
     * @throws JSONException
     */
    private JSONObject generalEndActivityUserTree(final WFActivity activity) {
        final JSONObject json = new JSONObject();
        json.put("activityName", activity.getName());
        json.put("activityCaption", activity.getCaption());
        json.put("multiReciver", false);
        JSONUtils.append(json, "tree", JSONUtils.parserJSONObject("{\"id\" : -65535,\"users\" : [ {\"id\" : -65535,\"name\" : \"工作流引擎\"} ],\"name\" : \"工作流引擎\"}"));
        return json;
    }

    @Override
    protected String getSQL4List() {
        return "select id from sys_workflow_d_process";
    }

    @Override
    protected String getSQL4Process() {
        //
        return "select name,descript,status,version,CREATEDATE,lastmodifydate,limit_num,limit_unit,property from sys_workflow_d_process where id=:id";
    }

    @Override
    protected String getSQL4Action() {
        return "select ID,NAME,DESCRIPT,LIMIT_num,limit_unit,taskscript,issplit,isjoin,type,caption,variables,receive_event,send_event,user_filter from sys_workflow_d_action where  id<>pid and pid=:pid";
    }

    @Override
    protected String getSQL4Links() {
        return "select ID,NAME,descript,condition link_condition,source_id,target_id,caption from sys_workflow_d_link t where pid=:pid";
    }

    @Override
    protected String getSQL4Roles() {
        return "select roleid from sys_workflow_d_roles where pid=:pid";
    }

    @Override
    protected String getSQL4Variables() {
        return "select id,name, datatype,paramcomment,isrequire,defvalue from SYS_WORKFLOW_D_VARIABLES t where pid=:pid";
    }

    @Override
    protected String getSQL4Forms() {
        return "select formid id,b.caption,controls from sys_workflow_d_forms a,sys_object_tree b where a.formid = b.id and a.pid=:pid order by b.sortorder";
    }

    @Override
    protected String getSQL4ObjectTree() {
        return "select id,caption, type from sys_object_tree where type in (1,2,11) and pid=:pid order by sortorder";
    }

    @Override
    protected String getSQL4CreateCase() {
        return "begin\n execute proc_workflow_create_case :process_id,:user_id,:process_type_id,:activity_name,:createdate,:process_durtion, :activity_duration ;\nend;";
    }

    @Override
    protected String getSQL4AbabdonProcessByStep() {
        return "begin\n execute proc_workflow_abandon_case_by_step :step_id,:user_id;\nend;";
    }

    @Override
    protected String getSQL4RestoreProcessByStep() {
        return "begin\n execute proc_workflow_restore_case_by_step :step_id,:user_id;\nend;";
    }

    // 更新意见
    @Override
    protected String getSQL4UpdateIdea() {
        return "update sys_workflow_r_ideas set idea_content=:idea_content,idea_date=getdate() where id=:step_id";
    }

    private WFDBStorage4SQLServer() {
        super();

    }

    private static WFDBStorage4SQLServer instance = new WFDBStorage4SQLServer();

    public static IWFStorage getInstance() {
        return instance;
    }

    @Override
    protected String getSQL4GetStepFormsInfo() {
        return new StringBuilder().append("select a.Process_Id Process_Type_Id, a.Id Process_Id, b.Activity_Name, b.Send_Userid, c.Realname Send_Username, b.Reciver_Userid, d.Realname Reciver_Username\n")//
                .append("  from Sys_Userinfo c, Sys_Userinfo d, Sys_Workflow_r_Process a, Sys_Workflow_r_Step b\n")//
                .append(" where b.Send_Userid = c.Id\n")//
                .append("       and b.Reciver_Userid = d.Id\n")//
                .append("       and a.Id = b.Process_Id\n")//
                .append("       and b.Id = :Step_Id\n")//
                .append("union\n")//
                .append("select a.Process_Id Process_Type_Id, a.Id Process_Id, b.Activity_Name, b.Send_Userid, c.Realname Send_Username, b.Reciver_Roleid, d.Name Reciver_Username\n")//
                .append("  from Sys_Userinfo c, Sys_Role d, Sys_Workflow_r_Process a, Sys_Workflow_r_Share_Step b\n")//
                .append(" where b.Send_Userid = c.Id\n")//
                .append("       and b.Reciver_Roleid = d.Id\n")//
                .append("       and a.Id = b.Process_Id\n")//
                .append("       and b.Id = :Step_Id").toString();
    }

    @Override
    protected String getSQL4SaveProcessVariableValue() {
        return "begin\n exec proc_workflow_save_variable_value :process_id,:var_name,:var_value;\nend;";
    }

    @Override
    protected String getSQL4SendToNextActitity() {
        return "begin\n exec proc_workflow_send :stepid,:activityname,:activitycaption,:userid,:limit_num,:limit_unit,:senddate,:activity_duration,:is_sendto_role;\nend;";
    }

    @Override
    protected String getSQL4FlagActivityFormSaved() {
        return "begin\n exec proc_workflow_register_form_saved :processid ,:activityname,:formid,:user_id;\nend;";
    }

    @Override
    protected String getSQL4BackProcess() {
        return "begin\n exec proc_workflow_back :step_id;\nend;";
    }

    @Override
    protected String getSQL4BackProcess2Creator() {
        return "begin\n exec proc_workflow_back2creator :step_id;\nend;";
    }

    @Override
    protected String getSQL4GeneralProcessId() {
        return "declare @id bigint;\nexec proc_workflow_get_processid null, @id output;\nselect @id as id;";
    }

    @Override
    protected String getSQL4ProcessSteps() {
        return "select t.id,t.reciver_userid, dbo.fun_datetime2str(t.reciver_finish_date) finish_date, dbo.fun_datetime2str(t.send_date) send_date,t.activity_name,t.activity_caption,(case t.step_type when 1 then '正常' else '退件' end) type,b.realname from sys_userinfo b, sys_workflow_r_step t where t.reciver_userid = b.id and t.process_id=:process_id order by t.send_date";
    }

    @Override
    protected String getSQL4ProcessIdeas() {
        return new StringBuilder().append("select a.id, a.idea_content, dbo.fun_datetime2str(idea_date) idea_date, b.activity_caption, c.realname, replace(replace(dbo.fun_attachment_getFileList('workflow_idea', b.id),'<a','<p><a'),'</a>','</a></p>') filelist\n") //
                .append("  from sys_userinfo c, sys_workflow_r_ideas a, sys_workflow_r_step b\n") //
                .append(" where a.id = b.id\n") //
                .append("   and b.reciver_userid = c.id\n") //
                .append("   and b.process_id = :process_id\n") //
                .append("   order by b.send_date").toString();
    }

    @Override
    protected String getSignCaseProcedureName() {
        return "proc_workflow_sign_case";
    }

}
