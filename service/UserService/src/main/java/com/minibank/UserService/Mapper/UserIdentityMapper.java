package com.minibank.UserService.Mapper;

import com.minibank.UserService.Entity.UserIdentityEntity;
import com.minibank.UserService.dto.Response.UserIdentityResponse;

public class UserIdentityMapper {
    public static UserIdentityResponse toResponse(UserIdentityEntity userIdentity) {
        return UserIdentityResponse.builder()
            .id(userIdentity.getId())
            .fullName(userIdentity.getFullName())
            .idCardNumber(userIdentity.getIdCardNumber())
            .issueDate(userIdentity.getIssueDate())
            .dateOfBirth(userIdentity.getDateOfBirth())
            .kycStatus(userIdentity.getKycStatus())
            .submittedAt(userIdentity.getSubmittedAt())
            .verifiedAt(userIdentity.getVerifiedAt())
            .idCardFrontUrl(userIdentity.getIdCardFrontUrl())
            .idCardBackUrl(userIdentity.getIdCardBackUrl())
            .build();
    }
}
