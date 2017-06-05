package com.estudio.define;

import net.minidev.json.JSONObject;

public class ResultInfo {
    boolean ok;
    String msg;

    /**
     * ���캯��
     * 
     * @param ok
     * @param msg
     */
    public ResultInfo(final boolean ok, final String msg) {
        super();
        this.ok = ok;
        this.msg = msg;
    }

    /**
     * �����Ƿ����гɹ�
     * 
     * @return
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * �õ�����������Ϣ
     * 
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     * ���캯��
     */
    public ResultInfo() {
        super();
        ok = true;
    }

    /**
     * ����JSON����
     * 
     * @return
     */
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        try {
            json.put("r", ok);
            json.put("msg", msg);
        } catch (final Exception e) {
        }
        return json;
    }

}
