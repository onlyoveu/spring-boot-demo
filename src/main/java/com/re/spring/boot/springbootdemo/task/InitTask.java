package com.re.spring.boot.springbootdemo.task;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class InitTask implements ApplicationRunner {
    private final InitService initService;

    @Override
    public void run(ApplicationArguments args) {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(initService::init, 1L, 5L, TimeUnit.SECONDS);
    }
}
