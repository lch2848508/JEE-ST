package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.Iterator;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.utils.JSONUtils;

public class PortalGridDefine extends AbstractPortalGridDefine {
    PortalGridDefineLayout layout = new PortalGridDefineLayout();
    PortalResources portalResources = new PortalResources();
    public ArrayList<BindForm> treeBindForms = new ArrayList<BindForm>();
    public ArrayList<BindForm> gridBindForms = new ArrayList<BindForm>();
    boolean commonSearch;
    boolean pagination = false;

    public boolean isPagination() {
        return pagination;
    }

    public void setPagination(boolean pagination) {
        this.pagination = pagination;
    }

    public void setCommonSearch(final boolean commonSearch) {
        this.commonSearch = commonSearch;
    }

    public boolean isCommonSearch() {
        return commonSearch;
    }

    public static final long SQLDEFINE_4_DELETE_TREE = 1;
    public static final long SQLDEFINE_4_EXCHANGE_TREE = 2;
    public static final long SQLDEFINE_4_DELETE_GRID = 3;
    public static final long SQLDEFINE_4_EXCHANGE_GRID = 4;
    public static final long SQLDEFINE_4_TREE_ROOT = 5;

    public PortalGridDefineLayout getLayout() {
        return layout;
    }

    /**
     * 得到同Tree绑定的所有表单列表
     * 
     * @return
     */
    public String[] getTreeBindForms() {
        final String[] result = new String[treeBindForms.size()];
        for (int i = 0; i < treeBindForms.size(); i++)
            result[i] = treeBindForms.get(i).getFormId();
        return result;
    }

    /**
     * 得到同Tree绑定的所有表单列表
     * 
     * @return
     */
    public String[] getGridBindForms() {
        final String[] result = new String[gridBindForms.size()];
        for (int i = 0; i < gridBindForms.size(); i++)
            result[i] = gridBindForms.get(i).getFormId();
        return result;
    }

    /**
     * 注册表单绑定
     * 
     * @param formID
     * @param type
     *            type=0 表示同树绑定的表单 其他标识同列表绑定的表单
     * @return
     */
    public BindForm registerBindForm(final String formID, final long type) {
        final BindForm result = new BindForm();
        result.formId = formID;
        if (type == 0)
            treeBindForms.add(result);
        else if (type == 1)
            gridBindForms.add(result);
        return result;
    }

    /**
     * 创建JSON对象
     * 
     * @throws Exception
     * 
     * @throws JSONException
     */
    @Override
    public void createPortalJSON() throws Exception {
        portalJSON.put("Layout", layout.toJSON());
        portalJSON.put("Resources", portalResources.toJSON());
        portalJSON.put("version", version);
        portalJSON.put("IsCommonSearch", commonSearch);

        // 导航树定义
        if (layout.isTreeView()) {
            final JSONObject treeJSON = new JSONObject();
            final SQLDefineBase treeSQLDefine = getSQLDefineByName("TREE");
            for (int i = 0; i < treeSQLDefine.getFieldCount(); i++) {
                final SQLField field = treeSQLDefine.getField(i);
                if (field.isCaption())
                    treeJSON.put("Caption", field.getFieldName());
                else if (field.isKey())
                    treeJSON.put("Key", field.getFieldName());
                else {
                    JSONObject extField = new JSONObject();
                    extField.put("FieldName", field.getFieldName());
                    extField.put("FieldLabel", field.fieldLabel);
                    JSONUtils.append(treeJSON, "ExtFields", extField);
                }
            }

            // 树状视图绑定表单
            treeJSON.put("BindForms", PortalUtils.bindForms2JSON(treeBindForms));

            portalJSON.put("Tree", treeJSON);
        }

        // 数据列表定义
        if (layout.isGridView()) {
            final JSONObject gridJSON = new JSONObject();
            final SQLDefine4Portal gridSQLDefine = (SQLDefine4Portal) getSQLDefineByName("GRID");
            if (layout.gridSupportCheckBox) {
                final JSONObject columnJSON = new JSONObject();
                columnJSON.put("Caption", "__chk__");
                columnJSON.put("Field", "__chk__");
                columnJSON.put("Align", "center");
                columnJSON.put("Width", "25");
                JSONUtils.append(gridJSON, "Columns", columnJSON);
                // gridJSON.append("Columns", columnJSON);
            }
            for (int i = 0; i < gridSQLDefine.getColumnCount(); i++) {
                final PortalGridColumn column = gridSQLDefine.getColumn(i);
                final JSONObject columnJSON = new JSONObject();
                columnJSON.put("Caption", column.getFieldLabel());
                columnJSON.put("Field", column.getFieldName());
                columnJSON.put("Align", column.getAlignment());
                columnJSON.put("Width", StringUtils.isEmpty(column.getWidth()) ? "*" : column.getWidth());
                columnJSON.put("ReadonlyAble", column.isReadonlyAble());
                columnJSON.put("DataType", gridSQLDefine.getFieldDataType(column.getFieldName()));
                columnJSON.put("Style", column.getStyle());
                columnJSON.put("FixedWidth", column.getFixedWidth());
                columnJSON.put("IsLinkColumn", column.getURLCount() != 0);
                columnJSON.put("Special", column.isSpecial());
                columnJSON.put("Editor", column.isEditor());
                columnJSON.put("CheckBox", column.isCheckBox());
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
                JSONUtils.append(gridJSON, "Columns", columnJSON);
                // gridJSON.append("Columns", columnJSON);
            }

            // 数据列表视图绑定表单
            gridJSON.put("BindForms", PortalUtils.bindForms2JSON(gridBindForms));

            final ArrayList<JSONObject> paramsList = new ArrayList<JSONObject>();
            for (int i = 0; i < gridSQLDefine.getParamCount(); i++) {
                final SQLParam4Portal param = gridSQLDefine.getParam(i);
                if (param.isFilter()) {
                    final JSONObject json = new JSONObject();
                    json.put("label", param.getLabel());
                    json.put("name", param.getName());
                    json.put("control", param.getFilterControl());
                    json.put("datatype", param.getDataType());
                    json.put("pos", param.getPos());
                    json.put("controlWidth", param.getControlWidth());
                    paramsList.add(json);
                }
            }
            gridJSON.put("Param", paramsList);
            portalJSON.put("Grid", gridJSON);
        }

    }

    /**
     * 得到树定义
     * 
     * @return
     */
    public SQLDefine4Portal getTreeSQLDefine() {
        return (SQLDefine4Portal) name2SQLDefine.get("TREE");
    }

    /**
     * 得到数据列表定义
     * 
     * @return
     */
    public SQLDefine4Portal getGridSQLDefine() {
        return (SQLDefine4Portal) name2SQLDefine.get("GRID");
    }

    /**
     * 根据绑定类型得到SQL定义
     * 
     * @param type
     * @return
     */
    public SQLDefineBase getSQLDefineByBindType(final long type) {
        SQLDefineBase result = null;
        final Iterator<SQLDefineBase> iterator = name2SQLDefine.values().iterator();
        while (iterator.hasNext()) {
            final SQLDefine4Portal sql = (SQLDefine4Portal) iterator.next();
            if (sql.getBindType() == type) {
                result = sql;
                break;
            }
        }
        return result;
    }

    public PortalResources getPortalResources() {
        return portalResources;
    }

    /**
     * 得到目录树图标
     * 
     * @return
     */
    public String getFolderGif() {
        return portalResources.getFolderGif();
    }

    @Override
    public String getJavaScript() {
        return this.layout.javascript;
    }

}
