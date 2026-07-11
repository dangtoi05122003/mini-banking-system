package com.minibank.AccountService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.AccountService.Service.AccountService;
import com.minibank.AccountService.dto.Response.ApiResponse;

@RestController
@RequestMapping("/service")
public class ServiceAccountController {
    @Autowired
    private AccountService accountService;
    @PutMapping("/{userId}/activate-account")
    public ApiResponse activateAccounts(@PathVariable Long userId) {
        int updatedCount = accountService.activatePendingAccountsByUserId(userId);
        return new ApiResponse(200, "Cập nhật " + updatedCount + " tài khoản sang pending cho User ID: " + userId);
    }
}
