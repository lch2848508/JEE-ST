package com.estudio.blazeds.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import net.minidev.json.JSONObject;

import com.estudio.utils.ExceptionUtils;

public class JMSHelper {

    private Context ctx = null;
    private ConnectionFactory conFactory = null;
    private Destination topicDestination = null;
    private Queue queueDestination = null;
    private Connection jmsConnection = null;
    private int MAX_RECORD = 20;

    private int sendThreadNumber = 2;
    private int reciveSessionNumber = 20;
    private BlockingQueue<SendMessageItem> sendMessageQueue = new LinkedBlockingQueue<SendMessageItem>();
    private BlockingQueue<Session> reciveSession = new LinkedBlockingQueue<Session>();
    private ExecutorService executorService = Executors.newFixedThreadPool(sendThreadNumber);

    private String connectionContextName;
    private String topicContextName;
    private String queueContextName;

    public String getConnectionContextName() {
        return connectionContextName;
    }

    public String getTopicContextName() {
        return topicContextName;
    }

    public String getQueueContextName() {
        return queueContextName;
    }

    public JSONObject reciverMessage(long userId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("r", false);
        Session session = null;
        if (existsConnection() && (session = reciveSession.poll(200, TimeUnit.MILLISECONDS)) != null) {
            MessageConsumer jmsConsumer = null;
            List<JSONObject> msgList = new ArrayList<JSONObject>();
            try {
                jmsConsumer = session.createConsumer(queueDestination, "userid = '" + userId + "'");
                Message message = null;
                int count = 0;
                while ((message = jmsConsumer.receive(200)) != null && count++ < MAX_RECORD) {
                    JSONObject msgJson = new JSONObject();
                    msgJson.put("group_userids", message.getStringProperty("group_userids"));
                    msgJson.put("send_userid", message.getStringProperty("send_userid"));
                    msgJson.put("send_datetime", message.getStringProperty("send_datetime"));
                    msgJson.put("content", ((TextMessage) message).getText());
                    msgList.add(msgJson);
                }
                reciveSession.add(session);
                json.put("msgs", msgList);
                json.put("r", !msgList.isEmpty());
            } finally {
                if (jmsConsumer != null)
                    jmsConsumer.close();
            }
        }
        return json;
    }

    public void initContextName(String connectionContextName, String topicContextName, String queueContextName) {
        this.connectionContextName = connectionContextName;
        this.topicContextName = topicContextName;
        this.queueContextName = queueContextName;

    }

    public void initJNDI() throws Exception {
        ctx = new InitialContext();
        conFactory = (ConnectionFactory) ctx.lookup(connectionContextName);
        topicDestination = (Destination) ctx.lookup(topicContextName);
        queueDestination = (Queue) ctx.lookup(queueContextName);
    }

    public void sendTopicMessage(String content, Map<String, String> propertys) {
        sendMessage(content, propertys, true);
    }

    public void sendQueueMessage(String content, Map<String, String> propertys) {
        sendMessage(content, propertys, false);
    }

    /**
     * @param content
     * @param propertys
     * @param producer
     */
    private void sendMessage(String content, Map<String, String> propertys, boolean isTopic) {
        SendMessageItem messageItem = new SendMessageItem(content, propertys, isTopic);
        sendMessageQueue.add(messageItem);

    }

    public void start() {
        for (int i = 0; i < sendThreadNumber; i++) {
            executorService.submit(new Runnable() {
                Session sendSession = null;
                MessageProducer topicProducer = null;
                MessageProducer queueProducer = null;

                @Override
                public void run() {
                    while (true) {
                        try {
                            if (existsConnection() && sendSession != null) {
                                SendMessageItem messageItem = sendMessageQueue.poll(5000, TimeUnit.MILLISECONDS);
                                if (messageItem == null)
                                    continue;
                                TextMessage message = sendSession.createTextMessage();
                                message.setText(messageItem.content);
                                for (Map.Entry<String, String> entry : messageItem.propertys.entrySet()) {
                                    message.setStringProperty(entry.getKey(), entry.getValue());
                                }
                                MessageProducer producer = messageItem.isTopic() ? topicProducer : queueProducer;
                                producer.send(message);
                            } else {
                                createResource();
                            }
                        } catch (final Exception e) {
                            ExceptionUtils.printExceptionTrace(e);
                        }
                    }
                }

                private void createResource() throws Exception {
                    if (topicProducer != null)
                        topicProducer.close();
                    if (queueProducer != null)
                        queueProducer.close();
                    if (sendSession != null)
                        sendSession.close();
                    sendSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    topicProducer = sendSession.createProducer(topicDestination);
                    queueProducer = sendSession.createProducer(queueDestination);
                    topicProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                    queueProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
                }
            });
        }
    }

    protected synchronized boolean existsConnection() {
        if (conFactory != null && jmsConnection == null) {
            try {
                jmsConnection = conFactory.createConnection();
                jmsConnection.setExceptionListener(new ExceptionListener() {
                    @Override
                    public void onException(JMSException ex) {
                        jmsConnection = null;
                    }
                });

                reciveSession.clear();
                for (int i = 0; i < reciveSessionNumber; i++)
                    reciveSession.add(jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE));

                jmsConnection.start();
            } catch (JMSException e) {
                ExceptionUtils.printExceptionTrace(e);
            }
        }
        return jmsConnection != null;
    }

    private JMSHelper() {

    }

    private static JMSHelper instance = new JMSHelper();

    public static JMSHelper getInstance() {
        return instance;
    }

}
