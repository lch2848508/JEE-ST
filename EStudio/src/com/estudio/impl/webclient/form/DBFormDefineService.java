package com.estudio.impl.webclient.form;

import java.sql.Connection;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.estudio.context.RuntimeContext;
import com.estudio.context.SystemCacheManager;
import com.estudio.define.webclient.form.FormDefine;
import com.estudio.intf.db.IDBCommand;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.webclient.form.IFormDefineService;
import com.estudio.utils.Convert;

public final class DBFormDefineService implements IFormDefineService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    // 获取FormDefine定义的SQL语句
    private String getFormDefineSQL() {
        return "select xmlstream,datasource,jsscript,sys_object_forms.type,caption,sys_object_forms.version from sys_object_forms,sys_object_tree where sys_object_forms.id = sys_object_tree.prop_id and sys_object_forms.id=?";
    }

    private String getKey(final long id) {
        return "FormDefine-" + id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.form.IFormDefineService#getFormDefine(long,
     * java.sql.Connection)
     */
    @Override
    public synchronized FormDefine getFormDefine(final long id, final Connection con) throws Exception {
        final String cacheKey = getKey(id);
        FormDefine result = (FormDefine) SystemCacheManager.getInstance().getDesignObject(cacheKey);
        if (result == null) {
            Connection tempCon = con;
            IDBCommand stmt = null;
            try {
                if (tempCon == null)
                    tempCon = DBHELPER.getConnection();
                stmt = DBHELPER.getCommand(tempCon, getFormDefineSQL(), true);
                stmt.setParam(1, id);
                stmt.executeQuery();
                if (stmt.next()) {
                    final Document xmlDOM = DocumentHelper.parseText(Convert.bytes2Str(stmt.getBytes(1)));
                    final Document dsDOM = DocumentHelper.parseText(Convert.bytes2Str(stmt.getBytes(2)));
                    final String jscript = Convert.bytes2Str(stmt.getBytes(3));
                    final int type = stmt.getInt(4);
                    final int version = stmt.getInt(6);
                    result = new FormDefine(xmlDOM, dsDOM, jscript, type == 0, stmt.getString(5), id, version, con);
                    SystemCacheManager.getInstance().putDesignObject(cacheKey, result);
                }
            } finally {
                DBHELPER.closeCommand(stmt);
                if (tempCon != con)
                    DBHELPER.closeConnection(tempCon);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.form.IFormDefineService#getFormDefines(java
     * .lang.String[], java.sql.Connection)
     */
    @Override
    public FormDefine[] getFormDefines(final String[] ids, final Connection con) throws Exception {
        final FormDefine[] result = new FormDefine[ids.length];
        Connection tempCon = con;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            for (int i = 0; i < ids.length; i++)
                result[i] = getFormDefine(Convert.str2Long(ids[i]), tempCon);
        } finally {
            if (tempCon != con)
                DBHELPER.closeConnection(tempCon);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.form.IFormDefineService#getFormMaxSize(com
     * .estudio.define.webclient.form.FormDefine[])
     */
    @Override
    public int[] getFormMaxSize(final FormDefine[] forms) {
        final int[] result = { 0, 0 };
        for (final FormDefine form : forms) {
            if (result[0] < form.getFormWidth())
                result[0] = form.getFormWidth();
            if (result[1] < form.getFormHeight())
                result[1] = form.getFormHeight();
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.form.IFormDefineService#getFormMaxSize(java
     * .lang.String[], java.sql.Connection)
     */
    @Override
    public int[] getFormMaxSize(final String[] ids, final Connection con) throws Exception {
        return getFormMaxSize(getFormDefines(ids, con));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.estudio.service.portal.form.IFormDefineService#notifyFormDefineIsChanged
     * (long)
     */
    @Override
    public synchronized void notifyFormDefineIsChanged(final long id) {
        DataSetCacheService4WebClient.getInstance().unregisterFormDataSet(id);
        SystemCacheManager.getInstance().removeDesignObject(getKey(id));
    }

    // HashMap<Long, FormDefine> formID2FormDefine = new HashMap<Long,
    // FormDefine>();

    private static final IFormDefineService INSTANCE = new DBFormDefineService();

    public static IFormDefineService getInstance() {
        return INSTANCE;
    }

    private DBFormDefineService() {
    }

}
