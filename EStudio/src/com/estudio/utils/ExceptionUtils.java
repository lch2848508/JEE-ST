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
     * 打印错误日志
     * 
     * @param e
     */
    public static void printExceptionTrace(Exception e) {
        e.printStackTrace();
        errorLogger.error(e);
    }

    /**
     * 记录错误日志
     * 
     * @param e
     * @return
     */
    public static String loggerException(final Exception e) {
        return loggerException(e, null);
    }

    /**
     * 记录日志信息
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
            return errorMsg; // 发送到客户端的错误信息 不需要记录到日志
        if (c instanceof DBException) { // 数据库错误
            traceInfo = ((DBException) c).getTraceInfo();
            traceInfo += "\nDebugInfo:\n" + ((DBException) c).getDebugInfo();
        } else {// 其他错误
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            traceInfo = sw.toString();
        }
        errorLogger.error(traceInfo);
        // 记录到日志
        final long recordId = saveExceptionToDb(errorMsg, traceInfo, con);
        errorMsg = "错误ID:" + recordId + " 错误信息:" + errorMsg;
        return errorMsg;
    }

    /**
     * 保存错误日志到数据库中
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
            String user_name = "系统";
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
