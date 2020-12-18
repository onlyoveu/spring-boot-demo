package com.re.spring.boot.springbootdemo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class SpringDocConfig {

    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.description}")
    private String appDescription;
    @Value("${spring.application.version}")
    private String appVersion;

    private static final String GROUP_NAME = "admin";
    private static final String PATHS_TO_MATCH = "/**";
    private static final String GLOBAL_AUTHORIZATION = "Authorization";
    private static final String PACKAGES_TO_SCAN = "com.unicom.microservice.cc.web";

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group(GROUP_NAME)
                .pathsToMatch(PATHS_TO_MATCH)
                .packagesToScan(PACKAGES_TO_SCAN)
                .build();
    }

    @Bean
    public OpenAPI springDocOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(GLOBAL_AUTHORIZATION, new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER).name(GLOBAL_AUTHORIZATION)))
                .addSecurityItem(new SecurityRequirement().addList(GLOBAL_AUTHORIZATION))
                .info(new Info().title(appName)
                        .description(new String(appDescription.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8))
                        .version(appVersion));
    }
}
