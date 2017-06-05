package com.estudio.define.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class DBException extends Exception {
    private static final long serialVersionUID = -8588358242642782318L;
    private final String debugInfo;
    private final String traceInfo;
    private final String errorMsg;
    private final int errorCode;

    public String getDebugInfo() {
        return debugInfo;
    }

    public String getTraceInfo() {
        return traceInfo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    /**
     * ¹¹Ôìº¯Êý
     * 
     * @param debugInfo
     * @param ex
     */
    public DBException(final String debugInfo, final SQLException ex) {
        super();
        this.debugInfo = debugInfo;
        final StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        traceInfo = sw.toString();
        errorMsg = ex.getMessage();
        errorCode = ex.getErrorCode();
    }

    @Override
    public String getMessage() {
        return errorMsg;
    }
}
