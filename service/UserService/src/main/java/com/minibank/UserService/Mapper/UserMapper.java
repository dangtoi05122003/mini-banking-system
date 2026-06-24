package com.minibank.UserService.Mapper;

import com.minibank.UserService.Entity.UserEntity;
import com.minibank.UserService.dto.Response.UserResponse;

public class UserMapper {
    public static UserResponse toResponse(UserEntity user) {
        return UserResponse.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .status(user.getStatus())
            .build();
    }
}
