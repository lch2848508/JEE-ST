package com.estudio.intf.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;

import org.dom4j.DocumentException;

import com.estudio.define.webclient.portal.AbstractPortalGridDefine;

public interface IPortal4ClientGridDefineService {

    /**
     * 通知栏目服务某一个栏目已经发生变化
     * 
     * @param id
     */
    public abstract void notifyGridDefineIsChanged(long id);

    /**
     * 根据ID取得Grid定义
     * 
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     */
    public abstract AbstractPortalGridDefine getPortalGridDefine(long id, Connection con) throws Exception, DocumentException;

}
