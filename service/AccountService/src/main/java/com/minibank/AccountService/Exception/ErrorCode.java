package com.minibank.AccountService.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ACCOUNT_ALREADY_EXISTS(5000, "Tài khoản của người dùng đã tồn tại"),
    ACCOUNT_NUMBER_GENERATION_FAILED(50001, "Tạo số tài khoản thất bại");
    private int code;
    private String message;
}
