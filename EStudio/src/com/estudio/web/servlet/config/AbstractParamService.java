package com.estudio.web.servlet.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.estudio.utils.ExceptionUtils;

public class AbstractParamService {

    private final HashMap<String, Element> code2Element = new HashMap<String, Element>();
    private final HashMap<String, ArrayList<Element>> group2ElementList = new HashMap<String, ArrayList<Element>>();
    private String configFileName;
    private Document dom;

    public AbstractParamService() {
        super();
    }

    /**
     * 取得DOM模型
     * 
     * @return
     */
    public Document getDOM() {
        return dom;
    }

    /**
     * 从数据库中读取配置
     * 
     * @return
     */
    public boolean loadConfig() {
        final boolean result = true;
        final List<?> gl = dom.getRootElement().elements("group");
        for (int i = 0; i < gl.size(); i++) {
            final Element ge = (Element) gl.get(i);
            final String groupName = ge.attributeValue("name");
            final ArrayList<Element> el = new ArrayList<Element>();
            final List<?> l = ge.elements("item");
            for (int j = 0; j < l.size(); j++) {
                final Element item = (Element) l.get(j);
                el.add(item);
                code2Element.put(item.attributeValue("name"), item);
            }
            group2ElementList.put(groupName, el);
        }

        return result;
    }

    /**
     * 设置参数
     * 
     * @param code
     * @param value
     */
    public void setParamValue(final String code, final String value) {
        final Element element = code2Element.get(code);
        if (element != null)
            element.addAttribute("value", value);
    }

    /**
     * 取得参数
     * 
     * @param code
     * @return
     */
    public String getParamValue(final String code) {
        return getParamValue(code, "");
    }

    /**
     * 获取参数
     * 
     * @param code
     * @param defValue
     * @return
     */
    public String getParamValue(final String code, String defValue) {
        final Element element = code2Element.get(code);
        if (element != null)
            return element.attributeValue("value");
        return defValue;
    }

    /**
     * 初始化配置文件
     * 
     * @param configFileName
     */
    public void init(final String configFileName) {
        try {
            this.configFileName = configFileName;
            dom = new SAXReader().read(new File(configFileName));
            loadConfig();
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

    /**
     * 保存配置文件
     */
    public void save() {
        try {
            final OutputFormat format = OutputFormat.createPrettyPrint();
            final FileOutputStream fos = new FileOutputStream(configFileName);
            final XMLWriter writer = new XMLWriter(fos, format);
            writer.write(dom);
            writer.close();
        } catch (final IOException e) {
            ExceptionUtils.printExceptionTrace(e);
        }
    }

}
