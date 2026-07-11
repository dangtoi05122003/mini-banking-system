package com.minibank.UserService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.UserService.Enum.KycStatus;
import com.minibank.UserService.Service.UserIdentityService;

@RestController
@RequestMapping("/service")
public class ServiceUserIdentityController {
    @Autowired
    private UserIdentityService userIdentityService;
    @GetMapping("/user-identity/{userId}/kyc-status")
    public KycStatus getKycStatusByUserId(@PathVariable Long userId) {
        return userIdentityService.getKycStatusByUserId(userId);
    }
}
