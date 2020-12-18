package com.re.spring.boot.springbootdemo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ServiceResponse {

    public ServiceResponse() {
        status = "0000";
        msg = "SUCCESS";
        txid = UUID.randomUUID().toString().replace("-", "");
        this.rsp = new RestResponse();
    }

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("MSG")
    private String msg;

    @JsonProperty("TXID")
    private String txid;

    @JsonProperty("RSP")
    private RestResponse rsp;

    public void error(String message) {
        this.rsp.setCode("0002");
        this.rsp.setDescription("业务请求异常");
        this.setStatus("0003");
        this.setMsg("系统错误");
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", txid='" + txid + '\'' +
                ", rsp=" + rsp +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public RestResponse getRsp() {
        return rsp;
    }

    public void setRsp(RestResponse rsp) {
        this.rsp = rsp;
    }
}
