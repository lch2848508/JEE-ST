package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.estudio.context.RuntimeContext;
import com.estudio.utils.ExceptionUtils;
import com.estudio.utils.JSONUtils;

public class PortalUtils {
    /**
     * 注册表单到绑定列表中
     * 
     * @param list
     * @param parentElement
     */
    public static void registerBindForms2List(final List<BindForm> list, final Element parentElement) {
        final Element bindFormsElement = parentElement.element("BindForms");
        if (bindFormsElement != null) {
            final List<?> el = bindFormsElement.elements("Form");
            for (int i = 0; i < el.size(); i++) {
                final Element fe = (Element) el.get(i);
                final BindForm bindForm = new BindForm();
                bindForm.setFormId(fe.attributeValue("ID"));
                list.add(bindForm);
                final List<?> pel = fe.element("Params").elements();
                for (int j = 0; j < pel.size(); j++) {
                    final Element pe = (Element) pel.get(j);
                    final String paramName = pe.attributeValue("Name");
                    final String paramValue = StringUtils.substringBetween(pe.attributeValue("Value"), "[", "]");
                    if (!StringUtils.isEmpty(paramValue))
                        bindForm.registerParam(paramName, paramValue);
                }
            }
        }
    }

    /**
     * 注册URL到URL列表中
     * 
     * @param list
     * @param parentElement
     */
    public static void registerURL2List(final ArrayList<URL> list, final Element parentElement) {
        final Element bindFormsElement = parentElement.element("URLS");
        if (bindFormsElement != null) {
            final List<?> el = bindFormsElement.elements("URL");
            for (int i = 0; i < el.size(); i++) {
                final Element fe = (Element) el.get(i);
                final URL url = new URL();
                url.setUrl(fe.attributeValue("URL"));
                list.add(url);
                final List<?> pel = fe.element("Params").elements();
                for (int j = 0; j < pel.size(); j++) {
                    final Element pe = (Element) pel.get(j);
                    final String paramName = pe.attributeValue("Name");
                    final String paramValue = StringUtils.substringBetween(pe.attributeValue("Value"), "[", "]");
                    if (!StringUtils.isEmpty(paramValue))
                        url.registerParam(paramName, paramValue);
                }
            }
        }
    }

    /**
     * 将绑定的表单转化为JSON对象
     * 
     * @param json
     * @throws Exception
     * @throws JSONException
     */
    public static JSONObject bindForms2JSON(final List<BindForm> list) throws Exception {

        if (list.size() > 1) {
            List<String> objectIds = new ArrayList<String>();
            Map<String, BindForm> id2Form = new HashMap<String, BindForm>();
            for (BindForm form : list) {
                objectIds.add(form.formId);
                id2Form.put(form.formId, form);
            }
            RuntimeContext.getObjectTreeService().sortObjects(objectIds);
            list.clear();
            for (String id : objectIds)
                list.add(id2Form.get(id));
        }
        final JSONObject json = new JSONObject();
        final Map<String, String> bindParams = new HashMap<String, String>();
        final ArrayList<String> bindFormIDS = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            final BindForm bindForm = list.get(i);
            bindFormIDS.add(bindForm.getFormId());
            bindParams.putAll(bindForm.getParams());
        }
        json.put("Forms", bindFormIDS);
        json.put("Params", bindParams);
        return json;
    }

    /**
     * 将绑定的表单转化为JSON对象
     * 
     * @param json
     * @throws JSONException
     */
    public static ArrayList<JSONObject> bindUrlsJSON(final ArrayList<URL> list) {
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        for (int i = 0; i < list.size(); i++) {
            final URL url = list.get(i);
            final JSONObject json = new JSONObject();
            json.put("url", url.getUrl());
            json.put("params", url.getParams());
            result.add(json);
        }
        return result;
    }

    /**
     * 生成栏目项的超级连接
     * 
     * @param portalID
     * @param evnParams
     * 
     * @param right
     * @param i
     * 
     * @param int1
     * @param string
     * @return
     */
    public static String generalPortalItemURL(final long type, final String property, final long readable, final long writeable, final long portalID, final Map<String, String> evnParams) {
        String result = property;
        final String right = writeable > 0 ? "2" : readable > 0 ? "1" : "0";
        if (type == 2) {
            if (StringUtils.isEmpty(result))
                return "about:Tabs";
            else if (result.toLowerCase().indexOf("http://") != -1)
                return result;
            else {
                if (result.indexOf("../") == -1)
                    result = "../" + result;
                if (result.indexOf("?") == -1)
                    result += "?portalid=" + portalID;
                else result += "&portalid=" + portalID;
                return result;
            }
        } else if (type == 1)
            return generalBindFormURL(result, right, evnParams);
        else return "../client/showgrid.jsp?id=" + result + "&right=" + right;
    }

    /**
     * 生成表单绑定URL
     * 
     * @param property
     * @param right
     * @param evnParams
     * @return
     */
    private static String generalBindFormURL(final String property, final String right, final Map<String, String> evnParams) {
        String result = "about:Tabs";
        if (!StringUtils.isEmpty(property))
            try {
                final Document dom = DocumentHelper.parseText(property);
                final ArrayList<BindForm> bindForms = new ArrayList<BindForm>();
                PortalUtils.registerBindForms2List(bindForms, dom.getRootElement());
                final ArrayList<String> formIDS = new ArrayList<String>();
                final Map<String, String> params = new HashMap<String, String>();
                for (int i = 0; i < bindForms.size(); i++) {
                    final BindForm bindForm = bindForms.get(i);
                    formIDS.add(bindForm.getFormId());
                    final Iterator<Entry<String, String>> iterator = bindForm.getParamsIterator();
                    while (iterator.hasNext()) {
                        final Entry<String, String> entry = iterator.next();
                        if (!params.containsKey(entry.getKey())) {
                            final String paramValue = evnParams.get(entry.getValue());
                            params.put(entry.getKey(), paramValue);
                        }
                    }
                }
                if (formIDS.size() != 0) {
                    result = "../client/showform.jsp?formids=" + StringUtils.join(formIDS, ",");
                    final Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Entry<String, String> entry = iterator.next();
                        result += "&" + entry.getKey() + "=" + entry.getValue();
                    }
                    result += "&right=" + right;
                }
            } catch (final Exception e) {
                ExceptionUtils.loggerException(e);

            }
        return result;
    }

    /**
     * 将代码解析为JSON对象
     * 
     * @param toolbarAdditions
     * @return
     * @throws JSONException
     */
    public static JSONObject parserToolbarAddition(final JSONObject json, final String toolbarAdditions) {
        JSONObject resultJson = json;
        if (!StringUtils.isEmpty(toolbarAdditions)) {
            if (resultJson == null)
                resultJson = JSONUtils.parserJSONObject(toolbarAdditions);
            if (resultJson.containsKey("Items")) {
                final JSONArray jsonArray = resultJson.getJSONArray("Items");
                if (jsonArray != null)
                    for (int i = 0; i < jsonArray.size(); i++) {
                        final JSONObject itemJson = jsonArray.getJSONObject(i);
                        if (itemJson.containsKey("Items") && !StringUtils.isEmpty(itemJson.getString("Items")))
                            itemJson.put("Items", JSONUtils.parserJSONArray(itemJson.getString("Items")));
                        else itemJson.remove("Items");
                    }
            }
        }
        return resultJson;
    }

}
