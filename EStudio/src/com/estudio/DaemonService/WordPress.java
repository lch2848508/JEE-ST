package com.estudio.DaemonService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.estudio.utils.Convert;

public class WordPress {
    private String user = "";
    private String password = "";
    private XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    private XmlRpcClient xmlRpcClient = new XmlRpcClient();
    private Map<String, Object> category2Struct = new HashMap<String, Object>();

    /**
     * 初始化
     * 
     * @param url
     * @param user
     * @param password
     * @throws Exception
     */
    public void init(String url, String user, String password) throws Exception {
        this.user = user;
        this.password = password;
        config.setServerURL(new URL(url));
        xmlRpcClient.setConfig(config);
    }

    /**
     * 获取所有的类别
     * 
     * @return
     * @throws Exception
     */
    public List<String> getCategories() throws Exception {
        List<String> list = new ArrayList<String>();
        Object[] params = new Object[] { "default", user, password };
        Object[] categorys = (Object[]) xmlRpcClient.execute("metaWeblog.getCategories", params);
        for (Object category : categorys) {
            Map<String, Object> categoryStruct = (Map<String, Object>) category;
            category2Struct.put((String) categoryStruct.get("categoryName"), category);
            list.add((String) categoryStruct.get("categoryName"));
        }
        return list;
    }

    /**
     * 发布新闻
     * 
     * @param title
     * @param descript
     * @param content
     * @param category
     * @return
     * @throws Exception
     */
    public long post(String category, String title, String content, String keyword, Date createDate) throws Exception {
        if (category2Struct.isEmpty())
            getCategories();
        if (!category2Struct.containsKey(category))
            return -1;
        Map<String, Object> post = new HashMap<String, Object>();
        post.put("title", title);
        post.put("description", content);
        post.put("categories", category2Struct.get(category));
        if (createDate != null) {
            processDate2GMT(createDate);
            post.put("dateCreated", createDate);
        }
        if (!StringUtils.isEmpty(keyword))
            post.put("mt_keywords", keyword);
        Object[] params = new Object[] { "default", user, password, post, Boolean.TRUE };
        String result = (String) xmlRpcClient.execute("metaWeblog.newPost", params);
        return Convert.str2Long(result);
    }

    private void processDate2GMT(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (c.get(Calendar.HOUR) + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) != 0)
            date.setTime(date.getTime() - 8 * 3600 * 1000); // 东八区
    }

    /**
     * 编辑新闻
     * 
     * @param post_id
     * @param category
     * @param title
     * @param content
     * @param keyword
     * @param createDate
     * @return
     * @throws Exception
     */
    public long edit(long post_id, String category, String title, String content, String keyword, Date createDate) throws Exception {
        if (category2Struct.isEmpty())
            getCategories();
        if (!category2Struct.containsKey(category))
            return -1;
        Map<String, Object> post = new HashMap<String, Object>();
        post.put("title", title);
        post.put("description", content);
        post.put("categories", category2Struct.get(category));
        if (createDate != null) {
            processDate2GMT(createDate);
            post.put("dateCreated", createDate);
        }
        if (!StringUtils.isEmpty(keyword))
            post.put("mt_keywords", keyword);
        Object[] params = new Object[] { Long.toString(post_id), user, password, post, Boolean.TRUE };
        boolean result = (Boolean) xmlRpcClient.execute("metaWeblog.editPost ", params);
        return result ? post_id : -1;
    }

    /**
     * 删除新闻
     * 
     * @param post_id
     * @return
     * @throws Exception
     */
    public boolean delete(long post_id) throws Exception {
        Object[] params = new Object[] { "default", Long.toString(post_id), user, password, Boolean.TRUE };
        boolean result = (Boolean) xmlRpcClient.execute("metaWeblog.deletePost", params);
        return result;
    }

    /**
     * 发布新闻
     * 
     * @param category
     * @param title
     * @param content
     * @return
     * @throws Exception
     */
    public long post(String category, String title, String content) throws Exception {
        return post(category, title, content, "", null);
    }

    /**
     * 获取新闻列表
     * 
     * @param num
     * @return
     * @throws XmlRpcException
     */
    public List<JSONObject> getNews(int num) throws XmlRpcException {
        List<JSONObject> result = new ArrayList<JSONObject>();
        Object[] params = new Object[] { "default", user, password, num };
        Object[] items = (Object[]) xmlRpcClient.execute("metaWeblog.getRecentPosts", params);
        if (items != null) {
            for (Object item : items) {
                Map<String, Object> infos = (Map<String, Object>) item;
                JSONObject json = new JSONObject();
                json.put("title", infos.get("title"));
                json.put("url", infos.get("link"));
                Object[] categorys = (Object[]) infos.get("categories");
                json.put("category", categorys != null && categorys.length != 0 ? categorys[0] : "");
                json.put("postid", infos.get("postid"));
                json.put("create_date", infos.get("dateCreated"));
                json.put("modify_date", infos.get("date_modified"));
                result.add(json);
            }
        }
        return result;
    }

    /**
     * 发布信息
     * 
     * @param category
     * @param title
     * @param content
     * @param keyword
     * @return
     * @throws Exception
     */
    public long post(String category, String title, String content, String keyword) throws Exception {
        return post(category, title, content, keyword, null);
    }

}
