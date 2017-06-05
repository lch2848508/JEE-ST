package com.estudio.context;

import com.estudio.define.sercure.ClientLoginInfo;

public final class GlobalContext {

    private static ThreadLocal<ClientLoginInfo> clientLoginInfo = new ThreadLocal<ClientLoginInfo>();
    private static ThreadLocal<StringBuffer> clientMessage = new ThreadLocal<StringBuffer>();
    private static ThreadLocal<StringBuffer> clientAlertMsg = new ThreadLocal<StringBuffer>();
    private static ThreadLocal<ClientInfo> clientInfo = new ThreadLocal<ClientInfo>();

    private GlobalContext() {

    }

    /**
     * 获取客户端登录
     * 
     * @return
     */
    public static ClientLoginInfo getLoginInfo() {
        return clientLoginInfo.get();
    }

    /**
     * 设置客户端登录
     * 
     * @param loginInfo
     */
    public static void setLoginInfo(final ClientLoginInfo loginInfo) {
        clientLoginInfo.set(loginInfo);
    }

    /**
     * 获取客户端登录
     * 
     * @return
     */
    public static StringBuffer getClientMessage() {
        return clientMessage.get();
    }

    /**
     * 设置客户端登录
     * 
     * @param loginInfo
     */
    public static void setClientMessage(final StringBuffer msg) {
        clientMessage.set(msg);
    }

    public static void setAlertMessage(final StringBuffer msg) {
        clientAlertMsg.set(msg);
    }

    public static StringBuffer getAlertMessage() {
        return clientAlertMsg.get();
    }

    public static ClientInfo getClientInfo() {
        return clientInfo.get();
    }

    public static void setClientInfo(final ClientInfo value) {
        clientInfo.set(value);
    }

}
