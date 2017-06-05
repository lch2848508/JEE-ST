package com.estudio.web.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.dom4j.DocumentException;

import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.define.webclient.SQLParamUtils;
import com.estudio.define.webclient.portal.AbstractPortalGridDefine;
import com.estudio.define.webclient.portal.SQLDefineBase;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.JSONUtils;

public class DataService4AbstractPortal {

    protected static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    protected static final DataService4AbstractPortal INSTANCE = new DataService4AbstractPortal();

    /**
     * 查询语句
     * 
     * @param con
     * @param sqlDefine
     * @param params
     * @return
     * @throws Exception
     */
    public JSONObject executeSQLDefine(final Connection con, final SQLDefineBase sqlDefine, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        IDBCommand cmd = null;
        try {
            cmd = sqlDefine.getCmd(con, params);
            for (int i = 0; i < sqlDefine.getParamCount(); i++) {
                final SQLParam4Portal param = sqlDefine.getParam(i);
                final String value = params.get(param.getName());
                SQLParamUtils.setParam(cmd, param.getDataType(), param.getName(), value);
            }
            cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
        }
        return json;
    }

    /**
     * 执行SQL语句定义
     * 
     * @param portal_id
     * @param sqlName
     * @param params
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws DBException
     */
    public JSONObject executeSQL(final long portal_id, final String sqlName, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand cmd = null;
        try {
            final AbstractPortalGridDefine portalGridDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
            final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByName(sqlName);
            if (sqlDefine == null) {
                json.put("r", false);
                json.put("msg", "服务器断尚未定义SQL语句,操作失败！");
            } else {
                con = DBHELPER.getConnection();
                cmd = sqlDefine.getCmd(con, null);
                for (int i = 0; i < sqlDefine.getParamCount(); i++) {

                    final SQLParam4Portal param = sqlDefine.getParam(i);
                    final String value = params.get(param.getName());
                    SQLParamUtils.setParam(cmd, param, value);
                }
                cmd.execute();
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 执行SQL语句定义
     * 
     * @param portal_id
     * @param sqlName
     * @param params
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws DBException
     */
    public JSONObject batchExecuteSQL(final long portal_id, final String sqlName, final Map<String, String> params) throws Exception {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand cmd = null;
        try {
            con = DBHELPER.getConnection();
            final AbstractPortalGridDefine portalGridDefine = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(portal_id, con);
            final SQLDefineBase sqlDefine = portalGridDefine.getSQLDefineByName(sqlName);
            if (sqlDefine == null) {
                json.put("r", false);
                json.put("msg", "服务器断尚未定义SQL语句,操作失败！");
            } else {
                con.setAutoCommit(false);
                cmd = sqlDefine.getCmd(con, null);
                JSONArray paramArray = JSONUtils.parserJSONArray(params.get("params"));
                for (int i = 0; i < paramArray.size(); i++) {
                    JSONObject paramJson = paramArray.getJSONObject(i);
                    for (int j = 0; j < sqlDefine.getParamCount(); j++) {
                        final SQLParam4Portal param = sqlDefine.getParam(j);
                        final String value = paramJson.getString(param.getName());
                        SQLParamUtils.setParam(cmd, param, value);
                    }
                    cmd.execute();
                }
                con.commit();
                json.put("r", true);
            }
        } finally {
            if (con != null) {
                con.rollback();
                con.setAutoCommit(false);
            }
            DBHELPER.closeCommand(cmd);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    private DataService4AbstractPortal() {
        super();
    }

    public static DataService4AbstractPortal getInstance() {
        return INSTANCE;
    }

}