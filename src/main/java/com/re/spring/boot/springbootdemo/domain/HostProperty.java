package com.re.spring.boot.springbootdemo.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;

@Component
public class HostProperty {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostProperty.class);

    @Autowired
    private Environment environment;

    public String getIp() {
        String result = "";
        InetAddress localHost = null;
        try {
            localHost = Inet4Address.getLocalHost();
            result = localHost.getHostAddress();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    public String getPort() {
        return environment.getProperty("local.server.port");
    }
}
