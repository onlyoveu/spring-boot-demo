package com.re.spring.boot.springbootdemo.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class InitService {
    private static final String REDIS_REGISTER_KEY = ProjectProperty.PROJECT_NAME + "_REDIS_REGISTER_KEY";
    private static final long REDIS_REGISTER_KEY_EXPIRE = 30L;
    private static final String SMS_CONSUMER_THREAD_1 = "SMS_CONSUMER_THREAD_1";
    private static final String SMS_CONSUMER_TASK = "SMS_CONSUMER_TASK";
    private static final String SMS_CONSUMER_THREAD = "SMS_CONSUMER_THREAD";

    public void init() {
        log.info("init start...");
        // 验证是否在 redis 中注册
        if (register()) {
            doWork();
        }
        log.info("init end...");
    }

    private static boolean register() {
        log.info("register start... k = {}", InitService.REDIS_REGISTER_KEY);
        boolean result = true;
        try {
            result = RedisUtils.setIfAbsent(InitService.REDIS_REGISTER_KEY, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), InitService.REDIS_REGISTER_KEY_EXPIRE, TimeUnit.SECONDS);
            Object o = RedisUtils.get(InitService.REDIS_REGISTER_KEY);
            Long expire = RedisUtils.getExpire(InitService.REDIS_REGISTER_KEY);
            if (result) {
                log.info("register success! o = {}, expire = {}", o, expire);
            } else {
                log.info("register fail!");
            }
        } catch (Exception e) {
            log.error("register error = {}", e.getMessage());
        }
        return result;
    }

    private void doWork() {
        SmsConsumerTask smsConsumerTask = new SmsConsumerTask();
        Thread smsConsumerThread = new Thread(smsConsumerTask);
        smsConsumerThread.setName(SMS_CONSUMER_THREAD_1);
        // 订阅topic
        KafkaUtils.getConsumer().subscribe(Collections.singletonList(KafkaConsumerProperty.getTopic()));
        smsConsumerThread.start();
        // 开启短信消费
        KafkaConsumerProperty.setStartConsume(true);
        ThreadInfo.THREAD_MAP.put(SMS_CONSUMER_TASK, smsConsumerTask);
        ThreadInfo.THREAD_MAP.put(SMS_CONSUMER_THREAD, smsConsumerThread);
        // 开启心跳监听
        ThreadProperty.setStopHeartBeat(true);
        heartBeat();
    }

    private void heartBeat() {
        log.info("heart beat start {}", ThreadInfo.THREAD_MAP.toString());
        SmsConsumerTask smsConsumerTask = (SmsConsumerTask) ThreadInfo.THREAD_MAP.get(SMS_CONSUMER_TASK);
        Thread smsConsumerThread = (Thread) ThreadInfo.THREAD_MAP.get(SMS_CONSUMER_THREAD);
        while (ThreadProperty.isStopHeartBeat()) {
            if (threadIsTerminated(smsConsumerThread) || timeOut()) {
                // 关闭短信消费
                KafkaConsumerProperty.setStartConsume(false);
                // 退出线程
                smsConsumerTask.stopRun();
                // 关闭心跳监听
                ThreadProperty.setStopHeartBeat(false);
            }
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean timeOut() {
        boolean result = true;
        if (RedisUtils.hasKey(InitService.REDIS_REGISTER_KEY)) {
            result = false;
            RedisUtils.set(InitService.REDIS_REGISTER_KEY, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), InitService.REDIS_REGISTER_KEY_EXPIRE, TimeUnit.SECONDS);
            Object o = RedisUtils.get(InitService.REDIS_REGISTER_KEY);
            Long expire = RedisUtils.getExpire(InitService.REDIS_REGISTER_KEY);
            log.debug("update key = {}, o = {}, expire = {}", InitService.REDIS_REGISTER_KEY, o, expire);
        }
        return result;
    }

    private boolean threadIsTerminated(Thread smsSendThread) {
        boolean threadIsTerminated = false;
        Thread.State state = smsSendThread.getState();
        if (Thread.State.TERMINATED.name().equalsIgnoreCase(state.name())) {
            threadIsTerminated = true;
        }
        return threadIsTerminated;
    }
}
