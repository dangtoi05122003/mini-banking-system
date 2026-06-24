package com.minibank.UserService.dto.Response;

import com.minibank.UserService.Enum.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String username;
    private String email;
    private UserStatus status;
}
