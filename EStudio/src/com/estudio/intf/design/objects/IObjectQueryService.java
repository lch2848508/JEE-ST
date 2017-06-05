package com.estudio.intf.design.objects;

import java.sql.Connection;

import net.minidev.json.JSONObject;

public interface IObjectQueryService {

    /**
     * �����������
     * 
     * @param id
     * @param content
     * @return
     * @throws Exception
     */
    public JSONObject save(long id, String content) throws Exception;

    /**
     * ��ȡ�������
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public JSONObject get(long id) throws Exception;

    public JSONObject get(Connection con, long id) throws Exception;
}
