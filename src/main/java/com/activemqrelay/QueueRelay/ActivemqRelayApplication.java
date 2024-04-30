package com.activemqrelay.QueueRelay;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class ActivemqRelayApplication {

    @Bean
    public MessageFlowConfig loadMessageFlows(@Value("classpath:messageFlows.json") Resource resource) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(resource.getInputStream(), MessageFlowConfig.class);
    }

    @Bean
    public static ConcurrencyConfig loadConcurrencySettings(@Value("classpath:queueConcurrency.json") Resource resource) {
        ObjectMapper mapper = new ObjectMapper();
        ConcurrencyConfig config = null;
        try {
            config = mapper.readValue(resource.getInputStream(), ConcurrencyConfig.class);
        } catch (IOException e) {
            config = new ConcurrencyConfig();
            config.setConcurrencySettings(Collections.emptyList()); // Set to empty if file is missing
        }
        return config;
    }
}

@Setter
@Getter
class MessageFlowConfig {
    private List<MessageFlow> messageFlows;

    @Setter
    @Getter
    static class MessageFlow {
        private String sourceQueue;
        private String destinationQueue;
        private String transformationFunction;

    }
}

@Getter
@Setter
class ConcurrencyConfig {
    private List<ConcurrencySetting> concurrencySettings;

    @Getter
    @Setter
    public static class ConcurrencySetting {
        private String queueName;
        private String concurrency;
    }
}
