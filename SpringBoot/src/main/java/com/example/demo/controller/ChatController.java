package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@CrossOrigin(origins = "http://localhost:9876")
public class ChatController {

    @PostMapping("/api/vivogpt")
    public ResponseEntity<String> handleRequest(@RequestBody Map<String, String> requestBody) {
        try {
            String appId = "3036723835";  // 替换为你的appId
            String appKey = "WFzNmtuCYlFxcjej";  // 替换为你的appKey
            String URI = "/vivogpt/completions";
            String DOMAIN = "api-ai.vivo.com.cn";
            String METHOD = "POST";
            UUID requestId = UUID.randomUUID();

            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("requestId", requestId.toString());
            String queryStr = mapToQueryString(queryParams);

            long timestamp = System.currentTimeMillis() / 1000;  // 确保时间戳是秒级的
            String nonce = generateNonce(8);
            String signedHeaders = "x-ai-gateway-app-id;x-ai-gateway-timestamp;x-ai-gateway-nonce";
            String signedHeadersString = "x-ai-gateway-app-id:" + appId + "\nx-ai-gateway-timestamp:" + timestamp + "\nx-ai-gateway-nonce:" + nonce;

            // 拼接签名字符串
            String stringToSign = METHOD + "\n" + URI + "\n" + queryStr + "\n" + appId + "\n" + timestamp + "\n" + signedHeadersString;
            String signature = generateSignature(stringToSign, appKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-AI-GATEWAY-APP-ID", appId);
            headers.set("X-AI-GATEWAY-TIMESTAMP", String.valueOf(timestamp));
            headers.set("X-AI-GATEWAY-NONCE", nonce);
            headers.set("X-AI-GATEWAY-SIGNED-HEADERS", signedHeaders);
            headers.set("X-AI-GATEWAY-SIGNATURE", signature);

            String url = String.format("https://%s%s?%s", DOMAIN, URI, queryStr);
            Map<String, Object> data = new HashMap<>();
            data.put("prompt", requestBody.get("prompt"));
            data.put("model", "vivo-BlueLM-TB");
            data.put("sessionId", UUID.randomUUID().toString());
            String requestBodyString = new ObjectMapper().writeValueAsString(data);

            // 打印请求头和请求体以便调试
            System.out.println("Authorization Headers: " + headers);
            System.out.println("Request Body: " + requestBodyString);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            // 提取content字段
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
            String content = (String) dataMap.get("content");

            return ResponseEntity.ok(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    // 生成签名
    private String generateSignature(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    // 将Map转换为查询字符串
    private String mapToQueryString(Map<String, Object> map) {
        StringBuilder queryStringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (queryStringBuilder.length() > 0) {
                queryStringBuilder.append("&");
            }
            queryStringBuilder.append(entry.getKey());
            queryStringBuilder.append("=");
            queryStringBuilder.append(entry.getValue());
        }
        return queryStringBuilder.toString();
    }

    // 生成随机的nonce
    private String generateNonce(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder nonce = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            nonce.append(characters.charAt(random.nextInt(characters.length())));
        }
        return nonce.toString();
    }
}




/*
package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@CrossOrigin(origins = "http://localhost:9876")
public class ChatController {

    @PostMapping("/api/vivogpt")
    public ResponseEntity<String> handleRequest(@RequestBody Map<String, String> requestBody) {
        try {
            String appId = "3036723835";  // 替换为你的appId
            String appKey = "WFzNmtuCYlFxcjej";  // 替换为你的appKey
            String URI = "/vivogpt/completions";
            String DOMAIN = "api-ai.vivo.com.cn";
            String METHOD = "POST";
            UUID requestId = UUID.randomUUID();

            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("requestId", requestId.toString());
            String queryStr = mapToQueryString(queryParams);

            long timestamp = System.currentTimeMillis() / 1000;  // 确保时间戳是秒级的
            String nonce = generateNonce(8);
            String signedHeaders = "x-ai-gateway-app-id;x-ai-gateway-timestamp;x-ai-gateway-nonce";
            String signedHeadersString = "x-ai-gateway-app-id:" + appId + "\nx-ai-gateway-timestamp:" + timestamp + "\nx-ai-gateway-nonce:" + nonce;

            // 拼接签名字符串
            String stringToSign = METHOD + "\n" + URI + "\n" + queryStr + "\n" + appId + "\n" + timestamp + "\n" + signedHeadersString;
            String signature = generateSignature(stringToSign, appKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-AI-GATEWAY-APP-ID", appId);
            headers.set("X-AI-GATEWAY-TIMESTAMP", String.valueOf(timestamp));
            headers.set("X-AI-GATEWAY-NONCE", nonce);
            headers.set("X-AI-GATEWAY-SIGNED-HEADERS", signedHeaders);
            headers.set("X-AI-GATEWAY-SIGNATURE", signature);

            String url = String.format("https://%s%s?%s", DOMAIN, URI, queryStr);
            Map<String, Object> data = new HashMap<>();
            data.put("prompt", requestBody.get("prompt"));
            data.put("model", "vivo-BlueLM-TB");
            data.put("sessionId", UUID.randomUUID().toString());
            String requestBodyString = new ObjectMapper().writeValueAsString(data);

            // 打印请求头和请求体以便调试
            System.out.println("Authorization Headers: " + headers);
            System.out.println("Request Body: " + requestBodyString);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    // 生成签名
    private String generateSignature(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    // 将Map转换为查询字符串
    private String mapToQueryString(Map<String, Object> map) {
        StringBuilder queryStringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (queryStringBuilder.length() > 0) {
                queryStringBuilder.append("&");
            }
            queryStringBuilder.append(entry.getKey());
            queryStringBuilder.append("=");
            queryStringBuilder.append(entry.getValue());
        }
        return queryStringBuilder.toString();
    }

    // 生成随机的nonce
    private String generateNonce(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder nonce = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            nonce.append(characters.charAt(random.nextInt(characters.length())));
        }
        return nonce.toString();
    }
}
*/