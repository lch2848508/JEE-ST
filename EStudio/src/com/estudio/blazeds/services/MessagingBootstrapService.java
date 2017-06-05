/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

package com.estudio.blazeds.services;

import com.estudio.context.RuntimeConfig;

import flex.messaging.MessageDestination;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ServerSettings;
import flex.messaging.services.AbstractBootstrapService;
import flex.messaging.services.Service;
import flex.messaging.services.messaging.adapters.JMSAdapter;
import flex.messaging.services.messaging.adapters.JMSSettings;

public class MessagingBootstrapService extends AbstractBootstrapService {

    /**
     * Called by the <code>MessageBroker</code> after all of the server
     * components are created but right before they are started. This is usually
     * the place to create dynamic components.
     * 
     * @param id
     *            Id of the <code>AbstractBootstrapService</code>.
     * @param properties
     *            Properties for the <code>AbstractBootstrapService</code>.
     */
    @Override
    public void initialize(String id, ConfigMap properties) {
        if (RuntimeConfig.getInstance().isJMSEnabled()) {
            Service messagingService = createService();
            createTopicDestination(messagingService);
            createQueueDestination(messagingService);
        }
    }

    /**
     * Called by the <code>MessageBroker</code> as server starts. Useful for
     * custom code that needs to run after all the components are initialized
     * and the server is starting up.
     */
    @Override
    public void start() {
        // No-op
    }

    /**
     * Called by the <code>MessageBroker</code> as server stops. Useful for
     * custom code that needs to run as the server is shutting down.
     */
    @Override
    public void stop() {
        // No-op
    }

    /**
     * 创建服务
     * 
     * @return
     */
    private Service createService() {
        String serviceId = "messaging-service";
        String serviceClass = "flex.messaging.services.MessageService";
        Service messageService = broker.createService(serviceId, serviceClass);

        messageService.registerAdapter("actionscript", "flex.messaging.services.messaging.adapters.ActionScriptAdapter");
        messageService.registerAdapter("jms", "flex.messaging.services.messaging.adapters.JMSAdapter");
        messageService.setDefaultAdapter("actionscript");

        return messageService;
    }

    /**
     * 创建广播消息
     * 
     * @param service
     */
    private void createTopicDestination(Service service) {
        String destinationId = "topic-destination";
        MessageDestination destination = (MessageDestination) service.createDestination(destinationId);

        ServerSettings ss = new ServerSettings();
        ss.setDurable(true);
        destination.setServerSettings(ss);

        String adapterId = "jms";
        JMSAdapter adapter = (JMSAdapter) destination.createAdapter(adapterId);

        // JMS settings are set at the adapter level
        JMSSettings jms = new JMSSettings();
        jms.setDestinationType("Topic");
        jms.setMessageType("javax.jms.TextMessage");
        jms.setConnectionFactory(JMSHelper.getInstance().getConnectionContextName());
        jms.setDestinationJNDIName(JMSHelper.getInstance().getTopicContextName());
        jms.setDeliveryMode("NON_PERSISTENT");
        // jms.setMessagePriority(javax.jms.Message.DEFAULT_PRIORITY);
        jms.setAcknowledgeMode("AUTO_ACKNOWLEDGE");
        // jms.setMaxProducers(1);
        // Hashtable envProps = new Hashtable();
        // envProps.put("Context.SECURITY_PRINCIPAL", "anonymous");
        // envProps.put("Context.SECURITY_CREDENTIALS", "anonymou");
        // envProps.put("Context.PROVIDER_URL", "http://{server.name}:1856");
        // envProps.put("Context.INITIAL_CONTEXT_FACTORY",
        // "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        // jms.setInitialContextEnvironment(envProps);
        adapter.setJMSSettings(jms);
        destination.addChannel("my-polling-amf");
    }

    /**
     * 创建点对点消息
     * 
     * @param service
     */
    private void createQueueDestination(Service service) {
        String destinationId = "queue-destination";
        MessageDestination destination = (MessageDestination) service.createDestination(destinationId);

        ServerSettings ss = new ServerSettings();
        ss.setDurable(true);
        destination.setServerSettings(ss);

        String adapterId = "jms";
        JMSAdapter adapter = (JMSAdapter) destination.createAdapter(adapterId);

        // JMS settings are set at the adapter level
        JMSSettings jms = new JMSSettings();
        jms.setDestinationType("Queue");
        jms.setMessageType("javax.jms.TextMessage");
        jms.setConnectionFactory(JMSHelper.getInstance().getConnectionContextName());
        jms.setDestinationJNDIName(JMSHelper.getInstance().getQueueContextName());
        jms.setDeliveryMode("PERSISTENT");
        // jms.setMessagePriority(javax.jms.Message.DEFAULT_PRIORITY);
        jms.setAcknowledgeMode("AUTO_ACKNOWLEDGE");
        // jms.setMaxProducers(1);
        // Hashtable envProps = new Hashtable();
        // envProps.put("Context.SECURITY_PRINCIPAL", "anonymous");
        // envProps.put("Context.SECURITY_CREDENTIALS", "anonymou");
        // envProps.put("Context.PROVIDER_URL", "http://{server.name}:1856");
        // envProps.put("Context.INITIAL_CONTEXT_FACTORY",
        // "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        // jms.setInitialContextEnvironment(envProps);
        adapter.setJMSSettings(jms);
        destination.addChannel("my-polling-amf");
    }
}
