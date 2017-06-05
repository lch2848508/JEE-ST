package com.estudio.gis;

import com.estudio.gis.oracle.WebGISCacheService4Oracle;
import com.estudio.utils.ThreadUtils;

public class WebGISDaemonService {
    private static WebGISDaemonService instance = new WebGISDaemonService();

    public static WebGISDaemonService getInstance() {
        return instance;
    }

    private WebGISSpatialConfig spatialConfig = null;

    protected WebGISDaemonService() {
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        WebGISCacheService4Oracle.getInstance().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ThreadUtils.sleepMinute(5);
                }
            }
        }).start();
    }

    /**
     * 初始化参数
     * 
     * @param wkid
     * @param extent
     * @param tolerance
     * @param simplify
     * @param unit
     */
    public void initSpatialParams(WebGISSpatialConfig config) {
        this.spatialConfig = config;
        WebGISCacheService4Oracle.getInstance().initSpatialConfig(spatialConfig);
    }

}
