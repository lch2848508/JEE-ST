package com.estudio.impl.design.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.user.IUserManagerService;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public abstract class DBUserManagerService implements IUserManagerService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected DBUserManagerService() {
        super();
    }

    protected abstract String getRoleTypesSQL();

    protected abstract String getRolesInfoSQL();

    protected abstract String getSysUserInfosByDepartmentIdSQL();

    protected abstract String getDepartmentsByParentDepartmentIdSQL();

    protected abstract String getDepartmentRootIdSQL();

    protected abstract String getSystemRoleSQL();

    /**
     * 加载部门及部门下的用户列表
     * 
     * @param stmt
     * @param userStmt
     * @param pid
     * @param json
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private void getDepartmentAndUsers(final IDBCommand stmt, final IDBCommand userStmt, final long pid, final JSONObject json) throws Exception {
        final ArrayList<Long> ids = new ArrayList<Long>();
        final ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
        stmt.setParam(1, pid);
        stmt.executeQuery();
        while (stmt.next()) {
            final JSONObject json_d = new JSONObject();
            json_d.put("id", stmt.getLong(1));
            json_d.put("n", stmt.getString(2));
            ids.add(stmt.getLong(1));
            jsons.add(json_d);

            final ArrayList<JSONObject> ujsons = new ArrayList<JSONObject>();
            userStmt.setParam(1, stmt.getLong(1));
            userStmt.executeQuery();
            while (userStmt.next()) {
                final JSONObject json_u = new JSONObject();
                json_u.put("id", userStmt.getLong(1));
                json_u.put("n", userStmt.getString(2));
                ujsons.add(json_u);
            }
            if (ujsons.size() != 0)
                json_d.put("us", ujsons);
        }
        if (jsons.size() != 0) {
            json.put("ds", jsons);
            for (int i = 0; i < ids.size(); i++)
                getDepartmentAndUsers(stmt, userStmt, ids.get(i), jsons.get(i));
        }
    }

    /**
     * 根据用户ID取得可管理部门的跟节点
     * 
     * @param stmt
     * @param userID
     * @return
     * @throws SQLException
     *             , DBException
     */
    private long getDepartmentRootIDByUserID(final IDBCommand stmt, final long userID) throws Exception {
        stmt.setParam(1, userID);
        stmt.executeQuery();
        if (stmt.next())
            return stmt.getLong(1);
        else return Long.MIN_VALUE;
    }

    /**
     * 读取部门列表
     * 
     * @param stmt
     * @param pid
     * @param json
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    private void getDepartments(final IDBCommand stmt, final long pid, final JSONObject json) throws Exception {
        final ArrayList<Long> ids = new ArrayList<Long>();
        final ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
        stmt.setParam(1, pid);
        stmt.executeQuery();
        while (stmt.next()) {
            final JSONObject json_d = new JSONObject();
            json_d.put("id", stmt.getLong(1));
            json_d.put("n", stmt.getString(2));
            ids.add(stmt.getLong(1));
            jsons.add(json_d);
        }
        if (jsons.size() != 0) {
            json.put("ds", jsons);
            for (int i = 0; i < ids.size(); i++)
                getDepartments(stmt, ids.get(i), jsons.get(i));
        }
    }

    @Override
    public JSONObject getDepartmentsAndRoles(final long parentId, final long userID) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        long pid = parentId;
        try {
            con = DBHELPER.getConnection();

            stmt = DBHELPER.getCommand(con, getDepartmentRootIdSQL(), true);
            pid = getDepartmentRootIDByUserID(stmt, userID);
            DBHELPER.closeCommand(stmt);
            stmt = null;

            stmt = DBHELPER.getCommand(con, getDepartmentsByParentDepartmentIdSQL(), true);
            getDepartments(stmt, pid, json);
            DBHELPER.closeCommand(stmt);
            stmt = null;

            getRoleTypeAndRoles(userID, json, -1l, con);

            json.put("d_root_id", pid);
            json.put("r", true);
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
     * 获取角色类型列表及角色列表
     * 
     * @param userID
     * @param json
     * @param l
     * @param con
     * @throws Exception
     */
    private void getRoleTypeAndRoles(final long userID, final JSONObject json, final long p_id, final Connection con) throws Exception {
        IDBCommand typeStmt = null;
        IDBCommand roleStmt = null;
        try {
            typeStmt = DBHELPER.getCommand(con, getRoleTypesSQL(), true);
            typeStmt.setParam(1, userID);
            typeStmt.executeQuery();
            final ArrayList<JSONObject> roleTypeList = new ArrayList<JSONObject>();
            final HashMap<String, JSONObject> roleTypeId2JSON = new HashMap<String, JSONObject>();
            while (typeStmt.next()) {
                final JSONObject typeJson = new JSONObject();
                typeJson.put("id", typeStmt.getString(1));
                typeJson.put("n", typeStmt.getString(2));
                roleTypeId2JSON.put(typeStmt.getString(1), typeJson);
                roleTypeList.add(typeJson);
            }

            roleStmt = DBHELPER.getCommand(con, getRolesInfoSQL(), true);
            roleStmt.setParam(1, userID);
            roleStmt.executeQuery();
            while (roleStmt.next()) {
                final JSONObject roleJson = new JSONObject();
                roleJson.put("id", roleStmt.getString(1));
                roleJson.put("n", roleStmt.getString(2));
                final String typeId = roleStmt.getString(4);
                if (!StringUtils.isEmpty(typeId) && roleTypeId2JSON.containsKey(typeId))
                    JSONUtils.append(roleTypeId2JSON.get(typeId), "rs", roleJson);
                // roleTypeId2JSON.get(typeId).append("rs", roleJson);
                else JSONUtils.append(json, "rs", roleJson);
                // json.append("rs", roleJson);
            }

            json.put("ts", roleTypeList);
        } finally {
            DBHELPER.closeCommand(roleStmt);
            DBHELPER.closeCommand(typeStmt);
        }
    }

    @Override
    public JSONObject getDepartmentsAndUsers(final long parentId, final long userID) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        IDBCommand stmt = null;
        IDBCommand userStmt = null;
        long pid = parentId;
        try {
            con = DBHELPER.getConnection();

            stmt = DBHELPER.getCommand(con, getDepartmentRootIdSQL(), true);
            pid = getDepartmentRootIDByUserID(stmt, userID);
            stmt.close();
            stmt = null;

            userStmt = DBHELPER.getCommand(con, getSysUserInfosByDepartmentIdSQL(), true);
            stmt = DBHELPER.getCommand(con, getDepartmentsByParentDepartmentIdSQL(), true);
            getDepartmentAndUsers(stmt, userStmt, pid, json);

            final ArrayList<JSONObject> ujsons = new ArrayList<JSONObject>();
            userStmt.setParam(1, pid);
            userStmt.executeQuery();
            while (userStmt.next()) {
                final JSONObject json_u = new JSONObject();
                json_u.put("id", userStmt.getLong(1));
                json_u.put("n", userStmt.getString(2));
                ujsons.add(json_u);
            }
            if (ujsons.size() != 0)
                json.put("us", ujsons);

            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);

            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeCommand(userStmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject getSystemRoles() {
        Connection con = null;
        IDBCommand stmt = null;
        final JSONObject json = new JSONObject();
        try {
            con = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(con, getSystemRoleSQL(), true);
            final ArrayList<JSONObject> roleArray = new ArrayList<JSONObject>();
            stmt.executeQuery();
            while (stmt.next()) {
                final JSONObject roleJSON = new JSONObject();
                roleJSON.put("id", stmt.getLong(1));
                roleJSON.put("name", stmt.getString(2));
                roleJSON.put("groupname", stmt.getString(3));
                roleArray.add(roleJSON);
            }
            json.put("roles", roleArray);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);

            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeCommand(stmt);
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    protected abstract String getUsersListByDepartmentIdSQL();

}
