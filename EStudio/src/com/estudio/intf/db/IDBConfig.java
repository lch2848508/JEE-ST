package com.estudio.intf.db;

public interface IDBConfig {

    /**
     * 获取配置信息
     * 
     * @param category
     * @param key
     * @return
     * @throws Exception
     */
    public String getConfig(String category, String key, IDBHelper dbHelper) throws Exception;

    /**
     * 保存配置信息
     * 
     * @param category
     * @param key
     * @param content
     * @return
     * @throws Exception
     */
    public void saveConfig(String category, String key, String content, IDBHelper dbHelper) throws Exception;
}
