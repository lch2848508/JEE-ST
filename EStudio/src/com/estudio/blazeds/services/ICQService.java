package com.estudio.blazeds.services;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.JSONUtils;

public class ICQService {
    private static IDBHelper DBHELPER = null;

    /**
     * 获取用户目录树
     * 
     * @return
     * @throws Exception
     */
    public String getUserTree(long userId) throws Exception {
        if (DBHELPER == null)
            DBHELPER = RuntimeContext.getDbHelper();
        return getUserTreeJson(userId).toString();
    }

    /**
     * 发送消息到一个具体的用户
     * 
     * @param userIds
     * @param content
     */
    public String sendMessage(long[] userIds, String content, long sendUserId) {
        JSONObject json = new JSONObject();
        json.put("r", false);
        MessageHelper.getInstance().sendMessage2User(userIds, content, sendUserId);
        json.put("send_datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        json.put("userIds", userIds);
        json.put("content", content);
        json.put("r", true);
        return json.toString();
    }

    /**
     * 接收消息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public String reciveMessage(long userId) throws Exception {
        return MessageHelper.getInstance().reciverMessage(userId).toString();
    }

    /**
     * 获取用户目录树
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    private JSONObject getUserTreeJson(long userId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", "-1");
        json.put("label", "系统用户列表");
        json.put("type", "root");
        Connection con = null;
        IDBCommand deptCmd = null;
        IDBCommand userCmd = null;
        try {
            con = DBHELPER.getConnection();
            deptCmd = DBHELPER.getCommand(con, "select id,name from sys_department where exists (select 'x' from sys_userinfo where p_id = sys_department.id) and valid=1 and p_id=:p_id order by sortorder");
            userCmd = DBHELPER.getCommand(con, "select id,realname,loginname from sys_userinfo where valid=1 and id<>:user_id and p_id=:p_id order by sortorder");
            userCmd.setParam("user_id", userId);
            generalUserTreeJson(json, deptCmd, userCmd, -1);
        } finally {
            DBHELPER.closeCommand(userCmd);
            DBHELPER.closeCommand(deptCmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 生成用户目录树
     * 
     * @param json
     * @param deptCmd
     * @param userCmd
     * @param i
     * @throws Exception
     */
    private void generalUserTreeJson(JSONObject json, IDBCommand deptCmd, IDBCommand userCmd, long p_id) throws Exception {
        Map<Long, JSONObject> dept2Json = new HashMap<Long, JSONObject>();
        deptCmd.setParam("p_id", p_id);
        deptCmd.executeQuery();
        while (deptCmd.next()) {
            JSONObject deptJson = new JSONObject();
            deptJson.put("id", deptCmd.getLong(1));
            deptJson.put("label", deptCmd.getString(2));
            deptJson.put("type", "dept");
            dept2Json.put(deptCmd.getLong(1), deptJson);
            JSONUtils.append(json, "children", deptJson);
        }

        userCmd.setParam("p_id", p_id);
        userCmd.executeQuery();
        while (userCmd.next()) {
            JSONObject userJson = new JSONObject();
            userJson.put("id", userCmd.getLong(1));
            userJson.put("label", userCmd.getString(2));
            userJson.put("type", "user");
            JSONUtils.append(json, "children", userJson);
        }

        for (Map.Entry<Long, JSONObject> entry : dept2Json.entrySet())
            generalUserTreeJson(entry.getValue(), deptCmd, userCmd, entry.getKey());
    }
}
