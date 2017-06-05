package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

public class PortalGridDefineEx extends AbstractPortalGridDefine {

    // 布局
    private PortalGridExLayout layout = new PortalGridExLayout();

    // 控件
    private List<PortalGridExControl> controls = new ArrayList<PortalGridExControl>();

    // JS脚本
    private String javascript = null;

    private String toolbarAdditions = null;

    @Override
    public void createPortalJSON() throws Exception {
        // 布局
        portalJSON.put("layout", createLayoutJSON());

        // 脚本
        // portalJSON.put("js", this.javascript);

        // 控件
        for (PortalGridExControl control : this.controls) {
            JSONUtils.append(portalJSON, "controls", createControlJSON(control));
        }

        // 工具条
        portalJSON.put("toolbar", PortalUtils.parserToolbarAddition(null, toolbarAdditions));
    }

    /**
     * 创建控件JSON
     * 
     * @param control
     * @return
     * @throws Exception
     */
    private JSONObject createControlJSON(PortalGridExControl control) throws Exception {
        JSONObject json = new JSONObject();
        json.put("name", control.getControlName());
        json.put("comment", control.getControlComment());
        json.put("type", control.getControlType());
        if (control.getControlType() == PortalGridExControl.CONTROL_GRID) {
            PortalGridExGrid grid = (PortalGridExGrid) control;
            json.put("bindforms", PortalUtils.bindForms2JSON(grid.getBindForms()));
            json.put("options", grid.getOptions());
            json.put("keyField", grid.getSelectSQLDefine().getKeyFieldName());
            int columnIndex = 0;
            for (PortalGridExColumn column : grid.getColumns()) {
                JSONUtils.append(json, "columns", createPortalGridExColumn(columnIndex, column, grid.getSelectSQLDefine()));
            }
            json.put("childControls", grid.getChildControls());
            json.put("relationFields", grid.getRelationFields());
            json.put("parentParams", createSQLParamsJSON(grid));

            final List<JSONObject> paramsList = new ArrayList<JSONObject>();
            for (int i = 0; i < grid.getSelectSQLDefine().getParamCount(); i++) {
                final SQLParam4Portal param = grid.getSelectSQLDefine().getParam(i);
                if (param.isFilter()) {
                    final JSONObject filterJson = new JSONObject();
                    filterJson.put("label", param.getLabel());
                    filterJson.put("name", param.getName());
                    filterJson.put("control", param.getFilterControl());
                    filterJson.put("datatype", param.getDataType());
                    filterJson.put("pos", param.getPos());
                    filterJson.put("controlWidth", param.getControlWidth());
                    filterJson.put("isExistsParent", "ComboBox".equals(param.getFilterControl()) && StringUtils.containsIgnoreCase(param.getParamValueSQL(), ":PARENT_COMBOBOX"));
                    paramsList.add(filterJson);
                }
            }
            json.put("filterParams", paramsList);

        } else if (control.getControlType() == PortalGridExControl.CONTROL_TREE) {
            PortalGridExTree tree = (PortalGridExTree) control;
            json.put("bindforms", PortalUtils.bindForms2JSON(tree.getBindForms()));
            json.put("options", tree.getOptions());
            json.put("keyField", tree.getSelectSQLDefine().getKeyFieldName());
            json.put("labelField", tree.getSelectSQLDefine().getLabelFieldName());
            json.put("childControls", tree.getChildControls());
            json.put("relationFields", tree.getRelationFields());
            json.put("parentParams", createSQLParamsJSON(tree));
            String iconFileName = StringUtils.replace(tree.getOptions().get("DefaultIcon"), ".bmp", ".png");
            if (StringUtils.isEmpty(iconFileName))
                iconFileName = "folder.png";
            json.put("iconFileName", iconFileName);
            iconFileName = StringUtils.replace(tree.getOptions().get("RootIcon"), ".bmp", ".png");
            if (StringUtils.isEmpty(iconFileName))
                iconFileName = "computer.png";
            json.put("RootIcon", iconFileName);
            json.put("checkbox", Convert.str2Boolean(tree.getOptions().get("CheckBoxEnable")));
            String checkFieldName = StringUtils.substringBetween(tree.getOptions().get("CheckField"), "[", "]");
            if (StringUtils.isEmpty(checkFieldName))
                checkFieldName = "selected";
            json.put("checkFieldName", checkFieldName);
            json.put("iconFieldName", StringUtils.substringBetween(tree.getOptions().get("IconField"), "[", "]"));
            json.put("checkEnabledField", StringUtils.substringBetween(tree.getOptions().get("CheckEnabledField"), "[", "]"));
        } else if (control.getControlType() == PortalGridExControl.CONTROL_FORM) {
            PortalGridExForm form = (PortalGridExForm) control;
            json.put("bindforms", PortalUtils.bindForms2JSON(form.getBindForms()));
            json.put("options", form.getOptions());
        } else if (control.getControlType() == PortalGridExControl.CONTROL_SWF) {
            json.put("url", ((PortalGridExSWF) control).getUrl());
            json.put("isModule", ((PortalGridExSWF) control).isModule());
        } else if (control.getControlType() == PortalGridExControl.CONTROL_IFRAME) {
            json.put("url", ((PortalGridExIFrame) control).getUrl());
        } else if (control.getControlType() >= PortalGridExControl.CONTROL_RICHVIEW && control.getControlType() <= PortalGridExControl.CONTROL_PAGECONTROL) {
            for (Entry<String, String> entry : ((PortalGridExCommon) control).getOptions().entrySet())
                if (StringUtils.equals(entry.getValue(), "True") || StringUtils.equals(entry.getValue(), "False"))
                    json.put(entry.getKey(), StringUtils.equals(entry.getValue(), "True"));
                else
                    json.put(entry.getKey(), entry.getValue());
        }
        return json;
    }

    /**
     * SQLDefine 参数
     * 
     * @param selectSQLDefine
     * @param rootSQLDefine
     * @return
     */
    private List<JSONObject> createSQLParamsJSON(PortalGridExSQLBase control) {
        List<String> filterList = new ArrayList<String>();
        List<JSONObject> list = new ArrayList<JSONObject>();
        createSQLParamsJSON(control.getSelectSQLDefine(), list, filterList);
        if (control.getRootSQLDefine() != null)
            createSQLParamsJSON(control.getRootSQLDefine(), list, filterList);
        if (control.getInsertSQLDefine() != null)
            createSQLParamsJSON(control.getInsertSQLDefine(), list, filterList);
        if (control.getUpdateSQLDefine() != null)
            createSQLParamsJSON(control.getUpdateSQLDefine(), list, filterList);
        if (control.getDeleteSQLDefine() != null)
            createSQLParamsJSON(control.getDeleteSQLDefine(), list, filterList);
        return list;
    }

    /**
     * @param sqlDefine
     * @param list
     * @param filterList
     */
    private void createSQLParamsJSON(SQLDefineBase sqlDefine, List<JSONObject> list, List<String> filterList) {
        for (int i = 0; i < sqlDefine.getParamCount(); i++) {
            SQLParam4Portal param = sqlDefine.getParam(i);
            if (StringUtils.isEmpty(param.getInitSQLName()))
                continue;
            String keyStr = param.getInitSQLName() + "$" + param.getInitField();
            if (filterList.indexOf(keyStr) == -1) {
                JSONObject paramJson = new JSONObject();
                paramJson.put("control", param.getInitSQLName());
                paramJson.put("field", param.getInitField());
                list.add(paramJson);
                filterList.add(keyStr);
            }
        }
    }

    /**
     * 数据列
     * 
     * @param columnIndex
     * 
     * @param column
     * @return
     */
    private JSONObject createPortalGridExColumn(int columnIndex, PortalGridExColumn column, SQLDefineBase sqlDefine) {
        final JSONObject columnJSON = new JSONObject();
        columnJSON.put("Caption", column.getFieldLabel());
        String fieldName = column.getFieldName();
        columnJSON.put("Field", fieldName);
        columnJSON.put("Align", column.getAlignment());
        columnJSON.put("Width", StringUtils.isEmpty(column.getWidth()) ? "*" : column.getWidth());
        columnJSON.put("ReadonlyAble", column.isReadonlyAble());
        columnJSON.put("DataType", sqlDefine.getFieldDataType(fieldName));
        columnJSON.put("Style", createColumnStyle(column));
        columnJSON.put("FixedWidth", column.getFixedWidth());
        columnJSON.put("IsLinkColumn", column.getURLCount() != 0);
        columnJSON.put("Special", column.isSpecial());
        columnJSON.put("EditorClass", column.getEditorClass());
        columnJSON.put("EditorProperty", column.getEditorProperty());
        columnJSON.put("Editable", column.isEditable());
        final ArrayList<JSONObject> URLList = new ArrayList<JSONObject>();
        for (int j = 0; j < column.getURLCount(); j++) {
            final SQLColumnURL url = column.getURL(j);
            final JSONObject urlJSON = new JSONObject();
            urlJSON.put("Type", url.getType());
            urlJSON.put("Params", url.getParams());
            urlJSON.put("Display", url.getLabel());
            URLList.add(urlJSON);
        }
        columnJSON.put("URLS", URLList);
        if (!URLList.isEmpty()) {
            if (StringUtils.isEmpty(fieldName)) {
                fieldName = "__F" + columnIndex + "__";
                column.setUrlFieldName(fieldName);
                columnJSON.put("Field", fieldName);
            } else {
                column.setUrlFieldName(fieldName + "_URL");
                columnJSON.put("Field", column.getUrlFieldName());
            }
        }
        return columnJSON;
    }

    /**
     * @param column
     * @return
     */
    private JSONObject createColumnStyle(PortalGridExColumn column) {
        JSONObject json = JSONUtils.parserJSONObject(column.style);
        if (json != null) {
            json.put("defaultStyle", JSONUtils.parserJSONObject("{" + json.getString("defaultStyle") + "}"));
            JSONArray array = json.getJSONArray("specialItems");
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject itemJson = array.getJSONObject(i);
                    itemJson.put("style", JSONUtils.parserJSONObject("{" + itemJson.getString("style") + "}"));
                }
            }
        }
        return json;
    }

    /**
     * 获取布局Layout
     * 
     * @return
     */
    private JSONObject createLayoutJSON() {
        JSONObject json = new JSONObject();
        json.put("layerType", this.layout.getLayerType());
        json.put("aControl", this.layout.getaControl());
        json.put("bControl", this.layout.getbControl());
        json.put("cControl", this.layout.getcControl());
        json.put("aToolbar", this.layout.isToolbarA());
        json.put("bToolbar", this.layout.isToolbarB());
        json.put("cToolbar", this.layout.isToolbarC());
        json.put("splitL", this.layout.getSplitL());
        json.put("splitR", this.layout.getSplitR());
        json.put("splitT", this.layout.getSplitT());
        json.put("splitB", this.layout.getSplitB());
        return json;
    }

    @Override
    public String getJavaScript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public PortalGridExLayout getLayer() {
        return layout;
    }

    public List<PortalGridExControl> getControls() {
        return controls;
    }

    /**
     * 注册数据源
     * 
     * @param sqlDefineName
     * @param controlName
     * @param sqlType
     * @param sqlDefine
     */
    public void registerSQLDefine(String sqlDefineName, String controlName, String sqlType, SQLDefineBase sqlDefine) {
        if (!StringUtils.isEmpty(sqlType) && !StringUtils.isEmpty(controlName)) {
            PortalGridExControl control = getPortalGridExControl(controlName);
            if (control != null && control instanceof PortalGridExSQLBase) {
                if (StringUtils.equals("insert", sqlType))
                    ((PortalGridExSQLBase) control).setInsertSQLDefine(sqlDefine);
                else if (StringUtils.equals("update", sqlType))
                    ((PortalGridExSQLBase) control).setUpdateSQLDefine(sqlDefine);
                else if (StringUtils.equals("delete", sqlType))
                    ((PortalGridExSQLBase) control).setDeleteSQLDefine(sqlDefine);
                else if (StringUtils.equals("exchange", sqlType))
                    ((PortalGridExSQLBase) control).setExchangeSQLDefine(sqlDefine);
                else if (StringUtils.equals("rootid", sqlType))
                    ((PortalGridExSQLBase) control).setRootSQLDefine(sqlDefine);
            }
        }
        this.addSQLDefine(sqlDefineName, sqlDefine);
    }

    /**
     * 根据控件名称获取控件实例
     * 
     * @param controlName
     * @return
     */
    public PortalGridExControl getPortalGridExControl(String controlName) {
        for (PortalGridExControl control : this.controls) {
            if (StringUtils.equals(controlName, control.getControlName()))
                return control;
        }
        return null;
    }

    public String getToolbarAdditions() {
        return toolbarAdditions;
    }

    public void setToolbarAdditions(String toolbarAdditions) {
        this.toolbarAdditions = toolbarAdditions;
    }

}
