package com.estudio.impl.design.portal;

import java.sql.Connection;
import java.util.Iterator;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.portal.IPortalRightService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public abstract class DBPortalRightService implements IPortalRightService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final String PORTAL_RIGHT_SETTINGS = "portal_right_settings";

    protected abstract String getRootPortalRightSQL();

    protected abstract String getPortalRightByUserIdSQL();

    protected abstract String getPortalItemRightSQL();

    protected abstract String getInsertPortalRightSQL();

    protected abstract String getGroupPortalRightSQL();

    protected abstract String getDeletePortalRightSQL();

    protected String deletePortalGroupOrItemRightSQL() {
        return "delete from sys_portal_right where portal_id=?";
    }

    public DBPortalRightService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalRightService#getPortalRight(java
     * .sql.Connection, long, long)
     */
    @Override
    public long getPortalRight(final Connection con, final long portalID, final long userID) throws Exception {
        long result = 0;
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getPortalRightByUserIdSQL(), true);
            stmt.setParam(1, portalID);
            stmt.setParam(2, userID);
            stmt.executeQuery();
            if (stmt.next())
                result = stmt.getInt(2) != 0 ? 2 : stmt.getLong(1) != 0 ? 1 : 0;
        } finally {
            DBHELPER.closeCommand(stmt);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalRightService#getPortalRight(long,
     * boolean)
     */
    @Override
    public JSONObject getPortalRight(final long id, final boolean isGroup) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            if (id == -1)
                stmt = DBHELPER.getCommand(con, getRootPortalRightSQL(), true);
            else if (isGroup) {
                stmt = DBHELPER.getCommand(con, getGroupPortalRightSQL(), true);
                stmt.setParam(1, id);
            } else {
                stmt = DBHELPER.getCommand(con, getPortalItemRightSQL(), true);
                stmt.setParam(1, id);
                stmt.setParam(2, id);
            }
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject rightJSON = new JSONObject();
                rightJSON.put("role_id", stmt.getLong(2));
                rightJSON.put("role_name", stmt.getString(1));
                rightJSON.put("readable", stmt.getInt(3) != 0);
                rightJSON.put("writeable", stmt.getInt(4) != 0);
                JSONUtils.append(json, "items", rightJSON);
                // json.append("items", rightJSON);
            }
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
     * com.estudio.impl.design.portal.IPortalRightService#savePortalRight(long,
     * java.lang.String)
     */
    @Override
    public JSONObject savePortalRight(final long id, final String rights) throws Exception {
        final JSONObject json = new JSONObject();
        final JSONObject rightJSON = JSONUtils.parserJSONObject(rights);
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, getDeletePortalRightSQL(), true);
            stmt.execute();
            RuntimeContext.getVersionService().incVersion(con, PORTAL_RIGHT_SETTINGS);
            stmt = DBHELPER.getCommand(con, getInsertPortalRightSQL(), true);
            final Iterator<?> pI = rightJSON.keySet().iterator();// rightJSON.keys();
            while (pI.hasNext()) {
                final String key = (String) pI.next();
                final long portalID = Convert.str2Long(key);
                final JSONObject pJSON = rightJSON.getJSONObject(key);
                final Iterator<?> rI = pJSON.keySet().iterator();
                while (rI.hasNext()) {
                    final String r = (String) rI.next();
                    final long roleID = Convert.str2Long(r);
                    final long right = pJSON.getInt(r);
                    stmt.setParam(1, DBHELPER.getUniqueID(con));
                    stmt.setParam(2, portalID);
                    stmt.setParam(3, roleID);
                    stmt.setParam(4, right != 0 ? 1 : 0);
                    stmt.setParam(5, right == 2 ? 1 : 0);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();

            // 通知客户端解析服务内容已经发生变化
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
     * com.estudio.impl.design.portal.IPortalRightService#savePortalRight(long,
     * java.lang.String, java.lang.String)
     */
    @Override
    public JSONObject savePortalRight(final long portal_id, final String roleids, final String rights) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            DBHELPER.beginTransaction(con);

            stmt = DBHELPER.getCommand(con, deletePortalGroupOrItemRightSQL(), true);
            stmt.setParam(1, portal_id);
            stmt.execute();
            stmt.close();
            stmt = null;

            if (!StringUtils.isEmpty(roleids)) {
                final String[] rids = roleids.split(";");
                final String[] rs = rights.split(";");
                stmt = DBHELPER.getCommand(con, getInsertPortalRightSQL(), true);
                for (int i = 0; i < rids.length; i++) {
                    stmt.setParam(1, DBHELPER.getUniqueID(con));
                    stmt.setParam(2, portal_id);
                    stmt.setParam(3, rids[i]);
                    stmt.setParam(4, 1);
                    stmt.setParam(5, Convert.str2Int(rs[i]) == 2 ? 1 : 0);
                    stmt.execute();
                }
            }
            DBHELPER.commit(con);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            DBHELPER.rollback(con, false);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.endTransaction(con);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

}
