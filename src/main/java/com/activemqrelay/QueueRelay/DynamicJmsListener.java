package com.activemqrelay.QueueRelay;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class DynamicJmsListener implements JmsListenerConfigurer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private MessageFlowConfig messageFlowConfig;

    @Autowired
    private ConcurrencyConfig concurrencyConfig;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        messageFlowConfig.getMessageFlows().forEach(flow -> {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            // Generate a unique ID incorporating transformation function if it exists
            String endpointId = "listener-" + flow.getSourceQueue() + "-" + flow.getDestinationQueue() +
                    (flow.getTransformationFunction() != null ? "-" + flow.getTransformationFunction() : "-noTransform");
            endpoint.setId(endpointId);
            endpoint.setDestination(flow.getSourceQueue());
            endpoint.setMessageListener(message -> {
                String textMessage = null;
                try {
                    textMessage = message instanceof TextMessage ? ((TextMessage) message).getText() : message.toString();
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
                String transformedMessage = (flow.getTransformationFunction() != null && !flow.getTransformationFunction().isEmpty()) ?
                        applyTransformation(textMessage, flow.getTransformationFunction()) : textMessage;
                System.out.println("[" + flow.getSourceQueue() + "](\"" + textMessage + "\")->[" + flow.getDestinationQueue() + "](\"" + transformedMessage + "\")");
                jmsTemplate.convertAndSend(flow.getDestinationQueue(), transformedMessage);
            });

            // Apply concurrency settings or use default
            String defaultConcurrency = "1-1"; // Default concurrency
            String concurrency = concurrencyConfig.getConcurrencySettings().stream()
                    .filter(c -> c.getQueueName().equals(flow.getSourceQueue()))
                    .findFirst()
                    .map(ConcurrencyConfig.ConcurrencySetting::getConcurrency)
                    .orElse(defaultConcurrency);

            endpoint.setConcurrency(concurrency);
            registrar.registerEndpoint(endpoint);
        });
    }


    private String applyTransformation(String message, String functionName) {
        try {
            Method method = TransformationFunctions.class.getMethod(functionName, String.class);
            return (String) method.invoke(null, message);
        } catch (Exception e) {
            throw new RuntimeException("Error applying transformation function", e);
        }
    }
}