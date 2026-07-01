package com.minibank.UserService.dto.Request.UserIdentity;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserIdentityRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 5, max = 100, message = "Họ tên phải từ 5 đến 100 ký tự")
    private String fullName;
    @NotBlank(message = "Số CCCD không được để trống")
    private String idCardNumber;
    @NotNull(message = "Ngày cấp không được để trống")
    private LocalDate issueDate;
    @NotNull(message = "Ảnh mặt trước CCCD không được để trống")
    private MultipartFile idCardFront;
    @NotNull(message = "Ảnh mặt sau CCCD không được để trống")
    private MultipartFile idCardBack;
    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate dateOfBirth;
}