package com.minibank.UserService.dto.Request.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
