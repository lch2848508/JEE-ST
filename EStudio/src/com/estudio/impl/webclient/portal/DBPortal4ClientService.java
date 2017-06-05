package com.estudio.impl.webclient.portal;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.webclient.form.IPortal4ClientService;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

public abstract class DBPortal4ClientService implements IPortal4ClientService {

    private static final String USER2PORTALCACHE_PREFIX = "user2PortalJson-";
    protected static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private long GLOBAL_USER_MANAGER_VERSION = -1;

    // private HashMap<Long, JSONObject> userID2PortalTreeJSON = new
    // HashMap<Long, JSONObject>();

    private String getKey(final long id) {
        return USER2PORTALCACHE_PREFIX + id;
    }

    protected abstract String getPortalGroupListSQL4Admin();

    protected abstract String getPortalItemListSQL4Admin();

    protected abstract String getPortalGroupList4CommonUser();

    protected abstract String getPortalItemListSQL4CommonUser();

    protected abstract long getUserManagerVersion() throws Exception;

    protected DBPortal4ClientService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.portal.oracle.IPortal4ClientService#
     * notifyPortalSettingChange()
     */
    @Override
    public void notifyPortalSettingChange() {
        SystemCacheManager.getInstance().removeWebClientByPrefix(USER2PORTALCACHE_PREFIX);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.portal.oracle.IPortal4ClientService#
     * getPortalTreeByUserID(java.sql.Connection, long, long,
     * com.estudio.define.sercure.ClientLoginInfo)
     */
    @Override
    public JSONObject getPortalTreeByUserID(final Connection con, final long user_id, final long department_id, final ClientLoginInfo loginInfo) throws Exception {
        final Map<String, String> EvnParams = new HashMap<String, String>();
        EvnParams.put("EVN.USER_ID", Long.toString(user_id));
        EvnParams.put("EVN.DEPARTMENT_ID", Long.toString(department_id));
        EvnParams.put("REQ.USER_ID", Long.toString(user_id));
        EvnParams.put("REQ.DEPARTMENT_ID", Long.toString(department_id));
        final String cacheKey = getKey(user_id);
        long tempUserManager = getUserManagerVersion();
        JSONObject json = (tempUserManager == GLOBAL_USER_MANAGER_VERSION) ? (JSONObject) SystemCacheManager.getInstance().getWebClientObject(cacheKey) : null;
        if (tempUserManager != GLOBAL_USER_MANAGER_VERSION)
            GLOBAL_USER_MANAGER_VERSION = tempUserManager;
        if (json == null) {
            json = new JSONObject();
            json.put("id", 0);
            final JSONObject rootJSON = new JSONObject();
            rootJSON.put("id", "-1");
            rootJSON.put("text", "<b>栏目列表</b>");
            rootJSON.put("open", "1");
            rootJSON.put("select", "1");
            rootJSON.put("closeable", "0");
            rootJSON.put("im0", "computer.png");
            JSONUtils.append(json, "item", rootJSON);
            // json.append("item", rootJSON);
            Connection tempCon = con;
            IDBCommand groupStmt = null;
            IDBCommand itemStmt = null;
            try {
                if (tempCon == null)
                    tempCon = DBHELPER.getConnection();

                if (loginInfo.isRole(-1)) { // 超级管理员可以查看所有栏目 避免栏目丢失问题
                    groupStmt = DBHELPER.getCommand(tempCon, getPortalGroupListSQL4Admin(), true);
                    itemStmt = DBHELPER.getCommand(tempCon, getPortalItemListSQL4Admin(), true);
                } else {
                    groupStmt = DBHELPER.getCommand(tempCon, getPortalGroupList4CommonUser(), true);
                    itemStmt = DBHELPER.getCommand(tempCon, getPortalItemListSQL4CommonUser(), true);
                    itemStmt.setParam(1, user_id);
                    groupStmt.setParam(1, user_id);
                }

                final HashMap<Long, ArrayList<JSONObject>> groupID2ItemList = new HashMap<Long, ArrayList<JSONObject>>();

                itemStmt.executeQuery();
                while (itemStmt.next()) {
                    final JSONObject itemJSON = new JSONObject();

                    itemJSON.put("id", itemStmt.getLong(1));
                    itemJSON.put("text", itemStmt.getString(2));
                    itemJSON.put("im0", itemStmt.getString(8));
                    itemJSON.put("im1", itemStmt.getString(8));
                    itemJSON.put("win", itemStmt.getInt(11) == 1 ? 0 : itemStmt.getString(9)); // 如果不允许关闭只能独享一个Window
                    itemJSON.put("autorun", itemStmt.getInt(10) == 1);
                    itemJSON.put("disableclose", itemStmt.getInt(11) == 1);
                    itemJSON.put("ishidden", itemStmt.getInt(12)==1);
                    final long type = itemStmt.getInt(5);
                    if (type == 0 || type == 5)
                        itemJSON.put("property", new String[] { Integer.toString(itemStmt.getInt(5)), Integer.toString(itemStmt.getInt(1)), itemStmt.getString(9) });
                    else
                        itemJSON.put("property", new String[] { Integer.toString(itemStmt.getInt(5)), Convert.bytes2Str(itemStmt.getBytes(6)), itemStmt.getString(9) });
                    itemJSON.put("read", itemStmt.getInt(3));
                    itemJSON.put("write", itemStmt.getInt(4));
                    final long groupID = itemStmt.getLong(7);
                    ArrayList<JSONObject> itemJSONS = groupID2ItemList.get(groupID);
                    if (itemJSONS == null) {
                        itemJSONS = new ArrayList<JSONObject>();
                        groupID2ItemList.put(groupID, itemJSONS);
                    }
                    itemJSONS.add(itemJSON);
                }
                groupStmt.executeQuery();
                while (groupStmt.next()) {
                    final long groupID = groupStmt.getLong(1);
                    final ArrayList<JSONObject> itemsList = groupID2ItemList.get(groupID);
                    if (itemsList != null) {
                        final JSONObject groupJSON = new JSONObject();
                        groupJSON.put("id", groupID);
                        groupJSON.put("text", groupStmt.getString(2));
                        groupJSON.put("open", "1");
                        groupJSON.put("im0", groupStmt.getString(4));
                        // groupJSON.put("im1", groupResultSet.getString(4));
                        // groupJSON.put("im2", groupResultSet.getString(4));
                        for (int i = 0; i < itemsList.size(); i++)
                            JSONUtils.append(groupJSON, "item", itemsList.get(i));
                        // groupJSON.append("item", itemsList.get(i));
                        JSONUtils.append(rootJSON, "item", groupJSON);
                        // rootJSON.append("item", groupJSON);
                    }
                }
                json.put("result", true);
                SystemCacheManager.getInstance().putWebClientObject(cacheKey, json);
            } finally {
                DBHELPER.closeCommand(groupStmt);
                DBHELPER.closeCommand(itemStmt);
                if (tempCon != con)
                    DBHELPER.closeConnection(tempCon);
            }
        }
        return json;
    }

}
