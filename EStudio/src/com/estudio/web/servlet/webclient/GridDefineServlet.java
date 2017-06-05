package com.estudio.web.servlet.webclient;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.dom4j.DocumentException;

import com.estudio.context.RuntimeContext;
import com.estudio.define.webclient.portal.AbstractPortalGridDefine;
import com.estudio.define.webclient.portal.PortalGridDefine;
import com.estudio.define.webclient.portal.PortalGridDefineEx;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.service.DataService4Portal;
import com.estudio.web.service.DataService4PortalEx;
import com.estudio.web.servlet.BaseServlet;

public class GridDefineServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = 4937275491263194813L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        long time = System.currentTimeMillis();
        response.getWriter().println(procGetDefineAndData(getParams()));
    }

    /**
     * 处理获取数据
     * 
     * @param response
     * @throws JSONException
     * @throws IOException
     */
    private JSONObject procGetDefineAndData(final Map<String, String> httpParams) throws IOException {
        final JSONObject json = new JSONObject();
        json.put("r", false);

        final long ID = getParamLong("id");
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final AbstractPortalGridDefine portalGridInstance = RuntimeContext.getPortal4ClientGridDefineService().getPortalGridDefine(ID, con);
            if (portalGridInstance instanceof PortalGridDefine) {
                PortalGridDefine portalGridDefine = (PortalGridDefine) portalGridInstance;
                procPortalGridDefine(con, ID, portalGridDefine, httpParams, json);
            } else {
                PortalGridDefineEx portalGridDefineEx = (PortalGridDefineEx) portalGridInstance;
                procPortalGridDefineEx(con, ID, portalGridDefineEx, httpParams, json);
            }

        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            JSONUtils.except2JSON(json, e);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 处理扩展栏目
     * 
     * @param con
     * @param iD
     * @param portalGridDefineEx
     * @param httpParams
     * @param httpRequestParams
     * @param json
     * @throws Exception
     */
    private void procPortalGridDefineEx(Connection con, long ID, PortalGridDefineEx portalGridDefineEx, Map<String, String> httpParams, JSONObject json) throws Exception {
        // 栏目ID
        json.put("PORTAL_ITEM_ID", ID);
        json.put("PORTAL_ID", ID);

        // 栏目定义
        json.put("PORTAL_DEFINE", portalGridDefineEx.getPortalJSON());
        json.put("VERSION", portalGridDefineEx.getVersion());

        // 数据
        json.put("INIT_DATA", DataService4PortalEx.getInstance().getInitData4Flex(con, portalGridDefineEx, httpParams));

        // Combobox
        json.put("COMBOBOX_ITEMS", DataService4PortalEx.getInstance().getFilterComboboxItems(con, portalGridDefineEx, httpParams));

        // 标志
        json.put("r", true);
    }

    /**
     * @param con
     * @param ID
     * @param portalGridDefine
     * @param httpParams
     * @param httpRequestParams
     * @param json
     * @throws Exception
     * @throws DocumentException
     */
    private void procPortalGridDefine(Connection con, final long ID, PortalGridDefine portalGridDefine, final Map<String, String> httpParams, final JSONObject json) throws Exception, DocumentException {
        final int[] gridFormSize = RuntimeContext.getFormDefineService().getFormMaxSize(portalGridDefine.getGridBindForms(), con);
        final int[] treeFormSize = RuntimeContext.getFormDefineService().getFormMaxSize(portalGridDefine.getTreeBindForms(), con);

        // 栏目ID
        json.put("PORTAL_ITEM_ID", ID);
        json.put("PORTAL_ID", ID);

        // 绑定表单尺寸
        // JSONUtils.append(json, key, value);
        JSONUtils.append(json, "GRID_FORMS_SIZE", gridFormSize[0]);
        JSONUtils.append(json, "GRID_FORMS_SIZE", gridFormSize[1]);
        JSONUtils.append(json, "TREE_FORMS_SIZE", treeFormSize[0]);
        JSONUtils.append(json, "TREE_FORMS_SIZE", treeFormSize[1]);

        // 栏目定义
        json.put("PORTAL_DEFINE", portalGridDefine.getPortalJSON());

        // 数据
        json.put("COMBOBOX_PARAMS", DataService4Portal.getInstance().getGridComboBoxParamItems(con, ID, httpParams));
        json.put("INIT_DATA", DataService4Portal.getInstance().getInitData4Flex(con, portalGridDefine, httpParams));

        // 脚本
        json.put("JS", portalGridDefine.getJavaScript());

        // 标志
        json.put("r", true);
    }

}
