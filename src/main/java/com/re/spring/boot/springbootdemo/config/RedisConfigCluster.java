package com.re.spring.boot.springbootdemo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * SpringBoot整合Redis-Cluster集群
 *
 * @author think
 */
@Configuration
public class RedisConfigCluster {


    /**
     * 实例化 RedisTemplate 对象
     *
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> functionDomainRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        initDomainRedisTemplate(redisTemplate, lettuceConnectionFactory);
        return redisTemplate;
    }

    /**
     * 设置数据存入 redis 的序列化方式,并开启事务
     */
    private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, LettuceConnectionFactory lettuceConnectionFactory) {
        /*
         * 连接失败时可以用以下方法解决(springframework RedisSystemException Connection reset by peer)：
         *
         * 1、重新 init 连接：
         * lettuceConnectionFactory.initConnection();
         * 2、重新初始化线程工厂：
         * redisTemplate.setConnectionFactory();
         */
        // 设置使用前先校验连接，可能会影响效率
        // lettuceConnectionFactory.setValidateConnection(true);
        // 是否允许多个线程操作共用同一个缓存连接，默认true，false时每个操作都将开辟新的连接，lettuce 基于 netty，用1个连接即可。
        // lettuceConnectionFactory.setShareNativeConnection(false);
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 配置redisTemplate
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);// key序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);// value序列化
        redisTemplate.setHashKeySerializer(stringSerializer);// Hash key序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);// Hash value序列化
        redisTemplate.afterPropertiesSet();
        // 事务
        redisTemplate.setEnableTransactionSupport(false);
    }
}
