package com.estudio.web.servlet.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minidev.json.JSONObject;

public class MessageItem {
    private final SimpleDateFormat _F = new java.text.SimpleDateFormat("MM-dd HH:mm:ss");

    private Object content = null; // 消息内容
    private long sendUserId; // 发件人ID
    private String sendUserName; // 发件人名称
    private final List<Long> recivers = new ArrayList<Long>(); // 接件用户
    private boolean broadcast; // 是否广播消息
    private final String timestamp = _F.format(new Date());

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(final boolean broadcast) {
        this.broadcast = broadcast;
    }

    public List<Long> getRecivers() {
        return recivers;
    }

    private MessageItemType type = MessageItemType.STRING;

    public void setContent(final Object content) {
        this.content = content;
    }

    public void setSendUserId(final long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public void setSendUserName(final String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public void setType(final MessageItemType type) {
        this.type = type;
    }

    public JSONObject toJSON() {
        synchronized (this) {
            if (json == null) {
                json = new JSONObject();
                json.put("time", timestamp);
                json.put("type", type == MessageItemType.STRING ? 0 : 1);
                json.put("sendId", sendUserId);
                json.put("sendName", sendUserName);
                json.put("content", content);
            }
        }
        return json;
    }

    private JSONObject json = null;

}
