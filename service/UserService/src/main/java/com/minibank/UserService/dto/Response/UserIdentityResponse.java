package com.minibank.UserService.dto.Response;

import java.time.Instant;
import java.time.LocalDate;

import com.minibank.UserService.Enum.KycStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserIdentityResponse {
    private Long id;
    private String fullName;
    private String idCardNumber;
    private LocalDate issueDate;
    private LocalDate dateOfBirth;
    private KycStatus kycStatus;
    private Instant submittedAt;
    private Instant verifiedAt;
    private String idCardFrontUrl;
    private String idCardBackUrl;
}
