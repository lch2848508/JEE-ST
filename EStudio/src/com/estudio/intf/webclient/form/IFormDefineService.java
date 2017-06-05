package com.estudio.intf.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;

import org.dom4j.DocumentException;

import com.estudio.define.webclient.form.FormDefine;

public interface IFormDefineService {

    /**
     * Form设计内容已经发生更改事件
     * 
     * @param id
     */
    public abstract void notifyFormDefineIsChanged(long id);

    /**
     * 得到表单组
     * 
     * @param ids
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     */
    public abstract FormDefine[] getFormDefines(String[] ids, Connection con) throws Exception;

    /**
     * 得到表单组的最大尺寸
     * 
     * @param ids
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     * @throws Exception
     */
    public abstract int[] getFormMaxSize(String[] ids, Connection con) throws Exception;

    /**
     * 得到表单组的最大尺寸
     * 
     * @param forms
     * @return
     */
    public abstract int[] getFormMaxSize(FormDefine[] forms);

    /**
     * 根据ID获取表单定义
     * 
     * @param id
     * @param con
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     */
    public abstract FormDefine getFormDefine(long id, Connection con) throws Exception;

}
