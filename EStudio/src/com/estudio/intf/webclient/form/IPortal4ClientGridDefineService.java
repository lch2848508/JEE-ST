package com.estudio.intf.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;

import org.dom4j.DocumentException;

import com.estudio.define.webclient.portal.AbstractPortalGridDefine;

public interface IPortal4ClientGridDefineService {

    /**
     * ֪ͨ��Ŀ����ĳһ����Ŀ�Ѿ������仯
     * 
     * @param id
     */
    public abstract void notifyGridDefineIsChanged(long id);

    /**
     * ����IDȡ��Grid����
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
