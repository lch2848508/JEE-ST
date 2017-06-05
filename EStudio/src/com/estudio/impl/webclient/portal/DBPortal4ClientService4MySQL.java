package com.estudio.impl.webclient.portal;

import java.sql.Connection;

import com.estudio.impl.db.DBHelper;

/**
 * ��������Clientҳ�����</br> ���ౣ֤ȫ��ֻ��һ��ʵ��</br> �޹���������� ���ж����̰߳�ȫ</br>
 * ���û��漰�汾��Ϣ����ٶ�</br> ��Ϊ���ճ��������һ���û�ͬʱ��¼�������˻��治ʹ���̰߳�ȫ��ʽ</br>
 * 
 * @author Administrator
 * 
 */
public final class DBPortal4ClientService4MySQL extends DBPortal4ClientService {
    /**
     * ��ͨ�û���ȡ��Ŀ���б�
     * 
     * @return
     */
    @Override
    protected String getPortalItemListSQL4CommonUser() {
        return "select a.id,a.name,b.readable,b.writeable,a.type,property,a.p_id,replace(icon, '.bmp', '.png') icon,a.win,a.autorun,a.disableclose,a.ishidden from sys_portal_item a, (select sum(readable) readable,sum(writeable) writeable,portal_id from sys_portal_right where role_id in (select r_id from sys_user2role where u_id = ?) group by portal_id) b where a.id = b.portal_id and a.published = 1 order by a.sortorder";
    }

    /**
     * ��ͨ�û���ȡ��Ŀ���б�
     * 
     * @return
     */
    @Override
    protected String getPortalGroupList4CommonUser() {
        return "select a.id,a.name, b.portal_right,replace(icon,'.bmp','.png') icon from sys_portal_group a, (select sum(readable + writeable) portal_right, portal_id from sys_portal_right where role_id in (select r_id from sys_user2role where u_id = ?) group by portal_id) b where a.id = b.portal_id and a.published = 1 order by a.sortorder";
    }

    /**
     * ����Ա��ȡ��Ŀ���б�
     * 
     * @return
     */
    @Override
    protected String getPortalItemListSQL4Admin() {
        return "select a.id, a.name, 1 readable, 1 writeable, a.type, property, a.p_id, replace(icon, '.bmp', '.png') icon, a.win,a.autorun,a.disableclose,a.ishidden from sys_portal_item a where a.published = 1 order by a.sortorder";
    }

    /**
     * ����Ԫ��ȡ��Ŀ���б�
     * 
     * @return
     */
    @Override
    protected String getPortalGroupListSQL4Admin() {
        return "select a.id,a.name, 2 portal_right,replace(icon,'.bmp','.png') icon from sys_portal_group a order by a.sortorder";
    }

    private DBPortal4ClientService4MySQL() {
        super();
    }

    private static final DBPortal4ClientService4MySQL INSTANCE = new DBPortal4ClientService4MySQL();

    public static DBPortal4ClientService4MySQL getInstance() {
        return INSTANCE;
    }

    @Override
    protected long getUserManagerVersion() throws Exception {
        Connection con = null;
        long result = -1;
        try {
            result = DBHELPER.executeScalarLong("select fun_usermanager_get_version()", con);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return result;
    }

}
