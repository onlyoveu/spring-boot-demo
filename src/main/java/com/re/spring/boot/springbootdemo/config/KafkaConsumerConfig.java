package com.re.spring.boot.springbootdemo.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${kafka.common.servers}")
    private String bootstrap_servers;
    @Value("${kafka.common.username}")
    private String username;
    @Value("${kafka.common.password}")
    private String password;
    @Value("${kafka.consumer.groupId}")
    private String groupId;
    @Value("${kafka.consumer.autoOffsetReset}")
    private String autoOffsetReset;
    @Value("${kafka.consumer.maxPollRecords}")
    private String maxPollRecords;
    @Value("${kafka.consumer.maxPollInterval}")
    private String maxPollInterval;
    @Value("${kafka.consumer.sessionTimeOut}")
    private String sessionTimeOut;
    @Value("${kafka.consumer.requestTimeOut}")
    private String requestTimeOut;

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeOut);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeOut);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + username + "\" password=\"" + password + "\";");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        return props;
    }

    /**
     * 消费者
     */
    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        return new KafkaConsumer<>(consumerConfigs());
    }
}
