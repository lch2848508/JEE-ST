package com.estudio.workflow.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.JSCompress;

public class WFActivity {
    long id; // 唯一标识号
    String name; // 名称
    String caption; // 标题
    String descript;// 描述信息
    String userFilter; // 人员过滤
    WFActivityType type;// 活动体类型
    WFTimeLimit timeLimit = new WFTimeLimit(0, WFTimeUnit.WORKDAY); // 时间限制
    String script; // 任务脚本
    boolean split; // 分流
    boolean join; // 合流
    boolean ideaAble = true;// 是否允许签署意见
    boolean multiReciver = false; // 允许多人接收
    boolean backAble; // 是否允许退件
    boolean smartSend = false; // 智能发送
    boolean smartSkip = false; // 智能跳转
    boolean roleAccept = false; // 发送到角色

    Map<String, String> variableValueScript = new HashMap<String, String>(); // 变量赋值脚本
    List<Long> roles = new ArrayList<Long>(); // 角色信息
    List<WFFormSetting> forms = new ArrayList<WFFormSetting>(); // 表单定义
    String receiveEvent; // 接收事件
    String sendEvent;// 发送事件
    String validDataEvent; // 数据校验事件
    String backEvent; // 退件事件

    /**
     * 获取表单定义
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
