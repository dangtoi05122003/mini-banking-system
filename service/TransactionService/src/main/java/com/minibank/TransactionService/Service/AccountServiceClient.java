package com.minibank.TransactionService.Service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.minibank.TransactionService.Exception.AppException;
import com.minibank.TransactionService.Exception.ErrorCode;
import com.minibank.TransactionService.dto.Request.TransferRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {
    private final RestTemplate restTemplate;
    @Value("${services.account-service.url}")
    private String accountServiceUrl;
    @Value("${security.service-token}")
    private String serviceToken;
    public void transfer(TransferRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Service-Token", serviceToken); 
            HttpEntity<TransferRequest> entity = new HttpEntity<>(request, headers);
            String url = accountServiceUrl + "/service" + "/transfer";
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
        }
    }
    public void deposit(String accountNumber, BigDecimal amount) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Service-Token", serviceToken); 
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = accountServiceUrl + "/service/" + accountNumber + "/deposit?amount=" + amount;
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
        }
    }
    public void withdraw(String accountNumber, BigDecimal amount) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Service-Token", serviceToken); 
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = accountServiceUrl + "/service/" + accountNumber + "/withdraw?amount=" + amount;
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
        }
    }
}
