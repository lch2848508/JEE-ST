package com.estudio.impl.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBConfig;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;

public abstract class DBConfig implements IDBConfig {
    private Map<String, String> key2Config = new HashMap<String, String>();

    @Override
    public synchronized String getConfig(String category, String key, IDBHelper dbHelper) throws Exception {
        String cacheKey = category + "-" + key;
        if (!key2Config.containsKey(cacheKey)) {
            Connection con = null;
            IDBCommand cmd = null;
            try {
                con = dbHelper.getConnection();
                if (checkSchema(con, dbHelper)) {
                    cmd = dbHelper.getCommand(con, "select content from sys_config where k=:key and c = :category");
                    cmd.setParam("key", key);
                    cmd.setParam("category", category);
                    cmd.executeQuery();
                    String content = cmd.next() ? Convert.bytes2Str(cmd.getBytes(1)) : "";
                    key2Config.put(cacheKey, content);
                }
            } finally {
                dbHelper.closeCommand(cmd);
                dbHelper.closeConnection(con);
            }
        }
        return key2Config.get(cacheKey);
    }

    @Override
    public synchronized void saveConfig(String category, String key, String content, IDBHelper dbHelper) throws Exception {
        Connection con = null;
        IDBCommand cmd = null;
        IDBCommand insCmd = null;
        try {
            con = dbHelper.getConnection();
            if (checkSchema(con, dbHelper)) {
                cmd = dbHelper.getCommand(con, "delete from sys_config where k=:key and c = :category");
                cmd.setParam("key", key);
                cmd.setParam("category", category);
                cmd.execute();

                insCmd = dbHelper.getCommand(con, "insert into sys_config (c,k,content) values (:category,:key,:content)");
                insCmd.setParam("key", key);
                insCmd.setParam("category", category);
                insCmd.setParam("content", Convert.str2Bytes(content));
                insCmd.execute();
            }
        } finally {
            dbHelper.closeCommand(insCmd);
            dbHelper.closeCommand(cmd);
            dbHelper.closeConnection(con);
        }
    }

    protected abstract boolean checkSchema(Connection con, IDBHelper dbHelper) throws Exception;
}
