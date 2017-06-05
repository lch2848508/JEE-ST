package com.estudio.impl.design.objects;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import com.estudio.context.NotifyService4Cluster;
import com.estudio.context.RuntimeContext;
import com.estudio.impl.webclient.query.QueryUIDefineService;
import com.estudio.intf.db.IDBHelper;
import com.estudio.intf.design.objects.IObjectQueryService;
import com.estudio.utils.Convert;

public class DBObjectQueryService implements IObjectQueryService {

    private static final IDBHelper DBHELPER = RuntimeContext.getDbHelper();

    @Override
    public JSONObject save(final long id, final String content) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection con = null;
        try {
            con = DBHELPER.getConnection();
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", id);
            params.put("content", Convert.str2Bytes(content));
            if (Convert.obj2Int(DBHELPER.executeScalar("select count(*) from sys_object_query where id=:id", params, con), 0) == 0)
                DBHELPER.execute("insert into sys_object_query (id,content,version) values (:id,:content,1)", params, con);
            else DBHELPER.execute("update sys_object_query set content=:content,version=version+1 where id=:id", params, con);
            json.put("r", true);
            QueryUIDefineService.getInstance().notifyQueryUIDefineIsChanged(id);
            NotifyService4Cluster.getInstance().notifyClusterMessage(3, id, 0, con);
        } finally {
            DBHELPER.closeConnection(con);
        }
        return json;
    }

    @Override
    public JSONObject get(final Connection con, final long id) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("r", false);
        Connection tempCon = con;
        java.sql.PreparedStatement stmt = null;
        try {
            if (tempCon == null)
                tempCon = DBHELPER.getConnection();
            stmt = tempCon.prepareStatement("select content from sys_object_query where id=?");
            stmt.setLong(1, id);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("content", Convert.bytes2Str(rs.getBytes(1)));
                json.put("r", true);
            }
        } finally {
            DBHELPER.closeStatement(stmt);
            if (con != tempCon)
                DBHELPER.closeConnection(tempCon);
        }
        return json;
    }

    @Override
    public JSONObject get(final long id) throws Exception {
        return get(null, id);
    }

    protected DBObjectQueryService() {
        super();
    }

}
