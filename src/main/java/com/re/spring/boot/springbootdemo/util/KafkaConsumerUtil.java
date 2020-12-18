package com.re.spring.boot.springbootdemo.util;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerUtil {
    private static KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    public void setKafkaConsumer(KafkaConsumer<String, String> kafkaConsumer) {
        KafkaUtils.kafkaConsumer = kafkaConsumer;
    }

    /* consumer */
    public static Consumer<String, String> getConsumer() {
        return kafkaConsumer;
    }
}
