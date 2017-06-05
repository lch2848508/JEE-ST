package com.estudio.gis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.estudio.net.HttpContent;
import com.estudio.net.WebClient;
import com.estudio.utils.Convert;

public class WebClientWebGIS extends WebClient {

    /**
     * 
     * @param url
     * @param timeoutSecond
     * @param saveFileName
     * @return
     * @throws Exception
     * @throws
     */
    public static WebGISProxyItem getWebGISProxyItem(String url, String saveFileName) throws Exception {
        WebGISProxyItem result = null;

        if (!StringUtils.isEmpty(saveFileName)) {
            File file = new File(saveFileName);
            if (file.exists())
                result = loadWebGISProxyItemFromFile(file);
        }
        if (result == null) {
            HttpContent httpConent = getHttpContent(url, 5 * 60);
            boolean isImage = StringUtils.containsIgnoreCase(httpConent.getContentType(), "image");
            boolean hasError = isImage ? false : StringUtils.containsIgnoreCase(Convert.bytes2Str(httpConent.getContent()), "error");
            result = new WebGISProxyItem();
            result.content = httpConent.getContent();
            result.contentType = httpConent.getContentType();
            result.isError = hasError;
            result.isImage = isImage;
            if (isImage && !StringUtils.isEmpty(saveFileName))
                saveWebGISProxyItemToFile(result, new File(saveFileName));

        }
        return result;
    }

    /**
     * 
     * @param url
     * @param i
     * @param fileName
     * @param params
     * @return
     * @throws Exception
     */
    public static WebGISProxyItem getWebGISProxyItem(String url, String saveFileName, Map<String, String> params) throws Exception {
        WebGISProxyItem result = null;
        if (!StringUtils.isEmpty(saveFileName)) {
            File file = new File(saveFileName);
            if (file.exists())
                result = loadWebGISProxyItemFromFile(file);
        }

        if (result == null) {
            HttpContent httpContent = postHttpContent(url, params, 5 * 60);
            result = new WebGISProxyItem();
            result.content = httpContent.getContent();
            result.contentType = httpContent.getContentType();
            if (!StringUtils.isEmpty(saveFileName))
                saveWebGISProxyItemToFile(result, new File(saveFileName));
        }
        return result;
    }

    /**
     * 
     * @param result
     * @param file
     * @throws Exception
     */
    private static void saveWebGISProxyItemToFile(WebGISProxyItem result, File file) throws Exception {
        Kryo kyro = new Kryo();
        Output out = null;
        try {
            out = new Output(new FileOutputStream(file));
            kyro.writeObject(out, result);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * 
     * @param file
     * @return
     * @throws Exception
     */
    private static WebGISProxyItem loadWebGISProxyItemFromFile(File file) {
        WebGISProxyItem result = null;
        Kryo kyro = new Kryo();
        Input input = null;
        try {
            input = new Input(new FileInputStream(file));
            result = kyro.readObject(input, WebGISProxyItem.class);
        } catch (final Exception e) {

        } finally {
            if (input != null)
                input.close();
        }
        return result;
    }

}