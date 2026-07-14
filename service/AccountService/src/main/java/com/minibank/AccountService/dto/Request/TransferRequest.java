package com.minibank.AccountService.dto.Request;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class TransferRequest {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
}
