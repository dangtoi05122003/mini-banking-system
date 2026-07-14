package com.minibank.AccountService.Controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.AccountService.Service.AccountService;
import com.minibank.AccountService.dto.Request.TransferRequest;
import com.minibank.AccountService.dto.Response.AccountResponse;
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
    @PostMapping("/transfer")
    public AccountResponse transferService(@RequestBody TransferRequest request) {
        return accountService.transferService(request);
    }
    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse depositService(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        return accountService.depositService(accountNumber, amount);
    }
    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdrawService(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        return accountService.withdrawService(accountNumber, amount);
    }
}
