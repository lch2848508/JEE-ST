package com.estudio.intf.web.sercure;

import javax.servlet.http.HttpSession;

import net.minidev.json.JSONObject;

import com.estudio.define.ResultInfo;
import com.estudio.define.sercure.ClientLoginInfo;

public interface IClientLoginService {

    /**
     * ע��
     * 
     * @param session
     */
    public abstract void logoff(HttpSession session);

    /**
     * �ж��û��Ƿ��Ѿ���¼
     * 
     * @param session
     * @param info
     * @return
     */
    public abstract boolean isLogined(HttpSession session);

    /**
     * ע��LoginInfo��Ϣ��Session��
     * 
     * @param session
     * @param info
     */
    public abstract void registerLoginInfo(HttpSession session, ClientLoginInfo info);

    /**
     * ��¼��Ϣ
     * 
     * @param session
     * @return
     */
    public abstract ClientLoginInfo getLoginInfo(HttpSession session);

    /**
     * ��������
     * 
     * @param info
     * @param oldpwd
     * @param newpwd
     */
    public abstract ResultInfo changePassword(ClientLoginInfo info, String oldpwd, String newpwd);

    /**
     * ϵͳ��¼
     * 
     * @param user
     * @param password
     * @param code
     * @param isSkipCode
     * @return
     */
    public abstract ClientLoginInfo login(String user, String password, String userType, String userArea, String orgId, String sessionId);

    /**
     * ͨ������ϵͳ��¼
     * 
     * @param uuid
     * @param code
     * @return
     */
    public abstract ClientLoginInfo loginByOther(String uuid, String code, String sessionId);

    /**
     * �û�ע��
     * 
     * @param sessionId
     * @param userName
     * @param sex
     * @param cardId
     * @param mobile
     * @param address
     * @param email
     * @param code
     * @return
     * @throws Exception 
     */
    public abstract JSONObject registerTempUserInfo(String sessionId, String userName, int sex, String cardId, String mobile, String address, String email) throws Exception;

}
