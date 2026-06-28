package com.minibank.UserService.dto.Request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    @NotBlank(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 5, message = "Mật khẩu mới phải ít nhất 5 ký tự")
    private String newPassword;
}
