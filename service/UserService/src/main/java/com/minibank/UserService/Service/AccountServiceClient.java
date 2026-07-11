package com.minibank.UserService.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.minibank.UserService.Exception.AppException;
import com.minibank.UserService.Exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {
    private final RestTemplate restTemplate;
    @Value("${services.account-service.url}")
    private String accountServiceUrl;
    @Value("${security.service-token}")
    private String serviceToken;
    public void sendActivationRequest(Long user_id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Service-Token", serviceToken); 
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = accountServiceUrl + "/service/" + user_id + "/activate-account";
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
        }
    }
}
