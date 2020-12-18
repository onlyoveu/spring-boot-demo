package com.re.spring.boot.springbootdemo.controller;

import com.re.spring.boot.springbootdemo.domain.ServiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;


@Tag(name = "任务管理")
@RestController
@RequestMapping("/spring/boot/demo")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Operation(summary = "根据id")
    @GetMapping("/task{taskId}")
    public ServiceResponse getById(
            @Parameter(description = "taskId", example = "1") @PathVariable String taskId
    ) {

        ServiceResponse sr = new ServiceResponse();
        try {
            sr.getRsp().setData(taskId);
        } catch (Exception e) {
            sr.error(e.getMessage());
            LOGGER.error(e.toString());
        }
        return sr;
    }

    @Operation(summary = "根据id")
    @GetMapping("/task")
    public ServiceResponse listTask(
            @Parameter(description = "taskId", example = "1") @RequestParam String taskId
    ) {

        ServiceResponse sr = new ServiceResponse();
        try {
            sr.getRsp().setData(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        } catch (Exception e) {
            sr.error(e.getMessage());
            LOGGER.error(e.toString());
        }
        return sr;
    }

    @Operation(summary = "新增任务")
    @PostMapping("/task")
    public ServiceResponse saveTask(
            @Parameter(description = "body", example = "{\"taskId\":\"1\"}") @RequestParam String body
    ) {

        ServiceResponse sr = new ServiceResponse();
        try {
            sr.getRsp().setData(body);
        } catch (Exception e) {
            sr.error(e.getMessage());
            LOGGER.error(e.toString());
        }
        return sr;
    }

    @Operation(summary = "修改任务")
    @PutMapping("/task")
    public ServiceResponse updateTask(
            @Parameter(description = "body", example = "{\"taskId\":\"1\"}") @RequestParam String body
    ) {

        ServiceResponse sr = new ServiceResponse();
        try {
            sr.getRsp().setData(body);
        } catch (Exception e) {
            sr.error(e.getMessage());
            LOGGER.error(e.toString());
        }
        return sr;
    }

    @Operation(summary = "删除任务")
    @PutMapping("/task/{taskId}")
    public ServiceResponse deleteTask(
            @Parameter(description = "taskId", example = "1") @PathVariable String taskId
    ) {

        ServiceResponse sr = new ServiceResponse();
        try {
            sr.getRsp().setData(taskId);
        } catch (Exception e) {
            sr.error(e.getMessage());
            LOGGER.error(e.toString());
        }
        return sr;
    }

}
