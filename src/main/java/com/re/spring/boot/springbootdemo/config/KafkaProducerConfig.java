package com.re.spring.boot.springbootdemo.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${kafka.common.servers}")
    private String bootstrap_servers;
    @Value("${kafka.common.username}")
    private String username;
    @Value("${kafka.common.password}")
    private String password;
    @Value("${kafka.producer.ack}")
    private String ack;
    @Value("${kafka.producer.retries}")
    private String retries;
    @Value("${kafka.producer.linger}")
    private String linger;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        // 集群节点
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        /*
         * acks 模式：1(默认)
         *
         * 0：意味着producer不等待broker同步完成的确认，继续发送下一条(批)信息，提供了最低的延迟。但是最弱的持久性，当服务器发生故障时，就很可能发生数据丢失。例如leader已经死亡，producer不知情，还会继续发送消息broker接收不到数据就会数据丢失
         * 1：意味着producer要等待leader成功收到数据并得到确认，才发送下一条message。此选项提供了较好的持久性较低的延迟性。Partition的Leader死亡，follwer尚未复制，数据就会丢失
         * -1：意味着producer得到follower确认，才发送下一条数据，持久性最好，延时性最差。
         */
        props.put(ProducerConfig.ACKS_CONFIG, ack);
        /*
         * 失败重试：0(默认)
         *
         * 生产者从服务器收到的错误消息有可能是临时的，当生产者收到服务器发来的错误消息，会启动重试机制，
         * 当充实了n（设置的值）次，还是收到错误消息，那么将会返回错误。生产者会在每次重试之间间隔100ms，
         * 不过可以通过retry.backoff.ms改变这个间隔。
         */
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        /*
         * 批次大小：16384(默认)
         *
         * 当多个消息发往 同一个分区，生产者会将他们放进同一个批次，该参数指定了一个批次可以使用的内存大小，
         * 按照字节数进行计算，不是消息个数，当批次被填满，批次里面所有得消息将会被发送，半满的批次，甚至只包含一个消息也可能会被发送，
         * 所以即使把批次设置的很大，也不会造成延迟，只是占用的内存打了一些而已。但是设置的太小，那么生产者将会频繁的发送小，增加一些额外的开销。
         */
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        /*
         * 发送延迟：0(默认)
         *
         * producer组将会汇总任何在请求与发送之间到达的消息记录一个单独批量的请求。通常来说，这只有在记录产生速度大于发送速度的时候才能发生。
         * 然而，在某些条件下，客户端将希望降低请求的数量，甚至降低到中等负载一下。这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，
         * producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理。这可以认为是TCP种Nagle的算法类似。
         * 这项设置设定了批量处理的更高的延迟边界：一旦我们获得某个partition的batch.size，他将会立即发送而不顾这项设置，然而如果我们获得消息字节数比这项设置要小的多，我们需要“linger”特定的时间以获取更多的消息。
         * 这个设置默认为0，即没有延迟。设定linger.ms=5，例如，将会减少请求数目，但是同时会增加5ms的延迟。
         */
        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        /*
         * 缓存大小：33554432(默认)
         *
         * producer可以用来缓存数据的内存大小。如果数据产生速度大于向broker发送的速度，producer会阻塞或者抛出异常，以“block.on.buffer.full”来表明。
         * 这项设置将和producer能够使用的总内存相关，但并不是一个硬性的限制，因为不是producer使用的所有内存都是用于缓存。
         * 一些额外的内存会用于压缩（如果引入压缩机制），同样还有一些用于维护请求。
         */
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);//用户名密码方式 begin
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + username + "\" password=\"" + password + "\";");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-256");

        return props;
    }

    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
