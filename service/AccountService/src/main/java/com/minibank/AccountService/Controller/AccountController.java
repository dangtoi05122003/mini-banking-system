package com.minibank.AccountService.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @PostMapping
    public AccountResponse createAccount() {
        return accountService.createAccount();
    }
    @GetMapping("/me")
    public List<AccountResponse> getMyAccount() {
        return accountService.getMyAccount();
    }
    @GetMapping("/{accountNumber}")
    public AccountResponse getAccountByNumber( @PathVariable String accountNumber) {
        return accountService.getAccountByNumber(accountNumber);
    }
    @GetMapping("/{accountNumber}/balance")
    public BigDecimal getBalance(@PathVariable String accountNumber) {
        return accountService.getBalance(accountNumber);
    }
    @PostMapping("/transfer")
    public AccountResponse transfer(@RequestBody TransferRequest request) {
        return accountService.transfer(request);
    }
    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        return accountService.deposit(accountNumber, amount);
    }
    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        return accountService.withdraw(accountNumber, amount);
    }
    @PutMapping("/{accountNumber}/freeze")
    public AccountResponse freeze(@PathVariable String accountNumber) {
        return accountService.freeze(accountNumber);
    }
    @PutMapping("/{accountNumber}/lock")
    public AccountResponse lock(@PathVariable String accountNumber) {
        return accountService.lock(accountNumber);
    }
    @PutMapping("/{accountNumber}/unlock")
    public AccountResponse unlock(@PathVariable String accountNumber) {
        return accountService.unlock(accountNumber);
    }
}