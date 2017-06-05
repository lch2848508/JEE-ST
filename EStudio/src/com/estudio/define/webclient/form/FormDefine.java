package com.estudio.define.webclient.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.SQLParam4Form;
import com.estudio.impl.webclient.form.DataSetCacheService4WebClient;
import com.estudio.impl.webclient.form.DataSetParser;
import com.estudio.impl.webclient.form.WinForm2HTMLParser;
import com.estudio.impl.webclient.form.WinForm2JsonParser;
import com.estudio.impl.webclient.form.XLSFormParser;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.JSCompress;
import com.estudio.web.service.DataService4Form;

public class FormDefine {
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private final ArrayList<DesignDataSource> datasets = new ArrayList<DesignDataSource>();
    private final HashMap<String, DesignDataSource> name2DataSet = new HashMap<String, DesignDataSource>();
    private String html;
    private final Document formDOM;
    private final String jscript;
    private int formWidth;
    private int formHeight;
    private final String formName;
    private final long formId;
    private String cssStyle;
    private final int version;
    private JSONObject formJson;

    public JSONObject getFormJson() {
        return formJson;
    }

    public void setFormJson(final JSONObject formJson) {
        this.formJson = formJson;
    }

    public int getVersion() {
        return version;
    }

    /**
     * DataSet数量
     * 
     * @return
     */
    public long getDataSetCount() {
        return datasets.size();
    }

    /**
     * 产生的CSS
     * 
     * @return
     */
    public String getCssStyle() {
        return cssStyle;
    }

    /**
     * 取表单宽度
     * 
     * @return
     */
    public int getFormWidth() {
        return formWidth;
    }

    /**
     * 设置表单宽度
     * 
     * @param formWidth
     */
    public void setFormWidth(final int formWidth) {
        this.formWidth = formWidth;
    }

    /**
     * 取表单高度
     * 
     * @return
     */
    public int getFormHeight() {
        return formHeight;
    }

    /**
     * 设置表单高度
     * 
     * @param formHeight
     */
    public void setFormHeight(final int formHeight) {
        this.formHeight = formHeight;
    }

    /**
     * 得到HTML字符串
     * 
     * @param offsetX
     * @param offsetY
     * @return
     */
    public String getHTML() {
        return html;
    }

    /**
     * 注册数据源
     * 
     * @param ds
     */
    public void registerDataSet(final DesignDataSource ds) {
        datasets.add(ds);
        name2DataSet.put(ds.getName(), ds);
    }

    /**
     * 根据名称取得DataSet
     * 
     * @param name
     * @return
     */
    public DesignDataSource getDataSet(final String name) {
        return name2DataSet.get(name);
    }

    /**
     * 根据索引取得DataSet
     * 
     * @param index
     * @return
     */
    public DesignDataSource getDataSet(final int index) {
        return datasets.get(index);
    }

    /**
     * 取得主DataSet
     * 
     * @param index
     * @return
     */
    public DesignDataSource getPrimaryDataSet() {
        return datasets.get(0);
    }

    /**
     * 构造函数
     * 
     * @param xmlDOM
     * @param dsDOM
     * @param jscript
     * @param id
     * @param formName
     * @param b
     * @throws JSONException
     * @throws Exception
     */
    public FormDefine(final Document xmlDOM, final Document dsDOM, final String jscript, final boolean isWinForm, final String formName, final long id, final int version, final Connection con) throws Exception {
        formDOM = xmlDOM;
        this.formName = formName;
        formId = id;
        this.version = version;
        DataSetParser.getInstance().parser(this, dsDOM, con);
        final StringBuilder jsSb = new StringBuilder();
        if (isWinForm) {
            WinForm2HTMLParser.getInstance().parser(this, formDOM, jsSb, con);
            WinForm2JsonParser.getInstance().parser(this, formDOM, jsSb, con);
        } else XLSFormParser.getInstance().parser(this, formDOM, jsSb);
        for (int i = 0; i < datasets.size(); i++) {
            final DesignDataSource ds = datasets.get(i);
            DataSetCacheService4WebClient.getInstance().registerDataSet(formId, ds);
            if (ds.isFormDataSet()) {
                final JSONObject json = new JSONObject();
                if (ds.getPrimaryField() != null) {
                    json.put("Key", ds.getPrimaryField().getName());
                    json.put("Fields", ds.getDataSetFields());
                } else {
                    json.put("Key", null);
                    json.put("Fields", new JSONArray());
                }
                json.put("ReadOnly", ds.isReadOnly());
                json.put("FieldCount", ds.getFieldCount());
                json.put("KeyFieldIndex", ds.getPrimaryFieldIndex());
                // jsSb.append("GLOBAL_DATASET_DEFINE[\"").append(ds.getName()).append("\"] = ").append(json).append(";\n");
                final JSONArray linkJsonArrays = generalDSCommandLinkageJSON(ds);
                ds.setLinkJsonArray(linkJsonArrays);
                // jsSb.append("GLOBAL_DATASET_PARENT_LINKAGE[\"").append(ds.getName()).append("\"] = ").append(linkJsonArrays).append(";\n");
            }
        }
        jsSb.append(JSCompress.getInstance().compress(jscript));
        this.jscript = JSCompress.getInstance().compress(jsSb.toString());
    }

    /**
     * 生成记录联动信息的JSON信息
     * 
     * @param ds
     * @param select
     * @return
     * @throws JSONException
     */
    private JSONArray generalDSCommandLinkageJSON(final DesignDataSource ds) {
        final JSONArray json = new JSONArray();
        final DesignDataSourceCommand cmds[] = { ds.getSelect(), ds.getInsert(), ds.getUpdate() };
        final ArrayList<String> keyList = new ArrayList<String>();

        for (int i = 0; i < ds.getRelations().size(); i++) {
            final JSONObject linkJson = new JSONObject();
            final DataSetRelation dsR = ds.getRelations().get(i);
            linkJson.put("DS", dsR.getParentDataSet());
            linkJson.put("ParentField", dsR.getParentFieldName());
            linkJson.put("LinkField", dsR.getLinkFieldName());
            linkJson.put("InitDS", dsR.getInitDataSet());
            linkJson.put("InitField", dsR.getInitFieldName());
            json.add(linkJson);
            keyList.add(dsR.getParentDataSet() + "_" + dsR.getParentFieldName());
        }

        for (final DesignDataSourceCommand cmd : cmds)
            for (int j = 0; j < cmd.getParamCount(); j++) {
                final SQLParam4Form param = cmd.getParam(j);
                if (!StringUtils.equals(param.getInitDS(), ds.getName()) && !StringUtils.isEmpty(param.getInitDS())) {
                    final String key = param.getInitDS() + "_" + param.getInitField();
                    if (keyList.indexOf(key) == -1) {
                        final JSONObject linkJson = new JSONObject();
                        linkJson.put("DS", param.getInitDS());
                        linkJson.put("ParentField", param.getInitField());
                        linkJson.put("InitDS", param.getFirstInitDS());
                        linkJson.put("InitField", param.getFirstInitField());
                        json.add(linkJson);
                        keyList.add(key);
                    }
                }
            }
        return json;
    }

    /**
     * 得到JavaScript代码部分
     * 
     * @return
     */
    public String getJscript() {
        return jscript;
    }

    public String getFormName() {
        return formName;
    }

    public long getFormId() {
        return formId;
    }

    public void setCssStyle(final String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setHTML(final String value) {
        html = value;
    }

    /**
     * 得到表单数据
     * 
     * @return
     * @throws SQLException
     *             , DBException
     * @throws JSONException
     */
    public void getData(final Connection con, final JSONObject datasetName2Values, final Map<String, String> param2Value, final HttpSession session) throws Exception {
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            for (int i = 0; i < datasets.size(); i++) {
                final DesignDataSource ds = datasets.get(i);
                if (ds.isFormDataSet() && (ds.getFieldCount() != 0))
                    datasetName2Values.put(ds.getName(), ds.isAsyncLoad() ? new JSONArray() : DataService4Form.getInstance().getDataSetData(con, datasetName2Values, ds, param2Value, session));
            }
        } finally {
            if ((tempCon != con) && (tempCon != null))
                DBHELPER.closeConnection(tempCon);
        }
    }

}
