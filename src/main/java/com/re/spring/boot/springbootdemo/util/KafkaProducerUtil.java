package com.re.spring.boot.springbootdemo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerUtil {
    private static KafkaTemplate<String, String> kafkaTemplate;

    public static KafkaTemplate<String, String> getKafkaTemplate() {
        return kafkaTemplate;
    }

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        KafkaProducerUtil.kafkaTemplate = kafkaTemplate;
    }

    /* producer */
    public static void send(String topic, String data) {
        kafkaTemplate.send(topic, data);
    }

}
