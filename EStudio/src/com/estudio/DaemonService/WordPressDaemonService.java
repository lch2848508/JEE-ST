package com.estudio.DaemonService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.ThreadUtils;

public class WordPressDaemonService {
    private int sleepMinutes = 5;
    private IDBHelper DBHELPER = RuntimeContext.getDbHelper();
    private boolean working = false;
    private List<String> categorys = null;

    public boolean isWorking() {
        return working;
    }

    private WordPressDaemonService() {
    }

    /**
     * 初始化参数
     * 
     * @param url
     * @param user
     * @param password
     * @param sleepMinutes
     * @throws Exception
     */
    public void initParams(String url, String user, String password, int sleepMinutes) {
        try {
            wordpress.init(url, user, password);
            this.sleepMinutes = sleepMinutes;
            working = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取WordPress的分类
     * 
     * @return
     */
    public List<String> getWordPressCatetorys() {
        try {
            if (isWorking() && categorys == null)
                categorys = wordpress.getCategories();
        } catch (Exception e) {
        }
        return categorys;
    }

    /**
     * 运行同步线程
     * 
     * @throws Exception
     */
    protected void executeSync() throws Exception {
        Connection con = null;
        Statement readStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insStmt = null;
        try {
            con = DBHELPER.getConnection();
            updateStmt = con.prepareStatement("update sys_ora2wordpress set posttimes = nvl(posttimes,0)+1,ISVALID=?,post_id=? where id=?");
            readStmt = con.createStatement();

            // 发送或编辑新闻
            String sql = "select id, title, content, category, post_id, keywords, create_datetime from sys_ora2wordpress where isvalid=1 order by id ";
            ResultSet rs = readStmt.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                String content = Convert.bytes2Str(rs.getBytes("content"));
                String category = rs.getString("category");
                String keyword = rs.getString("keywords");
                Date createDate = rs.getDate("create_datetime");
                long post_id = rs.getLong("post_id");
                boolean isOK = false;
                try {
                    if (post_id == -1)
                        isOK = (post_id = wordpress.post(category, title, content, keyword, createDate)) != -1;
                    else
                        isOK = wordpress.edit(post_id, category, title, content, keyword, createDate) != -1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateStmt.setInt(1, isOK ? 0 : 1);
                updateStmt.setLong(2, post_id);
                updateStmt.setLong(3, id);
                updateStmt.execute();
            }

            // 删除新闻
            sql = "select id, post_id from sys_ora2wordpress t where isvalid=0 and post_id!=-1 and t.deadtime<sysdate";
            rs = readStmt.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong("id");
                long post_id = rs.getLong("post_id");
                boolean isOK = false;
                try {
                    isOK = wordpress.delete(post_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isOK) {
                    updateStmt.setInt(1, 0);
                    updateStmt.setLong(2, -1);
                    updateStmt.setLong(3, id);
                    updateStmt.execute();
                }
            }

            // 读取新闻列表到本数据库中
            List<JSONObject> list = wordpress.getNews(20);
            if (!list.isEmpty()) {
                DBHELPER.beginTransaction(con);
                DBHELPER.execute("delete from SYS_WORDPRESS_NEWS", con);
                insStmt = con.prepareStatement("insert into sys_wordpress_news (id, caption, category, pub_date, url) values (seq_for_j2ee_uniqueid.nextval, ?, ?, ?, ?)");

                for (JSONObject json : list) {
                    insStmt.setString(1, json.getString("title"));
                    insStmt.setString(2, json.getString("category"));
                    insStmt.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(json.get("create_date")));
                    String url = json.getString("url");
                    url = "/" + StringUtils.substringAfter(StringUtils.substringAfter(url, "http://"), "/");
                    insStmt.setString(4, url);
                    insStmt.execute();
                }
                DBHELPER.commit(con);
            }
        } finally {
            DBHELPER.closeStatement(updateStmt);
            DBHELPER.closeStatement(readStmt);
            DBHELPER.closeStatement(insStmt);
            DBHELPER.endTransaction(con);
            DBHELPER.closeConnection(con);
        }
    }

    /**
     * 开始后台进程
     */
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        executeSync();
                    } catch (Exception e) {
                    }
                    ThreadUtils.sleepMinute(sleepMinutes);
                }
            }
        }).start();
    }

    private WordPress wordpress = new WordPress();
    private static WordPressDaemonService instance = new WordPressDaemonService();

    public static WordPressDaemonService getInstance() {
        return instance;
    }

}
