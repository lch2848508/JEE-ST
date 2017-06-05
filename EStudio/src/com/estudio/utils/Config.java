package com.estudio.utils;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public final class Config {
    private static HashMap<String, Config> str2Instance = new HashMap<String, Config>();

    /**
     * 根据配置文件名称得到配置类
     * 
     * @param configName
     * @return
     */
    public static Config getInstance(final String configName) {
        Config result = Config.str2Instance.get(configName);
        if (result == null) {
            result = new Config(configName);
            Config.str2Instance.put(configName, result);
        }
        return result;
    }

    private Document dom = null;

    /**
     * 构造函数
     * 
     * @param configName
     */
    private Config(final String configName) {
        try {
            final File f = new File(configName);
            if (f.exists())
                dom = new SAXReader().read(new File(configName));
        } catch (final DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据Category得到DOM节点
     * 
     * @param category
     * @return
     */

    private Element getCategoryElement(final String category) {
        final Element rootElement = dom.getRootElement();
        for (int i = 0; i < rootElement.elements().size(); i++) {
            final Element e = (Element) rootElement.elements().get(i);
            if ((e.attribute("type") != null) && e.attributeValue("type").equals(category))
                return e;
        }
        return null;
    }

    /**
     * 得到配置字符串
     * 
     * @param category
     * @param key
     * @return
     */
    public String getString(final String category, final String key) {
        return this.getString(category, key, null);
    }

    /**
     * 得到配置的整数
     * 
     * @param category
     * @param key
     * @return
     */
    public int getInt(final String category, final String key) {
        return Convert.try2Int(this.getString(category, key, null), 0);
    }

    /**
     * 得到配置信息
     * 
     * @param category
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(final String category, final String key, final String defaultValue) {
        if (dom == null)
            return defaultValue;
        final Element categoryElement = getCategoryElement(category);
        if (categoryElement == null)
            return defaultValue;
        final Element e = categoryElement.element(key);
        if (e == null)
            return defaultValue;
        return StringUtils.trim(e.getText());
    }

    public double getDouble(String category, String key) {
        return Convert.try2Double(getString(category, key), 0);
    }
}
