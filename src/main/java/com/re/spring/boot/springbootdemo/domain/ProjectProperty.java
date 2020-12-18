package com.re.spring.boot.springbootdemo.domain;

import org.springframework.beans.factory.annotation.Value;

public class ProjectProperty {
    private static String projectName;

    public static String getProjectName() {
        return projectName;
    }

    @Value("${spring.application.name}")
    public void setProjectName(String projectName) {
        ProjectProperty.projectName = projectName;
    }
}
