package com.minibank.TransactionService.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    TRANSACTION_NOT_FOUND(6000, "Không tìm thấy giao dịch");
    private int code;
    private String message;
}
