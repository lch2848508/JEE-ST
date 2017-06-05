package com.estudio.intf.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.define.sercure.ClientLoginInfo;

public interface IPortal4ClientService {

    /**
     * ֪ͨ������Ŀ�Ѿ���������(Ȩ�޻�����Ŀ)
     */
    public abstract void notifyPortalSettingChange();

    /**
     * �����û�IDȡ���û���ʹ�õ�ϵͳ��Ŀ�鼰��Ŀ��
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
