package com.minibank.AccountService.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.minibank.AccountService.Enum.KycStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserServiceClient {
    private final RestTemplate restTemplate;
    @Value("${services.user-service.url}")
    private String userServiceUrl;
    @Value("${security.service-token}")
    private String serviceToken;
    public KycStatus getKycStatusByUserId(Long user_id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Service-Token", serviceToken); 
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = userServiceUrl + "/service/user-identity/" + user_id + "/kyc-status";
            ResponseEntity<KycStatus> response = restTemplate.exchange(url, HttpMethod.GET, entity, KycStatus.class);
            return response.getBody();
        } catch (Exception e) {
            return KycStatus.PENDING;
        }
    }
}
