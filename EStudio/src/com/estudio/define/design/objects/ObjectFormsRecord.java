package com.estudio.define.design.objects;

import net.minidev.json.JSONObject;

import com.estudio.define.db.DBRecord;

public class ObjectFormsRecord extends DBRecord {
    long id; // 唯一标识符
    byte[] dfmstream; // 表单设计器所用到的数据流
    byte[] xmlstream; // XML格式数据流
    byte[] datasource; // 数据源的XML格式
    byte[] jsscript; // jsscript脚本
    long version; // 版本号
    long type; // 类型 0 Win风格 1 Excel风格
    String formParams;

    public String getFormParams() {
        return formParams;
    }

    public void setFormParams(final String formParams) {
        this.formParams = formParams;
    }

    /**
     * 获取唯一标识符
     * 
     * @return 返回唯一标识符
     */
    public long getId() {
        return id;
    }

    /**
     * 设置唯一标识符
     * 
     * @param value
     *            唯一标识符
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * 获取表单设计器所用到的数据流
     * 
     * @return 返回表单设计器所用到的数据流
     */
    public byte[] getDfmstream() {
        return dfmstream;
    }

    /**
     * 设置表单设计器所用到的数据流
     * 
     * @param value
     *            表单设计器所用到的数据流
     */
    public void setDfmstream(final byte[] value) {
        dfmstream = value;
    }

    /**
     * 获取XML格式数据流
     * 
     * @return 返回XML格式数据流
     */
    public byte[] getXmlstream() {
        return xmlstream;
    }

    /**
     * 设置XML格式数据流
     * 
     * @param value
     *            XML格式数据流
     */
    public void setXmlstream(final byte[] value) {
        xmlstream = value;
    }

    /**
     * 获取数据源的XML格式
     * 
     * @return 返回数据源的XML格式
     */
    public byte[] getDatasource() {
        return datasource;
    }

    /**
     * 设置数据源的XML格式
     * 
     * @param value
     *            数据源的XML格式
     */
    public void setDatasource(final byte[] value) {
        datasource = value;
    }

    /**
     * 获取jsscript脚本
     * 
     * @return 返回jsscript脚本
     */
    public byte[] getJsscript() {
        return jsscript;
    }

    /**
     * 设置jsscript脚本
     * 
     * @param value
     *            jsscript脚本
     */
    public void setJsscript(final byte[] value) {
        jsscript = value;
    }

    /**
     * 获取版本号
     * 
     * @return 返回版本号
     */
    public long getVersion() {
        return version;
    }

    /**
     * 设置版本号
     * 
     * @param value
     *            版本号
     */
    public void setVersion(final long value) {
        version = value;
    }

    /**
     * 获取类型 0 Win风格 1 Excel风格
     * 
     * @return 返回类型 0 Win风格 1 Excel风格
     */
    public long getType() {
        return type;
    }

    /**
     * 设置类型 0 Win风格 1 Excel风格
     * 
     * @param value
     *            类型 0 Win风格 1 Excel风格
     */
    public void setType(final long value) {
        type = value;
    }

    /**
     * @return 生成JSON对象
     */
    @Override
    public JSONObject getJSON() {
        final JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("version", version);
        json.put("type", type);
        return json;
    }
}
