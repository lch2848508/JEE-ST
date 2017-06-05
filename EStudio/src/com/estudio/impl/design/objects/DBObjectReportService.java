package com.estudio.impl.design.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.define.db.DBException;
import com.estudio.define.design.objects.ObjectReportRecord;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectReportService;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;

public abstract class DBObjectReportService implements IObjectReportService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    protected abstract String getUpdateSQL();

    protected abstract String getSelectSQL();

    protected abstract String getReportTemplateSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    protected IDBCommand selectCMD;
    protected IDBCommand updateCMD;
    protected IDBCommand insertCMD;
    protected IDBCommand deleteCMD;

    {
        try {
            selectCMD = DBHELPER.getCommand(null, getSelectSQL());
            updateCMD = DBHELPER.getCommand(null, getUpdateSQL());
            insertCMD = DBHELPER.getCommand(null, getInsertSQL());
            deleteCMD = DBHELPER.getCommand(null, getDeleteSQL());
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    public DBObjectReportService() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectReportService#deleteRecord(java
     * .sql.Connection, long)
     */
    @Override
    public boolean deleteRecord(final Connection con, final long id) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = deleteCMD.clone(tempCon);
            cmd.setParam(1, id);
            result = cmd.execute();
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectReportService#fillRecord(com.estudio
     * .intf.db.IDBCommand,
     * com.estudio.define.design.objects.ObjectReportRecord)
     */
    @Override
    public void fillRecord(final IDBCommand cmd, final ObjectReportRecord record) throws Exception {
        record.setId(cmd.getLong("ID"));
        record.setContent(Convert.bytes2Str(cmd.getBytes("CONTENT")));
        record.setVersion(cmd.getLong("VERSION"));
        record.setOld();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectReportService#getRecord(java.sql
     * .Connection, long)
     */
    @Override
    public ObjectReportRecord getRecord(final Connection con, final long id) throws Exception {
        ObjectReportRecord record = null;
        Connection tempCon = con;
        IDBCommand cmd = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            cmd = selectCMD.clone(tempCon);
            cmd.setParam(1, id);
            if (cmd.executeQuery() && cmd.next()) {
                record = new ObjectReportRecord();
                fillRecord(cmd, record);
            }
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return record;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectReportService#getTemplate(java
     * .sql.Connection, long, java.io.OutputStream)
     */
    @Override
    public void getTemplate(final Connection con, final long id, final OutputStream outputStream) throws Exception, IOException, DBException {
        Connection tempCon = con;
        IDBCommand stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = DBHELPER.getCommand(tempCon, getReportTemplateSQL(), true);
            stmt.setParam(1, id);
            stmt.executeQuery();
            if (stmt.next()) {
                final byte[] bs = stmt.getBytes(1);
                outputStream.write(bs);
            }
        } finally {
            outputStream.flush();
            outputStream.close();
            DBHELPER.closeCommand(stmt);
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.estudio.impl.design.objects.IObjectReportService#newRecord()
     */
    @Override
    public ObjectReportRecord newRecord() {
        return new ObjectReportRecord();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.impl.design.objects.IObjectReportService#saveRecord(java.
     * sql.Connection, com.estudio.define.design.objects.ObjectReportRecord)
     */
    @Override
    public boolean saveRecord(final Connection con, final ObjectReportRecord record) throws Exception {
        Connection tempCon = con;
        IDBCommand cmd = null;
        boolean result = false;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            if (record.isNew())
                cmd = insertCMD.clone(tempCon);
            else
                cmd = updateCMD.clone(tempCon);
            cmd.setParam("id", record.getId());
            cmd.setParam("content", Convert.str2Bytes(record.getContent()));
            cmd.setParam("report_params", record.getParams());
            cmd.setParam("template", record.getTemplate());
            record.setOld();
            cmd.execute();
            RuntimeContext.getReportDefineService().notifyTemplateIsModified(record.getId());
            NotifyService4Cluster.getInstance().notifyClusterMessage(0, record.getId(), 0, con);
            result = true;
        } finally {
            DBHELPER.closeCommand(cmd);
            if ((tempCon != null) && (tempCon != con))
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    @Override
    public void downloadTemplate(Connection con, long id, JSONObject json) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select filename,OFFICE_TEMPLATE from SYS_OBJECT_REPORT where (OFFICE_TEMPLATE is not null) and (filename is not null) and id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("filename", rs.getString(1));
                json.put("template", Convert.bytes2Base64(rs.getBytes(2)));
            }
        } finally {
            DBHELPER.closeStatement(stmt);
        }
    }

    @Override
    public void saveTemplate(Connection con, long id, String filename, String templateContent) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("update SYS_OBJECT_REPORT set OFFICE_TEMPLATE=?,filename=?,version=version+1 where id=?");
            stmt.setLong(3, id);
            stmt.setString(2, filename);
            stmt.setBytes(1, Convert.base64ToBytes(templateContent));
            stmt.execute();
        } finally {
            DBHELPER.closeStatement(stmt);
        }

    }

}
