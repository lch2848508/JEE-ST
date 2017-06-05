package com.estudio.intf.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;

import org.dom4j.DocumentException;

import com.estudio.define.webclient.form.FormDefine;

public interface IFormDefineService {

    /**
     * Form��������Ѿ����������¼�
     * 
     * @param id
     */
    public abstract void notifyFormDefineIsChanged(long id);

    /**
     * �õ�����
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
     * �õ���������ߴ�
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
     * �õ���������ߴ�
     * 
     * @param forms
     * @return
     */
    public abstract int[] getFormMaxSize(FormDefine[] forms);

    /**
     * ����ID��ȡ������
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
