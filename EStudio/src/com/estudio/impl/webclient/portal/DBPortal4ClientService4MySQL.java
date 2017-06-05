package com.estudio.impl.webclient.portal;

import java.sql.Connection;

import com.estudio.impl.db.DBHelper;

/**
 * 用来处理Client页面的类</br> 该类保证全局只有一个实例</br> 无公共类域变量 所有对象都线程安全</br>
 * 利用缓存及版本信息提高速度</br> 因为按照常理不会出现一个用户同时登录的情况因此缓存不使用线程安全方式</br>
 * 
 * @author Administrator
 * 
 */
public final class DBPortal4ClientService4MySQL extends DBPortal4ClientService {
    /**
     * 普通用户获取栏目项列表
     * 
     * @return
     */
    @Override
    protected String getPortalItemListSQL4CommonUser() {
        return "select a.id,a.name,b.readable,b.writeable,a.type,property,a.p_id,replace(icon, '.bmp', '.png') icon,a.win,a.autorun,a.disableclose,a.ishidden from sys_portal_item a, (select sum(readable) readable,sum(writeable) writeable,portal_id from sys_portal_right where role_id in (select r_id from sys_user2role where u_id = ?) group by portal_id) b where a.id = b.portal_id and a.published = 1 order by a.sortorder";
    }

    /**
     * 普通用户获取栏目组列表
     * 
     * @return
     */
    @Override
    protected String getPortalGroupList4CommonUser() {
        return "select a.id,a.name, b.portal_right,replace(icon,'.bmp','.png') icon from sys_portal_group a, (select sum(readable + writeable) portal_right, portal_id from sys_portal_right where role_id in (select r_id from sys_user2role where u_id = ?) group by portal_id) b where a.id = b.portal_id and a.published = 1 order by a.sortorder";
    }

    /**
     * 管理员获取栏目项列表
     * 
     * @return
     */
    @Override
    protected String getPortalItemListSQL4Admin() {
        return "select a.id, a.name, 1 readable, 1 writeable, a.type, property, a.p_id, replace(icon, '.bmp', '.png') icon, a.win,a.autorun,a.disableclose,a.ishidden from sys_portal_item a where a.published = 1 order by a.sortorder";
    }

    /**
     * 管理元获取栏目组列表
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
