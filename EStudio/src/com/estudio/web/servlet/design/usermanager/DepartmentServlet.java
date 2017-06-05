package com.estudio.web.servlet.design.usermanager;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.define.design.user.DepartmentRecord;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.user.IDepartmentService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;
import com.estudio.web.servlet.BaseServlet;
import com.estudio.web.servlet.FormValuesContain;

public class DepartmentServlet extends BaseServlet {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final long serialVersionUID = -1675429552446285962L;
    private final IDepartmentService service = RuntimeContext.getDepartmentService();

    /**
     * 处理HTTP请求
     */
    @Override
    protected void doRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().getLoginInfo(request.getSession());
        final String operation = request.getParameter("o");
        if (StringUtils.equals("getInfo", operation))
            response.getWriter().println(getRecordInfo(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("save", operation))
            response.getWriter().println(saveRecord(request));
        else if (StringUtils.equals("delete", operation))
            response.getWriter().println(deleteRecord(getParamLong("id", Long.MIN_VALUE)));
        else if (StringUtils.equals("list", operation))
            response.getWriter().println(listRecords(getParamLong("id", Long.MIN_VALUE), loginInfo.getId()));
        else if (StringUtils.endsWith("exchange", operation))
            response.getWriter().println(exchangeSortorder(getParamLong("id1", Long.MIN_VALUE), getParamLong("id2", Long.MIN_VALUE)));
        else if (StringUtils.endsWith("moveto", operation))
            response.getWriter().println(moveTo(request.getParameter("from"), getParamInt("to", -1)));
    }

    /**
     * 列表数据
     * 
     * @param try2Int
     * @return
     */
    private JSONObject listRecords(final long id, final long uid) {
        final JSONObject json = new JSONObject();
        try {
            final ArrayList<DepartmentRecord> records = service.getRecords(null, id, uid);
            json.put("r", true);
            for (int i = 0; i < records.size(); i++)
                JSONUtils.append(json, "record", records.get(i).getJSON());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);

        }
        return json;
    }

    /**
     * 得到参数数据集
     * 
     * @param request
     * @return
     */
    private FormValuesContain getFormValueContain(final HttpServletRequest request) {
        FormValuesContain result = null;
        try {
            result = FormValuesContain.getInstance(request, getAttachmentPath());
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
        }
        return result;
    }

    /**
     * 保存记录
     * 
     * @param request
     * @return
     */
    private JSONObject saveRecord(final HttpServletRequest request) {
        JSONObject json = null;
        Connection con = null;
        DepartmentRecord record = null;
        try {
            con = DBHELPER.getConnection();
            final FormValuesContain formValueContain = getFormValueContain(request);
            if (formValueContain != null) {
                final long id = Convert.try2Int(formValueContain.getParamValue("id"), -1);
                record = service.getRecord(con, id);
                if (record == null)
                    record = service.newRecord();
                record.setPId(Convert.try2Int(formValueContain.getParamValue("p_id"), -1));
                record.setName(formValueContain.getParamValue("n"));

            } else {
                final long id = getParamLong("id", -1);
                record = service.getRecord(con, id);
                if (record == null)
                    record = service.newRecord();
                record.setPId(getParamLong("p_id", -1l));
                record.setName(request.getParameter("n"));
            }
            service.saveRecord(con, record);
            json = record.getJSON();
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            json = new JSONObject();
            JSONUtils.except2JSON(json, e);

        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 得到记录的JSON对象
     * 
     * @param id
     * @return
     */
    private JSONObject getRecordInfo(final long id) {
        JSONObject json = null;
        try {
            final DepartmentRecord record = service.getRecord(null, id);
            if (record != null) {
                json = record.getJSON();
                json.put("r", true);
            } else {
                json = new JSONObject();
                JSONUtils.except2JSON(json, new Exception("无记录或记录已经被删除！"));
            }
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            json = new JSONObject();
            JSONUtils.except2JSON(json, e);

        }
        return json;
    }

    /**
     * 删除记录
     * 
     * @param try2Int
     * @return
     */
    private JSONObject deleteRecord(final long id) {
        final JSONObject json = new JSONObject();
        try {
            service.deleteRecord(null, id);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);

        }
        return json;
    }

    /**
     * 返回附件的存储路径
     * 
     * @return
     */
    private String getAttachmentPath() {
        return "";
    }

    /**
     * 移动节点
     * 
     * @param parameter
     * @param try2Int
     * @return
     */
    private JSONObject moveTo(final String formIDS, final long toID) {
        final JSONObject json = new JSONObject();
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            con.setAutoCommit(false);
            final String[] ids = formIDS.split(";");
            for (final String id : ids)
                service.moveTo(con, Convert.try2Long(id, -1l), toID);
            con.commit();
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e,con);
            DBHELPER.rollback(con);
            JSONUtils.except2JSON(json, e);

        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    /**
     * 交换节点顺序
     * 
     * @param id1
     * @param id2
     * @return
     */
    private JSONObject exchangeSortorder(final long id1, final long id2) {
        final JSONObject json = new JSONObject();
        try {
            service.exchange(null, id1, id2);
            json.put("r", true);
        } catch (final Exception e) {
            ExceptionUtils.loggerException(e);
            JSONUtils.except2JSON(json, e);

        }
        return json;
    }
}
