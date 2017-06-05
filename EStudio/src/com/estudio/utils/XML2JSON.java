package com.estudio.utils;

import java.util.Iterator;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

public class XML2JSON {

    public static JSONObject xml2Json(final Document dom) throws Exception {
        final JSONObject json = new JSONObject();
        json.put(dom.getRootElement().getName(), element2Json(dom.getRootElement()));
        return json;
    }

    private static JSONObject element2Json(final Element element) {
        final JSONObject json = new JSONObject();
        @SuppressWarnings("unchecked")
        final Iterator<Attribute> iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            final Attribute attrib = iterator.next();
            if (!StringUtils.isEmpty(attrib.getValue()))
                json.put(attrib.getName(), attrib.getValue());
        }
        @SuppressWarnings("unchecked")
        final Iterator<Element> ie = element.elementIterator();
        while (ie.hasNext()) {
            final Element e = ie.next();
            JSONUtils.append(json, e.getName(), element2Json(e));
        }
        return json;
    }
}
