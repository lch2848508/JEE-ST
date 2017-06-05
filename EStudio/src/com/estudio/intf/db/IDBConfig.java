package com.estudio.intf.db;

public interface IDBConfig {

    /**
     * ��ȡ������Ϣ
     * 
     * @param category
     * @param key
     * @return
     * @throws Exception
     */
    public String getConfig(String category, String key, IDBHelper dbHelper) throws Exception;

    /**
     * ����������Ϣ
     * 
     * @param category
     * @param key
     * @param content
     * @return
     * @throws Exception
     */
    public void saveConfig(String category, String key, String content, IDBHelper dbHelper) throws Exception;
}
