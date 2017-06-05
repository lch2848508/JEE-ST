package com.estudio.web.servlet.webclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;

public class MyDesktopServlet extends BaseServlet {
    private static final long serialVersionUID = 6955410214168040294L;
    protected static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            generateRiskInfo(con, json);
            generateCommonCaseList(con, json);
            // generateSuperviseCaseList(con, json);
            generateMessageList(con, json);
            generateImportanceMessage(con, json);
            generateWarningCase(con, json);
            generateAllCaseList(con, json);
        } finally {
            DBHELPER.closeConnection(con);
        }
        json.put("r", true);
        response.getWriter().println(json);
    }

    /**
     * 所有案件列表
     * 
     * @param con
     * @param json
     * @throws SQLException
     */
    private void generateAllCaseList(Connection con, JSONObject json) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "--工作流待办箱SQL语句\n" + //
                    "select c.Id, --工作流步骤唯一号\n" + //
                    "       c.Process_Id, --业务员编号\n" + //
                    "       a.id process_type_id, --流程业务类型ID\n" + //
                    "       a.Name Process_Name, --工作流名称\n" + //
                    "       b.Caption Process_Caption, --具体的case标题\n" + //
                    "       replace(U3.Realname,'工作流引擎',' ')  Create_Username, --工作流创建人\n" + //
                    "       To_Char(b.Create_Date, 'YYYY-MM-DD Hh24:MI') Create_Date, --创建日期\n" + //
                    "       (b.Limit_Num || b.Limit_Unit) Process_Limit, --办文时限\n" + //
                    "       To_Char(b.Deadline, 'YYYY-MM-DD HH24:MI') Process_Deadline, --整个流程最后期限\n" + //
                    "       c.Activity_Caption, --当前流程节点\n" + //
                    "       c.Activity_name, --当前流程节点名称\n" + //
                    "       To_Char(c.Send_Date, 'YYYY-MM-DD HH24:MI') Send_Date, --发送日期\n" + //
                    "       To_Char(c.Reciver_Finish_Date, 'YYYY-MM-DD HH24:MI') Finish_Date, --完成日期\n" + //
                    "       replace(U1.Realname,'工作流引擎',' ')  Send_Username, --发送人用户名\n" + //
                    "       replace(U2.Realname,'工作流引擎',' ')  Reciver_Username, --收件人用户名\n" + //
                    "       (c.Limit_Num || c.Limit_Unit) Activity_Limit, --环节办文时限\n" + //
                    "       To_Char(c.Deadline, 'YYYY-MM-DD HH24:MI') Activity_Deadline, --环节办文时限\n" + //
                    "       Decode(c.Step_Type, 2, '退件', '正常') Is_Back_Step,\n" + //
                    "       c.Step_Type,\n" + //
                    "       round(c.Deadline - sysdate) remaid_time\n" + //
                    "  from Sys_Userinfo           U3, --工作流创建人\n" + //
                    "       Sys_Userinfo           U1, --工作流发件人\n" + //
                    "       Sys_Userinfo           U2, --工作流收件人\n" + //
                    "       Sys_Workflow_d_Process a, --工作流设计表\n" + //
                    "       Sys_Workflow_r_Process b, --工作流运行时流程表\n" + //
                    "       Sys_Workflow_r_Step    c --工作流步骤表\n" + //
                    " where c.Reciver_Userid = U2.Id --收件人\n" + //
                    "   and c.Send_Userid = U1.Id --发件人\n" + //
                    "   and b.Create_Userid = U3.Id --创建人\n" + //
                    "   and b.Process_Id = a.Id --流程设计时\n" + //
                    "   and b.Valid = 1 --流程正常没有被删除\n" + //
                    "   and b.Id = c.Process_Id --流程运行时\n" + //
                    "   and c.Reciver_Finish_Date is null --完成日期\n" + //
                    " order by c.Send_Date asc";
            stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            while (rs.next() && (index++) < 15) {
                JSONObject recordJson = new JSONObject();
                recordJson.put("id", rs.getString("id"));
                recordJson.put("processName", rs.getString("PROCESS_NAME"));
                recordJson.put("processCaption", rs.getString("PROCESS_CAPTION"));
                recordJson.put("sendDate", rs.getString("SEND_DATE"));
                recordJson.put("sendUser", rs.getString("SEND_USERNAME"));
                recordJson.put("remaidTime", rs.getDouble("REMAID_TIME"));
                double remaidTime = rs.getDouble("REMAID_TIME");
                String icon = remaidTime <= 1 ? "flag_red.png" : remaidTime <= 2 ? "flag_yellow.png" : "flag_green.png";
                recordJson.put("icon", "../../images/" + icon);
                JSONUtils.append(json, "allCaseList", recordJson);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 
     * @param con
     * @param json
     * @throws SQLException
     */
    private void generateWarningCase(Connection con, JSONObject json) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "--工作流待办箱SQL语句\n" + //
                    "select c.Id, --工作流步骤唯一号\n" + //
                    "       c.Process_Id, --业务员编号\n" + //
                    "       a.id process_type_id, --流程业务类型ID\n" + //
                    "       a.Name Process_Name, --工作流名称\n" + //
                    "       b.Caption Process_Caption, --具体的case标题\n" + //
                    "       replace(U3.Realname,'工作流引擎',' ')  Create_Username, --工作流创建人\n" + //
                    "       To_Char(b.Create_Date, 'YYYY-MM-DD Hh24:MI') Create_Date, --创建日期\n" + //
                    "       (b.Limit_Num || b.Limit_Unit) Process_Limit, --办文时限\n" + //
                    "       To_Char(b.Deadline, 'YYYY-MM-DD HH24:MI') Process_Deadline, --整个流程最后期限\n" + //
                    "       c.Activity_Caption, --当前流程节点\n" + //
                    "       c.Activity_name, --当前流程节点名称\n" + //
                    "       To_Char(c.Send_Date, 'YYYY-MM-DD HH24:MI') Send_Date, --发送日期\n" + //
                    "       To_Char(c.Reciver_Finish_Date, 'YYYY-MM-DD HH24:MI') Finish_Date, --完成日期\n" + //
                    "       replace(U1.Realname,'工作流引擎',' ')  Send_Username, --发送人用户名\n" + //
                    "       replace(U2.Realname,'工作流引擎',' ')  Reciver_Username, --收件人用户名\n" + //
                    "       (c.Limit_Num || c.Limit_Unit) Activity_Limit, --环节办文时限\n" + //
                    "       To_Char(c.Deadline, 'YYYY-MM-DD HH24:MI') Activity_Deadline, --环节办文时限\n" + //
                    "       Decode(c.Step_Type, 2, '退件', '正常') Is_Back_Step,\n" + //
                    "       c.Step_Type,\n" + //
                    "       round(c.Deadline - sysdate) remaid_time\n" + //
                    "  from Sys_Userinfo           U3, --工作流创建人\n" + //
                    "       Sys_Userinfo           U1, --工作流发件人\n" + //
                    "       Sys_Userinfo           U2, --工作流收件人\n" + //
                    "       Sys_Workflow_d_Process a, --工作流设计表\n" + //
                    "       Sys_Workflow_r_Process b, --工作流运行时流程表\n" + //
                    "       Sys_Workflow_r_Step    c --工作流步骤表\n" + //
                    " where c.Reciver_Userid = U2.Id --收件人\n" + //
                    "   and c.Send_Userid = U1.Id --发件人\n" + //
                    "   and b.Create_Userid = U3.Id --创建人\n" + //
                    "   and b.Process_Id = a.Id --流程设计时\n" + //
                    "   and b.Valid = 1 --流程正常没有被删除\n" + //
                    "   and b.Id = c.Process_Id --流程运行时\n" + //
                    "   and c.Reciver_Finish_Date is null --完成日期\n" + //
                    // "   and exists (select 'x' from sys_workflow_r_process_msg where sys_workflow_r_process_msg.process_id = b.id)\n"
                    // + //
                    "   and (c.Deadline - sysdate)<=2" + //
                    // "   and c.Reciver_Userid = ? --收件人ID\n" + //
                    " order by c.Send_Date asc";
            stmt = con.prepareStatement(sql);
            // stmt.setLong(1, GlobalContext.getLoginInfo().getId());
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            while (rs.next() && (index++) < 15) {
                JSONObject recordJson = new JSONObject();
                recordJson.put("id", rs.getString("id"));
                recordJson.put("processName", rs.getString("PROCESS_NAME"));
                recordJson.put("processCaption", rs.getString("PROCESS_CAPTION"));
                recordJson.put("sendDate", rs.getString("SEND_DATE"));
                recordJson.put("sendUser", StringUtils.replace(rs.getString("SEND_USERNAME"), "工作流引擎", ""));
                recordJson.put("remaidTime", rs.getDouble("REMAID_TIME"));
                double remaidTime = rs.getDouble("REMAID_TIME");
                String icon = remaidTime <= 1 ? "flag_red.png" : remaidTime <= 2 ? "flag_yellow.png" : "flag_green.png";
                recordJson.put("icon", "../../images/" + icon);
                JSONUtils.append(json, "warningCaseList", recordJson);
            }
            if (!json.containsKey("warningCaseList"))
                json.put("warningCaseList", new JSONArray());
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 生成消息列表
     * 
     * @param con
     * @param json
     * @throws Exception
     */
    private void generateMessageList(Connection con, JSONObject json) throws Exception {
        json.put("newMessages", DBHELPER.executeQuery("select * from (select t.id,t.caption caption,t.author  username,t.regdate fbsj,t.source  comefrom,t.type lx from common_news t order by sortorder desc) where rownum < 15", con));
    }

    /**
     * 
     * @param con
     * @param json
     * @throws Exception
     */
    private void generateImportanceMessage(Connection con, JSONObject json) throws Exception {
        JSONArray records = DBHELPER.executeQuery("select * from (select t.id, t.bt caption, to_char(t.GSSJ,'YYYY-MM-DD') SJ from CD_SZYDNSNR t where t.sfgs = 1 and t.gsjssj > sysdate order by t.gssj desc) where rownum<15", con);
        if (records == null)
            records = new JSONArray();
        json.put("importanceMessage", records);
    }

    /**
     * 生成督办信息
     * 
     * @param con
     * @param json
     * @throws Exception
     */
    private void generateSuperviseCaseList(Connection con, JSONObject json) throws Exception {
        PreparedStatement stmt = null;
        try {
            String sql = "--工作流待办箱SQL语句\n" + //
                    "select c.Id, --工作流步骤唯一号\n" + //
                    "       c.Process_Id, --业务员编号\n" + //
                    "       a.id process_type_id, --流程业务类型ID\n" + //
                    "       a.Name Process_Name, --工作流名称\n" + //
                    "       b.Caption Process_Caption, --具体的case标题\n" + //
                    "       replace(U3.Realname,'工作流引擎',' ')  Create_Username, --工作流创建人\n" + //
                    "       To_Char(b.Create_Date, 'YYYY-MM-DD Hh24:MI') Create_Date, --创建日期\n" + //
                    "       (b.Limit_Num || b.Limit_Unit) Process_Limit, --办文时限\n" + //
                    "       To_Char(b.Deadline, 'YYYY-MM-DD HH24:MI') Process_Deadline, --整个流程最后期限\n" + //
                    "       c.Activity_Caption, --当前流程节点\n" + //
                    "       c.Activity_name, --当前流程节点名称\n" + //
                    "       To_Char(c.Send_Date, 'YYYY-MM-DD HH24:MI') Send_Date, --发送日期\n" + //
                    "       To_Char(c.Reciver_Finish_Date, 'YYYY-MM-DD HH24:MI') Finish_Date, --完成日期\n" + //
                    "       replace(U1.Realname,'工作流引擎',' ')  Send_Username, --发送人用户名\n" + //
                    "       replace(U2.Realname,'工作流引擎',' ')  Reciver_Username, --收件人用户名\n" + //
                    "       (c.Limit_Num || c.Limit_Unit) Activity_Limit, --环节办文时限\n" + //
                    "       To_Char(c.Deadline, 'YYYY-MM-DD HH24:MI') Activity_Deadline, --环节办文时限\n" + //
                    "       Decode(c.Step_Type, 2, '退件', '正常') Is_Back_Step,\n" + //
                    "       c.Step_Type,\n" + //
                    "       c.Deadline - sysdate remaid_time\n" + //
                    "  from Sys_Userinfo           U3, --工作流创建人\n" + //
                    "       Sys_Userinfo           U1, --工作流发件人\n" + //
                    "       Sys_Userinfo           U2, --工作流收件人\n" + //
                    "       Sys_Workflow_d_Process a, --工作流设计表\n" + //
                    "       Sys_Workflow_r_Process b, --工作流运行时流程表\n" + //
                    "       Sys_Workflow_r_Step    c --工作流步骤表\n" + //
                    " where c.Reciver_Userid = U2.Id --收件人\n" + //
                    "   and c.Send_Userid = U1.Id --发件人\n" + //
                    "   and b.Create_Userid = U3.Id --创建人\n" + //
                    "   and b.Process_Id = a.Id --流程设计时\n" + //
                    "   and b.Valid = 1 --流程正常没有被删除\n" + //
                    "   and b.Id = c.Process_Id --流程运行时\n" + //
                    "   and c.Reciver_Finish_Date is null --完成日期\n" + //
                    "   and exists (select 'x' from sys_workflow_r_process_msg where sys_workflow_r_process_msg.process_id = b.id)\n" + //
                    "   and c.Reciver_Userid = ? --收件人ID\n" + //
                    " order by c.Send_Date asc";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, GlobalContext.getLoginInfo().getId());
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            while (rs.next() && (index++) < 15) {
                JSONObject recordJson = new JSONObject();
                recordJson.put("id", rs.getString("id"));
                recordJson.put("processName", rs.getString("PROCESS_NAME"));
                recordJson.put("processCaption", rs.getString("PROCESS_CAPTION"));
                recordJson.put("sendDate", rs.getString("SEND_DATE"));
                recordJson.put("sendUser", StringUtils.replace(rs.getString("SEND_USERNAME"), "工作流引擎", ""));
                recordJson.put("remaidTime", rs.getDouble("REMAID_TIME"));
                double remaidTime = rs.getDouble("REMAID_TIME");
                String icon = remaidTime <= 1 ? "flag_red.png" : remaidTime <= 2 ? "flag_yellow.png" : "flag_green.png";
                recordJson.put("icon", "../../images/" + icon);
                JSONUtils.append(json, "superviseCaseList", recordJson);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 生成业务列表
     * 
     * @param con
     * @param json
     * @throws Exception
     */
    private void generateCommonCaseList(Connection con, JSONObject json) throws Exception {
        PreparedStatement stmt = null;
        try {
            String sql = "--工作流待办箱SQL语句\n" + //
                    "select c.Id, --工作流步骤唯一号\n" + //
                    "       c.Process_Id, --业务员编号\n" + //
                    "       a.id process_type_id, --流程业务类型ID\n" + //
                    "       a.Name Process_Name, --工作流名称\n" + //
                    "       b.Caption Process_Caption, --具体的case标题\n" + //
                    "       replace(U3.Realname,'工作流引擎',' ')  Create_Username, --工作流创建人\n" + //
                    "       To_Char(b.Create_Date, 'YYYY-MM-DD Hh24:MI') Create_Date, --创建日期\n" + //
                    "       (b.Limit_Num || b.Limit_Unit) Process_Limit, --办文时限\n" + //
                    "       To_Char(b.Deadline, 'YYYY-MM-DD HH24:MI') Process_Deadline, --整个流程最后期限\n" + //
                    "       c.Activity_Caption, --当前流程节点\n" + //
                    "       c.Activity_name, --当前流程节点名称\n" + //
                    "       To_Char(c.Send_Date, 'YYYY-MM-DD HH24:MI') Send_Date, --发送日期\n" + //
                    "       To_Char(c.Reciver_Finish_Date, 'YYYY-MM-DD HH24:MI') Finish_Date, --完成日期\n" + //
                    "       replace(U1.Realname,'工作流引擎',' ')  Send_Username, --发送人用户名\n" + //
                    "       replace(U2.Realname,'工作流引擎',' ')  Reciver_Username, --收件人用户名\n" + //
                    "       (c.Limit_Num || c.Limit_Unit) Activity_Limit, --环节办文时限\n" + //
                    "       To_Char(c.Deadline, 'YYYY-MM-DD HH24:MI') Activity_Deadline, --环节办文时限\n" + //
                    "       Decode(c.Step_Type, 2, '退件', '正常') Is_Back_Step,\n" + //
                    "       c.Step_Type,\n" + //
                    "       round(c.Deadline - sysdate) remaid_time\n" + //
                    "  from Sys_Userinfo           U3, --工作流创建人\n" + //
                    "       Sys_Userinfo           U1, --工作流发件人\n" + //
                    "       Sys_Userinfo           U2, --工作流收件人\n" + //
                    "       Sys_Workflow_d_Process a, --工作流设计表\n" + //
                    "       Sys_Workflow_r_Process b, --工作流运行时流程表\n" + //
                    "       Sys_Workflow_r_Step    c --工作流步骤表\n" + //
                    " where c.Reciver_Userid = U2.Id --收件人\n" + //
                    "   and c.Send_Userid = U1.Id --发件人\n" + //
                    "   and b.Create_Userid = U3.Id --创建人\n" + //
                    "   and b.Process_Id = a.Id --流程设计时\n" + //
                    "   and b.Valid = 1 --流程正常没有被删除\n" + //
                    "   and b.Id = c.Process_Id --流程运行时\n" + //
                    "   and c.Reciver_Finish_Date is null --完成日期\n" + //
                    "   and c.Reciver_Userid = ? --收件人ID\n" + //
                    " order by c.Send_Date asc";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, GlobalContext.getLoginInfo().getId());
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            while (rs.next() && (index++) < 15) {
                JSONObject recordJson = new JSONObject();
                recordJson.put("id", rs.getString("id"));
                recordJson.put("processName", rs.getString("PROCESS_NAME"));
                recordJson.put("processCaption", rs.getString("PROCESS_CAPTION"));
                recordJson.put("sendDate", rs.getString("SEND_DATE"));
                recordJson.put("sendUser", StringUtils.replace(rs.getString("SEND_USERNAME"), "工作流引擎", ""));
                recordJson.put("remaidTime", rs.getDouble("REMAID_TIME"));
                double remaidTime = rs.getDouble("REMAID_TIME");
                String icon = remaidTime <= 1 ? "flag_red.png" : remaidTime <= 2 ? "flag_yellow.png" : "flag_green.png";
                recordJson.put("icon", "../../images/" + icon);
                JSONUtils.append(json, "commonCaseList", recordJson);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    /**
     * 生成岗位放心信息
     * 
     * @param con
     * @param userId
     * @param json
     * @throws Exception
     */
    private void generateRiskInfo(Connection con, JSONObject json) throws Exception {
        Statement stmt = null;
        try {
            stmt = con.createStatement();

            // 生成部门信息
            ClientLoginInfo loginInfo2 = GlobalContext.getLoginInfo();
            ResultSet rs = stmt.executeQuery("select name,main_duty from sys_department where id=" + loginInfo2.getDepartmentId());
            if (rs.next()) {
                JSONObject departmentJson = new JSONObject();
                departmentJson.put("name", rs.getString(1));
                departmentJson.put("duty", Convert.bytes2Str(rs.getBytes(2)));
                json.put("department", departmentJson);
            }

            // 生成角色信息
            rs = stmt.executeQuery("select name,main_duty,main_risk,risk_level,control_method from sys_role where id in (" + StringUtils.join(loginInfo2.getRoles(), ",") + ") order by id,sortorder");
            while (rs.next()) {
                JSONObject roleJson = new JSONObject();
                roleJson.put("name", rs.getString(1));
                roleJson.put("duty", Convert.bytes2Str(rs.getBytes(2)));
                roleJson.put("risk", Convert.bytes2Str(rs.getBytes(3)));
                roleJson.put("level", rs.getString(4));
                roleJson.put("control", Convert.bytes2Str(rs.getBytes(5)));
                JSONUtils.append(json, "roles", roleJson);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

}
