package com.minibank.TransactionService.dto.Response;

import java.math.BigDecimal;

import com.minibank.TransactionService.Entity.TransactionEntity;
import com.minibank.TransactionService.Enum.TransactionType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransactionResponse {
    private Long id;
    private String transactionCode;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private TransactionType type;
    public static TransactionResponse toResponse(TransactionEntity transaction) {
        return TransactionResponse.builder()
            .id(transaction.getId())
            .transactionCode(transaction.getTransactionCode())
            .senderAccountNumber(transaction.getSenderAccountNumber())
            .receiverAccountNumber(transaction.getReceiverAccountNumber())
            .amount(transaction.getAmount())
            .type(transaction.getType())
            .build();
    }
}
