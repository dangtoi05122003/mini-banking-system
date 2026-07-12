package com.minibank.TransactionService.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.TransactionService.Enum.TransactionType;
import com.minibank.TransactionService.Service.TransactionService;
import com.minibank.TransactionService.dto.Request.DepositRequest;
import com.minibank.TransactionService.dto.Request.TransferRequest;
import com.minibank.TransactionService.dto.Request.WithdrawRequest;
import com.minibank.TransactionService.dto.Response.TransactionResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request);
    }
    @PostMapping("/deposit")
    public TransactionResponse deposit(@Valid @RequestBody DepositRequest request) {
        return transactionService.deposit(request);
    }
    @PostMapping("/withdraw")
    public TransactionResponse withdraw(@Valid @RequestBody WithdrawRequest request) {
        return transactionService.withdraw(request);
    }
    @GetMapping("/code/{transactionCode}")
    public TransactionResponse getByCode(@PathVariable String transactionCode) {
        return transactionService.getByCode(transactionCode);
    }
    @GetMapping("/account/{accountId}")
    public List<TransactionResponse> getTransactionsByAccount(@PathVariable Long accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }
    @GetMapping("/account/{accountId}/type/{type}")
    public List<TransactionResponse> getByAccountAndType(@PathVariable Long accountId, @PathVariable TransactionType type) {
        return transactionService.getByAccountAndType(accountId, type);
    }
    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable Long id) {
        return transactionService.getById(id);
    }
    @GetMapping("/type/{type}")
    public List<TransactionResponse> getByType(@PathVariable TransactionType type) {
        return transactionService.getByType(type);
    }
}
