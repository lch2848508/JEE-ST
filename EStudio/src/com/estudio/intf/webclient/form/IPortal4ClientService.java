package com.estudio.intf.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.define.sercure.ClientLoginInfo;

public interface IPortal4ClientService {

    /**
     * 通知该类栏目已经发生更改(权限或者栏目)
     */
    public abstract void notifyPortalSettingChange();

    /**
     * 根据用户ID取得用户可使用的系统栏目组及栏目项
     * 
     * @param con
     * @param user_id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract JSONObject getPortalTreeByUserID(Connection con, long user_id, long department_id, ClientLoginInfo loginInfo) throws Exception;

}
