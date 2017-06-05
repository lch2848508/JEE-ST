package com.estudio.impl.design.portal;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import net.minidev.json.JSONObject;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.portal.PortalGroupRecord;
import com.estudio.define.webclient.portal.PortalUtils;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.portal.IPortalGroupService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public abstract class DBPortalGroupService implements IPortalGroupService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getPortalGroupListSQL();

    protected abstract String getPortalItemListSQL();

    protected abstract String getPortalRightSettingSQL();

    protected abstract String getSystemRoleListSQL();

    protected abstract String getPublishedPortalGroupSQL();

    protected abstract String getSelectSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getListSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getMoveSQL();

    protected abstract String getUpdateSQL();

    protected IDBCommand selectCMD;
    protected IDBCommand updateCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand deleteCMD;
    protected IDBCommand listCMD;
    protected IDBCommand exchangeCMD;
    protected IDBCommand movetoCMD;
    protected HashMap<String, String> designEvnParams = new HashMap<String, String>();

    {
        designEvnParams.put("EVN.USER_ID", Long.toString(-1));
        designEvnParams.put("EVN.DEPARTMENT_ID", Long.toString(-1));
        designEvnParams.put("REQ.USER_ID", Long.toString(-1));
        designEvnParams.put("REQ.DEPARTMENT_ID", Long.toString(-1));

        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
            movetoCMD = DBHELPER.getCommand(null, getMoveSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.portal.IPortalGroupService#published(long,
     * boolean)
     */
    @Override
    public JSONObject published(final long id, final boolean isPublished) throws Exception, DBException {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, getPublishedPortalGroupSQL());
            stmt.setParam(1, isPublished ? 1 : 0);
            stmt.setParam(2, id);
            stmt.execute();
            // 通知客户端内容已经发生变化

            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);
            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalGroupService#getGroupsAndItems()
     */
    @Override
    public JSONObject getGroupsAndItems() throws Exception, DBException {
        return getGroupsAndItems(true, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalGroupService#getGroupsAndItems(
     * boolean, boolean)
     */
    @Override
    public JSONObject getGroupsAndItems(final boolean includeRight, final boolean includeRoles) throws Exception, DBException {
        Connection con = null;
        IDBCommand groupStmt = null;
        IDBCommand itemStmt = null;
        IDBCommand rightStmt = null;
        IDBCommand roleStmt = null;
        final JSONObject json = new JSONObject();
        try {
            con = DBHELPER.getConnection();
            groupStmt = DBHELPER.getCommand(con, getPortalGroupListSQL());
            itemStmt = DBHELPER.getCommand(con, getPortalItemListSQL());
            groupStmt.executeQuery();
            while (groupStmt.next()) {
                final JSONObject groupJSON = new JSONObject();
                groupJSON.put("id", groupStmt.getLong(1));
                groupJSON.put("name", groupStmt.getString(2));
                groupJSON.put("published", groupStmt.getInt(3));
                groupJSON.put("icon", groupStmt.getString(4));

                itemStmt.setParam(1, groupStmt.getLong(1));
                itemStmt.executeQuery();
                while (itemStmt.next()) {
                    final JSONObject itemJSON = new JSONObject();
                    itemJSON.put("id", itemStmt.getLong(1));
                    itemJSON.put("type", itemStmt.getInt(2));
                    itemJSON.put("published", itemStmt.getInt(3) == 1);
                    itemJSON.put("name", itemStmt.getString(4));
                    final String url = PortalUtils.generalPortalItemURL(itemStmt.getInt(2), itemStmt.getInt(2) == 0 ? itemStmt.getString(1) : Convert.bytes2Str(itemStmt.getBytes(6)), 1, 0, itemStmt.getLong(1), designEvnParams);
                    itemJSON.put("url", url);
                    // itemJSON.put("property", itemResultSet.getString(6));
                    itemJSON.put("icon", itemStmt.getString(5));
                    JSONUtils.append(groupJSON, "items", itemJSON);
                    // groupJSON.append("items", itemJSON);
                }

                JSONUtils.append(json, "groups", groupJSON);
                // json.append("groups", groupJSON);
            }

            // 读取栏目的权限设置
            if (includeRight) {
                final HashMap<Long, HashMap<Long, Long>> rightsMap = new HashMap<Long, HashMap<Long, Long>>();
                rightStmt = DBHELPER.getCommand(con, getPortalRightSettingSQL());
                rightStmt.executeQuery();
                while (rightStmt.next()) {
                    final long portalID = rightStmt.getLong(2);
                    final long roleID = rightStmt.getLong(3);
                    final long right = rightStmt.getInt(5) == 1 ? 2 : rightStmt.getInt(4) == 1 ? 1 : 0;
                    HashMap<Long, Long> rightArray = rightsMap.get(portalID);
                    if (rightArray == null) {
                        rightArray = new HashMap<Long, Long>();
                        rightsMap.put(portalID, rightArray);
                    }
                    rightArray.put(roleID, right);
                }
                json.put("rights", rightsMap);
            }

            // 读取系统的角色列表
            if (includeRoles) {
                roleStmt = DBHELPER.getCommand(con, getSystemRoleListSQL());
                final ArrayList<JSONObject> roleArray = new ArrayList<JSONObject>();
                roleStmt.executeQuery();
                while (roleStmt.next()) {
                    final JSONObject roleJSON = new JSONObject();
                    roleJSON.put("id", roleStmt.getLong(1));
                    roleJSON.put("name", roleStmt.getString(2));
                    roleJSON.put("groupname", roleStmt.getString(3));
                    roleArray.add(roleJSON);
                }
                json.put("roles", roleArray);
            }

            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(itemStmt);
            DBHELPER.closeCommand(groupStmt);
            DBHELPER.closeCommand(rightStmt);
            DBHELPER.closeCommand(roleStmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalGroupService#moveTo(java.sql.Connection
     * , long, long)
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
            // 通知客户端内容已经发生变化
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);

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
     * com.estudio.impl.design.portal.IPortalGroupService#exchange(java.sql.
     * Connection, long, long)
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
            // 通知客户端内容已经发生变化
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);

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
     * com.estudio.impl.design.portal.IPortalGroupService#saveRecord(java.sql
     * .Connection, com.estudio.define.design.portal.PortalGroupRecord)
     */
    @Override
    public boolean saveRecord(final Connection con, final PortalGroupRecord record) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            if (record.isNew()) {
                cmd = insertCMD.clone(tempCon);
                record.setId(DBHELPER.getUniqueID(tempCon));
            } else cmd = updateCMD.clone(tempCon);
            cmd.setParam("id", record.getId());
            cmd.setParam("name", record.getName());
            cmd.setParam("memo", record.getMemo());
            cmd.setParam("published", record.getPublished());
            cmd.setParam("icon", record.getIcon());
            record.setOld();
            cmd.execute();
            // 通知客户端内容已经发生变化
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);

            result = true;
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
     * com.estudio.impl.design.portal.IPortalGroupService#deleteRecord(java.
     * sql.Connection, long)
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
            // 通知客户端内容已经发生变化
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);

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
     * com.estudio.impl.design.portal.IPortalGroupService#fillRecord(com.estudio
     * .intf.db.IDBCommand, com.estudio.define.design.portal.PortalGroupRecord)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final PortalGroupRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setName(cmd.getString("NAME"));
        record.setSortorder(cmd.getLong("SORTORDER"));
        record.setMemo(cmd.getBytes("MEMO"));
        record.setCreatedate(cmd.getDate("CREATEDATE"));
        record.setPublished(cmd.getInt("PUBLISHED"));
        record.setIcon(cmd.getString("ICON"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalGroupService#getRecord(java.sql
     * .Connection, long)
     */
    @Override
    public PortalGroupRecord getRecord(final Connection con, final long id) throws Exception {
        PortalGroupRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new PortalGroupRecord();
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
     * com.estudio.impl.design.portal.IPortalGroupService#getRecords(java.sql
     * .Connection, long)
     */
    @Override
    public ArrayList<PortalGroupRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<PortalGroupRecord> records = new ArrayList<PortalGroupRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final PortalGroupRecord record = new PortalGroupRecord();
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

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.portal.IPortalGroupService#newRecord()
     */
    @Override
    public PortalGroupRecord newRecord() {
        return new PortalGroupRecord();
    }

    public DBPortalGroupService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalGroupService#getDesignEvnParams()
     */
    @Override
    public HashMap<String, String> getDesignEvnParams() {
        return designEvnParams;
    }

}
