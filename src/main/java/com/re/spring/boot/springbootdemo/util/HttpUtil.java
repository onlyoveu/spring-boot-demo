package com.re.spring.boot.springbootdemo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.re.spring.boot.springbootdemo.domain.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class HttpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * http请求返回list
     *
     * @param url          url
     * @param httpMethod   请求方式
     * @param httpEntity   请求header
     * @param uriVariables 请求参数
     * @param clazz        返回类型
     * @param <T>          返回类型
     * @return 数据集合
     * @throws Exception e
     */
    public static <T> List<T> exchangeList(String url, HttpMethod httpMethod, HttpEntity<Object> httpEntity, List<Map<String, Object>> uriVariables, Class<T> clazz) throws Exception {
        return JsonUtil.j2l(HttpUtil.exchangeData(url, httpMethod, httpEntity, uriVariables), clazz);
    }

    /**
     * http请求返回 obj
     *
     * @param url          url
     * @param httpMethod   请求方式
     * @param httpEntity   请求header
     * @param uriVariables 请求参数
     * @param clazz        返回类型
     * @param <T>          返回类型
     * @return 数据集合
     * @throws Exception e
     */
    public static <T> T exchangeObject(String url, HttpMethod httpMethod, HttpEntity<Object> httpEntity, List<Map<String, Object>> uriVariables, Class<T> clazz) throws Exception {
        return JsonUtil.j2b(HttpUtil.exchangeData(url, httpMethod, httpEntity, uriVariables), clazz);
    }

    /**
     * http请求解析
     *
     * @param url          url
     * @param httpMethod   请求方式
     * @param httpEntity   请求header
     * @param uriVariables 请求参数，K V格式，解析到URL中为http:///?k=v&k=v
     * @return 数据json字符串
     */
    public static String exchangeData(String url, HttpMethod httpMethod, HttpEntity<Object> httpEntity, List<Map<String, Object>> uriVariables) throws Exception {
        String data;
        ServiceResponse serviceResponse = exchangeServiceResponse(url, httpMethod, httpEntity, uriVariables);
        if (serviceResponse.getRsp().getData() instanceof String) {
            data = String.valueOf(serviceResponse.getRsp().getData());
        } else {
            data = new ObjectMapper().writeValueAsString(serviceResponse.getRsp().getData());
        }
        return data;
    }

    /**
     * http请求解析
     *
     * @param url          url
     * @param httpMethod   请求方式
     * @param httpEntity   请求header
     * @param uriVariables 请求参数，K V格式，解析到URL中为http:///?k=v&k=v
     * @return ServiceResponse
     * @throws Exception ex
     */
    public static ServiceResponse exchangeServiceResponse(String url, HttpMethod httpMethod, HttpEntity<Object> httpEntity, List<Map<String, Object>> uriVariables) throws Exception {
        return JsonUtil.j2b(exchangeServiceString(url, httpMethod, httpEntity, uriVariables), ServiceResponse.class);
    }

    /**
     * http请求解析
     *
     * @param url          url
     * @param httpMethod   请求方式
     * @param httpEntity   请求header
     * @param uriVariables 请求参数，K V格式，解析到URL中为http:///?k=v&k=v
     * @return String
     * @throws Exception ex
     */
    public static String exchangeServiceString(String url, HttpMethod httpMethod, HttpEntity<Object> httpEntity, List<Map<String, Object>> uriVariables) throws Exception {
        if (uriVariables != null && uriVariables.size() > 0) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
            for (Map<String, Object> uriVariable : uriVariables) {
                if (uriVariable != null && uriVariable.size() > 0) {
                    uriVariable.forEach(uriComponentsBuilder::queryParam);
                }
            }
            url = uriComponentsBuilder.build().encode(ISO_8859_1).toString();
        }
        if (httpEntity == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpEntity = new HttpEntity<>(httpHeaders);
        }
        LOGGER.info("http url = {}, method = {}, header = {}", url, httpMethod.name(), httpEntity.toString());
        return new RestTemplate().exchange(url, httpMethod, httpEntity, String.class).getBody();
    }
}
