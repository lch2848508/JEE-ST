package com.estudio.intf.design.utils;

import java.sql.Connection;
import java.sql.SQLException;

public interface IVersionService {

    /**
     * �õ�����汾��Ϣ
     * 
     * @param type
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long getVersion(Connection con, String type) throws Exception;

    /**
     * ���Ӱ汾��Ϣ
     * 
     * @param con
     * @param type
     * @return
     * @throws SQLException
     *             , DBException
     */
    public abstract long incVersion(Connection con, String type) throws Exception;

}
