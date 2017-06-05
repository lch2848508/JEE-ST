package com.estudio.blazeds.services;

import java.util.Map;

public class SendMessageItem {
    String content = null;
    Map<String, String> propertys = null;
    boolean topic = false;

    public SendMessageItem(String content, Map<String, String> propertys, boolean topic) {
        super();
        this.content = content;
        this.propertys = propertys;
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getPropertys() {
        return propertys;
    }

    public boolean isTopic() {
        return topic;
    }

}
