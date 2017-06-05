package com.estudio.impl.service.sercure;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

public final class HttpSessionService {
    private final HashMap<String, HttpSession> cacheMap = new HashMap<String, HttpSession>();

    /**
     * 注册数据缓存
     * 
     * @param datasetName
     * @param object
     */
    public void put(final HttpSession session) {
        cacheMap.put(session.getId(), session);
    }

    /**
     * 获取缓存
     * 
     * @param datasetName
     * @return
     */
    public HttpSession get(final String sessionID) {
        return cacheMap.get(sessionID);
    }

    /**
     * 获取缓存
     * 
     * @param datasetName
     * @return
     */
    public HttpSession del(final String sessionID) {
        return cacheMap.remove(sessionID);
    }

    private HttpSessionService() {
    }

    private static final HttpSessionService INSTANCE = new HttpSessionService();

    public static HttpSessionService getInstance() {
        return INSTANCE;
    }

}
