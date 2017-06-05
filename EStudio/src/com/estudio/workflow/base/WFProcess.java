package com.estudio.workflow.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.JSONUtils;

public class WFProcess {
    public static final String STATUS_IS_RUNING = "����״̬";

    private long id; // ���������
    private String name; // ����������
    private String descript; // ������������Ϣ
    private int version; // �汾
    private String status;
    private Date createDate;
    private Date modifyDate;
    private String designProperty;

    private final WFTimeLimit timeLimit = new WFTimeLimit(0, WFTimeUnit.WORKDAY); // ������ʱ������
    private final List<WFVariable> variables = new ArrayList<WFVariable>(); // �б�
    private final List<WFActivity> activities = new ArrayList<WFActivity>(); // ���
    private final List<WFLink> links = new ArrayList<WFLink>(); // ������
    private final List<WFFormSetting> forms = new ArrayList<WFFormSetting>(); // ������������
    private final List<Long> roles = new ArrayList<Long>();// ��ɫ����
    private final JSONObject diagramJson = new JSONObject();
    private String finishEvent = ""; // ��������¼�
    private String cancelEvent = ""; // �˼�����¼�

    public List<Long> getRoles() {
        return roles;
    }

    public List<WFFormSetting> getForms() {
        return forms;
    }

    public List<WFActivity> getActivities() {
        return activities;
    }

    public List<WFLink> getLinks() {
        return links;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(final String descript) {
        this.descript = descript;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public WFTimeLimit getTimeLimit() {
        return timeLimit;
    }

    public List<WFVariable> getVariables() {
        return variables;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(final Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    /**
     * ��ȡ��ʼ�ڵ�
     * 
     * @return
     */
    private WFActivity getBeginActivity() {
        WFActivity result = null;
        for (int i = 0; i < activities.size(); i++)
            if (activities.get(i).getType() == WFActivityType.BEGIN) {
                result = activities.get(i);
                break;
            }
        return result;
    }

    /**
     * ��ȡ�ո����
     * 
     * @return
     */
    public WFActivity getFirstActivity() {
        final WFActivity beginActivity = getBeginActivity();
        WFActivity result = null;
        if (beginActivity != null)
            result = getNextActivity(beginActivity);
        return result;
    }

    /**
     * ��ȡ��һ���
     * 
     * @param beginActivity
     * @return
     */
    public WFActivity getNextActivity(final WFActivity activity) {
        WFActivity result = null;
        for (int i = 0; i < links.size(); i++) {
            final WFLink link = links.get(i);
            if (StringUtils.equals(activity.getName(), link.getStartActivityName()) && !StringUtils.isEmpty(link.getEndActivityName())) {
                result = getActivity(link.getEndActivityName());
                break;
            }
        }
        return result;
    }

    /**
     * ��ȡ��һ���
     * 
     * @param activityName
     * @return
     */
    public List<WFActivity> getNextActivities(final String activityName) {
        final List<WFActivity> result = new ArrayList<WFActivity>();
        final List<WFLink> links = getLinks(activityName);
        for (int i = 0; i < links.size(); i++)
            result.add(getActivity(links.get(i).getEndActivityName()));
        return result;
    }

    /**
     * ��ȡ��һ���
     * 
     * @param activity
     * @return
     */
    public List<WFActivity> getNextActivities(final WFActivity activity) {
        return getNextActivities(activity.getName());
    }

    /**
     * ��ȡ�����б�
     * 
     * @param activity
     * @return
     */
    public List<WFLink> getLinks(final WFActivity activity) {
        return getLinks(activity.getName());
    }

    /**
     * ��ȡ�������б�
     * 
     * @param activityName
     * @return
     */
    public List<WFLink> getLinks(final String activityName) {
        final List<WFLink> result = new ArrayList<WFLink>();
        for (int i = 0; i < links.size(); i++)
            if (StringUtils.equals(activityName, links.get(i).getStartActivityName()) && !StringUtils.isEmpty(links.get(i).getEndActivityName()))
                result.add(links.get(i));
        return result;
    }

    /**
     * �������ƻ�ȡ���
     * 
     * @param name
     * @param scriptContextParams
     * @return
     */
    public WFActivity getActivity(final String name) {
        WFActivity result = null;
        for (int i = 0; i < activities.size(); i++)
            if (activities.get(i).getName().equals(name)) {
                result = activities.get(i);
                break;
            }
        return result;
    }

    public String getDesignProperty() {
        return designProperty;
    }

    public void setDesignProperty(final String designProperty) {
        this.designProperty = designProperty;
        activityDesignProperty.clear();
        final JSONObject json = JSONUtils.parserJSONObject(designProperty);
        JSONArray array = json.getJSONArray("Actions");
        for (int i = 0; i < array.size(); i++) {
            final JSONObject activityJson = array.getJSONObject(i);
            activityDesignProperty.put(activityJson.getString("Name"), activityJson);
        }

        // ������Ϣ
        if (json.containsKey("Forms")) {
            final JSONArray formArray = json.getJSONArray("Forms");
            for (int i = 0; i < formArray.size(); i++) {
                final Map<String, String> paramMap = new HashMap<String, String>();
                final JSONObject formJson = formArray.getJSONObject(i);
                final JSONArray paramArray = formJson.getJSONArray("params");
                for (int j = 0; j < paramArray.size(); j++) {
                    final JSONObject p = paramArray.getJSONObject(j);
                    paramMap.put(p.getString("name"), StringUtils.substringBetween(p.getString("value"), "[", "]"));
                }
                if (!paramMap.isEmpty())
                    formParams.put(formJson.getString("ID"), paramMap);
            }
        }

        final JSONObject propertyJson = json.getJSONObject("Property");

        if (propertyJson.containsKey("finishEvent"))
            setFinishEvent(propertyJson.getString("finishEvent"));

        if (propertyJson.containsKey("cancelEvent"))
            setCancelEvent(propertyJson.getString("cancelEvent"));

        // ����UI Diagram JSON;
        int offsetLeft = 65535; // ƫ���� X
        int offsetTop = 65535; // ƫ���� Y
        int diagramWidth = 0;
        int diagramHeight = 0;
        array = json.getJSONArray("Actions");
        for (int i = 0; i < array.size(); i++) {
            final JSONObject activityJson = array.getJSONObject(i);
            final JSONObject aJson = new JSONObject();
            aJson.put("Caption", activityJson.getString("Caption"));
            aJson.put("Type", activityJson.getString("Type"));
            aJson.put("FontColor", activityJson.getInt("FontColor"));
            aJson.put("Background", activityJson.getInt("Background"));
            aJson.put("X", activityJson.getInt("X"));
            aJson.put("Y", activityJson.getInt("Y"));
            aJson.put("W", activityJson.getInt("Width"));
            aJson.put("H", activityJson.getInt("Height"));
            aJson.put("Name", activityJson.getString("Name"));
            offsetLeft = Math.min(offsetLeft, activityJson.getInt("X"));
            offsetTop = Math.min(offsetTop, activityJson.getInt("Y"));
            diagramWidth = Math.max(activityJson.getInt("X") + activityJson.getInt("Width"), diagramWidth);
            diagramHeight = Math.max(activityJson.getInt("Y") + activityJson.getInt("Height"), diagramHeight);
            JSONUtils.append(diagramJson, "actions", aJson);
        }
        array = json.getJSONArray("Links");
        for (int i = 0; i < array.size(); i++) {
            final JSONObject lJson = array.getJSONObject(i);
            final JSONObject aJson = JSONUtils.parserJSONObject(lJson.toString());
            final JSONArray ps = pointsStr2Point(lJson.getString("Points"));
            for (int j = 0; j < ps.size(); j++) {
                offsetLeft = Math.min(offsetLeft, ps.getJSONArray(j).getInt(0));
                offsetTop = Math.min(offsetTop, ps.getJSONArray(j).getInt(1));
                diagramWidth = Math.max(diagramWidth, ps.getJSONArray(j).getInt(0));
                diagramHeight = Math.max(diagramHeight, ps.getJSONArray(j).getInt(1));
            }
            aJson.put("Points", ps);
            JSONUtils.append(diagramJson, "links", aJson);

            linkDesignProperty.put(lJson.getString("Name"), lJson);
        }

        diagramJson.put("offsetLeft", offsetLeft);
        diagramJson.put("offsetTop", offsetTop);
        diagramJson.put("diagramWidth", diagramWidth - offsetLeft);
        diagramJson.put("diagramHeight", diagramHeight - offsetTop);

        // Link����

    }

    /**
     * �ַ���ת��Ϊ����
     * 
     * @param points
     * @return
     */
    private JSONArray pointsStr2Point(final String points) {
        final JSONArray array = JSONUtils.parserJSONArray("[" + StringUtils.replace(StringUtils.replace(points, "(", "["), ")", "]") + "]");
        final JSONArray result = new JSONArray();
        for (int i = 0; i < array.size(); i++)
            if ((i == 0) || (array.getJSONArray(i).getInt(0) != result.getJSONArray(result.size() - 1).getInt(0)) || (array.getJSONArray(i).getInt(1) != result.getJSONArray(result.size() - 1).getInt(1)))
                result.add(array.getJSONArray(i));
        return result;
    }

    // ��ȡ����ͼ��ʽ
    public JSONObject getDiagramJson() {
        return diagramJson;
    }

    private final Map<String, Map<String, String>> formParams = new HashMap<String, Map<String, String>>();

    // �������ʱ����
    private final Map<String, JSONObject> activityDesignProperty = new HashMap<String, JSONObject>();

    // ���������ʱ����
    private final Map<String, JSONObject> linkDesignProperty = new HashMap<String, JSONObject>();

    // ��ȡԭʼ�����ʱ����
    public JSONObject getActivityDesignProperty(final String activityName) {
        return activityDesignProperty.get(activityName);
    }

    // ��ȡ����������
    public JSONObject getLinkDesignProperty(final String linkName) {
        return linkDesignProperty.get(linkName);
    }

    /**
     * ���ݱ������ƻ�ȡ��������
     * 
     * @param varName
     * @return
     */
    public WFVariable getVariable(final String varName) {
        WFVariable result = null;
        for (int i = 0; i < variables.size(); i++)
            if (StringUtils.equals(varName, variables.get(i).getName())) {
                result = variables.get(i);
                break;
            }
        return result;
    }

    /**
     * ��ȡ���еı�����
     * 
     * @return
     */
    public Map<String, Map<String, String>> getFormParams() {
        return formParams;
    }

    /**
     * ��ȡ������
     * 
     * @param formId
     * @return
     */
    public Map<String, String> getFormParams(final String formId) {
        return formParams.get(formId);
    }

    public String getFinishEvent() {
        return finishEvent;
    }

    public void setFinishEvent(final String finishEvent) {
        this.finishEvent = finishEvent;
    }

    public String getCancelEvent() {
        return cancelEvent;
    }

    public void setCancelEvent(final String cancelEvent) {
        this.cancelEvent = cancelEvent;
    }

}
