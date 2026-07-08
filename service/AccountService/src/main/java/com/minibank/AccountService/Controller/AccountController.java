package com.minibank.AccountService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.AccountService.Service.AccountService;
import com.minibank.AccountService.dto.Response.AccountResponse;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @PostMapping
    public AccountResponse createAccount() {
        return accountService.createAccount();
    }
}