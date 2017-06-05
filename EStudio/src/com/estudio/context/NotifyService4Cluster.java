package com.estudio.context;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.estudio.impl.webclient.query.QueryUIDefineService;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.ThreadUtils;
import com.estudio.workflow.web.WorkFlowUIDefineService;

public final class NotifyService4Cluster {
    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private static final String NODENAME = UUID.randomUUID().toString();
    private transient long startIndex = 0;

    private void initStartIndex() {
        try {
            startIndex = Convert.obj2Long(DBHELPER.executeScalar("select coalesce(max(id),0) from sys_notify_service_4_cluster", null), 0L);
        } catch (final Exception e) {

            ExceptionUtils.printExceptionTrace(e);
        }
    }

    private NotifyService4Cluster() {
        super();
    }

    public void start() {
        initStartIndex();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Connection con = null;
                    IDBCommand cmd = null;
                    try {
                        con = DBHELPER.getConnection();
                        cmd = DBHELPER.getCommand(con, "select id,object_id,type,ext from sys_notify_service_4_cluster where nodename=? and id>? order by id");
                        cmd.setParam(1, NODENAME);
                        cmd.setParam(2, startIndex);
                        cmd.executeQuery();
                        while (cmd.next()) {
                            startIndex = cmd.getLong(1);
                            final long objectId = cmd.getLong(2);
                            final int type = cmd.getInt(3);
                            final int ext = cmd.getInt(4);
                            processNotify(objectId, type, ext, con);
                        }
                        ThreadUtils.sleepMinute(5);
                    } catch (final Exception e) {

                        ExceptionUtils.printExceptionTrace(e);
                    } finally {
                        DBHELPER.closeCommand(cmd);
                        DBHELPER.closeConnection(con);
                    }
                }
            }

            /**
             * 处理集群通知消息
             * 
             * @param objectId
             * @param type
             * @param ext
             * @throws Exception
             */
            private void processNotify(final long objectId, final int type, final int ext, final Connection con) throws Exception {
                if (type == 0)
                    RuntimeContext.getReportDefineService().notifyTemplateIsModified(objectId); // OK
                else if (type == 1)
                    RuntimeContext.getWfStorage().notifyWFProcessChange(objectId, ext == 1, con);
                else if (type == 2)
                    RuntimeContext.getFormDefineService().notifyFormDefineIsChanged(objectId); // OK
                else if (type == 3)
                    QueryUIDefineService.getInstance().notifyQueryUIDefineIsChanged(objectId); // OK
                else if (type == 4)
                    RuntimeContext.getPortal4ClientService().notifyPortalSettingChange(); // ok
                else if (type == 5)
                    RuntimeContext.getPortal4ClientGridDefineService().notifyGridDefineIsChanged(objectId); // OK
                else if (type == 6) // OK
                    WorkFlowUIDefineService.getInstance().notifyDesignInfoChange(objectId); // OK
            }

        }).start();
    }

    public void notifyClusterMessage(final int objectType, final long objectId, final int extTag, final Connection con) throws Exception {
        Connection tempCon = con;
        try {
            if (con == null)
                tempCon = DBHELPER.getConnection();
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("object_id", objectId);
            DBHELPER.execute("delete from sys_notify_service_4_cluster where object_id=:object_id", params, tempCon);
            params.put("id", DBHELPER.getUniqueID(tempCon));
            params.put("type", objectType);
            params.put("ext", extTag);
            params.put("nodename", NODENAME);
            DBHELPER.execute("insert into sys_notify_service_4_cluster (id, type, ext, nodename, object_id) values (:id, :type, :ext, :nodename, :object_id)", params, tempCon);
        } finally {
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }
    }

    private static final NotifyService4Cluster INSTANCE = new NotifyService4Cluster();

    public static NotifyService4Cluster getInstance() {
        return INSTANCE;
    }

}
