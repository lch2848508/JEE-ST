package com.estudio.intf.webclient.report;

import java.sql.Connection;
import java.sql.SQLException;

import com.estudio.define.webclient.report.ReportTemplateDefine;

public interface IReportDefineService {

    /**
     * 获得对象
     * 
     * @param con
     * @param templateid
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public abstract ReportTemplateDefine getDefine(Connection con, long templateid) throws Exception;

    /**
     * 通知内容变化
     * 
     * @param id
     */
    public abstract void notifyTemplateIsModified(long id);

}
