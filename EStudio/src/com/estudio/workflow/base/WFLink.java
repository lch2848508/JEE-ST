package com.estudio.workflow.base;

import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.JSCompress;

public class WFLink implements Comparable<WFLink> {
    long id; // ��ʶ��
    String caption; // ����
    String descript; // ������Ϣ
    String script; // ��ת����
    String startActivityName; // ��ʼ�ڵ�NAME
    String endActivityName; // �����ڵ�NAME
    String name; // ����������
    int priority = 0;

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public WFLink() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
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

    public String getScript() {
        return script;
    }

    public void setScript(final String script) {
        if (!StringUtils.isEmpty(script))
            this.script = JSCompress.getInstance().compress(script);
    }

    public String getStartActivityName() {
        return startActivityName;
    }

    public void setStartActivityName(final String startActivityName) {
        this.startActivityName = startActivityName;
    }

    public String getEndActivityName() {
        return endActivityName;
    }

    public void setEndActivityName(final String endActivityName) {
        this.endActivityName = endActivityName;
    }

    @Override
    public int compareTo(final WFLink o) {
        return o.getPriority() - getPriority();
    }

}
