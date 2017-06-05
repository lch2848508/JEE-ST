package com.estudio.net;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;

/**
 * HTTP������,��װHttpClient4.3.x�������ṩ�򻯵�HTTP����
 * 
 * @author yangjian1004
 * @Date Aug 5, 2014
 */
public class HttpClient {

    private HttpProxy proxy;

    /**
     * ���ô����������
     * 
     * @param proxy
     */
    public void setProxy(HttpProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * �Ƿ�����SSLģʽ
     * 
     * @param enabled
     */
    public void enableSSL(boolean enabled) {
        HttpClientWrapper.enabledSSL(enabled);
    }

    /**
     * ʹ��Get��ʽ ����URL��ַ,��ȡResponseStatus����
     * 
     * @param url
     *            ������URL��ַ
     * @return ResponseStatus ��������쳣�򷵻�null,���򷵻�ResponseStatus����
     * @throws IOException
     * @throws HttpException
     */
    public ResponseStatus get(String url) throws HttpException, IOException {
        HttpClientWrapper hw = new HttpClientWrapper(proxy);
        return hw.sendRequest(url);
    }

    /**
     * ʹ��Get��ʽ ����URL��ַ,��ȡResponseStatus����
     * 
     * @param url
     *            ������URL��ַ
     * @param urlEncoding
     *            ����,����Ϊnull
     * @return ResponseStatus ��������쳣�򷵻�null,���򷵻�ResponseStatus����
     */
    public ResponseStatus get(String url, String urlEncoding) {
        HttpClientWrapper hw = new HttpClientWrapper(proxy);
        ResponseStatus response = null;
        try {
            response = hw.sendRequest(url, urlEncoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * ������ƴװ��url��,����post����
     * 
     * @param url
     * @return
     */
    public ResponseStatus post(String url) {
        HttpClientWrapper hw = new HttpClientWrapper(proxy);
        ResponseStatus ret = null;
        try {
            setParams(url, hw);
            ret = hw.postNV(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void setParams(String url, HttpClientWrapper hw) {
        String[] paramStr = url.split("[?]", 2);
        if (paramStr == null || paramStr.length != 2) {
            return;
        }
        String[] paramArray = paramStr[1].split("[&]");
        if (paramArray == null) {
            return;
        }
        for (String param : paramArray) {
            if (param == null || "".equals(param.trim())) {
                continue;
            }
            String[] keyValue = param.split("[=]", 2);
            if (keyValue == null || keyValue.length != 2) {
                continue;
            }
            hw.addNV(keyValue[0], keyValue[1]);
        }
    }

    /**
     * �ϴ��ļ�������ͼƬ��
     * 
     * @param url
     *            ����URL
     * @param paramsMap
     *            ������ֵ
     * @return
     */
    public ResponseStatus post(String url, Map<String, Object> paramsMap) {
        HttpClientWrapper hw = new HttpClientWrapper(proxy);
        ResponseStatus ret = null;
        try {
            setParams(url, hw);
            Iterator<String> iterator = paramsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = paramsMap.get(key);
                if (value instanceof File) {
                    FileBody fileBody = new FileBody((File) value);
                    hw.getContentBodies().add(fileBody);
                } else if (value instanceof byte[]) {
                    byte[] byteVlue = (byte[]) value;
                    ByteArrayBody byteArrayBody = new ByteArrayBody(byteVlue, key);
                    hw.getContentBodies().add(byteArrayBody);
                } else {
                    if (value != null && !"".equals(value)) {
                        hw.addNV(key, String.valueOf(value));
                    } else {
                        hw.addNV(key, "");
                    }
                }
            }
            ret = hw.postEntity(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * ʹ��post��ʽ,��������ת�ɵ�json��Rest����
     * 
     * @param url
     * @param jsonBody
     * @return
     */
    public ResponseStatus post(String url, String jsonBody) {
        return post(url, jsonBody, "application/json");
    }

    /**
     * ʹ��post��ʽ,��������ת�ɵ�xml��Rest����
     * 
     * @param url
     *            URL��ַ
     * @param xmlBody
     *            xml�ı��ַ���
     * @return ResponseStatus ��������쳣�򷵻ؿ�,���򷵻�ResponseStatus����
     */
    public ResponseStatus postXml(String url, String xmlBody) {
        return post(url, xmlBody, "application/xml");
    }

    private ResponseStatus post(String url, String body, String contentType) {
        HttpClientWrapper hw = new HttpClientWrapper(proxy);
        ResponseStatus ret = null;
        try {
            hw.addNV("body", body);
            ret = hw.postNV(url, contentType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) throws HttpException, IOException {
        testGet();
        // testUploadFile();
    }

    // test
    public static void testGet() throws HttpException, IOException {
        String url = "http://www.baidu.com/";
        HttpClient c = new HttpClient();
        ResponseStatus r = c.get(url);
        System.out.println(r.getContent());
    }

    // test
    public static void testUploadFile() {
        try {
            HttpClient c = new HttpClient();
            String url = "http://localhost:8280/jfly/action/admin/user/upload.do";
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("userName", "jj");
            paramsMap.put("password", "jj");
            paramsMap.put("filePath", new File("C:\\Users\\yangjian1004\\Pictures\\default (1).jpeg"));
            ResponseStatus ret = c.post(url, paramsMap);
            System.out.println(ret.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}