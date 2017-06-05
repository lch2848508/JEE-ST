package com.estudio.intf.design.utils;

import java.sql.SQLException;

import net.minidev.json.JSONObject;

public interface ISQLParserService {

    /**
     * 生成数据对象
     * 
     * @param tables
     * @param paramDataType
     * 
     * @param paramStr
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract JSONObject parser(String sql, String[] tables, String paramDataType) throws Exception;

    /**
     * 生成合成SQL语句
     * 
     * @param paramStr
     * @param paramStr2
     * @return
     * @throws Exception
     */
    public abstract JSONObject merge(String paramStr, String paramStr2) throws Exception;

}
