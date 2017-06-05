package com.estudio.impl.design.portal;

import java.sql.Connection;
import java.util.ArrayList;

import net.minidev.json.JSONObject;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.define.design.portal.PortalItemRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.portal.IPortalItemService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;

public abstract class DBPortalItemService implements IPortalItemService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getSelectSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getListSQL();

    protected abstract String getExchangeSQL();

    protected abstract String getMoveSQL();

    protected IDBCommand selectCMD;
    protected IDBCommand updateCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand deleteCMD;
    protected IDBCommand listCMD;
    protected IDBCommand exchangeCMD;
    protected IDBCommand movetoCMD;

    {
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

    protected DBPortalItemService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.portal.IPortalItemService#published(long,
     * boolean)
     */
    @Override
    public JSONObject published(final long id, final boolean isPublished) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, "update sys_portal_item set published=? where id=?", true);
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
     * com.estudio.impl.design.portal.IPortalItemService#moveTo(java.sql.Connection
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
     * @see com.estudio.impl.design.portal.IPortalItemService#exchange(java.sql.
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
     * com.estudio.impl.design.portal.IPortalItemService#saveRecord(java.sql
     * .Connection, com.estudio.define.design.portal.PortalItemRecord)
     */
    @Override
    public boolean saveRecord(final Connection con, final PortalItemRecord record) throws Exception {
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
            cmd.setParam("p_id", record.getPId());
            cmd.setParam("name", record.getName());
            cmd.setParam("sortorder", record.getSortorder());
            cmd.setParam("type", record.getType());
            cmd.setParam("property", Convert.str2Bytes(record.getProperty()));
            cmd.setParam("published", record.getPublished());
            cmd.setParam("createdate", record.getCreatedate());
            cmd.setParam("version", record.getVersion());
            cmd.setParam("icon", record.getIcon());
            cmd.setParam("win", record.getWin());
            cmd.setParam("autorun", record.getAutorun());
            cmd.setParam("disableclose", record.getDisableClose());
            cmd.setParam("ishidden", record.getHidden());
            record.setOld();

            // 通知客户端内容已经发生变化
            RuntimeContext.getPortal4ClientService().notifyPortalSettingChange();
            NotifyService4Cluster.getInstance().notifyClusterMessage(4, -1, -1, con);
            // 通知数据列表栏目服务栏目属性已经发生变化
            RuntimeContext.getPortal4ClientGridDefineService().notifyGridDefineIsChanged(record.getId());
            NotifyService4Cluster.getInstance().notifyClusterMessage(5, record.getId(), 0, con);

            cmd.execute();
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
     * com.estudio.impl.design.portal.IPortalItemService#deleteRecord(java.sql
     * .Connection, long)
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
     * com.estudio.impl.design.portal.IPortalItemService#fillRecord(com.estudio
     * .intf.db.IDBCommand, com.estudio.define.design.portal.PortalItemRecord)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final PortalItemRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setPId(cmd.getLong("P_ID"));
        record.setName(cmd.getString("NAME"));
        record.setSortorder(cmd.getLong("SORTORDER"));
        record.setType(cmd.getInt("TYPE"));
        record.setProperty(Convert.bytes2Str(cmd.getBytes("PROPERTY")));
        record.setPublished(cmd.getInt("PUBLISHED"));
        record.setCreatedate(cmd.getDate("CREATEDATE"));
        record.setVersion(cmd.getLong("VERSION"));
        record.setIcon(cmd.getString("ICON"));
        record.setWin(cmd.getInt("WIN"));
        record.setAutorun(cmd.getInt("AUTORUN"));
        record.setDisableClose(cmd.getInt("DISABLECLOSE"));
        record.setHidden(cmd.getInt("ishidden"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.portal.IPortalItemService#getRecord(java.sql.
     * Connection, long)
     */
    @Override
    public PortalItemRecord getRecord(final Connection con, final long id) throws Exception {
        PortalItemRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new PortalItemRecord();
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
     * com.estudio.impl.design.portal.IPortalItemService#getRecords(java.sql
     * .Connection, long)
     */
    @Override
    public ArrayList<PortalItemRecord> getRecords(final Connection con, final long pid) throws Exception {
        final ArrayList<PortalItemRecord> records = new ArrayList<PortalItemRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            cmd.setParam(1, pid);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final PortalItemRecord record = new PortalItemRecord();
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
     * @see com.estudio.impl.design.portal.IPortalItemService#newRecord()
     */
    @Override
    public PortalItemRecord newRecord() {
        return new PortalItemRecord();
    }

}
