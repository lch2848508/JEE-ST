package com.estudio.impl.webclient.portal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.webclient.SQLParam4Portal;
import com.estudio.define.webclient.portal.AbstractPortalGridDefine;
import com.estudio.define.webclient.portal.PortalGridColumn;
import com.estudio.define.webclient.portal.PortalGridDefine;
import com.estudio.define.webclient.portal.PortalGridDefineEx;
import com.estudio.define.webclient.portal.PortalGridDefineLayout;
import com.estudio.define.webclient.portal.PortalGridExColumn;
import com.estudio.define.webclient.portal.PortalGridExCommon;
import com.estudio.define.webclient.portal.PortalGridExControl;
import com.estudio.define.webclient.portal.PortalGridExForm;
import com.estudio.define.webclient.portal.PortalGridExGrid;
import com.estudio.define.webclient.portal.PortalGridExIFrame;
import com.estudio.define.webclient.portal.PortalGridExLayout;
import com.estudio.define.webclient.portal.PortalGridExSQLBase;
import com.estudio.define.webclient.portal.PortalGridExSWF;
import com.estudio.define.webclient.portal.PortalGridExTree;
import com.estudio.define.webclient.portal.PortalResources;
import com.estudio.define.webclient.portal.PortalUtils;
import com.estudio.define.webclient.portal.SQLColumnURL;
import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.define.webclient.portal.SQLDefineBase;
import com.estudio.define.webclient.portal.SQLField;
import com.estudio.impl.db.DBSqlUtils;
import com.estudio.intf.db.DBParamDataType;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.webclient.form.IPortal4ClientGridDefineService;
import com.estudio.utils.Convert;
import com.estudio.utils.JSCompress;
import com.estudio.web.service.DataService4Lookup;

public final class DBPortal4ClientGridDefineService implements IPortal4ClientGridDefineService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    // private java.util.Map<Long, PortalGridDefine> gridID2GridDefine =
    // Collections.synchronizedMap(new HashMap<Long, PortalGridDefine>());

    private String getKey(final long id) {
        return "ClientGridDefine-" + id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.service.portal.grid.DPortal4ClientGridDefineService#
     * notifyGridDefineIsChanged(long)
     */
    @Override
    public synchronized void notifyGridDefineIsChanged(final long id) {
        SystemCacheManager.getInstance().removeDesignObject(getKey(id));
        DataService4Lookup.getInstance().deleteDataSetJSON(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.service.portal.grid.DPortal4ClientGridDefineService#
     * getPortalGridDefine(long, java.sql.Connection)
     */
    @Override
    public synchronized AbstractPortalGridDefine getPortalGridDefine(final long id, final Connection con) throws Exception, DocumentException {
        final String cacheKey = getKey(id);
        AbstractPortalGridDefine result = (AbstractPortalGridDefine) SystemCacheManager.getInstance().getDesignObject(cacheKey);
        if (result == null) {
            result = loadPortalGridDefine(con, id);
            SystemCacheManager.getInstance().putDesignObject(cacheKey, result);
        }
        return result;
    }

    /**
     * @return
     */
    protected String getPortalGridDefineByIdSQL() {
        return "select property,version,name,type from sys_portal_item where id=?";
    }

    /**
     * 从数据库中读取数据列表配置
     * 
     * @param con
     * @param id
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     * @throws JSONException
     */
    private AbstractPortalGridDefine loadPortalGridDefine(final Connection con, final long id) throws Exception, DocumentException {
        Connection tempCon = con;
        IDBCommand stmt = null;
        AbstractPortalGridDefine result = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getPortalGridDefineByIdSQL(), true);
            stmt.setParam(1, id);
            stmt.executeQuery();
            if (stmt.next()) {
                final byte[] bs = stmt.getBytes(1);
                if (bs != null) {
                    final Document dom = DocumentHelper.parseText(Convert.bytes2Str(bs));
                    if (stmt.getInt("type") == 5) {
                        result = new PortalGridDefineEx();
                        loadPortalGridDefineEx((PortalGridDefineEx) result, stmt.getInt(2), dom, tempCon);
                    } else {
                        result = new PortalGridDefine();
                        loadPortalGridDefine((PortalGridDefine) result, stmt.getInt(2), dom, tempCon);
                    }
                    result.setPortalName(stmt.getString(3));
                    result.createPortalJSON();
                }
            }
        } finally {
            DBHELPER.closeCommand(stmt);
        }
        return result;
    }

    /**
     * 从XML中读取Layout定义
     * 
     * @param layout
     * @param layoutElement
     */
    private void element2Layout(final PortalGridDefineLayout layout, final Element layoutElement) {
        layout.setLayoutType(layoutElement.attributeValue("LayoutType"));
        layout.setTreeView(Convert.str2Boolean(layoutElement.attributeValue("TreeView")));
        layout.setGridView(Convert.str2Boolean(layoutElement.attributeValue("GridView")));
        layout.setGridPagination(Convert.str2Boolean(layoutElement.attributeValue("GridPagination")));
        layout.setDetailView(Convert.str2Boolean(layoutElement.attributeValue("DetailView")));
        layout.setA(Convert.str2Int(layoutElement.attributeValue("A")));
        layout.setB(Convert.str2Int(layoutElement.attributeValue("B")));
        layout.setC(Convert.str2Int(layoutElement.attributeValue("C")));
        layout.setDetailFor(Convert.str2Int(layoutElement.attributeValue("DetailFor")));
        layout.setDetailType(Convert.str2Int(layoutElement.attributeValue("DetailType")));
        layout.setSplitGridl(Convert.str2Boolean(layoutElement.attributeValue("Spliter")));
        layout.setSplitColumnIndex(Convert.try2Int(layoutElement.attributeValue("SpliterIndex"), 0));

        layout.setTreeFormShowType(Convert.try2Int(layoutElement.attributeValue("TreeFormType"), 0));
        layout.setGridFormShowType(Convert.try2Int(layoutElement.attributeValue("GridFormType"), 0));
        layout.setTreeViewAsGrid(Convert.str2Boolean(layoutElement.attributeValue("TreeViewAsGrid")));
        layout.setAsyncTreeData(Convert.str2Boolean(layoutElement.attributeValue("TreeDataAsyncLoad")));

        layout.setDetailProp(layoutElement.attributeValue("DetailProp"));
        layout.setToolbarSplit(Convert.str2Boolean(layoutElement.attributeValue("SplitToolbar"), true));
        layout.setToolbarTree(Convert.str2Boolean(layoutElement.attributeValue("ToolbarTree"), true));
        layout.setToolbarGrid(Convert.str2Boolean(layoutElement.attributeValue("ToolbarGrid"), true));
        layout.setToolbarAdditions(layoutElement.attributeValue("AdditionToolbarItems"));
        layout.setDetailShowType(Convert.try2Int(layoutElement.attributeValue("DetailShowType"), 0));
        layout.setTreeSingleLevel(Convert.str2Boolean(layoutElement.attributeValue("OnlySingleLevel"), false));
        layout.setGridSupportCheckBox(Convert.str2Boolean(layoutElement.attributeValue("GridSupportCheckBox"), false));
        layout.setTreeSupportCheckBox(Convert.str2Boolean(layoutElement.attributeValue("TreeSupportCheckBox"), false));

        if (layout.getDetailType() == 2)
            PortalUtils.registerBindForms2List(layout.getBindForm(), layoutElement);
        else if (layout.getDetailType() == 3)
            PortalUtils.registerURL2List(layout.getUrls(), layoutElement);

    }

    /**
     * 根据XML节点属性生成SQLDefine
     * 
     * @param treeElement
     * @param includeField
     * @param includeColumn
     * @return
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     */
    private SQLDefine4Portal element2SQLDefine(final Element element, final boolean includeField, final boolean includeColumn, final Connection con) throws Exception, DocumentException {
        String sql = element.element("SQL").getText();
        final Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/");
        sql = p.matcher(sql).replaceAll("$1");
        final SQLDefine4Portal sqlDefine = new SQLDefine4Portal(element.attributeValue("Name"), sql, Convert.try2Int(element.attributeValue("Type"), 0));

        final Element paramsElement = element.element("Params");
        for (int i = 0; i < paramsElement.elements().size(); i++) {
            final Element paramElement = (Element) paramsElement.elements().get(i);
            final String comboBoxItemsXML = StringUtils.trim(paramElement.attributeValue("FilterAddition"));
            boolean isFormDB = false;
            String comboBoxItems = "";
            String comboBoxDB = "";
            String paramValueSQL = "";
            if (!StringUtils.isEmpty(comboBoxItemsXML)) {
                if (StringUtils.startsWith(comboBoxItemsXML, "<") && StringUtils.endsWith(comboBoxItemsXML, ">")) {
                    final Document dom = DocumentHelper.parseText(comboBoxItemsXML);
                    isFormDB = Convert.str2Boolean(dom.getRootElement().attributeValue("FormDB"));
                    comboBoxDB = dom.getRootElement().attributeValue("DB");
                    comboBoxItems = dom.getRootElement().getText();
                } else
                    paramValueSQL = DBSqlUtils.deleteComment(comboBoxItemsXML);
            }
            int FilterWidth = Convert.try2Int(paramElement.attributeValue("FilterWidth"), 80);
            sqlDefine.addParam(new SQLParam4Portal(paramElement.attributeValue("Name"), paramElement.attributeValue("Label"), DBParamDataType.fromInt(Convert.try2Int(paramElement.attributeValue("DataType"), 0)), paramElement.attributeValue("Field"), paramElement.attributeValue("Init"), Convert.str2Boolean(paramElement.attributeValue("IsFilter")), paramElement.attributeValue("FilterControl"),
                    paramElement.attributeValue("FilterAddition"), isFormDB, comboBoxItems, comboBoxDB, StringUtils.equals(paramElement.attributeValue("WholeWord"), "True"), Convert.try2Int(paramElement.attributeValue("Pos"), 1), StringUtils.equals(paramElement.attributeValue("SkipNull"), "True"), paramValueSQL, FilterWidth));
        }

        if (includeField) {
            final Element fieldsElement = element.element("Fields");
            if (fieldsElement != null)
                for (int i = 0; i < fieldsElement.elements().size(); i++) {
                    final Element fieldElement = (Element) fieldsElement.elements().get(i);
                    String visibleAttribValue = fieldElement.attributeValue("Visible");
                    if (StringUtils.isEmpty(visibleAttribValue))
                        visibleAttribValue = "True";
                    boolean isVisible = BooleanUtils.toBoolean(visibleAttribValue);
                    final SQLField sqlField = new SQLField(fieldElement.attributeValue("Name"), fieldElement.attributeValue("Label"), BooleanUtils.toBoolean(fieldElement.attributeValue("Caption")), BooleanUtils.toBoolean(fieldElement.attributeValue("Primary")), fieldElement.attributeValue("DataType"), isVisible);
                    sqlField.setExtProp(fieldElement.attributeValue("ExtProp"));
                    sqlDefine.addField(sqlField);
                }
        }

        if (includeColumn) {
            final Element columnsElement = element.element("Columns");
            if (columnsElement != null)
                for (int i = 0; i < columnsElement.elements().size(); i++) {
                    final Element columnElement = (Element) columnsElement.elements().get(i);
                    String fieldName = columnElement.attributeValue("Name");
                    if (!StringUtils.isEmpty(fieldName))
                        fieldName = StringUtils.substringBetween(fieldName, "[", "]");
                    String clolumnCaption = columnElement.attributeValue("Caption");
                    boolean IsEditingColumn = BooleanUtils.toBoolean(columnElement.attributeValue("EditingColumn"));
                    final PortalGridColumn column = new PortalGridColumn(fieldName, clolumnCaption, columnElement.attributeValue("Width"), IsFixedWidth(columnElement), columnElement.attributeValue("Alignment"), columnElement.attributeValue("Style"), IsEditingColumn);
                    column.setSpecial(BooleanUtils.toBoolean(columnElement.attributeValue("IsSpecial")));
                    column.setEditor(BooleanUtils.toBoolean(columnElement.attributeValue("IsEditor")));
                    column.setCheckBox(BooleanUtils.toBoolean(columnElement.attributeValue("IsCheckBox")));
                    sqlDefine.addColumn(column);

                    final Element URLSElement = columnElement.element("URLS");
                    for (int j = 0; j < URLSElement.elements().size(); j++) {
                        final Element URLElement = (Element) URLSElement.elements().get(j);
                        column.addURL(new SQLColumnURL(StringUtils.substringBetween(URLElement.attributeValue("Type"), "[", "]"), URLElement.attributeValue("Params"), URLElement.attributeValue("Display")));
                    }
                }
        }

        sqlDefine.initCommand(con);

        return sqlDefine;
    }

    /**
     * @param columnElement
     * @return
     */
    private boolean IsFixedWidth(final Element columnElement) {
        return BooleanUtils.toBoolean(columnElement.attributeValue("FixedWidth"));
    }

    /**
     * 从XML中读取定义
     * 
     * @param result
     * @param dom
     * @throws SQLException
     *             , DBException
     * @throws DocumentException
     */
    private void loadPortalGridDefine(final PortalGridDefine portalGridDefine, final int version, final Document dom, final Connection con) throws Exception, DocumentException {

        portalGridDefine.setVersion(version);

        portalGridDefine.setCommonSearch(Convert.str2Boolean(dom.getRootElement().attributeValue("IsCommonSearch")));

        // 栏目布局信息
        final Element layoutElement = dom.getRootElement().element("Layout");
        element2Layout(portalGridDefine.getLayout(), layoutElement);
        portalGridDefine.setPagination(portalGridDefine.getLayout().isGridPagination());

        // 资源
        final Element resourceElement = dom.getRootElement().element("Resources");
        if (resourceElement != null)
            element2PortalResources(portalGridDefine.getPortalResources(), resourceElement);

        // JavaScript脚本
        final Element jsElement = dom.getRootElement().element("JavaScript");
        if (jsElement != null)
            portalGridDefine.getLayout().setJavascript(JSCompress.getInstance().compress(jsElement.getText()));

        final Element SQLRootElement = dom.getRootElement().element("SQLS");
        for (int i = 0; i < SQLRootElement.elements().size(); i++) {
            final Element SQLElement = (Element) SQLRootElement.elements().get(i);
            final String name = SQLElement.attributeValue("Name");
            final SQLDefine4Portal sqlDefine = element2SQLDefine(SQLElement, true, false, con);
            portalGridDefine.addSQLDefine(name, sqlDefine);
            portalGridDefine.addSQLDefine(SQLElement.attributeValue("Caption"), sqlDefine);
            if (sqlDefine.getFieldCount() != 0)
                sqlDefine.initPageAndCountCmd();
        }

        if (portalGridDefine.getLayout().isTreeView()) { // 定义同树相关的SQLDefine
            final Element treeElement = dom.getRootElement().element("Tree");
            final SQLDefine4Portal sqlDefine = element2SQLDefine(treeElement, true, false, con);
            sqlDefine.initSingleCmd();
            portalGridDefine.addSQLDefine("TREE", sqlDefine);
            // 读取绑定表单部分
            PortalUtils.registerBindForms2List(portalGridDefine.treeBindForms, treeElement);
            // registerBindForms(portalGridDefine, treeElement, 0);
            portalGridDefine.getLayout().setTreeSupportNew(portalGridDefine.treeBindForms.size() != 0);
            portalGridDefine.getLayout().setTreeSupportDelete(portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_DELETE_TREE) != null);
            portalGridDefine.getLayout().setTreeSupportExchange(portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_EXCHANGE_TREE) != null);
        }
        if (portalGridDefine.getLayout().isGridView()) { // 定义同列表相关的SQLDefine
            final Element gridElement = dom.getRootElement().element("Grid");
            final SQLDefine4Portal sqlDefine = element2SQLDefine(gridElement, true, true, con);
            sqlDefine.initPageAndCountCmd();
            sqlDefine.initSingleCmd();
            portalGridDefine.addSQLDefine("GRID", sqlDefine);
            // 读取绑定表单部分
            PortalUtils.registerBindForms2List(portalGridDefine.gridBindForms, gridElement);
            // registerBindForms(portalGridDefine, gridElement, 1);
            portalGridDefine.getLayout().setGridSupportNew(portalGridDefine.gridBindForms.size() != 0);
            portalGridDefine.getLayout().setGridSupportDelete(portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_DELETE_GRID) != null);
            portalGridDefine.getLayout().setGridSupportExchange(portalGridDefine.getSQLDefineByBindType(PortalGridDefine.SQLDEFINE_4_EXCHANGE_GRID) != null);
        }

    }

    /**
     * 加载扩展栏目
     * 
     * @param result
     * @param int1
     * @param dom
     * @param con
     * @throws Exception
     */
    private void loadPortalGridDefineEx(PortalGridDefineEx portalGridDefineEx, int version, Document dom, Connection con) throws Exception {
        portalGridDefineEx.setVersion(version);

        // 布局
        PortalGridExLayout layout = portalGridDefineEx.getLayer();
        Element layoutElement = dom.getRootElement().element("Layout");
        layout.setLayerType(layoutElement.attributeValue("LayoutType"));

        layout.setaControl(StringUtils.substringBetween(layoutElement.attributeValue("A"), "[", "]"));
        layout.setbControl(StringUtils.substringBetween(layoutElement.attributeValue("B"), "[", "]"));
        layout.setcControl(StringUtils.substringBetween(layoutElement.attributeValue("C"), "[", "]"));
        layout.setToolbarA(Convert.str2Boolean(layoutElement.attributeValue("TA")));
        layout.setToolbarB(Convert.str2Boolean(layoutElement.attributeValue("TB")));
        layout.setToolbarC(Convert.str2Boolean(layoutElement.attributeValue("TC")));
        layout.setSplitL(Convert.str2Int(layoutElement.attributeValue("SL")));
        layout.setSplitR(Convert.str2Int(layoutElement.attributeValue("SR")));
        layout.setSplitT(Convert.str2Int(layoutElement.attributeValue("ST")));
        layout.setSplitB(Convert.str2Int(layoutElement.attributeValue("SB")));
        layout.setCanHiddenA(false);
        layout.setCanHiddenB(Convert.str2Boolean(layoutElement.attributeValue("HB")));
        layout.setCanHiddenC(Convert.str2Boolean(layoutElement.attributeValue("HC")));

        // 脚本信息
        portalGridDefineEx.setJavascript(JSCompress.getInstance().compress(dom.getRootElement().element("JavaScript").getText()));

        // 控件信息
        List controlElements = dom.getRootElement().element("Controls").elements("Control");
        for (int i = 0; i < controlElements.size(); i++) {
            Element controlElement = (Element) controlElements.get(i);
            String controlName = controlElement.attributeValue("Name");
            String controlComment = controlElement.attributeValue("Comment");
            int controlType = Convert.str2Int(controlElement.attributeValue("ControlType"));
            if (controlType == PortalGridExControl.CONTROL_GRID)
                portalGridDefineEx.getControls().add(element2PortalGridExGrid(controlType, controlName, controlComment, controlElement, con));
            else if (controlType == PortalGridExControl.CONTROL_TREE)
                portalGridDefineEx.getControls().add(element2PortalGridExTree(controlType, controlName, controlComment, controlElement, con));
            else if (controlType == PortalGridExControl.CONTROL_FORM)
                portalGridDefineEx.getControls().add(element2PortalGridExForm(controlType, controlName, controlComment, controlElement, con));
            else if (controlType == PortalGridExControl.CONTROL_SWF)
                portalGridDefineEx.getControls().add(element2PortalGridExSWF(controlType, controlName, controlComment, controlElement, con));
            else if (controlType == PortalGridExControl.CONTROL_IFRAME)
                portalGridDefineEx.getControls().add(element2PortalGridExIFrame(controlType, controlName, controlComment, controlElement, con));
            else if (controlType >= PortalGridExControl.CONTROL_RICHVIEW && controlType <= PortalGridExControl.CONTROL_PAGECONTROL)
                portalGridDefineEx.getControls().add(element2PortalGridExCommon(controlType, controlName, controlComment, controlElement, con));
        }

        // SQLDefine信息
        Element sqlDefinesElement = dom.getRootElement().element("SQLDefines");
        List sqlDefineList = sqlDefinesElement.elements();
        for (int i = 0; i < sqlDefineList.size(); i++) {
            Element sqlDefineElement = (Element) sqlDefineList.get(i);
            SQLDefineBase sqlDefine = element2SQLDefine(sqlDefineElement, true, false, con);
            if (sqlDefine.getFieldCount() != 0 && DBHELPER.getSQLTrans().isSelectSQL(sqlDefine.getSQL()))
                sqlDefine.initPageAndCountCmd();
            String sqlDefineName = sqlDefineElement.attributeValue("Name");
            String controlName = StringUtils.substringBetween(sqlDefineElement.attributeValue("ControlName"), "[", "]");
            String sqlType = StringUtils.substringBetween(sqlDefineElement.attributeValue("SQLType"), "[", "]");
            portalGridDefineEx.registerSQLDefine(sqlDefineName, controlName, sqlType, sqlDefine);
        }

        // 工具条
        portalGridDefineEx.setToolbarAdditions(dom.getRootElement().elementText("Toolbars"));

        // 名称对控件字典
        Map<String, PortalGridExControl> name2Control = new HashMap<String, PortalGridExControl>();
        for (PortalGridExControl control : portalGridDefineEx.getControls())
            name2Control.put(control.getControlName(), control);

        // 计算父子关系
        for (PortalGridExControl control : portalGridDefineEx.getControls()) {
            int controlType = control.getControlType();
            if (controlType == PortalGridExControl.CONTROL_GRID || controlType == PortalGridExControl.CONTROL_TREE) {
                // select
                SQLDefineBase sqlDefine = ((PortalGridExSQLBase) control).getSelectSQLDefine();
                generalControlRelation(name2Control, control, sqlDefine);

                // root id
                sqlDefine = ((PortalGridExSQLBase) control).getRootSQLDefine();
                if (sqlDefine != null)
                    generalControlRelation(name2Control, control, sqlDefine);
            }
        }

        // 控件排序
        Collections.sort(portalGridDefineEx.getControls(), new Comparator<PortalGridExControl>() {
            @Override
            public int compare(PortalGridExControl o1, PortalGridExControl o2) {
                if (o1 instanceof PortalGridExSQLBase && o2 instanceof PortalGridExSQLBase) {
                    if (((PortalGridExSQLBase) o1).getChildControls().contains(o2.getControlName()))
                        return -1;
                    else if (((PortalGridExSQLBase) o2).getChildControls().contains(o1.getControlName()))
                        return 1;
                    else
                        return 0;
                }
                return o1.getControlType() - o2.getControlType();
            }
        });
    }

    /**
     * 
     * @param controlType
     * @param controlName
     * @param controlComment
     * @param controlElement
     * @param con
     * @return
     */
    private PortalGridExControl element2PortalGridExCommon(int controlType, String controlName, String controlComment, Element controlElement, Connection con) {
        PortalGridExCommon common = new PortalGridExCommon(controlName, controlComment, controlType);
        Element optionElement = controlElement.element("Options");
        for (Object attrib : optionElement.attributes())
            common.addParams(((Attribute) attrib).getName(), ((Attribute) attrib).getValue());
        return common;
    }

    /**
     * @param name2Control
     * @param control
     * @param sqlDefine
     */
    private void generalControlRelation(Map<String, PortalGridExControl> name2Control, PortalGridExControl control, SQLDefineBase sqlDefine) {
        for (int i = 0; i < sqlDefine.getParamCount(); i++) {
            SQLParam4Portal param = sqlDefine.getParam(i);
            if (StringUtils.isEmpty(param.getInitSQLName()))
                continue;
            PortalGridExControl pControl = name2Control.get(param.getInitSQLName());
            if (pControl != null && (pControl instanceof PortalGridExSQLBase) && !StringUtils.equals(control.getControlName(), pControl.getControlName())) {
                ((PortalGridExSQLBase) pControl).registerChildControl(control.getControlName());
                ((PortalGridExSQLBase) pControl).registerRelationField(param.getInitField());
            }
        }
    }

    /**
     * 解析生成SWF
     * 
     * @param controlType
     * @param controlName
     * @param controlComment
     * @param controlElement
     * @return
     */
    private PortalGridExControl element2PortalGridExSWF(int controlType, String controlName, String controlComment, Element controlElement, Connection con) {
        String url = controlElement.element("Options").attributeValue("URL");
        boolean isModule = Convert.obj2Boolean(controlElement.element("Options").attributeValue("IsModule"));
        PortalGridExSWF swf = new PortalGridExSWF(controlName, controlComment, controlType, url, isModule);
        return swf;
    }

    /**
     * 解析生成IFrame
     * 
     * @param controlType
     * @param controlName
     * @param controlComment
     * @param controlElement
     * @return
     */
    private PortalGridExControl element2PortalGridExIFrame(int controlType, String controlName, String controlComment, Element controlElement, Connection con) {
        String url = controlElement.element("Options").attributeValue("URL");
        PortalGridExIFrame iframe = new PortalGridExIFrame(controlName, controlComment, controlType, url);
        return iframe;
    }

    /**
     * 解析生成表单
     * 
     * @param controlType
     * @param controlName
     * @param controlComment
     * @param controlElement
     * @return
     */
    private PortalGridExControl element2PortalGridExForm(int controlType, String controlName, String controlComment, Element controlElement, Connection con) {
        PortalGridExForm form = new PortalGridExForm(controlName, controlComment, controlType);
        PortalUtils.registerBindForms2List(form.getBindForms(), controlElement.element("BindForm"));
        // 读取Options
        Element optionElement = controlElement.element("Options");
        for (int i = 0; i < optionElement.attributeCount(); i++) {
            Attribute attr = optionElement.attribute(i);
            form.getOptions().put(attr.getName(), attr.getValue());
        }
        return form;
    }

    /**
     * 解析生成Tree
     * 
     * @param controlType
     * @param controlName
     * @param controlComment
     * @param controlElement
     * @return
     * @throws Exception
     * @throws DocumentException
     */
    private PortalGridExControl element2PortalGridExTree(int controlType, String controlName, String controlComment, Element controlElement, Connection con) throws Exception {
        PortalGridExTree tree = new PortalGridExTree(controlName, controlComment, controlType);

        // 数据定义
        tree.setSelectSQLDefine(element2SQLDefine(controlElement.element("SQLDefine"), true, false, con));
        tree.getSelectSQLDefine().initSingleCmd();

        // 数据绑定
        PortalUtils.registerBindForms2List(tree.getBindForms(), controlElement.element("BindForm"));

        // 读取Options
        Element optionElement = controlElement.element("Options");
        for (int i = 0; i < optionElement.attributeCount(); i++) {
            Attribute attr = optionElement.attribute(i);
            tree.getOptions().put(attr.getName(), attr.getValue());
        }

        // 其他属性
        tree.setSingleLevel(Convert.str2Boolean(tree.getOptions().get("SingleLevel")));
        tree.setAsyncLoad(Convert.str2Boolean(tree.getOptions().get("AsyncLoad")));
        return tree;
    }

    /**
     * 解析生成Grid
     * 
     * @param controlType
     * @param controlName
     * @param controlComment
     * @param controlElement
     * @return
     * @throws Exception
     * @throws
     */
    private PortalGridExControl element2PortalGridExGrid(int controlType, String controlName, String controlComment, Element controlElement, Connection con) throws Exception {
        PortalGridExGrid grid = new PortalGridExGrid(controlName, controlComment, controlType);

        // 数据定义
        grid.setSelectSQLDefine(element2SQLDefine(controlElement.element("SQLDefine"), true, false, con));
        grid.getSelectSQLDefine().initSingleCmd();

        // 数据绑定
        PortalUtils.registerBindForms2List(grid.getBindForms(), controlElement.element("BindForm"));

        // 数据列定义
        List columnList = controlElement.element("Columns").elements("Column");
        for (int i = 0; i < columnList.size(); i++) {
            Element columnElement = (Element) columnList.get(i);
            String fieldName = StringUtils.substringBetween(columnElement.attributeValue("Name"), "[", "]");
            String caption = columnElement.attributeValue("Caption");
            String width = columnElement.attributeValue("Width");
            boolean isFixedWidth = Convert.str2Boolean(columnElement.attributeValue("FixedWidth"));
            String alignment = columnElement.attributeValue("Alignment");
            String style = columnElement.attributeValue("Style");
            boolean editColumn = Convert.str2Boolean(columnElement.attributeValue("EditingColumn"));
            String editorClass = columnElement.attributeValue("Control");
            String editorProperty = columnElement.attributeValue("ExtProp");
            boolean editable = Convert.str2Boolean(columnElement.attributeValue("Editable"));
            PortalGridExColumn column = new PortalGridExColumn(fieldName, caption, width, isFixedWidth, alignment, style, editColumn, editorClass, editorProperty, editable);
            final Element URLSElement = columnElement.element("URLS");
            for (int j = 0; j < URLSElement.elements().size(); j++) {
                final Element URLElement = (Element) URLSElement.elements().get(j);
                column.addURL(new SQLColumnURL(StringUtils.substringBetween(URLElement.attributeValue("Type"), "[", "]"), URLElement.attributeValue("Params"), URLElement.attributeValue("Display")));
            }
            grid.getColumns().add(column);
            if (!StringUtils.isEmpty(fieldName))
                grid.getColumnFields().add(fieldName);

        }

        // 读取Options
        Element optionElement = controlElement.element("Options");
        for (int i = 0; i < optionElement.attributeCount(); i++) {
            Attribute attr = optionElement.attribute(i);
            grid.getOptions().put(attr.getName(), attr.getValue());
        }

        // 其他属性
        grid.setGroupAble(Convert.str2Boolean(grid.getOptions().get("GroupColumn")));
        grid.setGroupField(grid.getOptions().get("GroupField"));
        grid.setPagination(Convert.str2Boolean(grid.getOptions().get("SupportPaging")));
        if (grid.isPagination())
            grid.getSelectSQLDefine().initPageAndCountCmd();

        grid.generalNoColumnFields();

        return grid;
    }

    /**
     * 读取资源信息
     * 
     * @param portalResources
     * @param resourceElement
     */
    private void element2PortalResources(final PortalResources portalResources, final Element resourceElement) {
        final List<?> el = resourceElement.elements();
        for (int i = 0; i < el.size(); i++) {
            final Element e = (Element) el.get(i);
            portalResources.addResource(e.attributeValue("Name"), e.attributeValue("Text"), e.attributeValue("Title"), e.attributeValue("Icon"));
        }
    }

    private DBPortal4ClientGridDefineService() {
    }

    private static final IPortal4ClientGridDefineService INSTANCE = new DBPortal4ClientGridDefineService();

    public static IPortal4ClientGridDefineService getInstance() {
        return INSTANCE;
    }
}
