package com.minibank.UserService.dto.Request.UserIdentity;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserIdentityRequest {

    private String fullName;
    private String idCardNumber;
    private LocalDate issueDate;
    private MultipartFile idCardFront;
    private MultipartFile idCardBack;
    private LocalDate dateOfBirth;
}