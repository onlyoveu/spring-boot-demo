package com.re.spring.boot.springbootdemo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestResponse {

    public RestResponse() {
        code = "0000";
        name = "SUCCESS";
        description = "业务操作成功";
    }

    @JsonProperty("RSP_CODE")
    private String code;

    @JsonProperty("RSP_NAME")
    private String name;

    @JsonProperty("RSP_DESC")
    private String description;

    @JsonProperty("DATA")
    private Object data;

    @JsonProperty("ATTACH")
    private Object attach;

    @Override
    public String toString() {
        return "RestResponse{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", data=" + data +
                ", attach=" + attach +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getAttach() {
        return attach;
    }

    public void setAttach(Object attach) {
        this.attach = attach;
    }
}
