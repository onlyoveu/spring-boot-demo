package com.re.spring.boot.springbootdemo.controller;

import com.re.spring.boot.springbootdemo.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskControllerTest extends BaseTest {

    @Test
    void getById() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.put("taskId", Collections.singletonList("1"));
        String res = mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URL + "/task")
                        .params(param)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.log())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(res);
    }

    @Test
    void listTask() {
    }

    @Test
    void saveTask() throws Exception {
        String content = "{\"taskId\":\"1\"}";
        String res = mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_URL + "/task")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.log())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(res);
    }

    @Test
    void updateTask() throws Exception {
        String content = "{\"taskId\":\"1\"}";
        String res = mockMvc.perform(
                MockMvcRequestBuilders.put(BASE_URL + "/task")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.log())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(res);
    }

    @Test
    void deleteTask() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.put("taskId", Collections.singletonList("1"));
        String res = mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/task")
                        .params(param)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.log())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(res);
    }
}
