package com.estudio.workflow.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.JSCompress;

public class WFActivity {
    long id; // Ψһ��ʶ��
    String name; // ����
    String caption; // ����
    String descript;// ������Ϣ
    String userFilter; // ��Ա����
    WFActivityType type;// �������
    WFTimeLimit timeLimit = new WFTimeLimit(0, WFTimeUnit.WORKDAY); // ʱ������
    String script; // ����ű�
    boolean split; // ����
    boolean join; // ����
    boolean ideaAble = true;// �Ƿ�����ǩ�����
    boolean multiReciver = false; // ������˽���
    boolean backAble; // �Ƿ������˼�
    boolean smartSend = false; // ���ܷ���
    boolean smartSkip = false; // ������ת
    boolean roleAccept = false; // ���͵���ɫ

    Map<String, String> variableValueScript = new HashMap<String, String>(); // ������ֵ�ű�
    List<Long> roles = new ArrayList<Long>(); // ��ɫ��Ϣ
    List<WFFormSetting> forms = new ArrayList<WFFormSetting>(); // ������
    String receiveEvent; // �����¼�
    String sendEvent;// �����¼�
    String validDataEvent; // ����У���¼�
    String backEvent; // �˼��¼�

    /**
     * ��ȡ������
     * 
     * @param formID
     * @return
     */
    public WFFormSetting getFormSetting(final long formID) {
        for (final WFFormSetting wf : forms)
            if (wf.id == formID)
                return wf;
        return null;
    }

    public boolean isSmartSkip() {
        return smartSkip;
    }

    public void setSmartSkip(final boolean smartSkip) {
        this.smartSkip = smartSkip;
    }

    public boolean isRoleAccept() {
        return roleAccept;
    }

    public void setRoleAccept(final boolean roleAccept) {
        this.roleAccept = roleAccept;
    }

    public boolean isSmartSend() {
        return smartSend;
    }

    public void setSmartSend(final boolean smartSend) {
        this.smartSend = smartSend;
    }

    public boolean isBackAble() {
        return backAble;
    }

    public void setBackAble(final boolean backAble) {
        this.backAble = backAble;
    }

    public boolean isIdeaAble() {
        return ideaAble;
    }

    public void setIdeaAble(final boolean ideaAble) {
        this.ideaAble = ideaAble;
    }

    public boolean isMultiReciver() {
        return multiReciver;
    }

    public void setMultiReciver(final boolean multiReciver) {
        this.multiReciver = multiReciver;
    }

    public String getReceiveEvent() {
        return receiveEvent;
    }

    public void setReceiveEvent(final String receiveEvent) {
        if (!StringUtils.isEmpty(receiveEvent))
            this.receiveEvent = JSCompress.getInstance().compress(receiveEvent);
    }

    public String getSendEvent() {
        return sendEvent;
    }

    public void setSendEvent(final String sendEvent) {
        if (!StringUtils.isEmpty(sendEvent))
            this.sendEvent = JSCompress.getInstance().compress(sendEvent);
    }

    public String getBackEvent() {
        return backEvent;
    }

    public void setBackEvent(final String backEvent) {
        this.backEvent = backEvent;
    }

    public List<WFFormSetting> getForms() {
        return forms;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(final String caption) {
        this.caption = caption;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(final String descript) {
        this.descript = descript;
    }

    public WFActivityType getType() {
        return type;
    }

    public void setType(final WFActivityType type) {
        this.type = type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(final String script) {
        this.script = JSCompress.getInstance().compress(script);
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(final boolean split) {
        this.split = split;
    }

    public boolean isJoin() {
        return join;
    }

    public void setJoin(final boolean join) {
        this.join = join;
    }

    public WFTimeLimit getTimeLimit() {
        return timeLimit;
    }

    public Map<String, String> getVariableValueScript() {
        return variableValueScript;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public String getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(final String userFilter) {
        if (!StringUtils.isEmpty(userFilter))
            this.userFilter = JSCompress.getInstance().compress(userFilter);
    }

    public String getValidDataEvent() {
        return validDataEvent;
    }

    public void setValidDataEvent(final String validDataEvent) {
        this.validDataEvent = validDataEvent;
    }

}
