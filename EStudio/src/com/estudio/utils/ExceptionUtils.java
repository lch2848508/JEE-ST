package com.estudio.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.estudio.context.GlobalContext;
import com.estudio.context.RuntimeContext;
import com.estudio.define.ClientException;
import com.estudio.define.db.DBException;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.intf.db.IDBHelper;

public class ExceptionUtils {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    private static Throwable getRootException(final Exception e) {
        Throwable c = e;
        while (c.getCause() != null)
            c = c.getCause();
        return c;
    }

    private static Logger errorLogger = Logger.getLogger("error");

    /**
     * ��ӡ������־
     * 
     * @param e
     */
    public static void printExceptionTrace(Exception e) {
        e.printStackTrace();
        errorLogger.error(e);
    }

    /**
     * ��¼������־
     * 
     * @param e
     * @return
     */
    public static String loggerException(final Exception e) {
        return loggerException(e, null);
    }

    /**
     * ��¼��־��Ϣ
     * 
     * @param e
     * @param con
     * @return
     */
    public static String loggerException(final Exception e, Connection con) {
        ExceptionUtils.printExceptionTrace(e);
        final Throwable c = getRootException(e);
        String errorMsg = c.getMessage();
        String traceInfo = "";
        if (c instanceof ClientException)
            return errorMsg; // ���͵��ͻ��˵Ĵ�����Ϣ ����Ҫ��¼����־
        if (c instanceof DBException) { // ���ݿ����
            traceInfo = ((DBException) c).getTraceInfo();
            traceInfo += "\nDebugInfo:\n" + ((DBException) c).getDebugInfo();
        } else {// ��������
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            traceInfo = sw.toString();
        }
        errorLogger.error(traceInfo);
        // ��¼����־
        final long recordId = saveExceptionToDb(errorMsg, traceInfo, con);
        errorMsg = "����ID:" + recordId + " ������Ϣ:" + errorMsg;
        return errorMsg;
    }

    /**
     * ���������־�����ݿ���
     * 
     * @param errorMsg
     * @param traceInfo
     * @return
     */
    private static long saveExceptionToDb(final String errorMsg, final String traceInfo, Connection con) {
        Connection tempCon = con;
        long recordId = -1;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            recordId = DBHELPER.getUniqueID(tempCon, "SEQ_FOR_J2EE_ERROR_LOGGER");
            final String sql = "insert into sys_error_logger (id, user_id, user_name, error_msg, error_trace_info) values (:id, :user_id, :user_name, :error_msg, :error_trace_info)";
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", recordId);
            long user_id = -1l;
            String user_name = "ϵͳ";
            ClientLoginInfo loginInfo = GlobalContext.getLoginInfo();
            if (loginInfo != null) {
                user_name = loginInfo.getRealName();
                user_id = loginInfo.getId();
            }
            params.put("user_id", user_id);
            params.put("user_name", user_name);
            params.put("error_msg", errorMsg);
            params.put("error_trace_info", Convert.str2Bytes(traceInfo));
            DBHELPER.execute(sql, params, tempCon);
        } catch (final Exception ex) {
        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return recordId;
    }

}
