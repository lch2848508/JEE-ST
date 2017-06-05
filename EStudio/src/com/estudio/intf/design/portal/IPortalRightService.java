package com.estudio.intf.design.portal;

import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBException;

public interface IPortalRightService {

    /**
     * �õ��ض��û� Portal ���Ȩ��
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
     * �õ�Portal���Ȩ��
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
     * ����PortalȨ��
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
     * ������Ȩ��
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
