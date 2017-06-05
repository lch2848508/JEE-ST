package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public class PortalGridDefineLayout {
    String javascript;
    String layoutType;
    boolean treeView;
    boolean gridView;
    boolean gridPagination;
    boolean detailView;
    boolean toolbarSplit;
    boolean toolbarGrid;
    boolean toolbarTree;
    boolean gridSupportCheckBox;
    boolean treeSupportCheckBox;
    boolean treeViewAsGrid;
    boolean asyncTreeData = false;

    int treeFormShowType = 0;
    int gridFormShowType = 0;

    public boolean isAsyncTreeData() {
        return asyncTreeData;
    }

    public void setAsyncTreeData(final boolean asyncTreeData) {
        this.asyncTreeData = asyncTreeData;
    }

    public boolean isTreeViewAsGrid() {
        return treeViewAsGrid;
    }

    public void setTreeViewAsGrid(final boolean treeViewAsGrid) {
        this.treeViewAsGrid = treeViewAsGrid;
    }

    public int getGridFormShowType() {
        return gridFormShowType;
    }

    public void setGridFormShowType(final int gridFormShowType) {
        this.gridFormShowType = gridFormShowType;
    }

    public int getTreeFormShowType() {
        return treeFormShowType;
    }

    public void setTreeFormShowType(final int treeFormShowType) {
        this.treeFormShowType = treeFormShowType;
    }

    public String getToolbarAdditions() {
        return toolbarAdditions;
    }

    public boolean isGridSupportCheckBox() {
        return gridSupportCheckBox;
    }

    public void setGridSupportCheckBox(final boolean gridSupportCheckBox) {
        this.gridSupportCheckBox = gridSupportCheckBox;
    }

    public boolean isTreeSupportCheckBox() {
        return treeSupportCheckBox;
    }

    public void setTreeSupportCheckBox(final boolean treeSupportCheckBox) {
        this.treeSupportCheckBox = treeSupportCheckBox;
    }

    String toolbarAdditions;
    int detailShowType;
    int a;
    int b;
    int c;
    int detailFor;
    int detailType;
    String detailProp;
    boolean splitGridl;
    int splitColumnIndex;
    ArrayList<BindForm> bindForm = new ArrayList<BindForm>();
    ArrayList<URL> urls = new ArrayList<URL>();

    boolean treeSupportNew;
    boolean treeSupportDelete;
    boolean treeSupportExchange;
    boolean gridSupportNew;
    boolean gridSupportDelete;
    boolean gridSupportExchange;
    boolean treeSingleLevel;

    public boolean isTreeSupportNew() {
        return treeSupportNew;
    }

    public void setTreeSupportNew(final boolean treeSupportNew) {
        this.treeSupportNew = treeSupportNew;
    }

    public boolean isTreeSupportDelete() {
        return treeSupportDelete;
    }

    public void setTreeSupportDelete(final boolean treeSupportDelete) {
        this.treeSupportDelete = treeSupportDelete;
    }

    public boolean isTreeSupportExchange() {
        return treeSupportExchange;
    }

    public void setTreeSupportExchange(final boolean treeSupportExchange) {
        this.treeSupportExchange = treeSupportExchange;
    }

    public boolean isGridSupportNew() {
        return gridSupportNew;
    }

    public void setGridSupportNew(final boolean gridSupportNew) {
        this.gridSupportNew = gridSupportNew;
    }

    public boolean isGridSupportDelete() {
        return gridSupportDelete;
    }

    public void setGridSupportDelete(final boolean gridSupportDelete) {
        this.gridSupportDelete = gridSupportDelete;
    }

    public boolean isGridSupportExchange() {
        return gridSupportExchange;
    }

    public void setGridSupportExchange(final boolean gridSupportExchange) {
        this.gridSupportExchange = gridSupportExchange;
    }

    /**
     * 得到URL迭代器
     * 
     * @return
     */
    public Iterator<URL> getURLIterator() {
        return urls.iterator();
    }

    /**
     * 得到布局的外观类型
     * 
     * @return
     */
    public String getLayoutType() {
        return layoutType;
    }

    /**
     * 设置布局的外观类型
     * 
     * @param layoutType
     */
    public void setLayoutType(final String layoutType) {
        this.layoutType = layoutType;
    }

    /**
     * 是否显示Tree
     * 
     * @return
     */
    public boolean isTreeView() {
        return treeView;
    }

    /**
     * 设置是否显示Tree
     * 
     * @param treeView
     */
    public void setTreeView(final boolean treeView) {
        this.treeView = treeView;
    }

    /**
     * 是否显示Grid
     * 
     * @return
     */
    public boolean isGridView() {
        return gridView;
    }

    /**
     * 设置是否显示Grid
     * 
     * @param gridView
     */
    public void setGridView(final boolean gridView) {
        this.gridView = gridView;
    }

    /**
     * Grid是否支持分页
     * 
     * @return
     */
    public boolean isGridPagination() {
        return gridPagination;
    }

    /**
     * 设置Grid是否支持分页
     * 
     * @param gridPagination
     */
    public void setGridPagination(final boolean gridPagination) {
        this.gridPagination = gridPagination;
    }

    /**
     * 是否显示单条详细信息
     */
    public boolean isDetailView() {
        return detailView;
    }

    /**
     * 设置是否显示单条详细信息
     * 
     * @param detailView
     */
    public void setDetailView(final boolean detailView) {
        this.detailView = detailView;
    }

    /**
     * 布局A区域内容类型
     * 
     * @return
     */
    public int getA() {
        return a;
    }

    /**
     * 设置布局A区域内的内容类型
     * 
     * @param a
     */
    public void setA(final int a) {
        this.a = a;
    }

    /**
     * 布局B区域内容类型
     * 
     * @return
     */
    public int getB() {
        return b;
    }

    /**
     * 设置布局b区域内的内容类型
     * 
     * @param b
     */
    public void setB(final int b) {
        this.b = b;
    }

    /**
     * 布局C区域内容类型
     * 
     * @return
     */
    public int getC() {
        return c;
    }

    /**
     * 设置布局C区域内的内容类型
     * 
     * @param c
     */

    public void setC(final int c) {
        this.c = c;
    }

    /**
     * 显示谁的详细信息 0:none 1:Tree 2:Grid
     * 
     * @return
     */
    public int getDetailFor() {
        return detailFor;
    }

    /**
     * 设置显示谁的详细信息 0:none 1:Tree 2:Grid
     * 
     * @return
     */
    public void setDetailFor(final int detailFor) {
        this.detailFor = detailFor;
    }

    /**
     * 详细信息的类别 0:none 1:绑定的表单 2:其他表单 3:外部URL
     * 
     * @return
     */
    public int getDetailType() {
        return detailType;
    }

    /**
     * 设置详细信息的类别 0:none 1:绑定的表单 2:其他表单 3:外部URL
     * 
     * @param detailType
     */
    public void setDetailType(final int detailType) {
        this.detailType = detailType;
    }

    private JSONObject str2JSON(final String str) {
        JSONObject json = null;
        if (!StringUtils.isEmpty(str))
            json = JSONUtils.parserJSONObject(str);
        return json;
    }

    /**
     * 生成JSON对象
     * 
     * @return
     * @throws Exception
     * @throws JSONException
     */
    public JSONObject toJSON() throws Exception {
        final JSONObject json = new JSONObject();
        json.put("LayoutType", layoutType);
        json.put("TreeView", treeView);
        json.put("GridView", gridView);
        json.put("GridPagination", gridPagination);
        json.put("DetailView", detailView);
        json.put("DetailFor", detailFor);
        json.put("DetailType", detailType);
        json.put("SplitGrid", splitGridl);
        json.put("SplitColumnIndex", splitColumnIndex);
        json.put("TreeSupportNew", treeSupportNew);
        json.put("TreeSupportDelete", treeSupportDelete);
        json.put("TreeSupportExchange", treeSupportExchange);
        json.put("GridSupportNew", gridSupportNew);
        json.put("GridSupportDelete", gridSupportDelete);
        json.put("GridSupportExchange", gridSupportExchange);
        json.put("DetailProp", detailType == 2 ? PortalUtils.bindForms2JSON(bindForm) : str2JSON(detailProp));
        json.put("ToolbarSplit", toolbarSplit);
        json.put("ToolbarTree", toolbarTree);
        json.put("ToolbarGrid", toolbarGrid);
        json.put("ToolbarAddition", PortalUtils.parserToolbarAddition(null, toolbarAdditions));
        json.put("DetailShowType", detailShowType);
        json.put("TreeSingleLevel", isTreeSingleLevel());
        json.put("GridSupportCheckBox", isGridSupportCheckBox());
        json.put("TreeSupportCheckBox", isTreeSupportCheckBox());
        json.put("TreeFormShowType", treeFormShowType);
        json.put("GridFormShowType", gridFormShowType);
        json.put("TreeViewAsGrid", treeViewAsGrid);
        json.put("AsyncTreeData", asyncTreeData);

        final HashMap<Integer, String> index2Type = new HashMap<Integer, String>();
        index2Type.put(1, "TreeCell");
        index2Type.put(2, "GridCell");
        index2Type.put(3, "DetailCell");
        index2Type.put(0, "None");
        index2Type.put(-1, "None");

        json.put(index2Type.get(a), "a");
        json.put(index2Type.get(b), "b");
        json.put(index2Type.get(c), "c");

        if (detailType == 2)
            json.put("BindForms", PortalUtils.bindForms2JSON(bindForm));
        else if (detailType == 3)
            json.put("Urls", PortalUtils.bindUrlsJSON(urls));
        return json;
    }

    public boolean isSplitGridl() {
        return splitGridl;
    }

    public void setSplitGridl(final boolean splitGridl) {
        this.splitGridl = splitGridl;
    }

    public int getSplitColumnIndex() {
        return splitColumnIndex;
    }

    public void setSplitColumnIndex(final int splitColumnIndex) {
        this.splitColumnIndex = splitColumnIndex;
    }

    public String getDetailProp() {
        return detailProp;
    }

    /**
     * 设置详细信息属性
     * 
     * @param value
     */
    public void setDetailProp(final String value) {
        detailProp = value;
        if ((detailType == 2) && !StringUtils.isEmpty(value))
            try {
                final Document dom = DocumentHelper.parseText(value);
                PortalUtils.registerBindForms2List(bindForm, dom.getRootElement());
            } catch (final Exception e) {
                ExceptionUtils.loggerException(e);
            }
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(final String javascript) {
        this.javascript = javascript;
    }

    public boolean isToolbarSplit() {
        return toolbarSplit;
    }

    public void setToolbarSplit(final boolean toolbarSplit) {
        this.toolbarSplit = toolbarSplit;
    }

    public boolean isToolbarGrid() {
        return toolbarGrid;
    }

    public void setToolbarGrid(final boolean toolbarGrid) {
        this.toolbarGrid = toolbarGrid;
    }

    public boolean isToolbarTree() {
        return toolbarTree;
    }

    public void setToolbarTree(final boolean toolbarTree) {
        this.toolbarTree = toolbarTree;
    }

    public String isToolbarAdditions() {
        return toolbarAdditions;
    }

    public void setToolbarAdditions(final String toolbarAdditions) {
        this.toolbarAdditions = toolbarAdditions;
        if (!StringUtils.isEmpty(toolbarAdditions))
            this.toolbarAdditions = this.toolbarAdditions.replace(".bmp", ".png");
    }

    public int getDetailShowType() {
        return detailShowType;
    }

    public void setDetailShowType(final int detailShowType) {
        this.detailShowType = detailShowType;
    }

    public boolean isTreeSingleLevel() {
        return treeSingleLevel;
    }

    public void setTreeSingleLevel(final boolean treeSingleLevel) {
        this.treeSingleLevel = treeSingleLevel;
    }

    public ArrayList<BindForm> getBindForm() {
        return bindForm;
    }

    public ArrayList<URL> getUrls() {
        return urls;
    }
}
