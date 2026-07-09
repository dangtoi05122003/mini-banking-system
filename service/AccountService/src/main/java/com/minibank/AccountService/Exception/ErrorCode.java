package com.minibank.AccountService.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ACCOUNT_ALREADY_EXISTS(5000, "Tài khoản của người dùng đã tồn tại"),
    ACCOUNT_NUMBER_GENERATION_FAILED(5001, "Tạo số tài khoản thất bại"),
    ACCOUNT_NOT_FOUND(5002, "Không tìm thấy tài khoản"),
    INVALID_AMOUNT(5003, "Số tiền không hợp lệ"),
    INSUFFICIENT_BALANCE(5004, "Số dư không đủ"),
    ACCOUNT_LOCKED(5005, "Tài khoản đã bị khóa"),
    ACCOUNT_FROZEN(5006, "Tài khoản đã bị đóng băng"),
    ACCOUNT_NOT_ACTIVE(5007, "Tài khoản không hoạt động"),
    ACCOUNT_ALREADY_ACTIVE(5008, "Tài khoản đang hoạt động"),
    UNAUTHORIZED(5009, "Bạn không có quyền thực hiện thao tác này"),
    SELF_TRANSFER_NOT_ALLOWED(5009, "Không thể chuyển tiền cùng một tài khoản");
    private int code;
    private String message;
}
