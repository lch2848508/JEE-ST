package com.estudio.intf.design.utils;

import java.sql.Connection;
import java.sql.SQLException;

public interface IVersionService {

    /**
     * 得到对象版本信息
     * 
     * @param type
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long getVersion(Connection con, String type) throws Exception;

    /**
     * 增加版本信息
     * 
     * @param con
     * @param type
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long incVersion(Connection con, String type) throws Exception;

}
