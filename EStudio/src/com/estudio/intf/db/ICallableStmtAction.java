package com.estudio.intf.db;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface ICallableStmtAction {
    /**
     * �������ݼ���
     * 
     * @param stmt
     * @throws SQLException
     */
    public void processStatement(CallableStatement stmt) throws SQLException;
}
