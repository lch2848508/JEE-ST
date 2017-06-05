package com.estudio.intf.web.sercure;

import javax.servlet.http.HttpSession;

import net.minidev.json.JSONObject;

import com.estudio.define.ResultInfo;
import com.estudio.define.sercure.ClientLoginInfo;

public interface IClientLoginService {

    /**
     * 注销
     * 
     * @param session
     */
    public abstract void logoff(HttpSession session);

    /**
     * 判断用户是否已经登录
     * 
     * @param session
     * @param info
     * @return
     */
    public abstract boolean isLogined(HttpSession session);

    /**
     * 注册LoginInfo信息到Session中
     * 
     * @param session
     * @param info
     */
    public abstract void registerLoginInfo(HttpSession session, ClientLoginInfo info);

    /**
     * 登录信息
     * 
     * @param session
     * @return
     */
    public abstract ClientLoginInfo getLoginInfo(HttpSession session);

    /**
     * 更改密码
     * 
     * @param info
     * @param oldpwd
     * @param newpwd
     */
    public abstract ResultInfo changePassword(ClientLoginInfo info, String oldpwd, String newpwd);

    /**
     * 系统登录
     * 
     * @param user
     * @param password
     * @param code
     * @param isSkipCode
     * @return
     */
    public abstract ClientLoginInfo login(String user, String password, String userType, String userArea, String orgId, String sessionId);

    /**
     * 通过其他系统登录
     * 
     * @param uuid
     * @param code
     * @return
     */
    public abstract ClientLoginInfo loginByOther(String uuid, String code, String sessionId);

    /**
     * 用户注册
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
