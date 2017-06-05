package com.estudio.impl.design.db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.db.DBException;
import com.estudio.define.design.db.DBEntry;
import com.estudio.define.design.db.DBFieldDataType;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.db.IDBEntryService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.SecurityUtils;

public abstract class DBEntryService implements IDBEntryService {

    protected static IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    // 判断表是否存在
    protected abstract String getTableIsExistsSQL();

    // 取得表键值信息
    protected abstract String getDBEntryConstraintSQL();

    // 取得表字段列表信息
    protected abstract String getDBEntryColumnsSQL();

    // 获取表名称及注释信息
    protected abstract String getEntryCodeAndNameSQL();

    // 获取模型中包含的表
    protected abstract String getDiagramDBEntrySQL();

    // 获取DDL版本信息
    protected abstract String getDDLVersionSQL();

    // 获取数据库表对象列表
    protected abstract String getDBEntrysSQL();

    // 获取数据库表索引信息
    protected abstract String getDBEntryIndexSQL();

    // 获取外键信息
    protected abstract String getDBEntryForeignKeys();

    // 获取数据库版本信息
    protected abstract String getDBEntryVersionSQL();

    // 增加DDL版本信息
    protected abstract String getIncDDLVersionSQL();

    protected abstract int getMaxObjectNameLength();

    protected abstract boolean getObjectNameIsUpperCaseStyle();

    /**
     * 缓存类
     * 
     * @author LSH
     * 
     */
    protected class DBEntryCacheItem {
        private long version;
        private JSONObject json = null;

        public long getVersion() {
            return version;
        }

        public void setVersion(final long version) {
            this.version = version;
        }

        public JSONObject getJson() {
            return json;
        }

        public void setJson(final JSONObject json) {
            this.json = json;
        }

        /**
         * 构造函数
         * 
         * @param version
         * @param json
         */
        public DBEntryCacheItem(final long version, final JSONObject json) {
            super();
            this.version = version;
            this.json = json;
        }

        /**
         * 构造函数
         */
        public DBEntryCacheItem() {
            super();
        }
    }

    // 缓存容器
    protected List<JSONObject> cacheEntryArrayList = new ArrayList<JSONObject>();
    protected JSONObject cacheLinkJSONCache = null;
    protected long ddlVersion = -1;
    protected long ddlLinkVersion = -1;
    protected ArrayList<DBFieldDataType> supportDBFieldDataTypes = new ArrayList<DBFieldDataType>();

    @Override
    public ArrayList<DBFieldDataType> getSupportDBFieldDataTypes() {
        return supportDBFieldDataTypes;
    }

    @Override
    public JSONArray getSupportDBFieldDataTypeJson() {
        final JSONArray array = new JSONArray();
        for (int i = 0; i < supportDBFieldDataTypes.size(); i++) {
            final DBFieldDataType dataType = supportDBFieldDataTypes.get(i);
            final JSONObject json = new JSONObject();
            json.put("name", dataType.getTypeName());
            json.put("notNullAble", dataType.isNotNullAble());
            json.put("primaryKeyAble", dataType.isPrimaryKeyAble());
            json.put("uniqueKeyAble", dataType.isUniqueKeyAble());
            json.put("indexAble", dataType.isIndexAble());
            json.put("fixedSize", dataType.isFixedSize());
            json.put("maxLength", dataType.getMaxLength());
            json.put("minLength", dataType.getMinLength());
            json.put("paramDataType", DBParamDataType.toInt(dataType.getParamDataType()));
            json.put("comment", dataType.getComment());
            json.put("category", dataType.getDataTypeCategory());
            array.add(json);
        }
        return array;
    }

    @Override
    public JSONObject getDatabasePropertys() {
        JSONObject json = new JSONObject();
        json.put("supportFieldDataTypes", getSupportDBFieldDataTypeJson());
        json.put("isObjectNameUpperCase", getObjectNameIsUpperCaseStyle());
        json.put("maxObjectNameLength", getMaxObjectNameLength());
        json.put("sqlLexFile", getLexFileName());
        json.put("databaseType", getDatabaseType());
        return json;
    }

    protected abstract String getDatabaseType();

    protected abstract String getLexFileName();

    /**
     * 数据类型生成转化为整数
     * 
     * @param string
     * @param length
     * @return
     */
    protected long dataTypeStr2Int(final String typeStr, final long length) {
        final String type = typeStr.toLowerCase();
        long result = 0;
        for (int i = 0; i < supportDBFieldDataTypes.size(); i++)
            if (StringUtils.equalsIgnoreCase(supportDBFieldDataTypes.get(i).getTypeName(), type)) {
                result = i;
                break;
            }
        return result;
    }

    /**
     * 判断表是否存在
     * 
     * @param
     * @param tableCode
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    protected boolean tableIsExists(final Connection con, final String tableCode) throws Exception {
        IDBCommand stmt = null;
        boolean result = false;
        try {
            stmt = DBHELPER.getCommand(con, getTableIsExistsSQL(), true);
            stmt.setParam(1, tableCode);
            stmt.executeQuery();
            stmt.next();
            result = stmt.getInt(1) != 0;
        } finally {
            DBHELPER.closeCommand(stmt);
        }
        return result;
    }

    /**
     * 生成code 和 comments
     * 
     * @param con
     * @param json
     * @param code
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    protected void getEntryCodeAndName(final Connection con, final JSONObject json, final String code) throws Exception {
        IDBCommand cmd = null;
        try {
            cmd = DBHELPER.getCommand(con, getEntryCodeAndNameSQL());
            cmd.setParam("table_name", code);
            if (cmd.executeQuery() && cmd.next()) {
                json.put("v", cmd.getLong(2));
                json.put("c", code);
                json.put("n", cmd.getString(1));
            }

        } finally {
            DBHELPER.closeCommand(cmd);
        }
    }

    /**
     * 生成Columns信息
     * 
     * @param con
     * @param json
     * @param code
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    protected void getEntryColumns(final Connection con, final JSONObject json, final String code) throws Exception, DBException {
        final ArrayList<JSONObject> columns = new ArrayList<JSONObject>();
        final HashMap<String, JSONObject> code2ColumnJSON = new HashMap<String, JSONObject>();
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, getDBEntryColumnsSQL(), true);
            stmt.setParam(1, code);
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject column_json = new JSONObject();
                String fieldName = stmt.getString(1);
                column_json.put("c", fieldName);
                final DBFieldDataType dataType = getDBFieldDataTypeByTypeString(stmt.getString(2));
                column_json.put("type", dataType.getTypeName());
                final long dataLength = stmt.getLong(3);
                column_json.put("length", dataType.isFixedSize() ? 0 : dataLength);
                column_json.put("unnullable", stmt.getInt(4) == 1);
                column_json.put("n", stmt.getString(5));
                final String defaultValue = Convert.bytes2Str(stmt.getBytes(6));
                column_json.put("default", !StringUtils.isEmpty(defaultValue) ? defaultValue : "");
                columns.add(column_json);
                code2ColumnJSON.put(fieldName, column_json);
            }
            DBHELPER.closeCommand(stmt);
            stmt = null;

            stmt = DBHELPER.getCommand(con, getDBEntryConstraintSQL(), true);
            stmt.setParam(1, code);
            stmt.executeQuery();
            while (stmt.next()) {
                final String column_name = stmt.getString(2);
                final JSONObject column_json = code2ColumnJSON.get(column_name);
                if (column_json != null) {
                    column_json.put("primary", StringUtils.equalsIgnoreCase("p", stmt.getString(3)));
                    column_json.put("unique", StringUtils.equalsIgnoreCase("u", stmt.getString(3)));
                }
            }

            json.put("r", true);
        } finally {
            DBHELPER.closeCommand(stmt);
        }
        json.put("cc", columns);
    }

    /**
     * 生成模型与实体的对应关系
     * 
     * @param json
     * @param con
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    protected void generalDBEntry2Diagrams(final JSONObject json, final Connection con) throws Exception, DBException {
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, getDiagramDBEntrySQL(), true);
            String diagramName = "";
            String dbEntryName = "";
            stmt.executeQuery();
            while (stmt.next()) {
                if (!StringUtils.equals(diagramName, stmt.getString(1))) {
                    if (!StringUtils.isEmpty(diagramName)) {
                        final JSONObject diagramJSON = new JSONObject();
                        diagramJSON.put("diagram", diagramName);
                        diagramJSON.put("dbentrys", dbEntryName);
                        JSONUtils.append(json, "diagrams", diagramJSON);
                        // json.append("diagrams", diagramJSON);
                        dbEntryName = "";
                    }
                    diagramName = stmt.getString(1);
                }
                dbEntryName += (StringUtils.isEmpty(dbEntryName) ? "" : ",") + stmt.getString(2);
            }

            if (!StringUtils.isEmpty(diagramName)) {
                final JSONObject diagramJSON = new JSONObject();
                diagramJSON.put("diagram", diagramName);
                diagramJSON.put("dbentrys", dbEntryName);
                JSONUtils.append(json, "diagrams", diagramJSON);
                // json.append("diagrams", diagramJSON);
            }
        } finally {
            DBHELPER.closeCommand(stmt);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getAllEntrys(java.lang
     * .String, boolean)
     */
    @Override
    public JSONObject getAllEntrys(final String code2VersionStr, final boolean includeDiagrams) throws Exception, DBException {
        final JSONObject json = new JSONObject();

        final HashMap<String, Long> code2Version = new HashMap<String, Long>();
        if (!StringUtils.isEmpty(code2VersionStr)) {
            final String[] code2VersionList = code2VersionStr.split(";");
            for (final String element : code2VersionList) {
                final String code = StringUtils.substringBefore(element, "=");
                final String version = StringUtils.substringAfter(element, "=");
                code2Version.put(code, Convert.str2Long(version));
            }
        }

        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            getEntrysJSON(con);
            for (int i = 0; i < cacheEntryArrayList.size(); i++) {
                final JSONObject entryJSON = cacheEntryArrayList.get(i);
                final String code = entryJSON.getString("c");
                final long version = code2Version.containsKey(code) ? code2Version.get(code) : -1;
                // json.append("items", this.getEntryInfo(con, code, version));
                JSONUtils.append(json, "items", this.getEntryInfo(con, code, version));
            }

            if (includeDiagrams)
                generalDBEntry2Diagrams(json, con);
            json.put("r", true);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getDDLVersion(java.
     * sql.Connection)
     */
    @Override
    public long getDDLVersion(final Connection con) throws Exception {
        long result = -1;
        Connection tempCon = con;
        Statement stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = tempCon.createStatement();
            final ResultSet resultSet = stmt.executeQuery(getDDLVersionSQL());
            if (resultSet.next())
                result = resultSet.getLong(1);

        } finally {
            DBHELPER.closeStatement(stmt);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getEntrysJSON(java.
     * sql.Connection)
     */
    @Override
    public List<JSONObject> getEntrysJSON(final Connection con) throws Exception {
        final long version = getDDLVersion(con);
        if (version != ddlVersion) {
            ddlVersion = version;
            cacheEntryArrayList.clear();
            IDBCommand stmt = null;
            try {
                stmt = DBHELPER.getCommand(con, getDBEntrysSQL(), true);
                stmt.executeQuery();
                while (stmt.next())
                    cacheEntryArrayList.add(new DBEntry(stmt.getString(2), stmt.getString(1)).toJSON());
            } finally {
                DBHELPER.closeCommand(stmt);
            }
        }
        return cacheEntryArrayList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getEntryInfo(java.lang
     * .String, long)
     */
    @Override
    public JSONObject getEntryInfo(final String code, final long version) {
        return getEntryInfo(null, code, version);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getEntryInfos(java.
     * sql.Connection, java.util.HashMap)
     */
    @Override
    public JSONObject getEntryInfos(final Connection con, final HashMap<String, Integer> code2Version) {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final Iterator<String> iterator = code2Version.keySet().iterator();
            while (iterator.hasNext()) {
                final String code = iterator.next();
                json.put(code, this.getEntryInfo(con, code, code2Version.get(code)));
            }
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            JSONUtils.except2JSON(json, e);
        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getEntryInfos(java.
     * sql.Connection, java.lang.String)
     */
    @Override
    public JSONObject getEntryInfos(final Connection con, final String codes) {
        final JSONObject json = new JSONObject();
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final String[] cs = codes.split(";");
            for (final String element : cs)
                json.put(element, this.getEntryInfo(element, -1));
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            JSONUtils.except2JSON(json, e);
        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getEntryInfo(java.sql
     * .Connection, java.lang.String, long)
     */
    @Override
    public JSONObject getEntryInfo(final Connection con, final String code, final long version) {
        JSONObject json = new JSONObject();
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final long serverVersion = this.getDBEntryVersion(tempCon, code);
            if (serverVersion == -1) {
                JSONUtils.except2JSON(json, new Exception("数据库实体:" + code + "不存在！"));
            } else if (version != serverVersion) {
                DBEntryCacheItem cacheItem = (DBEntryCacheItem) SystemCacheManager.getInstance().getDesignObject("DBENTRY-" + code);
                if ((cacheItem == null) || (cacheItem.getVersion() != serverVersion)) {
                    getEntryCodeAndName(tempCon, json, code);
                    getEntryColumns(tempCon, json, code);
                    getEntryIndexs(tempCon, json, code);
                    if (cacheItem == null) {
                        cacheItem = new DBEntryCacheItem(serverVersion, json);
                        SystemCacheManager.getInstance().putDesignObject("DBENTRY-" + code, cacheItem);
                    } else {
                        cacheItem.setJson(json);
                        cacheItem.setVersion(serverVersion);
                    }
                }
                json = cacheItem.getJson();
                json.put("r", true);
            } else {
                json.put("r", true);
                json.put("v", serverVersion);
                json.put("c", code);
            }

        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            JSONUtils.except2JSON(json, e);
        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /**
     * 读取索引项
     * 
     * @param tempCon
     * @param json
     * @param code
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     * @throws DBException
     */
    private void getEntryIndexs(final Connection con, final JSONObject json, final String code) throws Exception, DBException {
        final ArrayList<JSONObject> idxs = new ArrayList<JSONObject>();
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, getDBEntryIndexSQL());
            stmt.setParam(1, code);
            stmt.executeQuery();
            while (stmt.next()) {
                final String indexName = stmt.getString(1);
                final String columns = StringUtils.replace(stmt.getString(2), ",", ";");
                final JSONObject idx_json = new JSONObject();
                idx_json.put("n", indexName);
                idx_json.put("cls", columns);
                idxs.add(idx_json);
            }

        } finally {
            DBHELPER.closeCommand(stmt);
        }
        json.put("idxs", idxs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getDBEntryLinks(java
     * .sql.Connection, long)
     */
    @Override
    public JSONObject getDBEntryLinks(final Connection con, final long version) {
        JSONObject json = new JSONObject();
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            final long serverVersion = getDDLVersion(tempCon);
            if (((serverVersion != -1) && (serverVersion != version))) {
                if (ddlLinkVersion == serverVersion)
                    json = cacheLinkJSONCache;
                else {
                    json.put("ls", getDBEntryLinksList(tempCon));
                    json.put("r", true);
                    json.put("v", serverVersion);
                    ddlLinkVersion = serverVersion;
                    cacheLinkJSONCache = json;
                }
            } else if (serverVersion == -1)
                JSONUtils.except2JSON(json, new Exception("无法读取数据库信息！"));
            else {
                json.put("r", true);
                json.put("v", serverVersion);
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e, con);
            JSONUtils.except2JSON(json, e);
        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getDBEntryLinksList
     * (java.sql.Connection)
     */
    @Override
    public ArrayList<JSONObject> getDBEntryLinksList(final Connection con) throws Exception, DBException {
        final ArrayList<JSONObject> linksArray = new ArrayList<JSONObject>();
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, getDBEntryForeignKeys(), true);
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject link_json = new JSONObject();
                link_json.put("n", stmt.getString(1));
                link_json.put("pc", stmt.getString(2));
                link_json.put("pf", stmt.getString(3));
                link_json.put("cc", stmt.getString(4));
                link_json.put("cf", stmt.getString(5));
                linksArray.add(link_json);
            }

            // 生成缓存
            ddlLinkVersion = ddlVersion;
            cacheLinkJSONCache = new JSONObject();
            cacheLinkJSONCache.put("ls", linksArray);
            cacheLinkJSONCache.put("r", ddlLinkVersion);

        } finally {
            DBHELPER.closeCommand(stmt);
        }
        return linksArray;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.db.oracle.IDBEntryService#getDBEntryVersion(java
     * .sql.Connection, java.lang.String)
     */
    @Override
    public long getDBEntryVersion(final Connection con, final String code) throws Exception {
        long result = -1;
        IDBCommand stmt = null;
        try {
            stmt = DBHELPER.getCommand(con, getDBEntryVersionSQL());
            stmt.setParam(1, code);
            stmt.executeQuery();
            if (stmt.next())
                result = stmt.getLong(1);
        } finally {
            DBHELPER.closeCommand(stmt);
        }
        return result;
    }

    /**
     * 执行SQL语句
     * 
     * @param stmt
     * @param ddlSQL
     * @param errorSB
     */
    protected boolean exeSQL(final Statement stmt, final String ddlSQL, final StringBuilder errorSB) {
        boolean result = true;
        try {
            stmt.execute(ddlSQL);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            errorSB.append(e.getMessage()).append("\n");
            result = false;
            ExceptionUtils.printExceptionTrace(e);
        }
        return result;
    }

    /**
     * 清除DDL缓存信息
     */
    protected void incDDLVersion(final Statement stmt) {
        try {
            stmt.execute(getIncDDLVersionSQL());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    @Override
    public DBFieldDataType getDBFieldDataTypeByTypeString(final String dataType) {
        DBFieldDataType result = supportDBFieldDataTypes.get(0);
        for (int i = 1; i < supportDBFieldDataTypes.size(); i++) {
            final DBFieldDataType temp = supportDBFieldDataTypes.get(i);
            if (StringUtils.equalsIgnoreCase(dataType, temp.getTypeName())) {
                result = temp;
                break;
            }
        }
        return result;
    }

    @Override
    public long getColumnDataTypeIndex(final String dataType) {
        long result = 0;
        for (int i = 1; i < supportDBFieldDataTypes.size(); i++) {
            final DBFieldDataType temp = supportDBFieldDataTypes.get(i);
            if (StringUtils.equalsIgnoreCase(dataType, temp.getTypeName())) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 根据数据类型字符串及长度获取定义数据类型的SQL格式
     * 
     * @param typeStr
     * @param length
     * @return
     */
    protected String getDataTypeStr(final String typeStr, final long length) {
        String result = typeStr;
        if (length != 0)
            for (int i = 0; i < supportDBFieldDataTypes.size(); i++) {
                final DBFieldDataType temp = supportDBFieldDataTypes.get(i);
                if (StringUtils.equalsIgnoreCase(typeStr, temp.getTypeName())) {
                    if (!temp.isFixedSize())
                        result = result + "(" + length + ")";
                    break;
                }
            }
        return result;
    }

    @Override
    public JSONObject getDBEntryPchJson() throws Exception {
        String content = RuntimeContext.getDBConfig().getConfig("dbentry", "pchjson", DBHELPER);
        if (StringUtils.isEmpty(content))
            content = "{fields:[]}";
        return JSONUtils.parserJSONObject(content);
    }

    @Override
    public JSONObject saveDBEntryPchJson(String str) throws Exception {
        RuntimeContext.getDBConfig().saveConfig("dbentry", "pchjson", str, DBHELPER);
        return JSONUtils.parserJSONObject("{r:true}");
    }

    @Override
    public JSONObject getLex(String paramStr) throws Exception {
        String fileName = this.getClass().getResource("").getPath() + this.getLexFileName();
        JSONObject json = new JSONObject();
        json.put("lex", SecurityUtils.encodeBae64(FileUtils.readFileToByteArray(new File(fileName))));
        json.put("r", true);
        return json;
    }

    /**
     * 预定义字段信息
     * 
     * @return
     */
    protected abstract String getDBEntryPchFileName();
}
