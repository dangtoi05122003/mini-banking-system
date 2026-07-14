package com.minibank.TransactionService.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minibank.TransactionService.Entity.TransactionEntity;
import com.minibank.TransactionService.Enum.TransactionType;
import com.minibank.TransactionService.Exception.AppException;
import com.minibank.TransactionService.Exception.ErrorCode;
import com.minibank.TransactionService.Repository.TransactionRepository;
import com.minibank.TransactionService.dto.Request.DepositRequest;
import com.minibank.TransactionService.dto.Request.TransferRequest;
import com.minibank.TransactionService.dto.Request.WithdrawRequest;
import com.minibank.TransactionService.dto.Response.TransactionResponse;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountServiceClient accountServiceClient;
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setSenderAccountNumber(request.getSenderAccountNumber());
        transaction.setReceiverAccountNumber(request.getReceiverAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setTransactionCode(generateTransactionCode(TransactionType.TRANSFER));
        transaction.setDescription(request.getDescription());
        accountServiceClient.transfer(request);
        return TransactionResponse.toResponse(transactionRepository.save(transaction));
    }
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionCode(generateTransactionCode(TransactionType.DEPOSIT));
        transaction.setSenderAccountNumber(null);
        transaction.setReceiverAccountNumber(request.getAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDescription(request.getDescription());
        accountServiceClient.deposit(request.getAccountNumber(), request.getAmount());
        return TransactionResponse.toResponse(transactionRepository.save(transaction));
    }
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionCode(generateTransactionCode(TransactionType.WITHDRAW));
        transaction.setSenderAccountNumber(request.getAccountNumber());
        transaction.setReceiverAccountNumber(null);
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setDescription(request.getDescription());
        accountServiceClient.withdraw(request.getAccountNumber(), request.getAmount());
        return TransactionResponse.toResponse(transactionRepository.save(transaction));
    }
    public TransactionResponse getById(Long id) {
        TransactionEntity transaction = transactionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        return TransactionResponse.toResponse(transaction);
    }
    public TransactionResponse getByCode(String transactionCode) {
        TransactionEntity transaction = transactionRepository.findByTransactionCode(transactionCode).orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));
        return TransactionResponse.toResponse(transaction);
    }
    public List<TransactionResponse> getTransactionsByAccount(String accountNumber) {
        List<TransactionEntity> transactions = transactionRepository.findByAccountNumber(accountNumber);
        return transactions.stream().map(TransactionResponse::toResponse).toList();
    }
    public List<TransactionResponse> getByAccountAndType(String accountNumber, TransactionType type) {
        List<TransactionEntity> transactions = transactionRepository.findByAccountNumberAndType(accountNumber, type);
        return transactions.stream().map(TransactionResponse::toResponse).toList();
    }
    public List<TransactionResponse> getByType(TransactionType type) {
        List<TransactionEntity> transactions = transactionRepository.findByType(type);
        return transactions.stream().map(TransactionResponse::toResponse).toList();
    }
    private String generateTransactionCode(TransactionType type) {
        String prefix = switch (type) {
            case TRANSFER -> "TRF";
            case DEPOSIT -> "DEP";
            case WITHDRAW -> "WDR";
        };
        String code = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return prefix + "-" + code.substring(0, 10);
    }
}
