package com.estudio.web.servlet.webclient;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.estudio.web.service.DataService4AbstractPortal;
import com.estudio.web.service.DataService4Form;
import com.estudio.web.service.DataService4Lookup;
import com.estudio.web.service.DataService4Portal;
import com.estudio.web.service.DataService4PortalEx;
import com.estudio.web.service.DataService4Query;
import com.estudio.web.servlet.BaseServlet;

public class DataServlet extends BaseServlet {

    private static final long serialVersionUID = -8727470088124249910L;

    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final Map<String, String> params = getParams();
        final String operation = getParamStr("o");
        PrintWriter out = response.getWriter();
        if (StringUtils.equals(operation, "getgridjson"))
            out.println(DataService4Portal.getInstance().getGridJSON(null, getParamLong("id"), this.getParamLong("p_id", Long.MIN_VALUE), params));
        else if (StringUtils.equals(operation, "getgridjson4flex"))
            out.println(DataService4Portal.getInstance().getGridJSON4Flex(null, getParamLong("id"), this.getParamLong("p_id", Long.MIN_VALUE), params));
        else if (StringUtils.equals(operation, "deletegrid4flex"))
            out.println(DataService4Portal.getInstance().deleteGridJSON4Flex(null, params));
        else if (StringUtils.equals(operation, "deletegridrow"))
            out.println(DataService4Portal.getInstance().deleteGridRow(getParamLong("id"), getParamLongs("ids")));
        else if (StringUtils.equals(operation, "deletetreenode"))
            out.println(DataService4Portal.getInstance().deleteTreeNode(getParamLong("id"), getParamLongs("ids"), params));
        else if (StringUtils.equals(operation, "exchangegridorder"))
            out.println(DataService4Portal.getInstance().exchangeGridRowSortorder(getParamLong("id"), this.getParamLong("id1"), this.getParamLong("id2")));
        else if (StringUtils.equals(operation, "exchangetreeorder"))
            out.println(DataService4Portal.getInstance().exchangeTreeNodeSortorder(getParamLong("id"), this.getParamLong("id1"), this.getParamLong("id2")));
        else if (StringUtils.equals(operation, "saveform"))
            out.println(DataService4Form.getInstance().saveData4Flex(params, request.getSession()));
        else if (StringUtils.equals(operation, "getdatasetvalue"))
            out.println(DataService4Form.getInstance().getDataSetsData(params, request.getSession()));
        else if (StringUtils.equals(operation, "getformblankdata"))
            out.println(DataService4Form.getInstance().getFormBlankData(params, request.getSession()));
        else if (StringUtils.equals(operation, "executesql"))
            out.println(DataService4AbstractPortal.getInstance().executeSQL(getParamLong("portal_id"), getParamStr("sqlname"), params));
        else if (StringUtils.equals(operation, "batchexecutesql"))
            out.println(DataService4AbstractPortal.getInstance().batchExecuteSQL(getParamLong("portal_id"), getParamStr("sqlname"), params));
        else if (StringUtils.equals(operation, "executedataset"))
            out.println(DataService4Form.getInstance().executeDataSet(params.get("datasetname"), getParamLong("sqltype"), params));
        else if (StringUtils.equals(operation, "batchexecutedataset"))
            out.println(DataService4Form.getInstance().batchExecuteDataSet(params.get("datasetname"), getParamLong("sqltype"), params));
        else if (StringUtils.equals(operation, "getlookupdatasetjson"))
            out.println(DataService4Lookup.getInstance().getGridJSON4Flex(null, getParamLong("portal_id", -1l), params.get("dataset"), params));
        else if (StringUtils.equals("refreshgridselecteditem", operation))
            out.println(DataService4Portal.getInstance().getGridItemJSON4Flex(null, getParamLong("portal_id"), getParamLong("p_id"), params, getParamLong("key")));
        else if (StringUtils.equals("getportaltreedatas", operation))
            out.println(DataService4Portal.getInstance().getTreeData4Flex(null, getParamLong("portal_id"), params));
        else if (StringUtils.equals("getDataSetRecord4ComboBox", operation))
            out.println(DataService4Form.getInstance().getDataSetRecord4ComboBox(null, getParamStr("firstKey"), getParamStr("ds").split(","), getParamStr("firstDS"), getSession().getId()));
        else if (StringUtils.equals("dynamicLoadDataSetRecords4Combobox", operation))
            out.println(DataService4Form.getInstance().getDataSetRecord4ComboBox(null, getParamStr("datasetName"), getParamStr("keyValue"), getParams(), getSession().getId()));
        else if (StringUtils.equals("getDataSetRecords", operation))
            out.println(DataService4Form.getInstance().getDataSetRecords(null, getParamStr("ds").split(","), getParamStr("params"), params, getSession().getId()));
        else if (StringUtils.equals("loadASyncRecord", operation))
            out.println(DataService4Form.getInstance().getASyncDataSetRecord(null, getParamStr("datasetName"), params));
        else if (StringUtils.equalsIgnoreCase("dynamicLoadTreeData", operation))
            out.println(DataService4Portal.getInstance().getTreeData4Flex(null, getParamLong("portal_id"), params));
        else if (StringUtils.equalsIgnoreCase("getPortalExControlRecords", operation))
            out.println(DataService4PortalEx.getInstance().getControlRecords(params));
        else if (StringUtils.equalsIgnoreCase(operation, "deletePortalExControlRecord"))
            out.println(DataService4PortalEx.getInstance().deletePortalExControlRecord(params));
        else if (StringUtils.equalsIgnoreCase(operation, "exchangePortalExControlRecord"))
            out.println(DataService4PortalEx.getInstance().exchangePortalExControlRecord(params));
        else if (StringUtils.equalsIgnoreCase(operation, "refreshPortalGridExSelectedItem"))
            out.println(DataService4PortalEx.getInstance().refreshPortalGridExSelectedItem(params));
        else if (StringUtils.equalsIgnoreCase(operation, "savePortalGridEx"))
            out.println(DataService4PortalEx.getInstance().savePortalGridEx(params));
        else if (StringUtils.equalsIgnoreCase(operation, "getPortalControlFilterComboboxItems"))
            out.println(DataService4PortalEx.getInstance().getAFilterComboboxItems(null, getParamLong("portalId"), getParamStr("controlName"), getParamStr("paramName"), params));
        else if (StringUtils.equalsIgnoreCase(operation, "loadPortalExTreeAsyncRecords"))
            out.println(DataService4PortalEx.getInstance().loadPortalExTreeAsyncRecords(getParamLong("portalId"), getParamStr("controlName"), getParamStr("pid"), params));

    }
}
