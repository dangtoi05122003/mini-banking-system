package com.minibank.UserService.Mapper;

import com.minibank.UserService.Entity.StaffUserEntity;
import com.minibank.UserService.dto.Response.StaffUserResponse;

public class StaffUserMapper {
    public static StaffUserResponse toResponse(StaffUserEntity staffUser) {
        return StaffUserResponse.builder()
            .id(staffUser.getId())
            .username(staffUser.getUsername())
            .email(staffUser.getEmail())
            .role(staffUser.getRole())
            .status(staffUser.getStatus())
            .build();
    }
}
