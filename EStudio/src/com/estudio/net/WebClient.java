package com.estudio.net;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import wrapper.Http;

import com.estudio.utils.Convert;

public class WebClient {
    private static final String UTF_8 = "utf-8";

    public static String get(String url) throws Exception {
        return get(url, UTF_8);
    }

    /**
     * 获取数据
     * 
     * @param url
     * @param encode
     * @return
     * @throws Exception
     * @throws
     */
    public static String get(String url, String encode) throws Exception {
        Http http = new Http(url, encode);
        http.request();
        return Convert.inputStream2Str(http.getInputStream());
    }

    /**
     * 提交数据
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static String post(String url) throws Exception {
        Http http = new Http(url, UTF_8);
        http.request();
        return Convert.inputStream2Str(http.getInputStream());
    }

    /**
     * 提交数据
     * 
     * @param url
     * @param encode
     * @return
     * @throws Exception
     */
    public static String post(String url, String encode) throws Exception {
        Http http = new Http(url, encode);
        http.request();
        return Convert.inputStream2Str(http.getInputStream());
    }

    /**
     * 提交数据
     * 
     * @param url
     * @param encode
     * @param nameValuePair
     * @return
     * @throws Exception
     * @throws
     */
    public static String post(String url, String encode, Map<String, String> params) throws Exception {
        return post(url, encode, params, 30);
    }

    public static String post(String url, String encode, Map<String, String> params, int timeoutSecond) throws Exception {
        Http http = new Http(url, encode);
        http.setTimeOut(timeoutSecond * 1000);
        for (Entry<String, String> entry : params.entrySet())
            http.addPostData(entry.getKey(), entry.getValue());
        http.request();
        return Convert.inputStream2Str(http.getInputStream());
    }

    /**
     * 下载文件
     * 
     * @param url
     * @param file
     * @param timeoutSecond
     * @return
     * @throws Exception
     */
    public static boolean downloadFile(String url, File file) throws Exception {
        Http http = new Http(url, "utf-8");
        Convert.inputStream2File(http.getInputStream(), file);
        return true;

    }

    /**
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpContent getHttpContent(String url, int timeoutSecond) throws Exception {
        Http http = new Http(url, UTF_8);
        http.setTimeOut(timeoutSecond * 1000);
        http.request();
        return new HttpContent(http.getHeader("Content-Type"), Convert.inputStream2Bytes(http.getInputStream()));
    }

    /**
     * 
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpContent postHttpContent(String url, Map<String, String> params, int timeoutSecond) throws Exception {
        Http http = new Http(url, UTF_8);
        http.setTimeOut(timeoutSecond * 1000);
        for (Entry<String, String> entry : params.entrySet())
            http.addPostData(entry.getKey(), entry.getValue());
        http.request();
        return new HttpContent(http.getHeader("Content-Type"), Convert.inputStream2Bytes(http.getInputStream()));
    }

    public WebClient() {
        super();
    }

    public static void main(String[] args) throws Exception {
        String url = "http://192.168.0.178:6080/arcgis/rest/login?username=jttuser&password=24ba29c5c54a581f5c177938fa6a8b96018118e6b3120e4569a4fb17421f74b5f37e8b867581119b78124f70a8613a03c6cd9b9f6e74a76499bbfccfe08b7c2e&encrypted=true";
        System.out.println(get(url));
        test123();
    }

    private static void test123() throws Exception {
        String url;
        url = "http://192.168.0.178:6080/arcgis/rest/services/JTT/JTTServer2015/MapServer?f=pjson";
        System.out.println(get(url));
    }

}