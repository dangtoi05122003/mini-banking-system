package com.minibank.UserService.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USER_NOT_FOUND(1001, "Không tìm thấy người dùng"),
    USERNAME_ALREADY_EXISTS(1002, "Tên người dùng đã tồn tại"),
    EMAIL_ALREADY_EXISTS(1003, "Email đã được sử dụng"),
    WRONG_PASSWORD(1004, "Mật khẩu không đúng"),
    USER_NOT_ACTIVE(1005, "Tài khoản chưa được kích hoạt"),
    USER_LOCKED(1006, "Tài khoản đã bị khóa do đăng nhập sai quá nhiều lần"),
    USER_SUSPENDED(1007, "Tài khoản đã bị tạm khóa"),
    USER_DELETED(1008, "Tài khoản không còn tồn tại"),
    TOKEN_GENERATION_FAILED(2001, "Không thể tạo token đăng nhập"),
    IDENTITY_UPLOAD_FAILED(3001, "Tải thông tin định danh thất bại"),
    IDENTITY_NOT_FOUND(3002, "Không tìm thấy thông tin định danh"),
    IDENTITY_ALREADY_SUBMITTED(3003, "Hồ sơ KYC đang chờ duyệt hoặc đã được xác minh"),
    IDENTITY_ALREADY_VERIFIED(3004, "Hồ sơ định danh đã được xác minh, không thể chỉnh sửa.");
    private int code;
    private String message;
}
