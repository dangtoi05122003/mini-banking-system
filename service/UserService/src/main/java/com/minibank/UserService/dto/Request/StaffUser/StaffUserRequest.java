package com.minibank.UserService.dto.Request.StaffUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class StaffUserRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 5, max = 55, message = "Tên đăng nhập phải từ 5 đến 55 ký tự")
    private String username;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, max = 100, message = "Mật khẩu phải có ít nhất 5 ký tự")
    private String password;
}
