package com.estudio.impl.design.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class DBDesignUtilsService {

    public static void main(String[] args) {
        List<Long> list = new ArrayList<Long>();
        list.add(1l);
        list.add(2l);
    }

    private DBDesignUtilsService() {
        String tableDefine = "{";
        tableDefine += "sys_object_tree:{ID:1,CAPTION:0,TYPE:1,VERSION:1,SORTORDER:1,PID:1,PROP_ID:1,MEMO:4},";
        tableDefine += "sys_object_forms:{ID:1,DFMSTREAM:4,XMLSTREAM:4,DATASOURCE:4,JSSCRIPT:4,VERSION:1,TYPE:1,FORM_PARAMS:0},";
        tableDefine += "sys_object_report:{ID:1,CONTENT:4,VERSION:1,REPORT_PARAMS:0,TEMPLATE:4},";
        tableDefine += "sys_object_query:{ID:1,CONTENT:4,VERSION:1},";
        tableDefine += "sys_workflow_d_process:{ID:1,NAME:0,DESCRIPT:0,STATUS:0,VERSION:1,CREATEDATE:3,LASTMODIFYDATE:3,DFM:4,PROPERTY:4,LIMIT_NUM:1,LIMIT_UNIT:0},";
        tableDefine += "sys_portal_group:{ID:1,NAME:0,SORTORDER:1,MEMO:4,PUBLISHED:1,ICON:0},";
        tableDefine += "sys_portal_item:{DISABLECLOSE:1,ID:1,P_ID:1,NAME:0,SORTORDER:1,TYPE:1,VERSION:1,PROPERTY:4,ICON:0,WIN:1,AUTORUN:1,ISHIDDEN:1}";
        tableDefine += "}";
        tableColumnInfos = JSONUtils.parserJSONObject(tableDefine);
    }

    public static final DBDesignUtilsService instance = new DBDesignUtilsService();
    private IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private JSONObject tableColumnInfos = null;

    /**
     * 导出定义
     * 
     * @param paramLongs
     * @param paramInt
     * @return
     * @throws Exception
     */
    public JSONObject exportDesign(long[] ids, int type) throws Exception {
        JSONObject json = new JSONObject();
        json.put("type", type);
        if (type == 0)
            exportDesign4Object(json, ids);
        else
            exportDesign4Portal(json, ids);
        return json;
    }

    /**
     * 
     * @param json
     * @param ids
     * @throws Exception
     */
    private void exportDesign4Portal(JSONObject json, long[] ids) throws Exception {
        Connection con = null;
        Statement detailStmt = null;
        try {
            con = DBHELPER.getConnection();
            detailStmt = con.createStatement();
            List<Long> idss = new ArrayList<Long>();
            for (long id : ids)
                idss.add(id);
            String idsStr = StringUtils.join(idss, ",");
            json.put("sys_portal_item", DBHELPER.resultSet2JSONArray(detailStmt.executeQuery("select * from sys_portal_item where id in (" + idsStr + ")"), true));
            json.put("sys_portal_group", DBHELPER.resultSet2JSONArray(detailStmt.executeQuery("select * from sys_portal_group where id in (select p_id from sys_portal_item where id in (" + idsStr + "))"), true));
        } finally {
            DBHELPER.closeStatement(detailStmt);
            DBHELPER.closeConnection(con);
        }

    }

    /**
     * 
     * @param json
     * @param ids
     * @throws Exception
     */
    private void exportDesign4Object(JSONObject json, long[] ids) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        Statement detailStmt = null;
        try {
            con = DBHELPER.getConnection();
            detailStmt = con.createStatement();
            stmt = con.prepareStatement("select * from sys_object_tree");
            ResultSet rs = stmt.executeQuery();
            Map<Integer, List<Long>> type2List = new HashMap<Integer, List<Long>>();
            Map<Long, JSONObject> id2Json = new HashMap<Long, JSONObject>();
            Map<Long, Long> id2pid = new HashMap<Long, Long>();
            while (rs.next()) {
                JSONObject record = DBHELPER.resultSet2JSONObject(rs);
                id2Json.put(rs.getLong("id"), record);
                id2pid.put(rs.getLong("id"), rs.getLong("pid"));
            }

            List<Long> OKList = new java.util.ArrayList<Long>();
            List<JSONObject> folderList = new ArrayList<JSONObject>();
            for (long id : ids) {
                OKList.add(id);
                folderList.add(id2Json.get(id));
                Integer type = id2Json.get(id).getInt("TYPE");
                List<Long> typeList = type2List.get(type);
                if (typeList == null) {
                    typeList = new ArrayList<Long>();
                    type2List.put(type, typeList);
                }
                typeList.add(id);
                long pid = id2pid.get(id);
                while (pid != -1) {
                    if (OKList.indexOf(pid) == -1) {
                        OKList.add(pid);
                        folderList.add(id2Json.get(pid));
                    }
                    pid = id2pid.get(pid);
                }
            }
            json.put("sys_object_tree", folderList);
            Map<Integer, String> type2TableName = new HashMap<Integer, String>();
            type2TableName.put(4, "sys_object_forms");
            type2TableName.put(7, "sys_object_report");
            type2TableName.put(9, "sys_object_query");
            type2TableName.put(11, "sys_workflow_d_process");
            for (Entry<Integer, List<Long>> entry : type2List.entrySet()) {
                String tableName = type2TableName.get(entry.getKey());
                String SQL = "select * from " + tableName + " where id in (" + StringUtils.join(entry.getValue(), ",") + ")";
                rs = detailStmt.executeQuery(SQL);
                json.put(tableName, DBHELPER.resultSet2JSONArray(rs, true));
            }
        } finally {
            DBHELPER.closeStatement(detailStmt);
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
    }

    /**
     * 导入定义
     * 
     * @param paramStr
     * @param paramInt
     * @return
     * @throws Exception
     */
    public JSONObject importDesign(String params, int type) throws Exception {
        JSONObject json = new JSONObject();
        if (type == 0)
            importDesign4Object(JSONUtils.parserJSONObject(params));
        else
            importDesign4Portal(JSONUtils.parserJSONObject(params));
        return json;
    }

    /**
     * 
     * @param parserJSONObject
     * @throws Exception
     */
    private void importDesign4Portal(JSONObject recordJson) throws Exception {
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            importTables(con, recordJson, new String[] { "sys_portal_group", "sys_portal_item" });
        } finally {
            DBHELPER.closeConnection(con);
        }
    }

    /**
     * 
     * @param con
     * @param json
     * @param tables
     * @throws SQLException
     */
    private void importTables(Connection con, JSONObject json, String[] tables) throws SQLException {
        boolean isFistTable = true;
        for (String tableName : tables) {
            if (!json.containsKey(tableName)) {
                isFistTable = false;
                continue;
            }

            List<String> fieldList = new ArrayList<String>();
            JSONObject tableColumnInfo = tableColumnInfos.getJSONObject(tableName);
            fieldList.addAll(tableColumnInfo.keySet());

            PreparedStatement cstmt = null;
            PreparedStatement istmt = null;
            PreparedStatement ustmt = null;
            try {
                String csql = "select count(*) from " + tableName + " where id=?";
                cstmt = con.prepareStatement(csql);

                // 插入SQL
                String isql = "insert into " + tableName + " (";
                isql += StringUtils.join(fieldList, ",");
                isql += ") values (";
                for (int i = 0; i < fieldList.size(); i++) {
                    isql += "?";
                    if (i != fieldList.size() - 1)
                        isql += ",";
                }
                isql += ")";
                istmt = con.prepareStatement(isql);

                // 更新SQL
                String usql = "update " + tableName + " set ";
                for (int i = 0; i < fieldList.size(); i++) {
                    usql += fieldList.get(i) + "=?";
                    if (i != fieldList.size() - 1)
                        usql += ",";
                }
                usql += " where id=?";
                ustmt = con.prepareStatement(usql);

                JSONArray records = json.getJSONArray(tableName);
                for (int i = 0; i < records.size(); i++) {
                    JSONObject recordJson = records.getJSONObject(i);
                    long id = recordJson.getLong("ID");
                    cstmt.setLong(1, id);
                    ResultSet rs = cstmt.executeQuery();
                    rs.next();
                    boolean existsRecord = rs.getInt(1) != 0;
                    if (!existsRecord) {
                        setStatementParams(istmt, recordJson, tableColumnInfo, fieldList, false);
                        istmt.execute();
                    } else if (!isFistTable) {
                        setStatementParams(ustmt, recordJson, tableColumnInfo, fieldList, true);
                        ustmt.execute();
                    }
                }

            } finally {
                DBHELPER.closeStatement(cstmt);
                DBHELPER.closeStatement(istmt);
                DBHELPER.closeStatement(ustmt);
            }
            isFistTable = false;
        } // end for
    }

    private void setStatementParams(PreparedStatement stmt, JSONObject recordJson, JSONObject tableColumnInfo, List<String> fieldList, boolean includeWhere) throws SQLException {
        for (int index = 1; index <= fieldList.size(); index++) {
            String fieldName = fieldList.get(index - 1);
            String value = recordJson.getString(fieldName);
            if (StringUtils.isEmpty(value))
                stmt.setObject(index, null);
            else {
                int dataType = tableColumnInfo.getInt(fieldName);
                if (dataType == 0)
                    stmt.setString(index, recordJson.getString(fieldName));
                else if (dataType == 1)
                    stmt.setLong(index, recordJson.getLong(fieldName));
                else if (dataType == 2)
                    stmt.setLong(index, recordJson.getLong(fieldName));
                else if (dataType == 3)
                    stmt.setDate(index, Convert.date2SQLDate(Convert.str2Date(recordJson.getString(fieldName))));
                else if (dataType == 4)
                    stmt.setBytes(index, Convert.base64ToBytes(recordJson.getString(fieldName)));
            }
        }
        if (includeWhere)
            stmt.setLong(fieldList.size() + 1, recordJson.getLong("ID"));
    }

    /**
     * 
     * @param parserJSONObject
     * @throws Exception
     */
    private void importDesign4Object(JSONObject recordJson) throws Exception {
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            importTables(con, recordJson, new String[] { "sys_object_tree", "sys_object_forms", "sys_object_report", "sys_object_query", "sys_workflow_d_process" });
        } finally {
            DBHELPER.closeConnection(con);
        }
    }

}
