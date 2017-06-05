package com.estudio.intf.design.portal;

import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;

public interface IPortalRightService {

    /**
     * 得到特定用户 Portal 项的权限
     * 
     * @param portalID
     * @param userID
     * @param isGroup
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long getPortalRight(Connection con, long portalID, long userID) throws Exception;

    /**
     * 得到Portal项的权限
     * 
     * @param portalID
     * @param pID
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract JSONObject getPortalRight(long id, boolean isGroup) throws Exception;

    /**
     * 保存Portal权限
     * 
     * @param paramInt
     * @param paramStr
     * @return
     * @throws JSONException
     * @throws SQLException
     *             , DBException
     * @throws DBException
     */
    public abstract JSONObject savePortalRight(long id, String rights) throws Exception;

    /**
     * 保存项权限
     * 
     * @param portal_id
     * @param roleids
     * @param rights
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract JSONObject savePortalRight(long portal_id, String roleids, String rights) throws Exception;

}
