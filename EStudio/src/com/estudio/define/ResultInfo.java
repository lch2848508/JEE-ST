package com.estudio.define;

import net.minidev.json.JSONObject;

public class ResultInfo {
    boolean ok;
    String msg;

    /**
     * 构造函数
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
     * 函数是否运行成功
     * 
     * @return
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * 得到程序运行信息
     * 
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 构造函数
     */
    public ResultInfo() {
        super();
        ok = true;
    }

    /**
     * 生成JSON对象
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
