package com.estudio.impl.design.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.db.DBDiagramRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.db.IDBDiagramService;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public abstract class DBDiagramService implements IDBDiagramService {
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    protected IDBCommand selectCMD;
    protected IDBCommand updateCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand deleteCMD;
    protected IDBCommand listCMD;
    protected IDBCommand movetoCMD;
    protected IDBCommand exchangeCMD;

    // 缓存数据
    protected class CacheDiagramEntrysItem {
        long version;
        List<JSONObject> entryItems = new ArrayList<JSONObject>();
    }

    HashMap<Long, CacheDiagramEntrysItem> cacheDiagramID2DBEntrys = new HashMap<Long, CacheDiagramEntrysItem>();
    ArrayList<JSONObject> cacheDiagramArrayList = new ArrayList<JSONObject>();
    long diagramVersion = -1;

    protected abstract String getExchangeSQL();

    protected abstract String getMovetoSQL();

    protected abstract String getListSQL();

    protected abstract String getDeleteSQL();

    protected abstract String getInsertSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getSelectSQL();

    protected abstract String getInsertDiagramItemSQL();

    protected abstract String getDeleteDiagramItemSQL();

    protected abstract String getDiagramItemPositionSQL();

    protected abstract String getIncDiagramVersionSQL();

    protected abstract String getDiagramVersionSQL();

    protected abstract String getDBEntrysVersionSQL();

    /**
     * 构造函数
     */
    protected DBDiagramService() {
        super();
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
            listCMD = DBHELPER.getCommand(null, getListSQL());
            movetoCMD = DBHELPER.getCommand(null, getMovetoSQL());
            exchangeCMD = DBHELPER.getCommand(null, getExchangeSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 保存数据库实体设计
     */
    @Override
    public JSONObject saveDiagramDBEntrys(final long id, final String entrys) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        try {
            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, getDeleteDiagramItemSQL(), true);
            stmt.setParam(1, id);
            stmt.execute();
            stmt.close();
            stmt = null;

            stmt = DBHELPER.getCommand(con, getInsertDiagramItemSQL(), true);
            stmt.setParam(8, id);
            final JSONArray jsonArray = JSONUtils.parserJSONArray(entrys);
            for (int i = 0; i < jsonArray.size(); i++) {
                final JSONObject table_json = jsonArray.getJSONObject(i);
                stmt.setParam(1, DBHELPER.getUniqueID(con));
                stmt.setParam(2, table_json.getString("n"));
                stmt.setParam(3, table_json.getInt("l"));
                stmt.setParam(4, table_json.getInt("t"));
                stmt.setParam(5, table_json.getInt("w"));
                stmt.setParam(6, table_json.getInt("h"));
                stmt.setParam(7, table_json.getInt("c"));
                stmt.execute();
            }
            json.put("r", true);
            json.put("v", incDiagramVersion(con, id));
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 获取数据库实体设计
     */
    @Override
    public JSONObject getDiagramDBEntrys(final long id, final long diagramVersion, final long linkVersion) {
        JSONObject json = null;
        Connection con = null;
        IDBCommand stmt = null;
        long version = diagramVersion;
        try {
            con = DBHELPER.getConnection();
            json = getDiagramVersion(con, id);
            if (json.getLong("v") != version) {
                version = json.getLong("v");
                CacheDiagramEntrysItem item = cacheDiagramID2DBEntrys.get(id);
                if ((item == null) || (version != item.version)) {
                    if (item == null) {
                        item = new CacheDiagramEntrysItem();
                        cacheDiagramID2DBEntrys.put(id, item);
                    }
                    item.version = version;
                    item.entryItems.clear();
                    stmt = DBHELPER.getCommand(con, getDiagramItemPositionSQL(), true);
                    stmt.setParam(1, id);
                    stmt.executeQuery();
                    while (stmt.next()) {
                        final JSONObject entry_json = new JSONObject();
                        entry_json.put("n", stmt.getString(1));
                        entry_json.put("l", stmt.getInt(2));
                        entry_json.put("t", stmt.getInt(3));
                        entry_json.put("w", stmt.getInt(4));
                        entry_json.put("h", stmt.getInt(5));
                        entry_json.put("c", stmt.getInt(6));
                        item.entryItems.add(entry_json);
                    }
                }
                final JSONObject prop_json = new JSONObject();
                prop_json.put("r", true);
                prop_json.put("v", version);
                prop_json.put("es", item.entryItems);
                json.put("prop", prop_json);

            }
            final JSONObject link_json = RuntimeContext.getDbEntryService().getDBEntryLinks(con, linkVersion);
            json.put("lv", link_json.getLong("v"));
            if (link_json.containsKey("ls"))
                json.put("ls", link_json.getJSONArray("ls"));
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);

            json = new JSONObject();
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 移动
     * 
     * @throws DBException
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
            RuntimeContext.getVersionService().incVersion(tempCon, OBJECT_TYPE);
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 保存
     * 
     * @throws DBException
     */
    @Override
    public boolean saveRecord(final Connection con, final DBDiagramRecord record) throws Exception {
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
            cmd.setParam("descript", record.getDescript());
            cmd.setParam("sortorder", record.getSortorder());
            record.setOld();
            cmd.execute();
            RuntimeContext.getVersionService().incVersion(tempCon, OBJECT_TYPE);
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;

    }

    /**
     * 删除
     * 
     * @throws DBException
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
            RuntimeContext.getVersionService().incVersion(tempCon, OBJECT_TYPE);
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 填充数据
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final DBDiagramRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setName(cmd.getString("name"));
        record.setDescript(cmd.getBytes("descript"));
        record.setOld();
    }

    /**
     * 获取数据
     * 
     * @throws DBException
     */
    @Override
    public DBDiagramRecord getRecord(final Connection con, final long id) throws Exception {
        DBDiagramRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam("id", id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new DBDiagramRecord();
                fillRecord(cmd, record);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return record;
    }

    /**
     * 获取列表
     * 
     * @throws DBException
     */
    @Override
    public ArrayList<DBDiagramRecord> getRecords(final Connection con) throws Exception {
        final ArrayList<DBDiagramRecord> records = new ArrayList<DBDiagramRecord>();
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = listCMD.clone(tempCon);
            if (cmd.executeQuery())
                while (cmd.next()) {
                    final DBDiagramRecord record = new DBDiagramRecord();
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

    /**
     * 增加版本信息
     * 
     * @param con
     * @param id
     * @return
     */
    private long incDiagramVersion(final Connection con, final long id) {
        long result = -1;
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getIncDiagramVersionSQL(), true);
            stmt.setParam(1, id);
            stmt.execute();
            result = getDiagramVersion(tempCon, id).getInt("v");
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(stmt);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 得到单个Diagram的版本信息 同时包括Diagram中各个DBEntry的时间戳
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    private JSONObject getDiagramVersion(final Connection con, final long id) throws Exception, DBException {
        final JSONObject json = new JSONObject();
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, getDiagramVersionSQL(), true);
            stmt.setParam(1, id);
            stmt.executeQuery();
            if (stmt.next()) {
                json.put("v", stmt.getLong(1));
                json.put("r", true);
                final JSONArray json_a = new JSONArray();
                getDiagramDBEntrysVersion(con, id, json_a);
                json.put("evs", json_a);
            } else {
                json.put("r", true);
                json.put("msg", "读取版本信息失败,关系模型可能被删除!");
            }
        } finally {
            DBHELPER.closeCommand(stmt);
        }

        return json;
    }

    /**
     * 得到Diagram中所有Entrys的版本信息
     * 
     * @param con
     * @param id
     * @param json_a
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    private void getDiagramDBEntrysVersion(final Connection con, final long id, final JSONArray json_a) throws Exception, DBException {
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getDBEntrysVersionSQL(), true);
            stmt.setParam(1, id);
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject json = new JSONObject();
                json.put("c", stmt.getString(1));
                json.put("v", stmt.getLong(2));
                json_a.add(json);
            }
        } finally {
            DBHELPER.closeCommand(stmt);
            if ((con != tempCon) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }

    }

    /**
     * 获取对象JSON序列化列表
     * 
     * @throws DBException
     */
    @Override
    public ArrayList<JSONObject> getDiagramsJSON(final Connection con) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final long version = RuntimeContext.getVersionService().getVersion(con, OBJECT_TYPE);
            if (version != diagramVersion) {
                diagramVersion = version;
                cacheDiagramArrayList.clear();
                cmd = listCMD.clone(tempCon);
                if (cmd.executeQuery())
                    while (cmd.next()) {
                        final DBDiagramRecord record = new DBDiagramRecord();
                        fillRecord(cmd, record);
                        cacheDiagramArrayList.add(record.getJSON());
                    }
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }

        return cacheDiagramArrayList;
    }

    /**
     * 新增记录
     */
    @Override
    public DBDiagramRecord newRecord() {
        return new DBDiagramRecord();
    }

    /**
     * 取得树版本
     */
    @Override
    public JSONObject getTreeVersion() {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            json.put("dbentrys", RuntimeContext.getDbEntryService().getDDLVersion(con));
            json.put("diagrams", RuntimeContext.getVersionService().getVersion(con, OBJECT_TYPE));
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 删除多个记录
     * 
     * @throws DBException
     */
    @Override
    public boolean deleteRecords(final Connection con, final long[] ids) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = deleteCMD.clone(tempCon);
            for (final long id : ids) {
                cmd.setParam(1, id);
                cmd.execute();
            }
            RuntimeContext.getVersionService().incVersion(tempCon, OBJECT_TYPE);
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /**
     * 交换顺序
     * 
     * @throws DBException
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
            RuntimeContext.getVersionService().incVersion(tempCon, OBJECT_TYPE);
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

}
