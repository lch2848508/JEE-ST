package com.estudio.impl.design.utils;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.db.DBFieldDataType;
import com.estudio.impl.db.DBSqlUtils;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.utils.ISQLParserService;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

public abstract class DBSQLParserService implements ISQLParserService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    public DBSQLParserService() {
        super();
    }

    protected abstract Map<String, String> getTablesFieldComment(Connection con, String[] tables) throws Exception;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.utils.ISQLParserService#parser(java.lang.String,
     * java.lang.String[])
     */
    @Override
    public JSONObject parser(final String sqlStr, final String[] tables, final String paramDataTypes) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        String sql = sqlStr;
        if (!StringUtils.isEmpty(sql)) {
            final String initSQL = StringUtils.trim(DBSqlUtils.deleteComment(StringUtils.substringBetween(sql, "/**BEGIN_INITIALIZE_SQL**/", "/**END_INITIALIZE_SQL**/")));
            final String cleanSQL = StringUtils.trim(DBSqlUtils.deleteComment(StringUtils.substringBetween(sql, "/**BEGIN_CLEAN_SQL**/", "/**END_CLEAN_SQL**/")));
            sql = StringUtils.replace(StringUtils.replace(sql, initSQL, ""), cleanSQL, "");
            sql = StringUtils.trim(DBSqlUtils.deleteComment(sql));

            Connection con = null;
            IDBCommand cmd = null;
            IDBCommand initCmd = null;
            IDBCommand cleanCmd = null;
            try {
                con = DBHELPER.getConnection();
                final Map<String, String> fieldName2Comment = getTablesFieldComment(con, tables);
                final HashMap<String, DBParamDataType> fieldName2DataType = new HashMap<String, DBParamDataType>();
                if (!StringUtils.isEmpty(paramDataTypes)) {
                    final String[] strs = StringUtils.split(paramDataTypes, '|');
                    for (int i = 0; i < strs.length; i += 2)
                        fieldName2DataType.put(strs[i].toUpperCase(), DBParamDataType.fromInt(Convert.try2Int(strs[i + 1], 0)));
                }

                // 初始化
                if (!StringUtils.isEmpty(initSQL)) {
                    initCmd = DBHELPER.getCommand(con, initSQL);
                    initCmdParamValues(initCmd, fieldName2DataType);
                    initCmd.execute();
                }

                // 查询
                cmd = DBHELPER.getCommand(con, sql);
                initCmdParamValues(cmd, fieldName2DataType);
                cmd.executeQuery();

                final ResultSetMetaData metaData = cmd.getResultSetMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    final JSONObject columnJSON = new JSONObject();
                    final String columnName = metaData.getColumnLabel(i).toUpperCase();
                    columnJSON.put("name", columnName);
                    final String columnTypeName = metaData.getColumnTypeName(i);
                    final DBFieldDataType dataType = RuntimeContext.getDbEntryService().getDBFieldDataTypeByTypeString(columnTypeName);
                    columnJSON.put("type", dataType.getTypeName());
                    columnJSON.put("size", dataType.isFixedSize() ? 0 : metaData.getColumnDisplaySize(i));
                    columnJSON.put("comment", fieldName2Comment.get(columnName));
                    JSONUtils.append(json, "columns", columnJSON);
                    // json.append("columns", columnJSON);
                }

                // 清理
                if (!StringUtils.isEmpty(cleanSQL)) {
                    cleanCmd = DBHELPER.getCommand(con, cleanSQL);
                    initCmdParamValues(cleanCmd, fieldName2DataType);
                    cleanCmd.execute();
                }

                json.put("r", true);
            } finally {
                DBHELPER.closeCommand(cmd);
                DBHELPER.closeCommand(cleanCmd);
                DBHELPER.closeCommand(initCmd);
                DBHELPER.closeConnection(con);
            }
        }
        return json;
    }

    private void initCmdParamValues(final IDBCommand cmd, final HashMap<String, DBParamDataType> fieldName2DataType) throws Exception {
        final Iterator<String> cmdParams = cmd.getParams().iterator();
        while (cmdParams.hasNext()) {
            final String paramName = cmdParams.next().toUpperCase();
            DBParamDataType paramDataType = DBParamDataType.String;
            if (fieldName2DataType.containsKey(paramName))
                paramDataType = fieldName2DataType.get(paramName);
            if ((paramDataType == DBParamDataType.Date) || (paramDataType == DBParamDataType.DateTime) || (paramDataType == DBParamDataType.Time) || (paramDataType == DBParamDataType.Timestampe))
                cmd.setParam(paramName, Calendar.getInstance().getTime());
            else if ((paramDataType == DBParamDataType.Double) || (paramDataType == DBParamDataType.Float) || (paramDataType == DBParamDataType.Long) || (paramDataType == DBParamDataType.Int))
                cmd.setParam(paramName, -1);
            else cmd.setParam(paramName, "-1");
        }
    }
}
