package com.estudio.blazeds.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

public class MessageHelper {

    /**
     * 发送消息到用户
     * 
     * @param userId
     * @param content
     */
    public void sendMessage2User(long userId, String content, long sendUserId) {
        sendMessage2User(new long[] { userId }, content, sendUserId);
    }

    /**
     * 发送消息到用户组
     * 
     * @param userIds
     * @param content
     */
    public void sendMessage2User(long[] userIds, String content, long sendUserId) {
        Map<String, String> property = new HashMap<String, String>();
        Long[] tempReciverIds = new Long[userIds.length + 1];
        int index = 0;
        for (long id : userIds)
            tempReciverIds[index++] = id;
        tempReciverIds[index++] = sendUserId;
        String reciverUserIds = StringUtils.join(tempReciverIds, ",");
        for (long id : userIds) {
            property.clear();
            property.put("userid", Long.toString(id));
            property.put("send_userid", Long.toString(sendUserId));
            property.put("group_userids", reciverUserIds);
            property.put("send_datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            JMSHelper.getInstance().sendQueueMessage(content, property);
        }
    }

    /**
     * 发送信息到用户
     * 
     * @param userId
     * @param content
     */
    public void publishMessage2User(long[] userId, String messageContent) {
        List<Long> ids = new ArrayList<Long>();
        for (long l : userId)
            ids.add(l);
        Map<String, String> propertys = new HashMap<String, String>();
        propertys.put("ids", StringUtils.join(ids, ","));
        propertys.put("type", "user");
        JMSHelper.getInstance().sendTopicMessage(messageContent, propertys);
    }

    /**
     * 发送信息到角色
     * 
     * @param roleId
     * @param content
     */
    public void publishMessage2Role(long[] roleId, String messageContent) {
        List<Long> ids = new ArrayList<Long>();
        for (long l : roleId)
            ids.add(l);
        Map<String, String> propertys = new HashMap<String, String>();
        propertys.put("ids", StringUtils.join(ids, ","));
        propertys.put("type", "role");
        JMSHelper.getInstance().sendTopicMessage(messageContent, propertys);
    }

    /**
     * 发送消息到用户
     * 
     * @param id
     * @param messageContent
     */
    public void publishMessage2User(long id, String messageContent) {
        publishMessage2User(new long[] { id }, messageContent);
    }

    /**
     * 发送消息到角色
     * 
     * @param roleId
     * @param content
     */
    public void publishMessage2Role(long roleId, String messageContent) {
        publishMessage2Role(new long[] { roleId }, messageContent);
    }

    public JSONObject reciverMessage(long userId) throws Exception {
        return JMSHelper.getInstance().reciverMessage(userId);
    }

    /**
     * 广播消息
     * 
     * @param content
     */
    public void broadcast(String content) {
    }

    private MessageHelper() {
    }

    private static MessageHelper instance = new MessageHelper();

    public static MessageHelper getInstance() {
        return instance;
    }

}
